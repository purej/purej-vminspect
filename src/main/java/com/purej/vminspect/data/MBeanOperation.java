// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.Comparator;
import com.purej.vminspect.util.Utils;

/**
 * Represents a resolved MBean attribute.
 *
 * @author Stefan Mueller
 */
public class MBeanOperation {

  /**
   * Comparator to sort based on name.
   */
  public static final Comparator<MBeanOperation> COMPARATOR = new Comparator<MBeanOperation>() {
    @Override
    public int compare(MBeanOperation o1, MBeanOperation o2) {
      int result = Utils.compareTo(o1.getName(), o2.getName());
      if (result == 0) { // Same name
        result = Integer.valueOf(o1.getParameters().length).compareTo(Integer.valueOf(o2.getParameters().length));
        if (result == 0) { // Same # of parameters
          for (int i = 0; i < o1.getParameters().length; i++) {
            result = Utils.compareTo(o1.getParameters()[i].getType(), o2.getParameters()[i].getType());
            if (result != 0) {
              return result;
            }
          }
          throw new IllegalStateException("Methods '" + o1.getName() + "' have same parameters!");
        }
      }
      return result;
    }
  };

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
