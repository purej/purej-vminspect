// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdMemoryBackendFactory;
import org.jrobin.core.RrdNioBackendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.data.SystemData;
import com.purej.vminspect.util.Utils;

/**
 * This class holds the single statistics and provides a timer to collect all configured statistics on a regular basis.
 *
 * @author Stefan Mueller
 */
public final class StatisticsCollector {
  private static final Logger LOG = LoggerFactory.getLogger(StatisticsCollector.class);
  private static final double BYTES_PER_MB = 1024 * 1024;

  private static final FilenameFilter RRD_FILE_FILTER = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return name != null && name.endsWith(".rrd");
    }
  };

  private final Map<String, Statistics> _statistics = new HashMap<String, Statistics>();
  private final List<Statistics> _orderedStatistics = new ArrayList<Statistics>();
  private final String _storageDir;
  private final int _collectionFrequencyMillis;
  private final Timer _timer;

  // The JRobin backend factory, only created once for all statistics:
  private final RrdBackendFactory _rrdBackendFactory;

  // Will be changed with each collect-call:
  private volatile long _lastCollectTimestamp;
  private volatile long _lastCollectDurationMs;
  private volatile long _lastGcTimeMillis;
  private volatile long _estimatedMemorySize;

  /**
   * Creates a new instance of this class.
   *
   * @param storageDir where to store the statistics files
   * @param collectionFrequencyMillis the collection frequency in millseconds
   */
  public StatisticsCollector(String storageDir, int collectionFrequencyMillis) {
    super();
    _collectionFrequencyMillis = collectionFrequencyMillis;

    // Create a timer and timer tasks:
    _timer = new Timer("PureJ WebInspect Collector Timer", true);

    // Create the JRobin backend factory:
    try {
      if (storageDir == null) {
        _storageDir = null;
        _rrdBackendFactory = RrdBackendFactory.getFactory(RrdMemoryBackendFactory.NAME);
      }
      else {
        // Create the storage dir if not existing:
        File rrdFilesDir = new File(storageDir);
        if (!rrdFilesDir.exists() && !rrdFilesDir.mkdirs()) {
          throw new RuntimeException("PureJ VM-Inspect statistics directory '" + storageDir + "' could not be created!");
        }
        _storageDir = rrdFilesDir.getAbsolutePath();
        _rrdBackendFactory = RrdBackendFactory.getFactory(RrdNioBackendFactory.NAME);
      }
    }
    catch (RrdException e) {
      throw new RuntimeException("Could not created JRobin backend factory!", e);
    }

    // Init all statistics:
    try {
      initStatistics("usedMemory", "Used Heap Memory", "Used heap memory in megabytes", "mb");
      initStatistics("usedNonHeapMemory", "Used Non Heap Memory", "Used non-heap memory in megabytes", "mb");
      initStatistics("usedPhysicalMemory", "Used Physical Memory", "Used physical memory in megabytes", "mb");

      initStatistics("threads", "Live Threads", "Number of live threads", "");
      initStatistics("loadedClasses", "Loaded Classes", "Number of loaded classes", "");
      initStatistics("gcTime", "Garbage Collector Time", "Garbage collector time per statistics frequency in milliseconds", "ms");

      initStatistics("vmLoad", "VM CPU Load", "Recent VM CPU load (over all CPUs)", "%%");
      initStatistics("systemLoad", "System CPU Load", "Recent system CPU load (over all CPUs)", "%%");
    }
    catch (IOException e) {
      throw new RuntimeException("Could not initialize the statistics!", e);
    }
  }

  private void initStatistics(String name, String label, String description, String unit) throws IOException {
    Statistics stats = new Statistics(name, label, description, unit, _storageDir, _collectionFrequencyMillis / 1000, _rrdBackendFactory);
    _statistics.put(name, stats);
    _orderedStatistics.add(stats);
  }

  /**
   * Starts collecting statistics using a timer.
   */
  public void start() {
    // Execute a first collect to have minimal data ready:
    collect();
    _timer.schedule(new TimerTask() {
      @Override
      public void run() {
        collect();
      }
    }, _collectionFrequencyMillis, _collectionFrequencyMillis);
  }

  /**
   * Stops collecting statistics.
   */
  public void stop() {
    _timer.cancel();
  }

  /**
   * Returns the storage directory or null if no persistent store.
   */
  public String getStatisticsStorageDir() {
    return _storageDir;
  }

  /**
   * Returns the collection frequency in milliseconds.
   */
  public int getCollectionFrequencyMillis() {
    return _collectionFrequencyMillis;
  }

  /**
   * Returns the last collection timestamp.
   */
  public long getLastCollectTimestamp() {
    return _lastCollectTimestamp;
  }

  /**
   * Returns the last collection duration in milliseconds.
   */
  public long getLastCollectDurationMs() {
    return _lastCollectDurationMs;
  }

  /**
   * Returns the estimated VM memory used for the statistics data.
   */
  public long getEstimatedMemorySize() {
    return _estimatedMemorySize;
  }

  /**
   * Returns the size of this statistics files on disk.
   */
  public long getDiskUsage() {
    long sum = 0;
    if (_storageDir != null) {
      File[] files = new File(_storageDir).listFiles(RRD_FILE_FILTER);
      if (files != null) {
        for (File file : files) {
          sum += file.length();
        }
      }
    }
    return sum;
  }

  private synchronized void collect() {
    try {
      _lastCollectTimestamp = System.currentTimeMillis();
      Runtime.getRuntime().gc(); // Free unused memory before measuring stats...
      collectData(new SystemData());
      _estimatedMemorySize = Utils.estimateMemory(_rrdBackendFactory);
      _lastCollectDurationMs = System.currentTimeMillis() - _lastCollectTimestamp;
    }
    catch (Throwable t) {
      LOG.warn("Exception while collecting data", t);
    }
  }

  private void collectData(SystemData data) throws IOException {
    long gcTimeMillis = data.getGcCollectionTimeMillis() - _lastGcTimeMillis;
    _lastGcTimeMillis = data.getGcCollectionTimeMillis();

    getStatistics("usedMemory").addValue(data.getMemoryHeap().getUsed() / BYTES_PER_MB);
    getStatistics("usedNonHeapMemory").addValue(data.getMemoryNonHeap().getUsed() / BYTES_PER_MB);
    getStatistics("usedPhysicalMemory").addValue(data.getMemoryPhysical().getUsed() / BYTES_PER_MB);
    getStatistics("threads").addValue(data.getThreadCurrentCount());
    getStatistics("loadedClasses").addValue(data.getCLLoadedClassCount());
    getStatistics("gcTime").addValue(gcTimeMillis);
    getStatistics("vmLoad").addValue(data.getProcessCpuLoadPct());
    getStatistics("systemLoad").addValue(data.getSystemCpuLoadPct());
  }

  /**
   * Returns the {@link Statistics} with the given name.
   */
  public Statistics getStatistics(String graphName) {
    return _statistics.get(graphName);
  }

  /**
   * Returns the list of all {@link Statistics}.
   */
  public List<Statistics> getStatistics() {
    return _orderedStatistics;
  }
}
