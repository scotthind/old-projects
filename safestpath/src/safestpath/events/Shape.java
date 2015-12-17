/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */
package safestpath.events;

import safestpath.mapElements.Location;
import safestpath.*;
import java.util.*;

/**
 * The shape class defines a polygon from a list of points. It is not just
 * limited to simple polygons, but can be complex ones as well.
 */
public class Shape {

    private List<Location> corners;

    public Shape(List<Location> corners) {
        this.corners = corners;
    }

    /**
     * This method determines if a location is within the area of a polygon
     *
     * The algorithm used was found on http://alienryderflex.com/polygon/
     * The basis of it is this:
     * You are given a point to test if its inside a complex polygon.
     * You straight ling through the point.
     * If number of times the line intersects with an edge of the polygon on
     * both the left and right is an odd number, then that point is inside the polygon.
     *
     * @param loc The location which may be within the polygon.
     * @return True if the location entered is within the polygon
     */
    public boolean isInPolygon(Location loc) {

        if(loc.equals(new Location(-75.7273592, 44.6106202)))
        {
            int i = 0;
        }


        int crossings = 0;
        Location points[] = new Location[corners.size()];
        points = corners.toArray(points);
        for (int i = 0; i < corners.size() - 1; i++) {
            double slope = (points[i + 1].getLatitude() - points[i].getLatitude()) / (points[i + 1].getLongitude() - points[i].getLongitude());
            boolean cond1 = (points[i].getLongitude() <= loc.getLongitude()) && (loc.getLongitude() < points[i + 1].getLongitude());
            boolean cond2 = (points[i + 1].getLongitude() <= loc.getLongitude()) && (loc.getLongitude() < points[i].getLongitude());
            boolean cond3 = loc.getLatitude() < slope * (loc.getLongitude() - points[i].getLongitude()) + points[i].getLatitude();
            if ((cond1 || cond2) && cond3) {
                crossings++;
            }
        }
        return (crossings % 2 != 0);
    }

    /**
     * toString method used for testing purposes
     * @return
     */
    public String toString() {
        String result = "";
        for (Location loc : corners) {
            result += loc.toString();
        }
        return result;
    }
}





