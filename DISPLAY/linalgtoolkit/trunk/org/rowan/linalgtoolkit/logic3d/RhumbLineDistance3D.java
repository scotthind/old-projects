package org.rowan.linalgtoolkit.logic3d;

import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.shapes3d.Shape3D;

/**
 * The <code>RhumbLineDistance3D</code> class provides great rhumb line distance 
 * query logic for vertices and shapes in the <code>Shapes3D</code> package.
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public abstract class RhumbLineDistance3D {

    /**
     * Computes the rhumb line distance between a given shape and vertex, at sea 
     * level. 
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @return          The rhumb line distance between <code>vertex</code> and 
     *                  <code>shape</code> at sea level.
     */
    public static double distance(Shape3D shape, Vector3D vertex) {
        return LinAlg3D.rhumbLineDistance(shape.toWorld(Vector3D.ORIGIN), vertex);
    }
    
    /**
     * Computes the rhumb line distance between two given shapes, at sea level.
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @return          The rhumb line distance between <code>shape1</code> and 
     *                  <code>shape2</code> at sea level.
     */
    public static double distance(Shape3D shape1, Shape3D shape2) {
        return LinAlg3D.rhumbLineDistance(shape1.toWorld(Vector3D.ORIGIN), 
                                            shape2.toWorld(Vector3D.ORIGIN));
    }
    
}
