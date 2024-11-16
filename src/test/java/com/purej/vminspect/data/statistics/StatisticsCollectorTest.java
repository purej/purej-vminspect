// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the named functionality.
 *
 * @author Stefan Mueller
 */
public class StatisticsCollectorTest {

  /**
   * Tests the named functionality.
   */
  @Test
  public void testCollectInMemory() throws Exception {
    Assertions.assertNull(StatisticsCollector.getInstance());
    StatisticsCollector collector = StatisticsCollector.init(null, 10000, this);
    Assertions.assertSame(collector, StatisticsCollector.getInstance()); // Only one instance...
    Assertions.assertSame(collector, StatisticsCollector.init(null, -1, this)); // Only one instance...
    try {
      Assertions.assertEquals(null, collector.getStatisticsStorageDir());
      Assertions.assertEquals(10000, collector.getCollectionFrequencyMillis());
      // Check measures after collect run:
      Assertions.assertEquals(0, collector.getDiskUsage()); // no disk usage as in memory!
      Assertions.assertEquals(0, collector.getLastCollectDurationMs()); // no more direct collect
      Assertions.assertEquals(0, collector.getLastCollectTimestamp()); // no more direct collect

      // Make a collect call:
      collector.collect();
      Assertions.assertTrue(collector.getLastCollectTimestamp() > 0);

      // Check stats retrieve:
      for (Statistics statistics : collector.getStatistics()) {
        Statistics statistics2 = collector.getStatistics(statistics.getName());
        Assertions.assertSame(statistics, statistics2);

        // Create graphics:
        byte[] png = statistics.createGraph(Range.createPeriodRange(Period.DAY), 200, 200);
        Assertions.assertNotNull(png);
      }
    } finally {
      StatisticsCollector.destroy(this);
    }
    Assertions.assertNull(StatisticsCollector.getInstance());
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testCollectOnDisk() throws Exception {
    Assertions.assertNull(StatisticsCollector.getInstance());
    StatisticsCollector collector = StatisticsCollector.init("target/stats-store", 10000, this);
    Assertions.assertSame(collector, StatisticsCollector.getInstance()); // Only one instance...
    Assertions.assertSame(collector, StatisticsCollector.init(null, -1, this)); // Only one instance...
    try {
      Assertions.assertNotNull(collector.getStatisticsStorageDir());
      Assertions.assertEquals(10000, collector.getCollectionFrequencyMillis());
      // Check measures again after collect run:
      Assertions.assertEquals(0, collector.getDiskUsage());
      Assertions.assertEquals(0, collector.getLastCollectDurationMs()); // no more direct collect
      Assertions.assertEquals(0, collector.getLastCollectTimestamp()); // no more direct collect

      // Make a collect call:
      collector.collect();
      Assertions.assertTrue(collector.getDiskUsage() > 0);
      Assertions.assertTrue(collector.getLastCollectTimestamp() > 0);

      // Check stats retrieve:
      for (Statistics statistics : collector.getStatistics()) {
        Statistics statistics2 = collector.getStatistics(statistics.getName());
        Assertions.assertSame(statistics, statistics2);

        // Create graphics:
        byte[] png = statistics.createGraph(Range.createPeriodRange(Period.DAY), 200, 200);
        Assertions.assertNotNull(png);
      }
    } finally {
      StatisticsCollector.destroy(this);
    }
    Assertions.assertNull(StatisticsCollector.getInstance());
  }
}
