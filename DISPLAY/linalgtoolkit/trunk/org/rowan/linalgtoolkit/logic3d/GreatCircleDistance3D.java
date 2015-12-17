package org.rowan.linalgtoolkit.logic3d;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.WGS84Coord.DistanceMode;
import org.rowan.linalgtoolkit.shapes3d.Shape3D;

/**
 * The <code>GreatCircleDistance3D</code> class provides great circle distance 
 * query logic for vertices and shapes in the <code>Shapes3D</code> package.
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public abstract class GreatCircleDistance3D {
    
    /**
     * Computes the great circle distance between a given shape and vertex, at 
     * sea level. 
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @return          The great circle distance between <code>vertex</code> and 
     *                  <code>shape</code> at sea level.
     */
    public static double distance(Shape3D shape, Vector3D vertex) {
        return distance(shape, vertex, DistanceMode.SEA_LEVEL);
    }
    
    /**
     * Computes the great circle distance between a given shape and vertex, using 
     * a given mode to determine the altitude at which distance is calculated. 
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @param distMode  A <code>DistanceMode</code> defining what altitude to use
     *                  in the distance calculation.
     * @return          The great circle distance between <code>vertex</code> and 
     *                  <code>shape</code> using the given <code>DistanceMode</code>.
     */
    public static double distance(Shape3D shape, Vector3D vertex, DistanceMode distMode) {
        return LinAlg3D.greatCircleDistance(shape.toWorld(Vector3D.ORIGIN), vertex, distMode);
    }
    
    /**
     * Computes the great circle distance between a given shape and vertex, at a 
     * given altitude in kilometers. 
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @param altitude  The altitude, in kilometers, at which the distance will 
     *                  be computed.
     * @return          The great circle distance between <code>vertex</code> and 
     *                  <code>shape</code> at <code>altitude</code> kilometers above sea level.
     */
    public static double distance(Shape3D shape, Vector3D vertex, double altitude) {
        return LinAlg3D.greatCircleDistance(shape.toWorld(Vector3D.ORIGIN), vertex, altitude);
    }
    
    /**
     * Computes the great circle distance between two given shapes, at sea level.
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @return          The great circle distance between <code>shape1</code> and 
     *                  <code>shape2</code> at sea level.
     */
    public static double distance(Shape3D shape1, Shape3D shape2) {
        return LinAlg3D.greatCircleDistance(shape1, shape2, DistanceMode.SEA_LEVEL);
    }
    
    /**
     * Computes the great circle distance between two given shapes, using a given 
     * mode to determine the altitude at which distance is calculated. 
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @param distMode  A <code>DistanceMode</code> defining what altitude to use
     *                  in the distance calculation.
     * @return          The great circle distance between <code>shape1</code> and 
     *                  <code>shape2</code> using the given <code>DistanceMode</code>.
     */
    public static double distance(Shape3D shape1, Shape3D shape2, DistanceMode distMode) {
        return LinAlg3D.greatCircleDistance(shape1.toWorld(Vector3D.ORIGIN), 
                                            shape2.toWorld(Vector3D.ORIGIN),
                                            distMode);
    }
    
    /**
     * Computes the great circle distance between two given shapes, at a given 
     * altitude in kilometers. 
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @param altitude  The altitude, in kilometers, at which the distance will 
     *                  be computed.
     * @return          The great circle distance between <code>shape1</code> and
     *                  <code>shape2</code> at <code>altitude</code> kilometers 
     *                  above sea level.
     */
    public static double distance(Shape3D shape1, Shape3D shape2, double altitude) {
        return LinAlg3D.greatCircleDistance(shape1.toWorld(Vector3D.ORIGIN), 
                                            shape2.toWorld(Vector3D.ORIGIN),
                                            altitude);
    }
}