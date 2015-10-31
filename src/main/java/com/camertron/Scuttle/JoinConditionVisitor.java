package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class JoinConditionVisitor extends ValueExpressionVisitor {
  private TerminalNodeImpl[] m_operators;
  private Operand[] m_operands;

  public JoinConditionVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, ScuttleOptions sptOptions) {
    super(fmFromVisitor, arResolver, sptOptions);
  }

  public boolean isActiveRecordCompatible() {
    setOperatorsAndOperands();
    boolean isCompatible = (m_operators.length == 1 && isOperatorCompatible(m_operators[0]));

    if (m_stkOperandStack.size() == 2) {
      return isCompatible && isOperandCompatible(m_operands[0]) && isOperandCompatible(m_operands[1]);
    } else {
      return false;
    }
  }

  private boolean isOperatorCompatible(TerminalNodeImpl operator) {
    return operator.symbol.getType() == SQLParser.EQUAL;
  }

  private boolean isOperandCompatible(Operand operand) {
    return operand.getClass() == ColumnOperand.class;
  }

  private void setOperatorsAndOperands() {
    if (m_operators == null || m_operands == null) {
      m_operators = m_stkOperatorStack.toArray(new TerminalNodeImpl[m_stkOperatorStack.size()]);
      m_operands = m_stkOperandStack.toArray(new Operand[m_stkOperandStack.size()]);
    }
  }

  public String getFirstTableName() {
    setOperatorsAndOperands();
    return ((ColumnOperand)m_operands[0]).getRawTableName();
  }

  public String getFirstColumnName() {
    setOperatorsAndOperands();
    return ((ColumnOperand)m_operands[0]).getRawColumnName();
  }

  public String getSecondTableName() {
    setOperatorsAndOperands();
    return ((ColumnOperand)m_operands[1]).getRawTableName();
  }

  public String getSecondColumnName() {
    setOperatorsAndOperands();
    return ((ColumnOperand)m_operands[1]).getRawColumnName();
  }
}
