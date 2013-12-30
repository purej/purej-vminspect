// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.Comparator;
import com.purej.vminspect.util.Utils;

/**
 * Comparator to sort {@link ThreadData} objects based on name.
 *
 * @author Stefan Mueller
 */
public final class ThreadDataComparator implements Comparator<ThreadData> {
  @Override
  public int compare(ThreadData o1, ThreadData o2) {
    return Utils.compareTo(o1.getName(), o2.getName());
  }
}
