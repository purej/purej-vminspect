// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;
import com.purej.vminspect.data.MBeanParameter;

/**
 * Displays details about a single MBean.
 *
 * @author Stefan Mueller
 */
public class MBeansDetailView extends AbstractMBeansView {
  private final MBeanData _mbean;
  private final String _okMessage;
  private final String _warnMessage;
  private final boolean _readonly;

  /**
   * Creates a new instance of this view.
   */
  public MBeansDetailView(StringBuilder output, MBeanData mbean, String okMessage, String warnMessage, boolean readonly) {
    super(output);
    _mbean = mbean;
    _okMessage = okMessage;
    _warnMessage = warnMessage;
    _readonly = readonly;
  }

  @Override
  public void render() throws IOException {
    // Write the mbean title:
    writeln("<h3>" + img("icons/beans-24.png", "MBean") + "&nbsp;MBean: <i>" + htmlEncode(_mbean.getName().getObjectNameString()) + "</i></h3>");

    if (_okMessage != null) {
      writeln("<div id='okMsg'><br/>&nbsp;" + _okMessage + "<br/><br/></div>");
    }
    if (_warnMessage != null) {
      writeln("<div id='warnMsg'><br/>&nbsp;" + _warnMessage + "<br/><br/></div>");
    }

    // Write the attributes table:
    writeln("<h3>" + img("icons/books-24.png", "MBean Attributes") + "&nbsp;Attributes</h3>");
    CandyHtmlTable table = new CandyHtmlTable("MBean Attributes", "Name", "Value", "Type", "Description", "Edit");
    for (int i = 0; i < _mbean.getAttributes().length; i++) {
      MBeanAttribute attribute = _mbean.getAttributes()[i];
      table.nextRow();
      table.addValue(htmlEncode(attribute.getName()));
      write("<td>");
      writeMBeanValue(attribute.getValue(), true);
      write("</td>");
      table.addValue(htmlEncode(attribute.getType()));
      table.addValue(htmlEncode(attribute.getDescription()));
      if (attribute.isWritable()) {
        if (_readonly) {
          table.addValueCenter(img("icons/lock-grey-16.png", "No Permission"));
        }
        else {
          String atrIdxParam = "mbAtrName=" + attribute.getName();
          table.addValueCenter(lnk(getMBeanNameParams(atrIdxParam), img("icons/pencil-16.png", "Edit")));
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
    for (int i = 0; i < _mbean.getOperations().length; i++) {
      MBeanOperation operation = _mbean.getOperations()[i];
      table.nextRow();
      table.addValue(htmlEncode(operation.getName()));
      table.addValue(operation.getImpact());
      table.addValue(htmlEncode(operation.getReturnType()));
      write("<td>");
      for (int j = 0; j < operation.getParameters().length; j++) {
        MBeanParameter parameter = operation.getParameters()[j];
        if (j > 0) {
          write("<br/>");
        }
        write(htmlEncode(parameter.getName()) + ": ");
        write(htmlEncode(parameter.getType()));
      }
      write("</td>");
      table.addValue(htmlEncode(operation.getDescription()));
      if (_readonly && !operation.getImpact().equals("Info")) {
        table.addValueCenter(img("icons/lock-grey-16.png", "No Permission"));
      }
      else {
        String opIdxParam = "mbOpIdx=" + i;
        table.addValueCenter(lnk(getMBeanNameParams(opIdxParam), img("icons/play-green-16.png", "Invoke")));
      }
    }
    table.endTable();
  }

  private String getMBeanNameParams(String addon) {
    String mbSrvIdx = "mbSrvIdx=" + _mbean.getName().getServerIdx();
    String mbName = "mbName=" + urlEncode(_mbean.getName().getObjectNameString());
    return addon != null ? mBeanParams(mbSrvIdx, mbName, addon) : mBeanParams(mbSrvIdx, mbName);
  }
}
