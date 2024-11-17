// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.util.ArrayList;
import java.util.List;
import com.purej.vminspect.data.MemoryData;
import com.purej.vminspect.data.SystemData;

/**
 * Displays a system main page.
 *
 * @author Stefan Mueller
 */
public final class SystemMainView extends AbstractHtmlView {
  private static final double BAR_SCALE = 1.5;
  private static final int BAR_PIXELS = (int) (100 * BAR_SCALE);

  /**
   * Stores a memory bar picture and it's width.
   */
  private static class MemoryBarPic {
    private final String pic;
    private int width;

    public MemoryBarPic(String pic, int width) {
      this.pic = pic;
      this.width = width;
    }
  }

  private final SystemData sysData;

  /**
   * Creates a new instance of this view.
   */
  public SystemMainView(StringBuilder output, SystemData sysData) {
    super(output);
    this.sysData = sysData;
  }

  @Override
  public void render() {
    writeMemoryTable();
    writeRuntimeTable();
    writeGcTable();
    writeClassloaderTable();
  }

  private void writeMemoryTable() {
    write("<h3>").writeImg("icons/battery-24.png", "Memory").write("&nbsp;Memory</h3>");
    var table = new CandyHtmlTable("Memory", "Name", "Value", "Status");
    writeMemoryRow(table, "Heap Memory", sysData.getMemoryHeap());
    writeMemoryRow(table, "NonHeap Memory", sysData.getMemoryNonHeap());
    writeMemoryRow(table, "Physical Memory", sysData.getMemoryPhysical());
    writeMemoryRow(table, "Swap Memory", sysData.getMemorySwap());
    table.endTable();
    writeln("<br/>");
  }

  private void writeRuntimeTable() {
    write("<h3>").writeImg("icons/cup-24.png", "Runtime").writeln("&nbsp;Runtime</h3>");
    var table = new CandyHtmlTable("Runtime", "Name", "Value");
    table.nextRow("Host", sysData.getOsHostIp());
    table.nextRow("OS", sysData.getOsName() + ", Architecture: " + sysData.getOsArchitecture() + ", Version: " + sysData.getOsVersion()
        + ", Processors: " + sysData.getOsAvailableProcessors());
    table.nextRow("Java", sysData.getRtInfo());
    table.nextRow("Virtual Machine", sysData.getVmName() + ", Vendor: " + sysData.getVmVendor() + ", Version: " + sysData.getVmVersion());
    table.nextRow("VM Process Name", sysData.getRtProcessName());
    table.nextRow("VM Startup Time", formatDateTime(sysData.getRtProcessStartup()));
    table.nextRow("VM Startup Arguments", toShowLinkWithHiddenDiv("vmArgs", sysData.getRtProcessArguments()));
    table.nextRow("VM System Properties", toShowLinkWithHiddenDiv("sProps", sysData.getRtSystemProperties()));
    if (sysData.getMaxFileDescriptorCount() < 0) {
      table.nextRow("VM File Descriptors", "n/a");
    } else {
      table.nextRow("VM File Descriptors",
          "Open: " + formatNumber(sysData.getOpenFileDescriptorCount()) + " / Max: " + formatNumber(sysData.getMaxFileDescriptorCount()));
    }
    if (sysData.getProcessCpuLoadPct() < 0) {
      table.nextRow("VM CPU Load", "n/a");
    } else {
      var processCpuPct = formatPct(sysData.getProcessCpuLoadPct());
      table.nextRow("VM CPU Load", processCpuPct + ", Total CPU Time: " + formatNumber(sysData.getProcessCpuTimeMillis() / 1000) + "s");
    }
    var systemCpuPct = sysData.getSystemCpuLoadPct() < 0 ? "n/a" : formatPct(sysData.getSystemCpuLoadPct());
    table.nextRow("System CPU Load", systemCpuPct);
    table.endTable();
    writeln("<br/>");
  }

  private void writeGcTable() {
    write("<h3>").writeImg("icons/garbage-24.png", "Garbage Collector").write("&nbsp;Garbage Collector</h3>");
    var table = new CandyHtmlTable("Garbage Collector", "Name", "Value");
    table.nextRow("Names", sysData.getGcName());
    table.nextRow("Number of Collections", formatNumber(sysData.getGcCollectionCount()));
    table.nextRow("Total Collection Time", formatDecimal(sysData.getGcCollectionTimeMillis() / 1000d) + "s");
    table.endTable();
    writeln("<br/>");
  }

  private void writeClassloaderTable() {
    write("<h3>").writeImg("icons/box-24.png", "Class Loading").writeln("&nbsp;Class Loading</h3>");
    var table = new CandyHtmlTable("Class Loading", "Name", "Value");
    table.nextRow("Currently Loaded Classes", formatNumber(sysData.getCLLoadedClassCount()));
    table.nextRow("Total Loaded Classes", formatNumber(sysData.getCLTotalLoadedClassCount()));
    table.nextRow("Class Path", toShowLinkWithHiddenDiv("clCp", sysData.getCLClassPath()));
    table.nextRow("Boot Classpath", toShowLinkWithHiddenDiv("clBp", sysData.getCLBootClassPath()));
    table.nextRow("Library Path", toShowLinkWithHiddenDiv("clLp", sysData.getCLLibraryPath()));
    table.endTable();
    writeln("<br/>");
  }

  private static void writeMemoryRow(CandyHtmlTable table, String label, MemoryData memory) {
    String value;
    if (memory == MemoryData.UNKNOWN) {
      value = "n/a";
    } else {
      var used = "Used: " + formatDecimal(memory.getUsed() / 1024d / 1024d) + " Mb";
      var commited = memory.getCommitted() > 0 ? " / Alloc: " + formatDecimal(memory.getCommitted() / 1024d / 1024d) + " Mb" : "";
      var max = memory.getMax() > 0 ? " / Max: " + formatDecimal(memory.getMax() / 1024d / 1024d) + " Mb" : "";
      value = used + commited + max;
    }
    var bar = toMemoryBar(memory);
    table.nextRow(label, value, bar);
  }

  private static String toShowLinkWithHiddenDiv(String divId, String txt) {
    return showHideLink(divId, "Show") + "<br/>" + "<div id='" + divId + "' class='hidden'>" + htmlEncode(txt) + "</div>";
  }

  private static String toMemoryBar(MemoryData memory) {
    // Create the basic pics (start, middle, end):
    var pics = new ArrayList<MemoryBarPic>();
    pics.add(new MemoryBarPic("grey-start", 3));
    pics.add(new MemoryBarPic("grey", BAR_PIXELS));
    pics.add(new MemoryBarPic("grey-end", 3));

    // Note: commited and/or max might be missing!
    var title = "0%";
    if (memory.getUsed() > 0) {
      if (memory.getMax() > 0) {
        // Calc against max:
        if (memory.getCommitted() > 0) {
          var pct = pct(memory.getCommitted(), memory.getMax());
          title = " / " + formatPct(pct);
          overlayBarPics(pics, "blue", pct);
        } else {
          title = "";
        }
        var pct = pct(memory.getUsed(), memory.getMax());
        title = formatPct(pct) + title;
        overlayBarPics(pics, "violet", pct);
      } else if (memory.getCommitted() > 0) {
        // Calc against commited:
        var pct = pct(memory.getUsed(), memory.getCommitted());
        title = formatPct(pct);
        overlayBarPics(pics, "violet", pct);
      }
    }

    // Now render the complete bar:
    var result = new StringBuilder();
    for (var pic : pics) {
      if (pic.width > 0) {
        result.append("<img src='?resource=bar/");
        result.append(pic.pic);
        result.append(".gif' width='");
        result.append(pic.width);
        result.append("' height='10' alt='+' title='");
        result.append(title);
        result.append("' />");
      }
    }
    return result.toString();
  }

  private static double pct(long value, long maxValue) {
    return Math.max(Math.min(100d * value / maxValue, 100d), 0d);
  }

  private static void overlayBarPics(List<MemoryBarPic> pics, String color, double pct) {
    int pixels = (int) Math.round(pct * BAR_SCALE);
    if (pixels > 0) {
      // Replace start:
      pics.set(0, new MemoryBarPic(color + "-start", 3));

      // Add middle pic:
      pics.add(1, new MemoryBarPic(color, pixels));
      pics.get(2).width -= pixels; // follower width must be reduced...

      // Replace end if 100%:
      if (pixels == BAR_PIXELS) {
        pics.set(pics.size() - 1, new MemoryBarPic(color + "-end", 3));
      }
    }
  }
}
