package com.camertron.Scuttle.Resolver;

import java.util.ArrayList;
import java.util.List;

public class AssociationChain {
  private List<String> m_saChain;

  public AssociationChain() {
    m_saChain = new ArrayList<String>();
  }

  public AssociationChain(List<String> saChain) {
    m_saChain = saChain;
  }

  public void add(String sChainElement) {
    m_saChain.add(sChainElement);
  }

  public String toString() {
    return chainToString(0);
  }

  private String chainToString(int index) {
    if (index == m_saChain.size() - 1) {
      return symbolize(m_saChain.get(index));
    } else {
      return symbolize(m_saChain.get(index)) + " => { " + chainToString(index + 1) + " }";
    }
  }

  private String symbolize(String sStr) {
    return ":" + sStr;
  }
}
