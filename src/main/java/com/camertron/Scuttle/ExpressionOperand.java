package com.camertron.Scuttle;

public class ExpressionOperand extends StringOperand {
    public ExpressionOperand(String sValue) {
        super(sValue);
    }

    public static ExpressionOperand fromString(String sValue) {
        return new ExpressionOperand(sValue);
    }

    public OperandType getType() {
        return OperandType.EXPRESSION;
    }
}
