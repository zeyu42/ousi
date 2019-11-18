package org.ousi.ousi;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Iterator;
import java.util.LinkedList;

public class Ousi {

    // Currently, settings only include info about visualization threshold.
    private Settings settings = new Settings();

    private LinkedList<Graph<Vertex, DefaultWeightedEdge>> graphs = new LinkedList<>();
    private LinkedList<Graph<Vertex, DefaultWeightedEdge>> subgraphs = new LinkedList<>(); // The reduced graphs for visualization

    public LinkedList<Graph<Vertex, DefaultWeightedEdge>> getGraphs() {
        return graphs;
    }

    public LinkedList<Graph<Vertex, DefaultWeightedEdge>> getSubgraphs() {
        return subgraphs;
    }

    public void createRandomGraph(int n, double p) {
        graphs.addLast(RandomGraphFactory.getRandomGraph(n, p));
        subgraphs.addLast(subgraph(graphs.getLast()));
    }

    private Graph<Vertex, DefaultWeightedEdge> subgraph(Graph<Vertex, DefaultWeightedEdge> graph) {
        if (settings.getUseDegreeThreshold()) {
            int degreeThreshold = settings.getDegreeThreshold();
            int n = graph.vertexSet().size();
            Graph<Vertex, DefaultWeightedEdge> subgraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            // Add vertices whose degree are no less than the threshold to the subgraph
            for (Vertex vertex : graph.vertexSet()) {
                if (graph.degreeOf(vertex) >= degreeThreshold) {
                    subgraph.addVertex(vertex);
                }
            }
            // Add the edges between those vertices to the subgraph
            for (Vertex vertex1 : graph.vertexSet()) {
                for (Vertex vertex2 : graph.vertexSet()) {
                    if (graph.containsEdge(vertex1, vertex2)) {
                        subgraph.addEdge(vertex1, vertex2, graph.getEdge(vertex1, vertex2));
                    }
                }
            }
            return subgraph;
        } else {
            return graph;
        }
    }

    public void removeGraph(int index) {
        graphs.remove(index);
        subgraphs.remove(index);
    }

    public Settings getSettings() {
        return settings;
    }

    public static void main(String[] args) {
        Ousi ousi = new Ousi();
//        ousi.getSettings().setUseDegreeThreshold(true);
//        ousi.getSettings().setDegreeThreshold(50);
//        ousi.getSettings().writeSettings();
        System.out.println(ousi.getSettings().getUseDegreeThreshold());
        System.out.println(ousi.getSettings().getDegreeThreshold());
    }

}
