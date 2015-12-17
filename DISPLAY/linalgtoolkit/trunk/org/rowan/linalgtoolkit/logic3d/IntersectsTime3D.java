package org.rowan.linalgtoolkit.logic3d;

import java.util.LinkedList;
import org.rowan.linalgtoolkit.BoundingBox3D;
import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.WGS84Coord;
import org.rowan.linalgtoolkit.shapes3d.*;
import static java.lang.Math.*;

/**
 * The <code>IntersectsTime3D</code> class provides boolean intersection
 * prediction query logic for shapes in the <code>shapes3d</code> package.
 *
 * @author Michael Liguori
 * @version 1.1
 * @since 1.1
 */
public class IntersectsTime3D {

    /*********************************************
     * MARK: Point
     *********************************************/
    /**
     * Determines whether a given 3D point will intersect a given <code>Shape3D</code>
     * within the given amount of time.
     *
     *
     * @param point     The parent <code>Point3D</code>.
     * @param shape     A <code>Shape3D</code>.
     * @return          <code>true</code> if <code>point</code> will intersect
     *                  the given <code>Shape3D</code> in given time.
     */
    public static boolean intersects(Point3D point, Shape3D shape, double time) {
        Vector3D start = point.getPosition();
        Vector3D end = point.getPosition().add(point.getVelocity().multiply(time));

        Segment3D segment = new Segment3D(start, end);

        return segment.intersects(shape);
    }

    /*********************************************
     * MARK: Segment
     *********************************************/
    /**
     * Determines whether a given 3D segment will intersect a given <code>Shape3D</code>
     * within the given amount of time.
     *
     *
     * @param segment   The parent <code>Segment3D</code>.
     * @param shape     A <code>Shape3D</code>.
     * @return          <code>true</code> if <code>segment</code> will intersect
     *                  the given <code>Shape3D</code> in given time.
     */
    public static boolean intersects(Segment3D segment, Shape3D shape, double time) {
        Vector3D start = segment.toWorld(segment.getStart());
        Vector3D end = segment.toWorld(segment.getEnd());

        Vector3D projectedStart = start.add(segment.getVelocity().multiply(time));
        Vector3D projectedEnd = end.add(segment.getVelocity().multiply(time));


        LinkedList<Vector3D> points = new LinkedList<Vector3D>();
        points.add(start);
        points.add(projectedStart);
        points.add(projectedEnd);
        points.add(end);
        //This means the projection of the segment created a line
        if (end.subtract(start).isParallel(projectedEnd.subtract(start))) {
            Vector3D e1 = points.get(0);
            Vector3D e2 = points.get(1);

            for (int n = 2; n < points.size(); n++)
                if (!LinAlg3D.isBetween(points.get(n), e1, e2))
                    if (LinAlg3D.isBetween(e1, e2, points.get(n)))
                        e1 = points.get(n);
                    else
                        e2 = points.get(n);

            return new Segment3D(e1, e2).intersects(shape);
        }

        //We know the projected points create a bounded plane. See if it intersects

        BoundingBox3D bb = shape.boundingBox();
        Face3D f = new Face3D(points);
        //check to see if any of the line of the face intersects the bounding box.
        for (int i = 0; i < points.size(); i++) {
            Segment3D side = new Segment3D(points.get(i), points.get((i + 1) % points.size()));
            if (side.intersects(shape))
                return true;
        }

        //no sides are in the middle but the shape could be inside the plane untouched


        return new BoundingBox3D(points).intersects(shape.boundingBox());


    }

    /*********************************************
     * MARK: Sphere
     *********************************************/
    /**
     * Determines whether a given 3D sphere will intersect a given <code>Shape3D</code>
     * within the given amount of time.
     *
     *
     * @param sphere    The parent <code>Sphere3D</code>.
     * @param shape     A <code>Shape3D</code>.
     * @return          <code>true</code> if <code>sphere</code> will intersect
     *                  the given <code>Shape3D</code> in given time.
     */
    public static boolean intersects(Sphere3D sphere, Shape3D shape, double time) {
        Vector3D start = sphere.getPosition();
        Vector3D end = sphere.getPosition().add(sphere.getVelocity().multiply(time));

        Segment3D segment = new Segment3D(start, end);

        return (segment.distance(shape) <= sphere.getMajorRadius());
    }

    
    /*********************************************
     * MARK: BoundingBox
     *********************************************/
    
    /**
     * Determines if the moving <code>Shape3D</code> object, <code>moving</code>,
     * will intersect the stationary <code>Shape3D</code> object, <code>stationary</code>
     * in the given amount of time in seconds.
     *
     * <p>
     * The moving object has a vector which represents the distance in units the
     * <code>Shape3D</code> will travel in one second.
     * The stationary object cannot be moving.
     * Time must be positive.
     * </p>
     *
     * @param moving        The moving <code>Shape3D</code>.
     * @param stationary    The stationary <code>Shape3D</code>.
     * @return          <code>true</code> if the moving <code>Shape3D</code> will
     *                  intersect the stationary <code>Shape3D</code> in the
     *                  given amount of time.
     * @throws IllegalArgumentException If either given <code>Shape3D</code>
     *                  instance is not supported, if time is negative, or if
     *                  the stationary object is moving.
     */
    public static boolean intersects(Shape3D moving, Shape3D stationary, double time) {
        if (time < 0)
            throw new IllegalArgumentException("Time cannot be negative");

        if (!stationary.getVelocity().isZeroVector())
            throw new IllegalArgumentException("The stationary object cannot be moving");

        //currently intersecting?
        if (moving.intersects(stationary))
            return true;

        double[] predictedTimes = new double[3];

        //this is a current bounding box implementation of this method.

        Vector3D movingA = moving.boundingBox().getA();
        Vector3D movingB = moving.boundingBox().getB();

        Vector3D stationaryA = stationary.boundingBox().getA();
        Vector3D stationaryB = stationary.boundingBox().getB();

        //intersect on X axis?
        double axisVelocity = moving.getVelocity().getX();
        double dist = (movingA.getX() < stationaryA.getX()) ? stationaryA.getX() - movingB.getX() : movingA.getX() - stationaryB.getX();
        predictedTimes[0] = (dist / axisVelocity);
        //intersect on Y axis?
        axisVelocity = moving.getVelocity().getY();
        dist = (movingA.getY() < stationaryA.getY()) ? stationaryA.getY() - movingB.getY() : movingA.getY() - stationaryB.getY();
        predictedTimes[1] = (dist / axisVelocity);
        //intersect on Z axis?
        axisVelocity = moving.getVelocity().getZ();
        dist = (movingA.getZ() < stationaryA.getZ()) ? stationaryA.getZ() - movingB.getZ() : movingA.getZ() - stationaryB.getZ();
        predictedTimes[2] = (dist / axisVelocity);


        Vector3D origPosition = moving.getPosition();
        Vector3D intersectPosition;
        for (int i = 0; i < predictedTimes.length; i++)
            if (predictedTimes[i] <= time) {
                intersectPosition = origPosition.add(moving.getVelocity().multiply(predictedTimes[i]));
                moving.setPosition(intersectPosition);
                if (moving.boundingBox().intersects(stationary.boundingBox())) {
                    moving.setPosition(origPosition);
                    return true;
                }
            }

        //they did not intersect at any times
        moving.setPosition(origPosition);
        return false;
    }

    /**
     * Determines whether a moving <code>WGS84Coord</code>  will intersect a stationary
     * <code>WGS84Coord</code> given the moving coordinates bearing, speed, and a given time.
     *
     * @param pointOne A <code>WGS84Coord</code> Object of the moving point.
     * @param pointTwo A <code>WGS84Coord</code> Object of the stationary point.
     * @param bearing The bearing of pointOne.
     * @param speed The speed in units per second of pointOne.
     * @param time Given amount of time in seconds.
     * @return  Whether the moving <code>WGS84Coord</code> will intersect the stationary
     *          <code>WGS84Coord</code>
     */
    public static boolean rhumbLineIntersect(WGS84Coord pointOne, WGS84Coord pointTwo, double bearing, double speed, double time) {
        //error for longitude and latitude differences
        double error = .3; // 0.186411358 miles.
        double distance = pointOne.rhumbLineDistance(pointTwo);
        
        //Will path of point one intersect latitude of point two.
        WGS84Coord destination = findDestinationWGS(pointOne, bearing, distance);
        
        //too far to reach in time
        if ((pointOne.rhumbLineDistance(destination))  > (speed * time))
            return false;

        //within latitude bounds?
        if (abs(destination.getLatitude() - pointTwo.getLatitude()) > error)
            return false;

        //within longitude bounds?
        if (abs(destination.getLongitude() - pointTwo.getLongitude()) > error)
            return false;

        //Coordinate is within the allowed error value, distance is within range.
        return true;
    }

        /**
     * Calculates the destination of a <code>WGS84Coord</code> given its distance and bearing.
     * @param start     The starting coordinate.
     * @param bearing   The shape's bearing, in degrees.
     * @param distance  The distance to travel in WGS84 coordinates. This is in km.
     * @return A <code>WGS84Coord</code>.
     */
     public static WGS84Coord findDestinationWGS(WGS84Coord start, double bearing, double distance){

          // formula credited to http://www.movable-type.co.uk/scripts/latlong.html
          double R = WGS84Coord.EARTH_EQUATORIAL_RADIUS;
          double d = (distance)/R;  // d = angular distance covered on earth's surface
          
          double lat1 = toRadians(start.getLatitude());
          double lon1 = toRadians(start.getLongitude());
          double brng = toRadians(bearing);

          double lat2 = lat1 + d*Math.cos(brng);
          double dLat = lat2-lat1;
          double dPhi = Math.log(Math.tan(lat2/2+Math.PI/4)/Math.tan(lat1/2+Math.PI/4));
          double q = (!Double.isNaN(dLat/dPhi)) ? dLat/dPhi : Math.cos(lat1);  // E-W line gives dPhi=0
          double dLon = d*Math.sin(brng)/q;

          // check for going past a pole
          if (Math.abs(lat2) > Math.PI/2)
              lat2 = lat2>0 ? Math.PI-lat2 : -(Math.PI-lat2);

          double lon2 = (lon1+dLon+3*Math.PI)%(2*Math.PI) - Math.PI;

          return new WGS84Coord(toDegrees(lat2), toDegrees(lon2));
     }

}
