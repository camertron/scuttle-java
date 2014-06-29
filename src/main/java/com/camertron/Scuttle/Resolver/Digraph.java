package com.camertron.Scuttle.Resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Digraph<VertexVal> {
  public HashMap<VertexVal, Vertex<VertexVal>> m_vertices;

  public Digraph() {
    m_vertices = new HashMap<VertexVal, Vertex<VertexVal>>();
  }

  public void addVertex(VertexVal value) {
    if (!containsVertex(value)) {
      m_vertices.put(value, createVertex(value));
    }
  }

  public boolean containsVertex(VertexVal value) {
    return m_vertices.containsKey(value);
  }

  public void addEdge(VertexVal firstValue, VertexVal secondValue) {
    addEdge(firstValue, secondValue, null);
  }

  public void addEdge(VertexVal firstValue, VertexVal secondValue, Object metadata) {
    if (!containsEdge(firstValue, secondValue)) {
      m_vertices.get(firstValue).addNeighbor(m_vertices.get(secondValue), metadata);
    }
  }

  public boolean containsEdge(VertexVal firstValue, VertexVal secondValue) {
    return m_vertices.containsKey(firstValue) && m_vertices.get(firstValue).getNeighbors().containsKey(secondValue);
  }

  // Djikstra's shortest path algorithm, adapted from:
  // https://gist.github.com/yaraki/1730288
  public ArrayList<Vertex<VertexVal>> getShortestPath(VertexVal vvSource, VertexVal vvTarget) {
    HashMap<VertexVal, Integer> hmDistances = new HashMap<VertexVal, Integer>();
    HashMap<VertexVal, VertexVal> hmPreviouses = new HashMap<VertexVal, VertexVal>();
    Iterator iter = getVertices().entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry mePair = (Map.Entry)iter.next();
      VertexVal vvKey = (VertexVal)mePair.getKey();
      hmDistances.put(vvKey, null);
      hmPreviouses.put(vvKey, null);
    }

    hmDistances.put(vvSource, 0);

    HashMap<VertexVal, Vertex<VertexVal>> hmVerts = (HashMap<VertexVal, Vertex<VertexVal>>)getVertices().clone();
    VertexVal vvNearestVertex, vvNextVertex;

    while (!hmVerts.isEmpty()) {
      iter = hmVerts.entrySet().iterator();
      vvNearestVertex = (VertexVal)((Map.Entry)iter.next()).getKey();

      while (iter.hasNext()) {
        vvNextVertex = (VertexVal)((Map.Entry)iter.next()).getKey();

        if (hmDistances.get(vvNearestVertex) == null)
          vvNearestVertex = vvNextVertex;
        else if ((hmDistances.get(vvNextVertex) == null) || hmDistances.get(vvNearestVertex) < hmDistances.get(vvNextVertex))
          continue;
        else
          vvNearestVertex = vvNextVertex;
      }

      if (hmDistances.get(vvNearestVertex) == null)
        break;

      if (vvTarget != null && vvNearestVertex == vvTarget)
        return composePath(vvTarget, hmDistances.get(vvTarget), hmPreviouses);

      HashMap<VertexVal, Neighbor<VertexVal>> hmNeighbors = (HashMap<VertexVal, Neighbor<VertexVal>>)hmVerts.get(vvNearestVertex).getNeighbors().clone();
      Iterator neighborsIter = hmNeighbors.entrySet().iterator();

      while (neighborsIter.hasNext()) {
        Map.Entry neighborPair = (Map.Entry)neighborsIter.next();
        VertexVal vvName = (VertexVal)neighborPair.getKey();
        int iAlt = hmDistances.get(vvNearestVertex) + 1;

        if (hmDistances.get(vvName) == null || iAlt < hmDistances.get(vvName)) {
          hmDistances.put(vvName, iAlt);
          hmPreviouses.put(vvName, vvNearestVertex);
        }
      }

      hmVerts.remove(vvNearestVertex);
    }

    return null;
  }

  public HashMap<VertexVal, Vertex<VertexVal>> getVertices() {
    return m_vertices;
  }

  private ArrayList<Vertex<VertexVal>> composePath(VertexVal vvTarget, int iDistance, HashMap<VertexVal, VertexVal> hmPreviouses) {
    ArrayList<Vertex<VertexVal>> alPath = new ArrayList<Vertex<VertexVal>>(iDistance);

    for (int i = 0; i <= iDistance; i ++) { alPath.add(null); }

    for (int i = iDistance; i >= 0; i --) {
      alPath.set(i, m_vertices.get(vvTarget));
      vvTarget = hmPreviouses.get(vvTarget);
    }

    return alPath;
  }

  private Vertex createVertex(VertexVal value) {
    return new Vertex<VertexVal>(value);
  }
}
