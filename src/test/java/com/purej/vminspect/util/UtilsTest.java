// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the named functionality.
 *
 * @author Stefan Mueller
 */
public class UtilsTest {

  /**
   * Tests the named functionality.
   */
  @Test
  public void testCheckNotNull() throws Exception {
    Assert.assertEquals("x", Utils.checkNotNull("x"));
    try {
      Utils.checkNotNull(null);
      Assert.fail();
    } catch (IllegalArgumentException e) {
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
}
