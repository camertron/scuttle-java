package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class SelectVisitor extends ScuttleBaseVisitor {
  private SQLParser.Select_listContext m_slcSelectList;
  private ArrayList<ColumnVisitor> m_alColumns;

  public SelectVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, ScuttleOptions sptOptions) {
    super(fmFromVisitor, arResolver, sptOptions);
  }

  @Override public Void visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    m_slcSelectList = ctx;
    m_alColumns = new ArrayList<ColumnVisitor>();

    for (SQLParser.Select_sublistContext sublistItemContext : m_slcSelectList.select_sublist()) {
      ColumnVisitor colVisitor = new ColumnVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
      colVisitor.visit(sublistItemContext);
      m_alColumns.add(colVisitor);
    }

    return null;
  }

  public String toString() {
    ArrayList<String> alColumns = new ArrayList<String>();

    for(ColumnVisitor colVisitor : m_alColumns) {
      alColumns.add(colVisitor.toString());
    }

    return Utils.singletonArrayFormat(alColumns);
  }
}
