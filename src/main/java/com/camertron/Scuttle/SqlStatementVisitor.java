package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import com.sun.deploy.association.Association;
import org.antlr.v4.runtime.misc.NotNull;

public class SqlStatementVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sStatement;
  private AssociationResolver m_arResolver;
  private ScuttleOptions m_sptOptions;

  public SqlStatementVisitor(AssociationResolver arResolver, ScuttleOptions sptOptions) {
    m_arResolver = arResolver;
    m_sptOptions = sptOptions;
  }

  @Override public Void visitData_statement(@NotNull SQLParser.Data_statementContext ctx) {
    SelectFromVisitor sfVisitor = new SelectFromVisitor(m_arResolver, m_sptOptions);
    sfVisitor.visit(ctx);
    m_sStatement = sfVisitor.toString();
    return null;
  }

  @Override public Void visitInsert_statement(@NotNull SQLParser.Insert_statementContext ctx) {
    InsertVisitor insVisitor = new InsertVisitor(m_arResolver, m_sptOptions);
    insVisitor.visit(ctx);
    m_sStatement = insVisitor.toString();
    return null;
  }

  public String toString() {
    return m_sStatement;
  }
}
