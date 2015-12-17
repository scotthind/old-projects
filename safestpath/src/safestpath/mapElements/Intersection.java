/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */
package safestpath.mapElements;

import java.util.*;

/**
 * This class provides a representation of street intersections, which serve
 * as vertices in the pathing graph.
 */
public class Intersection {

    private Location location;
    private List<Road> roadsThrough;

    /**
     * Create an intersection at the specified location
     * @param loc
     */
    public Intersection(Location loc)
    {
        roadsThrough = new ArrayList<Road>();
        location = loc;
    }

    /**
     * Adds a Road to the list of edges
     * @param newRoad A Road which will be added to the list of edges
     */
    public void addRoad(Road newRoad)
    {
        roadsThrough.add(newRoad);
        newRoad.addIntersection(this);
    }

    /**
     * Accessor method for edges.
     * @return The edges associated with the intersection.
     */
    public List<Road> getRoads()
    {
        return roadsThrough;
    }

    /**
     * Accessor method for the intersection's location.
     * @return The location of the intersection.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Adds this intersection to the road.
     */
    public void setIntersectionInRoad()
    {
        for (Road road : roadsThrough)
        {
            road.addIntersection(this);
        }
    }

    /**
     * toString used for testing
     * @return
     */
    public String toString()
    {
        String result = "List of Intersections for point " + location;
        for (Road r : roadsThrough)
        {
            result += "\n--" + r.getRoadName();
        }
        return result;
    }

    /**
     * Remove a specified road from this intersection
     * @param road The road to remove
     */
    public void removeRoad(Road road)
    {
        roadsThrough.remove(road);

        //make sure to also remove the intersection from the road,
        //which has to be done by the road itself
        road.getIntersections().remove(this);
    }

    /**
     * Remove all of the roads of this intersection
     */
    public void removeAllRoads()
    {
        Iterator<Road> it = roadsThrough.iterator();
        while (it.hasNext())
        {
            Road curr = it.next();
            it.remove();
            curr.getIntersections().remove(this);
        }
    }

    /**
     * Check to see if a given intersection equals this.
     * They are compared using their location.
     * @param i The intersection to compare to
     * @return True if they have the same location, else false
     */
    public boolean equals(Intersection i)
    {
        return location.equals(i.getLocation());
    }

    /**
     * Find the road between this intersection and the given intersection
     * @param b The intersection to find the road between
     * @return The road that connects this intersection to b
     */
    public Road roadBetween(Intersection b)
    {
        Intersection a = this;
        for (Road roadA : a.roadsThrough)
        {
            if (!roadA.isDeadEnd() && roadA.otherIntersection(this).equals(b))
            {
                return roadA;
            }
        }

        return null;
    }

    /**
     * @return the number of roads in this intersection
     */
    public int getNumberOfRoads()
    {
        return roadsThrough.size();
    }
}
