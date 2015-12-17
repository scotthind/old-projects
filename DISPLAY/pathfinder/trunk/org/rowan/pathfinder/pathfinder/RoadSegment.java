package org.rowan.pathfinder.pathfinder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.WGS84Coord;
import org.rowan.linalgtoolkit.shapes2d.Segment2D;

/**
 * Class <code>RoadSegment</code> represents a piece of road from one
 * point to another.
 * 
 * @author Dan Urbano, Shahid Akhter
 * @version 1.0
 * @since 1.0
 */
public class RoadSegment implements Traversable {

    /** All of the events that this RoadSegment intersects */
    private Set<Event> events = new HashSet<Event>();
    /** The minimum clearance height of all clearances on this RoadSegment */
    private double minClearanceHeight = Double.MAX_VALUE;
    /** A set of all neighboring traversables */
    private Set<Traversable> neighbors = new HashSet<Traversable>();
    /** The segment that defines the start/end points of this RoadSegment */
    private Segment2D segment;
    /** The speed limit of this road segment (in km/h) */
    private int speedLimit;
    /** The name of the road that this RoadSegment is part of */
    private String roadName;
    /** The overall weight after everything has been calculated */
    private double cost;
    /** The temporary weight */
    private double tempCost;
    /**The safety value of the road */
    private double safetyVal;
    /**The speed value of the road */
    private double speedVal;
    /**The distance value of the road */
    private double distanceVal;
    /**The distance of the road */
    private double distance = -1;
    /**The Travel Time */
    private double travelTime = 0;

    /**
     * Create a new RoadSegment specified by the physical segment in space.
     * @param segment The Segment2D that represents location of the OffRaodSegment.
     * @param roadName The name of the road that this RoadSegment lies on.
     */
    public RoadSegment(Segment2D segment, String roadName) {
        this.segment = segment;
        this.roadName = roadName;
    }

    /**
     * Access the name of the road that this road segment lies on.
     * @return The name of the road of this RoadSegment.
     */
    public String getRoadName() {
        return roadName;
    }

    /**
     * Set the speed limit of this road segment.
     * @param limit The speed limit to set.
     */
    public void setSpeedLimit(int limit) {
        speedLimit = limit;
    }

    /**
     * Get the speed limit of this road segment.
     * @return The speed limit of the road of this RoadSegment.
     */
    public int getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Access the set of events that this RoadSegment intersects. The set
     * can be modified once accessed. Its contents are not guaranteed to be
     * correct unless all objects that call this method only add events that
     * intersect this OffRoadSegment.
     * @return The set of all events that this RoadSegment intersects.
     */
    @Override
    public Set<Event> getEvents() {
        return events;
    }

    /**
     * Add a clearance height to this road segment. If the height is less than
     * the current minimum height of this road segment, the minimum height
     * will be changed to the clearance height. If the height is greater than
     * the current minimum height of this road segment, no change will be made.
     * @param h The height of the clearance.
     */
    public void addClearanceHeight(double h) {
        if (h < minClearanceHeight) {
            minClearanceHeight = h;
            //System.err.println("Clearance Height: " + minClearanceHeight);
        }
    }

    /**
     * Return the minimum height of all clearances on this RoadSegment.
     * @return The minimum height of all clearances on this RoadSegment.
     */
    public double getMinClearanceHeight() {
        return minClearanceHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSafetyValue() {
        return safetyVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSpeedValue() {
        return speedVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDistanceValue() {
        return distanceVal;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTraversabilityValue(Vehicle V) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Segment2D getSegment() {
        return this.segment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getCost() {
        return this.cost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Traversable> getNeighbors() {
        return this.neighbors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNeighbors(Set<Traversable> n) {
        this.neighbors = n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateSafetyValue() {
        double totalSeverity = 0;

        if (distance < 0) {
            calculateDistanceValue();
        }

        for (Event event : events) {
            if (event.getBoundary().contains(segment)) {
                totalSeverity += event.getSeverity();
            } else {
                List<Vector2D> intersection = event.getBoundary().intersection(segment).getWorldVertices();
                if (intersection.size() == 2) {
                    WGS84Coord start = new WGS84Coord(intersection.get(0).getY(),
                            intersection.get(0).getX());
                    WGS84Coord end = new WGS84Coord(intersection.get(1).getY(),
                            intersection.get(1).getX());
                    double dist = start.greatCircleDistance(end);
                    totalSeverity += event.getSeverity() * (dist / distance) * event.getDecayPercent();
                }
            }
        }
        safetyVal = totalSeverity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateSpeedValue(Set<Vehicle> vehicles) {
        int minMaxSpeed = Integer.MAX_VALUE;

        if (vehicles == null) {
            return;
        }
        for (Vehicle vehicle : vehicles) {
            if (minMaxSpeed > vehicle.getMaxSpeed()) {
                minMaxSpeed = vehicle.getMaxSpeed();
            }
        }

        travelTime = distance / minMaxSpeed;

        double valueOfSpeed = 1 - (speedLimit / minMaxSpeed);

        if (valueOfSpeed <= 0) {
            speedVal = 0;
        } else {
            speedVal = valueOfSpeed;
        }
    }

    /**
     * Calculates the distance value of this off-road in Kilometers.
     */
    @Override
    public void calculateDistanceValue() {
        if (distance >= 0) {
            return;
        }

        List<Vector2D> vectors = segment.getWorldVertices();
        WGS84Coord start = new WGS84Coord(vectors.get(0).getX(),
                vectors.get(0).getY());
        WGS84Coord end = new WGS84Coord(vectors.get(1).getX(),
                vectors.get(1).getY());
        double dist = start.greatCircleDistance(end);

        distanceVal = dist;
        // System.err.println(dist);
        distance = dist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Set<Vehicle> vehicles) {
        //Invalid if a vehicle can't fit under an underpass or isn't mine
        //resistant if there are mines.
        if (vehicles == null) {
            return true;
        }
        boolean hasMines = false;

        for (Event event : events) {
            if (event.containsMines()) {
                hasMines = true;
                break;
            }
        }


        for (Vehicle vehicle : vehicles) {
            if (hasMines) {
                if (!(vehicle.isMineResistant())) {
                    setCost(Transformer.INFINITY);
                    return false;
                }
            }
            if (vehicle.getHeight() > minClearanceHeight) {
                setCost(Transformer.INFINITY);
                return false;
            }
        }


        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoadSegment)) {
            return false;
        }

        RoadSegment other = (RoadSegment) o;
        return (Logic2D.getStart(segment).equals(Logic2D.getStart(other.getSegment()))
                && Logic2D.getEnd(segment).equals(Logic2D.getEnd(other.getSegment())));
    }

    @Override
    public String toString() {
        String s = Logic2D.getStart(segment).toString();
        String e = Logic2D.getEnd(segment).toString();
        return "Road - " + roadName + " {" + s + " to " + e + "}";
    }

    /**
     * {@inheritDoc}
     */
    public double getTravelTime() {
        return travelTime;
    }
}
