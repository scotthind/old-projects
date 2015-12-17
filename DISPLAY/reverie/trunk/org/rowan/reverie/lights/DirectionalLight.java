package org.rowan.reverie.lights;

import javax.media.opengl.*;
import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>DirectionalLight</code> class defines a directional light source 
 * with infinite location. A directional light source is always an infinite distance
 * from the scene, so it emits light rays that are parallel. 
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class DirectionalLight extends Light {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The direction in which the light source emits light. */
    private Vector3D direction;


    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates a <code>DirectionalLight</code> that emits light in a given direction.
     * @param direction The direction in which the created light source will emit
     *                  light.
     */
    public DirectionalLight(Vector3D direction) {
        // call super constructor
        super();
        
        // set direction
        this.direction = direction;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the direction in which the light source emits light.
     * @return  The direction in which the light source emits light.
     */
    public Vector3D getDirection() {
        return this.direction;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the direction in which the light source emits light.
     * @param direction The direction in which the light source emits light.
     */
    public void setDirection(Vector3D direction) {
        this.direction = direction;
    }
    
    
    /*********************************************
     * MARK: Internal
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
        
        // set position as directional light
        float[] posArray = 
        {
            (float) -direction.getX(), 
            (float) -direction.getY(), 
            (float) -direction.getZ(), 
            0.0f
        };
        gl.glLightfv(light, GL.GL_POSITION, posArray, 0);
    }

}
