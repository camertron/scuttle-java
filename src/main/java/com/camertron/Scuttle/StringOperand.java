package com.camertron.Scuttle;

public class StringOperand implements Operand {
  private String m_sValue;

  public static StringOperand fromString(String sValue) {
    return new StringOperand(sValue);
  }

  public StringOperand(String sValue) {
    m_sValue = sValue;
  }

  public String toString() {
    return m_sValue;
  }

  @Override
  public OperandType getType() {
    return OperandType.STRING;
  }
}
