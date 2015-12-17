
package org.rowan.reverie.md2;

import javax.media.opengl.*;
import java.util.*;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import org.rowan.linalgtoolkit.*;
import org.rowan.reverie.*;
import org.rowan.reverie.md2.Animation.PlayStyle;
import org.rowan.reverie.md2.util.AnimationException;

/**
 * The <code>MD2Model</code> class defines an animated 3D model based on id 
 * Software's MD2 model specification. 
 * <p>
 * MD2 models contain a collection of key frames that define multiple animations. 
 * Each key frame has a name that, in some cases, indicates the name of the animation 
 * to which the key frame belongs. This, however, is strictly dependent on the 
 * exporter script used to generate the .md2 file. If a model's key frames are
 * not appropriately named, one can specify frame names programmatically by 
 * providing an array of <code>String</code>s to the <code>setFrameNames()</code> 
 * method. This, however, is not at all necessary and simply provides the ability
 * to use name-based animation cues (as will be described later).
 * <p>
 * <p>
 * In order to animate an <code>MD2Model</code>, one must pass a defined animation
 * sequence to one of the <code>start()</code> methods. Depending on the play style
 * associated with the given animation, the animation will either play through 
 * a single time, or loop continuously. A model's animation can be stopped, started, 
 * restarted or reversed at any time, using the <code>start()</code>, <code>stop()</code>, 
 * <code>restart()</code>, and <code>reverse()</code> methods, respectively.
 * <p>
 * An animation sequence is defined by a start frame, animation lenght (in frames)
 * framerate (key frames per second), and optional play style. An animation's play 
 * style simply defines whether the animation is looped and/or mirrored. The
 * default animation play style is not looped or mirrored. <code>Animation</code> 
 * objects can be used to pre-define and store reusable animation sequences.
 * <p>
 * If an <code>MD2Model</code>'s frames have been named according to their associated
 * animations, one can provide an animation name to the <code>start()</code> 
 * method or along with a desired framerate, and optional a play style. For example, 
 * calling <code>myModel.start("jump", 10)</code> will tell <code>myModel</code> to 
 * search through its key frames to find an animation named "jump". Once the model
 * has found a set of consecutive key frames matching the given animation name
 * (via substring matching), it will automatically construct an animation sequence
 * and begin animation. If no animation could be found to match the given string,
 * no animation will be loaded and the method call will be ignored.
 * <p>
 * <p>
 * <code>MD2Model</code>s provide support for multiple textures, with only one
 * texture being mapped at a time. Many times, texture paths will be specified
 * within a model's associated .md2 file. In this case, the first texture is set
 * to the current texture. The active texture can be changed by providing the
 * index of the desired texture to the <code>setTexture()</code> method. In the
 * event that texture paths are not specified in a model's .md2 file, or the
 * specified textures are unavailable, one can manually specify one or more
 * texture paths for the model using the <code>setTexturePath()</code> or
 * <code>setTexturePaths()</code> methods.
 * <p>
 * <p>
 * MD2 models can be loaded from .md2 files using the <code>MD2Loader</code> 
 * singleton class. Once an <code>MD2Model</code> has been loaded, it can be 
 * subsequently duplicated to provide multiple models of the same type with no 
 * additional I/O. This can be accomplished via its constructor, or the 
 * <code>copy()</code> method.
 *
 * @see <a href="http://en.wikipedia.org/wiki/MD2_(file_format)">MD2 File Format</a>
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public final class MD2Model extends Model {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** An ordered array of the model's texture paths. */
    private String[] texturePaths;
    
    /** An ordered array of the model's textures. */
    private Texture[] textures;
    
    /** An ordered array of vertex indices used to draw the model. */
    private int[] vertIndices;
    
    /** An ordered array of texture coordinate indices used to draw the model. */
    private int[] texCoordIndices;
    
    /** An array of texture coordinates mapping the model's texture to its vertices. */
    private Vector2D[] texCoords;
    
    /** An ordered array of key frames that define the model's animations. */
    private KeyFrame[] frames;
    
    
    /** A boolean value indicating whether the model is being animated. */
    private boolean animating;
    
    /** The index of the model's current frame of animation. */
    private int currentFrame;
    
    /** The current texture used to draw the model. */
    private int currentTexture;
    
    /** The degree of interpolation between the current and next frames. */
    private double interpolation;
    
    
    /** The model's current animation. */
    private Animation animation;
    
    /** The most recent interpolated frame, used to draw the model. */
    private KeyFrame interpolatedFrame;
    
    /** Whether the model's animation is  playing forward, as opposed to backward. */
    private boolean animatingForward;
    

    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates an <code>MD2Model</code> that represents the same type of model
     * as the argument.
     * <p>
     * This method uses the private, unchanging reference fields of the given 
     * model to construct a new <code>MD2Model</code> instance. As the new model
     * will be freshly initialized, no state information will be coppied from the 
     * original with exception of the the model's position and orientation offsets,
     * which will be coppied for convenience. 
     * <p>
     * In other words, this constructor creates a stateless deep copy of the 
     * argument.
     * <p>
     * This is the prefered method of creating duplicate MD2 models, rather than
     * using <code>MD2Loader</code> for each, as it limits I/O calls.
     * @param model The model to copy.
     */
    public MD2Model(MD2Model model) {
        // copy texture paths
        this.texturePaths = new String[model.texturePaths.length];
        for (int i=0; i<model.texturePaths.length; i++) {
            this.texturePaths[i] = new String(model.texturePaths[i]);
        }
        
        // copy frames
        this.frames = new KeyFrame[model.frames.length];
        for (int i=0; i<model.frames.length; i++) {
            this.frames[i] = new KeyFrame(new String(model.frames[i].getName()),
                                                model.frames[i].getVertices(),
                                                model.frames[i].getNormals());
        }
        
        // copy position/orientation offsets
        setPositionOffset(model.getPositionOffset());
        setOrientationOffset(model.getOrientationOffset());
        
        // set other model definition fields
        this.textures = new Texture[texturePaths.length];
        this.vertIndices = model.vertIndices;
        this.texCoordIndices = model.texCoordIndices;
        this.texCoords = model.texCoords;
        this.currentTexture = 0;
        
        // initialize animation properties
        reset();
    }
        
    /**
     * Creates an <code>MD2Model</code>. This method is not inteaded for use by
     * end-users. The <code>MD2Loader</code> class should be used to load and
     * create <code>MD2Model</code> instances.
     */
    public MD2Model(String[] texturePaths, int[] vertIndices, int[] texCoordIndices, Vector2D[] texCoords, KeyFrame[] frames) {
        // set model definition fields
        this.texturePaths = texturePaths;
        this.textures = new Texture[texturePaths.length];
        this.vertIndices = vertIndices;
        this.texCoordIndices = texCoordIndices;
        this.texCoords = texCoords;
        this.frames = frames;
        this.currentTexture = 0;
        
        // initialize animation properties
        reset();
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the model's texture paths.
     * @return      The filepaths of the model's texture images.
     */
    public String[] getTexturePaths() {
        return this.texturePaths;
    }
    
    /**
     * Returns the texture path of the specified texture.
     * @param index The index of the model's texture.
     * @return      The filepath of the specified texture image.
     */
    public String getTexturePath(int index) {
        return this.texturePaths[index];
    }
    
    /**
     * Returns the current texture index.
     * @return  The index of the texture currently used for drawing.
     */
    public int currentTexture() {
        return this.currentTexture;
    }
    
    /**
     * Returns the number of textures this model has.
     * @return  The number of textures stored in the model.
     */
    public int textureCount() {
        return this.texturePaths.length;
    }
    
    /**
     * Returns the number of faces that define the model.
     * @return  The number of faces that define the model.
     */
    public int faceCount() {
        return this.vertIndices.length/3;
    }
    
    /**
     * Returns the current frame index. 
     * @return  The index of the model's current frame of animation.
     */
    public int currentFrame() {
        return this.currentFrame;
    }
    
    /**
     * Returns the index of the next animation frame. This method will take the 
     * direction and play style of the current animation into account when
     * determining the next frame.
     * @return  The index of the next animation frame.
     */
    public int nextFrame() {
        // only one animation frame?
        if (animation.length() == 1)
            return currentFrame;
        
        // animating backward?
        if (!animatingForward)
            // return previous frame
            return previousFrameInAnimationSequence();
        return nextFrameInAnimationSequence();
    }
    
    /** 
     * Returns the index of the next frame in the current animation sequence,
     * without consideration of the current animation direction.
     * @return  The index of the next frame in the current animation sequence.
     */
    private int nextFrameInAnimationSequence() {
        // on last frame?
        if (currentFrame == animation.getEnd()) {
            // loop?
            if (animation.isLooped())
                return animation.getStart();
            return animation.getEnd();
        }
        
        return currentFrame+1;
    }
    
    /**
     * Returns the index of the previous animation frame. This method will take 
     * the direction and play style of the current animation into account when
     * determining the previous frame.
     * @return  The index of the previous animation frame.
     */
    public int previousFrame() {
        // only one animation frame?
        if (animation.length() == 1)
            return currentFrame;
            
        // animating backward?
        if (!animatingForward)
            // return next frame
            return nextFrameInAnimationSequence();
        return previousFrameInAnimationSequence();
    }
    
    /** 
     * Returns the index of the previous frame in the current animation sequence,
     * without consideration of the current animation direction.
     * @return  The index of the previous frame in the current animation sequence.
     */
    private int previousFrameInAnimationSequence() {
        // on first frame?
        if (currentFrame == animation.getStart()) {
            // loop?
            if (animation.isLooped())
                return animation.getEnd();
            return animation.getStart();
        }
        
        return currentFrame-1;
    }
    
    /**
     * Returns the current frame of the current animation.
     * @return  The relative frame index of the current animation.
     */
    public int animationFrame() {
        return currentFrame - animation.getStart();
    }
    
    /**
     * Returns whether the model is currently being animated. 
     * @return  <code>true</code> if the model is being animated; <code>false</code>
     *          otherwise.
     */
    public boolean isAnimating() {
        return this.animating;
    }
    
    /**
     * Returns the model's current animation.
     * @return  An <code>Animation</code> object that defines the model's current
     *          animation sequence.
     */
    public Animation getAnimation() {
        return this.animation;
    }
    
    /**
     * Returns the number of frames that define the model and its animations. 
     * @return  The number of frames that define the model and its animations.
     */
    public int frameCount() {
        return this.frames.length;
    }
    
    /**
     * Returns the name of a specified frame.
     * @param frame A frame index.
     * @return      The name of the specified frame.
     * @throws IndexOutOfBoundsException if <code>frame</code> is negative or 
     *              greater than <code>frameCount()-1</code>.
     */
    public String getFrameName(int frame) {
        // valid frame index?
        if (frame < 0 || frame >= frameCount())
            throw new IndexOutOfBoundsException("Frame index out of bounds: " + frame);
        
        return frames[frame].getName();
    }
    
    /**
     * Returns the names of every frame in the model.
     * @return  A list of frame names.
     */
    public List<String> getFrameNames() {
        ArrayList<String> names = new ArrayList<String>(frameCount());
        for (int i=0; i<frameCount(); i++)
            names.add(frames[i].getName());
        return names;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Replaces all of the model's textures with a specified set of images. The
     * first texture will be made the current texture.
     * @param filepaths An array containing the filepaths of the texture images.
     */
    public void setTexturePaths(String[] filepaths) {
        // replace texture filepaths 
        this.texturePaths = filepaths;
        
        // re-initialize textures array
        this.textures = new Texture[filepaths.length];
        
        // flag model to reload textures
        reload();
    }
    
    /**
     * Replaces all of the model's textures with a single specified image. This
     * is a convenience method and is identical to calling 
     * <code>setTexturePaths(new String[] {filepath})</code>.
     * @param filepath  The filepath of the texture image.
     */
    public void setTexturePath(String filepath) {
        setTexturePaths(new String[] {filepath});
    }
    
    /**
     * Sets the current texture. The given texture index will be clamped automatically
     * if out of range. If the model has no textures, this method has no effect.
     * @param index The index of the texture to be made current.
     */
    public void setTexture(int index) {
        // no textures?
        if (textureCount() == 0)
            return;
        
        // clamp given index
        index = (index >= textureCount())? textureCount()-1 : index;
        index = (index < 0)? 0 : index;
        
        // set texture index
        this.currentTexture = index;
    }
    
    /**
     * Sets the names of the model's animation key frames. The given names will
     * be mapped to each keyframe in order starting at frame 0. Any additional
     * names will be ignored. If there are more key frames than names given, 
     * excess key frames will be unaltered.
     * @param names An array of frame names.
     */
    public void setFrameNames(String[] names) {
        int count = Math.min(names.length, frameCount());
        
        for (int i=0; i<count; i++) {
            frames[i].setName(names[i]);
        }
    }
    
    /**
     * Sets the current frame to a specified frame, stopping and resetting all
     * animation properties.
     * @param frame The index of the model's frame to be set as current. The given
     *              frame index must reference a frame defined by the model, and 
     *              should thus be in the range [0, <code>frameCount()</code>).
     *              If the frame index is out of bounds, it will be clamped to 
     *              the acceptable range.
     */
    public void setCurrentFrame(int frame) {
        // clamp frame index
        if (frame < 0)
            frame = 0;
        if (frame >= frameCount())
            frame = frameCount()-1;
        
        // reset animation properties
        reset();
        
        // set frame
        this.currentFrame = frame;
    }
    
    /**
     * Sets the current frame to a specified frame of the current animation. This 
     * method does not stop/start the model's animation.
     * @param frame The relative frame index of the current animation to be set
     *              as the current frame. Because the specified frame index should
     *              relative to the current animation, it should be in the range
     *              [0, <code>getAnimation().frameCount()</code>). If the given 
     *              frame index is out of bounds, it will be clamped to the 
     *              acceptable range.
     */
    public void setAnimationFrame(int frame) {
        // clamp frame index
        if (frame < 0)
            frame = 0;
        if (frame >= getAnimation().frameCount())
            frame = getAnimation().frameCount()-1;
        
        // set current frame
        this.currentFrame = frame+animation.getStart();
    }
    
    
    /*********************************************
     * MARK: Animation
     *********************************************/
    
    /**
     * Stops and resets all animation properties.
     */
    public void reset() {
        this.animation = new Animation(0, 1, 0);
        this.currentFrame = 0;
        this.interpolation = 0.0;
        this.animating = false;
        this.interpolatedFrame = this.frames[0];
        this.animatingForward = true;
    }
    
    /** 
     * Starts a given animation.
     * @param animation The animation to start.
     * @throws AnimationException if the given animation references frames not
     *                  defined by the model.
     */
    public void start(Animation animation) {
        // valid animation for this model?
        if (animation.getStart()+1 > frameCount() || animation.getEnd()+1 > frameCount()) {
            throw new AnimationException("Animation not valid for model");
        }
        
        // set animation properties
        this.animation = animation;
        
        // start animating from first frame
        restart();
    }
    
    /**
     * Starts the pre-defined animation associated with the specified name. The 
     * animation will be played through once and stop on the last frame.
     * <p>
     * This method uses substring matching against loaded key frame names in order 
     * to find the first appropriate animation. For this reason, it is not 
     * necessarily reliable. If no animation could be matched, this method does
     * nothing.
     * @param name      The name of the animation. This name will be compared 
     *                  with the model's key frame names in an attempt to find a 
     *                  pre-defined animation.
     * @param framerate The number of frames to show per second of animation.
     */
    public void start(String name, int framerate) {
        start(name, framerate, PlayStyle.DEFAULT);
    }
    
    /**
     * Starts the pre-defined animation associated with the given animation name, 
     * using a given play style.
     * <p>
     * This method uses substring matching against loaded key frame names in order 
     * to find the first appropriate animation. For this reason, it is not 
     * necessarily reliable. If no animation could be matched, this method does
     * nothing.
     * @param name      The name of the animation. This name will be compared 
     *                  with the model's key frame names in an attempt to find a 
     *                  pre-defined animation.
     * @param framerate The number of frames to show per second of animation.
     * @param playStyle The animation style to use for the animation.
     */
    public void start(String name, int framerate, PlayStyle playStyle) {
        int start = -1;
        int length = 0;
        
        // find start frame
        for (int i=0; i<frameCount(); i++) {
            KeyFrame frame = this.frames[i];
            if (frame.getName().indexOf(name) >= 0) {
                start = i;
                break;
            }
        }
        
        // no match found?
        if (start < 0)
            return;
        
        // find animation length
        for (int i=start; i<frameCount(); i++) {
            KeyFrame frame = this.frames[i];
            if (frame.getName().indexOf(name) < 0)
                break;
            length++;
        }
        
        // start animation
        start(start, length, framerate, playStyle);
    }
    
    /** 
     * Starts an animation defined by a given start and end frame. The animation 
     * will be played through once and stop on the last frame.
     * @param start The index of the animation's first frame.
     * @param length    The number of frames in the animation.
     * @param framerate The number of frames to show per second of animation.
     * @throws AnimationException if <code>start</code> and <code>length</code> 
     *                  don't describe a valid animation for this model.
     */
    public void start(int start, int length, double framerate) {
        start(new Animation(start, length, framerate));
    }
    
    /** 
     * Starts an animation defined by a given start and end frame, using a given 
     * play style.
     * @param start The index of the animation's first frame.
     * @param length    The number of frames in the animation.
     * @param framerate The number of frames to show per second of animation.
     * @param playStyle The animation style to use for the defined animation.
     * @throws AnimationException if <code>start</code> and <code>length</code> 
     *                  don't describe a valid animation for this model.
     */
    public void start(int start, int length, double framerate, PlayStyle playStyle) {
        start(new Animation(start, length, framerate, playStyle));
    }
    
    /**
     * Starts the current animation if the model is not animating already.
     */
    public void start() {
        this.animating = true;
    }
    
    /**
     * Restarts the current animation. If the model is not currently being animated,
     * The animation will be started.
     */
    public void restart() {
        this.currentFrame = animation.getStart();
        this.animating = true;
    }
    
    /**
     * Reverses the model's animation direction.
     */
    public void reverse() {
        this.animatingForward = !this.animatingForward;
    }
    
    /**
     * Stops animation if the model is currently animating.
     */
    public void stop() {
        this.animating = false;
    }
    
    /**
     * Updates the model's animation and performs interpolation between the
     * two nearest frames. This method will automatically reverse the animation
     * direction as needed.
     * @param framerate The number of updates performed per second.
     */
    public void update(double framerate) {
        // only one animation frame?
        if (animation.length() == 1) {
            this.interpolatedFrame = frames[currentFrame()];
            return;
        }
        
        
        // progress time
        double delta = (animation.framerate() / framerate);
        interpolation += delta;
        
        
        // progress frame
        while (interpolation >= 1.0) {
            interpolation -= 1.0;
            
            // get next frame
            currentFrame = nextFrame();
            
            // reverse animation direction?
            if (((currentFrame >= animation.getEnd()) && animatingForward && animation.isMirrored()) ||
                ((currentFrame <= animation.getStart()) && !animatingForward && animation.isMirrored() && animation.isLooped()))
                reverse();
        }
        
        
        // not between frames?
        if (currentFrame() == nextFrame()) {
            this.interpolatedFrame = frames[currentFrame()];
            return;
        }

        
        // compute keyframe interpolation
        KeyFrame f1 = frames[currentFrame()];
        KeyFrame f2 = frames[nextFrame()];
        this.interpolatedFrame = f1.lerp(f2, interpolation);
    }
    
    
    /*********************************************
     * MARK: Model (Superclass)
     *********************************************/
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * This method does nothing. 
     * @param space     The simulation space for which the model will be initialized.
     * @param drawable  The OpenGL rendering target for which the model will be
     *                  initialized.
     */
    protected final void initModel(Space space, GLAutoDrawable drawable) {
        // nothing to do
    }
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * Loads all texture resources needed by the model.
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected final void loadAllResources(Space space, GLAutoDrawable drawable) {
        // initialize textures array
        this.textures = new Texture[textureCount()];
        
        // load textures
        for (int i=0; i<textureCount(); i++)
            this.textures[i] = ResourceManager.getInstance().loadTexture(this.texturePaths[i]);
    }
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * Loads the model's current texture, if it exists. 
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected final void loadResourcesForDrawing(Space space, GLAutoDrawable drawable) {
        // no textures to load?
        if (textureCount() == 0)
            return;
        
        // load the current texture
        String path = this.texturePaths[currentTexture];
        this.textures[currentTexture] = ResourceManager.getInstance().loadTexture(path);
    }
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * This method does nothing. 
     * @param space     The simulation space for which the model was initialized.
     * @param drawable  The OpenGL rendering target for which the model was
     *                  initialized.
     */
    protected final void disposeOfModel(Space space, GLAutoDrawable drawable) {
        // nothing to do
    }
    
    /**
     * A template method used by the <code>Model</code> class hierarchy that should 
     * not be called by end-users.
     * <p>
     * Draws the model based on previously calculated interpolation between the
     * model's current and next frames.
     * @param space     The simulation space for which the model will be drawn.
     * @param drawable  The OpenGL rendering target onto which the model will be
     *                  drawn.
     */
    protected final void drawModel(Space space, GLAutoDrawable drawable) {
        // get OpenGL rendering pipeline from drawable
        GL gl = drawable.getGL();

        
        // enable texturing and bind current texture if needed
        if (textureCount() > 0) {
            
            // get current texture
            Texture texture = this.textures[currentTexture];
            
            // enable and bind if not null
            if (texture != null) {
                texture.enable();
                texture.bind();
            }
        }
        
        // get vertex and normal arrays
        Vector3D[] vertices = this.interpolatedFrame.getVertices();
        Vector3D[] normals = this.interpolatedFrame.getNormals();
        
        
        // draw the model
        gl.glBegin(GL.GL_TRIANGLES);
        {
            // draw each triangle
            for (int i=0; i<faceCount(); i++) {

                // draw each vertex
                for (int j=0; j<3; j++) {
                    int index = i*3+j;

                    // get texture coordinate and pass to OpenGL
                    Vector2D texCoord = texCoords[texCoordIndices[index]];
                    gl.glTexCoord2d(texCoord.getX(), 
                                    texCoord.getY());
                    
                    // get surface normal and pass to OpenGL
                    Vector3D normal = normals[i];
                    gl.glNormal3d(normal.getX(), 
                                  normal.getY(), 
                                  normal.getZ());
                    
                    // get vertex and pass to OpenGL
                    Vector3D vertex = vertices[vertIndices[index]];
                    gl.glVertex3d(vertex.getX(),
                                  vertex.getY(), 
                                  vertex.getZ());
                }
            }
        }
        gl.glEnd();
        
        
        // disable texturing
        if (textureCount() > 0) {
            
            // get current texture
            Texture texture = this.textures[currentTexture];
            
            // disable if not null
            if (texture != null)
                texture.disable();
        }
    }
    
    
    /*********************************************
     * MARK: Copy
     *********************************************/
    
    /**
     * A convenience method identical to <code>new MD2Model(this)</code>.
     * <p>
     * This is the prefered method of creating duplicate MD2 models, rather than
     * using <code>MD2Loader</code> for each, as it limits I/O calls.
     * @return  A duplicate of this model.
     */
    public MD2Model copy() {
        return new MD2Model(this);
    }
}
