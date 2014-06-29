package com.camertron.Scuttle.Resolver;

public class ColumnRef {
  private String m_sTableName;
  private String m_sColumnName;

  public ColumnRef(String sTableName, String sColumnName) {
    m_sTableName = sTableName;
    m_sColumnName = sColumnName;
  }

  public String getTableName() {
    return m_sTableName;
  }

  public String getColumnName() {
    return m_sColumnName;
  }

  public String toString() {
    return "`" + getTableName() + "`.`" + getColumnName() + "`";
  }
}
