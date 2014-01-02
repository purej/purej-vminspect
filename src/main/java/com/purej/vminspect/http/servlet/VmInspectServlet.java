// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.data.statistics.StatisticsCollector;
import com.purej.vminspect.http.HttpPngResponse;
import com.purej.vminspect.http.HttpRequest;
import com.purej.vminspect.http.HttpResourceResponse;
import com.purej.vminspect.http.HttpResponse;
import com.purej.vminspect.http.HttpTextResponse;
import com.purej.vminspect.http.RequestController;

/**
 * The servlet that produces the VM inspection HTML report. This is currently implemented against servlet-spec version 2.5 but should
 * also run with newer versions.
 * <p/>
 * The following servlet init parameters are supported (all optional):
 * <ul>
 * <li>vminspect.mbeans.readonly: true/false, specifies if VmInspect is allowed to edit MBean values or invoke non-info operations (default false)</li>
 * <li>vminspect.statistics.collection.frequencyMs: Number of milliseconds for the statistics collection timer (default 60'000ms)</li>
 * <li>vminspect.statistics.storage.dir: Optional Path where to store the statistics files (default no storage directory)</li>
 * </ul>
 *
 * @author Stefan Mueller
 */
public final class VmInspectServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(VmInspectServlet.class);
  private static final long serialVersionUID = 1L;

  // Members that get set in the init method (Note: collector is only allowed once per VM!):
  private static StatisticsCollector _collector;
  private static Set<VmInspectServlet> _collectorRefs = new HashSet<VmInspectServlet>();
  private RequestController _controller;

  @Override
  public void init() {
    // Load configuration from init parameters:
    boolean mbeansReadonly = Boolean.parseBoolean(getServletConfig().getInitParameter("vminspect.mbeans.readonly"));
    String collectionFrequency = getServletConfig().getInitParameter("vminspect.statistics.collection.frequencyMs");
    String storageDir = getServletConfig().getInitParameter("vminspect.statistics.storage.dir");
    init(mbeansReadonly, collectionFrequency != null ? Integer.parseInt(collectionFrequency) : 60000, storageDir);
  }

  /**
   * Initializes this servlet instance or does nothing if already initialized.
   * This method can be used to programmatically initialize the servlet.
   *
   * @param mbeansReadonly if MBeans should be accessed read-only
   * @param statisticsCollectionFrequencyMs the statistics collection frequency in milliseconds (60'000 recommended)
   * @param statisticsStorageDir the optional statistics storage directory
   */
  public void init(boolean mbeansReadonly, int statisticsCollectionFrequencyMs, String statisticsStorageDir) {
    // Note: Do nothing if already initialized...
    if (_controller == null) {
      // Create the collector & start it (only once per VM):
      synchronized (VmInspectServlet.class) {
        if (_collector == null) {
          _collector = new StatisticsCollector(statisticsStorageDir, statisticsCollectionFrequencyMs);
          _collector.start();
        }
        _collectorRefs.add(this);
      }

      // Create the controller for pages:
      _controller = new RequestController(_collector, mbeansReadonly);
    }
  }

  @Override
  public void destroy() {
    synchronized (VmInspectServlet.class) {
      _collectorRefs.remove(this);
      if (_collectorRefs.size() == 0 && _collector != null) {
        _collector.stop();
        _collector = null;
      }
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      // Create the response in memory to be able to react on exceptions:
      HttpRequest httpRequest = toHttpRequest(request);
      HttpResponse httpResponse = _controller.process(httpRequest);

      // Write in correct order: a) content type, b) cookies, c) content, d) flush:
      response.setContentType(httpResponse.getContentType());
      for (Map.Entry<String, String> entry : httpResponse.getCookies().entrySet()) {
        Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        cookie.setPath(request.getRequestURI());
        response.addCookie(cookie);
      }

      // Content depends on the response instance:
      if (httpResponse instanceof HttpResourceResponse) {
        HttpResourceResponse res = (HttpResourceResponse) httpResponse;
        response.addHeader("Cache-Control", "max-age=3600"); // resources are stable, 1 hour caching...
        if (!res.writeTo(response.getOutputStream())) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
      }
      else if (httpResponse instanceof HttpPngResponse) {
        HttpPngResponse png = (HttpPngResponse) httpResponse;
        // No cache for dynamic content:
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "-1");
        response.setContentLength(png.getImg().length);
        response.addHeader("Content-Disposition", "inline;filename=" + png.getName());
        response.getOutputStream().write(png.getImg());
      }
      else {
        HttpTextResponse txt = (HttpTextResponse) httpResponse;
        // No cache for dynamic content:
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "-1");
        byte[] data = txt.getOutput().toString().getBytes("UTF-8");
        response.getOutputStream().write(data);
      }

      response.flushBuffer();
    }
    catch (Exception e) {
      LOGGER.debug("An error occurred processing get request!", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, getExceptionInfo(e));
    }
  }

  private static HttpRequest toHttpRequest(HttpServletRequest req) {
    HttpRequest request = new HttpRequest();

    // Add all parameters:
    for (Enumeration<?> e = req.getParameterNames(); e.hasMoreElements();) {
      String name = (String) e.nextElement();
      request.getParameters().put(name, req.getParameter(name));
    }

    // Add all cookies:
    for (Cookie cookie : req.getCookies()) {
      request.getCookies().put(cookie.getName(), cookie.getValue());
    }

    return request;
  }

  private static String getExceptionInfo(Throwable t) {
    StringBuilder builder = new StringBuilder();
    Throwable th = t;
    while (th != null) {
      builder.append(th.getClass()).append(": ").append(th.getMessage());
      builder.append("\n");
      th = th.getCause();
    }
    return builder.toString();
  }
}
