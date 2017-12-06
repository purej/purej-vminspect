// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.Hashtable;
import java.util.Map;
import javax.management.ObjectName;

/**
 * Represents the name of an MBean.
 *
 * @author Stefan Mueller
 */
public class MBeanName {
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

    // Build type and the other key=value pairs from full-name:
    String type = "Unknown";
    StringBuilder otherKeyValues = new StringBuilder();
    Hashtable<String, String> keys = objectName.getKeyPropertyList();
    for (Map.Entry<String, String> entry : keys.entrySet()) {
      if (entry.getKey().equalsIgnoreCase("type")) {
        type = entry.getValue();
      } else {
        if (otherKeyValues.length() > 0) {
          otherKeyValues.append(',');
        }
        otherKeyValues.append(entry.getKey()).append('=').append(entry.getValue());
      }
    }
    _type = type;
    _otherKeyValues = otherKeyValues.toString();
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
