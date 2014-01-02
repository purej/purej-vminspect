// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

/**
 * Http response with text content.
 *
 * @author Stefan Mueller
 */
public class HttpTextResponse extends HttpResponse {
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
}
