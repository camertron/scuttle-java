package com.camertron.Scuttle.Resolver;

import java.util.*;

public class AssociationResolver {
  private AssociationManager m_arManager;
  private HashMap<AssociationPair, List<JoinTablePair>> m_hmJoinsPerPair;

  public AssociationResolver(AssociationManager arManager) {
    m_arManager = arManager;
    m_hmJoinsPerPair = arManager.getAssociationJoins();
  }

  public AssociationChain getAssociationForJoins(JoinColumnPairList theirJoins) {
    Iterator iter = m_hmJoinsPerPair.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      AssociationPair pair = (AssociationPair)entry.getKey();
      List<JoinTablePair> ourJoins = (List<JoinTablePair>)entry.getValue();

      if (joinTableListsMatch(theirJoins, ourJoins)) {
        AssociationChain chain = new AssociationChain();

        for (JoinTablePair ourJoinTable : ourJoinTables) {
          chain.add(ourJoinTable.getRightHandTableName());
        }

        return chain;
      }
    }

    return null;
  }

  private List<JoinColumnPair> getAllJoins(List<JoinTablePair> joinTables) {
    List<JoinColumnPair> joinColumns = new ArrayList<JoinColumnPair>();

    for (JoinTablePair joinTable : joinTables) {
      joinColumns.addAll(joinTable.getJoins());
    }

    return joinColumns;
  }

  private boolean joinTableListsMatch(List<JoinTablePair> theirTables, List<JoinTablePair> ourTables) {
    boolean bFound;

    for (JoinTablePair theirTable : theirTables) {
      bFound = false;

      for (JoinTablePair ourTable : ourTables) {
        if (joinColumnListsMatch(theirTable.getJoins(), ourTable.getJoins())) {
          bFound = true;
          break;
        }
      }

      if (!bFound) {
        return false
      }
    }

    return true;
  }

  private boolean joinColumnListsMatch(List<JoinColumnPair> theirColumns, List<JoinColumnPair> ourColumns) {
    boolean bFound;

    for (JoinColumnPair theirColumn : theirColumns) {
      bFound = false;

      for (JoinColumnPair ourColumn : ourColumns) {
        if (columnRefsMatch(theirColumn.getFirst(), ourColumn.getFirst()) && columnRefsMatch(theirColumn.getSecond(), ourColumn.getSecond())) {
          bFound = true;
          break;
        }
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
