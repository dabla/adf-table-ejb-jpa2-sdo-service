package nl.amis.table.view.model;


import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.amis.sdo.jpa.services.Service;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.Criterion;
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
public class PagedListDataModel<S> extends CollectionModel implements ChangeListener {
  protected static final Logger logger = Logger.getLogger(PagedListDataModel.class.getName());
  private int pageSize;
  private int rowIndex = 0;
  private int lastStartRow = -1;
  private DataPage<S> page;
  private DataPage<S> lastPage;
  private final Service service;
  private final Class<S> implementation;
  private final FilterableQueryDescriptor filterModel;
  private final ObservableBoolean changed = new ObservableBoolean(this);
  
  //private final QueryModel queryModel;
  private List<SortCriterion> sortCriteria = new ArrayList<SortCriterion>(1);

  /*
          * Create a datamodel that pages through the data showing the specified
          * number of rows on each page.
          */

  public PagedListDataModel(final Service service,
                            final Class<S> implementation,
                            final int pageSize) {
    super();
    this.service = service;
    this.implementation = implementation;  // dirty solution, should find a beter way to resolve entity class
    this.filterModel = new FilterableQueryDescriptorImpl<S>(implementation, changed);
    this.pageSize = pageSize;
    this.rowIndex = 0;
    this.page = null;
    
    //oracle.adfinternal.view.faces.model.AdfListELResolver d;
  }

  public void invalidate() {
    System.out.println("invalidate");
    this.rowIndex = 0;
    this.lastStartRow = -1;
    this.page = null;
    this.lastPage = null;
    this.changed.setValue(false);
  }

  public void setSortCriteria(final List<SortCriterion> sortCriteria) {
    logger.log(Level.FINEST, "setSortCriteria: {0}", sortCriteria);
    this.sortCriteria = sortCriteria;
    this.changed.setValue(true);
  }

  public List<SortCriterion> getSortCriteria() {
    logger.log(Level.FINEST, "getSortCriteria: {0}", sortCriteria);
    return sortCriteria;
  }

  public FilterableQueryDescriptor getFilterModel() {
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
    logger.log(Level.FINEST, "getRowIndex: {0}", rowIndex);
    return rowIndex;
  }

  /**
   * Specify what the "current row" within the dataset is. Note that the
   * UIData component will repeatedly call this method followed by getRowData
   * to obtain the objects to render in the table.
   */
  @Override
  public void setRowIndex(int index) {
    logger.log(Level.FINEST, "setRowIndex: {0}", index);
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
    // ensure page exists; if rowIndex is beyond dataset size, then
    // we should still get back a DataPage object with the dataset size
    // in it...
    if ((page == null) || (getRowIndex() <= page.getStartRow()) || (getRowIndex() >= (page.getStartRow() + page.getData().size()))) {
      page = suggestFetchPage((getRowIndex() == -1 ? 0 : getRowIndex()), pageSize);
    }
    
    return page;
  }

  /**
   * Return the object corresponding to the current rowIndex. If the DataPage
   * object currently cached doesn't include that index then fetchPage is
   * called to retrieve the appropriate page.
   */
  @Override
  public Object getRowData() {
    logger.finest("getRowData");

    final DataPage<S> page = getPage();
    return page.getData().get(rowIndex - page.getStartRow());
  }

  @Override
  public Object getWrappedData() {
    return getPage().getData();
  }

  /**
   * Return true if the rowIndex value is currently set to a value that
   * matches some element in the dataset. Note that it may match a row that is
   * not in the currently cached DataPage; if so then when getRowData is
   * called the requiredType DataPage will be fetched by calling fetchData.
   */
  @Override
  public boolean isRowAvailable() {
    logger.finest("isRowAvailable");
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
    logger.log(Level.FINEST, "suggestFetchPage: {0}, {1}", new Object[]{startRow,pageSize});
    if (this.lastStartRow == startRow) {
      return this.lastPage;
    }

    this.lastStartRow = startRow;
    this.lastPage = fetchPage(startRow, pageSize);
    return this.lastPage;
  }

  public DataPage<S> fetchPage(int startRow, int pageSize) {
    System.out.println("fetchPage is being performed: " + startRow + ", " + pageSize);
    
    logger.log(Level.FINEST, "fetchPage: {0}, {1}", new Object[]{startRow,pageSize});
    final FindCriteria findCriteria =
      (FindCriteria)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(FindCriteria.class));
    findCriteria.setFetchStart(0);
    findCriteria.setFetchSize(-1);
    findCriteria.setExcludeAttribute(false);

    logger.log(Level.FINEST, "filterModel: {0}", getFilterModel());
    
    final List<Object> item =
      new ArrayList<Object>(getFilterModel().getFilterCriteria().entrySet().size());

    if ((getFilterModel().getFilterCriteria() != null) &&
        !getFilterModel().getFilterCriteria().entrySet().isEmpty()) {

      for (final Map.Entry<String, Object> entry :
           getFilterModel().getFilterCriteria().entrySet()) {
        logger.log(Level.FINEST, "key: {0}", entry.getKey());
        logger.log(Level.FINEST, "value: {0}", entry.getValue());

        if ((entry.getValue() != null) &&
            (entry.getValue().toString().trim().length() > 0)) {
          final List<Object> value = new ArrayList<Object>(1);
          value.add(entry.getValue());
          final ViewCriteriaItem viewCriteriaItem = toViewCriteriaItem(entry.getKey(), value, "like");
          logger.log(Level.FINEST, "viewCriteriaItem: {0}", viewCriteriaItem);
          item.add(viewCriteriaItem);
        }
      }
    }
    
    System.out.println("currentCriterion: " + getFilterModel().getCurrentCriterion());
    
    /*if (!getFilterModel().getCurrentCriterion().getValues().isEmpty()) {
      System.out.println(">>> value: " + getFilterModel().getCurrentCriterion().getValues().get(0));
      if (getFilterModel().getCurrentCriterion().getValues().get(0).toString().trim().length() > 0)
      {
      final ViewCriteriaItem viewCriteriaItem = toViewCriteriaItem(getFilterModel().getCurrentCriterion());
      System.out.println(">>> viewCriteriaItem: " + viewCriteriaItem);
      logger.log(Level.FINEST, "viewCriteriaItem: {0}", viewCriteriaItem);
      item.add(viewCriteriaItem);
      }
    }*/

    if (!getFilterModel().getConjunctionCriterion().getCriterionList().isEmpty()) {
      for (final Criterion criterion :
           getFilterModel().getConjunctionCriterion().getCriterionList()) {
        final AttributeCriterion attributeCriterion = (AttributeCriterion)criterion;
        if (String.valueOf(attributeCriterion.getValues().get(0)).trim().length() > 0) {
          final ViewCriteriaItem viewCriteriaItem = toViewCriteriaItem(attributeCriterion);
          System.out.println(">>> viewCriteriaItem: " + viewCriteriaItem);
          logger.log(Level.FINEST, "viewCriteriaItem: {0}", viewCriteriaItem);
          item.add(viewCriteriaItem);
        }
      }
    }
    
    if (!item.isEmpty()) {
      findCriteria.setFilter((ViewCriteria)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(ViewCriteria.class)));
      findCriteria.getFilter().setGroup(new ArrayList(1));
      final ViewCriteriaRow viewCriteriaRow =
        (ViewCriteriaRow)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(ViewCriteriaRow.class));
      viewCriteriaRow.setConjunction("And");
      viewCriteriaRow.setUpperCaseCompare(true);
      viewCriteriaRow.setItem(item);
      findCriteria.getFilter().getGroup().add(viewCriteriaRow);
    }

    logger.log(Level.FINEST, "sortCriteria: {0}", getSortCriteria());

    if (getSortCriteria() != null) {
      final List<SortAttribute> sortAttributes =
        new ArrayList<SortAttribute>(getSortCriteria().size());

      for (SortCriterion sortCriterion : getSortCriteria()) {
        final SortAttribute sortAttribute =
          (SortAttribute)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(SortAttribute.class));
        logger.log(Level.FINEST, "sortCriterion: {0}, {1}", new Object[]{sortCriterion.getProperty(),sortCriterion.isAscending()});
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

    logger.log(Level.FINEST, "size: {0}", size);

    findCriteria.setFetchStart(startRow);
    findCriteria.setFetchSize(pageSize);

    return new DataPage<S>(size, startRow,
                           service.find(implementation, findCriteria,
                                        findControl));
  }
  
  protected ViewCriteriaItem toViewCriteriaItem(final AttributeCriterion criterion) {
    return toViewCriteriaItem(criterion.getAttribute().getName(), criterion.getValues(), criterion.getOperator().toString());
  }
  
  protected ViewCriteriaItem toViewCriteriaItem(final String name, final List value, final String operator) {
    final ViewCriteriaItem viewCriteriaItem =
      (ViewCriteriaItem)DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(ViewCriteriaItem.class));
    //viewCriteriaItem.setConjunction("And");
    viewCriteriaItem.setConjunction(getFilterModel().getConjunctionCriterion().getConjunction().equals(ConjunctionCriterion.Conjunction.AND) ? "And" : "Or");
    viewCriteriaItem.setUpperCaseCompare(true);
    viewCriteriaItem.setAttribute(name);
    viewCriteriaItem.setOperator(operator);
    viewCriteriaItem.setValue(value);
    return viewCriteriaItem;
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
        (getRowData() != null ? ((S)getRowData()).hashCode() : null);

      logger.log(Level.FINEST, "getRowKey: {0}", key);

      return key;
    }

    return null;
  }

  public void setRowKey(Object key) {
    logger.log(Level.FINEST, "setRowKey: {0}", key);

    if (key != null) {
      for (int index = 0; index < page.getData().size(); index++) {
        if (Integer.valueOf(page.getData().get(index).hashCode()).equals(key)) {
          setRowIndex(index);
          return;
        }
      }
    }
  }
  
  public void stateChanged(final ChangeEvent e) {
    System.out.println("stateChanged: " + ((ObservableBoolean)e.getSource()).booleanValue());
    
    if (((ObservableBoolean)e.getSource()).booleanValue()) {
      invalidate();
    }
  }
}
