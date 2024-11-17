// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a HTTP response and decouples it from the original socket-stream or servlet-response.
 * <p/>
 * Reasons:<br/>
 * - If there are exceptions in the middle of writing the body content, it's too late to show an error page.
 * Therefore the complete response is built in memory and if successful, it will be written to the socket output-stream/servlet-response.
 * <br/>
 * - Http clients sometimes require the content-length to be specified in the HTML header - This is only possible if the number of bytes to
 * be written as content is known BEFORE the actual writing.
 *
 * @author Stefan Mueller
 */
public abstract class HttpResponse {
  private final Map<String, String> cookies = new HashMap<>(7);
  private final String contentType;
  private final int cacheSeconds;

  protected HttpResponse(String contentType, int cacheSeconds) {
    this.contentType = contentType;
    this.cacheSeconds = cacheSeconds;
  }

  /**
   * Returns the content type of this response.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Returns the number of seconds for this response to be cached
   * in the client-side.
   */
  public int getCacheSeconds() {
    return cacheSeconds;
  }

  /**
   * Returns the cookies to be set.
   */
  public Map<String, String> getCookies() {
    return cookies;
  }

  /**
   * Returns the content bytes to be written to the response.
   * @throws IOException if an I/O error occurred
   */
  public abstract byte[] getContentBytes() throws IOException;
}
