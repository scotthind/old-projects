package org.rowan.pathfinder.pathfinder;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.SurfacePolygon;
import java.awt.Color;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.Polygon2D;
import org.rowan.linalgtoolkit.shapes2d.Segment2D;
import org.rowan.pathfinder.display.Director;

/**
 * Class <code>Event</code> represents an event which could be deemed hazardous
 * in some way. Each event has danger/severity level associated with them as well
 * as a shape that represents the event's affected area.
 * 
 * @author Dan Urbano, Shahid Akhter
 * @version 1.0
 * @since 1.0
 */
public class Event implements Serializable {

    private static double MAX_OPACITY = .7;
    private static double MIN_OPACITY = .3;
    /** The beginning date of the event */
    private Calendar start;
    /** The end date of an event. */
    private Calendar end;
    /** The severity level associated with an effect. */
    private double severity;
    /** A detailed description of the event */
    private String description;
    /** A Polygon representing the affected area of the event*/
    private Polygon2D polygon;
    /** Whether this event contains mines */
    private boolean hasMines;

    
    private ArrayList<Double> polygonList = null;
    
    private static final long serialVersionUID = 0;

    /**
     * Event information will created based on the information in the Event
     * database. An Event will have a start Date, an End Date, the severity
     * (danger level) of the event, a detailed description of the event,
     * and the polygon that represents the area the event affects.
     * @param start Start date of the event.
     * @param end End date of the event.
     * @param severity The danger level of the event.
     * @param description A detailed description of the event.
     * @param polygon The shape of the area the event affects.
     * @param hasMines Whether the event contains mines or not.
     */
    public Event(Calendar start, Calendar end, double severity, String description,
            Polygon2D polygon, boolean hasMines) {
        this.start = start;
        this.end = end;
        this.severity = severity;
        this.description = description;
        this.polygon = polygon;
        this.hasMines = hasMines;
        
    }

    /**
     * Returns a Polygon2D that represents the shape of the event's affection
     * region.
     * @return A shape of the event.
     */
    public Polygon2D getBoundary() {
        return polygon;
    }

    /**
     * A detailed description of the event.
     * @return Description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date the event ended.
     * @return The date of the event or null if still ongoing.
     */
    public Calendar getEnd() {
        return end;
    }

    /**
     * Returns the level of severity (danger) of the event. Will be a value between
     * 0 and 1, 0 being no danger and 1 being the worst severity.
     * @return The severity level of the event. Value between 0 and 1.
     */
    public double getSeverity() {
        return severity;
    }

    /**
     * Returns the start date of an event.
     * @return The date the event began.
     */
    public Calendar getStart() {
        return start;
    }

    /**
     * Returns whether or not this event has mines.
     * @return True if the event contains mines, false otherwise. 
     */
    public boolean containsMines() {
        return hasMines;
    }

    @Override
    public String toString() {
        String info = "Start Date: " + start + " End Date: " + end + " Severity: "
                + severity + " Description: " + description + " Polygon:";

        for (Segment2D seg : polygon.getEdges()) {
            info += " ";
            info += seg.getStart().getX();
            info += ",";
            info += seg.getStart().getY();
            info += ", ";
            info += seg.getEnd().getX();
            info += ",";
            info += seg.getEnd().getY();
        }

     //   System.out.println("Verticies: " + polygon.getWorldVertices());

        return info;
    }

    /**
     * Adds the event to the provided RenderableLayer
     * @param The Event Renderable Layer
     */
    public void addToLayer(RenderableLayer layer) {
        List<LatLon> positions = new ArrayList<LatLon>();
        BasicShapeAttributes eventAttr;
        eventAttr = new BasicShapeAttributes();
        eventAttr.setOutlineMaterial(new Material(Color.MAGENTA));
        eventAttr.setInteriorMaterial(new Material(Color.MAGENTA));
        eventAttr.setOutlineOpacity((severity * (MAX_OPACITY - MIN_OPACITY)) + MIN_OPACITY);
        eventAttr.setInteriorOpacity((severity * (MAX_OPACITY - MIN_OPACITY)) + MIN_OPACITY);


      //  System.out.println(polygon.getWorldVertices());
        for (Vector2D v : polygon.getWorldVertices()) {
            positions.add(Logic2D.vector2DToLatLon(v));
        }
        SurfacePolygon eventShape = new SurfacePolygon(eventAttr, positions);
        eventShape.setValue("Details", annotationText());
        layer.addRenderable(eventShape);
    }

    /**
     * Returns a string that represents the proper format of the event
     * based on the XML requirements.
     * @return A string in Event XML Format.
     */
    public String export() {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        String export = "";
        export += "\t<Event>\n";
        export += "\t\t<StartDate>" + sdf.format(start.getTime()) + "</StartDate>\n";

        if (end != null) {
            export += "\t\t<EndDate>" + sdf.format(end.getTime()) + "</EndDate>\n";
        }
        export += "\t\t<Severity>" + severity + "</Severity>\n";
        export += "\t\t<Description>" + description + "</Description>\n";
        export += "\t\t<HasMines>" + hasMines + "</HasMines>\n";
        export += "\t\t<OuterBoundary>\n";
        for (Vector2D v : polygon.getWorldVertices()) {
            export += "\t\t\t<Coord>" + v.getX() + "," + v.getY() + "</Coord>\n";
        }
        export += "\t\t</OuterBoundary>\n";
        export += "\t</Event>\n";

        return export;
    }

    /**
     * Returns a string about the event which will eventually be displayed in
     * a Tooltip
     * @return A String containing information about the event.
     */
    private String annotationText()
    {
        String str = "Event\n";
        if(start != null)
        str += "Start Date: " + (start.get(start.MONTH) + 1)+ "/" 
                + start.get(start.DAY_OF_MONTH) + "/" + start.get(start.YEAR) + "\n";
        if(end == null)
            str += "Status: Ongoing." + "\n";
        else
            str += "Status: Ended on " + (end.get(end.MONTH)+ 1) + "/"
                + end.get(end.DAY_OF_MONTH) + "/" + end.get(end.YEAR) + "\n";
        str += "Severity: "  + severity*10 + "\n";
        str += "Description: " + description + "\n";

        return str;
    }
    
    public void convertPolyToList() {
        ArrayList<Double> list = new ArrayList<Double>();
        for (Vector2D v : polygon.getWorldVertices()) {
            list.add(v.getX());
            list.add(v.getY());
        }
        polygonList = list;
        polygon = null;
    }
    
    public void convertListToPoly() {
        ArrayList<Vector2D> list = new ArrayList<Vector2D>();
        for (int i=0; i<polygonList.size(); i+=2) {
            Vector2D v = new Vector2D(polygonList.get(i), polygonList.get(i+1));
            list.add(v);
        }
        polygon = new Polygon2D(Logic2D.getCentroid(list), Logic2D.centerVertices(list));
        polygonList = null;
    }

    public double getDecayPercent() {
        if (end == null) {
            return 1;
        }
        
        Calendar now = GregorianCalendar.getInstance();
        
        Calendar lowerCutoff = GregorianCalendar.getInstance();
        lowerCutoff.add(Calendar.YEAR, -1 * Director.decay1Years);
        lowerCutoff.add(Calendar.MONTH, -1 * Director.decay1Months);
        lowerCutoff.add(Calendar.DAY_OF_YEAR, -1 * Director.decay1Days);
        Calendar upperCutoff = GregorianCalendar.getInstance();
        upperCutoff.add(Calendar.YEAR, -1 * Director.decay2Years);
        upperCutoff.add(Calendar.MONTH, -1 * Director.decay2Months);
        upperCutoff.add(Calendar.DAY_OF_YEAR, -1 * Director.decay2Days);
        
        if (now.compareTo(lowerCutoff) <= 0) {
            return 1;
        } else if (now.compareTo(upperCutoff) >= 0) {
            return 0;
        } else {
            long n = now.getTimeInMillis();
            long low = lowerCutoff.getTimeInMillis();
            long upp = upperCutoff.getTimeInMillis();
            
            return 1d - (double)(n - low) / (double)(upp - low);
        }
    }
}
