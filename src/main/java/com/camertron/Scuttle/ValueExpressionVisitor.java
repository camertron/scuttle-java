package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.Stack;

public class ValueExpressionVisitor extends ScuttleBaseVisitor {
  private enum OperatorType {
    UNARY, BINARY
  }

  private boolean m_bQualifyColumns;
  protected Stack<TerminalNodeImpl> m_stkOperatorStack;
  protected Stack<Operand> m_stkOperandStack;
  private boolean m_bAs = false;
  private String m_sAlias;

  public ValueExpressionVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, ScuttleOptions sptOptions) {
    super(fmFromVisitor, arResolver, sptOptions);
    setup();
    m_bQualifyColumns = false;
  }

  public ValueExpressionVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, boolean bQualifyColumns, ScuttleOptions sptOptions) {
    super(fmFromVisitor, arResolver, sptOptions);
    setup();
    m_bQualifyColumns = bQualifyColumns;
  }

  private void setup() {
    m_stkOperatorStack = new Stack<TerminalNodeImpl>();
    m_stkOperandStack = new Stack<Operand>();
  }

  @Override public Void visitBetween_predicate(@NotNull SQLParser.Between_predicateContext ctx) {
    SQLParser.Between_predicate_part_2Context bpPartTwo = ctx.between_predicate_part_2();

    if (bpPartTwo != null) {
      String sPredicand = visitValueExpression(ctx.predicand);
      Operand opBegin = evaluateValueExpression(bpPartTwo.begin);
      Operand opEnd = evaluateValueExpression(bpPartTwo.end);

      String sFinal = m_sptOptions.namespaceArelNodeClass("Between") + ".new(" + sPredicand + ", ";

      switch (opBegin.getType()) {
        case COLUMN:
        case EXPRESSION:
          sFinal += "(" + ExpressionUtils.formatOperand(opBegin.toString(), true, true, m_sptOptions) + ")";
          sFinal += ".and(" + ExpressionUtils.formatOperand(opEnd.toString(), false, m_sptOptions) + "))";
          break;

        default:
          String sBegin = ExpressionUtils.formatOperand(opBegin.toString(), false, false, m_sptOptions);
          String sEnd = ExpressionUtils.formatOperand(opEnd.toString(), false, false, m_sptOptions);
          sFinal += m_sptOptions.namespaceArelNodeClass("And") + ".new([" + sBegin + ", " + sEnd + "]))";
      }

      m_stkOperandStack.push(ExpressionOperand.fromString(sFinal));
    }

    return null;
  }

  @Override public Void visitCase_expression(@NotNull SQLParser.Case_expressionContext ctx) {
    // CASE statements were added in Arel 7.0, i.e. Rails 5.0. If we're >= 5.0, visit children
    // to invoke visitSimple_case and visitSearched_case below.
    if (m_sptOptions.m_svRailsVersion.greaterThanOrEqualTo("5.0.0")) {
      return visitChildren(ctx);
    }

    CharStream stream = ctx.start.getInputStream();
    Interval interval = new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
    String text = stream.getText(interval);

    m_stkOperandStack.push(
      StringOperand.fromString("Arel.sql(" + Utils.quote(text) + ")")
    );

    return null;
  }

  @Override public Void visitSimple_case(@NotNull SQLParser.Simple_caseContext ctx) {
    StringBuilder result = new StringBuilder();

    result.append("Arel::Nodes::Case.new");
    if (!ctx.simple_when_clause().isEmpty()) {
      result.append("(");
    }

    result.append(visitValueExpression(ctx.boolean_value_expression()));
    result.append(")");

    for (SQLParser.Simple_when_clauseContext when : ctx.simple_when_clause()) {
      result.append(".when(");
      result.append(ExpressionUtils.formatOperand(visitValueExpression(when.search_condition()), false, m_sptOptions));
      result.append(").then(");
      result.append(ExpressionUtils.formatOperand(visitValueExpression(when.result()), false, m_sptOptions));
      result.append(")");
    }

    if (ctx.else_clause() != null) {
      result.append(".else(");
      result.append(ExpressionUtils.formatOperand(visitValueExpression(ctx.else_clause()), false, m_sptOptions));
      result.append(")");
    }

    m_stkOperandStack.push(StringOperand.fromString(result.toString()));
    return null;
  }

  @Override public Void visitSearched_case(@NotNull SQLParser.Searched_caseContext ctx) {
    StringBuilder result = new StringBuilder();
    result.append("Arel::Nodes::Case.new");

    for (SQLParser.Searched_when_clauseContext when : ctx.searched_when_clause()) {
      result.append(".when(");
      result.append(ExpressionUtils.formatOperand(visitValueExpression(when.search_condition()), false, m_sptOptions));
      result.append(")");
      result.append(".then(");
      result.append(ExpressionUtils.formatOperand(visitValueExpression(when.result()), false, m_sptOptions));
      result.append(")");
    }

    if (ctx.else_clause() != null) {
      result.append(".else(");
      result.append(ExpressionUtils.formatOperand(visitValueExpression(ctx.else_clause()), false, m_sptOptions));
      result.append(")");
    }

    m_stkOperandStack.push(StringOperand.fromString(result.toString()));
    return null;
  }

  @Override public Void visitSet_function_specification(@NotNull SQLParser.Set_function_specificationContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    funcVisitor.visit(ctx);
    m_stkOperandStack.push(ExpressionOperand.fromString(funcVisitor.toString()));
    return null;
  }

  @Override public Void visitRoutine_invocation(@NotNull SQLParser.Routine_invocationContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    funcVisitor.visit(ctx);
    m_stkOperandStack.push(ExpressionOperand.fromString(funcVisitor.toString()));
    return null;
  }

  @Override public Void visitAggregate_function(@NotNull SQLParser.Aggregate_functionContext ctx) {
    FunctionVisitor funcVisitor = new FunctionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    funcVisitor.visit(ctx);
    m_stkOperandStack.push(ExpressionOperand.fromString(funcVisitor.toString()));
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

  @Override public Void visitWindow_function(@NotNull SQLParser.Window_functionContext ctx) {
    String operand;

    if (ctx.window_function_type().ROW_NUMBER() != null) {
      operand = "Arel::Nodes::NamedFunction.new('ROW_NUMBER', [])";
    } else if (ctx.window_function_type().aggregate_function() != null) {
      FunctionVisitor funcVisitor = new FunctionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
      funcVisitor.visit(ctx.window_function_type().aggregate_function());
      operand = funcVisitor.toString();
    } else if (ctx.window_function_type().column_reference() != null) {
      ColumnVisitor columnVisitor = new ColumnVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
      columnVisitor.visit(ctx.window_function_type().column_reference());
      operand = columnVisitor.toString();
    } else {
      throw new RuntimeException("Could not determine window function type");
    }

    m_stkOperandStack.push(ExpressionOperand.fromString(operand + ".over"));
    return null;
  }

  @Override public Void visitNull_predicate(@NotNull SQLParser.Null_predicateContext ctx) {
    if (ctx.n == null) {
      m_stkOperatorStack.push((TerminalNodeImpl) ctx.IS());
    } else {
      m_stkOperatorStack.push((TerminalNodeImpl) ctx.NOT());
    }

    visitChildren(ctx);
    m_stkOperandStack.push(StringOperand.fromString("nil"));

    return null;
  }

  @Override public Void visitIs_clause(@NotNull SQLParser.Is_clauseContext ctx) {
    TerminalNode operator = ctx.IS();

    for (ParseTree child : ctx.children) {
      if (child == ctx.NOT()) {
        operator = ctx.NOT();
        break;
      }
    }

    m_stkOperatorStack.push((TerminalNodeImpl) operator);
    m_stkOperandStack.push(StringOperand.fromString(ctx.t.getText().toLowerCase()));
    return null;
  }

  @Override public Void visitUnsigned_numeric_literal(@NotNull SQLParser.Unsigned_numeric_literalContext ctx) {
    m_stkOperandStack.push(NumericOperand.fromString(ctx.getText()));
    visitChildren(ctx);
    return null;
  }

  @Override public Void visitGeneral_literal(@NotNull SQLParser.General_literalContext ctx) {
    m_stkOperandStack.push(StringOperand.fromString(wrapLiteral(ctx.getText())));
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
    ColumnVisitor cVisitor = new ColumnVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    cVisitor.visit(ctx);
    m_stkOperandStack.push(ColumnOperand.fromColumn(cVisitor));
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
      String sInValue = visitValueExpression(child);

      if (sInValue != null) {
        alInList.add(ExpressionUtils.formatOperand(sInValue, false, m_sptOptions));
      }
    }

    m_stkOperandStack.push(
      StringOperand.fromString(Utils.singletonArrayFormat(alInList))
    );

    return null;
  }

  @Override public Void visitExists_predicate(SQLParser.Exists_predicateContext ctx) {
    SelectFromVisitor visitor = new SelectFromVisitor(m_arResolver, m_sptOptions);
    visitor.visit(ctx.children.get(1));

    m_stkOperandStack.push(
      StringOperand.fromString(visitor.toString() + ".exists")
    );

    return null;
  }

  // Triggered for sub-queries like you might have with an IN(), eg. WHERE id IN(SELECT id FROM foo)
  @Override public Void visitQuery_expression(SQLParser.Query_expressionContext ctx) {
    SelectFromVisitor ssmtVisitor = new SelectFromVisitor(m_arResolver, m_sptOptions);
    ssmtVisitor.visit(ctx);

    StringOperand operand;

    if (m_sptOptions.getRailsVersion().lessThan("6.0.0")) {
      operand = StringOperand.fromString(ssmtVisitor.toString() + ".ast");
    } else {
      operand = StringOperand.fromString(ssmtVisitor.toString());
    }

    m_stkOperandStack.push(operand);

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

  @Override public Void visitInterval_literal(SQLParser.Interval_literalContext ctx) {
    String result = "INTERVAL " + ctx.interval_expression.getText() + " " + ctx.interval.getText();
    m_stkOperandStack.push(new StringOperand("Arel::Nodes::SqlLiteral.new(" + Utils.quote(result) + ")"));
    return null;
  }

  private void processLeftRight(ParseTree ptLeft, ParseTree ptRight, TerminalNodeImpl tniOperator) {
    m_stkOperandStack.push(evaluateValueExpression(ptLeft));

    if (ptRight != null) {
      // push operator
      if (tniOperator != null) {
        m_stkOperatorStack.push(tniOperator);
      }

      // push right operand
      m_stkOperandStack.push(evaluateValueExpression(ptRight));
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
    Operand odResult = evaluate();
    if (odResult == null) {
      return null;
    } else {
      return odResult.toString();
    }
  }

  private String visitValueExpression(ParseTree exp) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    veVisitor.visit(exp);
    return veVisitor.toString();
  }

  private Operand evaluateValueExpression(ParseTree exp) {
    ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
    veVisitor.visit(exp);
    return veVisitor.evaluate();
  }

  private Operand evaluate() {
    Stack<Operand> stkOperandStack = (Stack<Operand>)m_stkOperandStack.clone();
    Stack<TerminalNodeImpl> stkOperatorStack = (Stack<TerminalNodeImpl>)m_stkOperatorStack.clone();

    if (!stkOperandStack.empty()) {
      TerminalNodeImpl tniOperator = null;

      while (!stkOperatorStack.empty()) {
        tniOperator = stkOperatorStack.pop();

        switch(getOperatorType(tniOperator)) {
          case BINARY:
            Operand odSecondOperand = stkOperandStack.pop();
            String sSecondOperand = ExpressionUtils.formatOperand(odSecondOperand.toString(), false, m_sptOptions);
            Operand odFirstOperand = stkOperandStack.pop();
            String sFirstOperand = ExpressionUtils.formatOperand(odFirstOperand.toString(), true, m_sptOptions);

            if (isInfixOperator(tniOperator)) {
              String expression;

              switch (odFirstOperand.getType()) {
                case COLUMN:
                  ((ColumnOperand)odFirstOperand).setQualified(true);
                  sFirstOperand = ExpressionUtils.formatOperand(odFirstOperand.toString(), true, m_sptOptions);
                case EXPRESSION:
                  if (isMethodOperator(tniOperator)) {
                    expression = sFirstOperand + "." + getMethodNameForOperator(tniOperator) + "(" + sSecondOperand + ")";
                  } else {
                    expression = sFirstOperand + " " + tniOperator.getText() + " " + sSecondOperand;
                  }
                  break;
                default:
                  switch(odSecondOperand.getType()) {
                    case COLUMN:
                    case EXPRESSION:
                    case NUMERIC:
                      expression = m_sptOptions.namespaceArelNodeClass("InfixOperation") + ".new(" + Utils.quote(tniOperator.getText()) + ", " + sFirstOperand + ", " + sSecondOperand + ")";
                      break;
                    default:
                      expression = m_sptOptions.namespaceArelNodeClass("InfixOperation") + ".new(" + Utils.quote(tniOperator.getText()) + ", " + sFirstOperand + ", Arel::Nodes.build_quoted(" + sSecondOperand + "))";
                      break;
                  }
              }

              stkOperandStack.push(ExpressionOperand.fromString(expression));
            } else {
              stkOperandStack.push(
                StringOperand.fromString(
                  sFirstOperand + " " + tniOperator.getText() + " " + sSecondOperand
                )
              );
            }

            break;

          case UNARY:
            switch (tniOperator.symbol.getType()) {
              case SQLParser.LEFT_PAREN:
                Operand odOperand = stkOperandStack.pop();

                if (odOperand.getType() == Operand.OperandType.COLUMN) {
                  ((ColumnOperand)odOperand).setQualified(true);
                }

                stkOperandStack.push(
                  StringOperand.fromString(
                    m_sptOptions.namespaceArelNodeClass("Group") + ".new(" + odOperand.toString() + ")"
                  )
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
      Operand odOperand = stkOperandStack.pop();
      Operand odFinal = odOperand;

      if (odOperand.getType() == Operand.OperandType.COLUMN) {
        if (m_bQualifyColumns)
          ((ColumnOperand)odOperand).setQualified(true);
      }

//      if (tniOperator == null) {
//        odFinal = odOperand;
//      } else {
//        odFinal = StringOperand.fromString(
//          ExpressionUtils.formatOperand(
//            odOperand.toString(), isMethodOperator(tniOperator), m_sptOptions
//          )
//        );
//      }

      // The .as() method is not available on everything, most notably Arel::Nodes::Group. Watch out.
      if (m_sAlias != null) {
        odFinal = ExpressionOperand.fromString(
          odOperand.toString() + ".as(" + Utils.quote(m_sAlias) + ")"
        );
      }

      return odFinal;
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
      case SQLParser.IS:
      case SQLParser.NOT:
//      case SQLParser.NULL:
      case SQLParser.INFIX_OPERATOR:
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

  private boolean isInfixOperator(TerminalNodeImpl tniOperator) {
    switch (tniOperator.symbol.getType()) {
      case SQLParser.PLUS:
      case SQLParser.MINUS:
      case SQLParser.DIVIDE:
      case SQLParser.MULTIPLY:
      case SQLParser.AND:
      case SQLParser.OR:
      case SQLParser.EQUAL:
      case SQLParser.NOT_EQUAL:
      case SQLParser.GTH:
      case SQLParser.LTH:
      case SQLParser.GEQ:
      case SQLParser.LEQ:
      case SQLParser.IN:
      case SQLParser.IS:
      case SQLParser.NOT:
      case SQLParser.INFIX_OPERATOR:
        return true;
      default:
        return false;
    }
  }

  private String getMethodNameForOperator(TerminalNodeImpl tniOperator) {
    switch (tniOperator.symbol.getType()) {
      case SQLParser.AND:
        return "and";
      case SQLParser.OR:
        return "or";
      case SQLParser.IS:
      case SQLParser.EQUAL:
        return "eq";
      case SQLParser.NOT:       // used to indicate IS NOT NULL
      case SQLParser.NOT_EQUAL:
        return "not_eq";
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
