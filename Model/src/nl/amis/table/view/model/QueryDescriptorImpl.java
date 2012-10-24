package nl.amis.table.view.model;

import java.util.Collections;
import java.util.Map;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.QueryDescriptor;

public class QueryDescriptorImpl extends QueryDescriptor {
  private final ConjunctionCriterion conjunctionCriterion;
  
  public QueryDescriptorImpl(final ConjunctionCriterion conjunctionCriterion) {
    this.conjunctionCriterion = conjunctionCriterion;
  }

  public void addCriterion(String name) {
  }

  public void changeMode(QueryDescriptor.QueryMode mode) {
  }

  public ConjunctionCriterion getConjunctionCriterion() {
    return conjunctionCriterion;
  }

  public String getName() {
    return null;
  }

  public Map<String, Object> getUIHints() {
    return Collections.emptyMap();
  }

  public void removeCriterion(oracle.adf.view.rich.model.Criterion object) {
  }

  public AttributeCriterion getCurrentCriterion() {
    return null;
  }

  public void setCurrentCriterion(AttributeCriterion attrCriterion) {
  }
}
