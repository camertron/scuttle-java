package com.camertron.Scuttle.Resolver;

public class JoinColumnPair extends Pair<ColumnRef> {
  private String m_sJoinTable;

  public JoinColumnPair(ColumnRef first, ColumnRef second, String sJoinTable) {
    m_first = first;
    m_second = second;
    m_sJoinTable = sJoinTable;
  }

  public String toString() {
    return getFirst().toString() + " = " + getSecond().toString();
  }
}
