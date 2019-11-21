package org.ousi.ousi;

class CompleteGraphFactory {
    static Network getCompleteNetwork(int n, boolean isDirected, boolean hasWeight, int weightLowerBound, int weightUpperBound) {
        return RandomGraphFactory.getRandomNetwork(n, 1, isDirected, hasWeight, weightLowerBound, weightUpperBound);
    }
}
