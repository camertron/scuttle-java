package com.camertron.Scuttle.Resolver;

import com.camertron.Scuttle.Inflector;

import java.util.Arrays;

public class JoinTablePair extends Pair<String> {
  protected AssociationType m_atAssocType;

  public JoinTablePair(String first, String second) {
    m_first = first;
    m_second = second;
  }

  public JoinTablePair(String first, String second, AssociationType atAssocType) {
    m_first = first;
    m_second = second;
    m_atAssocType = atAssocType;
  }

  public String getLeftHandTableName() {
    return getFirst();
  }

  public String getRightHandTableName() {
    String sRightHandValue = getSecond();

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

  public JoinColumnPairList getJoins() {
    return getJoinsForAssociationType(m_atAssocType);
  }

  private JoinColumnPairList getJoinsForAssociationType(AssociationType atAssocType) {
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

  private JoinColumnPairList getJoinsForHasMany() {
    JoinColumnPairList ljColumns = new JoinColumnPairList();
    String sFirstCol = deriveColumnIdFromTable(getFirst());
    ColumnRef crFirstRef = new ColumnRef(getSecond(), sFirstCol);
    ColumnRef crSecondRef = new ColumnRef(getFirst(), "id");
    ljColumns.add(new JoinColumnPair(crSecondRef, crFirstRef, getSecond()));
    return ljColumns;
  }

  private JoinColumnPairList getJoinsForBelongsTo() {
    JoinColumnPairList ljColumns = new JoinColumnPairList();
    String sFirstCol = deriveColumnIdFromTable(getSecond());
    ColumnRef crFirstRef = new ColumnRef(getFirst(), sFirstCol);
    ColumnRef crSecondRef = new ColumnRef(getSecond(), "id");
    ljColumns.add(new JoinColumnPair(crFirstRef, crSecondRef, getSecond()));
    return ljColumns;
  }

  private JoinColumnPairList getJoinsForHasOne() {
    return getJoinsForHasMany();
  }

  private JoinColumnPairList getJoinsForHasAndBelongsToMany() {
    JoinColumnPairList ljColumns = new JoinColumnPairList();
    String sJoinTableName = deriveJoinTableName();

    ColumnRef crFirstRef = new ColumnRef(getFirst(), "id");
    ColumnRef crSecondRef = new ColumnRef(sJoinTableName, deriveColumnIdFromTable(getFirst()));
    ljColumns.add(new JoinColumnPair(crFirstRef, crSecondRef, getSecond()));

    crFirstRef = new ColumnRef(sJoinTableName, deriveColumnIdFromTable(getSecond()));
    crSecondRef = new ColumnRef(getSecond(), "id");
    ljColumns.add(new JoinColumnPair(crFirstRef, crSecondRef, getSecond()));

    return ljColumns;
  }

  private String deriveColumnIdFromTable(String sTableName) {
    return Inflector.singularize(sTableName) + "_id";
  }

  private String deriveJoinTableName() {
    String[] saTableParts = new String[] { getFirst(), getSecond() };
    Arrays.sort(saTableParts);
    return saTableParts[0] + "_" + saTableParts[1];
  }
}
