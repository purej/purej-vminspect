// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides information about a single thread.
 *
 * @author Stefan Mueller
 */
public final class ThreadData {
  private final String name;
  private final int priority;
  private final boolean daemon;
  private final Thread.State state;
  private final long cpuTimeMillis;
  private final long userTimeMillis;
  private final boolean deadlocked;
  private final StackTraceElement[] stackTrace;

  /**
   * Creates a new instance of this class.
   */
  public ThreadData(Thread thread, StackTraceElement[] stackTrace, long cpuTimeMillis, long userTimeMillis, boolean deadlocked) {
    this.name = thread.getName();
    this.priority = thread.getPriority();
    this.daemon = thread.isDaemon();
    this.state = thread.getState();
    this.stackTrace = stackTrace;
    this.cpuTimeMillis = cpuTimeMillis;
    this.userTimeMillis = userTimeMillis;
    this.deadlocked = deadlocked;
  }

  /**
   * Returns the list of all currently existing threads.
   */
  public static List<ThreadData> getAllThreads() {
    var threads = createThreadInfos(ManagementFactory.getThreadMXBean());
    Collections.sort(threads, new ThreadDataComparator());
    return threads;
  }

  private static List<ThreadData> createThreadInfos(ThreadMXBean threadBean) {
    var stackTraces = Thread.getAllStackTraces();
    var result = new ArrayList<ThreadData>(stackTraces.size());
    var deadlockedThreadIds = getDeadlockedThreadsIds(threadBean);
    var cpuTimeEnabled = threadBean.isThreadCpuTimeSupported() && threadBean.isThreadCpuTimeEnabled();
    for (var entry : stackTraces.entrySet()) {
      var t = entry.getKey();
      var cpuTimeMillis = cpuTimeEnabled ? threadBean.getThreadCpuTime(t.getId()) / 1000000 : -1;
      var userTimeMillis = cpuTimeEnabled ? threadBean.getThreadUserTime(t.getId()) / 1000000 : -1;
      var isDeadlocked = deadlockedThreadIds.contains(Long.valueOf(t.getId()));
      result.add(new ThreadData(t, entry.getValue(), cpuTimeMillis, userTimeMillis, isDeadlocked));
    }
    return result;
  }

  private static Set<Long> getDeadlockedThreadsIds(ThreadMXBean threadBean) {
    long[] deadlockedThreads;
    if (threadBean.isSynchronizerUsageSupported()) {
      deadlockedThreads = threadBean.findDeadlockedThreads();
    }
    else {
      deadlockedThreads = threadBean.findMonitorDeadlockedThreads();
    }
    var result = new HashSet<Long>();
    if (deadlockedThreads != null) {
      for (var t : deadlockedThreads) {
        result.add(Long.valueOf(t));
      }
    }
    return result;
  }

  /**
   * Returns the name of the thread.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the priority of the thread.
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Returns if the thread runs as deamon thread.
   */
  public boolean isDaemon() {
    return daemon;
  }

  /**
   * Returns the current state of the thread.
   */
  public Thread.State getState() {
    return state;
  }

  /**
   * Returns the list of stack trace elements.
   */
  public StackTraceElement[] getStackTrace() {
    return stackTrace;
  }

  /**
   * Returns the stack trace as a string.
   */
  public String getStackTraceString() {
    var result = new StringBuilder();
    for (var element : stackTrace) {
      if (result.length() > 0) {
        result.append('\n');
      }
      result.append(element.toString());
    }
    return result.toString();
  }

  /**
   * Returns the total CPU time this thread consumed so far.
   */
  public long getCpuTimeMillis() {
    return cpuTimeMillis;
  }

  /**
   * Returns the total user time this thread consumed so far.
   */
  public long getUserTimeMillis() {
    return userTimeMillis;
  }

  /**
   * Returns true if this thread is in a deadlock state.
   */
  public boolean isDeadlocked() {
    return deadlocked;
  }
}
