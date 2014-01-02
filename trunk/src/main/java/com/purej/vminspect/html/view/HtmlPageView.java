// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import java.util.Date;

/**
 * Html page that renders a common header and footer and a custom part in between.
 *
 * @author Stefan Mueller
 */
public class HtmlPageView extends AbstractHtmlView {
  private final long _startTimestamp;
  private final String _reloadParameters;
  private final AbstractHtmlView _bodyView;

  /**
   * Creates a new instance of this view.
   */
  public HtmlPageView(StringBuilder output, String reloadParameters, long startTimestamp, AbstractHtmlView bodyView) {
    super(output);
    _reloadParameters = reloadParameters;
    _startTimestamp = startTimestamp;
    _bodyView = bodyView;
  }

  @Override
  public void render() throws IOException {
    writeHtmlHeader();
    _bodyView.render();
    writeHtmlFooter();
  }

  private void writeHtmlHeader() throws IOException {
    writeln("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    writeln("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    writeln("<head>");
    writeln("<title>PureJ VM Inspection</title>");
    writeln("<meta name='author' content='Stefan Mueller, adopus consulting gmbh, Switzerland'/>");
    writeln("<link rel='stylesheet' href='?resource=vminspect.css' type='text/css'/>");
    writeln("<script type='text/javascript' src='?resource=vminspect.js'></script>");
    writeln("</head>");
    writeln("<body>");
    writeln("<div id='title'><h2>PureJ VM Inspection " + lnk(_reloadParameters, img("icons/refresh-24.png", "Refresh")) + " </h2>");
    writeln("</div><div id='menu'>");
    writeln("<ul>");
    writeln("<li>" + lnk("page=statistics", img("icons/charts-24.png", "Statistics") + "Statistics") + "</li>");
    writeln("<li>" + lnk("page=threads", img("icons/threads-24.png", "Threads") + "Threads") + "</li>");
    writeln("<li>" + lnk("page=mbeans", img("icons/beans-24.png", "MBeans") + "MBeans") + "</li>");
    writeln("<li>" + lnk("page=system", img("icons/system-24.png", "System") + "System") + "</li>");
    writeln("</ul>");
    writeln("</div>");
  }

  private void writeHtmlFooter() throws IOException {
    writeln("<br/><div style='font-size:8pt;'>");
    writeln("Display date: " + formatDateTime(new Date(_startTimestamp)) + "<br/>");
    writeln("Display duration: " + formatNumber(System.currentTimeMillis() - _startTimestamp) + " ms<br/>");
    writeln("Powered by: <a href='http://www.adopus.com/purej' target='_blank'>adopus consulting gmbh</a>");
    writeln("</div>");
    writeln("</body></html>");
  }
}
