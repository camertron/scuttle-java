package com.camertron.Scuttle.Resolver;

public class Pair<T> {
  protected T m_first;
  protected T m_second;

  public Pair() {
    m_first = null;
    m_second = null;
  }

  public Pair(T first, T second) {
    m_first = first;
    m_second = second;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Pair)) return false;
    Pair tmp = (Pair)obj;
    return ((tmp.getFirst() == m_first && tmp.getSecond() == m_second));
  }

  public T getFirst() {
    return m_first;
  }

  public T getSecond() {
    return m_second;
  }
}
