// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Http response that reads directly from file (eg. .png, .js, .css etc.).
 *
 * @author Stefan Mueller
 */
public final class HttpResourceResponse extends HttpResponse {
  private final String resource;

  /**
   * Creates a new instance of this class.
   *
   * @param resource the resource
   */
  public HttpResourceResponse(String resource) {
    super(getContentType(resource), 14400); // 4 hour cache for resources...
    this.resource = resource.replace("..", ""); // For security reason!;
  }

  private static String getContentType(String resource) {
    if (resource.endsWith(".css")) {
      return "text/css";
    }
    else if (resource.endsWith(".js")) {
      return "application/x-javascript";
    }
    else if (resource.endsWith(".gif")) {
      return "image/gif";
    }
    else if (resource.endsWith(".png")) {
      return "image/png";
    }
    else {
      throw new IllegalArgumentException("Not supported resource type '" + resource + "'!");
    }
  }

  @Override
  public byte[] getContentBytes() throws IOException {
    var input = HttpResourceResponse.class.getResourceAsStream("/res/" + resource);
    if (input == null) {
      return null;
    }
    try {
      // Transfer from input source to byte-array:
      input = new BufferedInputStream(input);
      var out = new ByteArrayOutputStream(2048);
      byte[] bytes = new byte[2048];
      int length;
      while ((length = input.read(bytes)) != -1) {
        out.write(bytes, 0, length);
      }
      return out.toByteArray();
    }
    finally {
      input.close();
    }
  }
}
