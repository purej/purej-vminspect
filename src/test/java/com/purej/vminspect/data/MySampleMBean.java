// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.math.BigDecimal;

/**
 * Example MBean with a lot of types.
 *
 * @author Stefan Mueller
 */
public interface MySampleMBean {
  // CHECKSTYLE:OFF

  // ========================================
  // Attributes
  // ========================================

  char getCharacterP();

  void setCharacterP(char value);

  Character getCharacterO();

  void setCharacterO(Character value);

  boolean isBooleanP();

  void setBooleanP(boolean value);

  Boolean getBooleanO();

  void setBooleanO(Boolean value);

  byte getByteP();

  void setByteP(byte value);

  Byte getByteO();

  void setByteO(Byte value);

  short getShortP();

  void setShortP(short value);

  Short getShortO();

  void setShortO(Short value);

  int getIntegerP();

  void setIntegerP(int value);

  Integer getIntegerO();

  void setIntegerO(Integer value);

  long getLongP();

  void setLongP(long value);

  Long getLongO();

  void setLongO(Long value);

  float getFloatP();

  void setFloatP(float value);

  Float getFloatO();

  void setFloatO(Float value);

  double getDoubleP();

  void setDoubleP(double value);

  Double getDoubleO();

  void setDoubleO(Double value);

  String getString();

  void setString(String value);

  BigDecimal getBigDecimal();

  void setBigDecimal(BigDecimal value);

  String[] getStringArray();

  void setStringArray(String[] value);

  // ========================================
  // Methods
  // ========================================

  void myVoidMethod();

  void myVoidMethod(String value);

  void myVoidMethod(String value1, String value2, String value3);

  char echoChar(char value);

  byte echoByte(byte value);

  Integer echoInteger(Integer value);

  BigDecimal echoBigDecimal(BigDecimal value);

  String echoString(String value);

  String[] echoStringArray(String[] value);

  void allocateMemory(int mbs);

  void freeLastAllocated();
}
