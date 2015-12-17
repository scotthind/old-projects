package org.rowan.reverie.view;

import java.awt.*;
import javax.swing.*;
import javax.media.opengl.*;

/**
 * A <code>WindowView</code> provides a simple single-window view of a simulation
 * space. Upon instantiation, a single window will be created to display the view. 
 * 
 * @author Spence DiNicolantonio
 * @version 1.3
 * @since 1.3
 */
public class WindowView implements View {
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The default title displayed in the title bar of the view's default frame. */
    public static final String DEFAULT_TITLE = "Reverie - A Visualization Test Space";
    
    /** The default window size for a space's display. */
    public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1024, 768);
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The view's window. */
    private JFrame frame;
    
    /** The view's canvas. */
    private GLCanvas canvas;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a new <code>WindowView</code>.
     */
    public WindowView() {
        this(DEFAULT_TITLE);
    }
    
    /**
     * Creates a new <code>WindowView</code> using a given title for the window.
     * @param title The title to be displayed in the window's title bar.
     */
    public WindowView(String title) {
        // create and configure canvas
        this.canvas = new GLCanvas();
        canvas.setFocusable(true);
        canvas.setIgnoreRepaint(true);
        
        // get current graphics configuration
        GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        
		// create and configure window
		this.frame = new JFrame(title, config);
        frame.setIgnoreRepaint(true);
        frame.setFocusTraversalKeysEnabled(false);
		frame.setSize(DEFAULT_WINDOW_SIZE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // add view's canvas to the window frame
        frame.add(canvas);
		
		// make the frame visible
		frame.setVisible(true);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the view's window frame.
     * @return  The <code>JFrame</code> used to display the view.
     */
    public JFrame getFrame() {
        return this.frame;
    }
    
    /**
     * Returns the view's canvas.
     * @return  The <code>GLCanvas</code> used to draw the view.
     */
    public GLCanvas getCanvas() {
        return this.canvas;
    }
    
    /**
     * Returns whether fullsreen mode is enabled.
     * @return  Whether fullsreen mode is enabled.
     */
    public boolean fullscreenEnabled() {
        // get graphics device
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        // fullscreen not supported?
        if (!device.isFullScreenSupported())
            return false;
        
        // not fullscreen window?
        return frame.equals(device.getFullScreenWindow());
    }
    
    
    /*********************************************
     * MARK: Fullscreen Mode
     *********************************************/
    
    /**
     * Enables/disables exclusive fullscreen mode if supported by the current 
     * graphics device. When fullscreen is enabled, the view's window will be 
     * set as the current graphics device's fullscreen window, regardless of 
     * whether there is already a window set as the fullscreen window.
     * @param enable    Whether fullscreen mode should be enabled.
     */
    public void setFullScreenEnabled(boolean enable) {
        if (enable)
            enableFullscreen();
        else
            disableFullscreen();
    }
    
    /**
     * Enables exclusive fullscreen mode, if supported by the current graphics
     * device. When invoked, the view's window will be set as the current graphics 
     * device's fullscreen window, if it is not already.
     */
    public void enableFullscreen() {
        // get graphics device
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        // fullscreen not supported?
        if (!device.isFullScreenSupported())
            return;
        
        // need to dispose of the frame before changing it
        frame.dispose();
        
        // undecorate frame
        frame.setUndecorated(true);
        
        // disable frame resizing
        frame.setResizable(false);
        
        // enable fullscreen
        device.setFullScreenWindow(this.frame);
    }
    
    /**
     * Disables exclusive fullscreen mode, if the view's window is the curerent
     * graphics device's fullscreen window. If the view's window is not the current
     * graphics device's fullscreen window, this method will do nothing.
     */
    public void disableFullscreen() {
        // get graphics device
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        // not using view's window as fullscreen window?
        if (!fullscreenEnabled())
            return;
        
        // fullscreen not supported?
        if (!device.isFullScreenSupported())
            return;
        
        
        // need to dispose of the frame before changing it
        this.frame.dispose();
        
        // decorate frame
        this.frame.setUndecorated(false);
        
        // enable frame resizing
        this.frame.setResizable(true);
        
        // disable fullscreen
        device.setFullScreenWindow(null);
        
        // show the frame
        this.frame.setVisible(true);
    }
    
    /**
     * Toggles exclusive fullscreen mode when using the view's default frame
     * in exclusive fullscreen mode. This method will only work if the view is 
     * displayed using <code>openInNewWindow()</code>.
     */
    public void toggleFullscreen() {
        // not using default frame?
        if (this.frame == null)
            return;
        
        // get graphics device
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        // not using default frame as fullscreen window?
        if (fullscreenEnabled())
            disableFullscreen();
        else
            enableFullscreen();
    }
    
}
