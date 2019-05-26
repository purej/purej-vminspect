package com.purej.vminspect.data.statistics.rrd;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.Calendar;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdBackend;
import org.rrd4j.core.RrdBackendFactory;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.RrdException;
import org.rrd4j.core.RrdMemoryBackendFactory;
import org.rrd4j.core.RrdRandomAccessFileBackend;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.data.statistics.Period;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.util.Utils;

/**
 * RRD4J based implementation of a RRD.
 * See https://github.com/rrd4j/rrd4j.
 *
 * @author Stefan Mueller
 */
public class Rrd4jImpl extends AbstractRrdImpl {
  private static final Logger LOG = LoggerFactory.getLogger(Rrd4jImpl.class);
  private static final ConsolFun FUNCTION_AVG = ConsolFun.AVERAGE;
  private static final ConsolFun FUNCTION_MAX = ConsolFun.MAX;

  private final RrdBackendFactory _rrdBackendFactory;
  private RrdDb _rrdDb; // Note: Might be reopend if broken...
  private RandomAccessFile _raf; // Underlying file or null, depending on RrdDb subclass

  /**
   * Creates a new instance of this class.
   *
   * @param name the name of the statistics, must be a simple name without spaces and special characters
   * @param storageDir where the RRD file should be stored
   * @param resolutionSeconds the resolution in seconds
   * @param rrdBackendFactory the backend factory to be used
   * @throws IOException if a RRD file access error occurred
   */
  public Rrd4jImpl(String name, String storageDir, int resolutionSeconds, Object rrdBackendFactory) throws IOException {
    super(name, storageDir, resolutionSeconds);
    _rrdBackendFactory = (RrdBackendFactory) Utils.checkNotNull(rrdBackendFactory);
    initRrdDb(false);
  }

  @Override
  public String getName() {
    return _name;
  }

  @Override
  public void addValue(double value) throws IOException {
    try {
      doAddValue(value);
    } catch (FileNotFoundException e) {
      LOG.warn("RRD file '" + _rrdPath + "' does not exist, recreating it...");
      initRrdDb(true);
      doAddValue(value);
    }
  }

  private void doAddValue(double value) throws IOException {
    try {
      // Create sample with the current timestamp:
      Sample sample = _rrdDb.createSample();
      if (sample.getTime() > _rrdDb.getLastUpdateTime()) {
        sample.setValue(0, value);
        sample.update();
        if (_raf != null) {
          _raf.getChannel().force(true);
        }
      }
    } catch (RrdException e) {
      String msg = "Accessing RRD statistics file '" + _rrdPath + "' failed! If the problem persists, delete the file so it will be recreated.";
      LOG.error(msg, e);
      throw new IOException(msg, e);
    }
  }

  @Override
  public byte[] createPng(String label, String unit, Range range, int width, int height) throws IOException {
    try {
      // Create the graph definition:
      RrdGraphDef graphDef = new RrdGraphDef();
      graphDef.setPoolUsed(true);
      graphDef.setFilename("-"); // Important for in-memory generation!

      // Set datasources:
      graphDef.datasource("average", _rrdPath, _name, FUNCTION_AVG, _rrdBackendFactory);
      graphDef.datasource("max", _rrdPath, _name, FUNCTION_MAX, _rrdBackendFactory);
      graphDef.setMinValue(0);

      // Set graphics stuff:
      graphDef.setImageFormat("png");
      graphDef.area("average", new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN, false), "Mean");
      graphDef.line("max", Color.BLUE, "Maximum");
      graphDef.gprint("average", FUNCTION_AVG, "Mean: %9.0f " + unit + "\\r");
      graphDef.gprint("max", FUNCTION_MAX, "Maximum: %9.0f " + unit + "\\r");
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
    RrdDef rrdDef = new RrdDef(_rrdPath, _resolutionSeconds);
    rrdDef.setStartTime(Util.getTime() - _resolutionSeconds); // Matches more or less as collect is called right after init
    rrdDef.addDatasource(_name, DsType.GAUGE, _resolutionSeconds * 2, 0, Double.NaN); // Single gauge

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
      _raf = null; // Reset underlying file
      RrdDef def = createRrdDef();
      if (_rrdBackendFactory instanceof RrdMemoryBackendFactory) {
        _rrdDb = new RrdDb(def, _rrdBackendFactory);
      } else {
        // File backend:
        File rrdFile = new File(_rrdPath);
        if (overwrite || !rrdFile.exists() || rrdFile.length() == 0) {
          // Create a new one:
          _rrdDb = new RrdDb(def, _rrdBackendFactory);
        } else {
          // Open existing:
          _rrdDb = new RrdDb(_rrdPath, false, _rrdBackendFactory);
          // Sanity check - compare only step (eg. frequency) for now:
          if (def.getStep() != _rrdDb.getRrdDef().getStep()) {
            LOG.warn("Step size changed for {}, creating new one...", _rrdPath);
            _rrdDb.close();
            renameRrd(rrdFile);
            _rrdDb = new RrdDb(def, _rrdBackendFactory);
          }
        }

        // Try get the underlying RandomAccessFile:
        RrdBackend backend = _rrdDb.getRrdBackend();
        if (backend instanceof RrdRandomAccessFileBackend) {
          try {
            Field f = RrdRandomAccessFileBackend.class.getDeclaredField("rafile");
            f.setAccessible(true);
            _raf = (RandomAccessFile) f.get(backend);
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
