package com.victor.utilities.datastructures.graph;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In computer science, a topological sort or topological ordering of a directed graph is a linear ordering of
 * its vertices such that, for every edge uv, u comes before v in the ordering.
 */
public class TopologicalSort {

    private TopologicalSort(){}

    /**
     * Performs a topological sort on a directed graph. Returns NULL if a cycle is detected.
     * 
     * @param graph
     * @return Sorted List of Vertices or NULL if graph has a cycle
     */
    public static <T extends Comparable<T>> List<GraphNode<T>> sort(Graph<T> graph) {
        if (graph == null)
            throw new IllegalArgumentException("Graph is NULL.");

        if (graph.getType() != GraphType.DIRECTED)
            throw new IllegalArgumentException("Cannot perform a topological sort on a non-directed graph. graph type = "+graph.getType());

        List<GraphNode<T>> sorted = new ArrayList<>();
        List<GraphNode<T>> noOutgoing = new ArrayList<>();

        List<Edge<T>> edges = new CopyOnWriteArrayList<>();
        edges.addAll(graph.getEdges());

        for (GraphNode<T> v : graph.getNodes()) {
            if (v.getEdges().size() == 0)
                noOutgoing.add(v);
        }
        while (noOutgoing.size() > 0) {
            GraphNode<T> v = noOutgoing.remove(0);
            sorted.add(v);
            for (Edge<T> e : edges) {
                GraphNode<T> v2 = e.getFromVertex();
                GraphNode<T> v3 = e.getToVertex();
                if (v3.equals(v)) {
                    edges.remove(e);
                    v2.getEdges().remove(e);
                }
                if (v2.getEdges().size() == 0)
                    noOutgoing.add(v2);
            }
        }
        if (edges.size() > 0)
            return null;
        return sorted;
    }

    public static <T extends Comparable<T>> List<T> sortThenGetRawData(Graph<T> graph) {
        List<GraphNode<T>> list = sort(graph);
        List<T> list1 = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(GraphNode<T> node : graph.getNodes()){
                list1.add(node.getValue());
            }
        }
        return list1;
    }
}
