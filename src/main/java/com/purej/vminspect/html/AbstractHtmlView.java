// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

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
    private boolean firstRow = true;

    protected HtmlTable(boolean firstRow) {
      this.firstRow = firstRow;
    }

    /**
     * Creates a new instance and writes the table start tags.
     */
    public HtmlTable(String summary) {
      this(true);
      write("<table summary='").write(summary).write("'>\n");
    }

    /**
     * Writes new row tags including optional values.
     */
    public void nextRow(String... values) {
      if (firstRow) {
        write("<tr>");
        firstRow = false;
      } else {
        write("</tr>\n<tr>");
      }
      for (String value : values) {
        addValue(value);
      }
    }

    /**
     * Writes the column value tags.
     */
    public void addValue(String value) {
      write("<td>").write(value).write("</td>");
    }

    /**
     * Writes the column value tags.
     */
    public void addValueCenter(String value) {
      write("<td align='center'>").write(value).write("</td>");
    }

    /**
     * Writes the column value tags.
     */
    public void addValueRight(String value) {
      write("<td align='right'>").write(value).write("</td>");
    }

    /**
     * Writes the table end tags.
     */
    public void endTable() {
      if (!firstRow) {
        write("</tr>");
      }
      write("</table>\n");
    }
  }

  /**
   * A beautify HTML table with alternated colored rows.
   */
  public class CandyHtmlTable extends HtmlTable {
    private boolean oddRow;

    /**
     * Creates a new instance and writes start tags including column names.
     */
    public CandyHtmlTable(String summary, String... columnNames) {
      super(false);
      write("<table width='100%' border='1' cellspacing='0' cellpadding='2' summary='");
      write(summary).write("'>\n").write("<tr class='header'>");
      for (var n : columnNames) {
        write("<td>").write(n).write("</td>");
      }
    }

    @Override
    public void nextRow(String... values) {
      nextRowWithClz(null);
      for (var value : values) {
        addValue(value);
      }
    }

    /**
     * Writes new row tags with a custom CSS class.
     */
    public void nextRowWithClz(String cssClass) {
      write("</tr>\n");
      if (cssClass != null) {
        write("<tr class='").write(cssClass).write("'>");
      } else {
        write("<tr>");
      }
      oddRow = !oddRow;
    }
  }

  private final StringBuilder output;

  protected AbstractHtmlView(StringBuilder output) {
    this.output = output;
  }

  /**
   * Renders this HMTL view by writing the content to the underlying writer.
   */
  public abstract void render();

  /**
   * Writes the given html to the output.
   */
  protected final AbstractHtmlView write(String html) {
    output.append(html == null ? "" : html);
    return this;
  }

  /**
   * Writes the given number to the output.
   */
  protected final AbstractHtmlView write(int nr) {
    output.append(nr);
    return this;
  }

  /**
   * Writes the given number to the output.
   */
  protected final AbstractHtmlView write(long nr) {
    output.append(nr);
    return this;
  }

  /**
   * Writes a link to the output.
   */
  protected final AbstractHtmlView writeLnk(String parameters, String txt) {
    output.append("<a href='?").append(parameters).append("'>").append(txt).append("</a>");
    return this;
  }

  /**
   * Writes a link to the output.
   */
  protected final AbstractHtmlView writeImg(String img, String title) {
    output.append("<img src='?resource=").append(img);
    output.append("' alt='").append(title).append("' title='").append(title).append("'/>");
    return this;
  }

  /**
   * Writes an image link to the output.
   */
  protected final AbstractHtmlView writeImgLnk(String parameters, String img, String title, String txt) {
    output.append("<a href='?").append(parameters).append("'>");
    output.append("<img src='?resource=").append(img).append("' alt='").append(title).append("' title='").append(title).append("'/>");
    if (txt != null) {
      output.append(txt);
    }
    output.append("</a>");
    return this;
  }

  /**
   * Writes the given html to the output.
   */
  protected final AbstractHtmlView writeln(String html) {
    if (html != null) {
      output.append(html);
    }
    output.append('\n');
    return this;
  }

  /**
   * Writes a new line to the output.
   */
  protected final AbstractHtmlView writeln() {
    output.append('\n');
    return this;
  }

  /**
   * Formats to a number without fractions.
   */
  protected static String formatNumber(long value) {
    return new DecimalFormat(NUMBER_FORMAT).format(value);
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

  protected static final String img(String img, String title) {
    return "<img src='?resource=" + img + "' alt='" + title + "' title='" + title + "'/>";
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
    var result = new StringBuilder();
    for (var param : params) {
      if (result.length() > 0) {
        result.append(PARAMS_SEPARATOR);
      }
      result.append(param);
    }
    return result.toString();
  }

  protected static final String showHideLink(String divId, String label) {
    return "<a showHide='" + divId + "'><img id='" + divId + "Img' src='?resource=bullets/plus.png' alt=''/> " + label + "</a>";
  }

  protected static final String tooltip(String label, String txt) {
    var builder = new StringBuilder();
    builder.append("<a class='tooltip'><em>\n");
    builder.append(htmlEncode(txt)).append("</em>").append(htmlEncode(label)).append("</a>\n");
    return builder.toString();
  }

}
