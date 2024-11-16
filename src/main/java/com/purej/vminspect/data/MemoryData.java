// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.MemoryUsage;

/**
 * Stores memory data for a single type of memory.
 *
 * @author Stefan Mueller
 */
public final class MemoryData {
  /**
   * An instance with -1 as values.
   */
  public static final MemoryData UNKNOWN = new MemoryData(-1, -1, -1);

  private final long used;
  private final long committed;
  private final long max;

  /**
   * Creates a new instance of this class.
   */
  public MemoryData(MemoryUsage usage) {
    this(usage.getUsed(), usage.getCommitted(), usage.getMax());
  }

  /**
   * Creates a new instance of this class.
   */
  public MemoryData(long used, long commited, long max) {
    super();
    this.used = used;
    this.committed = commited;
    this.max = max;
  }

  /**
   * Returns the used memory in bytes.
   */
  public long getUsed() {
    return used;
  }

  /**
   * Returns the commited memory in bytes.
   */
  public long getCommitted() {
    return committed;
  }

  /**
   * Returns the max memory in bytes. The max memory might not be guaranteed.
   */
  public long getMax() {
    return max;
  }
}
