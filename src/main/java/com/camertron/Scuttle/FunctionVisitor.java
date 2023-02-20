package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.List;

public class FunctionVisitor extends ScuttleBaseVisitor {
  private String m_sFunctionName = "";
  private ArrayList<String> m_alArgList = new ArrayList<String>();
  private boolean m_bIsAggregate;
  private boolean m_bDistinct;

  public FunctionVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, ScuttleOptions sptOptions) {
    super(fmFromVisitor, arResolver, sptOptions);
  }

  @Override public Void visitAggregate_function(@NotNull SQLParser.Aggregate_functionContext ctx) {
    m_bIsAggregate = true;

    // Aggregate functions called with "*" (eg. COUNT(*)) cause the name of said function to be listed
    // as a terminal node instead of allowing the function name to be handled by the visitFunction_name
    // or visitSet_function_type visitor methods. Weird but true.
    m_sFunctionName = getFunctionNameFromTerminalNode(getTerminalNode(ctx.children.get(0)));

    // Handle special symbol args like "*", which come to us as terminal nodes instead of via visitValue_expression.
    addTerminalNodesAsArgs(ctx.children);
    visitChildren(ctx);
    return null;
  }

  private void addTerminalNodesAsArgs(List<ParseTree> nodes) {
    for(ParseTree node : nodes) {
      TerminalNodeImpl tniNode = getTerminalNode(node);
      if (tniNode != null && isArgToken(tniNode.symbol)) {
        addArgument(argTokenToArel(tniNode.symbol));
      }
    }
  }

  private TerminalNodeImpl getTerminalNode(ParseTree node) {
    if (node.getClass().equals(TerminalNodeImpl.class)) {
      return (TerminalNodeImpl)node;
    } else {
      return null;
    }
  }

  private boolean isArgToken(Token token) {
    return token.getType() == SQLParser.MULTIPLY;
  }

  private String getFunctionNameFromTerminalNode(TerminalNodeImpl tniNode) {
    if (tniNode != null) {
      switch (tniNode.symbol.getType()) {
        case SQLParser.MAX:
          return "maximum";
        case SQLParser.MIN:
          return "minimum";
        case SQLParser.AVG:
          return "average";
        default:
          return tniNode.symbol.getText();
      }
    } else {
      return null;
    }
  }

  private String argTokenToArel(Token token) {
    switch (token.getType()) {
      case SQLParser.MULTIPLY:
        return "Arel.star";
      default:
        return token.getText();
    }
  }

  @Override public Void visitSet_function_type(@NotNull SQLParser.Set_function_typeContext ctx) {
    m_sFunctionName = getFunctionNameFromTerminalNode(getTerminalNode(ctx.children.get(0)));
    m_bIsAggregate = isAggregateFunction(getTerminalNode(ctx.children.get(0)));
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitFunction_name(@NotNull SQLParser.Function_nameContext ctx) {
    m_sFunctionName = ctx.getText();
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitValue_expression(@NotNull SQLParser.Value_expressionContext ctx) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver, m_bIsAggregate, m_sptOptions);
    veVisitor.visit(ctx);
    addArgument(veVisitor.toString());
    return null;
  }

  @Override public Void visitSet_qualifier(@NotNull SQLParser.Set_qualifierContext ctx) {
    if (ctx.DISTINCT() != null) {
      m_bDistinct = true;
    }

    visitChildren(ctx);
    return null;
  }

  private void addArgument(String arg) {
    if (arg != null) {
      m_alArgList.add(ExpressionUtils.formatOperand(arg, false, m_sptOptions));
    }
  }

  public String toString() {
    String sFunctionCall;

    if (m_alArgList.size() == 1 && m_bIsAggregate) {
      String functionName = m_sFunctionName.toLowerCase();
      sFunctionCall = m_alArgList.get(0) + "." + functionName;

      // count is a special aggregate function that accepts a `distinct` boolean argument
      if (functionName.equals("count") && m_bDistinct) {
        sFunctionCall += "(true)";
      }
    } else {
      sFunctionCall = m_sptOptions.namespaceArelNodeClass("NamedFunction") + ".new(" +
        Utils.quote(m_sFunctionName.toUpperCase()) + ", " + Utils.arrayFormat(m_alArgList) +
      ")";
    }

    return sFunctionCall;
  }

  // "Aggregate" here refers to whether we use an arel named function or just call a method on the column
  private boolean isAggregateFunction(TerminalNodeImpl tniNode) {
    switch (tniNode.symbol.getType()) {
      case SQLParser.MAX:
      case SQLParser.MIN:
      case SQLParser.AVG:
      case SQLParser.COUNT:
        return true;
      default:
        return false;
    }
  }
}
