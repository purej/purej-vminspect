// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.server;

import java.lang.management.ManagementFactory;
import javax.management.ObjectName;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import com.purej.vminspect.data.MySample;
import com.purej.vminspect.http.servlet.VmInspectionServlet;

/**
 * Starts the VmInspectServlet in a embedded jetty server for testing with a
 * web-browser.
 *
 * @author Stefan Mueller
 */
public final class JettyServerTest {

  private JettyServerTest() {
  }

  /**
   * The java main method.
   */
  public static void main(String[] args) throws Exception {
    // Config from cmd or default:
    int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
    int frequencyMs = args.length > 1 ? Integer.parseInt(args[1]) : 60000;
    String storageDir = args.length > 2 ? args[2] : null;

    Server server = new Server(port);

    // Init 1: Direct init
    // VmInspectionServlet servlet = new VmInspectionServlet();
    // servlet.init(new CustomMBeanAccessControlFactory(),
    // Integer.parseInt(args[1]), args.length > 2 ? args[2] : null);
    // ServletHolder servletHolder = new ServletHolder(servlet);

    // Init 2: servlet parameters
    ServletHolder servletHolder = new ServletHolder(VmInspectionServlet.class);
    servletHolder.setInitParameter("vminspect.mbeans.readonly", "false");
    servletHolder.setInitParameter("vminspect.mbeans.writeConfirmation", "true");
    // servletHolder.setInitParameter("vminspect.mbeans.accessControlFactory",
    // CustomMBeanAccessControlFactory.class.getName());
    servletHolder.setInitParameter("vminspect.statistics.collection.frequencyMs", String.valueOf(frequencyMs));
    servletHolder.setInitParameter("vminspect.statistics.storage.dir", storageDir);

    ServletContextHandler handler = new ServletContextHandler();
    handler.setContextPath("/inspect");
    handler.addServlet(servletHolder, "/*");
    server.setHandler(handler);
    server.start();

    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(false), new ObjectName("purej.vminspect", "id", "1"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect", "id", "2"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=my Type,id=12"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=myType,spaces=a b c"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=myType,sonderzeichen='äöü';"));

    System.out.println("Jetty-Server started and VmInspection deployed, check-out http://localhost:" + port + "/inspect");
  }
}
