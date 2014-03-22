package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class SqlStatementVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sFromTable = "";
  private String m_sSelect = "";

  @Override public Void visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    SelectVisitor selVisitor = new SelectVisitor();
    selVisitor.visit(ctx);
    m_sSelect = selVisitor.toString();
    return null;
  }

  @Override public Void visitFrom_clause(@NotNull SQLParser.From_clauseContext ctx) {
    FromVisitor fmVisitor = new FromVisitor();
    fmVisitor.visit(ctx);
    m_sFromTable = fmVisitor.getTableName();
    return null;
  }

  public String toString() {
    return Utils.camelize(m_sFromTable) + ".select(" + m_sSelect + ")";
  }
}
