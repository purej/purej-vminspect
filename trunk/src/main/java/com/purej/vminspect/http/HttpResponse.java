// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a HTTP response and decouples it from the original socket-stream or servlet-request.
 * Reason: If there are exceptions in the middle of writing of the body content, it's too late to show an error page.
 * Therefore the complete response is built in memory and if successful, it will be written to the socket output-stream/servlet-response.
 *
 * @author Stefan Mueller
 */
public abstract class HttpResponse {
  private final Map<String, String> _cookies = new HashMap<String, String>(7);
  private final String _contentType;

  protected HttpResponse(String contentType) {
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
  public Map<String, String> getCookies() {
    return _cookies;
  }
}
