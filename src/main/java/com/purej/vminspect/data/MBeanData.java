// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

/**
 * Provides data about a single MBean.
 *
 * @author Stefan Mueller
 */
public class MBeanData {
  private final MBeanName name;
  private final String description;
  private final MBeanAttribute[] attribute;
  private final MBeanOperation[] operations;

  /**
   * Creates a new instance of this class.
   */
  public MBeanData(MBeanName name, String description, MBeanAttribute[] attributes, MBeanOperation[] operations) {
    this.name = name;
    this.description = description;
    this.attribute = attributes;
    this.operations = operations;
  }

  /**
   * @return the name of this MBean
   */
  public MBeanName getName() {
    return name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the list of attributes
   */
  public MBeanAttribute[] getAttributes() {
    return attribute;
  }

  /**
   * Returns the attribute with the given name.
   */
  public MBeanAttribute getAttribute(String name) {
    for (var attribute : attribute) {
      if (name.equals(attribute.getName())) {
        return attribute;
      }
    }
    throw new IllegalArgumentException("Attribute with name '" + name + "' not found!");
  }

  /**
   * Returns the list of operations.
   */
  public MBeanOperation[] getOperations() {
    return operations;
  }
}
