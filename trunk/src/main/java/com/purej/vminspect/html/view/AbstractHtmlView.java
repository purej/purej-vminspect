// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html.view;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.purej.vminspect.html.RequestParams;

/**
 * Abstract view class with some utility functions to render HTML pages.
 *
 * @author Stefan Mueller
 */
public abstract class AbstractHtmlView {
  private static final String NUMBER_FORMAT = "###,###";
  private static final String DECIMAL_FORMAT = "###,###.##";
  private static final String PCT_FORMAT = "##.##";
  private static final String DATE_FORMAT = "dd.MM.yyyy";
  private static final String DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

  /**
   * Creates a new instance of this HTML table.
   */
  public class HtmlTable {
    private boolean _firstRow = true;

    /**
     * Returns if first row.
     */
    public boolean isFirstRow() {
      return _firstRow;
    }

    /**
     * Sets if first row.
     */
    public void setFirstRow(boolean firstRow) {
      _firstRow = firstRow;
    }

    /**
     * Writes the table start tags.
     */
    public void beginTable(String summary) throws IOException {
      write("<table summary='");
      write(summary);
      write("'>\n");
    }

    /**
     * Writes new row tags.
     */
    public void nextRow() throws IOException {
      if (_firstRow) {
        write("<tr>");
        _firstRow = false;
      }
      else {
        write("</tr>\n<tr>");
      }
    }

    /**
     * Writes new row tags including values.
     */
    public void nextRowWithValues(String... values) throws IOException {
      nextRow();
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
   * A HTML table that is sortable by default.
   */
  public class SortableHtmlTable extends HtmlTable {
    private boolean _oddRow;

    /**
     * Writes start tags including column names.
     */
    public void beginTable(String summary, String... columnNames) throws IOException {
      write("<table class='sortable' width='100%' border='1' cellspacing='0' cellpadding='2' summary='");
      write(summary);
      write("'>\n");
      write("<thead><tr>");
      for (int i = 0; i < columnNames.length; i++) {
        if (columnNames[i].startsWith("#")) {
          write("<th class='sorttable_numeric'>");
          write(columnNames[i].substring(1));
        }
        else {
          write("<th>");
          write(columnNames[i]);
        }
        write("</th>");
      }
    }

    @Override
    public void nextRow() throws IOException {
      nextRow("");
    }

    /**
     * Writes new row tags with a CSS class-suffix.
     */
    public void nextRow(String classSuffix) throws IOException {
      write("</tr>");
      if (isFirstRow()) {
        setFirstRow(false);
        write("</thead><tbody>\n");
      }
      String clz = _oddRow ? "odd" + classSuffix : "even" + classSuffix;
      write("<tr class='" + clz + "' onmouseover=\"this.className='highlight'\" onmouseout=\"this.className='" + clz + "'\">\n");
      _oddRow = !_oddRow;
    }

    @Override
    public void endTable() throws IOException {
      write("</tr>");
      if (isFirstRow()) {
        setFirstRow(false);
        write("</thead><tbody>\n");
      }
      write("</tbody></table>\n");
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
  public static String formatNumber(long value) {
    return new DecimalFormat(NUMBER_FORMAT).format(value);
  }

  /**
   * Formats the given Megabytes 2 fraction digits.
   */
  public static String formatMb(double mbs) {
    return new DecimalFormat(DECIMAL_FORMAT).format(mbs) + " Mb";
  }

  /**
   * Formats with 2 fraction digits.
   */
  public static String formatDecimal(double value) {
    return new DecimalFormat(DECIMAL_FORMAT).format(value);
  }

  /**
   * Formats to pcts with 2 fraction digits.
   */
  public static String formatPct(double value) {
    return new DecimalFormat(PCT_FORMAT).format(value) + "%";
  }

  /**
   * Formats date only.
   */
  public static String formatDate(Date date) {
    return new SimpleDateFormat(DATE_FORMAT).format(date);
  }

  /**
   * Parses the given date only.
   */
  public static Date parseDate(String date) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT).parse(date);
  }

  /**
   * Formats date and time.
   */
  public static String formatDateTime(Date date) {
    return new SimpleDateFormat(DATETIME_FORMAT).format(date);
  }

  protected static final String img(String img, String title) throws IOException {
    String t = title != null ? title : "";
    return "<img src='?" + RequestParams.RESOURCE + "=" + img + "' alt='" + t + "' title='" + t + "'/>";
  }

  protected static final String lnk(String parameters, String txt) {
    return "<a href='?" + parameters + "'>" + txt + "</a>";
  }

  /**
   * Encodes the given text to HTML save form.
   */
  public static final String htmlEncode(String text) {
    String txt = text == null ? "" : text;
    return txt.replaceAll("[&]", "&amp;").replaceAll("[<]", "&lt;").replaceAll("[>]", "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;")
        .replaceAll("[\n]", "<br/>");
  }

  protected static final String javascriptEncode(String text) {
    String txt = text == null ? "" : text;
    return txt.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"").replace("'", "\\'");
  }

  /**
   * Encodes the given URL to HTML save form.
   */
  public static final String urlEncode(String text) {
    try {
      return URLEncoder.encode(text != null ? text : "", "UTF-8");
    }
    catch (Exception e) {
      throw new RuntimeException("Could not URL-encode text '" + text + "'!");
    }
  }

  protected static String params(String... params) {
    StringBuilder result = new StringBuilder();
    for (String param : params) {
      if (result.length() > 0) {
        result.append(RequestParams.SEPARATOR);
      }
      result.append(param);
    }
    return result.toString();
  }

  protected static final String hiddenDiv(String divId, String txt) {
    return "<div id='" + divId + "' style='display: none;'>" + txt + "</div>";
  }

  protected static final String showHideLink(String divId, String label) {
    return "<a href=\"javascript:showHide('" + divId + "');\"><img id='" + divId + "Img' src='?resource=bullets/plus.png' alt=''/> "
        + label + "</a>";
  }

  protected static final String tooltip(String label, String txt) {
    StringBuilder builder = new StringBuilder();
    builder.append("<a class='tooltip'><em>\n");
    builder.append(htmlEncode(txt)).append("</em>").append(htmlEncode(label)).append("</a>\n");
    return builder.toString();
  }

}
