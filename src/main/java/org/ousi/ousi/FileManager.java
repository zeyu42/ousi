package org.ousi.ousi;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileManager {

    private static DOTExporter<Vertex, DefaultWeightedEdge> dotExporter = new DOTExporter<Vertex, DefaultWeightedEdge>();

    static public void saveGraphDOT(Graph<Vertex, DefaultWeightedEdge> graph, String filename) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
            dotExporter.exportGraph(graph, bufferedOutputStream);
        } catch (ExportException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static public void saveGraphCSV(Graph<Vertex, DefaultWeightedEdge> graph, String filename) {

    }

    static public void saveGraphBinary(Graph<Vertex, DefaultWeightedEdge> graph, String filename) {

    }

    static public Graph<Vertex, DefaultWeightedEdge> loadGraphDOT(String filename) {
        return null;
    }

    static public Graph<Vertex, DefaultWeightedEdge> loadGraphCSV(String filename) {
        return null;
    }

    static public Graph<Vertex, DefaultWeightedEdge> loadGraphBinary(String filename) {
        return null;
    }
}
