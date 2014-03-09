package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class SqlStatementVisitor extends SQLParserBaseVisitor<SqlStatement> {
  private SqlStatement m_ssStatement = new SqlStatement();

  @Override public SqlStatement visitSql(@NotNull SQLParser.SqlContext ctx) {
    return visitChildren(ctx);
  }

  @Override public SqlStatement visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    m_ssStatement.setSelect(new SelectVisitor().visit(ctx));
    return m_ssStatement;
  }

  @Override public SqlStatement visitFrom_clause(@NotNull SQLParser.From_clauseContext ctx) {
    m_ssStatement.setFrom(new FromVisitor().visit(ctx));
    return m_ssStatement;
  }
}
