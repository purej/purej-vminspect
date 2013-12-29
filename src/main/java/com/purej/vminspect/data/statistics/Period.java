// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

/**
 * Represents a period for the statistics views.
 *
 * @author Stefan Mueller
 */
public enum Period {
  /**
   * Day.
   */
  DAY(1, "icons/calendar-day.png", "1 Day", "Day"),
  /**
   * Week.
   */
  WEEK(7, "icons/calendar-week.png", "1 Week", "Week"),
  /**
   * Month.
   */
  MONTH(31, "icons/calendar-month.png", "1 Month", "Month"),
  /**
   * Year.
   */
  YEAR(366, "icons/calendar.png", "1 Year", "Year"),
  /**
   * All.
   */
  ALL(2 * 366, "icons/calendar.png", "All", "All"),
  /**
   * Custom.
   */
  CUSTOM(-1, "icons/calendar.png", "Custom", "Custom");

  private final int _durationSeconds;
  private final String _iconName;
  private final String _label;
  private final String _linkLabel;
  private final String _code;

  private Period(int durationDays, String iconName, String label, String linkLabel) {
    _durationSeconds = durationDays * 24 * 60 * 60;
    _iconName = iconName;
    _label = label;
    _linkLabel = linkLabel;
    _code = toString().toLowerCase();
  }

  /**
   * Returns the {@link Period} instance for the given name, ignoring case.
   */
  public static Period valueOfIgnoreCase(String period) {
    return valueOf(period.toUpperCase().trim());
  }

  /**
   * Returns the lower case name of this enum.
   */
  public String getCode() {
    return _code;
  }

  /**
   * Returns the label to be displayed.
   */
  public String getLabel() {
    return _label;
  }

  /**
   * Returns the link label to be displayed.
   */
  public String getLinkLabel() {
    return _linkLabel;
  }

  /**
   * Returns the duration in seconds of this period.
   */
  public int getDurationSeconds() {
    return _durationSeconds;
  }

  /**
   * Returns the icon name to be displayed.
   */
  public String getIconName() {
    return _iconName;
  }
}
