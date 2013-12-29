// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

/**
 * Provides data about a a single MBean.
 *
 * @author Stefan Mueller
 */
public class MBeanData {
  private final MBeanName _name;
  private final String _description;
  private final MBeanAttribute[] _attributes;
  private final MBeanOperation[] _operations;

  /**
   * Creates a new instance of this class.
   */
  public MBeanData(MBeanName name, String description, MBeanAttribute[] attributes, MBeanOperation[] operations) {
    this._name = name;
    this._description = description;
    this._attributes = attributes;
    this._operations = operations;
  }

  /**
   * @return the name of this MBean
   */
  public MBeanName getName() {
    return _name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return _description;
  }

  /**
   * @return the list of attributes
   */
  public MBeanAttribute[] getAttributes() {
    return _attributes;
  }

  /**
   * Returns the attribute with the given name.
   */
  public MBeanAttribute getAttribute(String name) {
    for (MBeanAttribute attribute : _attributes) {
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
    return _operations;
  }
}
