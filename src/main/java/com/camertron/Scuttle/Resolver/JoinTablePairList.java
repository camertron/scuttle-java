package com.camertron.Scuttle.Resolver;

import java.util.ArrayList;

public class JoinTablePairList extends ArrayList<JoinTablePair> {
  public JoinColumnPairList getAllJoins() {
    JoinColumnPairList joinColumns = new JoinColumnPairList();

    for (JoinTablePair joinTable : this) {
      joinColumns.addAll(joinTable.getJoins());
    }

    return joinColumns;
  }
}
