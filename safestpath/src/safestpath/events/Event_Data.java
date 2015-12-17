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
import java.util.*;

/**
 * This class serves as a list of all the events in the operational area.
 * This class holds a list of events.
 */
public class Event_Data {

    //the max severity is a 10 and the lowest a 10 in an attempt to normalize
    //cost ratings for the path generating algorithm
    //if the lowest severity was 0, we could not multiply cost ratings when
    //using combinations of parameters. So instead a severity of 1 means that
    //there is no threat from an event, and a severity of 10 is that maximum threat
    private static final double MAX_SEVERITY = 10;
    private List<Event> events;
    private static final double LOW_SEVERITY = 1;

    public Event_Data()
    {
        events = new ArrayList<Event>();
    }

    /**
     * Add an event to the event data
     * @param e The event to add
     */
    public void addEvent(Event e)
    {
        events.add(e);
    }

    /**
     * @return The maximum severity rating for all events
     */
    public static double getMaxSeverity()
    {
        return MAX_SEVERITY;
    }

    /**
     * Check to see if a given road is contained inside any events
     * @param r The road to check
     * @return True if the road is contained in an event, else false
     */
    public boolean contains(Road r)
    {
        for (Event event : events)
        {
            if (event.contains(r))
            {
                System.out.println(r.getRoadName());
                return true;
            }
        }
        return false;
    }

    /**
     * Get the severity of the given road
     * @param road The road to find the severity of
     * @return The severity of the road
     */
    public double severityOf(Road r)
    {
        double highSeverity = LOW_SEVERITY;
        for (Event event : events)
        {
            //short circuit so that an even is only checked to
            //see if it contains a road if it has a higher severity
            if (event.getSeverity() > highSeverity && event.contains(r))
            {
                highSeverity = event.getSeverity();
            }
        }
        return highSeverity;
    }
}
