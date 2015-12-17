package org.rowan.reverie;

import org.rowan.linalgtoolkit.*;
import org.rowan.linalgtoolkit.transform3d.*;
import javax.media.opengl.*;

/**
 * The <code>Model</code> class is an abstract class that provides a basic framework 
 * for JOGL models. All <code>Model</code> objects must be initialized before being
 * drawn. This is done by calling the model's <code>init()</code> method, which
 * takes a Reverie simulation space and a <code>GLAutoDrawable</code> to be used
 * as the OpenGL rendering target. Attempting to draw a <code>Model</code> that 
 * has not yet been initialized will result in a thrown exception.
 * <p>
 * <code>Model</code>s can not be shared between OpenGL contexts. Attempting to
 * draw a model to a target other than that used for initialization will result
 * in missing textures and other undefined behavior.
 * <p>
 * In order to keep all context specific data valid, models are typically initialized 
 * in in a <code>SpaceDelegate</code>'s <code>init()</code> method. This ensures 
 * that the model's are initialized and re-initialized in sync with <code>Space</code>'s
 * associated OpenGL context.
 * <p>
 * After a model has been initialized, it can be drawn as needed with a call to 
 * the <code>draw()</code> method.
 * <p>
 * </code>Model</code>s extend from <code>Object3D</code> and thus inherit
 * position and orientation, relative to the model's untransformed definition. 
 * Appropriate transformations are applied upon drawing, in order to place the 
 * model at its current position and orientation in the world. For this reason, 
 * all model meshes should be defined at the origin, thus ensuring that the 
 * intrinsic position property in fact represents the position at which 
 * the model will be seen. 
 * 
 * <code>Model</code>s also maintain a position offset and orientation offset
 * that is independent from its actual position and orientation. This is used
 * to realign a model in the event that a model's definition does not properly
 * position/orient it at the origin. The position and orientation offsets are 
 * applied to the model prior drawing, before the position and orientation 
 * transformations are made. For example, imagine that we have a cube model that 
 * was defined with one corner at (0, 0, 0) and the oposite corner at (1, 1, 1).
 * If we wanted to draw the cube at (5, 5, 5), we would actually have to set
 * its position to (4.5, 4.5, 4.5) in order for the center of the cube to be
 * where we want it. Rather than remembering this offset, we can simply set
 * the cubes position offset to (-0.5, -0.5, -0.5) and never worry about it again.
 * After this, the center of the cube will always be located directly on its 
 * current defined position.
 * 
 * @author Spence DiNicolantonio
 * @version 1.2
 * @since 1.2
 */
public abstract class Model extends Object3D {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** A boolean value indicating whether the model has been initialized. */
    private boolean initialized;
    
    /** A boolean value indicating whether the model's resources have been loaded. */
    private boolean resourcesLoaded;
    
    
    /** The Reverie space associated with the model */
    private Space space;
    
    /** The OpenGL rendering target associated with the model. */
    private GLAutoDrawable drawable;
    
    
    /** The relative position offset to apply to the model when drawing. */
    private Vector3D positionOffset;
    
    /** The relative orientation offset to apply to the model when drawing. */
    private Rotation orientationOffset;
        
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a model.
     */
    public Model() {
        // initialize fields
        this.initialized = false;
        this.resourcesLoaded = false;
        this.drawable = null;
        this.space = null;
        this.positionOffset = Vector3D.ZERO_VECTOR;
        this.orientationOffset = Rotation.IDENTITY;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Determines whether the model has been initialized.
     * @return  <code>true</code> if the model has been initialized; <code>false</code> 
     *          otherwise.
     */
    public final boolean isInitizlized() {
        return this.initialized;
    }
    
    /**
     * Determines whether all of the model's resources have been loaded.
     * @return  <code>true</code> if the model has loaded its resources; <code>false</code> 
     *          otherwise.
     */
    public final boolean isLoaded() {
        return this.resourcesLoaded;
    }
    
    /**
     * Returns the model's relative position offset. 
     * @return  The position offset applied to the model when drawing.
     */
    public Vector3D getPositionOffset() {
        return this.positionOffset;
    }
    
    /**
     * Returns the model's relative orientation offset. 
     * @return  The orientation offset applied to the model when drawing.
     */
    public Rotation getOrientationOffset() {
        return this.orientationOffset;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the model's relative position offset. 
     * @param positionOffset    The position offset applied to the model when 
     *                          drawing.
     */
    public void setPositionOffset(Vector3D positionOffset) {
        this.positionOffset = positionOffset;
    }
    
    /**
     * Sets the model's relative orientation offset. 
     * @param orientationOffset The position offset applied to the model when 
     *                          drawing.
     */
    public void setOrientationOffset(Rotation orientationOffset) {
        this.orientationOffset= orientationOffset;
    }
    
    
    /*********************************************
     * MARK: Initialize
     *********************************************/
    
    /**
     * Initializes the model for use with a given simulation space and 
     * <code>GLAutoDrawable</code>. If the model has already been initialized it 
     * will be disposed of and re-initialized. This method will trigger a call to 
     * the model's <code>initModel()</code> method. It will also trigger a call 
     * to the model's <code>loadResources()</code> method if lazy-loading is 
     * disabled in the given simulation space.
     * @param space     The simulation space for which the model will be initialized.
     * @param drawable  The OpenGL rendering target for which the model will be
     *                  initialized.
     */
    public final void init(Space space, GLAutoDrawable drawable) {
        // already initialized?
        if (initialized)
            dispose();
        
        // store simulation space and rendering target
        this.space = space;
        this.drawable = drawable;
        
        // initialize model
        initModel(space, drawable);
        
        // load resources?
        if (!space.getSettings().lazyLoadingEnabled() && !resourcesLoaded) {
            loadAllResources(space, drawable);
            this.resourcesLoaded = true;
        }
        
        // flag model as initialized and disposable
        this.initialized = true;
    }
    
    /**
     * Forces the model to load its resources. This will override the lazy-load 
     * setting. If the model has already been loaded, this method will have no 
     * effect.
     * <p>
     * Make sure that this method is called when the model's associated OpenGL 
     * context is the current context, otherwise context-specific resources
     * may be loaded incorrectly.
     * @throws RuntimeException if the model has not been initialized.
     */
    public final void forceLoad() {
        // not initialized?
        if (!initialized)
            throw new RuntimeException("Model not initialized");
        
        // load resources?
        if (!resourcesLoaded) {
            loadAllResources(this.space, this.drawable);
            this.resourcesLoaded = true;
        }
    }
    
    /**
     * Flags the model to reload its resources.
     */
    public final void reload() {
        this.resourcesLoaded = false;
    }
    
    
    /*********************************************
     * MARK: Dispose
     *********************************************/
    
    /**
     * Disposes of the model and all of its created resources. After disposal, a 
     * model will require initialization prior to drawing.
     */
    public final void dispose() {
        // do nothing if not initialized
        if (!initialized)
            return;
        
        // dispose of model resources
        disposeOfModel(this.space, this.drawable);
                
        // remove simulation space and drawable references
        this.space = null;
        this.drawable = null;
        
        // flag for initialization
        this.initialized = false;
    }
    
    
    /*********************************************
     * MARK: Draw
     *********************************************/
    
    /**
     * Draws the model.
     * @throws RuntimeException if the model has not been initialized.
     */
    public final void draw() {
        draw(1.0);
    }
    
    /**
     * Draws the model at a given scale.
     * @param scale The scale at which the model will be drawn
     * @throws RuntimeException if the model has not been initialized.
     */
    public final void draw(double scale) {
        // not initialized?
        if (!initialized)
            throw new RuntimeException("Model not initialized");
        
        // load all resources or just those needed for drawing?
        if (!space.getSettings().lazyLoadingEnabled() && !resourcesLoaded) {
            loadAllResources(space, this.drawable);
            this.resourcesLoaded = true;
        } else {
            loadResourcesForDrawing(this.space, this.drawable);
        }
        
        // get OpenGL rendering pipeline from drawable
        GL gl = this.drawable.getGL();
        
        
        // get transformation info
        Vector3D position = positionOffset.add(getPosition());
        Rotation orientation = orientationOffset.append(getOrientation());
        double angle = orientation.getAngle();
        Vector3D axis = orientation.getAxis();
        
        // draw
        gl.glPushMatrix();
		{
            gl.glTranslated(position.getX(), 
                            position.getY(), 
                            position.getZ());
            gl.glRotated(Math.toDegrees(angle), 
                         axis.getX(), 
                         axis.getY(), 
                         axis.getZ());
			gl.glScaled(scale, 
                        scale, 
                        scale);
			drawModel(this.space, this.drawable);
		}
		gl.glPopMatrix();
    }
    
    
    /*********************************************
     * MARK: Abstract
     *********************************************/
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * This method will be called once, when the model is initialized. 
     * <p>
     * <code>Model</code> subclasses should implement this method to perform any 
     * required initializations procedures.
     * @param space     The simulation space for which the model will be initialized.
     * @param drawable  The OpenGL rendering target for which the model will be
     *                  initialized.
     */
    protected abstract void initModel(Space space, GLAutoDrawable drawable);
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * When lazy-loading is disabled, this method will be called immediately after 
     * the model is initialized. 
     * <p>
     * <code>Model</code> subclasses should implement this method to load all 
     * resources required by the model.
     * <p>
     * All needed resources should be loaded using the singleton resource manager, 
     * accessable via a call to <code>ResourceManager.getInstance()</code>.
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected abstract void loadAllResources(Space space, GLAutoDrawable drawable);
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * This method will be called immediately before the model is drawn whether 
     * or not lazy-loading is enabled. 
     * <p>
     * <code>Model</code> subclasses should implement this method to load only 
     * the resources needed to draw the model at the instant the method is called. 
     * <p>
     * All needed resources should be loaded using the singleton resource manager, 
     * accessable via a call to <code>ResourceManager.getInstance()</code>.
     * <p>
     * Using the resource manager to load model resources should ensure that no 
     * resource is reloaded unnecessarily when lazy-loading is disabled. However, 
     * one may choose to short-circuit this method after confirming that the model
     * has been fully loaded. This can be confirmed with a call to <code>isLoaded()</code>.
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected abstract void loadResourcesForDrawing(Space space, GLAutoDrawable drawable);
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * This method will be called whenever the model requires disposal. 
     * <p>
     * <code>Model</code> subclasses should implement this method to perform any 
     * required disposal procedures. Any display lists or textures used strictly 
     * by this model alone should be deleted in this method.
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected abstract void disposeOfModel(Space space, GLAutoDrawable drawable);
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * This method will be called when the model needs to be drawn using a given
     * <code>GLAutoDrawable</code>.
     * <p>
     * <code>Model</code> subclasses should implement this method to draw themselves
     * using the given <code>GLAutoDrawable</code>.
     * @param space     The simulation space for which the model will be drawn.
     * @param drawable  The OpenGL rendering target onto which the model will be
     *                  drawn.
     */
    protected abstract void drawModel(Space space, GLAutoDrawable drawable);
    
}
