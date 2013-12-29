// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.data.statistics.StatisticsCollector;
import com.purej.vminspect.html.response.AbstractHttpResponse;

/**
 * The servlet that produces the VM inspection HTML report.
 * <p/>
 * Several properties can be set from outside using the servlet-config:
 * <ul>
 * <li>vminspect.mbeans.readonly: true/false, specifies if VmInspect is allowed to edit MBean values or invoke non-info operations (default false)</li>
 * <li>vminspect.statistics.collection.frequencyMs: Number of milliseconds for the statistics collection timer (default 60'000ms)</li>
 * <li>vminspect.statistics.storage.dir: Optional Path where to store the statistics files (default no storage directory)</li>
 * </ul>
 *
 * @author Stefan Mueller
 */
public class VmInspectServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(VmInspectServlet.class);
  private static final long serialVersionUID = 1L;

  // Members that get set in the init method (Note: collector is only allowed once per VM!):
  private static Set<VmInspectServlet> _collectorRefs = new HashSet<VmInspectServlet>();
  private static StatisticsCollector _collector;
  private RequestController _controller;

  @Override
  public void init(ServletConfig config) {
    try {
      // Load configuration from init parameters:
      boolean mbeansReadonly = Boolean.parseBoolean(config.getInitParameter("vminspect.mbeans.readonly"));
      String value = config.getInitParameter("vminspect.statistics.collection.frequencyMs");
      int statisticsCollectionFrequencyMillis = value != null ? Integer.parseInt(value) : 60000;
      String storageDir = config.getInitParameter("vminspect.statistics.storage.dir");

      // Create the collector & start it (only once):
      synchronized (VmInspectServlet.class) {
        if (_collector == null) {
          _collector = new StatisticsCollector(storageDir, statisticsCollectionFrequencyMillis);
          _collector.start();
        }
        _collectorRefs.add(this);
      }

      // Create the controller for pages:
      _controller = new RequestController(_collector, mbeansReadonly);
    }
    catch (Exception e) {
      throw new RuntimeException("Init of WebInspectServlet failed!", e);
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
      AbstractHttpResponse httpResponse = _controller.process(request);

      // Write in correct order: a) content type, b) cookies, c) content, d) flush:
      response.setContentType(httpResponse.getContentType());
      for (Cookie cookie : httpResponse.getCookies().values()) {
        response.addCookie(cookie);
      }
      httpResponse.writeTo(response);
      response.flushBuffer();
    }
    catch (Exception e) {
      LOGGER.debug("An error occurred processing get request!", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, getExceptionInfo(e));
    }
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
