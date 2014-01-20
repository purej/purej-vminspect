package com.purej.vminspect.http.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * An optional callback interface for dynamic check if write access is granted.
 *
 * @author Stefan Mueller
 */
public interface AuthorizationCallback {

  /**
   * Returns if the given request is allowed write access to MBeans
   * (eg. invoke attributes or operations).
   */
  boolean isMBeansWriteAllowed(HttpServletRequest request);

  /**
   * An implementation of the {@link AuthorizationCallback} that returns a static result.
   */
  public static class SimpleAuthorizationCallback implements AuthorizationCallback {
    private final boolean _writeAllowed;

    /**
     * Creates a new instance of this class that always returns the configured value.
     */
    public SimpleAuthorizationCallback(boolean writeAllowed) {
      _writeAllowed = writeAllowed;
    }

    @Override
    public boolean isMBeansWriteAllowed(HttpServletRequest request) {
      return _writeAllowed;
    }
  }
}
