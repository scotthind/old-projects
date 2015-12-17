package org.rowan.reverie.view;

import javax.media.opengl.*;

/**
 * A protocol for defining a view that uses a <code>GLCanvas</code> to display 
 * a <code>Space</code>.
 * 
 * @author Spence DiNicolantonio
 * @version 1.3
 * @since 1.3
 */
public interface View {
    

    /*********************************************
     * MARK: Methods
     *********************************************/
        
    /**
     * Returns the view's canvas.
     * @return  The <code>GLCanvas</code> used to draw the view.
     */
    public GLCanvas getCanvas();
        
}
