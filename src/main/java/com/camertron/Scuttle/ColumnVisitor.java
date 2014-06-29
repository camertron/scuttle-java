package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;

public class ColumnVisitor extends ScuttleBaseVisitor {
  private String m_sTableName;
  private String m_sColumnName;
  private String m_sExpression;

  public ColumnVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver) {
    super(fmFromVisitor, arResolver);
  }

  @Override public Void visitQualified_asterisk(@NotNull SQLParser.Qualified_asteriskContext ctx) {
    if (ctx.tb_name != null) {
      m_sTableName = ctx.tb_name.getText();
    }

    m_sColumnName = "*";
    return null;
  }

  @Override public Void visitDerived_column(@NotNull SQLParser.Derived_columnContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver);
    veVisitor.visit(ctx);
    m_sExpression = veVisitor.toString();
    return null;
  }

  @Override public Void visitColumn_reference(@NotNull SQLParser.Column_referenceContext ctx) {
    if (ctx.tb_name != null) {
      m_sTableName = ctx.tb_name.getText();
    }

    m_sColumnName = ctx.name.getText();
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
      sTableName = getTableName();
    }

    if (m_sExpression != null) {
      return m_sExpression;
    } else {
      if (sTableName == null) {
        return getColumnName();
      } else {
        if (m_fmFromVisitor != null && m_fmFromVisitor.hasSubquery()) {
          return m_fmFromVisitor.getSubqueryIdentifier() + "[" + getColumnName() + "]";
        } else {
          return sTableName + ".arel_table[" + getColumnName() + "]";
        }
      }
    }
  }

  public String toQualifiedString() {
    return toString(
      Utils.camelize(Inflector.singularize(m_fmFromVisitor.getTableRef()))
    );
  }

  public String getRawTableName() {
    return m_sTableName;
  }

  public String getRawColumnName() {
    return m_sColumnName;
  }

  public String getColumnName() {
    String sRawColumnName = getRawColumnName();

    if (sRawColumnName == "*") {
      return "Arel.star";
    } else {
      return Utils.symbolize(sRawColumnName);
    }
  }

  public String getTableName() {
    return Utils.camelize(Inflector.singularize(getRawTableName()));
  }
}
