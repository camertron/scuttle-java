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

  public void setQualified(boolean bQualified) {
    m_bQualified = bQualified;
  }

  public boolean getQualified() {
    return m_bQualified;
  }
}
