package org.ousi.ousi;

import java.io.Serializable;

public class Edge implements Serializable {
    private Vertex from;
    private Vertex to;
    private double weight = 0;

    Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
    }

    Edge(Vertex from, Vertex to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    Edge(Edge edge) {
        this.from = edge.from;
        this.to = edge.to;
        this.weight = edge.weight;
    }

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
