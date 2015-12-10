package com.victor.utilities.datastructures.graph;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Prim's minimum spanning tree. Only works on undirected graphs. It finds a
 * subset of the edges that forms a tree that includes every vertex, where the
 * total weight of all the edges in the tree is minimized.
 */
public class Prim {

    private static int cost = 0;
    private static Set<Edge<Integer>> path = null;
    private static List<GraphNode<Integer>> unvisited = null;
    private static Queue<Edge<Integer>> edgesAvailable = null;

    private Prim() { }

    public static CostPathPair<Integer> getMinimumSpanningTree(Graph<Integer> graph, GraphNode<Integer> start) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        // Reset variables
        cost = 0;
        path = null;
        unvisited = null;
        edgesAvailable = null;

        // Prim's algorithm only works on undirected graphs
        if (graph.getType() == GraphType.DIRECTED)
            throw (new IllegalArgumentException("Undirected graphs only."));

        path = new LinkedHashSet<>();

        unvisited = new ArrayList<>();
        unvisited.addAll(graph.getNodes());
        unvisited.remove(start);

        edgesAvailable = new PriorityQueue<>();

        GraphNode<Integer> graphNode = start;
        while (!unvisited.isEmpty()) {
            for (Edge<Integer> e : graphNode.getEdges()) {
                if (unvisited.contains(e.getToVertex()))
                    edgesAvailable.add(e);
            }

            Edge<Integer> e = edgesAvailable.remove();
            cost += e.getCost();
            path.add(e);

            graphNode = e.getToVertex();
            unvisited.remove(graphNode);
        }

        return (new CostPathPair<>(cost, path));
    }
}
