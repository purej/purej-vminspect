// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

/**
 * Represents a resolved MBean attribute.
 *
 * @author Stefan Mueller
 */
public class MBeanAttribute {
  private final String name;
  private final Object value;
  private final String description;
  private final String type;
  private final boolean writable;

  /**
   * Creates a new instance of this class.
   */
  public MBeanAttribute(String name, Object value, String description, String type, boolean writable) {
    this.name = name;
    this.value = value;
    this.description = description;
    this.type = type;
    this.writable = writable;
  }
  /**
   * @return the MBean attribute name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the MBean attribute value
   */
  public Object getValue() {
    return value;
  }

  /**
   * @return the MBean attribute description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the MBean attribute type
   */
  public String getType() {
    return type;
  }

  /**
   * @return if the MBean attribute is writable
   */
  public boolean isWritable() {
    return writable;
  }
}
