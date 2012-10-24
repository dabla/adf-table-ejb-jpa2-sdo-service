package nl.amis.table.view.model;

import commonj.sdo.Property;

import java.util.Collections;
import java.util.Set;

import oracle.adf.view.rich.model.AttributeDescriptor;

public class AttributeDescriptorImpl extends AttributeDescriptor {
    private final String name;
    private final boolean readOnly;
    private final boolean required;
    
    public AttributeDescriptorImpl(final Property property) {
      this.name = property.getName();
      this.readOnly = property.isReadOnly();
      this.required = !property.isNullable();

      System.out.println("SDO Property: " + property.getName() + ", " + property.getType().getName() + ", " + property.getOpposite() + ", " + property.getDefault() + ", " + property.isReadOnly());
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
      System.out.println("getSupportedOperators: ");
      return Collections.emptySet();
    }

    public Class getType() {
      //System.out.println("getType: " + property.getType().getClass());
      //return property.getType().getClass();
      System.out.println("getType: ");
      return String.class;
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
