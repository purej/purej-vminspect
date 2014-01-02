// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdMemoryBackendFactory;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.util.Utils;

/**
 * Abstract a single statistics holder using JRobin as the data store. See http://oldwww.jrobin.org/api/index.html for API documentation.
 *
 * @author Stefan Mueller
 */
public final class Statistics {
  private static final Logger LOG = LoggerFactory.getLogger(StatisticsCollector.class);
  private static final String FUNCTION_AVG = "AVERAGE";
  private static final String FUNCTION_MAX = "MAX";
  private static final int HOUR = 60 * 60;
  private static final int DAY = 24 * HOUR;

  private final String _name;
  private final String _label;
  private final String _description;
  private final String _unit;
  private final int _resolutionSeconds;
  private final RrdBackendFactory _rrdBackendFactory;
  private final String _rrdPath;

  /**
   * Creates a new instance of this class.
   *
   * @param name the name of this statistics
   * @param label the label of this statistics
   * @param description the description of this statistics
   * @param unit the unit to be displayed with this statistics
   * @param storageDir where the JRobin file should be stored
   * @param resolutionSeconds the resolution in seconds
   * @param rrdBackendFactory the backend factory to be used
   * @throws IOException if a JRobin file access error occurred
   */
  public Statistics(String name, String label, String description, String unit, String storageDir, int resolutionSeconds,
      RrdBackendFactory rrdBackendFactory) throws IOException {
    if (name == null || name.length() > 20) {
      throw new IllegalArgumentException("JRobin names cannot exceed 20 characaters!");
    }
    _name = name;
    _label = label;
    _description = description;
    _unit = unit;
    _resolutionSeconds = resolutionSeconds;
    _rrdBackendFactory = rrdBackendFactory;
    _rrdPath = storageDir != null ? new File(storageDir, name + ".rrd").getCanonicalPath() : name + ".rrd";
    initRrdDb(false);
  }

  private RrdDef createRrdDef() throws RrdException {
    RrdDef rrdDef = new RrdDef(_rrdPath, _resolutionSeconds);
    // StartTime required as addValue is called right away after creation:
    rrdDef.setStartTime(Util.getTime() - _resolutionSeconds);
    // Single gauge datasource:
    rrdDef.addDatasource(_name, "GAUGE", _resolutionSeconds * 2, 0, Double.NaN);

    // Archives for average/max for each supported period:
    // 1 second for 1 hour periods:
    rrdDef.addArchive(FUNCTION_AVG, 0.25, 1, DAY / _resolutionSeconds);
    rrdDef.addArchive(FUNCTION_MAX, 0.25, 1, DAY / _resolutionSeconds);
    // 1 hour for 1 week periods:
    rrdDef.addArchive(FUNCTION_AVG, 0.25, HOUR / _resolutionSeconds, 7 * 24);
    rrdDef.addArchive(FUNCTION_MAX, 0.25, HOUR / _resolutionSeconds, 7 * 24);
    // 6 hours for 1 Month periods:
    rrdDef.addArchive(FUNCTION_AVG, 0.25, 6 * HOUR / _resolutionSeconds, 31 * 4);
    rrdDef.addArchive(FUNCTION_MAX, 0.25, 6 * HOUR / _resolutionSeconds, 31 * 4);
    // 2 days for year/all periods:
    rrdDef.addArchive(FUNCTION_AVG, 0.25, 2 * DAY / _resolutionSeconds, 2 * 12 * 15);
    rrdDef.addArchive(FUNCTION_MAX, 0.25, 2 * DAY / _resolutionSeconds, 2 * 12 * 15);

    return rrdDef;
  }

  private void initRrdDb(boolean overwrite) throws IOException {
    try {
      if (_rrdBackendFactory instanceof RrdMemoryBackendFactory) {
        RrdDb rrdDb = new RrdDb(createRrdDef(), _rrdBackendFactory);
        rrdDb.close();
      }
      else {
        // File backend:
        File rrdFile = new File(_rrdPath);
        if (overwrite || !rrdFile.exists() || rrdFile.length() == 0) {
          RrdDb rrdDb = new RrdDb(createRrdDef(), _rrdBackendFactory);
          rrdDb.close();
        }
      }
    }
    catch (RrdException e) {
      throw new IOException(e);
    }
  }

  /**
   * Returns the technical name (JRobin datasource name).
   */
  public String getName() {
    return _name;
  }

  /**
   * Returns the label for the UI.
   */
  public String getLabel() {
    return _label;
  }

  /**
   * Returns the description for the UI.
   */
  public String getDescription() {
    return _description;
  }

  /**
   * Adds a new statistics value with the current timestamp.
   */
  public void addValue(double value) throws IOException {
    try {
      doAddValue(value);
    }
    catch (FileNotFoundException e) {
      LOG.warn("JRobin file '" + _rrdPath + "' does not exist, recreating it");
      initRrdDb(true);
      doAddValue(value);
    }
  }

  private void doAddValue(double value) throws IOException {
    try {
      // Open the existing file in r/w mode:
      RrdDb rrdDb = new RrdDb(_rrdPath, false, _rrdBackendFactory);
      try {
        // Create sample with the current timestamp:
        Sample sample = rrdDb.createSample();
        if (sample.getTime() > rrdDb.getLastUpdateTime()) {
          sample.setValue(_name, value);
          sample.update();
        }
      }
      finally {
        rrdDb.close();
      }
    }
    catch (RrdException e) {
      String msg = "Accessing JRobin statistics file '" + _rrdPath + "' failed! If the problem persists, delete the file so it will be recreated.";
      LOG.error(msg, e);
      throw new IOException(msg, e);
    }
  }

  /**
   * Creates a graphics binary of this statistics in PNG format.
   *
   * @param range the range to be shown
   * @param width the width of the created PNG
   * @param height the height of the created PNG
   * @return the created binary image data
   * @throws IOException if image creation failed
   */
  public byte[] createGraph(Range range, int width, int height) throws IOException {
    try {
      // Create the graph definition:
      RrdGraphDef graphDef = new RrdGraphDef();
      graphDef.setPoolUsed(true);
      graphDef.setFilename("-"); // Important for in-memory generation!

      // Set datasources:
      graphDef.datasource("average", _rrdPath, _name, FUNCTION_AVG, _rrdBackendFactory.getFactoryName());
      graphDef.datasource("max", _rrdPath, _name, FUNCTION_MAX, _rrdBackendFactory.getFactoryName());
      graphDef.setMinValue(0);

      // Set graphics stuff:
      graphDef.setImageFormat("png");
      graphDef.area("average", new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN, false), "Mean");
      graphDef.line("max", Color.BLUE, "Maximum");
      graphDef.gprint("average", FUNCTION_AVG, "Mean: %9.0f " + _unit + "\\r");
      graphDef.gprint("max", FUNCTION_MAX, "Maximum: %9.0f " + _unit + "\\r");
      graphDef.setWidth(width);
      graphDef.setHeight(height);

      setGraphStartEndTime(graphDef, range);
      setGraphTitle(graphDef, _label, range, width);

      return new RrdGraph(graphDef).getRrdGraphInfo().getBytes();
    }
    catch (final RrdException e) {
      throw new IOException(e);
    }
  }

  private static void setGraphStartEndTime(RrdGraphDef graphDef, Range range) {
    long endTime; // Current timestamp or custom end-date
    long startTime; // Depending on the range and end-date
    if (range.getPeriod().equals(Period.CUSTOM)) {
      // Custom period:
      endTime = Math.min(range.getEndDate().getTime() / 1000, Util.getTime());
      startTime = range.getStartDate().getTime() / 1000;
    }
    else {
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
      }
      else {
        titleEnd = " - " + Utils.formatDate(new Date());
      }
    }
    graphDef.setTitle(titleStart + titleEnd);
  }
}
