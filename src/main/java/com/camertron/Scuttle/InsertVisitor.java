package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class InsertVisitor extends SQLParserBaseVisitor<Void> {
  private FromVisitor m_fmFromVisitor;
  private ArrayList<String> m_alColumns = new ArrayList<String>();
  private ArrayList<String> m_alValues = new ArrayList<String>();

  @Override public Void visitInsert_statement(@NotNull SQLParser.Insert_statementContext ctx) {
    m_fmFromVisitor = new FromVisitor(ctx.tb_name.getText());

    for(ParseTree column : ctx.column_name_list().children) {
      m_alColumns.add(":" + column.getText());
    }

    for(ParseTree value : ctx.insert_value_list().children) {
      ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
      veVisitor.visit(value);
      m_alValues.add(ExpressionUtils.formatOperand(veVisitor.toString(), false));
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
