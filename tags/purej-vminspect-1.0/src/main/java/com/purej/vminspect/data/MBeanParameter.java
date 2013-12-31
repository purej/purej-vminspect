// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

/**
 * Represents an MBean operation parameter.
 *
 * @author Stefan Mueller
 */
public class MBeanParameter {
  private final String _name;
  private final String _type;
  private final String _description;

  /**
   * Creates a new instance of this class.
   */
  public MBeanParameter(String name, String type, String description) {
    this._name = name;
    this._type = type;
    this._description = description;
  }

  /**
   * Returns the parameter name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Returns the parameter type.
   */
  public String getType() {
    return _type;
  }

  /**
   * Returns the parameter description.
   */
  public String getDescription() {
    return _description;
  }
}
