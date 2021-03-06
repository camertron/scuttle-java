package com.camertron.Scuttle.Resolver;

import java.util.*;

public class AssociationManager {
  private Digraph<String> m_gphAssociations;

  public AssociationManager() {
    m_gphAssociations = new Digraph<String>();
  }

  public void addAssociation(String sFirstModel, String sSecondModel, AssociationType atType) {
    addVertices(sFirstModel, sSecondModel);
    addEdge(sFirstModel, sSecondModel, new AssociationMetadata(atType, null, null));
  }

  public void addAssociation(String sFirstModel, String sSecondModel, AssociationType atType, String sAssocName) {
    addVertices(sFirstModel, sSecondModel);
    addEdge(sFirstModel, sSecondModel, new AssociationMetadata(atType, sAssocName, null));
  }

  public void addAssociation(String sFirstModel, String sSecondModel, AssociationType atType, String sAssocName, String sForeignKey) {
    addVertices(sFirstModel, sSecondModel);
    addEdge(sFirstModel, sSecondModel, new AssociationMetadata(atType, sAssocName, sForeignKey));
  }

  private void addVertices(String sFirst, String sSecond) {
    m_gphAssociations.addVertex(sFirst);
    m_gphAssociations.addVertex(sSecond);
  }

  private void addEdge(String sFirst, String sSecond, AssociationMetadata metadata) {
    m_gphAssociations.addEdge(sFirst, sSecond, metadata);
  }

  public AssociationResolver createResolver() {
    return new AssociationResolver(this);
  }

  // @TODO: remove this method
  public void printAssociationJoins() {
    HashMap<AssociationPair, JoinTablePairList> joins = getAssociationJoins();
    Iterator iter = joins.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry e = (Map.Entry)iter.next();
      Pair<String> p = (Pair)e.getKey();
      List<JoinTablePair> j = (List<JoinTablePair>)e.getValue();
      StringBuilder msg = new StringBuilder(p.getFirst() + " and " + p.getSecond() + ": ");

      for (JoinTablePair join : j) {
        msg.append(join.getFirst() + " " + join.m_amAssocMetadata.getType().toString() + " " + join.getSecond() + ", ");
      }

      System.out.println(msg.toString());
    }
  }

  public HashMap<AssociationPair, JoinTablePairList> getAssociationJoins() {
    HashMap<AssociationPair, JoinTablePairList> hmResult = new HashMap<AssociationPair, JoinTablePairList>();
    Set<AssociationPair> spPairs = getVertexPairs();
    Iterator iter = spPairs.iterator();

    while (iter.hasNext()) {
      AssociationPair pair = (AssociationPair)iter.next();
      JoinTablePairList joins = getAssociationJoinsForPair(pair);

      if (joins != null) {
        hmResult.put(pair, getAssociationJoinsForPair(pair));
      }
    }

    return hmResult;
  }

  private JoinTablePairList getAssociationJoinsForPair(AssociationPair pair) {
    ArrayList<Vertex<String>> path = m_gphAssociations.getShortestPath(pair.getFirst(), pair.getSecond());

    if (path == null) {
      return null;
    }

    JoinTablePairList alResult = new JoinTablePairList();

    for (int i = 1; i < path.size(); i ++) {
      Vertex<String> vsFirst = path.get(i - 1);
      Vertex<String> vsSecond = path.get(i);

      AssociationMetadata amAssocMetaData = (AssociationMetadata)m_gphAssociations
        .getVertices().get(vsFirst.getValue())
        .getNeighbors().get(vsSecond.getValue())
        .getMetadata();

      alResult.add(new JoinTablePair(vsFirst.getValue(), vsSecond.getValue(), amAssocMetaData));
    }

    return alResult;
  }

  private Set<AssociationPair> getVertexPairs() {
    Set<String> alKeys = m_gphAssociations.getVertices().keySet();
    Set<AssociationPair> spPairs = new HashSet<AssociationPair>();

    for (String i : alKeys) {
      for (String j : alKeys) {
        if (i != j)
          spPairs.add(new AssociationPair(i, j));
      }
    }

    return spPairs;
  }
}
