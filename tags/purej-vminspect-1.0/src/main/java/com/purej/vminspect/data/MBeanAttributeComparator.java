// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.Comparator;
import com.purej.vminspect.util.Utils;

/**
 * Comparator to sort {@link MBeanAttribute} objects based on name.
 *
 * @author Stefan Mueller
 */
public final class MBeanAttributeComparator implements Comparator<MBeanAttribute> {
  @Override
  public int compare(MBeanAttribute o1, MBeanAttribute o2) {
    return Utils.compareTo(o1.getName(), o2.getName());
  }
}
