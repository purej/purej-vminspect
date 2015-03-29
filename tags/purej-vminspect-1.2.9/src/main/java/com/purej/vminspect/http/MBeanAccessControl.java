// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;

/**
 * A callback interface to allow dynamic fine-grained control of MBeans access.
 *
 * @author Stefan Mueller
 */
public interface MBeanAccessControl {

  /**
   * Returns if changing the given attribute is allowed.
   */
  boolean isChangeAllowed(MBeanData mbean, MBeanAttribute attribute);

  /**
   * Returns if executing the given operation is allowed.
   */
  boolean isCallAllowed(MBeanData mbean, MBeanOperation operation);

  /**
   * Returns if changing the given attribute needs a confirmation screen.
   */
  boolean needsChangeConfirmation(MBeanData mbean, MBeanAttribute attribute);

  /**
   * Returns if executing the given operation needs a confirmation screen.
   */
  boolean needsCallConfirmation(MBeanData mbean, MBeanOperation operation);

  /**
   * Will be called after an MBean attribute has been successfully changed.
   */
  void attributeChanged(MBeanData mbean, MBeanAttribute attribute, Object newValue);

  /**
   * Will be called after an MBean attribute change has failed with an exception.
   */
  void attributeChangeFailed(MBeanData mbean, MBeanAttribute attribute, Exception exception);

  /**
   * Will be called after an MBean operation has been successfully called.
   */
  void operationCalled(MBeanData mbean, MBeanOperation operation, String[] params, Object result);

  /**
   * Will be called after an MBean operation call has failed with an exception.
   */
  void operationCallFailed(MBeanData mbean, MBeanOperation operation, String[] params, Exception exception);
}
