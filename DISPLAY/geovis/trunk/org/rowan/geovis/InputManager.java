package org.rowan.geovis;

import static java.awt.event.KeyEvent.*;

import java.io.*;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;

import org.rowan.linalgtoolkit.*;
import org.rowan.linalgtoolkit.transform3d.*;
import org.rowan.camera.*;
import org.rowan.reverie.*;
import org.rowan.reverie.view.*;

/**
 * The <code>InputManager</code> class manages keyboard and mouse input for GeoVis
 * visualizations.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public class InputManager implements KeyListener, MouseMotionListener {
    
    /*********************************************
	 * MARK: Controls
	 *********************************************/
    
    /** Mouse sensitivity. */
    public static final double MOUSE_SENSITIVITY = 0.1;
    
    /** The position on which the camera's focus locks when focus-lock is toggled on. */
    public static final Vector3D FOCUS_LOCK_POINT = Vector3D.ORIGIN;
    
    
    
    /*********************************************
	 * MARK: Keybinding
	 *********************************************/
    
    /** Quit. */
    public static final int QUIT                        = VK_ESCAPE;
    
    /** Toggle fullscreen. */
    public static final int TOGGLE_FULLSCREEN           = VK_F;
    
    /** Toggle earth model. */
    public static final int TOGGLE_EARTH_MODEL          = VK_E;
    
    /** Toggle star field. */
    public static final int TOGGLE_STAR_FIELD           = VK_T;
    
    /** Toggle center points. */
    public static final int TOGGLE_CENTER               = VK_C;
    
    /** Toggle bounding boxes. */
    public static final int TOGGLE_BOUNDING_BOX         = VK_B;
    
    
    
    /** Move the camera forward. */
    public static final int MOVE_CAMERA_FORWARD         = VK_W;
    
    /** Move the camera backward. */
    public static final int MOVE_CAMERA_BACKWARD        = VK_S;
    
    /** Move the camera left. */
    public static final int MOVE_CAMERA_LEFT            = VK_A;
    
    /** Move the camera right. */
    public static final int MOVE_CAMERA_RIGHT           = VK_D;
    
    /** Move the camera up. */
    public static final int MOVE_CAMERA_UP              = VK_SPACE;
    
    /** Move the camera down. */
    public static final int MOVE_CAMERA_DOWN            = VK_SHIFT;
    
    
    
    /** Rotate the camera pitch up. */
    public static final int ROTATE_CAMERA_PITCH_UP      = VK_UP;
    
    /** Rotate the camera pitch down. */
    public static final int ROTATE_CAMERA_PITCH_DOWN    = VK_DOWN;
    
    /** Rotate the camera yaw left. */
    public static final int ROTATE_CAMERA_YAW_LEFT      = VK_LEFT;
    
    /** Rotate the camera yaw right. */
    public static final int ROTATE_CAMERA_YAW_RIGHT     = VK_RIGHT;
    
    /** Rotate the camera roll left. */
    public static final int ROTATE_CAMERA_ROLL_LEFT     = VK_COMMA;
    
    /** Rotate the camera roll right. */
    public static final int ROTATE_CAMERA_ROLL_RIGHT    = VK_PERIOD;
    
    
    
    /** Increase camera speed. */
    public static final int INCREASE_CAMERA_SPEED       = VK_EQUALS;
    
    /** Decrease camera speed. */
    public static final int DECREASE_CAMERA_SPEED       = VK_MINUS;
    
    
    
    /** Toggle camera focus lock. */
    public static final int TOGGLE_FOCUS_LOCK           = VK_L;
    
    /** Reset camera. */
    public static final int RESET_CAMERA                = VK_R;
    
    
        
    /** Toggle camera light. */
    public static final int TOGGLE_CAMERA_LIGHT         = VK_1;
    
    
    
    /** Cycle polygon mode. */
    public static final int CYCLE_POLYGON_MODE          = VK_Z;
    
    /** Toggle smooth shading. */
    public static final int TOGGLE_SMOOTH_SHADING       = VK_X;
    
    
    /*********************************************
	 * MARK: Other Constants
	 *********************************************/
    
    /** The transparent cursor image file path. */
    public static final String CURSOR_IMG_PATH = "org/rowan/geovis/images/cursor.png";
        
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The GeoVis visualization for which input is being managed. */
    private GeoVis visualization;
    
    /** A linked list of <code>KeyEvent</code>s that are currently active. */
	private LinkedList<KeyEvent> keyStack;
    
    /** The last know position of the mouse pointer. */
    private Point prevMousePoint;
    
    /** A transparent cursor used during fullscreen mode */
	private Cursor transparentCursor;
    
    /** A robot used for controlling cursur position when mouse-look is enabled */
    private Robot robot;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/ 
    
    /**
     * Creates an <code>InputManager</code> object to manage a given visualization.
     * @param visualization The visualization for which input will be managed.
     */
    public InputManager(GeoVis visualization) {
        this.visualization = visualization;
        
        // initialize key stack
        this.keyStack = new LinkedList<KeyEvent>();
        
        // set this input manager as the visualization's event listeners
        Canvas canvas = visualization.getSpace().getView().getCanvas();
        canvas.addKeyListener(this);
        canvas.addMouseMotionListener(this);
        
        // load transparent cursor image
		BufferedImage image = null;
        try {
            // create input stream for file
			InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(CURSOR_IMG_PATH);
			
            // read image data
            image = ImageIO.read(inputStream);
		} catch (Exception e) {
            System.err.println("Could not load cursor image");
        }
        
        // create transparent cursor
        if (image != null)
            transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "transparentCursor");        
    
        // initialize robot
		try {
			this.robot = new Robot();
		} catch (Exception e) {
			System.out.println("Could not initialize robot");
		}        
    }
    
    
    /*********************************************
	 * MARK: Key Listener
	 *********************************************/
	
	/**
	 * This method is is called when a key is pressed.
	 * @param e A KeyEvent object that describes the performed event.
	 */
	public void keyPressed(KeyEvent e) {
        // get visualization's simulation space
        Space space = visualization.getSpace();
        
        // get visualization's camera from space
        ReverieCamera camera = space.getCamera();
        
		// toggles:
		switch (e.getKeyCode()) {
				
            // lock camera focus
			case TOGGLE_FOCUS_LOCK:
				if (!camera.focusLocked())
					camera.lookAt(FOCUS_LOCK_POINT);
				camera.setFocusLocked(!camera.focusLocked());
				System.out.println("Toggle Focus Lock: "+ camera.focusLocked());
				return;
                                
            // reset camera
			case RESET_CAMERA:
				camera.reset();
				System.out.println("Camera Reset");
				return;
				
            // toggle fullscreen mode
			case TOGGLE_FULLSCREEN:
                
                // window view?
                if (space.getView() instanceof WindowView) {
                    
                    // cast view to WindowView
                    WindowView view = (WindowView)space.getView();
                    
                    // toggle fullscreen
                    view.toggleFullscreen();
                    
                    // show cursor?
                    System.out.println(view.fullscreenEnabled());
                    if (view.fullscreenEnabled())
                        view.getFrame().setCursor(transparentCursor);
                    else
                        view.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
				return;
                
            // toggle earth display
            case TOGGLE_EARTH_MODEL:
                visualization.toggleEarthModel();
                return;
                
            // toggle star field display
            case TOGGLE_STAR_FIELD:
                visualization.toggleStarField();
                return;
                
            // toggle center points
            case TOGGLE_CENTER:
                visualization.toggleCenterPoints();
				System.out.println("Toggle Center Points: " + 
                                   visualization.willDrawCenterPoints());
                return;
                
            // toggle bounding boxes
            case TOGGLE_BOUNDING_BOX:
                visualization.toggleBoundingBoxes();
				System.out.println("Toggle Bounding Boxes: " + 
                                   visualization.willDrawBoundingBoxes());
                return;
                
            // toggle camera light
            case TOGGLE_CAMERA_LIGHT:
                camera.getLight().toggle();
				System.out.println("Toggle Camera Light: " + 
                                   camera.getLight().isOn());
                return;
                
            // cycle polygon mode
			case CYCLE_POLYGON_MODE:
                int currentMode = space.getSettings().getPolygonMode();
                int newMode = GL.GL_FILL;
                switch (currentMode) {
                    case GL.GL_POINT:
                        newMode = GL.GL_LINE;
                        break;
                    case GL.GL_LINE:
                        newMode = GL.GL_FILL;
                        break;
                    case GL.GL_FILL:
                        newMode = GL.GL_POINT;
                        break;
                }
				space.getSettings().setPolygonMode(newMode);
				return;
               
            // toggle smooth shading
			case TOGGLE_SMOOTH_SHADING:
                boolean current = space.getSettings().smoothShadingEnabled();
				space.getSettings().enableSmoothShading(!current);
				return;
                
			default:
				break;
		}
		
		// supress repeated keys
		for (KeyEvent stackedEvent : keyStack)
			if (e.getKeyCode() == stackedEvent.getKeyCode())
				return;
		
		// add event to keyStack
		keyStack.addLast(e);
	}
	
	/**
	 * This method is called when a key is released. 
	 * @param e A KeyEvent object that describes the performed event.
	 */
	public void keyReleased(KeyEvent e) {
		// remove the event from the keyStack
		for (int i=0; i<keyStack.size(); i++)
			if (e.getKeyCode() == keyStack.get(i).getKeyCode())
				keyStack.remove(i);
	}
	
	/**
	 * This method is called when a key is typed
	 * @param e A KeyEvent that describes what key was typed.
	 */
	public void keyTyped(KeyEvent e) {
	}
	
	/**
	 * This method is called during every frame update to process all key input.
	 */
	public void processKeyInput() {        
        // get visualization's camera
        ReverieCamera camera = visualization.getSpace().getCamera();
        
		// check each key that is currently pressed and call appropriate methods
		for (KeyEvent stackedEvent : keyStack)
			
			switch (stackedEvent.getKeyCode()) {
                    
                // Quit
				case QUIT:
					System.exit(0);
					break;
					
                // camera movement
				case MOVE_CAMERA_FORWARD:
					camera.moveForward();
					break;
				case MOVE_CAMERA_BACKWARD:
					camera.moveBackward();
					break;
				case MOVE_CAMERA_LEFT:
					camera.moveLeft();
					break;
				case MOVE_CAMERA_RIGHT:
					camera.moveRight();
					break;
				case MOVE_CAMERA_UP:
					camera.moveUp();
					break;
				case MOVE_CAMERA_DOWN:
					camera.moveDown();
					break;
					
                // camera rotation
				case ROTATE_CAMERA_PITCH_UP:
					camera.pitchUp();
					break;
				case ROTATE_CAMERA_PITCH_DOWN:
					camera.pitchDown();
					break;
				case ROTATE_CAMERA_YAW_LEFT:
					camera.yawLeft();
					break;
				case ROTATE_CAMERA_YAW_RIGHT:
					camera.yawRight();
					break;
				case ROTATE_CAMERA_ROLL_LEFT:
					camera.rollLeft();
					break;
				case ROTATE_CAMERA_ROLL_RIGHT:
					camera.rollRight();
					break;
					
                // camera speed
				case INCREASE_CAMERA_SPEED:
					camera.setSpeed(camera.getSpeed() * 1.1);
					break;
				case DECREASE_CAMERA_SPEED:
					camera.setSpeed(camera.getSpeed() / 1.1);
					break;
                    
					
				default:
					break;
			}
	}
	
	
	/*********************************************
	 * MARK: Mouse Listener
	 *********************************************/
    
    /**
     * Called when a mouse button is pressed over a component.
     * @param e A MouseEvent object that describes the performed event.
     */
    public void mousePressed(MouseEvent e) {
        // update mouse point
        this.prevMousePoint = e.getPoint();
    }
	
	/**
	 * Called when the mouse is moved.
	 * @param e A MouseEvent object that describes the performed event.
	 */
	public void mouseMoved(MouseEvent e) {
        // get visualization's simulation space
        Space space = visualization.getSpace();
        
        // get visualization's camera
        ReverieCamera camera = space.getCamera();
		
		// get new mouse point
		Point newPoint = e.getPoint();
        
        // previous mouse point not yet set?
        if (prevMousePoint == null)
            prevMousePoint = newPoint;
		
		// calculate movement
		int deltaX = newPoint.x - prevMousePoint.x;
		int deltaY = newPoint.y - prevMousePoint.y;
        
        
        // compute drag angle-per-pixel
        JFrame frame = ((WindowView)space.getView()).getFrame();
        double[] anglePerPixel = GestureController.computeDragAnglePerPixel(deltaX, deltaY, frame.getSize(), camera.getFieldOfView());
		
        
        // fullscreen?
        if (space.getView() instanceof WindowView && ((WindowView) space.getView()).fullscreenEnabled()) {
            
            // focus locked?
            if (space.getCamera().focusLocked()) {
                // roll camera
                GestureController.rotate(camera,
                                         deltaX, 
                                         deltaY, 
                                         Vector3D.Z_AXIS.inverse(), 
                                         Vector3D.X_AXIS, 
                                         anglePerPixel[0], 
                                         anglePerPixel[1], 
                                         true);
                
            } else {
                // rotate camera
                GestureController.rotate(camera,
                                         deltaX, 
                                         deltaY, 
                                         Vector3D.Y_AXIS.inverse(),
                                         Vector3D.X_AXIS.inverse(), 
                                         anglePerPixel[0], 
                                         anglePerPixel[1], 
                                         true);
            }
            
            // get screen size
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            // recenter mouse point
            this.prevMousePoint = new Point((int)screenSize.width/2,
                                            (int)screenSize.height/2);
            this.robot.mouseMove((int)screenSize.width/2, 
                                       (int)screenSize.height/2);
        } else {
            prevMousePoint = newPoint;
        }

	}
	
	/**
	 * Called when the mouse is dragged.
	 */
	public void mouseDragged(MouseEvent e) {
        // get visualization's simulation space
        Space space = visualization.getSpace();
        
        // calculate movement from previous point
        int deltaX = e.getPoint().x - prevMousePoint.x;
        int deltaY = e.getPoint().y - prevMousePoint.y;
        
        // compute drag angle-per-pixel
        JFrame frame = ((WindowView)space.getView()).getFrame();
        double[] anglePerPixel = GestureController.computeDragAnglePerPixel(deltaX, deltaY, frame.getSize(), space.getCamera().getFieldOfView());
        

		// fullscreen or windowed mode?
		if (space.getView() instanceof WindowView && ((WindowView) space.getView()).fullscreenEnabled()) {
        
            // focus locked? (rotate around focus point)
            if (space.getCamera().focusLocked()) {
                Vector3D xAxis = Vector3D.Y_AXIS.inverse().rotate(space.getCamera().getOrientation());
                Vector3D yAxis = Vector3D.X_AXIS.inverse().rotate(space.getCamera().getOrientation());
                GestureController.rotate(space.getCamera(),
                                         space.getCamera().getFocusPoint(),
                                         deltaX, 
                                         deltaY, 
                                         xAxis, 
                                         yAxis, 
                                         anglePerPixel[0], 
                                         anglePerPixel[1],
                                         true);
            } else {
                // roll camera
                GestureController.rotate(space.getCamera(),
                                         deltaX, 
                                         deltaY, 
                                         Vector3D.Z_AXIS.inverse(), 
                                         Vector3D.X_AXIS.inverse(), 
                                         anglePerPixel[0], 
                                         anglePerPixel[1],
                                         true);
            }
            
            // get screen size
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            // reset mouse point to screen center
            this.robot.mouseMove((int)screenSize.width/2, 
                                 (int)screenSize.height/2);
            
        } 
        
        // not fullscreen:
        else {
            
            // right click?
            if (e.getButton() == MouseEvent.BUTTON3) {
                
                // focus locked?
                if (space.getCamera().focusLocked()) {
                    // roll camera
                    GestureController.rotate(space.getCamera(),
                                             deltaX, 
                                             deltaY, 
                                             Vector3D.Z_AXIS, 
                                             Vector3D.ZERO_VECTOR, 
                                             anglePerPixel[0], 
                                             0,
                                             true);
                } else {
                    // move camera
                    GestureController.move(space.getCamera(),
                                           deltaX, 
                                           deltaY,
                                           Vector3D.X_AXIS.inverse(),
                                           Vector3D.Y_AXIS,
                                           space.getCamera().getSpeed() * MOUSE_SENSITIVITY,
                                           space.getCamera().getSpeed() * MOUSE_SENSITIVITY,
                                           true);
                }
                
            } else {
                
                // focus locked?
                if (space.getCamera().focusLocked()) {
                    // rotate camera around focus point
                    Vector3D xAxis = Vector3D.Y_AXIS.inverse().rotate(space.getCamera().getOrientation());
                    Vector3D yAxis = Vector3D.X_AXIS.inverse().rotate(space.getCamera().getOrientation());
                    GestureController.rotate(space.getCamera(),
                                             space.getCamera().getFocusPoint(),
                                             deltaX, 
                                             deltaY, 
                                             xAxis, 
                                             yAxis, 
                                             anglePerPixel[0], 
                                             anglePerPixel[1],
                                             true);
                } else {
                    // rotate camera
                    GestureController.rotate(space.getCamera(),
                                             deltaX, 
                                             deltaY, 
                                             Vector3D.Y_AXIS, 
                                             Vector3D.X_AXIS, 
                                             anglePerPixel[0], 
                                             anglePerPixel[1], 
                                             true);
                }

            }
            
            // update mouse point
            prevMousePoint = e.getPoint();
        }
	}
        
}