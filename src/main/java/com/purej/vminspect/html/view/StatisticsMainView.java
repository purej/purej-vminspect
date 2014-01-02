// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.data.statistics.Statistics;
import com.purej.vminspect.data.statistics.StatisticsCollector;

/**
 * Displays the currently existing statistics.
 *
 * @author Stefan Mueller
 */
public final class StatisticsMainView extends AbstractStatisticsView {
  private final StatisticsCollector _statistics;

  /**
   * Creates a new instance of this view.
   */
  public StatisticsMainView(StringBuilder output, StatisticsCollector statistics, Range range) {
    super(output, range);
    _statistics = statistics;
  }

  @Override
  public void render() throws IOException {
    writeln("<h3>" + img("icons/charts-24.png", "Statistics") + "&nbsp;Statistics Overview</h3>");
    writeln("<div align='center'>");
    writeChoosePeriodLinks(null, -1, -1);
    writeln("<br/>");
    writeGraphs(_statistics.getStatistics());
    writeln("</div>");
    writeDurationAndOverhead();
  }

  private void writeGraphs(List<Statistics> statistics) throws IOException {
    for (int i = 0; i < statistics.size(); i++) {
      Statistics stats = statistics.get(i);
      String params = params("statsWidth=200", "statsHeight=50", "statsGraph=" + stats.getName());
      String img = "<img class='synthese' src='?" + params + "' alt='" + stats.getLabel() + "' title='" + stats.getDescription() + "'/>";
      String statsParams = statisticsParams("statsDetail=" + stats.getName(), "statsWidth=1000", "statsHeight=400");
      writeln(lnk(statsParams, img));
      write("&nbsp;");
      if ((i + 1) % 3 == 0) {
        writeln("<br/><br/>");
      }
    }
  }

  private void writeDurationAndOverhead() throws IOException {
    writeln("<a name='bottom'></a>");
    writeln("<br/><div style='font-size:8pt;'>");
    writeln("Statistics collection frequency: " + formatNumber(_statistics.getCollectionFrequencyMillis()) + "ms");
    writeln("<br/>Last statistics collection time: " + formatDateTime(new Date(_statistics.getLastCollectTimestamp())));
    writeln("<br/>Last statistics collection duration: " + formatNumber(_statistics.getLastCollectDurationMs()));
    if (_statistics.getStatisticsStorageDir() == null) {
      writeln("<br/>Statistics memory overhead: " + formatMb(_statistics.getEstimatedMemorySize() / 1024d / 1024d));
      writeln("<br/><b><font color='red'>Note: No statistics directory configured, measuring statistics in-memory without persistence!</font></b>");
    }
    else {
      writeln("<br/>Statistics directory: " + _statistics.getStatisticsStorageDir());
      writeln("<br/>Statistics disk usage: " + formatMb(_statistics.getDiskUsage() / 1024d / 1024d));
    }
    writeln("</div>");
  }
}
