package org.ousi.ousi;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Network extends DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> {

    static private int count = 0;

    private int id;
    private String label;
    private String description = "";

    public Network() {
        super(DefaultWeightedEdge.class);
        id = count++;
        label = ((Integer)id).toString();
    }

    public Network(boolean isSubNetwork) {
        super(DefaultWeightedEdge.class);
        if (!isSubNetwork) {
            id = count++;
        }
        label = ((Integer)id).toString();
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
}
