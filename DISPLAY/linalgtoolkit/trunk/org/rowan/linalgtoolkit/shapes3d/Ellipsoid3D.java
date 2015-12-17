package org.rowan.linalgtoolkit.shapes3d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.BoundingBox3D;

/**
 * The <code>Ellipsoid3D</code> class describes a regular ellipsoid. <code>Vector3D</code>
 * objects are used to define the semi-major, semi-minor, and semi-intermediate
 * axes. That is, these vectors describe the direction and length that the axes
 * extend from the ellipsoid's center point to its hull. These axes must be perpendicular.
 *
 * @author Spence DiNicolantonio, Jonathan Palka
 * @version 1.1
 * @since 1.1
 */
public class Ellipsoid3D extends Shape3D {

    /*********************************************
     * MARK: Fields
     *********************************************/

    /** The semi-major axis of this ellipsoid. */
    private Vector3D majorAxis;

    /** The semi-minor axis of this ellipsoid. */
    private Vector3D minorAxis;

    /** The semi-intermediate axis of this ellipsoid. */
    private Vector3D intermediateAxis;


    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Initializes an <code>Ellipsoid3D</code> object at a given position, with
     * given semi-major, semi-minor, and semi-intermediate axes.
     * @param position          A <code>Vector3D</code> object describing the
     *                          position of the ellipsoid in world coordinates.
     * @param majorAxis         A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-major axis.
     * @param minorAxis         A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-minor axis.
     * @param intermediateAxis  A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-intermediate axis.
     */
    public Ellipsoid3D(Vector3D position, Vector3D majorAxis, Vector3D minorAxis, Vector3D intermediateAxis) {
        // initialize with super constructor
        super(position);

        // set axes
        setAxes(majorAxis, minorAxis, intermediateAxis);
    }

    /**
     * Initializes an <code>Ellipsoid3D</code> object at the origin, with given
     * semi-major, semi-minor, and semi-intermediate axes.
     * @param majorAxis         A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-major axis.
     * @param minorAxis         A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-minor axis.
     * @param intermediateAxis  A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-intermediate axis.
     */
    public Ellipsoid3D(Vector3D majorAxis, Vector3D minorAxis, Vector3D intermediateAxis) {
        this(Vector3D.ORIGIN, majorAxis, minorAxis, intermediateAxis);
    }

    /**
     * Initializes an <code>Ellipsoid3D</code> object at a given position, with
     * major, minor, and intermediate axes, of given radii, parallel to the Euclidean
     * x, y, and z axes, respectively.
     * @param position              A <code>Vector3D</code> object describing the
     *                              position of the ellipsoid in world coordinates.
     * @param majorRadius           The major radius of the ellipsoid.
     * @param minorRadius           The minor radius of the ellipsoid.
     * @param intermediateRadius    The intermediate radius of the ellipsoid.
     */
    public Ellipsoid3D(Vector3D position, double majorRadius, double minorRadius, double intermediateRadius) {
        this(position,
             new Vector3D(Vector3D.X_AXIS, majorRadius),
             new Vector3D(Vector3D.Y_AXIS, minorRadius),
             new Vector3D(Vector3D.Z_AXIS, intermediateRadius));
    }

    /**
     * Initializes an <code>Ellipsoid3D</code> object at the origin, with major,
     * minor, and intermediate axes of given radii, parallel to the Euclidean
     * x, y, and z axes, respectively.
     * @param majorRadius           The major radius of the ellipsoid.
     * @param minorRadius           The minor radius of the ellipsoid.
     * @param intermediateRadius    The intermediate radius of the ellipsoid.
     */
    public Ellipsoid3D(double majorRadius, double minorRadius, double intermediateRadius) {
        this(Vector3D.ORIGIN,
             new Vector3D(Vector3D.X_AXIS, majorRadius),
             new Vector3D(Vector3D.Y_AXIS, minorRadius),
             new Vector3D(Vector3D.Z_AXIS, intermediateRadius));
    }


    /*********************************************
     * MARK: Accessors
     *********************************************/

    /**
     * Returns this ellipsoid's semi-major axis vector.
     * @return  A <code>Vector3D</code> object describing this ellipsoid's
     *          semi-major axis.
     */
    public Vector3D getMajorAxis() {
        return this.majorAxis.rotate(getOrientation());
    }

    /**
     * Returns this ellipsoid's semi-minor axis vector.
     * @return  A <code>Vector3D</code> object describing this ellipsoid's
     *          semi-minor axis.
     */
    public Vector3D getMinorAxis() {
        return this.minorAxis.rotate(getOrientation());
    }

    /**
     * Returns this ellipsoid's semi-intermediate axis vector.
     * @return  A <code>Vector3D</code> object describing this ellipsoid's
     *          semi-intermediate axis.
     */
    public Vector3D getIntermediateAxis() {
        return this.intermediateAxis.rotate(getOrientation());
    }

    /**
     * Returns this ellipsoid's major radius.
     * @return  This ellipsoid's major radius.
     */
    public double getMajorRadius() {
        return this.majorAxis.magnitude();
    }

    /**
     * Returns this ellipsoid's minor radius.
     * @return  This ellipsoid's minor radius.
     */
    public double getMinorRadius() {
        return this.minorAxis.magnitude();
    }

    /**
     * Returns this ellipsoid's intermediate radius.
     * @return  This ellipsoid's intermediate radius.
     */
    public double getIntermediateRadius() {
        return this.intermediateAxis.magnitude();
    }


    /*********************************************
     * MARK: Mutators
     *********************************************/

    /**
     * Sets the major, minor, and intermediate radii of this ellipsoid to given
     * values. The radii must be greater than 0.
     * @param majorRadius           A value, greater than 0, to be set as this
     *                              ellipsoid's major radius.
     * @param minorRadius           A value, greater than 0, to be set as this
     *                              ellipsoid's minor radius.
     * @param intermediateRadius    A value, greater than 0, to be set as this
     *                              ellipsoid's intermediate radius.
     * @throws IllegalArgumentException if any given radius is less than or
     *                              equal to 0.
     */
    public void setRadii(double majorRadius, double minorRadius, double intermediateRadius) {
        setMajorRadius(majorRadius);
        setMinorRadius(minorRadius);
        setIntermediateRadius(intermediateRadius);
    }

    /**
     * Sets the major radius of this ellipsoid to a given value. The radius must
     * be greater than 0.
     * @param radius    A value, greater than 0, to be set as this ellipsoid's
     *                  major radius.
     * @throws IllegalArgumentException if the given radius is less than or
     *                  equal to 0.
     */
    public void setMajorRadius(double radius) {
        // valid radius?
        if (radius <= 0)
            throw new IllegalArgumentException("Invalid radius: must be greater than 0");

        // create and set new axis
        Vector3D newAxis = new Vector3D(this.majorAxis, radius);
        setAxes(newAxis, this.minorAxis, this.intermediateAxis);
    }

    /**
     * Sets the minor radius of this ellipsoid to a given value. The radius must
     * be greater than 0.
     * @param radius    A value, greater than 0, to be set as this ellipsoid's
     *                  minor radius.
     * @throws IllegalArgumentException if the given radius is less than or
     *                  equal to 0.
     */
    public void setMinorRadius(double radius) {
        // valid radius?
        if (radius <= 0)
            throw new IllegalArgumentException("Invalid radius: must be greater than 0");

        // create and set new axis
        Vector3D newAxis = new Vector3D(this.minorAxis, radius);
        setAxes(this.majorAxis, newAxis, this.intermediateAxis);
    }

    /**
     * Sets the major radius of this ellipsoid to a given value. The radius must
     * be greater than 0.
     * @param radius    A value, greater than 0, to be set as this ellipsoid's
     *                  major radius.
     */
    public void setIntermediateRadius(double radius) {
        // valid radius?
        if (radius <= 0)
            throw new IllegalArgumentException("Invalid radius: must be greater than 0");

        // create and set new axis
        Vector3D newAxis = new Vector3D(this.intermediateAxis, radius);
        setAxes(this.majorAxis, this.minorAxis, newAxis);
    }


    /*********************************************
     * MARK: Queries
     *********************************************/

    /**
     * Computes the surface area of this ellipsoid.
     * Used approximation from: http://en.wikipedia.org/wiki/Ellipsoid#Surface_area
     * @return  The surface area of this ellipsoid.
     */
    public double surfaceArea() {
        //Error is at most 1.061%
        double p = 1.6075;
        double sa = 4 * Math.PI * Math.pow((Math.pow(majorAxis.magnitude(), p) * Math.pow(minorAxis.magnitude(), p)
                                    + Math.pow(majorAxis.magnitude(), p) * Math.pow(intermediateAxis.magnitude(), p)
                                    + Math.pow(minorAxis.magnitude(), p) * Math.pow(intermediateAxis.magnitude(), p)) / 3, 1/p);
        return sa;
    }

    /**
     * Computes the volume of this ellipsoid.
     * @return  The volume of this ellipsoid.
     */
    public double volume() {
        double a = majorAxis.magnitude();
        double b = minorAxis.magnitude();
        double c = intermediateAxis.magnitude();

        return (4.0/3.0)*Math.PI*a*b*c;
    }

    /**
     * Computes this ellipsoid's minimum bounding box.
     * This bounding box method currently does not have a best fit solution.
     * This implementation has some small error however it is still pretty accurate
     * and inexpensive to compute.
     * @return  This ellipsoid's minimum bounding box.
     */
    public BoundingBox3D boundingBox() {   
        double x = Math.sqrt((getMajorAxis().getX()*getMajorAxis().getX()) + (getMinorAxis().getX()*getMinorAxis().getX()) + (getIntermediateAxis().getX()*getIntermediateAxis().getX()));
        double y = Math.sqrt((getMajorAxis().getY()*getMajorAxis().getY()) + (getMinorAxis().getY()*getMinorAxis().getY()) + (getIntermediateAxis().getY()*getIntermediateAxis().getY()));
        double z = Math.sqrt((getMajorAxis().getZ()*getMajorAxis().getZ()) + (getMinorAxis().getZ()*getMinorAxis().getZ()) + (getIntermediateAxis().getZ()*getIntermediateAxis().getZ()));

        Vector3D extrema = new Vector3D(x, y, z);

        LinkedList<Vector3D> points = new LinkedList();
        points.add(toWorld(extrema));
        points.add(toWorld(extrema.inverse()));

        return new BoundingBox3D(points);
    }
    

    /*********************************************
     * MARK: Other
     *********************************************/

    /**
     * Determines whether the given <code>Vector3D</code> objects define a valid
     * set of ellipsoid axes.
     * @param majorAxis         The major axis of the proposed ellipsoid.
     * @param minorAxis         The minor axis of the proposed ellipsoid.
     * @param intermediateAxis  The intermediate axis of the proposed ellipsoid.
     * @return                  <code>true</code> if the given axes are non-zero;
     *                          <code>false</code> otherwise.
     */
    public boolean validateAxes(Vector3D majorAxis, Vector3D minorAxis, Vector3D intermediateAxis) {
        // zero radius?
        return (!majorAxis.isZeroVector() &&
                !minorAxis.isZeroVector() &&
                !intermediateAxis.isZeroVector());
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Redefines this ellipsoid to have a given semi-major, semi-minor, and
     * semi-intermediate axes. This will also reset the ellipsoid's orientation.
     * @param majorAxis         A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-major axis.
     * @param minorAxis         A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-minor axis.
     * @param intermediateAxis  A <code>Vector3D</code> object describing the
     *                          ellipsoid's semi-intermediate axis.
     * @throws IllegalArguementException if the given axes do not define a valid
     *                          ellipsoid.
     */
    private void setAxes(Vector3D majorAxis, Vector3D minorAxis, Vector3D intermediateAxis) {
        // validate axes
        if (!validateAxes(majorAxis, minorAxis, intermediateAxis))
            throw new IllegalArgumentException("Invalid Axes");
        
        // fix axes to be perpendicular
        minorAxis = new Vector3D(intermediateAxis.cross(majorAxis), minorAxis.magnitude());
        intermediateAxis = new Vector3D(majorAxis.cross(minorAxis), intermediateAxis.magnitude());
        
        // set axes
        this.majorAxis = majorAxis;
        this.minorAxis = minorAxis;
        this.intermediateAxis = intermediateAxis;
    }

}