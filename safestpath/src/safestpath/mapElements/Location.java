/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */
package safestpath.mapElements;

/**
 * Represents a location in the geographic coordinate system.
 */
public class Location {

    private double latitude;
    private double longitude;
    private double altitude;

    /**
     * Constructor for the Location class when using just latitude and
     * longitude points
     * @param latitude
     * @param longitude
     */
    public Location(double latitude, double longitude)
    {
        setLatitude(latitude);
        setLongitude(longitude);
        altitude = 0;
    }

    /**
     * Constructor for the Location class when using latitude, longitude,
     * and altitude points
     * @param latitude
     * @param longitude
     * @param altitude
     */
    public Location(double latitude, double longitude, double altitude)
    {
        setLatitude(latitude);
        setLongitude(longitude);
        this.altitude = altitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    /**
     * Set the latitude of this location
     * @param latitude
     */
    private void setLatitude(double latitude)
    {
        if (Math.abs(latitude) <= 90.00)
        {
            this.latitude = latitude;
        }
        else
        {
            System.err.println("Valid latitude ranges from -90.00 to 90.00");
        }
    }

    public double getLongitude()
    {
        return longitude;
    }

    /**
     * Set the longitude of this location
     * @param longitude
     */
    private void setLongitude(double longitude)
    {
        if (Math.abs(longitude) <= 180.00)
        {
            this.longitude = longitude;
        }
        else
        {
            System.err.println("Valid longitude ranges from -180.00 to 180.00");
        }
    }

    public String toString()
    {
        return "[" + latitude + "," + longitude + "," + altitude + "]";
    }

    /**
     * Check to see if this location equals the given location. They are equal
     * only if both their latitudes, longitudes, and altitudes are the same
     * @param loc The location to check against this
     * @return True if the 2 are the same location, else false
     */
    public boolean equals(Location loc)
    {
        return (this.latitude == loc.latitude) && (this.longitude == loc.longitude)
                && (this.altitude == loc.altitude);
    }

    /**
     * The findDistance method finds the total distance between two coordinates
     * using the Haversine Formula.
     * @return distance - Total distance of road in kilometers.
     */

    public double findDistance(Location loc)
    {

        double lat2 = loc.getLatitude();
        double lon2 = loc.getLongitude();
        double lat1 = this.getLatitude();
        double lon1 = this.getLongitude();
        double dlong = (lon2 - lon1) * (Math.PI / 180);
        double dlat = (lat2 - lat1) * (Math.PI / 180);

        //Haversine Formula: Finds distance between 2 coordinates
        double a = Math.pow(Math.sin(dlat / 2.0), 2)
                + Math.cos(lat1 * (Math.PI / 180))
                * Math.cos(lat2 * (Math.PI / 180))
                * Math.pow(Math.sin(dlong / 2.0), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = 6367 * c; //Converting distance to kilometers

        return distance;

    }
}
