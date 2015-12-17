package org.rowan.reverie;

import javax.media.opengl.*;

/**
 * The <code>SpaceDelegate</code> interface defines a template for delegate objects
 * that can be implemented to provide drawing and update logic for a simulation 
 * space.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public interface SpaceDelegate {
    
    /**
     * Called by a delegating space immediately after initialization. Any initializations
     * needed should be performed here.
     * @param space     The space from which the call was generated.
     * @param drawable  The drawable used to render the space.
     */
    public void init(Space space, GLAutoDrawable drawable);
    
    /**
     * Called by a delegating space before drawing every frame.
     * @param space The space from which the call was generated.
     */
    public void update(Space space);
    
    /**
     * Called by a delegating space after updating. All drawing logic should be
     * contained here.
     * @param space     The space from which the call was generated.
     * @param drawable  The drawable used to render the space.
     */
    public void display(Space space, GLAutoDrawable drawable);
    
}