package org.rowan.pathfinder.pathfinder;

import org.rowan.linalgtoolkit.Vector2D;

/**
 * Class <code>TraversableAndPOI</code> is a convenience class used to store
 * traversables and the corresponding point of intersection from another
 * particular traversable. Comparing two TraversableAndPOI objects is done via
 * comparison of the distance from the point of intersection to each
 * TraversableAndPOI's compare point. The smaller distance will be considered
 * smaller when compareTo is called. To use this class as intended, comparing
 * any two TraversableAndPOIs should meet the condition that both of them
 * have the same compare point; however, this can not be guaranteed.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class TraversableAndPOI implements Comparable<TraversableAndPOI> {
    public Traversable transversable;
    public Vector2D POI;
    public Vector2D comparePoint;

    public TraversableAndPOI(Traversable transversable, Vector2D POI, Vector2D comparePoint) {
        this.transversable = transversable;
        this.POI = POI;
        this.comparePoint = comparePoint;
    }

    /**
     * Compares this TraversableAndPOI with the specified TraversableAndPOI for
     * order. Returns a negative integer, zero, or a positive integer as this
     * TraversableAndPOI is closer to its compare point than, equal distance
     * to its compare point to, or further from its compare point than the
     * specified TraversableAndPOI's distance from its compare point. Note: this
     * class has a natural ordering that is inconsistent with equals.
     * @param other The TraversableAndPOI to be compared.
     * @return A negative integer, zero, or a positive integer as this
     *         TraversableAndPOI is closer to its compare point than, equal
     *         distance to its compare point to, or further from its compare
     *         point than the specified TraversableAndPOI's distance from its
     *         compare point.
     */
    @Override
    public int compareTo(TraversableAndPOI other) {
        double dif1 = this.POI.distance(comparePoint);
        double dif2 = other.POI.distance(other.comparePoint);
        
        return ((int)(dif1 - dif2));
    }
}