// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;
import com.purej.vminspect.data.MBeanOperation.Impact;

/**
 * A default implementation of the {@link MBeanAccessControl} interface that
 * decides based on two static booleans if change-/call-access is granted and write
 * confirmation is required, independent of the MBean attribute/operation.
 *
 * @author Stefan Mueller
 */
public class DefaultMBeanAccessControl implements MBeanAccessControl {
  private final String defaultDomainFilter;
  private final boolean readOnly;
  private final boolean needsWriteConfirmation;

  /**
   * Creates a new instance of this class.
   */
  public DefaultMBeanAccessControl(String defaultDomainFilter, boolean readOnly, boolean needsWriteConfirmation) {
    this.defaultDomainFilter = defaultDomainFilter;
    this.readOnly = readOnly;
    this.needsWriteConfirmation = needsWriteConfirmation;
  }

  @Override
  public String getDefaultDomainFilter() {
    return defaultDomainFilter;
  }

  @Override
  public boolean isChangeAllowed(MBeanData mbean, MBeanAttribute attribute) {
    return !readOnly;
  }

  @Override
  public boolean isCallAllowed(MBeanData mbean, MBeanOperation operation) {
    return !readOnly || operation.getImpact() == Impact.Info;
  }

  @Override
  public boolean needsChangeConfirmation(MBeanData mbean, MBeanAttribute attribute) {
    return needsWriteConfirmation;
  }

  @Override
  public boolean needsCallConfirmation(MBeanData mbean, MBeanOperation operation) {
    return needsWriteConfirmation;
  }

  @Override
  public void attributeChanged(MBeanData mbean, MBeanAttribute attribute, Object newValue) {
    // Nothing to do...
  }

  @Override
  public void attributeChangeFailed(MBeanData mbean, MBeanAttribute attribute, Exception exception) {
    // Nothing to do...
  }

  @Override
  public void operationCalled(MBeanData mbean, MBeanOperation operation, String[] params, Object result) {
    // Nothing to do...
  }

  @Override
  public void operationCallFailed(MBeanData mbean, MBeanOperation operation, String[] params, Exception exception) {
    // Nothing to do...
  }
}
