package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class SqlStatementVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sFromTable = "";
  private ArrayList<JoinVisitor> m_alJoins = new ArrayList<JoinVisitor>();
  private String m_sSelect = "";
  private String m_sWhereClause = "";
  private OrderByVisitor m_obOrderByVisitor = null;

  @Override public Void visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    SelectVisitor selVisitor = new SelectVisitor(m_sFromTable);
    selVisitor.visit(ctx);
    m_sSelect = selVisitor.toString();
    return null;
  }

  @Override public Void visitFrom_clause(@NotNull SQLParser.From_clauseContext ctx) {
    FromVisitor fmVisitor = new FromVisitor();
    fmVisitor.visit(ctx);
    m_sFromTable = fmVisitor.getTableName();
    m_alJoins.addAll(fmVisitor.getJoins());
    return null;
  }

  @Override public Void visitWhere_clause(@NotNull SQLParser.Where_clauseContext ctx) {
    WhereVisitor whVisitor = new WhereVisitor(m_sFromTable);
    whVisitor.visit(ctx);
    m_sWhereClause = whVisitor.toString();
    return null;
  }

  @Override public Void visitOrderby_clause(@NotNull SQLParser.Orderby_clauseContext ctx) {
    OrderByVisitor obVisitor = new OrderByVisitor(m_sFromTable);
    obVisitor.visit(ctx);
    m_obOrderByVisitor = obVisitor;
    return null;
  }

  public String toString() {
    String sQuery = m_sFromTable + ".select(" + m_sSelect + ")";

    if (!m_sWhereClause.equals("")) {
      sQuery += ".where(" + m_sWhereClause + ")";
    }

    if (!m_alJoins.isEmpty()) {
      for(JoinVisitor join : m_alJoins) {
        sQuery += ".joins(" + join.toString() + ".join_sources)";
      }
    }

    sQuery += composeOrderBy();
    return sQuery;
  }

  private String composeOrderBy() {
    if (m_obOrderByVisitor == null) {
      return "";
    } else {
      boolean bAllReverseOrder = m_obOrderByVisitor.isReverseOrder();
      ArrayList<String> alOrderStrings = new ArrayList<String>();

      for(OrderVisitor order : m_obOrderByVisitor.getOrders()) {
        // Arel default is ASC, so we don't need any additional function call
        // like we do for DESC.
        if (!bAllReverseOrder && order.isReverseOrder()) {
          // Get qualified column by passing in a default table (the FROM table).
          // The column must be qualified because we're calling .desc() on it.
          alOrderStrings.add(order.getQualifiedColumn(m_sFromTable) + ".desc");
        } else {
          alOrderStrings.add(order.getColumn());
        }
      }

      String sQuery = ".order(" + Utils.commaize(alOrderStrings) + ")";

      // If all the orders are DESCs, we can just call .reverse_order - much cleaner
      // than calling .desc() on each individual order.
      if (bAllReverseOrder) {
        return sQuery + ".reverse_order";
      }

      return sQuery;
    }
  }
}
