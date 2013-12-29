// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import org.junit.Test;

/**
 * Tests some MX bean stuff.
 *
 * @author Stefan Mueller
 */
public class MxBeansTest {
  private static final int ALLOCATE = 0;

  /**
   * Tests the named functionality.
   */
  @Test
  public void testMemoryInfo() throws Exception {
    byte[] allocated = new byte[ALLOCATE];

    System.out.println("Runtime - Total Memory: " + Runtime.getRuntime().totalMemory());
    System.out.println("Runtime - Free Memory: " + Runtime.getRuntime().freeMemory());
    System.out.println("Runtime - Max Memory: " + Runtime.getRuntime().maxMemory());
    System.out.println("Runtime - Used Memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
    System.out.println();

    MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
    System.out.println("MemoryMXBean - Heap Init Memory: " + bean.getHeapMemoryUsage().getInit());
    System.out.println("MemoryMXBean - Heap Max Memory: " + bean.getHeapMemoryUsage().getMax());
    System.out.println("MemoryMXBean - Heap Used Memory: " + bean.getHeapMemoryUsage().getUsed());
    System.out.println("MemoryMXBean - Heap Commited Memory: " + bean.getHeapMemoryUsage().getCommitted());
    System.out.println();

    System.out.println("MemoryMXBean - NonHeap Init Memory: " + bean.getNonHeapMemoryUsage().getInit());
    System.out.println("MemoryMXBean - NonHeap Max Memory: " + bean.getNonHeapMemoryUsage().getMax());
    System.out.println("MemoryMXBean - NonHeap Used Memory: " + bean.getNonHeapMemoryUsage().getUsed());
    System.out.println("MemoryMXBean - NonHeap Commited Memory: " + bean.getNonHeapMemoryUsage().getCommitted());
    System.out.println();

    for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
      System.out.println("MemoryPoolMXBean - Name: " + pool.getName());
      System.out.println("MemoryPoolMXBean - Init Memory: " + pool.getUsage().getInit());
      System.out.println("MemoryPoolMXBean - Max Memory: " + pool.getUsage().getMax());
      System.out.println("MemoryPoolMXBean - Used Memory: " + pool.getUsage().getUsed());
      System.out.println("MemoryPoolMXBean - Commited Memory: " + pool.getUsage().getCommitted());
      System.out.println();
    }

    System.out.println("Bytes allocated: " + allocated.length);
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testOsInfo() throws Exception {
    OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
    System.out.println("OsBean - Name: " + bean.getName());
    System.out.println("OsBean - Arch: " + bean.getArch());
    System.out.println("OsBean - Version: " + bean.getVersion());
    System.out.println("OsBean - AvailableProcessors: " + bean.getAvailableProcessors());
    System.out.println("OsBean - SystemLoadAverage: " + bean.getSystemLoadAverage());
    System.out.println();
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testRtInfo() throws Exception {
    RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
    System.out.println("RuntimeMXBean - Name: " + bean.getName());
    System.out.println("RuntimeMXBean - VmName: " + bean.getVmName());
    System.out.println("RuntimeMXBean - VmVendor: " + bean.getVmVendor());
    System.out.println("RuntimeMXBean - VmVersion: " + bean.getVmVersion());
    System.out.println("RuntimeMXBean - SpecName: " + bean.getSpecName());
    System.out.println("RuntimeMXBean - SpecVendor: " + bean.getSpecVendor());
    System.out.println("RuntimeMXBean - SpecVersion: " + bean.getSpecVersion());
    System.out.println("RuntimeMXBean - ManagementSpecVersion: " + bean.getManagementSpecVersion());
    System.out.println("RuntimeMXBean - ClassPath: " + bean.getClassPath());
    System.out.println("RuntimeMXBean - LibraryPath: " + bean.getLibraryPath());
    System.out.println("RuntimeMXBean - BootClassPath: " + bean.getBootClassPath());
    System.out.println("RuntimeMXBean - StartTime: " + new Date(bean.getStartTime()));
    System.out.println();
    System.out.println("java.runtime.name: " + System.getProperty("java.runtime.name"));
    System.out.println("java.runtime.version: " + System.getProperty("java.runtime.version"));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testMBeanServers() throws Exception {
    MBeanServer platformServer = ManagementFactory.getPlatformMBeanServer();
    System.out.println("PlatformMBeanServer - DefaultDomain: " + platformServer.getDefaultDomain());
    System.out.println("PlatformMBeanServer - MBeanCount: " + platformServer.getMBeanCount());
    List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
    for (MBeanServer server : servers) {
      System.out.println("MBeanServer - DefaultDomain: " + server.getDefaultDomain());
      System.out.println("MBeanServer - MBeanCount: " + server.getMBeanCount());
    }
  }
}
