
package org.rowan.reverie.md2.util;

/**
 * The <code>AnimationException</code> class defines an runtime exception that
 * can be caused by creating invalid animation sequences or trying to apply 
 * animations to a model that cannot handle it.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class AnimationException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * @param message   The detail message.
     */
    public AnimationException(String message) {
        super(message);
    }
}
