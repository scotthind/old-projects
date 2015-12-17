package org.rowan.opawarenessvis.display;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.rowan.opawarenessvis.data.Asset;
import org.rowan.opawarenessvis.data.Component;
import org.rowan.opawarenessvis.data.Entity;
import org.rowan.opawarenessvis.data.OpSystem;

/**
 * All JOGL code is handled in this class, as it is a GLEventListener and
 * MouseListener to the main GL canvas.
 * 
 * @author Dan Urbano
 * @version 1.0
 */
public class MainJOGL implements GLEventListener, MouseListener, MouseWheelListener, MouseMotionListener {

    private static final int BUFFER_SIZE = 512; //may be able to be 64? not sure
    private static final double CLIPPING_HEIGHT = 100.0;
    private static final double CLIPPING_WIDTH = 100.0;
    private static final double CIRCLE_ANGLE_PRECISION = .1;
    private static final float MAIN_COMPONENT_DEPTH = -4f;
    private static final float SECONDARY_COMPONENT_DEPTH = 0f;
    private static final float MAIN_COMPONENT_SCALE = 49f;
    private static final float SECONDARY_COMPONENT_SCALE = 49f * .95f;
    private static final float MIN_CANVAS_DEPTH = -10f;
    private static final float MAX_CANVAS_DEPTH = 10f;
    private static final float TRIANGLE_U_X = 0;
    private static final float TRIANGLE_U_Y = 1;
    private static final float TRIANGLE_LR_X = .86603f;
    private static final float TRIANGLE_LR_Y = -.5f;
    private static final float TRIANGLE_LL_X = -.86603f;
    private static final float TRIANGLE_LL_Y = -.5f;
    private static final String MSG_NO_SYSTEM_LOADED = "No System Loaded";
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static volatile int idCounter = 0;
    private final GLJPanel canvas;
    private Displayable currentDisplayable = null;
    private OpSystem currentSystem = null;
    private String currentMission = null;
    private Point pickPoint = new Point();
    private int pickClickCount = 0;
    private int pickClickButton = LEFT;
    private GLU glu;
    private GLUT glut;
    private int CIRCLE, TRIANGLE, CIRCLE_HIGHLIGHT;
    private Map<Integer, Displayable> pickingMap = new HashMap<Integer, Displayable>();
    private Map<Integer, Integer> outerRingMap = new TreeMap<Integer, Integer>();
    private boolean calculatedRings = false;
    private ArrayList<Integer> layers = new ArrayList<Integer>();
    private final JFrame mainFrame;
    private static final double MINZOOM = .001d;
    private static final double MAXZOOM = .5d - .05d;
    private double zoom = MINZOOM;
    private volatile Point dragPoint = new Point();
    private volatile double panX = 0;
    private volatile double panY = 0;

    /**
     * Create a new MainJOGL with an associated GLCanvas.
     * @param canvas The associated GLCanvas.
     */
    MainJOGL(GLJPanel canvas, JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.canvas = canvas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        // Obtain the GL
        GL gl = drawable.getGL();

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT); // try setting this to GL_FLAT and see what happens.
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthRange(0.0, 1.0); /* The default z mapping */

        // Create GLU and GLUT
        glu = new GLU();
        glut = new GLUT();

        // Build display lists
        buildDisplayLists(gl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Obtain the GL
        GL gl = drawable.getGL();

        // Get the ortho projection
        double[] ortho = getOrtho(x, y, width, height);

        // Use the projection to properly draw the screen
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(ortho[0], ortho[1], ortho[2], ortho[3], MIN_CANVAS_DEPTH, MAX_CANVAS_DEPTH);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        // Obtain the GL
        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Configure call back method for picking
        configurePicking(drawable);

        // Get the ortho projection and configure the screen
        double[] ortho = getOrtho(0, 0, canvas.getWidth(), canvas.getHeight());
        gl.glViewport(0, 0, canvas.getWidth(), canvas.getHeight());
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(ortho[0], ortho[1], ortho[2], ortho[3], MIN_CANVAS_DEPTH, MAX_CANVAS_DEPTH);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        
        // Draw
        draw(gl, GL.GL_RENDER);

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        //Not used
    }

    /**
     * Configure picking (selecting) for each object.
     * @param gl The GL.
     */
    private void configurePicking(GLAutoDrawable drawable) {
        // Obtain the GL
        GL gl = drawable.getGL();

        // Get the ortho projection
        double[] ortho = getOrtho(0, 0, drawable.getWidth(), drawable.getHeight());

        // Intialize variables
        int[] selectBuffer = new int[BUFFER_SIZE];
        IntBuffer buffer = BufferUtil.newIntBuffer(BUFFER_SIZE);
        int hits;
        int viewport[] = new int[4];

        // Intialize GL properties
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        gl.glSelectBuffer(BUFFER_SIZE, buffer);
        gl.glRenderMode(GL.GL_SELECT);
        gl.glInitNames();
        gl.glPushName(-1);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        // Create pixel picking region near cursor location
        glu.gluPickMatrix((double) pickPoint.x,
                (double) (viewport[3] - pickPoint.y),
                0.5, 0.5, viewport, 0);
        gl.glOrtho(ortho[0], ortho[1], ortho[2], ortho[3], MIN_CANVAS_DEPTH, MAX_CANVAS_DEPTH);
        draw(gl, GL.GL_SELECT);
        gl.glPopMatrix();
        gl.glFlush();

        // Process all picks
        hits = gl.glRenderMode(GL.GL_RENDER);
        buffer.get(selectBuffer);
        processHits(hits, selectBuffer);
    }

    /**
     * Draw all objects on the screen.
     * @param gl The GLDrawable.
     * @param mode The GL mode.
     */
    private void draw(GL gl, int mode) {
        // WHAT TO DRAW IF NO SYSTEM IS LOADED
        float textHeight = 119.05f;
        if (currentDisplayable == null) {
            float textWidth = glut.glutStrokeLengthf(GLUT.STROKE_ROMAN, MSG_NO_SYSTEM_LOADED);
            gl.glPushMatrix();
            gl.glTranslatef(50f, 50f, MAIN_COMPONENT_DEPTH);
            gl.glScalef(MAIN_COMPONENT_SCALE, MAIN_COMPONENT_SCALE, 1f);
            gl.glColor3f(.9f, .9f, .9f);
            gl.glCallList(CIRCLE);
            gl.glTranslatef(1.25f * -.5f, .25f * -.5f, 4f);
            gl.glColor3f(0f, 0f, 0f);
            gl.glScalef(1.25f * 1 / textWidth, .25f * 1 / textHeight, 1);
            glut.glutStrokeString(GLUT.STROKE_ROMAN, MSG_NO_SYSTEM_LOADED);
            gl.glPopMatrix();
            return;
        }
        
        // GATHER CURRENT DISPLAYABLE INFORMATION
        int numComponentsToDraw = 0;
        boolean isReady = false;
        Set<Component> components = new HashSet<Component>();
        if (currentDisplayable instanceof OpSystem) {
            OpSystem system = (OpSystem) currentDisplayable;
            components = system.getComponents();
            numComponentsToDraw = components.size();
            isReady = system.isReady(currentMission);
        } else if (currentDisplayable instanceof Entity) {
            Entity entity = (Entity) currentDisplayable;
            components = entity.getComponents();
            numComponentsToDraw = components.size();
            isReady = entity.isReady(currentMission);
        } else if (currentDisplayable instanceof Asset) {
            Asset asset = (Asset) currentDisplayable;
            numComponentsToDraw = 0;
            isReady = asset.isReady(currentMission);
        }

        // DRAW THE CURRENT DISPLAYABLE
        if (mode == GL.GL_SELECT) {
            int sysInt;
            if (pickingMap.containsValue(currentDisplayable)) {
                sysInt = getKeyByValue(pickingMap, currentDisplayable);
            } else {
                sysInt = getID();
                pickingMap.put(sysInt, currentDisplayable);
            }
            gl.glLoadName(sysInt);
        }
        gl.glPushMatrix();
        gl.glTranslatef(50f, 50f, MAIN_COMPONENT_DEPTH);
        gl.glScalef(MAIN_COMPONENT_SCALE, MAIN_COMPONENT_SCALE, 1f);
        if (isReady)
                gl.glColor3f(.7f, .8f, 1.0f);
            else
                gl.glColor3f(.9f, .8f, .6f);
        if (currentDisplayable instanceof Asset) {
            gl.glCallList(TRIANGLE);
            Asset asset = (Asset)(currentDisplayable);
            DecimalFormat df = new DecimalFormat("#.00");
            String score = df.format(asset.getScore());
            float textWidth = glut.glutStrokeLengthf(GLUT.STROKE_ROMAN, score);
   
            gl.glTranslatef(1.0f * -.5f, .25f * -1.25f, 4f);
            gl.glColor3f(0f, 0f, 0f);
            gl.glScalef(1.0f * 1 / textWidth, .25f * 1f / textHeight, 1);
            glut.glutStrokeString(GLUT.STROKE_ROMAN, score);
        } else {
            gl.glCallList(CIRCLE);
        }
        gl.glPopMatrix();  
        
        // GATHER ALL INSIDE COMPONENTS INFORMATION
        List<Component> sortedComponents = getSortedComponents(components);
        int firstNotReady = findFirstNotReady(sortedComponents);
        ArrayList<Integer> rings = calculateRings(numComponentsToDraw);
        float bigR = 1f;
        float radius;
        double angle;
        int compNum = 0;
        
        // DARW ALL INSIDE COMPONENTS
        for (int j = 0; j < rings.size(); j++) {
            float preRot = 0;
            if (compNum + rings.get(j) > firstNotReady) {
                float breakpoint = (firstNotReady) - compNum;
                preRot = (breakpoint / (float) rings.get(j)) * 180f;
            }
            radius = rad(rings.get(0));
            angle = 360f / rings.get(j);

            for (int i = 0; i < rings.get(j); i++) {
                if (mode == GL.GL_SELECT) {
                    int id;
                    if (pickingMap.containsValue(sortedComponents.get(compNum))) {
                        id = getKeyByValue(pickingMap, sortedComponents.get(compNum));
                    } else {
                        id = getID();
                        pickingMap.put(id, sortedComponents.get(compNum));
                    }
                    gl.glLoadName(id);
                }
                gl.glPushMatrix();
                gl.glTranslatef(50f, 50f, 0f);
                gl.glRotatef(90 + (float) (angle * i) - (preRot), 0f, 0f, 1f);
                if (rings.get(j) != 1) {
                    gl.glTranslatef((bigR * MAIN_COMPONENT_SCALE) - (SECONDARY_COMPONENT_SCALE * radius), 0f, SECONDARY_COMPONENT_DEPTH);
                }
                gl.glRotatef((float) -(angle * i) - 90 + (preRot), 0f, 0f, 1f);
                gl.glScalef(SECONDARY_COMPONENT_SCALE * radius, SECONDARY_COMPONENT_SCALE * radius, 1f);

                if (sortedComponents.get(compNum).isReady(currentMission)) {
                    gl.glColor3f(.4f, .5f, 1f);
                } else {
                    gl.glColor3f(1f, .7f, .4f);
                }
                if (sortedComponents.get(compNum) instanceof Entity) {
                    gl.glCallList(CIRCLE);
                } else {
                    gl.glCallList(TRIANGLE);
                }

                String id = sortedComponents.get(compNum).getID(); 
                float textWidth = glut.glutStrokeLengthf(GLUT.STROKE_ROMAN,id);
                float factor = 1f;
                if (textWidth < 250f) {
                    factor = Math.min(4f, 375f / textWidth);
                }

                if (sortedComponents.get(compNum) instanceof Entity) {
                    gl.glTranslatef(1.25f * -.5f, (factor > 2f? factor * .8f : factor) * .25f * -.5f, 4f);
                    gl.glColor3f(0f, 0f, 0f);
                    gl.glScalef(1.25f * 1 / textWidth, factor * .25f * 1 / textHeight, 1);
                    glut.glutStrokeString(GLUT.STROKE_ROMAN, id);
                } else {
                    gl.glTranslatef(1.0f * -.5f, (factor > 1f? factor * .7f : factor) * .25f * -1.25f, 4f);
                    gl.glColor3f(0f, 0f, 0f);
                    gl.glScalef(1.0f * 1 / textWidth, factor * .25f * 1 / textHeight, 1);
                    glut.glutStrokeString(GLUT.STROKE_ROMAN, id);
                }
                gl.glPopMatrix();
                compNum++;
            }
            bigR -= 2 * radius;
        }
    }

    /**
     * Process all of the hits (number of objects that were picked on a click).
     * @param hits The number of hits.
     * @param selectBuffer 
     */
    private void processHits(int hits, int[] selectBuffer) {
        if (pickClickCount != 1 && pickClickCount !=2) {
            return;
        }
        int smallestDepth = Integer.MAX_VALUE;
        int name = -1;
        for (int i = 0; i < hits; i++) {
            // selectBuffer[1] contains the min z depth
            // selectBuffer[3] contains the name of the object
            if (selectBuffer[1] <= smallestDepth) {
                smallestDepth = selectBuffer[(i*4)+1];
                name = selectBuffer[(i*4)+3];
            }
        }

        Displayable displayable = pickingMap.get(name);
        if (displayable != null) {
            if (pickClickButton == LEFT) {
                displayable.updateDetailWindow();
                if (pickClickCount == 2) {
                    pickClickCount = 0;
                    if (!displayable.equals(currentDisplayable)) {
                        BreadCrumbBar.getInstance().addCrumb(displayable);
                        PreviewLabel.getInstance().updatePanel(displayable.getType());
                        setDisplayable(displayable, true);
                        MainGUI.frame.validate();
                    }
                }
                else if (pickClickCount == 1)
                {
                    PreviewLabel.getInstance().updatePanel(displayable.getType());
                }
            }
        }
        
        pickClickCount = 0;
    }

    private void buildDisplayLists(GL gl) {
        CIRCLE = gl.glGenLists(1);
        gl.glNewList(CIRCLE, GL.GL_COMPILE);
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glVertex2f(0.0f, 0.0f);
        for (double angle = 0; angle <= 360; angle += CIRCLE_ANGLE_PRECISION) {
            gl.glVertex2f((float) Math.sin(angle), (float) Math.cos(angle));
        }
        gl.glEnd();
        gl.glEndList();

        CIRCLE_HIGHLIGHT = gl.glGenLists(1);
        gl.glNewList(CIRCLE_HIGHLIGHT, GL.GL_COMPILE);
        gl.glLineWidth(3f);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(0.0f, 0.0f);
        for (double angle = 0; angle <= 360; angle += CIRCLE_ANGLE_PRECISION) {
            gl.glVertex2f((float) Math.sin(angle), (float) Math.cos(angle));
        }
        gl.glEnd();
        gl.glLineWidth(.5f);
        gl.glEndList();

        TRIANGLE = gl.glGenLists(1);
        gl.glNewList(TRIANGLE, GL.GL_COMPILE);
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(TRIANGLE_U_X, TRIANGLE_U_Y);
        gl.glVertex2f(TRIANGLE_LL_X, TRIANGLE_LL_Y);
        gl.glVertex2f(TRIANGLE_LR_X, TRIANGLE_LR_Y);
        gl.glEnd();
        gl.glEndList();

    }

    /**
     * Get the ortho projection, given canvas window parameters.
     * @param x Not used in 2D.
     * @param y Not used in 2D.
     * @param width Width of the canvas.
     * @param height Height of the canvas.
     * @return An array of doubles representing the ortho x, w, y, and h
     *         parameters, in that order. The array returned is size 4.
     */
    private double[] getOrtho(int x, int y, int width, int height) {
        double ortho_x = 0d;
        double ortho_y = 0d;
        double ortho_w = CLIPPING_WIDTH;
        double ortho_h = CLIPPING_HEIGHT;
        double extraSpace;
        double factor = (width > height) ? (double) height / CLIPPING_HEIGHT : (double) width / CLIPPING_WIDTH;
        extraSpace = Math.abs(width - height) / factor;
        ortho_x = (width > height) ? -(extraSpace / 2d) : ortho_x;
        ortho_y = (width <= height) ? -(extraSpace / 2d) : ortho_y;
        ortho_w = (width > height) ? CLIPPING_HEIGHT + extraSpace / 2d : ortho_w;
        ortho_h = (width <= height) ? CLIPPING_WIDTH + extraSpace / 2d : ortho_h;
        return new double[]{ortho_x+(zoom*ortho_w) + panX, ortho_w-(zoom*ortho_w) + panX ,
            ortho_y+(zoom*ortho_h) + panY, ortho_h-(zoom*ortho_h) + panY};
    }
    
    public void setDisplayable(Displayable displayable, boolean refreshScreen) {
        calculatedRings = false;
        currentDisplayable = displayable;
        zoom = MINZOOM;
        panX = 0;
        panY = 0;
        PreviewLabel.getInstance().updatePanel(displayable.getType());
        if (refreshScreen) {
            this.canvas.display();
        }
    }
    
     public void setSystemAndMission(OpSystem system, String mission, boolean refreshScreen) {
        currentSystem = system;
        currentMission = mission;
        setDisplayable(system, refreshScreen);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (e.getClickCount() == 1) {
                if (currentDisplayable != null && currentSystem != null && (BreadCrumbBar.getInstance().getTheSize() > 1)) {
                    BreadCrumbBar.getInstance().removeLastCrumb();
                    setDisplayable(BreadCrumbBar.getInstance().getLastDisplayable(), true);
                    canvas.display();
                    mainFrame.repaint();
                    
                }
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (e.getClickCount() <= 2) {
                pickPoint = e.getPoint();
                pickClickCount = e.getClickCount();
                pickClickButton = LEFT;
                canvas.display();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragPoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        // get the delta of the mouse movement
        double deltaX = e.getPoint().x - dragPoint.x;
        double deltaY = e.getPoint().y - dragPoint.y;
        // normalize it to a percentage of the canvas
        deltaX /= canvas.getWidth() <= 0 ? 1f : (double) canvas.getWidth();
        deltaY /= canvas.getHeight() <= 0 ? 1f : (double) canvas.getHeight();
        // panning should be less sensative the more you are zoomed in
        deltaX *= MAXZOOM - (Math.min(zoom, .4d));
        deltaY *= MAXZOOM - (Math.min(zoom, .4d));
        // set the panning variables, reset drag point variable
        panX -= 200d * deltaX;
        panY += 200d * deltaY;
        dragPoint = e.getPoint();
        // perform the pan
        canvas.display();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // perform zoom operations
        zoom -= .02 * e.getWheelRotation();
        zoom = Math.max(MINZOOM, zoom);
        zoom = Math.min(zoom, MAXZOOM);
        canvas.display();
    }

    /**
     * Give the radius of inner circles, if n amount are placed on outside ring.
     * @param n Number of inner circles on outside ring.
     * @return The maximum radius to safely place the rings.
     */
    private float rad(int n) {
        // special case
        if (n == 1) {
            return (2f / 3f);
        }

        // normal case
        float t = (float) Math.sin(Math.PI / n);
        return (t / (t + 1));
    }

    private int radi(float bigR, float r) {
        float error = .0001f;
        float t = 1f / ((bigR / r) - 1f);

        // special case 1
        if (Math.abs(t) > 2 - error) {
            return 0;
        }

        // special case 2
        if (Math.abs(t) > 1 - error) {
            return 1;
        }

        // normal case
        return (int) Math.floor(error + (float) Math.PI / (float) Math.asin(t));
    }

    private ArrayList<Integer> calculateRings(int numOfComponents) {
        if (calculatedRings) {
            return layers;
        }

        Map<Float, ArrayList<Integer>> bestLayers = new TreeMap<Float, ArrayList<Integer>>();
        ArrayList<Integer> list = new ArrayList<Integer>();

        if (numOfComponents < 9) {
            list.add(numOfComponents);
            layers = list;
            calculatedRings = true;
            return list;
        }
        
        boolean isPossible;
        int compUsed;
        float radius;
        float bigR = 1f;
        for (int rings = 1; rings <= numOfComponents / 2; rings++) {
            for (int n = numOfComponents; n >= 1; n--) {
                isPossible = true;
                bigR = 1f;
                compUsed = 0;
                radius = rad(n);
                compUsed += n;
                bigR -= 2f * radius;
                list = new ArrayList<Integer>();
                list.add(compUsed);
                for (int r = rings - 1; r >= 1; r--) {
                    if (bigR < radius / 2) {
                        isPossible = false;
                        break;
                    }
                    if (compUsed >= numOfComponents) {
                        isPossible = false;
                        break;
                    }
                    int p = radi(bigR, radius);
                    if (p == 0) {
                        isPossible = false;
                        break;
                    }
                    if (p + compUsed >= numOfComponents) {
                        list.add(numOfComponents - compUsed);
                        compUsed += numOfComponents - compUsed;
                        break;
                    }
                    compUsed += p;
                    list.add(p);
                    bigR -= 2 * radius;
                }
                if (compUsed < numOfComponents) {
                    isPossible = false;
                }
                if (isPossible) {
                    bestLayers.put(radius, list);
                }
            }
        }

        Iterator it = bestLayers.keySet().iterator();
        while (it.hasNext()) {
            layers = bestLayers.get((Float) it.next());
        }

        calculatedRings = true;
        return layers;
    }
    
    private int findFirstNotReady(List<Component> sortedComponents) {
        int i=0;
        for (; i<sortedComponents.size(); i++) {
            if (!sortedComponents.get(i).isReady(currentMission)) {
                return i;
            }
        }
        return i;
    }
    
    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private synchronized int getID() {
        return ++idCounter;
    }

    private synchronized List<Component> getSortedComponents(Set<Component> components) {
        Component.SORT_OBJECTIVE = currentMission;
        List<Component> sortedComponents = new ArrayList<Component>(components);
        Collections.sort(sortedComponents);
        Component.SORT_OBJECTIVE = "";
        return sortedComponents;
    }
}
