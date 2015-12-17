package org.rowan.linalgtoolkit.shapes2d;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.BoundingBox2D;
import org.rowan.linalgtoolkit.logic2d.LinAlg2D;

/**
 * The <code>ComplexShape2D</code> class provides a means of describing irregular
 * shapes using a collection of sub-shapes. A <code>ComplexShape2D</code> object 
 * stores a collection of <code>Shape2D</code> objects, whose union defines the 
 * complex shape. 
 * <p> 
 * The center point of a <code>ComplexShape2D</code> object is located at the
 * center. Upon creation of a complex shape, the minimum bounding 
 * box must contain this point, or an exception will be thrown.
 *
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public class ComplexShape2D extends Shape2D {
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    private Collection<Shape2D> subShapes;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a <code>ComplexShape2D</code> object at a given position, defined 
     * by a given collection of 2D sub-shapes.
     * @param position  A <code>Vector2D</code> describing the position of the 
     *                  complex shape in world coordinates.
     * @param subShapes A collection of <code>Shape2D</code> objects describing  
     *                  the sub-shapes to define the created complex shape.
     */
    public ComplexShape2D(Vector2D position, Collection<Shape2D> subShapes) {
        // initialize with super constructor
        super(position);
        
        // set sub-shapes
        setSubShapes(subShapes);
    }
    
    /**
     * Creates a <code>ComplexShape2D</code> object at the origin, defined by a 
     * given collection of 2D sub-shapes.
     * @param subShapes A collection of <code>Shape2D</code> objects describing  
     *                  the sub-shapes to define the created complex shape.
     */
    public ComplexShape2D(Collection<Shape2D> subShapes) {
        this(Vector2D.ORIGIN, subShapes);
    }
    
    /**
     * Creates a <code>ComplexShape2D</code> object at a given position to represent 
     * a concave or convex polygon defined by a given list of vertices, in world 
     * coordinates, with clockwise winding.
     * @param position  A <code>Vector2D</code> describing the position of the 
     *                  complex shape in world coordinates.
     * @param vertices  A <code>List</code> of <code>Vector2D</code> objects that
     *                  describe the vertices of the desired shape, in world
     *                  coordinates, with clockwise winding.
     */
    public ComplexShape2D(Vector2D position, List<Vector2D> vertices) {
        this(position, triangulate(vertices));
    }
    
    /**
     * Creates a <code>ComplexShape2D</code> object at the origin to represent a 
     * concave or convex polygon defined by a given list of vertices, in world 
     * coordinates, with clockwise winding.
     * @param vertices  A <code>List</code> of <code>Vector2D</code> objects that
     *                  describe the vertices of the desired shape, in world
     *                  coordinates, with clockwise winding.
     */
    public ComplexShape2D(List<Vector2D> vertices) {
        this(triangulate(vertices));
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns an ordered list of the vertices, in local coordinates, that define 
     * this shape. The returned list is a concatenation each sub-shape's vertex
     * lists.
     * <p>
     * Altering the returned list will have no effect on this shape.
     * @return  A <code>List</code> of vertices that define this shape, in local 
     *          coordinates.
     */
    @Override
    public List<Vector2D> getVertices() {
        // create a list to store vertices
        List<Vector2D> vertices = new LinkedList<Vector2D>();
        
        // concatenate all sub-shape vertices
        for (Shape2D subShape : subShapes) {
            for (Vector2D vertex : subShape.getVertices()) {
                // convert vertex to local coords of complex shape and add to list
                vertices.add(toLocal(vertex, subShape));
            }
        }
        
        // return the created vertex list
        return vertices;
    }
    
    /**
     * Returns an ordered list of the vertices, in world coordinates, that define 
     * this shape. The returned list is a concatenation each sub-shape's vertex
     * lists.
     * <p>
     * Altering the returned list will have no effect on this shape.
     * @return  A <code>List</code> of vertices that define this shape, in world 
     *          coordinates.
     */
    @Override
    public List<Vector2D> getWorldVertices() {
        // get local vertices
        List<Vector2D> localVertices = getVertices();
        
        // convert each vertex to world coords
        List<Vector2D> worldVertices = new LinkedList<Vector2D>();
        for (Vector2D v : localVertices)
            worldVertices.add(this.toWorld(v));
        
        // return the created list of vertices
        return worldVertices;
    }
    
    /**
     * Returns this shape's vertex located at a given index.
     * @param index The index of the desired vertex.
     * @return      This shape's vertex at the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public Vector2D getVertex(int index) {
        return getVertices().get(index);
    }
    
    /**
     * Returns the collection of sub-shapes, that defines this complex shape. 
     * <p> 
     * The returned collection is an unmodifiable wrapper of the internal sub-shape 
     * collection, thus attempting to alter the collection in any way will result 
     * in an <code>UnsupportedOperationException</code> being thrown.
     * @return  An unmodifiable <code>Collection</code> of <code>Shape2D</code> 
     *          objects that define this complex shape.
     */
    public Collection<Shape2D> getSubShapes() {
        return Collections.unmodifiableCollection(subShapes);
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the position of this shape to a given point. This will also reposition
     * all sub-shapes.
     * @param position  A 2D vector describing the complex shape's new position.
     */
    @Override
    public void setPosition(Vector2D position) {
        // store change in position
        Vector2D delta = position.subtract(getPosition());
        
        // set position of this complex shape
        super.setPosition(position);
        
        // reposition all sub-shapes
        for (Shape2D subShape : subShapes)
            subShape.setPosition(subShape.getPosition().add(delta));
    }
    
    /**
     * Sets the velocity of this complex shape to a given vector. This will also 
     * set the velocity of each sub-shape.
     * @param velocity  A 2D vector describing the complex shape's new velocity.
     */
    @Override
    public void setVelocity(Vector2D velocity) {
        // set velocity field
        super.setVelocity(velocity);
        
        // set velocity of each sub-shape
        for (Shape2D subShape : subShapes)
            subShape.setVelocity(velocity);
    }
    
    /**
     * Redefines this complex shape with a given collection of sub-shapes
     * @param subShapes A collection of <code>Shape2D</code> objects describing  
     *                  the sub-shapes to define the created complex shape.
     * @throws IllegalArgumentException If the complex shape's local origin is
     *                  not contained by the minimum bounding box of the given
     *                  collection of sub-shapes.
     */
    public void setSubShapes(Collection<Shape2D> subShapes) {
        // set the velocity of each sub-shape to that of this complex shape
        Vector2D velocity = getVelocity();
        for (Shape2D subShape : subShapes)
            subShape.setVelocity(velocity);
        
        // set new sub-shapes
        this.subShapes = subShapes;
    }
    
    /**
     * Simplifies this complex shape to contain a maximum of two sub-shapes; creating
     * a tree of simplified complex sub-shapes as needed.
     */
    public void simplify() {
        // simplify all complex sub-shapes
        for (Shape2D subShape : subShapes)
            if (subShape instanceof ComplexShape2D)
                ((ComplexShape2D) subShape).simplify();
        
        // get array of sub-shapes
        Shape2D[] subShapesArray = (Shape2D[])subShapes.toArray(new Shape2D[0]);
        
        // only one sub-shape?
        if (subShapesArray.length == 1)
            return;
        
        // compute the union of all subshapes, except the head
        Shape2D composite = subShapesArray[1];
        for (int i=2; i<subShapesArray.length; i++)
            composite = composite.union(subShapesArray[i]);
        
        // create list of simplified subShapes
        LinkedList<Shape2D> simpSubShapes = new LinkedList<Shape2D>();
        simpSubShapes.add(subShapesArray[0]);
        simpSubShapes.add(composite);
        
        // set new sub-shapes
        setSubShapes(simpSubShapes);
    }
    
    /**
     * Expands this complex shape to contain only non-complex sub-shapes. All
     * complex sub-shapes will be expanded.
     */
    public void expand() {
        // expand all complex sub-shapes
        for (Shape2D subShape : subShapes)
            if (subShape instanceof ComplexShape2D)
                ((ComplexShape2D) subShape).expand();
        
        // expand all shapes into a new list of subshapes
        LinkedList<Shape2D> expandedSubShapes = new LinkedList<Shape2D>();
        for (Shape2D subShape : subShapes)
            if (subShape instanceof ComplexShape2D) {
                ComplexShape2D complex = (ComplexShape2D)subShape;
                expandedSubShapes.addAll(complex.getSubShapes());
            } else
                expandedSubShapes.add(subShape);
        
        // set new sub-shapes
        setSubShapes(expandedSubShapes);
    }
    
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Returns the number of vertices that define this shape. This is the sum
     * of all sub-shape vertices.
     * @return  The number of vertices that define this shape.
     */
    @Override
    public int vertCount() {
        int count = 0;
        for (Shape2D subShape : subShapes)
            count += subShape.vertCount();
        return count;
    }
    
    /**
     * Computes this shape's minimum bounding box. 
     * @return  This shape's minimum bounding box.
     */
    @Override
    public BoundingBox2D boundingBox() {
        BoundingBox2D bb = null;
        
        // merge the bounding boxes of all sub-shapes
        for (Shape2D subShape : subShapes)
            if (bb == null)
                bb = subShape.boundingBox();
            else
                bb = bb.merge(subShape.boundingBox());
        
        // return the resulting bounding box
        return bb;
    }
    
    /**
     * Computes the perimeter of this complex shape. This method has not yet 
     * been implemented, and currently throws an exception.
     * @return  The perimeter of this complex shape.
     */
    public double perimeter() {
        // TODO: implement
        throw new RuntimeException("Perimeter calculation for complex shapes has "+
                                   "not yet been implemented.");
    }
    
    /**
     * Computes the area of this complex shape.
     * @return  The area of this complex shape.
     */
    public double area() {
        // simplify this complex shape to two sub-shapes
        simplify();
        
        // get array of sub-shapes
        Shape2D[] subShapesArray = (Shape2D[])subShapes.toArray(new Shape2D[0]);
        
        // compute the sum of the area of each sub-shape
        double subArea1 = subShapesArray[0].area();
        double subArea2 = subShapesArray[1].area();
        double area = subArea1 + subArea2;
        
        // no intersection?
        if (!subShapesArray[0].intersects(subShapesArray[1]))
            return area;
        
        // compute the intersection area
        double intersectArea = LinAlg2D.intersectionArea(subShapesArray[0], subShapesArray[1]);
        
        // subtract intersection area from computed area and return
        return area - intersectArea;
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Converts a simple non-self-intersecting clockwise winding concave shape
     * described from it's global points to a complex shape using triangulation.
     *
     * @param points    A LinkedList of <code>Vector2D</code> instances.
     * @return          A <code>ComplexShape2D</code> defining Concave shape
     *                  from a list of <code>Triangle2D</code> instances.
     */
    private static List<Shape2D> triangulate(List<Vector2D> points){
        
        LinkedList<LinkedList<Vector2D>> vertexes = new LinkedList<LinkedList<Vector2D>>();
        LinkedList<Vector2D> adjacentPoints;
        LinkedList<Shape2D> triangles = new LinkedList<Shape2D>();
        //Initialize Everything
        //Make a LinkedList of all possible triangles.
        //possible triangles are previous point, current point , next point
        for(int i = 0 ; i < points.size(); i++){
            adjacentPoints = new LinkedList<Vector2D>();
            adjacentPoints.add(points.get((i-1+points.size())%points.size()));
            adjacentPoints.add(points.get(i));
            adjacentPoints.add(points.get((i+1)%points.size()));
            vertexes.addLast(adjacentPoints);
        }
        
        LinkedList<Vector2D> clipping,trianglesLocalPoints;
        Vector2D triangleCenter;
        int previousPoint, nextPoint;
        while(vertexes.size() > 2){
            //Find an ear
            int earLocation = findEar(vertexes);
            //remove the ear
            clipping = vertexes.get(earLocation);
            //update list
            previousPoint = (earLocation - 1 + vertexes.size()) % vertexes.size();
            nextPoint = (earLocation + 1 ) % vertexes.size();
            //previous point update
            vertexes.get(previousPoint).removeLast();
            vertexes.get(previousPoint).addLast(vertexes.get(nextPoint).get(1));
            //next point update
            vertexes.get(nextPoint).removeFirst();
            vertexes.get(nextPoint).addFirst(vertexes.get(previousPoint).get(1));
            //make the triangle
            triangleCenter = Triangle2D.center(clipping);
            trianglesLocalPoints = new LinkedList<Vector2D>();
            for(int k = 0; k < clipping.size();k++)
                trianglesLocalPoints.add(clipping.get(k).subtract(triangleCenter));
            //add triangle
            triangles.add(new Triangle2D(triangleCenter,trianglesLocalPoints));
            vertexes.remove(earLocation);
        }
        
        return triangles;
    }
    
    /**
     * Support method for the conversion of a concave shape to a complex shape
     * through triangulation. From the triangles that are left, determines which
     * triangle is an 'Ear'(a triangle which does not contain other points of the
     * larger shape and is an interior angled triangle)
     *
     * @param vertexes  A Double LinkedList of <code>Vector2D</code> instances.
     * @return          An int of the location in the double linked list
     *                  describing the best possible ear to remove.
     */
    private static int findEar(LinkedList<LinkedList<Vector2D>> vertexes){
        
        LinkedList<Integer> earLocations = new LinkedList();
        Vector2D vectorAB,vectorBC,normalOfAB,currentVector;
        LinkedList<Vector2D> clipping; //triangle linkedList
        double angle; //angle between vectors
        
        for(int i = 0; i < vertexes.size();i++){
            clipping = vertexes.get(i);
            //To be an ear it must be an interior angle
            vectorAB = clipping.get(1).subtract(clipping.get(0)); //A->B
            vectorBC = clipping.get(2).subtract(clipping.get(1)); //B->C
            normalOfAB = new Vector2D(vectorAB.getY() * -1, vectorAB.getX());
            //find the angle between normalOfAB and vectorBC
            angle = Math.toDegrees(Math.acos((normalOfAB.dot(vectorBC))/(vectorBC.magnitude()*normalOfAB.magnitude())));
            //interior angle?
            if(angle > 90 && angle < 270){
                boolean foundEar = true;
                //interior triangle cant of other points inside it
                for(int n = 0; n < vertexes.size();n++){
                    currentVector = vertexes.get(n).get(1);
                    //triangle cannot have any points contained inside
                    //not counting points associated with current triangle
                    if(LinAlg2D.contains(clipping, currentVector) &&
                       (!currentVector.equals(clipping.get(0))) &&
                       (!currentVector.equals(clipping.get(1))) &&
                       (!currentVector.equals(clipping.get(2)))){
                        foundEar = false;
                    }
                }
                if(foundEar)
                    earLocations.add(i);
            }
        }
        //find the ear thats furthest right.
        double greatestXcomponent = Double.NEGATIVE_INFINITY;
        int location = 0;
        for(int n = 0; n < earLocations.size();n++){
            if(vertexes.get(earLocations.get(n)).get(1).getX() >= greatestXcomponent){
                greatestXcomponent = vertexes.get(earLocations.get(n)).get(1).getX();
                location = earLocations.get(n);
            }
        }
        return location;
    }
}

