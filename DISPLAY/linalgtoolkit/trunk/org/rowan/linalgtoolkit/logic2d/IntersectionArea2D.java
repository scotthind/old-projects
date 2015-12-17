package org.rowan.linalgtoolkit.logic2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>IntersectionArea2D</code> class provides computation logic for 
 * determining the area of intersection between two shapes in the <code>shapes2d</code> 
 * package.
 * 
 * @author Spence DiNicolantonio, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public abstract class IntersectionArea2D {
	
    
    /*********************************************
     * MARK: Point
     *********************************************/
    
    /**
     * Computes the area of intersection between a given 2D point and 2D shape.
     * By definition, the area of intersection with a point is always 0.
     * @param point A <code>Point2D</code>.
     * @param shape A <code>Shape2D</code>.
     * @return      The area of intersection between <code>point</code> and 
     *              <code>shape</code>, which is always 0.
     */
    public static double intersectionArea(Point2D point, Shape2D shape) {
        // area of intersection with a point is 0, by definition
        return 0;
    }
    
    
    /*********************************************
     * MARK: Segment
     *********************************************/
    
    /**
     * Computes the area of intersection between a given 2D segment and 2D shape.
     * By definition, the area of intersection with a segment is always 0.
     * @param segment   A <code>Segment2D</code>.
     * @param shape     A <code>Shape2D</code>.
     * @return          The area of intersection between <code>segment</code> and 
     *                  <code>shape</code>, which is always 0.
     */
    public static double intersectionArea(Segment2D segment, Shape2D shape) {
        // area of intersection with a segment is 0, by definition
        return 0;
    }
    
    
    /*********************************************
     * MARK: Circle
     *********************************************/
    
    /**
     * Computes the area of intersection between two given 2D circles.
     * @param circle1   A <code>Circle2D</code>.
     * @param circle2   A <code>Circle2D</code>.
     * @return          The area of intersection between <code>circle1</code> and 
     *                  <code>circle2</code>.
     */
    public static double intersectionArea(Circle2D circle1, Circle2D circle2) {
        // algorithm derived from equations found at:
        // http://mathworld.wolfram.com/Circle-CircleIntersection.html
        
        // check for intersection
        if (!LinAlg2D.intersects(circle1, circle2))
            return 0;
        
        // compute distance between circles
        double d = LinAlg2D.distance(circle1, circle2);
        
        // get circle radii
        double r = circle1.getRadius();
        double R = circle2.getRadius();
        
        // compute squared radii and distance
        double r2 = r*r;
        double R2 = R*R;
        double d2 = d*d;
        
        // compute area
        double temp1 = r2 * Math.acos((d2+r2-R2)/(2*d*r));
        double temp2 = R2 * Math.acos((d2+R2-r2)/(2*d*R));
        double temp3 = 0.5 * Math.sqrt((-d+r+R) * (d+r-R) * (d-r+R) * (d+r+R));
        return temp1 + temp2 - temp3;
    }
    
    /**
     * Computes the area of intersection between a given 2D circle and 2D polygon.
     * @param circle    A <code>Circle2D</code>.
     * @param polygon   A <code>Polygon2D</code>.
     * @return          The area of intersection between <code>circle</code> and 
     *                  <code>polygon</code>.
     */
    public static double intersectionArea(Circle2D circle, Polygon2D polygon) {
        // check for intersection
        if (!LinAlg2D.intersects(circle, polygon))
            return 0;
        
        // polygon in circle?
        if (LinAlg2D.contains(circle, polygon))
            return polygon.area();
        
        // circle in polygon?
        if (LinAlg2D.contains(polygon, circle))
            return circle.area();
        
        // get polygon vertices in world coords
        List<Vector2D> polyVerts = polygon.getWorldVertices();
        
        // creat list to contain points of intersection shape
        LinkedList<Vector2D> intersectionShape = new LinkedList<Vector2D>();
        
        
        // create a list to store indexes of intersection vertices which are on 
        // the perimeter of the circle. This list will be used to compute the 
        // area of  non-polygon sections.
        LinkedList<Integer> circleVertIndex = new LinkedList<Integer>();
        
        for (int i=0; i<polyVerts.size(); i++) {
            Vector2D vert = polyVerts.get(i);
            
            // vertex in circle?
            if (LinAlg2D.contains(circle, vert) && !intersectionShape.contains(vert))
                intersectionShape.add(vert);
            
            
            // At this point, we know the vertex does not exist within the circle, 
            // but perhaps the line segment which connects this vertex and the 
            // next intersects the circle. If that is the case, store the vertices 
            // of intersection and make note of these vertices in the circleVertIndex 
            // list for computing the area later.
            
            // get next vertex
            Vector2D nextVert = polyVerts.get((i+1) % polyVerts.size());
            
            // create segment between current and next vertices
            Segment2D seg = new Segment2D(vert, nextVert);
            
            // check for intersection between circle and created segment
            // if no intersection, continue to next vertex
            if (!LinAlg2D.intersects(circle, seg))
                continue;
            
            // get intersection shape
            Shape2D intersection = LinAlg2D.intersection(circle, seg);
            
            // if segment intersects at a point add it to the intersecting shape
            if (intersection instanceof Point2D) {
                Vector2D intersectPoint = intersection.getPosition();
                if (!intersectionShape.contains(intersectPoint)) {
                    intersectionShape.add(intersectPoint);
                    circleVertIndex.add(intersectionShape.size() - 1);
                }
            } 
            
            // otherwise the intersection edge intersects the circle twice
            // so add both points endpoints to the intersecting shape
            else if (intersection instanceof Segment2D) {
                Segment2D segment = (Segment2D) intersection;
                Vector2D segStart = segment.toWorld(segment.getStart());
                Vector2D segEnd = segment.toWorld(segment.getEnd());
                if (!intersectionShape.contains(segStart)) {
                    intersectionShape.add(segStart);
                    circleVertIndex.add(intersectionShape.size() - 1);
                }
                if (!intersectionShape.contains(segEnd)) {
                    intersectionShape.add(segEnd);
                    circleVertIndex.add(intersectionShape.size() - 1);
                }
            }
                
        }
        
        // compute the polygonal area of the intersecting shape
        // this computations omits the curved non-polygon sections from the calculation
        double area = Polygon2D.area(intersectionShape);
        
        // go through pairs of vertices which lie both on the circle's perimeter 
        // and the polygon
        while (circleVertIndex.size() >= 2) {
            int index1 = circleVertIndex.remove(0).intValue();
            int index2 = circleVertIndex.remove(0).intValue();
            
            Vector2D vert1 = intersectionShape.get(index1);
            Vector2D vert2 = intersectionShape.get(index2);
            
            // determine the angle between the edges P1->Circle Center and 
            // P2->Circle Center using the law of cosines
            double a = circle.getCenter().distance(vert1);
            double b = vert1.distance(vert2);
            double c = circle.getCenter().distance(vert2);
            
            double theta = Math.acos((a*a + c*c - b*b) / (2*a*c));
            
            // subtract the triangle area from the partial circle area to obtain
            // the non-polygon area that was omited from the polygon area calculation 
            // previously
            double circleSliceArea = 0.5 * Math.pow(circle.getRadius(), 2) * (theta - Math.sin(theta));
            
            area += circleSliceArea;
        }
        
        return area;
    }
    
    
    /*********************************************
     * MARK: Polygon
     *********************************************/
    
    /**
     * Computes the area of intersection between two given 2D polygons.
     * @param polygon1  A <code>Polygon2D</code>.
     * @param polygon2  A <code>Polygon2D</code>.
     * @return          The area of intersection between <code>polygon1</code> and 
     *                  <code>polygon2</code>.
     */
    public static double intersectionArea(Polygon2D polygon1, Polygon2D polygon2) {
        // check for intersection
        if (!LinAlg2D.intersects(polygon1, polygon2))
            return 0;
        
        return LinAlg2D.intersection(polygon1, polygon2).area();
    }
    
    
    /*********************************************
     * MARK: Complex
     *********************************************/
    
    /**
     * Computes the area of intersection between a given 2D complex shape and
     * 2D shape.
     * @param complex   A <code>ComplexShape2D</code>.
     * @param shape     A <code>Shape2D</code>.
     * @return          The area of intersection between <code>complex</code> and 
     *                  <code>shape</code>.
     */
    public static double intersectionArea(ComplexShape2D complex, Shape2D shape) {
        // check for intersection
        if (!LinAlg2D.intersects(complex, shape))
            return 0;
        
        return LinAlg2D.intersection(complex, shape).area();
    }
}

