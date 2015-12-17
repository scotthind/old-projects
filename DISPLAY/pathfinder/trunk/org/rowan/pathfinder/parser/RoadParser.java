package org.rowan.pathfinder.parser;

import java.util.Dictionary;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.Segment2D;
import org.rowan.pathfinder.pathfinder.Logic2D;
import org.rowan.pathfinder.pathfinder.RoadSegment;

/**
 * Class <code>RoadParser</code> is responsible for parsing an OSM file
 * with road data into <code>RoadSegment</code> objects.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class RoadParser implements OSMSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, OUTER, NODE, WAY, ND, TAG, UNKNOWN}
    /** The name of the outer tag of the file */
    private static final String TAG_OUTER = "osm";
    /** The name of the node tag */
    private static final String TAG_NODE = "node";
    /** The name of the way tag */
    private static final String TAG_WAY = "way";
    /** The name of the 'referencing a node' tag */
    private static final String TAG_ND = "nd";
    /** The name of the tag tag (excuse the redundancy) */
    private static final String TAG_TAG = "tag";
    /** The name of the id attribute */
    private static final String ATR_ID = "id";
    /** The name of the latitude attribute */
    private static final String ATR_LAT = "lat";
    /** The name of the longitude attribute */
    private static final String ATR_LONG = "lon";
    /** The name of the node reference attribute */
    private static final String ATR_REF = "ref";
    /*  The default speed limits */
    private static final String HW_RES = "residential";
    private static final String HW_PRI = "primary";
    private static final String HW_SEC = "secondary";
    private static final String HW_TER = "tertiary";
    private static final String HW_TRUNK = "trunk";
    private static final String HW_MOTOR = "motorway";
    private static final String HW_MLINK = "motorway_link";
    private static final int HW_RES_MPH = 25;
    private static final int HW_PRI_MPH = 55;
    private static final int HW_SEC_MPH = 45;
    private static final int HW_TER_MPH = 35;
    private static final int HW_TRUNK_MPH = 45;
    private static final int HW_MOTOR_MPH = 65;
    private static final int HW_MLINK_MPH = 30;
    /** The default speed limit map */
    private HashMap<String, Double> defaultSpeedLimitMap = new HashMap<String, Double>();
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The set of RoadSegments that have been parsed */
    private Set<RoadSegment> roads = new HashSet<RoadSegment>();
    /** The mode of that parser is in */
    private Mode mode = Mode.NONE;
    /** a map of all the nodes used in this file */
    private TreeMap<String, Vector2D> nodeMap = new TreeMap<String, Vector2D>();
    /** a list of node ids for a way */
    private LinkedList<String> r_wayList = new LinkedList<String>();
    /** The name of the road */
    private String r_name = null;
    /** The speed limit of the road (if defined) */
    private double r_speedLimit = -1f;
    /** True if the current way that is being parsed is a road */
    private boolean r_isRoad = false;
    /** True if the road is one-way */
    private boolean r_isOneWay = false;
    /** The map of user defined speed limits */
    private Map<String, Double> speedLimitMap = new HashMap<String, Double>();
    
    /**
     * Create a new RoadParser, with defined speed limits for open street map
     * highway types. For example, the map might contain a key called 
     * "residential" which is mapped to the Double value 50f. Values should
     * be stored in km/hr.
     * @param speedLimitMap A map containing defined speed limits for open
     *                      street map highway types, possibly null.
     */
    public RoadParser(Map<String, Double> speedLimitMap) {
        defaultSpeedLimitMap.put(HW_RES, Logic2D.MPHtoKPH(HW_RES_MPH));
        defaultSpeedLimitMap.put(HW_PRI, Logic2D.MPHtoKPH(HW_PRI_MPH));
        defaultSpeedLimitMap.put(HW_SEC, Logic2D.MPHtoKPH(HW_SEC_MPH));
        defaultSpeedLimitMap.put(HW_TER, Logic2D.MPHtoKPH(HW_TER_MPH));
        defaultSpeedLimitMap.put(HW_TRUNK, Logic2D.MPHtoKPH(HW_TRUNK_MPH));
        defaultSpeedLimitMap.put(HW_MOTOR, Logic2D.MPHtoKPH(HW_MOTOR_MPH));
        defaultSpeedLimitMap.put(HW_MLINK, Logic2D.MPHtoKPH(HW_MLINK_MPH));
        if (speedLimitMap != null)
            this.speedLimitMap = speedLimitMap;
    }
    
    /**
     * Return all roads that were parsed using this parser. Be warned,
     * if this parser is reused, any newly parsed roads will be added
     * to this list unless clearRoads() is called.
     * @return all parsed roads.
     */
    public Set<RoadSegment> extractRoads() {
        return roads;
    }
    
    /**
     * Clear all roads from this parser so it can be reused. Be warned,
     * you must call extractRoads() before this method to retrieve a
     * list of any previously parsed roads.
     */
    public void clearRoads() {
        roads = new HashSet<RoadSegment>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String tag, Dictionary attributes, int line, int col) throws PFParseException {
        stack.push(tag);
        openTag(tag, attributes, line, col);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String tag, int line, int col) throws PFParseException {
        try {
            String s = stack.pop();
            if (s.equals(tag)) {
                closeTag(tag, line, col); //we closed the last opened tag, everything is OK
                return;
            }
            
            if (isOpen(tag)) {
                Errors.incorrectClose(tag, s, line, col); //we closed a tag, but not the last opened one
            } else {
                Errors.invalidClose(tag, line, col); //we closed a tag that was never even opened
            }
            stack.push(s);
        } catch (EmptyStackException ese) {
            Errors.invalidClose(tag, line, col); //we closed a tag but NO tags were opened
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void text(String text, int line, int col) {
        //ignore completely - everything we need is inside a tag
    }

    /**
     * Handle an opening tag.
     * @param tag The tag that was opened.
     * @param attributes The attributes dictionary of the tag.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    private void openTag(String tag, Dictionary attributes, int line, int col) throws PFParseException {
        if (isEqual(tag, TAG_OUTER)) {
            switch (mode) {
                case NONE:
                    mode = Mode.OUTER;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_NODE)) {
            switch (mode) {
                case OUTER:
                    mode = Mode.NODE;
                    //attempt to place the node in the node map with its id
                    Double lat, lon;                    
                    try {
                        lat = Double.parseDouble(attributes.get(ATR_LAT).toString());
                        try {
                            lon = Double.parseDouble(attributes.get(ATR_LONG).toString());
                            nodeMap.put(attributes.get(ATR_ID).toString().trim(), new Vector2D(lat, lon));
                        } catch (NumberFormatException ex) {
                            Warnings.invalidFormat(ATR_LONG+"=\"" + attributes.get(ATR_LONG).toString()+"\"", tag, line, col);
                        }
                    } catch (NumberFormatException ex) {
                        Warnings.invalidFormat(ATR_LAT+"=\""+attributes.get(ATR_LAT).toString()+"\"", tag, line, col);
                    }
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_WAY)) {
            switch (mode) {
                case OUTER:
                    r_name = null;
                    r_speedLimit = -1f;
                    r_isRoad = false;
                    r_isOneWay = false;
                    r_wayList.clear();
                    mode = Mode.WAY;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_ND)) {
            switch (mode) {
                case WAY:
                    mode = Mode.ND;
                    r_wayList.add(attributes.get(ATR_REF).toString());
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_TAG)) {
            switch (mode) {
                case WAY:
                    String k = attributes.get("k").toString();
                    String v = attributes.get("v").toString();
                    if (k.equals("highway")) {
                        r_isRoad = true;
                        if (speedLimitMap.containsKey(v) && r_speedLimit < 0) {
                            r_speedLimit = speedLimitMap.get(v);
                        } else if (defaultSpeedLimitMap.containsKey(v) && r_speedLimit < 0) {
                            r_speedLimit = defaultSpeedLimitMap.get(v);
                        } else {
                            r_isRoad = false;
                        }
                    } else if (k.equals("name")) {
                        r_name = v;
                    } else if (k.equals("oneway") && v.equals("yes")) {
                        r_isOneWay = true;
                    } else if (k.equals("maxspeed")) {
                        r_speedLimit = getSpeedInKM(v);
                    }
                    mode = Mode.TAG;
                    break;
                default:
                    mode = Mode.TAG;
            }            
        } else {
            //too many tags we need to ignore to list them all
            //assume an unrecognized tag is just going to be ignored
            //so we comment out the usual Warnings.unknownOpenTag call
            //Warnings.unknownOpenTag(tag, line, col);
        }
    }
    
    /**
     * Handle a closing tag.
     * @param tag The tag that was closed.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    private void closeTag(String tag, int line, int col) throws PFParseException {
        if (isEqual(tag, TAG_OUTER)) {
            switch (mode) {
                case OUTER:
                    if (stack.isEmpty()) {
                        mode = Mode.NONE;
                    } else {
                        Errors.expectedToHaveClosedAll(tag, line, col);
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_NODE)) {
            switch (mode) {
                case NODE:
                    mode = Mode.OUTER;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_WAY)) {
            switch (mode) {
                case WAY:
                    if (r_wayList == null || r_wayList.size() < 2) {
                        Warnings.unexpectedClose(tag, TAG_WAY, line, col);
                    } else if (r_isRoad) {
                        // declare variables for each segment
                        RoadSegment r = null;
                        Vector2D s, e = nodeMap.get(r_wayList.getFirst());
                        String errorRef;
                        for (int i = 1; i < r_wayList.size(); i++) {
                            s = e;
                            e = nodeMap.get(r_wayList.get(i));
                            // skip this segment if any of the points are invalid
                            errorRef = null;
                            if (s == null)
                                errorRef = r_wayList.get(i-1);
                            if (e == null) {
                                errorRef = r_wayList.get(i);
                                e = s;
                            }
                            if (errorRef != null) {
                                Warnings.general("While parsing a <way> tag, the nd tag " +
                                        "containing ref=\"" + errorRef + "\" refered " +
                                        "to an invalid node. Skipping that node.", line, col);
                                continue;
                            }
                            r = new RoadSegment(new Segment2D(s, e), r_name);
                            r.setSpeedLimit((int)r_speedLimit); //TODO KEEP AS DOUBLE, CHANGE ROADSEGMENT TO TAKE DOUBLE
                            roads.add(r);
                            if (!r_isOneWay) {
                                r = new RoadSegment(new Segment2D(e, s), r_name);
                                r.setSpeedLimit((int) r_speedLimit); //TODO KEEP AS DOUBLE, CHANGE ROADSEGMENT TO TAKE DOUBLE
                                roads.add(r);
                            }
                        }
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
            r_name = null;
            r_speedLimit = -1f;
            r_isRoad = false;
            r_isOneWay = false;
            r_wayList.clear();
            mode = Mode.OUTER;
        } else if (isEqual(tag, TAG_ND)) {
            switch (mode) {
                case ND:
                    if (stack.peek().equals(TAG_NODE))
                        mode = Mode.NODE;
                    else if (stack.peek().equals(TAG_WAY))
                        mode = Mode.WAY;
                    else
                        mode = Mode.OUTER;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_TAG)) {
            switch (mode) {
                case TAG:
                    if (stack.peek().equals(TAG_NODE))
                        mode = Mode.NODE;
                    else if (stack.peek().equals(TAG_WAY))
                        mode = Mode.WAY;
                    else
                        mode = Mode.OUTER;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else {
            //too many tags we need to ignore to list them all
            //assume an unrecognized tag is just going to be ignored
            //so we comment out the usual Warnings.unknownCloseTag call
            //Warnings.unknownCloseTag(tag, line, col);
        }
    }
    
    /**
     * Return the speed, in kilometers per hour, from the given text. If no
     * number is contained, -1 is returned. If the text contains "mph" (or MPH),
     * the unit will be treated as MPH and converted to kilometers per hour.
     * Otherwise, the unit will be treated as kilometers per hour and not changed.
     * @param text The text containing the speed.
     * @return The speed found in the text in kilometers per hour, or -1d if
     *         the speed couldn't be found.
     */
    private double getSpeedInKM(String text) {
        String digits = text.replaceAll("[^\\d]","");
        if (digits.length() < 1)
            return -1d;
        double speed = Double.parseDouble(digits);
        if (text.contains("mph") || text.contains("MPH")) {
            speed = Logic2D.MPHtoKPH(speed);
        }
        return speed;
    }
    
    /**
     * Return a Vector2D from a text containing coordinates.
     * @param text The text containing coordinates in "lat,lon" format.
     * @param line The line number that the coordinates are on.
     * @param col The column number that the coordinates are on.
     * @return The Vector2D that represents the coordinates contained in text.
     */
    private Vector2D getCoords(String text, int line, int col) throws PFParseException {
        String[] coord = text.split(",");
        if ((coord.length == 2) || (coord.length == 3 && coord[2].trim().equals("0"))) {
            double[] latlon = new double[2];
            try {
                latlon[0] = Double.parseDouble(coord[0].trim());
                latlon[1] = Double.parseDouble(coord[1].trim());
                return new Vector2D(latlon[0], latlon[1]);
            } catch (NumberFormatException nfe) {
                String tag = stack.isEmpty()? "null" : stack.peek();
                Errors.invalidInput(tag, "One of the coordinates was not a recognized number.", line, col);
            }
        } else {
            String tag = stack.isEmpty()? "null" : stack.peek();
            Errors.invalidInput(tag, "Coordinates were not entered in \"lat,lon\" format.", line, col);
        }
        return null;
    }

    /**
     * Test to see if two strings are equal (ignoring external spaces and
     * using a case insensitive approach).
     * @param a The first string to test.
     * @param b The second string to test.
     * @return true if a and b are equal according to the definition in the
     *         method description.
     */
    private boolean isEqual(String a, String b) {
        return (a.trim().toLowerCase().equals(b.trim().toLowerCase()));
    }
    
    /**
     * Determine if a given tag was opened, but not yet closed.
     * @param tag The tag to search for.
     * @return true if tag was located in the stack of opened tags.
     */
    private boolean isOpen(String tag) {
        // try to find elem by emptying stack. if found, s will equal tag
        String s = stack.peek();
        Stack<String> temp = new Stack<String>();
        while ((!s.equals(tag)) && (!stack.isEmpty())) {
            s = temp.push(stack.pop());
        }
        
        // put all emptied items back into the stack
        while (!temp.isEmpty()) {
            stack.push(temp.pop());
        }
        
        // if tag was found, it will be equal to s
        return (s.equals(tag));
    }
}
