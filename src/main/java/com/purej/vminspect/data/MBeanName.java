// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import javax.management.ObjectName;

/**
 * Represents the name of an MBean.
 *
 * @author Stefan Mueller
 */
public class MBeanName {
  private final int serverIdx;
  private final ObjectName objectName;
  private final String domain;
  private final String type;
  private final String otherKeyValues;

  /**
   * Creates a new instance of this class.
   */
  public MBeanName(int serverIdx, ObjectName objectName) {
    this.serverIdx = serverIdx;
    this.objectName = objectName;
    this.domain = objectName.getDomain();

    // Build type and the other key=value pairs from full-name:
    var type = "Unknown";
    var otherKeyValues = new StringBuilder();
    var keys = objectName.getKeyPropertyList();
    for (var entry : keys.entrySet()) {
      if (entry.getKey().equalsIgnoreCase("type")) {
        type = entry.getValue();
      } else {
        if (otherKeyValues.length() > 0) {
          otherKeyValues.append(',');
        }
        otherKeyValues.append(entry.getKey()).append('=').append(entry.getValue());
      }
    }
    this.type = type;
    this.otherKeyValues = otherKeyValues.toString();
  }

  /**
   * Returns the index of the MBean server.
   */
  public int getServerIdx() {
    return serverIdx;
  }

  /**
   * The object name.
   */
  public ObjectName getObjectName() {
    return objectName;
  }

  /**
   * Object name as string to resolve.
   */
  public String getObjectNameString() {
    return objectName.toString();
  }

  /**
   * Domain name.
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Type.
   */
  public String getType() {
    return type;
  }

  /**
   * Other key-value pairs.
   */
  public String getOtherKeyValues() {
    return otherKeyValues;
  }
}
