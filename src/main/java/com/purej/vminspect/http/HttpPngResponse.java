// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

/**
 * Http response with a binary PNG in memory pictures.
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
    super("image/png");
    _name = name;
  }

  /**
   * Returns the virtual image name.
   */
  public String getName() {
    return _name;
  }

  /**
   * Returns the image data.
   */
  public byte[] getImg() {
    return _img;
  }

  /**
   * Sets the image data.
   */
  public void setImg(byte[] img) {
    _img = img;
  }
}
