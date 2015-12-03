package com.victor.utilities.datastructures.graph;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class Graphs {

    private static int debug = 0; // Debug level. 0=None, 1=Some

    // Undirected
    private static class UndirectedGraph {
        List<GraphNode<Integer>> verticies = new ArrayList<GraphNode<Integer>>();
        GraphNode<Integer> v1 = new GraphNode<Integer>(1);
        GraphNode<Integer> v2 = new GraphNode<Integer>(2);
        GraphNode<Integer> v3 = new GraphNode<Integer>(3);
        GraphNode<Integer> v4 = new GraphNode<Integer>(4);
        GraphNode<Integer> v5 = new GraphNode<Integer>(5);
        GraphNode<Integer> v6 = new GraphNode<Integer>(6);
        {
            verticies.add(v1);
            verticies.add(v2);
            verticies.add(v3);
            verticies.add(v4);
            verticies.add(v5);
            verticies.add(v6);
        }

        List<Edge<Integer>> edges = new ArrayList<Edge<Integer>>();
        Edge<Integer> e1_2 = new Edge<Integer>(7, v1, v2);
        Edge<Integer> e1_3 = new Edge<Integer>(9, v1, v3);
        Edge<Integer> e1_6 = new Edge<Integer>(14, v1, v6);
        Edge<Integer> e2_3 = new Edge<Integer>(10, v2, v3);
        Edge<Integer> e2_4 = new Edge<Integer>(15, v2, v4);
        Edge<Integer> e3_4 = new Edge<Integer>(11, v3, v4);
        Edge<Integer> e3_6 = new Edge<Integer>(2, v3, v6);
        Edge<Integer> e5_6 = new Edge<Integer>(9, v5, v6);
        Edge<Integer> e4_5 = new Edge<Integer>(6, v4, v5);
        {
            edges.add(e1_2);
            edges.add(e1_3);
            edges.add(e1_6);
            edges.add(e2_3);
            edges.add(e2_4);
            edges.add(e3_4);
            edges.add(e3_6);
            edges.add(e5_6);
            edges.add(e4_5);
        }

        Graph<Integer> graph = new Graph<Integer>(verticies, edges);
    }
    private UndirectedGraph undirected = new UndirectedGraph();

    // Ideal undirected path
    private Map<GraphNode<Integer>, CostPathPair<Integer>> idealUndirectedPath = new HashMap<GraphNode<Integer>, CostPathPair<Integer>>();
    {
        {
            int cost = 11;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(undirected.e1_3);
            set.add(undirected.e3_6);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealUndirectedPath.put(undirected.v6, path);
        }
        {
            int cost = 20;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(undirected.e1_3);
            set.add(undirected.e3_6);
            set.add(new Edge<Integer>(9, undirected.v6, undirected.v5));
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealUndirectedPath.put(undirected.v5, path);
        }
        {
            int cost = 9;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(undirected.e1_3);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealUndirectedPath.put(undirected.v3, path);
        }
        {
            int cost = 20;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(undirected.e1_3);
            set.add(undirected.e3_4);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealUndirectedPath.put(undirected.v4, path);
        }
        {
            int cost = 7;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(undirected.e1_2);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealUndirectedPath.put(undirected.v2, path);
        }
        {
            int cost = 0;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealUndirectedPath.put(undirected.v1, path);
        }
    }

    // Ideal undirected PathPair
    private CostPathPair<Integer> idealUndirectedPathPair = null;
    {
        int cost = 20;
        Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
        set.add(undirected.e1_3);
        set.add(undirected.e3_6);
        set.add(new Edge<Integer>(9, undirected.v6, undirected.v5));
        idealUndirectedPathPair = new CostPathPair<Integer>(cost, set);
    }

    // Directed
    private static class DirectedGraph {
        List<GraphNode<Integer>> verticies = new ArrayList<GraphNode<Integer>>();
        GraphNode<Integer> v1 = new GraphNode<Integer>(1);
        GraphNode<Integer> v2 = new GraphNode<Integer>(2);
        GraphNode<Integer> v3 = new GraphNode<Integer>(3);
        GraphNode<Integer> v4 = new GraphNode<Integer>(4);
        GraphNode<Integer> v5 = new GraphNode<Integer>(5);
        GraphNode<Integer> v6 = new GraphNode<Integer>(6);
        GraphNode<Integer> v7 = new GraphNode<Integer>(7);
        {
            verticies.add(v1);
            verticies.add(v2);
            verticies.add(v3);
            verticies.add(v4);
            verticies.add(v5);
            verticies.add(v6);
            verticies.add(v7);
        }

        List<Edge<Integer>> edges = new ArrayList<Edge<Integer>>();
        Edge<Integer> e1_2 = new Edge<Integer>(7, v1, v2);
        Edge<Integer> e1_3 = new Edge<Integer>(9, v1, v3);
        Edge<Integer> e1_6 = new Edge<Integer>(14, v1, v6);
        Edge<Integer> e2_3 = new Edge<Integer>(10, v2, v3);
        Edge<Integer> e2_4 = new Edge<Integer>(15, v2, v4);
        Edge<Integer> e3_4 = new Edge<Integer>(11, v3, v4);
        Edge<Integer> e3_6 = new Edge<Integer>(2, v3, v6);
        Edge<Integer> e6_5 = new Edge<Integer>(9, v6, v5);
        Edge<Integer> e4_5 = new Edge<Integer>(6, v4, v5);
        Edge<Integer> e4_7 = new Edge<Integer>(16, v4, v7);
        {
            edges.add(e1_2);
            edges.add(e1_3);
            edges.add(e1_6);
            edges.add(e2_3);
            edges.add(e2_4);
            edges.add(e3_4);
            edges.add(e3_6);
            edges.add(e6_5);
            edges.add(e4_5);
            edges.add(e4_7);
        }

        Graph<Integer> graph = new Graph<Integer>(Graph.TYPE.DIRECTED, verticies, edges);
    }
    private DirectedGraph directed = new DirectedGraph();

    // Ideal directed path
    private Map<GraphNode<Integer>, CostPathPair<Integer>> idealDirectedPath = new HashMap<GraphNode<Integer>, CostPathPair<Integer>>();
    {
        {
            int cost = 11;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directed.e1_3);
            set.add(directed.e3_6);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedPath.put(directed.v6, path);
        }
        {
            int cost = 20;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directed.e1_3);
            set.add(directed.e3_6);
            set.add(new Edge<Integer>(9, directed.v6, directed.v5));
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedPath.put(directed.v5, path);
        }
        {
            int cost = 36;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directed.e1_3);
            set.add(directed.e3_4);
            set.add(directed.e4_7);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedPath.put(directed.v7, path);
        }
        {
            int cost = 9;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directed.e1_3);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedPath.put(directed.v3, path);
        }
        {
            int cost = 20;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directed.e1_3);
            set.add(directed.e3_4);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedPath.put(directed.v4, path);
        }
        {
            int cost = 7;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directed.e1_2);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedPath.put(directed.v2, path);
        }
        {
            int cost = 0;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedPath.put(directed.v1, path);
        }
    }

    // Ideal directed Path Pair
    private CostPathPair<Integer> idealPathPair = null;
    {
        int cost = 20;
        Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
        set.add(directed.e1_3);
        set.add(directed.e3_6);
        set.add(new Edge<Integer>(9, directed.v6, directed.v5));
        idealPathPair = new CostPathPair<Integer>(cost, set);
    }

    // Directed with negative weights
    private static class DirectedWithNegativeWeights {
        List<GraphNode<Integer>> verticies = new ArrayList<GraphNode<Integer>>();
        GraphNode<Integer> v1 = new GraphNode<Integer>(1);
        GraphNode<Integer> v2 = new GraphNode<Integer>(2);
        GraphNode<Integer> v3 = new GraphNode<Integer>(3);
        GraphNode<Integer> v4 = new GraphNode<Integer>(4);
        {
            verticies.add(v1);
            verticies.add(v2);
            verticies.add(v3);
            verticies.add(v4);
        }

        List<Edge<Integer>> edges = new ArrayList<Edge<Integer>>();
        Edge<Integer> e1_4 = new Edge<Integer>(2, v1, v4);
        Edge<Integer> e2_1 = new Edge<Integer>(6, v2, v1);
        Edge<Integer> e2_3 = new Edge<Integer>(3, v2, v3);
        Edge<Integer> e3_1 = new Edge<Integer>(4, v3, v1);
        Edge<Integer> e3_4 = new Edge<Integer>(5, v3, v4);
        Edge<Integer> e4_2 = new Edge<Integer>(-7, v4, v2);
        Edge<Integer> e4_3 = new Edge<Integer>(-3, v4, v3);
        {
            edges.add(e1_4);
            edges.add(e2_1);
            edges.add(e2_3);
            edges.add(e3_1);
            edges.add(e3_4);
            edges.add(e4_2);
            edges.add(e4_3);
        }

        Graph<Integer> graph = new Graph<Integer>(Graph.TYPE.DIRECTED, verticies, edges);
    }
    private DirectedWithNegativeWeights directedWithNegWeights = new DirectedWithNegativeWeights(); 

    // Ideal directed with negative weight path
    private Map<GraphNode<Integer>, CostPathPair<Integer>> idealDirectedNegWeight = new HashMap<GraphNode<Integer>, CostPathPair<Integer>>();
    {
        {
            int cost = -2;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directedWithNegWeights.e1_4);
            set.add(directedWithNegWeights.e4_2);
            set.add(directedWithNegWeights.e2_3);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedNegWeight.put(directedWithNegWeights.v3, path);
        }
        {
            int cost = 2;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directedWithNegWeights.e1_4);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedNegWeight.put(directedWithNegWeights.v4, path);
        }
        {
            int cost = -5;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            set.add(directedWithNegWeights.e1_4);
            set.add(directedWithNegWeights.e4_2);
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedNegWeight.put(directedWithNegWeights.v2, path);
        }
        {
            int cost = 0;
            Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
            CostPathPair<Integer> path = new CostPathPair<Integer>(cost, set);
            idealDirectedNegWeight.put(directedWithNegWeights.v1, path);
        }
    }

    // Ideal pair
    CostPathPair<Integer> idealDirectedWithNegWeightsPathPair = null;
    {
        int cost = -2;
        Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
        set.add(directedWithNegWeights.e1_4);
        set.add(directedWithNegWeights.e4_2);
        set.add(directedWithNegWeights.e2_3);
        idealDirectedWithNegWeightsPathPair = new CostPathPair<Integer>(cost, set);
    }

    @Test
    public void testDijstraUndirected() {
        if (debug > 0) System.out.println("Undirected ");

        GraphNode<Integer> start = undirected.v1;
        GraphNode<Integer> end = undirected.v5;

        {   // UNDIRECTED GRAPH
            if (debug > 0) System.out.println("Dijstra's shortest paths of the undirected graph from " + start.getValue()+" to all.");
            Map<GraphNode<Integer>, CostPathPair<Integer>> map1 = Dijkstra.getShortestPaths(undirected.graph, start);
            if (debug > 0) System.out.println(getPathMapString(start, map1));

            // Compare results
            for (GraphNode<Integer> v : map1.keySet()) {
                CostPathPair<Integer> path1 = map1.get(v);
                CostPathPair<Integer> path2 = idealUndirectedPath.get(v);
                assertTrue("Dijstra's shortest path error. path1="+path1+" path2="+path2, path1.equals(path2));
            }
            if (debug > 0) System.out.println("Dijstra's shortest path worked correctly.");

            if (debug > 0) System.out.println("Dijstra's shortest path of the undirected graph from " + start.getValue() + " to " + end.getValue()+".");
            CostPathPair<Integer> pair1 = Dijkstra.getShortestPath(undirected.graph, start, end);
            assertTrue("No path from " + start.getValue() + " to " + end.getValue(), (pair1 != null));
            if (debug > 0) System.out.println(pair1.toString());

            assertTrue("Dijstra's shortest path error. pair="+pair1+" pair="+idealUndirectedPathPair, pair1.equals(idealUndirectedPathPair));
            if (debug > 0) System.out.println("Dijstra's shortest path worked correctly");
        }
    }

    @Test
    public void testBellmanFordUndirected() {
        if (debug > 0) System.out.println("Undirected ");

        GraphNode<Integer> start = undirected.v1;
        GraphNode<Integer> end = undirected.v5;

        {
            if (debug > 0) System.out.println("Bellman-Ford's shortest paths of the undirected graph from " + start.getValue()+" to all.");
            Map<GraphNode<Integer>, CostPathPair<Integer>> map2 = BellmanFord.getShortestPaths(undirected.graph, start);
            if (debug > 0) System.out.println(getPathMapString(start, map2));

            // Compare results
            for (GraphNode<Integer> v : map2.keySet()) {
                CostPathPair<Integer> path1 = map2.get(v);
                CostPathPair<Integer> path2 = idealUndirectedPath.get(v);
                assertTrue("Bellman-Ford's shortest path error. path1="+path1+" path2="+path2, path1.equals(path2));
            }
            if (debug>0) System.out.println("Bellman-Ford's shortest path worked correctly.");

            if (debug>0) System.out.println("Bellman-Ford's shortest path of the undirected graph from " + start.getValue() + " to " + end.getValue()+".");
            CostPathPair<Integer> pair2 = BellmanFord.getShortestPath(undirected.graph, start, end);
            assertTrue("Bellman-Ford's shortest path error. pair="+pair2+" result="+idealUndirectedPathPair, pair2.equals(idealUndirectedPathPair));
            if (debug>0) System.out.println("Bellman-Ford's shortest path worked correctly");
        }
    }

    @Test
    public void testPrimUndirected() {
        if (debug > 0) System.out.println(undirected.toString());

        {
            GraphNode<Integer> start = undirected.v1;

            if (debug > 0) System.out.println("Prim's minimum spanning tree of the undirected graph starting from vertex " + start.getValue()+".");
            CostPathPair<Integer> resultMST = Prim.getMinimumSpanningTree(undirected.graph, start);
            if (debug > 0) System.out.println(resultMST.toString());
            {
                // Ideal MST
                int cost = 33;
                Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
                set.add(undirected.e1_2);
                set.add(undirected.e1_3);
                set.add(undirected.e3_6);
                set.add(new Edge<Integer>(9, undirected.v6, undirected.v5));
                set.add(new Edge<Integer>(6, undirected.v5, undirected.v4));
                CostPathPair<Integer> idealMST = new CostPathPair<Integer>(cost, set);
    
                assertTrue("Prim's minimum spanning tree error. resultMST="+resultMST+" idealMST="+idealMST, resultMST.equals(idealMST));
                if (debug > 0) System.out.println("Prim's minimum spanning tree worked correctly");
            }

            // Prim on a graph with cycles
            List<GraphNode<Integer>> cyclicVerticies = new ArrayList<GraphNode<Integer>>();
            GraphNode<Integer> cv1 = new GraphNode<Integer>(1);
            cyclicVerticies.add(cv1);
            GraphNode<Integer> cv2 = new GraphNode<Integer>(2);
            cyclicVerticies.add(cv2);
            GraphNode<Integer> cv3 = new GraphNode<Integer>(3);
            cyclicVerticies.add(cv3);
            GraphNode<Integer> cv4 = new GraphNode<Integer>(4);
            cyclicVerticies.add(cv4);
            GraphNode<Integer> cv5 = new GraphNode<Integer>(5);
            cyclicVerticies.add(cv5);

            List<Edge<Integer>> cyclicEdges = new ArrayList<Edge<Integer>>();
            Edge<Integer> ce1_2 = new Edge<Integer>(3, cv1, cv2);
            cyclicEdges.add(ce1_2);
            Edge<Integer> ce2_3 = new Edge<Integer>(2, cv2, cv3);
            cyclicEdges.add(ce2_3);
            Edge<Integer> ce3_4 = new Edge<Integer>(4, cv3, cv4);
            cyclicEdges.add(ce3_4);
            Edge<Integer> ce4_1 = new Edge<Integer>(1, cv4, cv1);
            cyclicEdges.add(ce4_1);
            Edge<Integer> ce4_5 = new Edge<Integer>(1, cv4, cv5);
            cyclicEdges.add(ce4_5);

            Graph<Integer> cyclicUndirected = new Graph<Integer>(cyclicVerticies, cyclicEdges);
            if (debug > 0) System.out.println(cyclicUndirected.toString());

            start = cv1;

            if (debug > 0) System.out.println("Prim's minimum spanning tree of a cyclic undirected graph from " + start.getValue());
            CostPathPair<Integer> pair4 = Prim.getMinimumSpanningTree(cyclicUndirected, start);
            if (debug > 0) System.out.println(pair4.toString());
            {
                // Ideal MST
                int cost = 7;
                Set<Edge<Integer>> set = new LinkedHashSet<Edge<Integer>>();
                set.add(new Edge<Integer>(1, undirected.v1, undirected.v4));
                set.add(ce4_5);
                set.add(ce1_2);
                set.add(ce2_3);
                CostPathPair<Integer> result4 = new CostPathPair<Integer>(cost, set);

                assertTrue("Prim's minimum spanning tree error. pair4="+pair4+" result4="+result4, pair4.equals(result4));
                if (debug > 0) System.out.println("Prim's minimum spanning tree worked correctly");
            }
        }
    }

    @Test
    public void testDijstraDirected() {
        if (debug>0) System.out.println(directed.toString());

        GraphNode<Integer> start = directed.v1;
        GraphNode<Integer> end = directed.v5;

        if (debug>0) System.out.println("Dijstra's shortest paths of the directed graph from "+start.getValue()+" to all.");
        Map<GraphNode<Integer>, CostPathPair<Integer>> map1 = Dijkstra.getShortestPaths(directed.graph, start);
        if (debug>0) System.out.println(getPathMapString(start, map1));

        // Compare results
        for (GraphNode<Integer> v : map1.keySet()) {
            CostPathPair<Integer> path1 = map1.get(v);
            CostPathPair<Integer> path2 = idealDirectedPath.get(v);
            assertTrue("Dijstra's shortest path error. path1="+path1+" path2="+path2, path1.equals(path2));
        }
        if (debug>0) System.out.println("Dijstra's shortest path worked correctly.");

        if (debug>0) System.out.println("Dijstra's shortest path of the directed graph from "+start.getValue()+" to "+end.getValue()+".");
        CostPathPair<Integer> pair1 = Dijkstra.getShortestPath(directed.graph, start, end);
        assertTrue("No path from "+start.getValue()+" to "+end.getValue(), (pair1!=null));

        // Compare pair
        assertTrue("Dijstra's shortest path error. pair1="+pair1+" idealPathPair="+idealPathPair, pair1.equals(idealPathPair));
        if (debug>0) System.out.println("Dijstra's shortest path worked correctly");
    }

    @Test
    public void testBellmanFordDirected() {
        GraphNode<Integer> start = directed.v1;
        GraphNode<Integer> end = directed.v5;

        if (debug>0) System.out.println("Bellman-Ford's shortest paths of the directed graph from "+start.getValue()+" to all.");
        Map<GraphNode<Integer>, CostPathPair<Integer>> map2 = BellmanFord.getShortestPaths(directed.graph, start);
        if (debug>0) System.out.println(getPathMapString(start, map2));

        // Compare results
        for (GraphNode<Integer> v : map2.keySet()) {
            CostPathPair<Integer> path1 = map2.get(v);
            CostPathPair<Integer> path2 = idealDirectedPath.get(v);
            assertTrue("Bellman-Ford's shortest path error. path1="+path1+" path2="+path2, path1.equals(path2));
        }
        if (debug>0) System.out.println("Bellman-Ford's shortest path worked correctly.");

        if (debug>0) System.out.println("Bellman-Ford's shortest path of the directed graph from "+start.getValue()+" to "+end.getValue()+".");
        CostPathPair<Integer> pair2 = BellmanFord.getShortestPath(directed.graph, start, end);
        assertTrue("No path from "+start.getValue()+" to "+end.getValue(), pair2!=null);

        // Compare pair
        assertTrue("Bellman-Ford's shortest path error. pair2="+pair2+" idealPathPair="+idealPathPair, pair2.equals(idealPathPair));
        if (debug>0) System.out.println("Bellman-Ford's shortest path worked correctly");
    }

    @Test
    public void testDijstraDirectedWihtNegativeWeights() {
        {   // DIRECTED GRAPH (WITH NEGATIVE WEIGHTS)
            if (debug > 0) System.out.println("Directed Graph with Negative Weights.");

            if (debug > 0) System.out.println(directedWithNegWeights.toString());

            GraphNode<Integer> start = directedWithNegWeights.v1;
            GraphNode<Integer> end = directedWithNegWeights.v3;

            if (debug > 0) System.out.println("Bellman-Ford's shortest paths of the directed graph with negative weight from " + start.getValue()+" to all.");
            Map<GraphNode<Integer>, CostPathPair<Integer>> map1 = BellmanFord.getShortestPaths(directedWithNegWeights.graph, start);
            if (debug > 0) System.out.println(getPathMapString(start, map1));

            // Compare results
            for (GraphNode<Integer> v : map1.keySet()) {
                CostPathPair<Integer> path1 = map1.get(v);
                CostPathPair<Integer> path2 = idealDirectedNegWeight.get(v);
                assertTrue("Bellman-Ford's shortest path error. path1="+path1+" path2="+path2, path1.equals(path2));
            }
            if (debug > 0) System.out.println("Bellman-Ford's shortest path worked correctly.");

            if (debug > 0) System.out.println("Bellman-Ford's shortest path of the directed graph from with negative weight " + start.getValue() + " to " + end.getValue()+ ".");
            CostPathPair<Integer> pair1 = BellmanFord.getShortestPath(directedWithNegWeights.graph, start, end);
            assertTrue("No path from " + start.getValue() + " to " + end.getValue(), pair1 != null);

            // Compare pair
            assertTrue("Bellman-Ford's shortest path error. pair1="+pair1+" result2="+idealDirectedWithNegWeightsPathPair, pair1.equals(idealDirectedWithNegWeightsPathPair));
            if (debug > 0) System.out.println("Bellman-Ford's shortest path worked correctly");
        }
    }
 
    @Test
    @SuppressWarnings("unchecked")
    public void testJohnonsonsAllPairsShortestPathOnDirecteWithNegWeights() {
        {
            if (debug > 0) System.out.println("Johnson's all-pairs shortest path of the directed graph with negative weight.");
            Map<GraphNode<Integer>, Map<GraphNode<Integer>, Set<Edge<Integer>>>> path1 = Johnson.getAllPairsShortestPaths(directedWithNegWeights.graph);
            if (debug > 0) System.out.println(getPathMapString(path1));
            assertTrue("Directed graph contains a negative weight cycle.", (path1 != null));

            Map<GraphNode<Integer>, Map<GraphNode<Integer>, Set<Edge<Integer>>>> result3 = new HashMap<GraphNode<Integer>, Map<GraphNode<Integer>, Set<Edge<Integer>>>>();
            {
                {   // vertex 3
                    Map<GraphNode<Integer>, Set<Edge<Integer>>> m = new HashMap<GraphNode<Integer>, Set<Edge<Integer>>>();
                    Set<Edge<Integer>> s3 = new LinkedHashSet<Edge<Integer>>();
                    m.put(directedWithNegWeights.v3, s3);
                    Set<Edge<Integer>> s4 = new LinkedHashSet<Edge<Integer>>();
                    s4.add(directedWithNegWeights.e3_4);
                    m.put(directedWithNegWeights.v4, s4);
                    Set<Edge<Integer>> s2 = new LinkedHashSet<Edge<Integer>>();
                    s2.add(directedWithNegWeights.e3_4);
                    s2.add(directedWithNegWeights.e4_2);
                    m.put(directedWithNegWeights.v2, s2);
                    Set<Edge<Integer>> s1 = new LinkedHashSet<Edge<Integer>>();
                    s1.add(directedWithNegWeights.e3_1);
                    m.put(directedWithNegWeights.v1, s1);
                    result3.put(directedWithNegWeights.v3, m);
                }
                {   // vertex 4
                    Map<GraphNode<Integer>, Set<Edge<Integer>>> m = new HashMap<GraphNode<Integer>, Set<Edge<Integer>>>();
                    Set<Edge<Integer>> s3 = new LinkedHashSet<Edge<Integer>>();
                    s3.add(directedWithNegWeights.e4_2);
                    s3.add(directedWithNegWeights.e2_3);
                    m.put(directedWithNegWeights.v3, s3);
                    Set<Edge<Integer>> s4 = new LinkedHashSet<Edge<Integer>>();
                    s4.add(directedWithNegWeights.e4_2);
                    s4.add(directedWithNegWeights.e2_3);
                    s4.add(directedWithNegWeights.e3_4);
                    m.put(directedWithNegWeights.v4, s4);
                    Set<Edge<Integer>> s2 = new LinkedHashSet<Edge<Integer>>();
                    s2.add(directedWithNegWeights.e4_2);
                    m.put(directedWithNegWeights.v2, s2);
                    Set<Edge<Integer>> s1 = new LinkedHashSet<Edge<Integer>>();
                    s1.add(directedWithNegWeights.e4_2);
                    s1.add(directedWithNegWeights.e2_1);
                    m.put(directedWithNegWeights.v1, s1);
                    result3.put(directedWithNegWeights.v4, m);
                }
                {   // vertex 2
                    Map<GraphNode<Integer>, Set<Edge<Integer>>> m = new HashMap<GraphNode<Integer>, Set<Edge<Integer>>>();
                    Set<Edge<Integer>> s3 = new LinkedHashSet<Edge<Integer>>();
                    s3.add(directedWithNegWeights.e2_3);
                    m.put(directedWithNegWeights.v3, s3);
                    Set<Edge<Integer>> s4 = new LinkedHashSet<Edge<Integer>>();
                    s4.add(directedWithNegWeights.e2_1);
                    s4.add(directedWithNegWeights.e1_4);
                    m.put(directedWithNegWeights.v4, s4);
                    Set<Edge<Integer>> s2 = new LinkedHashSet<Edge<Integer>>();
                    m.put(directedWithNegWeights.v2, s2);
                    Set<Edge<Integer>> s1 = new LinkedHashSet<Edge<Integer>>();
                    s1.add(directedWithNegWeights.e2_1);
                    m.put(directedWithNegWeights.v1, s1);
                    result3.put(directedWithNegWeights.v2, m);
                }
                {   // vertex 1
                    Map<GraphNode<Integer>, Set<Edge<Integer>>> m = new HashMap<GraphNode<Integer>, Set<Edge<Integer>>>();
                    Set<Edge<Integer>> s3 = new LinkedHashSet<Edge<Integer>>();
                    s3.add(directedWithNegWeights.e1_4);
                    s3.add(directedWithNegWeights.e4_2);
                    s3.add(directedWithNegWeights.e2_3);
                    m.put(directedWithNegWeights.v3, s3);
                    Set<Edge<Integer>> s4 = new LinkedHashSet<Edge<Integer>>();
                    s4.add(directedWithNegWeights.e1_4);
                    m.put(directedWithNegWeights.v4, s4);
                    Set<Edge<Integer>> s2 = new LinkedHashSet<Edge<Integer>>();
                    s2.add(directedWithNegWeights.e1_4);
                    s2.add(directedWithNegWeights.e4_2);
                    m.put(directedWithNegWeights.v2, s2);
                    Set<Edge<Integer>> s1 = new LinkedHashSet<Edge<Integer>>();
                    s1.add(directedWithNegWeights.e1_4);
                    m.put(directedWithNegWeights.v1, s1);
                    result3.put(directedWithNegWeights.v1, m);
                }
            }

            // Compare results
            for (GraphNode<Integer> graphNode1 : path1.keySet()) {
                Map<GraphNode<Integer>, Set<Edge<Integer>>> m1 = path1.get(graphNode1);
                Map<GraphNode<Integer>, Set<Edge<Integer>>> m2 = result3.get(graphNode1);
                for (GraphNode<Integer> graphNode2 : m1.keySet()) {
                    Set<Edge<Integer>> set3 = m1.get(graphNode2);
                    Set<Edge<Integer>> set4 = m2.get(graphNode2);
                    Object[] objs1 = set3.toArray();
                    Object[] objs2 = set4.toArray();
                    int size = objs1.length;
                    for (int i=0; i<size; i++) {
                        Edge<Integer> e1 = (Edge<Integer>)objs1[i];
                        Edge<Integer> e2 = (Edge<Integer>)objs2[i];
                        assertTrue("Johnson's all-pairs shortest path error. e1.from="+e1.getFromVertex()+" e2.from="+e2.getFromVertex(), 
                                   e1.getFromVertex().equals(e2.getFromVertex()));
                        assertTrue("Johnson's all-pairs shortest path error. e1.to="+e1.getToVertex()+" e2.to="+e2.getToVertex(), 
                                   e1.getToVertex().equals(e2.getToVertex()));
                    }
                }
            }
            if (debug > 0) System.out.println("Johnson's all-pairs shortest path worked correctly.");
        }
    }

    @Test
    public void testFloydWarshallonDirectedWithNegWeights() {
        {
            if (debug > 0) System.out.println("Floyd-Warshall's all-pairs shortest path weights of the directed graph with negative weight.");
            Map<GraphNode<Integer>, Map<GraphNode<Integer>, Integer>> pathWeights = FloydWarshall.getAllPairsShortestPaths(directedWithNegWeights.graph);
            if (debug > 0) System.out.println(getWeightMapString(pathWeights));

            Map<GraphNode<Integer>, Map<GraphNode<Integer>, Integer>> result4 = new HashMap<GraphNode<Integer>, Map<GraphNode<Integer>, Integer>>();
            {
                // Ideal weights
                {   // Vertex 3
                    Map<GraphNode<Integer>, Integer> m = new HashMap<GraphNode<Integer>, Integer>();
                    {
                        // Vertex 3
                        m.put(directedWithNegWeights.v3, 0);
                        // Vertex 4
                        m.put(directedWithNegWeights.v4, 5);
                        // Vertex 2
                        m.put(directedWithNegWeights.v2, -2);
                        // Vertex 1
                        m.put(directedWithNegWeights.v1, 4);
                    }
                    result4.put(directedWithNegWeights.v3, m);
                }
                {   // Vertex 4
                    Map<GraphNode<Integer>, Integer> m = new HashMap<GraphNode<Integer>, Integer>();
                    {
                        // Vertex 3
                        m.put(directedWithNegWeights.v3, -4);
                        // Vertex 4
                        m.put(directedWithNegWeights.v4, 0);
                        // Vertex 2
                        m.put(directedWithNegWeights.v2, -7);
                        // Vertex 1
                        m.put(directedWithNegWeights.v1, -1);
                    }
                    result4.put(directedWithNegWeights.v4, m);
                }
                {   // Vertex 2
                    Map<GraphNode<Integer>, Integer> m = new HashMap<GraphNode<Integer>, Integer>();
                    {
                        // Vertex 3
                        m.put(directedWithNegWeights.v3, 3);
                        // Vertex 4
                        m.put(directedWithNegWeights.v4, 8);
                        // Vertex 2
                        m.put(directedWithNegWeights.v2, 0);
                        // Vertex 1
                        m.put(directedWithNegWeights.v1, 6);
                    }
                    result4.put(directedWithNegWeights.v2, m);
                }
                {   // Vertex 1
                    Map<GraphNode<Integer>, Integer> m = new HashMap<GraphNode<Integer>, Integer>();
                    {
                        // Vertex 3
                        m.put(directedWithNegWeights.v3, -2);
                        // Vertex 4
                        m.put(directedWithNegWeights.v4, 2);
                        // Vertex 2
                        m.put(directedWithNegWeights.v2, -5);
                        // Vertex 1
                        m.put(directedWithNegWeights.v1, 0);
                    }
                    result4.put(directedWithNegWeights.v1, m);
                }
            }

            // Compare results
            for (GraphNode<Integer> graphNode1 : pathWeights.keySet()) {
                Map<GraphNode<Integer>, Integer> m1 = pathWeights.get(graphNode1);
                Map<GraphNode<Integer>, Integer> m2 = result4.get(graphNode1);
                for (GraphNode<Integer> v : m1.keySet()) {
                    int i1 = m1.get(v);
                    int i2 = m2.get(v);
                    assertTrue("Floyd-Warshall's all-pairs shortest path weights error. i1="+i1+" i2="+i2, i1 == i2);
                }

            }
            if (debug > 0) System.out.println("Floyd-Warshall's all-pairs shortest path worked correctly.");
        }
    }

    @Test
    public void cycleCheckOnUndirected() {
        {   // UNDIRECTED GRAPH
            if (debug > 0) System.out.println("Undirected Graph cycle check.");
            List<GraphNode<Integer>> cycledVerticies = new ArrayList<GraphNode<Integer>>();
            GraphNode<Integer> cv1 = new GraphNode<Integer>(1);
            cycledVerticies.add(cv1);
            GraphNode<Integer> cv2 = new GraphNode<Integer>(2);
            cycledVerticies.add(cv2);
            GraphNode<Integer> cv3 = new GraphNode<Integer>(3);
            cycledVerticies.add(cv3);
            GraphNode<Integer> cv4 = new GraphNode<Integer>(4);
            cycledVerticies.add(cv4);
            GraphNode<Integer> cv5 = new GraphNode<Integer>(5);
            cycledVerticies.add(cv5);
            GraphNode<Integer> cv6 = new GraphNode<Integer>(6);
            cycledVerticies.add(cv6);

            List<Edge<Integer>> cycledEdges = new ArrayList<Edge<Integer>>();
            Edge<Integer> ce1_2 = new Edge<Integer>(7, cv1, cv2);
            cycledEdges.add(ce1_2);
            Edge<Integer> ce2_4 = new Edge<Integer>(15, cv2, cv4);
            cycledEdges.add(ce2_4);
            Edge<Integer> ce3_4 = new Edge<Integer>(11, cv3, cv4);
            cycledEdges.add(ce3_4);
            Edge<Integer> ce3_6 = new Edge<Integer>(2, cv3, cv6);
            cycledEdges.add(ce3_6);
            Edge<Integer> ce5_6 = new Edge<Integer>(9, cv5, cv6);
            cycledEdges.add(ce5_6);
            Edge<Integer> ce4_5 = new Edge<Integer>(6, cv4, cv5);
            cycledEdges.add(ce4_5);

            Graph<Integer> undirectedWithCycle = new Graph<Integer>(cycledVerticies, cycledEdges);
            if (debug > 0) System.out.println(undirectedWithCycle.toString());

            if (debug > 0) System.out.println("Cycle detection of the undirected graph with a cycle.");
            boolean result = CycleDetection.detect(undirectedWithCycle);
            assertTrue("Cycle detection error.", result);
            if (debug > 0) System.out.println("Cycle detection worked correctly.");

            List<GraphNode<Integer>> verticies = new ArrayList<GraphNode<Integer>>();
            GraphNode<Integer> v1 = new GraphNode<Integer>(1);
            verticies.add(v1);
            GraphNode<Integer> v2 = new GraphNode<Integer>(2);
            verticies.add(v2);
            GraphNode<Integer> v3 = new GraphNode<Integer>(3);
            verticies.add(v3);
            GraphNode<Integer> v4 = new GraphNode<Integer>(4);
            verticies.add(v4);
            GraphNode<Integer> v5 = new GraphNode<Integer>(5);
            verticies.add(v5);
            GraphNode<Integer> v6 = new GraphNode<Integer>(6);
            verticies.add(v6);

            List<Edge<Integer>> edges = new ArrayList<Edge<Integer>>();
            Edge<Integer> e1_2 = new Edge<Integer>(7, v1, v2);
            edges.add(e1_2);
            Edge<Integer> e2_4 = new Edge<Integer>(15, v2, v4);
            edges.add(e2_4);
            Edge<Integer> e3_4 = new Edge<Integer>(11, v3, v4);
            edges.add(e3_4);
            Edge<Integer> e3_6 = new Edge<Integer>(2, v3, v6);
            edges.add(e3_6);
            Edge<Integer> e4_5 = new Edge<Integer>(6, v4, v5);
            edges.add(e4_5);

            Graph<Integer> undirectedWithoutCycle = new Graph<Integer>(verticies, edges);
            if (debug > 0) System.out.println(undirectedWithoutCycle.toString());

            if (debug > 0) System.out.println("Cycle detection of the undirected graph without a cycle.");
            result = CycleDetection.detect(undirectedWithoutCycle);
            assertFalse("Cycle detection error.", result);
            if (debug > 0) System.out.println("Cycle detection worked correctly.");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void topologicalSortOnDirectedGraph() {
        {   // DIRECTED GRAPH
            if (debug > 0) System.out.println("Directed Graph topological sort.");
            List<GraphNode<Integer>> graphNodes = new ArrayList<>();
            GraphNode<Integer> cv1 = new GraphNode<>(1);
            graphNodes.add(cv1);
            GraphNode<Integer> cv2 = new GraphNode<>(2);
            graphNodes.add(cv2);
            GraphNode<Integer> cv3 = new GraphNode<>(3);
            graphNodes.add(cv3);
            GraphNode<Integer> cv4 = new GraphNode<>(4);
            graphNodes.add(cv4);
            GraphNode<Integer> cv5 = new GraphNode<>(5);
            graphNodes.add(cv5);
            GraphNode<Integer> cv6 = new GraphNode<>(6);
            graphNodes.add(cv6);

            List<Edge<Integer>> edges = new ArrayList<>();
            edges.add(new Edge<>(cv1, cv4));
            edges.add(new Edge<>(cv2, cv4));
            edges.add(new Edge<>(cv2, cv5));
            edges.add(new Edge<>(cv2, cv6));
            edges.add(new Edge<>(cv3, cv5));
            edges.add(new Edge<>(cv3, cv6));
            edges.add(new Edge<>(cv4, cv6));

            Graph<Integer> directed = new Graph<>(Graph.TYPE.DIRECTED, graphNodes, edges);
            if (debug > 0) System.out.println(directed.toString());

            System.out.println("Topological sort of the directed graph.");
            List<GraphNode<Integer>> results = TopologicalSort.sort(directed);
            if (debug > 0) System.out.println("result=" + results);
            assertTrue("Topological sort error. results="+results, results.size()!=0);

            List<GraphNode<Integer>> results2 = new ArrayList<GraphNode<Integer>>(results.size());
            {  // Ideal sort
                results2.add(cv5);
                results2.add(cv6);
                results2.add(cv3);
                results2.add(cv4);
                results2.add(cv1);
                results2.add(cv2);
            }

            // Compare results
            {
                Object[] objs1 = results.toArray();
                Object[] objs2 = results2.toArray();
                int size = objs1.length;
                for (int i=0; i<size; i++) {
                    GraphNode<Integer> v1 = (GraphNode<Integer>) objs1[i];
                    GraphNode<Integer> v2 = (GraphNode<Integer>) objs2[i];
                    assertTrue("Topological sort error. v1="+v1+" v2", v1.equals(v2));
                }
            }
            if (debug > 0) System.out.println("Topological sort worked correctly.");

            if (debug > 0) System.out.println();

            graphNodes.clear();
            edges.clear();
            graphNodes.add(cv1);
            Graph<Integer> directed1 = new Graph<>(Graph.TYPE.DIRECTED, graphNodes, edges);
            List<GraphNode<Integer>> results1 = TopologicalSort.sort(directed1);
            assertTrue("Topological sort error", results1.size() == 1);
        }
    }

    private static final String getPathMapString(GraphNode<Integer> start, Map<GraphNode<Integer>, CostPathPair<Integer>> map) {
        StringBuilder builder = new StringBuilder();
        for (GraphNode<Integer> v : map.keySet()) {
            CostPathPair<Integer> pair = map.get(v);
            builder.append("From ").append(start.getValue()).append(" to vertex=").append(v.getValue()).append("\n");
            if (pair != null)
                builder.append(pair.toString()).append("\n");

        }
        return builder.toString();
    }

    private static final String getPathMapString(Map<GraphNode<Integer>, Map<GraphNode<Integer>, Set<Edge<Integer>>>> paths) {
        StringBuilder builder = new StringBuilder();
        for (GraphNode<Integer> v : paths.keySet()) {
            Map<GraphNode<Integer>, Set<Edge<Integer>>> map = paths.get(v);
            for (GraphNode<Integer> v2 : map.keySet()) {
                builder.append("From=").append(v.getValue()).append(" to=").append(v2.getValue()).append("\n");
                Set<Edge<Integer>> path = map.get(v2);
                builder.append(path).append("\n");
            }
        }
        return builder.toString();
    }

    private static final String getWeightMapString(Map<GraphNode<Integer>, Map<GraphNode<Integer>, Integer>> paths) {
        StringBuilder builder = new StringBuilder();
        for (GraphNode<Integer> v : paths.keySet()) {
            Map<GraphNode<Integer>, Integer> map = paths.get(v);
            for (GraphNode<Integer> v2 : map.keySet()) {
                builder.append("From=").append(v.getValue()).append(" to=").append(v2.getValue()).append("\n");
                Integer weight = map.get(v2);
                builder.append(weight).append("\n");
            }
        }
        return builder.toString();
    }
}
