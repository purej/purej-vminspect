<h1><img src="https://cloud.githubusercontent.com/assets/13910123/9428032/6939119e-499b-11e5-89d5-78ba70cc989b.png"/> VM Inspection</h1>

**PureJ VM Inspection** offers an easy to use, feature-rich, JMX-based and embeddable Java VM monitoring tool with a web-based user-interface. 

The goal is to monitor and control any Java application in production environments without the overhead and security-risks of an external JMX console. This works with Spring-Boot, Java-EE or any other Java-App which supports the servlet-spec. *PureJ VM Inspection* is easy to configure, requires minimal dependencies, is extremely lightweight (no profiling, no databases) and offers the following features:
  * **System**: memory-status, VM- and OS details, class-loader details
  * **MBeans**: Browse all MBeans, show and change attributes, invoke operations
  * **Threads**: Live threads overview with current status, stacktrace and total CPU/user times
  * **Statistics**: graphical memory-, threads-, and load-statistics with chooseable periods

For **spring-boot** apps, add the *VmInspectRegistrationBean* to your *@SpringBootApplication* or any other *@Configuration* class:

```
  @Bean
  public VmInspectRegistrationBean inspect() {
    return new VmInspectRegistrationBean();
  }
```


Other **web-apps** can add the servlet to their _web.xml_:

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

Optional properties might be set to configure some aspects (either as spring-environment properties or servlet-init-parameters):

| Property | Default Value | Description |
|----|----|----|
| vminspect.path | /inspect/* | Only for *spring-boot* - Specifies the path to serve the VM inspection pages |
| vminspect.mbeans.readonly | false | Specifies if VmInspect is allowed to edit MBean values or invoke non-info operations |
| vminspect.mbeans.writeConfirmation | false | Specifies if a confirmation screen is displayed before edit MBean attributes or invoke MBean operations |
| vminspect.mbeans.accessControlFactory | none | Fully qualified class name of an implementation of the {@link MBeanAccessControlFactory} interface |
| vminspect.statistics.collection.frequencyMs | 60'000 | Number of milliseconds for the statistics collection timer |
| vminspect.statistics.storage.dir | none | Optional Path where to store the statistics files. If no storage directory is configured, the statistics will be kept in-memory and thus will be lost after a VM restart. |

### Requisites

  * Attention: The latest inspect-version 2.0.0 supports spring-boot 3 and uses jakarta.servlet and no more javax.servlet (latest jakarta-servlet spec 6.0) - For javax.servlet support, use the 1.9 version or older
  * Java 17 or higher (required by jakarta-servlet / spring-boot 3)

Maven users just need to add the following dependency:

```
  <dependency>
    <groupId>com.purej</groupId>
    <artifactId>purej-vminspect</artifactId>
    <version>2.0.0</version>
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
