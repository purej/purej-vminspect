// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.Comparator;
import com.purej.vminspect.util.Utils;

/**
 * Comparator to sort {@link MBeanName} objects based on name.
 *
 * @author Stefan Mueller
 */
public final class MBeanNameComparator implements Comparator<MBeanName> {
  @Override
  public int compare(MBeanName o1, MBeanName o2) {
    int result = Utils.compareTo(o1.getDomain(), o2.getDomain());
    return result == 0 ? Utils.compareTo(o1.getType(), o2.getType()) : result;
  }
}
