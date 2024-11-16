// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;
import com.purej.vminspect.data.MBeanUtils;

/**
 * Displays the view to invoke an operation of an MBean.
 *
 * @author Stefan Mueller
 */
public class MBeansInvokeOperationView extends AbstractMBeansView {
  private final MBeanData mbean;
  private final int mbOpIdx;
  private final MBeanOperation operation;
  private final ConfirmState confirm;
  private final String[] parameters;

  /**
   * Creates a new instance of this view.
   */
  public MBeansInvokeOperationView(StringBuilder output, MBeanData mbean, int mbOpIdx, MBeanOperation operation, ConfirmState confirmState,
      String[] parameters) {
    super(output);
    this.mbean = mbean;
    this.mbOpIdx = mbOpIdx;
    this.operation = operation;
    this.confirm = confirmState;
    this.parameters = parameters;
  }

  @Override
  public void render() throws IOException {
    // Write the mbean title:
    writeln("<h3>" + img("icons/beans-24.png", "MBean") + "&nbsp;MBean: <i>" + htmlEncode(mbean.getName().getObjectNameString()) + "</i></h3>");
    if (confirm.isNow()) {
      writeln("<div id='warnMsg' style='font-size:12pt;font-weight:bold;padding:10px;'>Please confirm the MBeans operation invocation!</div><br/>");
    }

    // Write the form with a table:
    writeln("<form name='mbeanOperation' method='post' action=''><br/>");
    var table = new HtmlTable("MBean Operation");
    table.nextRow();
    table.addValue(img("icons/flash-16.png", "MBean Operation") + "<b>&nbsp;Operation&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>");
    table.addValue("<b>" + htmlEncode(operation.getName()) + "</b>");
    table.nextRow("Description", htmlEncode(operation.getDescription()));
    table.nextRow("Impact", htmlEncode(operation.getImpact().name()));
    table.nextRow("Return Type", htmlEncode(MBeanUtils.toDisplayType(operation.getReturnType())));
    for (var i = 0; i < operation.getParameters().length; i++) {
      table.nextRow("<br/>", "");
      var parameter = operation.getParameters()[i];
      table.nextRow("<b>Parameter</b>", "<b>" + parameter.getName() + "</b>");
      table.nextRow("Description", parameter.getDescription());
      table.nextRow("Type", MBeanUtils.toDisplayType(parameter.getType()));
      if (confirm.isNow()) {
        table.nextRow("Value", parameters[i] + "<input type='hidden' name='mbOpValue" + i + "' value='" + htmlEncode(parameters[i]) + "'/>");
      }
      else {
        table.nextRow("Value", "<input type='text' size='50' name='mbOpValue" + i + "' value=''/>");
      }
    }
    table.nextRow("<br/>", "");
    table.nextRow("");
    var okAction = confirm.isNext() ? "mbOpInvokeConfirm" : "mbOpInvoke";
    table.addValueRight("<input type='submit' name='mbOpCancel' value='Cancel'/><input type='submit' name='" + okAction + "' value='Ok'/>");
    table.endTable();
    writeln("<input type='hidden' name='page' value='mbeans'/>");
    writeln("<input type='hidden' name='mbSrvIdx' value='" + mbean.getName().getServerIdx() + "'/>");
    writeln("<input type='hidden' name='mbName' value='" + mbean.getName().getObjectNameString() + "'/>");
    writeln("<input type='hidden' name='mbOpIdx' value='" + mbOpIdx + "'/>");
    writeln("<br/></form>");
  }
}
