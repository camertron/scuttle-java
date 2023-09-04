package com.camertron.Scuttle;

// Distinguish between strings and numerics, but treat them all as strings
// under the hood.
public class NumericOperand extends StringOperand {
    public NumericOperand(String sValue) {
        super(sValue);
    }

    public static NumericOperand fromString(String sValue) {
        return new NumericOperand(sValue);
    }

    @Override
    public OperandType getType() {
        return OperandType.NUMERIC;
    }
}
