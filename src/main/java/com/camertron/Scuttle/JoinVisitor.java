package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;

public class JoinVisitor extends SQLParserBaseVisitor<Void> {
  private String m_sTableName;
  private String m_sConditions;
  private String m_sFromTableName;

  private boolean m_bLeft = false;
  private boolean m_bRight = false;
  private boolean m_bInner = false;
  private boolean m_bOuter = false;

  public JoinVisitor(String sFromTableName) {
    super();
    m_sFromTableName = sFromTableName;
  }

  @Override public Void visitTable_name(@NotNull SQLParser.Table_nameContext ctx) {
    m_sTableName = ctx.getText();
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitJoined_table_primary(@NotNull SQLParser.Joined_table_primaryContext ctx) {
    if (ctx.t != null) {
      setJoinType(gatherTerminals(ctx.t));
    }

    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_sFromTableName);
    veVisitor.visit(ctx.s);
    m_sConditions = veVisitor.toString();
    visit(ctx.right);
    return null;
  }

  private void setJoinType(ArrayList<TerminalNodeImpl> alJoinTypes) {
    for(TerminalNodeImpl tniJoinType : alJoinTypes) {
      switch (tniJoinType.symbol.getType()) {
        case SQLParser.LEFT:  m_bLeft  = true; break;
        case SQLParser.RIGHT: m_bRight = true; break;
        case SQLParser.INNER: m_bInner = true; break;
        case SQLParser.OUTER: m_bOuter = true; break;
      }
    }
  }

  private ArrayList<TerminalNodeImpl> gatherTerminals(ParseTree ctx) {
    ArrayList<TerminalNodeImpl> alFinal = new ArrayList<TerminalNodeImpl>();

    for(int i = 0; i < ctx.getChildCount(); i ++) {
      ParseTree child = ctx.getChild(i);

      if (child.getClass() == TerminalNodeImpl.class) {
        alFinal.add((TerminalNodeImpl)child);
      }

      alFinal.addAll(gatherTerminals(child));
    }

    return alFinal;
  }

  public String toString() {
    // this will produce an INNER JOIN
    String sJoin = m_sFromTableName + ".arel_table.join(" + getTableName() + ".arel_table";

    if (m_bOuter) {
      sJoin += ", Arel::Nodes::OuterJoin";
    }

    return sJoin + ")" + ".on(" + m_sConditions + ")";
  }

  public String getTableName() {
    return Utils.camelize(Inflector.dePluralize(m_sTableName));
  }
}
