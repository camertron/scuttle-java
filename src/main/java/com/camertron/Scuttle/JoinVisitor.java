package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;

public class JoinVisitor extends ScuttleBaseVisitor {
  private String m_sTableName;
  private JoinConditionVisitor m_jcConditionVisitor;

  private boolean m_bLeft = false;
  private boolean m_bRight = false;
  private boolean m_bInner = false;
  private boolean m_bOuter = false;

  public JoinVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, ScuttleOptions sptOptions) {
    super(fmFromVisitor, arResolver, sptOptions);
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

    JoinConditionVisitor veVisitor = new JoinConditionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    veVisitor.visit(ctx.s);
    m_jcConditionVisitor = veVisitor;
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
    String sJoin = m_fmFromVisitor.getTableRef() + ".arel_table.join(" + getTableName() + ".arel_table";

    if (m_bOuter) {
      sJoin += ", " + m_sptOptions.namespaceArelNodeClass("OuterJoin");
    }

    return sJoin + ")" + ".on(" + m_jcConditionVisitor.toString() + ")";
  }

  public String getTableName() {
    return Utils.camelize(Inflector.singularize(m_sTableName));
  }

  public boolean isActiveRecordCompatible() {
    return m_jcConditionVisitor.isActiveRecordCompatible() &&
      !m_bRight && !m_bOuter && !m_bLeft;
  }

  public JoinConditionVisitor getConditionVisitor() {
    return m_jcConditionVisitor;
  }
}
