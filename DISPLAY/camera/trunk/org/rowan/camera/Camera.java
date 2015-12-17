package org.rowan.camera;

import org.rowan.linalgtoolkit.Object3D;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.transform3d.Rotation;

/**
 * The <code>Camera</code> class describes a fully controllable 3D camera. A 
 * <code>Camera</code> can be manipulated relative to its current position/orientation, 
 * or its position/orientation can be set directly.
 * <p>
 * A <code>Camera</code>'s orientation is maintained as a rotation relative to a 
 * default orientation. This is defined as the orientation in which the camera is 
 * "looking" down the world -z axis with its local y axis relative to the world 
 * y axis.
 * <p>
 * <code>Camera</code>s maintain a focus depth that can be set and altered by the 
 * user. All <code>Camera</code>s are initialized with a default focus depth of 
 * 1.0. 
 * <p>
 * At any time, the user can lock a <code>Camera</code>'s focus. This causes all 
 * subsequent movement to be orbital, around the current focus point. By default 
 * all <code>Camera</code>s are created with focus unlocked.
 * <p>
 * By default, <code>Camera</code>s are initialized with field of view set to 35 
 * degrees, near clipping distance set to 0.01, and far clipping distance set to 
 * <code>Double.MAX_VALUE</code>. However, these values can be set manually by 
 * the user.
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public class Camera extends Object3D {
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The smallest value allowed for a <code>Camera</code>'s focus depth. */
    public static final double MIN_FOCUS_DEPTH = 0.001;
    
    /** The smallest value allowed for a <code>Camera</code>'s maximum follow distance. */
    public static final double MIN_FOLLOW_DIST = MIN_FOCUS_DEPTH;
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The distance between the camera and the point of focus. */
    private double focusDepth;
    
    /** Flag indicating whether the camera's focus is locked. */
    private boolean focusLocked;
    
    /** The Camera's field of view angle, in radians. */
    private double fieldOfView;
    
    /** The distance between the camera and the near clipping plane. */
    private double nearClip;
    
    /** The distance between the camera and the far clipping plane. */
    private double farClip;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/    
    
    /**
     * Creates a <code>Camera</code> object at a given position with a given 
     * orientation, relative to the default camera orientation.
     * @param position      The position at which the created camera will be set.
     * @param orientation   The orientation at which the created camera will be 
     *                      set, relative to the default camera orientation.
     * @throws IllegalArgumentException If the <code>position</code> or
     *                      <code>orientation</code> are <code>null</code>.
     */
    public Camera(Vector3D position, Rotation orientation) {
        // call super constructor
        super();
        
        // set position
        setPosition(position);
        
        // set orientation
        setOrientation(orientation);
        
        // set default field values
        this.focusDepth = 1.0;
        this.focusLocked = false;
        this.fieldOfView = Math.toRadians(35);
        this.nearClip = 0.01;
        this.farClip = Double.MAX_VALUE;
    }
    
    /**
     * Creates a <code>Camera</code> object positioned at the origin (0, 0, 0) 
     * with the default camera orientation.
     */
    public Camera() {
        this(Vector3D.ORIGIN, Rotation.IDENTITY);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns this camera's current focus depth.
     * @return  This camera's current focus depth.
     */
    public double getFocusDepth() {
        return this.focusDepth;
    }
    
    /**
     * Returns <code>true</code> if this camera's focus is locked; <code>false</code>
     * otherwise.
     * @return  <code>true</code> if this camera's focus is locked; <code>false</code>
     *          otherwise.
     */
    public boolean focusLocked() {
        return this.focusLocked;
    }
    
    /**
     * Returns a 3D vector pointing from this camera's position to its focus point, 
     * in world coordinates. The focus vector is the same direction as the local 
     * -z axis with magnitude equal to the camera's focus depth.
     * @return  This camera's current focus vector.
     */
    public Vector3D getFocusVector() {
        // focus vector has the same direction as the local -z axis:
        Vector3D focusVector = localZAxis().inverse();
        
        // set the magnitude to the current focus depth and return
        focusVector = new Vector3D(focusVector, focusDepth);
        return focusVector;
    }
    
    /**
     * Returns this camera's current point of focus.
     * @return  This camera's current point of focus.
     */
    public Vector3D getFocusPoint() {
        // calculate and return the position pointed to by the focus vector
        return getPosition().add(getFocusVector());
    }
    
    /**
     * Returns this camera's current field of view angle, in radians.
     * @return  This camera's current field of view angle, in radians.
     */
    public double getFieldOfView() {
        return this.fieldOfView;
    }
    
    /**
     * Returns the distance between this camera and the near clipping plane.
     * @return  The distance between this camera and the near clipping plane.
     */
    public double getNearClip() {
        return this.nearClip;
    }
    
    /**
     * Returns the distance between this camera and the far clipping plane.
     * @return  The distance between this camera and the far clipping plane.
     */
    public double getFarClip() {
        return this.farClip;
    }
    
    /**
     * Returns this camera's local x axis in world coordinates.
     * @return  This camera's local x axis in world coordinates.
     */
    public Vector3D localXAxis() {
        return Vector3D.X_AXIS.rotate(getOrientation()).unitVector();
    }
    
    /**
     * Returns this camera's local y axis in world coordinates.
     * @return  This camera's local y axis in world coordinates.
     */
    public Vector3D localYAxis() {
        return Vector3D.Y_AXIS.rotate(getOrientation()).unitVector();
    }
    
    /**
     * Returns this camera's local z axis in world coordinates.
     * @return  This camera's local z axis in world coordinates.
     */
    public Vector3D localZAxis() {
        return Vector3D.Z_AXIS.rotate(getOrientation()).unitVector();
    }
        
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
        
    /**
     * Sets this camera's focus depth to a given value. The given focus depth must
     * be a positive value greater than <code>MIN_FOCUS_DEPTH</code>. If the given 
     * value is not greater than <code>MIN_FOCUS_DEPTH</code>, then the focus 
     * depth will be set to <code>MIN_FOCUS_DEPTH</code>.
     * @param focusDepth    The distance from this camera to its point of focus.
     */
    public void setFocusDepth(double focusDepth) {
        this.focusDepth = (focusDepth > MIN_FOCUS_DEPTH)? focusDepth : MIN_FOCUS_DEPTH;
    }
    
    /**
     * Sets whether this camera's focus is locked. If the given value is 
     * <code>true</code> then this camera's focus will be locked. Otherwise, 
     * it will be unlocked.
     * <p>
     * When a <code>Camera</code>'s focus is locked, it will maintain its focus, 
     * regardless of any following mutation, unless its point of focus is explicitly 
     * changed using the <code>lookAt()</code> method.
     * @param locked    A boolean value stating whether this camera's focus should
     *                  be locked.
     */
    public void setFocusLocked(boolean locked) {
        this.focusLocked = locked;
    }
    
    /**
     * Sets this camera's field of view to a given angle, in radians. The given 
     * angle will be clamped to a value between 0 and pi radians.
     * @param fov   The desired field of view angle, in radians.
     */
    public void setFieldOfView(double fov) {
        this.fieldOfView = fov;
    }
    
    /**
     * Sets this camera's near clipping distance to a given value.
     * @param nearClip  The distance between this camera and the near clipping 
     *                  plane.
     */
    public void setNearClip(double nearClip) {
        this.nearClip = nearClip;
    }
    
    /**
     * Sets this camera's far clipping distance to a given value.
     * @param farClip   The distance between this camera and the far clipping 
     *                  plane.
     */
    public void setFarClip(double farClip) {
        this.farClip = farClip;
    }
        
    
    /*********************************************
     * MARK: Movement
     *********************************************/
    
    /**
     * Translates this camera according to a given displacement vector. If the 
     * <code>relative</code> flag is set to <code>true</code> then the translation 
     * will be performed relative to this camera's current orientation. Otherwise,
     * the translation will be performed relative to the world coordinate system.
     * @param displacement  A <code>Vector3D</code> describing the displacement 
     *                      to be applied to this camera.
     * @param relative      A boolean flag stating whether the translation performed 
     *                      should be relative to this camera's current orientation.
     */
    public void move(Vector3D displacement, boolean relative) {
        // find local and world displacement vectors 
        Vector3D worldDisp;
        Vector3D localDisp;
        if (relative) {
            worldDisp = displacement.rotate(getOrientation());
            localDisp = displacement;
        } else {
            worldDisp = displacement;
            localDisp = displacement.rotate(getOrientation().inverse());
        }
        
        // focus not locked?
        if (!focusLocked) {
            // move camera
            setPosition(getPosition().add(worldDisp));
            return;
        }
        
        // focus locked:
        
        // store z component of local displacement
        double zComp = localDisp.getZ();
        
        // create new displacement vector with only x and y components
        Vector3D rotationVector = new Vector3D(localDisp.getX(),
                                               localDisp.getY(),
                                               0);
        
        // find the angle of rotation 
        // (using rotation vector magnitude as arclength)
        double angle = rotationVector.magnitude() / focusDepth;
        
        // find the axis of rotation and normalize
        Vector3D axis = worldDisp.unitVector().cross(getFocusVector().unitVector());
        axis = axis.unitVector();
        
        // rotate camera around focus point
        rotate(new Rotation(axis, angle), getFocusPoint(), true);
        
        // clamp z component to a value that won't cause an illegal focus depth
        if ((focusDepth + zComp) < MIN_FOCUS_DEPTH)
            zComp = MIN_FOCUS_DEPTH - focusDepth;
        
        // calculate and apply z component of displacement
        Vector3D zDisp = new Vector3D(0, 0, zComp);
        zDisp = zDisp.rotate(getOrientation());
        setPosition(getPosition().add(zDisp));
    }
    
    /**
     * Translates this camera according to a given displacement, relative to the
     * world coordinate system.
     * <p>
     * For example: applying the displacement vector (0, 0, -1) will move this 
     * camera forward, applying the vector (0, 0, 1) will move this camera backward,
     * applying the vector (1, 0, 0) will move this camera right, etc.
     * <p>
     * This method has the same effect as a call to <code>move(displacement, true)</code>
     * @param displacement  A <code>Vector3D</code> describing the displacement 
     *                      to be applied to this camera.
     */
    public void move(Vector3D displacement) {
        move(displacement, true);
    }
    
    
    /*********************************************
     * MARK: Rotation
     *********************************************/
    
    /**
     * Rotates this camera by a given amount. If <code>relative</code> is set to
     * <code>true</code> then the rotation will be performed relative to this 
     * camera's current orientation. Otherwise, the rotation will be performed
     * relative to the world coordinate system.
     * <p>
     * If this camera's focus is locked, then only rotation about the z axis (roll)
     * will be performed.
     * @param rotation  The amount of rotation to be applied to this camera.
     * @param relative  A boolean flag stating whether the rotation performed 
     *                  should be relative to this camera's current orientation.
     */
    public void rotate(Rotation rotation, boolean relative) {
        // store current focus point
        Vector3D focusPoint = getFocusPoint();
        
        // perform rotation
        if (relative)
            super.setOrientation(getOrientation().append(rotation));
        else
            super.setOrientation(rotation.append(getOrientation()));
        
        // if focus is locked, look at the point of focus
        if (focusLocked)
            lookAt(focusPoint);
    }
    
    
    /**
     * Rotates this camera by a given amount, relative to its current orientation.
     * <p>
     * If this camera's focus is locked, then only rotation about the z axis (roll)
     * will be performed.
     * <p>
     * This method has the same effect as a call to <code>rotate(rotation, true)</code>
     * @param rotation  The amount of rotation to be applied to this camera.
     */
    public void rotate(Rotation rotation) {
        rotate(rotation, true);
    }
    
    /**
     * Rotates this camera by a given amount around a given point. The rotation 
     * will be performed relative to the world coordinate system. If the 
     * <cod>reorient</code> flag is set to <code>true</code>, then the rotation 
     * will be applied as a standard rotational transformation would; altering this 
     * camera's orientation. If set to <code>false</code>, this camera's orientation
     * will remain unchanged as the rotation is performed.
     * <p>
     * Note that the camera will maintain focus if the camera's focus is locked.
     * @param rotation  The amount of rotation to be applied to this camera.
     * @param point     The point, in the world coordinate system, about which 
     *                  the rotation will occur.
     * @param reorient  A flag stating whether or not to alter this camera's 
     *                  orientation as with the applied rotation.
     */
    public void rotate(Rotation rotation, Vector3D point, boolean reorient) {
        // store current focus point
        Vector3D focusPoint = getFocusPoint();
        
        // find vector between given point and camera, in local coordinates
        Vector3D vect = getPosition().subtract(point);
        
        // move camera to given point
        super.setPosition(point);
        
        // rotate camera for standard rotation
        if (reorient)
            super.setOrientation(rotation.append(getOrientation()));
        
        // rotate vector
        vect = vect.rotate(rotation);
        
        // move back to proper position
        super.setPosition(getPosition().add(vect));
        
        // if focus is locked, look at original focus point
        if (focusLocked)
            lookAt(focusPoint);
    }
    
    
    /*********************************************
     * MARK: Look At
     *********************************************/
    
    /**
     * Rotates this camera to focus on a given point. The local y-axis (up vector)
     * will be changed as little as possible.
     * @param point A <code>Vector3D</code> describing the point on which this 
     *              camera will focus.
     */
    public void lookAt(Vector3D point) {
        lookAt(point, localYAxis());
    }
    
    /**
     * Rotates this camera to focus on a given point with the local y-axis (up 
     * vector) set to a given vector. The resulting local y-axis may be altered 
     * as needed to make it perpendicular to the focus vector formed between this 
     * Camera's position and the given point.
     * @param point A <code>Vector3D</code> describing the point on which this 
     *              camera will focus.
     * @param up    A <code>Vector3D</code> describing the desired local y axis 
     *              (up vector).
     */
    public void lookAt(Vector3D point, Vector3D up) {
        // find focus vector and distance from point
        Vector3D focusVector = point.subtract(getPosition());
        
        // set focus depth
        setFocusDepth(focusVector.magnitude());
        
        // set orientation (using inverse of focus vector for +z-axis
        super.setOrientation(new Rotation(up, focusVector.inverse()));
    }
    
    
    /*********************************************
     * MARK: Following
     *********************************************/
    
    /**
     * Rotates and moves the camera in order to follow a given point from a given
     * distance. This method is similar to the <code>lookAt()</code> method, except 
     * it also adjusts position. When needed, the camera will be moved such that 
     * the distance between the camera and <code>point</code> is less than or  
     * equal to <code>distance</code>.
     * @param point     The point to look at / follow.
     * @param distance  The maximum allowed distance between the camera and the
     *                  given point.
     */
    public void follow(Vector3D point, double distance) {
        follow(point, distance, localYAxis());
    }
    
    /**
     * Rotates and moves the camera in order to follow a given point from a given
     * distance. This method is similar to the <code>lookAt()</code> method, except 
     * it also adjusts position. When needed, the camera will be moved such that 
     * the distance between the camera and <code>point</code> is less than or 
     * equal to <code>distance</code>.
     * @param point     The point to look at / follow.
     * @param distance  The maximum allowed distance between the camera and the
     *                  given point.
     * @param up        A vector describing the orientation of the desired local 
     *                  y-axis.
     */
    public void follow(Vector3D point, double distance, Vector3D up) {
        // look at the given point
        lookAt(point, up);
        
        // further than allowed follow distance?
        if (focusDepth > distance) {
            // fix position
            Vector3D delta = new Vector3D(getFocusVector(), focusDepth - distance);
            super.setPosition(getPosition().add(delta));
            
            // set focus depth
            setFocusDepth(distance);
        }
    }
    
    /**
     * Rotates and moves the camera in order to follow a given point from a given
     * distance, using a secondary point to stipulate position and orientation. 
     * This method is similar to the <code>lookAt()</code> method, except it also
     * adjusts position. The camera will be moved such that both <code>point</code>
     * and <code>secondary</code> lie onthe camera's focus vector, with <code>point</code>
     * located between the camera and <codes>secondary</code>, and the distance 
     * between the camera and <code>point</code> less than or equal to <code>distance</code>
     * @param point     The point to look at / follow.
     * @param secondary A secondary providing position/orientation stipulation.
     * @param distance  The maximum allowed distance between the camera and the
     *                  given point.
     * @param up        A vector describing the orientation of the desired local 
     *                  y-axis.
     */
    public void follow(Vector3D point, Vector3D secondary, double distance, Vector3D up) {
        follow(point, distance, up);
        
        double newDist = getPosition().distance(point);
    
        Vector3D projection = point.subtract(secondary);
        Vector3D position = point.add(new Vector3D(projection, newDist));
        
        setPosition(position);
        lookAt(point, up);
    }
    
    /**
     * Rotates and moves the camera in order to follow a given point from a given
     * distance, using a secondary point to stipulate position and orientation. 
     * This method is similar to the <code>lookAt()</code> method, except it also
     * adjusts position. The camera will be moved such that both <code>point</code>
     * and <code>secondary</code> lie onthe camera's focus vector, with <code>point</code>
     * located between the camera and <codes>secondary</code>, and the distance 
     * between the camera and <code>point</code> less than or equal to <code>distance</code>
     * @param point     The point to look at / follow.
     * @param secondary A secondary providing position/orientation stipulation.
     * @param distance  The maximum allowed distance between the camera and the
     *                  given point.
     */
    public void follow(Vector3D point, Vector3D secondary, double distance) {
        follow(point, secondary, distance, localYAxis());
    }
    
    
    /*********************************************
     * MARK: Overrides
     *********************************************/
    
    /**
     * Sets this camera's position to a given point.
     * @param position  A <code>Vector3D</code> describing the position at which 
     *                  to set the camera.
     * @throws IllegalArgumentException If <code>position</code> is <code>null</code>.
     */
    public void setPosition(Vector3D position) {
        // store current focus point
        Vector3D focusPoint = getFocusPoint();
        
        // set position
        super.setPosition(position);
        
        // if focus is locked, look at original focus point
        if (focusLocked())
            lookAt(focusPoint);
    }
    
    /**
     * Sets this object's orientation to that described by a given <code>Rotation</code> 
     * object, relative to the object's default orientation.
     * @param orientation   A <code>Rotation</code> object defining the desired 
     *                      orientation, relative to the object's default orientation.
     * @throws IllegalArgumentException If <code>orientation</code> is <code>null</code>.
     */
    public void setOrientation(Rotation orientation) {
        // null given?
        if (orientation == null)
            throw new IllegalArgumentException("Cannot set null orientation.");
        
        // if focus is locked, we can only alter the 
        // orientation of the local up (y-axis) vector
        if (focusLocked)
            lookAt(getFocusPoint(), Vector3D.Y_AXIS.rotate(orientation));
        else
            super.setOrientation(orientation);
    }
}

