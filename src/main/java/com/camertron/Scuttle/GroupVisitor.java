package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;

public class GroupVisitor extends ScuttleBaseVisitor {
  private ColumnVisitor m_cvColumn;
  private String m_sExpression;

  public GroupVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, ScuttleOptions sptOptions) {
    super(fmFromVisitor, arResolver, sptOptions);
  }

  @Override public Void visitColumn_reference(@NotNull SQLParser.Column_referenceContext ctx) {
    m_cvColumn = new ColumnVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    m_cvColumn.visit(ctx);
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitRoutine_invocation(@NotNull SQLParser.Routine_invocationContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    veVisitor.visit(ctx);
    m_sExpression = veVisitor.toString();
    return null;
  }

  @Override public Void visitNonparenthesized_value_expression_primary(@NotNull SQLParser.Nonparenthesized_value_expression_primaryContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
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
