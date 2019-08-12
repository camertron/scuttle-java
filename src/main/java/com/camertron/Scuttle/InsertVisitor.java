package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class InsertVisitor extends SQLParserBaseVisitor<Void> {
  private ArrayList<String> m_alColumns = new ArrayList<String>();
  private ArrayList<String> m_alValues = new ArrayList<String>();
  private FromVisitor m_fmFromVisitor;
  private AssociationResolver m_arResolver;
  private ScuttleOptions m_sptOptions;

  public InsertVisitor(AssociationResolver arResolver, ScuttleOptions sptOptions) {
    m_arResolver = arResolver;
    m_sptOptions = sptOptions;
  }

  @Override public Void visitInsert_statement(@NotNull SQLParser.Insert_statementContext ctx) {
    m_fmFromVisitor = new FromVisitor(ctx.table_name().getText(), m_sptOptions);

    for(SQLParser.Column_referenceContext column : ctx.column_reference_list().column_reference()) {
      m_alColumns.add(":" + column.getText());
    }

    for(SQLParser.Row_value_predicandContext value : ctx.row_value_predicand()) {
      ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
      veVisitor.visit(value);
      m_alValues.add(ExpressionUtils.formatOperand(veVisitor.toString(), false, m_sptOptions));
    }

    return null;
  }

  public String toString() {
    String sQuery = m_fmFromVisitor.getTableRef() + ".create(";
    ArrayList<String> alHash = new ArrayList<String>();

    for(int i = 0; i < m_alColumns.size(); i ++) {
      alHash.add(m_alColumns.get(i) + " => " + m_alValues.get(i));
    }

    sQuery += Utils.commaize(alHash) + ")";
    return sQuery;
  }
}
