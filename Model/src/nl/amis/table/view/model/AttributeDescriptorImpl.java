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

import nl.amis.sdo.jpa.services.Service;


public class AttributeDescriptorImpl extends ColumnDescriptor {
  // oracle.jbo.common.JboCompOper (adfm.jar)
  private static final Set<AttributeDescriptor.Operator> BOOLEAN_OPERATORS =
    new HashSet<AttributeDescriptor.Operator>(3);
  {
    BOOLEAN_OPERATORS.add(new OperatorImpl(Service.Operator.EQUALS));
    BOOLEAN_OPERATORS.add(new OperatorImpl(Service.Operator.NOT_EQUALS));
  }

  private static final Set<AttributeDescriptor.Operator> DATE_NUMBER_OPERATORS =
    new HashSet<AttributeDescriptor.Operator>(8);
  {
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(Service.Operator.EQUALS));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(Service.Operator.NOT_EQUALS));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(Service.Operator.GREATER_THAN));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(Service.Operator.LESS_THAN));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(Service.Operator.GREATER_THAN_EQUALS));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(Service.Operator.LESS_THAN_EQUALS));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(Service.Operator.BETWEEN));
    DATE_NUMBER_OPERATORS.add(new OperatorImpl(Service.Operator.NOT_BETWEEN));
  }

  private static final Set<AttributeDescriptor.Operator> STRING_OPERATORS =
    new HashSet<AttributeDescriptor.Operator>(8);
  {
    STRING_OPERATORS.add(new OperatorImpl(Service.Operator.EQUALS));
    STRING_OPERATORS.add(new OperatorImpl(Service.Operator.NOT_EQUALS));
    STRING_OPERATORS.add(new OperatorImpl(Service.Operator.LIKE));
    STRING_OPERATORS.add(new OperatorImpl(Service.Operator.STARTS_WITH));
    STRING_OPERATORS.add(new OperatorImpl(Service.Operator.ENDS_WITH));
    STRING_OPERATORS.add(new OperatorImpl(Service.Operator.CONTAINS));
    STRING_OPERATORS.add(new OperatorImpl(Service.Operator.DOES_NOT_CONTAIN));
  }
  
  /*public static enum Operation {
      EQUALS("Equals"), NOT_EQUALS("Not Equals"), LESS_THAN("Less Than"), GREATER_THAN("Greater Than"), LESS_THAN_EQUALS("Less Than Equals"), GREATER_THAN_EQUALS("Greater Than Equals"), LIKE("Like"), STARTS_WTIH("Starts With"), ENDS_WITH("Ends With"), BETWEEN("Between"), CONTAINS("Contains"), DOES_NOT_CONTAIN("Does not Contain");
    
      private final String value;
              
      Operation(final String value) {
        this.value = value;
      }
      
      public String toString() {
        return value;
      }
  }*/

  public class OperatorImpl extends AttributeDescriptor.Operator {
    private final Service.Operator operator;
    private final int operand;
    
    public OperatorImpl(final Service.Operator operator) {
      this.operator = operator;
      
      if (Service.Operator.BETWEEN.equals(operator) || Service.Operator.NOT_BETWEEN.equals(operator)) {
        operand = 2;
      }
      else if (Service.Operator.CONTAINS.equals(operator) || Service.Operator.DOES_NOT_CONTAIN.equals(operator)) {
        operand = -1;
      }
      else {
        operand = 1;
      }
    }

    public String getLabel() {
      return operator.getLabel();
    }

    public Object getValue() {
      return operator.getValue();
    }
    
    public String toString() {
      return operator.getValue();
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
