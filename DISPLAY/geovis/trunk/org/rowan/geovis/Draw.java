package org.rowan.geovis;

import static java.lang.Math.*;
import static org.rowan.geovis.DrawSettings.*;

import java.util.List;
import java.util.LinkedList;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.nio.*;
import com.sun.opengl.util.*;

import org.rowan.linalgtoolkit.*;
import org.rowan.linalgtoolkit.shapes2d.*;
import org.rowan.linalgtoolkit.shapes3d.*;
import org.rowan.linalgtoolkit.transform3d.*;

/**
 * The <code>Draw</code> class provides drawing logic for the shapes2d and shapes3d
 * packages.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */

public final class Draw {
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** 
     * The minimum sum of the RGB values generated for a shape's color. This value
     * effectively determines minimum brightness of the color generated. The higher
     * the value, the brighter the color. A value of 3.0 or greater will limit all
     * shapes to the color white.
     */
    public static final float RGB_MIN_SUM = 1.0f;
    
    
    
    /** 
     * Indicates whether a point should be drawn as a circular shape rather than 
     * the standard OpenGL point primitive. 
     */
    public static final boolean POINT_AS_SHAPE = true;
    
    /** 
     * Indicates whether a line segment should be drawn as a complex shape 
     * rather than the standard OpenGL line primitive. 
     */
    public static final boolean SEGMENT_AS_SHAPE = true;
    
    
    
    
    /** The color used to draw bounding boxes. */
    public static final double[] BOUNDING_BOX_COLOR = {1.0, 1.0, 1.0};
    
    
    /** The relative scale to draw labels. */
    public static final double LABEL_SCALE = 0.5;
    
    /** The size of label points. */
    public static final double LABEL_POINT_SIZE = 0.04      * LABEL_SCALE;
    
    /** The width of label staves. */
    public static final double LABEL_STAFF_WIDTH = 0.01     * LABEL_SCALE;
    
    /** The length of label staves. */
    public static final double LABEL_STAFF_LENGTH = 0.1     * LABEL_SCALE;
    
    /** The scale at which label text should be drawn. */
    public static final double LABEL_TEXT_SCALE = 0.0003    * LABEL_SCALE;
    
    /** The padding between text and flag ends. */
    public static final double LABEL_TEXT_PADDING = LABEL_STAFF_WIDTH*2;
    
    /** The color used to draw label points. */
    public static final double[] LABEL_POINT_COLOR = {1.0, 1.0, 1.0};
    
    /** The color used to draw label staves. */
    public static final double[] LABEL_STAFF_COLOR = {0.5, 0.0, 0.0};
    
    /** The color used to draw label text. */
    public static final double[] LABEL_TEXT_COLOR = {1.0, 1.0, 1.0};
    
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** A GLUT utility instance used for drawing ease. */
    private static final GLUT glut = new GLUT();
    
    /** A GLU utility instance used for drawing ease. */
    private static final GLU glu = new GLU();
    
    
    /*********************************************
     * MARK: 2D Shapes
     *********************************************/
    
    /**
     * Draws a given 2D vertex using a given rendering pipeline. The vertex will be 
     * drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw the given 
     *                  vertex.
     * @param vertex    The 2D vertex to be drawn.
     */
    public static void draw(GL gl, Vector2D vertex) {
        // draw point
        if (!POINT_AS_SHAPE) {
            gl.glBegin(GL.GL_POINTS);
            {
                vertex(gl, vertex);
            }
            gl.glEnd();
        }
        
        // draw point as circle
        else {
            
            gl.glPushMatrix();
            {
                // create quadric
                GLUquadric quadric = glu.gluNewQuadric();
                glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
                
                // apply transformation
                gl.glTranslated(vertex.getX(), vertex.getY(), 0.0);
                
                // draw point
                glu.gluDisk(quadric, 0.0, POINT_SIZE/2, CIRCLE_SLICES, CIRCLE_SLICES);
            }
            gl.glPopMatrix();
        }
    }
    
    /**
     * Draws a given 2D bounding box using a given rendering pipeline. 
     * @param gl    The OpenGL rendering pipeline with which to draw the given 
     *              bounding box.
     * @param bb    The 2D bounding box to be drawn.
     */
    public static void draw(GL gl, BoundingBox2D bb) {
        // define bounding box vertices
        Vector2D a = bb.getA();
        Vector2D b = bb.getB();
        Vector2D c = new Vector2D(a.getX(), b.getY());
        Vector2D d = new Vector2D(b.getX(), a.getY());
        
        // set color
        gl.glColor3d(BOUNDING_BOX_COLOR[0], 
                     BOUNDING_BOX_COLOR[1], 
                     BOUNDING_BOX_COLOR[2]);
        
        // set line width to 1
        gl.glLineWidth(1);
        
        // draw bounding box
        gl.glBegin(GL.GL_LINE_LOOP);
        {
            vertex(gl, a);
            vertex(gl, c);
            vertex(gl, b);
            vertex(gl, d);
        }
        gl.glEnd();
        
        // reset line width
        gl.glLineWidth(LINE_WIDTH);
    }

    /**
     * Draws a given 2D shape using a given rendering pipeline. A random drawing 
     * color will be generated from the shape's unique hash code.
     * @param gl    The OpenGL rendering pipeline with which to draw the given 
     *              shape.
     * @param shape The 2D shape to be drawn.
     */
    public static void draw(GL gl, Shape2D shape) {
        // set color
        setColor(gl, shape);
        
        // draw shape
        if (shape instanceof Point2D)
            draw(gl, (Point2D)shape);
        
        else if (shape instanceof Segment2D)
            draw(gl, (Segment2D)shape);
        
        else if (shape instanceof Circle2D)
            draw(gl, (Circle2D)shape);
        
        else if (shape instanceof Polygon2D)
            draw(gl, (Polygon2D)shape);
        
        else if (shape instanceof ComplexShape2D)
            draw(gl, (ComplexShape2D)shape);
        
        else
            System.out.println("Given 2D Shape cannot be drawn");
    }
    
    /**
     * Draws a given 2D point using a given rendering pipeline. The point will 
     * be drawn using the current foreground color.
     * @param gl    The OpenGL rendering pipeline with which to draw the given 
     *              point.
     * @param point The 2D point to be drawn.
     */
    public static void draw(GL gl, Point2D point) {
        // draw point
        draw(gl, point.getPosition());
    }
    
    /**
     * Draws a given 2D line segment using a given rendering pipeline. The line 
     * segment will be drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw the given 
     *                  line segment.
     * @param segment   The 2D line segment to be drawn.
     */
    public static void draw(GL gl, Segment2D segment) {
        // get segment endpoints
        Vector2D start = segment.getStart();
        Vector2D end = segment.getEnd();
        
        gl.glPushMatrix();
        {
            // apply transformation
            Vector2D position = segment.getPosition();
            gl.glTranslated(position.getX(), position.getY(), 0);
            
            // draw segment
            if (!SEGMENT_AS_SHAPE) {
                gl.glBegin(GL.GL_LINES);
                {
                    vertex(gl, start);
                    vertex(gl, end);
                }
                gl.glEnd();
            }
            
            // draw segment as shape
            else {                
                
                gl.glPushMatrix();
                {
                    // rotate
                    Vector2D v1 = start.unitVector();
                    Vector2D v2 = Vector2D.X_AXIS;
                    double angle = acos(v2.dot(v1));
                    if (start.getY() > 0)
                        angle = -angle;
                    gl.glRotated(toDegrees(angle), 0.0, 0.0, -1.0);
                    
                    // create quadric
                    GLUquadric quadric = glu.gluNewQuadric();
                    glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
                                        
                    // draw segment
                    gl.glPushMatrix();
                    {
                        Vector2D p1 = new Vector2D(-segment.length()/2 + LINE_WIDTH/2, -LINE_WIDTH/2);
                        Vector2D p2 = new Vector2D(segment.length()/2 - LINE_WIDTH/2, LINE_WIDTH/2);
                        gl.glRectd(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                    }
                    gl.glPopMatrix();
                    
                    // draw curved end 1
                    gl.glPushMatrix();
                    {
                        gl.glTranslated(segment.length()/2 - LINE_WIDTH/2, 0.0, 0);
                        glu.gluDisk(quadric, 0.0, LINE_WIDTH/2, 
                                    CIRCLE_SLICES, CIRCLE_SLICES);
                    }
                    gl.glPopMatrix();
                    
                    // draw curved end 2
                    gl.glPushMatrix();
                    {
                        gl.glTranslated(-segment.length()/2 + LINE_WIDTH/2, 0.0, 0);
                        glu.gluDisk(quadric, 0.0, LINE_WIDTH/2, 
                                    CIRCLE_SLICES, CIRCLE_SLICES);
                    }
                    gl.glPopMatrix();
                }
                gl.glPopMatrix();
            }

        }
        gl.glPopMatrix();
    }
    
    /**
     * Draws a given 2D circle using a given rendering pipeline. The circle will 
     * be drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw the given 
     *                  circle.
     * @param circle    The 2D circle to be drawn.
     */
    public static void draw(GL gl, Circle2D circle) {
        // get center point and radius
        Vector2D center = circle.getCenter();
        double radius = circle.getRadius();
        
        gl.glPushMatrix(); 
        {
            // create quadric
            GLUquadric quadric = glu.gluNewQuadric();
            glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
            
            // apply transformation
            gl.glTranslated(center.getX(), center.getY(), 0.0);
            
            // draw circle
            glu.gluDisk(quadric, 0.0, radius, CIRCLE_SLICES, CIRCLE_SLICES);
        } 
        gl.glPopMatrix();
    }
    
    /**
     * Draws a given 2D polygon using a given rendering pipeline. The polygon 
     * will be drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw the given 
     *                  polygon.
     * @param polygon   The 2D polygon to be drawn.
     */
    public static void draw(GL gl, Polygon2D polygon) {
        // get position
        Vector2D position = polygon.getPosition();
        
        // enable vertex and normal arrays
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
        
        // get and reverse vertex list
        LinkedList<Vector2D> verts = new LinkedList<Vector2D>();
        for (Vector2D vert : polygon.getVertices())
            verts.add(0, vert);
        
        // create vertex buffer and load vertices
        gl.glVertexPointer(2, GL.GL_DOUBLE, 0, vertBuffer2D(verts));
        
        // create normal buffer and load normals
        gl.glNormalPointer(GL.GL_DOUBLE, 0, normalBuffer2D(polygon.vertCount()));
        
        gl.glPushMatrix(); 
        {
            // apply transformation
            gl.glTranslated(position.getX(), position.getY(), 0.0);
            
            // draw circle
            gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, polygon.vertCount());
        } 
        gl.glPopMatrix();
        
        // disable vertex and normal arrays
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
    }
    
    /**
     * Draws a given 2D complex shape using a given rendering pipeline. The complex 
     * shape will be drawn using the current foreground color.
     * @param gl            The OpenGL rendering pipeline with which to draw the 
     *                      given polygon.
     * @param complexShape  The 2D complex shape to be drawn.
     */
    public static void draw(GL gl, ComplexShape2D complexShape) {
        // draw each subshape of the complex shape
        for (Shape2D subShape : complexShape.getSubShapes()) {
            
            // point?
            if (subShape instanceof Point2D)
                draw(gl, (Point2D)subShape);
            
            // segment?
            if (subShape instanceof Segment2D)
                draw(gl, (Segment2D)subShape);
            
            // circle?
            if (subShape instanceof Circle2D)
                draw(gl, (Circle2D)subShape);
            
            // polygon?
            if (subShape instanceof Polygon2D)
                draw(gl, (Polygon2D)subShape);
        }
    }
    
    
    /*********************************************
     * MARK: 3D Shapes
     *********************************************/
    
    /**
     * Draws a given 3D vertex using a given rendering pipeline. The vertex will 
     * be drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw the given 
     *                  vertex.
     * @param vertex    The 3D vertex to be drawn.
     */
    public static void draw(GL gl, Vector3D vertex) {
        // draw point
        if (!POINT_AS_SHAPE) {
            gl.glBegin(GL.GL_POINTS);
            {
                vertex(gl, vertex);
            }
            gl.glEnd();
        }
        
        // draw point as sphere
        else {
            gl.glPushMatrix();
            {
                gl.glTranslated(vertex.getX(), vertex.getY(), vertex.getZ());
                glut.glutSolidSphere(POINT_SIZE/2, 20, 20);
            }
            gl.glPopMatrix();
        }
    }
    
    /**
     * Draws a given 3D bounding box using a given rendering pipeline. 
     * @param gl    The OpenGL rendering pipeline with which to draw the given 
     *              bounding box.
     * @param bb    The 3D bounding box to be drawn.
     */
    public static void draw(GL gl, BoundingBox3D bb) {
        // get bounding box vertices
        List<Vector3D> corners = bb.corners();
        
        // set color
        gl.glColor3d(BOUNDING_BOX_COLOR[0], 
                     BOUNDING_BOX_COLOR[1], 
                     BOUNDING_BOX_COLOR[2]);
        
        // set line width to 1
        gl.glLineWidth(1);
        
        // draw bounding box
        gl.glBegin(GL.GL_LINE_LOOP);
        {
            vertex(gl, corners.get(0));
            vertex(gl, corners.get(3));
            vertex(gl, corners.get(7));
            vertex(gl, corners.get(6));
            vertex(gl, corners.get(5));
            vertex(gl, corners.get(4));
            vertex(gl, corners.get(2));
            vertex(gl, corners.get(1));
        }
        gl.glEnd();
        gl.glBegin(GL.GL_LINES);
        {
            vertex(gl, corners.get(3));
            vertex(gl, corners.get(2));
            vertex(gl, corners.get(6));
            vertex(gl, corners.get(0));
            vertex(gl, corners.get(5));
            vertex(gl, corners.get(1));
            vertex(gl, corners.get(7));
            vertex(gl, corners.get(4));
        }
        gl.glEnd();
        
        // reset line width
        gl.glLineWidth(LINE_WIDTH);
    }
    
    /**
     * Draws a given 3D shape using a given rendering pipeline. A random drawing 
     * color will be generated from the shape's unique hash code
     * @param gl    The OpenGL rendering pipeline with which to draw the given 
     *              shape.
     * @param shape The 3D shape to be drawn.
     */
    public static void draw(GL gl, Shape3D shape) {
        // set color
        setColor(gl, shape);
        
        // draw shape
        if (shape instanceof Point3D)
            draw(gl, (Point3D)shape);
        
        else if (shape instanceof Segment3D)
            draw(gl, (Segment3D)shape);
        
        else if (shape instanceof Cylinder3D)
            draw(gl, (Cylinder3D)shape);
        
        else if (shape instanceof Cone3D)
            draw(gl, (Cone3D)shape);
        
        else if (shape instanceof Polyhedron3D)
            draw(gl, (Polyhedron3D)shape);
        
        else if (shape instanceof Ellipsoid3D)
            draw(gl, (Ellipsoid3D)shape);
        
        else
            System.out.println("Given 3D Shape cannot be drawn");
    }
    
    /**
     * Draws a given 3D point using a given rendering pipeline. The point will 
     * be drawn using the current foreground color.
     * @param gl    The OpenGL rendering pipeline with which to draw the given 
     *              point.
     * @param point The 3D point to be drawn.
     */
    public static void draw(GL gl, Point3D point) {
        // draw point
        draw(gl, point.getPosition());
    }
    
    /**
     * Draws a given 3D line segment using a given rendering pipeline. The line 
     * segment will be drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw the given 
     *                  line segment.
     * @param segment   The 3D line segment to be drawn.
     */
    public static void draw(GL gl, Segment3D segment) {
        // get segment endpoints
        Vector3D start = segment.getStart();
        Vector3D end = segment.getEnd();
        
        // draw segment
        if (!SEGMENT_AS_SHAPE) {
            gl.glBegin(GL.GL_LINES);
            {
                vertex(gl, segment.toWorld(start));
                vertex(gl, segment.toWorld(end));
            }
            gl.glEnd();
        }
        
        // draw segment as shape
        else {
            
            gl.glPushMatrix();
            {
                // translate
                Vector3D position = segment.getPosition();
                gl.glTranslated(position.getX(), position.getY(), position.getZ());
                
                // rotate
                if (!segment.isParallel(Vector3D.X_AXIS)) {
                    Vector3D v1 = start.unitVector();
                    Vector3D v2 = Vector3D.X_AXIS;
                    double angle = acos(v2.dot(v1));
                    Vector3D axis = v2.cross(v1);
                    gl.glRotated(toDegrees(angle), axis.getX(), axis.getY(), axis.getZ());
                }
                
                // create quadric 
                GLUquadric quadric = glu.gluNewQuadric();
                glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
                
                // define clipping plane equations
                double[] clipEqArray1 = {LINE_WIDTH, 0.0, 0.0, 1.0};
                double[] clipEqArray2 = {-LINE_WIDTH, 0.0, 0.0, 1.0};
                
                // create equation buffers
                DoubleBuffer clipEq1 = BufferUtil.newDoubleBuffer(clipEqArray1.length);
                clipEq1.put(clipEqArray1);
                clipEq1.rewind();
                DoubleBuffer clipEq2 = BufferUtil.newDoubleBuffer(clipEqArray2.length);
                clipEq2.put(clipEqArray2);
                clipEq2.rewind();
                
                // 1st hemisphere
                gl.glPushMatrix();
                {
                    gl.glTranslated((segment.length() - LINE_WIDTH)/2, 0.0, 0.0);
                    gl.glRotated(90, 0.0, 1.0, 0.0);
                    gl.glEnable(GL.GL_CLIP_PLANE0);
                    gl.glClipPlane(GL.GL_CLIP_PLANE0, clipEq1);
                    glut.glutSolidSphere(POINT_SIZE/2, CIRCLE_SLICES, CIRCLE_SLICES);
                    gl.glDisable(GL.GL_CLIP_PLANE0);
                }
                gl.glPopMatrix();
                
                // 2nd hemisphere
                gl.glPushMatrix();
                {
                    gl.glTranslated(-(segment.length() - LINE_WIDTH)/2, 0.0, 0.0);
                    gl.glRotated(90, 0.0, 1.0, 0.0);
                    gl.glEnable(GL.GL_CLIP_PLANE0);
                    gl.glClipPlane(GL.GL_CLIP_PLANE0, clipEq2);
                    glut.glutSolidSphere(POINT_SIZE/2, CIRCLE_SLICES, CIRCLE_SLICES);
                    gl.glDisable(GL.GL_CLIP_PLANE0);
                }
                gl.glPopMatrix();
                
                // cylinder
                gl.glPushMatrix();
                {
                    gl.glTranslated(-(segment.length() - LINE_WIDTH)/2, 0.0, 0);
                    gl.glRotated(90.0, 0.0, 1.0, 0.0);
                    glu.gluCylinder(quadric, LINE_WIDTH/2, LINE_WIDTH/2, 
                                    segment.length() - LINE_WIDTH, 
                                    CIRCLE_SLICES, CIRCLE_SLICES);
                }
                gl.glPopMatrix();
            }
            gl.glPopMatrix();
        }
    }
    
    /**
     * Draws a given 3D cone using a given rendering pipeline. The cone will be 
     * drawn using the current foreground color.
     * @param gl    The OpenGL rendering pipeline with which to draw the given 
     *              cone.
     * @param cone  The 3D cone to be drawn.
     */
    public static void draw(GL gl, Cone3D cone) {
        // store cone base and apex
        Vector3D apex = cone.getApex();
        Vector3D base = cone.getBaseCenter();
        
        gl.glPushMatrix();
        {
            // create quadric
            GLUquadric quadric = glu.gluNewQuadric();
            glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
            
            // translate
            Vector3D position = cone.getPosition();
            gl.glTranslated(position.getX(), position.getY(), position.getZ());
            
            // rotate
            if (!cone.axis().isParallel(Vector3D.X_AXIS)) {
                Vector3D v1 = apex.unitVector();
                Vector3D v2 = Vector3D.X_AXIS;
                double angle = acos(v2.dot(v1));
                Vector3D axis = v2.cross(v1);
                gl.glRotated(toDegrees(angle), axis.getX(), axis.getY(), axis.getZ());
            }
            
            // draw cone
            gl.glPushMatrix();
            {
                gl.glTranslated(-cone.length()/2, 0.0, 0);
                gl.glRotated(90.0, 0.0, 1.0, 0.0);
                glut.glutSolidCone(cone.getBaseRadius(), cone.length(), 
                                   CIRCLE_SLICES, CIRCLE_SLICES);
            }
            gl.glPopMatrix();
            
            // draw base
            gl.glPushMatrix();
            {
                gl.glTranslated(-cone.length()/2, 0.0, 0);
                gl.glRotated(270.0, 0.0, 1.0, 0.0);
                glu.gluDisk(quadric, 0.0, cone.getBaseRadius(), 
                            CIRCLE_SLICES, CIRCLE_SLICES);
            }
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }
    
    /**
     * Draws a given 3D cylinder using a given rendering pipeline. The cylinder 
     * will be drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw the given 
     *                  cylinder.
     * @param cylinder  The 3D cylinder to be drawn.
     */
    public static void draw(GL gl, Cylinder3D cylinder) {
        // store cylinder base and apex
        Vector3D apex = cylinder.getApexCenter();
        Vector3D base = cylinder.getBaseCenter();
        
        gl.glPushMatrix();
        {
            // create quadric
            GLUquadric quadric = glu.gluNewQuadric();
            glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
            
            // translate
            Vector3D position = cylinder.getPosition();
            gl.glTranslated(position.getX(), position.getY(), position.getZ());
            
            // rotate
            if (!cylinder.axis().isParallel(Vector3D.X_AXIS)) {
                Vector3D v1 = apex.unitVector();
                Vector3D v2 = Vector3D.X_AXIS;
                double angle = acos(v2.dot(v1));
                Vector3D axis = v2.cross(v1);
                gl.glRotated(toDegrees(angle), axis.getX(), axis.getY(), axis.getZ());
            }
            
            // draw cylinder
            gl.glPushMatrix();
            {
                gl.glTranslated(-cylinder.length()/2, 0.0, 0);
                gl.glRotated(90.0, 0.0, 1.0, 0.0);
                glu.gluCylinder(quadric, cylinder.getBaseRadius(), 
                                cylinder.getApexRadius(), cylinder.length(), 
                                CIRCLE_SLICES, CIRCLE_SLICES);
            }
            gl.glPopMatrix();
            
            // draw base 2
            gl.glPushMatrix();
            {
                gl.glTranslated(cylinder.length()/2, 0.0, 0);
                gl.glRotated(90.0, 0.0, 1.0, 0.0);
                glu.gluDisk(quadric, 0.0, cylinder.getApexRadius(), 
                            CIRCLE_SLICES, CIRCLE_SLICES);
            }
            gl.glPopMatrix();
            
            // draw base 2
            gl.glPushMatrix();
            {
                gl.glTranslated(-cylinder.length()/2, 0.0, 0);
                gl.glRotated(270.0, 0.0, 1.0, 0.0);
                glu.gluDisk(quadric, 0.0, cylinder.getBaseRadius(), 
                            CIRCLE_SLICES, CIRCLE_SLICES);
            }
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }
    
    /**
     * Draws a given 3D face using a given rendering pipeline. The face will be 
     * drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw 
     *                  the given face.
     * @param face      The 3D face to be drawn.
     */
    public static void draw(GL gl, Face3D face) {
        // calculate normal vector
        Vector3D v1 = face.getVertex(1).subtract(face.getVertex(0));
        Vector3D v2 = face.getVertex(2).subtract(face.getVertex(0));
        Vector3D normal = v1.cross(v2).unitVector();
        
        gl.glBegin(GL.GL_POLYGON);
        {
            for (Vector3D vert : face.getVertices()) {
                normal(gl, normal);
                vertex(gl, vert);
            }
        }
        gl.glEnd();
    }
    
    /**
     * Draws a given 3D polyhedron using a given rendering pipeline. The polyhedron 
     * will be drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw the given 
     *                  polyhedron.
     * @param poly      The 3D polyhedron to be drawn.
     */
    public static void draw(GL gl, Polyhedron3D poly) {
        // get polyhedron's position
        Vector3D position = poly.getPosition();
        
        gl.glPushMatrix();
        {
            // translate
            gl.glTranslated(position.getX(), position.getY(), position.getZ());
            
            // draw each polyhedron face
            for (Face3D face : poly.getFaces())
                draw(gl, face);
        }
        gl.glPopMatrix();
    }
    
    /**
     * Draws a given 3D ellipsoid using a given rendering pipeline. The ellipsoid 
     * will be drawn using the current foreground color.
     * @param gl        The OpenGL rendering pipeline with which to draw 
     *                  the given ellipsoid.
     * @param ellipsoid The 3D ellipsoid to be drawn.
     */
    public static void draw(GL gl, Ellipsoid3D ellipsoid) {
        // get ellipsoid's position
        Vector3D position = ellipsoid.getPosition();
        
        gl.glPushMatrix();
        {
            
            // determine scaling from sphere to ellipsoid
            double majorRad = ellipsoid.getMajorRadius();
            double minorRad = ellipsoid.getMinorRadius();
            double intRad = ellipsoid.getIntermediateRadius();
            double scaleX = 1.0;
            double scaleY = minorRad / majorRad;
            double scaleZ = intRad / majorRad;
            
            // determine roatation
            Rotation rotation = new Rotation(ellipsoid.getMinorAxis().unitVector(),
                                             ellipsoid.getIntermediateAxis().unitVector());
            double angle = rotation.getAngle();
            Vector3D axis = rotation.getAxis();
            
            // apply transformations
            gl.glTranslated(position.getX(), position.getY(), position.getZ());
            gl.glRotated(toDegrees(angle), axis.getX(), axis.getY(), axis.getZ());
            gl.glScaled(scaleX, scaleY, scaleZ);
            
            // draw ellipsoid
            glut.glutSolidSphere(majorRad, CIRCLE_SLICES, CIRCLE_SLICES);
        }
        gl.glPopMatrix();
    }
    
    
    /*********************************************
     * MARK: Labels
     *********************************************/
    
    /**
     * Draws a given label using a given rendering pipeline.
     * @param gl    The OpenGL rendering pipeline with which to draw the given 
     *              label.
     * @param label The label to be drawn.
     */
    public static void draw(GL gl, Label label) {
        // draw label point
        drawLabelPoint(gl, label);
        
        // draw label staff
        drawLabelStaff(gl, label);
        
        // draw label flag
        drawLabelFlag(gl, label);
        
        // draw label text
        drawLabelText(gl, label);
    }
    
    /**
     * Draws the point for a given label, using a given rendering pipeline.
     * @param gl    The OpenGL rendering pipeline with which to draw the point.
     * @param label The label for which the point will be drawn.
     */
    public static void drawLabelPoint(GL gl, Label label) {
        // store label position for easy access
        Vector3D position = label.getPosition();
        
        
        // set color for drawing label point
        gl.glColor3dv(LABEL_POINT_COLOR, 0);
        
        // draw label point
        gl.glPushMatrix();
        {
            gl.glTranslated(position.getX(), position.getY(), position.getZ());
            glut.glutSolidSphere(LABEL_SCALE*LABEL_POINT_SIZE/2, CIRCLE_SLICES, CIRCLE_SLICES);
        }
        gl.glPopMatrix();
    }
        
    /**
     * Draws the staff for a given label, using a given rendering pipeline.
     * @param gl    The OpenGL rendering pipeline with which to draw the staff.
     * @param label The label for which the staff will be drawn.
     */
    public static void drawLabelStaff(GL gl, Label label) {
        // store label position for easy access
        Vector3D position = label.getPosition();
        
        // compute start and end points relative to center point
        Vector3D start = position;
        Vector3D end = position.add(new Vector3D(position, LABEL_STAFF_LENGTH));
        Vector3D delta = end.subtract(start);
        Vector3D center = start.add(delta.multiply(0.5));
        start = start.subtract(center);
        end = end.subtract(center);
        
        
        // set color for drawing label staff and flag
        gl.glColor3dv(LABEL_STAFF_COLOR, 0);
        
        gl.glPushMatrix();
        {
            // translate
            gl.glTranslated(center.getX(), center.getY(), center.getZ());
            
            // rotate
            if (!delta.isParallel(Vector3D.X_AXIS)) {
                Vector3D v1 = start.unitVector();
                Vector3D v2 = Vector3D.X_AXIS;
                double angle = acos(v2.dot(v1));
                Vector3D axis = v2.cross(v1);
                gl.glRotated(toDegrees(angle), axis.getX(), axis.getY(), axis.getZ());
            }
            
            // create quadric 
            GLUquadric quadric = glu.gluNewQuadric();
            glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
            
            // define clipping plane equations
            double[] clipEqArray = {-LABEL_STAFF_WIDTH, 0.0, 0.0, 1.0};
            
            // create equation buffers
            DoubleBuffer clipEq = BufferUtil.newDoubleBuffer(clipEqArray.length);
            clipEq.put(clipEqArray);
            clipEq.rewind();
            
            // hemisphere
            gl.glPushMatrix();
            {
                gl.glTranslated((delta.magnitude() - LABEL_STAFF_WIDTH)/2, 0.0, 0.0);
                gl.glRotated(90, 0.0, 1.0, 0.0);
                gl.glEnable(GL.GL_CLIP_PLANE0);
                gl.glClipPlane(GL.GL_CLIP_PLANE0, clipEq);
                glut.glutSolidSphere(LABEL_STAFF_WIDTH/2, CIRCLE_SLICES, CIRCLE_SLICES);
                gl.glDisable(GL.GL_CLIP_PLANE0);
            }
            gl.glPopMatrix();
            
            // cylinder
            gl.glPushMatrix();
            {
                gl.glTranslated(-(delta.magnitude() - LABEL_STAFF_WIDTH)/2, 0.0, 0);
                gl.glRotated(90.0, 0.0, 1.0, 0.0);
                glu.gluCylinder(quadric, LABEL_STAFF_WIDTH/2, LABEL_STAFF_WIDTH/2, 
                                delta.magnitude() - LABEL_STAFF_WIDTH, 
                                CIRCLE_SLICES, CIRCLE_SLICES);
            }
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }
    
    /**
     * Draws the flag for a given label, using a given rendering pipeline.
     * @param gl    The OpenGL rendering pipeline on which to draw the flag.
     * @param label The label for which the flag will be drawn.
     */
    public static void drawLabelFlag(GL gl, Label label) {
        // store label position for easy access
        Vector3D position = label.getPosition();
                
        // compute flag length
        double flagLength = LABEL_TEXT_PADDING*3 + LABEL_TEXT_SCALE * glut.glutStrokeLength(GLUT.STROKE_ROMAN, label.getText());
        
        // compute start and end points
        Vector3D staffEnd = position.add(new Vector3D(position, LABEL_STAFF_LENGTH));
        Vector3D cross = Vector3D.X_AXIS.cross(staffEnd);
        if (cross.isZeroVector())
            cross = Vector3D.Y_AXIS.cross(staffEnd);        
        
        Vector3D flagStart = staffEnd;
        flagStart = flagStart.subtract(new Vector3D(cross, LABEL_STAFF_WIDTH/2));
        flagStart = flagStart.subtract(new Vector3D(position, LABEL_STAFF_WIDTH/2));

        Vector3D flagEnd = flagStart.add(new Vector3D(cross, flagLength));
        Vector3D delta = flagEnd.subtract(flagStart);
        Vector3D center = flagStart.add(delta.multiply(0.5));
        Vector3D relativeStart = flagStart.subtract(center);
        Vector3D relativeEnd = flagEnd.subtract(center);
        
        
        // set color for drawing label staff and flag
        gl.glColor3dv(LABEL_STAFF_COLOR, 0);
        
        gl.glPushMatrix();
        {
            // translate
            gl.glTranslated(center.getX(), center.getY(), center.getZ());
            
            // rotate
            if (!delta.isParallel(Vector3D.X_AXIS)) {
                Vector3D v1 = relativeStart.unitVector();
                Vector3D v2 = Vector3D.X_AXIS;
                double angle = acos(v2.dot(v1));
                Vector3D axis = v2.cross(v1);
                gl.glRotated(toDegrees(angle), axis.getX(), axis.getY(), axis.getZ());
            }
            
            // create quadric 
            GLUquadric quadric = glu.gluNewQuadric();
            glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
            
            // define clipping plane equations
            double[] clipEqArray1 = {LABEL_STAFF_WIDTH, 0.0, 0.0, 1.0};
            double[] clipEqArray2 = {-LABEL_STAFF_WIDTH, 0.0, 0.0, 1.0};
            
            // create equation buffers
            DoubleBuffer clipEq1 = BufferUtil.newDoubleBuffer(clipEqArray1.length);
            clipEq1.put(clipEqArray1);
            clipEq1.rewind();
            DoubleBuffer clipEq2 = BufferUtil.newDoubleBuffer(clipEqArray2.length);
            clipEq2.put(clipEqArray2);
            clipEq2.rewind();
            
            // 1st hemisphere
            gl.glPushMatrix();
            {
                gl.glTranslated(-(delta.magnitude() - LABEL_STAFF_WIDTH)/2, 0.0, 0.0);
                gl.glRotated(90, 0.0, 1.0, 0.0);
                gl.glEnable(GL.GL_CLIP_PLANE0);
                gl.glClipPlane(GL.GL_CLIP_PLANE0, clipEq1);
                glut.glutSolidSphere(LABEL_STAFF_WIDTH/2, CIRCLE_SLICES, CIRCLE_SLICES);
                gl.glDisable(GL.GL_CLIP_PLANE0);
            }
            gl.glPopMatrix();
            
            // 2nd hemisphere
            gl.glPushMatrix();
            {
                gl.glTranslated((delta.magnitude() - LABEL_STAFF_WIDTH)/2, 0.0, 0.0);
                gl.glRotated(90, 0.0, 1.0, 0.0);
                gl.glEnable(GL.GL_CLIP_PLANE0);
                gl.glClipPlane(GL.GL_CLIP_PLANE0, clipEq2);
                glut.glutSolidSphere(LABEL_STAFF_WIDTH/2, CIRCLE_SLICES, CIRCLE_SLICES);
                gl.glDisable(GL.GL_CLIP_PLANE0);
            }
            gl.glPopMatrix();
            
            // cylinder
            gl.glPushMatrix();
            {
                gl.glTranslated(-(delta.magnitude() - LABEL_STAFF_WIDTH)/2, 0.0, 0);
                gl.glRotated(90.0, 0.0, 1.0, 0.0);
                glu.gluCylinder(quadric, LABEL_STAFF_WIDTH/2, LABEL_STAFF_WIDTH/2, 
                                delta.magnitude() - LABEL_STAFF_WIDTH, 
                                CIRCLE_SLICES, CIRCLE_SLICES);
            }
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
        
        
        
        // get text height
        double textHeight = LABEL_TEXT_SCALE * 100;
        
        // create list of vertices
        LinkedList<Vector3D> verts = new LinkedList<Vector3D>();
        Vector3D v1 = flagStart.add(new Vector3D(cross, LABEL_TEXT_PADDING/2));
        Vector3D v2 = flagEnd.subtract(new Vector3D(cross, LABEL_TEXT_PADDING/2));
        Vector3D v3 = v2.add(new Vector3D(flagStart, textHeight + LABEL_TEXT_PADDING*2 + LABEL_STAFF_WIDTH));
        Vector3D v4 = v1.add(new Vector3D(flagStart, textHeight + LABEL_TEXT_PADDING*2 + LABEL_STAFF_WIDTH));
        verts.add(v1);
        verts.add(v2);
        verts.add(v3);
        verts.add(v4);
        
        // enable vertex and normal arrays
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
        
        // create vertex buffer and load vertices
        gl.glVertexPointer(3, GL.GL_DOUBLE, 0, vertBuffer3D(verts));
        
        // create normal buffer and load normals
        Vector3D normal = v2.subtract(v1).cross(v3.subtract(v2));
        double[] normalArray = new double[12];
        for (int i=0; i<4; i++) {
            normalArray[3*i + 0] = normal.getX();
            normalArray[3*i + 1] = normal.getY();
            normalArray[3*i + 2] = normal.getZ();
        }
        gl.glNormalPointer(GL.GL_DOUBLE, 0, vertBuffer(normalArray));
        
        gl.glPushMatrix(); 
        {            
            // draw circle
            gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, 4);
        } 
        gl.glPopMatrix();
        
        // disable vertex and normal arrays
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
    }
    
    /**
     * Draws the text for a given label, using a given rendering pipeline.
     * @param gl    The OpenGL rendering pipeline with which to draw the text.
     * @param label The label for which the text will be drawn.
     */
    public static void drawLabelText(GL gl, Label label) {
        // store label position for easy access
        Vector3D position = label.getPosition();
                
        // compute flag length
        double flagLength = LABEL_TEXT_PADDING*2 + LABEL_TEXT_SCALE * glut.glutStrokeLength(GLUT.STROKE_ROMAN, label.getText());
        
        
        // compute flag endpoints
        Vector3D flagStart = position.add(new Vector3D(position, LABEL_STAFF_LENGTH));
        Vector3D cross = Vector3D.X_AXIS.cross(flagStart);
        if (cross.isZeroVector())
            cross = Vector3D.Y_AXIS.cross(flagStart); 
        Vector3D flagEnd = flagStart.add(new Vector3D(cross, flagLength));
        
        // compute text position 
        Vector3D textPos = position.add(new Vector3D(position, LABEL_STAFF_LENGTH+LABEL_TEXT_PADDING+LABEL_STAFF_WIDTH/2));
        textPos = textPos.add(new Vector3D(cross, LABEL_TEXT_PADDING));
        textPos = textPos.add(new Vector3D(cross.cross(flagStart), 0.003));
        
        // compute flag delta
        Vector3D delta = flagEnd.subtract(flagStart);
        
        
        // set color for drawing label text
        gl.glColor3dv(LABEL_TEXT_COLOR, 0);
        
        // side 1
        gl.glPushMatrix();
        {
            // translate
            gl.glTranslated(textPos.getX(), textPos.getY(), textPos.getZ());
            
            // rotate
            Vector3D yAxis = flagStart.unitVector();
            Vector3D zAxis = delta.cross(yAxis);
            Rotation rotation = new Rotation(yAxis, zAxis);
            Vector3D rotAxis = rotation.getAxis();
            double rotAngle = rotation.getAngle();
            gl.glRotated(Math.toDegrees(rotAngle), rotAxis.getX(), rotAxis.getY(), rotAxis.getZ());
            
            // scale
            gl.glScaled(LABEL_TEXT_SCALE, LABEL_TEXT_SCALE, 0.0);
            
            // draw text
            glut.glutStrokeString(GLUT.STROKE_ROMAN, label.getText());
        }
        gl.glPopMatrix();
        
        // side 2
        gl.glPushMatrix();
        {
            // translate
            Vector3D pos = flagEnd.subtract(new Vector3D(cross, LABEL_TEXT_PADDING/2));
            pos = pos.add(new Vector3D(position, LABEL_TEXT_PADDING+LABEL_STAFF_WIDTH/2));
            pos = pos.add(new Vector3D(cross.cross(flagStart).inverse(), 0.003));
            gl.glTranslated(pos.getX(), pos.getY(), pos.getZ());
            
            // rotate
            Vector3D yAxis = flagStart.unitVector();
            Vector3D zAxis = delta.cross(yAxis);
            Rotation rotation = new Rotation(yAxis, zAxis.inverse());
            Vector3D rotAxis = rotation.getAxis();
            double rotAngle = rotation.getAngle();
            gl.glRotated(Math.toDegrees(rotAngle), rotAxis.getX(), rotAxis.getY(), rotAxis.getZ());
            
            // scale
            gl.glScaled(LABEL_TEXT_SCALE, LABEL_TEXT_SCALE, 0.0);
            
            // draw text
            glut.glutStrokeString(GLUT.STROKE_ROMAN, label.getText());
        }
        gl.glPopMatrix();
    }
     
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Sets the current color for a given rendering pipeline to a color generated 
     * using the hash code of a given object.
     * @param gl    The OpenGL rendering pipeline with which the color will be set.
     * @param o     The object for which the color will be set.
     */
    private static void setColor(GL gl, Object o) {
        // store absolute value of object's unique hash id
        int hash = Math.abs(o.hashCode());

        // generate RGB values
        int rInt = hash & 0xFF;
        hash = Integer.rotateLeft(new Integer(hash), 8);
        int gInt = hash & 0xFF;
        hash = Integer.rotateLeft(new Integer(hash), 8);
        int bInt = hash & 0xFF;
        
        // convert RGB values to the range [0, 1]
        float r = ((float)rInt) / 255;
        float g = ((float)gInt) / 255;
        float b = ((float)bInt) / 255;
        
        // make sure color isn't too dark
        if (r+g+b < RGB_MIN_SUM) {
            float dif = (1.0f - (r+g+b)) / 3.0f;
            r += dif;
            g += dif;
            b += dif;
        }
            
        // set color state in OpenGL engine
        gl.glColor3f(r, g, b);
    }
    
    /**
     * Calls the appropriate glNormal*() method on a given rendering pipeline to 
     * draw a vertex defined by a given Vector object.
     * @param gl        The OpenGL rendering pipeline with which the vertex to be 
     *                  drawn.
     * @param vertex    A vector describing the vertex to be drawn.
     */
    private static void vertex(GL gl, Vector vertex) {
        if (vertex instanceof Vector2D) {
            Vector2D vert = (Vector2D)vertex;
            gl.glVertex2d(vert.getX(), vert.getY());
        }
        
        if (vertex instanceof Vector3D) {
            Vector3D vert = (Vector3D)vertex;
            gl.glVertex3d(vert.getX(), vert.getY(), vert.getZ());
        }
    }
    
    /**
     * Calls the appropriate glVertex*() method on a given OpenGL rendering pipeline
     * to define a vertex normal using given vector.
     * @param gl        The OpenGL rendering pipeline with which the normal to be 
     *                  defined.
     * @param normal    A vector describing the normal to be defined.
     */
    private static void normal(GL gl, Vector3D normal) {
        gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
    }
        
    /**
     * Creates a vertex buffer for a given vertex array.
     * @param vertArray A vertex array.
     * @return          A <code>Buffer</code> object containing the vertices
     *                  defined by the given vertex array.
     */
    private static Buffer vertBuffer(double[] vertArray) {
        // create and return buffer
        DoubleBuffer vertBuffer = BufferUtil.newDoubleBuffer(vertArray.length);
        vertBuffer.put(vertArray);
        vertBuffer.rewind();
        return vertBuffer;
    }
    
    /**
     * Creates a vertex buffer for a given list of 2D vertices.
     * @param vertices  A list of 2D vertices for which a vertex buffer is desired.
     * @return          A <code>Buffer</code> object containing the given vertices.
     */
    private static Buffer vertBuffer2D(List<Vector2D> vertices) {

        // create vertex array
        double[] vertArray = new double[vertices.size()*2];
        
        // populate vertex array
        for (int i=0; i<vertices.size(); i++) {
            Vector2D vert = vertices.get(i);
            vertArray[2*i] = vert.getX();
            vertArray[2*i+1] = vert.getY();
        }
        
        // create and return buffer
        DoubleBuffer vertBuffer = BufferUtil.newDoubleBuffer(vertArray.length);
        vertBuffer.put(vertArray);
        vertBuffer.rewind();
        return vertBuffer;
    }
    
    /**
     * Creates a vertex buffer for a given list of 3D vertices.
     * @param vertices  A list of 3D vertices for which a vertex buffer is desired.
     * @return          A <code>Buffer</code> object containing the given vertices.
     */
    private static Buffer vertBuffer3D(List<Vector3D> vertices) {
        
        // create vertex array
        double[] vertArray = new double[vertices.size()*3];
        
        // populate vertex array
        for (int i=0; i<vertices.size(); i++) {
            Vector3D vert = vertices.get(i);
            vertArray[3*i] = vert.getX();
            vertArray[3*i+1] = vert.getY();
            vertArray[3*i+2] = vert.getZ();
        }
        
        // create and return buffer
        DoubleBuffer vertBuffer = BufferUtil.newDoubleBuffer(vertArray.length);
        vertBuffer.put(vertArray);
        vertBuffer.rewind();
        return vertBuffer;
    }
    
    /**
     * Generates a normal buffer for a 2D polygon with a given number of vertices.
     * @param vertCount The number of polygon vertices.
     */
    private static Buffer normalBuffer2D(int vertCount) {
        double[] normalArray = new double[vertCount*3];
        for (int i=0; i<vertCount; i++) {
            normalArray[3*i + 0] = 0.0;
            normalArray[3*i + 1] = 0.0;
            normalArray[3*i + 2] = 1.0;
        }
        return vertBuffer(normalArray);
    }

}