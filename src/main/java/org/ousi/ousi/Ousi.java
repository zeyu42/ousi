package org.ousi.ousi;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.Span;

import java.util.LinkedList;

public class Ousi {

    // Currently, settings only include info about visualization threshold.
    private Settings settings = new Settings();

    // The output accordion created in MainView.java and to be set from there
    private Accordion outputAccordion = null;

    private LinkedList<Network> networks = new LinkedList<>();

    public LinkedList<Network> getNetworks() {
        return networks;
    }

    void createCompleteNetwork(int n, boolean isDirected, boolean hasWeight, int weightLowerBound, int weightUpperBound) {
        networks.addLast(CompleteGraphFactory.getCompleteNetwork(n, isDirected, hasWeight, weightLowerBound, weightUpperBound));
        String text;
        if (isDirected) {
            text = "Created directed complete network with ";
        } else {
            text = "Created undirected complete network with ";
        }
        text += n + " nodes.\n";
        addToAccordion("Generate -> Complete Network", text);
    }

    void createRandomNetwork(int n, double p, boolean isDirected, boolean hasWeight, int weightLowerBound, int weightUpperBound) {
        networks.addLast(RandomGraphFactory.getRandomNetwork(n, p, isDirected, hasWeight, weightLowerBound, weightUpperBound));
        String text;
        if (isDirected) {
            text = "Created directed network with ";
        } else {
            text = "Created undirected network with ";
        }
        text += n + " nodes and " + p + " link prob.\n";
        addToAccordion("Generate -> Random Network", text);
    }

    void removeNetwork(Network network) {
        int index = networks.indexOf(network);
        if (index == -1) {
            return;
        }
        networks.remove(index);
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
        addToAccordion("File -> Load", "Load network (label: " + network.getLabel() + ").\n");
    }

}
