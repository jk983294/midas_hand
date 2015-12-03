package com.victor.utilities.datastructures.graph;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<T extends Comparable<T>> implements Comparable<GraphNode<T>> {

    public T value = null;
    public int weight = 0;
    private List<Edge<T>> edges = new ArrayList<>();

    public GraphNode(T value) {
        this.value = value;
    }

    public GraphNode(T value, int weight) {
        this(value);
        this.weight = weight;
    }

    public GraphNode(GraphNode<T> graphNode) {
        this(graphNode.value, graphNode.weight);
        this.edges = new ArrayList<>();
        for (Edge<T> e : graphNode.edges)
            this.edges.add(new Edge<>(e));
    }

    public T getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void addEdge(Edge<T> e) {
        edges.add(e);
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }

    public boolean pathTo(GraphNode<T> v) {
        for (Edge<T> e : edges) {
            if (e.to.equals(v))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int code = this.value.hashCode() + this.weight;
        return 31 * code;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object v1) {
        if (!(v1 instanceof GraphNode))
            return false;

        GraphNode<T> v = (GraphNode<T>) v1;

        boolean values = this.value.equals(v.value);
        if (!values)
            return false;

        boolean weight = this.weight == v.weight;
        if (!weight)
            return false;

        return true;
    }

    @Override
    public int compareTo(GraphNode<T> v) {
        if (this.value == null || v.value == null)
            return -1;
        return this.value.compareTo(v.value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("vertex:").append(" value=").append(value).append(" weight=").append(weight).append("\n");
        for (Edge<T> e : edges) {
            builder.append("\t").append(e.toString());
        }
        return builder.toString();
    }
}
