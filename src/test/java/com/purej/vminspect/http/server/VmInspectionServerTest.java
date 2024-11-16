// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.server;

import java.lang.management.ManagementFactory;
import javax.management.ObjectName;

import org.junit.jupiter.api.Test;

import com.purej.vminspect.data.MySample;

/**
 * Tests the named functionality.
 *
 * @author Stefan Mueller
 */
public class VmInspectionServerTest {

  /**
   * Starts the {@link VmInspectionServer} on port 8080 for testing with a browser.
   */
  public static void main(String[] args) throws Exception {
    VmInspectionServer server = new VmInspectionServer(false, true, 60000, null, 8080);

    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(false), new ObjectName("purej.vminspect", "id", "1"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect", "id", "2"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=my Type,id=12"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=myType,spaces=a b c"));
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(true), new ObjectName("purej.vminspect:type=myType,sonderzeichen='äöü';"));

    System.out.println("VmInspectionServer started, check-out http://localhost:8080/inspect");
    Thread.sleep(Integer.MAX_VALUE);
    server.shutdown();
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testStartStop() throws Exception {
    VmInspectionServer server = new VmInspectionServer(12345);
    server.shutdown();
  }
}
