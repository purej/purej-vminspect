// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

/**
 * Represents an MBean operation parameter.
 *
 * @author Stefan Mueller
 */
public class MBeanParameter {
  private final String name;
  private final String type;
  private final String description;

  /**
   * Creates a new instance of this class.
   */
  public MBeanParameter(String name, String type, String description) {
    this.name = name;
    this.type = type;
    this.description = description;
  }

  /**
   * Returns the parameter name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the parameter type.
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the parameter description.
   */
  public String getDescription() {
    return description;
  }
}
