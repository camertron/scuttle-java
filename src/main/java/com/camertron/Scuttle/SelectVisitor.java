package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;

public class SelectVisitor extends SQLParserBaseVisitor<Void> {
  private ArrayList<String> m_alColumns = new ArrayList<String>();
  private String m_sFromTableName;

  public SelectVisitor(String sFromTableName) {
    super();
    m_sFromTableName = sFromTableName;
  }

  @Override public Void visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    for (SQLParser.Select_sublistContext sublistItemContext : ctx.select_sublist()) {
      ColumnVisitor scVisitor = new ColumnVisitor(m_sFromTableName);
      scVisitor.visit(sublistItemContext);
      m_alColumns.add(scVisitor.toString());
    }

    return null;
  }

  public String toString() {
    return Utils.commaize(m_alColumns);
  }
}
