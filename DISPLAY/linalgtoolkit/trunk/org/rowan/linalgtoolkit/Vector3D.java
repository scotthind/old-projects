package org.rowan.linalgtoolkit;

import java.util.Collection;
import org.rowan.linalgtoolkit.transform3d.Rotation;
import org.rowan.linalgtoolkit.transform3d.Matrix;

/**
 * The <code>Vector3D</code> class represents a 3D vector in Euclidean values.
 * All created <code>Vector3D</code> objects remain immutable.
 * 
 * @author Spence DiNicolantonio, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class Vector3D extends Vector {
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** A 3D zero vector. */
    public static final Vector3D ZERO_VECTOR = new Vector3D(0, 0, 0);
    
    /** The Euclidean origin in 3D space. */
    public static final Vector3D ORIGIN = ZERO_VECTOR;
    
    /** A normalized 3D vector representing the Euclidean x axis. */
    public static final Vector3D X_AXIS = new Vector3D(1, 0, 0);
    
    /** A normalized 3D vector representing the Euclidean y axis. */
    public static final Vector3D Y_AXIS = new Vector3D(0, 1, 0);
    
    /** A normalized 3D vector representing the Euclidean z axis. */
    public static final Vector3D Z_AXIS = new Vector3D(0, 0, 1);
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The x component of this vector. */
    private double x;
    
    /** The y component of this vector. */
    private double y;
    
    /** The z component of this vector. */
    private double z;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a Vector3D object with given x, y, and z components values.
     * @param x The x component to be set in the created vector.
     * @param y The y component to be set in the created vector.
     * @param z The z component to be set in the created vector.
     */
    public Vector3D(double x, double y, double z) {
        // set component values, after rounding to defined component precision
        this.x = precisionRound(x);
        this.y = precisionRound(y);
        this.z = precisionRound(z);
        
        // normalize zero value components
        normalizeZeros();
    }
    
    /**
     * Creates a <code>Vector3D</code> object out of a given 2D vector, by copying 
     * the x and y components and setting the z component to 0.
     * @param vector    The 2D vector from which the <code>Vector3D</code> object 
     *                  will be created.
     */
    public Vector3D(Vector2D vector) {
        this(vector.getX(), vector.getY(), 0);
    }
    
    /**
     * Creates a <code>Vector3D</code> object with given magnitude and direction.
     * <p>
     * If 0 or a zero vector are given for the magnitude or direction respectively,
     * a directionless zero vector will be created.
     * @param direction A vector describing the direction of the created vector.
     * @param magnitude The magnitude of the created vector.
     */
    public Vector3D(Vector3D direction, double magnitude) {
        // initialize with given direction vector
        this(direction.x, direction.y, direction.z);
        
        // set magnitude to given value
        setMagnitude(magnitude);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the x component of this vector.
     * @return  The x component of this vector.
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * Returns the y component of this vector.
     * @return  The y component of this vector.
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * Returns the z component of this vector.
     * @return  The z component of this vector.
     */
    public double getZ() {
        return this.z;
    }
    
    
    /*********************************************
     * MARK: Clonable
     *********************************************/
    
    /**
     * Creates a clone of this <code>Vector3D</code> object.
     * @return  A deep copy of this <code>Vector3D</code> object.
     */
    public Vector3D clone() {
        return new Vector3D(this.x, this.y, this.z);
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/

    /**
     * Calculates the slopes of this vector in the xy, yz, and zx planes. These
     * slopes are represented by an instance of <code> Vector3D</code>, which
     * contains slopes in that order.
     * @return  An instance of <code>Vector3D</code>, which contains the slopes
     *          of this vector in the xy, yz, and zx planes.
     */
    public Vector3D slope() {
        double slopeX = (this.x == 0)? Double.NaN : (this.y / this.x);
        double slopeY = (this.y == 0)? Double.NaN : (this.z / this.y);
        double slopeZ = (this.z == 0)? Double.NaN : (this.x / this.z);
        return new Vector3D(slopeX, slopeY, slopeZ);
    }

    /**
     * Calculates the magnitude of this vector.
     * @return  The magnitude of this vector.
     */
    public double magnitude() {
        // calculate magnitude using the Pythagorian theorem
        return Math.sqrt(x*x + y*y + z*z);
    }
    
    /**
     * Determines whether this vector is a zero vector. A zero vector is a 
     * directionless vector with 0 magnitude (all component values equal 0).
     * @return  <code>true</code> if this vector is a zero vector; <code>false</code>
     *          otherwise.
     */
    public boolean isZeroVector() {
        return (magnitude() == 0);
    }
    
    /**
     * Determines whether this vector is a unit vector. A unit vector is a vector
     * with magnitude of 1.0.
     * @return  <code>true</code> if this vector is a unit vector; <code>false</code>
     *          otherwise.
     */
    public boolean isUnitVector() {
        return (magnitude() == 1);
    }
    
    /**
     * Determines whether this vector is perpendicular to a given 3D vector.
     * @param vector    A 3D vector proposed to be perpendicular to this vector.
     * @return          <code>true</code> if this vector is perpendicular to the 
     *                  given vector; <code>false</code> otherwise.
     */
    public boolean isPerp(Vector3D vector) {
        return (dot(vector) == 0);
    }
    
    /**
     * Determines whether this vector is parallel to a given 3D vector.
     * @param vector    A 3D vector proposed to be parallel to this vector.
     * @return          <code>true</code> if this vector is parallel to the given 
     *                  vector; <code>false</code> otherwise.
     */
    public boolean isParallel(Vector3D vector) {
        return cross(vector).isZeroVector();
        
    }
    
    /**
     * Returns an array containing the component values of this vector.
     * @return  An array of doubles representing this vector.
     */
    public double[] toArray() {
        double[] array = {this.x, this.y, this.z};
        return array;
    }
    
    
    /*********************************************
     * MARK: Arithmetic
     *********************************************/
    
    /**
     * Creates a 3D unit vector with the same direction as this vector. If this 
     * vector is a zero vector, a zero vector is returned.
     * @return  A 3D unit vector with the same direction as this vector. A zero 
     *          vector if this vector is a zero vector.
     */
    public Vector3D unitVector() {
        // copy vector
        Vector3D unitVector = clone();
        
        // normalize and return copy
        unitVector.normalize();
        return unitVector;
    }
    
    /**
     * Calculates the vector inverse of this vector.
     * @return  A 3D vector with the same magnitude as this vector, but opposite 
     *          direction.
     */
    public Vector3D inverse() {
        return new Vector3D(-x, -y, -z);
    }
    
    /**
     * Adds a given 3D vector to this vector. Note that adding a vector's inverse
     * to itself will yield a directionless zero vector. 
     * @param vector    A 3D vector to be added to this vector.
     * @return          The vector sum of a given vector and this vector.
     */
    public Vector3D add(Vector3D vector) {
        // add components
        double x = this.x + vector.x;
        double y = this.y + vector.y;
        double z = this.z + vector.z;
        
        // create and return new vector
        return new Vector3D(x, y, z);
    }
    
    /**
     * Subtracts a given 3D vector from this vector. Note that subtracting a 
     * vector by itself or another equivalent vector will yield a directionless 
     * zero vector.
     * @param vector    A 3D vector to be subtracted from this vector.
     * @return          The vector difference between this vector and a given vector.
     */
    public Vector3D subtract(Vector3D vector) {
        // subtract components
        double x = this.x - vector.x;
        double y = this.y - vector.y;
        double z = this.z - vector.z;
        
        // create and return new vector
        return new Vector3D(x, y, z);
    }
    
    /**
     * Multiplies this vector by a given scalar value. Note that multiplying 
     * a vector by 0.0 will yield a directionless zero vector.
     * @param value A scalar value to be multiplied by this vector.
     * @return      The product of this vector and a given scalar value.
     */
    public Vector3D multiply(double value) {
        // copy vector
        Vector3D product = new Vector3D(this.x, this.y, this.z);
        
        // multiply copied components 
        product.x *= value;
        product.y *= value;
        product.z *= value;
        
        // return product
        return product;
    }
    
    /**
     * Calculates the scalar dot product of this vector and a given 3D vector.
     * @param vector    A 3D vector to be "dotted" with this vector.
     * @return          The scalar dot product of this vector and the given vector.
     */
    public double dot(Vector3D vector) {
        return (this.x * vector.x) + (this.y * vector.y) + (this.z * vector.z);
    }
    
    /**
     * Calculates the vector cross product of this vector and a given 3D vector.
     * @param vector    A 3D vector to be "crossed" with this vector.
     * @return          The vector cross product of this vector and the given vector.
     */
    public Vector3D cross(Vector3D vector) {
        // calculate component values
        double x = (this.y * vector.z) - (vector.y * this.z);
        double y = (this.z * vector.x) - (vector.z * this.x);
        double z = (this.x * vector.y) - (vector.x * this.y);
        
        // create and return cross product
        return new Vector3D(x, y, z);
    }
    
    
    /**
     * Rotates this vector by a given amount, in radians, about the origin. 
     * @param rotation  A <code>Rotation</code> object describing the rotation 
     *                  to be applied to this vector.
     * @return          The result of rotating this Vector by the given amount.
     */
    public Vector3D rotate(Rotation rotation) {
        // zero vector?
        if (isZeroVector())
            return this;
        
        // store vector magnitude
        double mag = magnitude();
        
        // get matrix from given rotation 
        Matrix matrix = rotation.toMatrix();
		
		// multiply matrix by given vector
		Vector3D result = matrix.multiply(this);
        
        // reset magnitude to account for computational error
        result.setMagnitude(mag);
        
        return result;
    }
    
    /**
     * Rotates this vector by a given amount, about a given vertex. 
     * @param rotation  A <code>Rotation</code> object describing the rotation 
     *                  to be applied to this vector.
     * @param vertex    A <code>Vector3D</code> describing the vertex about which
     *                  this vector will be rotated.
     * @return          The result of rotating this Vector by the given amount.
     */
    public Vector3D rotate(Rotation rotation, Vector3D vertex) {
        // translate
        Vector3D result = subtract(vertex);
        
        // rotate
        result = rotate(rotation);
        
        // translate back
        return result.add(vertex);
    }

    /**
     * Calculates the distance between the point described by this vector and the
     * point described by a given 3D vector.
     * @param point A 3D vector describing a point for which the distance will
     *              be calculated.
     * @return      The distance between the point described by this vector
     *              and the point described by the given vector.
     */
    public double distance(Vector3D point) {
        double deltaX = x - point.getX();
        double deltaY = y - point.getY();
        double deltaZ = z - point.getZ();
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));
    }
    
    /**
     * Interpolates between this vector and a given 3D vector by a given interpolation
     * factor.
     * @param vector    The terminal point of interpolation.
     * @param factor    The interpolation factor. This value will be clamped to 
     *                  the range [0.0, 1.0].
     */
    public Vector3D lerp(Vector3D vector, double factor) {
        return vector.subtract(this).multiply(factor).add(this);
    }
    
    
    /*********************************************
     * MARK: Equals
     *********************************************/
    
    /**
     * Determines whether this vector is equivalent to a given object.
     * @param object    The object being compared to this vector.
     * @return          <code>true</code> if the given object is a <code>Vector3D</code> 
     *                  object equivalent to this vector; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // not a 3D vector?
        if (!(object instanceof Vector3D))
            return false;
        
        // compare x, y, and z components
        Vector3D vect = (Vector3D)object;
        return (this.x == vect.getX() && 
                this.y == vect.getY() && 
                this.z == vect.getZ());
    }
    
    
    /*********************************************
     * MARK: toString
     *********************************************/
    
    /**
     * Creates a string to describe this vector.
     * @return  A string that describes this vector.
     */
    public String toString() {
        return ("("+this.x+", "+this.y+", "+this.z+")");
    }
    
    
    /*********************************************
     * MARK: Static
     *********************************************/
    
    /**
     * Computes the average a given collection of 3D vectors.
     * @param vectors   A collection of 3D vectors.
     * @return          The vector-average of the vectors contained in <code>vectors</code>.
     */
    public static Vector3D average(Collection<Vector3D> vectors) {
        double x = 0;
        double y = 0;
        double z = 0;
        
        for (Vector3D vect : vectors) {
            x += vect.getX();
            y += vect.getY();
            z += vect.getZ();
        }
        
        x /= vectors.size();
        y /= vectors.size();
        z /= vectors.size();
        
        return new Vector3D(x, y, z);
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Normalizes this vector to a 3D unit vector with the same direction. If this 
     * vector is a zero vector, this method has no effect.
     */
    private void normalize() {
        // zero vector?
        if (isZeroVector())
            return;
        
        // normalize to magnitude of 1.0
        double magnitude = magnitude();
        this.x = precisionRound(this.x / magnitude);
        this.y = precisionRound(this.y / magnitude);
        this.z = precisionRound(this.z / magnitude);
        normalizeZeros();
    }
    
    /**
     * Normalizes -0.0 value components in this vector to 0.0. This method is used
     * by the <code>Vector3D</code> constructor to maintain aesthetics.
     */
    private void normalizeZeros() {
        // if value is -0.0 set it to 0.0
        this.x = (this.x == 0.0) ? 0.0 : this.x;
        this.y = (this.y == 0.0) ? 0.0 : this.y;
        this.z = (this.z == 0.0) ? 0.0 : this.z;
    }
    
    /**
     * Sets this vector's magnitude to a given value. Note that setting the magnitude
     * to 0.0 will result in a directionless zero vector. Assuming the given 
     * magnitude is not 0.0, this vector's direction will be maintained.
     * @param magnitude The desired magnitude for this vector.
     */
    private void setMagnitude(double magnitude) {
        // zero magnitude?
        if (magnitude == 0.0) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
            return;
        }
        
        // calculate component values
        double currentMagnitude = magnitude();
        this.x = precisionRound(this.x * magnitude / currentMagnitude);
        this.y = precisionRound(this.y * magnitude / currentMagnitude);
        this.z = precisionRound(this.z * magnitude / currentMagnitude);
    }
    
    /**
     * Sets this vector's direction to that of a given vector object. The magnitude 
     * of this vector will remain unchanged.
     * @param direction A vector defining the desired direction for this vector.
     */
    private void setDirection(Vector3D direction) {
        // store current magnitude
        double mag = magnitude();
        
        // copy component values from the given vector
        this.x = direction.x;
        this.y = direction.y;
        this.z = direction.z;
        
        // set magnitude to previous
        setMagnitude(mag);
    }
}

