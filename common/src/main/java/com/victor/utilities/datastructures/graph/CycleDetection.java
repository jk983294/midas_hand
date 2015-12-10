package com.victor.utilities.datastructures.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cycle detection in an undirected graph using depth first search.
 */
public class CycleDetection {

    private static Set<GraphNode<Integer>> visitedVerticies = new HashSet<GraphNode<Integer>>();
    private static Set<Edge<Integer>> visitedEdges = new HashSet<Edge<Integer>>();

    private CycleDetection() { }

    /**
     * Cycle detection on a unidrected graph.
     * 
     * @param graph Graph
     * @return true if a cycle exists
     */
    public static boolean detect(Graph<Integer> graph) {
        if (graph == null)
            throw new IllegalArgumentException("Graph is NULL.");

        if (graph.getType() != GraphType.UNDIRECTED)
            throw new IllegalArgumentException("Graph is needs to be Undirected.");

        visitedVerticies.clear();
        visitedEdges.clear();
        List<GraphNode<Integer>> verticies = graph.getNodes();
        if (verticies == null || verticies.size() == 0)
            return false;

        // Select the zero-ith element as the root
        GraphNode<Integer> root = verticies.get(0);
        return depthFirstSearch(root);
    }

    private static final boolean depthFirstSearch(GraphNode<Integer> graphNode) {
        if (!visitedVerticies.contains(graphNode)) {
            // Not visited
            visitedVerticies.add(graphNode);

            List<Edge<Integer>> edges = graphNode.getEdges();
            if (edges != null) {
                for (Edge<Integer> edge : edges) {
                    GraphNode<Integer> to = edge.getToVertex();
                    boolean result = false;
                    if (to != null && !visitedEdges.contains(edge)) {
                        visitedEdges.add(edge);
                        Edge<Integer> recip = new Edge<Integer>(edge.getCost(), edge.getToVertex(), edge.getFromVertex());
                        visitedEdges.add(recip);
                        result = depthFirstSearch(to);
                    }
                    if (result == true)
                        return result;
                }
            }
        } else {
            // visited
            return true;
        }
        return false;
    }
}
