// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.Comparator;
import javax.management.ObjectName;
import com.purej.vminspect.util.Utils;

/**
 * Represents the name of an MBean.
 *
 * @author Stefan Mueller
 */
public class MBeanName {

  /**
   * A comparator to compare {@link MBeanName} objects.
   */
  public static final Comparator<MBeanName> COMPARATOR = new Comparator<MBeanName>() {
    @Override
    public int compare(MBeanName o1, MBeanName o2) {
      int result = Utils.compareTo(o1.getDomain(), o2.getDomain());
      return result == 0 ? Utils.compareTo(o1.getType(), o2.getType()) : result;
    }
  };

  private final int _serverIdx;
  private final ObjectName _objectName;
  private final String _domain;
  private final String _type;
  private final String _otherKeyValues;

  /**
   * Creates a new instance of this class.
   */
  public MBeanName(int serverIdx, ObjectName objectName) {
    _serverIdx = serverIdx;
    _objectName = objectName;
    _domain = objectName.getDomain();
    String properties = objectName.getKeyPropertyListString();
    if (properties != null && properties.startsWith("type=")) {
      int idx = properties.indexOf(',');
      _type = idx > 0 ? properties.substring(5, idx) : properties.substring(5);
      _otherKeyValues = idx > 0 ? properties.substring(idx + 1) : "";
    }
    else {
      _type = "Unknown";
      _otherKeyValues = properties;
    }
  }

  /**
   * Returns the index of the MBean server.
   */
  public int getServerIdx() {
    return _serverIdx;
  }

  /**
   * The object name.
   */
  public ObjectName getObjectName() {
    return _objectName;
  }

  /**
   * Object name as string to resolve.
   */
  public String getObjectNameString() {
    return _objectName.toString();
  }

  /**
   * Domain name.
   */
  public String getDomain() {
    return _domain;
  }

  /**
   * Type.
   */
  public String getType() {
    return _type;
  }

  /**
   * Other key-value pairs.
   */
  public String getOtherKeyValues() {
    return _otherKeyValues;
  }
}
