package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class WhereVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sWhereClause;
  private FromVisitor m_fmFromVisitor;

  public WhereVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitWhere_clause(@NotNull SQLParser.Where_clauseContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
    veVisitor.visitChildren(ctx);
    m_sWhereClause = veVisitor.toString();
    return null;
  }

  public String toString() {
    // format the operand for the case of: WHERE 1
    return ExpressionUtils.formatOperand(m_sWhereClause, false);
  }
}
