// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

/**
 * Defines the supported request parameters that are handled by the {@link RequestController}.
 *
 * @author Stefan Mueller
 */
public final class RequestParams {
  // CHECKSTYLE:OFF

  public static final String SEPARATOR = "&amp;";
  public static final String RESOURCE = "resource";
  public static final String PAGE = "page";

  public static final String MBEAN_SRV_IDX = "mbSrvIdx";
  public static final String MBEAN_NAME = "mbName";
  public static final String MBEAN_ATTRIBUTE_NAME = "mbAtrName";
  public static final String MBEAN_ATTRIBUTE_VALUE = "mbAtrValue";
  public static final String MBEAN_ATTRIBUTE_INVOKE = "mbAtrInvoke";
  public static final String MBEAN_ATTRIBUTE_CANCEL = "mbAtrCancel";
  public static final String MBEAN_OPERATION_IDX = "mbOpIdx";
  public static final String MBEAN_OPERATION_INVOKE = "mbOpInvoke";
  public static final String MBEAN_OPERATION_CANCEL = "mbOpCancel";
  public static final String MBEAN_OPERATION_VALUE = "mbOpValue";
  public static final String MBEAN_DOMAIN_FILTER = "mbDomainFilter";
  public static final String MBEAN_TYPE_FILTER = "mbTypeFilter";

  public static final String STATS_GRAPH = "statsGraph";
  public static final String STATS_DETAIL = "statsDetail";
  public static final String STATS_PERIOD_PARAMETER = "statsPeriod";
  public static final String STATS_FROM_DATE = "statsFromDate";
  public static final String STATS_TO_DATE = "statsToDate";
  public static final String STATS_WIDTH_PARAMETER = "statsWidth";
  public static final String STATS_HEIGHT_PARAMETER = "statsHeight";

  // CHECKSTYLE:ON

  private RequestParams() {
    super();
  }
}
