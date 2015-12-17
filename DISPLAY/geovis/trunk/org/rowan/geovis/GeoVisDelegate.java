package org.rowan.geovis;

/**
 * The <code>GeoVisDelegate</code> interface defines a template for delegate 
 * objects used to update an <code>GeoVis</code> visualization. 
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public interface GeoVisDelegate {
    
    /**
     * Called by a delegating <code>GeoVis</code> before drawing every frame.
     * @param vis The <code>GeoVis</code> from which the call was generated.
     */
    public void update(GeoVis vis);
    
}