/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */
package safestpath.parser;

import safestpath.pathing.Graph;
import safestpath.mapElements.Road;
import safestpath.mapElements.Intersection;
import safestpath.mapElements.Location;
import safestpath.events.Event_Data;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import safestpath.*;

/**
 * The Transformer class is responsible for taking in a list of road segments
 * and making it into a graph that will have both the road segments and all of the
 * intersections between the road segments as well.
 */
public class Transformer {
    //holds all the intersections between roads

    private List<Intersection> intersections;
    //holds all the roads (given from the KML parser)
    private List<Road> roadSegments;

    /**
     * Create a transformer with the given list of roads
     */
    public Transformer(List<Road> roadSegments)
    {
        intersections = new ArrayList<Intersection>();
        this.roadSegments = roadSegments;
    }

   
    /**
     * This method will go through the list of intersections, and make it so that
     * there are no repeating intersections (ie, there are no 2 intersections in
     * the list that have the same location).
     */
    private void combineIntersections()
    {
        //the new list of intersections, that will replace the reference
        //to the original list of intersections
        List<Intersection> newIntersections = new ArrayList<Intersection>();

        //go through every intersection, and compare it to the remaining intersections
        //to see if they are at the same point on the map
        for (Intersection currIntersection : intersections)
        {
            //if the list of intersections does not already contain the current
            //intersection, add it to the list

            //checking the first intersections, so add it to the list
            if (newIntersections.isEmpty())
            {
                newIntersections.add(currIntersection);
            }
            //1 or more intersections already in the list
            else
            {
                boolean add = true;

                //go through all the intersections in the list of new intersections
                //and if the current intersections' location is equal to the
                //new intersections' location, then do not add it to the list.
                //If you go through all of the intersections in newIntersections,
                //and have not encountered one with the same location, then add
                //it to the list
                for (Intersection newIntersection : newIntersections)
                {
                    Location currLocation = currIntersection.getLocation();
                    Location newLocation = newIntersection.getLocation();

                    if (currLocation.equals(newLocation))
                    {
                        add = false;
                    }
                }

                //go ahead and add the intersection to the list if you
                //marked to add it
                if (add)
                {
                    newIntersections.add(currIntersection);
                }
            }
        }

        //update the reference to the list of intersections
        intersections = newIntersections;
    }


    //given an event data object, set all the severities of each road
    public void addSeveritiesToRoads(Event_Data evts)
    {
        for (Road road : roadSegments)
        {
            road.setSeverity(evts.severityOf(road));
        }
    }

    /**
     * @return the graph representation of the list of roads given in the constructor
     */
    public Graph transform()
    {
        //first create all unique intersections
        createUniqueIntersections();

        //now add streets to the intersections, and list of roads
        addRoadsToIntersections();

        //make sure that you actually have intersections, instead of
        //intersection points that are just drawing points of roads
        trimIntersections();

        //now find any roads with too many intersections and split them
        //into subroads
        splitRoads();

        //to set up for path finding
        for(Road road : roadSegments)
        {
            road.findDistance();
        }

        int i = 0;
        for(Road road : roadSegments)
        {
            if(road.getIntersections().size() == 0)
                i++;
        }
        System.out.println(i + " roads with 0 intersections out of " + roadSegments.size());

        return new Graph(roadSegments, intersections);
    }

    //create all unique intersections, ie no duplicate locations
    private void createUniqueIntersections()
    {
        //to be sure that you get every possible intersection initially make
        //every point from every road an intersection. We will later trim this
        for(Road road : roadSegments)
        {
            for(Location loc : road.getPoints())
            {
                intersections.add(new Intersection(loc));
            }
        }

        //make sure you have no repeat intersections in the list
        combineIntersections();
    }

    //add all roads to the intersections they are on
    private void addRoadsToIntersections()
    {
        //go through every intersection, and if a road contains the intersection
        //point, add it to the intersection
        for(Intersection curr : intersections)
        {
            for(Road road : roadSegments)
            {
                if(road.contains(curr.getLocation()))
                {
                    curr.addRoad(road);
                }
            }
        }
    }

    /**
     * Remove any intersections that do not contain at least 2 roads.
     */
    private void trimIntersections()
    {
        Iterator<Intersection> it = intersections.iterator();
        while(it.hasNext())
        {
            Intersection curr = it.next();

            //2 because 1 means this isnt a real intersection, just
            //a drawing point of a road, and 0 should never happen
            if(curr.getNumberOfRoads() < 2)
            {
                it.remove();

                //have to remove all the roads from the intersection, so
                //that the road wont still act like it has this as an intersection
                curr.removeAllRoads();
            }
        }
    }

    //split roads into smaller roads if they have more than 2 intersections
    private void splitRoads()
    {
        Iterator<Road> it = roadSegments.iterator();

        Stack<Road> roadsToAdd = new Stack<Road>();

        while(it.hasNext())
        {
            Road road = it.next();

            List<Intersection> roadIntersections = road.getIntersections();

            //2 because a road should have 2 ends
            //size 1 is allowed because it is then a dead end
            if(roadIntersections.size() > 2)
            {
                it.remove();

                //remove the road from its intersections since we are
                //splitting it into smaller roads
                road.removeFromIntersections();

                for (int i = 1; i < roadIntersections.size(); i++)
                {
                    Intersection prev = roadIntersections.get(i - 1);
                    Intersection curr = roadIntersections.get(i);

                    //find the road between the prev and curr intersection
                    Road newRoad = road.roadBetween(prev, curr);
                    roadsToAdd.push(newRoad);
                }
            }
        }

        //cant add to the list inside the loop so used a stack to keep all of
        //the roads to add
        while(!roadsToAdd.isEmpty())
        {
            roadSegments.add(roadsToAdd.pop());
        }
    }
}
