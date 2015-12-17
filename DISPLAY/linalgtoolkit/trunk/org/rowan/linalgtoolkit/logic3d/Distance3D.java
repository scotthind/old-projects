package org.rowan.linalgtoolkit.logic3d;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.shapes3d.Shape3D;

/**
 * The <code>Distance3D</code> class provides distance query logic for vertices
 * and shapes in the <code>Shapes3D</code> package.
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public abstract class Distance3D {
    
    /**
     * Computes the distance between a given shape and vertex.
     * @param shape     A <code>Shape3D</code> object.
     * @param vertex    A <code>Vector3D</code> describing a vertex.
     * @return          The distance between <code>shape</code> and <code>vertex</code>.
     */
    public static double distance(Shape3D shape, Vector3D vertex) {
        Vector3D center = shape.toWorld(Vector3D.ORIGIN);
        return center.distance(vertex);
    }
    
    /**
     * Computes the distance between two given shapes.
     * @param shape1    A <code>Shape3D</code> object.
     * @param shape2    A <code>Shape3D</code> object.
     * @return          The distance between <code>shape1</code> and <code>shape2</code>.
     */
    public static double distance(Shape3D shape1, Shape3D shape2) {
        Vector3D p1 = shape1.toWorld(Vector3D.ORIGIN);
        Vector3D p2 = shape2.toWorld(Vector3D.ORIGIN);
        
        return p1.distance(p2);
    }
}