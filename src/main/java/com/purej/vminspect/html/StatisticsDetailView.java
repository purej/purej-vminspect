// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import com.purej.vminspect.data.statistics.Range;

/**
 * Displays the statistics detail page with one statistics.
 *
 * @author Stefan Mueller
 */
public final class StatisticsDetailView extends AbstractStatisticsView {
  private final String statsName;
  private final int statsWidth;
  private final int statsHeight;

  /**
   * Creates a new instance of this view.
   */
  public StatisticsDetailView(StringBuilder output, Range range, String statsName, int statsWidth, int statsHeight) {
    super(output, range);
    this.statsName = statsName;
    this.statsWidth = statsWidth;
    this.statsHeight = statsHeight;
  }

  @Override
  public void render() {
    write("<h3>").writeImg("icons/charts-24.png", "Statistics").write("&nbsp;Statistics Detail</h3>");
    writeln("<div align='center'>");
    writeChoosePeriodLinks(statsName, statsWidth, statsHeight);
    writeln("</div><br/>");
    String params = statisticsGraphParams(statsName, statsWidth, statsHeight);
    writeln("<div align='center'>");
    writeln("<img class='synthese' id='img' src='?" + params + "' alt='zoom'/><br/><br/>");
    String paramsOut = addRangeParams(statisticsPageParams("statsDetail=" + statsName, "statsWidth=" + (int) (statsWidth / 1.5d), "statsHeight="
        + (int) (statsHeight / 1.2)));
    String paramsIn = addRangeParams(statisticsPageParams("statsDetail=" + statsName, "statsWidth=" + (int) (statsWidth * 1.5d), "statsHeight="
        + (int) (statsHeight * 1.2)));
    writeImgLnk(paramsOut, "icons/zoom-out-24.png", "Zoom Out", null).writeln();
    writeImgLnk(paramsIn, "icons/zoom-in-24.png", "Zoom In", null).writeln();
    writeln("</div>");
  }
}
