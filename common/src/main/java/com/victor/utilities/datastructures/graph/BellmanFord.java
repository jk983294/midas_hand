package com.victor.utilities.datastructures.graph;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bellman-Ford's shortest path. Works on both negative and positive weighted
 * edges. Also detects negative weight cycles. Returns a tuple of total cost of
 * shortest path and the path.
 * 
 * Worst case: O(|V| |E|)
 */
public class BellmanFord {

    private static Map<GraphNode<Integer>, CostVertexPair<Integer>> costs = null;
    private static Map<GraphNode<Integer>, Set<Edge<Integer>>> paths = null;

    private BellmanFord() { }

    public static Map<GraphNode<Integer>, CostPathPair<Integer>> getShortestPaths(Graph<Integer> g, GraphNode<Integer> start) {
        getShortestPath(g, start, null);
        Map<GraphNode<Integer>, CostPathPair<Integer>> map = new HashMap<>();
        for (CostVertexPair<Integer> pair : costs.values()) {
            int cost = pair.getCost();
            GraphNode<Integer> graphNode = pair.getGraphNode();
            Set<Edge<Integer>> path = paths.get(graphNode);
            map.put(graphNode, new CostPathPair<Integer>(cost, path));
        }
        return map;
    }

    public static CostPathPair<Integer> getShortestPath(Graph<Integer> graph, GraphNode<Integer> start, GraphNode<Integer> end) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        // Reset variables
        costs = null;
        paths = null;

        paths = new HashMap<>();
        for (GraphNode<Integer> v : graph.getNodes())
            paths.put(v, new LinkedHashSet<>());

        costs = new HashMap<>();
        for (GraphNode<Integer> v : graph.getNodes())
            if (v.equals(start))
                costs.put(v, new CostVertexPair<>(0, v));
            else
                costs.put(v, new CostVertexPair<>(Integer.MAX_VALUE, v));

        boolean negativeCycleCheck = false;
        for (int i = 0; i < graph.getNodes().size(); i++) {

            // If it's the last vertices, perform a negative weight cycle check.
            // The graph should be finished by the size()-1 time through this loop.
            if (i == (graph.getNodes().size() - 1))
                negativeCycleCheck = true;

            // Compute costs to all vertices
            for (Edge<Integer> e : graph.getEdges()) {
                CostVertexPair<Integer> pair = costs.get(e.getToVertex());
                CostVertexPair<Integer> lowestCostToThisVertex = costs.get(e.getFromVertex());

                // If the cost of the from vertex is MAX_VALUE then treat as
                // INIFINITY.
                if (lowestCostToThisVertex.getCost() == Integer.MAX_VALUE)
                    continue;

                int cost = lowestCostToThisVertex.getCost() + e.getCost();
                if (cost < pair.getCost()) {
                    if (negativeCycleCheck) {
                        // Uhh ohh... negative weight cycle
                        System.out.println("Graph contains a negative weight cycle.");
                        return null;
                    }
                    // Found a shorter path to a reachable vertex
                    pair.setCost(cost);
                    Set<Edge<Integer>> set = paths.get(e.getToVertex());
                    set.clear();
                    set.addAll(paths.get(e.getFromVertex()));
                    set.add(e);
                }
            }
        }

        if (end != null) {
            CostVertexPair<Integer> pair = costs.get(end);
            Set<Edge<Integer>> set = paths.get(end);
            return (new CostPathPair<>(pair.getCost(), set));
        }
        return null;
    }
}
