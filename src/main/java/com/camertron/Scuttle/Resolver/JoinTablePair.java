package com.camertron.Scuttle.Resolver;

import com.camertron.Scuttle.Inflector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoinTablePair extends Pair<Vertex<String>> {
  protected AssociationType m_atAssocType;

  public JoinTablePair(Vertex<String> first, Vertex<String> second) {
    m_first = first;
    m_second = second;
  }

  public JoinTablePair(Vertex<String> first, Vertex<String> second, AssociationType atAssocType) {
    m_first = first;
    m_second = second;
    m_atAssocType = atAssocType;
  }

  public String getLeftHandTableName() {
    return getFirst().getValue();
  }

  public String getRightHandTableName() {
    String sRightHandValue = getSecond().getValue();

    switch (m_atAssocType) {
      case HAS_MANY:
      case HAS_AND_BELONGS_TO_MANY:
        return sRightHandValue;
      case BELONGS_TO:
      case HAS_ONE:
        return Inflector.singularize(sRightHandValue);
      default:
        return "";
    }
  }

  public List<JoinColumnPair> getJoins() {
    return getJoinsForAssociationType(m_atAssocType);
  }

  private List<JoinColumnPair> getJoinsForAssociationType(AssociationType atAssocType) {
    switch (atAssocType) {
      case HAS_MANY:
        return getJoinsForHasMany();
      case BELONGS_TO:
        return getJoinsForBelongsTo();
      case HAS_ONE:
        return getJoinsForHasOne();
      case HAS_AND_BELONGS_TO_MANY:
        return getJoinsForHasAndBelongsToMany();
      default:
        return null;
    }
  }

  private List<JoinColumnPair> getJoinsForHasMany() {
    List<JoinColumnPair> ljColumns = new ArrayList<JoinColumnPair>();
    String sFirstCol = deriveColumnIdFromTable(getFirst().getValue());
    ColumnRef crFirstRef = new ColumnRef(getSecond().getValue(), sFirstCol);
    ColumnRef crSecondRef = new ColumnRef(getFirst().getValue(), "id");
    ljColumns.add(new JoinColumnPair(crSecondRef, crFirstRef, getSecond().getValue()));
    return ljColumns;
  }

  private List<JoinColumnPair> getJoinsForBelongsTo() {
    List<JoinColumnPair> ljColumns = new ArrayList<JoinColumnPair>();
    String sFirstCol = deriveColumnIdFromTable(getSecond().getValue());
    ColumnRef crFirstRef = new ColumnRef(getFirst().getValue(), sFirstCol);
    ColumnRef crSecondRef = new ColumnRef(getSecond().getValue(), "id");
    ljColumns.add(new JoinColumnPair(crFirstRef, crSecondRef, getSecond().getValue()));
    return ljColumns;
  }

  private List<JoinColumnPair> getJoinsForHasOne() {
    return getJoinsForHasMany();
  }

  private List<JoinColumnPair> getJoinsForHasAndBelongsToMany() {
    List<JoinColumnPair> ljColumns = new ArrayList<JoinColumnPair>();
    String sJoinTableName = deriveJoinTableName();

    ColumnRef crFirstRef = new ColumnRef(getFirst().getValue(), "id");
    ColumnRef crSecondRef = new ColumnRef(sJoinTableName, deriveColumnIdFromTable(getFirst().getValue()));
    ljColumns.add(new JoinColumnPair(crFirstRef, crSecondRef, getSecond().getValue()));

    crFirstRef = new ColumnRef(sJoinTableName, deriveColumnIdFromTable(getSecond().getValue()));
    crSecondRef = new ColumnRef(getSecond().getValue(), "id");
    ljColumns.add(new JoinColumnPair(crFirstRef, crSecondRef, getSecond().getValue()));

    return ljColumns;
  }

  private String deriveColumnIdFromTable(String sTableName) {
    return Inflector.singularize(sTableName) + "_id";
  }

  private String deriveJoinTableName() {
    String[] saTableParts = new String[] { getFirst().getValue(), getSecond().getValue() };
    Arrays.sort(saTableParts);
    return saTableParts[0] + "_" + saTableParts[1];
  }
}
