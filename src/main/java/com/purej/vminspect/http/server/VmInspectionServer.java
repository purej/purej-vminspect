// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.data.statistics.StatisticsCollector;
import com.purej.vminspect.http.RequestController;
import com.purej.vminspect.http.servlet.DefaultMBeanAccessControlFactory;
import com.purej.vminspect.http.servlet.MBeanAccessControlFactory;

/**
 * This standalone server allows PureJ VM Inspection to be used without a servlet-container or other type of
 * web-server. It implements a very basic and lightweight HTTP server that handles only HTTP/1.0/1.1 GET/PUT requests
 * used by the VM Inspection functionality.
 * <p/>
 * Note: This class starts some listener threads and opens a server-socket so it should NEVER be used inside JEE
 * application server containers!
 * <p/>
 * Note: This server is very basic and maybe NOT secure enough, if security is required it is recommended to use
 * the VmInspectionServlet with one of the professional servlet-containers (Jetty, Tomcat, etc.)
 * <p/>
 * Beside the HTTP server socket, some more attributes can be configured when creating an instance of this class.
 * See the javadoc of the different constructors for details.
 *
 * @author Stefan Mueller
 */
public final class VmInspectionServer {
  private static final Logger LOGGER = LoggerFactory.getLogger(VmInspectionServer.class);
  private final ExecutorService executor;
  private final ServerSocket serverSocket;
  private final Thread listener;
  private final StatisticsCollector collector;
  private final RequestController controller;

  /**
   * Creates a new instance of this very basic HTTP server.
   * @param port the port where the server-socket listens for incoming HTTP requests
   * @throws IOException if the server socket could not be bound to the given port
   */
  public VmInspectionServer(int port) throws IOException {
    this(null, false, false, 60000, null, port);
  }

  /**
   * Creates a new instance of this very basic HTTP server. See the class javadoc for further argument details.
   *
   * @param defaultDomainFilter the default mbeans domain filter of no cookie value is given
   * @param mbeansReadonly if MBeans should be accessed read-only
   * @param mbeansWriteConfirmation if MBeans write operations require a confirmation screen
   * @param statisticsCollectionFrequencyMs the statistics collection frequency in milliseconds (60'000 recommended)
   * @param statisticsStorageDir the optional statistics storage directory
   * @param port the port where the server-socket listens for incoming HTTP requests
   * @throws IOException if the server socket could not be bound to the given port
   */
  public VmInspectionServer(String defaultDomainFilter, boolean mbeansReadonly, boolean mbeansWriteConfirmation, int statisticsCollectionFrequencyMs,
      String statisticsStorageDir, int port) throws IOException {
    this(new DefaultMBeanAccessControlFactory(defaultDomainFilter, mbeansReadonly, mbeansWriteConfirmation), statisticsCollectionFrequencyMs, statisticsStorageDir, port);
  }

  /**
   * Creates a new instance of this very basic HTTP server. See the class javadoc for further argument details.
   *
   * @param mBeanAccessControlFactory defines fine-grained access control to MBeans
   * @param statisticsCollectionFrequencyMs the statistics collection frequency in milliseconds (60'000 recommended)
   * @param statisticsStorageDir the optional statistics storage directory
   * @param port the port where the server-socket listens for incoming HTTP requests
   * @throws IOException if the server socket could not be bound to the given port
   */
  public VmInspectionServer(MBeanAccessControlFactory mBeanAccessControlFactory, int statisticsCollectionFrequencyMs, String statisticsStorageDir, int port)
      throws IOException {
    // Create the executor to handle request:
    executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
      @Override
      public Thread newThread(Runnable target) {
        return new Thread(target, "VmInspect-Request-Executor");
      }
    });

    // Open the server-socket:
    serverSocket = new ServerSocket(port, 10);

    // Create the listener thread:
    listener = new Thread("VmInspect-Http-Listener") {
      @Override
      public void run() {
        while (!serverSocket.isClosed()) {
          try {
            Socket socket = serverSocket.accept();
            executor.execute(new RequestExecutor(socket, controller));
          }
          catch (Exception e) {
            if (serverSocket.isClosed()) {
              break;
            }
            LOGGER.error("An error occurred accepting incomming HTTP connection!", e);
          }
        }
      }
    };
    listener.setDaemon(true);

    // Get or create collector, create controller and startup:
    collector = StatisticsCollector.init(statisticsStorageDir, statisticsCollectionFrequencyMs, this);
    controller = new RequestController(mBeanAccessControlFactory, collector);
    listener.start();
  }

  /**
   * Initiates an orderly shutdown in which previously accepted HTTP request are executed but no new incoming
   * requests will be accepted. The {@link StatisticsCollector} will be stopped as well if this instance was
   * the last reference to it.
   */
  public void shutdown() {
    StatisticsCollector.destroy(this);
    try {
      serverSocket.close();
    }
    catch (Exception e) {
      // Ignored...
    }
    executor.shutdown();
  }
}
