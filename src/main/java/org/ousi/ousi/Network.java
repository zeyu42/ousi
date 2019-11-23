package org.ousi.ousi;

import de.wathoserver.vaadin.visjs.network.NetworkDiagram;
import de.wathoserver.vaadin.visjs.network.Node;
import de.wathoserver.vaadin.visjs.network.options.Options;

import java.io.Serializable;
import java.util.*;

public class Network implements Serializable {

    static private int count = 0;

    private int id;
    private String label;
    private String description = "";
    private boolean isDirected;
    private boolean hasWeight;

    private LinkedHashMap<Vertex, HashSet<Edge>> vertexToEdges; // Use LinkedHashMap to make sure the order of iteration is the same as the order the items were inserted
    private LinkedHashMap<Vertex, HashSet<Vertex>> vertexToVertices;

    // Number of edges
    private int m = 0;

    // Diagram
    private transient Settings settings = null;
    private transient NetworkDiagram networkDiagram = null;
    private transient LinkedList<Node> nodeList = null;
    private transient LinkedList<de.wathoserver.vaadin.visjs.network.Edge> edgeList = null;

    public Network() {
        id = count++;
        label = ((Integer)id).toString();
        isDirected = true;
        hasWeight = false;
        vertexToEdges = new LinkedHashMap<>();
        vertexToVertices = new LinkedHashMap<>();
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

    public boolean isSimple() {
        for (Vertex vertex : vertexToEdges.keySet()) {
            if (vertexToEdges.get(vertex).size() > vertexToVertices.get(vertex).size()) {
                return false;
            }
        }
        return true;
    }

    static Network fromAdjacencyListString(String adjacencyListString) throws InputMismatchException {
        Scanner scanner = new Scanner(adjacencyListString);

        // Read label and description if they exist
        String label = "";
        String description = "";
        if (adjacencyListString.startsWith("[")) {
            label = scanner.next();
            label = label.replace("[", "");
            if (!label.endsWith("]")) {
                description = scanner.nextLine();
                description = description.replace("]", "");
                description = description.replaceFirst(" ", "");
            } else {
                label = label.replace("]", "");
            }
        }

        // Read whether it is a directed/weighted network
        int isDirectedInt = scanner.nextInt();
        boolean isDirected = isDirectedInt == 1;
        int hasWeightInt = scanner.nextInt();
        boolean hasWeight = hasWeightInt == 1;

        Network network = new Network(isDirected, hasWeight);
        if (!label.equals("")) {
            network.setLabel(label);
        }
        network.setDescription(description);
        scanner.nextLine();

        // Vertex list
        HashMap<Long, Vertex> vertexMap = new HashMap<>();
        String line = scanner.nextLine();
        Scanner vertexScanner = new Scanner(line);
        while (vertexScanner.hasNextLong()) {
            Long vertexLabel = vertexScanner.nextLong();
            Vertex vertex = network.addVertex();
            vertexMap.put(vertexLabel, vertex);
        }
        vertexScanner.close();

        // Edge list
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.equals("")) {
                break;
            }
            Scanner edgeScanner = new Scanner(line);
            // The first long represents the from vertex.
            Long fromLabel = edgeScanner.nextLong();
            Vertex from;
            if (!vertexMap.containsKey(fromLabel)) {
                from = network.addVertex();
                vertexMap.put(fromLabel, from);
            } else {
                from = vertexMap.get(fromLabel);
            }
            // The following are the to vertices.
            while (edgeScanner.hasNextLong()) {
                Long toLabel = edgeScanner.nextLong();
                Vertex to;
                if (!vertexMap.containsKey(toLabel)) {
                    to = network.addVertex();
                    vertexMap.put(toLabel, to);
                } else {
                    to = vertexMap.get(toLabel);
                }
                double weight = 0;
                if (hasWeight) {
                    weight = edgeScanner.nextDouble();
                }
                if (isDirected) {
                    network.addEdge(from, to, weight);
                } else {
                    network.addEdge(from, to, weight);
                    network.addEdge(to, from, weight);
                }
            }
        }
        scanner.close();
        return network;
    }

    private Vertex addVertex() {
        Vertex vertex = new Vertex();
        addVertex(vertex);
        return vertex;
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
                if (!isDirected && from.getId() > to.getId() || settings.getUseDegreeThreshold() && degreeOf(to) < settings.getDegreeThreshold()) {
                    continue;
                }
                String toId = String.valueOf(to.getId());
                de.wathoserver.vaadin.visjs.network.Edge visjsEdge = new de.wathoserver.vaadin.visjs.network.Edge(fromId, toId);
                if (isDirected) {
                    visjsEdge.setArrows("to");
                }
                edgeList.add(visjsEdge);
            }
        }
        networkDiagram.setNodes(nodeList);
        networkDiagram.setEdges(edgeList);
    }

    Vertex addVertex(Vertex vertex) {
        if (vertexToEdges.containsKey(vertex)) {
            return vertex;
        }
        vertexToEdges.put(vertex, new HashSet<>());
        vertexToVertices.put(vertex, new HashSet<>());
        if (networkDiagram != null && (!settings.getUseDegreeThreshold() || degreeOf(vertex) >= settings.getDegreeThreshold())) {
            nodeList.add(new Node(String.valueOf(vertex.getId())));
        }
        return vertex;
    }

    boolean getIsDirected() {
        return isDirected;
    }

    boolean getHasWeight() {
        return hasWeight;
    }

    String toDOTString() {
        StringBuilder DOT = new StringBuilder();
        if (isDirected) {
            DOT.append("digraph ").append("G").append(" {\n  ");
            for (Vertex vertex : vertexToEdges.keySet()) {
                DOT.append(vertex.getId()).append(" ; ");
            }
            DOT.append("\n");
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
            DOT.append("graph ").append("G").append(" {\n  ");
            for (Vertex vertex : vertexToEdges.keySet()) {
                DOT.append(vertex.getId()).append(" ; ");
            }
            DOT.append("\n");
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

    String toAdjacencyListString() {
        StringBuilder adjacencyListStringBuilder = new StringBuilder();
        // Label and description
        adjacencyListStringBuilder.append("[").append(label).append(" ").append(description).append("]\n");
        // Whether it's directed/weighted
        if (isDirected) {
            adjacencyListStringBuilder.append(1);
        } else {
            adjacencyListStringBuilder.append(0);
        }
        adjacencyListStringBuilder.append(" ");
        if (hasWeight) {
            adjacencyListStringBuilder.append(1);
        } else {
            adjacencyListStringBuilder.append(0);
        }
        adjacencyListStringBuilder.append("\n");
        // Vertices
        boolean first = true;
        for (Vertex vertex : vertexToEdges.keySet()) {
            if (first) {
                first = false;
            } else {
                adjacencyListStringBuilder.append(" ");
            }
            adjacencyListStringBuilder.append(vertex.getId());
        }
        adjacencyListStringBuilder.append("\n");
        // Edges
        for (Vertex from : vertexToEdges.keySet()) {
            first = true;
            adjacencyListStringBuilder.append(from.getId()).append(" ");
            for (Edge edge : vertexToEdges.get(from)) {
                Vertex to = edge.getTo();
                if (isDirected && from.getId() > to.getId()) {
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    adjacencyListStringBuilder.append(" ");
                }
                adjacencyListStringBuilder.append(to.getId());
                if (hasWeight) {
                    adjacencyListStringBuilder.append(" ").append(edge.getWeight());
                }
            }
            adjacencyListStringBuilder.append("\n");
        }
        return adjacencyListStringBuilder.toString();
    }
}
