package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class FromVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sTableName;

  @Override public Void visitTable_primary(@NotNull SQLParser.Table_primaryContext ctx) {
    m_sTableName = ctx.table_or_query_name().getText();
    return null;
  }

  public String getTableName() {
    return Inflector.dePluralize(m_sTableName);
  }
}
