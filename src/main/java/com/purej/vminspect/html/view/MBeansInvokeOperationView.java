// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;
import com.purej.vminspect.data.MBeanParameter;

/**
 * Displays the view to invoke an operation of an MBean.
 *
 * @author Stefan Mueller
 */
public class MBeansInvokeOperationView extends AbstractBeansView {
  private final MBeanData _mbean;
  private final int _mbOpIdx;
  private final MBeanOperation _operation;

  /**
   * Creates a new instance of this view.
   */
  public MBeansInvokeOperationView(StringBuilder output, MBeanData mbean, int mbOpIdx, MBeanOperation operation) {
    super(output);
    _mbean = mbean;
    _mbOpIdx = mbOpIdx;
    _operation = operation;
  }

  @Override
  public void render() throws IOException {
    // Write the mbean title:
    writeln("<h3>" + img("icons/beans-24.png", "MBean") + "&nbsp;MBean: <i>" + htmlEncode(_mbean.getName().getObjectNameString()) + "</i></h3>");

    // Write the form with a table:
    writeln("<form name='mbeanOperation' method='get' action=''><br/>");
    HtmlTable table = new HtmlTable();
    table.beginTable("MBean Operation");
    table.nextRow();
    table.addValue(img("icons/flash-16.png", "MBean Operation") + "<b>&nbsp;Operation&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>");
    table.addValue("<b>" + htmlEncode(_operation.getName()) + "</b>");
    table.nextRowWithValues("Description", htmlEncode(_operation.getDescription()));
    table.nextRowWithValues("Impact", htmlEncode(_operation.getImpact()));
    table.nextRowWithValues("Return Type", htmlEncode(_operation.getReturnType()));
    for (int i = 0; i < _operation.getParameters().length; i++) {
      table.nextRowWithValues("<br/>", "");
      MBeanParameter parameter = _operation.getParameters()[i];
      table.nextRowWithValues("<b>Parameter</b>", "<b>" + parameter.getName() + "</b>");
      table.nextRowWithValues("Description", parameter.getDescription());
      table.nextRowWithValues("Type", parameter.getType());
      table.nextRowWithValues("Value", "<input type='text' size='50' name='mbOpValue" + i + "' value=''/>");
    }
    table.nextRowWithValues("<br/>", "");
    table.nextRowWithValues("");
    table.addValueRight("<input type='submit' name='mbOpCancel' value='Cancel'/><input type='submit' name='mbOpInvoke' value='Ok'/>");
    table.endTable();
    writeln("<input type='hidden' name='page' value='mbeans'/>");
    writeln("<input type='hidden' name='mbSrvIdx' value='" + _mbean.getName().getServerIdx() + "'/>");
    writeln("<input type='hidden' name='mbName' value='" + _mbean.getName().getObjectNameString() + "'/>");
    writeln("<input type='hidden' name='mbOpIdx' value='" + _mbOpIdx + "'/>");
    writeln("<br/></form>");
  }
}
