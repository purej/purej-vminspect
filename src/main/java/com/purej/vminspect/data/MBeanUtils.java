// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

/**
 * Helper class for MBean operations.
 *
 * @author Stefan Mueller
 */
public final class MBeanUtils {

  private MBeanUtils() {
  }

  /**
   * Tries to find the MBean server that knows the given object name.
   *
   * @return the index or -1 if no MBean servers knows this object
   */
  public static int getMBeanServerIdx(ObjectName objectName) {
    List<MBeanServer> servers = getMBeanServers();
    for (int i = 0; i < servers.size(); i++) {
      if (servers.get(i).isRegistered(objectName)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns a list of all MBeans from all MBean servers.
   */
  public static List<MBeanName> getMBeanNames() {
    List<MBeanName> result = new ArrayList<MBeanName>(30);
    List<MBeanServer> servers = getMBeanServers();
    for (int i = 0; i < servers.size(); i++) {
      Set<ObjectName> objectNames = servers.get(i).queryNames(null, null);
      for (ObjectName name : objectNames) {
        result.add(new MBeanName(i, name));
      }
    }
    Collections.sort(result, MBeanName.COMPARATOR);
    return result;
  }

  /**
   * Returns the MBean for the given object name or null if the MBean could not be found.
   */
  public static MBeanData getMBean(int mbServerIdx, String mbName) {
    try {
      ObjectName name = new ObjectName(mbName);
      MBeanServer server = getMBeanServer(mbServerIdx);
      MBeanInfo mbeanInfo = server.getMBeanInfo(name);

      // Create the list of attributes:
      MBeanAttributeInfo[] attributeInfos = mbeanInfo.getAttributes();
      MBeanAttribute[] attributes = new MBeanAttribute[attributeInfos.length];
      for (int i = 0; i < attributes.length; i++) {
        attributes[i] = createAttribute(server, name, attributeInfos[i]);
      }
      Arrays.sort(attributes, MBeanAttribute.COMPARATOR);

      // Create the list of operations:
      MBeanOperationInfo[] operationInfos = mbeanInfo.getOperations();
      MBeanOperation[] operations = new MBeanOperation[operationInfos.length];
      for (int i = 0; i < operationInfos.length; i++) {
        operations[i] = createOperation(operationInfos[i]);
      }
      Arrays.sort(operations, MBeanOperation.COMPARATOR);

      return new MBeanData(new MBeanName(mbServerIdx, name), mbeanInfo.getDescription(), attributes, operations);
    }
    catch (Exception e) {
      throw new RuntimeException("MBean with name '" + mbName + "' could not be resolved!");
    }
  }

  /**
   * Sets the given attribute of the specified MBean to the passed value.
   */
  public static void invokeAttribute(MBeanData mbean, MBeanAttribute attribute, String value) {
    ObjectName objectName = mbean.getName().getObjectName();
    Attribute attr = new Attribute(attribute.getName(), toTypedValue(value, attribute.getType()));
    try {
      getMBeanServer(mbean.getName().getServerIdx()).setAttribute(objectName, attr);
    }
    catch (Exception e) {
      throw new RuntimeException("An error occurred setting attribute '" + attribute.getName() + "' to value '" + value + "'!", e);
    }
  }

  /**
   * Invokes the given operation and returns the result.
   */
  public static Object invokeOperation(MBeanData mbean, MBeanOperation operation, String[] parameterValues) {
    MBeanParameter[] params = operation.getParameters();
    if (params.length != parameterValues.length) {
      throw new RuntimeException("Parameter count missmatch!");
    }
    ObjectName objectName = mbean.getName().getObjectName();
    try {
      Object[] typedParams = new Object[params.length];
      String[] types = new String[params.length];
      for (int i = 0; i < parameterValues.length; i++) {
        typedParams[i] = toTypedValue(parameterValues[i], params[i].getType());
        types[i] = params[i].getType();
      }
      Object result = getMBeanServer(mbean.getName().getServerIdx()).invoke(objectName, operation.getName(), typedParams, types);
      return convertValueIfNeeded(result);
    }
    catch (Exception e) {
      throw new RuntimeException("An error occurred invoking operation '" + operation.getName() + "'!", e);
    }
  }

  private static List<MBeanServer> getMBeanServers() {
    try {
      // Make sure platform server is created!
      ManagementFactory.getPlatformMBeanServer();
      return MBeanServerFactory.findMBeanServer(null);
    }
    catch (Exception e) {
      throw new RuntimeException("Could not access MBean servers!", e);
    }
  }

  private static MBeanServer getMBeanServer(int mbServerIdx) {
    return getMBeanServers().get(mbServerIdx);
  }

  private static MBeanAttribute createAttribute(MBeanServer server, ObjectName name, MBeanAttributeInfo attributeInfo) {
    Object value = null;
    if (attributeInfo.isReadable()) {
      try {
        Object o = server.getAttribute(name, attributeInfo.getName());
        value = convertValueIfNeeded(o);
      }
      catch (Exception e) {
        value = "Exception reading attribute value: " + e.toString();
      }
    }

    // Convert arrays to readable stuff:
    String type = getTypeDescription(attributeInfo.getType());
    return new MBeanAttribute(attributeInfo.getName(), value, getDescription(attributeInfo), type, attributeInfo.isWritable());
  }

  private static MBeanOperation createOperation(MBeanOperationInfo operationInfo) {
    // Create parameter & return types:
    MBeanParameter[] parameters = new MBeanParameter[operationInfo.getSignature().length];
    for (int i = 0; i < operationInfo.getSignature().length; i++) {
      MBeanParameterInfo p = operationInfo.getSignature()[i];
      parameters[i] = new MBeanParameter(p.getName(), getTypeDescription(p.getType()), getDescription(p));
    }
    String returnType = getTypeDescription(operationInfo.getReturnType());

    String impact;
    switch (operationInfo.getImpact()) {
    case MBeanOperationInfo.INFO:
      impact = "Info";
      break;
    case MBeanOperationInfo.ACTION:
      impact = "Action";
      break;
    case MBeanOperationInfo.ACTION_INFO:
      impact = "Action/Info";
      break;
    default:
      impact = "Unknown";
    }
    return new MBeanOperation(operationInfo.getName(), parameters, returnType, impact, getDescription(operationInfo));
  }

  private static String getDescription(MBeanFeatureInfo element) {
    String desc = element.getDescription();
    return desc != null && desc.equals(element.getName()) ? null : desc; // Remove silly descriptions
  }

  private static String getTypeDescription(String type) {
    return type.startsWith("[L") && type.endsWith(";") ? type.substring(2, type.length() - 1) + "[]" : type;
  }

  private static Object convertValueIfNeeded(Object value) {
    if (value == null) {
      return null;
    }
    else if (value instanceof CompositeData) {
      CompositeData data = (CompositeData) value;
      Map<String, Object> values = new TreeMap<String, Object>();
      for (String key : data.getCompositeType().keySet()) {
        values.put(key, convertValueIfNeeded(data.get(key)));
      }
      return values;
    }
    else if (value.getClass().isArray()) {
      int length = Array.getLength(value);
      List<Object> list = new ArrayList<Object>(length);
      for (int i = 0; i < length; i++) {
        list.add(convertValueIfNeeded(Array.get(value, i)));
      }
      return list;
    }
    else if (value instanceof TabularData) {
      TabularData tabularData = (TabularData) value;
      return convertValueIfNeeded(tabularData.values());
    }
    else if (value instanceof Collection) {
      List<Object> list = new ArrayList<Object>();
      for (Object data : (Collection<?>) value) {
        list.add(convertValueIfNeeded(data));
      }
      return list;
    }
    return value;
  }

  private static Object toTypedValue(String value, String type) {
    try {
      if (value == null || value.length() == 0) {
        return null;
      }
      else if (type.equals("java.lang.String")) {
        return value;
      }
      else if (type.equals("java.lang.Character") || type.equals("char")) {
        return new Character(value.charAt(0));
      }
      else if (type.equals("java.lang.Boolean") || type.equals("boolean")) {
        return Boolean.valueOf(value);
      }
      else if (type.equals("java.lang.Byte") || type.equals("byte")) {
        return new Byte(value);
      }
      else if (type.equals("java.lang.Short") || type.equals("short")) {
        return new Short(value);
      }
      else if (type.equals("java.lang.Integer") || type.equals("int")) {
        return new Integer(value);
      }
      else if (type.equals("java.lang.Long") || type.equals("long")) {
        return new Long(value);
      }
      else if (type.equals("java.lang.Float") || type.equals("float")) {
        return new Float(value);
      }
      else if (type.equals("java.lang.Double") || type.equals("double")) {
        return new Double(value);
      }
      else if (type.equals("java.math.BigDecimal")) {
        return new BigDecimal(value);
      }
      throw new UnsupportedOperationException("Type '" + type + "' is currently not supported!");
    }
    catch (Exception e) {
      throw new IllegalArgumentException("The value '" + value + "' could not be converted to type '" + type + "'!", e);
    }
  }
}
