// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import com.purej.vminspect.data.ThreadData;

/**
 * Displays dumps of all threads.
 *
 * @author Stefan Mueller
 */
public final class ThreadsDumpView extends AbstractHtmlView {
  private final List<ThreadData> _threads;

  /**
   * Creates a new instance of this view.
   */
  public ThreadsDumpView(StringBuilder output, List<ThreadData> threads) {
    super(output);
    _threads = threads;
  }

  @Override
  public void render() throws IOException {
    write(formatDateTime(new Date()));
    write("\n\n");
    write("===== ALL THREADS DUMP =====");
    write("\n\n");
    for (ThreadData thread : _threads) {
      write("\"");
      write(thread.getName());
      write("\"");
      if (thread.isDaemon()) {
        write(" daemon");
      }
      write(" prio=");
      write(String.valueOf(thread.getPriority()));
      write(" ");
      write(String.valueOf(thread.getState()));
      final StackTraceElement[] stackTrace = thread.getStackTrace();
      if (stackTrace != null && stackTrace.length > 0) {
        for (final StackTraceElement element : stackTrace) {
          write("\n\t");
          write(element.toString());
        }
      }
      write("\n\n");
    }
    write("\n");
  }
}
