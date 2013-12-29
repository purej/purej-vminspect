// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import com.purej.vminspect.data.statistics.Range;

/**
 * Displays the currently existing statistics.
 *
 * @author Stefan Mueller
 */
public final class StatisticsDetailView extends AbstractStatisticsView {
  private final String _statsName;

  /**
   * Creates a new instance of this view.
   */
  public StatisticsDetailView(StringBuilder output, Range range, String statsName) {
    super(output, range);
    _statsName = statsName;
  }

  @Override
  public void render() throws IOException {
    writeln("<h3>" + img("icons/charts-24.png", "Statistics") + "&nbsp;Statistics Detail</h3>");
    writeln("<div align='center'>");
    writeChoosePeriodLinks(_statsName);
    writeln("</div>");
    writeln("<div id='track' class='noPrint'>");
    writeln("<div class='selected' id='handle'>");
    writeln("<img src='?resource=scaler_slider.gif' alt=''/>");
    writeln("</div></div>");
    writeln("<div align='center'><img class='synthese' id='img' src='?statsWidth=960&amp;statsHeight=400&amp;statsGraph=" + urlEncode(_statsName)
        + "' alt='zoom'/></div>");
    writeGraphDetailScript(_statsName);
  }

  private void writeGraphDetailScript(String graphName) throws IOException {
    writeln("<script type='text/javascript'>");
    writeln("/* <![CDATA[ */");
    writeln("function scaleImage(v, min, max) {");
    writeln("    var images = document.getElementsByClassName('synthese');");
    writeln("    w = (max - min) * v + min;");
    writeln("    for (i = 0; i < images.length; i++) {");
    writeln("        images[i].style.width = w + 'px';");
    writeln("    }");
    writeln("}");

    // 'animate' our slider
    writeln("var slider = new Control.Slider('handle', 'track', {axis:'horizontal', alignX: 0, increment: 2});");

    // resize the image as the slider moves. The image quality would deteriorate, but it
    // would not be final anyway. Once slider is released the image is re-requested from the server, where
    // it is rebuilt from vector format
    writeln("slider.options.onSlide = function(value) {");
    writeln("  scaleImage(value, initialWidth, initialWidth / 2 * 3);");
    writeln("}");

    // this is where the slider is released and the image is reloaded
    // we use current style settings to work the required image dimensions
    writeln("slider.options.onChange = function(value) {");
    writeln("  width = Math.round(Element.getStyle('img','width').replace('px','')) - 80;");
    writeln("  height = Math.round(width * initialHeight / initialWidth) - 48;");
    writeln("  document.getElementById('img').src = '?statsGraph=" + urlEncode(graphName)
        + "\\u0026statsWidth=' + width + '\\u0026statsHeight=' + height;");
    writeln("  document.getElementById('img').style.width = '';");
    writeln("}");
    writeln("window.onload = function() {");
    writeln("  if (navigator.appName == 'Microsoft Internet Explorer') {");
    writeln("    initialWidth = document.getElementById('img').width;");
    writeln("    initialHeight = document.getElementById('img').height;");
    writeln("  } else {");
    writeln("    initialWidth = Math.round(Element.getStyle('img','width').replace('px',''));");
    writeln("    initialHeight = Math.round(Element.getStyle('img','height').replace('px',''));");
    writeln("  }");
    writeln("}");
    writeln("/* ]]> */");
    writeln("</script>");
  }
}
