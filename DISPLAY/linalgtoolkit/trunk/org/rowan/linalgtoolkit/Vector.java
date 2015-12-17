package org.rowan.linalgtoolkit;

/**
 * The <code>Vector</code> class is an abstract class providing limited implementation 
 * and guidelines for 2D and 3D vectors. Vector component values are maintained
 * a precision defined by <code>PRECISION</code>. The value of <code>PRECISION</code>
 * describes the number of decimal places at which to round component values.
 * Component values are rounded by the constructor to a precision less than that
 * of the <code>double</code> type to avoid comparison discrepancies caused
 * by floating point computation errors.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.0
 */
public abstract class Vector implements Cloneable {
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/ 
    
    /** The number of decimal places to consider when comparing two vectors. */
    public static final int PRECISION = 10;
    
    
    /*********************************************
     * MARK: Clonable
     *********************************************/
    
    /**
     * Creates a clone of this vector.
     * @return  A deep copy of this vector.
     */
    @Override
    public abstract Vector clone();
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Calculates the magnitude of this vector.
     * @return  The magnitude of this vector.
     */
    public abstract double magnitude();
    
    /**
     * Determines whether this vector is a zero vector. A zero vector is a 
     * directionless vector with 0 magnitude (all component values equal 0).
     * @return  <code>true</code> if this vector is a zero vector; <code>false</code>
     *          otherwise.
     */
    public abstract boolean isZeroVector();
    
    /**
     * Determines whether this vector is a unit vector. A unit vector is a vector
     * with magnitude of 1.0.
     * @return  <code>true</code> if this vector is a unit vector; <code>false</code>
     *          otherwise.
     */
    public abstract boolean isUnitVector();
    
    /**
     * Returns an array containing the component values of this vector.
     * @return  An array of doubles representing this vector.
     */
    public abstract double[] toArray();
    
    
    /*********************************************
     * MARK: toString
     *********************************************/
    
    /**
     * Creates a string to describe this vector.
     * @return  A string that describes this vector.
     */
    @Override
    public abstract String toString();
        
    
    /*********************************************
     * MARK: Other
     *********************************************/
    
    /**
     * Rounds a given double value to the defined precision for vector components;
     * the value of <code>PRECISION</code>.
     * @param value The value to be rounded.
     * @return      The given value, rounded to the accepted precision for vector
     *              components.
     */
    protected double precisionRound(double value) {
        double conversionFactor = Math.pow(10, PRECISION);
        long roundedLong = Math.round(value * conversionFactor);
        return ((double)roundedLong) / conversionFactor;
    }
    
}

