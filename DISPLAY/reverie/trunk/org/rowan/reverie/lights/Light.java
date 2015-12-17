package org.rowan.reverie.lights;

import javax.media.opengl.*;

import java.awt.Color;
import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>Light</code> class provides a convenient interface for defining the
 * state and settings of OpenGL light sources.
 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public abstract class Light {
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The default ambient light color. */
    public static final Color DEFAULT_AMBIENT = Color.BLACK;
    
    /** The default diffuse light color. */
    public static final Color DEFAULT_DIFFUSE = Color.WHITE;
    
    /** The default specular light color. */
    public static final Color DEFAULT_SPECULAR = Color.WHITE;
    
    
    /** The default constant attenuation factor. */
    public static final float DEFAULT_CONSTANT_ATTENUATION_FACTOR = 1.0f;
    
    /** The default linear attenuation factor. */
    public static final float DEFAULT_LINEAR_ATTENUATION_FACTOR = 0.0f;
    
    /** The default quadratic attenuation factor. */
    public static final float DEFAULT_QUADRATIC_ATTENUATION_FACTOR = 0.0f;
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** Indicates whether the light source is turned on. */
    private boolean enabled;
    
    /** The color of ambient light emitted from the light source. */
    private Color ambient;
    
    /** The color of diffuse light emitted from the light source. */
    private Color diffuse;
    
    /** The color of specular light emitted from the light source. */
    private Color specular;
    
    
    /** The constant attenuation factor of the light source. */
    private float constantAttenFactor;
    
    /** The linear attenuation factor of the light source. */
    private float linearAttenFactor;
    
    /** The quadratic attenuation factor of the light source. */
    private float quadraticAttenFactor;
    

    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Designated Constructor.
     */
    public Light() {
        // enable light by default
        this.enabled = true;
        
        // set light colors to default
        this.ambient = DEFAULT_AMBIENT;
        this.diffuse = DEFAULT_DIFFUSE;
        this.specular = DEFAULT_SPECULAR;
        
        // set attenuation factors to default
        this.constantAttenFactor = DEFAULT_CONSTANT_ATTENUATION_FACTOR;
        this.linearAttenFactor = DEFAULT_LINEAR_ATTENUATION_FACTOR;
        this.quadraticAttenFactor = DEFAULT_QUADRATIC_ATTENUATION_FACTOR;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns whether the light source is enabled.
     * @return  Whether the light source is enabled.
     */
    public boolean isEnabled() {
        return this.enabled;
    }
    
    /**
     * Returns whether the light source is enabled. This method is the same as
     * <code>isEnabled()</code>
     * @return  Whether the light source is enabled.
     */
    public boolean isOn() {
        return this.enabled;
    }

    /**
     * Returns the color of ambient light emitted from the light source.
     * @return  The color of ambient light emitted from the light source.
     */
    public Color getAbient() {
        return this.ambient;
    }
    
    /**
     * Returns the color of diffuse light emitted from the light source.
     * @return  The color of diffuse light emitted from the light source.
     */
    public Color getDiffuse() {
        return this.diffuse;
    }
    
    /**
     * Returns the color of specular light emitted from the light source.
     * @return  The color of specular light emitted from the light source.
     */
    public Color getSpecular() {
        return this.specular;
    }
    
    /**
     * Returns the constant attenuation factor of the light source.
     * @return  The constant attenuation factor of the light source.
     */
    public float getConstantAttenuationFactor() {
        return this.constantAttenFactor;
    }
    
    /**
     * Returns the linear attenuation factor of the light source.
     * @return  The linear attenuation factor of the light source.
     */
    public float getLinearAttenuationFactor() {
        return this.linearAttenFactor;
    }
    
    /**
     * Returns the quadratic attenuation factor of the light source.
     * @return  The quadratic attenuation factor of the light source.
     */
    public float getQuadraticAttenuationFactor() {
        return this.quadraticAttenFactor;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets whether the light source is enabled.
     * @param enabled   Whether the light source should be enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Turns the light on
     */
    public void turnOn() {
        this.enabled = true;
    }
    
    /**
     * Turns the light off
     */
    public void turnOff() {
        this.enabled = false;
    }
    
    /**
     * Toggles the light source on/off.
     */
    public void toggle() {
        this.enabled = !this.enabled;
    }
    
    /**
     * Sets the color of ambient light emitted from the light source.
     * @param color The color of ambient light.
     */
    public void setAmbient(Color color) {
        this.ambient = color;
    }
    
    /**
     * Sets the color of diffuse light emitted from the light source.
     * @param color The color of diffuse light.
     */
    public void setDiffuse(Color color) {
        this.diffuse = color;
    }
    
    /**
     * Sets the color of specular light emitted from the light source.
     * @param color The color of specular light.
     */
    public void setSpecular(Color color) {
        this.specular = color;
    }
    
    /**
     * Sets the constant attenuation factor of the light source.
     * @param factor    The constant attenuation factor of the light source.
     */
    public void setConstantAttenuationFactor(float factor) {
        this.constantAttenFactor = factor;
    }
    
    /**
     * Sets the linear attenuation factor of the light source.
     * @param factor    The linear attenuation factor of the light source.
     */
    public void setLinearAttenuationFactor(float factor) {
        this.linearAttenFactor = factor;
    }
    
    /**
     * Sets the quadratic attenuation factor of the light source.
     * @param factor    The quadratic attenuation factor of the light source.
     */
    public void setQuadraticAttenuationFactor(float factor) {
        this.quadraticAttenFactor = factor;
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
        // create an array of floats to store color components
        float[] components = new float[4];
        
        // turn on the light
        if (enabled)
            gl.glEnable(light);
        else
            gl.glDisable(light);
        
        // set light color
        gl.glLightfv(light, GL.GL_AMBIENT, ambient.getRGBComponents(components), 0);
        gl.glLightfv(light, GL.GL_DIFFUSE, diffuse.getRGBComponents(components), 0);
        gl.glLightfv(light, GL.GL_SPECULAR, specular.getRGBComponents(components), 0);
        
        // set attenuation factors
        gl.glLightf(light, GL.GL_CONSTANT_ATTENUATION, constantAttenFactor);
        gl.glLightf(light, GL.GL_LINEAR_ATTENUATION, linearAttenFactor);
        gl.glLightf(light, GL.GL_QUADRATIC_ATTENUATION, quadraticAttenFactor);
    }
    
}
