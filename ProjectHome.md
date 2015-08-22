<p align='center'>
<img src='https://purej-vminspect.googlecode.com/svn/wiki/pics/purej-logo-48.png' />
<font size='20'>VM Inspection</font><br />
<a href='https://code.google.com/p/purej-vminspect/'>Home</a> | <a href='Documentation.md'>Documentation</a> | <a href='ReleaseNotes.md'>ReleaseNotes</a> | <a href='Downloads.md'>Downloads</a> | <a href='License.md'>License</a>**</p>**

**PureJ VM Inspection** offers an easy to use, feature-rich, JMX-based and embeddable Java VM monitoring tool with a web-based user-interface.

The goal is to monitor and control Java and Java-EE applications in production environments without the overhead and security-risks of an external JMX console. **PureJ VM Inspection** is easy to configure, requires minimal dependencies, is extremly lightweight (no profiling, no databases) and offers the following features:
  * **System**: memory-status, VM- and OS details, class-loader details
  * **MBeans**: Browse all MBeans, show and change attributes, invoke operations
  * **Threads**: Live threads overview with current status, stacktrace and total CPU/user times
  * **Statistics**: graphical memory-, threads-, and load-statistics with chooseable periods

Existing **web applications** can just add the servlet to their _web.xml_:

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

### Screenshots ###
**System View**
![https://purej-vminspect.googlecode.com/svn/wiki/screenshots/system-view.gif](https://purej-vminspect.googlecode.com/svn/wiki/screenshots/system-view.gif)

**Threads View**
![https://purej-vminspect.googlecode.com/svn/wiki/screenshots/threads-view.gif](https://purej-vminspect.googlecode.com/svn/wiki/screenshots/threads-view.gif)

**Statistics View**
![https://purej-vminspect.googlecode.com/svn/wiki/screenshots/statistics-view.gif](https://purej-vminspect.googlecode.com/svn/wiki/screenshots/statistics-view.gif)

**Statistics Detail View**
![https://purej-vminspect.googlecode.com/svn/wiki/screenshots/statistics-details-view.gif](https://purej-vminspect.googlecode.com/svn/wiki/screenshots/statistics-details-view.gif)

[More Screenshots](Screenshots.md)