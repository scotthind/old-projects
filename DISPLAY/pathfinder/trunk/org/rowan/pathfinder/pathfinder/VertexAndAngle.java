package org.rowan.pathfinder.pathfinder;

import org.rowan.linalgtoolkit.Vector2D;

/**
 * Class <code>VertexAndAngle</code> is a convenience class used to store
 * vectors and the corresponding angles from a particular point relative to
 * a particular axis. Comparing two VertexAndAngle objects is done via
 * comparison of the angle.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class VertexAndAngle implements Comparable<VertexAndAngle> {
    public Vector2D vector;
    public double angle;

    public VertexAndAngle(Vector2D vector, double angle) {
        this.vector = vector;
        this.angle = angle;
    }

    /**
     * Compares this VertexAndAngle with the specified VertexAndAngle for order.
     * Returns a negative integer, zero, or a positive integer as this
     * VertexAndAngle has an angle less than, equal to, or greater than the
     * specified VertexAndAngle's angle. Note: this class has a natural
     * ordering that is inconsistent with equals.
     * @param other The VertexAndAngle to be compared.
     * @return A negative integer, zero, or a positive integer as this
     *         VertexAndAngle has an angle less than, equal to, or greater than
     *         the specified VertexAndAngle's angle.
     */
    @Override
    public int compareTo(VertexAndAngle other) {
        double dif = this.angle - other.angle;
        if (dif < 0 && dif > -1)
            dif = -1.1;
        if (dif > 0 && dif < 1) {
            dif = 1.1;
        }
        return ((int)dif);
    }
}
