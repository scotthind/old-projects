package org.rowan.pathfinder.pathfinder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.WGS84Coord;
import org.rowan.linalgtoolkit.shapes2d.Segment2D;

/**
 * Class <code>OffRoadSegment</code> is responsible for 
 * 
 * @author Dan Urbano, Shahid Akhter
 * @version 1.0
 * @since 1.0
 */
public class OffRoadSegment implements Traversable {

    /** All of the events that this OffRoadSegment intersects */
    private Set<Event> events = new HashSet<Event>();
    /** A set of all neighboring traversables */
    private Set<Traversable> neighbors = new HashSet<Traversable>();
    /** The segment that defines the start/end points of this OffRoadSegment */
    private Segment2D segment;
    /** The type of terrain that this OffRoadSegment lies in. */
    private TerrainType terrainType;
    /** The Speed Limit of the offroad segment */
    private double speedLimit;
    /** The overall weight after everything has been calculated */
    private double cost;
    /**The safety value of the off-road */
    private double safetyVal;
    /**The speed value of the off-road */
    private double speedVal = 0;
    /**The distance value of the off-road */
    private double distanceVal;
    /**The distance of the off-road */
    private double distance = -1;
    /** Time it take to travel this offroad */
    private double travelTime;
    
    private volatile static int big=0;
    private volatile int num;
    
    /**
     * Create a new OffRoadSegment specified by the physical segment in space
     * and the type of terrain that the segment lies in.
     * @param segment The Segment2D that represents location of the OffRaodSegment.
     * @param terrainType The type of terrain that the segment lies in.
     */
    public OffRoadSegment(Segment2D segment, TerrainType terrainType, double speedLimit) {
        num = big++;
        this.segment = segment;
        this.terrainType = terrainType;
        this.speedLimit = speedLimit;
    }

    /**
     * Access the set of events that this OffRoadSegment intersects. The set
     * can be modified once accessed. Its contents are not guaranteed to be
     * correct unless all objects that call this method only add events that
     * intersect this OffRoadSegment.
     * @return The set of all events that this OffRoadSegment intersects.
     */
    @Override
    public Set<Event> getEvents() {
        return events;
    }

    /**
     * Return the terrain type that this off road segment lies in.
     * @return The terrain type that this off road segment lies in.
     */
    public TerrainType getTerrainType() {
        return terrainType;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTraversabilityValue(Vehicle vehicle) {
        double terrainValue = vehicle.getTraversabilityMap().get(terrainType);
        return terrainValue;
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
     * Calculates the value of safety of this Traversable.
     */
    @Override
    public void calculateSafetyValue() {
        double totalSeverity = 0;
        if (distance < 0) {
            calculateDistanceValue();
        }

        for (Event event : events) {
            if (event.getBoundary().contains(segment)){
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
     * Calculates the speed value based on the vehicle with the lowest
     * traversibility in this terrain.
     * @param vehicles A set of vehicles.
     */
    @Override
    public void calculateSpeedValue(Set<Vehicle> vehicles) {
        double leastTraversable = Double.MAX_VALUE;
        Vehicle v = null;
        if (vehicles == null) {
            return;
        }
        for (Vehicle vehicle : vehicles) {
            if (leastTraversable > getTraversabilityValue(vehicle)) {
                leastTraversable = getTraversabilityValue(vehicle);
                v = vehicle;
            }
        }
        if (getTraversabilityValue(v) != 0) {
            travelTime = distance / v.getMaxSpeed() * getTraversabilityValue(v);
        }

        double valueOfSpeed = 1 - leastTraversable;

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
        WGS84Coord start = new WGS84Coord(vectors.get(0).getY(),
                vectors.get(0).getX());
        WGS84Coord end = new WGS84Coord(vectors.get(1).getY(),
                vectors.get(1).getX());
        double dist = start.greatCircleDistance(end);

        distanceVal = dist;
        distance = dist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Set<Vehicle> vehicles) {
        //Invalid if a vehicle can't travese a terrain or isn't mine
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

            if (vehicle.getTraversabilityMap().get(terrainType) <= 0) {
                setCost(Transformer.INFINITY);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof OffRoadSegment)) {
            return false;
        }
        OffRoadSegment offroad = (OffRoadSegment) o;
        return Logic2D.getStart(segment).equals(Logic2D.getStart(offroad.getSegment()))
                && Logic2D.getEnd(segment).equals(Logic2D.getEnd(offroad.getSegment()));
    }

    @Override
    public String toString() {
        return "Offroad (terrain=" + getTerrainType() +  ") - {" + Logic2D.getStart(segment) + " to " + Logic2D.getEnd(segment) + "}";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the speed limit for this offroad
     * @return A speedlimit in km/h
     */
    public double getSpeedLimit() {
        return speedLimit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTravelTime() {
        return travelTime;
    }
}
