package org.rowan.linalgtoolkit.shapes3d;

import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.BoundingBox3D;
import org.rowan.linalgtoolkit.logic3d.LinAlg3D;
import org.rowan.linalgtoolkit.WGS84Coord.DistanceMode;
import org.rowan.linalgtoolkit.transform3d.Rotation;

/**
 * The <code>Shape3D</code> class is an abstract class providing limited implementation
 * and guidelines for defining 3D Shapes.
 *
 * @author Spence DiNicolantonio, Michael Liguori, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public abstract class Shape3D {

    /*********************************************
     * MARK: Fields
     *********************************************/

    /** The position of this shape in world coordinates. */
    private Vector3D position;
    
    /** The orientation of the shape. */
    private Rotation orientation;

    /** The velocity of this shape. */
    private Vector3D velocity;

    
    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Initializes a <code>Shape3D</code> object at a given position, with 0
     * velocity.
     * @param position  A 3D vector describing the position of the shape in world
     *                  coordinates.
     */
    protected Shape3D(Vector3D position) {
        this.position = position;
        this.orientation = Rotation.IDENTITY;
        this.velocity = Vector3D.ZERO_VECTOR;
    }

    /**
     * Initializes a <code>Shape3D</code> object at the origin with 0 velocity.
     */
    protected Shape3D() {
        this(Vector3D.ORIGIN);
    }

    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the position of this shape.
     * @return  The position of this shape.
     */
    public Vector3D getPosition() {
        return this.position;
    }
    
    /**
     * Returns the orientation of this shape.
     * @return  The orientation of this shape.
     */
    public Rotation getOrientation() {
        return this.orientation;
    }

    /**
     * Returns the velocity of this shape.
     * @return  The velocity of this shape.
     */
    public Vector3D getVelocity() {
        return this.velocity;
    }

    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the position of this shape to a given point.
     * @param position  A 3D vector describing the shape's new position.
     */
    public void setPosition(Vector3D position) {
        this.position = position;
    }
    
    /**
     * Sets the orientation of this shape.
     * @param orientation   A Rotation object describing the shape's new orientation.
     */
    public void setOrientation(Rotation orientation) {
        this.orientation = orientation;
    }

    /**
     * Sets the velocity of this shape to a given vector.
     * @param velocity  A 3D vector describing the shape's new velocity.
     */
    public void setVelocity(Vector3D velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Rotates this shape about its center point according to a given 3D rotation 
     * object.
     * @param rotation  A <code>Rotation</code> object describing the rotation
     *                  to be applied to this shape.
     */
    public void rotate(Rotation rotation) {
        setOrientation(rotation.append(this.orientation));
    }
    
    /**
     * Rotates this shape about a given vertex, in world coordinates, according 
     * to a given 3D rotation object.  
     * @param rotation  A <code>Rotation</code> object describing the rotation
     *                  to be applied to this shape.
     * @param vertex    A <code>Vector3D</code> describing the vertex, in world
     *                  coordinates about which this shape will be rotated.
     */
    public void rotate(Rotation rotation, Vector3D vertex) {
        // rotate the position vertex
        setPosition(getPosition().rotate(rotation, vertex));
        
        // rotate the shape around its local origin
        rotate(rotation);
    }
    

    /*********************************************
     * MARK: Linear Algebra
     *********************************************/

    /**
     * Determines whether a given 3D vertex, in world coordinates, lies within
     * this shape.
     * @param vertex    A <code>Vector3D</code> describing the vertex in question.
     * @return          <code>true</code> if <code>vertex</code> is wholly contained
     *                  by this shape; <code>false</code> otherwise.
     * @throws IllegalArgumentException If this shape is not supported by the
     *                  <code>LinAlg3D.contains()<code> method.
     */
    public boolean contains(Vector3D vertex) {
        return LinAlg3D.contains(this, vertex);
    }

    /**
     * Determines whether a given 3D shape, is wholly contained by this shape.
     * @param shape     The child <code>Shape3D</code>.
     * @return          <code>true</code> if <code>shape</code> is wholly contained
     *                  by this shape; <code>false</code> otherwise.
     * @throws IllegalArgumentException If the given <code>Shape3D</code> or this
     *                  shape is not supported by the <code>LinAlg3D.contains()<code>
     *                  method.
     */
    public boolean contains(Shape3D shape) {
        return LinAlg3D.contains(this, shape);
    }

    /**
     * Computes the distance between this shape and a given 3D vertex.
     * @param vertex    A <code>Vector3D</code> describing the vertex.
     * @return          The distance between this shape and <code>vertex</code>.
     * @throws IllegalArgumentException If this shape is not supported by the
     *                  <code>LinAlg3D.distance()<code> method.
     */
    public double distance(Vector3D vertex) {
        return LinAlg3D.distance(this, vertex);
    }

    /**
     * Computes the distance between this shape and a given 3D shape.
     * @param shape A <code>Shape3D</code>.
     * @return      The distance between this shape and <code>shape</code>.
     * @throws IllegalArgumentException If the given <code>Shape3D</code> or this
     *              shape is not supported by the <code>LinAlg3D.distance()<code>
     *              method.
     */
    public double distance(Shape3D shape) {
        return LinAlg3D.distance(this, shape);
    }
    
    /**
     * Computes the rhumb line distance between this shape and a given vertex, 
     * at sea level.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @return          The rhumb line distance between this shape and <code>vertex</code>
     *                  at sea level.
     */
    public double rhumbLineDistance(Vector3D vertex) {
        return LinAlg3D.rhumbLineDistance(this, vertex);
    }
    
    /**
     * Computes the rhumb line distance between this shape and a given shape, at
     * sea level
     * @param shape A <code>Shape3D</code> object.
     * @return      The great circle distance between this shape and <code>shape</code>
     *              at sea level.
     */
    public double rhumbLineDistance(Shape3D shape) {
        return LinAlg3D.rhumbLineDistance(this, shape);
    }

    /**
     * Computes the great circle distance between this shape and a given vertex, 
     * at sea level.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @return          The great circle distance between this shape and 
     *                  <code>vertex</code> at sea level.
     */
    public double greatCircleDistance(Vector3D vertex) {
        return LinAlg3D.greatCircleDistance(this, vertex);
    }
        
    /**
     * Computes the great circle distance between this shape and a given vertex, 
     * using a given mode to determine the altitude at which distance is calculated. 
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @param distMode  A <code>DistanceMode</code> defining what altitude to use
     *                  in the distance calculation.
     * @return          The great circle distance between this shape and 
     *                  <code>vertex</code> using the given <code>DistanceMode</code>.
     */
    public double greatCircleDistance(Shape3D shape, Vector3D vertex, DistanceMode distMode) {
        return LinAlg3D.greatCircleDistance(this, vertex, distMode);
    }
    
    /**
     * Computes the great circle distance between this shape and a given shape, 
     * at sea level.
     * @param shape A <code>Shape3D</code> object.
     * @return      The great circle distance between this shape and <code>shape</code>
     *              at sea level.
     */
    public double greatCircleDistance(Shape3D shape) {
        return LinAlg3D.greatCircleDistance(this, shape);
    }
    
    /**
     * Computes the great circle distance between this shape and a given vertex, 
     * using a given mode to determine the altitude at which distance is calculated. 
     * @param shape     A <code>Shape3D</code> object.
     * @param distMode  A <code>DistanceMode</code> defining what altitude to use
     *                  in the distance calculation.
     * @return          The great circle distance between this shape and 
     *                  <code>shape</code> using the given <code>DistanceMode</code>.
     */
    public double greatCircleDistance(Shape3D shape, DistanceMode distMode) {
        return LinAlg3D.greatCircleDistance(this, shape, distMode);
    }

    /**
     * Determines whether this shape intersects a given 3D shape.
     * @param shape A <code>Shape3D</code>.
     * @return      <code>true</code> if this shape intersects <code>shape</code>;
     *              <code>false</code> otherwise.
     * @throws IllegalArgumentException If the given <code>Shape3D</code> or this
     *              shape is not supported by the <code>LinAlg3D.intersects()<code>
     *              method.
     */
    public boolean intersects(Shape3D shape) {
        return LinAlg3D.intersects(this, shape);
    }

    /**
     * Calculates the 3D shape created by the intersection of this shape and a
     * given 3D shape.
     * @param shape A <code>Shape3D</code>.
     * @return      A <code>Shape3D</code> defining the shared intersection area
     *              between this shape and <code>shape</code>.
     * @throws IllegalArgumentException If the given <code>Shape3D</code> or this
     *              shape is not supported by the <code>LinAlg3D.intersection()<code>
     *              method.
     * @throws IntersectException if this shape and <code>shape</code> do not
     *              intersect.
     */
    public Shape3D intersection(Shape3D shape) {
        //return LinAlg3D.intersection(this, shape);
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Calculates the 3D shape volume created by the intersection of this shape and a
     * given 3D shape.
     * @param shape A <code>Shape3D</code>.
     * @return      A <code>Shape3D</code> defining the shared intersection area
     *              between this shape and <code>shape</code>.
     * @throws IllegalArgumentException If the given <code>Shape3D</code> or this
     *              shape is not supported by the <code>LinAlg3D.intersectionVolume()<code>
     *              method.
     * @throws IntersectException if this shape and <code>shape</code> do not
     *              intersect.
     */
    public double intersectionVolume(Shape3D shape) {
        return LinAlg3D.intersectionVolume(this, shape);
    }

    /*********************************************
     * MARK: Coordinate Conversion
     *********************************************/

    /**
     * Converts a given point from world coordinates to this shape's local
     * coordinate system.
     * @param point A 3D vector describing a point, in world coordinates, to be
     *              converted to this shapes local coordinate system.
     * @return      A <code>Vector3D</code> object describing the given point
     *              relative to this shape's current position.
     */
    public Vector3D toLocal(Vector3D point) {
        return point.subtract(getPosition());
    }

    /**
     * Converts a given point from the local coordinate system of a given shape
     * to this shape's local coordinate system.
     * @param point A 3D vector describing a point, in world coordinates, to be
     *              converted to this shapes local coordinate system.
     * @param shape A <code>Shape3D</code> from which the given point will be
     *              converted.
     * @return      A <code>Vector3D</code> object describing the given point
     *              relative to this shape's current position.
     */
    public Vector3D toLocal(Vector3D point, Shape3D shape) {
        return toLocal(shape.toWorld(point));
    }

    /**
     * Converts a given point from local coordinates to world coordinates, based
     * on the current position of this shape.
     * @param point A 3D vector describing a point, in this shapes local coordinate
     *              system, to be converted to world coordinates.
     */
    public Vector3D toWorld(Vector3D point) {
        return point.add(getPosition());
    }

    /*********************************************
     * MARK: Abstract
     *********************************************/

    /**
     * Computes the surface area of this shape.
     * @return  The surface area of this shape.
     */
    public abstract double surfaceArea();

    /**
     * Computes the volume of this shape.
     * @return  The volume of this shape.
     */
    public abstract double volume();
    
    /**
     * Computes this shape's minimum bounding box.
     * @return  This shape's minimum bounding box.
     */
    public abstract BoundingBox3D boundingBox();
}