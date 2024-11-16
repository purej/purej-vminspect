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
  DAY(1, "icons/calendar-day-16.png", "1 Day", "Day"),
  /**
   * Week.
   */
  WEEK(7, "icons/calendar-month-16.png", "1 Week", "Week"),
  /**
   * Month.
   */
  MONTH(31, "icons/calendar-month-16.png", "1 Month", "Month"),
  /**
   * Year.
   */
  YEAR(366, "icons/calendar-all-16.png", "1 Year", "Year"),
  /**
   * All.
   */
  ALL(2 * 366, "icons/calendar-all-16.png", "All", "All"),
  /**
   * Custom.
   */
  CUSTOM(-1, "icons/calendar-custom-16.png", "Custom", "Custom");

  private final int durationSeconds;
  private final String iconName;
  private final String label;
  private final String linkLabel;
  private final String code;

  private Period(int durationDays, String iconName, String label, String linkLabel) {
    this.durationSeconds = durationDays * 24 * 60 * 60;
    this.iconName = iconName;
    this.label = label;
    this.linkLabel = linkLabel;
    this.code = toString().toLowerCase();
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
    return code;
  }

  /**
   * Returns the label to be displayed.
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns the link label to be displayed.
   */
  public String getLinkLabel() {
    return linkLabel;
  }

  /**
   * Returns the duration in seconds of this period.
   */
  public int getDurationSeconds() {
    return durationSeconds;
  }

  /**
   * Returns the icon name to be displayed.
   */
  public String getIconName() {
    return iconName;
  }
}
