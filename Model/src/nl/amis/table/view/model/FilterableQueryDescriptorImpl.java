package nl.amis.table.view.model;


import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.Criterion;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;
import oracle.adf.view.rich.model.QueryDescriptor;
import oracle.adf.view.rich.model.QueryModel;


public class FilterableQueryDescriptorImpl<S> extends FilterableQueryDescriptor {
  private AttributeCriterion attributeCriterion = null;
  private final ConjunctionCriterion conjunctionCriterion;
  private final QueryModel queryModel;
  private final Map<String, Object> uiHints = new HashMap<String, Object>();
  private Map<String, Object> filterCriteria = null;
  
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
      return super.put(key, value);
    }
  }
  
  public FilterableQueryDescriptorImpl(final Class<S> implementation, final ObservableBoolean changed) {
    this.conjunctionCriterion = new ConjunctionCriterionImpl(DataFactory.INSTANCE.create(TypeHelper.INSTANCE.getType(implementation)).getInstanceProperties(), changed);
    this.filterCriteria = new ObservableHashMap(conjunctionCriterion.getCriterionList(), changed);
    this.queryModel = new QueryModelImpl(this, changed);
     this.uiHints.put(QueryDescriptor.UIHINT_AUTO_EXECUTE, Boolean.TRUE);
    this.uiHints.put(QueryDescriptor.UIHINT_DEFAULT, Boolean.TRUE);
    this.uiHints.put(QueryDescriptor.UIHINT_IMMUTABLE, Boolean.FALSE);
    this.uiHints.put(QueryDescriptor.UIHINT_MODE, QueryMode.ADVANCED);
    this.uiHints.put(QueryDescriptor.UIHINT_SAVE_RESULTS_LAYOUT, Boolean.TRUE);
    this.uiHints.put(QueryDescriptor.UIHINT_SHOW_IN_LIST, Boolean.TRUE);
    this.attributeCriterion = (AttributeCriterion)conjunctionCriterion.getCriterion(0);
  }

  public void setFilterCriteria(final Map<String, Object> filterCriteria) {
    this.filterCriteria = filterCriteria;
  }
  
  public Map<String, Object> getFilterCriteria() {
    return filterCriteria;
  }
  
  public QueryModel getQueryModel() {
    return queryModel;
  }

  public void changeMode(QueryDescriptor.QueryMode queryMode) {
    System.out.println("changeMode: " + queryMode);
  }

  public ConjunctionCriterion getConjunctionCriterion() {
    return conjunctionCriterion;
  }

  public String getName() {
    return null;
  }

  public Map<String, Object> getUIHints() {
    return uiHints;
  }
  
  public void addCriterion(String string) {
    //System.out.println("addCriterion: " + string);
  }

  public void removeCriterion(oracle.adf.view.rich.model.Criterion criterion) {
    //System.out.println("removeCriterion: " + criterion);
  }

  public AttributeCriterion getCurrentCriterion() {
    return attributeCriterion;
  }

  public void setCurrentCriterion(AttributeCriterion attributeCriterion) {
    this.attributeCriterion = attributeCriterion;
  }
}