// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.server;

import java.lang.management.ManagementFactory;
import javax.management.ObjectName;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import com.purej.vminspect.data.MySample;
import com.purej.vminspect.html.VmInspectServlet;

/**
 * Starts the {@link VmInspectServlet} in a embedded jetty server.
 *
 * @author Stefan Mueller
 */
public final class JettyServerConfigurable {

  private JettyServerConfigurable() {
  }

  /**
   * The java main method.
   * Usage: JettyServerConfigurableMain port frequencyMs
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 2 || args.length != 3) {
      System.out.println("Usage: " + JettyServerConfigurable.class.getName() + " port frequencyMs ");
    }

    SelectChannelConnector connector = new SelectChannelConnector();
    connector.setReuseAddress(false);
    connector.setPort(Integer.parseInt(args[0]));
    Server server = new Server();
    server.setConnectors(new Connector[] {connector});

    ServletContextHandler handler = new ServletContextHandler();
    handler.setContextPath("/inspect");

    ServletHolder servletHolder = new ServletHolder(VmInspectServlet.class);
    servletHolder.setInitParameter("vminspect.statistics.collection.frequencyMs", args[1]);
    if (args.length > 2) {
      servletHolder.setInitParameter("vminspect.statistics.storage.dir", args[2]);
    }
    servletHolder.setInitOrder(0);
    handler.addServlet(servletHolder, "/*");

    server.setHandler(handler);
    server.start();

    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(false), new ObjectName("purej.vminspect", "id", "1"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect", "id", "2"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=my Type,id=12"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=myType,spaces=a b c"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=myType,sonderzeichen='äöü';"));
  }
}
