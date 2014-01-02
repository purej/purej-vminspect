package com.purej.vminspect.data.statistics;

import junit.framework.Assert;
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
    StatisticsCollector collector = new StatisticsCollector(null, 10000);
    Assert.assertEquals(null, collector.getStatisticsStorageDir());
    Assert.assertEquals(10000, collector.getCollectionFrequencyMillis());
    // All measures are @ zero before first collect run:
    Assert.assertEquals(0, collector.getDiskUsage());
    Assert.assertEquals(0, collector.getEstimatedMemorySize());
    Assert.assertEquals(0, collector.getLastCollectDurationMs());
    Assert.assertEquals(0, collector.getLastCollectTimestamp());

    // Start to collect once:
    collector.start();
    collector.stop();

    // Check measures again after collect run:
    Assert.assertEquals(0, collector.getDiskUsage()); // no disk usage as in memory!
    Assert.assertTrue(collector.getEstimatedMemorySize() > 100000);
    Assert.assertTrue(collector.getLastCollectDurationMs() > 0);
    Assert.assertTrue(collector.getLastCollectTimestamp() > 0);

    // Check stats retrieve:
    for (Statistics statistics : collector.getStatistics()) {
      Statistics statistics2 = collector.getStatistics(statistics.getName());
      Assert.assertSame(statistics, statistics2);

      // Create graphics:
      byte[] png = statistics.createGraph(Range.createPeriodRange(Period.DAY), 200, 200);
      Assert.assertNotNull(png);
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testCollectOnDisk() throws Exception {
    StatisticsCollector collector = new StatisticsCollector("target/stats-store", 10000);
    Assert.assertNotNull(collector.getStatisticsStorageDir());
    Assert.assertEquals(10000, collector.getCollectionFrequencyMillis());
    // All measures are @ zero before first collect run:
    Assert.assertEquals(0, collector.getDiskUsage());
    Assert.assertEquals(0, collector.getEstimatedMemorySize());
    Assert.assertEquals(0, collector.getLastCollectDurationMs());
    Assert.assertEquals(0, collector.getLastCollectTimestamp());

    // Start to collect once:
    collector.start();
    collector.stop();

    // Check measures again after collect run:
    Assert.assertTrue(collector.getDiskUsage() > 0);
    Assert.assertEquals(0, collector.getEstimatedMemorySize()); // No memory usage as on disk!
    Assert.assertTrue(collector.getLastCollectDurationMs() > 0);
    Assert.assertTrue(collector.getLastCollectTimestamp() > 0);

    // Check stats retrieve:
    for (Statistics statistics : collector.getStatistics()) {
      Statistics statistics2 = collector.getStatistics(statistics.getName());
      Assert.assertSame(statistics, statistics2);

      // Create graphics:
      byte[] png = statistics.createGraph(Range.createPeriodRange(Period.DAY), 200, 200);
      Assert.assertNotNull(png);
    }
  }
}
