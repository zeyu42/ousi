package org.ousi.ousi;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Transformer {
    static public void add(Network destination, Network source) {
        Graphs.addGraph(destination, source);
    }
}
