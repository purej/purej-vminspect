// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import com.purej.vminspect.util.Utils;

/**
 * Represents a range for the statistics. A range can be a fixed period or a period of type CUSTOM and a custom start/end date.
 *
 * @author Stefan Mueller
 */
public final class Range {
  private static final char CUSTOM_PERIOD_SEPARATOR = '|';

  private final Period period;
  private final Date startDate;
  private final Date endDate;

  private Range(Period period, Date startDate, Date endDate) {
    super();
    this.period = Utils.checkNotNull(period);
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /**
   * Creates a standard period.
   */
  public static Range createPeriodRange(Period period) {
    return new Range(period, null, null);
  }

  /**
   * Creates a custom period.
   */
  public static Range createCustomRange(Date startDate, Date endDate) {
    var normalizedStartDate = startOfDay(startDate);
    var normalizedEndDate = endOfDay(endDate);
    var cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -5);
    var minDate = startOfDay(cal.getTime());
    var maxDate = endOfDay(new Date());
    if (normalizedStartDate.before(minDate)) {
      normalizedStartDate = minDate;
    }
    if (normalizedEndDate.after(maxDate)) {
      normalizedEndDate = maxDate;
    }
    if (normalizedStartDate.after(normalizedEndDate)) {
      normalizedStartDate = normalizedEndDate;
    }
    return new Range(Period.CUSTOM, normalizedStartDate, normalizedEndDate);
  }

  private static Date startOfDay(Date date) {
    var calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    return calendar.getTime();
  }

  private static Date endOfDay(Date date) {
    var calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    return calendar.getTime();
  }

  /**
   * Parses the given value and returns the correct {@link Range}.
   */
  public static Range parse(String value) throws ParseException {
    int index = value.indexOf(CUSTOM_PERIOD_SEPARATOR);
    if (index == -1) {
      return createPeriodRange(Period.valueOfIgnoreCase(value));
    }
    Date startDate = Utils.parseDate(value.substring(0, index));
    Date endDate = index < value.length() - 1 ? Utils.parseDate(value.substring(index + 1)) : new Date();
    return Range.createCustomRange(startDate, endDate);
  }

  /**
   * Returns the {@link Period} of this range.
   */
  public Period getPeriod() {
    return period;
  }

  /**
   * Returns the start date or null if standard period.
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * Returns the end date or null if standard period.
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * Returns this range as s string (which might be parsed again).
   */
  public String asString() {
    if (period.equals(Period.CUSTOM)) {
      return Utils.formatDate(startDate) + CUSTOM_PERIOD_SEPARATOR + Utils.formatDate(endDate);
    }
    return period.getCode();
  }
}
