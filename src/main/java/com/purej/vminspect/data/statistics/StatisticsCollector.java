// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdMemoryBackendFactory;
import org.jrobin.core.RrdNioBackendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.purej.vminspect.data.SystemData;

/**
 * This class holds the different {@link Statistics} instances and provides a timer to
 * collect all statistics values on a regular basis.
 * <p/>
 * Note: This class is a singleton and should only be created once per virtual machine!
 *
 * @author Stefan Mueller
 */
public final class StatisticsCollector {
  private static final Logger LOG = LoggerFactory.getLogger(StatisticsCollector.class);
  private static final double BYTES_PER_MB = 1024 * 1024;

  // This static variables ensure only one collector instance per VM:
  private static StatisticsCollector _instance;
  private static Set<Object> _instanceRefs = new HashSet<Object>();

  // Instance members:
  private final List<Statistics> _statistics = new ArrayList<Statistics>();
  private final String _storageDir;
  private final int _collectionFrequencyMillis;
  private final Timer _timer;

  // The JRobin backend factory, only created once for all statistics:
  private final RrdBackendFactory _rrdBackendFactory;

  // Will be changed with each collect-call:
  private volatile long _lastCollectTimestamp;
  private volatile long _lastCollectDurationMs;
  private volatile long _diskUsage;

  private StatisticsCollector(String storageDir, int collectionFrequencyMillis) {
    super();
    _collectionFrequencyMillis = collectionFrequencyMillis;

    // Create a timer and timer tasks:
    _timer = new Timer("PureJ VM Inspection Statistics Collector Timer", true);

    // Create the JRobin backend factory:
    try {
      if (storageDir == null || storageDir.isEmpty()) {
        _storageDir = null;
        _rrdBackendFactory = RrdBackendFactory.getFactory(RrdMemoryBackendFactory.NAME);
      } else {
        // Create the storage dir if not existing:
        File rrdFilesDir = new File(storageDir);
        if (!rrdFilesDir.exists() && !rrdFilesDir.mkdirs()) {
          throw new RuntimeException("Statistics storage directory '" + storageDir + "' could not be created!");
        }
        _storageDir = rrdFilesDir.getAbsolutePath();
        _rrdBackendFactory = RrdBackendFactory.getFactory(RrdNioBackendFactory.NAME);
      }
    } catch (RrdException e) {
      throw new RuntimeException("Could not created JRobin backend factory!", e);
    }

    // Register default statistics:
    try {
      // Register the memory statistics:
      registerStatistics("usedMemory", "Used Heap Memory", "mb", "Used heap memory in megabytes", new ValueProvider() {
        @Override
        public double getValue(SystemData data) {
          return data.getMemoryHeap().getUsed() / BYTES_PER_MB;
        }
      });
      registerStatistics("usedNonHeapMemory", "Used Non Heap Memory", "mb", "Used non-heap memory in megabytes", new ValueProvider() {
        @Override
        public double getValue(SystemData data) {
          return data.getMemoryNonHeap().getUsed() / BYTES_PER_MB;
        }
      });
      registerStatistics("usedPhysicalMemory", "Used Physical Memory", "mb", "Used physical memory in megabytes", new ValueProvider() {
        @Override
        public double getValue(SystemData data) {
          return data.getMemoryPhysical().getUsed() / BYTES_PER_MB;
        }
      });

      // Register the threads/classes/gc statistics:
      registerStatistics("threads", "Live Threads", "", "Number of live threads", new ValueProvider() {
        @Override
        public double getValue(SystemData data) {
          return data.getThreadCurrentCount();
        }
      });
      registerStatistics("loadedClasses", "Loaded Classes", "", "Number of loaded classes", new ValueProvider() {
        @Override
        public double getValue(SystemData data) {
          return data.getCLLoadedClassCount();
        }
      });
      registerStatistics("gcTime", "Garbage Collector Time", "ms", "Garbage collector time per statistics frequency in milliseconds",
          new ValueProvider() {
            private long _lastGcTimeMillis;

            @Override
            public double getValue(SystemData data) {
              long gcTimeMillis = data.getGcCollectionTimeMillis() - _lastGcTimeMillis;
              _lastGcTimeMillis = data.getGcCollectionTimeMillis();
              return gcTimeMillis;
            }
          });

      // Register the load/files statistics:
      registerStatistics("vmLoad", "VM CPU Load", "%%", "Recent VM CPU load (all CPUs)", new ValueProvider() {
        @Override
        public double getValue(SystemData data) {
          return data.getProcessCpuLoadPct();
        }
      });
      registerStatistics("systemLoad", "System CPU Load", "%%", "Recent system CPU load (all CPUs)", new ValueProvider() {
        @Override
        public double getValue(SystemData data) {
          return data.getSystemCpuLoadPct();
        }
      });
      registerStatistics("fileDescriptors", "Open File Descriptors", "", "Number of open file descriptors", new ValueProvider() {
        @Override
        public double getValue(SystemData data) {
          return data.getOpenFileDescriptorCount();
        }
      });
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize the default statistics!", e);
    }
  }

  /**
   * Returns the singleton instance of this class or null if not yet initialized.
   */
  public static StatisticsCollector getInstance() {
    return _instance;
  }

  /**
   * Initializes and returns the singleton instance of this collector or returns the already initialized instance.
   *
   * @param storageDir where to store the statistics files
   * @param collectionFrequencyMillis the collection frequency in milliseconds
   * @param ref the instance of the class that references this collector; will be used when calling destroy again
   * @see #destroy(Object)
   */
  public static synchronized StatisticsCollector init(String storageDir, int collectionFrequencyMillis, Object ref) {
    if (_instance == null) {
      _instance = new StatisticsCollector(storageDir, collectionFrequencyMillis);
      _instance.startTimer();
    }
    _instanceRefs.add(ref);
    return _instance;
  }

  /**
   * Destroys the collector instance and stops the underlying timer task if the given
   * reference is the last reference to the collector instance.
   * @see #init(String, int, Object)
   */
  public static synchronized void destroy(Object ref) {
    _instanceRefs.remove(ref);
    if (_instanceRefs.size() == 0 && _instance != null) {
      _instance._timer.cancel();
      _instance = null;
    }
  }

  /**
   * Registers a new statistics with the given configuration.
   *
   * @param name the name of the statistics, must be a simple name without spaces and special characters
   * @param label the label to be shown on the generated statistics graphics
   * @param unit the unit to be shown on the generated statistics graphics
   * @param description the description to be shown on the UI (mouse over)
   * @param valueProvider the provider for statistics values
   * @throws IOException if the {@link Statistics} instance could not be created for example if the JRobin file could not be created
   */
  public void registerStatistics(String name, String label, String unit, String description, ValueProvider valueProvider) throws IOException {
    Statistics stats = new Statistics(name, label, unit, description, valueProvider, _storageDir, _collectionFrequencyMillis / 1000,
        _rrdBackendFactory);
    _statistics.add(stats);
  }

  private void startTimer() {
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
   * Returns the size of this statistics files on disk.
   */
  public long getDiskUsage() {
    return _diskUsage;
  }

  private synchronized void collect() {
    try {
      _lastCollectTimestamp = System.currentTimeMillis();
      // Note: Freeing memory with System.gc() before measuring stats might be cool but the performance
      // impact is huge (collect() becomes 10x slower and consumes a lot of CPU). Therefore we don't run it here anymore...
      collectData(new SystemData());

      // Calculate disk usage or memory size:
      if (_storageDir != null) {
        long sum = 0;
        File[] files = new File(_storageDir).listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            return name != null && name.endsWith(".rrd");
          }
        });
        if (files != null) {
          for (File file : files) {
            sum += file.length();
          }
        }
        _diskUsage = sum;
      }

      // Collect done:
      _lastCollectDurationMs = System.currentTimeMillis() - _lastCollectTimestamp;
    } catch (Throwable t) {
      LOG.warn("Exception while collecting data", t);
    }
  }

  private void collectData(SystemData data) throws IOException {
    for (Statistics statistics : _statistics) {
      statistics.collectValue(data);
    }
  }

  /**
   * Returns the {@link Statistics} with the given name.
   */
  public Statistics getStatistics(String graphName) {
    for (Statistics statistics : _statistics) {
      if (statistics.getName().equals(graphName)) {
        return statistics;
      }
    }
    return null;
  }

  /**
   * Returns the list of all {@link Statistics}.
   */
  public List<Statistics> getStatistics() {
    return _statistics;
  }
}
