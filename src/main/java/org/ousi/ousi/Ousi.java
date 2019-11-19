package org.ousi.ousi;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.Span;

import java.util.HashSet;
import java.util.LinkedList;

public class Ousi {

    // Currently, settings only include info about visualization threshold.
    private Settings settings = new Settings();

    // The output accordion created in MainView.java and to be set from there
    private Accordion outputAccordion = null;

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
        addToAccordion("Generate -> Random Network", "Created random network with " + n + " nodes and " + p + " link prob.\n");
    }


    private Network subNetwork(Network network) {
        if (settings.getUseDegreeThreshold()) {
            int degreeThreshold = settings.getDegreeThreshold();
            int n = network.getVertices().size();
            Network subNetwork = new Network(true);
            HashSet<Vertex> vertices = new HashSet<>();
            // Add vertices whose degree are no less than the threshold to the subNetwork
            for (Vertex vertex : network.getVertices()) {
                if (network.degreeOf(vertex) >= degreeThreshold) {
                    subNetwork.addVertex(vertex);
                    vertices.add(vertex);
                }
            }
            // Add the edges between those vertices to the subgraph
            for (Vertex vertex : network.getVertices()) {
                if (subNetwork.containsVertex(vertex)) {
                    for (Edge edge : network.getEdges(vertex)) {
                        if (subNetwork.containsVertex(edge.getTo())) {
                            subNetwork.addEdge(edge);
                        }
                    }
                }
            }
            return subNetwork;
        } else {
            return network;
        }
    }

    void removeNetwork(Network network) {
        int index = networks.indexOf(network);
        if (index == -1) {
            return;
        }
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

    void setOutputAccordion(Accordion outputAccordion) {
        this.outputAccordion = outputAccordion;
    }

    void addToAccordion(String panelLabel, String text) {
        AccordionPanel panel = outputAccordion.add(panelLabel, new Span(text));
        outputAccordion.open(panel);
    }

    void addNetwork(Network network) {
        networks.addLast(network);
        subNetworks.addLast(subNetwork(network));
        addToAccordion("File -> Load", "Load network.\n");
    }

}
