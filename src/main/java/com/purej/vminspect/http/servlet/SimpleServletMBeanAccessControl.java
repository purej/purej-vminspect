// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.servlet;

import javax.servlet.http.HttpServletRequest;
import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;
import com.purej.vminspect.data.MBeanOperation.Impact;

/**
 * An implementation of the {@link ServletMBeanAccessControl} interface that
 * decides based on a read-only mode if change-/call-access is granted, independent
 * of the MBean attribute/operation.
 *
 * @author Stefan Mueller
 */
public class SimpleServletMBeanAccessControl implements ServletMBeanAccessControl {
  private final boolean _readOnly;
  private final boolean _needsWriteConfirmation;

  /**
   * Creates a new instance of this class.
   */
  public SimpleServletMBeanAccessControl(boolean readOnly, boolean needsWriteConfirmation) {
    _readOnly = readOnly;
    _needsWriteConfirmation = needsWriteConfirmation;
  }

  @Override
  public boolean isChangeAllowed(HttpServletRequest request, MBeanData mbean, MBeanAttribute attribute) {
    return !_readOnly;
  }

  @Override
  public boolean isCallAllowed(HttpServletRequest request, MBeanData mbean, MBeanOperation operation) {
    return !_readOnly || operation.getImpact() == Impact.Info;
  }

  @Override
  public boolean needsChangeConfirmation(HttpServletRequest request, MBeanData mbean, MBeanAttribute attribute) {
    return _needsWriteConfirmation;
  }

  @Override
  public boolean needsCallConfirmation(HttpServletRequest request, MBeanData mbean, MBeanOperation operation) {
    return _needsWriteConfirmation;
  }

  @Override
  public void attributeChanged(HttpServletRequest request, MBeanData mbean, MBeanAttribute attribute, Object newValue) {
    // Nothing to do...
  }

  @Override
  public void operationCalled(HttpServletRequest request, MBeanData mbean, MBeanOperation operation, String[] params, Object result) {
    // Nothing to do...
  }
}
