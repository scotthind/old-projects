
package org.rowan.reverie.md2;

import org.rowan.reverie.md2.util.AnimationException;

/**
 * The <code>Animation</code> class defines a sequence of frame indexes that in
 * turn define an animation. An <code>Animation</code> object has a defined
 * framerate that stipulates animation speed. This framerate is mutable to allow
 * dynamic animation speeds. <code>Animation</code> objects also have an associated
 * "play style", which indicates whether the animation is looped and/or mirrored.
 * Looped animations will continuously play, restarting at the first frame after
 * the last frame has been reached. Mirrored animations will automatically play 
 * in reverse (from last frame to first frame) after the last frame has been reached.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class Animation {
    
    
    /*********************************************
     * MARK: Enums
     *********************************************/
    
    /**
     * An enumumrated type defining the available play styles for animations.
     * Animation play styles are used to indicate the direction of an animation and
     * whether or not it is looped.
     */
    public enum PlayStyle {
        /* Standard single-play animation. */
        DEFAULT,
        
        /* Forward, looping animation. */
        LOOP,
        
        /* Auto-reversing, single-play animation. */
        MIRRORED,
        
        /* Auto-reversing, looped animation. */
        MIRRORED_LOOP
    };
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The index of the first frame of the animation. */
    private int start;
    
    /** The index of the last frame of the animation. */
    private int end;
    
    /** The number of frames shown per second of animation. */
    private double framerate;
    
    /** The animation's play style. */
    private PlayStyle playStyle;
    

    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates an <code>Animation</code> object with given start frame, length, 
     * and framerate, that follows the default single-play play style.
     * @param start     The index of the animation's first frame. The given value
     *                  will be clamped to 0 if negative.
     * @param length    The number of frames in the animation. The given value
     *                  will be clamped to 1 if less then 1.
     * @param framerate How many frames to show per second of animation. The given
     *                  value will be clamped to 0 if negative.
     */
    public Animation(int start, int length, double framerate) {
        this(start, length, framerate, PlayStyle.DEFAULT);
    }
    
    /**
     * Creates an <code>Animation</code> object with given start frame, length, 
     * framerate, and play style.
     * @param start     The index of the animation's first frame. The given value
     *                  will be clamped to 0 if negative.
     * @param length    The number of frames in the animation. The given value
     *                  will be clamped to 1 if less then 1.
     * @param framerate How many frames to show per second of animation. The given
     *                  value will be clamped to 0 if negative.
     * @param playStyle The animation's play style.
     */
    public Animation(int start, int length, double framerate, PlayStyle playStyle) {
        this.start = (start < 0)? 0 : start;
        this.end = (length < 1)? this.start : (this.start + length - 1);
        setFramerate(framerate);
        this.playStyle = playStyle;
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the animation's start frame. 
     * @return  The index of the animation's first frame.
     */
    public int getStart() {
        return this.start;
    }
    
    /**
     * Returns the animation's end frame. 
     * @return  The index of the animation's last frame.
     */
    public int getEnd() {
        return this.end;
    }
    
    /** 
     * Returns the number of frames included in the animation sequence.
     * @return  The number of frames in the animation sequence.
     */
    public int frameCount() {
        return this.end - this.start + 1;
    }
    
    /** 
     * Returns the length of the animation, in key frames. This method is identical
     * to <code>frameCount()</code>.
     * @return  The length of the animation, in key frames.
     */
    public int length() {
        return frameCount();
    }
    
    /**
     * Returns the animation's framerate.
     * @return  The number of frames shown per second of animation.
     */
    public double framerate() {
        return this.framerate;
    }
    
    /**
     * Returns the animation's play style.
     * @return  The animation's defined play style.
     */
    public PlayStyle playStyle() {
        return this.playStyle;
    }
    
    /**
     * Returns whether the animation is looped. 
     * @return  <code>true</code> if the animation is looped; <code>false</code> 
     *          otherwise.
     */
    public boolean isLooped() {
        return (playStyle == PlayStyle.LOOP || 
                playStyle == PlayStyle.MIRRORED_LOOP);
    }
    
    /**
     * Returns whether the animation is mirrored. 
     * @return  <code>true</code> if the animation is mirrored; <code>false</code> 
     *          otherwise.
     */
    public boolean isMirrored() {
        return (playStyle == PlayStyle.MIRRORED || 
                playStyle == PlayStyle.MIRRORED_LOOP);
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the animation's framerate.
     * @param framerate The number of frames to show per second of animation.
     *                  The given value will be clamped to 0 if negative.
     */
    public void setFramerate(double framerate) {
        this.framerate = (framerate < 0)? 0 : framerate;
    }

}
