// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import com.purej.vminspect.data.MBeanOperation.Impact;
import com.purej.vminspect.util.Utils;

/**
 * Helper class for MBean operations.
 *
 * @author Stefan Mueller
 */
public final class MBeanUtils {
  private static final String JAVA_LANG = "java.lang.";
  private static final int JAVA_LANG_LENGTH = JAVA_LANG.length();

  private MBeanUtils() {
  }

  /**
   * Returns the parameter type to be displayed (eg. without java.lang).
   */
  public static String toDisplayType(String type) {
    if (type != null && type.startsWith(JAVA_LANG)) {
      return type.substring(JAVA_LANG_LENGTH);
    }
    return type;
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
    Collections.sort(result, new MBeanNameComparator());
    return result;
  }

  /**
   * Returns the MBean for the given object name or null if the MBean could not be found.
   */
  public static MBeanData getMBean(MBeanName mbName) {
    try {
      MBeanServer server = getMBeanServer(mbName.getServerIdx());
      MBeanInfo mbeanInfo = server.getMBeanInfo(mbName.getObjectName());

      // The set of attribute getter/setters to hide redundant operations
      // later on - this is required as some frameworks (for example Spring)
      // exposes Attributes as operations too (which makes it redundant)...
      Map<String, String> getters = new HashMap<String, String>();
      Map<String, String> setters = new HashMap<String, String>();

      // Create the list of attributes:
      MBeanAttributeInfo[] attributeInfos = mbeanInfo.getAttributes();
      MBeanAttribute[] attributes = new MBeanAttribute[attributeInfos.length];
      for (int i = 0; i < attributes.length; i++) {
        MBeanAttributeInfo info = attributeInfos[i];
        if (info.isReadable()) {
          getters.put(info.isIs() ? "is" + info.getName() : "get" + info.getName(), info.getType());
        }
        if (info.isWritable()) {
          setters.put("set" + info.getName(), info.getType());
        }
        attributes[i] = createAttribute(server, mbName.getObjectName(), info);
      }
      Arrays.sort(attributes, new MBeanAttributeComparator());

      // Create the list of operations:
      MBeanOperationInfo[] operationInfos = mbeanInfo.getOperations();
      List<MBeanOperation> ops = new ArrayList<MBeanOperation>(operationInfos.length);
      for (int i = 0; i < operationInfos.length; i++) {
        MBeanOperationInfo info = operationInfos[i];
        if (!isGetter(getters, info) && !isSetter(setters, info)) {
          ops.add(createOperation(info));
        }
      }
      MBeanOperation[] operations = ops.toArray(new MBeanOperation[ops.size()]);
      Arrays.sort(operations, new MBeanOperationComparator());

      return new MBeanData(mbName, mbeanInfo.getDescription(), attributes, operations);
    }
    catch (Exception e) {
      throw new RuntimeException("MBean with name '" + mbName + "' could not be resolved!", e);
    }
  }

  /**
   * Returns the MBean for the given object name or null if the MBean could not be found.
   */
  public static MBeanData getMBean(int mbServerIdx, String mbName) {
    try {
      return getMBean(new MBeanName(mbServerIdx, new ObjectName(mbName)));
    }
    catch (MalformedObjectNameException e) {
      throw new RuntimeException("MBean with name '" + mbName + "' could not be resolved!", e);
    }
  }

  /**
   * Sets the given attribute of the specified MBean to the passed value.
   */
  public static Object invokeAttribute(MBeanName mbeanName, MBeanAttribute attribute, String value) {
    ObjectName objectName = mbeanName.getObjectName();
    Object objectValue = toTypedValue(value, attribute.getType());
    Attribute attr = new Attribute(attribute.getName(), objectValue);
    try {
      getMBeanServer(mbeanName.getServerIdx()).setAttribute(objectName, attr);
    }
    catch (Exception e) {
      throw new RuntimeException("An error occurred setting attribute '" + attribute.getName() + "' to value '" + value + "'!", e);
    }
    return convertValueIfNeeded(objectValue);
  }

  /**
   * Invokes the given operation and returns the result.
   */
  public static Object invokeOperation(MBeanName mbeanName, MBeanOperation operation, String[] parameterValues) {
    MBeanParameter[] params = operation.getParameters();
    if (params.length != parameterValues.length) {
      throw new RuntimeException("Parameter count missmatch!");
    }
    ObjectName objectName = mbeanName.getObjectName();
    try {
      Object[] typedParams = new Object[params.length];
      String[] types = new String[params.length];
      for (int i = 0; i < parameterValues.length; i++) {
        typedParams[i] = toTypedValue(parameterValues[i], params[i].getType());
        types[i] = params[i].getType();
      }
      Object result = getMBeanServer(mbeanName.getServerIdx()).invoke(objectName, operation.getName(), typedParams, types);
      return convertValueIfNeeded(result);
    }
    catch (Exception e) {
      throw new RuntimeException("An error occurred invoking operation '" + operation.getName() + "'!", e);
    }
  }

  /**
   * Gets the list of all MBean servers on this platform.
   */
  public static List<MBeanServer> getMBeanServers() {
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
        // Skip wrapper exception (MBeanException, ReflectionException) as it contains all info twice:
        value = "Exception reading attribute value: " + Utils.getExceptionInfo(e.getCause() != null ? e.getCause() : e);
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

    Impact impact;
    switch (operationInfo.getImpact()) {
    case MBeanOperationInfo.INFO:
      impact = Impact.Info;
      break;
    case MBeanOperationInfo.ACTION:
      impact = Impact.Action;
      break;
    case MBeanOperationInfo.ACTION_INFO:
      impact = Impact.ActionInfo;
      break;
    default:
      impact = Impact.Unknown;
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
      Map<String, Object> map = new TreeMap<String, Object>();
      for (String key : data.getCompositeType().keySet()) {
        map.put(key, convertValueIfNeeded(data.get(key)));
      }
      return map;
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
    else if (value instanceof Map) {
      Map<Object, Object> map = new HashMap<Object, Object>();  
      for (Map.Entry<?, ?>entry : ((Map<?,?>) value).entrySet()) {
        map.put(convertValueIfNeeded(entry.getKey()), convertValueIfNeeded(entry.getValue()));
      }
      return map;
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
