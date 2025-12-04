// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.servlet;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.data.statistics.StatisticsCollector;
import com.purej.vminspect.http.HttpRequest;
import com.purej.vminspect.http.HttpResponse;
import com.purej.vminspect.http.MBeanAccessControl;
import com.purej.vminspect.http.RequestController;
import com.purej.vminspect.util.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This servlet allows PureJ VM Inspection to be used in servlet-containers / JEE containers.
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
public class VmInspectionServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(VmInspectionServlet.class);
  private static final long serialVersionUID = 1L;

  // Members that get set in the init method:
  private StatisticsCollector collector;
  private RequestController controller;

  /**
   * Returns if this servlet is already initalized.
   */
  public boolean isInitialized() {
    return collector != null;
  }

  /**
   * Auto-initializes this instance based on servlet-lifecycle. Does nothing if already initialized.
   */
  @Override
  public void init() throws ServletException {
    if (!isInitialized()) {
      // Load configuration from init parameters:
      var defaultDomainFilter = getServletConfig().getInitParameter("vminspect.mbeans.defaultDomainFilter");
      var mbeansReadonly = Boolean.parseBoolean(getServletConfig().getInitParameter("vminspect.mbeans.readonly"));
      var mbeansWriteConfirmation = Boolean.parseBoolean(getServletConfig().getInitParameter("vminspect.mbeans.writeConfirmation"));
      var accessControlFactoryClz = getServletConfig().getInitParameter("vminspect.mbeans.accessControlFactory");
      var collectionFrequency = getServletConfig().getInitParameter("vminspect.statistics.collection.frequencyMs");
      var storageDir = getServletConfig().getInitParameter("vminspect.statistics.storage.dir");
      init(accessControlFactoryClz, defaultDomainFilter, mbeansReadonly, mbeansWriteConfirmation,
          collectionFrequency != null ? Integer.parseInt(collectionFrequency) : 60000, storageDir);
    }
  }

  /**
   * Initializes this VM inspection instance programmatically.
   * Note: Initialize can only be called once for this instance!
   *
   * @param mbeanAccessControlFactoryClz the optional {@link MBeanAccessControl} class, if null a default instance will be used
   * @param defaultDomainFilter the default MBean domain filter if no cookie value is found
   * @param mbeansReadonly if MBeans should be readonly
   * @param mbeansWriteConfirmation if MBean operation calls need a confirmation
   * @param statisticsCollectionFrequencyMs the statistics collection frequency in milliseconds (60'000 recommended)
   * @param statisticsStorageDir the optional statistics storage directory
   */
  public void init(String mbeanAccessControlFactoryClz, String defaultDomainFilter, boolean mbeansReadonly, boolean mbeansWriteConfirmation, int statisticsCollectionFrequencyMs,
      String statisticsStorageDir) {
    // Create the correct MBeanAccessControlFactory instance (custom or default):
    MBeanAccessControlFactory accessControlFactory;
    if (mbeanAccessControlFactoryClz != null) {
      try {
        accessControlFactory = (MBeanAccessControlFactory) Class.forName(mbeanAccessControlFactoryClz).getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Could not load configured MBeanAccessControlFactory class '" + mbeanAccessControlFactoryClz + "'!");
      }
    } else {
      accessControlFactory = new DefaultMBeanAccessControlFactory(defaultDomainFilter, mbeansReadonly, mbeansWriteConfirmation);
    }
    init(accessControlFactory, statisticsCollectionFrequencyMs, statisticsStorageDir);
  }

  /**
   * Initializes this VM inspection instance programmatically.
   * Note: Initialize can only be called once for this instance!
   *
   * @param mbeanAccessControlFactory the factory to create {@link MBeanAccessControl} instances for fine-grained MBeans access control
   * @param statisticsCollectionFrequencyMs the statistics collection frequency in milliseconds (60'000 recommended)
   * @param statisticsStorageDir the optional statistics storage directory
   */
  public void init(MBeanAccessControlFactory mbeanAccessControlFactory, int statisticsCollectionFrequencyMs, String statisticsStorageDir) {
    if (isInitialized()) {
      throw new IllegalStateException("This instance is already initialized, cannot initialize twice!");
    }
    if (mbeanAccessControlFactory == null) {
      throw new IllegalArgumentException("MBeanAccessControlFactory is null!");
    }
    // Get or create collector, create controller:
    collector = StatisticsCollector.init(statisticsStorageDir, statisticsCollectionFrequencyMs, this);
    controller = new RequestController(mbeanAccessControlFactory, collector);
  }

  @Override
  public void destroy() {
    StatisticsCollector.destroy(this); // Makes sure the collector is destroyed if this was the last reference...
    collector = null;
    controller = null;
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Same logic as for HTTP get...
    doGet(request, response);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      // Create the request, process it and render the response:
      var httpRequest = new HttpRequest(request);
      var httpResponse = controller.process(httpRequest);

      // Now write the rendered output:
      try {
        writeHttpResponse(httpResponse, request.getRequestURI(), response);
      } catch (IOException e) {
        // Might happen if the browser already cut the connection...
        LOGGER.debug("Exception writing the output to the response stream!", e);
      }
    } catch (Exception e) {
      LOGGER.warn("An error occurred processing request!", e);
      var code = e instanceof SecurityException ? 401 : 500;
      response.sendError(code, Utils.getExceptionInfo(e));
    }
  }

  private static void writeHttpResponse(HttpResponse httpResponse, String requestURI, HttpServletResponse response) throws IOException {
    // Sanity check first:
    var data = httpResponse.getContentBytes();
    if (data == null || data.length == 0) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // a) Cookies:
    for (var entry : httpResponse.getCookies().entrySet()) {
      var cookie = new Cookie(entry.getKey(), Utils.urlEncode(entry.getValue()));
      cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
      cookie.setPath(requestURI);
      response.addCookie(cookie);
    }

    // b) Caching:
    if (httpResponse.getCacheSeconds() > 0) {
      response.addHeader("Cache-Control", "max-age=" + httpResponse.getCacheSeconds());
    } else {
      response.addHeader("Cache-Control", "no-cache");
    }

    // c) content type and length:
    response.setContentType(httpResponse.getContentType());
    response.setContentLength(data.length);

    // d) binary content:
    response.getOutputStream().write(data);
  }
}
