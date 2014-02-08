// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.servlet;

import javax.servlet.http.HttpServletRequest;
import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;

/**
 * Enriched MBeanAccessControl interface that provides the {@link HttpServletRequest}
 * with each control call.
 *
 * @author Stefan Mueller
 */
public interface ServletMBeanAccessControl {

  /**
   * Returns if changing the given attribute is allowed.
   */
  boolean isChangeAllowed(HttpServletRequest request, MBeanData mbean, MBeanAttribute attribute);

  /**
   * Returns if executing the given operation is allowed.
   */
  boolean isCallAllowed(HttpServletRequest request, MBeanData mbean, MBeanOperation operation);

  /**
   * Returns if changing the given attribute needs a confirmation screen.
   */
  boolean needsChangeConfirmation(HttpServletRequest request, MBeanData mbean, MBeanAttribute attribute);

  /**
   * Returns if executing the given operation needs a confirmation screen.
   */
  boolean needsCallConfirmation(HttpServletRequest request, MBeanData mbean, MBeanOperation operation);

  /**
   * Will be called after an MBean attribute has been changed.
   */
  void attributeChanged(HttpServletRequest request, MBeanData mbean, MBeanAttribute attribute, Object newValue);

  /**
   * Will be called after an MBean operation has been called.
   */
  void operationCalled(HttpServletRequest request, MBeanData mbean, MBeanOperation operation, String[] params, Object result);
}
