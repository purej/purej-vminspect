// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.OperatingSystemMXBean;
import com.sun.management.UnixOperatingSystemMXBean;

/**
 * Subclass that downcasts to sun-classes to get more infos from {@link OperatingSystemMXBean}.
 *
 * @author Stefan Mueller
 */
@SuppressWarnings("restriction")
public class SunSystemData extends SystemData {

  /**
   * Creates a new instance of this class.
   */
  public SunSystemData() {
    if (_osb instanceof com.sun.management.OperatingSystemMXBean) {
      com.sun.management.OperatingSystemMXBean osb = (com.sun.management.OperatingSystemMXBean) _osb;

      long memPhysTotal = osb.getTotalPhysicalMemorySize();
      long memPhysFree = osb.getFreePhysicalMemorySize();
      _memoryPhysical = new MemoryData(memPhysTotal != -1 ? memPhysTotal - memPhysFree : -1, -1, memPhysTotal);
      long memSwapTotal = osb.getTotalSwapSpaceSize();
      long memSwapFree = osb.getFreeSwapSpaceSize();
      _memorySwap = new MemoryData(memSwapTotal != -1 ? memSwapTotal - memSwapFree : -1, -1, memSwapTotal);

      // Process Cpu time - value is in nanoseconds:
      long cpuTime = osb.getProcessCpuTime();
      _processCpuTimeMillis = cpuTime > 0 ? cpuTime / 1000000 : -1;

      // Process Cpu load - value is a double between 0..1:
      double cpuLoad = osb.getProcessCpuLoad();
      _processCpuLoadPct = cpuLoad < 0 ? cpuLoad : cpuLoad * 100;

      // System Cpu load - value is a double between 0..1:
      double systemLoad = osb.getSystemCpuLoad();
      _systemCpuLoadPct = systemLoad < 0 ? systemLoad : systemLoad * 100;
    }

    // Open/Max file descriptor count:
    if (_osb instanceof UnixOperatingSystemMXBean) {
      UnixOperatingSystemMXBean usb = (UnixOperatingSystemMXBean) _osb;
      _openFileDescriptorCount = usb.getOpenFileDescriptorCount();
      _maxFileDescriptorCount = usb.getMaxFileDescriptorCount();
    }
  }
}
