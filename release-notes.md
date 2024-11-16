Version "current"
- Upgraded to spring 3.3 and latest other dependencies
- MBeanAccessControl allows setting a default domain filter

Version 2.0.0 - (04.03.2023)
- Upgraded to jakarta-servlet 6.0.0 which renames javax.servlet to jakarta.servlet packages

Version 1.9.1 - (04.03.2023)
- Fix incorrect parent-pom version
- Upgraded to spring-boot 2.7.6 and matching slf4j/logback

Version 1.9.0 - (04.03.2023)
- Updated all dependencies and plugins to latest versions
- Added garbage collector names to main page
- More compact html output by using better stylesheets (hovers, odd/even row-selection)
- No more inline-styles and inline-javascript to allow using a very strict CSP policy

Version 1.8.0 - (04.07.2021)
 - Upgraded dependencies to latest versions
 - Prevent ClassNotFoundException if running on VMs without access to sun-classes (eg. com.sun.management.OperatingSystemMXBean)

Version 1.7.0 - (02.03.2021)
 - Upgraded dependencies to latest versions
 - Support for enums-types in MBean attributes/operations
 - Better support for spring-boot apps by providing the VmInspectRegistrationBean class

Version 1.6.1 - (01.12.2019)
 - Decrease system-startup times by initializing the underlying files and collection lazily

Version 1.6.0 - (26.05.2019)
 - Statistics keeps the RRD database open to prevent creating / garbage-collecting alot of objects
 - Changed RRD provider from JRobin to RRD4J as of lower memory- and CPU consumption

Version 1.5.0 - (12.12.2018)
 - Removed memory-estimate for in-memory statistics collections as of problems with java 11 (restricted reflection access)
 - Changed the way of accessing hidden memory and CPU data as of problems with java 11 (restricted reflection access)
 - Replaced some methods deprecated with java 9 and onwards

Version 1.4.1 - (12.11.2018)
 - Upgrade to latest jrobin with fixed unit-dependency scope

Version 1.4.0 - (04.10.2018)
 - Requires jdk 1.8
 - Added automatic-module-name to manifest for java 9 and higher support
 - Using newer version of jrobin to prevent errors on java 9 and higher

Version 1.3.0 - (06.12.2017)
 - Moved from google-code to github, changed repository URLs, upgraded parent pom
 - Better handling of MBean attributes of type Map (eg. displayed as multi-line key=value pairs)
 - Upgraded to latest slf4j
 - Redundant MBean operations that are exposed as attributes too will be ignored (cause Spring JMX exposes attributes as operations too)
 - More flexible MBean type extraction from key=value name part (for example if not first pair or if not lower-case)

Version 1.2.9 - (29.03.2015)
 - More stable exception handling in case of browser connection aborts

Version 1.2.8 - (26.10.2014)
 - Some display optimizations (for example display java.lang types without java.lang)
 - VmInspectionServlet is not final anymore to allow subclassing

Version 1.2.7 - (18.09.2014)
 - System properties sorted by alphabet
 - Display hostname / IP on system page
 - Display open/max file-descriptors on systen-page and statistics (available only on unix-platforms)
 - Optimized exception handling for some cases, little layout optimizations

Version 1.2.5 - (13.03.2014)
 - MBean write operations use HTTP POST instead of GET to prevent duplicate actions in case of browser-refreshes
 - Removed explicit garbage collector call before statistics measure as it produced high and unwanted CPU load on some platforms/VMs

Version 1.2.4 - (08.03.2014)
 - MBean attribute-change/operation-call exceptions are displayed inside the MBean view with a red block instead of the general white error page
 - New callback methods on the MBeanAccessControl interface to treat attribute-change/operation-call exceptions

Version 1.2.3 - (27.02.2014)
 - MBean sorted by additional key/values if both domain-name and types are equal
 - Display total CPU and garbage collector times in seconds instead of millis

Version 1.2.2 - (10.02.2014)
 - New MBeanAccessControl interfaces for fine-grained control of MBean access
 - New MBeanAccessControlFactory to create custom MBeanAccessControl for each !HttpServletRequest
 - Little optimizations of MBean confirm messages
 - Bugfix: Prevent !NullpointerException if servlet-container returns null as cookie list

Version 1.2.0 - (20.01.2014)
 - MBean write confirmation screen can be configured (default is no confirmation screen to be displayed)
 - Better replace for apos (&#39;) as of browser compatibility issues


Version 1.1 - (04.01.2014)
Changes impacting users:
 - *IMPORTANT:* Moved and renamed servlet to _com.purej.vminspect.http.servlet.!VmInspectionServlet_
 - New class !VmInspectionServer for standalone usage without having to use a servlet-container
 - Users can add their own statistics by implementing the new !ValueProvider interface and registering it at the !StatisticsCollector bug fixes and internal optimizations:
 - Cookie values are encoded/decoded to support non-http conform characters in searches
 - !StatisticsCollector ensures that there is only one instance running per VM/classloader (also if multiple servlet-instances are used)
 - Statistics ranges (UI choices) are preserved even if cookies are disabled
 - Showing the converted value in the status-message after setting an attribute instead of the original value
 - Clean separation between http-handling (package .http) and html-producing (package .html)
 - Lots more unit-test for higher coverage
 - Improved Javadoc
 - Utility methods moved to util package to clean up package dependencies (no more cycles)


Version 1.0 - 31.12.2013
 - No programmatic changes, but switched to Sonatype-Maven repository and officially released to Maven Central


Version 0.6 - 29.12.2013
 - Moved to code.google.com, added licensing
 - System-view shows commited memory in addition to used/max - memory-bar became two-colored
 - System-view shows the CPU load and total CPU time of the VM process
 - Corrected MBeans wild-card search to correctly apply wildcards
 - Threads-view shows the peek-lives and totally started threads
 - Statistics is optionally hold in-memory if no statistics-storage directory has been configured
 - Statistics detail view can be zoomed in and out and the size is preserved when switching period
 - Checked all HTML output to be W3C-compliant
 - Reduced java-script complexity, removed unused scripts
 - OperatingSystemMXBean.getSystemLoadAverage() did not work on many platforms and has been replaced by getSystemCpuLoad() 
 - {{{AuthorizationCallback}}} removed again, authorization should be implemented using a custom servlet filter if required


Version 0.4 - 22.12.2013
 - Added logging with slf4j
 - MBean access can be restricted to read-only by setting the servlet-param 'vminspect.mbeans.readonly' to true
 - {{{AuthorizationCallback}}} ifc that can be configured by setting the servlet-param 'vminspect.authorization.callback' to a custom class
 - Lots of internal refactoring without impact to users
 - Lots of javadoc and checkstyle-rules applied


Version 0.2 - 17.11.2013
 - First usable version
