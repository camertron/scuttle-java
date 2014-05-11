package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.Stack;

public class ValueExpressionVisitor extends SQLParserBaseVisitor<Void> {
  private enum OperatorType {
    UNARY, BINARY
  }

  private FromVisitor m_fmFromVisitor;
  private Stack<TerminalNodeImpl> m_stkOperatorStack;
  private Stack<String> m_stkOperandStack;
  private boolean m_bAs = false;
  private String m_sAlias;

  public ValueExpressionVisitor(FromVisitor fmFromVisitor) {
    super();
    m_fmFromVisitor = fmFromVisitor;
    setup();
  }

  private void setup() {
    m_stkOperatorStack = new Stack<TerminalNodeImpl>();
    m_stkOperandStack = new Stack<String>();
  }

  @Override public Void visitBetween_predicate(@NotNull SQLParser.Between_predicateContext ctx) {
    SQLParser.Between_predicate_part_2Context bpPartTwo = ctx.between_predicate_part_2();

    if (bpPartTwo != null) {
      ValueExpressionVisitor vePredicandVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
      vePredicandVisitor.visit(ctx.predicand);

      ValueExpressionVisitor veBeginVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
      veBeginVisitor.visit(bpPartTwo.begin);

      ValueExpressionVisitor veEndVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
      veEndVisitor.visit(bpPartTwo.end);

      String sFinal = "Arel::Nodes::Between.new(" + vePredicandVisitor.toString() + ", ";
      sFinal += "(" + ExpressionUtils.formatOperand(veBeginVisitor.toString(), true, true) + ")";
      sFinal += ".and(" + ExpressionUtils.formatOperand(veEndVisitor.toString(), false) + "))";

      m_stkOperandStack.push(sFinal);
    }

    return null;
  }

  @Override public Void visitSet_function_specification(@NotNull SQLParser.Set_function_specificationContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor(m_fmFromVisitor);
    funcVisitor.visit(ctx);
    m_stkOperandStack.push(funcVisitor.toString());
    return null;
  }

  @Override public Void visitRoutine_invocation(@NotNull SQLParser.Routine_invocationContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor(m_fmFromVisitor);
    funcVisitor.visit(ctx);
    m_stkOperandStack.push(funcVisitor.toString());
    return null;
  }

  @Override public Void visitAggregate_function(@NotNull SQLParser.Aggregate_functionContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor(m_fmFromVisitor);
    funcVisitor.visit(ctx);
    m_stkOperandStack.push(funcVisitor.toString());
    return null;
  }

  @Override public Void visitAs_clause(@NotNull SQLParser.As_clauseContext ctx) {
    m_bAs = true;
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitIdentifier(@NotNull SQLParser.IdentifierContext ctx) {
    if (m_bAs) {
      m_sAlias = ctx.getText();
      m_bAs = false;
    }

    visitChildren(ctx);
    return null;
  }

  @Override public Void visitUnsigned_numeric_literal(@NotNull SQLParser.Unsigned_numeric_literalContext ctx) {
    m_stkOperandStack.push(wrapLiteral(ctx.getText()));
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitGeneral_literal(@NotNull SQLParser.General_literalContext ctx) {
    m_stkOperandStack.push(wrapLiteral(ctx.getText()));
    visitChildren(ctx);
    return null;
  }

  // Literals (eg. integers, strings) won't themselves respond to
  // .and(), .or(), .eq(), etc, so we wrap them in a special tag for later.
  // The wrapping will be replaced with actual SqlLiteral node on toString().
  private String wrapLiteral(String sLiteral) {
    return "LITERAL<" + sLiteral + ">";
  }

  @Override public Void visitColumn_reference(@NotNull SQLParser.Column_referenceContext ctx) {
    ColumnVisitor cVisitor = new ColumnVisitor(m_fmFromVisitor);
    cVisitor.visit(ctx);
    String col = cVisitor.toString();
    m_stkOperandStack.push(col);
    return null;
  }

  // This visitor gets hit for *, /, -, and + operators
  @Override public Void visitNumeric_value_expression(SQLParser.Numeric_value_expressionContext ctx) {
    if (ctx.children.size() == 3) {
      TerminalNodeImpl tniOperator = getTerminalNode(ctx.children.get(1));

      switch (tniOperator.symbol.getType()) {
        case SQLParser.PLUS:
        case SQLParser.MINUS:
        case SQLParser.DIVIDE:
        case SQLParser.MULTIPLY:
          // extract is used to indicate a set of ruby parens
          m_stkOperatorStack.push(new TerminalNodeImpl(new CommonToken(SQLParser.EXTRACT, "(")));
      }

      processLeftRight(ctx.left, ctx.right, tniOperator);
    } else {
      visitChildren(ctx);
    }

    return null;
  }

  @Override public Void visitParenthesized_value_expression(SQLParser.Parenthesized_value_expressionContext ctx) {
    m_stkOperatorStack.push(new TerminalNodeImpl(new CommonToken(SQLParser.LEFT_PAREN, "(")));
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitTerm(SQLParser.TermContext ctx) {
    // 1 left + 1 right + 1 operator = 3 children
    if (ctx.children.size() == 3) {
      TerminalNodeImpl tniOperator = getTerminalNode(ctx.children.get(1));
      processLeftRight(ctx.left, ctx.right, tniOperator);
    } else {
      visitChildren(ctx);
    }

    return null;
  }

  @Override public Void visitAnd_predicate(SQLParser.And_predicateContext ctx) {
    if (ctx.children.size() == 3) {
      processLeftRight(
        // operator is in the middle
        ctx.children.get(0), ctx.children.get(2), getTerminalNode(ctx.children.get(1))
      );
    } else {
      visitChildren(ctx);
    }

    return null;
  }

  @Override public Void visitOr_predicate(SQLParser.Or_predicateContext ctx) {
    if (ctx.children.size() == 3) {
      processLeftRight(
        // operator is in the middle
        ctx.children.get(0), ctx.children.get(2), getTerminalNode(ctx.children.get(1))
      );
    } else {
      visitChildren(ctx);
    }

    return null;
  }

  @Override public Void visitIn_predicate(SQLParser.In_predicateContext ctx) {
    if (ctx.children.size() == 3) {
      processLeftRight(
        // operator is in the middle
        ctx.children.get(0), ctx.children.get(2), getTerminalNode(ctx.children.get(1))
      );
    } else {
      visitChildren(ctx);
    }

    return null;
  }

  @Override public Void visitIn_value_list(SQLParser.In_value_listContext ctx) {
    ArrayList<String> alInList = new ArrayList<String>();

    for(ParseTree child : ctx.children) {
      ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
      veVisitor.visit(child);

      if (veVisitor.toString() != null) {
        alInList.add(ExpressionUtils.formatOperand(veVisitor.toString(), false));
      }
    }

    m_stkOperandStack.push(Utils.commaize(alInList));
    return null;
  }

  // Triggered for sub-queries like you might have with an IN(), eg. WHERE id IN(SELECT id FROM foo)
  @Override public Void visitQuery_expression(SQLParser.Query_expressionContext ctx) {
    SelectFromVisitor ssmtVisitor = new SelectFromVisitor();
    ssmtVisitor.visit(ctx);
    m_stkOperandStack.push(ssmtVisitor.toString() + ".ast");
    return null;
  }

  @Override public Void visitComparison_predicate(SQLParser.Comparison_predicateContext ctx) {
    if (ctx.children.size() == 3) {
      processLeftRight(ctx.left, ctx.right, getTerminalNode(ctx.comp_op().children.get(0)));
    } else {
      visitChildren(ctx);
    }

    return null;
  }

  private void processLeftRight(ParseTree ptLeft, ParseTree ptRight, TerminalNodeImpl tniOperator) {
    ValueExpressionVisitor veLeftVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
    veLeftVisitor.visit(ptLeft);
    m_stkOperandStack.push(veLeftVisitor.toString());

    if (ptRight != null) {
      // push operator
      if (tniOperator != null) {
        m_stkOperatorStack.push(tniOperator);
      }

      // push right operand
      ValueExpressionVisitor veRightVisitor = new ValueExpressionVisitor(m_fmFromVisitor);
      veRightVisitor.visit(ptRight);
      m_stkOperandStack.push(veRightVisitor.toString());
    }
  }

  private TerminalNodeImpl getTerminalNode(ParseTree node) {
    if (node.getClass().equals(TerminalNodeImpl.class)) {
      return (TerminalNodeImpl)node;
    } else {
      return null;
    }
  }

  public String toString() {
    return evaluate();
  }

  private String evaluate() {
    Stack<String> stkOperandStack = (Stack<String>)m_stkOperandStack.clone();
    Stack<TerminalNodeImpl> stkOperatorStack = (Stack<TerminalNodeImpl>)m_stkOperatorStack.clone();

    if (!stkOperandStack.empty()) {
      TerminalNodeImpl tniOperator = null;

      while (!stkOperatorStack.empty()) {
        tniOperator = stkOperatorStack.pop();

        switch(getOperatorType(tniOperator)) {
          case BINARY:
            String sSecondOperand = ExpressionUtils.formatOperand(stkOperandStack.pop(), false);
            String sFirstOperand = ExpressionUtils.formatOperand(stkOperandStack.pop(), true);

            if (isMethodOperator(tniOperator)) {
              stkOperandStack.push(
                sFirstOperand + "." + getMethodNameForOperator(tniOperator) + "(" + sSecondOperand + ")"
              );
            } else {
              stkOperandStack.push(
                sFirstOperand + " " + tniOperator.getText() + " " + sSecondOperand
              );
            }

          case UNARY:
            switch (tniOperator.symbol.getType()) {
              case SQLParser.LEFT_PAREN:
                stkOperandStack.push(
                  "Arel::Nodes::Group.new(" + stkOperandStack.pop() + ")"
                );

              // extract is used to indicate a set of ruby parens
              case SQLParser.EXTRACT:
                // don't do anything for now... this may change later
                // stkOperandStack.push("(" + stkOperandStack.pop() + ")");
            }
        }
      }

      // If this operand is going to be the left-hand side of an arel method call (eg. .eq())
      // then wrap it in a SqlLiteral via ExpressionUtils.formatOperand().
      String sOperand;
      if (tniOperator == null) {
        sOperand = stkOperandStack.pop();
      } else {
        sOperand = ExpressionUtils.formatOperand(
          stkOperandStack.pop(), isMethodOperator(tniOperator)
        );
      }

      // The .as() method is not available on everything, most notably Arel::Nodes::Group. Watch out.
      if (m_sAlias != null) {
        sOperand += ".as(" + Utils.quote(m_sAlias) + ")";
      }

      return sOperand;
    } else {
      return null;
    }
  }

  private OperatorType getOperatorType(TerminalNodeImpl tniOperator) {
    switch (tniOperator.symbol.getType()) {
      case SQLParser.PLUS:
      case SQLParser.MINUS:
      case SQLParser.MULTIPLY:
      case SQLParser.DIVIDE:
      case SQLParser.EQUAL:
      case SQLParser.NOT_EQUAL:
      case SQLParser.GTH:
      case SQLParser.LTH:
      case SQLParser.GEQ:
      case SQLParser.LEQ:
      case SQLParser.AND:
      case SQLParser.OR:
      case SQLParser.IN:
        return OperatorType.BINARY;
      case SQLParser.LEFT_PAREN:
      case SQLParser.EXTRACT:  // extract is used to indicate a set of ruby parens
        return OperatorType.UNARY;
      default:
        return null;
    }
  }

  // Is the given operator represented as an Arel method call?
  // eg. =, and, or are all method operators: Table[:column].eq(1)
  // whereas +, - are regular operators: Table[:column] + 1
  private boolean isMethodOperator(TerminalNodeImpl tniOperator) {
    switch (tniOperator.symbol.getType()) {
      case SQLParser.PLUS:
      case SQLParser.MINUS:
      case SQLParser.DIVIDE:
      case SQLParser.MULTIPLY:
        return false;
      default:
        return true;
    }
  }

  private String getMethodNameForOperator(TerminalNodeImpl tniOperator) {
    switch (tniOperator.symbol.getType()) {
      case SQLParser.AND:
        return "and";
      case SQLParser.OR:
        return "or";
      case SQLParser.EQUAL:
        return "eq";
      case SQLParser.NOT_EQUAL:
        return "neq";
      case SQLParser.GTH:
        return "gt";
      case SQLParser.LTH:
        return "lt";
      case SQLParser.GEQ:
        return "gteq";
      case SQLParser.LEQ:
        return "lteq";
      case SQLParser.IN:
        return "in";
      default:
        return null;
    }
  }
}
