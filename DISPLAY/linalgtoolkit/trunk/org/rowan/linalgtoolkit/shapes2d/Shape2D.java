package org.rowan.linalgtoolkit.shapes2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.BoundingBox2D;
import org.rowan.linalgtoolkit.logic2d.LinAlg2D;

/**
 * The <code>Shape2D</code> class is an abstract class providing limited implementation 
 * and guidelines for defining 2D Shapes.
 * 
 * @author Spence DiNicolantonio, Michael Liguori, Robert Russell
 * @version 1.1
 * @since 1.0
 */
public abstract class Shape2D {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The position of this shape in world coordinates. */
    private Vector2D position;
    
    /** The angular rotation offset of this shape, in radians. */
    private double angle;
    
    /** The velocity of this shape. */
    private Vector2D velocity;
    
    /** The vertices that define this shape, in local coordinates */
    protected LinkedList<Vector2D> vertices;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/ 
    
    /** 
     * Initializes a <code>Shape2D</code> object at a given position, with 0 
     * velocity.
     * @param position  A 2D vector describing the position of the shape in world 
     *                  coordinates.
     */
    protected Shape2D(Vector2D position) {
        this.position = position;
        this.angle = 0.0;
        this.velocity = Vector2D.ZERO_VECTOR;
        this.vertices = new LinkedList<Vector2D>();
    }
    
    /** 
     * Initializes a <code>Shape2D</code> object at the origin with 0 velocity.
     */
    protected Shape2D() {
        this(Vector2D.ORIGIN);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the position of this shape.
     * @return  The position of this shape.
     */
    public Vector2D getPosition() {
        return this.position;
    }
    
    /**
     * Returns the angular rotation offset of this shape.
     * @return  The angular rotation offset, in radians.
     */
    public double getAngle() {
        return this.angle;
    }
    
    /**
     * Returns the velocity of this shape.
     * @return  The velocity of this shape.
     */
    public Vector2D getVelocity() {
        return this.velocity;
    }
    
    /**
     * Returns an ordered list of vertices, in local coordinates, that initially 
     * defined this shape.
     * <p>
     * Altering the returned list will have no effect on the shape.
     * @return  A <code>List</code> of <code>Vector2D</code> objects that define
     *          this shape.
     */
    public List<Vector2D> getInitialVertices() {
        return new LinkedList<Vector2D>(this.vertices);
    }
    
    /**
     * Returns an ordered list of vertices, in local coordinates, that define 
     * this shape. These are the initial vertices used to define the shape, rotated 
     * by the shape's angular offset.
     * <p>
     * Altering the returned list will have no effect on the shape.
     * @return  A <code>List</code> of <code>Vector2D</code> objects that define
     *          this shape.
     */
    public List<Vector2D> getVertices() {
        // compute rotation vector
        Vector2D rotVector = new Vector2D(this.angle);
        
        // rotate vertices
        LinkedList<Vector2D> rotatedVerts = new LinkedList<Vector2D>();
        for (Vector2D vert : this.vertices)
            rotatedVerts.add(vert.rotate(rotVector));
        
        // return the list of vertices
        return rotatedVerts;
    }
    
    /**
     * Returns an ordered list of the vertices, in world coordinates, that define 
     * this shape. These are the initial vertices used to define the shape, rotated 
     * by the shape's angular offset, and converted to world coordinates.
     * <p>
     * Altering the returned list will have no effect on the shape.
     * @return  A <code>List</code> of <code>Vector2D</code> objects that define
     *          this shape, in world coordinates.
     */
    public List<Vector2D> getWorldVertices() {
        // convert each vertex to world coords
        List<Vector2D> worldVertices = new LinkedList<Vector2D>();
        for (Vector2D vert : getVertices())
            worldVertices.add(toWorld(vert));
        
        // return the created list of vertices
        return worldVertices;
    }
    
    /**
     * Returns this shape's vertex located at a given index.
     * @param index The index of the desired vertex.
     * @return      This shape's vertex at the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    public Vector2D getVertex(int index) {
        return getVertices().get(index);
    }
    
    /**
     * Returns this shape's initially defined vertex located at a given index.
     * This is the shape's vertex without considering the shape's current angular 
     * offset.
     * @param index The index of the desired vertex.
     * @return      This shape's initially defined vertex at the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    public Vector2D getInitialVertex(int index) {
        return this.vertices.get(index);
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the position of this shape to a given point.
     * @param position  A 2D vector describing the shape's new position.
     */
    public void setPosition(Vector2D position) {
        this.position = position;
    }  
    
    /**
     * Sets the angular rotation offset of this shape to a given angle.
     * @param angle An double value defining the shape's new angle, in radians.
     */
    public void setAngle(double angle) {
        if (angle >= 0.0)
            this.angle = angle % (2*Math.PI);
        else
            this.angle = (2*Math.PI) - (Math.abs(angle) % 2*Math.PI);
    }
    
    /**
     * Sets the velocity of this shape to a given vector.
     * @param velocity  A 2D vector describing the shape's new velocity.
     */
    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Rotates this shape about its center point, by the angle described by a 
     * given 2D rotation vector.
     * <p>
     * This method only rotates this shape's vertices. Any <code>Shape2D</code> 
     * subclass that does not store its vertices explicitly, should override this 
     * method as needed.
     * @param rotVector A 2D rotation vector describing the angle of rotation 
     *                  to be applied to this shape.
     */
    public void rotate(Vector2D rotVector) {
        rotate(rotVector.toAngle());
    }
    
    /**
     * Rotates this shape about a given vertex, in world coordinates, by the angle 
     * described by a given 2D rotation vector.
     * @param rotVector A 2D rotation vector describing the angle of rotation 
     *                  to be applied to this shape.
     * @param vertex    A <code>Vector2D</code> describing the vertex, in world 
     *                  coordinates about which this shape will be rotated.
     */
    public void rotate(Vector2D rotVector, Vector2D vertex) {
        rotate(rotVector.toAngle(), vertex);
    }
    
    /**
     * Rotates this shape given angle, in radians, about its center
     * point.
     * @param angle The angle, in radians, to rotate this shape.
     */
    public void rotate(double angle) {
        setAngle(getAngle() + angle);
    }
    
    /**
     * Rotates this shape clockwise by a given angle, in radians, about a given 
     * vertex, in world coordinates.
     * @param angle     The angle, in radians, to rotate this shape.
     * @param vertex    A <code>Vector2D</code> describing the vertex, in world 
     *                  coordinates, around which to rotate this shape.
     */
    public void rotate(double angle, Vector2D vertex) {
        // rotate the position vertex
        this.position = position.rotate(angle, vertex);
        
        // rotate the shape around its local origin
        rotate(angle);
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Returns the number of vertices that define this shape.
     * @return  The number of vertices that define this shape.
     */
    public int vertCount() {
        return this.vertices.size();
    }
    
    /**
     * Computes this shape's minimum bounding box. 
     * <p>
     * This method uses this shape's vertex list to find the minimum enclosing 
     * box. Any <code>Shape2D</code> subclass that does not store its vertices 
     * explicitly, should override this method as needed.
     * @return  This shape's minimum bounding box.
     */
    public BoundingBox2D boundingBox() {
        // create bounding box using world coords
        return new BoundingBox2D(getWorldVertices());
    }
    
    
    /*********************************************
     * MARK: Linear Algebra
     *********************************************/
    
    /**
     * Determines whether a given 2D vertex, in world coordinates, lies within
     * this shape.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question.
     * @return          <code>true</code> if <code>vertex</code> is wholly contained
     *                  by this shape; <code>false</code> otherwise.
     * @throws IllegalArgumentException If this shape is not supported by the 
     *                  <code>LinAlg2D.contains()<code> method.
     */
    public boolean contains(Vector2D vertex) {
        return LinAlg2D.contains(this, vertex);
    }
    
    /**
     * Determines whether a given 2D shape, is wholly contained by this shape.
     * @param shape     The child <code>Shape2D</code>.
     * @return          <code>true</code> if <code>shape</code> is wholly contained
     *                  by this shape; <code>false</code> otherwise.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> or this 
     *                  shape is not supported by the <code>LinAlg2D.contains()<code> 
     *                  method.
     */
    public boolean contains(Shape2D shape) {
        return LinAlg2D.contains(this, shape);
    }
    
    /**
     * Computes the distance between this shape and a given 2D vertex.
     * @param vertex    A <code>Vector2D</code> describing the vertex.
     * @return          The distance between this shape and <code>vertex</code>.
     * @throws IllegalArgumentException If this shape is not supported by the 
     *                  <code>LinAlg2D.distance()<code> method.
     */
    public double distance(Vector2D vertex) {
        return LinAlg2D.distance(this, vertex);
    }
    
    /**
     * Computes the distance between this shape and a given 2D shape.
     * @param shape A <code>Shape2D</code>.
     * @return      The distance between this shape and <code>shape</code>.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> or this 
     *              shape is not supported by the <code>LinAlg2D.distance()<code> 
     *              method.
     */
    public double distance(Shape2D shape) {
        return LinAlg2D.distance(this, shape);
    }
    
    /**
     * Determines whether this shape intersects a given 2D shape.
     * @param shape A <code>Shape2D</code>.
     * @return      <code>true</code> if this shape intersects <code>shape</code>; 
     *              <code>false</code> otherwise.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> or this 
     *              shape is not supported by the <code>LinAlg2D.intersects()<code> 
     *              method.
     */
    public boolean intersects(Shape2D shape) {
        return LinAlg2D.intersects(this, shape);
    }
    
    /**
     * Calculates the 2D shape created by the intersection of this shape and a
     * given 2D shape.
     * @param shape A <code>Shape2D</code>.
     * @return      A <code>Shape2D</code> defining the shared intersection area 
     *              between this shape and <code>shape</code>.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> or this 
     *              shape is not supported by the <code>LinAlg2D.intersection()<code> 
     *              method.
     * @throws IntersectException if this shape and <code>shape</code> do not 
     *              intersect.
     */
    public Shape2D intersection(Shape2D shape) {
        return LinAlg2D.intersection(this, shape);
    }
    
    /**
     * Calculates the area of intersection between this shape and a given 2D shape.
     * <p>
     * This method does not currently support complex shapes that contain circles.
     * @param  shape    A <code>Shape2D</code>.
     * @return          The area of intersection this shape and <code>shape</code>.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> instance
     *                  is not supported.
     */
    public double intersectionArea(Shape2D shape) {
        return LinAlg2D.intersectionArea(this, shape);
    }
    
    /**
     * Calculates the union of this shape and a given 2D shape. This method creates 
     * a complex shape using this shape and the given shape as sub-shapes.
     * @param shape A <code>Shape2D</code>.
     * @return      A <code>ComplexShape2D</code> defining the union of
     *              <code>shape1</code> and <code>shape2</code>.
     */
    public ComplexShape2D union(Shape2D shape) {
        return LinAlg2D.union(this, shape);
    }
    
    
    /*********************************************
     * MARK: Coordinate Conversion
     *********************************************/
    
    /**
     * Converts a given point from world coordinates to this shape's local 
     * coordinate system.
     * @param point A 2D vector describing a point, in world coordinates, to be
     *              converted to this shapes local coordinate system.
     * @return      A <code>Vector2D</code> object describing the given point 
     *              relative to this shape's current position.
     */
    public Vector2D toLocal(Vector2D point) {
        return point.subtract(getPosition());
    }
    
    /**
     * Converts a given point from the local coordinate system of a given shape
     * to this shape's local coordinate system.
     * @param point A 2D vector describing a point, in world coordinates, to be
     *              converted to this shapes local coordinate system.
     * @param shape A <code>Shape2D</code> from which the given point will be 
     *              converted.
     * @return      A <code>Vector2D</code> object describing the given point 
     *              relative to this shape's current position.
     */
    public Vector2D toLocal(Vector2D point, Shape2D shape) {
        return toLocal(shape.toWorld(point));
    }
    
    /**
     * Converts a given point from local coordinates to world coordinates, based
     * on the current position of this shape.
     * @param point A 2D vector describing a point, in this shapes local coordinate
     *              system, to be converted to world coordinates.
     */
    public Vector2D toWorld(Vector2D point) {
        return point.add(getPosition());
    }
        
    
    /*********************************************
     * MARK: Abstract
     *********************************************/
    
    /**
     * Computes the perimeter of this shape.
     * @return  The perimeter of this shape.
     */
    public abstract double perimeter();
    
    /**
     * Computes the area of this shape.
     * @return  The area of this shape.
     */
    public abstract double area();

}

