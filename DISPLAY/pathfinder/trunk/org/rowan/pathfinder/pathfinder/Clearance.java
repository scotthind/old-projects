package org.rowan.pathfinder.pathfinder;

import org.rowan.linalgtoolkit.Vector2D;

/**
 * Class <code>Clearance</code> will represent a location where there is a
 * minimum height requirement to be able to traverse through. Clearances will
 * be made via the Transformer from Underpasses.
 * @author Dan Urbano, Shahid Akhter
 * @version 1.0
 * @since 1.0
 */
public class Clearance {
    /** Represents the coordinates of where the Clearance is located */
    private Vector2D location;
    /** The max height that is able to traverse through the clearance in meters */
    double height;

    /**
     * A Clearance will be created based on the information in an Underpass
     * which will include its maximum height allowable and location.
     * @param location A Vector2D representing the location of the clearance.
     * @param height The maximum height allowed for a Vehicle to pass through.
     */
    public Clearance(Vector2D location, double height) {
        this.location = location;
        this.height = height;
    }

    /**
     * Returns the maximum height that a Vehicle can not exceed in order to
     * pass through this Clearance.
     * @return The maximum height of the Clearance in meters.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the location where the Clearance is located.
     * @return A Vector2D that contains the location of the clearance.
     */
    public Vector2D getLocation() {
        return location;
    }

}
