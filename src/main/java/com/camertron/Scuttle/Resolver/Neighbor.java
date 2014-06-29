package com.camertron.Scuttle.Resolver;

public class Neighbor<VertexVal> {
  private Vertex<VertexVal> m_target;
  private Object m_metadata;

  public Neighbor(Vertex<VertexVal> target) {
    m_target = target;
  }

  public Neighbor(Vertex<VertexVal> target, Object metadata) {
    m_target = target;
    m_metadata = metadata;
  }

  public Vertex<VertexVal> getTarget() {
    return m_target;
  }

  public Object getMetadata() {
    return m_metadata;
  }
}
