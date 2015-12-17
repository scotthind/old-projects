package org.rowan.reverie.lights;

import javax.media.opengl.*;
import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>Spotlight</code> class defines a positionable spotlight that emits 
 * light in a single direction.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class Spotlight extends PositionalLight {
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The default direction. */
    public static final Vector3D DEFAULT_DIRECTION = Vector3D.Z_AXIS.inverse();
    
    /** The default cutoff angle. */
    public static final float DEFAULT_CUTOFF = 180.0f;
    
    /** The default spotlight exponent. */
    public static final float DEFAULT_SPOT_EXPONENT = 0.0f;
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The direction in which the spotlight emits light. */
    private Vector3D direction;
    
    /** The cutoff angle of the light emitted from the spotlight, in degrees. */
    private float cutoff;
    
    /** The spot exponent of the spotlight. */
    private float spotExponent;
    

    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates a <code>Spotlight</code>.
     */
    public Spotlight() {
        this(DEFAULT_POSITION, DEFAULT_DIRECTION);
    }
    
    /**
     * Creates a <code>Spotlight</code> at with given position and direction.
     * @param position  The position of the spotlight.
     * @param direction The direction of the spotlight.
     */
    public Spotlight(Vector3D position, Vector3D direction) {
        // call super constructor
        super(position);
        
        // set direction
        this.direction = direction;
        
        // set default cutoff and spot exponent
        this.cutoff = DEFAULT_CUTOFF;
        this.spotExponent = DEFAULT_SPOT_EXPONENT;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the direction in which the spotlight emits light.
     * @return  The current direction of the spotlight.
     */
    public Vector3D getDirection() {
        return this.direction;
    }
    
    /**
     * Returns the spotlight's cutoff angle.
     * @return  The spotlight's cutoff angle, in degrees.
     */
    public float getCutoff() {
        return this.cutoff;
    }
    
    /**
     * Returns the spotlight's spot exponent. Higher spot exponent values will
     * result in a more focused light source.
     * @return  The spotlight's spot exponent.
     */
    public float getSpotExponent() {
        return this.spotExponent;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * sets the direction in which the spotlight emits light.
     * @param direction The direction of the spotlight.
     */
    public void setDirection(Vector3D direction) {
        this.direction = direction;
    }
    
    /**
     * Sets the spotlight's cutoff angle.
     * @param cutoff    The spotlight's cutoff angle in degrees.
     */
    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }
    
    /**
     * Sets the spotlight's cutoff angle. Higher spot exponent values will result
     * in a more focused light source
     * @param spotExponent  The spotlight's spot exponent.
     */
    public void setSpotExponent(float spotExponent) {
        this.spotExponent = spotExponent;
    }
    
    /**
     * Sets the direction of the spotlight so it faces a given point.
     * @param point The point that the spotlight will face.
     */
    public void lookAt(Vector3D point) {
        this.direction = point.subtract(getPosition());
    }
    
    
    /*********************************************
     * MARK: internal
     *********************************************/
    
    /**
     * Performs OpenGL calls to properly define the light source. This method is 
     * used internally by Reverie and is not intended for external use.
     * @param gl    The OpenGL rendering pipeline that will be used to define the
     *              light source.
     * @param light A handle to the OpenGL light source being defined.
     */
    public void define(GL gl, int light) {
        // call superclass definition
        super.define(gl, light);
        
        // set spotlight direction
        float[] directionArray = 
        {
            (float) direction.getX(), 
            (float) direction.getY(), 
            (float) direction.getZ(), 
        };
        gl.glLightfv(light, GL.GL_SPOT_DIRECTION, directionArray, 0);
        
        // set spot cutoff
        gl.glLightf(light, GL.GL_SPOT_CUTOFF, cutoff);
        
        // set spot exponent
        gl.glLightf(light, GL.GL_SPOT_EXPONENT, spotExponent);
    }

}
