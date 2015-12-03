package com.victor.utilities.datastructures.graph;

/**
 * used for shortest path
 */
public class CostVertexPair<T extends Comparable<T>> implements Comparable<CostVertexPair<T>> {

    private int cost = Integer.MAX_VALUE;
    private GraphNode<T> graphNode = null;

    public CostVertexPair(int cost, GraphNode<T> graphNode) {
        if (graphNode == null)
            throw (new NullPointerException("vertex cannot be NULL."));

        this.cost = cost;
        this.graphNode = graphNode;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public GraphNode<T> getGraphNode() {
        return graphNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 31 * (this.cost * this.graphNode.hashCode());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object e1) {
        if (!(e1 instanceof CostVertexPair))
            return false;

        CostVertexPair pair = (CostVertexPair)e1;
        if (this.cost != pair.cost)
            return false;

        if (!this.graphNode.equals(pair))
            return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(CostVertexPair<T> p) {
        if (p == null)
            throw new NullPointerException("CostVertexPair 'p' must be non-NULL.");
        if (this.cost < p.cost)
            return -1;
        if (this.cost > p.cost)
            return 1;
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vertex=").append(graphNode.getValue()).append(" cost=").append(cost).append("\n");
        return builder.toString();
    }
}
