// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

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
    SystemData data = new SystemData();

    Assert.assertTrue(data.getMemoryHeap().getUsed() > 0);
    Assert.assertTrue(data.getMemoryHeap().getCommitted() > 0);
    Assert.assertTrue(data.getMemoryHeap().getMax() > 0);

    Assert.assertTrue(data.getMemoryNonHeap().getUsed() > 0);
    Assert.assertTrue(data.getMemoryNonHeap().getCommitted() > 0);
    // Assert.assertTrue(data.getMemoryNonHeap().getMax() > 0); Does not work on all platforms...

    // Note: Does not work on some machines/jdks...
    //Assert.assertTrue(data.getMemoryPhysical().getUsed() > 0);
    //Assert.assertTrue(data.getMemoryPhysical().getCommitted() > 0);
    Assert.assertTrue(data.getMemoryPhysical().getMax() > 0);

    // Note: Does not work on some machines/jdks...
    //Assert.assertTrue(data.getMemorySwap().getUsed() > 0);
    //Assert.assertTrue(data.getMemorySwap().getCommitted() > 0);
    Assert.assertTrue(data.getMemorySwap().getMax() > 0);
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testOsData() throws Exception {
    SystemData data = new SystemData();
    Assert.assertNotNull(data.getOsHostIp());
    Assert.assertNotNull(data.getOsName());
    Assert.assertNotNull(data.getOsArchitecture());
    Assert.assertNotNull(data.getOsVersion());
    Assert.assertTrue(data.getOsAvailableProcessors() > 0);
    Assert.assertTrue(data.getProcessCpuTimeMillis() > 0);
    // Note: Does not work on some machines/jdks...
    //Assert.assertTrue(data.getProcessCpuLoadPct() > 0);
    //Assert.assertTrue(data.getSystemCpuLoadPct() > 0);
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testRtVmData() throws Exception {
    SystemData data = new SystemData();
    Assert.assertNotNull(data.getRtInfo());
    Assert.assertNotNull(data.getRtProcessArguments());
    Assert.assertNotNull(data.getRtProcessName());
    Assert.assertNotNull(data.getRtSystemProperties());
    Assert.assertNotNull(data.getRtProcessStartup());
    Assert.assertNotNull(data.getVmName());
    Assert.assertNotNull(data.getVmVendor());
    Assert.assertNotNull(data.getVmVersion());
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testThreadData() throws Exception {
    SystemData data = new SystemData();
    Assert.assertTrue(data.getThreadCurrentCount() > 0);

    List<ThreadData> threads = ThreadData.getAllThreads();
    for (int i = 0; i < threads.size(); i++) {
      ThreadData td = threads.get(i);
      Assert.assertNotNull(td.getName());
      Assert.assertNotNull(td.getStackTrace());
      Assert.assertNotNull(td.getStackTraceString());
      Assert.assertNotNull(td.getState());
      Assert.assertEquals(false, td.isDeadlocked());
      //System.out.println("VmInfos - Threads[" + i + "] - Name: " + td.getName());
      //System.out.println("VmInfos - Threads[" + i + "] - Priority: " + td.getPriority());
      //System.out.println("VmInfos - Threads[" + i + "] - State: " + td.getState());
      //System.out.println("VmInfos - Threads[" + i + "] - StackTrace: " + td.getStackTrace().length);
      //System.out.println("VmInfos - Threads[" + i + "] - CpuTimeMillis: " + td.getCpuTimeMillis());
      //System.out.println("VmInfos - Threads[" + i + "] - UserTimeMillis: " + td.getUserTimeMillis());
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testCLData() throws Exception {
    SystemData data = new SystemData();
    Assert.assertNotNull(data.getCLBootClassPath());
    Assert.assertNotNull(data.getCLClassPath());
    Assert.assertNotNull(data.getCLLibraryPath());
    Assert.assertTrue(data.getCLLoadedClassCount() > 0);
    Assert.assertTrue(data.getCLTotalLoadedClassCount() > 0);
    // Note: gc execution cannot be forced...
    //Assert.assertTrue(data.getGcCollectionCount() > 0);
    //Assert.assertTrue(data.getGcCollectionTimeMillis() > 0);
  }
}
