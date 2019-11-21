package org.ousi.ousi;

import de.wathoserver.vaadin.visjs.network.NetworkDiagram;
import de.wathoserver.vaadin.visjs.network.Node;
import de.wathoserver.vaadin.visjs.network.options.Options;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Network implements Serializable {

    static private int count = 0;

    private int id;
    private String label;
    private String description = "";
    private boolean isDirected;
    private boolean hasWeight;

    private HashMap<Vertex, HashSet<Edge>> vertexToEdges;
    private HashMap<Vertex, HashSet<Vertex>> vertexToVertices;

    // Number of edges
    private int m = 0;

    // Diagram
    private Settings settings = null;
    private NetworkDiagram networkDiagram = null;
    private LinkedList<Node> nodeList = null;
    private LinkedList<de.wathoserver.vaadin.visjs.network.Edge> edgeList = null;

    public Network() {
        id = count++;
        label = ((Integer)id).toString();
        isDirected = true;
        hasWeight = false;
        vertexToEdges = new HashMap<>();
        vertexToVertices = new HashMap<>();
    }

    public Network(boolean isDirected) {
        this();
        this.isDirected = isDirected;
    }

    public Network(boolean isDirected, boolean hasWeight) {
        this(isDirected);
        this.hasWeight = hasWeight;
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
        addVertex(vertex);
    }

    public void addVertex() {
        addVertex("");
    }

    void addVertex(Vertex vertex) {
        if (vertexToEdges.containsKey(vertex)) {
            return;
        }
        vertexToEdges.put(vertex, new HashSet<>());
        vertexToVertices.put(vertex, new HashSet<>());
        if (networkDiagram != null && (!settings.getUseDegreeThreshold() || degreeOf(vertex) >= settings.getDegreeThreshold())) {
            nodeList.add(new Node(String.valueOf(vertex.getId())));
        }
    }

    public void addEdge(Edge edge) {
        Vertex from = edge.getFrom();
        Vertex to = edge.getTo();
        boolean containsFrom = vertexToEdges.containsKey(from);
        vertexToEdges.computeIfAbsent(from, (k) -> new HashSet<>()).add(edge);
        boolean containsTo = vertexToEdges.containsKey(to);
        vertexToVertices.computeIfAbsent(from, (k) -> new HashSet<>()).add(to);
        vertexToEdges.computeIfAbsent(to, (k) -> new HashSet<>());
        vertexToVertices.computeIfAbsent(to, (k) -> new HashSet<>());
        m++;
        if (networkDiagram != null && (!settings.getUseDegreeThreshold() || (degreeOf(from) >= settings.getDegreeThreshold() && degreeOf(to) >= settings.getDegreeThreshold()))) {
            String fromId = String.valueOf(from.getId());
            if (!containsFrom) {
                nodeList.add(new Node(fromId));
            }
            String toId = String.valueOf(to.getId());
            if (!containsTo) {
                nodeList.add(new Node(toId));
            }
            edgeList.add(new de.wathoserver.vaadin.visjs.network.Edge(fromId, toId));
            networkDiagram.setNodes(nodeList);
            networkDiagram.setEdges(edgeList);
        }
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

    private int degreeOf(Vertex vertex) {
        // out degree if directed graph
        return vertexToEdges.get(vertex).size();
    }

    public int getM() {
        if (isDirected) {
            return m;
        } else {
            return m / 2;
        }
    }

    public int getN() {
        return vertexToEdges.size();
    }

    NetworkDiagram getNetworkDiagram(boolean force, Settings settings) {
        if (networkDiagram == null || force) {
            this.settings = settings;
            createNetworkDiagram();
        }
        return networkDiagram;
    }

    private void createNetworkDiagram() {
        networkDiagram = new NetworkDiagram(Options.builder().withAutoResize(true).build());
        nodeList = new LinkedList<>();
        edgeList = new LinkedList<>();
        for (Vertex from : vertexToEdges.keySet()) {
            if (settings.getUseDegreeThreshold() && degreeOf(from) < settings.getDegreeThreshold()) {
                continue;
            }
            String fromId = String.valueOf(from.getId());
            nodeList.add(new Node(fromId));
            for (Edge edge : vertexToEdges.get(from)) {
                Vertex to = edge.getTo();
                if (settings.getUseDegreeThreshold() && degreeOf(to) < settings.getDegreeThreshold()) {
                    continue;
                }
                String toId = String.valueOf(to.getId());
                edgeList.add(new de.wathoserver.vaadin.visjs.network.Edge(fromId, toId));
            }
        }
        networkDiagram.setNodes(nodeList);
        networkDiagram.setEdges(edgeList);
    }

    String toDOT() {
        StringBuilder DOT = new StringBuilder();
        if (isDirected) {
            // currently always true
            // ignore weight for now
            DOT.append("digraph ").append("G").append(" {\n");
            for (Vertex from : vertexToEdges.keySet()) {
                for (Edge edge : getEdges(from)) {
                    Vertex to = edge.getTo();
                    long fromId = from.getId();
                    long toId = to.getId();
                    DOT.append("  ").append(fromId).append(" -> ").append(toId);
                    if (hasWeight) {
                        DOT.append(" [label = \"").append(edge.getWeight()).append("\"]");
                    }
                    DOT.append(";\n");
                }
            }
            DOT.append("}\n");
        } else {
            // currently always true
            // ignore weight for now
            DOT.append("graph ").append("G").append(" {\n");
            for (Vertex from : vertexToEdges.keySet()) {
                for (Edge edge : getEdges(from)) {
                    Vertex to = edge.getTo();
                    long fromId = from.getId();
                    long toId = to.getId();
                    if (fromId > toId) {
                        continue;
                    }
                    DOT.append("  ").append(fromId).append(" -- ").append(toId);
                    if (hasWeight) {
                        DOT.append(" [label = \"").append(edge.getWeight()).append("\"]");
                    }
                    DOT.append(";\n");
                }
            }
            DOT.append("}\n");
        }
        return DOT.toString();
    }

    boolean getIsDirected() {
        return isDirected;
    }

    boolean getHasWeight() {
        return hasWeight;
    }
}
