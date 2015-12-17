package org.rowan.linalgtoolkit.shapes3d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.shapes2d.Polygon2D;
import org.rowan.linalgtoolkit.transform3d.Rotation;

/**
 * The <code>Face3D</code> class describes a face on a <code>Polygon3D</code>
 * instance. A face is defined as a list of 3D vertices in clockwise order, which
 * are all on the same two-dimensional plane.
 *
 * @author Spence DiNicolantonio, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class Face3D {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** 
     * The number of decimal places to which the 'd' component of the plane equation 
     * is compared. 
     */
    private static final int PRECISION = 8;
    

    /*********************************************
     * MARK: Fields
     *********************************************/

    /** A list of vertices that define the face. */
    private LinkedList<Vector3D> vertices;
    
    /** A list containing the face's vertices projected into two-dimensional space. */
    private LinkedList<Vector2D> projectedVertices;

    /** The normal vector for this face. */
    private Vector3D normal;
    
    /** The face's orientation. */
    private Rotation orientation;
    

    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Constructs an instance of <code>Face3D</code>.
     * @param vertices a list of <code>Vector3D</code> instances, which represent
     *                 the newly created face.
     */
    public Face3D(List<Vector3D> vertices) {
        this.orientation = Rotation.IDENTITY;
        
        if(validateFace(vertices))
            this.vertices = new LinkedList<Vector3D>(vertices);
        else
            throw new IllegalArgumentException("Invalid vertices: the given vertices " +
                                               "do not describe a valid planar face.");
    }

    /*********************************************
     * MARK: Mutators
     *********************************************/

    /**
     * Sets the orientation of this face. This method is used by the Polyhedron3D
     * class and is not intended for external use.
     * @param orientation   A Rotation object describing the face's new orientation.
     */
    public void setOrientation(Rotation orientation) {
        this.orientation = orientation;
    }    
    

    /*********************************************
     * MARK: Queries
     *********************************************/

    /**
     * Returns the normal vector for this face.
     * @return the normal vector for this face.
     */
    public Vector3D getNormal() {
        return normal.rotate(orientation);
    }
    /**
     * Returns this face's vertex located at a given index.
     * @param index The index of the desired vertex.
     * @return      This face's vertex at the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    public Vector3D getVertex(int index) {
        return getVertices().get(index);
    }

     /**
     * Returns an ordered list of vertices, that define this face.
     * @return  A <code>List</code> of <code>Vector3D</code> objects that define 
     *          this face.
     */
    public List<Vector3D> getVertices() {
        // rotate vertices
        LinkedList<Vector3D> rotatedVerts = new LinkedList<Vector3D>();
        for (Vector3D vert : this.vertices)
            rotatedVerts.add(vert.rotate(this.orientation));
        
        // return rotated vertices
        return rotatedVerts;
    }

    /**
     * Returns the number of points on this 3D Face.
     * @return The number of points on this 3D Face.
     */
    public int vertCount() {
        return vertices.size();
    }

    /**
     * Computes the surface area of this <code>Face3D</code> instance. The surface
     * area of a 3D Face, is the area, as all the vertices lie on the same plane.
     * @return The surface area of this <code>Face3D</code> instance.
     */
    public double surfaceArea() {
        return Polygon2D.area(projectedVertices);
    }
    
    /**
     * Determines if the given <code>Vector3D</code> is behind the plane where 
     * this face exists. By definition, a vertex is behind a plane if it is on 
     * the side of the plane opposite to the normal vector.
     * @param vertex    a vertex in three-dimensional space.
     * @return          <code>true</code> if the given vertex is behind the face's
     *                  plane; <code>false</code> otherwise.
     */
    public boolean isBehind(Vector3D vertex) {
        Vector3D v = getVertex(0);
        Vector3D w = vertex.subtract(v);
        double distance = getNormal().dot(w) / getNormal().magnitude();
        
        return (distance <= 0);
    }

    
     /*********************************************
     * MARK: Other
     *********************************************/

    /**
     * Determines if the given list of vertices all lie within the same plane
     * in three-dimensional space. If not the vertices will be altered as needed
     * @param vertices the proposed list of vertices to create the new <code>Face3D
     *                 </code> instance.
     * @return         <code>true</code> if the given list of vertices all lie
     *                 within the same plane in three-dimensional space; <code>
     *                 false</code> otherwise.
     */
    private boolean isValidPlane(List<Vector3D> vertices) {
        // Get three arbitrary vertices to create the plane. If this plane is
        // indeed valid, it is irrevelant which three points are chosen since
        // they all lie on the same plane.
        Vector3D a = vertices.get(0);
        Vector3D b = vertices.get(1);
        Vector3D c = vertices.get(2);

        // Compute the normal vector of the plane.
        Vector3D ab = b.subtract(a);
        Vector3D ac = c.subtract(a);
        normal = ac.cross(ab);

        // We compute the d constant in the equation of the plane by plugging
        // in a point that lies on this plane into the equation.
        // Equation of a plane: Ax + By = Cz + d = 0
        double d = getNormal().getX() * a.getX() +
                   getNormal().getY() * a.getY() +
                   getNormal().getZ() * a.getZ();

        // Plug in all the points into the equation for this plane to ensure
        // they all lie in this plane. If one fails, then a valid plane cannot
        // be constructed from the given list of vertices.
        Vector3D newVert;
        for(int i=0; i<vertices.size(); i++) {
            Vector3D vertex = vertices.get(i);
            double xx = getNormal().getX() * vertex.getX();
            double yy = getNormal().getY() * vertex.getY();
            double zz = getNormal().getZ() * vertex.getZ();
            double currD = xx+yy+zz;
        
            // not on same plane (within set precision)?
            if (currD != d && Math.abs(currD - d) > (1.0 / Math.pow(10, PRECISION)))
                return false;
        }

        return true;
    }

    /**
     * Validates the given list of vertices to ensure that the newly created
     * instance of <code>Face3D</code> is composed of 3D Points that are all on 
     * the same two-dimensional plane, defining a simple convex polygon.
     * @param vertices  the proposed list of vertices to create the new <code>Face3D
     *                  </code> instance.
     * @return          <code>true</code> if the list of 3D Points are on the 
     *                  same two-dimensional plane, defining a simple convex polygon;
     *                  <code>false</code> otherwise.
     */
    private boolean validateFace(List<Vector3D> vertices) {
        // verify that all vertices are in one 2D plane
        if (!isValidPlane(vertices))
            return false;
        
        // At this point we know that all vertices lie on the same plane.
        // Now we must verify that the vertices define a simple convex polygon.
        
        // Create a tangent vector based on two arbitrary points.
        // The points can be arbitrary since if this is a valid plane, any combination
        // of two points would yield an equivalent tangent vector.
        Vector3D v1 = vertices.get(0);
        Vector3D v2 = vertices.get(1);
        Vector3D tangentUnit = (v1.subtract(v2)).unitVector();
        Vector3D normalUnit = getNormal().unitVector();
        Vector3D binormal = (tangentUnit.cross(normalUnit));    
        
        // We project the point relative to the plane into two-dimensional space.
        projectedVertices = new LinkedList<Vector2D>();
        for(Vector3D vertex: vertices) {
            Vector3D relative = vertex.subtract(vertices.get(0));
            double y = relative.dot(binormal);
            double x = relative.dot(tangentUnit);
            projectedVertices.add(new Vector2D(x, y));
        }
        
        // if the projected points pass convex polygon validation, then we are done
        if (Polygon2D.validateVertices(projectedVertices))
            return true;
        
        // otherwise, reverse the vertex winding and attempt validation again.
        for (int i=0; i<projectedVertices.size(); i++) {
            Vector2D vert = projectedVertices.remove(i);
            projectedVertices.add(0, vert);
        }
        return Polygon2D.validateVertices(projectedVertices);
    }
}