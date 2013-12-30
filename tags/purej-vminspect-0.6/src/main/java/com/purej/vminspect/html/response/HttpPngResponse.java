// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.response;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Http response with a binary PNG in memory pictures.
 *
 * @author Stefan Mueller
 */
public final class HttpPngResponse extends AbstractHttpResponse {
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
   * Sets the image data.
   */
  public void setImg(byte[] img) {
    _img = img;
  }

  @Override
  public void writeTo(HttpServletResponse response) throws IOException {
    // No cache for dynamic content:
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Pragma", "no-cache");
    response.addHeader("Expires", "-1");

    response.setContentLength(_img.length);
    response.addHeader("Content-Disposition", "inline;filename=" + _name);
    response.getOutputStream().write(_img);
  }
}
