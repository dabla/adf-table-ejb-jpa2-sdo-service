package nl.amis.table.view.model;

import commonj.sdo.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Set;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.Criterion;

public class ConjunctionCriterionImpl extends ConjunctionCriterion {
  private final List<Criterion> criterionList;
  
  public ConjunctionCriterionImpl(final List<Property> properties, final ObservableBoolean changed) {
    super();
    
    this.criterionList = new ArrayList<Criterion>(properties.size());
    
    for (final Property property : properties) {
      this.criterionList.add(new AttributeCriterionImpl(property, changed));
    }
  }

  public ConjunctionCriterion.Conjunction getConjunction() {
    System.out.println("getConjunction");
    return ConjunctionCriterion.Conjunction.AND;
  }

  public List<Criterion> getCriterionList() {
    System.out.println("getCriterionList: " + criterionList);
    return criterionList;
  }

  public Object getKey(oracle.adf.view.rich.model.Criterion criterion) {
    System.out.println("getKey: " + criterion);
    return null;
  }

  public oracle.adf.view.rich.model.Criterion getCriterion(Object object) {
    System.out.println("getCriterion: " + object);
    return null;
  }

  public void setConjunction(ConjunctionCriterion.Conjunction conjunction) {
    System.out.println("setConjunction: " + conjunction);
  }
}
