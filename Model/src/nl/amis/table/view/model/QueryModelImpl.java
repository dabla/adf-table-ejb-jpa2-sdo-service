package nl.amis.table.view.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.AttributeDescriptor;
import oracle.adf.view.rich.model.Criterion;
import oracle.adf.view.rich.model.QueryDescriptor;
import oracle.adf.view.rich.model.QueryModel;


public class QueryModelImpl extends QueryModel {
  private QueryDescriptor queryDescriptor = null;
  private final List<AttributeDescriptor> attributes;
  private final ObservableBoolean changed;
  
  public QueryModelImpl(final QueryDescriptor queryDescriptor, final ObservableBoolean changed) {
    this.queryDescriptor = queryDescriptor;
    this.attributes = new ArrayList<AttributeDescriptor>();
    this.changed = changed;
  }

  public QueryDescriptor create(String string,
                                QueryDescriptor queryDescriptor) {
    System.out.println("create: " + string + ", " + queryDescriptor);
    return null;
  }

  public void delete(QueryDescriptor queryDescriptor) {
    System.out.println("delete: " + queryDescriptor);
  }

  public List<AttributeDescriptor> getAttributes() {
    System.out.println("getAttributes: " + this.attributes);
    return this.attributes;
  }

  public List<QueryDescriptor> getSystemQueries() {
    System.out.println("getSystemQueries");
    return Collections.emptyList();
  }

  public List<QueryDescriptor> getUserQueries() {
    System.out.println("getUserQueries");
    return Collections.emptyList();
  }

  public void reset(QueryDescriptor queryDescriptor) {
    System.out.println("reset: " + queryDescriptor);
    for(final Criterion criterion : queryDescriptor.getConjunctionCriterion().getCriterionList()) {
      final List values = ((AttributeCriterion)criterion).getValues();
      values.clear();
      values.add("");
    }
    
    changed.setValue(true);
  }

  public void setCurrentDescriptor(QueryDescriptor queryDescriptor) {
    System.out.println("setCurrentDescriptor: " + queryDescriptor);
    this.queryDescriptor = queryDescriptor;
  }

  public void update(QueryDescriptor queryDescriptor,
                     Map<String, Object> map) {
    System.out.println("update: " + queryDescriptor + ", " + map);
  }
}
