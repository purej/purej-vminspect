// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import javax.servlet.http.HttpServletRequest;

/**
 * An interface that implements authorization.
 *
 * @author Stefan Mueller
 */
public interface AuthorizationCallback {

  /**
   * Returns if the current request is authorized.
   *
   * @return true if authorized, false otherwise
   */
  boolean isAuthorized(HttpServletRequest request);
}
