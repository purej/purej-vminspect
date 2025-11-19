// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

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
  public void render() {
    // Write the mbean title:
    write("<h3>").writeImg("icons/beans-24.png", "MBean").write("&nbsp;MBean: <i>").write(htmlEncode(mbean.getName().getObjectNameString())).writeln("</i></h3>");
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
      table.nextRow("<b>Parameter</b>", "<b>" + htmlEncode(parameter.getName()) + "</b>");
      table.nextRow("Description", htmlEncode(parameter.getDescription()));
      table.nextRow("Type", htmlEncode(MBeanUtils.toDisplayType(parameter.getType())));
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
    table.addValueRight("<input type='submit' name='" + okAction + "' value='Ok'/><input type='submit' name='mbOpCancel' value='Cancel'/>");
    table.endTable();
    writeln("<input type='hidden' name='page' value='mbeans'/>");
    write("<input type='hidden' name='mbSrvIdx' value='").write(mbean.getName().getServerIdx()).writeln("'/>");
    write("<input type='hidden' name='mbName' value='").write(htmlEncode(mbean.getName().getObjectNameString())).writeln("'/>");
    write("<input type='hidden' name='mbOpIdx' value='").write(mbOpIdx).writeln("'/>");
    writeln("<br/></form>");
  }
}
