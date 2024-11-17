// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanUtils;

/**
 * Displays the view to edit an attribute of an MBean.
 *
 * @author Stefan Mueller
 */
public class MBeansInvokeAttributeView extends AbstractMBeansView {
  private final MBeanData mbean;
  private final MBeanAttribute attribute;
  private final ConfirmState confirm;
  private final String newValue;

  /**
   * Creates a new instance of this view.
   */
  public MBeansInvokeAttributeView(StringBuilder output, MBeanData mbean, MBeanAttribute attribute, ConfirmState confirmState, String newValue) {
    super(output);
    this.mbean = mbean;
    this.attribute = attribute;
    this.confirm = confirmState;
    this.newValue = newValue;
  }

  @Override
  public void render() {
    // Write the mbean title:
    write("<h3>").writeImg("icons/beans-24.png", "MBean").write("&nbsp;MBean: <i>").write(htmlEncode(mbean.getName().getObjectNameString())).writeln("</i></h3>");
    if (confirm.isNow()) {
      writeln("<div id='warnMsg' style='font-size:12pt;font-weight:bold;padding:10px;'>Please confirm the MBeans attribute change!</div><br/>");
    }

    // Write the form with a table:
    writeln("<form name='mbeanAttribute' method='post' action=''><br/>");
    var table = new HtmlTable("MBean Attribute");
    table.nextRow();
    table.addValue(img("icons/books-16.png", "MBean Attribute") + "<b>&nbsp;Attribute</b>");
    table.addValue("<b>" + htmlEncode(attribute.getName()) + "</b>");
    table.nextRow("Description", htmlEncode(attribute.getDescription()));
    table.nextRow("Type", htmlEncode(MBeanUtils.toDisplayType(attribute.getType())));
    table.nextRow("Current Value&nbsp;&nbsp;&nbsp;&nbsp;");
    write("<td>");
    writeMBeanValue(attribute.getValue(), false);
    write("</td>");
    table.nextRow("<br/>", "");
    if (confirm.isNow()) {
      table.nextRow("New Value", newValue + "<input type='hidden' name='mbAtrValue' value='" + htmlEncode(newValue) + "'/>");
    }
    else {
      table.nextRow("New Value", "<input type='text' size='50' name='mbAtrValue' value=''/>");
    }
    table.nextRow("<br/>", "");
    table.nextRow("");
    var okAction = confirm.isNext() ? "mbAtrInvokeConfirm" : "mbAtrInvoke";
    table.addValueRight("<input type='submit' name='" + okAction + "' value='Ok'/><input type='submit' name='mbAtrCancel' value='Cancel'/>");
    table.endTable();
    writeln("<input type='hidden' name='page' value='mbeans'/>");
    write("<input type='hidden' name='mbSrvIdx' value='").write(mbean.getName().getServerIdx()).write("'/>");
    write("<input type='hidden' name='mbName' value='").write(htmlEncode(mbean.getName().getObjectNameString())).write("'/>");
    write("<input type='hidden' name='mbAtrName' value='").write(htmlEncode(attribute.getName())).write("'/>");
    writeln("<br/></form>");
  }
}
