// Copyright (c), 2024, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.servlet;

import com.purej.vminspect.http.DefaultMBeanAccessControl;
import com.purej.vminspect.http.MBeanAccessControl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The default implementation of the {@link MBeanAccessControlFactory}.
 * Returns always the same {@link MBeanAccessControl} instance.
 */
public class DefaultMBeanAccessControlFactory implements MBeanAccessControlFactory {
  private final MBeanAccessControl accessControl;

  public DefaultMBeanAccessControlFactory(String defaultDomainFilter, boolean readOnly, boolean needsWriteConfirmation) {
    accessControl  = new DefaultMBeanAccessControl(defaultDomainFilter, readOnly, needsWriteConfirmation);
  }

  @Override
  public MBeanAccessControl create(HttpServletRequest request) {
    return accessControl;
  }
}
