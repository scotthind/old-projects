package org.rowan.linalgtoolkit.logic3d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.BoundingBox3D;
import org.rowan.linalgtoolkit.shapes3d.*;
import org.rowan.linalgtoolkit.logic2d.LinAlg2D;

/**
 * The <code>Intersects3D</code> class provides boolean intersection query logic
 * for shapes in the <code>Shapes3D</code> package. Currently, Intersections are
 * determined using a shape's bounding box. For this reason, calculation is not
 * precise, and should be re-implemented in the future.
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public abstract class Intersects3D {


    /*********************************************
     * MARK: Point
     *********************************************/

    /**
     * Determines whether a given 3D point intersects a given 3D shape.
     * @param point A <code>Point3D</code>.
     * @param shape A <code>Shape3D</code>.
     * @return      <code>true</code> if <code>point</code> intersects
     *              <code>shape</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Point3D point, Shape3D shape) {
        // given point intersects given shape iff point is contained in shape
        return LinAlg3D.contains(shape, point);
    }


    /*********************************************
     * MARK: Segment
     *********************************************/

    /**
     * Determines whether two given 3D line segments intersect.
     * @param segment1  A <code>Segment3D</code>.
     * @param segment2  A <code>Segment3D</code>.
     * @return          <code>true</code> if <code>segment1</code> intersects
     *                  <code>segment2</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Segment3D segment1, Segment3D segment2) {
        // Derived From http://mathworld.wolfram.com/Line-LineIntersection.html
        // store segment endpoint component values, in world coords, for easy access
        Vector3D p1 = segment1.toWorld(segment1.getStart());
        Vector3D p2 = segment1.toWorld(segment1.getEnd());
        Vector3D p3 = segment2.toWorld(segment2.getStart());
        Vector3D p4 = segment2.toWorld(segment2.getEnd());

        Vector3D d1 = p2.subtract(p1);
        Vector3D d2 = p4.subtract(p3);

        //lines are parallel. Are they on top of each other?
        if(d1.cross(d2).isZeroVector())
            return LinAlg3D.isBetween(p3, p1, p2) ||
                   LinAlg3D.isBetween(p4, p1, p2) ||
                   LinAlg3D.isBetween(p2, p3, p4);

        //Value between [0,1]. Represents the placement on the segment of intersection
        double t = p3.subtract(p1).cross(d2).magnitude() / d1.cross(d2).magnitude();
        Vector3D intersection = p1.add(d1.multiply(t));

        //is the intersection point on both lines and between the segment endpoints
        return LinAlg3D.isBetween(intersection, p1, p2) &&
               LinAlg3D.isBetween(intersection, p3, p4);

    }

    /**
     * Determines whether a given 3D line segment intersects a given 3D face.
     * @param segment   A <code>Segment3D</code>.
     * @param face      A <code>Face3D</code>.
     * @return          <code>true</code> if <code>segment</code> intersects
     *                  <code>face</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Segment3D segment, Face3D face) {
        // convert segment endpoints to face's local coord system
        Vector3D start = segment.toWorld(segment.getStart());
        Vector3D end = segment.toWorld(segment.getEnd());


        // First determine if the segment intersects the plane on which the
        // given face lies, and compute the point at which the intersection occurs.
        // Logic for this process has been derived from:
        // http://softsurfer.com/Archive/algorithm_0104/algorithm_0104B.htm#intersect3D_SegPlane()

        Vector3D u = end.subtract(start);
        Vector3D w = start.subtract(face.getVertex(0));

        double d = face.getNormal().dot(u);
        double n = -face.getNormal().dot(w);

        // segment parallel to plane?
        if (Math.abs(d) < Double.MIN_VALUE)
            // segment lies in plane?
            if (n != 0)
                return false;

        // segment and plane are not parallel
        // compute intersect param
        double sI = n / d;

        // will the segment reach the plane?
        if (sI < 0 || sI > 1)
            return false;

        // compute intersection point, in the face's local coord system
        Vector3D delta = u.multiply(sI);
        Vector3D intersection = start.add(delta);


        // Now that we know where the segment intersects the plane, we can project
        // both the face and the intersection point to 2D space and check if the
        // intersection point lies within the confines of the face.

        List<Vector3D> vertices = face.getVertices();

        // create a tangent vector based on two arbitrary points.
        Vector3D tangentUnit = (vertices.get(0).subtract(vertices.get(1))).unitVector();
        Vector3D normalUnit = face.getNormal().unitVector();
        Vector3D binormal = (tangentUnit.cross(normalUnit));

        // project face vertices relative to the plane into 2D space.
        LinkedList<Vector2D> projectedFaceVerts = new LinkedList<Vector2D>();
        for(Vector3D vertex : vertices) {
            Vector3D relative = vertex.subtract(vertices.get(0));
            double y = relative.dot(binormal);
            double x = relative.dot(tangentUnit);
            projectedFaceVerts.add(new Vector2D(x, y));
        }

        // project intersection vertex to same 2D system
        Vector3D relative = intersection.subtract(vertices.get(0));
        double y = relative.dot(binormal);
        double x = relative.dot(tangentUnit);
        Vector2D projectedIntersection = new Vector2D(x, y);


        // if intersection point lies between the face vertices, we are done
        if (LinAlg2D.contains(projectedFaceVerts, projectedIntersection))
            return true;
        
        // otherwise, reverse the vertex winding and attempt validation again.
        for (int i=0; i<projectedFaceVerts.size(); i++) {
            Vector2D vert = projectedFaceVerts.remove(i);
            projectedFaceVerts.add(0, vert);
        }
        return LinAlg2D.contains(projectedFaceVerts, projectedIntersection);
    }

    /**
     * Determines whether a given 3D line segment intersects a given polyhedron.
     * @param segment       A <code>Segment3D</code>.
     * @param polyhedron    A <code>Polyhedron3D</code>.
     * @return              <code>true</code> if <code>segment</code> intersects
     *                      <code>polyhedron</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Segment3D segment, Polyhedron3D polyhedron) {
        // segment contained in polyhedron?
        if (LinAlg3D.contains(polyhedron, segment))
            return true;

        // segment intersects polyhedron face?
        for (Face3D face : polyhedron.getFaces())
            if (intersects(segment, face))
                return true;

        // at this point we know the segment and polyhedron do not intersect
        return false;
    }

    /**
     * Determines whether a given 3D line segment intersects a given 3D shape.
     * <p>
     * This implementation simply checks for intersection of the given segment
     * with the given shape's bounding box. This process yields very imprecise
     * results. Optimally, intersection methods should be implemented individually
     * for each segment-shape combination.
     * @param segment   A <code>Segment3D</code>.
     * @param shape     A <code>Shape3D</code>.
     * @return          <code>true</code> if <code>segment</code> intersects
     *                  <code>shape</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Segment3D segment, Shape3D shape) {
        // get the bounding box for the given shape
        BoundingBox3D bb = shape.boundingBox();

        // first, check if the segment is contained in the bounding box
        if (bb.contains(segment.boundingBox()))
            return true;

        // At this point, we need to check for intersection with the sides of the
        // bounding box.


        // create lists for storage
        LinkedList<Face3D> faces = new LinkedList<Face3D>();
        LinkedList<Vector3D> verts = new LinkedList<Vector3D>();

        // store bounding box vertices for easy access
        Vector3D a = bb.getA();
        Vector3D b = bb.getB();
        Vector3D c = new Vector3D(a.getX(), b.getY(), b.getZ());
        Vector3D d = new Vector3D(a.getX(), a.getY(), b.getZ());
        Vector3D e = new Vector3D(b.getX(), a.getY(), b.getZ());
        Vector3D f = new Vector3D(a.getX(), b.getY(), a.getZ());
        Vector3D g = new Vector3D(b.getX(), b.getY(), a.getZ());
        Vector3D h = new Vector3D(b.getX(), a.getY(), a.getZ());

        // create faces:
        // front
        verts.add(c);
        verts.add(b);
        verts.add(e);
        verts.add(d);
        faces.add(new Face3D(verts));
        // back
        verts.clear();
        verts.add(f);
        verts.add(g);
        verts.add(h);
        verts.add(a);
        faces.add(new Face3D(verts));
        // left
        verts.clear();
        verts.add(f);
        verts.add(c);
        verts.add(d);
        verts.add(a);
        faces.add(new Face3D(verts));
        // right
        verts.clear();
        verts.add(b);
        verts.add(g);
        verts.add(h);
        verts.add(e);
        faces.add(new Face3D(verts));
        // top
        verts.clear();
        verts.add(f);
        verts.add(g);
        verts.add(b);
        verts.add(c);
        faces.add(new Face3D(verts));
        // bottom
        verts.clear();
        verts.add(a);
        verts.add(h);
        verts.add(e);
        verts.add(d);
        faces.add(new Face3D(verts));

        // check each face for intersection with given segment
        for (Face3D face : faces)
            if (intersects(segment, face))
                return true;

        // at this point we know that the segment is not contained by the given
        // shape's bounding box and does not intersect it
        return false;
    }

    /**
     * Determines whether two given 3D shapes intersect.
     * <p>
     * This implementation simply checks for intersection of the shape's bounding
     * boxes. This process yields very imprecise results. Optimally, intersection
     * methods should be implemented individually for each shape-shape combination.
     * @param shape1    A <code>Shape3D</code>.
     * @param shape2    A <code>Shape3D</code>.
     * @return          <code>true</code> if <code>shape1</code> intersects
     *                  <code>shape2</code>; <code>false</code> otherwise.
     */
    public static boolean intersects(Shape3D shape1, Shape3D shape2) {
        return shape1.boundingBox().intersects(shape2.boundingBox());
    }

}

