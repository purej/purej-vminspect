// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import com.purej.vminspect.html.view.AbstractHtmlView;

/**
 * Represents a range for the statistics. A range can be a fixed period or a period of type CUSTOM and a custom start/end date.
 *
 * @author Stefan Mueller
 */
public final class Range {
  private static final char CUSTOM_PERIOD_SEPARATOR = '|';

  private final Period _period;
  private final Date _startDate;
  private final Date _endDate;

  private Range(Period period, Date startDate, Date endDate) {
    super();
    _period = period;
    _startDate = startDate;
    _endDate = endDate;
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
    Date normalizedStartDate = startOfDay(startDate);
    Date normalizedEndDate = endOfDay(endDate);
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -5);
    Date minDate = startOfDay(cal.getTime());
    Date maxDate = endOfDay(new Date());
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
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    return calendar.getTime();
  }

  private static Date endOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
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
    Date startDate = AbstractHtmlView.parseDate(value.substring(0, index));
    Date endDate = index < value.length() - 1 ? AbstractHtmlView.parseDate(value.substring(index + 1)) : new Date();
    return Range.createCustomRange(startDate, endDate);
  }

  /**
   * Returns the {@link Period} of this range.
   */
  public Period getPeriod() {
    return _period;
  }

  /**
   * Returns the start date or null if standard period.
   */
  public Date getStartDate() {
    return _startDate;
  }

  /**
   * Returns the end date or null if standard period.
   */
  public Date getEndDate() {
    return _endDate;
  }

  /**
   * Returns this range as s string (which might be parsed again).
   */
  public String asString() {
    if (_period.equals(Period.CUSTOM)) {
      return AbstractHtmlView.formatDate(_startDate) + CUSTOM_PERIOD_SEPARATOR + AbstractHtmlView.formatDate(_endDate);
    }
    return _period.getCode();
  }
}
