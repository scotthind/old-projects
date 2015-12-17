package org.rowan.geovis;

/**
 * The <code>DrawSettings</code> class contains a collection of constants that 
 * define various draw settings used by the GeoVis system.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public abstract class DrawSettings {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    
    /** Point size, in pixels. */
    public static final float POINT_SIZE = 0.02f;
    
    /** Line width, in pixels. */
    public static final float LINE_WIDTH = 0.02f;
    
    /** The number of slices used when drawing circles and ellipsoids. */
    public static final int CIRCLE_SLICES = 20;
    
    /** The color used to draw center points. */
    public static final double[] CENTER_POINT_COLOR = {.0, 0.0, 0.0};

}
