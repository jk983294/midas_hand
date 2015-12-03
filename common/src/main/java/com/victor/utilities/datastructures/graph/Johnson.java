package com.victor.utilities.datastructures.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Johnson's algorithm is a way to find the shortest paths between all pairs of
 * vertices in a sparse directed graph. It allows some of the edge weights to be
 * negative numbers, but no negative-weight cycles may exist.
 * 
 * Worst case: O(V^2 log V + VE)
 */
public class Johnson {

    private Johnson() { }

    public static Map<GraphNode<Integer>, Map<GraphNode<Integer>, Set<Edge<Integer>>>> getAllPairsShortestPaths(Graph<Integer> g) {
        if (g == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        Map<GraphNode<Integer>, Map<GraphNode<Integer>, Set<Edge<Integer>>>> allShortestPaths =
          new HashMap<>();

        // First, a new node 'connector' is added to the graph, connected by zero-weight edges to each of the other nodes.
        Graph<Integer> graph = new Graph<>(g);
        GraphNode<Integer> connector = new GraphNode<>(Integer.MAX_VALUE);
        graph.getNodes().add(connector);

        // Add the connector Vertex to all edges.
        for (GraphNode<Integer> v : g.getNodes()) {
            int indexOfV = graph.getNodes().indexOf(v);
            Edge<Integer> edge = new Edge<>(0, connector, graph.getNodes().get(indexOfV));
            connector.addEdge(edge);
            graph.getEdges().add(edge);
        }

        // Second, the Bellman–Ford algorithm is used, starting from the new vertex 'connector', to find for each vertex v 
        // the minimum weight h(v) of a path from 'connector' to v. If this step detects a negative cycle, the algorithm is terminated.
        Map<GraphNode<Integer>, CostPathPair<Integer>> costs = BellmanFord.getShortestPaths(graph, connector);
        if (costs==null) {
            System.out.println("Graph contains a negative weight cycle. Cannot compute shortest path.");
            return null;
        }

        // Next the edges of the original graph are reweighted using the values computed by the Bellman–Ford algorithm: an edge 
        // from u to v, having length w(u,v), is given the new length w(u,v) + h(u) − h(v).
        for (Edge<Integer> e : graph.getEdges()) {
            int weight = e.getCost();
            GraphNode<Integer> u = e.getFromVertex();
            GraphNode<Integer> v = e.getToVertex();

            // Don't worry about the connector
            if (u.equals(connector) || v.equals(connector)) continue;

            // Adjust the costs
            int uCost = costs.get(u).getCost();
            int vCost = costs.get(v).getCost();
            int newWeight = weight+uCost-vCost;
            e.setCost(newWeight);
        }

        // Finally, 'connector' is removed, and Dijkstra's algorithm is used to find the shortest paths from each node s to every 
        // other vertex in the reweighted graph.
        int indexOfConnector = graph.getNodes().indexOf(connector);
        graph.getNodes().remove(indexOfConnector);
        for (Edge<Integer> e : connector.getEdges()) {
            int indexOfConnectorEdge = graph.getEdges().indexOf(e);
            graph.getEdges().remove(indexOfConnectorEdge);
        }

        for (GraphNode<Integer> v : g.getNodes()) {
            Map<GraphNode<Integer>, CostPathPair<Integer>> costPaths = Dijkstra.getShortestPaths(graph, v);
            Map<GraphNode<Integer>, Set<Edge<Integer>>> paths = new HashMap<>();
            for (GraphNode<Integer> v2 : costPaths.keySet()) {
                CostPathPair<Integer> pair = costPaths.get(v2);
                paths.put(v2, pair.getPath());
            }
            allShortestPaths.put(v, paths);
        }

        return allShortestPaths;
    }
}