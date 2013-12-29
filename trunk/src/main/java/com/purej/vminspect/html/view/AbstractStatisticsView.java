// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import com.purej.vminspect.data.statistics.Period;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.html.RequestParams;

/**
 * Displays the currently existing statistics.
 *
 * @author Stefan Mueller
 */
public abstract class AbstractStatisticsView extends AbstractHtmlView {
  private final Range _range;

  /**
   * Creates a new instance of this view.
   */
  public AbstractStatisticsView(StringBuilder output, Range range) {
    super(output);
    _range = range;
  }

  protected static String statisticsParams(String... additionalParams) {
    StringBuilder builder = new StringBuilder();
    builder.append(RequestParams.PAGE).append("=statistics");
    for (String param : additionalParams) {
      builder.append(RequestParams.SEPARATOR).append(param);
    }
    return builder.toString();
  }

  protected void writeChoosePeriodLinks(String statsDetailName, int width, int height) throws IOException {
    writeln("<div>");
    String separator = "&nbsp;&nbsp;&nbsp;&nbsp;";
    String addParams = statsDetailName != null ? params("statsDetail=" + statsDetailName, "statsWidth=" + width, "statsHeight=" + height) : null;
    writeln(separator);
    writeln("Choice of period :&nbsp;");
    for (Period myPeriod : Period.values()) {
      if (myPeriod == Period.CUSTOM) {
        writeln("<a href=\"javascript:showHide('customPeriod');\" ");
        writeln("title='Choice of period Customized for charts and requests statistics'>");
        writeln(img(myPeriod.getIconName(), "Customized") + "&nbsp;Customized</a>");
      }
      else {
        String periodParam = "statsPeriod=" + myPeriod.getCode();
        String params = statsDetailName != null ? statisticsParams(addParams, periodParam) : statisticsParams(periodParam);
        write(lnk(params, img(myPeriod.getIconName(), "Choice of period " + myPeriod.getLabel()) + "&nbsp;" + myPeriod.getLinkLabel()));
        write("&nbsp;&nbsp;&nbsp;");
      }
    }
    writeln("</div>");
    writeCustomPeriodDiv(statsDetailName, width, height);
  }

  private void writeCustomPeriodDiv(String graphDetailName, int statsWidth, int statsHeight) throws IOException {
    writeln("<div id='customPeriod' style='display: none;'>");
    writeln("<br/><br/>");
    writeln("<form name='customPeriodForm' method='get' action=''>");
    write("<br/><b>From</b>&nbsp;&nbsp;<input type='text' size='10' name='statsFromDate' ");
    if (_range.getStartDate() != null) {
      write("value='" + formatDate(_range.getStartDate()) + "'");
    }
    writeln("/>&nbsp;&nbsp;");
    write("<b>To</b>&nbsp;&nbsp;<input type='text' size='10' name='statsToDate' ");
    if (_range.getEndDate() != null) {
      write("value='" + formatDate(_range.getEndDate()) + "'");
    }
    writeln("/>&nbsp;&nbsp;");
    write("(dd.MM.yyyy)");
    writeln("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='submit' value='Ok'/><br/><br/>");
    writeln("<input type='hidden' name='page' value='statistics'/>");
    writeln("<input type='hidden' name='statsPeriod' value='custom'/>");
    if (graphDetailName != null) {
      writeln("<input type='hidden' name='statsDetail' value='" + graphDetailName + "'/>");
      writeln("<input type='hidden' name='statsWidth' value='" + statsWidth + "'/>");
      writeln("<input type='hidden' name='statsHeight' value='" + statsHeight + "'/>");
    }
    writeln("</form><br/>");
    writeln("</div>");
  }
}
