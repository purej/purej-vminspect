// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Http response that reads directly from file.
 *
 * @author Stefan Mueller
 */
public final class HttpResourceResponse extends HttpResponse {
  private static final String RESOURCE_PREFIX = "/res/";
  private final String _resource;

  /**
   * Creates a new instance of this class.
   *
   * @param resource the resource
   */
  public HttpResourceResponse(String resource) {
    super(getContentType(resource), 3600); // 1 hour cache for resources...
    _resource = resource.replace("..", ""); // For security reason!;
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
    InputStream input = HttpResourceResponse.class.getResourceAsStream(RESOURCE_PREFIX + _resource);
    if (input == null) {
      return null;
    }
    try {
      // Transfer from input source to byte-array:
      input = new BufferedInputStream(input);
      ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
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

  /**
   * Writes the resource binary data to the given output stream.
   * @return true, if success, false if the resource could not be found
   */
  public boolean writeTo(OutputStream output) throws IOException {
    InputStream resourceStream = HttpResourceResponse.class.getResourceAsStream(RESOURCE_PREFIX + _resource);
    if (resourceStream == null) {
      return false;
    }
    else {
      try {
        // Transfer from input source to response stream:
        resourceStream = new BufferedInputStream(resourceStream);
        byte[] bytes = new byte[4096];
        int length = resourceStream.read(bytes);
        while (length != -1) {
          output.write(bytes, 0, length);
          length = resourceStream.read(bytes);
        }
      }
      finally {
        resourceStream.close();
      }
      return true;
    }
  }
}
