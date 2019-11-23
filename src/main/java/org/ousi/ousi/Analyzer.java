package org.ousi.ousi;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

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

    private static double correlation(ArrayList<Double> vector1, ArrayList<Double> vector2) {
        int length = vector1.size();
        double mean1 = mean(vector1);
        double mean2 = mean(vector2);
        double sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (vector1.get(i) - mean1) * (vector2.get(i) - mean2);
        }
        double std1 = std(vector1);
        double std2 = std(vector2);
        return sum / std1 / std2 / length;
    }

    private static ArrayList<Double> vectorOf(Network network) {
        ArrayList<Double> vector = new ArrayList<>(network.getN() * (network.getN() - 1));
        for (Vertex from : network.getVertices()) {
            Set<Edge> edges = network.getEdges(from);
            HashMap<Vertex, Double> toToWeight = new HashMap<>();
            for (Edge edge : edges) {
                toToWeight.put(edge.getTo(), edge.getWeight());
            }
            for (Vertex to : network.getVertices()) {
                if (from == to) {
                    continue;
                }
                if (toToWeight.containsKey(to)) {
                    vector.add(toToWeight.get(to));
                } else {
                    vector.add((double) 0);
                }
            }
        }
        return vector;
    }

    private static double mean(ArrayList<Double> vector) {
        double sum = 0;
        for (Double item : vector) {
            sum += item;
        }
        return sum / vector.size();
    }

    private static double std(ArrayList<Double> vector) {
        double mean = mean(vector);
        double sum = 0;
        for (Double item : vector) {
            double difference = item - mean;
            sum += difference * difference;
        }
        return Math.sqrt(sum / vector.size());
    }

    private static Pair<Double, Double> getQAP(Network network1, Network network2) {
        ArrayList<Double> vector1 = vectorOf(network1);
        ArrayList<Double> vector2 = vectorOf(network2);
        double correlation = correlation(vector1, vector2);
        int count = 0;
        int tries = 3000;
        int n = network1.getN();
        for (int i = 0; i < tries; i++) {
            double permutedCorrelation = correlation(vector1, permute(vector2, n));
            if (permutedCorrelation > correlation) {
                count++;
            }
        }
        Double significance = (double) count / (double) tries;
        return new ImmutablePair<>(correlation, significance);
    }

    private static ArrayList<Double> permute(ArrayList<Double> vector, int n) {
        ArrayList<Integer> map = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            map.add(i);
        }
        Collections.shuffle(map);
        ArrayList<Double> result = new ArrayList<>(Arrays.asList(new Double[vector.size()]));
        Collections.fill(result, 0.0);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                int oldRow = i; // Though IDE reports oldRow to be redundant, I consider it as of utter importance in terms of readability.
                int oldCol;
                if (i < j) {
                    oldCol = j - 1;
                } else {
                    oldCol = j;
                }
                int newRow = map.get(i);
                int newCol;
                if (newRow < map.get(j)) {
                    newCol = map.get(j) - 1;
                } else {
                    newCol = map.get(j);
                }
                int oldIndex = oldRow * (n - 1) + oldCol;
                int newIndex = newRow * (n - 1) + newCol;
                result.set(newIndex, vector.get(oldIndex));
            }
        }
        return result;
    }

    static String QAPString(Network network1, Network network2) {
        if (network1.getN() == network2.getN() && network1.isSimple() && network2.isSimple()) {
            Pair<Double, Double> QAP = getQAP(network1, network2);
            return "Correlation: " + QAP.getLeft() + "\nSignificance: " + QAP.getRight();
        } else {
            return "Cannot compute the QAP correlation between non-simple networks.";
        }
    }

}
