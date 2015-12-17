package org.rowan.linalgtoolkit.shapes2d;

import org.rowan.linalgtoolkit.Vector2D;

/**
 * The <code>Segment2D</code> class describes a line segment in 2D Cartesian space.
 * <p>
 * A <code>Segment2D</code> is always centered at its position. A <code>Segment2D</code> 
 * object has no width, or thickness and thus has no area and no perimeter.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public class Segment2D extends Shape2D {
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/ 
    
    /**
     * Creates a <code>Segment2D</code> object defined by to given vertices, in
     * world coordinates. The given coordinates will be used to compute the center
     * point of the segment
     * @param start A 2D vector defining one endpoint, in world coordinates.
     * @param end   A 2D vector defining one endpoint, in world coordinates.
     */
    public Segment2D(Vector2D start, Vector2D end) {
        // initialize with super constructor
        super();
        
        // calculate center point and set position
        Vector2D center = start.add(end.subtract(start).multiply(0.5));
        setPosition(center);
        
        // calculate local endpoints
        start = start.subtract(center);
        end = end.subtract(center);
        
        // set vertices
        this.vertices.add(start);
        this.vertices.add(end);
    }   
    
    /**
     * Creates a <code>Segment2D</code> object defined by the relative relation 
     * of two given coordinates. The given coordinates will be used to compute 
     * the local endpoints of the segment, which will be created at a given position.
     * @param position  A 2D vector describing the position of the segment in world 
     *                  coordinates.
     * @param start     A 2D vector defining one relative endpoint.
     * @param end       A 2D vector defining one relative endpoint.
     */
    public Segment2D(Vector2D position, Vector2D start, Vector2D end) {
        this(position.add(start.subtract(end).multiply(0.5)),
             position.add(start.subtract(end).multiply(0.5).inverse()));
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/    
    
    /**
     * Returns the starting endpoint of this segment, in local coordinates.
     * @return  A 2D vector defining the starting endpoint of this segment, relative
     *          to the segment's current position.
     */
    public Vector2D getStart() {
        return this.vertices.get(0);
    }
    
    /**
     * Returns the ending endpoint of this segment, in local coordinates.
     * @return  A 2D vector defining the ending endpoint of this segment, relative
     *          to the segment's current position.
     */
    public Vector2D getEnd() {
        return this.vertices.get(1);
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
        Vector2D start = new Vector2D(getStart(), halfLength);
        Vector2D end = new Vector2D(getEnd(), halfLength);
        
        // set new vertices
        this.vertices.clear();
        this.vertices.add(start);
        this.vertices.add(end);
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/    
    
    /**
     * Computes the length of this segment.
     * @return  The length of this segment.
     */
    public double length() {
        Vector2D start = getStart();
        Vector2D end = getEnd();
        return start.distance(end);
    }
    
    /**
     * Computes the slope of this segment.
     * @return  The slope of this segment.
     */
    public double slope() {
        Vector2D start = getStart();
        Vector2D end = getEnd();
        return end.subtract(start).slope();
    }
    
    /** 
     * Returns a vector describing the displacement from this segment's start
     * to end points.
     * @return  A <code>Vector2D</code> describing the displacement from this 
     *          segment's start to end points.
     */
    public Vector2D deltaVect() {
        return getEnd().subtract(getStart());
    }
    
    /**
     * Determines whether this segment is perpendicular to a given 2D vector.
     * @param vector    A 2D vector proposed to be perpendicular to this segment.
     * @return          <code>true</code> if this segment is perpendicular to 
     *                  the given vector; <code>false</code> otherwise.
     */
    public boolean isPerp(Vector2D vector) {
        return deltaVect().isPerp(vector);
    }
    
    /**
     * Determines whether this segment is parallel to a given 2D vector.
     * @param vector    A 2D vector proposed to be parallel to this segment.
     * @return          <code>true</code> if this segment is parallel to the given 
     *                  vector; <code>false</code> otherwise.
     */
    public boolean isParallel(Vector2D vector) {
        return deltaVect().isParallel(vector);
    }
    
    /**
     * Determines whether this segment is perpendicular to a given 2D segment.
     * @param segment   A 2D segment proposed to be perpendicular to this segment.
     * @return          <code>true</code> if this segment is perpendicular to 
     *                  the given segment; <code>false</code> otherwise.
     */
    public boolean isPerp(Segment2D segment) {
        return segment.isPerp(deltaVect());
    }
    
    /**
     * Determines whether this segment is parallel to a given 2D segment.
     * @param segment   A 2D segment proposed to be parallel to this segment.
     * @return          <code>true</code> if this segment is parallel to the given 
     *                  segment; <code>false</code> otherwise.
     */
    public boolean isParallel(Segment2D segment) {
        return segment.isParallel(deltaVect());
    }
    
    /**
     * Computes the perimeter of this segment. Note that the perimeter of a segment
     * is always 0.
     * @return  The perimeter of this segment, which is 0 by definition.
     */
    public double perimeter() {
        return 0;
    }
    
    /**
     * Computes the area of this segment. Note that the area of a segment is always 
     * 0.
     * @return  The area of this segment, which is 0 by definition.
     */
    public double area() {
        return 0;
    }
    
}

