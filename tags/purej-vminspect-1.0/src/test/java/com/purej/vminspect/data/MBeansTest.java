// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests some MBeans functionality.
 *
 * @author Stefan Mueller
 */
public class MBeansTest {

  /**
   * Tests the named functionality.
   */
  @Test
  public void testMBeanServers() {
    for (MBeanServer server : MBeanServerFactory.findMBeanServer(null)) {
      System.out.println("MBeanServer: " + server.toString());
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testMBeanName() {
    for (MBeanName name : MBeanUtils.getMBeanNames()) {
      String objectName = name.getObjectNameString();
      System.out.println("MBean objectName: " + objectName);
      MBeanData mbean = MBeanUtils.getMBean(0, objectName);
      Assert.assertEquals(objectName, mbean.getName().getObjectNameString());
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testMBeanAttributes() {
    for (MBeanName name : MBeanUtils.getMBeanNames()) {
      String objectName = name.getObjectNameString();
      MBeanData mbean = MBeanUtils.getMBean(0, objectName);
      System.out.println();
      System.out.println("Attributes for MBean : " + objectName);
      for (MBeanAttribute attribute : mbean.getAttributes()) {
        System.out.println("Attribute " + attribute.getName() + "=" + attribute.getValue() + " (" + attribute.getDescription() + ")");
      }
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testGetSetMBeanAttributes() throws Exception {
    String name = "purej.vminspect:type=MySample";
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(false), new ObjectName(name));
    try {
      doTestGetSetAttribute(name, "CharacterP", "a", "b");
      doTestGetSetAttribute(name, "CharacterO", "a", "b");
      doTestGetSetAttribute(name, "BooleanP", "true", "false");
      doTestGetSetAttribute(name, "BooleanO", "true", "false");
      doTestGetSetAttribute(name, "ByteP", "123", "-123");
      doTestGetSetAttribute(name, "ByteO", "22", "-22");
      doTestGetSetAttribute(name, "ShortP", "1234", "-1234");
      doTestGetSetAttribute(name, "ShortO", "567", "567");
      doTestGetSetAttribute(name, "IntegerP", "123456", "-123456");
      doTestGetSetAttribute(name, "IntegerO", "56789", "-56789");
      doTestGetSetAttribute(name, "LongP", "123123", "-132123");
      doTestGetSetAttribute(name, "LongO", "43254325", "-1243234");
      doTestGetSetAttribute(name, "FloatP", "23.44", "-23.66");
      doTestGetSetAttribute(name, "FloatO", "3245.677", "123.77");
      doTestGetSetAttribute(name, "DoubleP", "214354.7456", "-2143435.0");
      doTestGetSetAttribute(name, "DoubleO", "234.345", "-456.88");
      doTestGetSetAttribute(name, "BigDecimal", "12343.778", "2143.5436");
      doTestGetSetAttribute(name, "String", "abc def", " xx yy zz ");
    }
    finally {
      ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(name));
    }
  }

  /**
   * Tests the named functionality.
   */
  @Test
  public void testInvokeOperation() throws Exception {
    String name = "purej.vminspect:type=MySample";
    ManagementFactory.getPlatformMBeanServer().registerMBean(new MySample(false), new ObjectName(name));
    try {
      doTestInvokeOperation(name, "myVoidMethod", new String[] {}, null);
      doTestInvokeOperation(name, "myVoidMethod", new String[] {null}, null);
      doTestInvokeOperation(name, "myVoidMethod", new String[] {"bla"}, null);
      doTestInvokeOperation(name, "myVoidMethod", new String[] {null, null, null}, null);
      doTestInvokeOperation(name, "myVoidMethod", new String[] {"bla", "", "blu"}, null);
      doTestInvokeOperation(name, "myVoidMethod", new String[] {"bla", "bli", "blu"}, null);
      doTestInvokeOperation(name, "echoChar", new String[] {"a"}, "a");
      doTestInvokeOperation(name, "echoByte", new String[] {"12"}, "12");
      doTestInvokeOperation(name, "echoInteger", new String[] {null}, null);
      doTestInvokeOperation(name, "echoInteger", new String[] {""}, null);
      doTestInvokeOperation(name, "echoInteger", new String[] {"-12345"}, "-12345");
      doTestInvokeOperation(name, "echoBigDecimal", new String[] {null}, null);
      doTestInvokeOperation(name, "echoBigDecimal", new String[] {""}, null);
      doTestInvokeOperation(name, "echoBigDecimal", new String[] {"22.334455"}, "22.334455");
    }
    finally {
      ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(name));
    }
  }

  private static void doTestGetSetAttribute(String objectName, String attributeName, String value1, String value2) {
    // Load & set value1:
    MBeanData mbean = MBeanUtils.getMBean(0, objectName);
    Assert.assertNotNull(mbean);
    MBeanUtils.invokeAttribute(mbean, mbean.getAttribute(attributeName), value1);

    // Reload & set value2:
    mbean = MBeanUtils.getMBean(0, objectName);
    Assert.assertNotNull(mbean);
    Object v = mbean.getAttribute(attributeName).getValue();
    Assert.assertEquals(value1, v != null ? v.toString() : null);
    MBeanUtils.invokeAttribute(mbean, mbean.getAttribute(attributeName), value2);

    // Reload & check:
    mbean = MBeanUtils.getMBean(0, objectName);
    Assert.assertNotNull(mbean);
    v = mbean.getAttribute(attributeName).getValue();
    Assert.assertEquals(value2, v != null ? v.toString() : null);

    // For complex objects:
    if (mbean.getAttribute(attributeName).getType().contains(".")) {
      MBeanUtils.invokeAttribute(mbean, mbean.getAttribute(attributeName), null);
      mbean = MBeanUtils.getMBean(0, objectName);
      Assert.assertNotNull(mbean);
      Assert.assertEquals(null, mbean.getAttribute(attributeName).getValue());
    }
  }

  private static void doTestInvokeOperation(String objectName, String operationName, String[] params, String expectedResult) {
    // Load & invoke:
    MBeanData mbean = MBeanUtils.getMBean(0, objectName);
    Assert.assertNotNull(mbean);
    Object result = MBeanUtils.invokeOperation(mbean, getOperation(mbean, operationName, params.length), params);
    Assert.assertEquals(expectedResult, result != null ? result.toString() : null);
  }

  private static MBeanOperation getOperation(MBeanData mbean, String operationName, int paramCount) {
    for (MBeanOperation operation : mbean.getOperations()) {
      if (operation.getName().equals(operationName) && operation.getParameters().length == paramCount) {
        return operation;
      }
    }
    throw new IllegalArgumentException("Operation not found!");
  }
}
