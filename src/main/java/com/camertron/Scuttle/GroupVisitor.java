package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class GroupVisitor extends SQLParserBaseVisitor<Void> {
  private FromVisitor m_fmFromVisitor;
  private ColumnVisitor m_cvColumn;

  public GroupVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
  }

  @Override public Void visitGrouping_element(@NotNull SQLParser.Grouping_elementContext ctx) {
    m_cvColumn = new ColumnVisitor(m_fmFromVisitor);
    m_cvColumn.visit(ctx);
    visitChildren(ctx);
    return null;
  }

  public String toString() {
    return m_cvColumn.toString();
  }
}
