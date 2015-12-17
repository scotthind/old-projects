package org.rowan.geovis;

import static org.rowan.geovis.Draw.*;
import static org.rowan.geovis.DrawSettings.*;

import java.util.*;
import java.util.concurrent.*;

import org.rowan.linalgtoolkit.shapes2d.*;
import org.rowan.linalgtoolkit.shapes3d.*;
import org.rowan.reverie.*;
import org.rowan.geovis.models.*;

import javax.media.opengl.*;

/**
 * The <code>GeoVis</code> class provides a dynamic visualization space for the
 * Linear Algebra Toolkit. It encapsulates a Reverie simulation space in order 
 * to provide automated drawing of a collection of shapes, as well as an earth 
 * model that is to scale with the WGS-84 system.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public class GeoVis implements SpaceDelegate {
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** The number of kilometers represented by each Euclidean unit. */
    public static final double KM_PER_UNIT = 1000;
    
    
    
    /** Indicates whether the earth model should be turned on by default. */
	public static final boolean DRAW_EARTH = false;
    
    /** Indicates whether the star field should be turned on by default. */
	public static final boolean DRAW_STARS = false;
    
    /** The radius of the star field. */
	public static final double STAR_FIELD_RADIUS = 2000;
    
    
    
    /** Indicates whether center points should be drawn on shapes. */
    public static final boolean DRAW_CENTER = true;
    
    /** Indicates whether bounding boxes should be drawn around shapes. */
    public static final boolean DRAW_BOUNDING_BOX = false;
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The Reverie simulation space used visualization. **/
    private Space space;
    
    
    /** An input manager that manages all mouse and keyboard input to the visualization. */
    private InputManager inputManager;
    
    
    /** A delegate object for updating the visualization. */
    private GeoVisDelegate delegate;
    
    
    /** A collection of 2D shapes currently in the visualization. */
    private LinkedBlockingDeque<Shape2D> shapes2D;
    
    /** A collection of 3D shapes currently in the visualization. */
    private LinkedBlockingDeque<Shape3D> shapes3D;
    
    /** A collection of labels currently in the visualization. */
    private LinkedBlockingDeque<Label> labels;
    
    
    /** An earth model. */
    private Earth earth;
    
    /** A star field model. */
    private StarField starField;
    
    /** A boolean value indicating whether the earth model should be drawn. */
    private boolean drawEarth;
    
    /** A boolean value indicating whether the star field should be drawn. */
    private boolean drawStars;
    
    
    /** A boolean value indicating whether center points should be drawn on shapes. */
    private boolean drawCenterPoints;
    
    /** A boolean value indicating whether bounding boxes should be drawn around shapes. */
    private boolean drawBoundingBoxes;


    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates an <code>GeoVis</code> object.
     */
    public GeoVis() {
        
        // initialize shape and label collections
        this.shapes2D = new LinkedBlockingDeque<Shape2D>();
        this.shapes3D = new LinkedBlockingDeque<Shape3D>();
        this.labels = new LinkedBlockingDeque<Label>();
        
        
        // initialize earth/star instances and draw settings
        this.earth = new Earth();
        this.starField = new StarField();
        this.drawEarth = DRAW_EARTH;
        this.drawStars = DRAW_STARS;
        
        
        // initialize center point and bounding box drawing options
        this.drawCenterPoints = DRAW_CENTER;
        this.drawBoundingBoxes = DRAW_BOUNDING_BOX;
        
        
        // initialize Reverie space
        this.space = new Space();
        space.setDelegate(this);
        space.start();
        
        // initialize input manager
        inputManager = new InputManager(this);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the Reverie space used to display the visualization.
     * @return  The Reverie space used to display the visualization.
     */
    public Space getSpace() {
        return this.space;
    }
    
    /**
     * Returns the visualization's delegate object.
     * @return  The visualization's delegate object.
     */
    public GeoVisDelegate getDelegate() {
        return this.delegate;
    }
    
    /**
     * Returns a collection of 2D shapes currently in the visualization.
     * @return  A collection of 2D shapes currently in the visualization.
     */
    public Collection<Shape2D> get2DShapes() {
        return this.shapes2D;
    }
    
    /**
     * Returns a collection of 3D shapes currently in the visualization.
     * @return  A collection of 3D shapes currently in the visualization.
     */
    public Collection<Shape3D> get3DShapes() {
        return this.shapes3D;
    }
    
    /**
     * Returns a collection of labels currently in the visualization.
     * @return  A collection of labels currently in the visualization.
     */
    public Collection<Label> getLabels() {
        return this.labels;
    }
    
    /**
     * Returns whether the visualization will include the earth model.
     * @return  Whether the visualization will include the earth model.
     */
    public boolean willDrawEarth() {
        return drawEarth;
    }
    
    /**
     * Returns whether the visualization will include the star field.
     * @return  Whether the visualization will include the star field.
     */
    public boolean willDrawStars() {
        return drawStars;
    }
    
    /**
     * Returns whether center points will be drawn on shapes.
     * @return Whether center points will be drawn on shapes.
     */
    public boolean willDrawCenterPoints() {
        return this.drawCenterPoints;
    }
    
    /**
     * Returns whether bounding boxes will be drawn around shapes.
     * @return Whether bounding boxes will be drawn around shapes.
     */
    public boolean willDrawBoundingBoxes() {
        return this.drawBoundingBoxes;
    }
    
    
    /*********************************************
	 * MARK: Mutators
	 *********************************************/
    
    /**
     * Sets the visualization's delegate to a given object.
     * @param delegate  The visualization's new delegate object.
     */
    public void setDelegate(GeoVisDelegate delegate) {
        this.delegate = delegate;
    }
        
    /**
     * Sets whether the earth model should be drawn in the visualization.
     * @param draw  A <code>boolean</code> value indicating whether the earth model
     *              should be drawn in the visualization.
     */
    public void setDrawEarth(boolean draw) {
        this.drawEarth = draw;
    }
    
    /**
     * Sets whether the star field should be drawn in the visualization.
     * @param draw  A <code>boolean</code> value indicating whether the star field
     *              should be drawn in the visualization.
     */
    public void setDrawStars(boolean draw) {
        this.drawStars = draw;
    }
    
    /**
     * Toggles whether the earth model is drawn in the visualization.
     */
    public void toggleEarthModel() {
        this.drawEarth = !this.drawEarth;
    }
    
    /**
     * Toggles whether the star field model is drawn in the visualization.
     */
    public void toggleStarField() {
        this.drawStars = !this.drawStars;
    }
    
    /**
     * Toggles whether center points are drawn on shapes.
     */
    public void toggleCenterPoints() {
        this.drawCenterPoints = !this.drawCenterPoints;
    }
    
    /**
     * Toggles whether bounding boxes are drawn around shapes.
     */
    public void toggleBoundingBoxes() {
        this.drawBoundingBoxes = !this.drawBoundingBoxes;
    }
    
    
    /*********************************************
	 * MARK: Add/Remove Elements
	 *********************************************/
    
    /**
     * Adds a given 2D shape to the visualization.
     * @param shape The 2D shape to be added to the visualization.
     */
    public void add(Shape2D shape) {
        if (!shapes2D.contains(shape))
            shapes2D.add(shape);
    }
    
    /**
     * Adds a given 3D shape to the visualization space.
     * @param shape The 3D shape to be added to the visualization space.
     */
    public void add(Shape3D shape) {
        if (!shapes3D.contains(shape))
            shapes3D.add(shape);
    }
    
    /**
     * Adds a given label to the visualization space.
     * @param label The label to be added to the visualization space.
     */
    public void add(Label label) {
        if (!labels.contains(label))
            labels.add(label);
    }
    
    /**
     * Removes a given 2D shape from the visualization space.
     * @param shape The 2D shape to be removed from the visualization space.
     */
    public void remove(Shape2D shape) {
        shapes2D.remove(shape);
    }
    
    /**
     * Removes a given 3D shape from the visualization space.
     * @param shape The 3D shape to be removed from the visualization space.
     */
    public void remove(Shape3D shape) {
        shapes3D.remove(shape);
    }
    
    /**
     * Removes a given label from the visualization space.
     * @param label The label to be removed from the visualization space.
     */
    public void remove(Label label) {
        labels.remove(label);
    }
    
    
    /*********************************************
	 * MARK: Space Delegate
	 *********************************************/
    
    /**
     * Called by a delegating space immediately after initialization. Any initializations
     * needed should be performed here.
     * @param space     The space from which the call was generated.
     * @param drawable  The drawable used to render the space.
     */
    public void init(Space space, GLAutoDrawable drawable) {
        // retreive OpenGL rendering pipeline from drawable object
        GL gl = drawable.getGL();
        
        // build earth and star field models
        earth.init(space, drawable);
        starField.init(space, drawable);
        
        // set point size and line width
        gl.glPointSize(POINT_SIZE);
        gl.glLineWidth(LINE_WIDTH);
        
    }
    
    /**
     * Called by a delegating space before drawing every frame.
     * @param space The space from which the call was generated.
     */
    public void update(Space space) {
        // process input
        if (inputManager != null)
            inputManager.processKeyInput();
        
        // call delegate update method
        if (this.delegate != null)
            delegate.update(this);
    }
    
    /**
     * Called by a delegating space after updating. All drawing logic should be
     * contained here.
     * @param space     The space from which the call was generated.
     * @param drawable  The drawable used to render the space.
     */
    public void display(Space space, GLAutoDrawable drawable) {
        // retreive OpenGL rendering pipeline from drawable object
        GL gl = drawable.getGL();
        
        
        space.beginLightIndependentDrawing(gl);
        {
            // draw starfield
            if (drawStars)
                starField.draw(STAR_FIELD_RADIUS);
            
            // draw bounding boxes?
            if (drawBoundingBoxes) {
                
                // 2D
                for (Shape2D shape : shapes2D)
                    if (drawBoundingBoxes && !(shape instanceof Point2D))
                        draw(gl, shape.boundingBox());
                
                // 3D
                for (Shape3D shape : shapes3D)
                    if (!(shape instanceof Point3D))
                        draw(gl, shape.boundingBox());
            }
        }
        space.endLightIndependentDrawing(gl);        
        
        
        // draw earth
        if (drawEarth)
            earth.draw(1000/KM_PER_UNIT);
        
        // draw labels
        for (Label label : labels)
            draw(gl, label);
        
        // draw 2D shapes
        for (Shape2D shape : shapes2D) {
            draw(gl, shape);
            
            // draw center point?
            if (drawCenterPoints && !(shape instanceof Point2D)) {
                gl.glColor3d(CENTER_POINT_COLOR[0], 
                             CENTER_POINT_COLOR[1], 
                             CENTER_POINT_COLOR[2]);
                draw(gl, shape.getPosition());
            }
        }
        
        // draw 3D shapes
        for (Shape3D shape : shapes3D) {
            draw(gl, shape);
            
            // draw center point?
            if (drawCenterPoints && !(shape instanceof Point3D)) {
                gl.glColor3d(CENTER_POINT_COLOR[0], 
                             CENTER_POINT_COLOR[1], 
                             CENTER_POINT_COLOR[2]);
                draw(gl, shape.getPosition());
            }
        }
    }

}
