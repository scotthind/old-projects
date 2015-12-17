package org.rowan.reverie.view;

import javax.media.opengl.*;

/**
 * The <code>ComponentView</code> class is an extenssion of <code>GLCanvas</code>
 * used strictly for drawing a simulation space. A component view can be added to 
 * any AWT or Swing based container to provide integration within an application. 
 * 
 * @author Spence DiNicolantonio
 * @version 1.3
 * @since 1.3
 */
public class ComponentView extends GLCanvas implements View {
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates a new <code>ComponentView</code>.
     */
    public ComponentView() {
        // call super constructor
        super();
    }
    
    
    /*********************************************
     * MARK: View
     *********************************************/
    
    /**
     * Returns the view's canvas.
     * @return  The <code>GLCanvas</code> used to draw the view.
     */
    public GLCanvas getCanvas() {
        return this;
    }

}
