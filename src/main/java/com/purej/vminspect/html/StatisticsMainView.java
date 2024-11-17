// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.util.Date;
import java.util.List;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.data.statistics.Statistics;
import com.purej.vminspect.data.statistics.StatisticsCollector;

/**
 * Displays the statistics overview page with all statistics available.
 *
 * @author Stefan Mueller
 */
public final class StatisticsMainView extends AbstractStatisticsView {
  private final StatisticsCollector statistics;

  /**
   * Creates a new instance of this view.
   */
  public StatisticsMainView(StringBuilder output, StatisticsCollector statistics, Range range) {
    super(output, range);
    this.statistics = statistics;
  }

  @Override
  public void render() {
    write("<h3>").writeImg("icons/charts-24.png", "Statistics").write("&nbsp;Statistics Overview</h3>");
    writeln("<div align='center'>");
    writeChoosePeriodLinks(null, -1, -1);
    writeln("<br/>");
    writeGraphs(statistics.getStatistics());
    writeln("</div>");
    writeDurationAndOverhead();
  }

  private void writeGraphs(List<Statistics> statistics) {
    for (var i = 0; i < statistics.size(); i++) {
      var stats = statistics.get(i);
      var params = statisticsGraphParams(stats.getName(), 200, 50);
      var img = "<img class='synthese' src='?" + params + "' alt='" + stats.getLabel() + "' title='" + stats.getDescription() + "'/>";
      var statsParams = addRangeParams(statisticsPageParams("statsDetail=" + stats.getName(), "statsWidth=1000", "statsHeight=400"));
      writeLnk(statsParams, img).writeln();
      write("&nbsp;");
      if ((i + 1) % 3 == 0) {
        writeln("<br/><br/>");
      }
    }
  }

  private void writeDurationAndOverhead() {
    writeln("<a name='bottom'></a>");
    writeln("<br/><div class='footer'>");
    write("Statistics collection frequency: ").write(formatNumber(statistics.getCollectionFrequencyMillis())).writeln("ms");
    var time = statistics.getLastCollectTimestamp() > 0 ? formatDateTime(new Date(statistics.getLastCollectTimestamp())) : "-";
    write("<br/>Last statistics collection time: ").writeln(time);
    write("<br/>Last statistics collection duration: ").write(formatNumber(statistics.getLastCollectDurationMs())).writeln("ms");
    if (statistics.getStatisticsStorageDir() == null) {
      writeln("<br/><b><font color='red'>Note: No statistics directory configured, measuring statistics in-memory without persistence!</font></b>");
    } else {
      write("<br/>Statistics directory: ").writeln(statistics.getStatisticsStorageDir());
      write("<br/>Statistics disk usage: ").write(formatDecimal(statistics.getDiskUsage() / 1024d / 1024d)).writeln(" Mb");
    }
    writeln("</div>");
  }
}
