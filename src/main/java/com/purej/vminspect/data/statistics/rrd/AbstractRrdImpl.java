package com.purej.vminspect.data.statistics.rrd;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import com.purej.vminspect.data.statistics.Period;
import com.purej.vminspect.data.statistics.Range;
import com.purej.vminspect.util.Utils;

/**
 * Common abstract class for {@link Rrd} implementations.
 *
 * @author Stefan Mueller
 */
public abstract class AbstractRrdImpl implements Rrd {
  protected static final int HOUR = 60 * 60;
  protected static final int DAY = 24 * HOUR;

  protected final String name;
  protected final int resolutionSeconds;
  protected final String rrdPath;

  protected AbstractRrdImpl(String name, String storageDir, int resolutionSeconds) throws IOException {
    this.name = Utils.checkNotNull(name);
    this.resolutionSeconds = resolutionSeconds;
    this.rrdPath = storageDir != null ? new File(storageDir, name + ".rrd").getCanonicalPath() : name + ".rrd";
  }

  protected void renameRrd(File file) {
    var to = file.getName().replace(".rrd", "") + "-old-" + System.currentTimeMillis() + ".rrd";
    file.renameTo(new File(file.getParentFile(), to));
  }

  protected String getGraphTitle(String label, Range range, int width) {
    var titleStart = label + " - " + range.getPeriod().getLabel();
    var titleEnd = "";
    if (width > 400) {
      if (range.getPeriod().equals(Period.CUSTOM)) {
        titleEnd = " - " + Utils.formatDate(range.getStartDate()) + " - " + Utils.formatDate(range.getEndDate());
      } else {
        titleEnd = " - " + Utils.formatDate(new Date());
      }
    }
    return titleStart + titleEnd;
  }
}
