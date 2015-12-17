/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */
package safestpath.parser;

import safestpath.pathing.Graph;
import safestpath.mapElements.Road;
import safestpath.events.Shape;
import safestpath.events.Event_Data;
import safestpath.events.Event;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import safestpath.mapElements.Location;
import safestpath.mapElements.Route;

/**
 *Outline of a road data KML file (in terms of tags)
 * kml declaration
 * Document
 * Folder 
 *    Placemark
 *      description
 *          speed (sets the road speed)
 *      name
 *      Point
 *          coordinates (used for the display location of the road)
 *      LineString
 *          coordinates (used for points on the road)
 * Each placemarks corresponds to a single road segment
 *
 * Outline of a event data KML file (in terms of tags)
 * kml declaration
 * --some folder/styling info
 *  Placemark
 *      name
 *      description (now in regular xml tags)
 *          date
 *          severity (between 0 and 1)
 *      Polygon
 *          outerBoundaryIs
 *          LinearRing
 *              coordinates
 * Each placemark has only 1 polygon, and as of now polygons do not have any defined
 * inner regions.
 *
 * @author student
 */
public class KML_Parser {
    //the document that the DOM parser uses
    //this is never actually manipulated, simply used by the DocumentBuilder

    private Document dom;
    //list of Roads that are parsed in from the KML
    private List<Road> roadSegments;
    //Transformer object is used to convert list of roads made from the KML
    //into a graph
    private Transformer transformer;
    private List<Shape> shapes;
    private Event_Data evts;
    private static Graph graph;

    /**
     * Default constructor for the KML_Parser class
     */
    public KML_Parser()
    {
        roadSegments = new ArrayList<Road>();
        shapes = new ArrayList<Shape>();
        evts = new Event_Data();
    }

    /**
     *
     * @param filename
     */
    public void initParser(String filename)
    {
        try
        {
            dom = null;

            //open the file
            File file = new File(filename);

            //get the factory
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            //using the factiry get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using the builder to get DOM representation of the KML file
            dom = db.parse(file);

        }
        catch (SAXException ex)
        {
        }
        catch (IOException ex)
        {
            System.out.println("Invalid file.");
        }
        catch (ParserConfigurationException ex)
        {
        }
    }

    public Event_Data parseEventData()
    {
        //get the root element
        Element docElement = dom.getDocumentElement();

        //get a nodelist of the elements
        //here everything we need is inside Placemark tags
        NodeList nodes = docElement.getElementsByTagName("Placemark");

        if (nodes != null && nodes.getLength() > 0)
        {
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Element el = (Element) nodes.item(i);

                String eventName = getStringValue("name", el);

                Shape s = getShape(el);

                String date = getStringValue("date", el);

                String severityString = getStringValue("severity", el);
                double severity = 0;
                if (!severityString.equals(""))
                {
                    severity = Double.parseDouble(severityString);

                }
                else
                {
                }

                Event currEvent = new Event(eventName, severity, s);
                evts.addEvent(currEvent);
            }
        }

        return evts;
    }

    /**
     * Parse the XML file
     */
    public void parseRoadData()
    {
        //get the root element
        Element docElement = dom.getDocumentElement();

        //get a nodelist of the elements
        //here everything we need to look at is in the Placemark tage
        //Every road segment is defined in a placemark tag
        NodeList nodes = docElement.getElementsByTagName("Placemark");

        //check to make sure that there is at least 1 placemark element, then
        //iterate through all of them
        if (nodes != null && nodes.getLength() > 0)
        {
            for (int i = 0; i < nodes.getLength(); i++)
            {
                //get the placemark element
                Element el = (Element) nodes.item(i);

                //get the Placemark object
                Road p = getRoad(el);

                //add the placemark to the list of road segments
                roadSegments.add(p);
            }
        }
    }

    public Shape getShape(Element e)
    {
        String coordinateArray = getStringValue("coordinates", e);

        String[] stringArr = coordinateArray.split("\\s+");

        List<Location> locations = new ArrayList<Location>();
        for (String s : stringArr)
        {
            System.out.println(s);
            double lat, lon, alt;

            if(s.equals(""))
            {
                continue;
            }

            String[] coords = s.split(",");
            lat = Double.parseDouble(coords[0]);
            lon = Double.parseDouble(coords[1]);
            alt = Double.parseDouble(coords[2]);

            locations.add(new Location(lat, lon, alt));
        }

        Shape s = new Shape(locations);
        return s;
    }

    /**
     * Goes through a placemark element, and initializes the object based on
     * its fields in the KML file
     * @param placemarkElement
     * @return
     */
    private Road getRoad(Element placemarkElement)
    {
        //get the id of the placemark
        String id = placemarkElement.getAttribute("id");

        //get the street name of the placemark
        String streetName = getStringValue("name", placemarkElement);

        //get the display location
        //NOTE: had to change the street name to the id for now because
        //it wasnt correctly parsing a number as a string, but this should matter
        //for a KML file structured the canadian way, so change this back after done testing
        //Location displayLocation = getTextValue(placemarkElement, "coordinates");

        String roadSpeedString = getStringValue("speed", placemarkElement);


        int roadSpeed = 0;
        if(!roadSpeedString.equals(""))
        {
            roadSpeed = Integer.parseInt(roadSpeedString);
        }

        //get the array of locations
        String roadArray = getCoordinateArray(placemarkElement);

        //get the list of roads
        Route roads = initializeRoadSegment(roadArray);

        //create a new road and set it's list of points, and name
        Road road = new Road();
        //road.addRoute(roads);
        road.setDrawingPoints(roads.getPath());

        //road.setRoadName(streetName);
        road.setRoadName(id);

        if(roadSpeed != 0)
            road.setRoadSpeed(roadSpeed);

        //return new Placemark(streetName, id, roadArray);
        return road;
    }

    /**
     * Takes a placemark element, and then finds the array of coordinates
     * that is stored inside the <linestring> tag
     * @param placemarkElement The current placemark element that is being
     * scanned
     * @return A route, which contains all of the coordinates from the <linestring>
     */
    private String getCoordinateArray(Element placemarkElement)
    {
        Route result = null;
        String coordinates = "";

        //get a list of all the occurances of the <LineString> tag (there
        //should only be 1)
        NodeList nodes = placemarkElement.getElementsByTagName("LineString");
        if (nodes != null && nodes.getLength() > 0)
        {
            Element lineStringElement = (Element) nodes.item(0);

            //now get a list of all the occurances of the <coordinates> tag (again
            //there should only be 1)
            NodeList coordinateList = lineStringElement.getElementsByTagName("coordinates");

            if (coordinateList != null && coordinateList.getLength() > 0)
            {
                Element coords = (Element) coordinateList.item(0);

                //get the actual string value associated with the <coordinates> tag
                coordinates = coords.getFirstChild().getNodeValue();
            }
        }

        return coordinates;
    }

    public String toString()
    {
        String result = "";

        for (Road p : roadSegments)
        {
            result += p.toString() + "\n";
        }

        return result;
    }

    /**
     * Get the string value of the tag given, that is inside the given element
     * @param tag The tag to check for
     * @param placemarkElement The element you are checking in
     * @return The string value of the tag inside the placemarkElement
     */
    private String getStringValue(String tag, Element placemarkElement)
    {
        String result = "";

        NodeList nodes = placemarkElement.getElementsByTagName(tag);

        if (nodes != null && nodes.getLength() > 0)
        {
            result = nodes.item(0).getFirstChild().getNodeValue();
        }

        return result;
    }

    /**
     * Transform a string that contains multiple locations into a Route object
     * @param roadArray The string of multiple locations
     * @return A route corresponding to all of the locations in roadArray
     */
    private Route initializeRoadSegment(String roadArray)
    {
        List<Location> roadSegments = new ArrayList<Location>();

        //split the string at every whitespace element
        String[] stringArr = roadArray.split("\\s+");

        //go through each element, and transform it into a Locaiton object
        for (String s : stringArr)
        {
            Location loc;
            double lat, lon, alt;

            //split the string at every comma
            //the 0th element will be the latitude
            //the 1st element will be the longitude
            //the 2nd element will be the altitude (should always be 0)
            String[] locationString = s.split(",");

            lat = Double.parseDouble(locationString[0]);
            lon = Double.parseDouble(locationString[1]);
            alt = Double.parseDouble(locationString[2]);

            //create the location with the parsed values
            loc = new Location(lat, lon, alt);

            //add the location to the list of Locations
            roadSegments.add(loc);
        }

        //return a new Route made from the list of routes
        return new Route(roadSegments);
    }

    /**
     * Transform the list of roads created from the KML into a Graph
     * @return the graph corresponding to the list of roads.
     */
    public Graph transformToGraph()
    {
        transformer = new Transformer(roadSegments);

        Graph result = transformer.transform();
        return result;
    }
}
