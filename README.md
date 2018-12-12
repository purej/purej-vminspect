<h1><img src="https://cloud.githubusercontent.com/assets/13910123/9428032/6939119e-499b-11e5-89d5-78ba70cc989b.png"/> VM Inspection</h1>

**PureJ VM Inspection** offers an easy to use, feature-rich, JMX-based and embeddable Java VM monitoring tool with a web-based user-interface. 

The goal is to monitor and control Java and Java-EE applications in production environments without the overhead and security-risks of an external JMX console. *PureJ VM Inspection* is easy to configure, requires minimal dependencies, is extremly lightweight (no profiling, no databases) and offers the following features:
  * **System**: memory-status, VM- and OS details, class-loader details
  * **MBeans**: Browse all MBeans, show and change attributes, invoke operations
  * **Threads**: Live threads overview with current status, stacktrace and total CPU/user times
  * **Statistics**: graphical memory-, threads-, and load-statistics with chooseable periods

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

Optional properties might be set to configure some aspects:

| Property | Default Value | Description |
|----|----|----|
| vminspect.mbeans.readonly | false | Specifies if VmInspect is allowed to edit MBean values or invoke non-info operations |
| vminspect.mbeans.writeConfirmation | false | Specifies if a confirmation screen is displayed before edit MBean attributes or invoke MBean operations |
| vminspect.mbeans.accessControlFactory | none | Fully qualified class name of an implementation of the {@link MBeanAccessControlFactory} interface |
| vminspect.statistics.collection.frequencyMs | 60'000 | Number of milliseconds for the statistics collection timer |
| vminspect.statistics.storage.dir | none | Optional Path where to store the statistics files. If no storage directory is configured, the statistics will be kept in-memory and thus will be lost after a VM restart. |

### Requisites

  * Java 1.8 or higher

Maven users just need to add the following dependency:

```
  <dependency>
    <groupId>com.purej</groupId>
    <artifactId>purej-vminspect</artifactId>
    <version>1.5.0</version>
  </dependency>
```


## Screenshots

### System View
![system-view](https://cloud.githubusercontent.com/assets/13910123/9428035/72318b14-499b-11e5-889f-dcb97b33dd6a.gif)

### Threads View
![threads-view](https://cloud.githubusercontent.com/assets/13910123/9428036/723203dc-499b-11e5-89ee-285803d97e99.gif)

### Statistics View
![statistics-view](https://cloud.githubusercontent.com/assets/13910123/9428037/723220c4-499b-11e5-9a5c-342cefba10cb.gif)

### Statistics Detail View
![statistics-detail-view](https://cloud.githubusercontent.com/assets/13910123/9428041/7246b85e-499b-11e5-8a1c-91ecde920c8d.gif)

### MBean View
![mbean-view](https://cloud.githubusercontent.com/assets/13910123/9428039/7234d94a-499b-11e5-9510-65a72f5098a7.gif)

### MBean Detail View
![mbean-detail-view](https://cloud.githubusercontent.com/assets/13910123/9428038/7232c77c-499b-11e5-94cf-4ffc3ed87df6.gif)
