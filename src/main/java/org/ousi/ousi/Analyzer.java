package org.ousi.ousi;

class Analyzer {
    private static double getDensity(Network network) {
        int ne = network.getM();
        int nv = network.getN();
        if (network.getIsDirected()) {
            return (float) ne / nv / (nv - 1);
        } else {
            return (float) 2 * ne / nv / (nv - 1);
        }
    }

    static String densityString(Network network) {
        double density = getDensity(network);
        return "Assuming simplicity, density: " + density;
    }
}
