package org.ousi.ousi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Network implements Serializable {

    static private int count = 0;

    private int id;
    private String label;
    private String description = "";

    private HashMap<Vertex, HashSet<Edge>> vertexToEdges;
    private HashMap<Vertex, HashSet<Vertex>> vertexToVertices;

    private int m = 0;

    public Network() {
        id = count++;
        label = ((Integer)id).toString();
        vertexToEdges = new HashMap<>();
        vertexToVertices = new HashMap<>();
    }

    public Network(boolean isSubNetwork) {
        if (!isSubNetwork) {
            id = count++;
        }
        label = ((Integer)id).toString();
        vertexToEdges = new HashMap<>();
        vertexToVertices = new HashMap<>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private void addVertex(String label) {
        Vertex vertex = new Vertex(label);
        vertexToEdges.put(vertex, new HashSet<>());
        vertexToVertices.put(vertex, new HashSet<>());
    }

    public void addVertex() {
        addVertex("");
    }

    void addVertex(Vertex vertex) {
        vertexToEdges.put(vertex, new HashSet<>());
        vertexToVertices.put(vertex, new HashSet<>());
    }

    public void addEdge(Edge edge) {
        Vertex from = edge.getFrom();
        Vertex to = edge.getTo();
        vertexToEdges.getOrDefault(from, new HashSet<>()).add(edge);
        vertexToVertices.getOrDefault(from, new HashSet<>()).add(to);
        m++;
    }

    public void addEdge(Vertex from, Vertex to) {
        addEdge(new Edge(from, to));
    }

    public void addEdge(Vertex from, Vertex to, double weight) {
        addEdge(new Edge(from, to, weight));
    }

    public Set<Vertex> getVertices() {
        return vertexToEdges.keySet();
    }

    boolean containsVertex(Vertex vertex) {
        return vertexToEdges.containsKey(vertex);
    }

    public boolean containsEdge(Vertex from, Vertex to) {
        return vertexToEdges.containsKey(from) && vertexToVertices.get(from).contains(to);
    }

    Set<Edge> getEdges(Vertex vertex) {
        return vertexToEdges.get(vertex);
    }

    int degreeOf(Vertex vertex) {
        return vertexToEdges.get(vertex).size();
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return vertexToEdges.size();
    }

}
