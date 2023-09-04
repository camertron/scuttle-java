package com.camertron.Scuttle;

public interface Operand {
  public enum OperandType {
    COLUMN,
    STRING,
    NUMERIC,
    EXPRESSION
  }

  public String toString();
  public OperandType getType();
}
