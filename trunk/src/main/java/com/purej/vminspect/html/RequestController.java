// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;
import com.purej.vminspect.data.MBeanUtils;
import com.purej.vminspect.data.SystemData;
import com.purej.vminspect.data.ThreadData;
import com.purej.vminspect.data.statistics.Statistics;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.data.statistics.StatisticsCollector;
import com.purej.vminspect.html.response.AbstractHttpResponse;
import com.purej.vminspect.html.response.HttpPngResponse;
import com.purej.vminspect.html.response.HttpResourceResponse;
import com.purej.vminspect.html.response.HttpTextResponse;
import com.purej.vminspect.html.view.AbstractHtmlView;
import com.purej.vminspect.html.view.HtmlPageView;
import com.purej.vminspect.html.view.MBeansDetailView;
import com.purej.vminspect.html.view.MBeansInvokeAttributeView;
import com.purej.vminspect.html.view.MBeansInvokeOperationView;
import com.purej.vminspect.html.view.MBeansMainView;
import com.purej.vminspect.html.view.StatisticsDetailView;
import com.purej.vminspect.html.view.StatisticsMainView;
import com.purej.vminspect.html.view.SystemMainView;
import com.purej.vminspect.html.view.ThreadsDumpView;
import com.purej.vminspect.html.view.ThreadsMainView;

/**
 * This is the controller that dispatches each request depending on the request-parameters to the correct view.
 *
 * @author Stefan Mueller
 */
public class RequestController {
  private static final Set<String> NO_REFRESH_PARAMS = new HashSet<String>();
  static {
    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_DOMAIN_FILTER);
    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_TYPE_FILTER);

    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_ATTRIBUTE_INVOKE);
    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_ATTRIBUTE_CANCEL);
    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_ATTRIBUTE_NAME);
    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_ATTRIBUTE_VALUE);

    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_OPERATION_INVOKE);
    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_OPERATION_CANCEL);
    NO_REFRESH_PARAMS.add(RequestParams.MBEAN_OPERATION_IDX);
  }

  private final StatisticsCollector _collector;
  private final boolean _mbeansReadonly;

  /**
   * Creates a new instance of this class.
   *
   * @param collector the collector
   * @param mbeansReadonly if MBeans must be accessed read-only
   */
  public RequestController(StatisticsCollector collector, boolean mbeansReadonly) {
    super();
    _collector = collector;
    _mbeansReadonly = mbeansReadonly;
  }

  /**
   * Processes the given request and returns the in-memory response.
   *
   * @param httpRequest the request to be processed
   * @return the in-memory response
   * @throws IOException if an exception occurred
   */
  public AbstractHttpResponse process(HttpServletRequest httpRequest) throws IOException {
    // 1.) Check if static resource (eg. png/css/etc.)
    String resource = httpRequest.getParameter(RequestParams.RESOURCE);
    if (resource != null) {
      return new HttpResourceResponse(resource);
    }
    else {
      // 2.) Check if graph (eg. statistics graphic):
      String statsGraphName = httpRequest.getParameter(RequestParams.STATS_GRAPH);
      if (statsGraphName != null) {
        return doStatsGraph(httpRequest, statsGraphName);
      }
      // 3.) Check if thread dump:
      String page = httpRequest.getParameter(RequestParams.PAGE);
      if ("threadsDump".equals(page)) {
        return doThreadDump(httpRequest);
      }
      // 4.) All other output are HTML based:
      else {
        return doHtml(httpRequest, page);
      }
    }
  }

  static void noCache(HttpServletResponse httpResponse) {
    httpResponse.addHeader("Cache-Control", "no-cache");
    httpResponse.addHeader("Pragma", "no-cache");
    httpResponse.addHeader("Expires", "-1");
  }

  private AbstractHttpResponse doStatsGraph(HttpServletRequest httpRequest, String graphName) throws IOException {
    HttpPngResponse response = new HttpPngResponse(graphName);
    Range range = CookieManager.getRange(httpRequest, response);
    int width = Math.min(Integer.parseInt(httpRequest.getParameter(RequestParams.STATS_WIDTH_PARAMETER)), 1600);
    int height = Math.min(Integer.parseInt(httpRequest.getParameter(RequestParams.STATS_HEIGHT_PARAMETER)), 1600);
    Statistics stats = _collector.getStatistics(graphName);
    response.setImg(stats.createGraph(range, width, height));
    return response;
  }

  private static AbstractHttpResponse doThreadDump(HttpServletRequest request) throws IOException {
    HttpTextResponse response = new HttpTextResponse("text/plain; charset=UTF-8");
    new ThreadsDumpView(response.getOutput(), ThreadData.getAllThreads()).render();
    return response;
  }

  private AbstractHttpResponse doHtml(HttpServletRequest request, String page) throws IOException {
    long start = System.currentTimeMillis();
    HttpTextResponse response = new HttpTextResponse("text/html; charset=UTF-8");

    // Create the content view:
    AbstractHtmlView view;
    if ("mbeans".equals(page)) {
      view = getMBeansView(request, response);
    }
    else if ("threads".equals(page)) {
      view = new ThreadsMainView(response.getOutput(), ThreadData.getAllThreads());
    }
    else if ("statistics".equals(page)) {
      Range range = CookieManager.getRange(request, response);
      String statsName = request.getParameter(RequestParams.STATS_DETAIL);
      if (statsName != null) {
        view = new StatisticsDetailView(response.getOutput(), range, statsName);
      }
      else {
        view = new StatisticsMainView(response.getOutput(), _collector, range);
      }
    }
    else {
      // For all other cases (eg. page=system or page missing) we show system page:
      view = new SystemMainView(response.getOutput(), new SystemData());
    }

    // Create the page template and render:
    HtmlPageView html = new HtmlPageView(response.getOutput(), getCurrentParameters(request), start, view);
    html.render();
    return response;
  }

  private AbstractHtmlView getMBeansView(HttpServletRequest request, HttpTextResponse response) throws IOException {
    String mbServerIdx = request.getParameter(RequestParams.MBEAN_SRV_IDX);
    String mbName = request.getParameter(RequestParams.MBEAN_NAME);
    if (mbServerIdx != null && mbName != null) {
      // MBean specified:
      MBeanData mbean = MBeanUtils.getMBean(Integer.parseInt(mbServerIdx), mbName);
      String mbAtrName = request.getParameter(RequestParams.MBEAN_ATTRIBUTE_NAME);
      String mbOpIdx = request.getParameter(RequestParams.MBEAN_OPERATION_IDX);
      if (mbAtrName != null) {
        // MBean Attribute specified:
        MBeanAttribute attribute = mbean.getAttribute(mbAtrName);
        if (request.getParameter(RequestParams.MBEAN_ATTRIBUTE_INVOKE) != null) {
          if (_mbeansReadonly) {
            throw new UnsupportedOperationException("Not allowed to edit an attribute as in readonly-mode!");
          }
          // Invoke the attribute:
          String value = request.getParameter(RequestParams.MBEAN_ATTRIBUTE_VALUE);
          MBeanUtils.invokeAttribute(mbean, attribute, value);
          // Reload state & show MBean page:
          mbean = MBeanUtils.getMBean(Integer.parseInt(mbServerIdx), mbName);
          String okMsg = "Attribute <b>" + attribute.getName() + "</b> successfully set to value <b>" + AbstractHtmlView.htmlEncode(value) + "</b>!";
          return new MBeansDetailView(response.getOutput(), mbean, okMsg, null, _mbeansReadonly);
        }
        else if (request.getParameter(RequestParams.MBEAN_ATTRIBUTE_CANCEL) != null) {
          // Show MBean page:
          String warnMsg = "Canceled, attribute <b>" + attribute.getName() + "</b> not set!";
          return new MBeansDetailView(response.getOutput(), mbean, null, warnMsg, _mbeansReadonly);
        }
        else {
          // Show page to edit the attribute:
          return new MBeansInvokeAttributeView(response.getOutput(), mbean, attribute);
        }
      }
      else if (mbOpIdx != null) {
        // MBean Operation specified:
        int opIdx = Integer.parseInt(mbOpIdx);
        if (mbean.getOperations().length <= opIdx) {
          throw new RuntimeException("Operation at index '" + opIdx + "' does not exist!");
        }
        MBeanOperation operation = mbean.getOperations()[opIdx];
        if (request.getParameter(RequestParams.MBEAN_OPERATION_INVOKE) != null) {
          if (_mbeansReadonly && !operation.getImpact().equals("Info")) {
            throw new UnsupportedOperationException("Not allowed to invoke a non-info operation as in readonly-mode!");
          }
          // Invoke the operation:
          String[] params = new String[operation.getParameters().length];
          for (int i = 0; i < params.length; i++) {
            params[i] = request.getParameter(RequestParams.MBEAN_OPERATION_VALUE + i);
          }
          Object result = MBeanUtils.invokeOperation(mbean, operation, params);
          // Reload state & show MBean page:
          mbean = MBeanUtils.getMBean(Integer.parseInt(mbServerIdx), mbName);
          String okMsg = "Operation <b>" + operation.getName() + "</b> successfully invoked. Operation result is <b>"
              + AbstractHtmlView.htmlEncode(result != null ? result.toString() : "null") + "</b>!";
          return new MBeansDetailView(response.getOutput(), mbean, okMsg, null, _mbeansReadonly);
        }
        else if (request.getParameter(RequestParams.MBEAN_OPERATION_CANCEL) != null) {
          // Show MBean page:
          String warnMsg = "Canceled, operation <b>" + operation.getName() + "</b> not invoked!";
          return new MBeansDetailView(response.getOutput(), mbean, null, warnMsg, _mbeansReadonly);
        }
        else {
          // Show page to edit the attribute:
          return new MBeansInvokeOperationView(response.getOutput(), mbean, opIdx, operation);
        }
      }
      else {
        // Show MBean page:
        return new MBeansDetailView(response.getOutput(), mbean, null, null, _mbeansReadonly);
      }
    }
    else {
      // Show the main view:
      String domainFilter = CookieManager.getDomainFilter(request, response);
      String typeFilter = CookieManager.getTypeFilter(request, response);
      return new MBeansMainView(response.getOutput(), domainFilter, typeFilter, MBeanUtils.getMBeanNames());
    }
  }

  private static String getCurrentParameters(HttpServletRequest request) {
    StringBuilder params = new StringBuilder();
    for (Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();) {
      String key = (String) e.nextElement();
      String value = request.getParameter(key);
      if (!NO_REFRESH_PARAMS.contains(key) && value != null) {
        if (params.length() > 0) {
          params.append(RequestParams.SEPARATOR);
        }
        params.append(key).append("=").append(AbstractHtmlView.urlEncode(value));
      }
    }
    return params.toString();
  }
}
