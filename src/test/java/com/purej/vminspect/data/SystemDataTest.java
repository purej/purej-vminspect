// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the named functionality.
 *
 * @author Stefan Mueller
 */
public class SystemDataTest {

  /**
   * Tests the named functionality.
   */
  @Test
  public void testMemoryData() throws Exception {
    // Test with default system-data (no sun-infos):
    SystemData data = new SystemData();

    Assertions.assertTrue(data.getMemoryHeap().getUsed() > 0);
    Assertions.assertTrue(data.getMemoryHeap().getCommitted() > 0);
    Assertions.assertTrue(data.getMemoryHeap().getMax() > 0);

    Assertions.assertTrue(data.getMemoryNonHeap().getUsed() > 0);
    Assertions.assertTrue(data.getMemoryNonHeap().getCommitted() > 0);
    // Assertions.assertTrue(data.getMemoryNonHeap().getMax() > 0); Does not work on all platforms...
    Assertions.assertSame(data.getMemoryPhysical(), MemoryData.UNKNOWN);
    Assertions.assertSame(data.getMemorySwap(), MemoryData.UNKNOWN);

    // Now test sun-class:
    data = new SunSystemData();

    Assertions.assertTrue(data.getMemoryPhysical().getUsed() > 0);
    // Assertions.assertTrue(data.getMemoryPhysical().getCommitted() > 0);
    Assertions.assertTrue(data.getMemoryPhysical().getMax() > 0);

    Assertions.assertTrue(data.getMemorySwap().getUsed() > 0);
    // Assertions.assertTrue(data.getMemorySwap().getCommitted() > 0);
    Assertions.assertTrue(data.getMemorySwap().getMax() > 0);
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testOsData() throws Exception {
    // Test without sun-classes:
    SystemData data = new SystemData();
    Assertions.assertNotNull(data.getOsHostIp());
    Assertions.assertNotNull(data.getOsName());
    Assertions.assertNotNull(data.getOsArchitecture());
    Assertions.assertNotNull(data.getOsVersion());
    Assertions.assertTrue(data.getOsAvailableProcessors() > 0);
    Assertions.assertEquals(data.getProcessCpuTimeMillis(), -1);

    // Test sun-infos:
    data = new SunSystemData();
    Assertions.assertTrue(data.getProcessCpuTimeMillis() > 0);
    // Note: Does not work on some machines/jdks...
    Assertions.assertTrue(data.getProcessCpuLoadPct() > -1);
    //Assertions.assertTrue(data.getSystemCpuLoadPct() > 0);
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testRtVmData() throws Exception {
    SystemData data = new SystemData();
    Assertions.assertNotNull(data.getRtInfo());
    Assertions.assertNotNull(data.getRtProcessArguments());
    Assertions.assertNotNull(data.getRtProcessName());
    Assertions.assertNotNull(data.getRtSystemProperties());
    Assertions.assertNotNull(data.getRtProcessStartup());
    Assertions.assertNotNull(data.getVmName());
    Assertions.assertNotNull(data.getVmVendor());
    Assertions.assertNotNull(data.getVmVersion());
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testThreadData() throws Exception {
    SystemData data = new SystemData();
    Assertions.assertTrue(data.getThreadCurrentCount() > 0);

    List<ThreadData> threads = ThreadData.getAllThreads();
    for (int i = 0; i < threads.size(); i++) {
      ThreadData td = threads.get(i);
      Assertions.assertNotNull(td.getName());
      Assertions.assertNotNull(td.getStackTrace());
      Assertions.assertNotNull(td.getStackTraceString());
      Assertions.assertNotNull(td.getState());
      Assertions.assertEquals(false, td.isDeadlocked());
      // System.out.println("VmInfos - Threads[" + i + "] - Name: " + td.getName());
      // System.out.println("VmInfos - Threads[" + i + "] - Priority: " +
      // td.getPriority());
      // System.out.println("VmInfos - Threads[" + i + "] - State: " + td.getState());
      // System.out.println("VmInfos - Threads[" + i + "] - StackTrace: " +
      // td.getStackTrace().length);
      // System.out.println("VmInfos - Threads[" + i + "] - CpuTimeMillis: " +
      // td.getCpuTimeMillis());
      // System.out.println("VmInfos - Threads[" + i + "] - UserTimeMillis: " +
      // td.getUserTimeMillis());
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testCLData() throws Exception {
    SystemData data = new SystemData();
    // Note: Not supported by all java versions
    // Assertions.assertNotNull(data.getCLBootClassPath());
    Assertions.assertNotNull(data.getCLClassPath());
    Assertions.assertNotNull(data.getCLLibraryPath());
    Assertions.assertTrue(data.getCLLoadedClassCount() > 0);
    Assertions.assertTrue(data.getCLTotalLoadedClassCount() > 0);
    // Note: gc execution cannot be forced...
    // Assertions.assertTrue(data.getGcCollectionCount() > 0);
    // Assertions.assertTrue(data.getGcCollectionTimeMillis() > 0);
  }
}
