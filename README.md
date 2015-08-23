<h1><img src="https://cloud.githubusercontent.com/assets/13910123/9427455/ac87593c-497e-11e5-806c-beac4cc50ae3.png"/> VM Inspection</h1>

**PureJ VM Inspection** offers an easy to use, feature-rich, JMX-based and embeddable Java VM monitoring tool with a web-based user-interface. 

The goal is to monitor and control Java and Java-EE applications in production environments without the overhead and security-risks of an external JMX console. *PureJ VM Inspection* is easy to configure, requires minimal dependencies, is extremly lightweight (no profiling, no databases) and offers the following features:
  * System: memory-status, VM- and OS details, class-loader details
  * MBeans: Browse all MBeans, show and change attributes, invoke operations
  * Threads: Live threads overview with current status, stacktrace and total CPU/user times
  * Statistics: graphical memory-, threads-, and load-statistics with chooseable periods

Existing *web applications* can just add the servlet to their _web.xml_:

```
  <servlet>
    <servlet-name>vminspect</servlet-name>
    <servlet-class>com.purej.vminspect.http.servlet.VmInspectionServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>vminspect</servlet-name>
    <url-pattern>/inspect/*</url-pattern>
  </servlet-mapping>      
```

For **non-webapps**, VM inspection can be enabled using the internal lightweight standalone HTTP server:

```
  new VmInspectionServer(8080);
```

Alternatively, **non-webapps**, can also use the VM inspection servlet with an embeddable web-server such as for example [Jetty](http://www.eclipse.org/jetty):

```
  Server server = new Server(8080);
  ServletContextHandler handler = new ServletContextHandler(server, "/inspect");
  ServletHolder servletHolder = new ServletHolder(VmInspectionServlet.class);
  servletHolder.setInitOrder(1);
  handler.addServlet(servletHolder, "/*");
  server.start();
```

#Screenshots
...
