
package org.rowan.reverie.md2;

import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.reverie.md2.util.AnimationException;

/**
 * The <code>KeyFrame</code> class represents a kry frame of an MD2 model animation. 
 * A  <code>KeyFrame</code> consist a list of a vertices and surface normals 
 * corresponding to the faces of an associted MD2 model at a given point in 
 * animation. A key frame also contains a name.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class KeyFrame {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The key frame's name. **/
    private String name;
    
    /** An ordered array of vertices used to draw the key frame. **/
    private Vector3D[] vertices;
    
    /** An ordered array of surface normals used to draw the key frame. **/
    private Vector3D[] normals;


    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates a <code>KeyFrame</code> with given properties. This constructor
     * is used by the <code>MD2Loader</code> class and is not inteaded for
     * end-users.
     * @param name      The key frame's name.
     * @param vertices  An ordered array of vertices used to draw the key frame.
     * @param normals   An ordered array of surface normals used to draw the key 
     *                  frame.
     */
    public KeyFrame(String name, Vector3D[] vertices, Vector3D[] normals) {
        this.name = name;
        this.vertices = vertices;
        this.normals = normals;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the key frame's name.
     * @return  The key frame's name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns the key frame's vertices.
     * @return  An ordered array of vertices used to draw the key frame.
     */
    public Vector3D[] getVertices() {
        return this.vertices;
    }
    
    /**
     * Returns the key frame's surface normals.
     * @return  An ordered array of surface normals used to draw the key frame.
     */
    public Vector3D[] getNormals() {
        return this.normals;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the the name of the frame.
     * @param name  The frame's name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /*********************************************
     * MARK: Interpolation
     *********************************************/
    
    /**
     * Linearly interpolates between this key frame and a given key frame. The 
     * given destination frame must be compatible with this frame in order for
     * interpolation to be performed correctly; that is, the destination frame
     * must have the same number of vertices and surface normals as this frame.
     * @param destFrame The destination key frame to which interpolation will
     *                  be performed.
     * @param factor    The degree of interpolation to be applied. The given 
     *                  value should be in the range [0.0, 1.0] and will be 
     *                  clamped as needed.
     * @return          The resulting interpolated frame.
     * @throws AnimationException if <code>destFrame</code> doesn't have the same
     *                  number of vertices and surface normals as this frame.
     */
    public KeyFrame lerp(KeyFrame destFrame, double factor) {
        // validate destination key frame
        if (destFrame.getVertices().length != this.vertices.length ||
            destFrame.getNormals().length != this.normals.length)
            throw new AnimationException("Attempted to interpolate incopatible key frames");
        
        // clamp interpolation factor
        factor = (factor < 0.0)? 0.0 : factor;
        factor = (factor > 1.0)? 1.0 : factor;
        
        // construct name
        String newName = this.name + "-" + destFrame.getName() + "-" + factor;
        
        // compute new vertices
        Vector3D[] newVerts = new Vector3D[vertices.length];
        for (int i=0; i<vertices.length; i++) {
            Vector3D v1 = this.vertices[i];
            Vector3D v2 = destFrame.getVertices()[i];
            newVerts[i] = v1.lerp(v2, factor);
        }
        
        // compute new normals
        Vector3D[] newNormals = new Vector3D[normals.length];
        for (int i=0; i<normals.length; i++) {
            Vector3D n1 = this.normals[i];
            Vector3D n2 = destFrame.getNormals()[i];
            newNormals[i] = n1.lerp(n2, factor);
        }
        
        // construct and return interpolated key frame
        return new KeyFrame(newName, newVerts, newNormals);
    }
}
