package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class WhereVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sWhereClause;
  private String m_sFromTableName;

  public WhereVisitor(String sFromTableName) {
    super();
    m_sFromTableName = sFromTableName;
  }

  @Override public Void visitWhere_clause(@NotNull SQLParser.Where_clauseContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_sFromTableName);
    veVisitor.visitChildren(ctx);
    m_sWhereClause = veVisitor.toString();
    return null;
  }

  public String toString() {
    // format the operand for the case of: WHERE 1
    return ExpressionUtils.formatOperand(m_sWhereClause, false);
  }
}
