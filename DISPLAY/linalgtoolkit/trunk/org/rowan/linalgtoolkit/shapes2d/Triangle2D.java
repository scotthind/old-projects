package org.rowan.linalgtoolkit.shapes2d;

import java.util.LinkedList;
import java.util.List;
import org.rowan.linalgtoolkit.Vector2D;

/**
 * The <code>Triangle2D</code> class describes a triangle in 2D cartesian space
 * defined by a list of three vertices with clockwise winding.
 *
 * @author Michael Liguori
 * @version 1.1
 * @since 1.1
 */
public class Triangle2D extends Polygon2D {


    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Creates a <code>Triangle2D</code> object at a given position, defined by 
     * a given list of three vertices, in local coordinates, with clockwise winding.
     * @param position  A 2D vector describing the position of the triangle in world
     *                  coordinates.
     * @param vertices  An ordered list of three vertices, relative to the triangle's
     *                  center point, describing a triangle with clockwise winding.
     */
    public Triangle2D(Vector2D position, List<Vector2D> vertices){
        super(position, vertices);
    }

    /**
     * Creates a <code>Triangle2D</code> object at the origin, defined by a given 
     * list of three vertices, in local coordinates, with clockwise winding.
     * @param vertices  An ordered list of three vertices, relative to the triangle's
     *                  center point, describing a triangle with clockwise winding.
     */
    public Triangle2D(List<Vector2D> vertices){
        super(vertices);
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/

    /**
     * Redefines this polygon with a given list of vertices, in local coordinates,
     * with clockwise winding.
     * @param vertices  An ordered list of three vertices, relative to the polygon's
     *                  center point, describing a convex polygon with clockwise
     *                  winding.
     * @throws IllegalArgumentException If the given list of vertices does not
     *                  describe a triangle with clockwise winding, or if the 
     *                  given vertices to not surround the triangles's local
     *                  origin.
     */
    @Override
    public void setVertices(List<Vector2D> vertices) {
        if(vertices.size() != 3){
            throw new IllegalArgumentException("Invalid vertices: A triangle must " +
                                               "be defined with exactly 3 vertices. " +
                                               vertices.size() + " given.");
        }
        super.setVertices(vertices);
    }
    
    /*********************************************
     * MARK: Static
     *********************************************/
    
    /**
     * Calculates the Global center point of a triangle given
     * 3 global coordinates in a list.
     *
     * @param points A LinkedList of <code>Vector2D</code> instances which
     *               describe a triangles global points.
     * @throws       IllegalArgumentException
     * @return       A <code>Vector2D</code> instance which is the calculated
     *               global center point of the triangle.
     */
    public static Vector2D center(LinkedList<Vector2D> points){
        if(points.size() != 3){
            throw new IllegalArgumentException ("These points do not " +
                                                  " describe a Triangle");
        }

        Vector2D a = points.get(0);
        Vector2D b = points.get(1);
        Vector2D c = points.get(2);

        double sideA = b.distance(c);//side A is oppisite point Xa, Ya
        double sideB = c.distance(a);//side B is oppisite point Xb, Yb
        double sideC = a.distance(b);//side C is oppisite point Xc, Yc

        double perimeter = sideA + sideB + sideC;

        double centerX = (sideA*a.getX() + sideB*b.getX() + sideC*c.getX())/perimeter;
        double centerY = (sideA*a.getY() + sideB*b.getY() + sideC*c.getY())/perimeter;

        return new Vector2D(centerX,centerY);
    }
}
