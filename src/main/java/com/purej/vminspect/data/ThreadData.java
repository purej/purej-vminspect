// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.purej.vminspect.util.Utils;

/**
 * Provides information about a single thread.
 *
 * @author Stefan Mueller
 */
public final class ThreadData implements Serializable {

  /**
   * Comparator to sort based on name.
   */
  public static final Comparator<ThreadData> COMPARATOR = new Comparator<ThreadData>() {
    @Override
    public int compare(ThreadData o1, ThreadData o2) {
      return Utils.compareTo(o1.getName(), o2.getName());
    }
  };

  private static final long serialVersionUID = -1;
  private final String _name;
  private final int _priority;
  private final boolean _daemon;
  private final Thread.State _state;
  private final long _cpuTimeMillis;
  private final long _userTimeMillis;
  private final boolean _deadlocked;
  private final StackTraceElement[] _stackTrace;

  /**
   * Creates a new instance of this class.
   */
  public ThreadData(Thread thread, StackTraceElement[] stackTrace, long cpuTimeMillis, long userTimeMillis, boolean deadlocked) {
    _name = thread.getName();
    _priority = thread.getPriority();
    _daemon = thread.isDaemon();
    _state = thread.getState();
    _stackTrace = stackTrace;
    _cpuTimeMillis = cpuTimeMillis;
    _userTimeMillis = userTimeMillis;
    _deadlocked = deadlocked;
  }

  /**
   * Returns the list of all currently existing threads.
   */
  public static List<ThreadData> getAllThreads() {
    List<ThreadData> threads = createThreadInfos(ManagementFactory.getThreadMXBean());
    Collections.sort(threads, ThreadData.COMPARATOR);
    return threads;
  }

  private static List<ThreadData> createThreadInfos(ThreadMXBean threadBean) {
    Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
    List<ThreadData> result = new ArrayList<ThreadData>(stackTraces.size());
    Set<Long> deadlockedThreadIds = getDeadlockedThreadsIds(threadBean);
    boolean cpuTimeEnabled = threadBean.isThreadCpuTimeSupported() && threadBean.isThreadCpuTimeEnabled();
    for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
      Thread t = entry.getKey();
      long cpuTimeMillis = cpuTimeEnabled ? threadBean.getThreadCpuTime(t.getId()) / 1000000 : -1;
      long userTimeMillis = cpuTimeEnabled ? threadBean.getThreadUserTime(t.getId()) / 1000000 : -1;
      boolean isDeadlocked = deadlockedThreadIds.contains(Long.valueOf(t.getId()));
      result.add(new ThreadData(t, entry.getValue(), cpuTimeMillis, userTimeMillis, isDeadlocked));
    }
    return result;
  }

  private static Set<Long> getDeadlockedThreadsIds(ThreadMXBean threadBean) {
    final long[] deadlockedThreads;
    if (threadBean.isSynchronizerUsageSupported()) {
      deadlockedThreads = threadBean.findDeadlockedThreads();
    }
    else {
      deadlockedThreads = threadBean.findMonitorDeadlockedThreads();
    }
    Set<Long> result = new HashSet<Long>();
    if (deadlockedThreads != null) {
      for (int i = 0; i < deadlockedThreads.length; i++) {
        result.add(Long.valueOf(deadlockedThreads[i]));
      }
    }
    return result;
  }

  /**
   * Returns the name of the thread.
   */
  public String getName() {
    return _name;
  }

  /**
   * Returns the priority of the thread.
   */
  public int getPriority() {
    return _priority;
  }

  /**
   * Returns if the thread runs as deamon thread.
   */
  public boolean isDaemon() {
    return _daemon;
  }

  /**
   * Returns the current state of the thread.
   */
  public Thread.State getState() {
    return _state;
  }

  /**
   * Returns the list of stack trace elements.
   */
  public StackTraceElement[] getStackTrace() {
    return _stackTrace;
  }

  /**
   * Returns the stack trace as a string.
   */
  public String getStackTraceString() {
    StringBuilder result = new StringBuilder();
    for (StackTraceElement element : _stackTrace) {
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
    return _cpuTimeMillis;
  }

  /**
   * Returns the total user time this thread consumed so far.
   */
  public long getUserTimeMillis() {
    return _userTimeMillis;
  }

  /**
   * Returns true if this thread is in a deadlock state.
   */
  public boolean isDeadlocked() {
    return _deadlocked;
  }
}
