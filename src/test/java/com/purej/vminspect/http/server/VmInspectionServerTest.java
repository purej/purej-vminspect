package com.purej.vminspect.http.server;

import org.junit.Test;

/**
 * Tests the named functionality.
 *
 * @author Stefan Mueller
 */
public class VmInspectionServerTest {

  /**
   * Starts the {@link VmInspectionServer} on port 8080 for testing with a browser.
   */
  @SuppressWarnings("unused")
  public static void main(String[] args) throws Exception {
    new VmInspectionServer(8080);
    Thread.sleep(Integer.MAX_VALUE);
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testStartStop() throws Exception {
    VmInspectionServer server = new VmInspectionServer(12345);
    server.shutdown();
  }
}
