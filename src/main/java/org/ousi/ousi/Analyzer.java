package org.ousi.ousi;

class Analyzer {
    private static double getDensity(Network network) {
        int ne = network.getM();
        int nv = network.getN();
        return (float)ne / nv / (nv - 1);
    }

    static String densityString(Network network) {
        double density = getDensity(network);
        return "Density: " + density;
    }
}
