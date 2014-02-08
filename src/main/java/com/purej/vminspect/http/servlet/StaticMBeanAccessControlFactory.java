package com.purej.vminspect.http.servlet;

import javax.servlet.http.HttpServletRequest;
import com.purej.vminspect.http.MBeanAccessControl;

/**
 * Static implementation of the {@link MBeanAccessControlFactory} interface
 * that returns always the same {@link MBeanAccessControl} implementation.
 *
 * @author Stefan Mueller
 */
public final class StaticMBeanAccessControlFactory implements MBeanAccessControlFactory {
  private final MBeanAccessControl _mbeanAccessControl;

  /**
   * Creates a new instance of this class.
   *
   * @param mbeanAccessControl the {@link MBeanAccessControl} instance to be returned with each request
   */
  public StaticMBeanAccessControlFactory(MBeanAccessControl mbeanAccessControl) {
    _mbeanAccessControl = mbeanAccessControl;
  }

  @Override
  public MBeanAccessControl create(HttpServletRequest request) {
    return _mbeanAccessControl;
  }
}
