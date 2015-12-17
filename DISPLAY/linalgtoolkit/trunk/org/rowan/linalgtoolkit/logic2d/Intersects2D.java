package org.rowan.linalgtoolkit.logic2d;

import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>Intersects2D</code> class provides boolean intersection query logic 
 * for shapes in the <code>shapes2d</code> package.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public abstract class Intersects2D {
    
    /*********************************************
     * MARK: Point
     *********************************************/
    
    /**
     * Determines whether a given 2D point intersects a given 2D shape.
     * @param point A <code>Point2D</code>.
     * @param shape A <code>Shape2D</code>.
     * @return      <code>true</code> if <code>point</code> intersects
     *              <code>shape</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Point2D point, Shape2D shape) {
        // given point intersects given shape iff point is contained in shape
        return LinAlg2D.contains(shape, point);
    }
    
    
    /*********************************************
     * MARK: Segment
     *********************************************/
    
    /**
     * Determines whether two given 2D line segments intersect.
     * @param segment1  A <code>Segment2D</code>.
     * @param segment2  A <code>Segment2D</code>.
     * @return          <code>true</code> if <code>segment1</code> intersects
     *                  <code>segment2</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Segment2D segment1, Segment2D segment2) {
        // algorithm developed by Paul Bourke:
        // http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
        
        // store segment endpoint component values, in world coords, for easy access
        Vector2D p1 = segment1.toWorld(segment1.getStart());
        Vector2D p2 = segment1.toWorld(segment1.getEnd());
        Vector2D p3 = segment2.toWorld(segment2.getStart());
        Vector2D p4 = segment2.toWorld(segment2.getEnd());
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();
        double x4 = p4.getX();
        double y4 = p4.getY();
        
        // compute shared denominator
        double denom = (y4 - y3)*(x2 - x1) - (x4 - x3)*(y2 - y1);
        
        // compute determinate value numerators
        double numA = (x4 - x3)*(y1 - y3) - (y4 - y3)*(x1 - x3);
        double numB = (x2 - x1)*(y1 - y3) - (y2 - y1)*(x1 - x3);
        
        
        // if denom is 0, the segments are parallel
        if (denom == 0) {
            
            // if either numerator is not 0 segments are not coincidental
            if (numA != 0 || numB != 0)
                return false;
            
            // coincidental at this point... check for endpoint containment
            return (LinAlg2D.contains(segment1, p3) ||
                    LinAlg2D.contains(segment1, p4) ||
                    LinAlg2D.contains(segment2, p1) ||
                    LinAlg2D.contains(segment2, p2));
        }
        
        // compute determinate values
        double detA = numA / denom;
        double detB = numB / denom;
        
        // the segments intersect if both ua and ub are between 0 and 1
        return (detA >= 0 && detA <= 1 &&
                detB >= 0 && detB <= 1);
    }
    
    /**
     * Determines whether a given 2D line segment intersects a given 2D circle.
     * @param segment   A <code>Segment2D</code>.
     * @param circle    A <code>Circle2D</code>.
     * @return          <code>true</code> if <code>segment</code> intersects
     *                  <code>circle</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Segment2D segment, Circle2D circle) {
        // compute the distance from the segment to the circle center point
        double centerDist = LinAlg2D.distance(segment, circle.getCenter());
        
        // intersecting if distance from circle center point not greater than radius
        return (centerDist <= circle.getRadius());
    }
    
    /**
     * Determines whether a given 2D line segment intersects a given 2D polygon.
     * @param segment   A <code>Segment2D</code>.
     * @param polygon   A <code>Polygon2D</code>.
     * @return          <code>true</code> if <code>segment</code> intersects
     *                  <code>polygon</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Segment2D segment, Polygon2D polygon) {
        // convert segment endpoints to polygon's local coords
        Vector2D start = polygon.toLocal(segment.getStart(), segment);
        Vector2D end = polygon.toLocal(segment.getEnd(), segment);
        
        // if either segment endpoint is contained in polygon, then they intersect
        Vector2D worldStart = segment.toWorld(segment.getStart());
        Vector2D worldEnd = segment.toWorld(segment.getEnd());
        if (LinAlg2D.contains(polygon, worldStart) || LinAlg2D.contains(polygon, worldEnd))
            return true;
        
        
        // for every edge of the polygon...
        for (int i=0; i<polygon.edgeCount(); i++) {
            Segment2D edge = polygon.getEdge(i);
            
            // check for intersection of given segment with current edge
            if (intersects(segment, edge))
                return true;
        }
        
        // at this point, no intersection was found
        return false;
    }
    
    
    /*********************************************
     * MARK: Circle
     *********************************************/
    
    /**
     * Determines whether two given 2D circles intersect.
     * @param circle1   A <code>Circle2D</code>.
     * @param circle2   A <code>Circle2D</code>.
     * @return          <code>true</code> if <code>circle1</code> intersects
     *                  <code>circle2</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Circle2D circle1, Circle2D circle2) {
        // compute distance between circle center points
        double centerDist = circle1.getCenter().distance(circle2.getCenter());
        
        // intersecting if center point distance minus radii not greater than 0
        return (centerDist - circle1.getRadius() - circle2.getRadius()) <= 0;
    }
    
    /**
     * Determines whether a given 2D circle intersects a given 2D polygon.
     * @param circle    A <code>Circle2D</code>.
     * @param polygon   A <code>Polygon2D</code>.
     * @return          <code>true</code> if <code>circle</code> intersects
     *                  <code>polygon</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Circle2D circle, Polygon2D polygon) {
        // find shortest distance from circle center point to a polygon edge
        double centerDist = LinAlg2D.distance(polygon, circle.getCenter());
        
        // intersecting if distance from circle center point not greater than radius
        return (centerDist <= circle.getRadius());
    }
    
    
    /*********************************************
     * MARK: Polygon
     *********************************************/
    
    /**
     * Determines whether two given 2D polygons intersect.
     * @param polygon1  A <code>Polygon2D</code>.
     * @param polygon2  A <code>Polygon2D</code>.
     * @return          <code>true</code> if <code>polygon1</code> intersects
     *                  <code>polygon2</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Polygon2D polygon1, Polygon2D polygon2) {
        // for every edge in polygon1...
        for (int i=0; i<polygon1.edgeCount(); i++) {
            Segment2D edge = polygon1.getEdge(i);
            
            // check for edge intersection with polygon2
            if (intersects(edge, polygon2))
                return true;
        }
        
        // at this point, the only way the polygons can intersect is if 
        // polygon1 contains polygon2
        if (LinAlg2D.contains(polygon1, polygon2))
            return true;
        return false;
    }
    
    
    /*********************************************
     * MARK: Complex
     *********************************************/
    
    /**
     * Determines whether a given 2D complex shape intersects a given 2D shape.
     * @param complex   A <code>ComplexShape2D</code>.
     * @param shape     A <code>Shape2D</code>.
     * @return          <code>true</code> if <code>segment</code> intersects
     *                  <code>complex</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(ComplexShape2D complex, Shape2D shape) {
        // check for intersection in each of complex shape's sub-shapes
        for (Shape2D subShape : complex.getSubShapes())
            if (LinAlg2D.intersects(shape, subShape))
                return true;
        
        // at this point, no sub-shape intersects the segment
        return false;
    }
    
}