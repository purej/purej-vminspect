// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

/**
 * Represents a resolved MBean operation.
 *
 * @author Stefan Mueller
 */
public class MBeanOperation {

  /**
   * Enumeration for MBean operation impact.
   */
  public static enum Impact {
    /** The operation is readonly. */
    Info,
    /** The operation might change the MBean. */
    Action,
    /** The operation might change the MBean. */
    ActionInfo,
    /** The operation has an unknown impact. */
    Unknown
  }

  private final String _name;
  private final MBeanParameter[] _parameters;
  private final String _returnType;
  private final Impact _impact;
  private final String _description;

  /**
   * Creates a new instance of this class.
   */
  public MBeanOperation(String name, MBeanParameter[] parameters, String returnType, Impact impact, String description) {
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
  public Impact getImpact() {
    return _impact;
  }

  /**
   * Returns the description of this operation.
   */
  public String getDescription() {
    return _description;
  }
}
