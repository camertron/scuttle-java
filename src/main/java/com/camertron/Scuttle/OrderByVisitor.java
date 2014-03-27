package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class OrderByVisitor extends SQLParserBaseVisitor<Void> {
  private ArrayList<OrderVisitor> m_alOrders = new ArrayList<OrderVisitor>();
  public FromVisitor m_fmFromVisitor;

  public OrderByVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitSort_specifier_list(@NotNull SQLParser.Sort_specifier_listContext ctx) {
    for(ParseTree child : ctx.children) {
      if (child.getClass() == SQLParser.Sort_specifierContext.class) {
        OrderVisitor odrVisitor = new OrderVisitor(m_fmFromVisitor);
        odrVisitor.visit(child);
        m_alOrders.add(odrVisitor);
      }
    }

    return null;
  }

  public ArrayList<OrderVisitor> getOrders() {
    return m_alOrders;
  }

  public boolean isReverseOrder() {
    boolean bReverseOrder = true;

    for(OrderVisitor order : m_alOrders) {
      bReverseOrder = bReverseOrder && order.isReverseOrder();
    }

    return bReverseOrder;
  }
}
