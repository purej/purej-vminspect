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
    if (osb instanceof com.sun.management.OperatingSystemMXBean) {
      var ssb = (com.sun.management.OperatingSystemMXBean)osb;

      var memPhysTotal = ssb.getTotalMemorySize();
      var memPhysFree = ssb.getFreeMemorySize();
      memoryPhysical = new MemoryData(memPhysTotal != -1 ? memPhysTotal - memPhysFree : -1, -1, memPhysTotal);
      var memSwapTotal = ssb.getTotalSwapSpaceSize();
      var memSwapFree = ssb.getFreeSwapSpaceSize();
      memorySwap = new MemoryData(memSwapTotal != -1 ? memSwapTotal - memSwapFree : -1, -1, memSwapTotal);

      // Process Cpu time - value is in nanoseconds:
      var cpuTime = ssb.getProcessCpuTime();
      processCpuTimeMillis = cpuTime > 0 ? cpuTime / 1000000 : -1;

      // Process Cpu load - value is a double between 0..1:
      var cpuLoad = ssb.getProcessCpuLoad();
      processCpuLoadPct = cpuLoad < 0 ? cpuLoad : cpuLoad * 100;

      // System Cpu load - value is a double between 0..1:
      var systemLoad = ssb.getCpuLoad();
      systemCpuLoadPct = systemLoad < 0 ? systemLoad : systemLoad * 100;
    }

    // Open/Max file descriptor count:
    if (osb instanceof UnixOperatingSystemMXBean) {
      var usb = (UnixOperatingSystemMXBean)osb;
      openFileDescriptorCount = usb.getOpenFileDescriptorCount();
      maxFileDescriptorCount = usb.getMaxFileDescriptorCount();
    }
  }
}
