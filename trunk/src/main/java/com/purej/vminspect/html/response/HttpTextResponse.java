// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.response;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Http response with text content.
 *
 * @author Stefan Mueller
 */
public class HttpTextResponse extends AbstractHttpResponse {
  private final StringBuilder _output = new StringBuilder(4096);

  /**
   * Creates a new instance of this class.
   *
   * @param contentType the content type
   */
  public HttpTextResponse(String contentType) {
    super(contentType);
  }

  /**
   * Returns the output to append data to.
   */
  public StringBuilder getOutput() {
    return _output;
  }

  @Override
  public void writeTo(HttpServletResponse response) throws IOException {
    // No cache for dynamic content:
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Pragma", "no-cache");
    response.addHeader("Expires", "-1");

    byte[] data = _output.toString().getBytes("UTF-8");
    response.getOutputStream().write(data);
  }
}
