package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class SqlStatementVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sStatement;

  @Override public Void visitData_statement(@NotNull SQLParser.Data_statementContext ctx) {
    SelectFromVisitor sfVisitor = new SelectFromVisitor();
    sfVisitor.visit(ctx);
    m_sStatement = sfVisitor.toString();
    return null;
  }

  @Override public Void visitInsert_statement(@NotNull SQLParser.Insert_statementContext ctx) {
    InsertVisitor insVisitor = new InsertVisitor();
    insVisitor.visit(ctx);
    m_sStatement = insVisitor.toString();
    return null;
  }

  public String toString() {
    return m_sStatement;
  }
}
