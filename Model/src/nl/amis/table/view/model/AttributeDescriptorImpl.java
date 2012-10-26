package nl.amis.table.view.model;

import commonj.sdo.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.NumberConverter;

import oracle.adf.view.rich.model.AttributeDescriptor;
import oracle.adf.view.rich.model.ColumnDescriptor;

public class AttributeDescriptorImpl extends ColumnDescriptor {
  private static final Set<AttributeDescriptor.Operator> BOOLEAN_OPERATORS =
    new HashSet<AttributeDescriptor.Operator>(3);
  {
    BOOLEAN_OPERATORS.add(new OperatorImpl("Equals"));
    BOOLEAN_OPERATORS.add(new OperatorImpl("Not Equals"));
  }

  private static final Set<AttributeDescriptor.Operator> DATE_NUMBER_OPERATORS =
    new HashSet<AttributeDescriptor.Operator>(8);
  {
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
    private final int operand;

    public OperatorImpl(final String label) {
      this.label = label;
      
      if ("Between".equals(label)) {
        operand = 2;
      }
      else if ("In".equals(label) || "Not In".equals(label)) {
        operand = -1;
      }
      else {
        operand = 1;
      }
    }

    public String getLabel() {
      return label;
    }

    public Object getValue() {
      return null;
    }
    
    @Override
    public String toString() {
      /*if ("Like".equalsIgnoreCase(getLabel())) return "like";
      if ("Equals".equalsIgnoreCase(getLabel())) return "=";
      if ("Not Equals".equalsIgnoreCase(getLabel())) return "<>";
      if ("Greater Than".equalsIgnoreCase(getLabel())) return ">";
      if ("Less Than".equalsIgnoreCase(getLabel())) return "<";
      if ("Greater Than Equals".equalsIgnoreCase(getLabel())) return ">=";
      if ("Less Than Equals".equalsIgnoreCase(getLabel())) return "<=";*/
      //if ("Between".equalsIgnoreCase(getLabel())) return "><";
      return "like";
    }

    /**
     * Returns the number of operands required by an OperatorType instance. This may be useful in
     * determining the number of input components to display for the operator and attribute.
     * @return an int
     */
    public int getOperandCount() {
      return operand;
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
  }

  public AttributeDescriptor.ComponentType getComponentType() {
    if (isReadOnly()) {
      return AttributeDescriptor.ComponentType.selectOneChoice;
    }

    if (getType().isAssignableFrom(Date.class)) {
      return AttributeDescriptor.ComponentType.inputDate;
    }
    
    return AttributeDescriptor.ComponentType.inputText;
  }

  public String getDescription() {
    return "";
  }

  public String getLabel() {
    return name;
  }

  public int getLength() {
    return 0;
  }

  public int getMaximumLength() {
    return 0;
  }

  public Object getModel() {
    return null;
  }

  public String getName() {
    return name;
  }

  public Set<AttributeDescriptor.Operator> getSupportedOperators() {
    if (getType().isAssignableFrom(Date.class) ||
        getType().isAssignableFrom(Number.class)) {
      return DATE_NUMBER_OPERATORS;
    }
    
    if (getType().isAssignableFrom(String.class)) {
      return STRING_OPERATORS;
    }

    return BOOLEAN_OPERATORS;
  }
  
  /*@Override
  public Converter getConverter() {
      if (getType().isAssignableFrom(Number.class)) {
          return new NumberConverter();
      }
      
      if (getType().isAssignableFrom(Date.class)) {
          return new DateTimeConverter();
      }
      
      return new Converter() {
          public Object getAsObject(final FacesContext facesContext, final UIComponent uiComponent, final String string) {
              return string;
          }
          public String getAsString(final FacesContext facesContext, final UIComponent uiComponent, final Object object) {
            return (object != null ? object.toString() : null);
          }
      };
  }*/

  /*@Override
  public boolean hasDefaultConverter() {
      return !(   getComponentType() == AttributeDescriptor.ComponentType.selectManyChoice
               || getComponentType() == AttributeDescriptor.ComponentType.selectOneChoice
               || getComponentType() == AttributeDescriptor.ComponentType.selectOneListbox
               || getComponentType() == AttributeDescriptor.ComponentType.selectOneRadio);
  }*/  

  public Class getType() {
    return this.implementation;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public boolean isRequired() {
    return false;
  }

  public int getWidth() {
    return 0;
  }

  public String getAlign() {
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
