package org.rowan.linalgtoolkit.shapes3d;

import org.rowan.linalgtoolkit.Vector;
import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>Sphere3D</code> class describes a uniform sphere in Euclidean space.
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public class Sphere3D extends Spheroid3D {
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Initializes a <code>Sphere3D</code> object at a given position, with a 
     * given radius.
     * @param position  A <code>Vector3D</code> object describing the position of 
     *                  the sphere in world coordinates.
     * @param radius    The radius of the sphere.
     */
    public Sphere3D(Vector3D position, double radius) {
        super(position, radius, radius);
    }
    
    /**
     * Initializes a <code>Sphere3D</code> object at the origin, with a given 
     * radius.
     * @param radius    The radius of the sphere.
     */
    public Sphere3D(double radius) {
        this(Vector3D.ORIGIN, radius);
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the major and minor radii of this spheroid to given values. The radii 
     * must be greater than 0.
     * @param radius    A value, greater than 0, to be set as this sphere's radius.
     * @throws IllegalArgumentException if any given radius is less than or
     *                              equal to 0.
     */
    public void setRadius(double radius) {
        setRadii(radius, radius, radius);
    }
    
    
    /*********************************************
     * MARK: Other
     *********************************************/
    
    /**
     * Determines whether the given <code>Vector3D</code> objects define a valid
     * set of sphere axes.
     * <p>
     * Overrides the <code>setAxes()</code> method of the <code>Ellipsoid3D</code> 
     * super class to ensure axes that define a valid sphere.
     * @param majorAxis         The major axis of the proposed sphere.
     * @param minorAxis         The minor axis of the proposed sphere.
     * @param intermediateAxis  The intermediate axis of the proposed sphere.
     * @return                  <code>true</code> if the given axes are non-zero 
     *                          and the length of <code>majorAxis</code>, 
     *                          <code>minorAxis</code>, and <code>intermediateAxis</code> 
     *                          are equivalent; <code>false</code> otherwise.
     */
    @Override
    public boolean validateAxes(Vector3D majorAxis, Vector3D minorAxis, Vector3D intermediateAxis) {
        // zero radius?
        return (super.validateAxes(majorAxis, minorAxis, intermediateAxis) &&
                Math.abs(majorAxis.magnitude() - minorAxis.magnitude()) < (Vector.PRECISION - 2));
    }
    
}