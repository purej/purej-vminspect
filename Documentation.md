<p align='center'>
<img src='https://purej-vminspect.googlecode.com/svn/wiki/pics/purej-logo-48.png' />
<font size='20'>VM Inspection</font><br />
<a href='https://code.google.com/p/purej-vminspect/'>Home</a> | <a href='Documentation.md'>Documentation</a> | <a href='ReleaseNotes.md'>ReleaseNotes</a> | <a href='Downloads.md'>Downloads</a> | <a href='License.md'>License</a>**</p>**

# Purpose #
JMX is a great and simple approach to manage enterprise applications in production environment. Unfortunately, the JDK lacks a lightweight tool to access those information securely without the need for a complex infrastructure.

_PureJ VM Inspect_ can be embedded in any Java / Java-EE application and provides remote JMX management features over a HTTP / web-based user-interface.

# Requisites #

  * Java 1.6+
  * slf4j for logging (Version 1.7.5, see [slf4j.org](http://www.slf4j.org/download.html))
  * JRobin for statistics graphics (Version 1.5.9, see [JRobin on Sourceforge](http://sourceforge.net/projects/jrobin))

Maven users just need to add the following dependency, it will download the required libraries automatically:

```
  <dependency>
    <groupId>com.purej</groupId>
    <artifactId>purej-vminspect</artifactId>
    <version>1.2.9</version>
  </dependency>
```

# Enabling VM Inspection #

## Web-Applications ##

Add the following config to the _web.xml_:

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


## Non-Web-Applications ##

For non-webapps, VM inspection can be enabled using the internal lightweight standalone HTTP server which requires no external dependencies:

```
  new VmInspectionServer(8080);
```

Note that this server-implementation is very basic and might not be secure enough for certain usages. If security and more configuration options are required, the recommended way is the use the VM inspection servlet with one of the many powerful embeddable web-servers out there (for example [Jetty](http://www.eclipse.org/jetty)):

```
  Server server = new Server(8080);
  ServletContextHandler handler = new ServletContextHandler(server, "/inspect");
  ServletHolder servletHolder = new ServletHolder(VmInspectionServlet.class);
  servletHolder.setInitOrder(1);
  handler.addServlet(servletHolder, "/*");
  server.start();
```


# Configuring VM Inspection #
VM Inspection supports a few configuration parameters. All of them provide a default value so they are optional to be configured.

| **Servlet Init Parameter** | **Description** | **Default** |
|:---------------------------|:----------------|:------------|
|vminspect.mbeans.readonly   |true/false, specifies if MBeans should only be accessed read-only|false        |
|vminspect.mbeans.writeConfirmation|true/false, specifies if MBeans write operations require a confirmation screen|false        |
|vminspect.mbeans.accessControlFactory|optional full class name of an MBeanAccessControlFactory interface implementation|null         |
|vminspect.statistics.collection.frequencyMs|Number of milliseconds for the statistics collection timer|60000        |
|vminspect.statistics.storage.dir|Optional path where to store the statistics files or null if no storage required|null         |

**Note**: A custom MBeanAccessControlFactory might be used if fine-grained control of MBean access is required, but for many cases it might be sufficient to just configure the _readonly_ and _writeConfirmation_ properties.

**Note**: If no statistics storage-path is specified, the statistics will be kept only in memory and thus will be lost after a VM restart.

**Example configuration for web-apps**
```
  <servlet>
    <servlet-name>vminspect</servlet-name>
    <servlet-class>com.purej.vminspect.http.servlet.VmInspectionServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
    <init-param>
      <param-name>vminspect.mbeans.readonly</param-name>
      <param-value>true</param-value>
    </init-param>
    <init-param>
      <param-name>vminspect.statistics.storage.dir</param-name>
      <param-value>/home/users/john/my-app-statistics</param-value>
    </init-param>
  </servlet>
```

**Example configuration for non-web-apps using the VmInspectionServer**
```
  new VmInspectionServer(false, true, 60000, "/home/users/john/my-app-statistics", 8080);
```

**Example configuration for non-web-apps using an embedded Jetty**
```
  servletHolder.setInitParameter("vminspect.mbeans.readonly", "true");
  servletHolder.setInitParameter("vminspect.statistics.storage.dir", "/home/users/john/my-app-statistics");
```