package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class GroupByVisitor extends ScuttleBaseVisitor {
  ArrayList<String> m_alGroups = new ArrayList<String>();

  public GroupByVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver) {
    super(fmFromVisitor, arResolver);
  }

  @Override public Void visitGrouping_element_list(@NotNull SQLParser.Grouping_element_listContext ctx) {
    for(ParseTree child : ctx.children) {
      GroupVisitor gVisitor = new GroupVisitor(m_fmFromVisitor, m_arResolver);
      gVisitor.visit(child);
      String sResult = gVisitor.toString();

      if (sResult != null) {
        m_alGroups.add(sResult);
      }
    }

    return null;
  }

  public String toString() {
    return Utils.commaize(m_alGroups);
  }
}
