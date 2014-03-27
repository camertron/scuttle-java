package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class SelectVisitor extends SQLParserBaseVisitor<Void> {
  private ArrayList<String> m_alColumns = new ArrayList<String>();
  private FromVisitor m_fmFromVisitor;

  public SelectVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    for (SQLParser.Select_sublistContext sublistItemContext : ctx.select_sublist()) {
      ColumnVisitor scVisitor = new ColumnVisitor(m_fmFromVisitor);
      scVisitor.visit(sublistItemContext);
      m_alColumns.add(scVisitor.toString());
    }

    return null;
  }

  public String toString() {
    return Utils.commaize(m_alColumns);
  }
}
