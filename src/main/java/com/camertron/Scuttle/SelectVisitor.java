package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class SelectVisitor extends ScuttleBaseVisitor {
  private SQLParser.Select_listContext m_slcSelectList;

  public SelectVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver) {
    super(fmFromVisitor, arResolver);
  }

  @Override public Void visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    m_slcSelectList = ctx;
    return null;
  }

  public String toString(FromVisitor fmFromVisitor) {
    ArrayList<String> alColumns = new ArrayList<String>();

    for(ColumnVisitor colVisitor : getColumns(fmFromVisitor)) {
      alColumns.add(colVisitor.toString());
    }

    return Utils.commaize(alColumns);
  }

  private ArrayList<ColumnVisitor> getColumns(FromVisitor fmFromVisitor) {
    ArrayList<ColumnVisitor> alColumns = new ArrayList<ColumnVisitor>();

    for (SQLParser.Select_sublistContext sublistItemContext : m_slcSelectList.select_sublist()) {
      ColumnVisitor colVisitor = new ColumnVisitor(fmFromVisitor, m_arResolver);
      colVisitor.visit(sublistItemContext);
      alColumns.add(colVisitor);
    }

    return alColumns;
  }
}
