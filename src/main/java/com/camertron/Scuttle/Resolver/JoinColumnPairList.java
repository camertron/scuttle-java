package com.camertron.Scuttle.Resolver;

import java.util.ArrayList;

public class JoinColumnPairList extends ArrayList<JoinColumnPair> {
  public void addPair(String sJoinTable, String sFirstTableName, String sFirstColumnName, String sSecondTableName, String sSecondColumnName) {
    add(
      new JoinColumnPair(
        new ColumnRef(sFirstTableName, sFirstColumnName),
        new ColumnRef(sSecondTableName, sSecondColumnName),
        sJoinTable
      )
    );
  }
}
