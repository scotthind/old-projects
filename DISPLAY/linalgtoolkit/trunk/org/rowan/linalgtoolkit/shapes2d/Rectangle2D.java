package org.rowan.linalgtoolkit.shapes2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;

/**
 * The <code>Rectangle2D</code> class describes a rectangle in 2D cartesian space 
 * defined by a list of four perpendicular vertices with clockwise winding. 
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public class Rectangle2D extends Polygon2D {
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/

    /** The width of this rectangle. */
    double width;
    
    /** The height of this rectangle. */
    double height;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a <code>Rectangle2D</code> object at a given position, defined by 
     * a given list of vertices, in local coordinates, with clockwise winding.
     * <p>
     * The given list of vertices should start with either the bottom-left or 
     * top-right vertex of the rectangle, as this will determine the orientation
     * of height vs. width.
     * @param position  A 2D vector describing the position of the rectange in 
     *                  world coordinates.
     * @param vertices  An ordered list of vertices, relative to the rectangle's
     *                  center point, describing a rectange with clockwise winding.
     */
    public Rectangle2D(Vector2D position, List<Vector2D> vertices) {
        super(position, vertices);
    }

    /**
     * Creates a <code>Rectangle2D</code> object at the origin, defined by a given 
     * list of vertices, in local coordinates, with clockwise winding.
     * <p>
     * The given list of vertices should start with either the bottom-left or 
     * top-right vertex of the rectangle, as this will determine the orientation
     * of height vs. width.
     * @param vertices  An ordered list of vertices, relative to the rectangles's
     *                  center point, describing a rectange with clockwise
     *                  winding.
     */
    public Rectangle2D(List<Vector2D> vertices) {
        super(vertices);
    }
    
    /**
     * Creates a <code>Rectangle2D</code> object at a given position, width, and
     * height. The local center point of the rectangle will be located in the center
     * of the rectangle. All sides of the rectangle will be parallel or perpendicular
     * to the x and y axis, respectively.
     * @param position  A 2D vector describing the position of the rectange in 
     *                  world coordinates.
     * @param width     The width of the rectangle.
     * @param height    The height of the rectangle.
     */
    public Rectangle2D(Vector2D position, double width, double height) {
        // compute vertices and call super constructor
        super(position, Rectangle2D.computeVerts(width, height));
    }
    
    /**
     * Creates a <code>Rectangle2D</code> object at the origin, with a given width, 
     * and height. The local center point of the rectangle will be located in the 
     * center of the rectangle. All sides of the rectangle will be parallel or 
     * perpendicular to the x and y axis, respectively.
     * @param width     The width of the rectangle.
     * @param height    The height of the rectangle.
     */
    public Rectangle2D(double width, double height) {
        this(Vector2D.ORIGIN, width, height);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the width of this rectangle.
     * @return  The width of this rectangle.
     */
    public double getWidth() {
        return this.width;
    }
    
    /**
     * Returns the height of this rectangle.
     * @return  The height of this rectangle.
     */
    public double getHeight() {
        return this.height;
    }

    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Redefines this polygon with a given list of vertices, in local coordinates,
     * with clockwise winding. 
     * <p>
     * The given list of vertices should start with either the bottom-left or 
     * top-right vertex of the rectangle, as this will determine the orientation
     * of height vs. width.
     * @param vertices  An ordered list of vertices, relative to the rectangles's
     *                  center point, describing a rectangle with clockwise
     *                  winding.
     */
    @Override
    public void setVertices(List<Vector2D> vertices) {
        // check to make sure there is 4 vertices
        if (vertices.size() != 4) {
            throw new IllegalArgumentException("Invalid vertices: A rectangle must " +
                                               "be defined with exactly 4 vertices. " +
                                               vertices.size() + " given.");
        }
        // For a polygon to be a rectangle
        // angle between adjacent edges must be 90 degrees
        // Make sure the lines are parallel
        Vector2D v1 = vertices.get(0);
        Vector2D v2 = vertices.get(1);
        Vector2D v3 = vertices.get(2);
        Vector2D v4 = vertices.get(3);

        // see if v1-v2 is perp to v2-v3 and perp to v1-v4
        if ((v2.subtract(v1).dot(v3.subtract(v2))) != 0
                || (v2.subtract(v1).dot(v4.subtract(v1))) != 0) {

            throw new IllegalArgumentException("Invalid vertices: Given vertices "
                    + "do not describe a rectangle:\n" + vertices);
        }
        // see if v3-v4 is perp to v4-v1 and perp to v2-v3
        if ((v4.subtract(v3).dot(v4.subtract(v1))) != 0
                || (v4.subtract(v3).dot(v3.subtract(v2))) != 0) {

            throw new IllegalArgumentException("Invalid vertices: Given vertices "
                    + "do not describe a rectangle:\n" + vertices);
        }

        // set vertices
        super.setVertices(vertices);
        
        // set height and width
        this.height = v1.distance(v2);
        this.width = v2.distance(v3);
    }
    
    /**
     * Sets the width of this rectangle to a given value. The rectangle will expand
     * or contract, equally on both sides to reach the given width.
     * @param width A value, greater than or equal to 0, to which this rectangle's
     *              width will be set.
     * @throws IllegalArgumentException If <code>width</code> is less than 0.
     */
    public void setWidth(double width) {
        // negative width?
        if (width < 0)
            throw new IllegalArgumentException("Invalid rectangle width: Cannot " +
                                               "be less than 0.");
        
        // store all vertices for easy access
        Vector2D v1 = getVertex(0);
        Vector2D v2 = getVertex(1);
        Vector2D v3 = getVertex(2);
        Vector2D v4 = getVertex(3);
        
        // compute the change in width
        double delta = width - this.width;
        
        // find the direction of v2 to v3
        Vector2D v2v3 = v3.subtract(v2);
        
        // determine changes for vertices
        Vector2D delta3And4 = new Vector2D(v2v3, delta/2);
        Vector2D delta1And2 = delta3And4.inverse();
        
        // apply changes
        v1 = v1.add(delta1And2);
        v2 = v2.add(delta1And2);
        v3 = v3.add(delta3And4);
        v4 = v4.add(delta3And4);
        
        // set new vertices
        LinkedList<Vector2D> verts = new LinkedList<Vector2D>();
        verts.add(v1);
        verts.add(v2);
        verts.add(v3);
        verts.add(v4);
        setVertices(verts);
    }
    
    /**
     * Sets the height of this rectangle to a given value. The rectangle will expand
     * or contract, equally on both sides to reach the given height.
     * @param height    A value, greater than or equal to 0, to which this rectangle's
     *                  height will be set.
     * @throws IllegalArgumentException If <code>height</code> is less than 0.
     */
    public void setHeight(double height) {
        // negative height?
        if (height < 0)
            throw new IllegalArgumentException("Invalid rectangle height: Cannot " +
                                               "be less than 0.");
        
        // store all vertices for easy access
        Vector2D v1 = getVertex(0);
        Vector2D v2 = getVertex(1);
        Vector2D v3 = getVertex(2);
        Vector2D v4 = getVertex(3);
        
        // compute the change in height
        double delta = height - this.height;
        
        // find the direction of v1 to v2
        Vector2D v1v2 = v2.subtract(v1);
        
        // determine changes for vertices
        Vector2D delta2And3 = new Vector2D(v1v2, delta/2);
        Vector2D delta1And4 = delta2And3.inverse();
        
        // apply changes
        v1 = v1.add(delta1And4);
        v2 = v2.add(delta2And3);
        v3 = v3.add(delta2And3);
        v4 = v4.add(delta1And4);
        
        // set new vertices
        LinkedList<Vector2D> verts = new LinkedList<Vector2D>();
        verts.add(v1);
        verts.add(v2);
        verts.add(v3);
        verts.add(v4);
        setVertices(verts);
    }
    
    
    
    /**
     * Sets the width and height of this rectangle to given values. The rectangle 
     * will expand or contract, equally on respective sides to reach the given 
     * width and height.
     * @param width     A value, greater than or equal to 0, to which this rectangle's
     *                  width will be set.
     * @param height    A value, greater than or equal to 0, to which this rectangle's
     *                  height will be set.
     * @throws IllegalArgumentException If <code>width</code> or <code>height</code>
     *                  is less than 0.
     */
    public void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Computes the vertices to define a rectangle with a given width and height.
     * The local center point of the rectangle will be located in the center of 
     * the rectangle. All sides of the rectangle will be parallel or perpendicular
     * to the x and y axis, respectively.
     */
    private static List<Vector2D> computeVerts(double width, double height) {
        // calculate vertices for rectangle
        LinkedList<Vector2D> verts = new LinkedList<Vector2D>();
        verts.add(new Vector2D(-width/2, -height/2));
        verts.add(new Vector2D(-width/2, height/2));
        verts.add(new Vector2D(width/2, height/2));
        verts.add(new Vector2D(width/2, -height/2));
        
        return verts;
    }
}
