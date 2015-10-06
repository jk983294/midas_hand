package com.victor.utilities.datastructures;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.victor.utilities.datastructures.tree.IntervalTree;

public class IntervalTreeTests {

    @Test
    public void testIntervalTree() {
        {   // Interval tree
            final String RED        = "RED";
            final String ORANGE     = "ORANGE";
            final String GREEN      = "GREEN";
            final String DARK_GREEN = "DARK_GREEN";
            final String BLUE       = "BLUE";
            final String PURPLE     = "PURPLE";
            final String BLACK      = "BLACK";
            java.util.List<IntervalTree.IntervalData<String>> intervals = new ArrayList<IntervalTree.IntervalData<String>>();
            intervals.add((new IntervalTree.IntervalData<String>(2,  6,   RED)));
            intervals.add((new IntervalTree.IntervalData<String>(3,  5,   ORANGE)));
            intervals.add((new IntervalTree.IntervalData<String>(4,  11,  GREEN)));
            intervals.add((new IntervalTree.IntervalData<String>(5,  10,  DARK_GREEN)));
            intervals.add((new IntervalTree.IntervalData<String>(8,  12,  BLUE)));
            intervals.add((new IntervalTree.IntervalData<String>(9,  14,  PURPLE)));
            intervals.add((new IntervalTree.IntervalData<String>(13, 15,  BLACK)));
            IntervalTree<String> tree = new IntervalTree<String>(intervals);

            IntervalTree.IntervalData<String> query = tree.query(2);
            assertTrue("Interval Tree query error. query=2 returned="+query, query.getData().contains(RED));

            query = tree.query(4); // Stabbing query
            assertTrue("Interval Tree query error. query=4 returned="+query, query.getData().contains(GREEN));

            query = tree.query(9); // Stabbing query
            assertTrue("Interval Tree query error. query=9 returned="+query, query.getData().contains(PURPLE));

            query = tree.query(1, 16); // Range query
            assertTrue("Interval Tree query error. query=1->16 returned="+query, (query.getData().contains(RED) &&
                                                                                  query.getData().contains(ORANGE) &&
                                                                                  query.getData().contains(GREEN) &&
                                                                                  query.getData().contains(DARK_GREEN) &&
                                                                                  query.getData().contains(BLUE) &&
                                                                                  query.getData().contains(PURPLE) &&
                                                                                  query.getData().contains(BLACK))
            );

            query = tree.query(7, 14); // Range query
            assertTrue("Interval Tree query error. query=7->14 returned="+query, (query.getData().contains(GREEN) &&
                                                                                  query.getData().contains(DARK_GREEN) &&
                                                                                  query.getData().contains(BLUE) &&
                                                                                  query.getData().contains(PURPLE) &&
                                                                                  query.getData().contains(BLACK))
            );

            query = tree.query(14, 15); // Range query
            assertTrue((query.getData().contains(PURPLE) &&
                        query.getData().contains(BLACK))
            );
        }

        {   // Lifespan Interval tree
            final String stravinsky = "Stravinsky";
            final String schoenberg = "Schoenberg";
            final String grieg      = "Grieg";
            final String schubert   = "Schubert";
            final String mozart     = "Mozart";
            final String schuetz    = "Schuetz";
            java.util.List<IntervalTree.IntervalData<String>> intervals = new ArrayList<IntervalTree.IntervalData<String>>();
            intervals.add((new IntervalTree.IntervalData<String>(1888, 1971, stravinsky)));
            intervals.add((new IntervalTree.IntervalData<String>(1874, 1951, schoenberg)));
            intervals.add((new IntervalTree.IntervalData<String>(1843, 1907, grieg)));
            intervals.add((new IntervalTree.IntervalData<String>(1779, 1828, schubert)));
            intervals.add((new IntervalTree.IntervalData<String>(1756, 1791, mozart)));
            intervals.add((new IntervalTree.IntervalData<String>(1585, 1672, schuetz)));
            IntervalTree<String> tree = new IntervalTree<String>(intervals);

            IntervalTree.IntervalData<String> query = tree.query(1890); // Stabbing query
            assertTrue("Interval Tree query error. query=1890 returned="+query, (query.getData().contains(stravinsky) &&
                                                                                 query.getData().contains(schoenberg) &&
                                                                                 query.getData().contains(grieg))
            );

            query = tree.query(1909); // Stabbing query
            assertTrue("Interval Tree query error. query=1909 returned="+query , (query.getData().contains(stravinsky) &&
                                                                                  query.getData().contains(schoenberg))
            );

            query = tree.query(1792, 1903); // Range query
            assertTrue("Interval Tree query error. query=1792->1903 returned="+query, query.getData().contains(stravinsky) &&
                                                                                      query.getData().contains(schoenberg) &&
                                                                                      query.getData().contains(grieg) &&
                                                                                      query.getData().contains(schubert)
            );

            query = tree.query(1776, 1799); // Range query
            assertTrue("Interval Tree query error. query=1776->1799 returned="+query, (query.getData().contains(schubert) &&
                                                                                       query.getData().contains(mozart))
            );
        }
    }
}
