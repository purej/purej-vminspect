// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

/**
 * Represents a resolved MBean attribute.
 *
 * @author Stefan Mueller
 */
public class MBeanOperation {
  private final String _name;
  private final MBeanParameter[] _parameters;
  private final String _returnType;
  private final String _impact;
  private final String _description;

  /**
   * Creates a new instance of this class.
   */
  public MBeanOperation(String name, MBeanParameter[] parameters, String returnType, String impact, String description) {
    _name = name;
    _parameters = parameters;
    _returnType = returnType;
    _impact = impact;
    _description = description;
  }

  /**
   * Returns the operation name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Returns the list of parameters.
   */
  public MBeanParameter[] getParameters() {
    return _parameters;
  }

  /**
   * Returns the return type of this operation.
   */
  public String getReturnType() {
    return _returnType;
  }

  /**
   * Returns the displayable impact of this operation (Info, Action or Info/Action).
   */
  public String getImpact() {
    return _impact;
  }

  /**
   * Returns the description of this operation.
   */
  public String getDescription() {
    return _description;
  }
}
