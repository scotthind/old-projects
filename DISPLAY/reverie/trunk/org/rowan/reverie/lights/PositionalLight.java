package org.rowan.reverie.lights;

import javax.media.opengl.*;
import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>PositionalLight</code> class defines a positionable light source 
 * which emits light in all directions.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public class PositionalLight extends Light {
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The default position. */
    public static final Vector3D DEFAULT_POSITION = Vector3D.ORIGIN;
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The position of the light source. */
    private Vector3D position;


    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a <code>PositionalLight</code> at the default position.
     */
    public PositionalLight() {
        this(DEFAULT_POSITION);
    }
    
    /**
     * Creates a <code>PositionalLight</code> at a given position.
     * @param position  the position of the created light.
     */
    public PositionalLight(Vector3D position) {
        // call super constructor
        super();
        
        // set position
        this.position = position;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the position of the light source.
     * @return  The position of the light source.
     */
    public Vector3D getPosition() {
        return this.position;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the position of the light source.
     * @param position  The position of the light source.
     */
    public void setPosition(Vector3D position) {
        this.position = position;
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
        
        // set position as positional light
        float[] posArray = 
        {
            (float) position.getX(), 
            (float) position.getY(), 
            (float) position.getZ(), 
            1.0f
        };
        gl.glLightfv(light, GL.GL_POSITION, posArray, 0);
    }
    
}
