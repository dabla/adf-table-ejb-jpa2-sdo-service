package nl.amis.table.view.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import oracle.adf.view.rich.model.AttributeDescriptor;
import oracle.adf.view.rich.model.QueryDescriptor;
import oracle.adf.view.rich.model.QueryModel;

public class QueryModelImpl extends QueryModel {
  public QueryModelImpl() {
    super();
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
    System.out.println("getAttributes");
    return Collections.emptyList();
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
  }

  public void setCurrentDescriptor(QueryDescriptor queryDescriptor) {
    System.out.println("setCurrentDescriptor: " + queryDescriptor);
  }

  public void update(QueryDescriptor queryDescriptor,
                     Map<String, Object> map) {
    System.out.println("update: " + queryDescriptor + ", " + map);
  }
}
