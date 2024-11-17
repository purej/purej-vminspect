// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.util.HashMap;
import java.util.Map;
import com.purej.vminspect.util.Utils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Encapsulates a HTTP request. This only stores the parts of a GET request that are
 * used by VM inspection. This class allows decoupling execution logic from the concrete
 * request-implementation (e.g. for example servlet-spec version, custom HTTP server etc.)
 *
 * @author Stefan Mueller
 */
public class HttpRequest {
  private final HttpServletRequest request;
  private final Map<String, String> parameters = new HashMap<>();
  private final Map<String, String> cookies = new HashMap<>();

  public HttpRequest() {
    this.request = null;
  }

  public HttpRequest(HttpServletRequest request) {
    this.request = request;
    // Add all parameters:
    for (var e = request.getParameterNames(); e.hasMoreElements();) {
      var name = e.nextElement();
      parameters.put(name, request.getParameter(name));
    }
    // Add all cookies:
    if (request.getCookies() != null) {
      for (var cookie : request.getCookies()) {
        cookies.put(cookie.getName(), Utils.urlDecode(cookie.getValue()));
      }
    }
  }

  /**
   * Returns the original request. Attention: Might be null for none-http-servlet based implementations!
   */
  public HttpServletRequest getHttpServletRequest() {
    return request;
  }

  /**
   * Returns the named parameter value.
   */
  public String getParameter(String name) {
    return parameters.get(name);
  }

  /**
   * Returns the map of parameter keys and values.
   */
  public Map<String, String> getParameters() {
    return parameters;
  }

  /**
   * Returns the named cookie value.
   */
  public String getCookie(String name) {
    return cookies.get(name);
  }

  /**
   * Returns the map of cookies names and values.
   */
  public Map<String, String> getCookies() {
    return cookies;
  }
}
