package org.rowan.linalgtoolkit;

import java.util.List;
import java.util.LinkedList;

/**
 * The <code>BoundingBox2D</code> class is is used to define an immutable 2D 
 * minimum bounding box. For any given 2D shape, that shape's bounding box is a
 * 2D rectangle, parallel to the x and y axes, that contains every point on that 
 * shape.
 * 
 * A <code>BoundingBox2D</code> is defined by two points, <code>a</code> and 
 * <code>b</code>, in world coordinates. Point <code>a</code> is the bottom-left 
 * vertex of the bounding box, while point <code>b</code> is the top-right vertex.
 *
 * @author Spence DiNicolantonio, Michael Liguori
 * @version 1.1
 * @since 1.1
 */
public class BoundingBox2D {
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The bottom-left vertex of this bounding box */
    private Vector2D a;
    
    /** The top-right vertex of this bounding box */
    private Vector2D b;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a <code>BoundingBox2D</code> object with given bottom-left and 
     * top-right vertices.
     * @param a A <code>Vector2D</code> describing the bounding box's bottom-left 
     *          vertex.
     * @param b A <code>Vector2D</code> describing the bounding box's top-right 
     *          vertex.
     * @throws IllegalArgumentException If <code>b</code>'s x component is less 
     *                      than <code>a</code>'s x component, or <code>b</code>'s 
     *                      y component is less than <code>a</code>'s y component.
     */
    public BoundingBox2D(Vector2D a, Vector2D b) {
        // validate given vertices
        if (b.getX() < a.getX() || b.getY() < a.getY())
            throw new IllegalArgumentException("Invalid bounding box coordinate values");
        
        // set a and b vertices
        this.a = a;
        this.b = b;
    }
    
    /**
     * Creates a <code>BoundingBox2D</code> object for a given set of vertices, 
     * in world coordinates
     * @param vertices  A list of <code>Vector2D</code>s describing the vertices,
     *                  in world coordinates, for which the bounding box will be 
     *                  created.
     */
    public BoundingBox2D(List<Vector2D> vertices){
        double bottomMostY = Double.POSITIVE_INFINITY;
        double leftMostX   = Double.POSITIVE_INFINITY;
        double topMostY    = Double.NEGATIVE_INFINITY;
        double rightMostX  = Double.NEGATIVE_INFINITY;
        for(Vector2D v : vertices){
            //is vector's X farthest negative X value?
            if(v.getX() < leftMostX)
                leftMostX = v.getX();
            //is vector's X farthest positive X value?
            if(v.getX() > rightMostX)
                rightMostX = v.getX();
            //is vector's Y farthest negative Y value?
            if(v.getY() < bottomMostY)
                bottomMostY = v.getY();
            //is vector's Y farthest positive Y value?
            if(v.getY() > topMostY)
                topMostY = v.getY();
        }
        
        // set a and b vertices
        this.a = new Vector2D(leftMostX, bottomMostY);
        this.b = new Vector2D(rightMostX, topMostY);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns this bounding box's bottom-left vertex, in world coordinates.
     * @return  This bounding box's bottom-left vertex, in world coordinates.
     */
    public Vector2D getA() {
        return this.a;
    }
    
    /**
     * Returns this bounding box's top-right vertex, in world coordinates.
     * @return  This bounding box's top-right vertex, in world coordinates.
     */
    public Vector2D getB() {
        return this.b;
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Returns this bounding box's center point, in world coordinates.
     * @return  This bounding box's center point, in world coordinates.
     */
    public Vector2D getCenter() {
        double x = a.getX() + (b.getX() - a.getX()) / 2;
        double y = a.getY() + (b.getY() - a.getY()) / 2;
        return new Vector2D(x, y);
    }
    
    /**
     * Calculates the width of this bounding box. That is, the length of this of
     * this bounding box along the x axis.
     * @return  The width of this bounding box.
     */
    public double width() {
        return b.getX() - a.getX();
    }
    
    /**
     * Calculates the height of this bounding box. That is, the length of this of
     * this bounding box along the y axis.
     * @return  The height of this bounding box.
     */
    public double height() {
        return b.getY() - a.getY();
    }
    
    
    /*********************************************
     * MARK: Linear Algebra
     *********************************************/
    
    /**
     * Determines whether a given 2D vertex lies within this bounding box.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question.
     * @return          <code>true</code> if <code>vertex</code> is wholly contained
     *                  by this bounding box; <code>false</code> otherwise.
     */
    public boolean contains(Vector2D vertex) {
        return (vertex.getX() >= this.a.getX() &&
                vertex.getY() >= this.a.getY() &&
                vertex.getX() <= this.b.getX() &&
                vertex.getY() <= this.b.getY());
    }
    
    /**
     * Determines whether a given 2D bounding box is wholly contained by this 
     * bounding box.
     * @param boundingBox   The <code>BoundingBox2D</code> in question.
     * @return              <code>true</code> if <code>boundingBox</code> is 
     *                      wholly contained by this bounding box; <code>false</code> 
     *                      otherwise.
     */
    public boolean contains(BoundingBox2D boundingBox) {
        return (contains(boundingBox.getA()) &&
                contains(boundingBox.getB()));
    }
    
    /**
     * Determines whether a given 2D bounding box intersects this bounding box.
     * @param boundingBox   A <code>BoundingBox2D</code>.
     * @return              <code>true</code> if <code>boundingBox</code> intersects
     *                      this bounding box; <code>false</code> otherwise.
     */
    public boolean intersects(BoundingBox2D boundingBox) {
        double w = (width() + boundingBox.width()) / 2;
        double h = (height() + boundingBox.height()) / 2;
        
        Vector2D thisCenter = getCenter();
        Vector2D givenCenter = boundingBox.getCenter();
        
        double dx = Math.abs(getCenter().getX() - boundingBox.getCenter().getX());
        double dy = Math.abs(getCenter().getY() - boundingBox.getCenter().getY());
        
        if (dx <= w && dy <= h)
            return true;
        return false;
    }
    
    
    /*********************************************
     * MARK: Other
     *********************************************/
    
    /**
     * Calculates the minimum bounding box that contains both this bounding box 
     * and a given 2D bounding box.
     * @param boundingBox   The <code>BoundingBox2D</code> to be merged with this 
     *                      bounding box.
     * @return              The 2D minimum bounding box that contains both this 
     *                      bounding box and <code>boundingBox</code>.
     */
    public BoundingBox2D merge(BoundingBox2D boundingBox) {
        // create a list of both bounding boxes' vertices
        LinkedList<Vector2D> vertices = new LinkedList<Vector2D>();
        vertices.add(a);
        vertices.add(b);
        vertices.add(boundingBox.getA());
        vertices.add(boundingBox.getB());
        
        // create and return a new bounding box using the constructed list
        return new BoundingBox2D(vertices);
    }
    
    /**
     * Calculates the 2D minimum bounding box that contains both this bounding 
     * box and a given 2D vertex.
     * @param vertex    A <code>Vector2D</code> object describing the vertex.
     * @return          The minimum bounding box that contains both this bounding 
     *                  box and <code>vertex</code>.
     */
    public BoundingBox2D expand(Vector2D vertex) {
        // create a list of this bounding box's vertices and the given vertex
        LinkedList<Vector2D> vertices = new LinkedList<Vector2D>();
        vertices.add(a);
        vertices.add(b);
        vertices.add(vertex);
        
        // create and return a new bounding box using the constructed list
        return new BoundingBox2D(vertices);
    }
    
}
