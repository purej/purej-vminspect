// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.data.statistics.StatisticsCollector;
import com.purej.vminspect.http.DefaultMBeanAccessControl;
import com.purej.vminspect.http.HttpRequest;
import com.purej.vminspect.http.HttpResponse;
import com.purej.vminspect.http.MBeanAccessControl;
import com.purej.vminspect.http.RequestController;
import com.purej.vminspect.http.RequestParams;
import com.purej.vminspect.util.Utils;

/**
 * This servlet allows PureJ VM Inspection to be used in servlet-containers / JEE containers. This servlet is implemented
 * against servlet-spec version 2.5 but should also run with newer versions.
 * <p/>
 * The following servlet init parameters are supported (all optional):
 * <ul>
 * <li>vminspect.mbeans.readonly: true/false, specifies if VmInspect is allowed to edit MBean values or invoke non-info operations (default: false)</li>
 * <li>vminspect.mbeans.writeConfirmation: true/false, specifies if a confirmation screen is displayed before edit MBean attributes or invoke MBean operations</li>
 * <li>vminspect.mbeans.accessControlFactory: fully qualified class name of an implementation of the {@link MBeanAccessControlFactory} interface</li>
 * <li>vminspect.statistics.collection.frequencyMs: Number of milliseconds for the statistics collection timer (default: 60'000ms)</li>
 * <li>vminspect.statistics.storage.dir: Optional Path where to store the statistics files (default: no storage directory). If no storage
 * directory is configured, the statistics will be kept in-memory and thus will be lost after a VM restart.</li>
 * </ul>
 *
 * @author Stefan Mueller
 */
public final class VmInspectionServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(VmInspectionServlet.class);
  private static final long serialVersionUID = 1L;

  // Members that get set in the init method:
  private StatisticsCollector _collector;
  private RequestController _controller;
  private MBeanAccessControlFactory _mbeanAccessControlFactory;

  @Override
  public void init() throws ServletException {
    // Load configuration from init parameters:
    boolean mbeansReadonly = Boolean.parseBoolean(getServletConfig().getInitParameter("vminspect.mbeans.readonly"));
    boolean mbeansWriteConfirmation = Boolean.parseBoolean(getServletConfig().getInitParameter("vminspect.mbeans.writeConfirmation"));
    String accessControlFactoryClz = getServletConfig().getInitParameter("vminspect.mbeans.accessControlFactory");
    String collectionFrequency = getServletConfig().getInitParameter("vminspect.statistics.collection.frequencyMs");
    String storageDir = getServletConfig().getInitParameter("vminspect.statistics.storage.dir");

    // Create the correct MBeanAccessControlFactory instance (custom or default):
    MBeanAccessControlFactory accessControlFactory;
    if (accessControlFactoryClz != null) {
      try {
        accessControlFactory = (MBeanAccessControlFactory) Class.forName(accessControlFactoryClz).newInstance();
      }
      catch (Exception e) {
        throw new ServletException("Could not load configured MBeanAccessControlFactory class '" + accessControlFactoryClz + "'!");
      }
    }
    else {
      // Create a default static MBeanAccessControlFactory instance using the boolean attributes:
      final MBeanAccessControl accessControl = new DefaultMBeanAccessControl(mbeansReadonly, mbeansWriteConfirmation);
      accessControlFactory = new MBeanAccessControlFactory() {
        @Override
        public MBeanAccessControl create(HttpServletRequest request) {
          return accessControl;
        }
      };
    }

    // Call the init:
    init(accessControlFactory, collectionFrequency != null ? Integer.parseInt(collectionFrequency) : 60000, storageDir);
  }

  /**
   * Initializes this servlet instance or does nothing if already initialized.
   * This method can be used to programmatically initialize the servlet.
   *
   * @param mbeanAccessControlFactory the factory to create {@link MBeanAccessControl} instances for fine-grained MBeans access control
   * @param statisticsCollectionFrequencyMs the statistics collection frequency in milliseconds (60'000 recommended)
   * @param statisticsStorageDir the optional statistics storage directory
   */
  public void init(MBeanAccessControlFactory mbeanAccessControlFactory, int statisticsCollectionFrequencyMs, String statisticsStorageDir) {
    if (_collector == null) {
      if (mbeanAccessControlFactory == null) {
        throw new IllegalArgumentException("MBeanAccessControlFactory is null!");
      }
      // Get or create collector, create controller:
      _collector = StatisticsCollector.init(statisticsStorageDir, statisticsCollectionFrequencyMs, this);
      _controller = new RequestController(_collector);
      _mbeanAccessControlFactory = mbeanAccessControlFactory;
    }
  }

  @Override
  public void destroy() {
    StatisticsCollector.destroy(this); // Makes sure the collector is destroyed if this was the last reference...
    _collector = null;
    _controller = null;
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Same logic as for HTTP get...
    doGet(request, response);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      // Create the request, process it and write the response:
      HttpRequest httpRequest = createHttpRequest(request);
      MBeanAccessControl mbeanAccessControl = null;
      if (httpRequest.getParameter(RequestParams.MBEAN_NAME) != null) {
        // Only create an instance of the MBeanAccessControl if a single MBean is targeted:
        mbeanAccessControl = _mbeanAccessControlFactory.create(request);
      }
      HttpResponse httpResponse = _controller.process(httpRequest, mbeanAccessControl);
      writeHttpResponse(httpResponse, request.getRequestURI(), response);
    }
    catch (Exception e) {
      LOGGER.debug("An error occurred processing get request!", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Utils.getExceptionInfo(e));
    }
    finally {
      response.flushBuffer();
    }
  }

  private static HttpRequest createHttpRequest(HttpServletRequest req) {
    HttpRequest request = new HttpRequest();

    // Add all parameters:
    for (Enumeration<?> e = req.getParameterNames(); e.hasMoreElements();) {
      String name = (String) e.nextElement();
      request.getParameters().put(name, req.getParameter(name));
    }

    // Add all cookies:
    if (req.getCookies() != null) {
      for (Cookie cookie : req.getCookies()) {
        request.getCookies().put(cookie.getName(), Utils.urlDecode(cookie.getValue()));
      }
    }
    return request;
  }

  private static void writeHttpResponse(HttpResponse httpResponse, String requestURI, HttpServletResponse response) throws IOException {
    // Sanity check first:
    byte[] data = httpResponse.getContentBytes();
    if (data == null || data.length == 0) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // a) Cookies:
    for (Map.Entry<String, String> entry : httpResponse.getCookies().entrySet()) {
      Cookie cookie = new Cookie(entry.getKey(), Utils.urlEncode(entry.getValue()));
      cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
      cookie.setPath(requestURI);
      response.addCookie(cookie);
    }

    // b) Caching:
    if (httpResponse.getCacheSeconds() > 0) {
      response.addHeader("Cache-Control", "max-age=" + httpResponse.getCacheSeconds());
    }
    else {
      response.addHeader("Cache-Control", "no-cache");
      response.addHeader("Pragma", "no-cache");
      response.addHeader("Expires", "-1");
    }

    // c) content type and length:
    response.setContentType(httpResponse.getContentType());
    response.setContentLength(data.length);

    // d) binary content:
    response.getOutputStream().write(data);
  }

}
