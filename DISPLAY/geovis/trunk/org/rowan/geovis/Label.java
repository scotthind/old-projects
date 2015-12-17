package org.rowan.geovis;

import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.WGS84Coord;
import org.rowan.coordconversion.*;

/**
 * The <code>Label</code> class defines a positionable flag that can be used within
 * the GeoVis system to label various points on the earth model.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public class Label {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The position of the label. */
    private Vector3D position;
    
    /** The label's text. */
    private String text; 


    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates a label at a given position with given text.
     * @param position  A <code>Vector3D</code> describing the label's position.
     * @param text      The label's text.
     */
    public Label(Vector3D position, String text) {
        this.position = position;
        this.text = text;
    }
    
    /**
     * Creates a label at a given WGS-84 coordinate with given text.
     * @param position  A <code>WGS84Coord</code> describing the label's position.
     * @param text      The label's text.
     */
    public Label(WGS84Coord position, String text) {
        this(new CoordinateConversion(GeoVis.KM_PER_UNIT).toEuclidean(position), text);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the label's position.
     * @return  The label's position.
     */
    public Vector3D getPosition() {
        return this.position;
    }
    
    /**
     * Returns the label's text.
     * @return  The label's text.
     */
    public String getText() {
        return this.text;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the label's position to a given vertex.
     * @param position  A <code>Vector3D</code> describing the label's new position.
     */
    public void setPosition(Vector3D position) {
        this.position = position;
    }
    
    /**
     * Sets the label's position to a given WGS-84 coordinate.
     * @param position  A <code>WGS84Coord</code> describing the label's new position.
     */
    public void setPosition(WGS84Coord position) {
        setPosition(new CoordinateConversion(GeoVis.KM_PER_UNIT).toEuclidean(position));
    }
    
    /**
     * Sets the label's text to a given string.
     * @param text  The label's new text.
     */
    public void setText(String text) {
        this.text = text;
    }

}
