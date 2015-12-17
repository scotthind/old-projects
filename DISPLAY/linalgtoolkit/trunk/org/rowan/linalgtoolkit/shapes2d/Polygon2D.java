package org.rowan.linalgtoolkit.shapes2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.logic2d.LinAlg2D;

/**
 * The <code>Polygon2D</code> class describes a convex polygon defined by a list 
 * of vertices with clockwise winding.
 * <p>
 * The center point of a <code>Polygon2D</code> object is located at its local 
 * origin (0, 0). The vertices supplied to a <cod>Polygon2D</code> constructor 
 * must surround this point, or an exception will be thrown.
 * 
 * @author Spence DiNicolantonio, Robert Russell
 * @version 1.1
 * @since 1.0
 */
public class Polygon2D extends Shape2D {
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/    
    
    /**
     * Creates a <code>Polygon2D</code> object at a given position, defined by a 
     * given list of vertices, in local coordinates, with clockwise winding. 
     * @param position  A 2D vector describing the position of the polygon in world 
     *                  coordinates.
     * @param vertices  An ordered list of vertices, relative to the polygon's 
     *                  center point, describing a convex polygon with clockwise 
     *                  winding.
     * @throws IllegalArgumentException If the given list of vertices does not 
     *                  describe a convex polygon with clockwise winding, or if 
     *                  the given vertices to not surround the polygon's local 
     *                  origin.
     */
    public Polygon2D(Vector2D position, List<Vector2D> vertices) {
        // initialize with super constructor
        super(position);
        
        // set vertices
        setVertices(vertices);
    }
    
    /**
     * Creates a <code>Polygon2D</code> object at the origin, defined by a given 
     * list of vertices, in local coordinates, with clockwise winding. 
     * @param vertices  An ordered list of vertices, relative to the polygon's 
     *                  center point, describing a convex polygon with clockwise 
     *                  winding.
     * @throws IllegalArgumentException If the given list of vertices does not 
     *                  describe a convex polygon with clockwise winding, or if 
     *                  the given vertices to not surround the polygon's local 
     *                  origin.
     */
    public Polygon2D(List<Vector2D> vertices) {
        this(Vector2D.ORIGIN, vertices);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/    
    
    /** 
     * Constructs an ordered <code>LinkedList</code> of 2D segments that define 
     * the edges of this polygon. 
     * <p>
     * The order in which the segments appear in the returned list coincides with 
     * the order of the polygon's vertices. Thus, the first segment in the list
     * will be created from the vertices 1 and 2, the second segment from vertices
     * 2 and 3, etc.
     * <p>
     * The segments returned are created on the fly and not cached.
     * <p>
     * As a polygon, by definition will always have at least three edges, the
     * returned list will have a size of at least three.
     * @return  An ordered <code>LinkedList</code> of <code>Segment2D</code> 
     *          objects that define this polygon.
     */
    public LinkedList<Segment2D> getEdges() {
        // create a linked list to store the edges
        LinkedList<Segment2D> edges = new LinkedList<Segment2D>();
        
        // for each edge in the polygon...
        for (int i=0; i<vertices.size(); i++) {
            
            // create a 2D segment and add it to the list
            edges.add(getEdge(i));
        }
        
        // return the list of edges
        return edges;
    }
    
    /**
     * Constructs a 2D segment defining this polygon's edge located at a given 
     * index. The constructed segment will describe the edge from the vertex at 
     * the given index to the following vertex. The segment is constructed with
     * in the world coordinate system.
     * <p>
     * The segment returned is created on the fly, and not cached.
     * @param index The index of the desired segment.
     * @return      A <code>Segment2D</code> describing this polygon's edge at 
     *              the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    public Segment2D getEdge(int index) {
        // get polygon vertices
        Vector2D vert1 = vertices.get(index);
        Vector2D vert2 = vertices.get((index+1) % vertices.size());
        
        // convert polygon vertices to world coords
        vert1 = toWorld(vert1);
        vert2 = toWorld(vert2);
        
        // construct and return a 2D segment
        return new Segment2D(vert1, vert2);
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/    
    
    /**
     * Redefines this polygon with a given list of vertices, in local coordinates, 
     * with clockwise winding. 
     * @param vertices  An ordered list of vertices, relative to the polygon's 
     *                  center point, describing a convex polygon with clockwise 
     *                  winding.
     * @throws IllegalArgumentException If the given list of vertices does not 
     *                  describe a convex polygon with clockwise winding, or if 
     *                  the given vertices to not surround the polygon's local 
     *                  origin.
     */
    public void setVertices(List<Vector2D> vertices) {
        // validate given vertices
        if (!validateVertices(vertices))
            throw new IllegalArgumentException("Invalid vertices: Given vertices " +
                                               "do not describe a convex polygon " +
                                               "with clockwise winding:\n" + vertices);
        
        // set new vertices
        this.vertices = new LinkedList<Vector2D>(vertices);
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Returns the number of edges that define this polygon. The number of edges 
     * on a 2D polygon is always equal to the number of vertices.
     * @return  The number of edges that define this polygon.
     */
    public int edgeCount() {
        return vertCount();
    }
    
    /**
     * Returns the number of points on this polygon. This is the same as the 
     * <code>vertCount()</code> method.
     * @return  The number of points on this polygon.
     */
    public int points() {
        return vertCount();
    }
    
    /**
     * Returns the number of sides this polygon has. The number of sides on a 
     * 2D polygon is always equal to the number of points.
     * @return  The number of sides this polygon has.
     */
    public int sides() {
        return vertCount();
    }
    
    /**
     * Computes the perimeter of this polygon.
     * @return  The perimeter of this polygon.
     */
    public double perimeter() {
        double perimeter = 0.0;
        
        Vector2D last = vertices.get(vertices.size()-1);
        for (Vector2D curr : vertices) {
            // add distance to tally
            perimeter += last.distance(curr);
            
            // set new last vertex
            last = curr;
        }
        
        return perimeter;
    }
    
    /**
     * Computes the area of this polygon.
     * @return  The area of this polygon.
     */
    public double area() {
        return Polygon2D.area(vertices);
    }

    /**
     * Computes the area for a list of two-dimensional vertices. It is assumed
     * that the list of vertices will represent a convex polygon with clockwise
     * winding.
     * @param vertices a list of two-dimensional vertices.
     * @return the area for a list of two-dimensional vertices.
     */
    public static double area(LinkedList<Vector2D> vertices) {
        // calculate polygon area using Gauss' formula
        double area = 0.0;
        
        for (int i=0; i<vertices.size(); i++) {
            Vector2D curr = vertices.get(i);
            Vector2D next = vertices.get((i+1) % vertices.size());
            
            area += curr.getX() * next.getY();
            area -= curr.getY() * next.getX();
        }
        
        area = -(area / 2);
        return (area == 0)? 0 : area;
    }
    
        
    /*********************************************
     * MARK: Other
     *********************************************/

    /**
     * Computes the centroid of any given <code>Polygon2D</code> instance. The
     * centroid of a polygon is defined as the physical center of mass of the polygon.
     * @return an instance of <code>Vector2D</code>, which is the centroid, or
     * the physical center of mass of the polygon.
     */
    public Vector2D centroid() {
        double centroidX = 0.0, centroidY = 0.0;

        // Summations to determine the X,Y coordinates of the centroid.
        // Summation ranges from i = 0 through n -1, where n is the number of vertices.
        for (int i = 0; i < vertCount() - 1; i++) {
            double x = getVertex(i).getX();
            double y = getVertex(i).getY();
            double nextX = getVertex(i + 1).getX();
            double nextY = getVertex(i + 1).getY();

            // See: http://en.wikipedia.org/wiki/Centroid#Centroid_of_polygon
            // to learn more about the formula.
            centroidX += (x + nextX) * (y * nextX - x * nextY);
            centroidY += (y + nextY) * (y * nextX - x * nextY);
        }

        centroidX /= (6 * area());
        centroidY /= (6 * area());
        return new Vector2D(centroidX, centroidY);
    }

    /**
     * Determines whether a given ordered list of vertices, in local coordinates, 
     * describes a valid convex polygon with clockwise winding.
     * @param vertices  An ordered list of <code>vector2D</code> objects defining 
     *                  the proposed convex polygon.
     * @return          <code>true</code> if the given list of vertices define a 
     *                  valid convex polygon with clockwise winding, and the
     *                  defined polygon surrounds the local origin; <code>false</code> 
     *                  otherwise.
     */
    public static boolean validateVertices(List<Vector2D> vertices) {
        // a valid convex polygon will always have at least 3 points
        if (vertices.size() < 3)
            return false;
        
        // are all vertices at origin?
        boolean allAtOrigin = true;
        for (Vector2D vertex : vertices)
            if (!vertex.equals(Vector2D.ORIGIN)) {
                allAtOrigin = false;
                break;
            }
        
        // if so, return false
        if (allAtOrigin)
            return false;
            
        // verify the angle of each set of neighboring edges
        int vertCount = vertices.size();
        for (int i = 0; i < vertices.size(); i++) {
            Vector2D a = vertices.get(i);
            Vector2D b = vertices.get((i + 1) % vertCount);
            Vector2D c = vertices.get((i + 2) % vertCount);
            
            Vector2D v1 = b.subtract(a);
            Vector2D v2 = c.subtract(b);
            
            double crossMag = (v1.getX() * v2.getY()) - (v1.getY() * v2.getX());
            if (crossMag > 0.0) {
                return false;
            }
            
            // if v1 and v2 are parallel, verify same direction
            if (v1.isParallel(v2) && !v1.unitVector().equals(v2.unitVector()))
                return false;
        }

        // verify that the polygon is not self-intersecting
        for (int i=0; i<vertices.size(); i++) {
            Vector2D a = vertices.get(i);
            Vector2D b = vertices.get((i + 1) % vertCount);
            Segment2D edge1 = new Segment2D(a, b);

            for (int j=i+1; j<vertices.size()-1; j++) {
                Vector2D c = vertices.get(j);
                Vector2D d = vertices.get((j + 1) % vertCount);
                Segment2D edge2 = new Segment2D(c, d);

                if (LinAlg2D.intersects(edge1, edge2)) {
                    Shape2D intersection = LinAlg2D.intersection(edge1, edge2);
                    if (intersection instanceof Segment2D)
                        return false;
                    if (!intersection.getPosition().equals(c))
                        return false;
                }
            }
        }
        
        // verify that the vertices suround the origin.
        return LinAlg2D.contains(vertices, Vector2D.ORIGIN);
    }   
}

