package org.ousi.ousi;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Transformer {
    static public void add(Graph<Vertex, DefaultWeightedEdge> destination, Graph<Vertex, DefaultWeightedEdge> source) {
        Graphs.addGraph(destination, source);
    }
}
