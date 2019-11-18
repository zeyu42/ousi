package org.ousi.ousi;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Random;

class RandomGraphFactory {
    static Network getRandomNetwork(int n, double p) {
        Random random = new Random();
        Network graph = new Network();
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
