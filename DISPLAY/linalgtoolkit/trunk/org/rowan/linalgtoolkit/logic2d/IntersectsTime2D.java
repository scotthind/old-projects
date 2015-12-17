package org.rowan.linalgtoolkit.logic2d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>IntersectsTime2D</code> class provides boolean intersection
 * prediction query logic for shapes in the <code>shapes2d</code> package.
 *
 * @author Michael Liguori
 * @version 1.1
 * @since 1.1
 */
public abstract class IntersectsTime2D {

    /*********************************************
     * MARK: Point-Circle
     *********************************************/

    /**
     * Determines whether two given shapes will intersect in a given amount of time.
     * This method is designed for a <code>Point2D</code> instance or the center
     * of a <code>Circle2D</code> instance to be the moving object. An instance
     * of <code>Shape2D</code> should be the boundary object.
     * @param movObj      A <code>Point2D</code> instance that is the moving object.
     * @param boundaryObj A <code>Shape2D</code> instance that is the boundary object.
     * @param padding     Amount of distance that the moving point can be from
     *                    the boundary object.
     * @param time        The given amount of time for intersection.
     * @return      <code>true</code> if <code>movObj</code> will intersect
     *              <code>boundaryObj</code> in the given time;
     *              <code>false</code> otherwise.
     */
    public static boolean intersectsTime(Point2D movObj, Shape2D boundaryObj, double padding, double time) {
        //position of point2 after velocity* time
        Vector2D finalPos = movObj.getPosition().add(movObj.getVelocity().multiply(time));
        //line from start to finish
        Segment2D path = new Segment2D(movObj.getPosition(),finalPos);

        //at anypoint if the distance is within range. It Intersects
        return path.intersects(boundaryObj) || path.distance(boundaryObj) <= padding;
    }

    /*********************************************
     * MARK: Segment2D-Polygon2D
     *********************************************/

    /**
     * Determines whether two given shapes will intersect in a given amount of time.
     * This method is designed for a <code>Segment2D</code> instance or
     * <code>Polygon2D</code> instance to be the moving object. An instance
     * of <code>Point2D</code>, <code>Segment2D</code>, <code>Polygon2D</code>
     * should be the boundary object.
     * @param movObj       A <code>Shape2D</code> instance that is the moving object.
     * @param boundaryObj  A <code>Shape2D</code> instance that is the boundary object.
     * @param time        The given amount of time for intersection.
     * @return          <code>true</code> if <code>movObj</code>  will intersect
     *                  <code>boundaryObj</code>, in the given time of time;
     *                  <code>false</code> otherwise.
     */
    public static boolean intersectsTime(Shape2D movObj, Shape2D boundaryObj, double time) {
        boolean intersect = false;
        LinkedList<Segment2D> sides = new LinkedList<Segment2D>();

       //Does the moving object contain sides? Possible Axis
        if(movObj instanceof Segment2D){
            Segment2D seg = (Segment2D)movObj;
            sides.add(seg);
        }else if (movObj instanceof Polygon2D){
            Polygon2D poly = (Polygon2D)movObj;
            sides.addAll(poly.getEdges());
        }

        //Does the boundary Object contain sides? Possible Axis
        if(boundaryObj instanceof Segment2D){
            Segment2D seg = (Segment2D)boundaryObj;
            sides.add(seg);
        }else if (boundaryObj instanceof Polygon2D){
            Polygon2D poly = (Polygon2D)boundaryObj;
            sides.addAll(poly.getEdges());
        }

        //Axis from closest point between objects
        Vector2D[] closestPoints = LinAlg2D.closest(movObj.getWorldVertices(),boundaryObj.getWorldVertices());
        Segment2D closestAxis = new Segment2D(closestPoints[0],closestPoints[1]);
        closestAxis.rotate(Math.PI/2);
        sides.add(closestAxis);

        //Store the original position to reset at the end
        Vector2D origPosition = movObj.getPosition();

        //Loop through sides
        for(Segment2D side : sides){
            //Axis of separating axis theorum are perpendicular to
            Vector2D vDelta = side.deltaVect();
            Vector2D axis = new Vector2D(-vDelta.getY(),vDelta.getX()).unitVector();

            //Projected Velocity in the direction of the axis
            double relativeVelocity = axis.dot(movObj.getVelocity());
            //Projection on axis. Min, Max of the shapes on the axis
            double[] projA = ShapeProjection(movObj,axis);
            double[] projB = ShapeProjection(boundaryObj,axis);
            
            //Get closest Distance
            double dist = (projA[0] < projB[0]) ? projB[0] - projA[1] : projA[0] - projB[1];
 
            //The predicted time of intersection
            double projTimeIntersect = Math.abs(dist/relativeVelocity);

            //try intersection at the given time
            if(projTimeIntersect <= time){
                movObj.setPosition(origPosition.add(movObj.getVelocity().multiply(projTimeIntersect)));
            }

            //Check to see if they intersect
            if(movObj.intersects(boundaryObj)){
                intersect = true;
                break;
            }
        }

        //reset position
        movObj.setPosition(origPosition);
        return intersect;
    }


    /**
     * Projects the given shape onto the given axis. Returns the min and max
     * respectively in an array of size 2 of the project onto the given axis
     * @param shape A <code>Shape2D</code> instance.
     * @param axis  A <code>Vector2D</code> instance representing the axis to be
     *              projected onto
     * @return      An array of size 2. Min projection in the 0 position and
     *              Max in the 1 position of the array.
     */
    private static double[] ShapeProjection(Shape2D shape, Vector2D axis){
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double d;

        if(shape instanceof Point2D){
            //Project point on the axis, bounds are equal
            Point2D p = (Point2D)shape;
            min = axis.dot(p.getPosition());
            max = axis.dot(p.getPosition());
        }else if(shape instanceof Segment2D){
            //Segment project on axis
            Segment2D seg = (Segment2D)shape;
            d = seg.toWorld(seg.getStart()).dot(axis);
            double d2 = seg.toWorld(seg.getEnd()).dot(axis);

            if(d < d2){
                max = d2;
                min = d;
            }else{
                min = d2;
                max = d;
            }

        }else if(shape instanceof Polygon2D){
            //Polygon projection on axis
            Polygon2D poly = (Polygon2D)shape;
            for (Vector2D p : poly.getWorldVertices()) {
                d = p.dot(axis);
                if (d < min)
                    min = d;
                else if (d > max)
                    max = d;
            }
        }else if(shape instanceof Circle2D){
            //Circle projection
            Circle2D c = (Circle2D)shape;
            d = axis.dot(c.getPosition());

            if(d + c.getRadius() < d){
                min = d + c.getRadius();
                max = d - c.getRadius();
            }else{
                min = d - c.getRadius();
                max = d + c.getRadius();
            }
        }

        return new double[]{
            min,max
        };

    }



}