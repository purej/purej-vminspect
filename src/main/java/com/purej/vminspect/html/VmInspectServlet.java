// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
 * The following servlet init parameters can be set:
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
