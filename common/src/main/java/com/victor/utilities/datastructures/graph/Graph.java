package com.victor.utilities.datastructures.graph;

import java.util.*;

/**
 * Graph. Could be directed or undirected depending on the TYPE enum. A graph is
 * an abstract representation of a set of objects where some pairs of the
 * objects are connected by links.
 * 
 * http://en.wikipedia.org/wiki/Graph_(mathematics)
 */
public class Graph<T extends Comparable<T>> {

    private List<GraphNode<T>> nodes = new ArrayList<GraphNode<T>>();
    private List<Edge<T>> edges = new ArrayList<Edge<T>>();

    private Map<T, GraphNode<T>> data2node = new HashMap<>();

    private GraphType type = GraphType.UNDIRECTED;

    public Graph() {
    }

    public Graph(GraphType type) {
        this();
        this.type = type;
    }

    public Graph(Graph<T> g) {
        // Deep copies

        type = g.getType();

        // Copy the vertices (which copies the edges)
        for (GraphNode<T> v : g.getNodes())
            this.nodes.add(new GraphNode<T>(v));

        // Update the object references
        for (GraphNode<T> v : this.nodes) {
            for (Edge<T> e : v.getEdges()) {
                GraphNode<T> fromGraphNode = e.getFromVertex();
                GraphNode<T> toGraphNode = e.getToVertex();
                int indexOfFrom = this.nodes.indexOf(fromGraphNode);
                e.from = this.nodes.get(indexOfFrom);
                int indexOfTo = this.nodes.indexOf(toGraphNode);
                e.to = this.nodes.get(indexOfTo);
                this.edges.add(e);
            }
        }
    }

    public Graph(Collection<GraphNode<T>> nodes, Collection<Edge<T>> edges) {
        this(GraphType.UNDIRECTED, nodes, edges);
    }

    public Graph(GraphType type, Collection<GraphNode<T>> nodes, Collection<Edge<T>> edges) {
        this(type);
        this.nodes.addAll(nodes);
        this.edges.addAll(edges);

        for (Edge<T> e : edges) {
            GraphNode<T> from = e.from;
            GraphNode<T> to = e.to;

            if (!this.nodes.contains(from) || !this.nodes.contains(to))
                continue;

            int index = this.nodes.indexOf(from);
            GraphNode<T> fromGraphNode = this.nodes.get(index);
            index = this.nodes.indexOf(to);
            GraphNode<T> toGraphNode = this.nodes.get(index);
            fromGraphNode.addEdge(e);
            if (this.type == GraphType.UNDIRECTED) {
                Edge<T> reciprical = new Edge<T>(e.cost, toGraphNode, fromGraphNode);
                toGraphNode.addEdge(reciprical);
                this.edges.add(reciprical);
            }
        }
    }

    public GraphType getType() {
        return type;
    }

    public List<GraphNode<T>> getNodes() {
        return nodes;
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }

    public void addEdge(T from, T to){
        if(from != null && to != null){
            GraphNode<T> nodeFrom, nodeTo;
            if(data2node.containsKey(from)){
                nodeFrom = data2node.get(from);
            } else {
                nodeFrom = new GraphNode<T>(from);
                nodes.add(nodeFrom);
                data2node.put(from, nodeFrom);
            }
            if(data2node.containsKey(to)){
                nodeTo = data2node.get(to);
            } else {
                nodeTo = new GraphNode<T>(to);
                nodes.add(nodeTo);
                data2node.put(to, nodeTo);
            }
            edges.add(new Edge<T>(nodeFrom, nodeTo));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (GraphNode<T> v : nodes) {
            builder.append(v.toString());
        }
        return builder.toString();
    }
}