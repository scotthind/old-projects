package org.rowan.linalgtoolkit.shapes3d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.BoundingBox3D;

/**
 * The <code>Segment3D</code> class describes a line segment in 3D Euclidean space.
 * <p>
 * A <code>Segment3D</code> is always centered at its position. A <code>Segment3D</code>
 * object has no width, or thickness and thus has no area and no perimeter.
 *
 * @author Spence DiNicolantonio, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class Segment3D extends Shape3D {

    /** A 3D Vector defining the first endpoint, in world coordinates. **/
    private Vector3D start;

    /** A 3D Vector defining the second endpoint, in world coordinates. **/
    private Vector3D end;

    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Creates a <code>Segment3D</code> object defined by to given vertices, in
     * world coordinates. The given coordinates will be used to compute the center
     * point of the segment
     * @param start A 3D vector defining one endpoint, in world coordinates.
     * @param end   A 3D vector defining one endpoint, in world coordinates.
     */
    public Segment3D(Vector3D start, Vector3D end) {
        // initialize with super constructor
        super();

        // calculate center point and set position
        Vector3D center = start.add(end.subtract(start).multiply(0.5));
        setPosition(center);

        // calculate local endpoints
        start = start.subtract(center);
        end = end.subtract(center);

        // set vertices
        this.start = start;
        this.end = end;
    }

    /**
     * Creates a <code>Segment3D</code> object defined by the relative relation
     * of two given coordinates. The given coordinates will be used to compute
     * the local endpoints of the segment, which will be created at a given position.
     * @param position  A 3D vector describing the position of the segment in world
     *                  coordinates.
     * @param start     A 3D vector defining one relative endpoint.
     * @param end       A 3D vector defining one relative endpoint.
     */
    public Segment3D(Vector3D position, Vector3D start, Vector3D end) {
        this(position.add(start.subtract(end).multiply(0.5)),
             position.add(start.subtract(end).multiply(0.5).inverse()));
    }

    /*********************************************
     * MARK: Accessors
     *********************************************/

    /**
     * Returns the starting endpoint of this segment, in local coordinates.
     * @return  A 3D vector defining the starting endpoint of this segment, relative
     *          to the segment's current position.
     */
    public Vector3D getStart() {
        return this.start.rotate(getOrientation());
    }

    /**
     * Returns the ending endpoint of this segment, in local coordinates.
     * @return  A 3D vector defining the ending endpoint of this segment, relative
     *          to the segment's current position.
     */
    public Vector3D getEnd() {
        return this.end.rotate(getOrientation());
    }

    /*********************************************
     * MARK: Mutators
     *********************************************/

    /**
     * Sets the length of this segment to a given value. The segment will expand
     * or contract as needed, equally at both ends, to reach the given length.
     * @param length    A value, greater than or equal to 0, to which this segment's
     *                  length will be set.
     * @throws IllegalArgumentException If <code>length</code> is less than 0.
     */
    public void setLength(double length) {
        // invalid length?
        if (length < 0)
            throw new IllegalArgumentException("Invalid segment length: Must be " +
                                               "greater than or equal to 0");

        // recalculate local endpoints
        double halfLength = length/2;
        Vector3D start = new Vector3D(this.start, halfLength);
        Vector3D end = new Vector3D(this.end, halfLength);

        // set new vertices
        this.start = start;
        this.end = end;
    }

    /*********************************************
     * MARK: Queries
     *********************************************/

    /**
     * Computes the length of this segment.
     * @return  The length of this segment.
     */
    public double length() {
        Vector3D start = getStart();
        Vector3D end = getEnd();
        return start.distance(end);
    }
    
    /** 
     * Returns a vector describing the displacement from this segment's start
     * to end points.
     * @return  A <code>Vector3D</code> describing the displacement from this 
     *          segment's start to end points.
     */
    public Vector3D deltaVect() {
        return getEnd().subtract(getStart());
    }

    /**
     * Computes the slopes of this segment in the xy, yz, and zx planes.
     * @return  An instance of <code>Vector3D</code> which contains the slopes of
     *          this segment in the xy, yz, and zx planes.
     */
    public Vector3D slope() {
        return deltaVect();
    }
    
    /**
     * Determines whether this segment is perpendicular to a given 3D vector.
     * @param vector    A 3D vector proposed to be perpendicular to this segment.
     * @return          <code>true</code> if this segment is perpendicular to 
     *                  the given vector; <code>false</code> otherwise.
     */
    public boolean isPerp(Vector3D vector) {
        return deltaVect().isPerp(vector);
    }
    
    /**
     * Determines whether this segment is parallel to a given 3D vector.
     * @param vector    A 3D vector proposed to be parallel to this segment.
     * @return          <code>true</code> if this segment is parallel to the given 
     *                  vector; <code>false</code> otherwise.
     */
    public boolean isParallel(Vector3D vector) {
        return deltaVect().isParallel(vector);
    }
    
    /**
     * Determines whether this segment is perpendicular to a given 3D segment.
     * @param segment   A 3D segment proposed to be perpendicular to this segment.
     * @return          <code>true</code> if this segment is perpendicular to 
     *                  the given segment; <code>false</code> otherwise.
     */
    public boolean isPerp(Segment3D segment) {
        return segment.isPerp(deltaVect());
    }
    
    /**
     * Determines whether this segment is parallel to a given 3D segment.
     * @param segment   A 3D segment proposed to be parallel to this segment.
     * @return          <code>true</code> if this segment is parallel to the given 
     *                  segment; <code>false</code> otherwise.
     */
    public boolean isParallel(Segment3D segment) {
        return segment.isParallel(deltaVect());
    }

    /**
     * Computes the surface area of this segment. Note that the surface area of 
     * a segment is always 0.
     * @return  The surface area of this segment, which is 0 by definition.
     */
    public double surfaceArea() {
        return 0;
    }

    /**
     * Computes the volume of this segment. Note that the volume of a segment is 
     * always 0.
     * @return  The volume of this segment, which is 0 by definition.
     */
    public double volume() {
        return 0;
    }
    
    /**
     * Computes this segment's minimum bounding box.
     * @return  This segment's minimum bounding box.
     */
    public BoundingBox3D boundingBox() {
        // convert vertices to world coordinates
        LinkedList<Vector3D> worldVerts = new LinkedList<Vector3D>();
        worldVerts.add(toWorld(getStart()));
        worldVerts.add(toWorld(getEnd()));
        
        // create bounding box using world coords
        return new BoundingBox3D(worldVerts);
    }
    
}