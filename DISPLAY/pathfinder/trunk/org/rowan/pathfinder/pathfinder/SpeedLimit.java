package org.rowan.pathfinder.pathfinder;

import org.rowan.linalgtoolkit.Vector2D;

/**
 * Class <code>SpeedLimit</code> represents the speed limit located on a road.
 * Speed limits may change as one progresses along a road so there will be
 * a start and end point for the speed limit. If there are more the one speed
 * limit on a given road then it will be broken up into different segments.
 * 
 * @author Dan Urbano, Shahid Akhter
 * @version 1.0
 * @since 1.0
 */
public class SpeedLimit {
    /** The start location where the new speed limit begins  */
    private Vector2D start;
    /** The end location where the speed limit ends */
    private Vector2D end;
    /** The name of the road that the speed limit is associated with */
    private String roadName;
    /** The speed limit itself in terms of kilometers per hour */
    private int limit;

    /**
     * A speed limit can be either the entire length of the road or could be
     * broken up into smaller segments of the road.
     * @param start The location where the limit begins.
     * @param end The location where the limit changes or the road ends.
     * @param roadName The name of the road.
     * @param limit The speed limit in kilometers per hour.
     */
    public SpeedLimit(Vector2D start, Vector2D end, String roadName, int limit) {
        this.start = start;
        this.end = end;
        this.roadName = roadName;
        this.limit = limit;
    }

    /**
     * Returns the end point of the speed limit
     * @return A Vector2D containing the final location of the current limit.
     */
    public Vector2D getEnd() {
        return end;
    }

    /**
     * Returns the speed limit of the road.
     * @return The value of the speed limit in terms of kilometers per hour.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Returns the road name this speed limit is contained in.
     * @return A string representing the name of the road.
     */
    public String getRoadName() {
        return roadName;
    }

    /**
     * Returns the start point of the speed limit.
     * @return A Vector2D containing the start location of the current limit.
     */
    public Vector2D getStart() {
        return start;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (start == null || end == null) {
            return roadName + " (" + limit + "kmph)";
        } else {
            return roadName + " (" + limit + "kmph) from ("
                + start.getX() + "," + start.getY() + ") to ("
                + end.getX() + "," + end.getY() + ")";
        }
    }
}
