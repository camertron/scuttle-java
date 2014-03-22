package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class ColumnVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sTableName;
  private String m_sColumnName;
  private String m_sExpression;

  @Override public Void visitQualified_asterisk(@NotNull SQLParser.Qualified_asteriskContext ctx) {
    if (ctx.tb_name != null) {
      m_sTableName = Inflector.dePluralize(Utils.camelize(ctx.tb_name.getText()));
    }

    m_sColumnName = "Arel.star";
    return null;
  }

  @Override public Void visitDerived_column(@NotNull SQLParser.Derived_columnContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor();
    veVisitor.visit(ctx);
    m_sExpression = veVisitor.toString();
    return null;
  }

  @Override public Void visitColumn_reference(@NotNull SQLParser.Column_referenceContext ctx) {
    m_sTableName = Inflector.dePluralize(Utils.camelize(ctx.tb_name.getText()));
    m_sColumnName = ":" + ctx.name.getText();
    visitChildren(ctx);
    return null;
  }

  public String toString() {
    if (m_sExpression != null) {
      return m_sExpression;
    } else {
      if (m_sTableName == null) {
        return m_sColumnName;
      } else {
        return m_sTableName + ".arel_table[" + m_sColumnName + "]";
      }
    }
  }
}
