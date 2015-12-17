package org.rowan.pathfinder.pathfinder;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.Polygon2D;
import org.rowan.linalgtoolkit.shapes2d.Segment2D;

/**
 * Class <code>Logic2D</code> contains wrapper methods to classes inside the
 * Linear Algebra Toolkit for convenience.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class Logic2D {

    /** The maximum distance allowed between two vertices of a polygon */
    public static final double MAX_POLYGON_VERTEX_DISTANCE = 3; //TODO need to change
    /** The maximum distance between two shapes to be considered touching */
    public static final double MAX_SHAPE_PROXIMITY_DISTANCE = .0005; //TODO need to change

    /**
     * Enhance a polygon by making sure the distance between every vertex is
     * less than Logic2D.MAX_POLYGON_VERTEX_DISTANCE.
     * @param polygon The polygon to enhance.
     * @return The enhanced polygon.
     */
    public static Polygon2D enhancePolygon(Polygon2D polygon) {
        List<Vector2D> verticies = polygon.getWorldVertices();
        List<Vector2D> newVerticies = new ArrayList<Vector2D>();
        Vector2D v1, v2, v, delta;
        int i2, numSections;
        for (int i = 0; i < verticies.size(); i++) {
            i2 = (i + 1 == verticies.size()) ? 0 : i + 1;
            v1 = verticies.get(i);
            v2 = verticies.get(i2);
            newVerticies.add(v1);
            double d = v1.distance(v2);
            if (d > MAX_POLYGON_VERTEX_DISTANCE) {
                numSections = (int) Math.ceil(d / MAX_POLYGON_VERTEX_DISTANCE);
                delta = new Segment2D(v1, v2).deltaVect().multiply(1 / (double) numSections);
                v = v1;
                for (int j = 1; j < numSections; j++) {
                    v = v.add(delta);
                    newVerticies.add(v);
                }
            }
        }
        return new Polygon2D(newVerticies);
    }

    /**
     * Center a list of vertices around the origin. Only works properly if
     * the list of vertices describes a convex shape. Note that this check is
     * not performed in this method.
     * @param vertices The list of vertices to be centered.
     * @return The new list of vertices centered around the origin.
     */
    public static List<Vector2D> centerVertices(List<Vector2D> vertices) {
        List<Vector2D> newVertices = new LinkedList<Vector2D>();
        Vector2D centroid = getCentroid(vertices);
        for (Vector2D v : vertices) {
            newVertices.add(v.add(centroid.inverse()));
        }
        return newVertices;
    }

    /**
     * Return the centroid of the collection of vertices representing a polygon.
     * @param vertices The collection of vertices representing a polygon.
     * @return The centroid of the collection of the vertices.
     */
    public static Vector2D getCentroid(Collection<Vector2D> vertices) {
        Vector2D sum = Vector2D.ORIGIN;
        for (Vector2D v : vertices) {
            sum = sum.add(v);
        }
        return sum.multiply(1 / (double) vertices.size());
    }

    /**
     * Check to see if two polygons are close enough to each other to be
     * considered neighbors. If any vertices of the two polygons are within
     * sqrt(Logic2D.MAX_SHAPE_PROXIMITY_DISTANCE^2 + MAX_POLYGON_VERTEX_DISTANCE^2)
     * then this method will return true.
     * @param p1 The first polygon.
     * @param p2 The second polygon.
     * @return true if the two polygons are close enough to each other to be
     *              considered neighbors.
     */
    public static boolean isNeighbor(Polygon2D p1, Polygon2D p2) {
        for (Vector2D v1 : p1.getWorldVertices()) {
            for (Vector2D v2 : p2.getWorldVertices()) {
                if (v1.distance(v2) < MAX_SHAPE_PROXIMITY_DISTANCE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retrieve the start vector of a segment in world coordinates.
     * @param segment The segment to locate.
     * @return A vector representing the start of the segment.
     */
    public static Vector2D getStart(Segment2D segment) {
        return segment.getStart().add(segment.getPosition());
    }

    /**
     * Retrieve the end vector of a segment in world coordinates.
     * @param segment The segment to locate.
     * @return A vector representing the end of the segment.
     */
    public static Vector2D getEnd(Segment2D segment) {
        return segment.getEnd().add(segment.getPosition());
    }

    /**
     * Given a collection of vertices that represent a convex polygon, sort the
     * vertices such that traversing from each vertex in the list to the next
     * (ending by traversing from the last vertex straight to the first) will
     * form a cycle that will maintain the convexity of the represented polygon.
     * The list that is returned guarantees the fact that each traversal as
     * described above will not intersect any other traversal. If the given 
     * vertices do not represent a convex polygon, this method does not
     * guarantee its results.
     * @param vertices The collection of vertices that represent a convex
     *                 polygon (if connected in a particular order).
     * @return         A list of vertices that represent a cycle through
     *                 the given vertices (no line connecting two adjacent
     *                 vertices will intersect any other).
     */
    public static List<Vector2D> createCycle(Collection<Vector2D> vertices) {
        LinkedList<Vector2D> list = new LinkedList();
        // an empty collection contains no cycle
        if (vertices.size() < 2) {
            return list;
        }
        // a list of size 1, 2, or 3 is always a cycle
        if (vertices.size() < 4) {
            return new LinkedList(vertices);
        }

        // find angles of all vertices relative to the center of all vertices
        ArrayList<VertexAndAngle> values = new ArrayList<VertexAndAngle>();
        Vector2D centroid = getCentroid(vertices);
        double angle;
        for (Vector2D v : vertices) {
            angle = v.add(centroid.inverse()).toAngle();
            values.add(new VertexAndAngle(v, angle));
        }

        // sort all values based on the angles and build a list based on the
        // vertex corresponding to each angle
        Collections.sort(values);
        for (VertexAndAngle v : values) {
            list.add(v.vector);
        }

        return list;
    }

    /**
     * Find the closest point along the edges of a polygon to a given vertex.
     * @param vertex  The vertex to calculate the closest point to.
     * @param polygon The polygon from which to calculate the closet point to
     *                the given vertex.
     * @return The closest point to the given vertex along the edges of the
     *                given polygon.
     */
    public static Vector2D closestPoint(Vector2D vertex, Polygon2D polygon) {
        vertex = polygon.toLocal(vertex);
        List<Vector2D> vertList = polygon.getVertices();

        // algorithm taken from:
        // org.rowan.linalgtoolkit.logic2d.LinAlg2D.closestBetween(Vector2D, List<Vector2D>)

        Vector2D closest = null;
        double dist = Double.MAX_VALUE;

        // for every set of consecutive vertices in vertList...
        for (int i = 0; i < vertList.size(); i++) {
            Vector2D current = vertList.get(i);
            Vector2D next = vertList.get((i + 1) % vertList.size());

            // find closest point between current vertices
            Vector2D tempClosest = closestBetween(vertex, current, next);
            double tempDist = tempClosest.distance(vertex);

            // closest yet?
            if (closest == null || tempDist < dist) {
                closest = tempClosest;
                dist = tempDist;
            }
        }

        // return closest found in world coordinates
        return polygon.toWorld(closest);
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
    private static Vector2D closestBetween(Vector2D vertex, Vector2D between1, Vector2D between2) {
        // entire method taken from:
        // org.rowan.linalgtoolkit.logic2d.LinAlg2D.closestBetween(Vector2D, Vector2D, Vector2D)

        if (between1.equals(between2)) {
            return between1;
        }

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

        double u = ((v1x - v2x) * dx + (v1y - v2y) * dy) / (dx * dx + dy * dy);

        if (u < 0) {
            return between1;
        } else if (u > 1) {
            return between2;
        }

        return new Vector2D(v2x + u * dx,
                v2y + u * dy);
    }

    /**
     * Convert a given unit from MPH to kph.
     * @param mph The unit in MPH.
     * @return The converted value to KMPH.
     */
    public static double MPHtoKPH(double mph) {
        return mph * 1.609344d;
    }

    /**
     * Converts a Vector2D to LatLon, the format used by WorldWind.
     * @param v A Vector2D to be transformed into a LatLon.
     * @return A LatLon representing the original Vector2D.
     */
    public static LatLon vector2DToLatLon(Vector2D v) {
        return new LatLon(Angle.fromDegrees(v.getX()), Angle.fromDegrees(v.getY()));
    }

    /**
     * Converts a LatLon, the format used by WorldWin, to a Vector2D.
     * @param v A LatLon to be transformed into a Vector2D.
     * @return A Vector2D representing the original LatLon.
     */
    public static Vector2D latLonToVector2D(LatLon l) {
        return new Vector2D(l.getLatitude().getDegrees(), l.getLongitude().getDegrees());
    }
}

