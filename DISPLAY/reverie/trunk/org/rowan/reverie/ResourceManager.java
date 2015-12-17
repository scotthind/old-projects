package org.rowan.reverie;

import java.util.*;
import java.io.*;
import javax.media.opengl.*;
import com.sun.opengl.util.texture.*;

/**
 * Loads and manages texture resources for a simulation space and its associated
 * OpenGL context. 
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class ResourceManager {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** Singleton instance. */
    private static final ResourceManager INSTANCE = new ResourceManager();
    
    /** A map of all loaded and cached resources. */
    private HashMap<String, Texture> resources;


    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Constructor.
     */
    private ResourceManager() {
        this.resources = new HashMap<String, Texture>();
    }
    
    /**
     * Returns the singleton resource manager.
     * @return  The singleton resource manager.
     */
    public static ResourceManager getInstance() {
        return INSTANCE;
    }
    
    
    /*********************************************
     * MARK: Loading
     *********************************************/

    /**
     * Loads a a JOGL texture from a specified image. If the specified texture has 
     * already been loaded, the cached texture will be returned. The loaded texture
     * will be applied to the current OpenGL context. To ensure that the appropriate
     * context is current, it is recommended that all texture loading be performed
     * from the <code>GLEventListener</code> methods.
     * @param filepath  The the file path of the image to load.
     * @return          A <code>Texture</code> object loaded from the specified
     *                  file, or <code>null</code> on IO failure.
     */
    public Texture loadTexture(String filepath) {
        // construct key
        String key = createKey(filepath);
        
        // texture cached?
        if (this.resources.containsKey(key))
            return this.resources.get(key);
        
        // create input stream for file
        InputStream inputStream;
        inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(filepath);
        try {
            // load, cache, and return texture
            Texture tex = TextureIO.newTexture(inputStream, true, null);
            this.resources.put(key, tex);
            return tex;
        } catch (IOException ioe) {
            System.err.println("Could not load texture image: " + filepath);
        }
        
        // return null on IO fail
        return null;
    }
    
    /**
     * Constructs a key for a given resource.
     * @param filepath  The filepath of the texture resource.
     */
    private String createKey(String filepath) {
        return GLContext.getCurrent().getGL().toString() + filepath;
    }
}
