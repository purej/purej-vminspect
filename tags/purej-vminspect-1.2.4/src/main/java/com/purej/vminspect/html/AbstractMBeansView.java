// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import java.util.List;
import javax.management.ObjectName;
import com.purej.vminspect.data.MBeanUtils;

/**
 * Abstract class for the MBeans views.
 *
 * @author Stefan Mueller
 */
abstract class AbstractMBeansView extends AbstractHtmlView {

  /**
   * Creates a new instance of this view.
   */
  public AbstractMBeansView(StringBuilder output) {
    super(output);
  }

  @SuppressWarnings("unchecked")
  protected void writeMBeanValue(Object object, boolean tryShowMBeanLinks) throws IOException {
    if (object instanceof List) {
      List<Object> list = (List<Object>) object;
      for (int i = 0; i < list.size(); i++) {
        write(i == 0 ? "[" : "<br/>[");
        writeMBeanValue(list.get(i), tryShowMBeanLinks);
        write("]");
      }
    }
    else {
      String value = object != null ? object.toString() : null;
      boolean written = false;
      if (value != null && tryShowMBeanLinks && value.indexOf(":type=") > 0) {
        // Try to resolve as MBean object name and display as link:
        try {
          ObjectName objectName = new ObjectName(value);
          int serverIdx = MBeanUtils.getMBeanServerIdx(objectName);
          if (serverIdx > -1) {
            write(mBeanLnk(serverIdx, objectName, htmlEncode(value)));
            written = true;
          }
        }
        catch (Exception e) {
          // Ignore, write as normal value...
        }
      }
      if (!written) {
        // Write the value as given:
        write(htmlEncode(value));
      }
    }
  }

  protected static String mBeanParams(String... additionalParams) {
    StringBuilder builder = new StringBuilder();
    builder.append("page").append("=mbeans");
    for (String param : additionalParams) {
      builder.append(PARAMS_SEPARATOR).append(param);
    }
    return builder.toString();
  }

  protected static String mBeanLnk(int serverIdx, ObjectName objectName, String value) {
    String mbSrvIdx = "mbSrvIdx=" + serverIdx;
    String mbName = "mbName=" + urlEncode(objectName.toString());
    return lnk(mBeanParams(mbSrvIdx, mbName), value);
  }
}
