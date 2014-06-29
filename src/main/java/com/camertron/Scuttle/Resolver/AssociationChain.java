package com.camertron.Scuttle.Resolver;

import com.camertron.Scuttle.Utils;

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
    String sFirstSymbol = Utils.symbolize(m_saChain.get(index));

    if (index == m_saChain.size() - 1) {
      return sFirstSymbol;
    } else if (index == m_saChain.size() - 2) {
      return sFirstSymbol + " => " + chainToString(index + 1);
    } else {
      return sFirstSymbol + " => { " + chainToString(index + 1) + " }";
    }
  }
}
