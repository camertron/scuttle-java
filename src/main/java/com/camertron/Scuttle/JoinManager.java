package com.camertron.Scuttle;

import com.camertron.Scuttle.Resolver.AssociationChain;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import com.camertron.Scuttle.Resolver.JoinColumnPair;
import com.camertron.Scuttle.Resolver.JoinColumnPairList;

import java.util.ArrayList;
import java.util.List;

public class JoinManager {
  private AssociationResolver m_arResolver;
  private List<JoinVisitor> m_ljvJoins;

  public JoinManager(ArrayList<JoinVisitor> ljvJoins, AssociationResolver arResolver) {
    m_arResolver = arResolver;
    m_ljvJoins = ljvJoins;
  }

  // @TODO: handle outer joins
  public String toString() {
    if (allJoinsAreActiveRecordCompatible()) {
      return toActiveRecordString();
    } else {
      return toArelString();
    }
  }

  private String toActiveRecordString() {
    JoinColumnPairList jcPairs = new JoinColumnPairList();
    JoinConditionVisitor jcCurVisitor;

    for (JoinVisitor join : m_ljvJoins) {
      jcCurVisitor = join.getConditionVisitor();
      jcPairs.addPair(
        Inflector.pluralize(Inflector.underscore(join.getTableName())),
        Inflector.pluralize(Inflector.underscore(jcCurVisitor.getFirstTableName())), jcCurVisitor.getFirstColumnName(),
        Inflector.pluralize(Inflector.underscore(jcCurVisitor.getSecondTableName())), jcCurVisitor.getSecondColumnName()
      );
    }

    AssociationChain chain = m_arResolver.getAssociationChainForJoins(jcPairs);

    if (chain == null) {
      return toArelString();
    } else {
      return ".joins(" + chain.toString() + ")";
    }
  }

  private String toArelString() {
    StringBuilder sbJoins = new StringBuilder();

    if (!m_ljvJoins.isEmpty()) {
      for(JoinVisitor join : m_ljvJoins) {
        sbJoins.append(".joins(" + join.toString() + ".join_sources)");
      }
    }

    return sbJoins.toString();
  }

  private boolean allJoinsAreActiveRecordCompatible() {
    for (JoinVisitor join : m_ljvJoins) {
      if (!join.isActiveRecordCompatible())
        return false;
    }

    return true;
  }
}
