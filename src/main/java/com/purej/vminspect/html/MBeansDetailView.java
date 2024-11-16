// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanUtils;
import com.purej.vminspect.http.MBeanAccessControl;
import com.purej.vminspect.util.Message;

/**
 * Displays details about a single MBean.
 *
 * @author Stefan Mueller
 */
public class MBeansDetailView extends AbstractMBeansView {
  private final MBeanData mbean;
  private final Message message;
  private final MBeanAccessControl mbeanAccessControl;

  /**
   * Creates a new instance of this view.
   */
  public MBeansDetailView(StringBuilder output, MBeanData mbean, Message message, MBeanAccessControl mbeanAccessControl) {
    super(output);
    this.mbean = mbean;
    this.message = message;
    this.mbeanAccessControl = mbeanAccessControl;
  }

  @Override
  public void render() throws IOException {
    // Write the mbean title:
    writeln("<h3>" + img("icons/beans-24.png", "MBean") + "&nbsp;MBean: <i>" + htmlEncode(mbean.getName().getObjectNameString()) + "</i></h3>");

    if (message != null) {
      writeln("<div id='" + message.getType().getTag() + "'><br/>&nbsp;" + message.getText() + "<br/><br/></div>");
    }

    // Write the attributes table:
    writeln("<h3>" + img("icons/books-24.png", "MBean Attributes") + "&nbsp;Attributes</h3>");
    var table = new CandyHtmlTable("MBean Attributes", "Name", "Value", "Type", "Description", "Edit");
    for (int i = 0; i < mbean.getAttributes().length; i++) {
      var attribute = mbean.getAttributes()[i];
      table.nextRow();
      table.addValue(htmlEncode(attribute.getName()));
      write("<td>");
      writeMBeanValue(attribute.getValue(), true);
      write("</td>");
      table.addValue(htmlEncode(MBeanUtils.toDisplayType(attribute.getType())));
      table.addValue(htmlEncode(attribute.getDescription()));
      if (attribute.isWritable()) {
        if (mbeanAccessControl.isChangeAllowed(mbean, attribute)) {
          var atrIdxParam = "mbAtrName=" + attribute.getName();
          table.addValueCenter(lnk(getMBeanNameParams(atrIdxParam), img("icons/pencil-16.png", "Edit")));
        }
        else {
          table.addValueCenter(img("icons/lock-grey-16.png", "No Permission"));
        }
      }
      else {
        table.addValueCenter(img("icons/pencil-grey-16.png", "Read Only"));
      }
    }
    table.endTable();
    writeln("<br/>");

    // Write the operations table:
    writeln("<h3>" + img("icons/flash-24.png", "MBean Operations") + "&nbsp;Operations</h3>");
    table = new CandyHtmlTable("MBean Operations", "Name", "Impact", "Return", "Parameters", "Description", "Invoke");
    for (var i = 0; i < mbean.getOperations().length; i++) {
      var operation = mbean.getOperations()[i];
      table.nextRow();
      table.addValue(htmlEncode(operation.getName()));
      table.addValue(operation.getImpact().name());
      table.addValue(htmlEncode(MBeanUtils.toDisplayType(operation.getReturnType())));
      write("<td>");
      for (var j = 0; j < operation.getParameters().length; j++) {
        var parameter = operation.getParameters()[j];
        if (j > 0) {
          write("<br/>");
        }
        write(htmlEncode(parameter.getName()) + ": ");
        write(htmlEncode(MBeanUtils.toDisplayType(parameter.getType())));
      }
      write("</td>");
      table.addValue(htmlEncode(operation.getDescription()));
      if (mbeanAccessControl.isCallAllowed(mbean, operation)) {
        var opIdxParam = "mbOpIdx=" + i;
        table.addValueCenter(lnk(getMBeanNameParams(opIdxParam), img("icons/play-green-16.png", "Invoke")));
      }
      else {
        table.addValueCenter(img("icons/lock-grey-16.png", "No Permission"));
      }
    }
    table.endTable();
  }

  private String getMBeanNameParams(String addon) {
    var mbSrvIdx = "mbSrvIdx=" + mbean.getName().getServerIdx();
    var mbName = "mbName=" + urlEncode(mbean.getName().getObjectNameString());
    return addon != null ? mBeanParams(mbSrvIdx, mbName, addon) : mBeanParams(mbSrvIdx, mbName);
  }
}
