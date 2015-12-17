package org.rowan.reverie;

import static org.rowan.reverie.Settings.*;

import java.util.*;
import java.nio.DoubleBuffer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import org.rowan.camera.Camera;
import org.rowan.linalgtoolkit.transform3d.*;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.reverie.lights.*;
import org.rowan.reverie.view.*;
import org.rowan.reverie.Settings.SettingType;


/**
 * The <code>Space</code> class defines a navigable Euclidean space in which 
 * JOGL based visualizations can be constructed and explored in real time. 
 * Navigation throughout the space is provided via integration of the camera 
 * module. A <code>Space</code> can be presented using a simple windowed display,
 * or a component that can be inserted into any AWT or Swing based GUI.
 * <p>
 * A <code>SpaceDelegate</code> should be used to provide initialization and drawing 
 * logic to a space.
 * <p>
 * After initializing a space and setting the space's delegate as needed, a call 
 * to the <code>start()</code> method should be made to initialize the OpenGL 
 * context and begin simulation/animation.
 * <p>
 * For convenience a space initially contains a single directional light source 
 * that provides enough ambient light to fill the scene. However, light sources 
 * can be added/removed as needed to customize simulation lighting. Up to 8 light 
 * sources are supported within any given space. Any additional sources will be 
 * ignored. Optionally, a camera spot light can be enabled. When enabled, the 
 * camera light is considered as the 8th light source. Therefore, if an 8th light 
 * source exists, it will be ignored upon enabling the camera light.
 * <p>
 * A <code>Settings</code> object is used to define all draw settings for the 
 * space. Specific settings can be changed within a <code>Settings</code> object
 * and viewed in real time. In addition, <code>Settings</code> objects can be
 * swapped in and out at runtime for convenient configuration toggling.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public final class Space implements GLEventListener, Observer {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The view used to display the space. */
    private View view;
    
    /** A collection of settings used to configure the space. */
    private Settings settings;
    
    /** A delegate object for performing extending drawing and update logic. */
    private SpaceDelegate delegate;
        
    /** The current frame (number of frames drawn so far). */
    private long frameCount;
    
    
    /** A camera instance used to control navigation within the space. */
    private ReverieCamera camera;
    
    
    /** A GLU object used to access GLU utility methods. */
    private GLU glu;
    
    /** A GLUT object used to access GLUT utility methods. */
    private GLUT glut;
            
    /** The animation timer used to progress through drawing frames. */ 
	private FPSAnimator animator;
    
    
    /** An array of light sources contained in the space. */
    private Light[] light;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/ 
    
    /**
     * Creates a <code>Space</code> object with default settings and a windowed
     * view.
     */
    public Space() {
        this(new WindowView(), new Settings());
    }
    
    /**
     * Creates a <code>Space</code> object with default settings and a specified
     * view.
     * @param view  The view with which the created space will be displayed.
     */
    public Space(View view) {
        this(view, new Settings());
    }
    
    /**
     * Creates a <code>Space</code> object with a windowed view and given settings.
     * @param settings  The settings to use in the created space.
     */
    public Space(Settings settings) {
        this(new WindowView(), settings);
    }
    
    /**
     * Creates a <code>Space</code> object a specified view and settings.
     * @param view      The view with which the created space will be displayed.
     * @param settings  The settings to use in the created space.
     */
    public Space(View view, Settings settings) {
        // define settings
        setSettings(settings);
        
        // initialize view
        this.view = view;
        
        // initialize camera
        this.camera = new ReverieCamera();
        
        
        // initialize GLU and GLUT
        this.glu = new GLU();
        this.glut = new GLUT();
        
        
        // create default directional light
        Light defaultLight = new DirectionalLight(Vector3D.Y_AXIS.inverse());
        defaultLight.setAmbient(Color.WHITE);
        
        // initialize light array
        this.light = new Light[8];
        this.light[0] = defaultLight;
        for (int i=1; i<8; i++)
            light[i] = null;
        
        
        // add space to view's canvas as OpenGL event listener
        view.getCanvas().addGLEventListener(this);
    }
    
    /**
     * Initializes and starts the space's animator, using a given target framerate. 
     * If an animator has already been initialized, the current animator will be 
     * stopped prior to initializing a new one.
     */
    private void setupAnimator(int framerate) {
        // current animator exists?
        if (this.animator != null)
            this.animator.stop();
        
        // create new animator for canvas
        this.animator = new FPSAnimator(view.getCanvas(), framerate);
        this.animator.start();
    }
    
    
    /*********************************************
	 * MARK: Accessors
	 *********************************************/
    
    /**
     * Returns the space's view.
     * @return  The space's view.
     */
    public View getView() {
        return this.view;
    }
    
    /**
     * Returns the space's current settings.
     * @return  The space's current settings.
     */
    public Settings getSettings() {
        return this.settings;
    }
    
    /**
     * Returns the space's delegate object.
     * @return  The space's delegate object.
     */
    public SpaceDelegate getDelegate() {
        return this.delegate;
    }
    
    /**
     * Returns the camera used to navigate throughout the space.
     * @return  The camera used to navigate throughout the space.
     */
    public ReverieCamera getCamera() {
        return this.camera;
    }
    
    /**
     * Returns current frame. This is also the number of frames drawn so far.
     * @return  The current frame.
     */
    public long getFrameCount() {
        return this.frameCount;
    }
        
    /**
     * Retrieves a given light source from the space.
     * @param index The index of the desired light.
     * @throws      <code>IndexOutOfBoundsException</code> if the given index
     *              is not in the range [0, 7].
     */
    public Light getLight(int index) {
        // invalid index?
        if (index < 0 || index > 7)
            throw new IndexOutOfBoundsException("Light index must be in the range [0, 7]");
        
        // return appropriate light
        return this.light[index];
    }
    
    
    /*********************************************
	 * MARK: Mutators
	 *********************************************/
    
    /**
     * Sets the settings used by the space.
     * @param settings  The settings to use.
     */
    public void setSettings(Settings settings) {
        // stop observing current settings
        if (this.settings != null)
            this.settings.deleteObserver(this);
        
        // set new settings
        this.settings = settings;
        
        // start observing new settings
        settings.addObserver(this);
    }
    
    /**
     * Sets the space's delegate to a given object.
     * @param delegate  The space's new delegate object.
     */
    public void setDelegate(SpaceDelegate delegate) {
        this.delegate = delegate;
    }
            
    /**
     * Adds a given light to the space at a given index. If a light already exists
     * in the given index, that light will be replaced.
     * @param index The index at which light source should be added.
     * @param light The light source to be added.
     * @throws      <code>IndexOutOfBoundsException</code> if the given index is
     *              not in the range [0, 7].
     */
    public void setLight(int index, Light light) {
        // invalid index?
        if (index < 0 || index > 7)
            throw new IndexOutOfBoundsException("Light index must be in the range [0, 7]");
        
        // add light to light array
        this.light[index] = light;
    }
    
    
    /*********************************************
	 * MARK: Start/Stop
	 *********************************************/
    
    /**
     * Starts simulation/drawing within the Space. The first call to this method
     * will trigger initialization of the Space's OpenGL context.
     */
    public void start() {
        // setup and start the animator
        setupAnimator(settings.getFramerate());
    }
    
    /**
     * Halts simulation/drawing, until a following call to <code>start()</code>
     * is made.
     */
    public void stop() {
        // current animator exists?
        if (this.animator != null)
            this.animator.stop();
    }
    
    
    /*********************************************
	 * MARK: JOGL
	 *********************************************/
	
	/**
	 * A method used internally, that should not be called by end-users.
     * <p>
     * Called by the <code>GLAutoDrawable</code> immediately after the OpenGL 
     * context is initialized for the first time. This method will call the 
     * <code>init()</code> method of the space's delegate, if one has been assigned.
	 * @param drawable  The drawable object to be referred to.
	 */
	public final void init(GLAutoDrawable drawable) {
		// retreive the OpenGL rendering pipeline from the drawable object
		GL gl = drawable.getGL();
		
		// Set the clear color to black
        Color clearColor = settings.getClearColor();
		gl.glClearColor(clearColor.getRed(), 
                        clearColor.getGreen(), 
                        clearColor.getBlue(), 
                        clearColor.getAlpha());
        
        // enable automatic unit vector normalization
        gl.glEnable(GL.GL_NORMALIZE);
		
		// enable depth testing
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
        
        // enable polygon offset for Z-fighting correction
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(settings.getPolygonOffsetFactor(), 
                           settings.getPolygonOffsetUnits());
        
        
        // enable color material
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        
        
		// enable lighting
        gl.glEnable(GL.GL_LIGHTING);
        
        // set light model
        gl.glLightModelf(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
        gl.glLightModelf(GL.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
        
        // set global ambience
        float[] ambience = new float[4];
        settings.getGlobalAmbience().getRGBComponents(ambience);
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, ambience, 0);
        
        
		
        // set fragment shader correction hint
        gl.glHint(GL.GL_FRAGMENT_SHADER_DERIVATIVE_HINT, GL.GL_NICEST);
        
		// set perspective correction hint
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, 
				  GL.GL_NICEST);
		
		// set the matrix mode to model view
		gl.glMatrixMode(GL.GL_MODELVIEW);
        
        
        // call delegate init method
        if (delegate != null)
            delegate.init(this, drawable);
    }
	
	/**
	 * A method used internally, that should not be called by end-users.
     * <p>
     * Called to initiate the OpenGL rendering. This is also the method called 
	 * by the FPSAnimator object to update the display. This method will call 
	 * the update() method of the Space's delegate, if one has been specified.
	 * @param drawable  The drawable object to be referred to. 
	 */
	public final void display(GLAutoDrawable drawable) {
		// retreive the OpenGL rendering pipeline from the drawable object
		GL gl = drawable.getGL();
        
        
        // call delegate update method
        if (delegate != null)
            delegate.update(this);
        
        
        // increment frame counter
        frameCount++;
        
        
        // set polygon mode
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, settings.getPolygonMode());
        
		// set shade model
        if (settings.smoothShadingEnabled())
            gl.glShadeModel(GL.GL_SMOOTH);
        else
            gl.glShadeModel(GL.GL_FLAT);
        
        // enable antialiasing?
        if (settings.antialiasingEnabled()) {
            // enable antialiasing
            gl.glEnable(GL.GL_POINT_SMOOTH);
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glEnable(GL.GL_POLYGON_SMOOTH);
            
            // set AA level
            int antialiasLevel = settings.getAntialiasLevel();
            gl.glHint(GL.GL_POINT_SMOOTH_HINT, antialiasLevel);
            gl.glHint(GL.GL_LINE_SMOOTH_HINT, antialiasLevel);
            gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, antialiasLevel);
        }
        
		
		// clear the screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// set the current matrix to the MODELVIEW
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
		gl.glLoadIdentity();
        
        
        // setup camera light
        if (camera.getLight().isOn())
            camera.getLight().define(gl, GL.GL_LIGHT7);
        
		
		// get rotation matrix from camera
		Matrix rotationMatrix = camera.getOrientation().inverse().toMatrix();
		
		// get matrix data array
		double[] matrixData = rotationMatrix.toArray(Matrix.ArrayOrder.COLUMN_MAJOR);
		
		// create double buffer of matrix data
		DoubleBuffer matrixBuffer = BufferUtil.newDoubleBuffer(matrixData.length);
		matrixBuffer.put(matrixData);
		matrixBuffer.rewind();
		
		// set camera position
		gl.glMultMatrixd(matrixBuffer);
		gl.glTranslated(-camera.getPosition().getX(), 
						-camera.getPosition().getY(), 
						-camera.getPosition().getZ());
        
        
        // setup first 7 lights
        for (int i=0; i<7; i++) {
            // diable first
            gl.glDisable(GL.GL_LIGHT0+i);
            
            // define light
            if (light[i] != null)
                light[i].define(gl, GL.GL_LIGHT0+i);
        }
        
        // setup 8th light if camera light disabled
        if (!camera.getLight().isOn()) {
            // diable first
            gl.glDisable(GL.GL_LIGHT7);
            
            // define light
            if (light[7] != null)
                light[7].define(gl, GL.GL_LIGHT7);
        }
        
        
        // call delegate display method
        if (delegate != null)
            delegate.display(this, drawable);
    }
	
	/**
	 * A method used internally, that should not be called by end-users.
     * <p>
     * Called during the first repaint after the component is resized. This 
	 * includes the first time the component appears on the screen.
	 * @param drawable  The drawable object to be referred to.
	 * @param x         The x component of the new position.
	 * @param y         The y component of the new position.
	 * @param newWidth  The new width.
	 * @param newHeight The new height.
	 */
	public final void reshape(GLAutoDrawable drawable, 
                              int x, int y, 
                              int newWidth, int newHeight) {
		
		// retreive the OpenGL rendering pipeline from the drawable object
		GL gl = drawable.getGL();
				
		// set the viewport
		gl.glViewport(0, 0, newWidth, newHeight);
		
		// set the clipping volume
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(Math.toDegrees(camera.getFieldOfView()),
						   (float) newWidth / (float) newHeight,
						   camera.getNearClip(), 
						   camera.getFarClip());
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}
	
	/**
	 * A method used internally, that should not be called by end-users.
     * <p>
     * Called when the display mode or display device changes.
	 * @param drawable 
	 * @param modeChanged Whether the mode was changed or not
	 * @param deviceChanged Whether the device had been changed or not
	 */
	public final void displayChanged(GLAutoDrawable drawable,	
							   boolean modeChanged, 
							   boolean deviceChanged) {
	}
    
    
    /*********************************************
	 * MARK: Light-Independent Drawing
	 *********************************************/
    
    /** 
     * Starts a light-independent drawing block. All drawing performed after a 
     * call to this method will be drawn fully lit, independent of space lighting.
     * @param gl    The OpenGL rendering pipeline on which light-independent 
     *              drawing will occur.
     */
    public final void beginLightIndependentDrawing(GL gl) {
        // turn off lights 1-7
        for (int i=1; i<8; i++)
            gl.glDisable(GL.GL_LIGHT0+i);
        
        // define light 0 properties
        float[] ambient = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] specular = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] position = {0.0f, -1.0f, 1.0f, 0.0f};
        
        // set light 0 properties
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
        gl.glLightf(GL.GL_LIGHT0, GL.GL_CONSTANT_ATTENUATION, 0.0f);
        gl.glLightf(GL.GL_LIGHT0, GL.GL_LINEAR_ATTENUATION, 0.0f);
        gl.glLightf(GL.GL_LIGHT0, GL.GL_QUADRATIC_ATTENUATION, 0.0f);
        
        // enable light 0
        gl.glEnable(GL.GL_LIGHT0);
    }
    
    /**
     * Ends a light-independent drawing block. This method effectively reloads
     * all light settings. All transformations should be popped off the stack prior
     * to a call to this method.
     * @param gl    The OpenGL rendering pipeline on which light-independent 
     *              drawing occurred.
     */
    public final void endLightIndependentDrawing(GL gl) {
        // redefine light 0
        if (light[0] != null)
            light[0].define(gl, GL.GL_LIGHT0);
        
        // turn on lights
        for (int i=1; i<7; i++) {
            // no light defined?
            if (light[i] == null)
                continue;
            
            // turn on/off light
            if (light[i].isOn())
                gl.glEnable(GL.GL_LIGHT0+i);
            else
                gl.glDisable(GL.GL_LIGHT0+i);
        }
        
        // turn on light 7 if camera light if enabled
        if (camera.getLight().isOn())
            gl.glEnable(GL.GL_LIGHT7);
    }
    
    
    /*********************************************
     * MARK: Observer (settings)
     *********************************************/
    
    /**
     * Called by the space's settings object when any setting has changed.
     * @param settingsChanged   The settings object that changed.
     * @param settingType       The setting type that was changed.
     */
    public final void update(Observable settingsChanged, Object settingType) {
        // cast setting type
        SettingType type = (SettingType) settingType;
        
        // if framerate changed, reinitialize animator
        if (type == SettingType.FRAMERATE) {
            setupAnimator(settings.getFramerate());
        }
    }
    
}