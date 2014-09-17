// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;

/**
 * Displays the view to edit an attribute of an MBean.
 *
 * @author Stefan Mueller
 */
public class MBeansInvokeAttributeView extends AbstractMBeansView {
  private final MBeanData _mbean;
  private final MBeanAttribute _attribute;
  private final ConfirmState _confirm;
  private final String _newValue;

  /**
   * Creates a new instance of this view.
   */
  public MBeansInvokeAttributeView(StringBuilder output, MBeanData mbean, MBeanAttribute attribute, ConfirmState confirmState, String newValue) {
    super(output);
    _mbean = mbean;
    _attribute = attribute;
    _confirm = confirmState;
    _newValue = newValue;
  }

  @Override
  public void render() throws IOException {
    // Write the mbean title:
    writeln("<h3>" + img("icons/beans-24.png", "MBean") + "&nbsp;MBean: <i>" + htmlEncode(_mbean.getName().getObjectNameString()) + "</i></h3>");
    if (_confirm.isNow()) {
      writeln("<div id='warnMsg' style='font-size:12pt;font-weight:bold;padding:10px;'>Please confirm the MBeans attribute change!</div><br/>");
    }

    // Write the form with a table:
    writeln("<form name='mbeanAttribute' method='post' action=''><br/>");
    HtmlTable table = new HtmlTable("MBean Attribute");
    table.nextRow();
    table.addValue(img("icons/books-16.png", "MBean Attribute") + "<b>&nbsp;Attribute</b>");
    table.addValue("<b>" + htmlEncode(_attribute.getName()) + "</b>");
    table.nextRow("Description", htmlEncode(_attribute.getDescription()));
    table.nextRow("Type", htmlEncode(_attribute.getType()));
    table.nextRow("Current Value&nbsp;&nbsp;&nbsp;&nbsp;");
    write("<td>");
    writeMBeanValue(_attribute.getValue(), false);
    write("</td>");
    table.nextRow("<br/>", "");
    if (_confirm.isNow()) {
      table.nextRow("New Value", _newValue + "<input type='hidden' name='mbAtrValue' value='" + htmlEncode(_newValue) + "'/>");
    }
    else {
      table.nextRow("New Value", "<input type='text' size='50' name='mbAtrValue' value=''/>");
    }
    table.nextRow("<br/>", "");
    table.nextRow("");
    String okAction = _confirm.isNext() ? "mbAtrInvokeConfirm" : "mbAtrInvoke";
    table.addValueRight("<input type='submit' name='mbAtrCancel' value='Cancel'/><input type='submit' name='" + okAction + "' value='Ok'/>");
    table.endTable();
    writeln("<input type='hidden' name='page' value='mbeans'/>");
    writeln("<input type='hidden' name='mbSrvIdx' value='" + _mbean.getName().getServerIdx() + "'/>");
    writeln("<input type='hidden' name='mbName' value='" + _mbean.getName().getObjectNameString() + "'/>");
    writeln("<input type='hidden' name='mbAtrName' value='" + _attribute.getName() + "'/>");
    writeln("<br/></form>");
  }
}
