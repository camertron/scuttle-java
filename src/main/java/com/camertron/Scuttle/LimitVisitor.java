package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class LimitVisitor extends SQLParserBaseVisitor<Void> {
  FromVisitor m_fmFromVisitor;
  String m_sExpression = "";

  public LimitVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitNumeric_value_expression(@NotNull SQLParser.Numeric_value_expressionContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
    veVisitor.visit(ctx);
    m_sExpression = ExpressionUtils.formatOperand(veVisitor.toString(), false);
    return null;
  }

  public String toString() {
    return m_sExpression;
  }
}
