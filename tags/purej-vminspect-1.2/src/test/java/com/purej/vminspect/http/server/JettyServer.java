// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.server;

/**
 * Starts the VmInspectServlet in a embedded jetty server.
 *
 * @author Stefan Mueller
 */
public final class JettyServer {

  private JettyServer() {
  }

  /**
   * The java main method.
   */
  public static void main(String[] args) throws Exception {
    //JettyServerConfigurable.main(new String[] {"8080", "10000"});
    JettyServerConfigurable.main(new String[] {"8080", "10000", "C:\\Temp\\vminspect"});
  }
}
