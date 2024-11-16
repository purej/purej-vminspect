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

  private final String name;
  private final MBeanParameter[] parameters;
  private final String returnType;
  private final Impact impact;
  private final String description;

  /**
   * Creates a new instance of this class.
   */
  public MBeanOperation(String name, MBeanParameter[] parameters, String returnType, Impact impact, String description) {
    this.name = name;
    this.parameters = parameters;
    this.returnType = returnType;
    this.impact = impact;
    this.description = description;
  }

  /**
   * Returns the operation name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the list of parameters.
   */
  public MBeanParameter[] getParameters() {
    return parameters;
  }

  /**
   * Returns the return type of this operation.
   */
  public String getReturnType() {
    return returnType;
  }

  /**
   * Returns the displayable impact of this operation (Info, Action or Info/Action).
   */
  public Impact getImpact() {
    return impact;
  }

  /**
   * Returns the description of this operation.
   */
  public String getDescription() {
    return description;
  }
}
