package org.rowan.linalgtoolkit.logic3d;

import org.rowan.linalgtoolkit.shapes3d.*;

/**
 * The <code>IntersectionVolume3D</code> class provides computation logic for
 * determining the volume of intersection between two shapes in the <code>shapes3d</code>
 * package.
 *
 * @author Michael Liguori
 * @version 1.1
 * @since 1.1
 */
public class IntersectionVolume3D {

    /*********************************************
     * MARK: Polyhedron
     *********************************************/
    /**
     * Computes the volume of intersection between two given 3D polyhedron.
     *
     * @param polyhedronOne  A <code>Polyhedron3D</code>.
     * @param polyhedronTwo  A <code>Polyhedron3D</code>.
     * @return          The volume of intersection between <code>polyhedronOne</code> and
     *                  <code>polyhedronTwo</code>.
     */
    public static double intersectionVolume(Polyhedron3D polyhedronOne, Polyhedron3D polyhedronTwo) {
        return polyhedronOne.boundingBox().intersection(polyhedronTwo.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D polyhedron and 3D
     * cone.
     *
     * @param polyhedron  A <code>Polyhedron3D</code>.
     * @param cone        A <code>Cone3D</code>.
     * @return          The volume of intersection between <code>polyhedron</code> and
     *                  <code>cone</code>.
     */
    public static double intersectionVolume(Polyhedron3D polyhedron, Cone3D cone) {
        return polyhedron.boundingBox().intersection(cone.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D polyhedron and 3D
     * cylinder.
     *
     * @param polyhedron  A <code>Polyhedron3D</code>.
     * @param cylinder    A <code>Cylinder3D</code>.
     * @return          The volume of intersection between <code>polyhedron</code> and
     *                  <code>cylinder</code>.
     */
    public static double intersectionVolume(Polyhedron3D polyhedron, Cylinder3D cylinder) {
        return polyhedron.boundingBox().intersection(cylinder.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D polyhedron and 3D
     * sphere.
     *
     * @param polyhedron  A <code>Polyhedron3D</code>.
     * @param sphere      A <code>Sphere3D</code>.
     * @return          The volume of intersection between <code>polyhedron</code> and
     *                  <code>sphere</code>.
     */
    public static double intersectionVolume(Polyhedron3D polyhedron, Sphere3D sphere) {
        return polyhedron.boundingBox().intersection(sphere.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D polyhedron and 3D
     * spheroid.
     *
     * @param polyhedron  A <code>Polyhedron3D</code>.
     * @param spheroid    A <code>Spheroid3D</code>.
     * @return          The volume of intersection between <code>polyhedron</code> and
     *                  <code>spheroid</code>.
     */
    public static double intersectionVolume(Polyhedron3D polyhedron, Spheroid3D spheroid) {
        return polyhedron.boundingBox().intersection(spheroid.boundingBox()).volume();
    }


    /**
     * Computes the volume of intersection between a given 3D polyhedron and 3D
     * ellipsoid.
     *
     * @param polyhedron  A <code>Polyhedron3D</code>.
     * @param ellipsoid   A <code>Ellipsoid3D</code>.
     * @return          The volume of intersection between <code>polyhedron</code> and
     *                  <code>ellipsoid</code>.
     */
    public static double intersectionVolume(Polyhedron3D polyhedron, Ellipsoid3D ellipsoid) {
        return polyhedron.boundingBox().intersection(ellipsoid.boundingBox()).volume();
    }


    /*********************************************
     * MARK: Cone
     *********************************************/

    /**
     * Computes the volume of intersection between two given 3D cones.
     *
     * @param coneOne   A <code>Cone3D</code>.
     * @param coneTwo   A <code>Cone3D</code>.
     * @return          The volume of intersection between <code>coneOne</code> and
     *                  <code>coneTwo</code>.
     */
    public static double intersectionVolume(Cone3D coneOne, Cone3D coneTwo) {
        return coneOne.boundingBox().intersection(coneTwo.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D cone and 3D
     * cylinder.
     *
     * @param cone      A <code>Cone3D</code>.
     * @param cylinder  A <code>Cylinder3D</code>.
     * @return          The volume of intersection between <code>cone</code> and
     *                  <code>cylinder</code>.
     */
    public static double intersectionVolume(Cone3D cone, Cylinder3D cylinder) {
        return cone.boundingBox().intersection(cylinder.boundingBox()).volume();
    }

     /**
     * Computes the volume of intersection between a given 3D cone and 3D
     * sphere.
     *
     * @param cone      A <code>Cone3D</code>.
     * @param sphere    A <code>Sphere3D</code>.
     * @return          The volume of intersection between <code>cone</code> and
     *                  <code>sphere</code>.
     */
    public static double intersectionVolume(Cone3D cone, Sphere3D sphere) {
        return cone.boundingBox().intersection(sphere.boundingBox()).volume();
    }


    /**
     * Computes the volume of intersection between a given 3D cone and 3D
     * spheroid.
     *
     * @param cone      A <code>Cone3D</code>.
     * @param spheroid  A <code>Spheroid3D</code>.
     * @return          The volume of intersection between <code>cone</code> and
     *                  <code>spheroid</code>.
     */
    public static double intersectionVolume(Cone3D cone, Spheroid3D spheroid) {
        return cone.boundingBox().intersection(spheroid.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D cone and 3D
     * ellipsoid.
     *
     * @param cone      A <code>Cone3D</code>.
     * @param ellipsoid A <code>Ellipsoid3D</code>.
     * @return          The volume of intersection between <code>cone</code> and
     *                  <code>ellipsoid</code>.
     */
    public static double intersectionVolume(Cone3D cone, Ellipsoid3D ellipsoid) {
        return cone.boundingBox().intersection(ellipsoid.boundingBox()).volume();
    }

    /*********************************************
     * MARK: Cylinder
     *********************************************/

    /**
     * Computes the volume of intersection between two given 3D cylinders.
     *
     * @param cylinderOne A <code>Cylinder3D</code>.
     * @param cylinderTwo A <code>Cylinder3D</code>.
     * @return          The volume of intersection between <code>cylinderOne</code> and
     *                  <code>cylinderTwo</code>.
     */
    public static double intersectionVolume(Cylinder3D cylinderOne, Cylinder3D cylinderTwo) {
        return cylinderOne.boundingBox().intersection(cylinderTwo.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D cylinder and 3D
     * sphere.
     *
     * @param cylinder  A <code>Cylinder3D</code>.
     * @param sphere    A <code>Sphere3D</code>.
     * @return          The volume of intersection between <code>cylinder</code> and
     *                  <code>sphere</code>.
     */
    public static double intersectionVolume(Cylinder3D cylinder, Sphere3D sphere) {
        return cylinder.boundingBox().intersection(sphere.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D cylinder and 3D
     * spheroid.
     *
     * @param cylinder  A <code>Cylinder3D</code>.
     * @param spheroid  A <code>Spheroid3D</code>.
     * @return          The volume of intersection between <code>cylinder</code> and
     *                  <code>spheroid</code>.
     */
    public static double intersectionVolume(Cylinder3D cylinder, Spheroid3D spheroid) {
        return cylinder.boundingBox().intersection(spheroid.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D cylinder and 3D
     * ellipsoid.
     *
     * @param cylinder  A <code>Cylinder3D</code>.
     * @param ellipsoid A <code>Ellipsoid3D</code>.
     * @return          The volume of intersection between <code>cylinder</code> and
     *                  <code>ellipsoid</code>.
     */
    public static double intersectionVolume(Cylinder3D cylinder, Ellipsoid3D ellipsoid) {
        return cylinder.boundingBox().intersection(ellipsoid.boundingBox()).volume();
    }

    /*********************************************
     * MARK: Sphere
     *********************************************/

    /**
     * Computes the volume of intersection between two given 3D spheres.
     *
     * @param sphereOne A <code>Sphere3D</code>.
     * @param sphereTwo A <code>Sphere3D</code>.
     * @return          The volume of intersection between <code>sphereOne</code> and
     *                  <code>sphereTwo</code>.
     */
    public static double intersectionVolume(Sphere3D sphereOne, Sphere3D sphereTwo) {
        //From Wolfram Mathematica

        //Radius and Equation Simplification
        double r1 = sphereOne.getMajorRadius(); 
        double r2 = sphereTwo.getMajorRadius();
        double radii = r1 + r2;
        double deltaR = r1 - r2;

        //distance between the centers
        double d = sphereOne.getPosition().distance(sphereTwo.getPosition());
        

        //Oringal Equation is [ PI(r1 + r2 - d)^2 (d^2 + 2d(r2) - 3r^2 + 2d(r1) + 6(r1)(r2) - 3(r2)^2 ] / 12d
        return (Math.PI * (radii - d) * (radii - d) * ((d * d) + (2 * d) * (radii) - 3 * (deltaR * deltaR))) / (12 * d);
                
    }

    /**
     * Computes the volume of intersection between a given 3D sphere and 3D
     * spheroid.
     *
     * @param sphere   A <code>Sphere3D</code>.
     * @param spheroid A <code>Spheroid3D</code>.
     * @return          The volume of intersection between <code>sphere</code> and
     *                  <code>spheroid</code>.
     */
    public static double intersectionVolume(Sphere3D sphere, Spheroid3D spheroid) {
        return sphere.boundingBox().intersection(spheroid.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D sphere and 3D
     * ellipsoid.
     *
     * @param sphere    A <code>Sphere3D</code>.
     * @param ellipsoid A <code>Ellipsoid3D</code>.
     * @return          The volume of intersection between <code>sphere</code> and
     *                  <code>ellipsoid</code>.
     */
    public static double intersectionVolume(Sphere3D sphere, Ellipsoid3D ellipsoid) {
        return sphere.boundingBox().intersection(ellipsoid.boundingBox()).volume();
    }

    /*********************************************
     * MARK: Spheroid
     *********************************************/

    /**
     * Computes the volume of intersection between two given 3D spheroids.
     *
     * @param spheroidOne A <code>Spheroid3D</code>.
     * @param spheroidTwo A <code>Spheroid3D</code>.
     * @return          The volume of intersection between <code>spheroidOne</code> and
     *                  <code>spheroidTwo</code>.
     */
    public static double intersectionVolume(Spheroid3D spheroidOne, Spheroid3D spheroidTwo) {
        return spheroidOne.boundingBox().intersection(spheroidTwo.boundingBox()).volume();
    }

    /**
     * Computes the volume of intersection between a given 3D spheroid and 3D
     * ellipsoid.
     *
     * @param spheroid  A <code>Spheroid3D</code>.
     * @param ellipsoid A <code>Ellipsoid3D</code>.
     * @return          The volume of intersection between <code>spheroid</code> and
     *                  <code>ellipsoid</code>.
     */
    public static double intersectionVolume(Spheroid3D spheroid, Ellipsoid3D ellipsoid) {
        return spheroid.boundingBox().intersection(ellipsoid.boundingBox()).volume();
    }


    /*********************************************
     * MARK: Ellipsoid
     *********************************************/

     /**
     * Computes the volume of intersection between two given 3D ellipsoids.
     *
     * @param ellipsoidOne A <code>Ellipsoid3D</code>.
     * @param ellipsoidTwo A <code>Ellipsoid3D</code>.
     * @return          The volume of intersection between <code>ellipsoidOne</code> and
     *                  <code>ellipsoidTwo</code>.
     */
    public static double intersectionVolume(Ellipsoid3D ellipsoidOne, Ellipsoid3D ellipsoidTwo) {
        return ellipsoidOne.boundingBox().intersection(ellipsoidTwo.boundingBox()).volume();
    }


}
