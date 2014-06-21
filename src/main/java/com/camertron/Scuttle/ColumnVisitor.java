package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class ColumnVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sTableName;
  private String m_sColumnName;
  private String m_sExpression;
  private FromVisitor m_fmFromVisitor;

  public ColumnVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitQualified_asterisk(@NotNull SQLParser.Qualified_asteriskContext ctx) {
    if (ctx.tb_name != null) {
      m_sTableName = Utils.camelize(Inflector.singularize(ctx.tb_name.getText()));
    }

    m_sColumnName = "Arel.star";
    return null;
  }

  @Override public Void visitDerived_column(@NotNull SQLParser.Derived_columnContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
    veVisitor.visit(ctx);
    m_sExpression = veVisitor.toString();
    return null;
  }

  @Override public Void visitColumn_reference(@NotNull SQLParser.Column_referenceContext ctx) {
    if (ctx.tb_name != null) {
      m_sTableName = Utils.camelize(Inflector.singularize(ctx.tb_name.getText()));
    }

    m_sColumnName = ":" + ctx.name.getText();
    visitChildren(ctx);
    return null;
  }

  public String toString() {
    return toString(null);
  }

  public String toString(String sFromTableName) {
    String sTableName;

    // Override with the passed in table name if m_sTableName is null.
    if (m_sTableName == null) {
      sTableName = sFromTableName;
    } else {
      sTableName = m_sTableName;
    }

    if (m_sExpression != null) {
      return m_sExpression;
    } else {
      if (sTableName == null) {
        return m_sColumnName;
      } else {
        if (m_fmFromVisitor != null && m_fmFromVisitor.hasSubquery()) {
          return m_fmFromVisitor.getSubqueryIdentifier() + "[" + m_sColumnName + "]";
        } else {
          return sTableName + ".arel_table[" + m_sColumnName + "]";
        }
      }
    }
  }

  public String toQualifiedString() {
    return toString(
      Utils.camelize(Inflector.singularize(m_fmFromVisitor.getTableRef()))
    );
  }
}
