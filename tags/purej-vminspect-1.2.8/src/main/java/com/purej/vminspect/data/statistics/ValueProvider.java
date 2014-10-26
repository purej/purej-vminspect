// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import com.purej.vminspect.data.SystemData;

/**
 * Implementations of this interface are responsible to provide statistics values.
 * This will be called by the {@link StatisticsCollector} on a regular basis.
 *
 * @author Stefan Mueller
 */
public interface ValueProvider {
  /**
   * Returns the current value of this counter.
   * @param data the {@link SystemData} that might be used to calculate the value (not required)
   */
  double getValue(SystemData data);
}
