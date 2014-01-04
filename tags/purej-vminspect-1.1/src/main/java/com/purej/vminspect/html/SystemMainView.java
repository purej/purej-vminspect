// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
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
    private final String _pic;
    private int _width;

    public MemoryBarPic(String pic, int width) {
      _pic = pic;
      _width = width;
    }
  }

  private final SystemData _sysData;

  /**
   * Creates a new instance of this view.
   */
  public SystemMainView(StringBuilder output, SystemData sysData) {
    super(output);
    _sysData = sysData;
  }

  @Override
  public void render() throws IOException {
    writeMemoryTable();
    writeRuntimeTable();
    writeGcTable();
    writeClassloaderTable();
  }

  private void writeMemoryTable() throws IOException {
    writeln("<h3>" + img("icons/battery-24.png", "Memory") + "&nbsp;Memory</h3>");
    CandyHtmlTable table = new CandyHtmlTable("Memory", "Name", "Value", "Status");
    writeMemoryRow(table, "Heap Memory", _sysData.getMemoryHeap());
    writeMemoryRow(table, "NonHeap Memory", _sysData.getMemoryNonHeap());
    writeMemoryRow(table, "Physical Memory", _sysData.getMemoryPhysical());
    writeMemoryRow(table, "Swap Memory", _sysData.getMemorySwap());
    table.endTable();
    writeln("<br/>");
  }

  private void writeRuntimeTable() throws IOException {
    writeln("<h3>" + img("icons/cup-24.png", "Runtime") + "&nbsp;Runtime</h3>");
    CandyHtmlTable table = new CandyHtmlTable("Runtime", "Name", "Value");
    table.nextRow("OS", _sysData.getOsName() + ", Architecture: " + _sysData.getOsArchitecture() + ", Version: " + _sysData.getOsVersion()
        + ", Processors: " + _sysData.getOsAvailableProcessors());
    table.nextRow("Java", _sysData.getRtInfo());
    table.nextRow("Virtual Machine", _sysData.getVmName() + ", Vendor: " + _sysData.getVmVendor() + ", Version: " + _sysData.getVmVersion());
    table.nextRow("VM Process Name", _sysData.getRtProcessName());
    table.nextRow("VM Startup Time", formatDateTime(_sysData.getRtProcessStartup()));
    table.nextRow("VM Startup Arguments", toShowLinkWithHiddenDiv("vmArgs", _sysData.getRtProcessArguments()));
    table.nextRow("VM System Properties", toShowLinkWithHiddenDiv("sProps", _sysData.getRtSystemProperties()));
    String processCpuPct = _sysData.getProcessCpuLoadPct() < 0 ? "n/a" : formatPct(_sysData.getProcessCpuLoadPct());
    String systemCpuPct = _sysData.getSystemCpuLoadPct() < 0 ? "n/a" : formatPct(_sysData.getSystemCpuLoadPct());
    table.nextRow("VM CPU Load", processCpuPct + ", Total CPU Time: " + formatNumber(_sysData.getProcessCpuTimeMillis()) + "ms");
    table.nextRow("System CPU Load", systemCpuPct);
    table.endTable();
    writeln("<br/>");
  }

  private void writeGcTable() throws IOException {
    writeln("<h3>" + img("icons/garbage-24.png", "Garbage Collector") + "&nbsp;Garbage Collector</h3>");
    CandyHtmlTable table = new CandyHtmlTable("Garbage Collector", "Name", "Value");
    table.nextRow("Number of Collections", formatNumber(_sysData.getGcCollectionCount()));
    table.nextRow("Total Collection Time", formatNumber(_sysData.getGcCollectionTimeMillis()) + "ms");
    table.endTable();
    writeln("<br/>");
  }

  private void writeClassloaderTable() throws IOException {
    writeln("<h3>" + img("icons/box-24.png", "Class Loading") + "&nbsp;Class Loading</h3>");
    CandyHtmlTable table = new CandyHtmlTable("Class Loading", "Name", "Value");
    table.nextRow("Currently Loaded Classes", formatNumber(_sysData.getCLLoadedClassCount()));
    table.nextRow("Total Loaded Classes", formatNumber(_sysData.getCLTotalLoadedClassCount()));
    table.nextRow("Class Path", toShowLinkWithHiddenDiv("clCp", _sysData.getCLClassPath()));
    table.nextRow("Boot Classpath", toShowLinkWithHiddenDiv("clBp", _sysData.getCLBootClassPath()));
    table.nextRow("Library Path", toShowLinkWithHiddenDiv("clLp", _sysData.getCLLibraryPath()));
    table.endTable();
    writeln("<br/>");
  }

  private static void writeMemoryRow(CandyHtmlTable table, String label, MemoryData memory) throws IOException {
    String bar = toMemoryBar(memory);
    String used = "Used: " + formatMb(memory.getUsed() / 1024d / 1024d);
    String commited = memory.getCommitted() > 0 ? " / Alloc: " + formatMb(memory.getCommitted() / 1024d / 1024d) : "";
    String max = " / Max: " + formatMb(memory.getMax() / 1024d / 1024d);
    table.nextRow(label, used + commited + max, bar);
  }

  private static String toShowLinkWithHiddenDiv(String divId, String txt) {
    return showHideLink(divId, "Show") + "<br/>" + "<div id='" + divId + "' style='display: none;'>" + htmlEncode(txt) + "</div>";
  }

  private static String toMemoryBar(MemoryData memory) {
    // Create the basic pics (start, middle, end):
    List<MemoryBarPic> pics = new ArrayList<MemoryBarPic>();
    pics.add(new MemoryBarPic("grey-start", 3));
    pics.add(new MemoryBarPic("grey", BAR_PIXELS));
    pics.add(new MemoryBarPic("grey-end", 3));

    // Add commited (optional):
    String title = "";
    if (memory.getCommitted() > 0) {
      double pct = pct(memory.getCommitted(), memory.getMax());
      title = " / " + formatPct(pct);
      overlayBarPics(pics, "blue", pct);
    }

    // Add used:
    double pct = pct(memory.getUsed(), memory.getMax());
    title = formatPct(pct) + title;
    overlayBarPics(pics, "violet", pct);

    // Now render the complete bar:
    StringBuilder result = new StringBuilder();
    for (MemoryBarPic pic : pics) {
      if (pic._width > 0) {
        result.append("<img src='?resource=bar/");
        result.append(pic._pic);
        result.append(".gif' width='");
        result.append(pic._width);
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
      pics.get(2)._width -= pixels; // follower width must be reduced...

      // Replace end if 100%:
      if (pixels == BAR_PIXELS) {
        pics.set(pics.size() - 1, new MemoryBarPic(color + "-end", 3));
      }
    }
  }
}
