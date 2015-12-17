package org.rowan.linalgtoolkit.logic2d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.*;
import org.rowan.linalgtoolkit.BoundingBox2D;

/**
 * The <code>Intersection2D</code> class provides intersection query logic for 
 * shapes in the <code>shapes2d</code> package.
 * 
 * @author Spence DiNicolantonio, Michael Liguori, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public abstract class Intersection2D {
    
    /*********************************************
     * MARK: Point
     *********************************************/
    
    /**
     * Calculates the 2D shape created by the intersection of two given 2D points.
     * @param  point1   A <code>Point2D</code>.
     * @param  point2   A <code>Point2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>point1</code> and <code>point2</code>.
     * @throws IntersectException if <code>point1</code> and <code>point2</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Point2D point1, Point2D point2) {
        // intersection exists?
        if (!LinAlg2D.intersects(point1, point2))
            throw new IntersectException("Given shapes do not intersect");
        
        return new Point2D(point1.getPosition());
    }
    
    /**
     * Calculates the 2D shape created by the intersection of a given 2D point
     * and 2D segment.
     * @param  point    A <code>Point2D</code>.
     * @param  segment  A <code>Segment2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>point</code> and <code>segment</code>.
     * @throws IntersectException if <code>point</code> and <code>segment</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Point2D point, Segment2D segment) {
        // intersection exists?
        if (!LinAlg2D.intersects(point, segment))
            throw new IntersectException("Given shapes do not intersect");
        
        return new Point2D(point.getPosition());
    }
    
    /**
     * Calculates the 2D shape created by the intersection of a given 2D point
     * and 2D circle.
     * @param  point    A <code>Point2D</code>.
     * @param  circle   A <code>Circle2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>point</code> and <code>circle</code>.
     * @throws IntersectException if <code>point</code> and <code>circle</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Point2D point, Circle2D circle) {
        // intersection exists?
        if (!LinAlg2D.intersects(point, circle))
            throw new IntersectException("Given shapes do not intersect");
        
        return new Point2D(point.getPosition());
    }
    
    /**
     * Calculates the 2D shape created by the intersection of a given 2D point
     * and 2D polygon.
     * @param  point    A <code>Point2D</code>.
     * @param  polygon  A <code>Polygon2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>point</code> and <code>polygon</code>.
     * @throws IntersectException if <code>point</code> and <code>polygon</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Point2D point, Polygon2D polygon) {
        // intersection exists?
        if (!LinAlg2D.intersects(point, polygon))
            throw new IntersectException("Given shapes do not intersect");
        
        return new Point2D(point.getPosition());
    }
    
    
    /*********************************************
     * MARK: Segment
     *********************************************/
    
    /**
     * Calculates the 2D shape created by the intersection of two given 2D segments.
     * @param segment1  A <code>Segment2D</code>.
     * @param segment2  A <code>Segment2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>segment1</code> and <code>segment2</code>.
     * @throws IntersectException if <code>segment1</code> and <code>segment2</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Segment2D segment1, Segment2D segment2) {
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
        
        // compute determinate numerators
        double numA = (x4 - x3)*(y1 - y3) - (y4 - y3)*(x1 - x3);
        double numB = (x2 - x1)*(y1 - y3) - (y2 - y1)*(x1 - x3);
        
        // if determinate denom is 0, the segments are parallel
        boolean parallel = (denom == 0);
        
        // if segments not parallel, then the intersection is a single point
        if (!parallel) {
            
            // compute determinate values
            double detA = numA / denom;
            double detB = numB / denom;
            
            // if either determinate value is outside the range [0, 1],
            // then the segments do not intersect
            if (detA < 0 || detA > 1 || detB < 0 || detB > 1)
                throw new IntersectException("Given shapes do not intersect");
            
            // at this point we know the segments intersect at a single point
            // create and return that point
            double x = x1 + detA*(x2 - x1);
            double y = y1 + detA*(y2 - y1);
            return new Point2D(x, y);
        }
        
        
        // at this point, we know that the given segments are parallel
        // if either numerator is not 0 segments are not coincidental
        // and thus do not intersect
        if (numA != 0 || numB != 0)
            throw new IntersectException("Given shapes do not intersect");
        
        // coincidental at this point... check for endpoint containment
        // if the segments do intersect, then the two contained endpoints
        // will make the endpoints of the intersection segment
        Vector2D endpoint1 = null;
        Vector2D endpoint2 = null;
        
        // p3:
        if (LinAlg2D.contains(segment1, p3))
            endpoint1 = p3;
        
        // p4:
        if (LinAlg2D.contains(segment1, p4)) {
            if (endpoint1 == null)
                endpoint1 = p4;
            else
                endpoint2 = p4;
        }
        
        // p1:
        if (LinAlg2D.contains(segment2, p1)) {
            if (endpoint1 == null)
                endpoint1 = p1;
            else
                endpoint2 = p1;
        }
        
        // p2:
        if (LinAlg2D.contains(segment2, p2)) {
            if (endpoint1 == null)
                endpoint1 = p2;
            else
                endpoint2 = p2;
        }
        
        // if no points are contained, the segments do not intersect
        if (endpoint1 == null || endpoint2 == null)
            throw new IntersectException("Given shapes do not intersect");
        
        // if the two endpoints are the same, return a point
        if (endpoint1.equals(endpoint2))
            return new Point2D(endpoint1);
        
        // create intersection segment and return
        return new Segment2D(endpoint1, endpoint2);
    }
    
    /**
     * Calculates the 2D shape created by the intersection of a given 2D segment
     * and 2D circle.
     * @param segment   A <code>Segment2D</code>.
     * @param circle    A <code>Circle2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>segment</code> and <code>circle</code>.
     * @throws IntersectException if <code>segment</code> and <code>circle</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Segment2D segment, Circle2D circle) {// intersection exists?
        if (!LinAlg2D.intersects(segment, circle))
            throw new IntersectException("Given shapes do not intersect");
        
        // get segment endpoints in world coords
        Vector2D start = segment.toWorld(segment.getStart());
        Vector2D end = segment.toWorld(segment.getEnd());
        
        // determine which segment endpoints are inside the circle
        boolean startInside = LinAlg2D.contains(circle, start);
        boolean endInside = LinAlg2D.contains(circle, end);
        
        // if both endpoints inside circle, the segment is wholly contained
        if (startInside && endInside)
            return new Segment2D(start, end);
        
        
        // get circle center point and radius in world coords
        Vector2D center = circle.getCenter();
        double radius = circle.getRadius();
        
        // find closest point on segment to circle center point and store distance
        Vector2D closestPoint = LinAlg2D.closestBetween(center, start, end);
        double closestDist = center.distance(closestPoint);
        
        // if closest distance equals circle radius, then segment is tangential
        if (closestDist == radius)
            return new Point2D(closestPoint);
        
        // find closest endpoint to circle center and store distance
        double startDist = center.distance(start);
        double endDist = center.distance(end);
        Vector2D closestEnd = (startDist < endDist)? start : end;
        double closestEndDist = (startDist < endDist)? startDist : endDist;
        
        
        // both endpoints outside the circle:
        if (!startInside && !endInside) {
            
            // compute intersection segment distance
            double c = Math.asin(closestDist / radius);
            double halfIntSegDist = radius * Math.cos(c);
            
            // compute two intersection points
            Vector2D segDeltaVect = end.subtract(start);
            Vector2D deltaVect = new Vector2D(segDeltaVect, halfIntSegDist);
            Vector2D point1 = closestPoint.subtract(deltaVect);
            Vector2D point2 = closestPoint.add(deltaVect);
            
            // create and return intersection segment
            return new Segment2D(point1, point2);
        }
        
        
        // at this point we know that only one endpoint is inside the circle:
        
        // determine which endpoint is inside/outside
        Vector2D inside = (startInside)? start : end;
        Vector2D outside = (startInside)? end : start;
        
        // compute distance between inside point and intersection point
        double deltaDist;
        
        // segment parallel to radius:
        if (segment.isParallel(closestEnd.subtract(center))) {
            // pass through circle center point?
            if (closestDist == 0)
                deltaDist = radius + closestEndDist;
            else 
                deltaDist = radius - closestEndDist;
        }
        
        // segment not parallel to radius:
        else {
            double a1 = Math.acos(closestDist / radius);
            double a2 = Math.acos(closestDist / closestEndDist);
            deltaDist = Math.sin(a1+a2) * closestEndDist * radius / closestDist;
        }
        
        // compute intersection point
        Vector2D deltaVect = new Vector2D(outside.subtract(inside), deltaDist);
        Vector2D intersectionPoint = inside.add(deltaVect);
        
        // create and return intersection segment
        return new Segment2D(inside, intersectionPoint);
    }
    
    /**
     * Calculates the 2D shape created by the intersection of a given 2D segment
     * and 2D polygon.
     * @param segment   A <code>Segment2D</code>.
     * @param polygon   A <code>Polygon2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>segment</code> and <code>polygon</code>.
     * @throws IntersectException if <code>segment</code> and <code>polygon</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Segment2D segment, Polygon2D polygon) {
        // intersection exists?
        if (!LinAlg2D.intersects(segment, polygon))
            throw new IntersectException("Given shapes do not intersect");
        
        // get segment start and end points in world coords
        Vector2D segStart = segment.toWorld(segment.getStart());
        Vector2D segEnd = segment.toWorld(segment.getEnd());
        
        // first check to see which endpoints are inside the polygon
        boolean segStartContained = LinAlg2D.contains(polygon, segStart);
        boolean segEndContained   = LinAlg2D.contains(polygon, segEnd);
        
        // if polygon contain the entire segment, return copy of segment
        if (segStartContained && segEndContained)
            return new Segment2D(segStart, segEnd);
        
        // get polygon edges
        LinkedList<Segment2D> edges = polygon.getEdges();
        LinkedList<Point2D> intersectPoints = new LinkedList<Point2D>();
        LinkedList<Segment2D> intersectSegs = new LinkedList<Segment2D>();
        
        // for each edge of given polygon...
        for (Segment2D edge : edges) {
            
            // check for intersection with given segment
            if(!LinAlg2D.intersects(segment, edge))
                continue;
            
            // compute intersection shape
            Shape2D intersection = intersection(segment, edge);
            
            // if intersection shape is a segment, add it to list and continue
            if(intersection instanceof Segment2D) {
                intersectSegs.add((Segment2D)intersection);
                continue;
            }
            
            // at this point, the intersection must be a point
            // check if this intersection point has been found already
            boolean inList = false;
            for(Point2D point : intersectPoints)
                if(point.equals(intersection))
                    inList = true;
            
            // point not in list? add it
            if(!inList)
                intersectPoints.add((Point2D)intersection);
        }
        
        
        // if intersection segments were found concatenate and return
        if (intersectSegs.size() > 0) {
            Vector2D p1 = null;
            Vector2D p2 = null;
            for (Segment2D seg : intersectSegs) {
                // get segment endpoints in world coords
                Vector2D currStart = seg.toWorld(seg.getStart());
                Vector2D currEnd = seg.toWorld(seg.getEnd());
                
                // if first segment, set points and continue
                if (p1 == null) {
                    p1 = currStart;
                    p2 = currEnd;
                    continue;
                }
                
                // replace shared point with new exclusive point
                if (currStart.equals(p1))
                    p1 = currEnd;
                else if (currStart.equals(p2))
                    p2 = currEnd;
                else if (currEnd.equals(p1))
                    p1 = currStart;
                else if (currEnd.equals(p2))
                    p2 = currStart;
            }
            
            // create and return segment out of p1 and p2
            return new Segment2D(p1, p2);
        }
        
        
        // at this point we know that any edge of the polygon intersects the
        // given segment 1 time at most
        
        // if two intersection points, create and return a segment
        if(intersectPoints.size() == 2) {
            Vector2D p1 = intersectPoints.get(0).getPosition();
            Vector2D p2 = intersectPoints.get(1).getPosition();
            return new Segment2D(p1, p2);
        }
        
        // at this point we know that there is only one intersection point
        Point2D point = intersectPoints.get(0);
        
        // if the point is one of the given segment's endpoints, return it
        if (segStart.equals(point.getPosition()) || segEnd.equals(point.getPosition()))
            return point;
        
        // at this point, we know that the intersection shape is a segment shape
        // starting at the enpoint of the given segment that is contained by the
        // polygon and ending at the point in which the given segment intersects
        // the polygon's edge
        
        // create segment and return
        if (segStartContained)
            return new Segment2D(segStart, point.getPosition());
        else if (segEndContained)
            return new Segment2D(point.getPosition(), segEnd);
        else if (intersectPoints.size() == 1)
            return intersectPoints.get(0);
        
        // if this point is reached, something went wrong with this algorithm
        throw new IntersectException("Computation of Segment2D-Polygon2D intersection failed.");
    }
    
    
    /*********************************************
     * MARK: Circle
     *********************************************/
    
    /**
     * Calculates the 2D shape created by the intersection of two given 2D circles.
     * @param circle1   A <code>Circle2D</code>.
     * @param circle2   A <code>Circle2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>circle1</code> and <code>circle2</code>.
     * @throws IntersectException if <code>circle1</code> and <code>circle2</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Circle2D circle1, Circle2D circle2) {
        throw new RuntimeException("Circle2D-Circle2D intersection is not yet " +
                                   "supported. The Shape2D subclass needed to " +
                                   "represent the resulting shape has not yet " +
                                   "been developed. This will be added in a " +
                                   "future release.");
    }
    
    /**
     * Calculates the 2D shape created by the intersection of a given 2D circle
     * and 2D polygon.
     * @param circle    A <code>Circle2D</code>.
     * @param polygon   A <code>Polygon2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>circle</code> and <code>polygon</code>.
     * @throws IntersectException if <code>circle</code> and <code>polygon</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Circle2D circle, Polygon2D polygon) {
        throw new RuntimeException("Circle2D-Polygon2D intersection is not yet " +
                                   "supported. The Shape2D subclass needed to " +
                                   "represent the resulting shape has not yet " +
                                   "been developed. This will be added in a " +
                                   "future release.");
    }
    
    
    /*********************************************
     * MARK: Polygon
     *********************************************/
    
    /**
     * Calculates the 2D shape created by the intersection of two given 2D polygons.
     * @param polygon1  A <code>Polygon2D</code>.
     * @param polygon2  A <code>Polygon2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>polygon1</code> and <code>polygon2</code>.
     * @throws IntersectException if <code>polygon1</code> and <code>polygon2</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(Polygon2D polygon1, Polygon2D polygon2) {
        // poly2 contained in poly 1?
        if (LinAlg2D.contains(polygon1, polygon2))
            return polygon2;
        
        // poly 1 contained in poly2?
        if (LinAlg2D.contains(polygon2, polygon1))
            return polygon1;
        
        // create lists to store intersection shapes/vertices
        LinkedList<Segment2D> poly1Edges = new LinkedList();
        LinkedList<Segment2D> poly2Edges = new LinkedList();
        LinkedList<Vector2D> vertices = new LinkedList<Vector2D>();
        
        
        // for every edge in polygon 1...
        for (Segment2D edge : polygon1.getEdges()) {
            // intersects polygon2?
            if (!LinAlg2D.intersects(edge, polygon2))
                continue;
            
            // get intersection shape
            Shape2D intersection = LinAlg2D.intersection(edge, polygon2);
            
            // determine intersection shape type and store
            if (intersection instanceof Point2D) {
                Vector2D vertex = ((Point2D) intersection).getPosition();
                if (!vertices.contains(vertex))
                    vertices.add(vertex);
            } else {
                poly1Edges.add((Segment2D)intersection);
            }
        }
        
        
        // for every edge in polygon 2...
        for (Segment2D edge : polygon2.getEdges()) {
            // intersects polygon1?
            if (!LinAlg2D.intersects(edge, polygon1))
                continue;
            
            // get intersection shape
            Shape2D intersection = LinAlg2D.intersection(edge, polygon1);
            
            // determine intersection shape type and store
            if (intersection instanceof Point2D) {
                Vector2D vertex = ((Point2D) intersection).getPosition();
                if (!vertices.contains(vertex))
                    vertices.add(vertex);
            } else {
                poly2Edges.add((Segment2D)intersection);
            }
        }
        
        // if no intersections were found, throw exception
        if (poly1Edges.isEmpty() && poly2Edges.isEmpty() && vertices.isEmpty())
            throw new IntersectException("Given shapes do not intersect");
        
        // if one vertex and no edges found, return the intersection point.
        if (poly1Edges.isEmpty() && poly2Edges.isEmpty() && vertices.size() == 1)
            return new Point2D(vertices.get(0));
        
        
        // clear vertex list
        vertices.clear();
        
        // construct a list of vertices based on all found intersection edges
        for (Segment2D edge : poly1Edges) {
            Vector2D start = edge.toWorld(edge.getStart());
            Vector2D end = edge.toWorld(edge.getEnd());
            if (!vertices.contains(start))
                vertices.add(start);
            if (!vertices.contains(end))
                vertices.add(end);
        }
        for (Segment2D edge : poly2Edges) {
            Vector2D start = edge.toWorld(edge.getStart());
            Vector2D end = edge.toWorld(edge.getEnd());
            if (!vertices.contains(start))
                vertices.add(start);
            if (!vertices.contains(end))
                vertices.add(end);
        }
        
        // remove linearly redundant vertices
        for (int i=0; i<vertices.size() && vertices.size() > 2; i++) {
            Vector2D vert1 = vertices.get(i);
            Vector2D vert2 = vertices.get((i+1) % vertices.size());
            Vector2D vert3 = vertices.get((i+2) % vertices.size());
            if (LinAlg2D.isBetween(vert2, vert1, vert3)) {
                vertices.remove(vert2);
                i--;
            }
        }
        
        
        // at this point we have properly ordered vertices.
        // if only two vertices in the list, create and return a segment
        // otherwise, create and return a polygon
        
        // segment?
        if (vertices.size() == 2)
            return new Segment2D(vertices.get(0), vertices.get(1));
        
        
        // convert vertices to local coordinates
        Vector2D polyCenter = new BoundingBox2D(vertices).getCenter();
        LinkedList<Vector2D> localVerts = new LinkedList<Vector2D>();
        for (Vector2D vertex : vertices)
            localVerts.add(vertex.subtract(polyCenter));
        
        
        /** order vertices with clockwise winding **/
        
        // compute list of polar angles for vertices
        LinkedList<Double> polarAngles = new LinkedList<Double>();
        for (int i=0; i<localVerts.size(); i++) {
            Vector2D vertex = localVerts.get(i);
            double angle;
            if (vertex.getX() == 0 && vertex.getY() == 0) {
                polarAngles.add(new Double(0));
                continue;
            }
            
            double temp = Math.asin(vertex.getY() / vertex.magnitude());
            if (vertex.getX() < 0)
                polarAngles.add(new Double(Math.PI - temp));
            else
                polarAngles.add(new Double(temp));
        }
        
        // create list to store ordered vertices
        LinkedList<Vector2D> orderedVerts = new LinkedList<Vector2D>();
        
        // while more vertices to add to ordered list...
        while (!localVerts.isEmpty()) {
            // find the vertex with largest polar angle
            int largestIndex = 0;
            double largestAngle = Double.NEGATIVE_INFINITY;
            for (int i=0; i<localVerts.size(); i++) {
                if (polarAngles.get(i) > largestAngle) {
                    largestAngle = polarAngles.get(i);
                    largestIndex = i;
                }
            }

            // add the vertex with greatest polar angle to the ordered vertex
            // list, remove the unordered vertex list, and remove its polar angle
            orderedVerts.add(localVerts.get(largestIndex));
            localVerts.remove(largestIndex);
            polarAngles.remove(largestIndex);
        }
               
        
        // create and return polygon
        return new Polygon2D(polyCenter, orderedVerts);
    }
    
    
    /*********************************************
     * MARK: Complex
     *********************************************/
    
    /**
     * Calculates the 2D shape created by the intersection of a given 2D complex
     * shape and another given 2D shape.
     * @param complex   A <code>ComplexShape2D</code>.
     * @param shape     A <code>Shape2D</code>.
     * @return          A <code>Shape2D</code> defining the shared intersection
     *                  area between <code>complex</code> and <code>shape</code>.
     * @throws IntersectException if <code>complex</code> and <code>shape</code>
     *                  do not intersect.
     */
    public static Shape2D intersection(ComplexShape2D complex, Shape2D shape) {
        // create list to contain subshape intersections
        LinkedList<Shape2D> intersections = new LinkedList<Shape2D>();
        
        // find all intersections
        for (Shape2D subshape : complex.getSubShapes())
            if (LinAlg2D.intersects(subshape, shape))
                intersections.add(LinAlg2D.intersection(subshape, shape));
        
        // no intersection found?
        if (intersections.size() == 0)
            throw new IntersectException("Given shapes do not intersect");
        
        // if only one intersection found, return it
        if (intersections.size() == 1)
            return intersections.get(0);
        
        // otherwise, create a complex shape with all found intersections (union)
        return new ComplexShape2D(intersections);
    }
    
}