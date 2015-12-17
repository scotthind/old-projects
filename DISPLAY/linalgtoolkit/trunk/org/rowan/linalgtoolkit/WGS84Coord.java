package org.rowan.linalgtoolkit;

import static java.lang.Math.*;

/**
 * The <code>WGS84Coord</code> class describes a coordinate in the World Geodetic
 * System. All latitude and longitude values are in degrees.
 * 
 * @author Spence DiNicolantonio
 * @version 1.1
 * @since 1.1
 */
public class WGS84Coord {
    
    /*********************************************
     * MARK: Enums
     *********************************************/
    
    /** Manners in which altitude is considered during distance calculations. */
    public enum DistanceMode {
        
        /** Distance is calculated at sea level, disregarding altitude (default mode). */ 
        SEA_LEVEL,
        
        /** Distance is calculated at the lesser of the two altitude coordinates. */
        MIN,
        
        /** Distance is calculated at the greater of the two altitude coordinates. */
        MAX,
        
        /** Distance is calculated at the mean altitude of the two coordinates. */ 
        MEAN
    }
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The earth's equatorial radius, in kilometers. */
    public static final double EARTH_EQUATORIAL_RADIUS = 6378.137;
    
    /** The earth's polor radius, in kilometers. */
    public static final double EARTH_POLAR_RADIUS = 6356.75231425;
    
    /** The earth's average radius, in kilometers. */
    public static final double EARTH_MEAN_RADIUS = 6371;

    /** The earth's flattening constant. */
    public static final double EARTH_FLATENING = 1 / 298.257223563;

    /** The earth's eccentricity constant. */
    public static final double EARTH_ECCENTRICITY = sqrt(((EARTH_EQUATORIAL_RADIUS * EARTH_EQUATORIAL_RADIUS)
                                                          - (EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS)) 
                                                         / (EARTH_EQUATORIAL_RADIUS * EARTH_EQUATORIAL_RADIUS));

    /** The earth's second eccentricity constant. */
    public static final double EARTH_SECOND_ECCENTRICITY = sqrt(((EARTH_EQUATORIAL_RADIUS * EARTH_EQUATORIAL_RADIUS)
                                                                 - (EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS)) 
                                                                / (EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS));

    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The latitude value of this coordinate. */
    private double latitude;
    
    /** The longitude value of this coordinate. */
    private double longitude;
    
    /** The altitude of this coordinate, in kilometers from the earth's surface. */
    private double altitude;


    /*********************************************
     * MARK: Constructors
     *********************************************/
        
    /**
     * Creates a <code>WGS84Coord</code> with a given latitude, longitude, and
     * altitude.
     * @param latitude  The latitude value of the coordinate.
     * @param longitude The longitude value of the coordinate.
     * @param altitude  The height of the coordinate, in kilometers from the 
     * earth's surface.
     */
    public WGS84Coord(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
    
    /**
     * Creates a <code>WGS84Coord</code> on the earths surface, with a given 
     * latitude and longitude.
     * @param latitude  The latitude value of the coordinate.
     * @param longitude The longitude value of the coordinate.
     */
    public WGS84Coord(double latitude, double longitude) {
        this(latitude, longitude, 0.0);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the latitude value of this coordinate.
     * @return  The latitude value of this coordinate.
     */
    public double getLatitude() {
        return this.latitude;
    }
    
    /**
     * Returns the longitude value of this coordinate.
     * @return  The longitude value of this coordinate.
     */
    public double getLongitude() {
        return this.longitude;
    }
    
    /**
     * Returns the height of this coordinate.
     * @return  The height of this coordinate.
     */
    public double getAltitude() {
        return this.altitude;
    }
    
    
    /*********************************************
     * MARK: Arithmetic
     *********************************************/
    
    /**
     * Computes the rhumb line distance between this coordinate and a given 
     * WGS-84 coordinate at see level.
     * @param coord A <code>WGS84Coord</code>.
     * @return      The rhumb line distance between this coordinate and <code>coord</code>.
     */
    public double rhumbLineDistance(WGS84Coord coord) {        
        // convert latitude and longitude values to radians
        double lat1 = toRadians(this.latitude); 
        double lat2 = toRadians(coord.getLatitude());
        double lon1 = toRadians(this.longitude); 
        double lon2 = toRadians(coord.getLongitude());
        
        // compute latitude and longitude differences
        double latDiff = lat2 - lat1;
        double lonDiff = abs(lon2-lon1);
        
        
        double dPhi = log(tan(lat2/2 + PI/4) / tan(lat1/2 + PI/4));
        double q = (dPhi != 0)? latDiff/dPhi : cos(lat1);
        
        // if dLon over 180 take shorter rhumb across 180 meridian:
        if (lonDiff > PI) 
            lonDiff = 2*PI - lonDiff;
        
        return sqrt(latDiff*latDiff + q*q*lonDiff*lonDiff) * EARTH_MEAN_RADIUS; 
    }
    
    /**
     * Computes the great circle distance between this coordinate and a given 
     * WGS-84 coordinate at see level.
     * @param coord A <code>WGS84Coord</code>.
     * @return      The great circle distance between this coordinate and 
     *              <code>coord</code>.
     */
    public double greatCircleDistance(WGS84Coord coord) {
        return greatCircleDistance(coord, DistanceMode.SEA_LEVEL);
    }
    
    /**
     * Computes the great circle distance between this coordinate and a given 
     * WGS-84 coordinate using a given distance mode.
     * @param coord     A <code>WGS84Coord</code>.
     * @param distMode  A <code>DistanceMode</code> defining what altitude to use
     *                  in the distance calculation.
     * @return          The great circle distance between this coordinate and 
     *                  <code>coord</code>.
     */
    public double greatCircleDistance(WGS84Coord coord, DistanceMode distMode) {
        // determine altitude
        double altitude = 0.0;
        switch (distMode) {
            case MIN:
                altitude = Math.min(this.altitude, coord.getAltitude());
                break;
            case MAX:
                altitude = Math.max(this.altitude, coord.getAltitude());
                break;
            case MEAN:
                altitude = (this.altitude + coord.getAltitude())/2;
                break;
        }
        
        // compute distance
        return greatCircleDistance(coord, altitude);
    }
    
    /**
     * Computes the great circle distance between this coordinate and a given 
     * WGS-84 coordinate at a given altitude.
     * @link http://tchester.org/sgm/analysis/peaks/how_to_get_view_params.html#distance
     * @param coord     A <code>WGS84Coord</code>.
     * @param altitude  The altitude at which the distance will be computed.
     * @return          The great circle distance between this coordinate and 
     *                  <code>coord</code> at <code>altitude</code> kilometers 
     *                  above sea level.
     */
    public double greatCircleDistance(WGS84Coord coord, double altitude) {
        // convert latitude and longitude values to radians
        double lat1 = Math.toRadians(this.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(coord.getLatitude());
        double lon2 = Math.toRadians(coord.getLongitude());
        
        // compute latitude and longitude differences
        double latDiff = lat2 - lat1;
        double lonDiff = lon2 - lon1;

        // compute great circle distance in radians
        double temp = pow(sin(latDiff/2), 2) + (cos(lat1) * cos(lat2) * pow(sin(lonDiff/2), 2));
        double greatCircleDist = 2 * asin(min(1, sqrt(temp)));
        
        // convert from radians to km by multiplying by the radius of the earth
        // because we don't know the actual radius of the earth, we will use the 
        // average
        double distance = greatCircleDist * EARTH_MEAN_RADIUS;
        
        // account for altitude
        distance += altitude * (distance/EARTH_MEAN_RADIUS);
        
        return distance;
    }
    
    
    /*********************************************
     * MARK: Equals
     *********************************************/
    
    /**
     * Determines whether this WGS-84 coordinate is equivalent to a given object.
     * @param object    The object being compared to this coordinate.
     * @return          <code>true</code> if the given object is a <code>WGS84Coord</code> 
     *                  object equivalent to this coordinate; <code>false</code>
     *                  otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // not a WGS-84 coordinate?
        if (!(object instanceof WGS84Coord))
            return false;
        
        // compare latitude, longitude, and altitude
        WGS84Coord coord = (WGS84Coord)object;
        return (this.latitude == coord.getLatitude() && 
                this.longitude == coord.getLongitude() && 
                this.altitude == coord.getAltitude());
    }
    
    
    /*********************************************
     * MARK: toString
     *********************************************/
    
    /**
     * Creates a string to describe this vector.
     * @return  A string that describes this vector.
     */
    @Override
    public String toString() {
        return ("("+this.latitude+", "+this.longitude+", "+this.altitude+")");
    }
}
