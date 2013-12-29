// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.Comparator;
import com.purej.vminspect.util.Utils;

/**
 * Represents a resolved MBean attribute.
 *
 * @author Stefan Mueller
 */
public class MBeanAttribute {

  /**
   * Comparator to sort based on name.
   */
  public static final Comparator<MBeanAttribute> COMPARATOR = new Comparator<MBeanAttribute>() {
    @Override
    public int compare(MBeanAttribute o1, MBeanAttribute o2) {
      return Utils.compareTo(o1.getName(), o2.getName());
    }
  };

  private final String _name;
  private final Object _value;
  private final String _description;
  private final String _type;
  private final boolean _writable;

  /**
   * Creates a new instance of this class.
   */
  public MBeanAttribute(String name, Object value, String description, String type, boolean writable) {
    _name = name;
    _value = value;
    _description = description;
    _type = type;
    _writable = writable;
  }

  /**
   * @return the MBean attribute name
   */
  public String getName() {
    return _name;
  }

  /**
   * @return the MBean attribute value
   */
  public Object getValue() {
    return _value;
  }

  /**
   * @return the MBean attribute description
   */
  public String getDescription() {
    return _description;
  }

  /**
   * @return the MBean attribute type
   */
  public String getType() {
    return _type;
  }

  /**
   * @return if the MBean attribute is writable
   */
  public boolean isWritable() {
    return _writable;
  }
}
