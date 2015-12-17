package org.rowan.linalgtoolkit.transform3d;

import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>Rotation</code> class represents a 3-dimensional rotation. The 
 * rotation is stored internally using a normalized Quaternion. However, accessor 
 * methods are provided to retrieve the defined rotation in terms of Euler angles,
 * axis/angle, and rotation matrix.
 * <p>
 * For Euler angles, the NASA standard y, z, x (yaw, roll, pitch) order convention
 * is used. In other words, all Euler based rotations are applied in the sequential 
 * order: yaw, roll, pitch.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public class Rotation {
    
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The identity rotation (no rotation). */
    public static final Rotation IDENTITY = new Rotation(Quaternion.IDENTITY);
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The quaternion defining this Rotation. */
    private Quaternion quaternion;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/    
    
    /**
     * Designated Constructor.
     * <p>
     * Creates a Rotation with a given quaternion.
     * @param quaternion    The quaternion rotation to define the created Rotation.
     */
    public Rotation(Quaternion quaternion) {
        this.quaternion = quaternion;
    }
    
    /**
     * Creates a Rotation with a given set of Euler angles.
     * @param pitch The degree of rotation, in radians, about the x axis.
     * @param yaw   The degree of rotation, in radians, about the y axis.
     * @param roll  The degree of rotation, in radians, about the z axis.
     */
    public Rotation(double pitch, double yaw, double roll) {
        // convert given angles to Quaternion and call super constructor
        this(RotationConversion.toQuaternion(pitch, yaw, roll));
    }
    
    /**
     * Creates a Rotation with a given axis and angle of rotation.
     * @param axis  The axis about which the rotation will occur.
     * @param angle The degree of rotation, in radians, about the given axis.
     */
    public Rotation(Vector3D axis, double angle) {
        // convert given axis/angle to Quaternion and call super constructor
        this(RotationConversion.toQuaternion(axis, angle));
    }
    
    /**
     * Creates a Rotation with a given rotation matrix.
     * @param matrix    The rotation matrix defining the rotation.
     */
    public Rotation(Matrix matrix) {
        // convert given matrix to Quaternion and call super constructor
        this(RotationConversion.toQuaternion(matrix));
    }
    
    /**
     * Creates a Rotation that will achieve a given local y and z axis when applied
     * to a world coordinate system.
     * @param yAxis The local y axis vector desired after applying the created 
     *              Rotation.
     * @param zAxis The local z axis vector desired after applying the created
     *              Rotation.
     */
    public Rotation(Vector3D yAxis, Vector3D zAxis) {
        // normalize given axes
        yAxis = yAxis.unitVector();
        zAxis = zAxis.unitVector();
        
        // find X vector (Y x Z)
        Vector3D xAxis = yAxis.cross(zAxis);
        
        // recompute Y (Z x X) in case Y and Z were not perpendicular
        yAxis = zAxis.cross(xAxis);
        
        // normalize X and Y axes
        xAxis = xAxis.unitVector();
        yAxis = yAxis.unitVector();
        
        // create rotation matrix
        double[][] matrixData = {
            {xAxis.getX(),    yAxis.getX(),    zAxis.getX(),    0.0},
            {xAxis.getY(),    yAxis.getY(),    zAxis.getY(),    0.0},
            {xAxis.getZ(),    yAxis.getZ(),    zAxis.getZ(),    0.0},
            {0.0,            0.0,            0.0,            1.0}
        };
        Matrix matrix = new Matrix(matrixData);
        
        // extract quaternion from the rotation matrix
        this.quaternion = RotationConversion.toQuaternion(matrix);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the Quaternion equivalent to this Rotation.
     * @return  The Quaternion equivalent to this Rotation.
     */
    public Quaternion getQuaternion() {
        return this.quaternion;
    }
    
    /**
     * Returns the axis of rotation, in terms of a single axis and angle of rotation,
     * defined by this Rotation.
     * @return  The axis of rotation.
     */
    public Vector3D getAxis() {
        return RotationConversion.toAxisAngleAxis(this.quaternion);
    }
    
    /**
     * Returns the angle of rotation, in terms of a single axis and angle of rotation,
     * defined by this Rotation.
     * @return  The angle of rotation, in radians.
     */
    public double getAngle() {
        return RotationConversion.toAxisAngleAngle(this.quaternion);
    }
    
    /**
     * Returns the set of Euler angles defined by this Rotation.
     * @return  An array containing the appropriate Euler angles, in radians, in 
     *          the order: pitch, yaw, roll (x, y, z).
     */
    public double[] getEulerAngles() {
        return RotationConversion.toEuler(this.quaternion);
    }
    
    /**
     * Returns the pitch angle defined by this Rotation.
     * @return  The degree of rotation, in radians, defined by this Rotation, about 
     *          the x axis.
     */
    public double getPitch() {
        double[] angles = RotationConversion.toEuler(this.quaternion);
        return angles[0];
    }
    
    /**
     * Returns the yaw angle defined by this Rotation.
     * @return  The degree of rotation, in radians, defined by this Rotation, about 
     *          the y axis.
     */
    public double getYaw() {
        double[] angles = RotationConversion.toEuler(this.quaternion);
        return angles[1];
    }
    
    /**
     * Returns the roll angle defined by this Rotation.
     * @return  The degree of rotation, in radians, defined by this Rotation, about 
     *          the z axis.
     */
    public double getRoll() {
        double[] angles = RotationConversion.toEuler(this.quaternion);
        return angles[2];
    }
    
    /**
     * Creates and returns a rotation matrix equivalent to this Rotation.
     * @return  the matrix equivalent of this Rotation.
     */
    public Matrix toMatrix() {
        return RotationConversion.toMatrix(this.quaternion);
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Redefines this Rotation to a given Quaternion rotation. Note that 
     * quaternion-based rotation requires the quaternion to be normalized. Thus, 
     * rotation cannot be defined by a zero quaternion.
     * @param quaternion    The quaternion defining the rotation.
     * @throws              IllegalArgumentException if the given Quaternion is 
     *                      a zero quaternion.
     */
    public void setQuaternion(Quaternion quaternion) {
        // zero quaternion?
        if (quaternion.isZeroQuaternion())
            throw new IllegalArgumentException("Rotation cannot be defined by a zero quaternion.");

        this.quaternion = quaternion;
    }
    
    /**
     * Redefines this Rotation to be a given angle of rotation, in degrees, about
     * a given axis. Note that if the given angle equals 0 degrees, or the given
     * axis is a zero vector, then this Rotation will be set to no rotation (0
     * degrees about a zero vector).
     * @param axis  The axis about which the rotation will occur.
     * @param angle The degree of rotation, in radians, about the given axis.
     */
    public void setAxisAngle(Vector3D axis, double angle) {
        this.quaternion = RotationConversion.toQuaternion(axis, angle);
    }
    
    /**
     * Redefines this Rotation to be about a given axis. The degree of rotation,
     * in terms of a single axis and angle of rotation, will remain unchanged.
     * @param axis  The axis about which rotation will occur.
     */
    public void setAxis(Vector3D axis) {
        setAxisAngle(axis, getAngle());
    }
    
    /**
     * Redefines this Rotation's magnitude to a given angle. The axis, in terms
     * of a single axis and angle of rotation, will remain unchanged.
     * <p>
     * Note that when the Rotation defines no rotation (0 degrees), the axis of 
     * rotation is a zero vector. For this reason, changing the rotation angle 
     * from 0 to a non-zero value will have no effect without explicitly setting 
     * the axis of rotation.
     * @param angle The degree of rotation, in radians, about the current axis.
     */
    public void setAngle(double angle) {
        setAxisAngle(getAxis(), angle);
    }
    
    /**
     * Redefines this Rotation to that which is defined by a given set of Euler
     * angles.
     * @param pitch The degree of rotation, in radians, about the x axis.
     * @param yaw   The degree of rotation, in radians, about the y axis.
     * @param roll  The degree of rotation, in radians, about the z axis.
     */
    public void setEulerAngles(double pitch, double yaw, double roll) {
        this.quaternion = RotationConversion.toQuaternion(pitch, yaw, roll);
    }
    
    /**
     * Sets the degree of pitch rotation defined by this Rotation to a given value.
     * @param angle The degree of rotation, in degrees, about the x axis.
     */
    public void setPitch(double angle) {
        setEulerAngles(angle, getYaw(), getRoll());
    }
    
    /**
     * Sets the degree of yaw rotation defined by this Rotation to a given value.
     * @param angle The degree of rotation, in degrees, about the y axis.
     */
    public void setYaw(double angle) {
        setEulerAngles(getPitch(), angle, getRoll());
    }
    
    /**
     * Sets the degree of roll rotation defined by this Rotation to a given value.
     * @param angle The degree of rotation, in degrees, about the z axis.
     */
    public void setRoll(double angle) {
        setEulerAngles(getPitch(), getYaw(), angle);
    }
    
    
    /*********************************************
     * MARK: Arithmetic
     *********************************************/
    
    /**
     * Calculates the rotational inverse of this Rotation. 
     * @return  A Rotation with the same magnitude as this Rotation, but opposite
     *          direction.
     */
    public Rotation inverse() {
        return new Rotation(this.quaternion.inverse());
    }
    
    /**
     * Calculates the result of concatenating this Rotation with a given Rotation.
     * @param rotation  The rotation to be appended to this Rotation.
     * @return          The Rotation resulting from the concatenation of this 
     *                  Rotation and the given Rotation.
     */
    public Rotation append(Rotation rotation) {
        // multiply this rotation's quaternion with the given rotation's quaternion
        Quaternion product = this.quaternion.multiply(rotation.getQuaternion());
        
        // create and return new Rotation object using the calculated quaternion
        return new Rotation(product);
    }
    
    /**
     * Interpolates between this Rotation and a given Rotation by a given 
     * interpolation factor.
     * @param rotation  The terminal point of interpolation.
     * @param factor    The interpolation factor. This value will be clamped to 
     *                  the range [0.0, 1.0].
     */
    public Rotation slerp(Rotation rotation, double factor) {
        // store quaternion components for easy access
        Quaternion startQuat = this.quaternion;
        Quaternion endQuat = rotation.getQuaternion();
        double x1 = startQuat.getX();
        double y1 = startQuat.getY();
        double z1 = startQuat.getZ();
        double w1 = startQuat.getW();
        double x2 = endQuat.getX();
        double y2 = endQuat.getY();
        double z2 = endQuat.getZ();
        double w2 = endQuat.getW();
        
        // calculate angle between start and end
        double cosHalfTheta = w1*w2 + x1*x2 + y1*y2 + z1 * z2;
        
        // if start=end or start=-end, we can return start
        if (Math.abs(cosHalfTheta) >= 1.0) 
            return this;
        
        // calculate temporary values
        double halfTheta = Math.acos(cosHalfTheta);
        double sinHalfTheta = Math.sqrt(1.0 - cosHalfTheta*cosHalfTheta);
        
        // if theta is 180°, then the result is not fully defined
        // we could rotate around any axis normal to start or end
        if (Math.abs(sinHalfTheta) < 0.001) {
            Vector3D v1 = startQuat.vectorComponent();
            Vector3D v2 = endQuat.vectorComponent();
            Vector3D vect = v1.add(v2).multiply(0.5);
            double scalar = (w1 + w2) * 0.5;
            return new Rotation(new Quaternion(vect, scalar));
        }
        
        double ratioA = Math.sin((1-factor) * halfTheta) / sinHalfTheta;
        double ratioB = Math.sin(factor * halfTheta) / sinHalfTheta;
        
        
        // compute and return quaternion
        Vector3D v1 = startQuat.vectorComponent();
        Vector3D v2 = endQuat.vectorComponent();
        Vector3D vect = v1.multiply(ratioA).add((v2).multiply(ratioB));
        double scalar = (w1 * ratioA) + (w2 * ratioB);
        return new Rotation(new Quaternion(vect, scalar));
    }    
    
    
    /*********************************************
     * MARK: toString
     *********************************************/    
    
    
    /**
     * Creates a string to describe this Rotation as a quaternion.
     * @return  A string that describes this Rotation as a quaternion.
     */
    public String toQuaternionString() {
        return    "(" +quaternion.getX()+
        ", "+quaternion.getY()+
        ", "+quaternion.getZ()+
        ", "+quaternion.getW()+
        ")";
    }
    
    /**
     * Creates a string to describe this Rotation in axis/angle notation.
     * @return  A string that describes this Rotation in axis/angle notation.
     */
    public String toAxisAngleString() {
        Vector3D axis = getAxis();
        return    "((" +axis.getX()+
        ", " +axis.getY()+
        ", " +axis.getZ()+
        "), "+getAngle()+
        ")";
    }
    
    /**
     * Creates a string to describe this Rotation in Euler angles.
     * @return  A string that describes this Rotation in Euler angles.
     */
    public String toEulerString() {
        double[] angles = getEulerAngles();
        return    "(" +angles[0]+
        ", "+angles[1]+
        ", "+angles[2]+
        ")";
    }
    
}

