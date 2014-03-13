// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.util.Comparator;
import com.purej.vminspect.util.Utils;

/**
 * Comparator to sort {@link MBeanOperation} objects based on name and arguments.
 *
 * @author Stefan Mueller
 */
public final class MBeanOperationComparator implements Comparator<MBeanOperation> {
  @Override
  public int compare(MBeanOperation o1, MBeanOperation o2) {
    int result = Utils.compareTo(o1.getName(), o2.getName());
    if (result == 0) { // Same name
      result = Integer.valueOf(o1.getParameters().length).compareTo(Integer.valueOf(o2.getParameters().length));
      if (result == 0) { // Same # of parameters
        for (int i = 0; i < o1.getParameters().length; i++) {
          result = Utils.compareTo(o1.getParameters()[i].getType(), o2.getParameters()[i].getType());
          if (result != 0) {
            return result;
          }
        }
        throw new IllegalStateException("Methods '" + o1.getName() + "' have same parameters!");
      }
    }
    return result;
  }
}
