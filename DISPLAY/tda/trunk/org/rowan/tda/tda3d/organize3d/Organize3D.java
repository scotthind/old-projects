package org.rowan.tda.tda3d.organize3d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.BoundingBox3D;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.shapes2d.Polygon2D;
import org.rowan.linalgtoolkit.shapes2d.Shape2D;
import org.rowan.linalgtoolkit.shapes3d.Shape3D;
import org.rowan.linalgtoolkit.transform3d.Rotation;
import org.rowan.tda.tda2d.organize2d.Organize2D;

/**
 * The <code>Organize3D</code> class is a tactical decision aid will take a collection
 * of <code>Shape3D</code> instances and position them as close as possible to
 * the origin without any of the shapes overlapping.
 * <p>
 * The algorithm will first compute the bounding box of each <code>Shape3D</code>.
 * For each bounding box, the algorithm will discover which face has the smallest
 * surface area. The algorithm rotates the face that has the smallest surface area
 * into the X-Y plane. Once this is complete, the <code>Face3D</code> instance that
 * is in the X-Y plane is converted into a <code>Shape2D</code> instance for each
 * bounding box. The <code>Organize2D</code> class is used, as the collection of
 * <code>Shape2D</code> instances are passed into the algorithm. Essentially, the
 * <code>Organize3D</code> algorithm projects each three-dimensional shape into
 * two-dimensional space. Once the <code>Organize2D</code> algorithm is complete,
 * the position and angle of rotation of each <code>Shape2D</code> is acquired
 * and is applied to the respective <code>Shape3D</code> instance.

 * @author Spence DiNicolantonio, Michael Liguori, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class Organize3D {

    /*********************************************
     * MARK: Fields
     *********************************************/

    /** The collection of three-dimensional shapes to be organized. */
    private LinkedList<Shape3D> shapes;

    /** The collection of two-dimensional shapes, which are the three-dimensional shapes
     *  projected into two-dimensional space.
     */
    private LinkedList<Shape2D> projectedShapes;

    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Constructs an instance of <code>Organize3D</code>.
     * @param shapes the collection of three-dimensional shapes to be organized.
     */
    public Organize3D(LinkedList<Shape3D> shapes){
        this.projectedShapes = new LinkedList<Shape2D>();
        this.shapes = shapes;
    }

    /*********************************************
     * MARK: Run
     *********************************************/

    /**
     * Executes the <code>Organize3D</code> algorithm. This algorithm will take
     * the <code>Shape3D</code> instances given to this class and position them
     * as close as possible to the origin without any of the shapes overlapping.
     */
    public void run() {
        // rotate shapes so the smallest bounding box side is in the X Y plane
        for (Shape3D shape : shapes)
            rotateShapeForSmallestXYSide(shape);
        
        // a list of 2D shapes that represent the 3D shapes' bounding box's front side
        for (Shape3D shape : shapes)
            projectedShapes.add(createBBFrontPolygon(shape));
        
        // create 2D organize TDA and run on 2D shapes
        Organize2D o = new Organize2D(projectedShapes);
        o.run();
        
        // update 3D shapes according to 2D TDA's results
        for(int i = 0; i < projectedShapes.size(); i++){
            double angle = projectedShapes.get(i).getAngle();
            Vector2D pos = projectedShapes.get(i).getPosition();
            
            //puts the shapes like a skyline on the XY plane
            shapes.get(i).setPosition(new Vector3D(pos.getX(), pos.getY(), 0.0));
            shapes.get(i).rotate(new Rotation(0.0, 0.0, -angle));
        }
    }

    /*********************************************
     * MARK: Algorithm Steps
     *********************************************/

    /** 
     * Rotates a given three-dimensional shape so the smallest side of its
     * bounding box is in the X-Y plane.
     * @param shape The three-dimensional shape to be altered.
     */
    private void rotateShapeForSmallestXYSide(Shape3D shape) {
        // 3 different size faces. Start with the current positioned face
        // and check to see if any of the other 2 faces are smaller.
        // if they are smaller rotate the shape to the smaller face.
        BoundingBox3D bb = shape.boundingBox();
        double width  = bb.width();
        double height = bb.height();
        double depth  = bb.depth();
        
        double xRot = 0;
        double yRot = 0;
        double zRot = 0;
        
        double smallestAreaFace = width * height;
        
        //left and right face smaller?
        if(smallestAreaFace > height * depth){
            smallestAreaFace = height*depth;
            xRot =  0;
            yRot =  Math.PI*0.5;
            zRot =  0;
        }
        // top and bottom face smaller?
        if(smallestAreaFace > width *depth){
            xRot = Math.PI*0.5;
            yRot =  0;
            zRot =  0;
        }
        
        shape.rotate(new Rotation(xRot, yRot, zRot));
    }

    /**
     * Creates a two-dimensional polygon that represents the front side of a given
     * three-dimensional shape's bounding box.
     * @param shape The three-dimensional shape for which the polygon is being created.
     * @return      A two-dimensional shape representing the front side of the
     *              given three-dimensional shape's bounding box.
     */
    private Shape2D createBBFrontPolygon(Shape3D shape) {
        BoundingBox3D bb = shape.boundingBox();

        LinkedList<Vector3D> frontFacePoints = getFrontFaceVertices(bb);
        LinkedList<Vector2D> convertedPoints = new LinkedList<Vector2D>();
        
        //Convert the front facing points to 2D
        for(Vector3D v : frontFacePoints)
            convertedPoints.add(new Vector2D(v.getX(), v.getY()));
        
        return new Polygon2D(convertedPoints);
    }

    /**
     * Gets the vertices for the front face of a given bounding box.
     * @param bb    the <code>BoundingBox3D</code> instance.
     * @return      A collection of vertices, which represent the front face of the
     *              given bounding box.
     */
    private LinkedList<Vector3D> getFrontFaceVertices(BoundingBox3D bb){
        LinkedList<Vector3D> frontPoints = new LinkedList<Vector3D>();
        Vector3D c = bb.center();

        // Get the x, y, z, location of the front upper right vertex
        double x = bb.getB().getX();
        double y = bb.getB().getY();
        double z = bb.getB().getZ();

        // Get the width and height of the bounding box
        double w = bb.width();
        double h = bb.height();

        // Upper right vertex
        frontPoints.add(bb.getB().subtract(c));

        // Bottom right vertex
        frontPoints.add(new Vector3D(x    , y - h, z).subtract(c));

        // Bottom left vertex
        frontPoints.add(new Vector3D(x - w, y - h, z).subtract(c));

        // Upper left vertex
        frontPoints.add(new Vector3D(x - w,     y, z).subtract(c));
        
        return frontPoints;
    }
}