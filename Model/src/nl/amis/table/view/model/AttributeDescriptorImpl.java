package nl.amis.table.view.model;


import commonj.sdo.Property;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import oracle.adf.view.rich.model.AttributeDescriptor;


public class AttributeDescriptorImpl extends AttributeDescriptor {
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
  
  private String name = null;
  private boolean readOnly = false;
  private boolean required = false;
  private Class implementation = null;
  
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
    return -1;
  }

  public int getMaximumLength() {
    System.out.println("getMaximumLength: ");
    return -1;
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
    final Set<AttributeDescriptor.Operator> operators = new HashSet<AttributeDescriptor.Operator>(8);
    operators.add(new OperatorImpl(""));
    operators.add(new OperatorImpl("Equals"));
    operators.add(new OperatorImpl("Not Equals"));
    
    if (getType().isAssignableFrom(Date.class) || getType().isAssignableFrom(Number.class)) {
      operators.add(new OperatorImpl("Greater Than"));
      operators.add(new OperatorImpl("Less Than"));
      operators.add(new OperatorImpl("Greater Than Equals"));
      operators.add(new OperatorImpl("Less Than Equals"));
      operators.add(new OperatorImpl("Between"));
    }
    else if (getType().isAssignableFrom(String.class)) {
      operators.add(new OperatorImpl("Like"));
      operators.add(new OperatorImpl("Starts With"));
      operators.add(new OperatorImpl("Ends  With"));
      operators.add(new OperatorImpl("Contains"));
      operators.add(new OperatorImpl("Does not Contain"));
    }
    
    System.out.println("getSupportedOperators: " + operators);

    return operators;
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

  /*public int getWidth() {
      System.out.println("getWidth: ");
      return 0;
    }

    public String getAlign() {
      System.out.println("getAlign: ");
      return "left";
    }*/


  public String toString() {
    return new StringBuilder(getClass().getName()).append("@").append(hashCode()).append("[name=").append(getName()).append("]").toString();
  }
}
