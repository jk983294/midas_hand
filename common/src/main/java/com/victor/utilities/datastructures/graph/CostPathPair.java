package com.victor.utilities.datastructures.graph;

import java.util.Set;

/**
 * used for shortest path
 */
public class CostPathPair<T extends Comparable<T>> {

    private int cost = 0;
    private Set<Edge<T>> path = null;

    public CostPathPair(int cost, Set<Edge<T>> path) {
        if (path == null)
            throw (new NullPointerException("path cannot be NULL."));

        this.cost = cost;
        this.path = path;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Set<Edge<T>> getPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = this.cost;
        for (Edge<T> e : path)
            hash *= e.cost;
        return 31 * hash;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CostPathPair))
            return false;

        CostPathPair pair = (CostPathPair)obj;
        if (this.cost != pair.cost)
            return false;

        Object[] e = pair.path.toArray();
        int i=0;
        for (Edge<T> e1 : path) {
            Edge<T> e2 = (Edge<T>) e[i++];
            if (!e1.equals(e2))
                return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Cost = ").append(cost).append("\n");
        for (Edge<T> e : path) {
            builder.append("\t").append(e);
        }
        return builder.toString();
    }
}
