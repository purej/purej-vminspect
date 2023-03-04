// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

  private final String _rtInfo;
  private final RuntimeMXBean _rtb;
  protected final OperatingSystemMXBean _osb;
  private final int _threadCurrentCount;
  private final int _clLoadedClassCount;
  private final long _clTotalLoadedClassCount;
  private final String _gcName;
  private final long _gcCollectionCount;
  private final long _gcCollectionTimeMillis;
  private final MemoryData _memoryHeap;
  private final MemoryData _memoryNonHeap;
  // Those values might be set by subclasses:
  protected MemoryData _memoryPhysical;
  protected MemoryData _memorySwap;
  protected long _processCpuTimeMillis;
  protected double _processCpuLoadPct;
  protected double _systemCpuLoadPct;
  protected long _openFileDescriptorCount;
  protected long _maxFileDescriptorCount;

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
    _rtInfo = System.getProperty("java.runtime.name") + ", " + System.getProperty("java.runtime.version");
    _rtb = ManagementFactory.getRuntimeMXBean();

    // Store thread-infos:
    ThreadMXBean tb = ManagementFactory.getThreadMXBean();
    _threadCurrentCount = tb.getThreadCount();

    // Get class-loader stuff:
    ClassLoadingMXBean clb = ManagementFactory.getClassLoadingMXBean();
    _clLoadedClassCount = clb.getLoadedClassCount();
    _clTotalLoadedClassCount = clb.getTotalLoadedClassCount();

    // Build gc name and collection time:
    long tmpGcCollectionCount = 0;
    long tmpGcCollectionTimeMillis = 0;
    StringBuilder tmpGcName = new StringBuilder();
    for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
      if (tmpGcName.length() > 0) {
        tmpGcName.append(", ");
      }
      tmpGcName.append(gc.getName());
      tmpGcCollectionCount += gc.getCollectionCount();
      tmpGcCollectionTimeMillis += gc.getCollectionTime();
    }
    _gcName = tmpGcName.toString();
    _gcCollectionCount = tmpGcCollectionCount;
    _gcCollectionTimeMillis = tmpGcCollectionTimeMillis;

    // Get heap / none heap memory:
    MemoryMXBean mb = ManagementFactory.getMemoryMXBean();
    _memoryHeap = new MemoryData(mb.getHeapMemoryUsage());
    _memoryNonHeap = new MemoryData(mb.getNonHeapMemoryUsage());

    // Physical / swap memory - Note: Most info is hidden in sun-classes, cannot check instance-of here
    // to prevent class-not-found-exception for VMs without sun-classes! See dedicated sub-class SunSystemData.
    _osb = ManagementFactory.getOperatingSystemMXBean();
    _memoryPhysical = MemoryData.UNKNOWN;
    _memorySwap = MemoryData.UNKNOWN;
    _processCpuTimeMillis = -1;
    _processCpuLoadPct = -1;
    _systemCpuLoadPct = -1;
    _openFileDescriptorCount = -1;
    _maxFileDescriptorCount = -1;
  }

  /**
   * Returns the java name and version.
   */
  public String getRtInfo() {
    return _rtInfo;
  }

  /**
   * The name of the runtime process.
   */
  public String getRtProcessName() {
    return _rtb.getName();
  }

  /**
   * Returns the startup time of this runtime process.
   */
  public Date getRtProcessStartup() {
    return new Date(_rtb.getStartTime());
  }

  /**
   * Returns the input arguments passed to the Java virtual machine.
   */
  public String getRtProcessArguments() {
    StringBuilder result = new StringBuilder();
    for (String jvmArg : _rtb.getInputArguments()) {
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
    List<String> lines = new ArrayList<String>();
    for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
      lines.add(entry.getKey() + "=" + entry.getValue());
    }
    Collections.sort(lines);
    StringBuilder result = new StringBuilder();
    for (String line : lines) {
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
    return _rtb.getVmName();
  }

  /**
   * The VM implementation vendor.
   */
  public String getVmVendor() {
    return _rtb.getVmVendor();
  }

  /**
   * The VM implementation version.
   */
  public String getVmVersion() {
    return _rtb.getVmVersion();
  }

  /**
   * The open file descriptors.
   */
  public long getOpenFileDescriptorCount() {
    return _openFileDescriptorCount;
  }

  /**
   * The max file descriptors.
   */
  public long getMaxFileDescriptorCount() {
    return _maxFileDescriptorCount;
  }

  /**
   * Returns the used/max heap memory.
   */
  public MemoryData getMemoryHeap() {
    return _memoryHeap;
  }

  /**
   * Returns the used/max none-heap memory.
   */
  public MemoryData getMemoryNonHeap() {
    return _memoryNonHeap;
  }

  /**
   * Returns the used/max physical memory.
   */
  public MemoryData getMemoryPhysical() {
    return _memoryPhysical;
  }

  /**
   * Returns the used/max physical swap memory.
   */
  public MemoryData getMemorySwap() {
    return _memorySwap;
  }

  /**
   * Returns the number of currently loaded classes.
   */
  public int getCLLoadedClassCount() {
    return _clLoadedClassCount;
  }

  /**
   * Returns the total number of classes loaded so far.
   */
  public long getCLTotalLoadedClassCount() {
    return _clTotalLoadedClassCount;
  }

  /**
   * Returns the Java class path that is used by the system class loader.
   */
  public String getCLClassPath() {
    return _rtb.getClassPath();
  }

  /**
   * Returns the boot class path that is used by the bootstrap class loader.
   */
  public String getCLBootClassPath() {
    return _rtb.isBootClassPathSupported() ? _rtb.getBootClassPath() : null;
  }

  /**
   * Returns the java library path.
   */
  public String getCLLibraryPath() {
    return _rtb.getLibraryPath();
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
    return _osb.getName();
  }

  /**
   * Returns the operating system architecture.
   */
  public String getOsArchitecture() {
    return _osb.getArch();
  }

  /**
   * Returns the operating system version.
   */
  public String getOsVersion() {
    return _osb.getVersion();
  }

  /**
   * Returns the number of processors (cores).
   */
  public int getOsAvailableProcessors() {
    return _osb.getAvailableProcessors();
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
    return _systemCpuLoadPct;
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
    return _processCpuLoadPct;
  }

  /**
   * Returns the CPU time used by the process on which the Java virtual machine is running in milliseconds.
   * <p/>
   * This method returns -1 if the the platform does not support this operation.
   */
  public long getProcessCpuTimeMillis() {
    return _processCpuTimeMillis;
  }
  
  /**
   * Returns the name of the garbage collector.
   */
  public String getGcName() {
    return _gcName;
  }

  /**
   * Returns the number of times garbage collection occurred.
   */
  public long getGcCollectionCount() {
    return _gcCollectionCount;
  }

  /**
   * Returns the sum of times of all garbage collection.
   */
  public long getGcCollectionTimeMillis() {
    return _gcCollectionTimeMillis;
  }

  /**
   * Returns the current number of threads.
   */
  public int getThreadCurrentCount() {
    return _threadCurrentCount;
  }
}
