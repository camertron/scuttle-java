package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class FromVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sTableName;
  private String m_sSubqueryIdentifier;
  private SelectFromVisitor m_stmtSubquery;
  private ScuttleOptions m_sptOptions;
  private ArrayList<JoinVisitor> m_alJoins;
  private AssociationResolver m_arResolver;

  public FromVisitor(AssociationResolver arResolver, ScuttleOptions sptOptions) {
    super();
    m_arResolver = arResolver;
    m_alJoins = new ArrayList<JoinVisitor>();
    m_sptOptions = sptOptions;
  }

  public FromVisitor(String sTableName, ScuttleOptions sptOptions) {
    super();
    m_sTableName = sTableName;
    m_sptOptions = sptOptions;
  }

  @Override public Void visitTable_primary(@NotNull SQLParser.Table_primaryContext ctx) {
    if (ctx.table_or_query_name() != null) {
      m_sTableName = ctx.table_or_query_name().getText();
    }

    if (ctx.identifier() != null) {
      m_sSubqueryIdentifier = ctx.identifier().getText();
    }

    visitChildren(ctx);
    return null;
  }

  @Override public Void visitJoined_table_primary(@NotNull SQLParser.Joined_table_primaryContext ctx) {
    JoinVisitor jnVisitor = new JoinVisitor(this, m_arResolver, m_sptOptions);
    jnVisitor.visit(ctx);
    m_alJoins.add(jnVisitor);
    return null;
  }

  @Override public Void visitTable_subquery(@NotNull SQLParser.Table_subqueryContext ctx) {
    SelectFromVisitor ssVisitor = new SelectFromVisitor(m_arResolver, m_sptOptions);
    ssVisitor.visit(ctx);
    m_stmtSubquery = ssVisitor;
    return null;
  }

  public String getTableName() {
    if (m_stmtSubquery != null) {
      return m_stmtSubquery.getFromRef();
    } else if (m_sTableName != null) {
      return m_sTableName;
    } else {
      return null;
    }
  }

  public String getSubquery() {
    return m_stmtSubquery.toString();
  }

  public String getSubqueryIdentifier() {
    return m_sSubqueryIdentifier;
  }

  public boolean hasSubquery() {
    return m_stmtSubquery != null;
  }

  public String getModelName() {
    return Utils.camelize(
      Inflector.singularize(getTableName())
    );
  }

  public String getTableRef() {
    if (hasSubquery()) {
      return getSubqueryIdentifier();
    } else {
      return Utils.camelize(
        Inflector.singularize(getTableName())
      );
    }
  }

  public ArrayList<JoinVisitor> getJoins() {
    return m_alJoins;
  }
}
