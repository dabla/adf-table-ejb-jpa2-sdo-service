package nl.amis.table.view.model;


import commonj.sdo.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.AttributeDescriptor;


public class AttributeCriterionImpl extends AttributeCriterion {
  private final List<? extends Object> values;
  private final AttributeDescriptor attributeDescriptor;
  private final Map<String,AttributeDescriptor.Operator> operators;
  private final ObservableBoolean changed;
  private AttributeDescriptor.Operator operator = null;
  private RequiredType requiredType = RequiredType.OPTIONAL;
  private boolean matchCase = false;
  
  private final class ObservableArrayList extends ArrayList {
    ObservableArrayList() {
      super(1);
      add("");
      add("");
    }
    
    @Override
    public Object get(final int index) {
      System.out.println(attributeDescriptor.getName() + "-GET: " + index + ", value: " + super.get(index));
      return super.get(index);
    }
    
    @Override
    public Object set(final int index, Object value) {
      if (value == null) value = "";
      System.out.println(attributeDescriptor.getName() + "-SET: " + index + ", is: " + value + ", was:" + get(index) + ", changed: " + changed + " (" + !value.equals(get(index)) + ")");
      changed.setValue(changed.booleanValue() || !value.equals(get(index)));
      return super.set(index, value);
    }
  }

  public AttributeCriterionImpl(final Property property, final ObservableBoolean changed) {
    super();
    this.changed = changed;
    values = new ObservableArrayList();
    attributeDescriptor = new AttributeDescriptorImpl(property);
    operators = new HashMap<String,AttributeDescriptor.Operator>(attributeDescriptor.getSupportedOperators().size());
    
    final Iterator<AttributeDescriptor.Operator> i = attributeDescriptor.getSupportedOperators().iterator();
    
    while(i.hasNext()) {
      final AttributeDescriptor.Operator o = i.next();
      operators.put(o.getLabel(), o);
    }
  }

  public AttributeDescriptor getAttribute() {
    return attributeDescriptor;
  }

  public Map<String, AttributeDescriptor.Operator> getOperators() {
    return operators;
  }

  public List<? extends Object> getValues() {
    return values;
  }

  public boolean isRemovable() {
    return false;
  }

  public void setOperator(final AttributeDescriptor.Operator operator) {
    this.operator = operator;
    changed.setValue((getValues().get(0) != null) && (getValues().get(0).toString().trim().length() > 0));
  }
  
  public AttributeDescriptor.Operator getOperator() {
    return operator;
  }

  public boolean hasDependentCriterion(final int index) {
    return false;
  }

  public void setMatchCase(final boolean matchCase) {
    this.matchCase = matchCase;
  }

  public boolean getMatchCase() {
    return matchCase;
  }

  public void setRequiredType(final RequiredType requiredType) {
    this.requiredType = requiredType;
  }

  public RequiredType getRequiredType() {
    return requiredType;
  }
}