// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.io.IOException;

/**
 * Http response with text content.
 *
 * @author Stefan Mueller
 */
public class HttpTextResponse extends HttpResponse {
  private final StringBuilder output = new StringBuilder(4096);

  /**
   * Creates a new instance of this class.
   *
   * @param contentType the content type
   */
  public HttpTextResponse(String contentType) {
    super(contentType, 0); // No cache for dynamic content...
  }

  /**
   * Returns the output to append data to.
   */
  public StringBuilder getOutput() {
    return output;
  }

  @Override
  public byte[] getContentBytes() throws IOException {
    return output.toString().getBytes("UTF-8");
  }
}
