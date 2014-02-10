// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.servlet;

import javax.servlet.http.HttpServletRequest;
import com.purej.vminspect.http.MBeanAccessControl;

/**
 * Factory to create custom {@link MBeanAccessControl} instances for fine-grained
 * control over MBean access.
 *
 * @author Stefan Mueller
 */
public interface MBeanAccessControlFactory {

  /**
   * Creates or returns an {@link MBeanAccessControl} implementation that controls
   * the MBean interaction for the given HTTP request.
   */
  MBeanAccessControl create(HttpServletRequest request);
}
