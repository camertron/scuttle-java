package com.camertron.Scuttle.Resolver;

import java.util.HashMap;

public class Vertex<VertexVal> {
  private VertexVal m_value;
  private HashMap<VertexVal, Neighbor<VertexVal>> m_neighbors;

  public Vertex(VertexVal value) {
    m_value = value;
    m_neighbors = new HashMap<VertexVal, Neighbor<VertexVal>>();
  }

  public void addNeighbor(Vertex<VertexVal> vertex) {
    addNeighbor(vertex, null);
  }

  public void addNeighbor(Vertex<VertexVal> vertex, Object metadata) {
    if (!contains(vertex.getValue())) {
      m_neighbors.put(vertex.getValue(), new Neighbor(vertex, metadata));
    }
  }

  public boolean contains(VertexVal value) {
    return m_neighbors.containsKey(value);
  }

  public VertexVal getValue() {
    return m_value;
  }

  public HashMap<VertexVal, Neighbor<VertexVal>> getNeighbors() {
    return m_neighbors;
  }
}
