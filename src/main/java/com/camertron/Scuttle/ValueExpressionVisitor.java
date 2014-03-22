package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.SQLParser.SQLParserBaseVisitor;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.Stack;

public class ValueExpressionVisitor extends SQLParserBaseVisitor<Void> {
  private enum OperatorType {
    UNARY, BINARY
  }

  private Stack<TerminalNodeImpl> m_stkOperatorStack;
  private Stack<String> m_stkOperandStack;
  private boolean m_bAs = false;
  private String m_sAlias;

  public ValueExpressionVisitor() {
    super();
    m_stkOperatorStack = new Stack<TerminalNodeImpl>();
    m_stkOperandStack = new Stack<String>();
  }

  @Override public Void visitSet_function_specification(@NotNull SQLParser.Set_function_specificationContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor();
    funcVisitor.visit(ctx);
    m_stkOperandStack.push(funcVisitor.toString());
    return null;
  }

  @Override public Void visitRoutine_invocation(@NotNull SQLParser.Routine_invocationContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor();
    funcVisitor.visit(ctx);
    m_stkOperandStack.push(funcVisitor.toString());
    return null;
  }

  // NECESSARY?
  @Override public Void visitAggregate_function(@NotNull SQLParser.Aggregate_functionContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor();
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
    m_stkOperandStack.push(ctx.getText());
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitGeneral_literal(@NotNull SQLParser.General_literalContext ctx) {
    m_stkOperandStack.push(ctx.getText());
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitColumn_reference(@NotNull SQLParser.Column_referenceContext ctx) {
    ColumnVisitor cVisitor = new ColumnVisitor();
    cVisitor.visit(ctx);
    String col = cVisitor.toString();
    m_stkOperandStack.push(col);
    return null;
  }

  // This visitor gets hit for * and / operators, but not + or -
  @Override public Void visitNumeric_value_expression(SQLParser.Numeric_value_expressionContext ctx) {
    if (ctx.children.size() == 3) {
      TerminalNodeImpl tniOperator = getTerminalNode(ctx.children.get(1));
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

  // Strangely, this visitor method gets hit for + and - operators, but not for * or / (see visitNumeric_value_expression)
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

  private void processLeftRight(ParseTree ptLeft, ParseTree ptRight, TerminalNodeImpl tniOperator) {
    ValueExpressionVisitor veLeftVisitor = new ValueExpressionVisitor();
    veLeftVisitor.visit(ptLeft);
    m_stkOperandStack.push(veLeftVisitor.toString());

    if (ptRight != null) {
      // push operator
      if (tniOperator != null) {
        m_stkOperatorStack.push(tniOperator);
      }

      // push right operand
      ValueExpressionVisitor veRightVisitor = new ValueExpressionVisitor();
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
      while (!stkOperatorStack.empty()) {
        TerminalNodeImpl tniOperator = stkOperatorStack.pop();

        switch(getOperatorType(tniOperator)) {
          case BINARY:
            String sSecondOperand = stkOperandStack.pop();
            String sFirstOperand = stkOperandStack.pop();

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
            }
        }
      }

      String sOperand = stkOperandStack.pop();

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
        return OperatorType.BINARY;
      case SQLParser.LEFT_PAREN:
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
      default:
        return null;
    }
  }
}
