// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.util;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.purej.vminspect.data.statistics.StatisticsCollector;

/**
 * Tests the named functionality.
 *
 * @author Stefan Mueller
 */
public class UtilsTest {

  private static class MyEmpty {
    // Empty...
  }

  @SuppressWarnings("unused")
  private static class MyPrimitives {
    private boolean _f1;
    private char _f2;
    private byte _f3;
    private short _f4;
    private int _f5;
    private long _f6;
    private float _f7;
    private double _f8;
  }

  private static class MyO {
    private Object _o;
  }

  @SuppressWarnings("unused")
  private static class MyArrays {
    private boolean[] _f1;
    private char[] _f2;
    private byte[] _f3;
    private short[] _f4;
    private int[] _f5;
    private long[] _f6;
    private float[] _f7;
    private double[] _f8;
    private Object[] _f9;
  }

  private int _stringMemoryOverhead;

  /**
   * Setup.
   */
  @Before
  public void setUp() {
    String javaVersion = System.getProperty("java.version");
    _stringMemoryOverhead = 28; // For older VMs
    if (javaVersion.startsWith("1.7")) {
      _stringMemoryOverhead = 24;
    }
    else if (javaVersion.startsWith("1.8")) {
      _stringMemoryOverhead = 20;
    }

  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testCheckNotNull() throws Exception {
    Assert.assertEquals("x", Utils.checkNotNull("x"));
    try {
      Utils.checkNotNull(null);
      Assert.fail();
    }
    catch (IllegalArgumentException e) {
      // Expected...
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testUrlEncodeDecode() throws Exception {
    Assert.assertEquals("", Utils.urlEncode(null));
    Assert.assertEquals("", Utils.urlEncode(""));
    Assert.assertEquals("ja%26va", Utils.urlEncode("ja&va"));
    Assert.assertEquals("ja%3Dva", Utils.urlEncode("ja=va"));
    Assert.assertEquals("ja%3D%22%3Bva", Utils.urlEncode("ja=\";va"));
    Assert.assertEquals("ja+v-%3C%3Ea.*", Utils.urlEncode("ja v-<>a.*"));

    Assert.assertEquals(null, Utils.urlDecode(null));
    Assert.assertEquals("", Utils.urlDecode(""));
    Assert.assertEquals("ja&va", Utils.urlDecode("ja%26va"));
    Assert.assertEquals("ja=va", Utils.urlDecode("ja%3Dva"));
    Assert.assertEquals("ja=\";va", Utils.urlDecode("ja%3D%22%3Bva"));
    Assert.assertEquals("ja v-<>a.*", Utils.urlDecode("ja+v-%3C%3Ea.*")); // produced by encoder
    Assert.assertEquals("ja v-<>a.*", Utils.urlDecode("ja+v-<>a.*")); // produced by html forms with get
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testHtmlEncode() throws Exception {
    Assert.assertEquals("", Utils.htmlEncode(null));
    Assert.assertEquals("", Utils.htmlEncode(""));
    Assert.assertEquals(" hello ", Utils.htmlEncode(" hello "));
    Assert.assertEquals("&amp;hello&lt;&gt;world", Utils.htmlEncode("&hello<>world"));
    Assert.assertEquals("hello&quot;world", Utils.htmlEncode("hello\"world"));
    Assert.assertEquals("1&#39;2&#39;3&#39;", Utils.htmlEncode("1'2'3'"));
    Assert.assertEquals("hel<br/>lo<br/>world", Utils.htmlEncode("hel\r\nlo\nworld"));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testWildCardMatch() throws Exception {
    String s1 = null;
    String s2 = "";
    String s3 = "a";
    String s4 = "abcdefg";
    String s5 = "Der mit dem Wolf tanzt";

    // Test with null pattern:
    Assert.assertEquals(true, Utils.wildCardMatch(s1, null));
    Assert.assertEquals(true, Utils.wildCardMatch(s2, null));
    Assert.assertEquals(true, Utils.wildCardMatch(s3, null));

    // Test with empty pattern:
    Assert.assertEquals(true, Utils.wildCardMatch(s1, ""));
    Assert.assertEquals(true, Utils.wildCardMatch(s2, ""));
    Assert.assertEquals(true, Utils.wildCardMatch(s3, ""));

    // Test with wildcard only pattern:
    Assert.assertEquals(true, Utils.wildCardMatch(s1, "*"));
    Assert.assertEquals(true, Utils.wildCardMatch(s2, "*"));
    Assert.assertEquals(true, Utils.wildCardMatch(s3, "*"));

    // Test with simple pattern:
    Assert.assertEquals(false, Utils.wildCardMatch(s1, "a"));
    Assert.assertEquals(false, Utils.wildCardMatch(s2, "a"));
    Assert.assertEquals(true, Utils.wildCardMatch(s3, "a"));
    Assert.assertEquals(false, Utils.wildCardMatch(s4, "a"));
    Assert.assertEquals(false, Utils.wildCardMatch(s5, "a"));

    // Test with simple pattern with wildcard start:
    Assert.assertEquals(false, Utils.wildCardMatch(s1, "*a"));
    Assert.assertEquals(false, Utils.wildCardMatch(s2, "*a"));
    Assert.assertEquals(true, Utils.wildCardMatch(s3, "*a"));
    Assert.assertEquals(false, Utils.wildCardMatch(s4, "*a"));
    Assert.assertEquals(false, Utils.wildCardMatch(s5, "*a"));

    // Test with simple pattern with wildcard end:
    Assert.assertEquals(false, Utils.wildCardMatch(s1, "a*"));
    Assert.assertEquals(false, Utils.wildCardMatch(s2, "a*"));
    Assert.assertEquals(true, Utils.wildCardMatch(s3, "a*"));
    Assert.assertEquals(true, Utils.wildCardMatch(s4, "a*"));
    Assert.assertEquals(false, Utils.wildCardMatch(s5, "a*"));

    // Test with long pattern:
    Assert.assertEquals(false, Utils.wildCardMatch(s1, "abcdefgh"));
    Assert.assertEquals(false, Utils.wildCardMatch(s2, "abcdefgh"));
    Assert.assertEquals(false, Utils.wildCardMatch(s3, "abcdefgh"));
    Assert.assertEquals(false, Utils.wildCardMatch(s4, "abcdefgh"));
    Assert.assertEquals(false, Utils.wildCardMatch(s5, "abcdefgh"));

    // Test with special patterns ok:
    Assert.assertEquals(true, Utils.wildCardMatch(s5, "Der*"));
    Assert.assertEquals(true, Utils.wildCardMatch(s5, "Der *"));
    Assert.assertEquals(true, Utils.wildCardMatch(s5, "* mit dem *"));
    Assert.assertEquals(true, Utils.wildCardMatch(s5, "*tanzt"));
    Assert.assertEquals(true, Utils.wildCardMatch(s5, "*er*dem *nzt*"));
    Assert.assertEquals(true, Utils.wildCardMatch(s5, "Der*dem*t"));

    // Test with special patterns nok:
    Assert.assertEquals(false, Utils.wildCardMatch(s5, "Des*"));
    Assert.assertEquals(false, Utils.wildCardMatch(s5, "* mit des *"));
    Assert.assertEquals(false, Utils.wildCardMatch(s5, "*tantzt"));
    Assert.assertEquals(false, Utils.wildCardMatch(s5, "*er*dm *nzt*"));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testEstimateMemoryEmpty() throws Exception {
    Assert.assertEquals(8, Utils.estimateMemory(new MyEmpty()));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testEstimateMemoryPrimitives() throws Exception {
    Assert.assertEquals(38, Utils.estimateMemory(new MyPrimitives()));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testEstimateMemoryString() throws Exception {
    String o = "";
    Assert.assertEquals(_stringMemoryOverhead, Utils.estimateMemory(o));

    o = "abc";
    Assert.assertEquals(_stringMemoryOverhead + 3 * 2, Utils.estimateMemory(o));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testEstimateMemoryInteger() throws Exception {
    Integer o = Integer.valueOf(0);
    Assert.assertEquals(12, Utils.estimateMemory(o));

    o = Integer.valueOf(1234567);
    Assert.assertEquals(12, Utils.estimateMemory(o));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testEstimateMemoryO() throws Exception {
    MyO root = new MyO();
    Assert.assertEquals(12, Utils.estimateMemory(root));

    root._o = root; // cycle
    Assert.assertEquals(12, Utils.estimateMemory(root));

    root._o = new MyO(); // nested
    Assert.assertEquals(24, Utils.estimateMemory(root));

    ((MyO) root._o)._o = root; // nested cycle
    Assert.assertEquals(24, Utils.estimateMemory(root));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testEstimateMemoryArrays() throws Exception {
    // Test empty:
    MyArrays arrays = new MyArrays();
    int expected = 44; // --> 9x 4 bytes + 8 bytes
    Assert.assertEquals(expected, Utils.estimateMemory(arrays));

    // Test with booleans[]:
    arrays._f1 = new boolean[0];
    expected += 4; // --> + 4 bytes for the array instance
    Assert.assertEquals(expected, Utils.estimateMemory(arrays));

    // Test with byte[]:
    arrays._f3 = new byte[1024];
    expected += 1028; // --> + 4 bytes for the array instance + 1024 bytes for content
    Assert.assertEquals(expected, Utils.estimateMemory(arrays));

    // Test with char[]:
    arrays._f2 = new char[3];
    expected += 10; // --> + 4 bytes for the array instance + 6 bytes for content
    Assert.assertEquals(expected, Utils.estimateMemory(arrays));

    // Test with Object[]:
    arrays._f9 = new Object[0];
    expected += 4; // --> + 4 bytes for the array instance
    Assert.assertEquals(expected, Utils.estimateMemory(arrays));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testEstimateMemoryList() throws Exception {
    List<Object> list = new ArrayList<Object>(10);
    int expected = 64;
    Assert.assertEquals(expected, Utils.estimateMemory(list));

    list.add("a");
    list.add("b");
    expected += 2 * _stringMemoryOverhead + 4;
    Assert.assertEquals(expected, Utils.estimateMemory(list));
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testEstimateStatisticsCollector() throws Exception {
    StatisticsCollector collector = StatisticsCollector.init(null, 10000, this);
    try {
      System.out.println("Memory of the collector: " + Utils.estimateMemory(collector));
      Assert.assertTrue(Utils.estimateMemory(collector) > 1000000);
    }
    finally {
      StatisticsCollector.destroy(this);
    }
  }
}
