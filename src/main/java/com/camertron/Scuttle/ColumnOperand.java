package com.camertron.Scuttle;

public class ColumnOperand implements Operand {
  private ColumnVisitor m_cvColumn;
  private boolean m_bQualified;

  public static ColumnOperand fromColumn(ColumnVisitor cvColumn) {
    return new ColumnOperand(cvColumn);
  }

  public ColumnOperand(ColumnVisitor cvColumn) {
    m_cvColumn = cvColumn;
  }

  public String toString() {
    if (m_bQualified)
      return m_cvColumn.toQualifiedString();
    else
      return m_cvColumn.toString();
  }

  @Override
  public OperandType getType() {
    return OperandType.COLUMN;
  }

  public void setQualified(boolean bQualified) {
    m_bQualified = bQualified;
  }

  public boolean getQualified() {
    return m_bQualified;
  }

  public String getRawTableName() {
    return m_cvColumn.getRawTableName();
  }

  public String getRawColumnName() {
    return m_cvColumn.getRawColumnName();
  }

  public String getTableName() {
    return m_cvColumn.getTableName();
  }

  public String getColumnName() {
    return m_cvColumn.getColumnName();
  }
}
