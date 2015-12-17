/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */
package safestpath.pathing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import safestpath.mapElements.Intersection;
import safestpath.mapElements.Location;
import safestpath.mapElements.Road;
import safestpath.mapElements.Route;

/**
 * The PathFinder class takes care of finding a route from a start and end point.
 * We used an implementation of Djikstra's algorithm to find the routes. The roads
 * that are given to the path finder all have an associated cost with them, which
 * is what allows it to actually find paths.
 *
 * The routes that are created are actually distinct routes, in that they dont share
 * any roads with each other. The only roads that they actually do share are the
 * first road out of the start intersection and the last road that goes into the end
 * intersection.
 */
public class PathFinder {

    //type of route wanted
    private boolean fastest, shortest, safest;

    //holds the roads and intersections
    private Graph graph;

    //start and end points
    private Intersection startLoc, endLoc;

    private List<Intersection> nodes;
    private List<Road> edges;

    //intersections that have been used in the route
    private Set<Intersection> settledNodes;

    //intersections that havent been used in a route yet
    private Set<Intersection> unsettledNodes;

    //stores the source intersections for each intersection when it is added to
    //the route
    private Map<Intersection, Intersection> predecessors;

    //stores the distances of all intersections
    private Map<Intersection, Double> distance;

    //the number of routes that will be generated
    private static final int NUM_ROUTES = 3;

    public PathFinder(Graph graph, boolean fastest, boolean shortest, boolean safest, Intersection start, Intersection end)
    {
        this.fastest = fastest;
        this.shortest = shortest;
        this.safest = safest;

        this.startLoc = start;
        this.endLoc = end;

        this.graph = graph;

        //vertexQ = new PriorityQueue<Intersection>();

        nodes = graph.getAllIntersections();
        edges = graph.getRoads();

        setWeights();
    }

    /**
     * Run the algorithm with the given start point
     * @param source The start point of the rotue
     */
    public void execute()
    {
        //initialization of all lists
        settledNodes = new HashSet<Intersection>();
        unsettledNodes = new HashSet<Intersection>();
        distance = new HashMap<Intersection, Double>();
        predecessors = new HashMap<Intersection, Intersection>();

        //distance of the source to itself is 0
        distance.put(startLoc, 0.0);
        unsettledNodes.add(startLoc);

        //each iteration, get the node with the minimum distance of all the negihbors
        //of any already settled nodes, then make it a settled node, and then
        //find all of the lowest distances from that node to all other adjacent nodes
        while (unsettledNodes.size() > 0)
        {
            Intersection node = getMinimum(unsettledNodes);
            settledNodes.add(node);
            unsettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Intersection node)
    {
        List<Intersection> adjacentNodes = getNeighbors(node);

        for (Intersection target : adjacentNodes)
        {
            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target))
            {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unsettledNodes.add(target);
            }
        }
    }

    /**
     * Get the distance from one node to a target node
     * @param node The start node
     * @param target The end node
     * @return The distance from node to target
     */
    private double getDistance(Intersection node, Intersection target)
    {
        for (Road road : node.getRoads())
        {
            if (!road.isDeadEnd())
            {
                Location loc = road.otherIntersection(node).getLocation();
                if (loc.equals(target.getLocation()))
                {
                    return road.getCost();
                }
            }
        }
        return 0;
    }

    /**
     * Get all of the nodes that are adjacent to the given nodes
     * @param node The node to check
     * @return A list of nodes that are adjacent to the given node
     */
    private List<Intersection> getNeighbors(Intersection node)
    {
        List<Intersection> neighbors = new ArrayList<Intersection>();

        for (Road road : node.getRoads())
        {
            //a road wont have another intersection if its a dead end
            if (!road.isDeadEnd())
            {
                Intersection other = road.otherIntersection(node);
                neighbors.add(other);
            }
        }
        return neighbors;
    }

    /**
     * Find the intersection with the lowest cost out of all intersections in the set
     * @param vertexes Set of vertices
     * @return The intersection with the lowest cost
     */
    private Intersection getMinimum(Set<Intersection> vertexes)
    {
        Intersection minimum = null;

        for (Intersection intersection : vertexes)
        {
            if (minimum == null)
            {
                minimum = intersection;
            }
            else
            {
                if (getShortestDistance(intersection) < getShortestDistance(minimum))
                {
                    minimum = intersection;
                }
            }
        }

        return minimum;
    }

    /**
     * Check to see if a node has been settled yet
     * @param vertex The node to check
     * @return True if it was settled, else false
     */
    private boolean isSettled(Intersection vertex)
    {
        return settledNodes.contains(vertex);
    }

    /**
     * Get the shortest distance to a given destination
     * @param destination The destination point
     * @return The distance to the destination
     */
    private double getShortestDistance(Intersection destination)
    {
        Double d = distance.get(destination);
        if (d == null)
        {
            return Double.MAX_VALUE;
        }
        else
        {
            return d;
        }
    }

    /**
     * Get a list of locations to the target intersection
     *
     * The differant routes are completely unique, in that no 2 routes will contain
     * any of the same roads. If the start location is a 1 way road, then only 1
     * route will be made, since there is only one way out, so there will be no other
     * unique routes. This also applies to the 2nd route, if there is no unique way
     * to get to the endpoint, one will not be generated.
     *
     * @param target The end location
     * @return Route consisting of the path
     */

    /*
     * this looks like a good candidate for black/whitebox testing
     */
    public List<Route> getPath()
    {


        List<Route> routes = new ArrayList<Route>();

        //generate all routes by finding a route to from the start location
        //to the end location. After the route is found, then set the weights of
        //all roads contained in the route to infinity (here we use Double.maxValue,
        //since that is the highest number we can get ahold of), so that they cannot
        //be touched for the next route.
        for (int i = 0; i < NUM_ROUTES; i++)
        {
            execute();
            LinkedList<Intersection> path = new LinkedList<Intersection>();
            List<Location> locations = new ArrayList<Location>();

            //step is used to "iterate" through the route
            Intersection step = endLoc;

            //path is not found for this iteration
            if (predecessors.get(step) == null)
            {
                break;
            }

            path.add(step);
            locations.add(step.getLocation());
            while (predecessors.get(step) != null)
            {
                step = predecessors.get(step);
                path.add(step);
                locations.add(step.getLocation());
            }

            routes.add(i, new Route(locations));
            Collections.reverse(path);
            reWeightNodes(path);
        }

        return routes;
    }

    /**
     * Initialize the weights of all roads
     */
    private void setWeights()
    {
        for (Road road : graph.getRoads())
        {
            road.setCost(safest, shortest, fastest);
        }
    }

    /**
     * Set all of the roads visited in the given route list to an unreachable value
     * so that they cannot be visited in the next iteration
     * @param visited The list of intersection corresponding to a route
     */
    private void reWeightNodes(List<Intersection> visited)
    {
        //start at 2 to account for taking the previous, and also so that
        //the first road of the route does not have it's cost reset, as this has
        //the least impact on creating a uniquely distinct route
        //also end at size - 1 so that the last road is also not reset for the same reason
        for (int i = 2; i < visited.size() - 1; i++)
        {
            //get the road that connects the 2 intersections
            Road reweightRoad = visited.get(i).roadBetween(visited.get(i - 1));

            Intersection curr = visited.get(i);
            Intersection prev = visited.get(i - 1);

            //should never be null, but just to be sure
            if (reweightRoad != null)
            {
                reweightRoad.setCost(Double.MAX_VALUE);
            }
        }
    }
}
