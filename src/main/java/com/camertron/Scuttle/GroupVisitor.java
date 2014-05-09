package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class GroupVisitor extends SQLParserBaseVisitor<Void> {
  private FromVisitor m_fmFromVisitor;
  private ColumnVisitor m_cvColumn;
  private String m_sExpression;

  public GroupVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitColumn_reference(@NotNull SQLParser.Column_referenceContext ctx) {
    m_cvColumn = new ColumnVisitor(m_fmFromVisitor);
    m_cvColumn.visit(ctx);
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitRoutine_invocation(@NotNull SQLParser.Routine_invocationContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
    veVisitor.visit(ctx);
    m_sExpression = veVisitor.toString();
    return null;
  }

  public String toString() {
    if (m_cvColumn == null) {
      return m_sExpression;
    } else {
      return m_cvColumn.toString();
    }
  }
}
