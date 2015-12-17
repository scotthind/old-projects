package org.rowan.linalgtoolkit;

import java.util.Collection;

/**
 * The <code>Vector2D</code> class represents a 2D vector in Cartesian values. 
 * All created <code>Vector2D</code> objects remain immutable.
 * <p>
 * In addition to representing points, velocities, and other vector-based data, 
 * a <code>Vector2D</code> object can be used to represent 2D rotation about the 
 * origin.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public class Vector2D extends Vector {
    
    /*********************************************
     * MARK: Constants
     *********************************************/    
    
    /** A 2D zero vector. */
    public static final Vector2D ZERO_VECTOR = new Vector2D(0, 0);
    
    /** The Cartesian origin in 2D space. */
    public static final Vector2D ORIGIN = ZERO_VECTOR;
    
    /** A normalized 2D vector representing the Cartesian x axis. */
    public static final Vector2D X_AXIS = new Vector2D(1, 0);
    
    /** A normalized 2D vector representing the Cartesian y axis. */
    public static final Vector2D Y_AXIS = new Vector2D(0, 1);
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The x component of this vector. */
    private double x;
    
    /** The y component of this vector. */
    private double y;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a <code>Vector2D</code> object with given x, and y components values.
     * @param x The x component to be set in the created vector.
     * @param y The y component to be set in the created vector.
     */
    public Vector2D(double x, double y) {
        // set component values, after rounding to defined vector precision
        this.x = precisionRound(x);
        this.y = precisionRound(y);
        
        // normalize zero value components
        normalizeZeros();
    }
    
    /**
     * Creates a <code>Vector2D</code> object out of a given 3D vector, by copying 
     * the x and y components and truncating the z component.
     * @param vector    The 3D vector from which the Vector2D object will be
     *                  created.
     */
    public Vector2D(Vector3D vector) {
        // copy given x and y vector components
        this(vector.getX(), vector.getY());
    }
    
    /**
     * Creates a <code>Vector2D</code> object with given magnitude and direction.
     * <p>
     * If 0 or a zero vector are given for the magnitude or direction respectively,
     * a directionless zero vector will be created.
     * @param direction A vector describing the direction of the created vector.
     * @param magnitude The magnitude of the created vector.
     */
    public Vector2D(Vector2D direction, double magnitude) {
        // initialize with given direction vector
        this(direction.x, direction.y);
        
        // set magnitude to given value
        setMagnitude(magnitude);
    }
    
    /**
     * Creates a <code>Vector2D</code> object to represent a given angle of rotation, 
     * in radians, about the origin.
     * @param angle The angle of rotation, in radians, to be represented by the
     *              created rotation vector.
     */
    public Vector2D(double angle) {
        this(Math.cos(angle), Math.sin(angle));
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
    
    /*********************************************
     * MARK: Clonable
     *********************************************/
    
    /**
     * Creates a clone of this <code>Vector2D</code> object.
     * @return  A deep copy of this <code>Vector2D</code> object.
     */
    public Vector2D clone() {
        return new Vector2D(this.x, this.y);
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Calculates the slope of the line defined by this vector.
     * @return  The slope of the line defined by this vector; <code>NaN</code> 
     *          if the slope is undefined.
     */
    public double slope() {
        return (this.x == 0)? Double.NaN : (this.y / this.x);
    }
    
    /**
     * Calculates the magnitude of this vector.
     * @return  The magnitude of this vector.
     */
    public double magnitude() {
        // calculate magnitude using the Pythagorian theorem
        return Math.sqrt(x*x + y*y);
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
     * Determines whether this vector is perpendicular to a given 2D vector.
     * @param vector    A 2D vector proposed to be perpendicular to this vector.
     * @return          <code>true</code> if this vector is perpendicular to the 
     *                  given vector; <code>false</code> otherwise.
     */
    public boolean isPerp(Vector2D vector) {
        return (dot(vector) == 0);
    }
    
    /**
     * Determines whether this vector is parallel to a given 2D vector.
     * @param vector    A 2D vector proposed to be parallel to this vector.
     * @return          <code>true</code> if this vector is parallel to the given 
     *                  vector; <code>false</code> otherwise.
     */
    public boolean isParallel(Vector2D vector) {
        double slope1 = slope();
        double slope2 = vector.slope();
        
        // slope of two parallel vectors should be equal
        return (slope1 == slope2 || 
                (Double.isNaN(slope1) && Double.isNaN(slope2)));
    }
    
    /**
     * Calculates the angle of rotation, in radians, represented this rotation vector.
     * @return          The angle, in radians, described by this rotation vector.
     */
    public double toAngle() {
        return Math.atan2(this.y, this.x);
    }
    
    /**
     * Returns an array containing the component values of this vector.
     * @return  An array of doubles representing this vector.
     */
    public double[] toArray() {
        double[] array = {this.x, this.y};
        return array;
    }
    
    
    /*********************************************
     * MARK: Arithmetic
     *********************************************/
    
    /**
     * Creates a 2D unit vector with the same direction as this vector. If this 
     * vector is a zero vector, a zero vector is returned.
     * @return  A 2D unit vector with the same direction as this vector. A zero 
     *          vector if this vector is a zero vector.
     */
    public Vector2D unitVector() {
        // copy vector
        Vector2D unitVector = clone();
        
        // normalize and return copy
        unitVector.normalize();
        return unitVector;
    }
    
    /**
     * Calculates the vector inverse of this vector.
     * @return  A 2D vector with the same magnitude as this vector, but opposite 
     *          direction.
     */
    public Vector2D inverse() {
        return new Vector2D(-x, -y);
    }
    
    /**
     * Adds a given 2D vector to this vector. Note that adding a vector's inverse
     * to itself will yield a directionless zero vector. 
     * @param vector    A 2D vector to be added to this vector.
     * @return          The vector sum of a given vector and this vector.
     */
    public Vector2D add(Vector2D vector) {
        // add components
        double x = this.x + vector.x;
        double y = this.y + vector.y;
        
        // create and return new vector
        return new Vector2D(x, y);
    }
    
    /**
     * Subtracts a given 2D vector from this vector. Note that subtracting a 
     * vector by itself or another equivalent vector will yield a directionless 
     * zero vector.
     * @param vector    A 2D vector to be subtracted from this vector.
     * @return          The vector difference between this vector and a given vector.
     */
    public Vector2D subtract(Vector2D vector) {
        // subtract components
        double x = this.x - vector.x;
        double y = this.y - vector.y;
        
        // create and return new vector
        return new Vector2D(x, y);
    }
    
    /**
     * Multiplies this vector by a given scalar value. Note that multiplying 
     * a vector by 0.0 will yield a directionless zero vector.
     * @param value A scalar value to be multiplied by this vector.
     * @return  The product of this vector and a given scalar value.
     */
    public Vector2D multiply(double value) {
        // copy vector
        Vector2D product = clone();
        
        // multiply copied components 
        product.x *= value;
        product.y *= value;
        
        // return product
        return product;
    }
    
    /**
     * Calculates the scalar dot product of this vector and a given 2D vector.
     * @param vector    A 2D vector to be "dotted" with this vector.
     * @return          The scalar dot product of this vector and the given vector.
     */
    public double dot(Vector2D vector) {
        return (this.x * vector.x) + (this.y * vector.y);
    }
    
    /**
     * Calculates the vector cross product of this vector and a given 2D vector.
     * Note that this method will always return a 3D vector object parallel to 
     * either the positive, or negative, z axis.
     * @param vector    A 2D vector to be "crossed" with this vector.
     * @return          The vector cross product of this vector and the given vector.
     */
    public Vector3D cross(Vector2D vector) {
        // calculate z component value
        double z = (this.x * vector.y) - (vector.x * this.y);
        
        // create and return cross product
        return new Vector3D(0, 0, z);
    }
    
    /**
     * Rotates this vector by the angle described by a given 2D rotation vector,
     * about the origin.
     * @param rotVector A 2D rotation vector describing the angle of rotation 
     *                  to be applied to this vector.
     * @return          The vector that results from rotating this vector according
     *                  to <code>rotVector</code>, about the origin.
     */
    public Vector2D rotate(Vector2D rotVector) {
        double x = (this.x * rotVector.x) - (this.y * rotVector.y);
        double y = (this.x * rotVector.y) + (this.y * rotVector.x);
        return new Vector2D(x, y);
    }
    
    /**
     * Rotates this vector by the angle described by a given 2D rotation vector,
     * about a given vertex.
     * @param rotVector A 2D rotation vector describing the angle of rotation 
     *                  to be applied to this vector.
     * @param vertex    A <code>Vector2D</code> describing the vertex about which
     *                  this vector will be rotated.
     * @return          The vector that results from rotating this vector according
     *                  to <code>rotVector</code>, about <code>vertex</code>.
     */
    public Vector2D rotate(Vector2D rotVector, Vector2D vertex) {
        // translate
        Vector2D result = subtract(vertex);
        
        // rotate
        result = result.rotate(rotVector);
        
        // translate back
        return result.add(vertex);
    }
    
    /**
     * Rotates this vector by a given angle, in radians, about the origin. 
     * <p>
     * This method simply converts the given angle to a rotation vector and computes 
     * rotation according to that vector. For this reason, when rotating multiple 
     * vectors by the same angle, it is much more efficient to cache rotation 
     * vector and use the <code>rotate(Vector2D)</code> method.
     * @param angle The angle, in radians to rotate this vector.
     * @return      The vector that results from rotating this vector by the given
     *              angle, about the origin.
     */
    public Vector2D rotate(double angle) {
        return rotate(vectorForAngle(angle));
    }
    
    /**
     * Rotates this vector by a given angle, in radians, about a given vertex. 
     * <p>
     * This method simply converts the given angle to a rotation vector and computes 
     * rotation according to that vector. For this reason, when rotating multiple 
     * vectors by the same angle, it is much more efficient to cache rotation 
     * vector and use the <code>rotate(Vector2D)</code> method.
     * @param angle     The angle, in radians to rotate this vector.
     * @param vertex    A <code>Vector2D</code> describing the vertex about which
     *                  this vector will be rotated.
     * @return          The vector that results from rotating this vector
     *                  <code>angle</code> degrees, about <code>vertex</code>.
     */
    public Vector2D rotate(double angle, Vector2D vertex) {
        return rotate(vectorForAngle(angle), vertex);
    }
    
    /**
     * Calculates the distance between the point described by this vector and the
     * point described by a given 2D vector.
     * @param point A 2D vector describing a point for which the distance will
     *              be calculated.
     * @return      The distance between the point described by this vector
     *              and the point described by the given vector.
     */
    public double distance(Vector2D point) {
        return subtract(point).magnitude();
    }
    
    /**
     * Interpolates between this vector and a given 2D vector by a given interpolation
     * factor.
     * @param vector    The terminal point of interpolation.
     * @param factor    The interpolation factor. This value will be clamped to 
     *                  the range [0.0, 1.0].
     */
    public Vector2D lerp(Vector2D vector, double factor) {
        return vector.subtract(this).multiply(factor).add(this);
    }
    
    
    /*********************************************
     * MARK: Equals
     *********************************************/
    
    /**
     * Determines whether this vector is equivalent to a given object.
     * @param object    The object being compared to this vector.
     * @return          <code>true</code> if the given object is a <code>Vector2D</code> 
     *                  object equivalent to this vector; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // not a 2D vector?
        if (!(object instanceof Vector2D))
            return false;
        
        // compare x and y components
        Vector2D vect = (Vector2D)object;
        return (this.x == vect.getX() && 
                this.y == vect.getY());
    }
        
    
    /*********************************************
     * MARK: toString
     *********************************************/
    
    /**
     * Creates a string to describe this vector.
     * @return  A string that describes this vector.
     */
    public String toString() {
        return ("("+this.x+", "+this.y+")");
    }
    
    
    /*********************************************
     * MARK: Static
     *********************************************/
    
    /**
     * Creates a 2D rotation vector using a given angle, in radians.
     * @param angle The angle of rotation, in radians, to be represented by the
     *              created rotation vector.
     */
    public static Vector2D vectorForAngle(double angle) {
        return new Vector2D(Math.cos(angle), 
                            Math.sin(angle));
    }
    
    /**
     * Calculates the angle of rotation, in radians represented by a given 2D 
     * rotation vector.
     * @param rotVector A 2D rotation vector.
     * @return          The angle, in radians, described by the given rotation 
     *                  vector.
     */
    public static double toAngle(Vector2D rotVector) {
        return Math.atan2(rotVector.y, rotVector.x);
    }
    
    /**
     * Computes the average a given collection of 2D vectors.
     * @param vectors   A collection of 2D vectors.
     * @return          The vector-average of the vectors contained in <code>vectors</code>.
     */
    public static Vector2D average(Collection<Vector2D> vectors) {
        double x = 0;
        double y = 0;
        
        for (Vector2D vect : vectors) {
            x += vect.getX();
            y += vect.getY();
        }
        
        x /= vectors.size();
        y /= vectors.size();
        
        return new Vector2D(x, y);
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Normalizes this vector to a 2D unit vector with the same direction. If this 
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
        normalizeZeros();
    }
    
    /**
     * Normalizes -0.0 value components in this vector to 0.0. This method is used
     * by the <code>Vector2D</code> constructor to maintain aesthetics.
     */
    private void normalizeZeros() {
        // if value is -0.0 set it to 0.0
        this.x = (this.x == 0.0) ? 0.0 : this.x;
        this.y = (this.y == 0.0) ? 0.0 : this.y;
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
            return;
        }
        
        // calculate component values
        double currentMagnitude = magnitude();
        this.x = precisionRound(this.x * magnitude / currentMagnitude);
        this.y = precisionRound(this.y * magnitude / currentMagnitude);
    }
    
    /**
     * Sets this vector's direction to that of a given vector object. The magnitude 
     * of this vector will remain unchanged. 
     * @param direction A vector defining the desired direction for this vector.
     */
    private void setDirection(Vector2D direction) {
        // store current magnitude
        double mag = magnitude();
        
        // copy component values from the given vector
        this.x = direction.x;
        this.y = direction.y;
        
        // set magnitude to previous
        setMagnitude(mag);
    }
}

