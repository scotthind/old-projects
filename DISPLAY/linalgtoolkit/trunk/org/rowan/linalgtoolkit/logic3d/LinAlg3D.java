package org.rowan.linalgtoolkit.logic3d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.WGS84Coord;
import org.rowan.linalgtoolkit.WGS84Coord.DistanceMode;
import org.rowan.linalgtoolkit.shapes3d.*;
import org.rowan.coordconversion.*;

/**
 * The <code>LinAlg3D</code> class provides implementation of various linear algebra
 * based computations for three dimensional space. These computations include: the
 * distance between two 3D shapes based on WGS coordinates on the globe,
 * whether a moving 3D shape will intersect a stationary 3D shape in a given
 * amount of time, and whether a 3D shape wholly contains another 3D shape.
 * <p>
 *
 * Containment is defined as:
 * <ul><li>Every point on the child shape is inside the parent shape.</li>
 * <li>A point is inside the parent if the point is in the interior or lies on one of
 * the vertices or edges of the parent.</li></ul></p>
 * <p>
 *
 * @author Spence DiNicolantonio, Michael Liguori, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public abstract class LinAlg3D {

     /*********************************************
     * MARK: Contains
     *********************************************/

    /**
     * Determines whether a given 3D vertex, in world coordinates, lies within
     * a given 3D shape.
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Polyhedron3D</code>,
     * <code>Cone3D</code>, <code>Cylinder3D</code>, <code>Ellipsoid3D</code>
     * and all subclasses of these.
     *
     * Certain Shapes currently have contains methods which apply the bounding box
     * containment of these shapes. Shapes which currently apply this method are
     * <code>Ellipsoid3D</code>.
     *
     * @param shape     A <code>Shape3D</code>.
     * @param vertex    A <code>Vector3D</code> describing the vertex in question.
     * @return          <code>true</code> if <code>vertex</code> is wholly contained
     *                  by <code>shape</code>; <code>false</code> otherwise.
     * @throws IllegalArgumentException If the given <code>Shape3D</code> instance
     *                  is not supported.
     */
    public static boolean contains(Shape3D shape, Vector3D vertex) {
        // point?
        if (shape instanceof Point3D)
            return Contains3D.contains((Point3D)shape, vertex);

        // line segment?
        if (shape instanceof Segment3D)
            return Contains3D.contains((Segment3D)shape, vertex);

        // cylinder?
        if (shape instanceof Cylinder3D)
            return Contains3D.contains((Cylinder3D)shape, vertex);

        // cone?
        if (shape instanceof Cone3D)
            return Contains3D.contains((Cone3D)shape, vertex);

        // Polyhedron?
        if (shape instanceof Polyhedron3D)
            return Contains3D.contains((Polyhedron3D)shape, vertex);

        // Ellipsoid?
        if (shape instanceof Ellipsoid3D)
            return Contains3D.contains((Ellipsoid3D)shape, vertex);

        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }

    /**
     * Determines whether a given child shape, is wholly contained by a given
     * parent shape.
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     *
     * Certain Shapes currently have contains methods which apply the bounding box
     * containment of both shapes. Shapes which currently apply this method is
     * <code>Cone3D</code>, <code>Cylinder3D</code>, <code>Ellipsoid3D</code>.
     *
     *
     * @param parent    The parent <code>Shape3D</code>.
     * @param child     The child <code>Shape3D</code>.
     * @return          <code>true</code> if <code>child</code> is wholly contained
     *                  by <code>parent</code>; <code>false</code> otherwise.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported.
     */
     public static boolean contains(Shape3D parent, Shape3D child) {
        // point?
        if (parent instanceof Point3D) {

            // point-point
            if (child instanceof Point3D)
                return Contains3D.contains((Point3D)parent, (Point3D)child);

            // point-segment
            if (child instanceof Segment3D)
                return false; // false by definition

            // point-cone
            if (child instanceof Cone3D)
                return false; // false by definition

            // point-cylinder
            if (child instanceof Cylinder3D)
                return false; // false by definition

            // point-polyhedron
            if (child instanceof Polyhedron3D)
                return false; // false by definition

            // point-ellipsoid
            if (child instanceof Ellipsoid3D)
                return false; // false by definition

            // point-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // line segment?
        if (parent instanceof Segment3D) {

            // segment-point
            if (child instanceof Point3D)
                return Contains3D.contains((Segment3D)parent, (Point3D)child);

            // segment-segment
            if (child instanceof Segment3D)
                return Contains3D.contains((Segment3D)parent, (Segment3D)child);

            // segment-cone
            if (child instanceof Cone3D)
                return false; // false by definition

            // segment-polygon
            if (child instanceof Cylinder3D)
                return false; // false by definition

            // segment-polyhedron
            if (child instanceof Polyhedron3D)
                return false; // false by definition

            // segment-ellipsoid
            if (child instanceof Ellipsoid3D)
                return false; // false by definition

            // segment-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // cone?
        if (parent instanceof Cone3D) {

            // cone-point
            if (child instanceof Point3D)
                return Contains3D.contains((Cone3D)parent, (Point3D)child);

            // cone-segment
            if (child instanceof Segment3D)
                return Contains3D.contains((Cone3D)parent, (Segment3D)child);

            // cone-cone
            if (child instanceof Cone3D)
                return Contains3D.contains((Cone3D)parent, (Cone3D)child);

            // cone-cylinder
            if (child instanceof Cylinder3D)
                return Contains3D.contains((Cone3D)parent, (Cylinder3D)child);

            // cone-polyhedron
            if (child instanceof Polyhedron3D)
                return Contains3D.contains((Cone3D)parent, (Polyhedron3D)child);

            // cone-ellipsoid
            if (child instanceof Ellipsoid3D)
                return Contains3D.contains((Cone3D)parent, (Ellipsoid3D)child);


            // cone-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // cylinder?
        if (parent instanceof Cylinder3D) {

            // cylinder-point
            if (child instanceof Point3D)
                return Contains3D.contains((Cylinder3D)parent, (Point3D)child);

            // cylinder-segment
            if (child instanceof Segment3D)
                return Contains3D.contains((Cylinder3D)parent, (Segment3D)child);

            // cylinder-cone
            if (child instanceof Cone3D)
                return Contains3D.contains((Cylinder3D)parent, (Cone3D)child);

            // cylinder-cylinder
            if (child instanceof Cylinder3D)
                return Contains3D.contains((Cylinder3D)parent, (Cylinder3D)child);

            // cylinder-polyhedron
            if (child instanceof Polyhedron3D)
                return Contains3D.contains((Cylinder3D)parent, (Polyhedron3D)child);

            // cylinder-ellipsoid
            if (child instanceof Ellipsoid3D)
                return Contains3D.contains((Cylinder3D)parent, (Ellipsoid3D)child);

            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // polyhedron?
        if (parent instanceof Polyhedron3D) {

            // polyhedron-point
            if (child instanceof Point3D)
                return Contains3D.contains((Polyhedron3D)parent, (Point3D)child);

            // polyhedron-segment
            if (child instanceof Segment3D)
                return Contains3D.contains((Polyhedron3D)parent, (Segment3D)child);

            // polyhedron-cone
            if (child instanceof Cone3D)
                return Contains3D.contains((Polyhedron3D)parent, (Cone3D)child);

            // polyhedron-cylinder
            if (child instanceof Cylinder3D)
                return Contains3D.contains((Polyhedron3D)parent, (Cylinder3D)child);

            // polyhedron-polyhedron
            if (child instanceof Polyhedron3D)
                return Contains3D.contains((Polyhedron3D)parent, (Polyhedron3D)child);

            // polyhedron-ellipsoid
            if (child instanceof Ellipsoid3D)
                return Contains3D.contains((Polyhedron3D)parent, (Ellipsoid3D)child);

            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // ellipsoid?
        if (parent instanceof Ellipsoid3D) {

            // ellipsoid-point
            if (child instanceof Point3D)
                 return Contains3D.contains((Ellipsoid3D)parent, (Point3D)child);

            // ellipsoid-segment
            if (child instanceof Segment3D)
                 return Contains3D.contains((Ellipsoid3D)parent, (Segment3D)child);

            // ellipsoid-cone
            if (child instanceof Cone3D)
                 return Contains3D.contains((Ellipsoid3D)parent, (Cone3D)child);

            // ellipsoid-cylinder
            if (child instanceof Cylinder3D)
                 return Contains3D.contains((Ellipsoid3D)parent, (Cylinder3D)child);

            // ellipsoid-polyhedron
            if (child instanceof Polyhedron3D)
                 return Contains3D.contains((Ellipsoid3D)parent, (Polyhedron3D)child);

            // ellipsoid-ellipsoid
            if (child instanceof Ellipsoid3D)
                return Contains3D.contains((Ellipsoid3D)parent, (Ellipsoid3D)child);

            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // unknown
        throw new IllegalArgumentException("Unsupported parent Shape3D instance");
     }
    
    
    /*********************************************
     * MARK: Distance
     *********************************************/
    
    /**
     * Computes the distance between a given shape and vertex.
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shapes'
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shape's hull.
     * @param shape     A <code>Shape3D</code>.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @return          The distance between <code>shape</code> and <code>vertex</code>.
     * @throws IllegalArgumentException If the given <code>Shape3D</code> instance 
     *                  is not supported.
     */
    public static double distance(Shape3D shape, Vector3D vertex) {
        // point?
        if (shape instanceof Point3D)
            return Distance3D.distance((Point3D)shape, vertex);
        
        // line segment?
        if (shape instanceof Segment3D)
            return Distance3D.distance((Segment3D)shape, vertex);
        
        // cone?
        if (shape instanceof Cone3D)
            return Distance3D.distance((Cone3D)shape, vertex);
        
        // cylinder?
        if (shape instanceof Cylinder3D)
            return Distance3D.distance((Cylinder3D)shape, vertex);
        
        // polyhedron?
        if (shape instanceof Polyhedron3D)
            return Distance3D.distance((Polyhedron3D)shape, vertex);
        
        // ellipsoid?
        if (shape instanceof Ellipsoid3D)
            return Distance3D.distance((Ellipsoid3D)shape, vertex);
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }
    
    /**
     * Computes the distance between two given shapes.
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shapes'
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shapes' hull.
     * @param shape1    A <code>Shape3D</code>.
     * @param shape2    A <code>Shape3D</code>.
     * @return          The distance between <code>shape1</code> and <code>shape2</code>.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported.
     */
    public static double distance(Shape3D shape1, Shape3D shape2) {
        // point?
        if (shape1 instanceof Point3D) {
            
            // point-point
            if (shape2 instanceof Point3D)
                return Distance3D.distance((Point3D)shape1, (Point3D)shape2);
            
            // point-segment
            if (shape2 instanceof Segment3D)
                return Distance3D.distance((Point3D)shape1, (Segment3D)shape2);
            
            // point-cone
            if (shape2 instanceof Cone3D)
                return Distance3D.distance((Point3D)shape1, (Cone3D)shape2);
            
            // point-cylinder
            if (shape2 instanceof Cylinder3D)
                return Distance3D.distance((Point3D)shape1, (Cylinder3D)shape2);
            
            // point-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Distance3D.distance((Point3D)shape1, (Polyhedron3D)shape2);
            
            // point-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Distance3D.distance((Point3D)shape1, (Ellipsoid3D)shape2);
            
            // point-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // line segment?
        if (shape1 instanceof Segment3D) {
            
            // segment-point
            if (shape2 instanceof Point3D)
                return Distance3D.distance((Point3D)shape2, (Segment3D)shape1);
            
            // segment-segment
            if (shape2 instanceof Segment3D)
                return Distance3D.distance((Segment3D)shape1, (Segment3D)shape2);
            
            // segment-cone
            if (shape2 instanceof Cone3D)
                return Distance3D.distance((Segment3D)shape1, (Cone3D)shape2);
            
            // segment-polygon
            if (shape2 instanceof Cylinder3D)
                return Distance3D.distance((Segment3D)shape1, (Cylinder3D)shape2);
            
            // segment-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Distance3D.distance((Segment3D)shape1, (Polyhedron3D)shape2);
            
            // segment-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Distance3D.distance((Segment3D)shape1, (Ellipsoid3D)shape2);
            
            // segment-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cone?
        if (shape1 instanceof Cone3D) {
            
            // cone-point
            if (shape2 instanceof Point3D)
                return Distance3D.distance((Point3D)shape2, (Cone3D)shape1);
            
            // cone-segment
            if (shape2 instanceof Segment3D)
                return Distance3D.distance((Segment3D)shape2, (Cone3D)shape1);
            
            // cone-cone
            if (shape2 instanceof Cone3D)
                return Distance3D.distance((Cone3D)shape1, (Cone3D)shape2);
            
            // cone-cylinder
            if (shape2 instanceof Cylinder3D)
                return Distance3D.distance((Cone3D)shape1, (Cylinder3D)shape2);
            
            // cone-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Distance3D.distance((Cone3D)shape1, (Polyhedron3D)shape2);
            
            // cone-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Distance3D.distance((Cone3D)shape1, (Ellipsoid3D)shape2);
            
            // cone-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cylinder?
        if (shape1 instanceof Cylinder3D) {
            
            // cylinder-point
            if (shape2 instanceof Point3D)
                return Distance3D.distance((Point3D)shape2, (Cylinder3D)shape1);
        
            // cylinder-segment
            if (shape2 instanceof Segment3D)
                return Distance3D.distance((Segment3D)shape2, (Cylinder3D)shape1);
            
            // cylinder-cone
            if (shape2 instanceof Cone3D)
                return Distance3D.distance((Cone3D)shape2, (Cylinder3D)shape1);
            
            // cylinder-cylinder
            if (shape2 instanceof Cylinder3D)
                return Distance3D.distance((Cylinder3D)shape1, (Cylinder3D)shape2);
            
            // cylinder-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Distance3D.distance((Cylinder3D)shape1, (Polyhedron3D)shape2);
            
            // cylinder-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Distance3D.distance((Cylinder3D)shape1, (Ellipsoid3D)shape2);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // polyhedron?
        if (shape1 instanceof Polyhedron3D) {
            
            // polyhedron-point
            if (shape2 instanceof Point3D)
                return Distance3D.distance((Point3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-segment
            if (shape2 instanceof Segment3D)
                return Distance3D.distance((Segment3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-cone
            if (shape2 instanceof Cone3D)
                return Distance3D.distance((Cone3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-cylinder
            if (shape2 instanceof Cylinder3D)
                return Distance3D.distance((Cylinder3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Distance3D.distance((Polyhedron3D)shape1, (Polyhedron3D)shape2);
            
            // polyhedron-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Distance3D.distance((Polyhedron3D)shape1, (Ellipsoid3D)shape2);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // ellipsoid?
        if (shape1 instanceof Ellipsoid3D) {
            
            // ellipsoid-point
            if (shape2 instanceof Point3D)
                return Distance3D.distance((Point3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-segment
            if (shape2 instanceof Segment3D)
                return Distance3D.distance((Segment3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-cone
            if (shape2 instanceof Cone3D)
                return Distance3D.distance((Cone3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-cylinder
            if (shape2 instanceof Cylinder3D)
                return Distance3D.distance((Cylinder3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Distance3D.distance((Polyhedron3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Distance3D.distance((Ellipsoid3D)shape1, (Ellipsoid3D)shape2);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }
    
    
    /*********************************************
     * MARK: Great Circle Distance
     *********************************************/
    
    /**
     * Computes the great circle distance between two given vertices, at sea level.
     * @param vert1     A <code>Vector3D</code> object.
     * @param vert2     A <code>Vector3D</code> object.
     * @return          The great circle distance between <code>vert1</code> and 
     *                  <code>vert2</code> at sea level.
     */
    public static double greatCircleDistance(Vector3D vert1, Vector3D vert2) {
        return greatCircleDistance(vert1, vert2, DistanceMode.SEA_LEVEL);
    }
    
    /**
     * Computes the great circle distance between two given vertices, using a 
     * given mode to determine the altitude at which distance is calculated. 
     * @param vert1     A <code>Vector3D</code> object.
     * @param vert2     A <code>Vector3D</code> object.
     * @param distMode  A <code>DistanceMode</code> defining what altitude to use
     *                  in the distance calculation.
     * @return          The great circle distance between <code>vert1</code> and 
     *                  <code>vert2</code> using the given <code>DistanceMode</code>.
     */
    public static double greatCircleDistance(Vector3D vert1, Vector3D vert2, DistanceMode distMode) {
        // convert vectors to WGS-84 coordinates
        CoordinateConversion conversion = new CoordinateConversion();
        WGS84Coord coord1 = conversion.toWGS84(vert1);
        WGS84Coord coord2 = conversion.toWGS84(vert2);
        
        // compute distance between coords
        return coord1.greatCircleDistance(coord2, distMode);
    }
    
    /**
     * Computes the great circle distance between two given vertices, at a given 
     * altitude in kilometers.
     * @param vert1     A <code>Vector3D</code> object.
     * @param vert2     A <code>Vector3D</code> object.
     * @param altitude  The altitude, in kilometers, at which the distance will 
     *                  be computed.
     * @return          The great circle distance between <code>vert1</code> and 
     *                  <code>vert2</code> at <code>latitude</code> kilometers 
     *                  above sea level.
     */
    public static double greatCircleDistance(Vector3D vert1, Vector3D vert2, double altitude) {
        // convert vectors to WGS-84 coordinates
        CoordinateConversion conversion = new CoordinateConversion();
        WGS84Coord coord1 = conversion.toWGS84(vert1);
        WGS84Coord coord2 = conversion.toWGS84(vert2);
        
        // compute distance between coords
        return coord1.greatCircleDistance(coord2, altitude);
    }
    
    /**
     * Computes the great circle distance between a given shape and vertex, at 
     * sea level. 
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shape's
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shape's hull.     
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @return          The great circle distance between <code>vertex</code> and 
     *                  <code>shape</code> at sea level.
     */
    public static double greatCircleDistance(Shape3D shape, Vector3D vertex) {
        return greatCircleDistance(shape, vertex, DistanceMode.SEA_LEVEL);
    }
    
    /**
     * Computes the great circle distance between a given shape and vertex, using 
     * a given mode to determine the altitude at which distance is calculated. 
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shape's
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shape's hull.     
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @param distMode  A <code>DistanceMode</code> defining what altitude to use
     *                  in the distance calculation.
     * @return          The great circle distance between <code>vertex</code> and 
     *                  <code>shape</code> using the given <code>DistanceMode</code>.
     */
    public static double greatCircleDistance(Shape3D shape, Vector3D vertex, DistanceMode distMode) {
        // point?
        if (shape instanceof Point3D)
            return GreatCircleDistance3D.distance((Point3D)shape, vertex, distMode);
        
        // line segment?
        if (shape instanceof Segment3D)
            return GreatCircleDistance3D.distance((Segment3D)shape, vertex, distMode);
        
        // cone?
        if (shape instanceof Cone3D)
            return GreatCircleDistance3D.distance((Cone3D)shape, vertex, distMode);
        
        // cylinder?
        if (shape instanceof Cylinder3D)
            return GreatCircleDistance3D.distance((Cylinder3D)shape, vertex, distMode);
        
        // polyhedron?
        if (shape instanceof Polyhedron3D)
            return GreatCircleDistance3D.distance((Polyhedron3D)shape, vertex, distMode);
        
        // ellipsoid?
        if (shape instanceof Ellipsoid3D)
            return GreatCircleDistance3D.distance((Ellipsoid3D)shape, vertex, distMode);
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }
    
    /**
     * Computes the great circle distance between a given shape and vertex, at a 
     * given altitude in kilometers. 
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shape's
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shape's hull.     
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @param altitude  The altitude, in kilometers, at which the distance will 
     *                  be computed.
     * @return          The great circle distance between <code>vertex</code> and 
     *                  <code>shape</code> at <code>altitude</code> kilometers 
     *                  above sea level.
     */
    public static double greatCircleDistance(Shape3D shape, Vector3D vertex, double altitude) {
        // point?
        if (shape instanceof Point3D)
            return GreatCircleDistance3D.distance((Point3D)shape, vertex, altitude);
        
        // line segment?
        if (shape instanceof Segment3D)
            return GreatCircleDistance3D.distance((Segment3D)shape, vertex, altitude);
        
        // cone?
        if (shape instanceof Cone3D)
            return GreatCircleDistance3D.distance((Cone3D)shape, vertex, altitude);
        
        // cylinder?
        if (shape instanceof Cylinder3D)
            return GreatCircleDistance3D.distance((Cylinder3D)shape, vertex, altitude);
        
        // polyhedron?
        if (shape instanceof Polyhedron3D)
            return GreatCircleDistance3D.distance((Polyhedron3D)shape, vertex, altitude);
        
        // ellipsoid?
        if (shape instanceof Ellipsoid3D)
            return GreatCircleDistance3D.distance((Ellipsoid3D)shape, vertex, altitude);
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }
    
    /**
     * Computes the great circle distance between two given shapes, at sea level.
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shapes'
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shapes' hull.
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @return          The great circle distance between <code>shape1</code> and 
     *                  <code>shape2</code> at sea level.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported.
     */
    public static double greatCircleDistance(Shape3D shape1, Shape3D shape2) {
        return greatCircleDistance(shape1, shape2, DistanceMode.SEA_LEVEL);
    }
    
    /**
     * Computes the great circle distance between two given shapes, using a given 
     * mode to determine the altitude at which distance is calculated. 
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shapes'
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shapes' hull.
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @param distMode  A <code>DistanceMode</code> defining what altitude to use
     *                  in the distance calculation.
     * @return          The great circle distance between <code>shape1</code> and 
     *                  <code>shape2</code> using the given <code>DistanceMode</code>.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported.
     */
    public static double greatCircleDistance(Shape3D shape1, Shape3D shape2, DistanceMode distMode) {
        // point?
        if (shape1 instanceof Point3D) {
            
            // point-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Point3D)shape2, distMode);
            
            // point-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Segment3D)shape2, distMode);
            
            // point-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Cone3D)shape2, distMode);
            
            // point-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Cylinder3D)shape2, distMode);
            
            // point-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Polyhedron3D)shape2, distMode);
            
            // point-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Ellipsoid3D)shape2, distMode);
            
            // point-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // line segment?
        if (shape1 instanceof Segment3D) {
            
            // segment-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Segment3D)shape1, distMode);
            
            // segment-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Segment3D)shape2, distMode);
            
            // segment-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Cone3D)shape2, distMode);
            
            // segment-polygon
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Cylinder3D)shape2, distMode);
            
            // segment-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Polyhedron3D)shape2, distMode);
            
            // segment-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Ellipsoid3D)shape2, distMode);
            
            // segment-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cone?
        if (shape1 instanceof Cone3D) {
            
            // cone-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Cone3D)shape1, distMode);
            
            // cone-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape2, (Cone3D)shape1, distMode);
            
            // cone-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Cone3D)shape1, (Cone3D)shape2, distMode);
            
            // cone-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Cone3D)shape1, (Cylinder3D)shape2, distMode);
            
            // cone-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Cone3D)shape1, (Polyhedron3D)shape2, distMode);
            
            // cone-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Cone3D)shape1, (Ellipsoid3D)shape2, distMode);
            
            // cone-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cylinder?
        if (shape1 instanceof Cylinder3D) {
            
            // cylinder-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Cylinder3D)shape1, distMode);
            
            // cylinder-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape2, (Cylinder3D)shape1, distMode);
            
            // cylinder-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Cone3D)shape2, (Cylinder3D)shape1, distMode);
            
            // cylinder-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape1, (Cylinder3D)shape2, distMode);
            
            // cylinder-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape1, (Polyhedron3D)shape2, distMode);
            
            // cylinder-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape1, (Ellipsoid3D)shape2, distMode);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // polyhedron?
        if (shape1 instanceof Polyhedron3D) {
            
            // polyhedron-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Polyhedron3D)shape1, distMode);
            
            // polyhedron-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape2, (Polyhedron3D)shape1, distMode);
            
            // polyhedron-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Cone3D)shape2, (Polyhedron3D)shape1, distMode);
            
            // polyhedron-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape2, (Polyhedron3D)shape1, distMode);
            
            // polyhedron-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Polyhedron3D)shape1, (Polyhedron3D)shape2, distMode);
            
            // polyhedron-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Polyhedron3D)shape1, (Ellipsoid3D)shape2, distMode);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // ellipsoid?
        if (shape1 instanceof Ellipsoid3D) {
            
            // ellipsoid-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Ellipsoid3D)shape1, distMode);
            
            // ellipsoid-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape2, (Ellipsoid3D)shape1, distMode);
            
            // ellipsoid-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Cone3D)shape2, (Ellipsoid3D)shape1, distMode);
            
            // ellipsoid-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape2, (Ellipsoid3D)shape1, distMode);
            
            // ellipsoid-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Polyhedron3D)shape2, (Ellipsoid3D)shape1, distMode);
            
            // ellipsoid-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Ellipsoid3D)shape1, (Ellipsoid3D)shape2, distMode);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }
    
    /**
     * Computes the great circle distance between two given shapes, at a given 
     * altitude in kilometers. 
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shapes'
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shapes' hull.
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @param altitude  The altitude, in kilometers, at which the distance will 
     *                  be computed.
     * @return          The great circle distance between <code>shape1</code> and.
     *                  <code>shape2</code> at <code>altitude</code> kilometers 
     *                  above sea level.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported.
     */
    public static double greatCircleDistance(Shape3D shape1, Shape3D shape2, double altitude) {
        // point?
        if (shape1 instanceof Point3D) {
            
            // point-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Point3D)shape2, altitude);
            
            // point-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Segment3D)shape2, altitude);
            
            // point-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Cone3D)shape2, altitude);
            
            // point-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Cylinder3D)shape2, altitude);
            
            // point-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Polyhedron3D)shape2, altitude);
            
            // point-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Point3D)shape1, (Ellipsoid3D)shape2, altitude);
            
            // point-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // line segment?
        if (shape1 instanceof Segment3D) {
            
            // segment-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Segment3D)shape1, altitude);
            
            // segment-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Segment3D)shape2, altitude);
            
            // segment-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Cone3D)shape2, altitude);
            
            // segment-polygon
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Cylinder3D)shape2, altitude);
            
            // segment-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Polyhedron3D)shape2, altitude);
            
            // segment-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Segment3D)shape1, (Ellipsoid3D)shape2, altitude);
            
            // segment-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cone?
        if (shape1 instanceof Cone3D) {
            
            // cone-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Cone3D)shape1, altitude);
            
            // cone-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape2, (Cone3D)shape1, altitude);
            
            // cone-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Cone3D)shape1, (Cone3D)shape2, altitude);
            
            // cone-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Cone3D)shape1, (Cylinder3D)shape2, altitude);
            
            // cone-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Cone3D)shape1, (Polyhedron3D)shape2, altitude);
            
            // cone-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Cone3D)shape1, (Ellipsoid3D)shape2, altitude);
            
            // cone-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cylinder?
        if (shape1 instanceof Cylinder3D) {
            
            // cylinder-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Cylinder3D)shape1, altitude);
            
            // cylinder-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape2, (Cylinder3D)shape1, altitude);
            
            // cylinder-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Cone3D)shape2, (Cylinder3D)shape1, altitude);
            
            // cylinder-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape1, (Cylinder3D)shape2, altitude);
            
            // cylinder-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape1, (Polyhedron3D)shape2, altitude);
            
            // cylinder-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape1, (Ellipsoid3D)shape2, altitude);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // polyhedron?
        if (shape1 instanceof Polyhedron3D) {
            
            // polyhedron-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Polyhedron3D)shape1, altitude);
            
            // polyhedron-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape2, (Polyhedron3D)shape1, altitude);
            
            // polyhedron-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Cone3D)shape2, (Polyhedron3D)shape1, altitude);
            
            // polyhedron-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape2, (Polyhedron3D)shape1, altitude);
            
            // polyhedron-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Polyhedron3D)shape1, (Polyhedron3D)shape2, altitude);
            
            // polyhedron-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Polyhedron3D)shape1, (Ellipsoid3D)shape2, altitude);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // ellipsoid?
        if (shape1 instanceof Ellipsoid3D) {
            
            // ellipsoid-point
            if (shape2 instanceof Point3D)
                return GreatCircleDistance3D.distance((Point3D)shape2, (Ellipsoid3D)shape1, altitude);
            
            // ellipsoid-segment
            if (shape2 instanceof Segment3D)
                return GreatCircleDistance3D.distance((Segment3D)shape2, (Ellipsoid3D)shape1, altitude);
            
            // ellipsoid-cone
            if (shape2 instanceof Cone3D)
                return GreatCircleDistance3D.distance((Cone3D)shape2, (Ellipsoid3D)shape1, altitude);
            
            // ellipsoid-cylinder
            if (shape2 instanceof Cylinder3D)
                return GreatCircleDistance3D.distance((Cylinder3D)shape2, (Ellipsoid3D)shape1, altitude);
            
            // ellipsoid-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return GreatCircleDistance3D.distance((Polyhedron3D)shape2, (Ellipsoid3D)shape1, altitude);
            
            // ellipsoid-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return GreatCircleDistance3D.distance((Ellipsoid3D)shape1, (Ellipsoid3D)shape2, altitude);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }
    
    
    /*********************************************
     * MARK: Rhumb Line Distance
     *********************************************/
    
    /**
     * Computes the rhumb line distance between two given vertices, at sea level.
     * @param vert1     A <code>Vector3D</code> object.
     * @param vert2     A <code>Vector3D</code> object.
     * @return          The rhumb line distance between <code>vert1</code> and 
     *                  <code>vert2</code> at sea level.
     */
    public static double rhumbLineDistance(Vector3D vert1, Vector3D vert2) {
        // convert vectors to WGS-84 coordinates
        CoordinateConversion conversion = new CoordinateConversion();
        WGS84Coord coord1 = conversion.toWGS84(vert1);
        WGS84Coord coord2 = conversion.toWGS84(vert2);
        
        // compute distance between coords
        return coord1.rhumbLineDistance(coord2);
    }
    
    /**
     * Computes the rhumb line distance between a given shape and vertex, at 
     * sea level. 
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shape's
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shape's hull.     
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @return          The rhumb line distance between <code>vertex</code> and 
     *                  <code>shape</code> at sea level.
     */
    public static double rhumbLineDistance(Shape3D shape, Vector3D vertex) {
        // point?
        if (shape instanceof Point3D)
            return RhumbLineDistance3D.distance((Point3D)shape, vertex);
        
        // line segment?
        if (shape instanceof Segment3D)
            return RhumbLineDistance3D.distance((Segment3D)shape, vertex);
        
        // cone?
        if (shape instanceof Cone3D)
            return RhumbLineDistance3D.distance((Cone3D)shape, vertex);
        
        // cylinder?
        if (shape instanceof Cylinder3D)
            return RhumbLineDistance3D.distance((Cylinder3D)shape, vertex);
        
        // polyhedron?
        if (shape instanceof Polyhedron3D)
            return RhumbLineDistance3D.distance((Polyhedron3D)shape, vertex);
        
        // ellipsoid?
        if (shape instanceof Ellipsoid3D)
            return RhumbLineDistance3D.distance((Ellipsoid3D)shape, vertex);
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }
    
    /**
     * Computes the rhumb line distance between two given shapes, at sea level.
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, distance is computed using the shapes'
     * center points. This approach should be fixed in the future to use the closest 
     * points on the shapes' hull.
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @return          The rhumb line distance between <code>shape1</code> and 
     *                  <code>shape2</code> at sea level.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported.
     */
    public static double rhumbLineDistance(Shape3D shape1, Shape3D shape2) {
        // point?
        if (shape1 instanceof Point3D) {
            
            // point-point
            if (shape2 instanceof Point3D)
                return RhumbLineDistance3D.distance((Point3D)shape1, (Point3D)shape2);
            
            // point-segment
            if (shape2 instanceof Segment3D)
                return RhumbLineDistance3D.distance((Point3D)shape1, (Segment3D)shape2);
            
            // point-cone
            if (shape2 instanceof Cone3D)
                return RhumbLineDistance3D.distance((Point3D)shape1, (Cone3D)shape2);
            
            // point-cylinder
            if (shape2 instanceof Cylinder3D)
                return RhumbLineDistance3D.distance((Point3D)shape1, (Cylinder3D)shape2);
            
            // point-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return RhumbLineDistance3D.distance((Point3D)shape1, (Polyhedron3D)shape2);
            
            // point-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return RhumbLineDistance3D.distance((Point3D)shape1, (Ellipsoid3D)shape2);
            
            // point-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // line segment?
        if (shape1 instanceof Segment3D) {
            
            // segment-point
            if (shape2 instanceof Point3D)
                return RhumbLineDistance3D.distance((Point3D)shape2, (Segment3D)shape1);
            
            // segment-segment
            if (shape2 instanceof Segment3D)
                return RhumbLineDistance3D.distance((Segment3D)shape1, (Segment3D)shape2);
            
            // segment-cone
            if (shape2 instanceof Cone3D)
                return RhumbLineDistance3D.distance((Segment3D)shape1, (Cone3D)shape2);
            
            // segment-polygon
            if (shape2 instanceof Cylinder3D)
                return RhumbLineDistance3D.distance((Segment3D)shape1, (Cylinder3D)shape2);
            
            // segment-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return RhumbLineDistance3D.distance((Segment3D)shape1, (Polyhedron3D)shape2);
            
            // segment-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return RhumbLineDistance3D.distance((Segment3D)shape1, (Ellipsoid3D)shape2);
            
            // segment-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cone?
        if (shape1 instanceof Cone3D) {
            
            // cone-point
            if (shape2 instanceof Point3D)
                return RhumbLineDistance3D.distance((Point3D)shape2, (Cone3D)shape1);
            
            // cone-segment
            if (shape2 instanceof Segment3D)
                return RhumbLineDistance3D.distance((Segment3D)shape2, (Cone3D)shape1);
            
            // cone-cone
            if (shape2 instanceof Cone3D)
                return RhumbLineDistance3D.distance((Cone3D)shape1, (Cone3D)shape2);
            
            // cone-cylinder
            if (shape2 instanceof Cylinder3D)
                return RhumbLineDistance3D.distance((Cone3D)shape1, (Cylinder3D)shape2);
            
            // cone-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return RhumbLineDistance3D.distance((Cone3D)shape1, (Polyhedron3D)shape2);
            
            // cone-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return RhumbLineDistance3D.distance((Cone3D)shape1, (Ellipsoid3D)shape2);
            
            // cone-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cylinder?
        if (shape1 instanceof Cylinder3D) {
            
            // cylinder-point
            if (shape2 instanceof Point3D)
                return RhumbLineDistance3D.distance((Point3D)shape2, (Cylinder3D)shape1);
            
            // cylinder-segment
            if (shape2 instanceof Segment3D)
                return RhumbLineDistance3D.distance((Segment3D)shape2, (Cylinder3D)shape1);
            
            // cylinder-cone
            if (shape2 instanceof Cone3D)
                return RhumbLineDistance3D.distance((Cone3D)shape2, (Cylinder3D)shape1);
            
            // cylinder-cylinder
            if (shape2 instanceof Cylinder3D)
                return RhumbLineDistance3D.distance((Cylinder3D)shape1, (Cylinder3D)shape2);
            
            // cylinder-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return RhumbLineDistance3D.distance((Cylinder3D)shape1, (Polyhedron3D)shape2);
            
            // cylinder-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return RhumbLineDistance3D.distance((Cylinder3D)shape1, (Ellipsoid3D)shape2);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // polyhedron?
        if (shape1 instanceof Polyhedron3D) {
            
            // polyhedron-point
            if (shape2 instanceof Point3D)
                return RhumbLineDistance3D.distance((Point3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-segment
            if (shape2 instanceof Segment3D)
                return RhumbLineDistance3D.distance((Segment3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-cone
            if (shape2 instanceof Cone3D)
                return RhumbLineDistance3D.distance((Cone3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-cylinder
            if (shape2 instanceof Cylinder3D)
                return RhumbLineDistance3D.distance((Cylinder3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return RhumbLineDistance3D.distance((Polyhedron3D)shape1, (Polyhedron3D)shape2);
            
            // polyhedron-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return RhumbLineDistance3D.distance((Polyhedron3D)shape1, (Ellipsoid3D)shape2);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // ellipsoid?
        if (shape1 instanceof Ellipsoid3D) {
            
            // ellipsoid-point
            if (shape2 instanceof Point3D)
                return RhumbLineDistance3D.distance((Point3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-segment
            if (shape2 instanceof Segment3D)
                return RhumbLineDistance3D.distance((Segment3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-cone
            if (shape2 instanceof Cone3D)
                return RhumbLineDistance3D.distance((Cone3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-cylinder
            if (shape2 instanceof Cylinder3D)
                return RhumbLineDistance3D.distance((Cylinder3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return RhumbLineDistance3D.distance((Polyhedron3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return RhumbLineDistance3D.distance((Ellipsoid3D)shape1, (Ellipsoid3D)shape2);
            
            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }

    
    /*********************************************
     * MARK: Intersects
     *********************************************/
    
    /**
     * Determines whether two given shapes intersect.
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Cone3D</code>,
     * <code>Cylinder3D</code>, <code>Ellipsoid3D</code>, <code>Polyhedron3D</code>,
     * and all subclasses of these.
     * <p>
     * Do to logic and time constraints, intersection between certain shapes is
     * computed using the shapes' bounding boxes, rather than their actual hull.
     * This approach should be fixed in the future, as using bounding box intersection
     * queries can yield very imprecise results.
     * @param shape1    A <code>Shape3D</code>.
     * @param shape2    A <code>Shape3D</code>.
     * @return          <code>true</code> if <code>shape1</code> is wholly contained
     *                  by <code>shape2</code>; <code>false</code> otherwise.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported.
     */
    public static boolean intersects(Shape3D shape1, Shape3D shape2) {
        // point?
        if (shape1 instanceof Point3D)
            return Intersects3D.intersects((Point3D)shape1, shape2);
        
        // segment?
        if (shape1 instanceof Segment3D) {
            
            // segment-point
            if (shape2 instanceof Point3D)
                return Intersects3D.intersects((Point3D)shape2, (Segment3D)shape1);
            
            // segment-segment
            if (shape2 instanceof Segment3D)
                return Intersects3D.intersects((Segment3D)shape1, (Segment3D)shape2);
            
            // segment-cylinder
            if (shape2 instanceof Cylinder3D)
                return Intersects3D.intersects((Segment3D)shape1, (Cylinder3D)shape2);
            
            // segment-cone
            if (shape2 instanceof Cone3D)
                return Intersects3D.intersects((Segment3D)shape1, (Cone3D)shape2);
            
            // segment-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Intersects3D.intersects((Segment3D)shape1, (Ellipsoid3D)shape2);
            
            // segment-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Intersects3D.intersects((Segment3D)shape1, (Polyhedron3D)shape2);
            
            // segment-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cylinder?
        if (shape1 instanceof Cylinder3D) {
            
            // cylinder-point
            if (shape2 instanceof Point3D)
                return Intersects3D.intersects((Point3D)shape2, (Cylinder3D)shape1);
            
            // cylinder-segment
            if (shape2 instanceof Segment3D)
                return Intersects3D.intersects((Segment3D)shape2, (Cylinder3D)shape1);
            
            // cylinder-cylinder
            if (shape2 instanceof Cylinder3D)
                return Intersects3D.intersects((Cylinder3D)shape1, (Cylinder3D)shape2);
            
            // cylinder-cone
            if (shape2 instanceof Cone3D)
                return Intersects3D.intersects((Cylinder3D)shape1, (Cone3D)shape2);
            
            // cylinder-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Intersects3D.intersects((Cylinder3D)shape1, (Ellipsoid3D)shape2);
            
            // cylinder-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Intersects3D.intersects((Cylinder3D)shape1, (Polyhedron3D)shape2);
            
            // cone-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // cone?
        if (shape1 instanceof Cone3D) {
            
            // cone-point
            if (shape2 instanceof Point3D)
                return Intersects3D.intersects((Point3D)shape2, (Cone3D)shape1);
            
            // cone-segment
            if (shape2 instanceof Segment3D)
                return Intersects3D.intersects((Segment3D)shape2, (Cone3D)shape1);
            
            // cone-cylinder
            if (shape2 instanceof Cylinder3D)
                return Intersects3D.intersects((Cylinder3D)shape2, (Cone3D)shape1);
            
            // cone-cone
            if (shape2 instanceof Cone3D)
                return Intersects3D.intersects((Cone3D)shape1, (Cone3D)shape2);
            
            // cone-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Intersects3D.intersects((Cone3D)shape1, (Ellipsoid3D)shape2);
            
            // cone-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Intersects3D.intersects((Cone3D)shape1, (Polyhedron3D)shape2);
            
            // cone-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // ellipsoid?
        if (shape1 instanceof Ellipsoid3D) {
            
            // ellipsoid-point
            if (shape2 instanceof Point3D)
                return Intersects3D.intersects((Point3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-segment
            if (shape2 instanceof Segment3D)
                return Intersects3D.intersects((Segment3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-cylinder
            if (shape2 instanceof Cylinder3D)
                return Intersects3D.intersects((Cylinder3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-cone
            if (shape2 instanceof Cone3D)
                return Intersects3D.intersects((Cone3D)shape2, (Ellipsoid3D)shape1);
            
            // ellipsoid-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Intersects3D.intersects((Ellipsoid3D)shape1, (Ellipsoid3D)shape2);
            
            // ellipsoid-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Intersects3D.intersects((Ellipsoid3D)shape1, (Polyhedron3D)shape2);
            
            // ellipsoid-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // polyhedron?
        if (shape1 instanceof Polyhedron3D) {
            
            // polyhedron-point
            if (shape2 instanceof Point3D)
                return Intersects3D.intersects((Point3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-segment
            if (shape2 instanceof Segment3D)
                return Intersects3D.intersects((Segment3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-cylinder
            if (shape2 instanceof Cylinder3D)
                return Intersects3D.intersects((Cylinder3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-cone
            if (shape2 instanceof Cone3D)
                return Intersects3D.intersects((Cone3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-ellipsoid
            if (shape2 instanceof Ellipsoid3D)
                return Intersects3D.intersects((Ellipsoid3D)shape2, (Polyhedron3D)shape1);
            
            // polyhedron-polyhedron
            if (shape2 instanceof Polyhedron3D)
                return Intersects3D.intersects((Polyhedron3D)shape1, (Polyhedron3D)shape2);
            
            // polyhedron-unknown
            throw new IllegalArgumentException("Unsupported Shape3D instance");
        }
        
        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
    }
    

    /*********************************************
     * MARK: Intersection Volume
     *********************************************/

     /**
     * Computes the volume of intersection between two <code>Shape3D</code>.
     *
     * <p>
     * This method currently supports the following subclasses of <code>Shape3D</code>:
     * <code>Point3D</code>, <code>Segment3D</code>, <code>Polyhedron3D</code>,
     * <code>Cone3D</code>, <code>Cylinder3D</code>, <code>Sphere3D</code>,
     * <code>Sphereoid3D</code>, <code>Ellipsoid3D</code>
     * and all subclasses of these.
     *
     * If <code>parent</code> does not intersect <code>child</code>, volume is 0.
     * If volume of is <code>Point3D</code> and  <code>Segment3D</code> is 0
     * by definition.
     *
     *
     * Currently this method calls the contains methods which apply the bounding box
     * containment of both shapes. An precise computation for intersection volume
     * have not been implemented yet.
     *
     * @param parent    The parent <code>Shape3D</code>.
     * @param child     The child <code>Shape3D</code>.
     * @return          The volume of intersection between <code>parent</code> and
     *                  <code>child</code>.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported.
     */
     public static double intersectionVolume(Shape3D parent, Shape3D child) {
         //intersect?
         if(!parent.intersects(child))
             return 0;

        // point?
        if ((parent instanceof Point3D) || (child instanceof Point3D))
            return 0; //all cases

        // line segment?
        if ((parent instanceof Segment3D) || (child instanceof Segment3D))
            return 0; //all cases

        //volume is child's volume?
        if(parent.contains(child))
            return child.volume();

        //volume is parents volume?
        if(child.contains(parent))
            return parent.volume();

        // polyhedron?
        if (parent instanceof Polyhedron3D) {

             // polyhedron-polyhedron
            if (child instanceof Polyhedron3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)parent, (Polyhedron3D)child);

            // polyhedron-cone
            if (child instanceof Cone3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)parent, (Cone3D)child);

            // polyhedron-cylinder
            if (child instanceof Cylinder3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)parent, (Cylinder3D)child);

            // polyhedron-sphere
            if (child instanceof Sphere3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)parent, (Sphere3D)child);

            // polyhedron-spheroid
            if (child instanceof Spheroid3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)parent, (Spheroid3D)child);

            // polyhedron-ellipsoid
            if (child instanceof Ellipsoid3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)parent, (Ellipsoid3D)child);

            // polyhedron-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }


        // cone?
        if (parent instanceof Cone3D) {

            // cone-polyhedron
            if (child instanceof Polyhedron3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)child, (Cone3D)parent);

            // cone-cone
            if (child instanceof Cone3D)
                return IntersectionVolume3D.intersectionVolume((Cone3D)parent, (Cone3D)child);

            // cone-cylinder
            if (child instanceof Cylinder3D)
                return IntersectionVolume3D.intersectionVolume((Cone3D)parent, (Cylinder3D)child);

            // cone-sphere
            if (child instanceof Sphere3D)
                return IntersectionVolume3D.intersectionVolume((Cone3D)parent, (Sphere3D)child);

            // cone-spheroid
            if (child instanceof Spheroid3D)
                return IntersectionVolume3D.intersectionVolume((Cone3D)parent, (Spheroid3D)child);


            // cone-ellipsoid
            if (child instanceof Ellipsoid3D)
                return IntersectionVolume3D.intersectionVolume((Cone3D)parent, (Ellipsoid3D)child);

            // cone-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // cylinder?
        if (parent instanceof Cylinder3D) {

            // cylinder-polyhedron
            if (child instanceof Polyhedron3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)child, (Cylinder3D)parent);

            // cylinder-cone
            if (child instanceof Cone3D)
                return IntersectionVolume3D.intersectionVolume((Cone3D)child, (Cylinder3D)parent);

            // cylinder-cylinder
            if (child instanceof Cylinder3D)
                return IntersectionVolume3D.intersectionVolume((Cylinder3D)parent, (Cylinder3D)child);

            // cylinder-sphere
            if (child instanceof Sphere3D)
                return IntersectionVolume3D.intersectionVolume((Cylinder3D)parent, (Sphere3D)child);

            // cylinder-spheroid
            if (child instanceof Spheroid3D)
                return IntersectionVolume3D.intersectionVolume((Cylinder3D)parent, (Spheroid3D)child);

            // cylinder-ellipsoid
            if (child instanceof Ellipsoid3D)
                return IntersectionVolume3D.intersectionVolume((Cylinder3D)parent, (Ellipsoid3D)child);

            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

         // sphere?
        if (parent instanceof Sphere3D) {

            // sphere-polyhedron
            if (child instanceof Polyhedron3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)child, (Sphere3D)parent);

            // sphere-cone
            if (child instanceof Cone3D)
                return IntersectionVolume3D.intersectionVolume((Cone3D)child, (Sphere3D)parent);

            // sphere-cylinder
            if (child instanceof Cylinder3D)
                return IntersectionVolume3D.intersectionVolume((Cylinder3D)child, (Sphere3D)parent);

            // sphere-sphere
            if (child instanceof Sphere3D)
                return IntersectionVolume3D.intersectionVolume((Sphere3D)parent, (Sphere3D)child);

            // sphere-spheroid
            if (child instanceof Spheroid3D)
                return IntersectionVolume3D.intersectionVolume((Sphere3D)parent, (Spheroid3D)child);

            // sphere-ellipsoid
            if (child instanceof Ellipsoid3D)
                return IntersectionVolume3D.intersectionVolume((Sphere3D)parent, (Ellipsoid3D)child);

            // sphere-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

          // spheroid?
        if (parent instanceof Spheroid3D) {

            // spheroid-polyhedron
            if (child instanceof Polyhedron3D)
                return IntersectionVolume3D.intersectionVolume((Polyhedron3D)child, (Spheroid3D)parent);

            // spheroid-cone
            if (child instanceof Cone3D)
                return IntersectionVolume3D.intersectionVolume((Cone3D)child, (Spheroid3D)parent);

            // spheroid-cylinder
            if (child instanceof Cylinder3D)
                return IntersectionVolume3D.intersectionVolume((Cylinder3D)child, (Spheroid3D)parent);

            // spheroid-sphere
            if (child instanceof Sphere3D)
                return IntersectionVolume3D.intersectionVolume((Sphere3D)child, (Spheroid3D)parent);

            // spheroid-spheroid
            if (child instanceof Spheroid3D)
                return IntersectionVolume3D.intersectionVolume((Spheroid3D)parent, (Spheroid3D)child);

            // spheroid-ellipsoid
            if (child instanceof Ellipsoid3D)
                return IntersectionVolume3D.intersectionVolume((Spheroid3D)parent, (Ellipsoid3D)child);

            // spheroid-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // ellipsoid?
        if (parent instanceof Ellipsoid3D) {

            // ellipsoid-polyhedron
            if (child instanceof Polyhedron3D)
                 return IntersectionVolume3D.intersectionVolume((Polyhedron3D)child, (Ellipsoid3D)parent);

            // ellipsoid-cone
            if (child instanceof Cone3D)
                 return IntersectionVolume3D.intersectionVolume((Cone3D)child, (Ellipsoid3D)parent);

            // ellipsoid-cylinder
            if (child instanceof Cylinder3D)
                 return IntersectionVolume3D.intersectionVolume((Cylinder3D)child, (Ellipsoid3D)parent);

            // ellipsoid-sphere
            if (child instanceof Sphere3D)
                return IntersectionVolume3D.intersectionVolume((Sphere3D)child, (Ellipsoid3D)parent);

            // ellipsoid-spheroid
            if (child instanceof Spheroid3D)
                return IntersectionVolume3D.intersectionVolume((Spheroid3D)child, (Ellipsoid3D)parent);

            // ellipsoid-ellipsoid
            if (child instanceof Ellipsoid3D)
                return IntersectionVolume3D.intersectionVolume((Ellipsoid3D)parent, (Ellipsoid3D)child);

            // cylinder-unknown
            throw new IllegalArgumentException("Unsupported child Shape3D instance");
        }

        // unknown
        throw new IllegalArgumentException("Unsupported parent Shape3D instance");
     }

     /*********************************************
     * MARK: Intersects Time
     *********************************************/

     /**
     * Determines if the moving <code>Shape3D</code> object, <code>moving</code>,
     * will intersect the stationary <code>Shape3D</code> object, <code>stationary</code>
     * in the given amount of time in seconds.
     *
     * <p>
     * The moving object has a vector which represents the distance in units the
     * <code>Shape3D</code> will travel in one second.
     * The stationary object cannot be moving.
     * Time must be positive.
     * </p>
     *
     * @param moving        The moving <code>Shape3D</code>.
     * @param stationary    The stationary <code>Shape3D</code>.
     * @return          <code>true</code> if the moving <code>Shape3D</code> will
     *                  intersect the stationary <code>Shape3D</code> in the
     *                  given amount of time.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported, if time is negative, or if
     *                  the stationary object is moving.
     */
     public static boolean intersects(Shape3D moving, Shape3D stationary, double time){
         // TODO: Intersect Time. Euclidean WGS Conflict

        if(time < 0)
            throw new IllegalArgumentException("Time cannot be negative");

        if(!stationary.getVelocity().isZeroVector())
            throw new IllegalArgumentException("The stationary object cannot be moving");

         //currently intersecting?
        if(moving.intersects(stationary))
            return true;

        if(moving instanceof Point3D)
            return IntersectsTime3D.intersects((Point3D)moving, stationary, time);

        if(moving instanceof Segment3D)
            return IntersectsTime3D.intersects((Segment3D)moving, stationary, time);

        if(moving instanceof Polyhedron3D)
            return IntersectsTime3D.intersects((Polyhedron3D)moving, stationary, time);

        if(moving instanceof Cone3D)
            return IntersectsTime3D.intersects((Cone3D)moving, stationary, time);

        if(moving instanceof Cylinder3D)
            return IntersectsTime3D.intersects((Cylinder3D)moving, stationary, time);

        if(moving instanceof Sphere3D)
            return IntersectsTime3D.intersects((Sphere3D)moving, stationary, time);

        if(moving instanceof Spheroid3D)
            return IntersectsTime3D.intersects((Spheroid3D)moving, stationary, time);

        if(moving instanceof Ellipsoid3D)
            return IntersectsTime3D.intersects((Ellipsoid3D)moving, stationary, time);

        // unknown
        throw new IllegalArgumentException("Unsupported Shape3D instance");
     }

      /**
     * Determines if two <code>Shape3D</code> objects intersect in a given amount of time.
     * The <code>stationary</code> shape cannot be moving. Time may not be negative
     *
     * @param moving A <code>Shape3D</code> object which is moving.
     * @param stationary A <code>Shape3D</code> object which is stationary.
     * @param time Given amount of time until possible intersect.
     * @return <code>true</code> if the moving shape will intersection the stationary
     *         shape within the given amount of time; else <code>false</code>.
     */
    public static boolean rhumbLineIntersects(Shape3D moving, Shape3D stationary, double time) {
        if (time < 0)
            throw new IllegalArgumentException("Time cannot be negative");

        if (!stationary.getVelocity().isZeroVector())
            throw new IllegalArgumentException("The stationary shape cannot have a velocity");

        CoordinateConversion conversion = new CoordinateConversion();
        WGS84Coord start = conversion.toWGS84(moving.getPosition());
        WGS84Coord end = conversion.toWGS84(stationary.getPosition());

        // Velocity
        Vector3D v = moving.getVelocity();
   
        //Calculate the speed and bearing
        double speed = Math.sqrt((v.getX()*v.getX()) + (v.getY()*v.getY()));
        double tempDegrees = Math.toDegrees((Math.atan2(v.getY(), v.getX())));
        double bearing = (450 - tempDegrees) % 360;

        return IntersectsTime3D.rhumbLineIntersect(start, end,(bearing), speed, time);
    }
    
     /*********************************************
     * MARK: Other
     *********************************************/

    /**
     * Determines whether a given 3D vertex lies between two other 3D vertices.
     * @param vert1 A <code>Vector3D</code> describing the vertex in question.
     * @param vert2 A <code>Vector3D</code> describing one of the vertex limits.
     * @param vert3 A <code>Vector3D</code> describing one of the vertex limits.
     * @return      <code>true</code> if <code>vert1</code> lies directly between
     *              <code>vert2</code> and <code>vert3</code>.
     */
    public static boolean isBetween(Vector3D vert1, Vector3D vert2, Vector3D vert3) {
        // vertex on one of endpoints?
        if (vert1.equals(vert2) || vert1.equals(vert3))
            return true;

        // store component values for easy access
        double v1x = vert1.getX();
        double v1y = vert1.getY();
        double v1z = vert1.getZ();
        double v2x = vert2.getX();
        double v2y = vert2.getY();
        double v2z = vert2.getZ();
        double v3x = vert3.getX();
        double v3y = vert3.getY();
        double v3z = vert3.getZ();

        // vertex out of x bounds?
        if ((v1x > v2x && v1x > v3x) || (v1x < v2x && v1x < v3x))
            return false;

        // vertex out of y bounds?
        if ((v1y > v2y && v1y > v3y) || (v1y < v2y && v1y < v3y))
            return false;

        // vertex out of z bounds?
        if ((v1z > v2z && v1z > v3z) || (v1z < v2z && v1z < v3z))
            return false;

        // calculate vectors from vert1 to vert3 and vert2 to vert3
        Vector3D v1 = vert3.subtract(vert1);
        Vector3D v2 = vert3.subtract(vert2);

        // if the vectors are parallel, then vert1 is between vert2 and vert3
        return (v1.isParallel(v2));
    }

    /**
     * Calculates the equation of a plane given 3 Vector3D instances
     * on the desired plane.
     * @param a A Vector3D instance on the desired plane.
     * @param b A Vector3D instance on the desired plane.
     * @param c A Vector3D instance on the desired plane.
     * @return An array of values respresenting the equation for a plane.
     *         Ax + By + Cz + D = 0 in their respected array order.
     *         Position 0 holds A,1 holds B, 2 holds C, 3 holds D.
     */
     public static double[] planeEquation(Vector3D a, Vector3D b, Vector3D c){
         //Vectors
         Vector3D AB = b.subtract(a);
         Vector3D AC = c.subtract(a);

         //Normal to the Plane
         Vector3D n = AB.cross(AC);

         // Nx, Ny, Nz
         double[] planeValues = {
            n.getX(),
            n.getY(),
            n.getZ(),
            n.inverse().dot(a)
         };

         return planeValues;
     }
}
