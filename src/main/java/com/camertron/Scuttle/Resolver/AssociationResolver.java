package com.camertron.Scuttle.Resolver;

import java.util.*;

public class AssociationResolver {
  private AssociationManager m_arManager;
  private HashMap<AssociationPair, JoinTablePairList> m_hmJoinsPerPair;

  public AssociationResolver(AssociationManager arManager) {
    m_arManager = arManager;
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

  public List<JoinColumnPair> getJoinsForAssociation(String sFirstModel, String sSecondModel) {
    Iterator iter = m_hmJoinsPerPair.entrySet().iterator();
    List<JoinColumnPair> ljResult = new ArrayList<JoinColumnPair>();

    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      Pair<String> pair = (Pair<String>)entry.getKey();
      List<JoinTablePair> joinTables = (List<JoinTablePair>)entry.getValue();

      if (pair.getFirst() == sFirstModel && pair.getSecond() == sSecondModel) {
        for (JoinTablePair joinTable : joinTables) {
          ljResult.addAll(joinTable.getJoins());
        }
      }
    }

    return ljResult;
  }
}
