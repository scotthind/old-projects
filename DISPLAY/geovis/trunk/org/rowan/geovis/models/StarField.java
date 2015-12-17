package org.rowan.geovis.models;

import org.rowan.reverie.*;

import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.texture.*;

/**
 * The <code>StarField</code> class defines a static star field model for use in 
 * GeoVis visualizations.
 *
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public final class StarField extends StaticModel {
    
    /*********************************************
	 * MARK: Constants
	 *********************************************/
    
    /** The star field texture file path. */
    private static final String TEXTURE_PATH = "org/rowan/geovis/images/textures/stars.jpg";
    
    /** The number of slices used to draw the star field model. */
	public static final int STAR_FIELD_SLICES = 5;
    
    
    /*********************************************
     * MARK: Model Definition
     *********************************************/
    
    /**
     * Prepares textures needed to draw the model. This method will be called 
     * immediately after the model is initialized if lazy-loading is disabled; 
     * otherwise, the first time it is drawn. All textures needed by the model 
     * should be loaded in this method using the.
     * <p>
     * All needed texture resources should be loaded using the singleton resource 
     * manager, accessable via a call to <code>ResourceManager.getInstance()</code>.
     * @return  A list of loaded textures; null if no textures are needed by 
     *          the model.
     */
    protected List<Texture> loadTextures() {
        LinkedList<Texture> textures = new LinkedList<Texture>();
        
        // load star field texture
        Texture starTex = ResourceManager.getInstance().loadTexture(TEXTURE_PATH);
        
        // texture IO failed?
        if (starTex == null)
            return null;
        
        // set texture settings
        starTex.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST); 
        starTex.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        
        // add earth texture to list and return
        textures.add(starTex);
        return textures;
    }
    
    /**
     * Defines how to draw the model using a given <code>GLAutoDrawable</code>. 
     * The contents of this method will be compiled in the model's display list.
     * @param space     The Reverie space for which the model will be defined.
     * @param drawable  The OpenGL rendering target for which the model will be
     *                  defined.
     * @param textures  An array of pre-loaded textures available for use by the
     *                  model.
     */
    protected void defineModel(Space space, GLAutoDrawable drawable, List<Texture> textures) {
        // get OpenGL rendering pipeline from drawable
        GL gl = drawable.getGL();
        
        // create a GLU utility object
        GLU glu = new GLU();
        
        
        // get earth texture from list
        Texture starTex = textures.get(0);
        
        // create quadric
        GLUquadric quadric = glu.gluNewQuadric();
        glu.gluQuadricTexture(quadric, true);
		
        // set color to white
        gl.glColor4d(1, 1, 1, 1);
        
        // enable textures and bind the star field texture 
        gl.glEnable(GL.GL_TEXTURE_2D); 
        starTex.bind();
                
        // draw texture mapped sphere
        glu.gluSphere(quadric, 1.0f, STAR_FIELD_SLICES, STAR_FIELD_SLICES);
        
        // disable textures
        gl.glDisable(GL.GL_TEXTURE_2D);
    }
    
}
