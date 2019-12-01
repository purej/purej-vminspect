// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import org.junit.Assert;
import org.junit.Test;

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
    Assert.assertNull(StatisticsCollector.getInstance());
    StatisticsCollector collector = StatisticsCollector.init(null, 10000, this);
    Assert.assertSame(collector, StatisticsCollector.getInstance()); // Only one instance...
    Assert.assertSame(collector, StatisticsCollector.init(null, -1, this)); // Only one instance...
    try {
      Assert.assertEquals(null, collector.getStatisticsStorageDir());
      Assert.assertEquals(10000, collector.getCollectionFrequencyMillis());
      // Check measures after collect run:
      Assert.assertEquals(0, collector.getDiskUsage()); // no disk usage as in memory!
      Assert.assertEquals(0, collector.getLastCollectDurationMs()); // no more direct collect
      Assert.assertEquals(0, collector.getLastCollectTimestamp()); // no more direct collect

      // Make a collect call:
      collector.collect();
      Assert.assertTrue(collector.getLastCollectTimestamp() > 0);

      // Check stats retrieve:
      for (Statistics statistics : collector.getStatistics()) {
        Statistics statistics2 = collector.getStatistics(statistics.getName());
        Assert.assertSame(statistics, statistics2);

        // Create graphics:
        byte[] png = statistics.createGraph(Range.createPeriodRange(Period.DAY), 200, 200);
        Assert.assertNotNull(png);
      }
    } finally {
      StatisticsCollector.destroy(this);
    }
    Assert.assertNull(StatisticsCollector.getInstance());
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testCollectOnDisk() throws Exception {
    Assert.assertNull(StatisticsCollector.getInstance());
    StatisticsCollector collector = StatisticsCollector.init("target/stats-store", 10000, this);
    Assert.assertSame(collector, StatisticsCollector.getInstance()); // Only one instance...
    Assert.assertSame(collector, StatisticsCollector.init(null, -1, this)); // Only one instance...
    try {
      Assert.assertNotNull(collector.getStatisticsStorageDir());
      Assert.assertEquals(10000, collector.getCollectionFrequencyMillis());
      // Check measures again after collect run:
      Assert.assertEquals(0, collector.getDiskUsage());
      Assert.assertEquals(0, collector.getLastCollectDurationMs()); // no more direct collect
      Assert.assertEquals(0, collector.getLastCollectTimestamp()); // no more direct collect

      // Make a collect call:
      collector.collect();
      Assert.assertTrue(collector.getDiskUsage() > 0);
      Assert.assertTrue(collector.getLastCollectTimestamp() > 0);

      // Check stats retrieve:
      for (Statistics statistics : collector.getStatistics()) {
        Statistics statistics2 = collector.getStatistics(statistics.getName());
        Assert.assertSame(statistics, statistics2);

        // Create graphics:
        byte[] png = statistics.createGraph(Range.createPeriodRange(Period.DAY), 200, 200);
        Assert.assertNotNull(png);
      }
    } finally {
      StatisticsCollector.destroy(this);
    }
    Assert.assertNull(StatisticsCollector.getInstance());
  }
}
