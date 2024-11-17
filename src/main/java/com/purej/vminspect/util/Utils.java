// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains some utility methods for different purposes.
 *
 * @author Stefan Mueller
 */
public final class Utils {
  private static final String DATE_FORMAT = "dd.MM.yyyy";

  private Utils() {
  }

  /**
   * Checks and throws an exception if the given value is null.
   */
  public static <T> T checkNotNull(T value) {
    if (value == null) {
      throw new IllegalArgumentException("Argument is null!");
    }
    return value;
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
   * Creates an exception info string from the given throwable.
   */
  public static String getExceptionInfo(Throwable t) {
    var builder = new StringBuilder();
    var th = t;
    while (th != null) {
      if (builder.length() > 0) {
        builder.append("\nCaused by: ");
      }
      builder.append(th.getClass().getName()).append(": ").append(th.getMessage());
      th = th.getCause();
    }
    return builder.toString();
  }

  /**
   * Creates an exception info string from the given throwable.
   */
  public static String getHtmlExceptionInfo(Throwable t) {
    var builder = new StringBuilder();
    var th = t;
    boolean isFirst = true;
    while (th != null) {
      if (!isFirst) {
        builder.append("<br/>Caused by: ");
      }
      if (isFirst) {
        builder.append("<b>");
      }
      builder.append(th.getClass().getName()).append(": ").append(htmlEncode(th.getMessage()));
      if (isFirst) {
        builder.append("</b>");
      }
      th = th.getCause();
      isFirst = false;
    }
    return builder.toString();
  }

  /**
   * Encodes the given value to URL save form.
   */
  public static String urlEncode(String value) {
    try {
      return value != null ? URLEncoder.encode(value, "UTF-8") : "";
    } catch (Exception e) {
      throw new RuntimeException("Encoding '" + value + "' failed!", e);
    }
  }

  /**
   * Decodes the given URL save encoded value.
   */
  public static String urlDecode(String value) {
    try {
      return value != null ? URLDecoder.decode(value, "UTF-8") : value;
    } catch (Exception e) {
      throw new RuntimeException("Decoding '" + value + "' failed!", e);
    }
  }

  /**
   * Encodes the given text to HTML save form.
   */
  public static String htmlEncode(String txt) {
    if (txt == null) {
      return "";
    }
    return txt.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;").replace("\r\n", "<br/>")
        .replace("\n", "<br/>");
  }

  /**
   * Compares the two given objects and handles null values for both of them.
   */
  public static <T extends Comparable<T>> int compareTo(T c1, T c2) {
    if (c1 != null) {
      return c2 != null ? c1.compareTo(c2) : -1;
    } else {
      return c2 != null ? 1 : 0;
    }
  }

  /**
   * Returns if the given text matches the given search expression.
   * @param text the text to check
   * @param pattern the pattern to match with or without wildcards, for example 'x*y*z'
   */
  public static boolean wildCardMatch(String text, String pattern) {
    if (pattern == null || pattern.length() == 0 || (pattern.length() == 1 && pattern.charAt(0) == '*')) {
      return true;
    }
    if (text == null || text.length() == 0) {
      return false;
    }

    int patternIdx = 0;
    int txtIdx = 0;
    var matchTag = new StringBuilder();
    boolean isFirst = true;
    while (true) {
      matchTag.setLength(0);
      patternIdx = nextMatchTag(pattern, patternIdx, matchTag);
      if (matchTag.length() == 0) {
        // Wildcard...
        if (patternIdx == -1) {
          return true;
        }
        isFirst = false;
        continue;
      }
      var matchTagString = matchTag.toString();
      if (isFirst && patternIdx == -1) {
        // First pattern equals last pattern <> *, txt must be equals:
        return text.equals(matchTagString);
      } else if (isFirst) {
        // First pattern part <> *, txt must start with matchTag:
        if (!text.startsWith(matchTagString)) {
          return false;
        }
        txtIdx = matchTag.length();
      } else if (patternIdx == -1) {
        // Last pattern part <> *, txt must end with matchTag:
        return text.endsWith(matchTagString);
      } else {
        // Pattern in between, txt must contain it:
        txtIdx = text.indexOf(matchTagString, txtIdx);
        if (txtIdx == -1) {
          return false;
        }
      }
      isFirst = false;
    }
  }

  private static int nextMatchTag(String pattern, int fromIdx, StringBuilder tag) {
    for (var i = fromIdx; i < pattern.length(); i++) {
      var c = pattern.charAt(i);
      if (c == '*') {
        return i + 1;
      } else {
        tag.append(c);
      }
    }
    return -1;
  }
}
