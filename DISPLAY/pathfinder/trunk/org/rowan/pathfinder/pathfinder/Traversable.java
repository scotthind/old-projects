package org.rowan.pathfinder.pathfinder;

import java.util.Set;
import org.rowan.linalgtoolkit.shapes2d.Segment2D;

/**
 * Interface <code>Traversable</code> represents any graph segment that can
 * be traversed by a vehicle (including on-foot).
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public interface Traversable {

    /**
     * Return the safety weight of this Traversable.
     * @return Safety weight (range 0 to 1).
     */
    public double getSafetyValue();

    /**
     * Return the speed weight of this Traversable.
     * @return Speed weight (range 0 to 1).
     */
    public double getSpeedValue();

    /**
     * Return the Distance weight of this Traversable.
     * @return Distance weight (range 0 to 1).
     */
    public double getDistanceValue();

    /**
     * Return the Traversability value of this Traversable.
     * @param V The vehicle that is traversing this Traversable.
     * @return Traversability weight (range 0 to 1).
     */
    public double getTraversabilityValue(Vehicle v);

    /**
     * Return the set of events that intersect this Traversable.
     * @return The set of (modifiable) events intersecting this Traversable.
     */
    public Set<Event> getEvents();

    /**
     * Return the Segment2D of this Traversable.
     * @return The Segment2D of this Traversable. 
     */
    public Segment2D getSegment();

    /**
     * Returns the final cost after everything has been calculated
     * @return The weight associated with the given Traversable. 
     */
    public double getCost();

    /**
     * Returns all neighbors of a given traversable.
     * @return A set containing all neighboring traversables. 
     */
    public Set<Traversable> getNeighbors();

    /**
     * Returns the distance of this Traversable
     * @return double distance in kilometers.
     */
    public double getDistance();

    /**
     * Sets the cost of the road.
     * @param cost A value between 0 and 1, or 'infinity.'
     */
    public void setCost(double cost);

    /**
     * Calculates the value of safety of this road.
     * @param vehicles A set of Vehicles that will traverse this path.
     */
    public void calculateSafetyValue();

    /**
     * Calculates the speed value based on the slowest vehicle in the set of
     * vehicles.
     * @param vehicles A set of vehicles.
     */
    public void calculateSpeedValue(Set<Vehicle> vehicles);

    /**
     * Calculates the distance value of this road in Kilometers.
     */
    public void calculateDistanceValue();

    /**
     * Sets the neighbors of this traversable.
     * @param neighbors The neighbors of this traversable
     */
    public void setNeighbors(Set<Traversable> neighbors);

    /**
     * Returns whether or not a set of vehicle can travese this Traversable.
     * @param A set of vehicles which will be checked to see if they can traverse this Traversable.
     * @return True if all vehicles can traverse this, false otherwise.
     */
    public boolean isValid(Set<Vehicle> vehicles);

    public double getTravelTime();

    @Override
    public boolean equals(Object o);
}
