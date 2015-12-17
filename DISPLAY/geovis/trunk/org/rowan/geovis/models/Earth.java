package org.rowan.geovis.models;

import org.rowan.reverie.*;
import org.rowan.geovis.*;
import org.rowan.linalgtoolkit.WGS84Coord;

import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.texture.*;

/**
 * The <code>Earth</code> class defines an static earth model for use in GeoVis 
 * visualizations.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public final class Earth extends StaticModel {
    
    /*********************************************
	 * MARK: Constants
	 *********************************************/
    
    /** The earth texture file path. */
    private static final String TEXTURE_PATH = "org/rowan/geovis/images/textures/earth.jpg";
    
    /** The number of slices used to draw the earth model. */
	public static final int EARTH_SLICES = 100;
    
    
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
        
        // load earth texture
        Texture earthTex = ResourceManager.getInstance().loadTexture(TEXTURE_PATH);
        
        // texture IO failed?
        if (earthTex == null)
            return null;
        
        // set texture settings
        earthTex.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST); 
        earthTex.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        
        // add earth texture to list and return
        textures.add(earthTex);
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
        Texture earthTex = textures.get(0);
                
        // create quadric
        GLUquadric quadric = glu.gluNewQuadric();
        glu.gluQuadricTexture(quadric, true);
		
        // set color to white
        gl.glColor4d(1, 1, 1, 1);
        
        // enable textures and bind the earth texture 
        gl.glEnable(GL.GL_TEXTURE_2D); 
        earthTex.bind();
        
        // scale earth to WGS-84 standard
        double xScale = WGS84Coord.EARTH_EQUATORIAL_RADIUS/GeoVis.KM_PER_UNIT;
        double yScale = WGS84Coord.EARTH_POLAR_RADIUS/GeoVis.KM_PER_UNIT;
        double zScale = xScale;
        gl.glScaled(xScale, yScale, zScale);
        
        // rotate to proper orientation
        gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
        
        // draw texture mapped sphere
        glu.gluSphere(quadric, 1.0f, EARTH_SLICES, EARTH_SLICES);
        
        // disable textures
        gl.glDisable(GL.GL_TEXTURE_2D);
    }
}
