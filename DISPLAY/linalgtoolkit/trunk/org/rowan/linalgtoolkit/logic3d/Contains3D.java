package org.rowan.linalgtoolkit.logic3d;

import org.rowan.linalgtoolkit.BoundingBox3D;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.shapes3d.*;

/**
 * The <code>Contains3D</code> class provides containment query logic for vertices
 * and shapes in the <code>shapes3d</code> package.
 *
 * @author Michael Liguori, Jonathan Palka, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public abstract class Contains3D {
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The number of decimal places considered during containment comparisons. */
    public static final int PRECISION = 10;
    

    /*********************************************
     * MARK: Point
     *********************************************/

    /**
     * Determines whether a given 3D vertex, in world coordinates, lies on a given
     * 3D point.
     *
     * @param point     The parent <code>Point3D</code>.
     * @param vertex    A <code>Vector3D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>point</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Point3D point, Vector3D vertex) {
        // compare point position vertex to given vertex
        return (point.getPosition().equals(vertex));
    }

    /**
     * Determines whether a given 3D point lies on another given 3D point.
     *
     * @param parent    The parent <code>Point3D</code>.
     * @param child     The child <code>Point3D</code>.
     * @return          <code>true</code> if <code>child</code> is wholly contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Point3D parent, Point3D child) {
        // compare point position vertices
        return (parent.getPosition().equals(child.getPosition()));
    }

    /*********************************************
     * MARK: Segment
     *********************************************/
    /**
     * Determines whether a given 3D vertex, in world coordinates, lies on a given
     * 3D line segment.
     *
     * @param segment   The parent <code>Segment3D</code>.
     * @param vertex    A <code>Vector3D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by the <code>segment</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Segment3D segment, Vector3D vertex) {
        // convert vertex to local coordinates
        vertex = segment.toLocal(vertex);

        // check if vertex is between segment enpoints
        return LinAlg3D.isBetween(vertex, segment.getStart(), segment.getEnd());
    }

    /**
     * Determines whether a given 3D line segment contains a given 3D point.
     *
     * @param segment   The parent <code>Segment3D</code>.
     * @param point     The child <code>Point3D</code>.
     * @return          <code>true</code> if <code>point</code> is wholly contained
     *                  by <code>segment</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Segment3D segment, Point3D point) {
        // check if point position vertex is contained by segment
        return contains(segment, point.getPosition());
    }

    /**
     * Determines whether a given 3D line segment contains another given 3D line
     * segment.
     *
     * @param parent    The parent <code>Segment3D</code>.
     * @param child     The child <code>Segment3D</code>.
     * @return          <code>true</code> if <code>child</code> is wholly contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Segment3D parent, Segment3D child) {
        // convert child endpoints to world coordinates
        Vector3D start = child.toWorld(child.getStart());
        Vector3D end = child.toWorld(child.getEnd());

        // check if both child segment endpoints are contained by parent segment
        return (contains(parent, start)
                && contains(parent, end));
    }

    /*********************************************
     * MARK: Cone3D
     *********************************************/

    /**
     * Determines whether a given 3D vertex, in world coordinates, lies within a
     * given 3D cone.
     *
     * @param cone      The parent <code>Cone3D</code>.
     * @param vertex    A <code>Vector3D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>cone</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cone3D cone, Vector3D vertex) {
        double precision = Math.pow(PRECISION, 10);
        //base local to apex
        Vector3D a = cone.getBaseCenter().subtract(cone.getApex());
        //vertex local to apex
        Vector3D b = vertex.subtract(cone.toWorld(cone.getApex()));
        //dot product
        double dot = a.dot(b);
        //height
        double h = cone.getBaseCenter().distance(cone.getApex());

        //See if the point is between the base and the coneApex planes.
        //dot product is negative if greater than 90s
        //longer than height^2, too far away from planes to be between
        //Could be floating point error. See if comparision is over the desired precision amount
        if ((dot > (h * h) && ((dot - (h * h)) / dot > 1/precision)) || dot < 0) {
            return false;
        }

        //angle between cone axis and vertex
        double arcCos = Math.acos(b.dot(a) / (b.magnitude() * a.magnitude()));

        //This is meant to truncate the values to give exclude floating point errors
        

        //angle of vertex less than or equal than angle of cone?
        return ((int)(arcCos * precision) <= (int)(cone.coneAngle() * precision));
         
    }

    /**
     * Determines whether a given 3D point, in world coordinates, lies within a
     * given 3D cone.
     *
     * @param cone      The parent <code>Cone3D</code>.
     * @param point     The child <code>Point3D</code>.
     *
     * @return          <code>true</code> if <code>point</code> is contained
     *                  by <code>cone</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cone3D cone, Point3D point) {

        return contains(cone, point.getPosition());
    }

    /**
     * Determines whether a given 3D segment lies within a given 3D cone.
     *
     * @param cone      The parent <code>Cone3D</code>.
     * @param segment   The child <code>Segment3D</code>.
     * @return          <code>true</code> if <code>segment</code> is contained
     *                  by <code>cone</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cone3D cone, Segment3D segment) {
        // convert child endpoints to world coordinates
        Vector3D start = segment.toWorld(segment.getStart());
        Vector3D end = segment.toWorld(segment.getEnd());

        // check if both segment endpoints are contained by the cone
        return (contains(cone, start) && contains(cone, end));
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by a 3D cone.
     *
     * @param cone        The parent <code>Cone3D</code>.
     * @param polyhedron  The child <code>Polyhedron3D</code>
     * @return            <code>true</code> if <code>polyhedron</code> is
     *                    contained by <code>cone</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Cone3D cone, Polyhedron3D polyhedron) {
        for (Vector3D vertice : polyhedron.getWorldVertices()) {
            if (!contains(cone, vertice)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether a given 3D cone is wholly contained by another given 3D cone.
     *
     * @param parent    The parent <code>Cone3D</code>.
     * @param child     The child <code>Cone3D</code>.
     * @return          <code>true</code> if <code>child</code> is contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cone3D parent, Cone3D child) {

        //apex inside?
        if (!contains(parent, child.toWorld(child.getApex()))) {
            return false;
        }

        //base center point inside?
        if (!contains(parent, child.toWorld(child.getBaseCenter()))) {
            return false;
        }

        //Further calculations that are not very costly have not been implemented
        //At this point we use the bounding box to determine containment.
        return parent.boundingBox().contains(child.boundingBox());
    }

    /**
     * Determines whether a given 3D cylinder is wholly contained by a 3D cone.
     *
     * @param cone      The parent <code>Cone3D</code>.
     * @param cylinder  The child <code>Cylinder3D</code>.
     * @return          <code>true</code> if <code>cylinder</code> is contained
     *                  by <code>cone</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cone3D cone, Cylinder3D cylinder) {

        //check to see if the apex is inside the cone
        if (!contains(cone, cylinder.toWorld(cylinder.getApexCenter()))) {
            return false;
        }
        //check to see if the base Center is inside the cone
        if (!contains(cone, cylinder.toWorld(cylinder.getBaseCenter()))) {
            return false;
        }

        //Further calculations that are not very costly have not been implemented
        //At this point we use the bounding box to determine containment.
        //bounding Box
        return cone.boundingBox().contains(cylinder.boundingBox());
    }

    /**
     * Determines whether a given 3D sphere is wholly contained by a 3D cone.
     *
     * @param cone        The parent <code>Cone3D</code>.
     * @param sphere      The child <code>Sphere3D</code>.
     * @return            <code>true</code> if <code>sphere</code> is
     *                    contained by <code>cone</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Cone3D cone, Sphere3D sphere) {
        //Use Bounding Boxes for containment
        return cone.boundingBox().contains(sphere.boundingBox());
    }

    /**
     * Determines whether a given 3D spheroid is wholly contained by a 3D cone.
     *
     * @param cone        The parent <code>Cone3D</code>.
     * @param spheroid    The child <code>Spheroid3D</code>.
     * @return            <code>true</code> if <code>spheroid</code> is
     *                    contained by <code>cone</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Cone3D cone, Spheroid3D spheroid) {

        //Use Bounding Boxes for containment
        return cone.boundingBox().contains(spheroid.boundingBox());
    }

    /**
     * Determines whether a given 3D ellipsoid is wholly contained by a 3D cone
     *
     * @param cone        The parent <code>Cone3D</code>.
     * @param ellipsoid   The child <code>Ellipsoid3D</code>.
     * @return            <code>true</code> if <code>ellipsoid</code> is
     *                    contained by <code>cone</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Cone3D cone, Ellipsoid3D ellipsoid) {

        //Use Bounding Boxes for containment
        return cone.boundingBox().contains(ellipsoid.boundingBox());
    }

    /*********************************************
     * MARK: Cylinder3D
     *********************************************/

    /**
     * Determines whether a given 3D vertex, in world coordinates, lies within a
     * given 3D cylinder.
     *
     * @param cylinder  The parent <code>Cylinder3D</code>.
     * @param vertex    A <code>Vector3D</code> describing the vertex in question
     *                  in world coordinates.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>cylinder</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cylinder3D cylinder, Vector3D vertex) {
        double PRECISION = Math.pow(10, 10);
        //apex local to base
        Vector3D a = cylinder.getApexCenter().subtract(cylinder.getBaseCenter());
        //vertex local to base
        Vector3D b = vertex.subtract(cylinder.toWorld(cylinder.getBaseCenter()));
        double dot = a.dot(b);
        double h = cylinder.getBaseCenter().distance(cylinder.getApexCenter());

        //See if the point is between the base and the coneApex.
        //Could be floating point error. See if comparision is over the desired precision amount
        if ((dot > (h * h) && ((dot - (h * h)) / dot > 1/PRECISION)) || dot < 0) {
            return false;
        }

        //distance to cylinder axis
        double distToCylinderAxis = (b.dot(b)) - ((dot * dot) / (h * h));

        //coneApex radius is smaller. If in that radius then its
        //true for truncated and regular cylinders
        if (distToCylinderAxis <= (cylinder.getApexRadius() * cylinder.getApexRadius())) {
            return true;
        }

        //beyond larger radius, makes false for trunacted
        if (distToCylinderAxis > (cylinder.getBaseRadius() * cylinder.getBaseRadius())) {
            return false;
        }

        //Truncated Cone, between the two radius
        //find theoredical coneApex height, angle
        double coneHeight = h + ((h * cylinder.getApexRadius()) / (cylinder.getBaseRadius() - cylinder.getApexRadius()));

        //extends truncated cylinder to a cone coneApex
        Vector3D coneApex = cylinder.getBaseCenter().add(a.unitVector().multiply(coneHeight));
        Vector3D vectorLocalToApex = vertex.subtract(coneApex);
        Vector3D baseLocalToApex = cylinder.toWorld(cylinder.getBaseCenter()).subtract(coneApex);

        //angle between cylinder axis and truncated side
        double coneAngle = Math.atan(cylinder.getBaseRadius() / coneHeight);
        //angle between cylinder axis and vector
        double arcCos = Math.acos(vectorLocalToApex.dot(baseLocalToApex) / (vectorLocalToApex.magnitude() * baseLocalToApex.magnitude()));

        //This is meant to truncate the values to give exclude floating point errors
        //How many decimal places past the decimal times 10

        //angle of truncated side less than angle to vertex?
        return ((int)(arcCos * PRECISION) <= (int)(coneAngle * PRECISION));
        
    }

    /**
     * Determines whether a given 3D point, lies within a given 3D cylinder.
     *
     * @param cylinder  The parent <code>Cylinder3D</code>.
     * @param point     The child <code>Point3D</code>.
     * @return          <code>true</code> if <code>point</code> is contained
     *                  by <code>cylinder</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cylinder3D cylinder, Point3D point) {

        return contains(cylinder, point.getPosition());
    }

    /**
     * Determines whether a given 3D segment lies within a given 3D cylinder.
     *
     * @param cylinder  The parent <code>Cylinder3D</code>.
     * @param segment   The child <code>Segment3D</code>.
     * @return          <code>true</code> if <code>segment</code> is contained
     *                  by <code>cylinder</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cylinder3D cylinder, Segment3D segment) {
        // convert child endpoints to world coordinates
        Vector3D start = segment.toWorld(segment.getStart());
        Vector3D end = segment.toWorld(segment.getEnd());

        // check if both segment endpoints are contained by the cylinder
        return (contains(cylinder, start) && contains(cylinder, end));
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by a 3D cylinder.
     *
     * @param cylinder    The parent <code>Cylinder3D</code>.
     * @param polyhedron  The child <code>Polyhedron3D</code>.
     * @return            <code>true</code> if <code>polyhedron</code> is contained
     *                    by <code>cylinder</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cylinder3D cylinder, Polyhedron3D polyhedron) {
        for (Vector3D vertice : polyhedron.getWorldVertices()) {
            if (!contains(cylinder, vertice)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether a given 3D cone is wholly contained by a 3D cylinder.
     *
     * @param cylinder  The parent <code>Cylinder3D</code>.
     * @param cone      The child <code>Cone3D</code>
     * @return          <code>true</code> if <code>cone</code> is contained
     *                  by <code>cylinder</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cylinder3D cylinder, Cone3D cone) {

        //apex inside?
        if (!contains(cylinder, cone.toWorld(cone.getApex()))) {
            return false;
        }

        //base center point inside?
        if (!contains(cylinder, cone.toWorld(cone.getBaseCenter()))) {
            return false;
        }

        //cylinder bounding box contain cone bounding box?
        return cylinder.boundingBox().contains(cone.boundingBox());

    }

    /**
     * Determines whether a given 3D cylinder is wholly contained by another given
     * 3D cylinder.
     *
     * @param parent    The parent <code>Cylinder3D</code>.
     * @param child     The child <code>Cylinder3D</code>.
     * @return          <code>true</code> if <code>child</code> is contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Cylinder3D parent, Cylinder3D child) {

        //apex inside?
        if (!contains(parent, child.toWorld(child.getApexCenter()))) {
            return false;
        }

        //base center point inside?
        if (!contains(parent, child.toWorld(child.getBaseCenter()))) {
            return false;
        }

        //Bounding Boxes
        //cylinder bounding box contain cone bounding box?
        return parent.boundingBox().contains(child.boundingBox());
    }

    /**
     * Determines whether a given 3D cylinder is wholly contained by a 3D cone.
     *
     * @param cylinder    The parent <code>Cylinder3D</code>.
     * @param sphere      The child <code>Sphere3D</code>.
     * @return            <code>true</code> if <code>sphere</code> is
     *                    contained by <code>cylinder</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Cylinder3D cylinder, Sphere3D sphere) {

        //Use Bounding Boxes for containment
        return cylinder.boundingBox().contains(sphere.boundingBox());
    }

    /**
     * Determines whether a given 3D cylinder is wholly contained by a 3D cone.
     *
     * @param cylinder    The parent <code>Cylinder3D</code>.
     * @param spheroid    The child <code>Spheroid3D</code>.
     * @return            <code>true</code> if <code>spheroid</code> is
     *                    contained by <code>cylinder</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Cylinder3D cylinder, Spheroid3D spheroid) {

        //Use Bounding Boxes for containment
        return cylinder.boundingBox().contains(spheroid.boundingBox());
    }

    /**
     * Determines whether a given 3D cylinder is wholly contained by a 3D cone.
     *
     * @param cylinder    The parent <code>Cylinder3D</code>.
     * @param ellipsoid   The child <code>Ellipsoid3D</code>.
     * @return            <code>true</code> if <code>ellipsoid</code> is
     *                    contained by <code>cylinder</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Cylinder3D cylinder, Ellipsoid3D ellipsoid) {

        //Use Bounding Boxes for containment
        return cylinder.boundingBox().contains(ellipsoid.boundingBox());
    }

    /*********************************************
     * MARK: Polyhedron3D
     *********************************************/

    /**
     * Determines whether a given 3D vertex, in world coordinates, lies within a
     * given 3D polyhedron.
     *
     * @param polyhedron The parent <code>Polyhedron3D</code>.
     * @param vertex     A <code>Vector3D</code> describing the vertex in question
     *                   in world coordinates.
     * @return           <code>true</code> if <code>vertex</code> is contained
     *                   by <code>polyhedron</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polyhedron3D polyhedron, Vector3D vertex) {
        for (Face3D face : polyhedron.getFaces()) {
            if (!face.isBehind(vertex)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether a given 3D point, lies within a given 3D polyhedron.
     *
     * @param polyhedron  The parent <code>Polyhedron3D</code>.
     * @param point       The child <code>Point3D</code>.
     * @return            <code>true</code> if <code>point</code> is contained
     *                    by <code>polyhedron</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polyhedron3D polyhedron, Point3D point) {

        return contains(polyhedron, point.getPosition());
    }

    /**
     * Determines whether a given 3D segment lies within a given 3D polyhedron.
     *
     * @param polyhedron  The parent <code>Polyhedron3D</code>.
     * @param segment     The child <code>Segment3D</code>.
     * @return            <code>true</code> if <code>segment</code> is contained
     *                    by <code>polyhedron</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polyhedron3D polyhedron, Segment3D segment) {
        // convert child endpoints to world coordinates
        Vector3D start = segment.toWorld(segment.getStart());
        Vector3D end = segment.toWorld(segment.getEnd());

        // check if both child segment endpoints are contained by the polyhedron
        return (contains(polyhedron, start) && contains(polyhedron, end));
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by another given
     * 3D polyhedron.
     *
     * @param parent    The parent <code>Polyhedron3D</code>.
     * @param child     The child <code>Polyhedron3D</code>.
     * @return          <code>true</code> if <code>child</code> is contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polyhedron3D parent, Polyhedron3D child) {
        for (Vector3D vertex : child.getVertices()) {
            if (!contains(parent, vertex)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether a given 3D Cone lies within a given 3D polyhedron.
     * Currently, this method checks to see if the cylinders apex and base center point
     * are inside. Then the bounding boxes are used to determine containment since
     * further calculations have not been implemented.
     * @param polyhedron  The parent <code>Polyhedron3D</code>.
     * @param cone        The child <code>Cone3D</code>.
     * @return            <code>true</code> if <code>cone</code> is contained
     *                    by <code>polyhedron</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polyhedron3D polyhedron, Cone3D cone) {
        //cone apex inside?
        if (!contains(polyhedron, cone.toWorld(cone.getApex()))) {
            return false;
        }

        //cone base center inside?
        if (!contains(polyhedron, cone.toWorld(cone.getBaseCenter()))) {
            return false;
        }

        //Bounding Box
        BoundingBox3D boundingBoxPolyhedron = polyhedron.boundingBox();
        BoundingBox3D boundingBoxCone = cone.boundingBox();

        return boundingBoxPolyhedron.contains(boundingBoxCone);
    }

    /**
     * Determines whether a given 3D Cylinder lies within a given 3D polyhedron.
     * Currently, this method checks to see if the cylinders apex and base center point
     * are inside. Then the bounding boxes are used to determine containment since
     * further calculations have not been implemented.
     *
     * @param polyhedron  The parent <code>Polyhedron3D</code>.
     * @param cylinder    The child <code>Cylinder3D</code>.
     * @return            <code>true</code> if <code>cylinder</code> is contained
     *                    by <code>polyhedron</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Polyhedron3D polyhedron, Cylinder3D cylinder) {

        //cylinder apex inside?
        if (!contains(polyhedron, cylinder.toWorld(cylinder.getApexCenter()))) {
            return false;
        }

        //cylinder base center inside?
        if (!contains(polyhedron, cylinder.toWorld(cylinder.getBaseCenter()))) {
            return false;
        }

        //Bounding Box
        return polyhedron.boundingBox().contains(cylinder.boundingBox());
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by a 3D cone.
     *
     * @param polyhedron  The parent <code>Polyhedron3D</code>.
     * @param sphere      The child <code>Sphere3D</code>.
     * @return            <code>true</code> if <code>sphere</code> is
     *                    contained by <code>cylinder</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Polyhedron3D polyhedron, Sphere3D sphere) {

        //Use Bounding Boxes for containment
        return polyhedron.boundingBox().contains(sphere.boundingBox());
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by a 3D cone.
     *
     * @param polyhedron    The parent <code>Polyhedron3D</code>.
     * @param spheroid    The child <code>Spheroid3D</code>.
     * @return            <code>true</code> if <code>spheroid</code> is
     *                    contained by <code>polyhedron</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Polyhedron3D polyhedron, Spheroid3D spheroid) {

        //Use Bounding Boxes for containment
        return polyhedron.boundingBox().contains(spheroid.boundingBox());
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by a 3D cone.
     *
     * @param polyhedron  The parent <code>Polyhedron3D</code>.
     * @param ellipsoid   The child <code>Ellipsoid3D</code>.
     * @return            <code>true</code> if <code>ellipsoid</code> is
     *                    contained by <code>polyhedron</code>; <code>false</code>
     *                    otherwise.
     */
    public static boolean contains(Polyhedron3D polyhedron, Ellipsoid3D ellipsoid) {

        //Use Bounding Boxes for containment
        return polyhedron.boundingBox().contains(ellipsoid.boundingBox());
    }

    /*********************************************
     * MARK: Sphere3D
     *********************************************/
    /**
     * Determines whether a given 3D point, in world coordinates, lies within a
     * given 3D sphere.
     *
     * @param sphere    The parent <code>Sphere3D</code>.
     * @param vertex    The child  <code>Vertex3D</code>.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>sphere</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D sphere, Vector3D vertex) {

        return (sphere.getPosition().distance(vertex) <= sphere.getMajorRadius());
    }

    /**
     * Determines whether a given 3D point, in world coordinates, lies within a
     * given 3D sphere.
     *
     * @param sphere    The parent <code>Sphere3D</code>.
     * @param point     The child  <code>Point3D</code>.
     * @return          <code>true</code> if <code>point</code> is contained
     *                  by <code>sphere</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D sphere, Point3D point) {

        return contains(sphere, point.getPosition());
    }

    /**
     * Determines whether a given 3D segment lies within a given 3D sphere.
     *
     * @param sphere   The parent <code>Sphere3D</code>.
     * @param segment  The child  <code>Segment3D</code>.
     * @return          <code>true</code> if <code>segment</code> is contained
     *                  by <code>sphere</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D sphere, Segment3D segment) {
        //End Points
        Vector3D start = segment.toWorld(segment.getStart());
        Vector3D end = segment.toWorld(segment.getEnd());

        //both endpoints contained?
        return (contains(sphere, start) && contains(sphere, end));
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by a 3D sphere.
     *
     * @param sphere      The parent <code>Sphere3D</code>.
     * @param polyhedron  The child  <code>Polyhedron3D</code>.
     * @return          <code>true</code> if <code>polyhedron</code> is contained
     *                  by <code>sphere</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D sphere, Polyhedron3D polyhedron) {
        //all points of polyhedron contained?
        for (Vector3D v : polyhedron.getWorldVertices()) {
            if (!contains(sphere, v)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether a given 3D cone is wholly contained by a 3D sphere.
     *
     * @param sphere    The parent <code>Sphere3D</code>.
     * @param cone      The child  <code>Cone3D</code>.
     * @return          <code>true</code> if <code>cone</code> is contained
     *                  by <code>sphere</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D sphere, Cone3D cone) {

        //cone apex inside?
        if (!contains(sphere, cone.toWorld(cone.getApex()))) {
            return false;
        }

        //cone base center inside?
        if (!contains(sphere, cone.toWorld(cone.getBaseCenter()))) {
            return false;
        }

        //Bounding Box
        return sphere.boundingBox().contains(cone.boundingBox());
    }

    /**
     * Determines whether a given 3D cylinder is wholly contained by a 3D sphere.
     *
     * @param sphere    The parent <code>Sphere3D</code>.
     * @param cylinder  The child  <code>Cylinder3D</code>.
     * @return          <code>true</code> if <code>cylinder</code> is contained
     *                  by <code>sphere</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D sphere, Cylinder3D cylinder) {
        //cylinder apex inside?
        if (!contains(sphere, cylinder.toWorld(cylinder.getApexCenter()))) {
            return false;
        }

        //cylinder base center inside?
        if (!contains(sphere, cylinder.toWorld(cylinder.getBaseCenter()))) {
            return false;
        }

        //Bounding Box
        return sphere.boundingBox().contains(cylinder.boundingBox());
    }

    /**
     * Determines whether a given 3D sphere is wholly contained by another 3D
     * sphere.
     *
     * @param parent    The parent <code>Sphere3D</code>.
     * @param child     The child  <code>Sphere3D</code>.
     * @return          <code>true</code> if <code>child</code> is contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D parent, Sphere3D child) {

        //Use Bounding Boxes for containment
        return parent.boundingBox().contains(child.boundingBox());
    }

    /**
     * Determines whether a given 3D spheroid is wholly contained by a 3D sphere.
     *
     * @param sphere    The parent <code>Sphere3D</code>.
     * @param spheroid  The child  <code>Spheroid3D</code>.
     * @return          <code>true</code> if <code>spheroid</code> is contained
     *                  by <code>sphere</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D sphere, Spheroid3D spheroid) {

        //Use Bounding Boxes for containment
        return sphere.boundingBox().contains(spheroid.boundingBox());
    }

    /**
     * Determines whether a given 3D ellipsoid is wholly contained by a 3D sphere.
     *
     * @param sphere     The parent <code>Sphere3D</code>.
     * @param ellipsoid  The child  <code>Ellipsoid</code>.
     * @return          <code>true</code> if <code>ellipsoid</code> is contained
     *                  by <code>sphere</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Sphere3D sphere, Ellipsoid3D ellipsoid) {

        //Use Bounding Boxes for containment
        return sphere.boundingBox().contains(ellipsoid.boundingBox());
    }

    /*********************************************
     * MARK: Spheroid3D
     *********************************************/
    /**
     * Determines whether a given 3D vector, in world coordinates, lies within a
     * given 3D spheroid.
     *
     * @param spheroid  The parent <code>Spheroid3D</code>.
     * @param vertex    The child  <code>Vector3D</code>.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>spheroid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D spheroid, Vector3D vertex) {

        //Use Bounding Boxes for containment
        return spheroid.boundingBox().contains(vertex);
    }

    /**
     * Determines whether a given 3D point, in world coordinates, lies within a
     * given 3D spheroid.
     *
     * @param spheroid  The parent <code>Spheroid3D</code>.
     * @param point     The child  <code>Point3D</code>.
     * @return          <code>true</code> if <code>point</code> is contained
     *                  by <code>spheroid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D spheroid, Point3D point) {

        return contains(spheroid, point.getPosition());
    }

    /**
     * Determines whether a given 3D segment lies within a given 3D spheroid.
     *
     * @param spheroid  The parent <code>Spheroid3D</code>.
     * @param segment   The child  <code>Segment3D</code>.
     * @return          <code>true</code> if <code>segment</code> is contained
     *                  by <code>spheroid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D spheroid, Segment3D segment) {
        //end points
        Vector3D start = segment.toWorld(segment.getStart());
        Vector3D end = segment.toWorld(segment.getEnd());

        //both end points inside?
        return (contains(spheroid, start) && contains(spheroid, end));
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by a 3D spheroid.
     *
     * @param spheroid   The parent <code>Spheroid3D</code>.
     * @param polyhedron The child  <code>Polyhedron3D</code>.
     * @return          <code>true</code> if <code>polyhedron</code> is contained
     *                  by <code>spheroid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D spheroid, Polyhedron3D polyhedron) {
        //all points of polyhedron contained?
        for (Vector3D v : polyhedron.getWorldVertices()) {
            if (!contains(spheroid, v)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether a given 3D cone is wholly contained by a 3D spheroid.
     *
     * @param spheroid  The parent <code>Spheroid3D</code>.
     * @param cone      The child  <code>Cone3D</code>.
     * @return          <code>true</code> if <code>cone</code> is contained
     *                  by <code>spheroid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D spheroid, Cone3D cone) {
        //cone apex inside?
        if (!contains(spheroid, cone.toWorld(cone.getApex()))) {
            return false;
        }

        //cone base center inside?
        if (!contains(spheroid, cone.toWorld(cone.getBaseCenter()))) {
            return false;
        }

        //Bounding Box
        return spheroid.boundingBox().contains(cone.boundingBox());
    }

    /**
     * Determines whether a given 3D cylinder is wholly contained by a 3D spheroid.
     *
     * @param spheroid  The parent <code>Spheroid3D</code>.
     * @param cylinder  The child  <code>Cylinder3D</code>.
     * @return          <code>true</code> if <code>cylinder</code> is contained
     *                  by <code>spheroid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D spheroid, Cylinder3D cylinder) {

        //cylinder apex inside?
        if (!contains(spheroid, cylinder.toWorld(cylinder.getApexCenter()))) {
            return false;
        }

        //cylinder base center inside?
        if (!contains(spheroid, cylinder.toWorld(cylinder.getBaseCenter()))) {
            return false;
        }

        //Bounding Box
        return spheroid.boundingBox().contains(cylinder.boundingBox());
    }

    /**
     * Determines whether a given 3D sphere is wholly contained by a 3D spheroid.
     *
     * @param spheroid  The parent <code>Spheroid3D</code>.
     * @param sphere    The child  <code>Sphere3D</code>.
     * @return          <code>true</code> if <code>sphere</code> is contained
     *                  by <code>spheroid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D spheroid, Sphere3D sphere) {

        //Use Bounding Boxes for containment
        return spheroid.boundingBox().contains(sphere.boundingBox());
    }

    /**
     * Determines whether a given 3D spheroid is wholly contained by another 3D
     * spheroid.
     *
     * @param parent    The parent <code>Spheroid3D</code>.
     * @param child     The child  <code>Spheroid3D</code>.
     * @return          <code>true</code> if <code>child</code> is contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D parent, Spheroid3D child) {

        //Use Bounding Boxes for containment
        return  parent.boundingBox().contains(child.boundingBox());
    }

    /**
     * Determines whether a given 3D ellipsoid is wholly contained by a 3D spheroid.
     *
     * @param spheroid   The parent <code>Spheroid3D</code>.
     * @param ellipsoid  The child  <code>Ellipsoid3D</code>.
     * @return          <code>true</code> if <code>ellipsoid</code> is contained
     *                  by <code>spheroid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Spheroid3D spheroid, Ellipsoid3D ellipsoid) {

        //Use Bounding Boxes for containment
        return spheroid.boundingBox().contains(ellipsoid.boundingBox());
    }

    /*********************************************
     * MARK: Ellipsoid3D
     *********************************************/
    /**
     * Determines whether a given 3D vector, in world coordinates, lies within a
     * given 3D ellipsoid.
     *
     * @param ellipsoid The parent <code>Ellipsoid3D</code>.
     * @param vertex    The child  <code>Vector3D</code>.
     * @return          <code>true</code> if <code>vertex</code> is contained
     *                  by <code>ellipsoid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D ellipsoid, Vector3D vertex) {

        //Use Bounding Boxes for containment
        return ellipsoid.boundingBox().contains(vertex);
    }

    /**
     *Determines whether a given 3D point, in world coordinates, lies within a
     * given 3D ellipsoid.
     *
     * @param ellipsoid The parent <code>Ellipsoid3D</code>.
     * @param point     The child  <code>Point3D</code>.
     * @return          <code>true</code> if <code>point</code> is contained
     *                  by <code>ellipsoid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D ellipsoid, Point3D point) {

        return contains(ellipsoid, point.getPosition());
    }

    /**
     * Determines whether a given 3D segment lies within a given 3D ellipsoid.
     *
     * @param ellipsoid The parent <code>Ellipsoid3D</code>.
     * @param segment   The child  <code>Segment3D</code>.
     * @return          <code>true</code> if <code>segment</code> is contained
     *                  by <code>ellipsoid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D ellipsoid, Segment3D segment) {
        //end points
        Vector3D start = segment.toWorld(segment.getStart());
        Vector3D end = segment.toWorld(segment.getEnd());

        //both end points inside?
        return (contains(ellipsoid, start) && contains(ellipsoid, end));
    }

    /**
     * Determines whether a given 3D polyhedron is wholly contained by a 3D ellipsoid.
     *
     * @param ellipsoid     The parent <code>Ellipsoid3D</code>.
     * @param polyhedron    The child  <code>Polyhedron3D</code>.
     * @return              <code>true</code> if <code>polyhedron</code> is contained
     *                      by <code>ellipsoid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D ellipsoid, Polyhedron3D polyhedron) {
        //all points of polyhedron contained?
        for (Vector3D v : polyhedron.getWorldVertices()) {
            if (!contains(ellipsoid, v)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether a given 3D cone is wholly contained by a 3D ellipsoid.
     *
     * @param ellipsoid The parent <code>Ellipsoid3D</code>.
     * @param cone      The child  <code>Cone3D</code>.
     * @return          <code>true</code> if <code>cone</code> is contained
     *                  by <code>ellipsoid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D ellipsoid, Cone3D cone) {

        //cone apex inside?
        if (!contains(ellipsoid, cone.toWorld(cone.getApex()))) {
            return false;
        }

        //cone base center inside?
        if (!contains(ellipsoid, cone.toWorld(cone.getBaseCenter()))) {
            return false;
        }

        //Bounding Box
        return ellipsoid.boundingBox().contains(cone.boundingBox());
    }

    /**
     * Determines whether a given 3D cylinder is wholly contained by a 3D ellipsoid.
     *
     * @param ellipsoid The parent <code>Ellipsoid3D</code>.
     * @param cylinder  The child  <code>Cylinder3D</code>.
     * @return          <code>true</code> if <code>cylinder</code> is contained
     *                  by <code>ellipsoid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D ellipsoid, Cylinder3D cylinder) {

        //cylinder apex inside?
        if (!contains(ellipsoid, cylinder.toWorld(cylinder.getApexCenter()))) {
            return false;
        }

        //cylinder base center inside?
        if (!contains(ellipsoid, cylinder.toWorld(cylinder.getBaseCenter()))) {
            return false;
        }

        //Bounding Box
        return ellipsoid.boundingBox().contains(cylinder.boundingBox());
    }

    /**
     * Determines whether a given 3D sphere is wholly contained by a 3D ellipsoid.
     *
     * @param ellipsoid The parent <code>Ellipsoid3D</code>.
     * @param sphere    The child  <code>Sphere3D</code>.
     * @return          <code>true</code> if <code>sphere</code> is contained
     *                  by <code>ellipsoid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D ellipsoid, Sphere3D sphere) {

        //Use Bounding Boxes for containment
        return ellipsoid.boundingBox().contains(sphere.boundingBox());
    }

    /**
     * Determines whether a given 3D spheroid is wholly contained by a 3D ellipsoid.
     *
     * @param ellipsoid The parent <code>Ellipsoid3D</code>.
     * @param spheroid  The child  <code>Spheroid3D</code>.
     * @return          <code>true</code> if <code>spheroid</code> is contained
     *                  by <code>ellipsoid</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D ellipsoid, Spheroid3D spheroid) {

        //Use Bounding Boxes for containment
        return ellipsoid.boundingBox().contains(spheroid.boundingBox());
    }

    /**
     * Determines whether a given 3D ellipsoid is wholly contained by another 3D
     * ellipsoid.
     *
     * @param parent    The parent <code>Ellipsoid3D</code>.
     * @param child     The child  <code>Ellipsoid3D</code>.
     * @return          <code>true</code> if <code>child</code> is contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     */
    public static boolean contains(Ellipsoid3D parent, Ellipsoid3D child) {

        //Use Bounding Boxes for containment
        return parent.boundingBox().contains(child.boundingBox());
    }
}
