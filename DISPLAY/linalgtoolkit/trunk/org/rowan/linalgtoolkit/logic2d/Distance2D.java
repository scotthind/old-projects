package org.rowan.linalgtoolkit.logic2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>Distance2D</code> class provides distance query logic for vertices
 * and shapes in the <code>shapes2d</code> package.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public abstract class Distance2D {

    /*********************************************
     * MARK: Point
     *********************************************/
    
    /**
     * Computes the distance between a given 2D point and 2D vertex.
     * @param point     A <code>Point2D</code>.
     * @param vertex    A <code>Vector2D</code> representing a 2D vertex.
     * @return          The shortest distance between the given line segment and
     *                  vertex.
     */
    public static double distance(Point2D point, Vector2D vertex) {
        return point.getPosition().distance(vertex);
    }
    
    /**
     * Computes the distance between two given 2D points.
     * @param point1    A <code>Point2D</code>.
     * @param point2    A <code>Point2D</code>.
     * @return          The shortest distance between <code>point1</code> and
     *                  <code>point2</code>.
     */
    public static double distance(Point2D point1, Point2D point2) {
        return point1.getPosition().distance(point2.getPosition());
    }
    
    /**
     * Computes the distance between a given 2D point and 2D line segment.
     * @param point     A <code>Point2D</code>.
     * @param segment   A <code>Segment2D</code>.
     * @return          The shortest distance between <code>point</code> and
     *                  <code>segment</code>.
     */
    public static double distance(Point2D point, Segment2D segment) {
        return distance(segment, point.getPosition());
    }
    
    /**
     * Computes the distance between a given 2D point and 2D circle.
     * @param point     A <code>Point2D</code>.
     * @param circle    A <code>Circle2D</code>.
     * @return          The shortest distance between <code>point</code> and
     *                  <code>circle</code>.
     */
    public static double distance(Point2D point, Circle2D circle) {
        return distance(circle, point.getPosition());
    }
    
    /**
     * Computes the distance between a given 2D point and 2D polygon.
     * @param point     A <code>Point2D</code>.
     * @param polygon   A <code>Polygon2D</code>.
     * @return          The shortest distance between <code>point</code> and
     *                  <code>polygon</code>.
     */
    public static double distance(Point2D point, Polygon2D polygon) {
        return distance(polygon, point.getPosition());
    }
    
    
    /*********************************************
     * MARK: Segment
     *********************************************/
    
    /**
     * Computes the distance between a given 2D line segment and 2D vertex.
     * @param segment   A <code>Segment2D</code>.
     * @param vertex    A <code>Vector2D</code> representing a 2D vertex.
     * @return          The shortest distance between <code>vertex</code> and
     *                  <code>segment</code>.
     */
    public static double distance(Segment2D segment, Vector2D vertex) {
        // check for intersection
        if (LinAlg2D.contains(segment, vertex))
            return 0.0;
        
        // convert given vertex to segment's local coords
        vertex = segment.toLocal(vertex);
        
        // compute distance from vertex to segment
        return LinAlg2D.distanceBetween(vertex, segment.getStart(), segment.getEnd());
    }
    
    /**
     * Computes the distance between two given 2D line segments.
     * @param segment1  A <code>Segment2D</code>.
     * @param segment2  A <code>Segment2D</code>.
     * @return          The shortest distance between <code>segment1</code> and
     *                  <code>segment2</code>.
     */
    public static double distance(Segment2D segment1, Segment2D segment2) {
        // check for intersection
        if (LinAlg2D.intersects(segment1, segment2))
            return 0.0;
        
        // create list of segment1's endpoints, converted to world coords
        LinkedList<Vector2D> verts1 = new LinkedList<Vector2D>();
        verts1.add(segment1.toWorld(segment1.getStart()));
        verts1.add(segment1.toWorld(segment1.getEnd()));
        
        // create list of segment2's endpoints, converted to world coords
        LinkedList<Vector2D> verts2 = new LinkedList<Vector2D>();
        verts2.add(segment2.toWorld(segment2.getStart()));
        verts2.add(segment2.toWorld(segment2.getEnd()));
        
        // compute distance between given segments
        return LinAlg2D.distanceBetween(verts1, verts2);
    }
    
    /**
     * Computes the distance between a given 2D line segment and 2D circle.
     * @param segment   A <code>Segment2D</code>.
     * @param circle    A <code>Circle2D</code>.
     * @return          The shortest distance between <code>segment</code> and
     *                  <code>circle</code>.
     */
    public static double distance(Segment2D segment, Circle2D circle) {
        // check for intersection
        if (LinAlg2D.intersects(segment, circle))
            return 0.0;
        
        // compute distance between segment and circle center point
        double centerDist = distance(segment, circle.getCenter());
        
        // subtract circle radius and return
        return centerDist - circle.getRadius();
    }
    
    /**
     * Computes the distance between a given 2D line segment and 2D polygon.
     * @param segment   A <code>Segment2D</code>.
     * @param polygon   A <code>Polygon2D</code>.
     * @return          The shortest distance between <code>segment</code> and
     *                  <code>polygon</code>.
     */
    public static double distance(Segment2D segment, Polygon2D polygon) {
        // check for intersection
        if (LinAlg2D.intersects(segment, polygon))
            return 0.0;
        
        // create list of segment's endpoints, converted to circle's local coords
        LinkedList<Vector2D> segVerts = new LinkedList<Vector2D>();
        segVerts.add(polygon.toLocal(segment.getStart(), segment));
        segVerts.add(polygon.toLocal(segment.getEnd(), segment));
        
        // compute distance between converted segment vertices and polygon vertices
        return LinAlg2D.distanceBetween(segVerts, polygon.getVertices());
    }
    
    
    /*********************************************
     * MARK: Circle
     *********************************************/
    
    /**
     * Computes the distance between a given 2D circle and 2D vertex.
     * @param circle    A <code>Circle2D</code>.
     * @param vertex    A <code>Vector2D</code> representing a 2D vertex.
     * @return          The shortest distance between <code>vertex</code> and
     *                  <code>circle</code>.
     */
    public static double distance(Circle2D circle, Vector2D vertex) {
        // check for intersection
        if (LinAlg2D.contains(circle, vertex))
            return 0.0;
        
        // compute distance from circle center point
        double centerDist = circle.getCenter().distance(vertex);
        
        // subtract circle radius
        double distance = centerDist - circle.getRadius();
        
        return (distance <= 0.0)? 0.0 : distance;
    }
    
    /**
     * Computes the distance between two given 2D circles.
     * @param circle1   A <code>Circle2D</code>.
     * @param circle2   A <code>Circle2D</code>.
     * @return          The shortest distance between <code>circle1</code> and
     *                  <code>circle2</code>.
     */
    public static double distance(Circle2D circle1, Circle2D circle2) {
        // check for intersection
        if (LinAlg2D.intersects(circle1, circle2))
            return 0.0;
        
        // compute distance between circle center points
        double centerDist = circle1.getCenter().distance(circle2.getCenter());
        
        // subtract circle radii and return
        return centerDist - circle1.getRadius() - circle2.getRadius();
    }
    
    /**
     * Computes the distance between a given 2D circle and 2D polygon.
     * @param circle    A <code>Circle2D</code>.
     * @param polygon   A <code>Polygon2D</code>.
     * @return          The shortest distance between <code>circle</code> and
     *                  <code>polygon</code>.
     */
    public static double distance(Circle2D circle, Polygon2D polygon) {
        // check for intersection
        if (LinAlg2D.intersects(circle, polygon))
            return 0.0;
        
        // compute distance between polygon and circle center point
        double centerDist = distance(polygon, circle.getCenter());
        
        // subtract circle radius and return
        return centerDist - circle.getRadius();
    }
    
    
    /*********************************************
     * MARK: Polygon
     *********************************************/
    
    /**
     * Computes the distance between a given 2D polygon and 2D vertex.
     * @param polygon   A <code>Polygon2D</code>.
     * @param vertex    A <code>Vector2D</code> representing a 2D vertex.
     * @return          The shortest distance between <code>vertex</code> and
     *                  <code>polygon</code>.
     */
    public static double distance(Polygon2D polygon, Vector2D vertex) {
        // check for intersection
        if (LinAlg2D.contains(polygon, vertex))
            return 0.0;
        
        // convert given vertex to local coordinates
        vertex = polygon.toLocal(vertex);
        
        // calculate shortest distance between polygon edges and given vertex
        return LinAlg2D.distanceBetween(vertex, polygon.getVertices());
    }
    
    /**
     * Computes the distance between two given 2D polygons.
     * @param polygon1  A <code>Polygon2D</code>.
     * @param polygon2  A <code>Polygon2D</code>.
     * @return          The shortest distance between <code>polygon1</code> and
     *                  <code>polygon2</code>.
     */
    public static double distance(Polygon2D polygon1, Polygon2D polygon2) {
        // check for intersection
        if (LinAlg2D.intersects(polygon1, polygon2))
            return 0.0;
        
        // get list of each polygon's vertices
        List<Vector2D> poly1Verts = new LinkedList<Vector2D>(polygon1.getVertices());
        List<Vector2D> poly2Verts = new LinkedList<Vector2D>(polygon2.getVertices());
        
        // convert polygon2's vertices to polygon1's local coords
        for (int i=0; i<poly2Verts.size(); i++) {
            // store current vertex
            Vector2D vertex = poly2Verts.get(i);
            
            // remove current vertex from list
            poly2Verts.remove(i);
            
            // convert vertex to polygon1 local coords
            vertex = polygon1.toLocal(vertex, polygon2);
            
            // add converted vertex back to list
            poly2Verts.add(i,vertex);
        }
        
        // compute distance
        return LinAlg2D.distanceBetween(poly1Verts, poly2Verts);
    }
    
    
    /*********************************************
     * MARK: Complex
     *********************************************/
    
    /**
     * Computes the distance between a given 2D complex shape and 2D vertex.
     * @param complex   A <code>ComplexShape2D</code>.
     * @param vertex    A <code>Vector2D</code> representing a 2D vertex.
     * @return          The shortest distance between <code>vertex</code> and
     *                  <code>complex</code>.
     */
    public static double distance(ComplexShape2D complex, Vector2D vertex) {
        double shortestDist = Double.MAX_VALUE;
        
        // for every sub-shape in the complex shape...    
        for (Shape2D subShape : complex.getSubShapes()) {
            
            // compute distance from vertex
            double currDist = LinAlg2D.distance(subShape, vertex);
            
            // if distance is 0, return
            if (currDist == 0)
                return currDist;
            
            // shortest distance so far?
            if (currDist < shortestDist)
                shortestDist = currDist;
        }
        
        // return the shortest distance found
        return shortestDist;
    }
    
    /**
     * Computes the distance between a given 2D complex shape and 2D shape.
     * @param complex   A <code>ComplexShape2D</code>.
     * @param shape     A <code>Shape2D</code>.
     * @return          The shortest distance between <code>complex</code> and
     *                  <code>shape</code>.
     */
    public static double distance(ComplexShape2D complex, Shape2D shape) {
        double shortestDist = Double.MAX_VALUE;
        
        // for every sub-shape in the complex shape...    
        for (Shape2D subShape : complex.getSubShapes()) {
            
            // compute distance from shape
            double currDist = LinAlg2D.distance(shape, subShape);
            
            // if distance is 0, return
            if (currDist == 0)
                return currDist;
            
            // shortest distance so far?
            if (currDist < shortestDist)
                shortestDist = currDist;
        }
        
        // return the shortest distance found
        return shortestDist;
    }

}