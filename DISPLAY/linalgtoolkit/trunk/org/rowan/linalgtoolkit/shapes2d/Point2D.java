package org.rowan.linalgtoolkit.shapes2d;

import org.rowan.linalgtoolkit.Vector2D;

/**
 * The <code>Point2D</code> class describes a single point in 2D Cartesian space.
 * <code>Point2D</code> shapes are defined with a single vertex at its local origin.
 * <p>
 * A <code>Point2D</code> object is infinitesimal and thus has no area and no 
 * perimeter.
 * 
 * @author Spence DiNicolantonio, Robert Russell
 * @version 1.1
 * @since 1.0
 */
public class Point2D extends Shape2D {
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/    
    
    /**
     * Creates a <code>Point2D</code> object at a given position.
     * @param position  A 2D vector describing the position of the point in world 
     *                  coordinates.
     */
    public Point2D(Vector2D position) {
        // initialize with super constructor
        super(position);
        
        // a point is defined by a single point at its local origin
        this.vertices.add(Vector2D.ORIGIN);
    }
    
    /**
     * Creates a <code>Point2D</code> object with given x and y component values.
     * @param x The x component of the point to be created.
     * @param y The y component of the point to be created.
     */
    public Point2D(double x, double y) {
        this(new Vector2D(x, y));
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/    
    
    /**
     * Returns the x component of this point.
     * @return  The x component of this point.
     */
    public double getX() {
        return getPosition().getX();
    }
    
    /**
     * Returns the y component of this point.
     * @return  The y component of this point.
     */
    public double getY() {
        return getPosition().getY();
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/    
    
    /**
     * Sets the x component of this point to a given value.
     * @param x The x component to be set for this point.
     */
    public void setX(double x) {
        double y = getPosition().getY();
        setPosition(new Vector2D(x, y));
    }
    
    /**
     * Sets the y component of this point to a given value.
     * @param y The y component to be set for this point.
     */
    public void setY(double y) {
        double x = getPosition().getX();
        setPosition(new Vector2D(x, y));
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/    
    
    /**
     * Computes the perimeter of this point. Note that the perimeter of a point
     * is always 0.
     * @return  The perimeter of this point, which is 0 by definition.
     */
    public double perimeter() {
        return 0;
    }
    
    /**
     * Computes the area of this point. Note that the area of a point is always 
     * 0.
     * @return  The area of this point, which is 0 by definition.
     */
    public double area() {
        return 0;
    }
    
}

