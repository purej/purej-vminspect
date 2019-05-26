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

  protected final String _name;
  protected final int _resolutionSeconds;
  protected final String _rrdPath;

  protected AbstractRrdImpl(String name, String storageDir, int resolutionSeconds) throws IOException {
    _name = Utils.checkNotNull(name);
    _resolutionSeconds = resolutionSeconds;
    _rrdPath = storageDir != null ? new File(storageDir, name + ".rrd").getCanonicalPath() : name + ".rrd";
  }

  protected void renameRrd(File file) {
    String to = file.getName().replace(".rrd", "") + "-old-" + System.currentTimeMillis() + ".rrd";
    file.renameTo(new File(file.getParentFile(), to));
  }

  protected String getGraphTitle(String label, Range range, int width) {
    String titleStart = label + " - " + range.getPeriod().getLabel();
    String titleEnd = "";
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
