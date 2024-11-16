// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import java.util.Date;

/**
 * Html page that renders a common header and footer and a custom part in between.
 *
 * @author Stefan Mueller
 */
public class HtmlPageView extends AbstractHtmlView {
  private final long startTimestamp;
  private final String reloadParameters;
  private final AbstractHtmlView bodyView;

  /**
   * Creates a new instance of this view.
   */
  public HtmlPageView(StringBuilder output, String reloadParameters, long startTimestamp, AbstractHtmlView bodyView) {
    super(output);
    this.reloadParameters = reloadParameters;
    this.startTimestamp = startTimestamp;
    this.bodyView = bodyView;
  }

  @Override
  public void render() throws IOException {
    writeHtmlHeader();
    bodyView.render();
    writeHtmlFooter();
  }

  private void writeHtmlHeader() throws IOException {
    writeln("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    writeln("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    writeln("<head>");
    writeln("<title>PureJ VM Inspection</title>");
    writeln("<meta name='author' content='Stefan Mueller, adopus consulting gmbh, Switzerland'/>");
    writeln("<link rel='stylesheet' href='?resource=vminspect.css' type='text/css'/>");
    writeln("<link rel='icon' href='?resource=icons/vminspect-icon.png' type='image/png'>");
    writeln("<script type='text/javascript' src='?resource=vminspect.js'></script>");
    writeln("</head>");
    writeln("<body>");
    writeln("<div id='title'><h2>PureJ VM Inspection " + lnk(reloadParameters, img("icons/refresh-24.png", "Refresh")) + " </h2>");
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
    writeln("<br/><div class='footer'>");
    writeln("Display date: " + formatDateTime(new Date(startTimestamp)) + "<br/>");
    writeln("Display duration: " + formatNumber(System.currentTimeMillis() - startTimestamp) + "ms<br/>");
    writeln("</div>");
    writeln("</body></html>");
  }
}
