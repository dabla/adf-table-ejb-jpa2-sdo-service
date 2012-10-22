package nl.amis.table.view.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;
import oracle.adf.view.rich.model.QueryDescriptor;

public class FilterableQueryDescriptorImpl extends FilterableQueryDescriptor {
  private final Map<String, Object> filterCriteria =
    new HashMap<String, Object>(1);

  public Map<String, Object> getFilterCriteria() {
    System.out.println("getFilterCriteria: " + filterCriteria);
    return filterCriteria;
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
    System.out.println("getConjunctionCriterion: ");
    return null;
  }

  public String getName() {
    System.out.println("getName: ");
    return null;
  }

  public Map<String, Object> getUIHints() {
    System.out.println("getUIHints: ");
    return Collections.emptyMap();
  }

  public void removeCriterion(oracle.adf.view.rich.model.Criterion criterion) {
    System.out.println("removeCriterion: " + criterion);
  }

  public AttributeCriterion getCurrentCriterion() {
    System.out.println("getCurrentCriterion: ");
    return null;
  }

  public void setCurrentCriterion(AttributeCriterion attributeCriterion) {
    System.out.println("setCurrentCriterion: " + attributeCriterion);
  }
}