// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    StringBuilder builder = new StringBuilder();
    Throwable th = t;
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
    StringBuilder builder = new StringBuilder();
    Throwable th = t;
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
    }
    catch (Exception e) {
      throw new RuntimeException("Encoding '" + value + "' failed!", e);
    }
  }

  /**
   * Decodes the given URL save encoded value.
   */
  public static String urlDecode(String value) {
    try {
      return value != null ? URLDecoder.decode(value, "UTF-8") : value;
    }
    catch (Exception e) {
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
    }
    else {
      return c2 != null ? 1 : 0;
    }
  }

  /**
   * Returns if the given text matches the given search expression.
   * @param text the text to check
   * @param pattern the pattern to match with or without wildcards, for example 'x*y*z'
   */
  public static boolean wildCardMatch(String text, String pattern) {
    if (pattern == null || pattern.length() == 0) {
      return true;
    }
    if (pattern.length() == 1 && pattern.charAt(0) == '*') {
      return true;
    }
    if (text == null || text.length() == 0) {
      return false;
    }

    int patternIdx = 0;
    int txtIdx = 0;
    StringBuilder matchTag = new StringBuilder();
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
      String matchTagString = matchTag.toString();
      if (isFirst && patternIdx == -1) {
        // First pattern equals last pattern <> *, txt must be equals:
        return text.equals(matchTagString);
      }
      else if (isFirst) {
        // First pattern part <> *, txt must start with matchTag:
        if (!text.startsWith(matchTagString)) {
          return false;
        }
        txtIdx = matchTag.length();
      }
      else if (patternIdx == -1) {
        // Last pattern part <> *, txt must end with matchTag:
        return text.endsWith(matchTagString);
      }
      else {
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
    for (int i = fromIdx; i < pattern.length(); i++) {
      char c = pattern.charAt(i);
      if (c == '*') {
        return i + 1;
      }
      else {
        tag.append(c);
      }
    }
    return -1;
  }

  /**
   * Returns the size of the given object and all contained content (eg. transitives) in bytes.
   * <p/>
   * Note: Static fields will NOT be measured, only instance fields.
   * <p/>
   * Note: This is just an estimate and might different depending on the VM implementation.
   *
   * @param o the object graph to be measured
   * @return the estimated number of bytes
   */
  public static long estimateMemory(Object o) throws Exception {
    Set<Object> measured = new HashSet<Object>();
    measured.add(o);
    return guessObjectMemory(o, measured);
  }

  private static long guessObjectMemory(Object o, Set<Object> measured) throws IllegalAccessException {
    long memory = 8; // Base overhead of an object...

    // Iterate from sub to super-class:
    Class<?> clz = o.getClass();
    while (clz != null) {
      // Iterate over all fields:
      Field[] fields = clz.getDeclaredFields();
      for (Field field : fields) {
        if (Modifier.isStatic(field.getModifiers())) {
          continue; // static stuff is not measured...
        }
        if (field.getType().isPrimitive()) {
          memory += getPrimitiveSize(field.getType());
        }
        else {
          field.setAccessible(true);
          Object fieldValue = field.get(o);
          memory += guessFieldMemory(fieldValue, measured);
        }
      }
      clz = clz.getSuperclass();
    }
    return memory;
  }

  private static long guessFieldMemory(Object o, Set<Object> measured) throws IllegalAccessException {
    long memory = 4; // 4 bytes for the ref to the none-primitive object...
    if (o == null || !measured.add(o)) { // Add prevents cyclic loops...
      return memory;
    }
    if (o.getClass().isArray()) {
      memory += 4; // 4 bytes for the none-null array instance
      Class<?> entryClz = o.getClass().getComponentType();
      int size = Array.getLength(o);
      if (entryClz.isPrimitive()) {
        memory += size * getPrimitiveSize(entryClz);
      }
      else {
        for (int i = 0; i < size; i++) {
          Object entry = Array.get(o, i);
          memory += guessFieldMemory(entry, measured);
        }
      }
    }
    else {
      memory += guessObjectMemory(o, measured);
    }
    return memory;
  }

  private static int getPrimitiveSize(Class<?> primitiveClass) {
    if (primitiveClass == long.class || primitiveClass == double.class) {
      return 8;
    }
    else if (primitiveClass == int.class || primitiveClass == float.class) {
      return 4;
    }
    else if (primitiveClass == short.class || primitiveClass == char.class) {
      return 2;
    }
    return 1;
  }
}
