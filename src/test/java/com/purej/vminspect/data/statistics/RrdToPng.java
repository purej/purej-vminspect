package com.purej.vminspect.data.statistics;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.RrdBackendFactory;
import org.rrd4j.core.RrdRandomAccessFileBackendFactory;
import org.rrd4j.core.Util;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import com.purej.vminspect.util.Utils;

/**
 * Java main to convert all .rrd files from a directory to .png files.
 *
 * @author Stefan Mueller
 */
public class RrdToPng {
  private static final ConsolFun FUNCTION_AVG = ConsolFun.AVERAGE;
  private static final ConsolFun FUNCTION_MAX = ConsolFun.MAX;

  public static void main(String[] args) throws IOException {
    RrdBackendFactory factory = new RrdRandomAccessFileBackendFactory();
    File dir = new File("target/stats");
    process(dir, factory);
  }

  private static void process(File dir, RrdBackendFactory factory) throws IOException {
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        process(file, factory);
      } else {
        if (file.getName().endsWith(".rrd")) {
          String name = file.getName().replace(".rrd", "");
          String path = file.getCanonicalPath();
          byte[] png = createPng(name, path, factory);
          String pngName = name + ".png";
          System.out.println("Creating " + pngName + " ...");
          try (FileOutputStream out = new FileOutputStream(new File(dir, pngName))) {
            out.write(png);
          }
        }
      }
    }
  }

  private static byte[] createPng(String name, String path, RrdBackendFactory factory) throws IOException {
    Range range = Range.createPeriodRange(Period.DAY);
    int width = 1000;
    int height = 400;

    // Create the graph definition:
    RrdGraphDef graphDef = new RrdGraphDef();
    graphDef.setPoolUsed(true);
    graphDef.setFilename("-"); // Important for in-memory generation!

    // Set datasources:
    graphDef.datasource("average", path, name, FUNCTION_AVG, factory);
    graphDef.datasource("max", path, name, FUNCTION_MAX, factory);
    graphDef.setMinValue(0);

    // Set graphics stuff:
    graphDef.setImageFormat("png");
    graphDef.area("average", new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN, false), "Avg");
    graphDef.line("max", Color.BLUE, "Max");
    graphDef.setWidth(width);
    graphDef.setHeight(height);

    // Print Avg/Max under the chart:
    graphDef.datasource("vavg", "average", FUNCTION_AVG.getVariable());
    graphDef.datasource("vmax", "max", FUNCTION_MAX.getVariable());
    graphDef.gprint("vavg", "Avg: %9.0f x\\r");
    graphDef.gprint("vmax", "Max: %9.0f x\\r");

    setGraphStartEndTime(graphDef, range);
    setGraphTitle(graphDef, name + " Label", range, width);

    return new RrdGraph(graphDef).getRrdGraphInfo().getBytes();
  }

  private static void setGraphStartEndTime(RrdGraphDef graphDef, Range range) {
    long endTime; // Current timestamp or custom end-date
    long startTime; // Depending on the range and end-date
    if (range.getPeriod().equals(Period.CUSTOM)) {
      // Custom period:
      endTime = Math.min(range.getEndDate().getTime() / 1000, Util.getTime());
      startTime = range.getStartDate().getTime() / 1000;
    } else {
      endTime = Util.getTime();
      startTime = endTime - range.getPeriod().getDurationSeconds();
    }
    graphDef.setStartTime(startTime);
    graphDef.setEndTime(endTime);
    graphDef.setFirstDayOfWeek(Calendar.getInstance().getFirstDayOfWeek());
  }

  private static void setGraphTitle(RrdGraphDef graphDef, String label, Range range, int width) {
    String titleStart = label + " - " + range.getPeriod().getLabel();
    String titleEnd = "";
    if (width > 400) {
      if (range.getPeriod().equals(Period.CUSTOM)) {
        titleEnd = " - " + Utils.formatDate(range.getStartDate()) + " - " + Utils.formatDate(range.getEndDate());
      } else {
        titleEnd = " - " + Utils.formatDate(new Date());
      }
    }
    graphDef.setTitle(titleStart + titleEnd);
  }
}
