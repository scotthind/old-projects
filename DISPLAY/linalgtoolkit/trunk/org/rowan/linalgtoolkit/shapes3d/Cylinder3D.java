package org.rowan.linalgtoolkit.shapes3d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.BoundingBox3D;

/**
 * The <code>Cylinder3D</code> class describes a shape which has two bases and
 * a given height which separate these bases. The bases are described as
 * circular shapes with a center point and a radius. Giving different radii for the
 * base and the top result in a conical frustum.
 *
 * @author Michael Liguori
 * @version 1.1
 * @since 1.1
 */
public class Cylinder3D extends Shape3D{

    /*********************************************
     * MARK: Fields
     *********************************************/

    /** The Vector3D describing the center of the top of the cylinder */
    private Vector3D apexCenter;

    /** The Vector3D describing the center of the base of the cylinder */
    private Vector3D baseCenter;

    /** The radius of the top of the cylinder */
    private double apexRadius;

    /** The radius of the base of the cylinder */
    private double baseRadius;

     /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Creates a <code>Cylinder3D</code> object, defined by a given apex and base 
     * center point, in world coordinates, and a given apex and base radii.
     *
     * @param apexCenter A 3D vector describing the position of the top of the cylinders
     *                   center in local coordinates
     * @param apexRadius A <code>double</code> which specifies the radius of the
     *                   circular top.
     * @param baseCenter A 3D vector describing the center of the circular base of the
     *                   cone in local coordinates.
     * @param baseRadius A <code>double</code> which specifies the radius of the
     *                   circular base.
     * @throws IllegalArgumentException If a given radius is less than or equal 
     *                  to 0.
     */
    public Cylinder3D(Vector3D baseCenter, double baseRadius, Vector3D apexCenter, double apexRadius) {
        // initialize with super constructor
        super();
        
        // calculate position
        Vector3D halfAxis = apexCenter.subtract(baseCenter).multiply(0.5);
        setPosition(baseCenter.add(halfAxis));

        if (!validateCylinder(toLocal(baseCenter), baseRadius, toLocal(apexCenter), apexRadius))
            throw new IllegalArgumentException("Invalid vertices: Given vertices " +
                                               "do not describe a valid cylinder ");

        // set the values of the cylinder
        this.baseCenter = toLocal(baseCenter);
        this.baseRadius = baseRadius;
        this.apexCenter = toLocal(apexCenter);
        this.apexRadius = apexRadius;
    }

    /**
     * Creates a <code>Cylinder3D</code> object, defined by a given apex and base 
     * center point, in world coordinates, and a given common radius.
     * @param apexCenter A 3D vector describing the position of the top of the cylinders
     *                   center in local coordinates
     * @param baseCenter A 3D vector describing the center of the circular base of the
     *                   cone in local coordinates.
     * @param radius    A <code>double</code> which specifies the radius of the
     *                  cylinder's bases.
     * @throws IllegalArgumentException If a given radius is less than or
     *                  equal to 0.
     */
    public Cylinder3D(Vector3D baseCenter, double radius, Vector3D apexCenter) {
        this(baseCenter, radius, apexCenter, radius);
    }


    /*********************************************
     * MARK: Accessors
     *********************************************/

    /**
     * The Center of the <code>apex</code> surface.
     * @return A <code>Vector3D</code> describing the center of the apex surface.
     */
    public Vector3D getApexCenter(){
        return apexCenter.rotate(getOrientation());
    }
    /**
     * The Center of the <code>base</code> surface.
     * @return A <code>Vector3D</code> describing the center of the base surface.
     */
    public Vector3D getBaseCenter(){
        return baseCenter.rotate(getOrientation());
    }
    /**
     * The radius of the base.
     * @return The Length of the base's radius.
     */
    public double getBaseRadius(){
        return baseRadius;
    }
    /**
     * The radius of the apex.
     * @return The length of the apex's radius
     */
    public double getApexRadius(){
        return apexRadius;
    }

    
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Computes the length of this cylinder.
     * @return The length of this cylinder. 
     */
    public double length() {
        return axis().magnitude();
    }
    
    /**
     * Returns the axis of this cylinder.
     * @return A 3D vector pointing from the cylinder's base to apex. 
     */
    public Vector3D axis() {
        return getApexCenter().subtract(getBaseCenter());
    }
    
     /**
     * Computes the surface area of this cylinder. If the cylinder has an equal
     * radius at the base and apex. Then the cylinders surface area is
     * SA = 2*Pi*R*(R+H). If the cylinder has different radii then the surface
     * area is SA = PI(R1 + R2)*SQRT(R1-R2)^2+h^2)
     * @return  The surface area of this shape.
     */
    public double surfaceArea() {
        //Regular Surface Area = 2*PI*R(R+H)
        if(this.apexRadius == this.baseRadius)
            return (2*Math.PI* baseRadius *(baseRadius + baseCenter.distance(apexCenter)));

        //Truncated Cone  Suface Area = PI(R1 + R2)*SQRT(R1-R2)^2+h^2)
        return (Math.PI*(baseRadius + apexRadius)*Math.sqrt(Math.pow(baseRadius-apexRadius,2)
             + (Math.pow(apexCenter.distance(baseCenter),2))));

    }

    /**
     * Computes the volume of this cylinder. If the cylinder has an equal
     * radius at the base and apex. Then the cylinders volume is V = 2PI(R^2)H.
     * If the cylinder has different radii then the volume is
     * V = (h/3)*[A1+A2+sqrt(A1*A2)].
     * @return  The volume of this shape.
     */
    public double volume() {
        double baseArea = 2*Math.PI*(baseRadius*baseRadius);
        double h = apexCenter.distance(baseCenter);

        //Regular Cylinder Volume = 2PI(R^2)H
        if(this.apexRadius == this.baseRadius)
            return (baseArea*h);

        //truncated
        //V=(h/3)*[A1+A2+sqrt(A1*A2)],
        double apexArea = 2*Math.PI*(apexRadius*apexRadius);
        return ((h/3) * (baseArea + apexArea + Math.sqrt(baseArea*apexArea)));
    }
    
     /**
     * Computes this cylinder's minimum bounding box.
     * @return  This cylinder's minimum bounding box.
     */
     public BoundingBox3D boundingBox() {
        LinkedList<Vector3D> worldVerts = new LinkedList<Vector3D>();

        Vector3D wApex = toWorld(getApexCenter());
        Vector3D wBase = toWorld(getBaseCenter());
        Vector3D axis = axis().unitVector();

        // length of X, Y, Z looking at a disk with a bounding box perspective
        double kX = Math.sqrt((axis.getY()*axis.getY()) + (axis.getZ()*axis.getZ()));
        double kY = Math.sqrt((axis.getX()*axis.getX()) + (axis.getZ()*axis.getZ()));
        double kZ = Math.sqrt((axis.getX()*axis.getX()) + (axis.getY()*axis.getY()));

        //add lengths of X,Y,Z to the disk Centers for bounds.
        Vector3D baseExtremaA = new Vector3D(wBase.getX()-kX*baseRadius, wBase.getY()-kY*baseRadius, wBase.getZ()-kZ*baseRadius);
        Vector3D baseExtremaB = new Vector3D(wBase.getX()+kX*baseRadius, wBase.getY()+kY*baseRadius, wBase.getZ()+kZ*baseRadius);

        Vector3D apexExtremaA = new Vector3D(wApex.getX()-kX*apexRadius, wApex.getY()-kY*apexRadius, wApex.getZ()-kZ*apexRadius);
        Vector3D apexExtremaB = new Vector3D(wApex.getX()+kX*apexRadius, wApex.getY()+kY*apexRadius, wApex.getZ()+kZ*apexRadius);

        //Centers
        worldVerts.add(wApex);
        worldVerts.add(wBase);
        //bounds of base
        worldVerts.add(baseExtremaA);
        worldVerts.add(baseExtremaB);
        //bounds of apex
        worldVerts.add(apexExtremaA);
        worldVerts.add(apexExtremaB);

        // create bounding box using world coords
        return new BoundingBox3D(worldVerts);
    }


    /*********************************************
     * MARK: Other
     *********************************************/

    /**
     * Determines whether the given vertices, in local coordinates,
     * describes a valid cylinder.
     *
     * @param apexCenter A 3D vector describing the position of the top of the cylinders
     *                   center in local coordinates
     * @param apexRadius A <code>double</code> which specifies the radius of the
     *                   circular top.
     * @param baseCenter A 3D vector describing the center of the circular base of the
     *                   cone in local coordinates.
     * @param baseRadius A <code>double</code> which specifies the radius of the
     *                   circular base.
     * @return          <code>true</code> if the given parameters describe a valid
     *                  cylinder and the defined cylinder surrounds the local
     *                  origin; <code>false</code> otherwise.
     */
    private boolean validateCylinder(Vector3D baseCenter, double baseRadius, Vector3D apexCenter, double apexRadius){
        //Cylinder cannot have a raidus of 0 on either base. Must have origin between
        return (baseRadius > 0 && apexRadius > 0 && !baseCenter.equals(apexCenter) && baseRadius >= apexRadius);
    }

}
