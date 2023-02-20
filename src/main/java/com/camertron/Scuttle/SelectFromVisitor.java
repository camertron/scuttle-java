package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class SelectFromVisitor extends SQLParserBaseVisitor<Void> {
  private FromVisitor m_fmFromVisitor;
  private AssociationResolver m_arResolver;
  private ScuttleOptions m_sptOptions;
  private ArrayList<JoinVisitor> m_alJoins = new ArrayList<JoinVisitor>();
  private SelectVisitor m_svSelectVisitor;
  private SQLParser.Select_listContext m_slSelectList;
  private String m_sWhereClause = "";
  private OrderByVisitor m_obOrderByVisitor;
  private String m_sGroupByClause = "";
  private String m_sLimitClause = "";
  private String m_sHavingClause = "";
  private boolean m_bDistinct = false;

  public SelectFromVisitor(AssociationResolver arResolver, ScuttleOptions sptOptions) {
    m_arResolver = arResolver;
    m_sptOptions = sptOptions;
  }

  @Override public Void visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    m_slSelectList = ctx;
    return null;
  }

  @Override public Void visitSet_qualifier(@NotNull SQLParser.Set_qualifierContext ctx) {
    if (ctx.DISTINCT() != null) {
      m_bDistinct = true;
    }

    visitChildren(ctx);
    return null;
  }

  @Override public Void visitFrom_clause(@NotNull SQLParser.From_clauseContext ctx) {
    FromVisitor fmVisitor = new FromVisitor(m_arResolver, m_sptOptions);
    fmVisitor.visit(ctx);
    m_fmFromVisitor = fmVisitor;
    m_alJoins.addAll(fmVisitor.getJoins());

    // now that we've visited the FROM clause we can visit the column list
    SelectVisitor selVisitor = new SelectVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    selVisitor.visit(m_slSelectList);
    m_svSelectVisitor = selVisitor;

    return null;
  }

  @Override public Void visitWhere_clause(@NotNull SQLParser.Where_clauseContext ctx) {
    WhereVisitor whVisitor = new WhereVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    whVisitor.visit(ctx);
    m_sWhereClause = whVisitor.toString();
    return null;
  }

  @Override public Void visitOrderby_clause(@NotNull SQLParser.Orderby_clauseContext ctx) {
    OrderByVisitor obVisitor = new OrderByVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    obVisitor.visit(ctx);
    m_obOrderByVisitor = obVisitor;
    return null;
  }

  @Override public Void visitGroupby_clause(@NotNull SQLParser.Groupby_clauseContext ctx) {
    GroupByVisitor gbVisitor = new GroupByVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    gbVisitor.visit(ctx);
    m_sGroupByClause = gbVisitor.toString();
    return null;
  }

  @Override public Void visitLimit_clause(@NotNull SQLParser.Limit_clauseContext ctx) {
    LimitVisitor lmVisitor = new LimitVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    lmVisitor.visit(ctx);
    m_sLimitClause = lmVisitor.toString();
    return null;
  }

  @Override public Void visitHaving_clause(@NotNull SQLParser.Having_clauseContext ctx) {
    HavingVisitor haVisitor = new HavingVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    haVisitor.visit(ctx);
    m_sHavingClause = haVisitor.toString();
    return null;
  }

  public String toString() {
    ArrayList<String> alStatements = new ArrayList<String>();
    String sQuery = m_fmFromVisitor.getModelName() + ".select(" + m_svSelectVisitor.toString() + ")";

    if (m_fmFromVisitor.hasSubquery()) {
      sQuery += ".from(" + m_fmFromVisitor.getSubquery();
      sQuery += ".as(" + Utils.quote(m_fmFromVisitor.getSubqueryIdentifier()) + "))";
    }

    if (!m_sWhereClause.equals("")) {
      sQuery += ".where(" + m_sWhereClause + ")";
    }

    if (!m_sHavingClause.equals("")) {
      sQuery += ".having(" + m_sHavingClause + ")";
    }

    sQuery += composeJoins();
    sQuery += composeOrderBy();

    if (!m_sGroupByClause.equals("")) {
      sQuery += ".group(" + m_sGroupByClause + ")";
    }

    if (m_fmFromVisitor.hasSubquery()) {
      String sTableRef = m_fmFromVisitor.getTableRef();
      alStatements.add(
        sTableRef + " = Arel::Table.new(" + Utils.quote(sTableRef) + ")"
      );
    }

    if (!m_sLimitClause.equals("")) {
      sQuery += ".limit(" + m_sLimitClause + ")";
    }

    if (m_bDistinct) {
      if (m_sptOptions.getRailsVersion().greaterThanOrEqualTo("4.0.0")) {
        sQuery += ".distinct";
      } else {
        sQuery += ".uniq";
      }
    }

    alStatements.add(sQuery);
    return Utils.join(alStatements, "\n");
  }

  public String getFromRef() {
    return m_fmFromVisitor.getTableRef();
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
          if (order.hasExpression()) {
            alOrderStrings.add(order.getExpression() + ".desc");
          } else {
            alOrderStrings.add(order.getQualifiedColumn(getFromRef()) + ".desc");
          }
        } else {
          if (order.hasExpression()) {
            alOrderStrings.add(order.getExpression());
          } else {
            alOrderStrings.add(order.getColumn());
          }
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

  private String composeJoins() {
    JoinManager jmJoinManager = new JoinManager(m_alJoins, m_arResolver);
    return jmJoinManager.toString();
  }
}
