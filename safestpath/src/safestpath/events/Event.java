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

import safestpath.mapElements.Road;
import safestpath.mapElements.Location;

/**
 * An Event corresponds to a single event on a map. Each event has a name, time,
 * date, severity (number between 1 and 10), and a shape (which is a polygon).
 */
public class Event {

    private String name;
    private String time;
    private String date;
    private double severity;
    private Location location;
    private Shape shape;

    /**
     * 
     * @param eventType represents what kind of event occurred (ie: Military,
     * criminal, weather, etc.)
     * @param time represents the time which when the event occurred.
     * @param date represents the date when the event occurred.
     * @param severity represents the how severe the event was on a scale from 0
     * to 10. A level 0 severity event is negligible whereas a level 10 is the 
     * highest level of threat.
     * @param impact
     * @param location represents the location the event occurs in Latitude and
     * Longitude.
     * @param shape
     */
    public Event(String eventType, String time, String date, double severity,
            float impact, Location location, Shape shape)
    {
        this.name = eventType;
        this.time = time;
        this.date = date;
        this.severity = severity;
        this.location = location;
        this.shape = shape;

    }

    /**
     * Contructor for the Event class
     * @param name The name of the event
     * @param severity The severity associated with this event
     * @param shape The shape of this event
     */
    public Event(String name, double severity, Shape shape)
    {
        this.name = name;
        this.severity = severity;
        this.shape = shape;
    }

    /**
     * @return The Location the event occured.
     */
    public Location getLocation() {
        return location;
    }
    /**
     *
     * @return The date the event occured.
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @return The type of event that occured.
     */
    public String getEventType() {
        return name;
    }

    /**
     *
     * @return The severity of the event that occured.
     */
    public double getSeverity() {
        return severity;
    }

    /**
     *
     * @return The Shape in how the event affects a particular area.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     *
     * @return The time the event took place.
     */
    public String getTime() {
        return time;
    }
/**
 * The contains method checks to see if a given road is within the area of
 * effect of a given event.
 * @author Shahid Akhter
 * @return boolean True if the road is within the event, false otherwise.
 */
    public boolean contains(Road road)
    {
        //if any point is contained inside this event's shape
        for(Location loc : road.getPoints())
        {
            if(shape.isInPolygon(loc))
            {
                return true;
            }
        }

        return false;
    }

    public String toString()
    {
        String result = "";
        result += "Event Name: " + name + "\n";
        result += "Event Severity: " + severity + "\n";
        result += "Shape: " + shape.toString() + "\n";
        return result;
    }
}
