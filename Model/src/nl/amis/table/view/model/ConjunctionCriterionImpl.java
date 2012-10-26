package nl.amis.table.view.model;


import commonj.sdo.Property;

import java.util.ArrayList;
import java.util.List;

import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.Criterion;


public class ConjunctionCriterionImpl extends ConjunctionCriterion {
  private final List<Criterion> criterionList;
  private ConjunctionCriterion.Conjunction conjunction = ConjunctionCriterion.Conjunction.AND;
  
  public ConjunctionCriterionImpl(final List<Property> properties, final ObservableBoolean changed) {
    super();
    
    this.criterionList = new ArrayList<Criterion>(properties.size());
    
    for (final Property property : properties) {
      this.criterionList.add(new AttributeCriterionImpl(property, changed));
    }
  }
  
  public void setConjunction(final ConjunctionCriterion.Conjunction conjunction) {
    //System.out.println("setConjunction: " + conjunction);
    this.conjunction = conjunction;
  }

  public ConjunctionCriterion.Conjunction getConjunction() {
    //System.out.println("getConjunction: " + conjunction);
    return conjunction;
  }

  public List<Criterion> getCriterionList() {
    //System.out.println("getCriterionList: " + criterionList);
    return criterionList;
  }

  public Object getKey(final Criterion criterion) {
    //System.out.println("getKey: " + criterion);
    return criterionList.indexOf(criterion);
  }

  public Criterion getCriterion(final Object object) {
    //System.out.println("getCriterion: " + object);
    
    return criterionList.get(getIndex(object));
  }
  
  private int getIndex(final Object object) {
    if (object instanceof String) {
        return Integer.valueOf((String)object);
    }
    
    if (object instanceof Number) {
        return ((Number)object).intValue();
    }
    
    return 0;
  }
}
