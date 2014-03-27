package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class GroupByVisitor extends SQLParserBaseVisitor<Void> {
  private FromVisitor m_fmFromVisitor;
  ArrayList<String> m_alGroups = new ArrayList<String>();

  public GroupByVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitGrouping_element_list(@NotNull SQLParser.Grouping_element_listContext ctx) {
    for(ParseTree child : ctx.children) {
      GroupVisitor gVisitor = new GroupVisitor(m_fmFromVisitor);
      gVisitor.visit(child);
      m_alGroups.add(gVisitor.toString());
    }

    return null;
  }

  public String toString() {
    return Utils.commaize(m_alGroups);
  }
}
