
package org.rowan.camera;

import javax.swing.*;

/**
 * A stand-alone window containing a camera control panel. The <code>ControlWindow</code>
 * class provides a convenient method of instantiating a <code>ControlPanel</code>
 * embedded in its own, independent frame.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class ControlWindow {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The JFrame used to display the control panel. */
    private JFrame frame;
    
    /** The control panel embedded in the window. */
    private ControlPanel controlPanel;


    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates a stand-alone camera control window with an embedded <code>ControlPanel</code>.
     */
    public ControlWindow() {
        this(null);
    }
    
    /**
     * Creates a stand-alone camera control window with an embedded <code>ControlPanel</code>
     * linked to the given <code>Camera</code> object.
     * @param camera    The camera to be controlled by the created control window.
     */
    public ControlWindow(Camera camera) {
        // create control panel
        this.controlPanel = new ControlPanel(camera);
        
        // setup frame
        this.frame = new JFrame("Camera Control Panel");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        // add control panel to frame
        this.frame.getContentPane().add(controlPanel);
        
        // show the frame
        show();
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the control panel embedded in the window.
     * @return  The <code>ControlPanel</code> object associated with the window.
     */
    public ControlPanel getControlPanel() {
        return this.controlPanel;
    }
    
    
    /*********************************************
     * MARK: Window Control
     *********************************************/
    
    /**
     * Shows the control window, in the event that it has been closed.
     */
    public void show() {
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Hides the control window.
     */
    public void hide() {
        frame.setVisible(false);
    }
    
    /**
     * Returns the control window's title string.
     * @return  The title shown in the control window's title bar.
     */
    public String getTitle() {
        return frame.getTitle();
    }
    
    /**
     * Sets the control window's title to a given string.
     * @param title A title to be set in the control window's title bar.
     */
    public void setTitle(String title) {
        frame.setTitle(title);
    }

}
