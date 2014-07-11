package com.camertron.Scuttle.Resolver;

import java.util.*;

public class AssociationResolver {
  private HashMap<AssociationPair, JoinTablePairList> m_hmJoinsPerPair;

  public AssociationResolver(AssociationManager arManager) {
    m_hmJoinsPerPair = arManager.getAssociationJoins();
  }

  public AssociationChain getAssociationChainForJoins(JoinColumnPairList theirJoins) {
    Iterator iter = m_hmJoinsPerPair.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      JoinTablePairList ourJoinTables = (JoinTablePairList)entry.getValue();
      JoinColumnPairList ourJoins = ourJoinTables.getAllJoins();

      if (joinColumnListsMatch(theirJoins, ourJoins)) {
        AssociationChain chain = new AssociationChain();

        for (JoinTablePair ourJoinTable : ourJoinTables) {
          chain.add(ourJoinTable.getRightHandTableName());
        }

        return chain;
      }
    }

    return null;
  }

  private boolean joinColumnListsMatch(JoinColumnPairList theirColumns, JoinColumnPairList ourColumns) {
    if (theirColumns.size() != ourColumns.size()) {
      return false;
    }

    boolean bFound;

    for (JoinColumnPair theirColumn : theirColumns) {
      bFound = false;

      for (JoinColumnPair ourColumn : ourColumns) {
        bFound = bFound || (columnRefsMatch(theirColumn.getFirst(), ourColumn.getFirst()) && columnRefsMatch(theirColumn.getSecond(), ourColumn.getSecond()));
        bFound = bFound || (columnRefsMatch(theirColumn.getSecond(), ourColumn.getFirst()) && columnRefsMatch(theirColumn.getFirst(), ourColumn.getSecond()));
        bFound = bFound && ourColumn.getJoinTable().equals(theirColumn.getJoinTable());
        if (bFound) { break; }
      }

      if (!bFound) {
        return false;
      }
    }

    return true;
  }

  private boolean columnRefsMatch(ColumnRef theirColumn, ColumnRef ourColumn) {
    return theirColumn.getTableName().equals(ourColumn.getTableName()) &&
      theirColumn.getColumnName().equals(ourColumn.getColumnName());
  }
}
