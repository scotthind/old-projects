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
import safestpath.events.Event_Data;

/**
 * Representation of a road, which serves as edges in the pathing graph.
 */
public class Road {

    private static final int ROAD_MAX_SPEED = 65;
    private List<Intersection> intersections;
    private double severity;
    private int roadWidth;
    private int roadSpeed;
    private int distance;
    private String roadName;
    private List<Location> drawingPoints;
    private double cost;

    private static double maxDistance;

    /**
     * Initializes a Road object
     */
    public Road()
    {
        intersections = new ArrayList<Intersection>();

        drawingPoints = new ArrayList<Location>();

        //default initializers
        roadWidth = 0;
        roadSpeed = 30;
        distance = 0;
        cost = 0;
        roadName = "";
        maxDistance = 0;

        //severity of 1 means no event in the road, so if there is an event
        //it must be added after the road is created
        severity = 1;
    }

    /**
     * Constructor for use when a list of points is available
     * @param theList
     */
    public Road(List<Location> theList)
    {
        drawingPoints = theList;
        // weight = 0;

    }

    /**
     * Set all of the points used to draw this road on a map
     * @param points The locations
     */
    public void setDrawingPoints(List<Location> points)
    {
        this.drawingPoints = points;
    }

    /**
     * @return all of the intersections of this road
     */
    public List<Intersection> getIntersections()
    {
        return intersections;
    }

    /**
     * @return All of the drawing points of this road
     */
    public List<Location> getPoints()
    {
        return drawingPoints;
    }

    /**
     * Add a drawing point to this road
     * @param loc the point to add
     */
    public void addPoint(Location loc)
    {
        drawingPoints.add(loc);
    }

    /**
     * @return the speed of this road
     */
    public int getRoadSpeed()
    {
        return roadSpeed;
    }

    /**
     * Set the road speed
     * @param roadSpeed the new road speed
     */
    public void setRoadSpeed(int roadSpeed)
    {
        this.roadSpeed = roadSpeed;
    }

    /**
     * @return this road's width
     */
    public int getRoadWidth()
    {
        return roadWidth;
    }

    /**
     * @return this road's name
     */
    public String getRoadName()
    {
        return roadName;
    }

    /**
     * @param name The new name for the road
     */
    public void setRoadName(String name)
    {
        roadName = name;
    }

    /**
     * Check to see if this road equals the given road.
     * They will be compared on the list of drawing points
     * @param r
     * @return
     */
    public boolean equals(Road r)
    {
        int i;
        int rSize = r.drawingPoints.size();
        int thisSize = this.drawingPoints.size();
        for (i = 0; i < rSize && i < thisSize; i++)
        {
            if (!r.drawingPoints.get(i).equals(this.drawingPoints.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString()
    {
        String result = "The Intersections of " + roadName + "\n";
        for (Intersection loc : intersections)
        {
            result += "--" + loc.getLocation() + "\n";
        }
        return result;
    }

    /**
     * Get the cost of this road. Used in the path generating algorithm
     * @return
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Set the cost of this road. Used in the path generating algorithm
     * Should only be used when manually reweighting costs after a path is found
     * @param cost
     */
    public void setCost(double cost)
    {
        this.cost = cost;
    }

    /**
     * Set the cost based on the given parameters
     * @param safest Cost will be based on the safety of the road
     * @param shortest Cost will be based on the length of the road
     * @param fastest Cost will be based on the speed of the road
     */
    public void setCost(boolean safest, boolean shortest, boolean fastest)
    {
        //we are trying to keep the cost between 0 and 1
        //to ensure this, we are making all cost rating a proportion of
        //their max value
        //to ensure that the cost does not get larger than 1 when using multiple
        //parameters we are multiplying the cost ratings together
        //all of the cost ratings are the actual / max

        //initialize it to 1 instead of 1, so that we can freely multiply
        //for multiple parameters

        //special cause for only the safest: cost is just the safety rating
        if(safest && !shortest && !fastest)
        {
            cost = severity / Event_Data.getMaxSeverity();

            return;
        }

        cost = 1;

        if (fastest == true)
        {
            if(roadSpeed < ROAD_MAX_SPEED)
            {
                cost *= roadSpeed / ROAD_MAX_SPEED;
            }
        }

        if (safest == true)
        {
            cost *= (severity / Event_Data.getMaxSeverity());
        }

        if (shortest == true)
        {
            cost *= findDistance() / maxDistance;
        }
    }

    /**
     * The findDistance method finds the total distance of the road by taking the
     * first and second location, finding the distance between those 2 points utilizing
     * Haversian's formula, and then adding the distance to variable dist which
     * is initialized to 0. This will loop through each pair of coordinates until it
     * reaches the end of the road segment.
     * @author Shahid Akhter
     * @return dist - Total distance of road in kilometers.
     */
    public double findDistance()
    {
        double dist = 0;

        //how to find the distance between each drawing point, because the road
        //will not necessarily be straight, so finding the distance between the 2
        //intersections of the road will not give an accurate distance, while
        //going through every drawing point will not you an exact distance, it is the
        //best we can get from a kml-created road
        for (int i = 0; i < drawingPoints.size() - 1; i++)
        {
            Location firstPoint = drawingPoints.get(i);
            Location secondPoint = drawingPoints.get(i + 1);

            dist += firstPoint.findDistance(secondPoint);
        }

        if(dist > maxDistance)
        {
            maxDistance = dist;
        }

        return dist;
    }

    public double distanceProportion()
    {
        return findDistance() / maxDistance;
    }

    /**
     * Add an intersection to this road
     * @param a The intersection to add
     */
    public void addIntersection(Intersection a)
    {
        intersections.add(a);
    }

    /**
     * Find all the locations from the list of points between the begin Location
     * and end Location.
     * Assumes that begin and end are actually in the list of points, and that
     * begin will occur before end in the list.
     * @param start The start intersection
     * @param end The end intersection
     * @return a new road that consists of points between the start and end
     * intersection
     */

    /*
     * testing candidate
     */
    public Road roadBetween(Intersection start, Intersection end)
    {
        List<Location> pointsForNewRoad = new ArrayList<Location>();
        List<Intersection> newIntersections = new ArrayList<Intersection>();

        newIntersections.add(start);
        newIntersections.add(end);

        boolean addToList = false;

        for (Location loc : drawingPoints)
        {
            if (loc.equals(start.getLocation()))
            {
                addToList = true;
            }

            if (loc.equals(end.getLocation()))
            {
                addToList = false;
            }

            if (addToList)
            {
                pointsForNewRoad.add(loc);
            }
        }

        Road newRoad = this.copy();

        newRoad.setDrawingPoints(pointsForNewRoad);
        newRoad.setIntersections(newIntersections);

        return newRoad;
    }

    /**
     * Create a copy of this road (does not deep copy lists).
     * @return
     */
    private Road copy()
    {
        Road newRoad = new Road();

        newRoad.cost = this.cost;
        newRoad.distance = this.distance;
        newRoad.intersections = this.intersections;
        newRoad.drawingPoints = this.drawingPoints;
        newRoad.roadName = this.roadName;
        newRoad.roadSpeed = this.roadSpeed;
        newRoad.roadWidth = this.roadWidth;

        return newRoad;
    }

    //set the intersections of this road (used for copying)
    private void setIntersections(List<Intersection> intersections)
    {
        this.intersections = intersections;
    }

    /**
     * Set the severity of this road
     * @param severity
     */
    public void setSeverity(double severity)
    {
        this.severity = severity;
    }

    /*
     * test candidate
     */
    public Intersection otherIntersection(Intersection i)
    {
        //if this is a dead end then there is no intersection
        if(this.isDeadEnd())
            return null;

        Location intersection1 = intersections.get(0).getLocation();
        Location intersection2 = intersections.get(1).getLocation();
        Location given = i.getLocation();

        if (given.equals(intersection1))
        {
            return intersections.get(1);
        }
        else
        {
            if (given.equals(intersection2))
            {
                return intersections.get(0);
            }
            else
            {
                return null;
            }
        }

    }

    /**
     * Remove this road from all of its intersections
     */
    public void removeFromIntersections()
    {
        Iterator<Intersection> it = intersections.iterator();
        while (it.hasNext())
        {
            Intersection i = it.next();
            it.remove();
        }
    }

    /**
     * Find if a road contains a particular location in its list of points
     * @param location The location to check
     * @return True if this road contains location, else false
     */
    public boolean contains(Location location)
    {
        for(Location loc : drawingPoints)
        {
            if(loc.equals(location))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if this road is a dead end, else false
     */
    public boolean isDeadEnd()
    {
        return intersections.size()  < 2;
    }
}
