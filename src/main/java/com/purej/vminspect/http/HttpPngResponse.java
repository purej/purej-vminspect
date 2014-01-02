// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.io.IOException;

/**
 * Http response with a dynamically created, binary PNG in-memory picture.
 *
 * @author Stefan Mueller
 */
public final class HttpPngResponse extends HttpResponse {
  private final String _name;
  private byte[] _img;

  /**
   * Creates a new instance of this class.
   */
  public HttpPngResponse(String name) {
    super("image/png", 0); // No cache for dynamic picture...
    _name = name;
  }

  /**
   * Returns the virtual image name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Sets the image data.
   */
  public void setImg(byte[] img) {
    _img = img;
  }

  @Override
  public byte[] getContentBytes() throws IOException {
    return _img;
  }
}
