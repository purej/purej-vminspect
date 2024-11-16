// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Provides information about the virtual machine currently running in.
 *
 * @author Stefan Mueller
 */
public class SystemData {
  // Host name and IP:
  private static final String HOST_IP;
  // If sun-classes exists in classpath:
  private static final boolean SUN_CLASSES_EXIST;
  static {
    // Calc localhost:
    String hostIp;
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      hostIp = localHost.getHostName() + " (" + localHost.getHostAddress() + ")";
    } catch (Exception e) {
      hostIp = "Unknown";
    }
    HOST_IP = hostIp;

    // Calc sun-classes:
    boolean sunexists = false;
    try {
      Class.forName("com.sun.management.OperatingSystemMXBean");
      Class.forName("com.sun.management.UnixOperatingSystemMXBean");
      sunexists = true;
    } catch (Throwable t) {
      // Ignore...
    }
    SUN_CLASSES_EXIST = sunexists;
  }

  private final String rtInfo;
  private final RuntimeMXBean rtb;
  protected final OperatingSystemMXBean osb;
  private final int threadCurrentCount;
  private final int clLoadedClassCount;
  private final long clTotalLoadedClassCount;
  private final String gcName;
  private final long gcCollectionCount;
  private final long gcCollectionTimeMillis;
  private final MemoryData memoryHeap;
  private final MemoryData memoryNonHeap;
  // Those values might be set by subclasses:
  protected MemoryData memoryPhysical;
  protected MemoryData memorySwap;
  protected long processCpuTimeMillis;
  protected double processCpuLoadPct;
  protected double systemCpuLoadPct;
  protected long openFileDescriptorCount;
  protected long maxFileDescriptorCount;

  /**
   * Creates a new instanceof of the correct SystemData instance.
   */
  public static SystemData create() {
    return SUN_CLASSES_EXIST ? new SunSystemData() : new SystemData();
  }

  /**
   * Creates a new instance of this class.
   */
  protected SystemData() {
    // Store runtime-infos:
    rtInfo = System.getProperty("java.runtime.name") + ", " + System.getProperty("java.runtime.version");
    rtb = ManagementFactory.getRuntimeMXBean();

    // Store thread-infos:
    var tb = ManagementFactory.getThreadMXBean();
    threadCurrentCount = tb.getThreadCount();

    // Get class-loader stuff:
    var clb = ManagementFactory.getClassLoadingMXBean();
    clLoadedClassCount = clb.getLoadedClassCount();
    clTotalLoadedClassCount = clb.getTotalLoadedClassCount();

    // Build gc name and collection time:
    var tmpGcCollectionCount = 0L;
    var tmpGcCollectionTimeMillis = 0L;
    var tmpGcName = new StringBuilder();
    for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
      if (tmpGcName.length() > 0) {
        tmpGcName.append(", ");
      }
      tmpGcName.append(gc.getName());
      tmpGcCollectionCount += gc.getCollectionCount();
      tmpGcCollectionTimeMillis += gc.getCollectionTime();
    }
    gcName = tmpGcName.toString();
    gcCollectionCount = tmpGcCollectionCount;
    gcCollectionTimeMillis = tmpGcCollectionTimeMillis;

    // Get heap / none heap memory:
    var mb = ManagementFactory.getMemoryMXBean();
    memoryHeap = new MemoryData(mb.getHeapMemoryUsage());
    memoryNonHeap = new MemoryData(mb.getNonHeapMemoryUsage());

    // Physical / swap memory - Note: Most info is hidden in sun-classes, cannot check instance-of here
    // to prevent class-not-found-exception for VMs without sun-classes! See dedicated sub-class SunSystemData.
    osb = ManagementFactory.getOperatingSystemMXBean();
    memoryPhysical = MemoryData.UNKNOWN;
    memorySwap = MemoryData.UNKNOWN;
    processCpuTimeMillis = -1;
    processCpuLoadPct = -1;
    systemCpuLoadPct = -1;
    openFileDescriptorCount = -1;
    maxFileDescriptorCount = -1;
  }

  /**
   * Returns the java name and version.
   */
  public String getRtInfo() {
    return rtInfo;
  }

  /**
   * The name of the runtime process.
   */
  public String getRtProcessName() {
    return rtb.getName();
  }

  /**
   * Returns the startup time of this runtime process.
   */
  public Date getRtProcessStartup() {
    return new Date(rtb.getStartTime());
  }

  /**
   * Returns the input arguments passed to the Java virtual machine.
   */
  public String getRtProcessArguments() {
    StringBuilder result = new StringBuilder();
    for (String jvmArg : rtb.getInputArguments()) {
      if (result.length() > 0) {
        result.append('\n');
      }
      result.append(jvmArg);
    }
    return result.toString();
  }

  /**
   * Returns the current system properties.
   */
  public String getRtSystemProperties() {
    var lines = new ArrayList<String>();
    for (var entry : System.getProperties().entrySet()) {
      lines.add(entry.getKey() + "=" + entry.getValue());
    }
    Collections.sort(lines);
    var result = new StringBuilder();
    for (var line : lines) {
      if (result.length() > 0) {
        result.append('\n');
      }
      result.append(line);
    }
    return result.toString();
  }

  /**
   * The VM implementation name.
   */
  public String getVmName() {
    return rtb.getVmName();
  }

  /**
   * The VM implementation vendor.
   */
  public String getVmVendor() {
    return rtb.getVmVendor();
  }

  /**
   * The VM implementation version.
   */
  public String getVmVersion() {
    return rtb.getVmVersion();
  }

  /**
   * The open file descriptors.
   */
  public long getOpenFileDescriptorCount() {
    return openFileDescriptorCount;
  }

  /**
   * The max file descriptors.
   */
  public long getMaxFileDescriptorCount() {
    return maxFileDescriptorCount;
  }

  /**
   * Returns the used/max heap memory.
   */
  public MemoryData getMemoryHeap() {
    return memoryHeap;
  }

  /**
   * Returns the used/max none-heap memory.
   */
  public MemoryData getMemoryNonHeap() {
    return memoryNonHeap;
  }

  /**
   * Returns the used/max physical memory.
   */
  public MemoryData getMemoryPhysical() {
    return memoryPhysical;
  }

  /**
   * Returns the used/max physical swap memory.
   */
  public MemoryData getMemorySwap() {
    return memorySwap;
  }

  /**
   * Returns the number of currently loaded classes.
   */
  public int getCLLoadedClassCount() {
    return clLoadedClassCount;
  }

  /**
   * Returns the total number of classes loaded so far.
   */
  public long getCLTotalLoadedClassCount() {
    return clTotalLoadedClassCount;
  }

  /**
   * Returns the Java class path that is used by the system class loader.
   */
  public String getCLClassPath() {
    return rtb.getClassPath();
  }

  /**
   * Returns the boot class path that is used by the bootstrap class loader.
   */
  public String getCLBootClassPath() {
    return rtb.isBootClassPathSupported() ? rtb.getBootClassPath() : null;
  }

  /**
   * Returns the java library path.
   */
  public String getCLLibraryPath() {
    return rtb.getLibraryPath();
  }

  /**
   * The host name and IP.
   */
  public String getOsHostIp() {
    return HOST_IP;
  }

  /**
   * Returns the operating system name.
   */
  public String getOsName() {
    return osb.getName();
  }

  /**
   * Returns the operating system architecture.
   */
  public String getOsArchitecture() {
    return osb.getArch();
  }

  /**
   * Returns the operating system version.
   */
  public String getOsVersion() {
    return osb.getVersion();
  }

  /**
   * Returns the number of processors (cores).
   */
  public int getOsAvailableProcessors() {
    return osb.getAvailableProcessors();
  }

  /**
   * Returns the "recent cpu usage" for the whole system in percentage between 0-100.
   * <p/>
   * The value in percentage 0-100. A value of 0.0 means that all CPUs were idle during the recent period
   * of time observed, while a value of 100% means that all CPUs were actively running 100% of the time during the recent period
   * being observed.
   * <p/>
   * If the system recent cpu usage is not available, the method returns -1.
   */
  public double getSystemCpuLoadPct() {
    return systemCpuLoadPct;
  }

  /**
   * Returns the "recent cpu usage" for the Java Virtual Machine process in percentage between 0-100.
   * <p/>
   * This value is a double in the [0.0,1.0] interval. A value of 0.0 means that none of the CPUs were running threads from
   * the JVM process during the recent period of time observed, while a value of 1.0 means that all CPUs were actively running
   * threads from the JVM 100% of the time during the recent period being observed. Threads from the JVM include the application
   * threads as well as the JVM internal threads.
   * <p/>
   * If the Java Virtual Machine recent CPU usage is not available, the method returns -1.
   */
  public double getProcessCpuLoadPct() {
    return processCpuLoadPct;
  }

  /**
   * Returns the CPU time used by the process on which the Java virtual machine is running in milliseconds.
   * <p/>
   * This method returns -1 if the the platform does not support this operation.
   */
  public long getProcessCpuTimeMillis() {
    return processCpuTimeMillis;
  }
  
  /**
   * Returns the name of the garbage collector.
   */
  public String getGcName() {
    return gcName;
  }

  /**
   * Returns the number of times garbage collection occurred.
   */
  public long getGcCollectionCount() {
    return gcCollectionCount;
  }

  /**
   * Returns the sum of times of all garbage collection.
   */
  public long getGcCollectionTimeMillis() {
    return gcCollectionTimeMillis;
  }

  /**
   * Returns the current number of threads.
   */
  public int getThreadCurrentCount() {
    return threadCurrentCount;
  }
}
