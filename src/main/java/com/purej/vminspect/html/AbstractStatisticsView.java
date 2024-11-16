// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import com.purej.vminspect.data.statistics.Period;
import com.purej.vminspect.data.statistics.Range;

/**
 * Abstract class for statistics views.
 *
 * @author Stefan Mueller
 */
abstract class AbstractStatisticsView extends AbstractHtmlView {
  private final Range range;

  /**
   * Creates a new instance of this view.
   */
  public AbstractStatisticsView(StringBuilder output, Range range) {
    super(output);
    this.range = range;
  }

  protected String statisticsGraphParams(String graphName, int width, int height) {
    return addRangeParams(params("statsGraph=" + graphName, "statsWidth=" + width, "statsHeight=" + height), range);
  }

  protected static String statisticsPageParams(String... additionalParams) {
    var builder = new StringBuilder();
    builder.append("page=statistics");
    for (var param : additionalParams) {
      builder.append(PARAMS_SEPARATOR).append(param);
    }
    return builder.toString();
  }

  protected String addRangeParams(String otherParams) {
    return addRangeParams(otherParams, range);
  }

  protected static String addRangeParams(String otherParams, Range range) {
    if (range.getPeriod() == Period.CUSTOM) {
      if (range.getStartDate() != null && range.getEndDate() != null) {
        var from = urlEncode(formatDate(range.getStartDate()));
        var to = urlEncode(formatDate(range.getEndDate()));
        return params(otherParams, "statsPeriod=custom", "statsFromDate=" + from, "statsToDate=" + to);
      }
      return params(otherParams, "statsPeriod=custom");
    }
    else {
      return params(otherParams, "statsPeriod=" + range.getPeriod().getCode());
    }
  }

  protected void writeChoosePeriodLinks(String statsDetailName, int width, int height) throws IOException {
    writeln("<div>");
    var separator = "&nbsp;&nbsp;&nbsp;&nbsp;";
    var addParams = statsDetailName != null ? params("statsDetail=" + statsDetailName, "statsWidth=" + width, "statsHeight=" + height) : null;
    writeln(separator);
    writeln("Choice of period :&nbsp;");
    for (Period myPeriod : Period.values()) {
      if (myPeriod == Period.CUSTOM) {
        write("\n<a showHide='customPeriod' ");
        write("title='Choice of period Customized for charts and requests statistics'>");
        writeln(img(myPeriod.getIconName(), "Customized") + "&nbsp;Customized</a>");
      }
      else {
        var periodParam = "statsPeriod=" + myPeriod.getCode();
        var params = statsDetailName != null ? statisticsPageParams(addParams, periodParam) : statisticsPageParams(periodParam);
        write(lnk(params, img(myPeriod.getIconName(), "Choice of period " + myPeriod.getLabel()) + "&nbsp;" + myPeriod.getLinkLabel()));
        write("&nbsp;&nbsp;&nbsp;");
      }
    }
    writeln("</div>");
    writeCustomPeriodDiv(statsDetailName, width, height);
  }

  private void writeCustomPeriodDiv(String graphDetailName, int statsWidth, int statsHeight) throws IOException {
    writeln("<div id='customPeriod' class='hidden'>");
    writeln("<br/><br/>");
    writeln("<form name='customPeriodForm' method='get' action=''>");
    write("<br/><b>From</b>&nbsp;&nbsp;<input type='text' size='10' name='statsFromDate' ");
    if (range.getStartDate() != null) {
      write("value='").write(formatDate(range.getStartDate())).write("'");
    }
    writeln("/>&nbsp;&nbsp;");
    write("<b>To</b>&nbsp;&nbsp;<input type='text' size='10' name='statsToDate' ");
    if (range.getEndDate() != null) {
      write("value='").write(formatDate(range.getEndDate())).write("'");
    }
    writeln("/>&nbsp;&nbsp;");
    write("(dd.MM.yyyy)");
    writeln("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='submit' value='Ok'/><br/><br/>");
    writeln("<input type='hidden' name='page' value='statistics'/>");
    writeln("<input type='hidden' name='statsPeriod' value='custom'/>");
    if (graphDetailName != null) {
      write("<input type='hidden' name='statsDetail' value='").write(graphDetailName).writeln("'/>");
      write("<input type='hidden' name='statsWidth' value='").write(statsWidth).writeln("'/>");
      write("<input type='hidden' name='statsHeight' value='").write(statsHeight).writeln("'/>");
    }
    writeln("</form><br/>");
    writeln("</div>");
  }
}
