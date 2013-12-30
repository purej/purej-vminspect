// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * Http response that reads directly from file.
 *
 * @author Stefan Mueller
 */
public final class HttpResourceResponse extends AbstractHttpResponse {
  private static final String RESOURCE_PREFIX = "/res/";
  private final String _resource;

  /**
   * Creates a new instance of this class.
   *
   * @param resource the resource
   */
  public HttpResourceResponse(String resource) {
    super(getContentType(resource));
    _resource = resource;
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
  public void writeTo(HttpServletResponse response) throws IOException {
    // No cache for dynamic content:
    // TODO - Add 1 hour again?
    // response.addHeader("Cache-Control", "max-age=3600"); // 1 hour
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Pragma", "no-cache");
    response.addHeader("Expires", "-1");

    String res = _resource.replace("..", ""); // For security reason!
    InputStream resourceStream = HttpResourceResponse.class.getResourceAsStream(RESOURCE_PREFIX + res);
    if (resourceStream == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    else {
      try {
        // Transfer from input source to response stream:
        resourceStream = new BufferedInputStream(resourceStream);
        byte[] bytes = new byte[4096];
        int length = resourceStream.read(bytes);
        while (length != -1) {
          response.getOutputStream().write(bytes, 0, length);
          length = resourceStream.read(bytes);
        }
      }
      finally {
        resourceStream.close();
      }
    }
  }
}
