package org.rowan.linalgtoolkit.logic2d;

/**
 * Thrown to indicate a failed intersection calculation on two shapes that do 
 * not intersect
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public class IntersectException extends IllegalArgumentException {
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/    
    
    /**
     * Constructs an <code>IntersectException</code> with no detail message.
     */
    public IntersectException() {
        super();
    }
    
    /**
     * Constructs an <code>IntersectException</code> with a given detail message.
     * @param message   The detail message.
     */
    public IntersectException(String message) {
        super(message);
    }
}

