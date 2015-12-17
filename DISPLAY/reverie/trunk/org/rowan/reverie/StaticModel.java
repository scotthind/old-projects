package org.rowan.reverie;

import static org.rowan.reverie.Settings.*;

import java.util.*;
import javax.media.opengl.*;
import com.sun.opengl.util.texture.*;

/**
 * The <code>StaticModel</code> class is an abstract class that provides a basic 
 * framework for defining static JOGL models. <code>StaticModel</code> subclasses 
 * must implement the <code>loadTextures()</code> and <code>defineModel()</code> 
 * methods.
 * <p>
 * The <code>loadTextures()</code> method is called once, when the model is 
 * initilized. In this method, all required textures should be loaded and returned 
 * in a list. These textures will later be passed to the <code>defineModel()</code> 
 * method for drawing purposes.
 * <p>
 * The <code>defineModel()</code> method should contain the appropriate JOGL
 * code to define and draw the model. A list of textures, previously defined in 
 * the <code>loadTextures()</code> method, is passed to <code>defineModel()</code>.
 * The contents of the <code>defineModel()</code> method will be encapsulated in
 * a display list for efficient drawing. In the event that a static model's display list 
 * needs to be rebuilt, calling the <code>rebuild()</code> method will signal the
 * model to rebuild its dispaly list before the next drawing.
 * 
 * @author Spence DiNicolantonio
 * @version 1.2
 * @since 1.2
 */
public abstract class StaticModel extends Model{
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** A boolean value indicating whether the model's textures have been loaded. */
    private boolean texturesLoaded;
    
    /** A boolean value indicating whether the model's display list needs to be built. */
    private boolean built;
    
    /** A reference to the model's display list. */
    private int displayList;
    
    /** A list of textures used to draw the model. */
    private List<Texture> textures;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a static model.
     */
    public StaticModel() {
        // require texture load
        this.texturesLoaded = false;
        
        // require build
        this.built = false;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Determines whether the model's display list has been built.
     * @return  <code>true</code> if the given model's display list has been 
     *          constructed; <code>false</code> otherwise.
     */
    public final boolean isBuilt() {
        return this.built;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Flags the model to have its display list rebuilt.
     */
    public final void rebuild() {
        this.built = false;
    }
    
    
    /*********************************************
     * MARK: Model (Superclass)
     *********************************************/
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * Allocates a display list reference. The display list will be built when 
     * resources are loaded.
     * @param space     The simulation space for which the model will be initialized.
     * @param drawable  The OpenGL rendering target for which the model will be
     *                  initialized.
     */
    protected final void initModel(Space space, GLAutoDrawable drawable) {
        // get OpenGL rendering pipeline from drawable
        GL gl = drawable.getGL();
        
        // generate display list reference
        this.displayList = gl.glGenLists(1);
    }
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * Loads all texture resources needed by the model and builds the model's 
     * display list.
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected final void loadAllResources(Space space, GLAutoDrawable drawable) {
        // load textures
        this.textures = loadTextures();
        
        // build display list
        build(space, drawable);
    }
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * Loads all texture resources needed by the model and builds the model's 
     * display list.
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected final void loadResourcesForDrawing(Space space, GLAutoDrawable drawable) {
        loadAllResources(space, drawable);
    }
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * Deletes the model's display list. Texture references associated with the 
     * model will be dropped, but textures will not be disposed of, as they may 
     * still be needed by other models.
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected final void disposeOfModel(Space space, GLAutoDrawable drawable) {
        // get OpenGL rendering pipeline from drawable
        GL gl = drawable.getGL();
        
        // delete display list
        gl.glDeleteLists(displayList, 1);
        
        // remove reference to texture list
        this.textures = null;
        
        // flag for rebuild
        this.built = false;
        
        // flag for texture load
        this.texturesLoaded = false;
    }
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * Draws the model's display list using a given <code>GLAutoDrawable</code>.
     * @param space     The simulation space for which the model will be drawn.
     * @param drawable  The OpenGL rendering target on which the model will be 
     *                  drawn.
     */
    public final void drawModel(Space space, GLAutoDrawable drawable) {
        // get OpenGL rendering pipeline from drawable
        GL gl = drawable.getGL();
        
        // need build?
        if (!built)
            build(space, drawable);
        
        // draw
        gl.glPushMatrix();
		{
			gl.glCallList(displayList);
		}
		gl.glPopMatrix();
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Builds the model's display list using a given OpenGL rendering pipline. 
     * If the display list has already been built, this method has no effect.
     * @param space The simulation space for which the model will be built.
     * @param drawable  The OpenGL rendering target for which the model will be 
     *                  built.
     */
    private final void build(Space space, GLAutoDrawable drawable) {
        // get OpenGL rendering pipeline from drawable
        GL gl = drawable.getGL();
        
        // already built?
        if (this.built)
            return;
        
        // delete display list (just in case)
        gl.glDeleteLists(displayList, 1);
        
        // start display list definition
        gl.glNewList(displayList, GL.GL_COMPILE);
        {
            // push matrix onto stack
            gl.glPushMatrix();
            {
                // define model
                if (textures == null)
                    defineModel(space, drawable, null);
                else
                    defineModel(space, drawable, Collections.unmodifiableList(textures));
            }
            gl.glPopMatrix();
        }
        gl.glEndList();
        
        // flag model as built
        this.built = true;
    }
    
    
    /*********************************************
     * MARK: Abstract
     *********************************************/
    
    /**
     * A template method used by the <code>StaticModel</code> class hierarchy 
     * that should not be called by end-users.
     * <p>
     * When lazy loading is disabled, this method will be called immediately after 
     * the model is initialized; otherwise, the first time the model is drawn. 
     * <p>
     * <code>StaticModel</code> subclasses should implement this method to load
     * all required texture resources and return them in a list.
     * <p>
     * All texture resources should be loaded using the singleton resource manager, 
     * accessable via a call to <code>ResourceManager.getInstance()</code>.
     * @return  A list of loaded textures; null if no textures are needed by 
     *          the model.
     */
    protected abstract List<Texture> loadTextures();
    
    /**
     * A template method used by the <code>StaticModel</code> class hierarchy 
     * that should not be called by end-users.
     * <p>
     * The contents of this method will be compiled in the model's display list
     * when it is built.
     * <p>
     * <code>StaticModel</code> subclasses should implement this method to draw
     * themselves using the given <code>GLAutoDrawable</code> and texture list.
     * @param space     The simulation space for which the model will be drawn.
     * @param drawable  The OpenGL rendering target on which the model will be 
     *                  drawn.
     * @param textures  An ordered list of pre-loaded textures available for use
     *                  in drawing the model.
     */
    protected abstract void defineModel(Space space, GLAutoDrawable drawable, List<Texture> textures);
    
}
