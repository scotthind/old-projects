package org.rowan.linalgtoolkit;

import java.util.List;
import java.util.LinkedList;

/**
 * The <code>BoundingBox3D</code> class is is used to define an immutable 3D
 * minimum bounding box. For any given 3D shape, that shape's bounding box is a
 * 3D cuboid, parallel to the x, y, and z axes, that contains every point on that
 * shape.
 *
 * A <code>BoundingBox3D</code> is defined by two points, <code>a</code> and
 * <code>b</code>, in world coordinates. Point <code>a</code> is the back-bottom-left
 * vertex of the bounding box, while point <code>b</code> is the front-top-right
 * vertex.
 *
 * @author Spence DiNicolantonio, Michael Liguori
 * @version 1.1
 * @since 1.1
 */
public class BoundingBox3D {


    /*********************************************
     * MARK: Fields
     *********************************************/

    /** The back-bottom-left vertex of this bounding box */
    private Vector3D a;

    /** The front-top-right vertex of this bounding box */
    private Vector3D b;


    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Creates a <code>BoundingBox3D</code> object with given back-bottom-left
     * and front-top-right vertices.
     * @param a A <code>Vector3D</code> describing the bounding box's back-bottom-left
     *          vertex.
     * @param b A <code>Vector3D</code> describing the bounding box's front-top-right
     *          vertex.
     * @throws IllegalArgumentException If <code>b</code>'s x component is less
     *                      than <code>a</code>'s x component, <code>b</code>'s
     *                      y component is less than <code>a</code>'s x component,
     *                      or <code>b</code>'s z component is less than <code>a</code>'s
     *                      z component.
     */
    public BoundingBox3D(Vector3D a, Vector3D b) {
        // validate given vertices
        if (b.getX() < a.getX() || b.getY() < a.getY() || b.getZ() < a.getZ())
            throw new IllegalArgumentException("Invalid bounding box coordinate values");

        // set a and b vertices
        this.a = a;
        this.b = b;
    }

    /**
     * Creates a <code>BoundingBox3D</code> object for a given set of vertices,
     * in world coordinates
     * @param vertices  A list of <code>Vector3D</code>s describing the vertices,
     *                  in world coordinates, for which the bounding box will be
     *                  created.
     */
    public BoundingBox3D(List<Vector3D> vertices){
        double backMostZ   = Double.POSITIVE_INFINITY;
        double bottomMostY = Double.POSITIVE_INFINITY;
        double leftMostX   = Double.POSITIVE_INFINITY;
        double frontMostZ  = Double.NEGATIVE_INFINITY;
        double topMostY    = Double.NEGATIVE_INFINITY;
        double rightMostX  = Double.NEGATIVE_INFINITY;
        for(Vector3D v : vertices){
            //is vector's X farthest negative X value?
            if(v.getX() < leftMostX)
                leftMostX = v.getX();
            //is vector's X farthest positive X value?
            if(v.getX() > rightMostX)
                rightMostX = v.getX();
            //is vector's Y farthest negative Y value?
            if(v.getY() < bottomMostY)
                bottomMostY = v.getY();
            //is vector's Y farthest positive Y value?
            if(v.getY() > topMostY)
                topMostY = v.getY();
            //is vector's Z farthest negative Z value?
            if(v.getZ() < backMostZ)
                backMostZ = v.getZ();
            //is vector's Z farthest positive Z value?
            if(v.getZ() > frontMostZ)
                frontMostZ = v.getZ();
        }

        // set a and b vertices
        this.a = new Vector3D(leftMostX, bottomMostY, backMostZ);
        this.b = new Vector3D(rightMostX, topMostY, frontMostZ);
    }


    /*********************************************
     * MARK: Accessors
     *********************************************/

    /**
     * Returns this bounding box's back-bottom-left vertex, in world coordinates.
     * @return  This bounding box's back-bottom-left vertex, in world coordinates.
     */
    public Vector3D getA() {
        return this.a;
    }

    /**
     * Returns this bounding box's front-top-right vertex, in world coordinates.
     * @return  This bounding box's front-top-right vertex, in world coordinates.
     */
    public Vector3D getB() {
        return this.b;
    }


    /*********************************************
     * MARK: Queries
     *********************************************/

    /**
     * Returns this bounding box's center point, in world coordinates.
     * @return  This bounding box's center point, in world coordinates.
     */
    public Vector3D center() {
        double x = a.getX() + (b.getX() - a.getX()) / 2;
        double y = a.getY() + (b.getY() - a.getY()) / 2;
        double z = a.getZ() + (b.getZ() - a.getZ()) / 2;
        return new Vector3D(x, y, z);
    }

    /**
     * Calculates the width of this bounding box. That is, the length of this of
     * this bounding box along the x axis.
     * @return  The width of this bounding box.
     */
    public double width() {
        return b.getX() - a.getX();
    }

    /**
     * Calculates the height of this bounding box. That is, the length of this of
     * this bounding box along the y axis.
     * @return  The height of this bounding box.
     */
    public double height() {
        return b.getY() - a.getY();
    }

    /**
     * Calculates the depth of this bounding box. That is, the length of this of
     * this bounding box along the z axis.
     * @return  The depth of this bounding box.
     */
    public double depth() {
        return b.getZ() - a.getZ();
    }

    /**
     * Determines the Volume of this Bounding Box
     * given bounding box.
     * @return  The volume of this bounding box
     */
    public double volume() {
        return (height() * width() * depth());
    }
    

    /*********************************************
     * MARK: Linear Algebra
     *********************************************/
    
    /**
     * Determines the distance between this bounding box and a given bounding box.
     * The current implementation computes the distance using the center of the 
     * bounding boxes. In the future, this implementation should be replaced with
     * one that concders the closest points on the surface of the bounding boxes.
     * @param bb    A <code>BoundingBox3D</code>
     * @return      The distance between this bounding box and <code>bb</code>
     */
    public double distance(BoundingBox3D bb) {
        return center().distance(bb.center());
    }

    /**
     * Determines whether a given 3D vertex lies within this bounding box.
     * @param vertex    A <code>Vector3D</code> describing the vertex in question.
     * @return          <code>true</code> if <code>vertex</code> is wholly contained
     *                  by this bounding box; <code>false</code> otherwise.
     */
    public boolean contains(Vector3D vertex) {
        return (vertex.getX() >= this.a.getX() &&
                vertex.getY() >= this.a.getY() &&
                vertex.getZ() >= this.a.getZ() &&
                vertex.getX() <= this.b.getX() &&
                vertex.getY() <= this.b.getY() &&
                vertex.getZ() <= this.b.getZ());
    }

    /**
     * Determines whether a given 3D bounding box is wholly contained by this
     * bounding box.
     * @param boundingBox   The <code>BoundingBox3D</code> in questixon.
     * @return              <code>true</code> if <code>boundingBox</code> is
     *                      wholly contained by this bounding box; <code>false</code>
     *                      otherwise.
     */
    public boolean contains(BoundingBox3D boundingBox) {
        return (contains(boundingBox.getA()) &&
                contains(boundingBox.getB()));
    }

    /**
     * Determines whether a given 3D bounding box intersects this bounding box.
     * @param boundingBox   A <code>BoundingBox3D</code>.
     * @return              <code>true</code> if <code>boundingBox</code> intersects
     *                      this bounding box; <code>false</code> otherwise.
     */
    public boolean intersects(BoundingBox3D boundingBox) {
        double w = (width() + boundingBox.width()) / 2;
        double h = (height() + boundingBox.height()) / 2;
        double d = (depth() + boundingBox.depth()) / 2;

        double dx = Math.abs(center().getX() - boundingBox.center().getX());
        double dy = Math.abs(center().getY() - boundingBox.center().getY());
        double dz = Math.abs(center().getZ() - boundingBox.center().getZ());

        return (dx <= w && dy <= h && dz <= d);
    }

    /**
     * Determines the intersecting Bounding box of this bounding box and the
     * given bounding box.
     * @param boundingBox   A <code>BoundingBox3D</code>.
     * @return              The intersecting Bounding Box
     */
    public BoundingBox3D intersection(BoundingBox3D boundingBox) {
        if(!intersects(boundingBox))
            throw new IllegalArgumentException("These bounding boxes do not intersect");

        LinkedList<Vector3D> points = new LinkedList<Vector3D>();
       
        //Face line intersection
        for(LinkedList<Vector3D> face : this.faces())
            for(LinkedList<Vector3D> face2 : boundingBox.faces())
                for(int i = 0; i < face2.size(); i++)
                    points.addAll(planeLineIntersection(face, face2.get(i), face2.get((i+1)%face2.size())));


             //Face line intersection
        for(LinkedList<Vector3D> face : boundingBox.faces())
            for(LinkedList<Vector3D> face2 : this.faces())
                for(int i = 0; i < face2.size(); i++)
                    points.addAll(planeLineIntersection(face, face2.get(i), face2.get((i+1)%face2.size())));

        //Generate List of corners for the bounding box
        for(Vector3D v : this.corners())
            if(boundingBox.contains(v))
                points.add(v);

        for(Vector3D v : boundingBox.corners())
            if(this.contains(v))
                points.add(v);
        
        for(int n = 0 ; n < points.size();)
            if(!(this.contains(points.get(n)) && boundingBox.contains(points.get(n))))
                points.remove(n);
            else
                n++;

        return new BoundingBox3D(points);
    }




    /*********************************************
     * MARK: Other
     *********************************************/

    /**
     * Calculates the minimum bounding box that contains both this bounding box
     * and a given 3D bounding box.
     * @param boundingBox   The <code>BoundingBox3D</code> to be merged with this
     *                      bounding box.
     * @return              The 3D minimum bounding box that contains both this
     *                      bounding box and <code>boundingBox</code>.
     */
    public BoundingBox3D merge(BoundingBox3D boundingBox) {
        // create a list of both bounding boxes' vertices
        LinkedList<Vector3D> vertices = new LinkedList<Vector3D>();
        vertices.add(a);
        vertices.add(b);
        vertices.add(boundingBox.getA());
        vertices.add(boundingBox.getB());

        // create and return a new bounding box using the constructed list
        return new BoundingBox3D(vertices);
    }

    /**
     * Calculates the 3D minimum bounding box that contains both this bounding
     * box and a given 3D vertex.
     * @param vertex    A <code>Vector3D</code> object describing the vertex.
     * @return          The minimum bounding box that contains both this bounding
     *                  box and <code>vertex</code>.
     */
    public BoundingBox3D expand(Vector3D vertex) {
        // create a list of this bounding box's vertices and the given vertex
        LinkedList<Vector3D> vertices = new LinkedList<Vector3D>();
        vertices.add(a);
        vertices.add(b);
        vertices.add(vertex);

        // create and return a new bounding box using the constructed list
        return new BoundingBox3D(vertices);
    }


    /**
     * Computes the corners of this bounding box. The list returned by this method
     * will contain the bounding boxes corners ordered as follows:
     * <start>
     * <lu>
     *  <li>back-bottom-left (a)
     *  <li>back-bottom-right
     *  <li>back-top-right
     *  <li>back-top-left
     *  <li>front-top-right (b)
     *  <li>front-bottom-right
     *  <li>front-bottom-left
     *  <li>front-top-left
     * </lu>
     * <start>
     * <p>
     * That is, the back plane of the bounding box is defined, starting with point
     * 'a', then the front plane is defined, starting with point 'b', both with
     * clockwise winding.
     * @return  List of the corners of the bounding box;
     */
    public LinkedList<Vector3D> corners() {
        double w = width();
        double h = height();
        LinkedList<Vector3D> vertices = new LinkedList<Vector3D>();

        // create the 8 points which define a bounding box
        vertices.add(a);
        vertices.add(new Vector3D(a.getX() + w, a.getY(),     a.getZ()));
        vertices.add(new Vector3D(a.getX() + w, a.getY() + h, a.getZ()));
        vertices.add(new Vector3D(a.getX(),     a.getY() + h, a.getZ()));

        vertices.add(b);
        vertices.add(new Vector3D(b.getX(),     b.getY() - h, b.getZ()));
        vertices.add(new Vector3D(b.getX() - w, b.getY() - h, b.getZ()));
        vertices.add(new Vector3D(b.getX() - w, b.getY(),     b.getZ()));

        return vertices;
    }



    /**
     * Determines the intersection between a line segment and a plane. This is a helper method
     * for bounding box intersection.
     * @param plane A list of <code>Vector3D</code> which describe a plane.
     * @param segment A list of two <code>Vector3D</code> which describe a line segment.
     * @return A list of intersection points between the line segment and plane. A most
     *         there are three points returned. Minimum is 0, an empty list.
     */
    private LinkedList<Vector3D> planeLineIntersection(LinkedList<Vector3D> plane, Vector3D start, Vector3D end) {
        LinkedList<Vector3D> intersection = new LinkedList<Vector3D>();

        Vector3D u = end.subtract(start);
        Vector3D w = start.subtract(plane.get(0));

        //Vectors
        Vector3D AB = plane.get(1).subtract(plane.get(0));
        Vector3D AC = plane.get(2).subtract(plane.get(0));

        //Normal to the Plane
        Vector3D normal = AC.cross(AB);

        double sI =  -normal.dot(w) / normal.dot(u);

        // compute intersection point, in the plane's local coord system
        Vector3D delta = u.multiply(sI);
        Vector3D intersectPoint = start.add(delta);

        intersection.add(start);
        intersection.add(end);
        intersection.add(intersectPoint);

        //See if the segment ends and the interseciton pt are on the plane
        for (Vector3D p : plane) 
            for (int i = 0; i < intersection.size();)
                if (normal.dot(p.subtract(intersection.get(i))) != 0) 
                    intersection.remove(i);
                 else 
                    i++;


        return intersection;

    }

    /**
     * Creates  a LinkedList for each face of the bounding box and stores each
     * face list in a LinkedList.
     *
     * @return  A LinkedList holding LinkedLists of the points which describe each
     *          face of the bounding box.
     */
    private LinkedList<LinkedList<Vector3D>> faces() {
        double w = width();
        double h = height();

        Vector3D a2 = new Vector3D(a.getX() + w, a.getY(), a.getZ()); // BackBottomRight
        Vector3D a3 = new Vector3D(a.getX() + w, a.getY() + h, a.getZ()); // BackTopRight
        Vector3D a4 = new Vector3D(a.getX(), a.getY() + h, a.getZ()); // BackTopLeft

        Vector3D b2 = new Vector3D(b.getX(), b.getY() - h, b.getZ()); // FrontBottomRight
        Vector3D b3 = new Vector3D(b.getX() - w, b.getY() - h, b.getZ()); // FrontBottomLeft
        Vector3D b4 = new Vector3D(b.getX() - w, b.getY(), b.getZ()); // FrontTopLeft

        LinkedList<Vector3D> frontFace = new LinkedList<Vector3D>();
        frontFace.add(b);
        frontFace.add(b2);
        frontFace.add(b3);
        frontFace.add(b4);
        LinkedList<Vector3D> backFace = new LinkedList<Vector3D>();
        backFace.add(a);
        backFace.add(a2);
        backFace.add(a3);
        backFace.add(a4);
        LinkedList<Vector3D> topFace = new LinkedList<Vector3D>();
        topFace.add(b);
        topFace.add(b4);
        topFace.add(a4);
        topFace.add(a3);
        LinkedList<Vector3D> bottomFace = new LinkedList<Vector3D>();
        bottomFace.add(a);
        bottomFace.add(b3);
        bottomFace.add(b2);
        bottomFace.add(a2);
        LinkedList<Vector3D> leftFace = new LinkedList<Vector3D>();
        leftFace.add(a);
        leftFace.add(a4);
        leftFace.add(b4);
        leftFace.add(b3);
        LinkedList<Vector3D> rightFace = new LinkedList<Vector3D>();
        rightFace.add(b);
        rightFace.add(a3);
        rightFace.add(a2);
        rightFace.add(b2);
        LinkedList<LinkedList<Vector3D>> faces = new LinkedList<LinkedList<Vector3D>>();
        faces.add(frontFace);
        faces.add(backFace);
        faces.add(topFace);
        faces.add(bottomFace);
        faces.add(rightFace);
        faces.add(leftFace);

        return faces;
    }
}
