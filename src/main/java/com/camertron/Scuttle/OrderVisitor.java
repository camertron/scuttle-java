package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class OrderVisitor extends SQLParserBaseVisitor<Void> {
  private TerminalNodeImpl m_tniOrder;
  private ColumnVisitor m_cvColumn;
  private String m_sExpression;
  private FromVisitor m_fmFromVisitor;

  public OrderVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitSort_specifier(@NotNull SQLParser.Sort_specifierContext ctx) {
    m_cvColumn = new ColumnVisitor(m_fmFromVisitor);
    m_cvColumn.visit(ctx);
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitOrder_specification(@NotNull SQLParser.Order_specificationContext ctx) {
    m_tniOrder = (TerminalNodeImpl)ctx.children.get(0);
    return null;
  }

  @Override public Void visitRoutine_invocation(@NotNull SQLParser.Routine_invocationContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
    veVisitor.visit(ctx);
    m_sExpression = veVisitor.toString();
    return null;
  }

  @Override public Void visitNonparenthesized_value_expression_primary(@NotNull SQLParser.Nonparenthesized_value_expression_primaryContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
    veVisitor.visit(ctx);
    m_sExpression = veVisitor.toString();
    return null;
  }

  public boolean isReverseOrder() {
    if (m_tniOrder == null) {
      return false;
    } else {
      return m_tniOrder.symbol.getType() == SQLParser.DESC;
    }
  }

  public boolean hasExpression() { return m_sExpression != null; }
  public String getColumn() {
    return m_cvColumn.toString();
  }
  public String getExpression() { return m_sExpression; }
  public String getQualifiedColumn(String sTableName) {
    return m_cvColumn.toString(sTableName);
  }
}
