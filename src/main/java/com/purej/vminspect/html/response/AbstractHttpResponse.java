// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Encapsulates a HTTP response and decouples it from the original {@link HttpServletResponse}.
 * Reason: If there are exceptions in the middle of writing of the body content, it's too late to show an error page.
 * Therefore the complete response is built in memory and if successful, it will be written to the {@link HttpServletResponse}.
 *
 * @author Stefan Mueller
 */
public abstract class AbstractHttpResponse {
  private final Map<String, Cookie> _cookies = new HashMap<String, Cookie>(7);
  private final String _contentType;

  protected AbstractHttpResponse(String contentType) {
    _contentType = contentType;
  }

  /**
   * Returns the content type of this response.
   */
  public String getContentType() {
    return _contentType;
  }

  /**
   * Returns the cookies to be set.
   */
  public Map<String, Cookie> getCookies() {
    return _cookies;
  }

  /**
   * Writes this in-memory response to the given {@link HttpServletResponse} response stream.
   */
  public abstract void writeTo(HttpServletResponse response) throws IOException;
}
