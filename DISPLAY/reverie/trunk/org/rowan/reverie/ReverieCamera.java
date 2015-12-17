package org.rowan.reverie;

import java.awt.Color;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.transform3d.*;
import org.rowan.camera.Camera;
import org.rowan.reverie.lights.*;

/**
 * The <code>ReverieCamera</code> class defines a custom subclass of the 
 * <code>org.rowan.Camera</code> for navigating through a Reverie space. 
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public class ReverieCamera extends Camera {
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The default camera position. */
    public static final Vector3D DEFAULT_CAMERA_POSITION = new Vector3D(0.0, 0.0, 1.0);
    
    /** The focus point of a camera upon initialization. */
    public static final Vector3D DEFAULT_FOCUS_POINT = Vector3D.ORIGIN;
    
    /** Indicates whether a camera should have its focus locked by default. */
    public static final boolean DEFAULT_FOCUS_LOCKED = false;
    
    /** The default field of view angle, in radians. */
    public static final double DEFAULT_FIELD_OF_VIEW_ANGLE = Math.toRadians(45);
    
    /** Movement speed. */
    public static final double DEFAULT_SPEED = 0.07;
    
    /** Rotation speed. */
    public static final double DEFAULT_ROTATION_SPEED = Math.toRadians(.8);
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The current movement speed of the camera. */
    private double speed;
    
    /** The current rotation speed of the camera. */
    private double rotSpeed;
    
    /** The camera's built-in spotlight. */
    private Spotlight light;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/ 
    
    /**
     * Initializes a <code>ReverieCamera</code> object.
     */
    public ReverieCamera() {
        super();
        reset();
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the camera's speed.
     * @return  The camera's speed.
     */
    public double getSpeed() {
        return this.speed;
    }
    
    /**
     * Returns the camera's rotation speed.
     * @return  The camera's rotation speed.
     */
    public double getRotSpeed() {
        return this.rotSpeed;
    }
    
    /**
     * returns the camera's built-in spotlight.
     * @return  The camera's spotlight.
     */
    public Spotlight getLight() {
        return this.light;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the camera's speed to a given value.
     * @param speed The camera's speed.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    /**
     * Sets the camera's rotation speed to a given value.
     * @param rotSpeed  The camera's rotation speed.
     */
    public void setRotSpeed(double rotSpeed) {
        this.rotSpeed = rotSpeed;
    }
    
    
    /*********************************************
     * MARK: Control
     *********************************************/
    
    /**
     * Moves the camera forward according to the camera's current speed.
     */
    public void moveForward() {
        move(new Vector3D(0, 0, -speed));
    }
    
    /**
     * Moves the camera backward according to the camera's current speed.
     */
    public void moveBackward() {
        move(new Vector3D(0, 0, speed));
    }
    
    /**
     * Moves the camera left according to the camera's current speed.
     */
    public void moveLeft() {
        move(new Vector3D(-speed, 0, 0));
    }
    
    /**
     * Moves the camera right according to the camera's current speed.
     */
    public void moveRight() {
        move(new Vector3D(speed, 0, 0));
    }
    
    /**
     * Moves the camera up according to the camera's current speed.
     */
    public void moveUp() {
        move(new Vector3D(0, speed, 0));
    }
    
    /**
     * Moves the camera down according to the camera's current speed.
     */
    public void moveDown() {
        move(new Vector3D(0, -speed, 0));
    }
    
    /**
     * Pitches the camera upward according to the camera's current rotation speed.
     */
    public void pitchUp() {
        rotate(new Rotation(rotSpeed, 0, 0));
    }
    
    /**
     * Pitches the camera downward according to the camera's current rotation speed.
     */
    public void pitchDown() {
        rotate(new Rotation(-rotSpeed, 0, 0));
    }
    
    /**
     * Yaws the camera left according to the camera's current rotation speed.
     */
    public void yawLeft() {
        rotate(new Rotation(0, rotSpeed, 0));
    }
    
    /**
     * Yaws the camera right according to the camera's current rotation speed.
     */
    public void yawRight() {
        rotate(new Rotation(0, -rotSpeed, 0));
    }
    
    /**
     * Rolls the camera left according to the camera's current rotation speed.
     */
    public void rollLeft() {
        rotate(new Rotation(0, 0, rotSpeed));
    }
    
    /**
     * Rolls the camera right according to the camera's current rotation speed.
     */
    public void rollRight() {
        rotate(new Rotation(0, 0, -rotSpeed));
    }
	
	/**
	 * Sets/Resets the camera to the default position and settings.
	 */
	public void reset() {
        // reset orientation
		setOrientation(new Rotation(Quaternion.IDENTITY));
        
        // reset position
		setPosition(DEFAULT_CAMERA_POSITION);
        
        // reset focus and orientation to look at default focus point
		lookAt(DEFAULT_FOCUS_POINT);
        
        // set focus-lock to default state
		setFocusLocked(DEFAULT_FOCUS_LOCKED);
        
        // reset field of view angle
        setFieldOfView(DEFAULT_FIELD_OF_VIEW_ANGLE
                       );
        
        // reset camera speed
        this.speed = DEFAULT_SPEED;
        this.rotSpeed = DEFAULT_ROTATION_SPEED;
        
        // create camera light
        this.light = createLight();
	}
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Creates the camera spotlight.
     * @return  A <code>Spotlight</code> for use as a camera spotlight.
     */
    private Spotlight createLight() {
        // create spotlight
        Spotlight light = new Spotlight();
        
        // set light colors
        light.setAmbient(new Color(0.4f, 0.4f, 0.4f, 1.0f));
        light.setDiffuse(new Color(0.9f, 0.9f, 0.9f, 1.0f));
        light.setSpecular(Color.WHITE);
        
        // set attenuation factors
        light.setConstantAttenuationFactor(0.6f);
        light.setLinearAttenuationFactor(0.005f);
        light.setQuadraticAttenuationFactor(0.001f);
        
        // set spot cutoff and exponent
        light.setCutoff(45);
        light.setSpotExponent(30);
        
        // turn off light
        light.turnOff();
        
        // return the light
        return light;
    }
    
}