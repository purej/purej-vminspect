// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Example MBean with a lot of types.
 *
 * @author Stefan Mueller
 */
public class MySample implements MySampleMBean {
  private char _characterP;
  private Character _characterO;
  private boolean _booleanP;
  private Boolean _booleanO;
  private byte _byteP;
  private Byte _byteO;
  private short _shortP;
  private Short _shortO;
  private int _integerP;
  private Integer _integerO;
  private long _longP;
  private Long _longO;
  private float _floatP;
  private Float _floatO;
  private double _doubleP;
  private Double _doubleO;
  private BigDecimal _bigDecimal;
  private String _string;
  private String[] _stringArray;
  private List<byte[]> _allocated = new ArrayList<byte[]>();

  /**
   * Creates a new instance of this class.
   */
  public MySample(boolean initialize) {
    if (initialize) {
      _characterP = 'c';
      _characterO = Character.valueOf('x');
      _booleanP = true;
      _booleanO = Boolean.TRUE;
      _byteP = 12;
      _byteO = Byte.valueOf((byte) -12);
      _shortP = 1234;
      _shortO = Short.valueOf((short) -1234);
      _integerP = 123456;
      _integerO = Integer.valueOf(-123456);
      _longP = 1234567890L;
      _longO = Long.valueOf(-1234567890L);
      _floatP = 12.25f;
      _floatO = Float.valueOf(-12.25f);
      _doubleP = 1234.56789;
      _doubleO = Double.valueOf(-1234.56789);
      _bigDecimal = new BigDecimal("123456.78909");
      _string = "my string value";
      _stringArray = new String[] {"string1", "purej.vminspect:type=my Type,id=12", null, "my.objectName:type=Abc", "string3"};
    }
  }

  @Override
  public char getCharacterP() {
    return _characterP;
  }

  @Override
  public void setCharacterP(char inCharacterP) {
    _characterP = inCharacterP;
  }

  @Override
  public Character getCharacterO() {
    return _characterO;
  }

  @Override
  public void setCharacterO(Character inCharacterO) {
    _characterO = inCharacterO;
  }

  @Override
  public boolean isBooleanP() {
    return _booleanP;
  }

  @Override
  public void setBooleanP(boolean inBooleanP) {
    _booleanP = inBooleanP;
  }

  @Override
  public Boolean getBooleanO() {
    return _booleanO;
  }

  @Override
  public void setBooleanO(Boolean inBooleanO) {
    _booleanO = inBooleanO;
  }

  @Override
  public byte getByteP() {
    return _byteP;
  }

  @Override
  public void setByteP(byte inByteP) {
    _byteP = inByteP;
  }

  @Override
  public Byte getByteO() {
    return _byteO;
  }

  @Override
  public void setByteO(Byte inByteO) {
    _byteO = inByteO;
  }

  @Override
  public short getShortP() {
    return _shortP;
  }

  @Override
  public void setShortP(short inShortP) {
    _shortP = inShortP;
  }

  @Override
  public Short getShortO() {
    return _shortO;
  }

  @Override
  public void setShortO(Short inShortO) {
    _shortO = inShortO;
  }

  @Override
  public int getIntegerP() {
    return _integerP;
  }

  @Override
  public void setIntegerP(int inIntegerP) {
    _integerP = inIntegerP;
  }

  @Override
  public Integer getIntegerO() {
    return _integerO;
  }

  @Override
  public void setIntegerO(Integer inIntegerO) {
    _integerO = inIntegerO;
  }

  @Override
  public long getLongP() {
    return _longP;
  }

  @Override
  public void setLongP(long inLongP) {
    _longP = inLongP;
  }

  @Override
  public Long getLongO() {
    return _longO;
  }

  @Override
  public void setLongO(Long inLongO) {
    _longO = inLongO;
  }

  @Override
  public float getFloatP() {
    return _floatP;
  }

  @Override
  public void setFloatP(float inFloatP) {
    _floatP = inFloatP;
  }

  @Override
  public Float getFloatO() {
    return _floatO;
  }

  @Override
  public void setFloatO(Float inFloatO) {
    _floatO = inFloatO;
  }

  @Override
  public double getDoubleP() {
    return _doubleP;
  }

  @Override
  public void setDoubleP(double inDoubleP) {
    _doubleP = inDoubleP;
  }

  @Override
  public Double getDoubleO() {
    return _doubleO;
  }

  @Override
  public void setDoubleO(Double inDoubleO) {
    _doubleO = inDoubleO;
  }

  @Override
  public BigDecimal getBigDecimal() {
    return _bigDecimal;
  }

  @Override
  public void setBigDecimal(BigDecimal inBigDecimal) {
    _bigDecimal = inBigDecimal;
  }

  @Override
  public String getString() {
    return _string;
  }

  @Override
  public void setString(String inString) {
    _string = inString;
  }

  @Override
  public String[] getStringArray() {
    return _stringArray;
  }

  @Override
  public void setStringArray(String[] inStringArray) {
    _stringArray = inStringArray;
  }

  // ========================================
  // Methods
  // ========================================

  @Override
  public void myVoidMethod() {
    System.out.println("MySample - myVoidMethod()");
  }

  @Override
  public void myVoidMethod(String inValue) {
    System.out.println("MySample - myVoidMethod(" + inValue + ")");
  }

  @Override
  public void myVoidMethod(String inValue1, String inValue2, String inValue3) {
    System.out.println("MySample - myVoidMethod(" + inValue1 + "," + inValue2 + "," + inValue3 + ")");
  }

  @Override
  public char echoChar(char inValue) {
    return inValue;
  }

  @Override
  public byte echoByte(byte inValue) {
    return inValue;
  }

  @Override
  public Integer echoInteger(Integer inValue) {
    return inValue;
  }

  @Override
  public BigDecimal echoBigDecimal(BigDecimal inValue) {
    return inValue;
  }

  @Override
  public String[] echoStringArray(String[] inValue) {
    return inValue;
  }

  @Override
  public void allocateMemory(int mbs) {
    _allocated.add(new byte[mbs * 1024 * 1024]);
  }

  @Override
  public void freeLastAllocated() {
    if (_allocated.size() > 0) {
      _allocated.remove(_allocated.size() - 1);
    }
  }
}
