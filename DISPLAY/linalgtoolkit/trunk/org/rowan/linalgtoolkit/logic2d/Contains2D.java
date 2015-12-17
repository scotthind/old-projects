package org.rowan.linalgtoolkit.logic2d;

import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>Contains2D</code> class provides containment query logic for vertices
 * and shapes in the <code>shapes2d</code> package.
 * 
 * @author Spence DiNicolantonio, Michael Liguori, Jonathan Palka
 * @version 1.1
 * @since 1.1
 */
public abstract class Contains2D {
    
    
    /*********************************************
     * MARK: Point
     *********************************************/
    
    /**
     * Determines whether a given 2D vertex, in world coordinates, lies on a given
     * 2D point.
     * @param point     The parent <code>Point2D</code>.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>point</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Point2D point, Vector2D vertex) {
        // compare point position vertex to given vertex
        return (point.getPosition().equals(vertex));
    }
    
    /**
     * Determines whether a given 2D point lies on another given 2D point.
     * @param parent    The parent <code>Point2D</code>.
     * @param child     The child <code>Point2D</code>.
     * @return          <code>true</code> if <code>child</code> is wholly contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Point2D parent, Point2D child) {
        // compare point position vertices
        return (parent.getPosition().equals(child.getPosition()));
    }
    
    
    /*********************************************
     * MARK: Segment
     *********************************************/
    
    /**
     * Determines whether a given 2D vertex, in world coordinates, lies on a given
     * 2D line segment.
     * @param segment   The parent <code>Segment2D</code>.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>segment</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Segment2D segment, Vector2D vertex) {
        // convert vertex to local coordinates
        vertex = segment.toLocal(vertex);
        
        // check if vertex is between segment enpoints
        return LinAlg2D.isBetween(vertex, segment.getStart(), segment.getEnd());
    }
    
    /**
     * Determines whether a given 2D line segment contains a given 2D point.
     * @param segment   The parent <code>Segment2D</code>.
     * @param point     The child <code>Point2D</code>.
     * @return          <code>true</code> if <code>point</code> is wholly contained
     *                  by <code>segment</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Segment2D segment, Point2D point) {
        // check if point position vertex is contained by segment
        return contains(segment, point.getPosition());
    }
    
    /**
     * Determines whether a given 2D line segment contains another given 2D line
     * segment.
     * @param parent    The parent <code>Segment2D</code>.
     * @param child     The child <code>Segment2D</code>.
     * @return          <code>true</code> if <code>child</code> is wholly contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Segment2D parent, Segment2D child) {
        // convert child endpoints to world coordinates
        Vector2D start = child.toWorld(child.getStart());
        Vector2D end = child.toWorld(child.getEnd());
        
        // check if both child segment endpoints are contained by parent segment
        return (contains(parent, start) &&
                contains(parent, end));
    }
    
    
    /*********************************************
     * MARK: Circle
     *********************************************/
    
    /**
     * Determines whether a given 2D vertex, in world coordinates, lies within a
     * given 2D circle.
     * @param circle    The parent <code>Circle2D</code>.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>circle</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Circle2D circle, Vector2D vertex) {
        // calculate distance between circle center point and vertex
        double vertexDist = circle.getCenter().distance(vertex);
        
        // compare distance to circle radius
        return (vertexDist <= circle.getRadius());
    }
    
    /**
     * Determines whether a given 2D circle contains a given 2D point.
     * @param circle    The parent <code>Circle2D</code>.
     * @param point     The child <code>Point2D</code>.
     * @return          <code>true</code> if <code>point</code> is wholly contained
     *                  by <code>circle</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Circle2D circle, Point2D point) {
        // check if point position vertex is contained by circle
        return contains(circle, point.getPosition());
    }
    
    /**
     * Determines whether a given 2D circle wholly contains a given 2D segment.
     * @param circle    The parent <code>Circle2D</code>.
     * @param segment   The child <code>Segment2D</code>.
     * @return          <code>true</code> if <code>segment</code> is wholly contained
     *                  by <code>circle</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Circle2D circle, Segment2D segment) {
        // convert segment endpoints to world coordinates
        Vector2D start = segment.toWorld(segment.getStart());
        Vector2D end = segment.toWorld(segment.getEnd());
        
        // check if both endpoints are contained by circle
        return (contains(circle, start) &&
                contains(circle, end));
    }
    
    /**
     * Determines whether a given 2D circle wholly contains another given 2D circle.
     * @param parent    The parent <code>Circle2D</code>.
     * @param child     The child <code>Circle2D</code>.
     * @return          <code>true</code> if <code>child</code> is wholly contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Circle2D parent, Circle2D child) {
        // calculate distance between circle center points
        double centerDist = parent.getPosition().distance(child.getPosition());
        
        // compare sum of distance and child radius to parent radius
        if (centerDist + child.getRadius() <= parent.getRadius())
            return true;
        return false;
    }
    
    /**
     * Determines whether a given 2D circle wholly contains a given 2D polygon.
     * @param circle    The parent <code>Circle2D</code>.
     * @param polygon   The child <code>Polygon2D</code>.
     * @return          <code>true</code> if <code>polygon</code> is wholly contained
     *                  by <code>circle</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Circle2D circle, Polygon2D polygon) {
        // for each polygon vertex...
        for (Vector2D vertex : polygon.getVertices()) {
            
            // convert vertex to world coordinates
            vertex = polygon.toWorld(vertex);
            
            // check if vertex is ouside polygon
            if (!contains(circle, vertex))
                return false;
        }
        return true;
    }
    
    
    /*********************************************
     * MARK: Polygon
     *********************************************/
    
    /**
     * Determines whether a given 2D vertex, in world coordinates, lies within a
     * given 2D polygon.
     * @param polygon   The parent <code>Polygon2D</code>.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>polygon</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polygon2D polygon, Vector2D vertex) {
        // convert vertex to local coordinates
        vertex = polygon.toLocal(vertex);
        
        // check if given vertex is contained by polygon vertices
        return LinAlg2D.contains(polygon.getVertices(), vertex);
    }
    
    /**
     * Determines whether a given 2D polygon contains a given 2D point.
     * @param polygon   The parent <code>Polygon2D</code>.
     * @param point     The child <code>Point2D</code>.
     * @return          <code>true</code> if <code>point</code> is wholly contained
     *                  by <code>polygon</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polygon2D polygon, Point2D point) {
        // check if point position vertex is contained by polygon
        return contains(polygon, point.getPosition());
    }
    
    /**
     * Determines whether a given 2D polygon wholly contains a given 2D segment.
     * @param polygon   The parent <code>Polygon2D</code>.
     * @param segment   The child <code>Segment2D</code>.
     * @return          <code>true</code> if <code>segment</code> is wholly contained
     *                  by <code>polygon</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polygon2D polygon, Segment2D segment) {
        // convert segment endpoints to world coordinates
        Vector2D start = segment.toWorld(segment.getStart());
        Vector2D end = segment.toWorld(segment.getEnd());
        
        // check if both endpoints of segment are contained by polygon
        return (contains(polygon, start) &&
                contains(polygon, end));
    }
    
    /**
     * Determines whether a given 2D polygon wholly contains a given 2D circle.
     * @param polygon   The parent <code>Polygon2D</code>.
     * @param circle    The child <code>Circle2D</code>.
     * @return          <code>true</code> if <code>circle</code> is wholly contained
     *                  by <code>polygon</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polygon2D polygon, Circle2D circle) {
        // check if polygon contains circle center point
        if (!contains(polygon, circle.getCenter()))
            return false;
        
        // find shortest distance from an edge of the polygon to the circle center
        Vector2D localCenter = polygon.toLocal(circle.getCenter());
        double shortestDist = LinAlg2D.distanceBetween(localCenter, polygon.getVertices());
        
        // verify that the shortest distance is not less than the circle radius
        return (shortestDist >= circle.getRadius());
    }
    
    /**
     * Determines whether a given 2D polygon wholly contains another given 2D
     * polygon.
     * @param parent    The parent <code>Polygon2D</code>.
     * @param child     The child <code>Polygon2D</code>.
     * @return          <code>true</code> if <code>child</code> is wholly contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polygon2D parent, Polygon2D child) {
        // check each child polygon vertex for containment
        for (Vector2D vertex : child.getVertices()) {
            // convert vertex to world coords
            vertex = child.toWorld(vertex);
            
            // check for containment in parent
            if (!contains(parent, vertex))
                return false;
        }
        
        // at this point all child polygon vertices are contained
        return true;
    }
    
    
    /*********************************************
     * MARK: Complex
     *********************************************/
    
    /**
     * Determines whether a given 2D vertex, in world coordinates, lies within a
     * given 2D complex shape.
     * @param complex   The parent <code>ComplexShape2D</code>.
     * @param vertex    A <code>Vector2D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>complex</code>; <code>false</code> otherwise.
     */
    public static boolean contains(ComplexShape2D complex, Vector2D vertex) {
        // check for containment in each of complex shape's sub-shapes
        for (Shape2D subShape : complex.getSubShapes())
            if (LinAlg2D.contains(subShape, vertex))
                return true;
        
        // at this point the vertex is not contained in any sub-shapes
        return false;
    }
    
    /**
     * Determines whether a given 2D complex shape lies within a given 2D shape.
     * @param shape     The parent <code>Shape2D</code>.
     * @param complex   The child <code>ComplexShape2D</code>.
     * @return          <code>true</code> if <code>complex</code> is contained
     *                  by <code>shape</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Shape2D shape, ComplexShape2D complex) {
        // check for containment of each of complex shape's sub-shapes
        for (Shape2D subShape : complex.getSubShapes())
            if (!LinAlg2D.contains(shape, subShape))
                return false;
        
        // at this point we know all sub-shapes are contained in the parent shape
        return true;
    }
}