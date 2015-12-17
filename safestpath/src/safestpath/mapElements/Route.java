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
import safestpath.mapElements.Location;
import java.util.*;
/**
 * Representation of a path as a collection of Location objects.
 */
public class Route {
    //this is the actual route
    private List <Location> path;

    //final Strings used for generating KML
    private static final String ROUTE_HEADER = "\n<Placemark>";

    private static final String ROUTE_HEADER_CLOSE = "\n</Placemark>";

    private static final String POINT_HEADER = "\n<LineString>" +
            "\n<coordinates>";

    private static final String POINT_HEADER_CLOSE = "\n</coordinates>" +
            "\n</LineString>";


    /**
     * Default constructor for the Route class
     */
    public Route()
    {
        path = new ArrayList<Location>();
    }

    /**
     * Constructor used when a list of locations is already constructed
     * @param path
     */
    public Route(List<Location> path)
    {
        this.path = path;
    }

    /**
     * @return all of the locations in this route
     */
    public List<Location> getPath() {
        return path;
    }

    /**
     * Create a KML representation of this route with the given style id
     * (has been created elsewhere in our case)
     * @param styleID The stlye name used to create the color of the route
     * @return The KML representation of this route
     */
    public String toKMLString(String styleID)
    {
        String result = ROUTE_HEADER;

        result += "\n<styleUrl>#" + styleID + "</styleUrl>";

        result += "\n<MultiGeometry>";

        result += POINT_HEADER;

        for(Location loc : path)
        {
            result += loc.getLatitude() + "," + loc.getLongitude() + ",0 ";
        }

        result += POINT_HEADER_CLOSE;

        result += "\n</MultiGeometry>";

        result += ROUTE_HEADER_CLOSE;

        return result;
    }

}
