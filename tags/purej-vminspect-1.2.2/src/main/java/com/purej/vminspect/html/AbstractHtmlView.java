// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.purej.vminspect.util.Utils;

/**
 * Abstract view class with some utility functions to render HTML pages.
 *
 * @author Stefan Mueller
 */
public abstract class AbstractHtmlView {
  protected static final String PARAMS_SEPARATOR = "&amp;";
  private static final String NUMBER_FORMAT = "###,###";
  private static final String DECIMAL_FORMAT = "###,###.##";
  private static final String PCT_FORMAT = "##.##";
  private static final String DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

  /**
   * Creates a new instance of this HTML table.
   */
  public class HtmlTable {
    private boolean _firstRow = true;

    protected HtmlTable(boolean firstRow) {
      _firstRow = firstRow;
    }

    /**
     * Creates a new instance and writes the table start tags.
     */
    public HtmlTable(String summary) throws IOException {
      this(true);
      write("<table summary='");
      write(summary);
      write("'>\n");
    }

    /**
     * Writes new row tags including optional values.
     */
    public void nextRow(String... values) throws IOException {
      if (_firstRow) {
        write("<tr>");
        _firstRow = false;
      }
      else {
        write("</tr>\n<tr>");
      }
      for (String value : values) {
        addValue(value);
      }
    }

    /**
     * Writes the column value tags.
     */
    public void addValue(String value) throws IOException {
      write("<td>");
      write(value);
      write("</td>");
    }

    /**
     * Writes the column value tags.
     */
    public void addValueCenter(String value) throws IOException {
      write("<td align='center'>");
      write(value);
      write("</td>");
    }

    /**
     * Writes the column value tags.
     */
    public void addValueRight(String value) throws IOException {
      write("<td align='right'>");
      write(value);
      write("</td>");
    }

    /**
     * Writes the table end tags.
     */
    public void endTable() throws IOException {
      if (!_firstRow) {
        write("</tr>");
      }
      write("</table>\n");
    }
  }

  /**
   * A beautify HTML table with alternated colored rows.
   */
  public class CandyHtmlTable extends HtmlTable {
    private boolean _oddRow;

    /**
     * Creates a new instance and writes start tags including column names.
     */
    public CandyHtmlTable(String summary, String... columnNames) throws IOException {
      super(false);
      write("<table width='100%' border='1' cellspacing='0' cellpadding='2' summary='");
      write(summary);
      write("'>\n");
      write("<tr class='header'>");
      for (int i = 0; i < columnNames.length; i++) {
        write("<td>");
        write(columnNames[i]);
        write("</td>");
      }
    }

    @Override
    public void nextRow(String... values) throws IOException {
      nextRowWithClz("");
      for (String value : values) {
        addValue(value);
      }
    }

    /**
     * Writes new row tags with a CSS class-suffix.
     */
    public void nextRowWithClz(String classSuffix) throws IOException {
      write("</tr>\n");
      String clz = _oddRow ? "odd" + classSuffix : "even" + classSuffix;
      write("<tr class='" + clz + "' onmouseover=\"this.className='highlight'\" onmouseout=\"this.className='" + clz + "'\">\n");
      _oddRow = !_oddRow;
    }
  }

  private final StringBuilder _output;

  protected AbstractHtmlView(StringBuilder output) {
    _output = output;
  }

  /**
   * Renders this HMTL view by writing the content to the underlying writer.
   */
  public abstract void render() throws IOException;

  /**
   * Writes the given html to the writer.
   */
  protected final void write(String html) throws IOException {
    _output.append(html == null ? "" : html);
  }

  /**
   * Writes the given html to the writer.
   */
  protected final void writeln(String html) throws IOException {
    _output.append(html);
    _output.append('\n');
  }

  /**
   * Formats to a number without fractions.
   */
  protected static String formatNumber(long value) {
    return new DecimalFormat(NUMBER_FORMAT).format(value);
  }

  /**
   * Formats the given Megabytes 2 fraction digits.
   */
  protected static String formatMb(double mbs) {
    return new DecimalFormat(DECIMAL_FORMAT).format(mbs) + " Mb";
  }

  /**
   * Formats with 2 fraction digits.
   */
  protected static String formatDecimal(double value) {
    return new DecimalFormat(DECIMAL_FORMAT).format(value);
  }

  /**
   * Formats to pcts with 2 fraction digits.
   */
  protected static String formatPct(double value) {
    return new DecimalFormat(PCT_FORMAT).format(value) + "%";
  }

  /**
   * Formats date only.
   */
  protected static String formatDate(Date date) {
    return Utils.formatDate(date);
  }

  /**
   * Formats date and time.
   */
  protected static String formatDateTime(Date date) {
    return new SimpleDateFormat(DATETIME_FORMAT).format(date);
  }

  protected static final String img(String img, String title) throws IOException {
    String t = title != null ? title : "";
    return "<img src='?resource=" + img + "' alt='" + t + "' title='" + t + "'/>";
  }

  protected static final String lnk(String parameters, String txt) {
    return "<a href='?" + parameters + "'>" + txt + "</a>";
  }

  protected static final String htmlEncode(String text) {
    return Utils.htmlEncode(text);
  }

  protected static final String urlEncode(String text) {
    return Utils.urlEncode(text);
  }

  protected static String params(String... params) {
    StringBuilder result = new StringBuilder();
    for (String param : params) {
      if (result.length() > 0) {
        result.append(PARAMS_SEPARATOR);
      }
      result.append(param);
    }
    return result.toString();
  }

  protected static final String showHideLink(String divId, String label) {
    return "<a href=\"javascript:showHide('" + divId + "');\"><img id='" + divId + "Img' src='?resource=bullets/plus.png' alt=''/> " + label + "</a>";
  }

  protected static final String tooltip(String label, String txt) {
    StringBuilder builder = new StringBuilder();
    builder.append("<a class='tooltip'><em>\n");
    builder.append(htmlEncode(txt)).append("</em>").append(htmlEncode(label)).append("</a>\n");
    return builder.toString();
  }

}
