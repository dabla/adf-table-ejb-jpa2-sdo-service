package nl.amis.table.view.model;


import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.amis.sdo.jpa.entities.BaseDataObject;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.Criterion;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;
import oracle.adf.view.rich.model.QueryDescriptor;
import oracle.adf.view.rich.model.QueryModel;


public class FilterableQueryDescriptorImpl<S extends BaseDataObject> extends FilterableQueryDescriptor {
  private AttributeCriterion attributeCriterion = null;
  private final ConjunctionCriterion conjunctionCriterion;
  private final QueryModel queryModel;
  private final Map<String, Object> filterCriteria;
  
  private final class ObservableHashMap extends HashMap<String, Object> {
    private final ObservableBoolean changed;
    
    ObservableHashMap(final List<Criterion> criterions, final ObservableBoolean changed) {
      super(criterions.size());
      
      this.changed = changed;
      
      for (final Criterion criterion : criterions) {
        super.put(((AttributeCriterion)criterion).getAttribute().getName(), "");
      }
    }
    
    @Override
    public Object put(final String key, final Object value) {
      changed.setValue(changed.booleanValue() || !value.equals(get(key)));
      System.out.println("PUT: " + key + ", is: " + value + ", was:" + get(key) + ", changed: " + changed);
      
      return super.put(key, value);
    }
  }
  
  public FilterableQueryDescriptorImpl(final Class<S> implementation, final ObservableBoolean changed) {
    this.conjunctionCriterion = new ConjunctionCriterionImpl(DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(implementation)).getInstanceProperties(), changed);
    this.filterCriteria = new ObservableHashMap(conjunctionCriterion.getCriterionList(), changed);
    this.queryModel = new QueryModelImpl(conjunctionCriterion);
  }

  public Map<String, Object> getFilterCriteria() {
    System.out.println("getFilterCriteria: " + filterCriteria);
    return filterCriteria;
  }
  
  public QueryModel getQueryModel() {
    System.out.println("getQueryModel: " + queryModel);
    return queryModel;
  }

  public void setFilterCriteria(Map<String, Object> map) {
    System.out.println("setFilterCriteria: " + map);
  }

  public void addCriterion(String string) {
    System.out.println("addCriterion: " + string);
  }

  public void changeMode(QueryDescriptor.QueryMode queryMode) {
    System.out.println("changeMode: " + queryMode);
  }

  public ConjunctionCriterion getConjunctionCriterion() {
    System.out.println("getConjunctionCriterion: " + conjunctionCriterion);
    return conjunctionCriterion;
  }

  public String getName() {
    System.out.println("getName: ");
    return null;
  }

  public Map<String, Object> getUIHints() {
    final Map<String, Object> uiHints = new HashMap<String, Object>();
            uiHints.put(QueryDescriptor.UIHINT_AUTO_EXECUTE, Boolean.TRUE);
            uiHints.put(QueryDescriptor.UIHINT_DEFAULT, Boolean.TRUE);
            uiHints.put(QueryDescriptor.UIHINT_IMMUTABLE, Boolean.FALSE);
                uiHints.put(QueryDescriptor.UIHINT_MODE, QueryMode.ADVANCED);
                //uiHints.put(QueryDescriptor.UIHINT_MODE, QueryMode.BASIC);
            uiHints.put(QueryDescriptor.UIHINT_NAME, "A");
            uiHints.put(QueryDescriptor.UIHINT_SAVE_RESULTS_LAYOUT, Boolean.TRUE);
            uiHints.put(QueryDescriptor.UIHINT_SHOW_IN_LIST, Boolean.TRUE);
    System.out.println("getUIHints: " + uiHints);
    return uiHints;
  }

  public void removeCriterion(oracle.adf.view.rich.model.Criterion criterion) {
    System.out.println("removeCriterion: " + criterion);
    this.attributeCriterion = null;
  }

  public AttributeCriterion getCurrentCriterion() {
    System.out.println("getCurrentCriterion: " + attributeCriterion);
    return attributeCriterion;
  }

  public void setCurrentCriterion(AttributeCriterion attributeCriterion) {
    System.out.println("setCurrentCriterion: " + attributeCriterion);
    this.attributeCriterion = attributeCriterion;
  }
}