package org.rowan.pathfinder.pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <code>Pathfinder</code> is responsible for finding the shortest paths
 * based on weight assigned to each segment. Pathfinder will employ the use
 * of Djikstra's algorithm to locate these paths.
 * @author Shahid Akhter
 */
public class Pathfinder {

    /** Nodes that have been used in the route */
    private static Set<Traversable> settledNodes;
    /** Nodes that haven't been used in the path yet */
    private static Set<Traversable> unsettledNodes;
    /**Stores the source node for each node when it is added to the
    route */
    private static Map<Traversable, Traversable> predecessors;
    /** Stores the distances (weight) of all nodes */
    private static Map<Traversable, Double> distance;
    /**the number of routes that will be generated */
    private static final int NUM_PATHS = 3;
    /** Infinity value will be represented as -1 */
    public static final double INFINITY = -1;
    /** High value for reweighting purposes */
    public static final double REWEIGHT_VAL = 10000;

    public static List<Path> getPaths(Traversable start, Traversable end) {

        List<Path> paths = new ArrayList<Path>();

        for (int i = 0; i < NUM_PATHS; i++) {
            execute(start);
            List<Traversable> pathSegments = new ArrayList<Traversable>();
            Traversable step = end;
            /**If this is null it means a full path to the destination
            is not possible */
            if (predecessors.get(step) == null) {
                break;
            }

            pathSegments.add(step);

            while (predecessors.get(step) != null) {
                step = predecessors.get(step);
                pathSegments.add(step);
            }
            Collections.reverse(pathSegments);
            if (pathSegments.get(0).equals(start)) {
                paths.add(i, new Path(pathSegments));
            }
            reWeight(pathSegments);
        }
        return paths;
    }

    /**
     * Runs Djikstra's algorithm with the given start point
     */
    private static void execute(Traversable start) {
        settledNodes = new HashSet<Traversable>();
        unsettledNodes = new HashSet<Traversable>();
        distance = new HashMap<Traversable, Double>();
        predecessors = new HashMap<Traversable, Traversable>();

        distance.put(start, 0.0);
        unsettledNodes.add(start);

        /** For each iteration, get the node with the minimum distance of all
         * neighbors of any settled nodes, make it a settled node, then find
         * all of the lowest distances (weights) from that node to all other
         * adjacent nodes */
        while (unsettledNodes.size() > 0) {
            Traversable node = getMinimum(unsettledNodes);
            settledNodes.add(node);
            unsettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    /**
     * Finds the minimal distances of every neighboring node.
     * @param node A Vector2D whose neighbors are being searched for.
     */
    private static void findMinimalDistances(Traversable node) {
        Set<Traversable> adjacentSegments = node.getNeighbors();

        for (Traversable target : adjacentSegments) {
      
            if (target.getCost() >= 0) {
                if (getShortestDistance(target) > getShortestDistance(node)
                        + target.getCost()) {

                    distance.put(target, getShortestDistance(node) + target.getCost());

                    predecessors.put(target, node);
                    unsettledNodes.add(target);

                }
            }
        }


    }

    /**
     * Get the shortest distance (weight)to a given destination.
     * @param dest The destiation point.
     * @return The shortest distance (weight) to the destination.
     */
    private static double getShortestDistance(Traversable dest) {
        Double shortestDistance = distance.get(dest);

        if (shortestDistance == null) {
            return Double.MAX_VALUE;
        } else {
            return shortestDistance;
        }
    }

    /**
     * Find the node with the lowest cost out of all intesections in the set
     * @param nodes A set of nodes
     * @return The node with the lowest cost.
     */
    private static Traversable getMinimum(Set<Traversable> nodes) {
        Traversable minimum = null;

        for (Traversable node : nodes) {
            if (minimum == null) {
                minimum = node;
            } else {
                if (getShortestDistance(node) < getShortestDistance(minimum)) {
                    minimum = node;
                }
            }
        }

        return minimum;
    }

    /**
     * This will reweight 70% of the path. The first and last 15% will not be
     * touched.
     * @param path The path generated that needs to be reweighted
     */
    private static void reWeight(List<Traversable> path) {

     double distance = 0;
     double totalDistance = 0;
     double threshold;
     for(Traversable t: path)
     {
         distance+=t.getDistance();
     }

     threshold = distance * .35;

     for(Traversable t: path)
     {
         totalDistance += t.getDistance() / 2;
         if(totalDistance > threshold && totalDistance < (distance - threshold))
             predecessors.get(t).setCost(t.getCost() + 100);
         totalDistance += t.getDistance()/2;
     }
        
        //        int boundary = (int) (path.size() * .15);
//
//        for (int i = boundary + 1; i < path.size() - boundary - 1; i++) {
//            Traversable t = predecessors.get(path.get(i));
//            t.setCost(t.getCost() + 100);
//        }
//

    }
}
 