package org.rowan.linalgtoolkit.logic2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>LinAlg2D</code> class provides implementation of various linear algebra
 * based computations for two dimensional space. These computations include: the
 * distance between two 2D shapes, the area of intersection between two 2D shapes,
 * whether a moving 2D shape will intersect a stationary 2D shape in a given amount
 * of time, and whether a 2D shape wholly contains another 2D shape.
 * <p>
 *
 * Containment is defined as:
 * <ul><li>Every point on the child shape is inside the parent shape.</li>
 * <li>A point is inside the parent if the point is in the interior or lies on one of
 * the vertices or edges of the parent.</li></ul></p>
 * <p>
 *
 * Minimum distance is defined as:
 * <ul>
 * <li>The shortest distance between any point on the first shape to any point
 * on the second shape.</li>
 * <li>The points chosen for the minimum distance must be a vertex or point
 * that lies on an edge.</li>
 * <li>If one of the shapes contains the either, the distance is zero.</li>
 *
 * <li>If the components intersect, the distance is zero.</li>
 * </ul>
 *
 * @author Spence DiNicolantonio, Michael Liguori, Robert Russell
 * @version 1.1
 * @since 1.0
 */
public abstract class LinAlg2D {


    /*********************************************
     * MARK: Contains
     *********************************************/

    /**
     * Determines whether a given child shape, is wholly contained by a given
     * parent shape.
     * <p>
     * This method currently supports the following subclasses of <code>Shape2D</code>:
     * <code>Point2D</code>, <code>Segment2D</code>, <code>Circle2D</code>,
     * <code>Polygon2D</code>, and all subclasses of these.
     * @param parent    The parent <code>Shape2D</code>.
     * @param child     The child <code>Shape2D</code>.
     * @return          <code>true</code> if <code>child</code> is wholly contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     * @throws IllegalArgumentException If either given <code>Shape2D</code>
     *                  instance is not supported.
     */
    public static boolean contains(Shape2D parent, Shape2D child) {
        // point?
        if (parent instanceof Point2D) {

            // point-point
            if (child instanceof Point2D)
                return Contains2D.contains((Point2D)parent, (Point2D)child);

            // point-segment
            if (child instanceof Segment2D)
                return false; // false by definition

            // point-circle
            if (child instanceof Circle2D)
                return false; // false by definition

            // point-polygon
            if (child instanceof Polygon2D)
                return false; // false by definition

            // point-unknown
            throw new IllegalArgumentException("Unsupported child Shape2D instance");
        }

        // line segment?
        if (parent instanceof Segment2D) {

            // segment-point
            if (child instanceof Point2D)
                return Contains2D.contains((Segment2D)parent, (Point2D)child);

            // segment-segment
            if (child instanceof Segment2D)
                return Contains2D.contains((Segment2D)parent, (Segment2D)child);

            // segment-circle
            if (child instanceof Circle2D)
                return false; // false by definition

            // segment-polygon
            if (child instanceof Polygon2D)
                return false; // false by definition

            // segment-unknown
            throw new IllegalArgumentException("Unsupported child Shape2D instance");
        }

        // circle?
        if (parent instanceof Circle2D) {

            // circle-point
            if (child instanceof Point2D)
                return Contains2D.contains((Circle2D)parent, (Point2D)child);

            // circle-segment
            if (child instanceof Segment2D)
                return Contains2D.contains((Circle2D)parent, (Segment2D)child);

            // circle-circle
            if (child instanceof Circle2D)
                return Contains2D.contains((Circle2D)parent, (Circle2D)child);

            // circle-polygon
            if (child instanceof Polygon2D)
                return false; // false by definition

            // circle-unknown
            throw new IllegalArgumentException("Unsupported child Shape2D instance");
        }

        // polygon?
        if (parent instanceof Polygon2D) {

            // polygon-point
            if (child instanceof Point2D)
                return Contains2D.contains((Polygon2D)parent, (Point2D)child);

            // polygon-segment
            if (child instanceof Segment2D)
                return Contains2D.contains((Polygon2D)parent, (Segment2D)child);

            // polygon-circle
            if (child instanceof Circle2D)
                return Contains2D.contains((Polygon2D)parent, (Circle2D)child);

            // polygon-polygon
            if (child instanceof Polygon2D)
                return Contains2D.contains((Polygon2D)parent, (Polygon2D)child);

            // polygon-unknown
            throw new IllegalArgumentException("Unsupported child Shape2D instance");
        }

        // unknown
        throw new IllegalArgumentException("Unsupported parent Shape2D instance");
    }
    
    /**
     * Determines whether a given 2D vertex, in world coordinates, lies within
     * a given 2D shape.
     * <p>
     * This method currently supports the following subclasses of <code>Shape2D</code>:
     * <code>Point2D</code>, <code>Segment2D</code>, <code>Circle2D</code>,
     * <code>Polygon2D</code>, <code>ComplexShape2D</code>, and all subclasses 
     * of these.
     * @param shape     A <code>Shape2D</code>.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question.
     * @return          <code>true</code> if <code>vertex</code> is wholly contained
     *                  by <code>shape</code>; <code>false</code> otherwise.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> instance
     *                  is not supported.
     */
    public static boolean contains(Shape2D shape, Vector2D vertex) {
        // point?
        if (shape instanceof Point2D)
            return Contains2D.contains((Point2D)shape, vertex);
        
        // line segment?
        if (shape instanceof Segment2D)
            return Contains2D.contains((Segment2D)shape, vertex);
        
        // circle?
        if (shape instanceof Circle2D)
            return Contains2D.contains((Circle2D)shape, vertex);
        
        // polygon?
        if (shape instanceof Polygon2D)
            return Contains2D.contains((Polygon2D)shape, vertex);
        
        // complex?
        if (shape instanceof ComplexShape2D)
            return Contains2D.contains((ComplexShape2D)shape, vertex);
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape2D instance");
    }
    
    /**
     * Determines whether a given 2D vertex is contained by a given ordered list
     * of vertices that define a 2D convex polygon. This method assumes that the
     * given list of vertices describes a convex polygon with clockwise winding.
     * If this is not the case, the result of this method is unknown.
     * @param vertList  An ordered list of <code>Vector2D</code> objects defining
     *                  A convex polygon with clockwise winding.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question.
     * @return          <code>true</code> if <code>vertex</code> is contained in
     *                  the polygon described by <code>vertList</code>;
     *                  <code>false</code> otherwise.
     */
    public static boolean contains(List<Vector2D> vertList, Vector2D vertex) {
        // for each vertex in the given list...
        for (int i=0; i<vertList.size(); i++) {
            Vector2D current = vertList.get(i);
            Vector2D next = vertList.get((i+1) % vertList.size());
            
            // store component values for easy access
            double x = vertex.getX();
            double y = vertex.getY();
            double x0 = current.getX();
            double y0 = current.getY();
            double x1 = next.getX();
            double y1 = next.getY();
            
            // calculate determinate value
            double determinate = (y - y0)*(x1 - x0) - (x - x0)*(y1 - y0);
            
            // if the determinate is greater than 0, the given vertex is to the
            // right of the edge between the current and next vertices in vertList
            if (determinate > 0)
                return false;
        }
        
        // at this point we know that the given vertex is to the on or to
        // the right of all edges found while traversing vertList
        return true;
    }


    /*********************************************
     * MARK: Distance
     *********************************************/

    /**
     * Computes the distance between two given 2D shapes.
     * <p>
     * This method currently supports the following subclasses of <code>Shape2D</code>:
     * <code>Point2D</code>, <code>Segment2D</code>, <code>Circle2D</code>,
     * <code>Polygon2D</code>, <code>ComplexShape2D</code>, and all subclasses 
     * of these.
     * @param shape1    A <code>Shape2D</code>.
     * @param shape2    A <code>Shape2D</code>.
     * @return          The distance between <code>shape1</code> and <code>shape2</code>.
     * @throws IllegalArgumentException If either given <code>Shape2D</code>
     *                  instance is not supported.
     */
    public static double distance(Shape2D shape1, Shape2D shape2) {
        // point?
        if (shape1 instanceof Point2D) {

            // point-point
            if (shape2 instanceof Point2D)
                return Distance2D.distance((Point2D)shape1, (Point2D)shape2);

            // point-segment
            if (shape2 instanceof Segment2D)
                return Distance2D.distance((Point2D)shape1, (Segment2D)shape2);

            // point-circle
            if (shape2 instanceof Circle2D)
                return Distance2D.distance((Point2D)shape1, (Circle2D)shape2);

            // point-polygon
            if (shape2 instanceof Polygon2D)
                return Distance2D.distance((Point2D)shape1, (Polygon2D)shape2);
            
            // point-complex
            if (shape2 instanceof ComplexShape2D)
                return Distance2D.distance((ComplexShape2D)shape2, (Point2D)shape1);

            // point-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // line segment?
        if (shape1 instanceof Segment2D) {

            // segment-point
            if (shape2 instanceof Point2D)
                return Distance2D.distance((Point2D)shape2, (Segment2D)shape1);

            // segment-segment
            if (shape2 instanceof Segment2D)
                return Distance2D.distance((Segment2D)shape1, (Segment2D)shape2);

            // segment-circle
            if (shape2 instanceof Circle2D)
                return Distance2D.distance((Segment2D)shape1, (Circle2D)shape2);

            // segment-polygon
            if (shape2 instanceof Polygon2D)
                return Distance2D.distance((Segment2D)shape1, (Polygon2D)shape2);
            
            // segment-complex
            if (shape2 instanceof ComplexShape2D)
                return Distance2D.distance((ComplexShape2D)shape2, (Segment2D)shape1);

            // segment-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // circle?
        if (shape1 instanceof Circle2D) {

            // circle-point
            if (shape2 instanceof Point2D)
                return Distance2D.distance((Point2D)shape2, (Circle2D)shape1);

            // circle-segment
            if (shape2 instanceof Segment2D)
                return Distance2D.distance((Segment2D)shape2, (Circle2D)shape1);

            // circle-circle
            if (shape2 instanceof Circle2D)
                return Distance2D.distance((Circle2D)shape1, (Circle2D)shape2);

            // circle-polygon
            if (shape2 instanceof Polygon2D)
                return Distance2D.distance((Circle2D)shape1, (Polygon2D)shape2);
            
            // circle-complex
            if (shape2 instanceof ComplexShape2D)
                return Distance2D.distance((ComplexShape2D)shape2, (Circle2D)shape1);

            // circle-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // polygon?
        if (shape1 instanceof Polygon2D) {

            // polygon-point
            if (shape2 instanceof Point2D)
                return Distance2D.distance((Point2D)shape2, (Polygon2D)shape1);

            // polygon-segment
            if (shape2 instanceof Segment2D)
                return Distance2D.distance((Segment2D)shape2, (Polygon2D)shape1);

            // polygon-circle
            if (shape2 instanceof Circle2D)
                return Distance2D.distance((Circle2D)shape2, (Polygon2D)shape1);

            // polygon-polygon
            if (shape2 instanceof Polygon2D)
                return Distance2D.distance((Polygon2D)shape1, (Polygon2D)shape2);
            
            // polygon-complex
            if (shape2 instanceof ComplexShape2D)
                return Distance2D.distance((ComplexShape2D)shape2, (Polygon2D)shape1);

            // polygon-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }
        
        // complex?
        if (shape1 instanceof ComplexShape2D) {
            if (shape2 instanceof Point2D || 
                shape2 instanceof Segment2D ||
                shape2 instanceof Circle2D ||
                shape2 instanceof Polygon2D ||
                shape2 instanceof ComplexShape2D)
                return Distance2D.distance((ComplexShape2D)shape1, shape2);
            else
                throw new IllegalArgumentException("Unsupported Shape2D instance");
        }
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape2D instance");
    }
    
    /**
     * Computes the distance between a given 2D shape and a 2D vertex.
     * <p>
     * This method currently supports the following subclasses of <code>Shape2D</code>:
     * <code>Point2D</code>, <code>Segment2D</code>, <code>Circle2D</code>,
     * <code>Polygon2D</code>, <code>ComplexShape2D</code>, and all subclasses 
     * of these.
     * @param shape     A <code>Shape2D</code>.
     * @param vertex    A <code>Vector2D</code> describing the vertex.
     * @return          The distance between <code>shape</code> and <code>vertex</code>.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> instance
     *                  is not supported.
     */
    public static double distance(Shape2D shape, Vector2D vertex) {
        // point?
        if (shape instanceof Point2D)
            return Distance2D.distance((Point2D)shape, vertex);
        
        // line segment?
        if (shape instanceof Segment2D)
            return Distance2D.distance((Segment2D)shape, vertex);
        
        // circle?
        if (shape instanceof Circle2D)
            return Distance2D.distance((Circle2D)shape, vertex);
        
        // polygon?
        if (shape instanceof Polygon2D)
            return Distance2D.distance((Polygon2D)shape, vertex);
        
        // complex?
        if (shape instanceof ComplexShape2D)
            return Distance2D.distance((ComplexShape2D)shape, vertex);
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape2D instance");
    }
    
    /**
     * Computes the distance between one given 2D vertex and the closest vertex
     * found in a given list of 2D vertices.
     * @param vertex    The vertex to which the shortest distance will be found.
     * @param vertList  A list of vertices, from which the shortest distance will
     *                  be found.
     * @return          The distance between <code>vertex</code> and the closest
     *                  vertex found in <code>vertList</code>.
     * @throws IllegalArgumentException If the given vertex list is empty.
     */
    public static double distance(Vector2D vertex, List<Vector2D> vertList) {
        // empty vertex list?
        if (vertList.isEmpty())
            throw new IllegalArgumentException("Invalid vertex list: Given vertex " +
                                               "list must contain at least one vertex");
        
        // find closest vertex
        Vector2D closest = closest(vertex, vertList);
        
        // calculate distance
        return vertex.distance(closest);
    }
    
    /**
     * Computes the distance between the two closest vertices found in two given
     * lists of 2D vertices.
     * @param list1 A list of vertices.
     * @param list2 A list of vertices.
     * @return      The distance between the two closest vertices found in
     *              <code>list1</code> and <code>list2</code>.
     * @throws IllegalArgumentException If either given vertex list is empty.
     */
    public static double distance(List<Vector2D> list1, List<Vector2D> list2) {
        // empty vertex list?
        if (list1.isEmpty() || list2.isEmpty())
            throw new IllegalArgumentException("Invalid vertex list: Given vertex " +
                                               "list must contain at least one vertex");
        
        // find closest vertices
        Vector2D[] closest = closest(list1, list2);
        
        // calculate distance
        return closest[0].distance(closest[1]);
    }
    
    /**
     * Computes the distance from a given 2D vertex to the closest point between
     * two other given 2D vertices.
     * @param vertex    A <code>Vector2D</code> describing the vertex from which
     *                  the distance will be calculated.
     * @param between1  One of the vertices stipulating the location of the point
     *                  used for distance calculation from <code>vertex</code>.
     * @param between2  One of the vertices stipulating the location of the point
     *                  used for distance calculation from <code>vertex</code>.
     * @return          The distance from <code>vertex</code> to the point between
     *                  <code>between1</code> and <code>between2</code> that is
     *                  closest to <code>vertex</code>.
     */
    public static double distanceBetween(Vector2D vertex, Vector2D between1, Vector2D between2) {
        // find closest point between two given vertices
        Vector2D closest = closestBetween(vertex, between1, between2);
        
        // compute distance from given vertex
        return closest.distance(vertex);
    }
    
    /**
     * Computes the distance from a given 2D vertex to the closest point between
     * two two consecutive vertices in a given list of 2D vertices.
     * @param vertex    A <code>Vector2D</code> describing the vertex from which
     *                  the distance will be calculated.
     * @param vertList  A list of vertices, stipulating the possibilities for
     *                  the closest vertex.
     * @return          The distance from <code>vertex</code> to the closest vertex
     *                  between two consecutive vertices in <code>vertList</code>.
     * @throws IllegalArgumentException If the given vertex list is empty.
     */
    public static double distanceBetween(Vector2D vertex, List<Vector2D> vertList) {
        // empty vertex list?
        if (vertList.isEmpty())
            throw new IllegalArgumentException("Invalid vertex list: Given vertex " +
                                               "list must contain at least one vertex");
        
        // find closest vertex
        Vector2D closest = closestBetween(vertex, vertList);
        
        // compute distance from given vertex
        return closest.distance(vertex);
    }
    
    /**
     * Computes the shortest distance between any two vertices found between two
     * consecutive vertices of two given lists of 2D vertices, respectively.
     * @param list1 A list of vertices.
     * @param list2 A list of vertices.
     * @return      The shortest distance between the two closest points found
     *              between two consecutive vertices in <code>list1</code> and
     *              <code>list2</code>.
     * @throws IllegalArgumentException If either given vertex list is empty.
     */
    public static double distanceBetween(List<Vector2D> list1, List<Vector2D> list2) {
        // empty vertex list?
        if (list1.isEmpty() || list2.isEmpty())
            throw new IllegalArgumentException("Invalid vertex list: Given vertex " +
                                               "list must contain at least one vertex");
        
        // find closest vertices
        Vector2D[] closest = closestBetween(list1, list2);
        
        // compute distance from given vertex
        return closest[0].distance(closest[1]);
    }


    /*********************************************
     * MARK: Intersects
     *********************************************/

    /**
     * Determines whether two given 2D shapes intersect.
     * <p>
     * This method currently supports the following subclasses of <code>Shape2D</code>:
     * <code>Point2D</code>, <code>Segment2D</code>, <code>Circle2D</code>,
     * <code>Polygon2D</code>, <code>ComplexShape2D</code>, and all subclasses 
     * of these.
     * @param shape1    A <code>Shape2D</code>.
     * @param shape2    A <code>Shape2D</code>.
     * @return          <code>true</code> if <code>shape1</code> intersects
     *                  <code>shape2</code>; <code>false</code> otherwise.
     * @throws IllegalArgumentException If either given <code>Shape2D</code>
     *                  instance is not supported.
     */
    public static boolean intersects(Shape2D shape1, Shape2D shape2) {
        // point?
        if (shape1 instanceof Point2D) {
            if (shape2 instanceof Point2D || 
                shape2 instanceof Segment2D ||
                shape2 instanceof Circle2D ||
                shape2 instanceof Polygon2D ||
                shape2 instanceof ComplexShape2D)
                return Intersects2D.intersects((Point2D)shape1, shape2);
            else
                throw new IllegalArgumentException("Unsupported Shape2D instance");
        }
        
        // line segment?
        if (shape1 instanceof Segment2D) {

            // segment-point
            if (shape2 instanceof Point2D)
                return Intersects2D.intersects((Point2D)shape2, (Segment2D)shape1);

            // segment-segment
            if (shape2 instanceof Segment2D)
                return Intersects2D.intersects((Segment2D)shape1, (Segment2D)shape2);

            // segment-circle
            if (shape2 instanceof Circle2D)
                return Intersects2D.intersects((Segment2D)shape1, (Circle2D)shape2);

            // segment-polygon
            if (shape2 instanceof Polygon2D)
                return Intersects2D.intersects((Segment2D)shape1, (Polygon2D)shape2);
            
            // segment-complex
            if (shape2 instanceof ComplexShape2D)
                return Intersects2D.intersects((ComplexShape2D)shape2, (Segment2D)shape1);

            // segment-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // circle?
        if (shape1 instanceof Circle2D) {

            // circle-point
            if (shape2 instanceof Point2D)
                return Intersects2D.intersects((Point2D)shape2, (Circle2D)shape1);

            // circle-segment
            if (shape2 instanceof Segment2D)
                return Intersects2D.intersects((Segment2D)shape2, (Circle2D)shape1);

            // circle-circle
            if (shape2 instanceof Circle2D)
                return Intersects2D.intersects((Circle2D)shape1, (Circle2D)shape2);

            // circle-polygon
            if (shape2 instanceof Polygon2D)
                return Intersects2D.intersects((Circle2D)shape1, (Polygon2D)shape2);
            
            // circle-complex
            if (shape2 instanceof ComplexShape2D)
                return Intersects2D.intersects((ComplexShape2D)shape2, (Circle2D)shape1);

            // circle-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // polygon?
        if (shape1 instanceof Polygon2D) {

            // polygon-point
            if (shape2 instanceof Point2D)
                return Intersects2D.intersects((Point2D)shape2, (Polygon2D)shape1);

            // polygon-segment
            if (shape2 instanceof Segment2D)
                return Intersects2D.intersects((Segment2D)shape2, (Polygon2D)shape1);

            // polygon-circle
            if (shape2 instanceof Circle2D)
                return Intersects2D.intersects((Circle2D)shape2, (Polygon2D)shape1);

            // polygon-polygon
            if (shape2 instanceof Polygon2D)
                return Intersects2D.intersects((Polygon2D)shape1, (Polygon2D)shape2);
            
            // polygon-complex
            if (shape2 instanceof ComplexShape2D)
                return Intersects2D.intersects((ComplexShape2D)shape2, (Polygon2D)shape1);

            // polygon-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }
        
        // complex?
        if (shape1 instanceof ComplexShape2D) {
            if (shape2 instanceof Point2D || 
                shape2 instanceof Segment2D ||
                shape2 instanceof Circle2D ||
                shape2 instanceof Polygon2D ||
                shape2 instanceof ComplexShape2D)
                return Intersects2D.intersects((ComplexShape2D)shape1, shape2);
            else
                throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // unknown
        throw new IllegalArgumentException("Unsupported Shape2D instance");
    }


    /*********************************************
     * MARK: Intersection
     *********************************************/

    /**
     * Calculates the 2D shape created by the intersection of two given 2D shapes.
     * <p>
     * This method currently supports the following subclasses of <code>Shape2D</code>:
     * <code>Point2D</code>, <code>Segment2D</code>, <code>Polygon2D</code>, and
     * all subclasses of these.
     * @param  shape1   A <code>Shape2D</code>.
     * @param  shape2   A <code>Shape2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>shape1</code> and <code>shape2</code>.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> instance
     *                  is not supported.
     * @throws IntersectException if <code>shape1</code> and <code>shape2</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Shape2D shape1, Shape2D shape2) {

        // point?
        if (shape1 instanceof Point2D) {

            // point-point
            if (shape2 instanceof Point2D)
                return Intersection2D.intersection((Point2D)shape1, (Point2D)shape2);

            // point-segment
            if (shape2 instanceof Segment2D)
                return Intersection2D.intersection((Point2D)shape1, (Segment2D)shape2);

            // point-circle
            if (shape2 instanceof Circle2D)
                return Intersection2D.intersection((Point2D)shape1, (Circle2D)shape2);

            // point-polygon
            if (shape2 instanceof Polygon2D)
                return Intersection2D.intersection((Point2D)shape1, (Polygon2D)shape2);
            
            // point-complex
            if (shape2 instanceof ComplexShape2D)
                return Intersection2D.intersection((ComplexShape2D)shape2, shape1);

            // point-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // line segment?
        if (shape1 instanceof Segment2D) {

            // segment-point
            if (shape2 instanceof Point2D)
                return Intersection2D.intersection((Point2D)shape2, (Segment2D)shape1);

            // segment-segment
            if (shape2 instanceof Segment2D)
                return Intersection2D.intersection((Segment2D)shape1, (Segment2D)shape2);

            // segment-circle
            if (shape2 instanceof Circle2D)
                return Intersection2D.intersection((Segment2D)shape1, (Circle2D)shape2);

            // segment-polygon
            if (shape2 instanceof Polygon2D)
                return Intersection2D.intersection((Segment2D)shape1, (Polygon2D)shape2);
            
            // segment-complex
            if (shape2 instanceof ComplexShape2D)
                return Intersection2D.intersection((ComplexShape2D)shape2, shape1);

            // segment-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // circle?
        if (shape1 instanceof Circle2D) {

            // circle-point
            if (shape2 instanceof Point2D)
                return Intersection2D.intersection((Point2D)shape2, (Circle2D)shape1);

            // circle-segment
            if (shape2 instanceof Segment2D)
                return Intersection2D.intersection((Segment2D)shape2, (Circle2D)shape1);

            // circle-circle
            if (shape2 instanceof Circle2D)
                return Intersection2D.intersection((Circle2D)shape1, (Circle2D)shape2);

            // circle-polygon
            if (shape2 instanceof Polygon2D)
                return Intersection2D.intersection((Circle2D)shape1, (Polygon2D)shape2);
            
            // circle-complex
            if (shape2 instanceof ComplexShape2D)
                return Intersection2D.intersection((ComplexShape2D)shape2, shape1);

            // circle-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }

        // polygon?
        if (shape1 instanceof Polygon2D) {

            // polygon-point
            if (shape2 instanceof Point2D)
                return Intersection2D.intersection((Point2D)shape2, (Polygon2D)shape1);

            // polygon-segment
            if (shape2 instanceof Segment2D)
                return Intersection2D.intersection((Segment2D)shape2, (Polygon2D)shape1);

            // polygon-circle
            if (shape2 instanceof Circle2D)
                return Intersection2D.intersection((Circle2D)shape2, (Polygon2D)shape1);

            // polygon-polygon
            if (shape2 instanceof Polygon2D)
                return Intersection2D.intersection((Polygon2D)shape1, (Polygon2D)shape2);
            
            // polygon-complex
            if (shape2 instanceof ComplexShape2D)
                return Intersection2D.intersection((ComplexShape2D)shape2, shape1);

            // polygon-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }
        
        // complex?
        if (shape1 instanceof ComplexShape2D)
            return Intersection2D.intersection((ComplexShape2D)shape1, shape2);

        // unknown
        throw new IllegalArgumentException("Unsupported Shape2D instance.");
    }
    
    
    /*********************************************
     * MARK: Intersection
     *********************************************/
    
    /**
     * Calculates the area of intersection between two given 2D shapes.
     * <p>
     * This method does not currently support complex shapes that contain circles.
     * @param  shape1   A <code>Shape2D</code>.
     * @param  shape2   A <code>Shape2D</code>.
     * @return          The area of intersection between <code>shape1</code> and
     *                  <code>shape2</code>.
     * @throws IllegalArgumentException If the given <code>Shape2D</code> instance
     *                  is not supported.
     */
    public static double intersectionArea(Shape2D shape1, Shape2D shape2) {
        
        // point?
        if (shape1 instanceof Point2D)
            return IntersectionArea2D.intersectionArea((Point2D)shape1, shape2);
        
        // line segment?
        if (shape1 instanceof Segment2D)
            return IntersectionArea2D.intersectionArea((Segment2D)shape1, shape2);
        
        // circle?
        if (shape1 instanceof Circle2D) {
            
            // circle-point
            if (shape2 instanceof Point2D)
                return IntersectionArea2D.intersectionArea((Point2D)shape2, (Circle2D)shape1);
            
            // circle-segment
            if (shape2 instanceof Segment2D)
                return IntersectionArea2D.intersectionArea((Segment2D)shape2, (Circle2D)shape1);
            
            // circle-circle
            if (shape2 instanceof Circle2D)
                return IntersectionArea2D.intersectionArea((Circle2D)shape1, (Circle2D)shape2);
            
            // circle-polygon
            if (shape2 instanceof Polygon2D)
                return IntersectionArea2D.intersectionArea((Circle2D)shape1, (Polygon2D)shape2);
            
            // circle-complex
            if (shape2 instanceof ComplexShape2D)
                return IntersectionArea2D.intersectionArea((ComplexShape2D)shape2, shape1);
            
            // circle-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }
        
        // polygon?
        if (shape1 instanceof Polygon2D) {
            
            // polygon-point
            if (shape2 instanceof Point2D)
                return IntersectionArea2D.intersectionArea((Point2D)shape2, (Polygon2D)shape1);
            
            // polygon-segment
            if (shape2 instanceof Segment2D)
                return IntersectionArea2D.intersectionArea((Segment2D)shape2, (Polygon2D)shape1);
            
            // polygon-circle
            if (shape2 instanceof Circle2D)
                return IntersectionArea2D.intersectionArea((Circle2D)shape2, (Polygon2D)shape1);
            
            // polygon-polygon
            if (shape2 instanceof Polygon2D)
                return IntersectionArea2D.intersectionArea((Polygon2D)shape1, (Polygon2D)shape2);
            
            // polygon-complex
            if (shape2 instanceof ComplexShape2D)
                return IntersectionArea2D.intersectionArea((ComplexShape2D)shape2, shape1);
            
            // polygon-unknown
            throw new IllegalArgumentException("Unsupported Shape2D instance");
        }
        
        // complex?
        if (shape1 instanceof ComplexShape2D)
            return IntersectionArea2D.intersectionArea((ComplexShape2D)shape1, shape2);
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape2D instance.");
    }
    
    
    /*********************************************
     * MARK: Union
     *********************************************/
    
    /**
     * Calculates the union of two given 2D shapes as a <code>ComplexShape2D</code> object.
     * This method creates a complex shape using the two given shapes as sub-shapes.
     * @param shape1    A <code>Shape2D</code>.
     * @param shape2    A <code>Shape2D</code>.
     * @return          A <code>ComplexShape2D</code> defining the union of
     *                  <code>shape1</code> and <code>shape2</code>.
     */
    public static ComplexShape2D union(Shape2D shape1, Shape2D shape2) {
        // create a new complex shape using the given shapes
        LinkedList<Shape2D> subShapes = new LinkedList<Shape2D>();
        subShapes.add(shape1);
        subShapes.add(shape2);
        return new ComplexShape2D(subShapes);
    }
    
    
    /*********************************************
     * MARK: Closest
     *********************************************/
    
    /**
     * Determines the closest vertex, from a given list of 2D vertices, to another
     * given 2D vertex.
     * @param vertex    The vertex to which the closest vertex will be found.
     * @param vertList  A list of vertices, from which the closest will be found.
     * @return          The vertex found in <code>vertList</code> that is closest
     *                  to <code>vertex</code>.
     * @throws IllegalArgumentException If the given vertex list is empty.
     */
    public static Vector2D closest(Vector2D vertex, List<Vector2D> vertList) {
        // empty vertex list?
        if (vertList.isEmpty())
            throw new IllegalArgumentException("Invalid vertex list: Given vertex " +
                                               "list must contain at least one vertex");
        
        // only one vertex in list?
        if (vertList.size() == 1)
            return vertList.get(0);
        
        // to start get distance from first vertex in list
        Vector2D closest = vertList.get(0);
        double dist = vertex.distance(closest);
        
        // find the closest vertex
        for (int i=1; i<vertList.size(); i++) {
            // get current vertex and distance
            Vector2D currVert = vertList.get(i);
            double currDist = vertex.distance(currVert);
            
            // closest so far?
            if (currDist < dist) {
                dist = currDist;
                closest = currVert;
            }
        }
        
        // return closest vertex
        return closest;
    }
    
    /**
     * Determines the two closest vertices, from two given list of 2D vertices.
     * @param list1 A list of vertices.
     * @param list2 A list of vertices.
     * @return      An array containing the two closest vertices in <code>list1</code>
     *              and <code>list2</code>. The vertex taken from <code>list1</code>
     *              will be placed at index 0 in the returned array and the vertex
     *              taken from <code>list2</code> will be placed at index 1 in
     *              the returned array.
     * @throws IllegalArgumentException If either given vertex list is empty.
     */
    public static Vector2D[] closest(List<Vector2D> list1, List<Vector2D> list2) {
        // empty vertex list?
        if (list1.isEmpty() || list2.isEmpty())
            throw new IllegalArgumentException("Invalid vertex list: Given vertex " +
                                               "list must contain at least one vertex");
        
        // start with closest vertex to first vertex in list1
        Vector2D[] closest = {
            list1.get(0),
            closest(list1.get(0), list2)
        };
        double dist = closest[0].distance(closest[1]);
        
        // find the closest vertex pair
        for (int i=1; i<list1.size(); i++) {
            Vector2D currVert = list1.get(i);
            
            // get closest vertex to current
            Vector2D currClosest = closest(currVert, list2);
            double currDist = currVert.distance(currClosest);
            
            // closest vertex pair so far?
            if (currDist < dist) {
                dist = currDist;
                closest[0] = currVert;
                closest[1] = currClosest;
            }
        }
        
        // return closest vertex pair
        return closest;
    }
    
    /**
     * Determines the closest vertex, between two given 2D vertices, to a given
     * 2D vertex.
     * @param vertex    A <code>Vector2D</code> describing the vertex from which
     *                  the closest vertex will be found.
     * @param between1  One of two vertices stipulating the possible location of
     *                  the found vertex.
     * @param between2  One of two vertices stipulating the possible location of
     *                  the found vertex.
     * @return          A <code>Vector2D</code> describing the vertex between
     *                  <code>between1</code> and <code>between2</code> that is
     *                  closest to <code>vertex</code>.
     */
    public static Vector2D closestBetween(Vector2D vertex, Vector2D between1, Vector2D between2) {
        if (between1.equals(between2))
            return between1;
        
        // algorithm taken from:
        // http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/
        
        double v1x = vertex.getX();
        double v1y = vertex.getY();
        double v2x = between1.getX();
        double v2y = between1.getY();
        double v3x = between2.getX();
        double v3y = between2.getY();
        double dx = (v3x - v2x);
        double dy = (v3y - v2y);
        
        double u = ((v1x - v2x)*dx + (v1y - v2y)*dy) / (dx*dx + dy*dy);
        
        if (u < 0)
            return between1;
        else if (u > 1)
            return between2;
        
        return new Vector2D(v2x + u*dx,
                            v2y + u*dy);
    }
    
    /**
     * Determines the closest vertex to given 2D vertex, that is between two
     * consecutive vertices found in a given list of 2D vertices.
     * <p>
     * Note that there could be more than one point with equal, and shortest,
     * distances from the given vertex. This method only finds one of them.
     * @param vertex    The vertex to which the closest vertex will be found.
     * @param vertList  A list of vertices, stipulating the possibilities for
     *                  the closest vertex.
     * @return          The vertex between two consecutive vertices in
     *                  <code>vertList</code> that is closest to <code>vertex</code>.
     * @throws IllegalArgumentException If the given vertex list is empty.
     */
    public static Vector2D closestBetween(Vector2D vertex, List<Vector2D> vertList) {
        // empty vertex list?
        if (vertList.isEmpty())
            throw new IllegalArgumentException("Invalid vertex list: Given vertex " +
                                               "list must contain at least one vertex");
        // only one vertex in list?
        if (vertList.size() == 1)
            return vertList.get(0);
        
        // current closest
        Vector2D closest = null;
        double dist = Double.MAX_VALUE;
        
        // for every set of consecutive vertices in vertList...
        for (int i=0; i<vertList.size(); i++) {
            Vector2D current = vertList.get(i);
            Vector2D next = vertList.get((i+1) % vertList.size());
            
            // find closest point between current vertices
            Vector2D tempClosest = closestBetween(vertex, current, next);
            double tempDist = tempClosest.distance(vertex);
            
            // closest yet?
            if (closest == null || tempDist < dist) {
                closest = tempClosest;
                dist = tempDist;
            }
        }
        
        // return closest found
        return closest;
    }
    
    /**
     * Determines the closest vertices that are between two consecutive vertices
     * found in two given lists of 2D vertices.
     * <p>
     * Note that there could be more than one set of points with equal, and shortest,
     * distances from each other. This method only finds one of them.
     * @param list1 A list of vertices.
     * @param list2 A list of vertices.
     * @return      An array containing the two closest vertices found between
     *              two consecutive vertices in <code>list1</code> and <code>list2</code>
     *              respectively. The vertex found between vertices in <code>list1</code>
     *              will be placed at index 0 in the returned array and the vertex
     *              found between vertices in <code>list2</code> will be placed
     *              at index 1 in the returned array.
     * @throws IllegalArgumentException If either given vertex list is empty.
     */
    public static Vector2D[] closestBetween(List<Vector2D> list1, List<Vector2D> list2) {
        // empty vertex list?
        if (list1.isEmpty() || list2.isEmpty())
            throw new IllegalArgumentException("Invalid vertex list: Given vertex " +
                                               "list must contain at least one vertex");
        
        // only one vertex in either list?
        if (list1.size() == 1) {
            Vector2D[] closest = {
                list1.get(0),
                closestBetween(list1.get(0), list2)
            };
            return closest;
        }
        if (list2.size() == 1) {
            Vector2D[] closest = {
                closestBetween(list2.get(0), list1),
                list2.get(0)
            };
            return closest;
        }
        
        
        // current closest
        Vector2D[] closest = new Vector2D[2];
        double dist = Double.MAX_VALUE;
        
        // for every vertex in list 1...
        for (int i=0; i<list1.size(); i++) {
            Vector2D vertex = list1.get(i);
            
            // for every set of consecutive vertices in list2...
            for (int j=0; j<list2.size(); j++) {
                Vector2D current = list2.get(j);
                Vector2D next = list2.get((j+1) % list2.size());
                
                // find the closest point between vertex from list 1
                // and consecutive vertices in list2
                Vector2D tempClosest = closestBetween(vertex, current, next);
                double tempDist = tempClosest.distance(vertex);
                
                // closest so far?
                if (tempDist < dist) {
                    closest[0] = vertex;
                    closest[1] = tempClosest;
                    dist = tempDist;
                }
            }
        }
        
        // for every vertex in list 2...
        for (int i=0; i<list2.size(); i++) {
            Vector2D vertex = list2.get(i);
            
            // for every set of consecutive vertices in list1...
            for (int j=0; j<list1.size(); j++) {
                Vector2D current = list1.get(j);
                Vector2D next = list1.get((j+1) % list1.size());
                
                // find the closest point between vertex from list 1
                // and consecutive vertices in list2
                Vector2D tempClosest = closestBetween(vertex, current, next);
                double tempDist = tempClosest.distance(vertex);
                
                // closest so far?
                if (tempDist < dist) {
                    closest[0] = tempClosest;
                    closest[1] = vertex;
                    dist = tempDist;
                }
            }
        }
        
        // return closest found
        return closest;
    }


    /*********************************************
     * MARK: Intersects Given Time
     *********************************************/  
    
    /**
     * Determines whether the moving <code>Shape2D</code> will intersect the boundary
     * <code>Shape2D</code> in a given amount of time.
     * @param  movingObject an instance of <code>Shape2D</code> which has a non-zero trajectory.
     * @param  boundaryObject  an instance of <code>Shape2D</code> which represents the potential
     *         point of intersection. This boundary is stationary.
     * @param  time the the maximum amount of time given for the <code>Shape2D</code>
     *         instances to intersect.
     * @return <code>true</code> if the <code>movingObject</code> will intersect the
     *         <code>boundaryObject</code> in a given amount of time; <code>false</code>
     *         otherwise.
     * @throws IllegalArgumentException the boundary cannot be moving.
     */
        public static boolean intersects(Shape2D movingObject, Shape2D boundaryObject, double time) {

        //boundary object must be stationary
        if (!boundaryObject.getVelocity().isZeroVector()) {
            throw new IllegalArgumentException("Boundary Cannot be moving");
        }
        //moving object cannot be still.
        if (movingObject.getVelocity().isZeroVector() && time != 0) {
            throw new IllegalArgumentException("Must have a Vector");
        }

        //Check to see if they are currectly Intersecting
        if (movingObject.intersects(boundaryObject)) {
            return true;
        }

        //List of shapes to test for complex shape
        LinkedList<Shape2D> movingShapes = new LinkedList<Shape2D>();

        //All sub shapes of complex shape or just the single moving object
        if (movingObject instanceof ComplexShape2D) {
            ComplexShape2D complex = (ComplexShape2D) movingObject;
            movingShapes.addAll(complex.getSubShapes());
        } else {
            movingShapes.add(movingObject);
        }

        boolean intersect = false;

        //store the velocity to set it back after testing
        Vector2D relVelocity = movingObject.getVelocity();

        //Loop through all the moving shapes
        for (Shape2D movSubShape : movingShapes) {
            //if subshape had a velocity, store it to set back later
            Vector2D origVelocity = movSubShape.getVelocity();
            //subshape is moving at same velocity as entire shape
            movSubShape.setVelocity(relVelocity);

            //point, circle, segment, polygon
            if (movSubShape instanceof Point2D) {
                //Method which treats the moving point as a path
                intersect = IntersectsTime2D.intersectsTime((Point2D) movSubShape, boundaryObject, 0, time);
            } else if (boundaryObject instanceof Circle2D) {
                //Special Case.
                //Higher accuracy to treat circle as a moving point with a "padding"
                Circle2D origCircle = (Circle2D) boundaryObject;
                Point2D circleCenter = new Point2D(origCircle.getPosition());
                //velocity in relation to boundary object is inversed
                circleCenter.setVelocity(relVelocity.inverse());
                movSubShape.setVelocity(Vector2D.ZERO_VECTOR);

                //treats the boundary object as the moving object with relative velocity : inverse velocity
                intersect = IntersectsTime2D.intersectsTime(circleCenter, movSubShape, origCircle.getRadius(), time);
            } else if (movSubShape instanceof Circle2D) {
                //Treat circle as a moving point with a padding
                Circle2D c = (Circle2D) movSubShape;
                Point2D p = new Point2D(c.getPosition());
                p.setVelocity(c.getVelocity());

                intersect = IntersectsTime2D.intersectsTime(p, boundaryObject, c.getRadius(), time);
            } else if (movSubShape instanceof Segment2D) {
                //Seperating Axis Theorem
                intersect = IntersectsTime2D.intersectsTime(movSubShape, boundaryObject, time);
            } else if (movSubShape instanceof Polygon2D) {
                //Seperating Axis Theorem
                intersect = IntersectsTime2D.intersectsTime(movSubShape, boundaryObject, time);
            } else {
                throw new IllegalArgumentException("Unsupported Shape2D instance.");
            }

            //reset the sub shapes velocity
            movSubShape.setVelocity(origVelocity);

            if (!intersect) {
                continue;
            }
        }


        return intersect;
    }
    
    
    /*********************************************
     * MARK: Other
     *********************************************/
    
    /**
     * Determines whether a given 2D vertex lies between two other 2D vertices.
     * @param vert1 A <code>Vector2D</code> describing the vertex in question.
     * @param vert2 A <code>Vector2D</code> describing one of the vertex limits.
     * @param vert3 A <code>Vector2D</code> describing one of the vertex limits.
     * @return      <code>true</code> if <code>vert1</code> lies directly between
     *              <code>vert2</code> and <code>vert3</code>.
     */
    public static boolean isBetween(Vector2D vert1, Vector2D vert2, Vector2D vert3) {
        // vertex on one of endpoints?
        if (vert1.equals(vert2) || vert1.equals(vert3))
            return true;
        
        // store component values for easy access
        double v1x = vert1.getX();
        double v1y = vert1.getY();
        double v2x = vert2.getX();
        double v2y = vert2.getY();
        double v3x = vert3.getX();
        double v3y = vert3.getY();
        
        // vertex out of x bounds?
        if ((v1x > v2x && v1x > v3x) || (v1x < v2x && v1x < v3x))
            return false;
        
        // vertex out of y bounds?
        if ((v1y > v2y && v1y > v3y) || (v1y < v2y && v1y < v3y))
            return false;
        
        // calculate vectors from vert1 to vert3 and vert2 to vert3
        Vector2D v1 = vert3.subtract(vert1);
        Vector2D v2 = vert3.subtract(vert2);
        
        // if the vectors are parallel, then vert1 is between vert2 and vert3
        return (v1.isParallel(v2));
    }

}

