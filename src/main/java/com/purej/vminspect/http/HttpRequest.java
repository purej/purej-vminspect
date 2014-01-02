// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a HTTP request. This only stores the parts of a GET request that are
 * used by VM inspection.
 *
 * @author Stefan Mueller
 */
public class HttpRequest {
  private final Map<String, String> _parameters = new HashMap<String, String>();
  private final Map<String, String> _cookies = new HashMap<String, String>();

  /**
   * Returns the named parameter value.
   */
  public String getParameter(String name) {
    return _parameters.get(name);
  }

  /**
   * Returns the map of parameter keys and values.
   */
  public Map<String, String> getParameters() {
    return _parameters;
  }

  /**
   * Returns the named cookie value.
   */
  public String getCookie(String name) {
    return _cookies.get(name);
  }

  /**
   * Returns the map of cookies names and values.
   */
  public Map<String, String> getCookies() {
    return _cookies;
  }
}
