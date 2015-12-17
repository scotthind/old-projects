package org.rowan.coordconversion;

import org.rowan.linalgtoolkit.Vector3D;
import org.rowan.linalgtoolkit.WGS84Coord;

 /**
  * <p>The CoordinateConversion class is able to take in coordinates from a given
  * coordinate system and convert them into coordinates for another specified
  * coordinate system.</p>
  *
  *<p>A conversion factor is used to describe the distance in kilometers for every
  * Euclidean unit. Here are some example of conversion factors: <ul>
  * <li>1000 -> 1000 kilometers</li>
  * <li>2.3 -> 2.3 kilometers</li>
  * <li>0.5 -> 500 meters</li></ul></p>
  *
  * @author Spence DiNicolantonio, Robert Russell
  * @version 1.1
  * @since 1.0
  */
 public class CoordinateConversion {

    /*********************************************
     * MARK: Fields
     *********************************************/

     /** The number of kilometers to be represented by every Euclidean unit. */
     private double conversionFactor;

    /*********************************************
     * MARK: Constructors
     *********************************************/

     /**
      * Creates an instance of CoordinateConversion with a given conversion factor.
      * @param conversionFactor the conversion factor to be used in conversion computations.
      * @throws IllegalArgumentException the conversion factor must be positive.
      */
     public CoordinateConversion(double conversionFactor) {
         if (conversionFactor <= 0) {
             throw new IllegalArgumentException("The conversion factor must be positive.");
         }
         this.conversionFactor = conversionFactor;
     }

     /**
      * Creates an instance of CoordinateConversion with a conversion factor of 1.
      */
     public CoordinateConversion() {
         this.conversionFactor = 1;
     }

    /*********************************************
     * MARK: Conversion Queries
     *********************************************/
     
     /**
      * Converts a given set of Euclidean coordinates to WGS-84 coordinates.
      * @param point an instance of Vector representing a location in the Euclidean
      * coordinate system in meters multiplied by the conversion factor.
      * @return an array of doubles, which contains the longitude, latitude, and
      * height in meters, in the WGS-84 coordinate system.
      */
     public WGS84Coord toWGS84(Vector3D point) {
         // convert euclidean coordinate values to meters
         double x = point.getX() * this.conversionFactor * 1000;
         double y = point.getY() * this.conversionFactor * 1000;
         double z = point.getZ() * this.conversionFactor * 1000;

         // store equatorial and polar radius for easy access 
         double a = WGS84Coord.EARTH_EQUATORIAL_RADIUS*1000;
         double b = WGS84Coord.EARTH_POLAR_RADIUS*1000;
         
         double p = Math.sqrt((x * x) + (y * y));
         double theta = Math.atan2((z * a), (p * b));
         double ePrime2 = ((a * a) - (b * b)) / (b * b);
         double f = (a - b) / a;
         double e2 = 2 * f - (f * f);

         double lat = Math.atan2((z + ePrime2 * b * Math.pow(Math.sin(theta), 3)), (p - e2 * a * Math.pow(Math.cos(theta), 3)));
         double n = a / Math.sqrt(1 - e2 * (Math.sin(lat) * Math.sin(lat)));

         double lon = Math.atan2(y, x);
         double alt = (p / Math.cos(lat)) - n;
         
         // Convert latitude and longitude points to degrees and return new WGS84Coord
         return new WGS84Coord(lat * 57.2957795, lon * 57.2957795, alt/1000);
     }

     /**
      * Converts a given set of WGS-84 coordinates to Euclidean coordinates.
      * @param coord A <code>WGS84Coord</code> to be converted.
      * @return an array of doubles, which contains the X, Y, and Z positions in
      * the Euclidean coordinate system in units of kilometers multiplied by the conversion factor.
      */
     public Vector3D toEuclidean(WGS84Coord coord) {
         double lat = coord.getLatitude() * 0.0174532925;
         double lon = coord.getLongitude() * 0.0174532925;
         double alt = coord.getAltitude() * 1000;
         
         // store equatorial and polar radius for easy access 
         double a = WGS84Coord.EARTH_EQUATORIAL_RADIUS*1000;
         double b = WGS84Coord.EARTH_POLAR_RADIUS*1000;
         
         double f = (a - b) / a;
         double e2 = (2 * f) - (f * f);
         double n = a / (Math.sqrt(1 - e2 * Math.pow(Math.sin(lat), 2)));
         double x = ((n + alt) * Math.cos(lat) * Math.cos(lon)) / (this.conversionFactor * 1000);
         double y = ((n + alt) * Math.cos(lat) * Math.sin(lon)) / (this.conversionFactor * 1000);
         double z = ((n * (1 - e2) + alt) * Math.sin(lat)) / (this.conversionFactor * 1000);

         // create and return vector
         return new Vector3D(x, y, z);
     }
 }