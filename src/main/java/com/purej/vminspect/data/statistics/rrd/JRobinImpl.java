package com.purej.vminspect.data.statistics.rrd;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.Calendar;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdFileBackend;
import org.jrobin.core.RrdMemoryBackendFactory;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.data.statistics.Period;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.util.Utils;

/**
 * JRobin based implementation of a RRD.
 *
 * @author Stefan Mueller
 */
public class JRobinImpl extends AbstractRrdImpl {
  private static final Logger LOG = LoggerFactory.getLogger(JRobinImpl.class);
  private static final String FUNCTION_AVG = "AVERAGE";
  private static final String FUNCTION_MAX = "MAX";

  private final RrdBackendFactory rrdBackendFactory;
  private RrdDb rrdDb; // Note: Might be reopend if broken...
  private RandomAccessFile raf; // Underlying file or null, depending on RrdDb subclass

  /**
   * Creates a new instance of this class.
   *
   * @param name the name of the statistics, must be a simple name without spaces and special characters
   * @param storageDir where the RRD file should be stored
   * @param resolutionSeconds the resolution in seconds
   * @param rrdBackendFactory the backend factory to be used
   * @throws IOException if a RRD file access error occurred
   */
  public JRobinImpl(String name, String storageDir, int resolutionSeconds, Object rrdBackendFactory) throws IOException {
    super(name, storageDir, resolutionSeconds);
    this.rrdBackendFactory = (RrdBackendFactory) Utils.checkNotNull(rrdBackendFactory);
    initRrdDb(false);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void addValue(double value) throws IOException {
    try {
      doAddValue(value);
    } catch (FileNotFoundException e) {
      LOG.warn("RRD file '" + rrdPath + "' does not exist, recreating it...");
      initRrdDb(true);
      doAddValue(value);
    }
  }

  private void doAddValue(double value) throws IOException {
    try {
      // Create sample with the current timestamp:
      var sample = rrdDb.createSample();
      if (sample.getTime() > rrdDb.getLastUpdateTime()) {
        sample.setValue(0, value);
        sample.update();
        if (raf != null) {
          raf.getChannel().force(true);
        }
      }
    } catch (RrdException e) {
      String msg = "Accessing RRD statistics file '" + rrdPath + "' failed! If the problem persists, delete the file so it will be recreated.";
      LOG.error(msg, e);
      throw new IOException(msg, e);
    }
  }

  @Override
  public byte[] createPng(String label, String unit, Range range, int width, int height) throws IOException {
    try {
      // Create the graph definition:
      var graphDef = new RrdGraphDef();
      graphDef.setPoolUsed(true);
      graphDef.setFilename("-"); // Important for in-memory generation!

      // Set datasources:
      graphDef.datasource("average", rrdPath, name, FUNCTION_AVG, rrdBackendFactory.getFactoryName());
      graphDef.datasource("max", rrdPath, name, FUNCTION_MAX, rrdBackendFactory.getFactoryName());
      graphDef.setMinValue(0);

      // Set graphics stuff:
      graphDef.setImageFormat("png");
      graphDef.area("average", new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN, false), "Avg");
      graphDef.line("max", Color.BLUE, "Max");
      graphDef.gprint("average", FUNCTION_AVG, "Avg: %9.0f " + unit + "\\r");
      graphDef.gprint("max", FUNCTION_MAX, "Max: %9.0f " + unit + "\\r");
      graphDef.setWidth(width);
      graphDef.setHeight(height);

      setGraphStartEndTime(graphDef, range);
      graphDef.setTitle(getGraphTitle(label, range, width));

      return new RrdGraph(graphDef).getRrdGraphInfo().getBytes();
    } catch (final RrdException e) {
      throw new IOException(e);
    }
  }

  private RrdDef createRrdDef() throws RrdException {
    RrdDef rrdDef = new RrdDef(rrdPath, resolutionSeconds);
    rrdDef.setStartTime(Util.getTime() - resolutionSeconds); // Matches more or less as collect is called right after init
    rrdDef.addDatasource(name, "GAUGE", resolutionSeconds * 2, 0, Double.NaN); // Single gauge

    // Archives for average/max for each supported period:
    // 1 second for 1 hour periods:
    rrdDef.addArchive(FUNCTION_AVG, 0.25, 1, DAY / resolutionSeconds);
    rrdDef.addArchive(FUNCTION_MAX, 0.25, 1, DAY / resolutionSeconds);
    // 1 hour for 1 week periods:
    rrdDef.addArchive(FUNCTION_AVG, 0.25, HOUR / resolutionSeconds, 7 * 24);
    rrdDef.addArchive(FUNCTION_MAX, 0.25, HOUR / resolutionSeconds, 7 * 24);
    // 6 hours for 1 Month periods:
    rrdDef.addArchive(FUNCTION_AVG, 0.25, 6 * HOUR / resolutionSeconds, 31 * 4);
    rrdDef.addArchive(FUNCTION_MAX, 0.25, 6 * HOUR / resolutionSeconds, 31 * 4);
    // 2 days for year/all periods:
    rrdDef.addArchive(FUNCTION_AVG, 0.25, 2 * DAY / resolutionSeconds, 2 * 12 * 15);
    rrdDef.addArchive(FUNCTION_MAX, 0.25, 2 * DAY / resolutionSeconds, 2 * 12 * 15);

    return rrdDef;
  }

  private void initRrdDb(boolean overwrite) throws IOException {
    try {
      raf = null; // Reset underlying file...
      var def = createRrdDef();
      if (rrdBackendFactory instanceof RrdMemoryBackendFactory) {
        rrdDb = new RrdDb(def, rrdBackendFactory);
      } else {
        // File backend:
        var rrdFile = new File(rrdPath);
        if (overwrite || !rrdFile.exists() || rrdFile.length() == 0) {
          // Create a new one:
          rrdDb = new RrdDb(def, rrdBackendFactory);
        } else {
          // Open existing:
          rrdDb = new RrdDb(rrdPath, false, rrdBackendFactory);
          // Sanity check - compare only step (eg. frequency) for now:
          if (def.getStep() != rrdDb.getRrdDef().getStep()) {
            LOG.warn("Step size changed for {}, creating new one...", rrdPath);
            rrdDb.close();
            renameRrd(rrdFile);
            rrdDb = new RrdDb(def, rrdBackendFactory);
          }
        }

        // Try get the underlying RandomAccessFile:
        var backend = rrdDb.getRrdBackend();
        if (backend.getClass() == RrdFileBackend.class) { // No instanceof check as of subclasses to be ignored...
          try {
            Field f = RrdFileBackend.class.getDeclaredField("file");
            f.setAccessible(true);
            raf = (RandomAccessFile) f.get(backend);
          } catch (Exception e) {
            LOG.warn("Cannot get underlying RandomAccessFile of RrdBackend, no explicit file sync/flush will be performed!");
          }
        }
      }
    } catch (RrdException e) {
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
    } else {
      endTime = Util.getTime();
      startTime = endTime - range.getPeriod().getDurationSeconds();
    }
    graphDef.setStartTime(startTime);
    graphDef.setEndTime(endTime);
    graphDef.setFirstDayOfWeek(Calendar.getInstance().getFirstDayOfWeek());
  }
}
