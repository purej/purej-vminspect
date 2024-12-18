// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.lang.management.ManagementFactory;
import java.util.List;
import com.purej.vminspect.data.ThreadData;

/**
 * Displays a list of threads as a HTML table.
 *
 * @author Stefan Mueller
 */
public final class ThreadsMainView extends AbstractHtmlView {
  private final List<ThreadData> threads;

  /**
   * Creates a new instance of this view.
   */
  public ThreadsMainView(StringBuilder output, List<ThreadData> threads) {
    super(output);
    this.threads = threads;
  }

  @Override
  public void render() {
    write("<h3>").writeImg("icons/threads-24.png", "Threads").writeln("&nbsp;Threads</h3>");

    var mxBean = ManagementFactory.getThreadMXBean();
    write("<div align='left'>");
    write("Total live threads: ").write(threads.size()).write(" (peek: ").write(mxBean.getPeakThreadCount()).write(")<br/>");
    write("Total started threads: ").write(mxBean.getTotalStartedThreadCount());
    writeln("</div><br/>");

    var table = new CandyHtmlTable("Threads", "Thread", "Demon", "Priority", "State", "Executing Method", "CPU Time Ms", "User Time Ms");
    for (var thread : threads) {
      table.nextRowWithClz(thread.isDeadlocked() ? "deadlock" : "");
      table.addValue(htmlEncode(thread.getName()));
      table.addValueCenter(thread.isDaemon() ? "Yes" : "No");
      table.addValueRight(formatNumber(thread.getPriority()));
      table.addValueCenter(img("bullets/" + getStateIcon(thread), String.valueOf(thread.getState())));
      table.addValue(getExecutingMethodWithStacktrace(thread));
      table.addValueRight(formatNumber(thread.getCpuTimeMillis()));
      table.addValueRight(formatNumber(thread.getUserTimeMillis()));
    }
    table.endTable();
    writeln("<div align='right'>");
    var threadsDumpParam = "page=threadsDump";
    write("<br/>").writeImgLnk(threadsDumpParam, "icons/text-16.png", "Dump threads as text", "&nbsp;Dump threads as text").writeln();
    writeln("</div>");
  }

  private static String getExecutingMethodWithStacktrace(ThreadData thread) {
    if (thread.getStackTrace() != null && thread.getStackTrace().length > 0) {
      return tooltip(thread.getStackTrace()[0].toString(), thread.getStackTraceString());
    }
    return "";
  }

  private static String getStateIcon(ThreadData threadData) {
    switch (threadData.getState()) {
    case RUNNABLE:
      return "green.png";
    case WAITING:
      return "yellow.png";
    case TIMED_WAITING:
      return "yellow.png";
    case BLOCKED:
      return "red.png";
    default:
      return "gray.png";
    }
  }
}
