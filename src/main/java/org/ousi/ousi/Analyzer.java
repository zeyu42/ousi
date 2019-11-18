package org.ousi.ousi;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Analyzer {
    private static double getDensity(Network network) {
        int ne = network.edgeSet().size();
        int nv = network.vertexSet().size();
        return (float)ne / nv / (nv - 1);
    }

    public static String densityString(Network network) {
        double density = getDensity(network);
        return "Density: " + density;
    }
}
