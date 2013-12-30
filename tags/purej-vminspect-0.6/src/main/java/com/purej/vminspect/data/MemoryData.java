// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.MemoryUsage;

/**
 * Stores memory data for a single type of memory.
 *
 * @author Stefan Mueller
 */
public final class MemoryData {
  private final long _used;
  private final long _committed;
  private final long _max;

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
    this._used = used;
    this._committed = commited;
    this._max = max;
  }

  /**
   * Returns the used memory in bytes.
   */
  public long getUsed() {
    return _used;
  }

  /**
   * Returns the commited memory in bytes.
   */
  public long getCommitted() {
    return _committed;
  }

  /**
   * Returns the max memory in bytes. The max memory might not be guaranteed.
   */
  public long getMax() {
    return _max;
  }
}
