package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;

public class WhereVisitor extends ScuttleBaseVisitor {
  private String m_sWhereClause;

  public WhereVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, ScuttleOptions sptOptions) {
    super(fmFromVisitor, arResolver, sptOptions);
  }

  @Override public Void visitWhere_clause(@NotNull SQLParser.Where_clauseContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    veVisitor.visitChildren(ctx);
    m_sWhereClause = veVisitor.toString();
    return null;
  }

  public String toString() {
    // format the operand for the case of: WHERE 1
    return ExpressionUtils.formatOperand(m_sWhereClause, false, m_sptOptions);
  }
}
