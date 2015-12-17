package org.rowan.linalgtoolkit.shapes3d;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.BoundingBox3D;
import org.rowan.linalgtoolkit.transform3d.Rotation;

/**
 * The <code>Polyhedron3D</code> class describes a convex polyhedron defined by a list
 * of vertices with clockwise winding.
 * <p>
 * The center point of a <code>Polyhedron3D</code> object is located at its local
 * origin (0, 0, 0). As there is no practical approach to polyhedron validation, 
 * it is up to the user to ensure that the faces provided to the <code>Polyhedron3D</code>
 * constructor do, in fact, define a valid polyhedron. Failure to define a valid
 * polyhedron may yield errored results from some linear algebra queries.
 *
 * @author Spence DiNicolantonio, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class Polyhedron3D extends Shape3D {

    /** A list containing the unique vertices of the faces that define this polyhedron. */
    private LinkedList<Vector3D> vertices;
    
    /** The faces that define this polyhedron. */
    private LinkedList<Face3D> faces;

    /*********************************************
     * MARK: Constructors
     *********************************************/
    /**
     * Creates a <code>Polyhedron3D</code> object at a given position, defined by a
     * given list of faces, in local coordinates, with clockwise winding.
     * @param position  A 3D vector describing the position of the polyhedron in world
     *                  coordinates.
     * @param faces     A list of faces, which represent each side of the <code>
     *                  Polyhedron3D</code> instance being constructed.
     * @throws IllegalArgumentException If the given list of faces does not
     *                  describe a convex 3D polyhedron with clockwise winding, or if
     *                  the given faces to not surround the polyhedron's local origin.
     */
    public Polyhedron3D(Vector3D position, List<Face3D> faces) {
        // initialize with super constructor
        super(position);

        // set faces
        setFaces(faces);
    }

    /**
     * Creates a <code>Polyhedron3D</code> object at the origin, defined by a given
     * list of vertices, in local coordinates, with clockwise winding.
     * @param faces An ordered list of vertices, relative to the polyhedron's
     *              center point, describing a convex polyhedron with clockwise
     *              winding.
     * @throws IllegalArgumentException If the given list of vertices does not
     *              describe a convex polyhedron with clockwise winding, or if
     *              the given vertices to not surround the polyhedron's local
     *              origin.
     */
    public Polyhedron3D(List<Face3D> faces) {
        this(Vector3D.ORIGIN, faces);
    }

    /*********************************************
     * MARK: Accessors
     *********************************************/
    /**
     * Returns a list of faces, which define this <code>Polyhedron3D</code> object.
     * The returned list is an unmodifiable wrapper of the internal face list,
     * thus attempting to alter the list in any way will result in an
     * <code>UnsupportedOperationException</code> being thrown.
     * @return  An unmodifiable <code>List</code> of <code>Face3D</code> objects
     *          that define this polyhedron.
     */
    public List<Face3D> getFaces() {
        return Collections.unmodifiableList(this.faces);
    }

    /**
     * Returns a list of vertices, in local coordinates, that define this polyhedron.
     * <p>
     * Altering the returned list will have no effect on the shape.
     * @return  A <code>List</code> of <code>Vector3D</code> objects that define 
     * this polyhedron.
     */
    public List<Vector3D> getVertices() {
        // rotate each vertex
        List<Vector3D> rotatedVerts = new LinkedList<Vector3D>();
        for (Vector3D vert : this.vertices)
            rotatedVerts.add(vert.rotate(getOrientation()));
            
        // return rotated vertices
        return rotatedVerts;
    }

    /**
     * Returns a list of the vertices, in world coordinates, that define this 
     * polyhedron.
     * <p>
     * Altering the returned list will have no effect on this shape.
     * @return  A <code>List</code> of <code>Vector3D</code> objects that define
     *          this shape, in world coordinates.
     */
    public List<Vector3D> getWorldVertices() {
        // convert each vertex to world coords
        List<Vector3D> worldVertices = new LinkedList<Vector3D>();
        for (Vector3D v : getVertices()) {
            worldVertices.add(this.toWorld(v));
        }

        // return the created list of vertices
        return worldVertices;
    }

    /**
     * Returns this shape's vertex located at a given index.
     * @param index The index of the desired vertex.
     * @return      This shape's vertex at the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    public Vector3D getVertex(int index) {
        return getVertices().get(index);
    }

    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the orientation of this shape. 
     * <p>
     * Overridden to rotate faces that compose this polyhedron.
     * @param orientation   A Rotation object describing the shape's new orientation.
     */
    @Override
    public void setOrientation(Rotation orientation) {
        super.setOrientation(orientation);
        for(Face3D face : this.faces)
            face.setOrientation(orientation);
    }


    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Returns the number of edges that define this polyhedron. The number of edges
     * on a 3D polyhedron is always equal to the number of vertices.
     * @return  The number of edges that define this polyhedron.
     */
    public int edgeCount() {
        return vertCount();
    }

    /**
     * Returns the number of points on this polyhedron. This is the same as the
     * <code>vertCount()</code> method.
     * @return  The number of points on this polyhedron.
     */
    public int points() {
        return vertCount();
    }

    /**
     * Returns the number of sides this polyhedron has. The number of sides on a
     * 3D polyhedron is always equal to the number of points.
     * @return  The number of sides this polyhedron has.
     */
    public int sides() {
        return vertCount();
    }

    /**
     * Returns the number of vertices that define this polyhedron.
     * @return  The number of vertices that define this polyhedron.
     */
    public int vertCount() {
        return this.vertices.size();
    }

    /**
     * Computes the surface area of this polyhedron.
     * @return  The surface area of this polyhedron.
     */
    public double surfaceArea() {
        double surfaceArea = 0;

        // Compute the area of each face and sum the total of all
        // the faces, which make up this polyhedron.
        for (Face3D face : faces) {
            surfaceArea += face.surfaceArea();
        }

        return surfaceArea;
    }

    /**
     * Computes the volume of this polyhedron.
     * @return  The volume of this polyhedron.
     */
    public double volume() {
        double volume = 0;

        // Construct a pyramid by using the face as the base
        // and the height as the distance between the position of the
        // polyhedron and the plane. Sum all the pyramid volumes to obtain the
        // overall volume.
        for (Face3D face : faces) {
            Vector3D w = getPosition().subtract(face.getVertex(0));
            double height = Math.abs(w.dot(face.getNormal().inverse()) / face.getNormal().inverse().magnitude());
            volume += (1.0/3.0) * face.surfaceArea() * height;
        }

        return volume;
    }

    /**
     * Computes this polyhedron's minimum bounding box.
     * @return  This polyhedron's minimum bounding box.
     */
    public BoundingBox3D boundingBox() {
        // create bounding box using world coords
        return new BoundingBox3D(getWorldVertices());
    }
    
    
    /*********************************************
     * MARK: Other
     *********************************************/
    
    /**
     * Determines whether a given list of faces, describes a valid convex polyhedron.
     * @param faces A list of <code>Face3D</code> objects defining the proposed 
     *              polyhedron.
     * @return      <code>true</code> if the given list of faces define a
     *              valid convex polyhedron, and the defined polyhedron surrounds 
     *              the local origin; <code>false</code> otherwise.
     */
    public static boolean validateFaces(List<Face3D> faces) {
        // As there is no practical approach to polyhedron validation, it is left
        // to the user to ensure that given faces define a valid polyhedron.
        
        // verify that faces surround local origin
        for (Face3D face : faces)
            if (!face.isBehind(Vector3D.ORIGIN))
                return false;
        return true;
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Redefines this polyhedron with a given list of faces, in local coordinates,
     * with clockwise winding.
     * @param faces An ordered list of faces, relative to the polyhedrons's
     *              center point, describing a convex polyhedron with clockwise
     *              winding.
     * @throws IllegalArgumentException If the given list of faces does not
     *              describe a convex polyhedron with clockwise winding, or if
     *              the given vertices to not surround the polyhedron's local
     *              origin.
     */
    private void setFaces(List<Face3D> faces) {
        // validate given vertices
        if (!validateFaces(faces)) {
            throw new IllegalArgumentException("Invalid faces: Given faces "
                                               + "do not describe a convex polyhedron\n" + faces);
        }
        
        // set new faces
        this.faces = new LinkedList<Face3D>(faces);
        
        // recompute the unique vertices, since the faces were altered
        this.vertices = computeVertices(faces);
    }

    /**
     * Returns a list of all the unique vertices that make up this instance of <code>
     * Polyhedron3D</code>. The list maintains no order, but will not contain
     * duplicates.
     * @param faces the instances of <code>Face3D</code>, which make up this
     *              polyhedron.
     * @return      a list of all the unique vertices that make up this instance
     *              of <code>Polyhedron3D</code>.
     */
    private LinkedList<Vector3D> computeVertices(List<Face3D> faces) {
        LinkedList<Vector3D> points = new LinkedList<Vector3D>();

        // Iterate through each face and only add the point to the list if is
        // not currently in the list already.
        for (Face3D face : faces) {
            for (Vector3D vertex : face.getVertices()) {
                if (!points.contains(vertex)) {
                    points.add(vertex);
                }
            }
        }

        return points;
    }
}
