package com.victor.utilities.datastructures.graph;

public class Edge<T extends Comparable<T>> implements Comparable<Edge<T>> {

    public GraphNode<T> from = null;
    public GraphNode<T> to = null;
    public int cost = 0;

    public Edge(int cost, GraphNode<T> from, GraphNode<T> to) {
        if (from == null || to == null)
            throw (new NullPointerException("Both 'to' and 'from' Verticies need to be non-NULL."));
        this.cost = cost;
        this.from = from;
        this.to = to;
    }

    public Edge(GraphNode<T> from, GraphNode<T> to) {
        if (from == null || to == null)
            throw (new NullPointerException("Both 'to' and 'from' Verticies need to be non-NULL."));
        this.cost = 1;
        this.from = from;
        this.to = to;
    }

    public Edge(Edge<T> e) {
        this(e.cost, e.from, e.to);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public GraphNode<T> getFromVertex() {
        return from;
    }

    public GraphNode<T> getToVertex() {
        return to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 31 * (this.cost * (this.getFromVertex().value.hashCode() * this.getToVertex().value.hashCode()));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object e1) {
        if (!(e1 instanceof Edge))
            return false;

        Edge<T> e = (Edge<T>) e1;

        boolean costs = this.cost == e.cost;
        if (!costs)
            return false;

        boolean froms = this.from.equals(e.from);
        if (!froms)
            return false;

        boolean tos = this.to.equals(e.to);
        if (!tos)
            return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Edge<T> e) {
        if (this.cost < e.cost)
            return -1;
        if (this.cost > e.cost)
            return 1;
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("edge:").append(" [").append(from.value).append("]").append(" -> ").append("[")
                .append(to.value).append("]").append(" = ").append(cost).append("\n");
        return builder.toString();
    }
}
