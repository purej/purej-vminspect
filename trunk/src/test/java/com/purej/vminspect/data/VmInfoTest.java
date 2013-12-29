// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.List;
import org.junit.Test;

/**
 * Tests some VM infos.
 *
 * @author Stefan Mueller
 */
public class VmInfoTest {

  /**
   * Tests the named functionality.
   */
  @Test
  public void testMemoryInfos() throws Exception {
    SystemData vmInfos = new SystemData();
    System.out.println("VmInfos - Memory - HeapMax: " + vmInfos.getMemoryHeap().getMax());
    System.out.println("VmInfos - Memory - HeapUsed: " + vmInfos.getMemoryHeap().getUsed());
    System.out.println("VmInfos - Memory - NonHeapMax: " + vmInfos.getMemoryNonHeap().getMax());
    System.out.println("VmInfos - Memory - NonHeapUsed: " + vmInfos.getMemoryNonHeap().getUsed());
    System.out.println("VmInfos - Memory - PhysicalTotal: " + vmInfos.getMemoryPhysical().getMax());
    System.out.println("VmInfos - Memory - PhysicalUsed: " + vmInfos.getMemoryPhysical().getUsed());
    System.out.println("VmInfos - Memory - SwapTotal: " + vmInfos.getMemorySwap().getMax());
    System.out.println("VmInfos - Memory - SwapUsed: " + vmInfos.getMemorySwap().getUsed());
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testOsInfos() throws Exception {
    SystemData vmInfos = new SystemData();
    System.out.println("VmInfos - Os - Name: " + vmInfos.getOsName());
    System.out.println("VmInfos - Os - Architecture: " + vmInfos.getOsArchitecture());
    System.out.println("VmInfos - Os - Version: " + vmInfos.getOsVersion());
    System.out.println("VmInfos - Os - AvailableProcessors: " + vmInfos.getOsAvailableProcessors());
    System.out.println("VmInfos - Os - SystemCpuLoad: " + vmInfos.getSystemCpuLoadPct());
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testThreadInfos() throws Exception {
    SystemData vmInfos = new SystemData();
    System.out.println("VmInfos - Threads - ThreadCurrentCount: " + vmInfos.getThreadCurrentCount());

    List<ThreadData> threads = ThreadData.getAllThreads();
    for (int i = 0; i < threads.size(); i++) {
      System.out.println("VmInfos - Threads[" + i + "] - Name: " + threads.get(i).getName());
      System.out.println("VmInfos - Threads[" + i + "] - Priority: " + threads.get(i).getPriority());
      System.out.println("VmInfos - Threads[" + i + "] - State: " + threads.get(i).getState());
      System.out.println("VmInfos - Threads[" + i + "] - StackTrace: " + threads.get(i).getStackTrace().length);
      System.out.println("VmInfos - Threads[" + i + "] - CpuTimeMillis: " + threads.get(i).getCpuTimeMillis());
      System.out.println("VmInfos - Threads[" + i + "] - UserTimeMillis: " + threads.get(i).getUserTimeMillis());
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testOtherInfos() throws Exception {
    SystemData vmInfos = new SystemData();
    System.out.println("VmInfos - Other - LoadedClassCount: " + vmInfos.getCLLoadedClassCount());
    System.out.println("VmInfos - Other - ProcessCpuTimeMillis: " + vmInfos.getProcessCpuTimeMillis());
    System.out.println("VmInfos - Other - ProcessCpuLoad: " + vmInfos.getProcessCpuLoadPct());
  }
}
