package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class SelectVisitor extends SQLParserBaseVisitor<Select> {
  @Override public Select visitSelect_list(@NotNull SQLParser.Select_listContext ctx) {
    SelectColumnVisitor scVisitor = new SelectColumnVisitor();
    ArrayList<SelectColumn> alColumns = new ArrayList<SelectColumn>();

    for (SQLParser.Select_sublistContext sublistItemContext : ctx.select_sublist()) {
      if (sublistItemContext.derived_column() != null) {
        alColumns.add(
          scVisitor.visit(sublistItemContext.derived_column())
        );
      }
    }

    return new Select(alColumns);
  }
}
