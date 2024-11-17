// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.util.List;
import com.purej.vminspect.data.MBeanName;
import com.purej.vminspect.util.Utils;

/**
 * Displays the MBeans main view.
 *
 * @author Stefan Mueller
 */
public class MBeansMainView extends AbstractMBeansView {
  private final String domainFilter;
  private final String typeFilter;
  private final List<MBeanName> mbeans;

  /**
   * Creates a new instance of this view.
   */
  public MBeansMainView(StringBuilder output, String domainFilter, String typeFilter, List<MBeanName> mbeans) {
    super(output);
    this.domainFilter = domainFilter != null ? domainFilter : "";
    this.typeFilter = typeFilter != null ? typeFilter : "";
    this.mbeans = mbeans;
  }

  @Override
  public void render() {
    write("<h3>").writeImg("icons/beans-24.png", "MBeans").writeln("&nbsp;MBeans</h3>");

    // Write the filter row:
    writeln("<form name='mbeansFilter' method='get' action=''>");
    writeln("<br/><b>&nbsp;Domain Filter</b>&nbsp;&nbsp;");
    write("<input type='text' size='30' name='mbDomainFilter' value='").write(domainFilter).write("'/>");
    writeln("&nbsp;&nbsp;<b>Type Filter</b>&nbsp;&nbsp;");
    write("<input type='text' size='30' name='mbTypeFilter' value='").write(typeFilter).write("'/>");
    writeln("&nbsp;&nbsp;(Use wild cards = *)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    writeln("<input type='submit' value='Ok'/><br/><br/>");
    writeln("<input type='hidden' name='page' value='mbeans'/>");
    writeln("</form><br/>");

    // Write the table:
    var table = new CandyHtmlTable("MBeans", "Domain", "Type", "Properties");
    int matched = 0;
    for (var mbean : mbeans) {
      if (Utils.wildCardMatch(mbean.getDomain(), domainFilter) && Utils.wildCardMatch(mbean.getType(), typeFilter)) {
        matched++;
        // Note: We want to full row to be clickable, not so easy with HTML without javascript!
        var mbSrvIdx = "mbSrvIdx=" + mbean.getServerIdx();
        var mbName = "mbName=" + urlEncode(mbean.getObjectName().toString());
        var prefix = "<a href='?" + mBeanParams(mbSrvIdx, mbName) + "'>";

        table.nextRowWithClz("clickable-row");
        table.addValue(prefix + mbean.getDomain() + "</a>");
        table.addValue(prefix + htmlEncode(mbean.getType()) + "</a>");
        var props = htmlEncode(mbean.getOtherKeyValues()); // Might be empty
        table.addValue(prefix + (props == null || props.length() == 0 ? "&nbsp;" : props) + "</a>");
      }
    }
    table.endTable();
    write("Filter matched ").write(matched).write("/").write(mbeans.size()).writeln(" MBeans<br/>");
  }
}
