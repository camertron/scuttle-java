package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class FromVisitor extends SQLParserBaseVisitor<From> {
  @Override public From visitTable_primary(@NotNull SQLParser.Table_primaryContext ctx) {
    return new From(ctx.identifier().Identifier().getSymbol().getText());
  }
}
