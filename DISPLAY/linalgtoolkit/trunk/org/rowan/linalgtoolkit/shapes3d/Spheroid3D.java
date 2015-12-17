package org.rowan.linalgtoolkit.shapes3d;

import org.rowan.linalgtoolkit.Vector;
import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>Spheroid3D</code> class describes a regular spheroid. <code>Vector3D</code>
 * objects are used to define the semi-major and semi-minor axes. That is, these 
 * vectors describe the direction and length that the axes extend from the spheroid's 
 * center point to its hull. These axes must be perpendicular.
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public class Spheroid3D extends Ellipsoid3D {
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Initializes a <code>Spheroid3D</code> object at a given posiiton, with
     * given semi-major and smi-minor axes.
     * @param position          A <code>Vector3D</code> object describing the 
     *                          position of the spheroid in world coordinates.
     * @param majorAxis         A <code>Vector3D</code> object describing the 
     *                          spheroid's semi-major axis.
     * @param minorAxis         A <code>Vector3D</code> object describing the
     *                          spheroid's semi-minor axis.
     */
    public Spheroid3D(Vector3D position, Vector3D majorAxis, Vector3D minorAxis) {
        // initialize with super constructor
        super(position, majorAxis, minorAxis, new Vector3D(majorAxis.cross(minorAxis), minorAxis.magnitude()));
    }
    
    /**
     * Initializes a <code>Spheroid3D</code> object at the origin, with given 
     * semi-major and semi-minor axes.
     * @param majorAxis         A <code>Vector3D</code> object describing the 
     *                          spheroid's semi-major axis.
     * @param minorAxis         A <code>Vector3D</code> object describing the
     *                          spheroid's semi-minor axis.
     */
    public Spheroid3D(Vector3D majorAxis, Vector3D minorAxis) {
        this(Vector3D.ORIGIN, majorAxis, minorAxis);
    }
    
    /**
     * Initializes a <code>Spheroid3D</code> object at a given position, with 
     * major and minor axes, of given radii, parallel to the euclidean x and y 
     * axes, respectively.
     * @param position              A <code>Vector3D</code> object describing the 
     *                              position of the spheroid in world coordinates.
     * @param majorRadius           The major radius of the spheroid.
     * @param minorRadius           The minor radius of the spheroid.
     */
    public Spheroid3D(Vector3D position, double majorRadius, double minorRadius) {
        this(position, 
             new Vector3D(Vector3D.X_AXIS, majorRadius),
             new Vector3D(Vector3D.Y_AXIS, minorRadius));
    }
    
    /**
     * Initializes a <code>Spheroid3D</code> object at the origin, with major
     * and minor axes of given radii, parallel to the euclidean x and y axes, 
     * respectively.
     * @param majorRadius           The major radius of the spheroid.
     * @param minorRadius           The minor radius of the spheroid.
     */
    public Spheroid3D(double majorRadius, double minorRadius) {
        this(Vector3D.ORIGIN, 
             new Vector3D(Vector3D.X_AXIS, majorRadius),
             new Vector3D(Vector3D.Y_AXIS, minorRadius));
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
        
    /**
     * Sets the major and minor radii of this spheroid to given values. The radii 
     * must be greater than 0.
     * @param majorRadius           A value, greater than 0, to be set as this
     *                              spheroid's major radius.
     * @param minorRadius           A value, greater than 0, to be set as this
     *                              spheroid's minor radius.
     * @throws IllegalArgumentException if any given radius is less than or
     *                              equal to 0.
     */
    public void setRadii(double majorRadius, double minorRadius) {
        setRadii(majorRadius, minorRadius, minorRadius);
    }
    
    
    /*********************************************
     * MARK: Other
     *********************************************/
    
    /**
     * Determines whether the given <code>Vector3D</code> objects define a valid
     * set of spheroid axes.
     * <p>
     * Overrides the <code>setAxes()</code> method of the <code>Ellipsoid3D</code> 
     * super class to ensure axes that define a valid spheroid.
     * @param majorAxis         The major axis of the proposed spheroid.
     * @param minorAxis         The minor axis of the proposed spheroid.
     * @param intermediateAxis  The intermediate axis of the proposed spheroid.
     * @return                  <code>true</code> if the given axes are non-zero 
     *                          and the length of <code>minorAxis</code> and
     *                          <code>intermediateAxis</code> are equivalent;
     *                          <code>false</code> otherwise.
     */
    @Override
    public boolean validateAxes(Vector3D majorAxis, Vector3D minorAxis, Vector3D intermediateAxis) {
        // zero radius?
        return (super.validateAxes(majorAxis, minorAxis, intermediateAxis) &&
                Math.abs(minorAxis.magnitude() - intermediateAxis.magnitude()) < (Vector.PRECISION - 2));
    }

}