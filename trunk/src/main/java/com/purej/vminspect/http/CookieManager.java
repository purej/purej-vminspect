// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http;

import java.util.Date;
import com.purej.vminspect.data.statistics.Period;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.util.Utils;

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
  public static Range getRange(HttpRequest request, HttpResponse response) {
    try {
      String period = request.getParameter(RequestParams.STATS_PERIOD);
      if (period == null) {
        String range = getCookie(request, PERIOD_COOKIE_NAME, null);
        return range == null ? DEFAULT_RANGE : Range.parse(range);
      }
      else {
        Period p = Period.valueOfIgnoreCase(period);
        Range range;
        if (p == Period.CUSTOM) {
          String from = request.getParameter(RequestParams.STATS_FROM_DATE);
          String to = request.getParameter(RequestParams.STATS_TO_DATE);
          Date fromDate = from != null && from.length() > 0 ? Utils.parseDate(from) : new Date();
          Date toDate = to != null && to.length() > 0 ? Utils.parseDate(to) : new Date();
          range = Range.createCustomRange(fromDate, toDate);
        }
        else {
          range = Range.createPeriodRange(p);
        }
        response.getCookies().put(PERIOD_COOKIE_NAME, range.asString());
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
  public static String getDomainFilter(HttpRequest request, HttpResponse response) {
    String param = request.getParameter(RequestParams.MBEAN_DOMAIN_FILTER);
    if (param == null) {
      return getCookie(request, DOMAIN_FILTER_COOKIE_NAME, DEFAULT_FILTER);
    }
    else {
      response.getCookies().put(DOMAIN_FILTER_COOKIE_NAME, param);
      return param;
    }
  }

  /**
   * Returns the MBean type filter from the request parameters or from the cookie. Stores a cookie if the request parameter could be parsed.
   */
  public static String getTypeFilter(HttpRequest request, HttpResponse response) {
    String param = request.getParameter(RequestParams.MBEAN_TYPE_FILTER);
    if (param == null) {
      return getCookie(request, TYPE_FILTER_COOKIE_NAME, DEFAULT_FILTER);
    }
    else {
      response.getCookies().put(TYPE_FILTER_COOKIE_NAME, param);
      return param;
    }
  }

  private static String getCookie(HttpRequest request, String cookieName, String defaultValue) {
    String value = request.getCookie(cookieName);
    return value != null ? value : defaultValue;
  }
}
