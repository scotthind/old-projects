package org.rowan.linalgtoolkit.shapes2d;

import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.BoundingBox2D;

/**
 * The <code>Circle2D</code> class describes a circle in 2D cartesian space defined
 * by a center point position and radius.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public class Circle2D extends Shape2D {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The radius of this circle. */
    private double radius;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/    
    
    /**
     * Creates a <code>Circle2D</code> object at a given position, with a given 
     * radius.
     * @param position  A 2D vector describing the position of the circle in world 
     *                  coordinates.
     * @param radius    The radius of the circle to be created.
     */
    public Circle2D(Vector2D position, double radius) {
        // initialize with super constructor
        super(position);
        
        // set radius
        this.radius = radius;
        
        // a circle is defined by a single point at its local origin
        this.vertices.add(Vector2D.ORIGIN);
    }
    
    /**
     * Creates a <code>Circle2D</code> object at the origin, with a given radius.
     * @param radius    The radius of the circle to be created.
     */
    public Circle2D(double radius) {
        this(Vector2D.ORIGIN, radius);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/    
    
    /**
     * Returns the radius of this circle.
     * @return  The radius of this circle.
     */
    public double getRadius() {
        return this.radius;
    }
    
    /**
     * Returns the center point of this circle. The Center point of a <code>Circle2D</code>
     * is always identical to the circle's current position.
     * @return  The center point of this circle.
     */
    public Vector2D getCenter() {
        return getPosition();
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/    
    
    /**
     * Sets the radius of this circle to a given value.
     * @param radius    The value, greater than 0, to be set as the circle's radius.
     * @throws  IllegalArgumentException If the given radius is not greater than
     *          0.
     */
    public void setRadius(double radius) {
        // invalid radius?
        if (radius <= 0)
            throw new IllegalArgumentException("Invalid circle radius: must be greater than 0");
        
        // set new radius
        this.radius = radius;
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/    
    
    /**
     * Computes the perimeter of this circle.
     * @return  The perimeter of this circle.
     */
    public double perimeter() {
        return (2 * Math.PI * radius);
    }
    
    /**
     * Computes the area of this circle.
     * @return  The area of this circle.
     */
    public double area() {
        return (Math.PI * radius * radius);
    }
    
    /**
     * Computes this circle's minimum bounding box. This overides the <code>boundingBox()</code> 
     * method in <code>Shape2D</code>.
     * @return  This circle's minimum bounding box.
     */
    public BoundingBox2D boundingBox() {
        // create bounding box vertices
        Vector2D bottomLeft = new Vector2D(-this.radius, -this.radius);
        Vector2D topRight = new Vector2D(this.radius, this.radius);
        
        // convert vertices to world coords
        bottomLeft = toWorld(bottomLeft);
        topRight = toWorld(topRight);
        
        // create and return bounding box
        return new BoundingBox2D(bottomLeft, topRight);
    }

}

