package org.rowan.reverie;

import java.util.*;
import java.awt.Color;
import javax.media.opengl.*;

/**
 * The <code>Settings</code> encapsulates a set of various drawing and other 
 * settings used by a Reverie simulation space. 
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public class Settings extends Observable {
    
    /*********************************************
     * MARK: Enums
     *********************************************/
    
    public enum SettingType {
        // initialize all settings
        FRAMERATE,
        POLYGON_OFFSET_FACTOR,
        POLYGON_OFFSET_UNITS,
        SMOOTH_SHADING,
        ANTIALIASING,
        AA_LEVEL,
        LAZY_LOADING,
        CLEAR_COLOR,
        GLOBAL_AMBIENCE
    }
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** Default target framerate of a simulation. */
    public static final int DEFAULT_FRAMERATE = 45;
        
    
    /** Polygon offset factor used to prevent Z-fighting. */
    public static final float DEFAULT_POLYGON_OFFSET_FACTOR = -1.0f;
    
    /** Polygon offset units used to prevent Z-fighting. */
    public static final float DEFAULT_POLYGON_OFFSET_UNITS = -2.0f;
    
    
    /** The default polygon drawing mode. */
    public static final int DEFAULT_POLYGON_MODE = GL.GL_FILL;
    
    /** Indicates whether smooth shading is enabled by default. */
    public static final boolean DEFAULT_SMOOTH_SHADING = true;
    
    /** Indicates whether antialiasing is enabled by default. */
    public static final boolean DEFAULT_ANTIALIASING = true;
    
    /** The level of antialiasing to apply. */
    public static final int DEFAULT_AA_LEVEL = GL.GL_NICEST;
    
    /** The level of antialiasing to apply. */
    public static final boolean DEFAULT_LAZY_LOADING = true;
    
    
    /** The clear color. */
    public static final Color DEFAULT_CLEAR_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    
    /** The color of light emitted by the global ambient light source. */
    public static final Color DEFAULT_GLOBAL_AMBIENCE = Color.BLACK;
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The target framerate of a simulation. */
    private int framerate;
        
    
    /** Polygon offset factor used to prevent Z-fighting. */
    private float polygonOffsetFactor;
    
    /** Polygon offset units used to prevent Z-fighting. */
    private float polygonOffsetUnits;
    
    
    /** The polygon drawing mode. */
    private int polygonMode;
    
    /** Indicates whether smooth shading is enabled by default. */
    private boolean smoothShading;
    
    /** Indicates whether antialiasing is enabled by default. */
    private boolean antialiasing;
    
    /** The level of antialiasing to apply. */
    private int antialiasLevel;
    
    /** The level of antialiasing to apply. */
    private boolean lazyLoad;
    
    
    /** The clear color. */
    private Color clearColor;    
    
    /** The color of light emitted by the global ambient light source. */
    private Color globalAmbience;
    

    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates a <code>Settings</code> object populated with the default settings.
     */
    public Settings() {
        // initialize all settings
        this.framerate = DEFAULT_FRAMERATE;
        this.polygonOffsetFactor = DEFAULT_POLYGON_OFFSET_FACTOR;
        this.polygonOffsetUnits = DEFAULT_POLYGON_OFFSET_UNITS;
        this.polygonMode = DEFAULT_POLYGON_MODE;
        this.smoothShading = DEFAULT_SMOOTH_SHADING;
        this.antialiasing = DEFAULT_ANTIALIASING;
        this.antialiasLevel = DEFAULT_AA_LEVEL;
        this.lazyLoad = DEFAULT_LAZY_LOADING;
        this.clearColor = DEFAULT_CLEAR_COLOR;
        this.globalAmbience = DEFAULT_GLOBAL_AMBIENCE;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the target framerate.
     * @return  The target framerate.
     */
    public int getFramerate() {
        return this.framerate;
    }
    
    
    /**
     * Returns the polygon offset factor used to prevent z-fighting.
     * @return  The polygon offset factor.
     */
    public float getPolygonOffsetFactor() {
        return this.polygonOffsetFactor;
    }
    
    /**
     * Returns the polygon offset units used to prevent z-fighting.
     * @return  The polygon offset units.
     */
    public float getPolygonOffsetUnits() {
        return this.polygonOffsetUnits;
    }
    
    /**
     * Returns whether smooth shading is enabled.
     * @return  Whether smooth shading is enabled.
     */
    public boolean smoothShadingEnabled() {
        return this.smoothShading;
    }
    
    /**
     * Returns whether antialiasing is enabled.
     * @return  Whether antialiasing is enabled.
     */
    public boolean antialiasingEnabled() {
        return this.antialiasing;
    }
    
    /**
     * Returns the level of antialiasing used.
     * @return  Either <code>GL.GL_NICEST</code>, <code>GL.GL_FASTEST</code>, or 
     *          <code>GL.GL_DONT_CARE</code>.
     */
    public int getAntialiasLevel() {
        return this.antialiasLevel;
    }
    
    /**
     * Returns whether lazy-loading is enabled for models.
     * @return  Whether lazy-loading is enabled.
     */
    public boolean lazyLoadingEnabled() {
        return this.lazyLoad;
    }
    
    /**
     * Returns the clear color.
     * @return  The clear color.
     */
    public Color getClearColor() {
        return this.clearColor;
    }
    
    /**
     * Returns the color of light emitted by the global ambient light source.
     * @return  The color global ambient light.
     */
    public Color getGlobalAmbience() {
        return this.globalAmbience;
    }
    
    /**
     * Returns the polygon drawing mode.
     * @return  Either <code>GL.GL_POINT</code>, <code>GL.GL_LINE</code>, or 
     *          <code>GL.GL_FILL</code>.
     */
    public int getPolygonMode() {
        return this.polygonMode;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the target framerate.
     * @param framerate The target framerate. The given value will be clamped to 
     *                  0 if negative.
     */
    public void setFramerate(int framerate) {
        this.framerate = (framerate < 0)? 0 : framerate;
        setChanged();
        notifyObservers(SettingType.FRAMERATE);
    }
        
    /**
     * Sets the polygon offset factor used to prevent z-fighting.
     * @param offsetFactor  The polygon offset factor.
     */
    public void setPolygonOffsetFactor(float offsetFactor) {
        this.polygonOffsetFactor = offsetFactor;
        setChanged();
        notifyObservers(SettingType.POLYGON_OFFSET_FACTOR);
    }
    
    /**
     * Sets the polygon offset units used to prevent z-fighting.
     * @param offsetUnits   The polygon offset units.
     */
    public void setPolygonOffsetUnits(float offsetUnits) {
        this.polygonOffsetUnits = offsetUnits;
        setChanged();
        notifyObservers(SettingType.POLYGON_OFFSET_UNITS);
    }
    
    /**
     * Sets whether smooth shading is enabled.
     * @param enabled   Whether smooth shading should be enabled.
     */
    public void enableSmoothShading(boolean enabled) {
        // no change?
        if (this.smoothShading == enabled)
            return;
        
        // update field
        this.smoothShading = enabled;
        setChanged();
        notifyObservers(SettingType.SMOOTH_SHADING);
    }
    
    /**
     * Toggles smooth shading.
     */
    public void toggleSmoothShading() {
        enableSmoothShading(!this.smoothShading);
    }
    
    /**
     * Sets whether antialiasing is enabled.
     * @param enabled   Whether antialiasing should be enabled.
     */
    public void enableAntialiasing(boolean enabled) {
        // no change?
        if (this.antialiasing == enabled)
            return;
        
        // update field
        this.antialiasing = enabled;
        setChanged();
        notifyObservers(SettingType.ANTIALIASING);
    }
    
    /**
     * Toggles antialiasing.
     */
    public void toggleAntialiasing() {
        enableAntialiasing(!this.antialiasing);
    }
    
    /**
     * Sets the level of antialiasing used.
     * @param level     The level of antialiasing to use. Either <code>GL.GL_NICEST</code>, 
     *                  <code>GL.GL_FASTEST</code>, or <code>GL.GL_DONT_CARE</code>.
     */
    public void setAntialiasLevel(int level) {
        this.antialiasLevel = level;
        setChanged();
        notifyObservers(SettingType.AA_LEVEL);
    }
    
    /**
     * Sets whether lazy-loading is enabled.
     * @param enabled Whether lazy-loading should be enabled.
     */
    public void enableLazyLoading(boolean enabled) {
        // no change?
        if (this.lazyLoad == enabled)
            return;
        
        // update field
        this.lazyLoad = enabled;
        setChanged();
        notifyObservers(SettingType.LAZY_LOADING);
    }
    
    /**
     * Toggles lazy-loading.
     */
    public void toggleLazyLoading() {
        enableLazyLoading(!this.lazyLoad);
    }
    
    /**
     * Sets the clear color.
     * @param color The clear color.
     */
    public void setClearColor(Color color) {
        this.clearColor = color;
        setChanged();
        notifyObservers(SettingType.CLEAR_COLOR);
    }
    
    /**
     * Sets the color of light emitted by the global ambient light source.
     * @param color The color of global ambient light.
     */
    public void setGlobalAmbience(Color color) {
        this.globalAmbience = color;
        setChanged();
        notifyObservers(SettingType.GLOBAL_AMBIENCE);
    }
    
    /**
     * Sets the polygon drawing mode.
     * @param mode  Either <code>GL.GL_POINT</code>, <code>GL.GL_LINE</code>, or 
     *          <code>GL.GL_FILL</code>.
     */
    public void setPolygonMode(int mode) {
        this.polygonMode = mode;
    }

}
