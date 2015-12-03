package com.victor.utilities.datastructures.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Floyd–Warshall algorithm is a graph analysis algorithm for finding shortest
 * paths in a weighted graph (with positive or negative edge weights).
 * 
 * Worst case: O(V^3)
 */
public class FloydWarshall {

    private FloydWarshall() {
    }

    public static Map<GraphNode<Integer>, Map<GraphNode<Integer>, Integer>> getAllPairsShortestPaths(Graph<Integer> graph) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        Map<GraphNode<Integer>, Map<GraphNode<Integer>, Integer>> allShortestPaths = new HashMap<>();

        List<GraphNode<Integer>> vertices = graph.getNodes();
        int[][] sums = new int[vertices.size()][vertices.size()];

        for (int i = 0; i < sums.length; i++) {
            for (int j = 0; j < sums[i].length; j++) {
                sums[i][j] = Integer.MAX_VALUE;
            }
        }

        List<Edge<Integer>> edges = graph.getEdges();
        for (Edge<Integer> e : edges) {
            int indexOfFrom = vertices.indexOf(e.getFromVertex());
            int indexOfTo = vertices.indexOf(e.getToVertex());
            sums[indexOfFrom][indexOfTo] = e.getCost();
        }

        for (int k = 0; k < vertices.size(); k++) {
            for (int i = 0; i < vertices.size(); i++) {
                for (int j = 0; j < vertices.size(); j++) {
                    if (i == j) {
                        sums[i][j] = 0;
                    } else {
                        int ijCost = sums[i][j];
                        int ikCost = sums[i][k];
                        int kjCost = sums[k][j];
                        int summed = (ikCost != Integer.MAX_VALUE && kjCost != Integer.MAX_VALUE) ? (ikCost + kjCost)
                                : Integer.MAX_VALUE;
                        if (ijCost > summed)
                            sums[i][j] = summed;
                    }
                }
            }
        }

        for (int i = 0; i < sums.length; i++) {
            for (int j = 0; j < sums[i].length; j++) {
                GraphNode<Integer> from = vertices.get(i);
                GraphNode<Integer> to = vertices.get(j);
                Map<GraphNode<Integer>, Integer> map = allShortestPaths.get(from);
                if (map == null)
                    map = new HashMap<>();
                int cost = sums[i][j];
                if (cost != Integer.MAX_VALUE)
                    map.put(to, cost);
                allShortestPaths.put(from, map);
            }
        }

        return allShortestPaths;
    }
}
