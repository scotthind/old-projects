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

import safestpath.mapElements.Road;
import safestpath.mapElements.Intersection;
import safestpath.mapElements.Location;
import java.util.*;
import safestpath.events.Event_Data;

/**
 * This class is a graph interpretation of the operational area which is
 * needed for using Dijkstra's pathfinding algorithm.
 */
public class Graph {

    private List<Road> roads;
    private List<Intersection> intersections;

    public Graph(List<Road> roads, List<Intersection> intersections)
    {
        this.roads = roads;
        this.intersections = intersections;
    }

    public List<Road> getRoads()
    {
        return roads;
    }

    public List<Intersection> getAllIntersections()
    {
        return intersections;
    }

    /**
     * Find the nearest intersection to the given point
     * @param startLocation The location you are checking
     * @return
     */
    public Intersection nearestIntersection(Location start)
    {
        //initialize everything to whatever is the first element in the list
        Intersection lowIntersection = intersections.get(0);
        double lowDistance = lowIntersection.getLocation().findDistance(start);

        //go through all the intersections and see if there is an intersection
        //closer to the point than the lowest cost intersection
        for (Intersection i : intersections)
        {
            Location loc = i.getLocation();
            double currDistance = loc.findDistance(start);

            if (currDistance < lowDistance)
            {
                lowDistance = currDistance;
                lowIntersection = i;
            }
        }
        return lowIntersection;
    }

    public void addSeveritesToRoad(Event_Data evts)
    {
        for (Road road : roads)
        {
            if(road.getRoadName().equalsIgnoreCase("McClarry Road"))
            {
                int i = 0;
            }

            double severity = evts.severityOf(road);

            if(severity != 1)
            {
                int i = 0;
            }

            road.setSeverity(evts.severityOf(road));
        }
    }
}
