package nl.amis.table.view.model;


import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.amis.sdo.jpa.entities.BaseDataObject;
import nl.amis.sdo.jpa.entities.BaseEntity;
import nl.amis.sdo.jpa.services.Service;

import oracle.adf.view.rich.model.FilterableQueryDescriptor;

import oracle.jbo.common.service.types.FindControl;
import oracle.jbo.common.service.types.FindCriteria;
import oracle.jbo.common.service.types.SortAttribute;
import oracle.jbo.common.service.types.SortOrder;
import oracle.jbo.common.service.types.ViewCriteria;
import oracle.jbo.common.service.types.ViewCriteriaItem;
import oracle.jbo.common.service.types.ViewCriteriaRow;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortCriterion;


/**
 * A special type of JSF DataModel to allow a datatable and datascroller
 * to page through a large set of data without having to hold the entire
 * set of data in memory at once.
 * <p>
 * Any time a managed bean wants to avoid holding an entire dataset,
 * the managed bean should declare an inner class which extends this
 * class and implements the fetchData method. This method is called
 * as needed when the table requires data that isn't available in the
 * current data page held by this object.
 * <p>
 * This does require the managed bean (and in general the business
 * method that the managed bean uses) to provide the data wrapped in
 * a DataPage object that provides info on the full size of the dataset.
 */
// http://wiki.apache.org/myfaces/WorkingWithLargeTables
public class PagedListDataModel<S extends BaseDataObject<T>, T extends BaseEntity<S>> extends CollectionModel {

  private int pageSize;
  private int rowIndex;
  private int lastStartRow = -1;
  private DataPage<S> page;
  private DataPage<S> lastPage;
  private final Service service;
  private final Class<T> implementation;
  private final FilterableQueryDescriptorImpl filterModel;
  private List<SortCriterion> sortCriteria = new ArrayList<SortCriterion>(1);

  /*
          * Create a datamodel that pages through the data showing the specified
          * number of rows on each page.
          */

  public PagedListDataModel(final Service service,
                            final Class<T> implementation,
                            final int pageSize) {
    super();
    this.service = service;
    this.implementation = implementation;
    this.filterModel = new FilterableQueryDescriptorImpl();
    this.pageSize = pageSize;
    this.rowIndex = -1;
    this.page = null;
  }

  public void reset() {
    this.rowIndex = -1;
    this.lastStartRow = -1;
    this.page = null;
    this.lastPage = null;
  }

  public void setSortCriteria(List<SortCriterion> sortCriteria) {
    System.out.println("setSortCriteria: " + sortCriteria);
    this.sortCriteria = sortCriteria;
    reset();
  }

  public List<SortCriterion> getSortCriteria() {
    System.out.println("getSortCriteria: " + sortCriteria);
    return sortCriteria;
  }

  public FilterableQueryDescriptor getFilterModel() {
    System.out.println("getFilterModel: " + filterModel);
    reset();
    return filterModel;
  }

  /**
   * Not used in this class; data is fetched via a callback to the fetchData
   * method rather than by explicitly assigning a list.
   */
  @Override
  public void setWrappedData(Object o) {
    throw new UnsupportedOperationException("setWrappedData");
  }

  @Override
  public int getRowIndex() {
    return rowIndex;
  }

  /**
   * Specify what the "current row" within the dataset is. Note that the
   * UIData component will repeatedly call this method followed by getRowData
   * to obtain the objects to render in the table.
   */
  @Override
  public void setRowIndex(int index) {
    System.out.println("setRowIndex: " + index);
    rowIndex = index;
  }

  /**
   * Return the total number of rows of data available (not just the number of
   * rows in the current page!).
   */
  @Override
  public int getRowCount() {
    return getPage().getDatasetSize();
  }

  /**
   * Return a DataPage object; if one is not currently available then fetch
   * one. Note that this doesn't ensure that the datapage returned includes
   * the current rowIndex row; see getRowData.
   */
  private DataPage<S> getPage() {
    if (page != null)
      return page;

    int rowIndex = getRowIndex();
    int startRow = rowIndex;
    if (rowIndex == -1) {
      // even when no row is selected, we still need a page
      // object so that we know the amount of data available.
      startRow = 0;
    }

    // invoke method on enclosing class
    page = suggestFetchPage(startRow, pageSize);
    return page;
  }

  /**
   * Return the object corresponding to the current rowIndex. If the DataPage
   * object currently cached doesn't include that index then fetchPage is
   * called to retrieve the appropriate page.
   */
  @Override
  public Object getRowData() {
    System.out.println("getRowData");
    if (rowIndex < 0) {
      throw new IllegalArgumentException("Invalid rowIndex for PagedListDataModel; not within page");
    }

    // ensure page exists; if rowIndex is beyond dataset size, then
    // we should still get back a DataPage object with the dataset size
    // in it...
    if (page == null) {
      page = suggestFetchPage(rowIndex, pageSize);
    }

    // Check if rowIndex is equal to startRow,
    // useful for dynamic sorting on pages

    if (rowIndex == page.getStartRow()) {
      page = suggestFetchPage(rowIndex, pageSize);
    }

    int datasetSize = page.getDatasetSize();
    int startRow = page.getStartRow();
    int nRows = page.getData().size();
    int endRow = startRow + nRows;

    if (rowIndex >= datasetSize) {
      throw new IllegalArgumentException("Invalid rowIndex");
    }

    if (rowIndex < startRow) {
      page = suggestFetchPage(rowIndex, pageSize);
      startRow = page.getStartRow();
    } else if (rowIndex >= endRow) {
      page = suggestFetchPage(rowIndex, pageSize);
      startRow = page.getStartRow();
    }

    return page.getData().get(rowIndex - startRow);
  }

  @Override
  public Object getWrappedData() {
    return page.getData();
  }

  /**
   * Return true if the rowIndex value is currently set to a value that
   * matches some element in the dataset. Note that it may match a row that is
   * not in the currently cached DataPage; if so then when getRowData is
   * called the required DataPage will be fetched by calling fetchData.
   */
  @Override
  public boolean isRowAvailable() {
    System.out.println("isRowAvailable");
    if ((getPage() == null) || (getRowIndex() < 0) ||
        (getRowIndex() >= getPage().getDatasetSize())) {
      return false;
    }
    return true;
  }

  /**
   * the jsf framework can be funky.  It could ask for the same start row multiple times within the
   * same cycle.   Therefore, we cache the results for the startRow and make sure that if the framework
   * asks for them again in very short order that we simply returned the cached value.   What the
   * extended class could do in fetchPage could be costly, so we make sure it is called only when needed!
   *
   * @param startRow
   * @param pageSize
   * @return
   */
  public DataPage<S> suggestFetchPage(int startRow, int pageSize) {
    System.out.println("suggestFetchPage: " + startRow + ", " + pageSize);
    if (this.lastStartRow == startRow) {
      return this.lastPage;
    }

    this.lastStartRow = startRow;
    this.lastPage = fetchPage(startRow, pageSize);
    return this.lastPage;
  }

  public DataPage<S> fetchPage(int startRow, int pageSize) {
    System.out.println("getDataPage: " + startRow + ", " + pageSize);
    final FindCriteria findCriteria =
      (FindCriteria)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(FindCriteria.class));
    findCriteria.setFetchStart(0);
    findCriteria.setFetchSize(-1);
    findCriteria.setExcludeAttribute(false);

    System.out.println("filterModel: " + filterModel);

    if ((filterModel != null) && (filterModel.getFilterCriteria() != null) &&
        !filterModel.getFilterCriteria().entrySet().isEmpty()) {
      final List<Object> item =
        new ArrayList<Object>(filterModel.getFilterCriteria().entrySet().size());

      for (final Map.Entry<String, Object> entry :
           filterModel.getFilterCriteria().entrySet()) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("key: " + entry.getKey());
        System.out.println("value: " + entry.getValue());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<");

        if ((entry.getValue() != null) &&
            (entry.getValue().toString().trim().length() > 0)) {
          final ViewCriteriaItem viewCriteriaItem =
            (ViewCriteriaItem)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(ViewCriteriaItem.class));
          viewCriteriaItem.setConjunction("And");
          viewCriteriaItem.setUpperCaseCompare(true);
          viewCriteriaItem.setAttribute(entry.getKey());
          viewCriteriaItem.setOperator("like");
          final List<Object> value = new ArrayList<Object>(1);
          value.add(entry.getValue());
          viewCriteriaItem.setValue(value);
          item.add(viewCriteriaItem);

          System.out.println("viewCriteriaItem added: " + viewCriteriaItem);
        }
      }

      if (!item.isEmpty()) {
        findCriteria.setFilter((ViewCriteria)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(ViewCriteria.class)));
        findCriteria.getFilter().setGroup(new ArrayList(1));
        final ViewCriteriaRow viewCriteriaRow =
          (ViewCriteriaRow)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(ViewCriteriaRow.class));
        viewCriteriaRow.setUpperCaseCompare(true);
        viewCriteriaRow.setItem(item);
        findCriteria.getFilter().getGroup().add(viewCriteriaRow);
      }
    }

    System.out.println("sortCriteria: " + getSortCriteria());

    if (getSortCriteria() != null) {
      final List<SortAttribute> sortAttributes =
        new ArrayList<SortAttribute>(getSortCriteria().size());

      for (SortCriterion sortCriterion : getSortCriteria()) {
        final SortAttribute sortAttribute =
          (SortAttribute)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(SortAttribute.class));
        System.out.println(">>> sortCriterion: " +
                           sortCriterion.getProperty() + " = " +
                           sortCriterion.isAscending());
        sortAttribute.setName(sortCriterion.getProperty());
        sortAttribute.setDescending(!sortCriterion.isAscending());
        sortAttributes.add(sortAttribute);
      }

      final SortOrder sortOrder =
        (SortOrder)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(SortOrder.class));
      sortOrder.setSortAttribute(sortAttributes);

      findCriteria.setSortOrder(sortOrder);
    }

    final FindControl findControl =
      (FindControl)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(FindControl.class));
    findControl.setRetrieveAllTranslations(false);

    final int size =
      service.count(implementation, findCriteria, findControl).intValue();

    System.out.println("size: " + size);

    findCriteria.setFetchStart(startRow);
    findCriteria.setFetchSize(pageSize);

    return new DataPage<S>(size, startRow,
                           service.find(implementation, findCriteria,
                                        findControl));
  }

  public String toString() {
    return new StringBuilder("pageSize=").append(pageSize).append("\n,rowIndex=").append(getRowIndex()).append("\n,rowCount=").append(getRowCount()).append("\n,page=").append((page !=
                                                                                                                                                                                null ?
                                                                                                                                                                                Arrays.toString(page.getData().toArray()) :
                                                                                                                                                                                null)).append("\n,lastPage=").append((lastPage !=
                                                                                                                                                                                                                      null ?
                                                                                                                                                                                                                      Arrays.toString(lastPage.getData().toArray()) :
                                                                                                                                                                                                                      null)).toString();
  }

  public Object getRowKey() {
    if (isRowAvailable()) {
      final Object key =
        (getRowData() != null ? ((S)getRowData()).toEntity().getId() : null);

      System.out.println("getRowKey: " + key);

      return key;
    }

    return null;
  }

  public void setRowKey(Object key) {
    System.out.println("setRowKey: " + key);

    if (key != null) {
      for (int index = 0; index < page.getData().size(); index++) {
        if (page.getData().get(index).toEntity().getId().equals(key)) {
          setRowIndex(index);
          return;
        }
      }
    }
  }
}
