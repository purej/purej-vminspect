// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import java.util.Date;
import com.purej.vminspect.html.RequestParams;

/**
 * Html page that renders a common header and footer and a custom part in between.
 *
 * @author Stefan Mueller
 */
public class HtmlPageView extends AbstractHtmlView {
  private final long _startTimestamp;
  private final String _currentParameters;
  private final AbstractHtmlView _bodyView;

  /**
   * Creates a new instance of this view.
   */
  public HtmlPageView(StringBuilder output, String currentParameters, long startTimestamp, AbstractHtmlView bodyView) {
    super(output);
    _currentParameters = currentParameters;
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
    writeln("<meta name='author' content='Stefan Mueller, www.purej.com'/>");
    writeln("<link rel='stylesheet' href='?resource=vminspect.css' type='text/css'/>");
    writeln("<script type='text/javascript' src='?resource=sorttable.js'></script>");
    writeln("<script type='text/javascript' src='?resource=prototype.js'></script>");
    writeln("<script type='text/javascript' src='?resource=effects.js'></script>");
    if (_bodyView instanceof StatisticsDetailView) {
      writeln("<script type='text/javascript' src='?resource=slider.js'></script>");
    }
    writeJavaScript();
    writeln("</head>");
    writeln("<body>");
    writeln("<div id='title'><h2>PureJ VM Inspection " + lnk(_currentParameters, img("action_refresh.png", "Refresh")) + " </h2>");
    writeln("</div><div id='menu'>");
    writeln("<ul>");
    writeln("<li>" + lnk(RequestParams.PAGE + "=statistics", img("icons/charts-24.png", "Statistics") + "Statistics") + "</li>");
    writeln("<li>" + lnk(RequestParams.PAGE + "=threads", img("icons/threads-24.png", "Threads") + "Threads") + "</li>");
    writeln("<li>" + lnk(RequestParams.PAGE + "=mbeans", img("icons/beans-24.png", "MBeans") + "MBeans") + "</li>");
    writeln("<li>" + lnk(RequestParams.PAGE + "=system", img("icons/system-24.png", "System") + "System") + "</li>");
    writeln("</ul>");
    writeln("</div>");
  }

  private void writeHtmlFooter() throws IOException {
    writeln("<br/><div style='font-size:8pt;'>");
    writeln("Display date: " + formatDateTime(new Date(_startTimestamp)) + "<br/>");
    writeln("Display duration: " + formatNumber(System.currentTimeMillis() - _startTimestamp) + " ms<br/>");
    writeln("Powered by: <a href='http://www.purej.com' target='_blank'>purej.com</a>");
    writeln("</div>");
    writeln("</body></html>");
  }

  private void writeJavaScript() throws IOException {
    writeln("<script type='text/javascript'>");
    writeln("function showHide(id) {");
    writeln("  if (document.getElementById(id).style.display=='none') {");
    writeln("    if (document.getElementById(id + 'Img') != null) {");
    writeln("      document.getElementById(id + 'Img').src='?resource=bullets/minus.png';");
    writeln("    }");
    writeln("    try {");
    writeln("      Effect.SlideDown(id, { duration: 0.5 });");
    writeln("    } catch (e) {");
    writeln("      document.getElementById(id).style.display='inline';");
    writeln("    }");
    writeln("  } else {");
    writeln("    if (document.getElementById(id + 'Img') != null) {");
    writeln("      document.getElementById(id + 'Img').src='?resource=bullets/plus.png';");
    writeln("    }");
    writeln("    try {");
    writeln("      Effect.SlideUp(id, { duration: 0.5 });");
    writeln("    } catch (e) {");
    writeln("      document.getElementById(id).style.display='none';");
    writeln("    }");
    writeln("  }");
    writeln("}");
    writeln("</script>");
  }
}
