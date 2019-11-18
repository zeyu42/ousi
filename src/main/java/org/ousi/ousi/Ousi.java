package org.ousi.ousi;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Ousi {

    // Currently, settings only include info about visualization threshold.
    private Settings settings = new Settings();

    private LinkedList<Network> networks = new LinkedList<>();
    private LinkedList<Network> subNetworks = new LinkedList<>(); // The reduced networks for visualization

    public LinkedList<Network> getNetworks() {
        return networks;
    }

    public LinkedList<Network> getSubNetworks() {
        return subNetworks;
    }

    void createRandomNetwork(int n, double p) {
        networks.addLast(RandomGraphFactory.getRandomNetwork(n, p));
        subNetworks.addLast(subNetwork(networks.getLast()));
    }

    private Network subNetwork(Network network) {
        if (settings.getUseDegreeThreshold()) {
            int degreeThreshold = settings.getDegreeThreshold();
            int n = network.vertexSet().size();
            Network subNetwork = new Network(true);
            HashSet<Vertex> vertices = new HashSet<>();
            // Add vertices whose degree are no less than the threshold to the subNetwork
            for (Vertex vertex : network.vertexSet()) {
                if (network.degreeOf(vertex) >= degreeThreshold) {
                    subNetwork.addVertex(vertex);
                    vertices.add(vertex);
                }
            }
            // Add the edges between those vertices to the subgraph
            for (Vertex vertex1 : network.vertexSet()) {
                for (Vertex vertex2 : network.vertexSet()) {
                    if (vertices.contains(vertex1) && vertices.contains(vertex2) && network.containsEdge(vertex1, vertex2)) {
                        subNetwork.addEdge(vertex1, vertex2, network.getEdge(vertex1, vertex2));
                    }
                }
            }
            return subNetwork;
        } else {
            return network;
        }
    }

    public void removeNetwork(int index) {
        networks.remove(index);
        subNetworks.remove(index);
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
