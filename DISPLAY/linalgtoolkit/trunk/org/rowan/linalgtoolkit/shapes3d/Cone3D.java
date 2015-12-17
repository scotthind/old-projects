package org.rowan.linalgtoolkit.shapes3d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.BoundingBox3D;

/**
 * The <code>Cone3D</code> class describes a shape which has a base and
 * a given point to represent the cone's tip , the <code>apex</code>. The base
 * is a circular shape with a center point and a given radius. The base is described as
 * a circular shape with a center point and a radius.
 *
 * @author Michael Liguori
 * @version 1.1
 * @since 1.1
 */
public class Cone3D extends Shape3D{

    /*********************************************
     * MARK: Fields
     *********************************************/

    /** The Vector3D describing the top of the cone */
    private Vector3D apex;

    /** The Vector3D describing the center of the base of the cone */
    private Vector3D baseCenter;

    /** The radius of the base of the cone */
    private double baseRadius;
  
    
    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Creates a <code>Cone3D</code> object, defined by a given apex and base 
     * center point, in world coordinates, and a base radius.
     * @param apex       A 3D vector describing the position of the top of the cone
     *                   in world coordinates
     * @param baseCenter A 3D vector describing the center of the circular base of the
     *                   cone in world coordinates.
     * @param baseRadius A <code>double</code> which specifies the radius of the
     *                   circular base.
     * @throws IllegalArgumentException If the given base radius is less than or
     *                  or equal to 0.
     */
    public Cone3D(Vector3D apex, Vector3D baseCenter, double baseRadius) {
        // initialize with super constructor
        super();
        
        // calculate position
        Vector3D halfAxis = apex.subtract(baseCenter).multiply(0.5);
        setPosition(baseCenter.add(halfAxis));

        if (!validateCone(toLocal(apex), toLocal(baseCenter), baseRadius))
            throw new IllegalArgumentException("Invalid vertices: Given vertices " +
                                               "do not describe a valid cone ");

        //set values of the cone
        this.apex = toLocal(apex);
        this.baseCenter = toLocal(baseCenter);
        this.baseRadius = baseRadius;

    }

    
    /*********************************************
     * MARK: Accessors
     *********************************************/

    /**
     * The Center of the base surface.
     * @return A <code>Vector3D</code> describing the center of the base surface
     *         in world coordinates.
     */
    public Vector3D getBaseCenter() {
        return baseCenter.rotate(getOrientation());
    }
    
    /**
     * The top of the cone
     * @return A <code>Vector3D</code> describing the top of the cone in world
     *         coordinates.
     */
    public Vector3D getApex() {
        return apex.rotate(getOrientation());
    }
    
    /**
     * The radius of the base.
     * @return The Length of the base's radius
     */
    public double getBaseRadius() {
        return baseRadius;
    }
    
        
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Computes the length of this cone.
     * @return The length of this cone. 
     */
    public double length() {
        return axis().magnitude();
    }
    
    /**
     * Returns the axis of this cone.
     * @return A 3D vector pointing from the cone's base center to apex. 
     */
    public Vector3D axis() {
        return getApex().subtract(getBaseCenter());
    }

    /**
     * Computes the surface area of this cone. The cone's surface are is defined
     * by  SA = Pi*R^2 + Pi*R*s where s is the length of the apex to a point on
     * the out edge of the base.
     * @return  The surface area of this shape.
     */
    public double surfaceArea() {
        double h= apex.distance(baseCenter);
        double s= Math.sqrt((h*h)+ (baseRadius*baseRadius));
        //Pi*R^2 + Pi*R*s
        return (Math.PI*baseRadius*(s + baseRadius));
    }

    /**
     * Computes the volume of this cone. The cone's volume is described by
     * V = (1/3)B*h. 
     * @return  The volume of this shape.
     */
    public double volume() {
        //(1/3)B*h
        return (apex.distance(baseCenter)/3)*(Math.PI*(baseRadius*baseRadius));
    }

    /**
     * Gives the angle between side to side from the cones apex
     * @return Angle in radians of the cone's angle. Angle is between -pi/2 and pi/2.
     */
    public double coneAngle(){

        return Math.atan(this.baseRadius/(this.baseCenter.distance(this.apex)));
    }
    
    /**
     * Computes this cone's minimum bounding box.
     * @return  This cone's minimum bounding box.
     */
    public BoundingBox3D boundingBox() {
        LinkedList<Vector3D> worldVerts = new LinkedList<Vector3D>();

        Vector3D wApex = toWorld(getApex());        
        Vector3D wBase = toWorld(getBaseCenter());
        Vector3D axis = axis().unitVector(); 

        // length of X, Y, Z looking at a disk with a bounding box perspective
        double kX = Math.sqrt((axis.getY()*axis.getY()) + (axis.getZ()*axis.getZ()));
        double kY = Math.sqrt((axis.getX()*axis.getX()) + (axis.getZ()*axis.getZ()));
        double kZ = Math.sqrt((axis.getX()*axis.getX()) + (axis.getY()*axis.getY()));

        //add length of X,Y,Z to the orignal disk center
        Vector3D baseExtremaA = new Vector3D(wBase.getX()-kX*baseRadius, wBase.getY()-kY*baseRadius, wBase.getZ()-kZ*baseRadius);
        Vector3D baseExtremaB = new Vector3D(wBase.getX()+kX*baseRadius, wBase.getY()+kY*baseRadius, wBase.getZ()+kZ*baseRadius);

        //Centers
        worldVerts.add(wApex);
        worldVerts.add(wBase);
        //bounds of base
        worldVerts.add(baseExtremaA);
        worldVerts.add(baseExtremaB);

        // create bounding box using world coords
        return new BoundingBox3D(worldVerts);
    }


    /*********************************************
     * MARK: Other
     *********************************************/

    /**
     * Determines whether the given vertices, in local coordinates,
     * describes a valid cone.
     *
     * @param apex       A 3D vector describing the position of the top of the
     *                   cone's center in local coordinates
     * @param baseCenter A 3D vector describing the center of the cone's base
     *                   local coordinates.
     * @param baseRadius A <code>double</code> which specifies the radius of the
     *                   circular base.
     * @return          <code>true</code> if the given list of vertices define a
     *                  valid cone.The defined cones center is directly in
     *                  between the apex and the base center.
     *                  origin; <code>false</code> otherwise.
     */
    private boolean validateCone(Vector3D apex, Vector3D baseCenter, double baseRadius){
        //valid radius? different points of the cone?
        return (baseRadius > 0 && !apex.equals(baseCenter));
    }
}
