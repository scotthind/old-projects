package org.rowan.linalgtoolkit.transform3d;

import org.rowan.linalgtoolkit.Vector3D;

/**
 * <code>Quaternion</code>s are used to represent 3-dimensional rotation. This 
 * allows smooth appending and alteration of rotations while avoiding the gimbal 
 * lock. A quaternion is defined by a vector component and a scalar (w) component. 
 * This can also be thought of as four scalar (x, y, z, w) components.
 * <p>
 * Rotational calculations using quaternions is simplified when the quaternion
 * is normalized to a unit quaternion. As this <code>Quaternion</code> objects 
 * is intended to be used for rotation only, all created <code>Quaternion</code> 
 * objects are automatically normalized and remain immutable.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public class Quaternion {
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /* The identity quaternion. */
    public static final Quaternion IDENTITY = new Quaternion(0, 0, 0, 1);
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The vector component of this Quaternion. */
    private double x, y, z;
    
    /** The scalar component of this Quaternion. */
    private double w;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/    
    
    /**
     * Creates a <code>Quaternion</code> object with given x, y, z, and w components. 
     * The created <code>Quaternion</code> will be normalized automatically.
     * @param x The x component of the quaternion.
     * @param y The y component of the quaternion.
     * @param z The z component of the quaternion.
     * @param w The w component of the quaternion.
     */
    public Quaternion(double x, double y, double z, double w) {
        // set component values
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        
        // normalize
        normalize();
    }
    
    /**
     * Creates a <code>Quaternion</code> object with given given vector and scalar 
     * components. The created <code>Quaternion</code> will be normalized automatically.
     * @param vector    A <code>Vector3D</code> describing the vector component 
     *                  of the quaternion.
     * @param scalar    The scalar component of the quaternion.
     */
    public Quaternion(Vector3D vector, double scalar) {
        // call designated constructor
        this(vector.getX(), vector.getY(), vector.getZ(), scalar);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/    
    
    /**
     * Returns the x component of this quaternion.
     * @return  The x component of this quaternion.
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * Returns the y component of this quaternion.
     * @return  The y component of this quaternion.
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * Returns the z component of this quaternion.
     * @return  The z component of this quaternion.
     */
    public double getZ() {
        return this.z;
    }
    
    /**
     * Returns the w component of this quaternion.
     * @return  The w component of this quaternion.
     */
    public double getW() {
        return this.w;
    }
    
    /**
     * Returns the scalar component of this quaternion. This is the same as the 
     * w component.
     * @return  The scalar component of this quaternion.
     */
    public double scalarComponent() {
        return this.w;
    }
    
    /**
     * Returns the vector component of this quaternion. This is a <code>Vector3D</code> 
     * object created using the x, y, and z components of this quaternion.
     * @return  A <code>Vector3D</code> describing the vector component of this 
     *          quaternion.
     */
    public Vector3D vectorComponent() {
        return new Vector3D(this.x, this.y, this.z);
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/    
    
    /**
     * Computes the magnitude of this quaternion.
     * @return  The magnitude of this quaternion.
     */
    public double magnitude() {
        // calculate magnitude using the Pythagorian theorem
        return Math.sqrt(x*x + y*y + z*z + w*w);
    }
    
    /**
     * Determines whether this quaternion is the identity quaternion. The identity 
     * quaternion is a unit quaternion with scalar component equal to 1.
     * @return  <code>true</code> if this quaternion is the identity quaternion;
     *          <code>false</code> otherwise.
     */
    public boolean isIdentity() {
        return (this.x == 0.0 &&
                this.y == 0.0 &&
                this.z == 0.0 &&
                this.w == 1.0);
    }
    
    /**
     * Determines whether this quaternion is a zero quaternion. A zero quaternion 
     * is a quaternion with 0 magnitude (all component values equal 0). The rotation
     * represented by a zero quaternion is undefined.
     * @return  <code>true</code> if this quaternion is a zero quaternion; 
     *          <code>false</code> otherwise.
     */
    public boolean isZeroQuaternion() {
        return (magnitude() == 0);
    }
    
    /**
     * Determines whether this quaternion is a unit quaternion. A unit quaternion 
     * is a quaternion with magnitude of 1.0.
     * @return  <code>true</code> if this quaternion is a unit quaternion; 
     *          <code>false</code> otherwise.
     */
    public boolean isUnitQuaternion() {
        return (magnitude() == 1);
    }
    
    
    /*********************************************
     * MARK: Arithmetic
     *********************************************/    
    
    /**
     * Creates a unit quaternion with the same direction as this quaternion. If 
     * this quaternion is a zero quaternion, a zero quaternion is returned.
     * @return  A unit quaternion with the same direction as this quaternion. A 
     *          zero quaternion if this quaternion is a zero quaternion.
     */
    public Quaternion unitQuaternion() {
        // copy quaternion
        Quaternion unitQuaternion = new Quaternion(this.x, this.y, this.z, this.w);
        
        // normalize and return copy
        unitQuaternion.normalize();
        return unitQuaternion;
    }
    
    /**
     * Calculates the quaternion inverse of this quaternion.
     * @return  A <code>Quaternion</code> with the same magnitude as this quaternion, 
     *          but opposite direction.
     */
    public Quaternion inverse() {
        // create a new quaternion with an inverse vector component
        return new Quaternion(-this.x, -this.y, -this.z, this.w);
    }
    
    /**
     * Multiplies this quaternion by a given quaternion. 
     * @param quaternion    A <code>Quaternion</code> object to be multiplied by 
     *                      this Quaternion.
     * @return              The product of this quaternion and the given quaternion.
     */
    public Quaternion multiply(Quaternion quaternion) {
        // calculate component values
        double x = (this.w * quaternion.getX()) + (this.x * quaternion.getW()) + (this.y * quaternion.getZ()) - (this.z * quaternion.getY());
        double y = (this.w * quaternion.getY()) - (this.x * quaternion.getZ()) + (this.y * quaternion.getW()) + (this.z * quaternion.getX());
        double z = (this.w * quaternion.getZ()) + (this.x * quaternion.getY()) - (this.y * quaternion.getX()) + (this.z * quaternion.getW());
        double w = (this.w * quaternion.getW()) - (this.x * quaternion.getX()) - (this.y * quaternion.getY()) - (this.z * quaternion.getZ());
        
        // create and return quaternion product using calculated component values
        return new Quaternion(x, y, z, w);
    }
    
    
    /*********************************************
     * MARK: toString
     *********************************************/    
    
    /**
     * Creates a string to describe this quaternion.
     * @return  A string that describes this quaternion.
     */
    @Override
    public String toString() {
        return ("("+this.x+", "+this.y+", "+this.z+", "+this.w+")");
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/    
    
    /**
     * Normalizes this quaternion to a unit quaternion with the same direction. 
     * If this quaternion is a zero quaternion, this method has no effect.
     */
    private void normalize() {
        // zero quaternion?
        if (isZeroQuaternion())
            return;
        
        // normalize to magnitude of 1.0
        double magnitude = magnitude();
        this.x /= magnitude;
        this.y /= magnitude;
        this.z /= magnitude;
        this.w /= magnitude;
        
        // normalize zeros
        normalizeZeros();
    }
    
    /**
     * Normalizes -0.0 value components in this quaternion to 0.0. This method is used
     * by the quaternion constructor to maintain aesthetics.
     */
    private void normalizeZeros() {
        // if value is -0.0 set it to 0.0
        this.x = (this.x == 0.0) ? 0.0 : this.x;
        this.y = (this.y == 0.0) ? 0.0 : this.y;
        this.z = (this.z == 0.0) ? 0.0 : this.z;
        this.w = (this.w == 0.0) ? 0.0 : this.w;
    }
}

