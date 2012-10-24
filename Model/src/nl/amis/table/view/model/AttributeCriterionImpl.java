package nl.amis.table.view.model;


import commonj.sdo.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.AttributeDescriptor;
import oracle.adf.view.rich.model.Criterion;


public class AttributeCriterionImpl extends AttributeCriterion {
  private final List values;
  private final AttributeDescriptor attributeDescriptor;
  private AttributeDescriptor.Operator operator = null;
  private RequiredType requiredType = RequiredType.OPTIONAL;
  private boolean matchCase = false;
  
  private final class ObservableArrayList extends ArrayList {
    private final ObservableBoolean changed;
    ObservableArrayList(final ObservableBoolean changed) {
      super(1);
      this.changed = changed;
      add("");
    }
    
    public Object set(final int index, final Object value) {
      changed.setValue(changed.booleanValue() || !value.equals(get(index)));
      System.out.println("SET: " + index + ", is: " + value + ", was:" + get(index) + ", changed: " + changed);
      return super.set(index, value);
    }
  }

  public AttributeCriterionImpl(final Property property, final ObservableBoolean changed) {
    super();
    values = new ObservableArrayList(changed);
    attributeDescriptor = new AttributeDescriptorImpl(property);
    operator = attributeDescriptor.getSupportedOperators().iterator().next();
  }

  public AttributeDescriptor getAttribute() {
    System.out.println("getAttribute: " + attributeDescriptor);
    return attributeDescriptor;
  }

  public Map<String, AttributeDescriptor.Operator> getOperators() {
    System.out.println("getOperators: ");
    return Collections.emptyMap();
  }

  public List<? extends Object> getValues() {
    System.out.println("getValues: " + values);
    return values;
  }

  public boolean isRemovable() {
    System.out.println("isRemovable: ");
    return false;
  }

  public void setOperator(AttributeDescriptor.Operator operator) {
    System.out.println("setOperator: " + operator);
    this.operator = operator;
  }
  
  public AttributeDescriptor.Operator getOperator() {
    System.out.println("getOperator: " + operator);
    return operator;
  }

  public boolean hasDependentCriterion(int index) {
    System.out.println("hasDependentCriterion: " + index);
    return false;
  }

  public void setMatchCase(boolean matchCase) {
    System.out.println("setMatchCase: " + matchCase);
    this.matchCase = matchCase;
  }

  public boolean getMatchCase() {
    System.out.println("getMatchCase: " + matchCase);
    return matchCase;
  }

  public void setRequiredType(RequiredType requiredType) {
    System.out.println("setRequiredType: " + requiredType);
    this.requiredType = requiredType;
  }

  public RequiredType getRequiredType() {
    System.out.println("getRequiredType: " + requiredType);
    return requiredType;
  }
}
