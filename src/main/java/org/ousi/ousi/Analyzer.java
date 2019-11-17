package org.ousi.ousi;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Analyzer {
    private static double getDensity(Graph<Vertex, DefaultWeightedEdge> graph) {
        int ne = graph.edgeSet().size();
        int nv = graph.vertexSet().size();
        return (float)ne / nv / (nv - 1);
    }

    public static String densityString(Graph<Vertex, DefaultWeightedEdge> graph) {
        double density = getDensity(graph);
        return "Density: " + density;
    }
}
