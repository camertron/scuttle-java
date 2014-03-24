package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class FromVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sTableName;
  private ArrayList<JoinVisitor> m_alJoins = new ArrayList<JoinVisitor>();

  @Override public Void visitTable_primary(@NotNull SQLParser.Table_primaryContext ctx) {
    m_sTableName = ctx.table_or_query_name().getText();
    return null;
  }

  @Override public Void visitJoined_table_primary(@NotNull SQLParser.Joined_table_primaryContext ctx) {
    JoinVisitor jnVisitor = new JoinVisitor(getTableName());
    jnVisitor.visit(ctx);
    m_alJoins.add(jnVisitor);
    return null;
  }

  public String getTableName() {
    return Utils.camelize(Inflector.dePluralize(m_sTableName));
  }

  public ArrayList<JoinVisitor> getJoins() {
    return m_alJoins;
  }
}
