// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import com.purej.vminspect.data.statistics.Range;

/**
 * Displays the currently existing statistics.
 *
 * @author Stefan Mueller
 */
public final class StatisticsDetailView extends AbstractStatisticsView {
  private final String _statsName;
  private final int _statsWidth;
  private final int _statsHeight;

  /**
   * Creates a new instance of this view.
   */
  public StatisticsDetailView(StringBuilder output, Range range, String statsName, int statsWidth, int statsHeight) {
    super(output, range);
    _statsName = statsName;
    _statsWidth = statsWidth;
    _statsHeight = statsHeight;
  }

  @Override
  public void render() throws IOException {
    writeln("<h3>" + img("icons/charts-24.png", "Statistics") + "&nbsp;Statistics Detail</h3>");
    writeln("<div align='center'>");
    writeChoosePeriodLinks(_statsName, _statsWidth, _statsHeight);
    writeln("</div><br/>");
    String params = params("statsGraph=" + _statsName, "statsWidth=" + _statsWidth, "statsHeight=" + _statsHeight);
    writeln("<div align='center'><img class='synthese' id='img' src='?" + params + "' alt='zoom'/></div>");
  }
}
