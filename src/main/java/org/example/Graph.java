package org.example;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph {

    private Map<String, List<String>> adjList;

    public Graph() {
        adjList = new HashMap<>();
    }

    public void addVertex(String vertex) {
        adjList.putIfAbsent(vertex, new LinkedList<>());
    }

    public void addEdge(String source, String destination) {
        if (!adjList.containsKey(source)) {
            addVertex(source);
        }

        if (!adjList.containsKey(destination)) {
            addVertex(destination);
        }

        adjList.get(source).add(destination);
    }

    public List<String> getAdjVertices(String vertex) {
        return adjList.getOrDefault(vertex, new LinkedList<>());
    }

    public void printGraph() {
        for (String v : adjList.keySet()) {
            if(!adjList.get(v).isEmpty())
                System.out.println("Vertex " + v + "    ->    " + adjList.get(v));
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.addEdge("$universe.legion.$region.megabus.brand.avro", "$universe.legion.$region.shared.product-catalogs.avro");
        graph.addEdge("$universe.legion.$region.megabus.product.avro" , "$universe.legion.$region.shared.product-catalogs.avro");

        graph.printGraph();
    }
}
