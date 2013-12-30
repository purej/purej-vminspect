// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.purej.vminspect.data.statistics.Period;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.html.response.AbstractHttpResponse;
import com.purej.vminspect.html.view.AbstractHtmlView;

/**
 * This class retrieves data from cookies or the request and stores them back to the response.
 *
 * @author Stefan Mueller
 */
public final class CookieManager {
  private static final String PERIOD_COOKIE_NAME = "purej.vminspect.period";
  private static final String DOMAIN_FILTER_COOKIE_NAME = "purej.vminspect.domainFilter";
  private static final String TYPE_FILTER_COOKIE_NAME = "purej.vminspect.typeFilter";

  private static final Range DEFAULT_RANGE = Range.createPeriodRange(Period.DAY);
  private static final String DEFAULT_FILTER = "";

  private CookieManager() {
  }

  /**
   * Returns the range from the request parameters or from the cookie. Stores a cookie if the request parameter could be parsed.
   */
  public static Range getRange(HttpServletRequest req, AbstractHttpResponse resp) {
    try {
      String period = req.getParameter(RequestParams.STATS_PERIOD);
      if (period == null) {
        String range = getCookie(req, PERIOD_COOKIE_NAME, DEFAULT_RANGE.asString());
        return Range.parse(range);
      }
      else {
        Period p = Period.valueOfIgnoreCase(period);
        Range range;
        if (p == Period.CUSTOM) {
          String from = req.getParameter(RequestParams.STATS_FROM_DATE);
          String to = req.getParameter(RequestParams.STATS_TO_DATE);
          Date fromDate = from != null && from.length() > 0 ? AbstractHtmlView.parseDate(from) : new Date();
          Date toDate = to != null && to.length() > 0 ? AbstractHtmlView.parseDate(to) : new Date();
          range = Range.createCustomRange(fromDate, toDate);
        }
        else {
          range = Range.createPeriodRange(p);
        }
        addCookie(req, resp, PERIOD_COOKIE_NAME, range.asString());
        return range;
      }
    }
    catch (Exception e) {
      return DEFAULT_RANGE;
    }
  }

  /**
   * Returns the MBean domain filter from the request parameters or from the cookie. Stores a cookie if the request parameter could be parsed.
   */
  public static String getDomainFilter(HttpServletRequest req, AbstractHttpResponse resp) {
    String param = req.getParameter(RequestParams.MBEAN_DOMAIN_FILTER);
    if (param == null) {
      return getCookie(req, DOMAIN_FILTER_COOKIE_NAME, DEFAULT_FILTER);
    }
    else {
      addCookie(req, resp, DOMAIN_FILTER_COOKIE_NAME, param);
      return param;
    }
  }

  /**
   * Returns the MBean type filter from the request parameters or from the cookie. Stores a cookie if the request parameter could be parsed.
   */
  public static String getTypeFilter(HttpServletRequest req, AbstractHttpResponse resp) {
    String param = req.getParameter(RequestParams.MBEAN_TYPE_FILTER);
    if (param == null) {
      return getCookie(req, TYPE_FILTER_COOKIE_NAME, DEFAULT_FILTER);
    }
    else {
      addCookie(req, resp, TYPE_FILTER_COOKIE_NAME, param);
      return param;
    }
  }

  private static String getCookie(HttpServletRequest req, String cookieName, String defaultValue) {
    Cookie cookie = getCookie(req, cookieName);
    return cookie != null ? cookie.getValue() : defaultValue;
  }

  private static Cookie getCookie(HttpServletRequest req, String cookieName) {
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookieName.equals(cookie.getName())) {
          return cookie;
        }
      }
    }
    return null;
  }

  private static void addCookie(HttpServletRequest req, AbstractHttpResponse resp, String cookieName, String cookieValue) {
    if (!resp.getCookies().containsKey(cookieName)) {
      Cookie cookie = new Cookie(cookieName, cookieValue);
      cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
      cookie.setPath(req.getRequestURI());
      resp.getCookies().put(cookieName, cookie);
    }
  }
}
