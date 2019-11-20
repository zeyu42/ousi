package org.ousi.ousi;

import java.util.ArrayList;
import java.util.Random;

class RandomGraphFactory {
    static Network getRandomNetwork(int n, double p, boolean isDirected, boolean hasWeight, int weightLowerBound, int weightUpperBound) {
        Random random = new Random();
        Network graph = new Network(isDirected, hasWeight);
        ArrayList<Vertex> vertices = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vertices.add(new Vertex());
            graph.addVertex(vertices.get(i));
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (isDirected) {
                    if (i != j && random.nextDouble() < p) {
                        if (hasWeight) {
                            double weight = random.nextInt(weightUpperBound - weightLowerBound) + weightLowerBound;
                            graph.addEdge(vertices.get(i), vertices.get(j), weight);
                        } else {
                            graph.addEdge(vertices.get(i), vertices.get(j));
                        }
                    }
                } else {
                    if (i < j && random.nextDouble() < p) {
                        if (hasWeight) {
                            double weight = random.nextInt(weightUpperBound - weightLowerBound) + weightLowerBound;
                            graph.addEdge(vertices.get(i), vertices.get(j), weight);
                            graph.addEdge(vertices.get(j), vertices.get(i), weight);
                        } else {
                            graph.addEdge(vertices.get(i), vertices.get(j));
                            graph.addEdge(vertices.get(j), vertices.get(i));
                        }
                    }
                }
            }
        }
        return graph;
    }
}
