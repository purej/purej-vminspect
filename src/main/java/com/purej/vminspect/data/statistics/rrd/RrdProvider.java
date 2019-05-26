package com.purej.vminspect.data.statistics.rrd;

import java.io.File;
import java.io.IOException;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdFileBackendFactory;
import org.rrd4j.core.RrdMemoryBackendFactory;
import org.rrd4j.core.RrdRandomAccessFileBackendFactory;

/**
 * Abstracts the RRD implementation provider like RRD4J, JRobin, etc.
 * Allows easier switching and testing with different providers.
 * <p/>
 * Note: Until Mai 2019, VmInspect used JRobin as RRD implementation
 * but due to high memory usage and JRobin not maintained anymore,
 * we switched to RRD4J, see https://github.com/rrd4j/rrd4j. 
 *
 * @author Stefan Mueller
 */
public class RrdProvider {
  private final String _storageDir;
  private final int _collectionFrequencySecs;
  private final boolean _isRrd4j;
  private final Object _backendFactory;

  /**
   * Creates a new instance.
   *
   * @param storageDir where to store the statistics files
   * @param collectionFrequencyMillis the collection frequency in milliseconds
   */
  public RrdProvider(String storageDir, int collectionFrequencyMillis) {
    _collectionFrequencySecs = collectionFrequencyMillis / 1000;

    // Ensure storage-dir:
    if (storageDir == null || storageDir.isEmpty()) {
      _storageDir = null;
    } else {
      // Create the storage dir if not existing:
      File rrdFilesDir = new File(storageDir);
      if (!rrdFilesDir.exists() && !rrdFilesDir.mkdirs()) {
        throw new RuntimeException("Statistics storage directory '" + storageDir + "' could not be created!");
      }
      _storageDir = rrdFilesDir.getAbsolutePath();
    }

    // Currently only 2 providers (rrd4j, jrobin):
    _isRrd4j = isAvailable("org.rrd4j.core.RrdBackendFactory");
    if (_isRrd4j) {
      _backendFactory = createRrd4JBackendFactory(_storageDir);
    } else {
      _backendFactory = createJRobinBackendFactory(_storageDir);
    }
  }

  private static boolean isAvailable(String clz) {
    try {
      Class.forName(clz);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  private static Object createRrd4JBackendFactory(String storageDir) {
    if (storageDir == null) {
      return new RrdMemoryBackendFactory();
    } else {
      return new RrdRandomAccessFileBackendFactory();
    }
  }

  private static Object createJRobinBackendFactory(String storageDir) {
    try {
      if (storageDir == null) {
        return RrdBackendFactory.getFactory(org.jrobin.core.RrdMemoryBackendFactory.NAME);
      } else {
        return RrdBackendFactory.getFactory(RrdFileBackendFactory.NAME);
      }
    } catch (Exception e) {
      throw new RuntimeException("Could not create JRobin backend factory!", e);
    }
  }

  /**
   * Returns the configured, optional storage directory.
   *
   * @return the storage dir or null if no persistent storage
   */
  public String getStorageDir() {
    return _storageDir;
  }

  /**
   * Creates the single round-robin-database with the given name.
   *
   * @return the new RRD implementation
   * @throws IOException if an I/O error occurred
   */
  public Rrd create(String name) throws IOException {
    if (_isRrd4j) {
      return new Rrd4jImpl(name, _storageDir, _collectionFrequencySecs, _backendFactory);
    } else {
      return new JRobinImpl(name, _storageDir, _collectionFrequencySecs, _backendFactory);
    }
  }
}
