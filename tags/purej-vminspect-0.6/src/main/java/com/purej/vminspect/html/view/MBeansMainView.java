// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import java.util.List;
import com.purej.vminspect.data.MBeanName;
import com.purej.vminspect.util.Utils;

/**
 * Displays the MBeans main view.
 *
 * @author Stefan Mueller
 */
public class MBeansMainView extends AbstractMBeansView {
  private final String _domainFilter;
  private final String _typeFilter;
  private final List<MBeanName> _mbeans;

  /**
   * Creates a new instance of this view.
   */
  public MBeansMainView(StringBuilder output, String domainFilter, String typeFilter, List<MBeanName> mbeans) {
    super(output);
    _domainFilter = domainFilter != null ? domainFilter : "";
    _typeFilter = typeFilter != null ? typeFilter : "";
    _mbeans = mbeans;
  }

  @Override
  public void render() throws IOException {
    writeln("<h3>" + img("icons/beans-24.png", "MBeans") + "&nbsp;MBeans</h3>");

    // Write the filter row:
    writeln("<form name='mbeansFilter' method='get' action=''>");
    writeln("<br/><b>&nbsp;Domain Filter</b>&nbsp;&nbsp;");
    writeln("<input type='text' size='30' name='mbDomainFilter' value='" + _domainFilter + "'/>");
    writeln("&nbsp;&nbsp;<b>Type Filter</b>&nbsp;&nbsp;");
    writeln("<input type='text' size='30' name='mbTypeFilter' value='" + _typeFilter + "'/>");
    writeln("&nbsp;&nbsp;(Use wild cards = *)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    writeln("<input type='submit' value='Ok'/><br/><br/>");
    writeln("<input type='hidden' name='page' value='mbeans'/>");
    writeln("</form><br/>");

    // Write the table:
    CandyHtmlTable table = new CandyHtmlTable("MBeans", "Domain", "Type", "Properties", "Details");
    int filterMatchingCount = 0;
    for (MBeanName mbean : _mbeans) {
      if (!Utils.wildCardMatch(mbean.getDomain(), _domainFilter)) {
        continue;
      }
      if (!Utils.wildCardMatch(mbean.getType(), _typeFilter)) {
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
    writeln("Filter matched " + filterMatchingCount + "/" + _mbeans.size() + " MBeans<br/>");
  }
}
