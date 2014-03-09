package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class SelectColumnVisitor extends SQLParserBaseVisitor<SelectColumn> {
  @Override public SelectColumn visitDerived_column(@NotNull SQLParser.Derived_columnContext ctx) {
    return new SelectColumn(ctx.value_expression());
  }
}
