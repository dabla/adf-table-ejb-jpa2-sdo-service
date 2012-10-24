package nl.amis.table.view.model;

import commonj.sdo.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.adf.view.rich.model.AttributeDescriptor;
import oracle.adf.view.rich.model.ColumnDescriptor;

public class AttributeDescriptorImpl extends ColumnDescriptor {
  private static final Set<AttributeDescriptor.Operator> BOOLEAN_OPERATORS =
    new HashSet<AttributeDescriptor.Operator>(3);
  {
    BOOLEAN_OPERATORS.add(new OperatorImpl(""));
    BOOLEAN_OPERATORS.add(new OperatorImpl("Equals"));
    BOOLEAN_OPERATORS.add(new OperatorImpl("Not Equals"));
  }

  private static final Set<AttributeDescriptor.Operator> DATE_NUMBER_OPERATORS =
    new HashSet<AttributeDescriptor.Operator>(8);
  {
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(""));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl("Equals"));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl("Not Equals"));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl("Greater Than"));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl("Less Than"));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl("Greater Than Equals"));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl("Less Than Equals"));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl("Between"));
  }

  private static final Set<AttributeDescriptor.Operator> STRING_OPERATORS =
    new HashSet<AttributeDescriptor.Operator>(8);
  {
    STRING_OPERATORS.add(new OperatorImpl(""));
    STRING_OPERATORS.add(new OperatorImpl("Equals"));
    STRING_OPERATORS.add(new OperatorImpl("Not Equals"));
    STRING_OPERATORS.add(new OperatorImpl("Like"));
    STRING_OPERATORS.add(new OperatorImpl("Starts With"));
    STRING_OPERATORS.add(new OperatorImpl("Ends  With"));
    STRING_OPERATORS.add(new OperatorImpl("Contains"));
    STRING_OPERATORS.add(new OperatorImpl("Does not Contain"));
  }

  public class OperatorImpl extends AttributeDescriptor.Operator {
    private final String label;

    public OperatorImpl(final String label) {
      this.label = label;
    }

    public String getLabel() {
      System.out.println("getLabel: " + label);
      return label;
    }

    public Object getValue() {
      System.out.println("getValue: ");
      return null;
    }

    /**
     * Returns the number of operands required by an OperatorType instance. This may be useful in
     * determining the number of input components to display for the operator and attribute.
     * @return an int
     */
    public int getOperandCount() {

      if ("Between".equals(getLabel())) {
        System.out.println("getOperandCount: 2");
        return 2;
      }
      if ("In".equals(getLabel()) || "Not In".equals(getLabel())) {
        System.out.println("getOperandCount: -1");
        return -1;
      }

      System.out.println("getOperandCount: 1");

      return 1;
    }
  }

  private final String name;
  private final boolean readOnly;
  private final boolean required;
  private final Class implementation;

  public AttributeDescriptorImpl(final Property property) {
    this.name = property.getName();
    this.readOnly = property.isReadOnly();
    this.required = !property.isNullable();
    this.implementation = property.getType().getInstanceClass();

    System.out.println("SDO Property: " + property.getName() + ", " +
                       property.getType().getName() + ", " +
                       property.getOpposite() + ", " + property.getDefault() +
                       ", " + property.isReadOnly());
  }

  public AttributeDescriptor.ComponentType getComponentType() {
    System.out.println("getComponentType: ");
    if (isReadOnly()) {
      return AttributeDescriptor.ComponentType.selectOneChoice;
    }

    if (getType().isAssignableFrom(Date.class)) {
      return AttributeDescriptor.ComponentType.inputDate;
    }

    return AttributeDescriptor.ComponentType.inputText;
  }

  public String getDescription() {
    System.out.println("getDescription: ");
    return "";
  }

  public String getLabel() {
    System.out.println("getLabel: " + name);
    return name;
  }

  public int getLength() {
    System.out.println("getLength: ");
    return 0;
  }

  public int getMaximumLength() {
    System.out.println("getMaximumLength: ");
    return 0;
  }

  public Object getModel() {
    System.out.println("getModel: ");
    return null;
  }

  public String getName() {
    System.out.println("getName: " + name);
    return name;
  }

  public Set<AttributeDescriptor.Operator> getSupportedOperators() {
    System.out.println("getSupportedOperators: ");

    if (getType().isAssignableFrom(Date.class) ||
        getType().isAssignableFrom(Number.class)) {
      return DATE_NUMBER_OPERATORS;
    } else if (getType().isAssignableFrom(String.class)) {
      return STRING_OPERATORS;
    }

    return BOOLEAN_OPERATORS;
  }

  public Class getType() {
    System.out.println("getType: " + this.implementation);
    return this.implementation;
  }

  public boolean isReadOnly() {
    System.out.println("isReadOnly: " + readOnly);
    return readOnly;
  }

  public boolean isRequired() {
    System.out.println("isRequired: " + required);
    return required;
  }

  public int getWidth() {
    System.out.println("getWidth: 0");
    return 0;
  }

  public String getAlign() {
    System.out.println("getAlign: ");
    if (getType().isAssignableFrom(Date.class) ||
        getType().isAssignableFrom(Number.class)) {
      return "right";
    }
    return "left";
  }


  public String toString() {
    return new StringBuilder(getClass().getName()).append("@").append(hashCode()).append("[name=").append(getName()).append("]").toString();
  }
}
