package org.rowan.linalgtoolkit.shapes3d;

import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.BoundingBox3D;

/**
 * The <code>Point3D</code> class describes a single point in 3D Euclidean space.
 * <p>
 * A <code>Point3D</code> object is infinitesimal and thus has no area and no
 * perimeter.
 *
 * @author Spence DiNicolantonio, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class Point3D extends Shape3D {
    

    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Creates a <code>Point3D</code> object at a given position.
     * @param position  A 3D vector describing the position of the point in world
     *                  coordinates.
     */
    public Point3D(Vector3D position) {
        // initialize with super constructor
        super(position);
    }

    /**
     * Creates a <code>Point3D</code> object with given x, y, and z component values.
     * @param x The x component of the point to be created.
     * @param y The y component of the point to be created.
     * @param z The z component of the point to be created.
     */
    public Point3D(double x, double y, double z) {
        this(new Vector3D(x, y, z));
    }

    /*********************************************
     * MARK: Accessors
     *********************************************/

    /**
     * Returns the x component of this point.
     * @return  The x component of this point.
     */
    public double getX() {
        return getPosition().getX();
    }

    /**
     * Returns the y component of this point.
     * @return  The y component of this point.
     */
    public double getY() {
        return getPosition().getY();
    }

    /**
     * Returns the z component of this point.
     * @return  The z component of this point.
     */
    public double getZ() {
        return getPosition().getZ();
    }

    /*********************************************
     * MARK: Mutators
     *********************************************/

    /**
     * Sets the x component of this point to a given value.
     * @param x The x component to be set for this point.
     */
    public void setX(double x) {
        double y = getPosition().getY();
        double z = getPosition().getZ();
        setPosition(new Vector3D(x, y, z));
    }

    /**
     * Sets the y component of this point to a given value.
     * @param y The y component to be set for this point.
     */
    public void setY(double y) {
        double x = getPosition().getX();
        double z = getPosition().getZ();
        setPosition(new Vector3D(x, y, z));
    }

    /**
     * Sets the z component of this point to a given value.
     * @param z The z component to be set for this point.
     */
    public void setZ(double z) {
        double x = getPosition().getX();
        double y = getPosition().getY();
        setPosition(new Vector3D(x, y, z));
    }

    /*********************************************
     * MARK: Queries
     *********************************************/

    /**
     * Computes the surface area of this point. Note that by definition, the 
     * surface area of a point is always 0.
     * @return  The surface area of this point, which is 0 by definition.
     */
    public double surfaceArea() {
        return 0;
    }

    /**
     * Computes the volume of this point. Note that by definition, the volume of 
     * a point is always 0.
     * @return  The volume of this point, which is 0 by definition.
     */
    public double volume() {
        return 0;
    }
    
    /**
     * Computes this point's minimum bounding box.
     * @return  This point's minimum bounding box.
     */
    public BoundingBox3D boundingBox() {
        return new BoundingBox3D(getPosition(), getPosition());
    }
    
}