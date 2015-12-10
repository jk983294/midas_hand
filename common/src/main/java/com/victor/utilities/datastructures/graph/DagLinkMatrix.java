package com.victor.utilities.datastructures.graph;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * use weight matrix
 */
public class DagLinkMatrix {

    private static final Logger logger = Logger.getLogger(DagLinkMatrix.class);

    public int weight[][];       //weight matrix
    public int path[][];        //previous node to node w is path[w]
    public int distance[][];    //dist[u][v] is least weight between u and v
    public int n;               //node count
    public int m;               //edge count

    public GraphType type = GraphType.DIRECTED;

    public DagLinkMatrix() {
    }

    public DagLinkMatrix(GraphType type) {
        this();
        this.type = type;
    }

    public DagLinkMatrix(GraphLinkMatrix g) {
        // Deep copies
        this.type = g.type;
        this.n = g.n;
        weight = new int[n][n];
        for(int i =0; i < n; i++){
            for(int j =0; j < n; j++){
                weight[i][j] = g.weight[i][j];
            }
        }
    }

    public DagLinkMatrix(int n) {
        this.n = n;
        weight = new int[n][n];
        for(int i =0; i < n; i++){
            for(int j =0; j < n; j++){
                weight[i][j] = 0;
            }
        }
    }

    public void setDirectedEdge(int i,int j,int len){
        weight[i][j] = len;
        m++;
    }

    public void BFS(int start){
        boolean visited[] = new boolean[n];
        int current = start;
        visitNode(current);
        visited[current] = true;
        Queue<Integer> q = new LinkedList<>();
        q.offer(current);
        while (!q.isEmpty()){
            current = q.poll();
            //add every node w adjacent to v into stack
            for(int i =0; i < n; i++){
                if(weight[current][i] > 0 && current != i && !visited[i]){
                    visitNode(i);
                    visited[i] = true;
                    q.offer(i);
                }
            }
        }
    }

    void visitNode(int current){
        logger.info(current);
    }

    public static void main(String[] args) {
        GraphLinkMatrix g = new GraphLinkMatrix(GraphType.DIRECTED, 5);	//5 nodes
        // example from 算法导论 P367
        g.setDirectedEdge(0, 1, 10);
        g.setDirectedEdge(0, 3, 5);
        g.setDirectedEdge(1, 2, 1);
        g.setDirectedEdge(1, 3, 2);
        g.setDirectedEdge(2, 4, 4);
        g.setDirectedEdge(3, 1, 3);
        g.setDirectedEdge(3, 2, 9);
        g.setDirectedEdge(3, 4, 2);
        g.setDirectedEdge(4, 0, 7);
        g.setDirectedEdge(4, 2, 6);
        g.BFS(0);
    }
}