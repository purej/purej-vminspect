// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    Assertions.assertEquals("x", Utils.checkNotNull("x"));
    try {
      Utils.checkNotNull(null);
      Assertions.fail();
    } catch (IllegalArgumentException e) {
      // Expected...
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testUrlEncodeDecode() throws Exception {
    Assertions.assertEquals("", Utils.urlEncode(null));
    Assertions.assertEquals("", Utils.urlEncode(""));
    Assertions.assertEquals("ja%26va", Utils.urlEncode("ja&va"));
    Assertions.assertEquals("ja%3Dva", Utils.urlEncode("ja=va"));
    Assertions.assertEquals("ja%3D%22%3Bva", Utils.urlEncode("ja=\";va"));
    Assertions.assertEquals("ja+v-%3C%3Ea.*", Utils.urlEncode("ja v-<>a.*"));

    Assertions.assertEquals(null, Utils.urlDecode(null));
    Assertions.assertEquals("", Utils.urlDecode(""));
    Assertions.assertEquals("ja&va", Utils.urlDecode("ja%26va"));
    Assertions.assertEquals("ja=va", Utils.urlDecode("ja%3Dva"));
    Assertions.assertEquals("ja=\";va", Utils.urlDecode("ja%3D%22%3Bva"));
    Assertions.assertEquals("ja v-<>a.*", Utils.urlDecode("ja+v-%3C%3Ea.*")); // produced by encoder
    Assertions.assertEquals("ja v-<>a.*", Utils.urlDecode("ja+v-<>a.*")); // produced by html forms with get
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testHtmlEncode() throws Exception {
    Assertions.assertEquals("", Utils.htmlEncode(null));
    Assertions.assertEquals("", Utils.htmlEncode(""));
    Assertions.assertEquals(" hello ", Utils.htmlEncode(" hello "));
    Assertions.assertEquals("&amp;hello&lt;&gt;world", Utils.htmlEncode("&hello<>world"));
    Assertions.assertEquals("hello&quot;world", Utils.htmlEncode("hello\"world"));
    Assertions.assertEquals("1&#39;2&#39;3&#39;", Utils.htmlEncode("1'2'3'"));
    Assertions.assertEquals("hel<br/>lo<br/>world", Utils.htmlEncode("hel\r\nlo\nworld"));
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
    Assertions.assertEquals(true, Utils.wildCardMatch(s1, null));
    Assertions.assertEquals(true, Utils.wildCardMatch(s2, null));
    Assertions.assertEquals(true, Utils.wildCardMatch(s3, null));

    // Test with empty pattern:
    Assertions.assertEquals(true, Utils.wildCardMatch(s1, ""));
    Assertions.assertEquals(true, Utils.wildCardMatch(s2, ""));
    Assertions.assertEquals(true, Utils.wildCardMatch(s3, ""));

    // Test with wildcard only pattern:
    Assertions.assertEquals(true, Utils.wildCardMatch(s1, "*"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s2, "*"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s3, "*"));

    // Test with simple pattern:
    Assertions.assertEquals(false, Utils.wildCardMatch(s1, "a"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s2, "a"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s3, "a"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s4, "a"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s5, "a"));

    // Test with simple pattern with wildcard start:
    Assertions.assertEquals(false, Utils.wildCardMatch(s1, "*a"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s2, "*a"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s3, "*a"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s4, "*a"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s5, "*a"));

    // Test with simple pattern with wildcard end:
    Assertions.assertEquals(false, Utils.wildCardMatch(s1, "a*"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s2, "a*"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s3, "a*"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s4, "a*"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s5, "a*"));

    // Test with long pattern:
    Assertions.assertEquals(false, Utils.wildCardMatch(s1, "abcdefgh"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s2, "abcdefgh"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s3, "abcdefgh"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s4, "abcdefgh"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s5, "abcdefgh"));

    // Test with special patterns ok:
    Assertions.assertEquals(true, Utils.wildCardMatch(s5, "Der*"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s5, "Der *"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s5, "* mit dem *"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s5, "*tanzt"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s5, "*er*dem *nzt*"));
    Assertions.assertEquals(true, Utils.wildCardMatch(s5, "Der*dem*t"));

    // Test with special patterns nok:
    Assertions.assertEquals(false, Utils.wildCardMatch(s5, "Des*"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s5, "* mit des *"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s5, "*tantzt"));
    Assertions.assertEquals(false, Utils.wildCardMatch(s5, "*er*dm *nzt*"));
  }
}
