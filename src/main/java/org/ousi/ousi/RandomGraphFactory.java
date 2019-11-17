package org.ousi.ousi;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Random;

public class RandomGraphFactory {
    static public Graph<Vertex, DefaultWeightedEdge> getRandomGraph(int n, double p) {
        Random random = new Random();
        Graph<Vertex, DefaultWeightedEdge> graph = new DefaultDirectedGraph<>(DefaultWeightedEdge.class);
        ArrayList<Vertex> vertices = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vertices.add(new Vertex());
            graph.addVertex(vertices.get(i));
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && random.nextDouble() < p) {
                    graph.addEdge(vertices.get(i), vertices.get(j));
                }
            }
        }
        return graph;
    }
}
