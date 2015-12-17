package org.rowan.linalgtoolkit.transform3d;

import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>RotationConversion</code> class supplies static methods for conversion 
 * between various forms of rotational notation. These forms include Euler notation 
 * (pitch, roll, and yaw), axis/angle notation, quaternion notation, and rotation 
 * matrix.
 * <p> 
 * When converting to and from Euler angle notation, all calculations follow the 
 * NASA standard y, z, x (yaw, roll, pitch) order convention. In other words,
 * all Euler based rotations are applied in the sequential order: yaw, roll, pitch.
 * <p> 
 * All angle values are expected in radians
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public abstract class RotationConversion {
    
    
    /*********************************************
     * MARK: To Quaternion
     *********************************************/
    
    /**
     * Calculates the Quaternion equivalent to the rotation defined by a given 
     * set of Euler angles.
     * @param pitch The degree of rotation, in radians, about the x axis.
     * @param yaw   The degree of rotation, in radians, about the y axis.
     * @param roll  The degree of rotation, in radians, about the z axis.
     * @return      The Quaternion equivalent to the rotation defined by the given 
     *              set of Euler angles.
     */
    public static Quaternion toQuaternion(double pitch, double yaw, double roll) {
        // pre-calculate sin/cos of half-angles
        double c1 = Math.cos(yaw    / 2.0);
        double s1 = Math.sin(yaw    / 2.0);
        double c2 = Math.cos(roll   / 2.0);
        double s2 = Math.sin(roll   / 2.0);
        double c3 = Math.cos(pitch  / 2.0);
        double s3 = Math.sin(pitch  / 2.0);
        
        // calculate quaternion components
        double w = (c1 * c2 * c3) - (s1 * s2 * s3);
        double x = (c1 * c2 * s3) + (s1 * s2 * c3);
        double y = (s1 * c2 * c3) + (c1 * s2 * s3);
        double z = (c1 * s2 * c3) - (s1 * c2 * s3);
        
        // create and return new Quaternion with calculated component values
        return new Quaternion(x, y, z, w);
    }
    
    /**
     * Calculates the Quaternion equivalent to the rotation defined by a given 
     * axis and angle of rotation.
     * @param axis  The axis about which the rotation will occur.
     * @param angle The degree of rotation, in radians, about the given axis.
     * @return      The Quaternion equivalent to the rotation defined by the given 
     *              axis and angle of rotation
     */
    public static Quaternion toQuaternion(Vector3D axis, double angle) {
        // make sure the given axis is normalized
        Vector3D unitAxis = axis.unitVector();
        
        // pre-calculate sin/cos of half-angle
        double sin = Math.sin(angle / 2.0);
        double cos = Math.cos(angle / 2.0);
        
        // calculate quaternion components
        double x = unitAxis.getX() * sin;
        double y = unitAxis.getY() * sin;
        double z = unitAxis.getZ() * sin;
        double w = cos;
        
        // create and return new Quaternion with calculated component values
        return new Quaternion(x, y, z, w);
    }
    
    /**
     * Calculates the Quaternion equivalent to the rotation defined by a given 
     * rotation matrix.
     * @param matrix    The rotation matrix defining the rotation.
     * @return          The Quaternion equivalent to the rotation defined by the 
     *                  given rotation matrix.
     */
    public static Quaternion toQuaternion(Matrix matrix) {
        double scalar;
        int nextIndex[] = {1, 2, 0};
        double[] temp = new double[4];
        double x, y, z, w;
        double[][] m = matrix.toArray();
        
        double trace = m[0][0] + m[1][1] + m[2][2];
        
        // positive diagonal:
        if(trace > 0.0) {
            scalar = Math.sqrt(trace + 1.0);
            w = (scalar * 0.5);
            
            scalar = (0.5 / scalar);
            x = (m[2][1] - m[1][2]) * scalar;
            y = (m[0][2] - m[2][0]) * scalar;
            z = (m[1][0] - m[0][1]) * scalar;
            
        }
        
        // negative diagonal
        else {
            int i = 0;
            if (m[1][1] > m[0][0])
                i = 1;
            if (m[2][2] > m[i][i])
                i = 2;
            int j = nextIndex[i];
            int k = nextIndex[j];
            
            scalar = Math.sqrt((m[i][i] - m[j][j] - m[k][k]) + 1.0);
            
            temp[i] = (scalar * 0.5);
            
            if(scalar != 0.0) 
                scalar = (0.5 / scalar);
            temp[3] = (m[k][j] - m[j][k]) * scalar;
            temp[j] = (m[j][i] + m[i][j]) * scalar;
            temp[k] = (m[k][i] + m[i][k]) * scalar;
            
            x = temp[0];
            y = temp[1];
            z = temp[2];
            w = temp[3];
        }
        
        // create and return new Quaternion with calculated component values
        return new Quaternion(x, y, z, w);
    }
    
    
    /*********************************************
     * MARK: To Euler
     *********************************************/
    
    /**
     * Calculates the Euler angles defining the rotation defined by a given axis 
     * and angle of rotation.
     * @param axis  The axis about which the rotation will occur.
     * @param angle The degree of rotation, in radians, about the given axis.
     * @return      An array containing the calculated Euler angles in the order:
     *              pitch, yaw, roll (x, y, z).
     */
    public static double[] toEuler(Vector3D axis, double angle) {
        double[] angles = new double[3];
        double x = axis.getX();
        double y = axis.getY();
        double z = axis.getZ();
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double temp = 1 - cos;
        
        // normalize the axis vector
        axis = axis.unitVector();
        
        // singularity at the north pole?
        if ((x*y*temp + z*sin) > Math.nextAfter(1.0, 0.0)) {
            angles[0] = 0;
            angles[1] = 2 * Math.atan2(x * Math.sin(angle/2), Math.cos(angle/2));
            angles[2] = Math.PI / 2.0; 
            return angles;
        }
        
        // singularity at the south pole?
        if ((x*y*temp + z*sin) < -Math.nextAfter(1.0, 0.0)) {
            angles[0] = 0;
            angles[1] = -2 * Math.atan2(x * Math.sin(angle/2), Math.cos(angle/2));
            angles[2] = -Math.PI / 2.0;
            return angles;
        }
        
        angles[0] = Math.atan2(x*sin - y*z*temp , 1 - (x*x + z*z) * temp);
        angles[1] = Math.atan2(y*sin - x*z*temp , 1 - (y*y + z*z) * temp);
        angles[2] = Math.asin(x*y*temp + z*sin);
        return angles;
    }
    
    /**
     * Calculates the Euler angles defining a rotation equivalent to a given 
     * quaternion rotation.
     * @param quaternion    The quaternion defining the rotation.
     * @return              An array containing the calculated Euler angles in 
     *                      the order: pitch, yaw, roll (x, y, z).
     */    
    public static double[] toEuler(Quaternion quaternion) {
        double[] angles = new double[3];
        double x = quaternion.getX();
        double y = quaternion.getY();
        double z = quaternion.getZ();
        double w = quaternion.getW();
        double test = (x * y) + (z * w);
        
        // singularity at north pole?
        if (test > Math.nextAfter(0.5, 0.0)) {
            angles[0] = 0;
            angles[1] = 2 * Math.atan2(x, w);
            angles[2] = Math.PI / 2.0;
        }
        
        // singularity at south pole?
        if (test < -Math.nextAfter(0.5, 0.0)) {
            angles[0] = 0;
            angles[1] = -2 * Math.atan2(x, w);
            angles[2] = -Math.PI / 2.0;
        }
        
        // calculate Euler angles
        angles[0] = Math.atan2((2*x*w) - (2*y*z), 1 - (2*x*x) - (2*z*z));
        angles[1] = Math.atan2((2*y*w) - (2*x*z), 1 - (2*y*y) - (2*z*z));
        angles[2] = Math.asin(2 * test);
        
        // return calculated angles
        return angles;
    }
    
    /**
     * Calculates the Euler angles defining the rotation defined by a given 
     * rotation matrix.
     * @param matrix    The rotation matrix defining the rotation.
     * @return          An array containing the calculated Euler angles in the 
     *                  order: pitch, yaw, roll (x, y, z).     
     */
    public static double[] toEuler(Matrix matrix) {
        return toEuler(toQuaternion(matrix));
    }
    
    
    /*********************************************
     * MARK: To Axis/Angle
     *********************************************/
    
    /**
     * Calculates the axis of rotation, in terms of a single axis and angle of
     * rotation, defined by a given set of Euler angles.
     * @param pitch The degree of rotation, in radians, about the x axis.
     * @param yaw   The degree of rotation, in radians, about the y axis.
     * @param roll  The degree of rotation, in radians, about the z axis.
     * @return      The axis of rotation defined by the given set of Euler angles.
     *              If the there is no rotation (all given angles equal 0.0),
     *              than the axis of rotation is arbitrary/nonexistent; thus
     *              a unit vector along the x axis (1, 0, 0) is returned.
     */
    public static Vector3D toAxisAngleAxis(double pitch, double yaw, double roll) {
        // pre-calculate sin/cos of half-angles
        double c1 = Math.cos(yaw    / 2.0);
        double s1 = Math.sin(yaw    / 2.0);
        double c2 = Math.cos(roll   / 2.0);
        double s2 = Math.sin(roll   / 2.0);
        double c3 = Math.cos(pitch  / 2.0);
        double s3 = Math.sin(pitch  / 2.0);
        
        // calculate axis vector components
        double x = (c1 * c2 * s3) + (s1 * s2 * c3);
        double y = (s1 * c2 * c3) + (c1 * s2 * s3);
        double z = (c1 * s2 * c3) - (s1 * c2 * s3);
        
        // create, normalize, and return vector with calculated component values
        Vector3D axis = new Vector3D(x, y, z);
        return axis.unitVector();
    }
    
    /**
     * Calculates the angle of rotation, in terms of a single axis and angle of
     * rotation, defined by a given set of Euler angles.
     * @param pitch The degree of rotation, in radians, about the x axis.
     * @param yaw   The degree of rotation, in radians, about the y axis.
     * @param roll  The degree of rotation, in radians, about the z axis.
     * @return      The angle, in radians, of rotation defined by the given set 
     *              of Euler angles.
     */
    public static double toAxisAngleAngle(double pitch, double yaw, double roll) {
        // pre-calculate sin/cos of half-angles
        double c1 = Math.cos(yaw    / 2.0);
        double s1 = Math.sin(yaw    / 2.0);
        double c2 = Math.cos(roll   / 2.0);
        double s2 = Math.sin(roll   / 2.0);
        double c3 = Math.cos(pitch  / 2.0);
        double s3 = Math.sin(pitch  / 2.0);
        
        // calculate and return angle
        return 2 * Math.acos((c1 * c2 * c3 - s1 * s2 * s3));
    }
    
    /**
     * Calculates the axis of rotation, in terms of a single axis and angle of
     * rotation, defined by a given quaternion rotation.
     * <p>
     * This method will return a directionless zero vector in the given quaternion
     * is the identity quaternion.
     * @param quaternion    The quaternion defining the rotation.
     * @return              The axis of rotation defined by the given quaternion 
     *                      rotation.
     */    
    public static Vector3D toAxisAngleAxis(Quaternion quaternion) {
        // calculate angle of rotation
        double angle = RotationConversion.toAxisAngleAngle(quaternion);
        
        // no rotation?
        if (Math.abs(angle) == 0) 
            return new Vector3D(0, 0, 0);
        
        // pre-calculate sin of half-angle
        double sin = Math.sin(angle / 2.0);
        
        // calculate component values
        double x = quaternion.getX() / sin;
        double y = quaternion.getY() / sin;
        double z = quaternion.getZ() / sin;
        
        // create and return normalized Vector out of calculated component values
        return new Vector3D(x, y, z).unitVector();
    }
    
    /**
     * Calculates the angle of rotation, in terms of a single axis and angle of
     * rotation, defined by a given quaternion rotation.
     * @param quaternion    The quaternion defining the rotation.
     * @return              The angle, in radians, of rotation defined by the 
     *                      given quaternion rotation.
     */    
    public static double toAxisAngleAngle(Quaternion quaternion) {
        // calculate and return angle
        return 2 * Math.acos(quaternion.getW());
    }
    
    /**
     * Calculates the axis of rotation, in terms of a single axis and angle of
     * rotation, defined by a given rotation matrix.
     * @param matrix    The rotation matrix defining the rotation.
     * @return          The axis of rotation defined by the given transformation 
     *                  matrix.     
     */
    public static Vector3D toAxisAngleAxis(Matrix matrix) {
        return toAxisAngleAxis(toQuaternion(matrix));
    }
    
    /**
     * Calculates the angle of rotation, in terms of a single axis and angle of
     * rotation, defined by a given rotation matrix.
     * @param matrix    The rotation matrix defining the rotation.
     * @return          The angle of rotation, in radians, defined by the given 
     *                  rotation matrix.     
     */
    public static double toAxisAngleAngle(Matrix matrix) {
        return toAxisAngleAngle(toQuaternion(matrix));
    }
    
    
    /*********************************************
     * MARK: To Rotation Matrix
     *********************************************/
    
    /**
     * Calculates the rotation matrix that defines the rotation defined by a given 
     * set of Euler angles.
     * @param pitch The degree of rotation, in radians, about the x axis.
     * @param yaw   The degree of rotation, in radians, about the y axis.
     * @param roll  The degree of rotation, in radians, about the z axis.
     * @return      The rotational rotation matrix that defines the rotation 
     *              defined by the given set of Euler angles.
     */
    public static Matrix toMatrix(double pitch, double yaw, double roll) {
        // pre-calculate sin/cos of angles
        double c1 = Math.cos(pitch  / 2.0);
        double s1 = Math.sin(pitch  / 2.0);
        double c2 = Math.cos(yaw    / 2.0);
        double s2 = Math.sin(yaw    / 2.0);
        double c3 = Math.cos(roll   / 2.0);
        double s3 = Math.sin(roll   / 2.0);
        
        // create array of matrix data (in row-major order)
        double[][] matrix = new double[4][4];
        // row 0
        matrix[0][0] = (c2 * c1);
        matrix[0][1] = (s2 * s3) - (c2 * s1 * c3);
        matrix[0][2] = (c2 * s1 * s3) + (s2 * c3);
        matrix[0][3] = 0.0;
        // row 1
        matrix[1][0] = s1;
        matrix[1][1] = (c1 * c3);
        matrix[1][2] = (-c1 * s3);
        matrix[1][3] = 0.0;
        // row 2
        matrix[2][0] = (-s2 * c1);
        matrix[2][1] = (s2 * s1 * c3) + (c2 * s3);
        matrix[2][2] = (-s2 * s1 * s3) + (c2 * c3);
        matrix[2][3] = 0.0;
        // row 3
        matrix[3][0] = 0.0;
        matrix[3][1] = 0.0;
        matrix[3][2] = 0.0;
        matrix[3][3] = 1.0;
        
        // create and return new Matrix using calculated matrix data
        return new Matrix(matrix);
    }
    
    /**
     * Calculates the rotation matrix that defines the rotation defined by a given 
     * axis and angle of rotation.
     * @param axis  The axis about which the rotation will occur.
     * @param angle The degree of rotation, in radians, about the given axis.
     * @return      The rotational rotation matrix that defines the rotation 
     *              defined by the given axis and angle of rotation.
     */
    public static Matrix toMatrix(Vector3D axis, double angle) {
        // normalize the axis vector
        axis = axis.unitVector();
        
        // store axis component values for easy access
        double x = axis.getX();
        double y = axis.getY();
        double z = axis.getZ();
        
        // pre-calculate sin/cos of the angle
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double temp = 1.0 - cos;
        
        // create array of matrix data (in row-major order)
        double[][] matrix = new double[4][4];
        
        matrix[0][0] = cos + (x * x * temp);
        matrix[1][1] = cos + (y * y * temp);
        matrix[2][2] = cos + (z * z * temp);
        
        double t1 = (x * y * temp);
        double t2 = (z * sin);
        matrix[1][0] = t1 + t2;
        matrix[0][1] = t1 - t2;
        t1 = (x * z * temp);
        t2 = (y * sin);
        matrix[2][0] = t1 - t2;
        matrix[0][2] = t1 + t2;   
        t1 = (y * z * temp);
        t2 = (x * sin);
        matrix[2][1] = t1 + t2;
        matrix[1][2] = t1 - t2;
        
        matrix[0][3] = 0.0;
        matrix[1][3] = 0.0;
        matrix[2][3] = 0.0;
        matrix[3][3] = 1.0;
        matrix[3][0] = 0.0;
        matrix[3][1] = 0.0;
        matrix[3][2] = 0.0;
        
        // create and return new Matrix using calculated matrix data
        return new Matrix(matrix);
    }
    
    /**
     * Calculates the rotation matrix that defines the rotation equivalent to a 
     * given quaternion rotation.
     * @param quaternion    The quaternion defining the rotation.
     * @return              The rotational rotation matrix that defines the 
     *                      rotation equivalent to the given Quaternion rotation.
     */    
    public static Matrix toMatrix(Quaternion quaternion) {
        // store quaternion values for easy access
        double x = quaternion.getX();
        double y = quaternion.getY();
        double z = quaternion.getZ();
        double w = quaternion.getW();
        
        // pre-calculate products
        double xx = (x * x);
        double xy = (x * y);
        double xz = (x * z);
        double xw = (x * w);
        double yy = (y * y);
        double yz = (y * z);
        double yw = (y * w);
        double zz = (z * z);
        double zw = (z * w);
        
        // create array of matrix data (in row-major order)
        double[][] matrix = new double[4][4];
        
        // col 0
        matrix[0][0] = 1 - 2*(yy + zz);
        matrix[1][0] = 2*(xy + zw);
        matrix[2][0] = 2*(xz - yw);
        matrix[3][0] = 0;
        // col 1
        matrix[0][1] = 2*(xy - zw);
        matrix[1][1] = 1 - 2*(xx + zz);
        matrix[2][1] = 2*(yz + xw);
        matrix[3][1] = 0;
        // col 2
        matrix[0][2] = 2*(xz + yw);
        matrix[1][2] = 2*(yz - xw);
        matrix[2][2] = 1 - 2*(xx + yy);
        matrix[3][2] = 0;
        // col 3
        matrix[0][3] = 0;
        matrix[1][3] = 0;
        matrix[2][3] = 0;
        matrix[3][3] = 1;
        
        // create and return new Matrix using calculated matrix data
        return new Matrix(matrix);
    }
}

