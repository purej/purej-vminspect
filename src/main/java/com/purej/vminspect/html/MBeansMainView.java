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
    CandyHtmlTable table = new CandyHtmlTable("MBeans", "Domain", "Type", "Properties", "Details");
    int filterMatchingCount = 0;
    for (MBeanName mbean : mbeans) {
      if (!Utils.wildCardMatch(mbean.getDomain(), domainFilter)) {
        continue;
      }
      if (!Utils.wildCardMatch(mbean.getType(), typeFilter)) {
        continue;
      }
      filterMatchingCount++;

      table.nextRow();
      table.addValue(mbean.getDomain());
      table.addValue(htmlEncode(mbean.getType()));
      table.addValue(htmlEncode(mbean.getOtherKeyValues()));
      table.addValueCenter(mBeanLnk(mbean.getServerIdx(), mbean.getObjectName(), img("icons/bean-view-16.png", "Details")));
    }
    table.endTable();
    write("Filter matched ").write(filterMatchingCount).write("/").write(mbeans.size()).writeln(" MBeans<br/>");
  }
}
