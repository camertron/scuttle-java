package com.camertron.Scuttle;

public class SqlStatement {
  private Select m_select;
  private From m_from;

  public void setSelect(Select select) {
    m_select = select;
  }

  public Select getSelect() {
    return m_select;
  }

  public void setFrom(From from) {
    m_from = from;
  }

  public From getFrom() {
    return m_from;
  }
}
