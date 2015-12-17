
package org.rowan.linalgtoolkit;

import org.rowan.linalgtoolkit.transform3d.Rotation;

/**
 * The <code>Object3D</code> class defines an object with position and orientation
 * in 3D space.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public abstract class Object3D {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The current position of the object */
    private Vector3D position;
    
    /** The current orientation of the object relative to the default orientation. */
    private Rotation orientation;


    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Designated Constructor.
     */
    public Object3D() {
        this.position = Vector3D.ORIGIN;
        this.orientation = Rotation.IDENTITY;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the object's current position.
     * @return  the object's current position.
     */
    public Vector3D getPosition() {
        return this.position;
    }
    
    /**
     * Returns the object's current orientation.
     * @return    The object's current orientation.
     */
    public Rotation getOrientation() {
        return this.orientation;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/

    /**
     * Sets this object's position to a given point.
     * @param position  A <code>Vector3D</code> describing the position at which 
     *                  to set the object.
     * @throws IllegalArgumentException If <code>position</code> is <code>null</code>.
     */
    public void setPosition(Vector3D position) {
        // null given?
        if (position == null)
            throw new IllegalArgumentException("Cannot set null position.");
        
        // set position
        this.position = position;
    }
    
    /**
     * Sets this object's orientation to that described by a given <code>Rotation</code> 
     * object, relative to the object's default orientation.
     * @param orientation   A <code>Rotation</code> object defining the desired 
     *                      orientation, relative to the object's default orientation.
     * @throws IllegalArgumentException If <code>orientation</code> is <code>null</code>.
     */
    public void setOrientation(Rotation orientation) {
        // null given?
        if (orientation == null)
            throw new IllegalArgumentException("Cannot set null orientation.");
        
        // set orientation
        this.orientation = orientation;
    }
    
}
