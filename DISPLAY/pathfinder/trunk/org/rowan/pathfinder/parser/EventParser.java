package org.rowan.pathfinder.parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.Polygon2D;
import org.rowan.pathfinder.pathfinder.Event;
import org.rowan.pathfinder.pathfinder.Logic2D;

/**
 * Class <code>EventParser</code> is responsible for parsing an XML file
 * with event data into <code>Event</code> objects.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class EventParser implements XMLSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, OUTER, INST, DSTART, DEND, SEVERITY, DESC,
                              MINES, BOUND, COORD, UNKNOWN}
    /** The name of the outer tag of the file */
    private static final String TAG_OUTER = "Events";
    /** The name of an event instance tag */
    private static final String TAG_INST  = "Event";
    /** The name of the start of the date tag */
    private static final String TAG_DATE_START  = "StartDate";
    /** The name of the end of the date tag */
    private static final String TAG_DATE_END  = "EndDate";
    /** The name of the severity tag */
    private static final String TAG_SEVERITY  = "Severity";
    /** The name of the description tag */
    private static final String TAG_DESCRIPTION  = "Description";
    /** The name of the has mines tag */
    private static final String TAG_MINES = "HasMines";
    /** The name of the boundary tag */
    private static final String TAG_BOUNDARY  = "OuterBoundary";
     /** The name of the coordinates tag */
    private static final String TAG_COORD = "Coord"; 
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The set of Events that have been parsed */
    private Set<Event> events = new HashSet<Event>();
    /** The mode of that parser is in */
    private Mode mode = Mode.NONE;
    /** The start date of the event */
    private Calendar e_start = null;
    /** The end date of the event */
    private Calendar e_end = null;
    /** The severity of the event */
    private double e_severity = -1f;
    /** The description of the event */
    private String e_description = null;
    /** Does the event have mines? */
    private boolean e_hasMines = false;
    /** The polygon representing the boundary of the event */
    private Polygon2D e_boundary = null;
    /** The list of vertices that will make up the polygon */
    private List<Vector2D> e_vertices = new ArrayList<Vector2D>();
    
    /**
     * Return all events that were parsed using this parser. Be warned,
     * if this parser is reused, any newly parsed events will be added
     * to this list unless clearEvents() is called.
     * @return all parsed terrains.
     */
    public Set<Event> extractEvents() {
        return events;
    }
    
    /**
     * Clear all events from this parser so it can be reused. Be warned,
     * you must call extractEvents() before this method to retrieve a
     * list of any previously parsed events.
     */
    public void clearEvents() {
        events = new HashSet<Event>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String tag, int line, int col) throws PFParseException {
        stack.push(tag);
        openTag(tag, line, col);
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
                if (!isRecognizedTag(tag)) {
                    Warnings.unknownCloseTag(tag, line, col); //we closed a tag that doesn't exist
                    return;
                }
                Errors.invalidClose(tag, line, col); //we closed a tag that was never even opened
            }
            stack.push(s);
        } catch (EmptyStackException ese) {
            if (!isRecognizedTag(tag)) {
                Warnings.unknownCloseTag(tag, line, col); //we closed a tag that doesn't exist
                return;
            }
            Errors.invalidClose(tag, line, col); //we closed a tag but NO tags were opened
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void text(String text, int line, int col) throws PFParseException {
        switch (mode) {
            case DSTART:
                e_start = getDate(text.trim(), line, col);
                break;
            case DEND:
                e_end = getDate(text.trim(), line, col);
                break;
            case SEVERITY:
                e_severity = getSeverity(text.trim(), line, col);
                break;
            case DESC:
                e_description = text.trim();
                break;
            case MINES:
                e_hasMines = getBoolHasMines(text.trim(), line, col);
                break;
            case COORD:
                e_vertices.add(getCoords(text.trim(), line, col));
                break;
            default:
                if (!text.trim().equals("")) {
                    String tag;
                    if (!stack.isEmpty()) {
                        tag = "(no_tag)";
                    } else {
                        tag = stack.peek();
                    }
                    Warnings.unexpectedText(tag, line, col);
                }
        }
    }

    /**
     * Handle an opening tag.
     * @param tag The tag that was opened.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    private void openTag(String tag, int line, int col) throws PFParseException {
        if (isEqual(tag, TAG_OUTER)) {
            switch (mode) {
                case NONE:
                    mode = Mode.OUTER;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_INST)) {
            switch (mode) {
                case OUTER:
                    e_start = null;
                    e_end = null;
                    e_severity = -1f;
                    e_hasMines = false;
                    e_description = null;
                    e_vertices = new ArrayList<Vector2D>();
                    e_boundary = null;
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_DATE_START)) {
            switch (mode) {
                case INST:
                    mode = Mode.DSTART;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_DATE_END)) {
            switch (mode) {
                case INST:
                    mode = Mode.DEND;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_SEVERITY)) {
            switch (mode) {
                case INST:
                    mode = Mode.SEVERITY;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_DESCRIPTION)) {
            switch (mode) {
                case INST:
                    mode = Mode.DESC;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_MINES)) {
            switch (mode) {
                case INST:
                    mode = Mode.MINES;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_BOUNDARY)) {
            switch (mode) {
                case INST:
                    mode = Mode.BOUND;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_COORD)) {
            switch (mode) {
                case BOUND:
                    mode = Mode.COORD;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else {
            Warnings.unknownOpenTag(tag, line, col);
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
        } else if (isEqual(tag, TAG_INST)) {
            switch (mode) {
                case INST:
                    if (e_start == null) {
                        Warnings.unexpectedClose(tag, TAG_DATE_START, line, col);
                    } else if (e_severity < 0) {
                        Warnings.unexpectedClose(tag, TAG_SEVERITY, line, col);
                    } else if (e_boundary == null) {
                        Warnings.unexpectedClose(tag, TAG_BOUNDARY, line, col);
                    } else {
                        events.add(new Event(e_start, e_end, e_severity, e_description, e_boundary, e_hasMines));
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
            e_start = null;
            e_end = null;
            e_severity = -1f;
            e_description = null;
            e_hasMines = false;
            e_vertices = new ArrayList<Vector2D>();
            e_boundary = null;
            mode = Mode.OUTER;
        } else if (isEqual(tag, TAG_DATE_START)) {
            switch (mode) {
                case DSTART:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_DATE_END)) {
            switch (mode) {
                case DEND:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_SEVERITY)) {
            switch (mode) {
                case SEVERITY:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_DESCRIPTION)) {
            switch (mode) {
                case DESC:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_MINES)) {
            switch (mode) {
                case MINES:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_BOUNDARY)) {
            switch (mode) {
                case BOUND:
                    if (e_vertices.size() < 3) {
                        Warnings.unexpectedClose(tag, TAG_COORD, line, col);
                    } else {
                        Vector2D position = Logic2D.getCentroid(e_vertices);
                        if (!Polygon2D.validateVertices(Logic2D.centerVertices(e_vertices))) {
                            Collections.reverse(e_vertices);
                            if (!Polygon2D.validateVertices(Logic2D.centerVertices(e_vertices))) {
                                Warnings.general("Found </" + TAG_BOUNDARY +
                                        "> but the coordinates given do not " +
                                        "form a convex polygon.", line, col);
                            } else {
                                e_boundary = new Polygon2D(position, Logic2D.centerVertices(e_vertices));
                            }
                        } else {
                            e_boundary = new Polygon2D(position, Logic2D.centerVertices(e_vertices));
                        }
                    }
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } 
        else if (isEqual(tag, TAG_COORD)) {
            switch (mode) {
                case COORD:
                    mode = Mode.BOUND;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else {
            Warnings.unknownCloseTag(tag, line, col);
        }
    }
    
    /**
     * Return the date of an event contained in event data text.
     * @param text The text containing the date (MM/DD/YY).
     * @param line The line number that the date is on.
     * @param col The column number that the date is on.
     * @return a Calendar object representing the date contained in text.
     */
    private Calendar getDate(String text, int line, int col) throws PFParseException {
        String[] date = text.split("/");
        if (date.length == 3) {
            int year, month, day;
            Calendar c = GregorianCalendar.getInstance();
            try {
                month = Integer.parseInt(date[0]);
                day = Integer.parseInt(date[1]);
                year = Integer.parseInt(date[2]);
                if (!validMonth(month)) {
                    Errors.invalidFormat(month + "", "The month should " +
                            "be between 1 and 12.", line, col);
                    return null;
                }
                if (!validDay(day, month)) {
                    Errors.invalidFormat(day + "", "The day should be " +
                            "between 1 and " + maxDay(month) + ".", line, col +
                            date[0].length());
                    return null;
                }
                if (!validYear(year)) {
                    Errors.invalidFormat(year + "", "The year must be a four " +
                            "digit number greater than 0.", line, col +
                            date[0].length() + date[1].length());
                }
                 if (year < 100) {
                        Warnings.invalidFormat(year + "", "The year is being " +
                                "interpreted as "  + (year+2000), line, col +
                                date[0].length() + date[1].length());
                        year += 2000;
                }
                c.set(year, (month - 1), day);
                return c;
            } catch (NumberFormatException ex) {
                Errors.invalidInput(TAG_DATE_START + " OR " + TAG_DATE_END, text, line, col);
                return null;
            }
        } else {
            Errors.invalidInput(TAG_DATE_START + " OR " + TAG_DATE_END, text, line, col);
            return null;
        }
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
     * Return the event severity from text containing event data.
     * @param text The text containing the event severity.
     * @param line The line number that the severity is on.
     * @param col The column number that the severity is on.
     * @return A double representing the severity contained in text.
     */
    private double getSeverity(String text, int line, int col) throws PFParseException {
        try {
            double severity = Double.parseDouble(text);
            return severity;
        } catch (NumberFormatException nfe) {
            Errors.invalidInput(stack.peek(), "The event severity was not a recognized number.", line, col);
        }
        return -1f;
    }
    
     /**
     * Return a boolean value from text containing event mine data.
     * @param text The text containing the event mine hasMines boolean.
     * @param line The line number that the data is on.
     * @param col The column number that the data is on.
     * @return A boolean representing the existence of mines during the event.
     */
    private boolean getBoolHasMines(String text, int line, int col) throws PFParseException {
        String t = text.trim();
        if (t.equals("1")) return true;
        if (t.equals("0")) return false;
        if (t.equalsIgnoreCase("t")) return true;
        if (t.equalsIgnoreCase("f")) return false;
        if (t.equalsIgnoreCase("yes")) return true;
        if (t.equalsIgnoreCase("no")) return false;
        if (t.equalsIgnoreCase("true")) return true;
        if (t.equalsIgnoreCase("false")) return false;
        
        Errors.invalidInput(stack.peek(), "The event's HasMines field was a recognized boolean.", line, col);
        return false;
    }
    
    /**
     * Check if a month is valid.
     * @param month The number of the month.
     * @return true if month > 0 and month < 13, false otherwise.
     */
    private boolean validMonth(int month) {
        return ((month > 0) && (month < 13));
    }
    
    /**
     * Check if a day is valid.
     * @param day The number of the day.
     * @param month The number of the month that the day is in.
     * @return true if day is a valid day of the month
     */
    private boolean validDay(int day, int month) {
        if (day>0 && day<32) {
            return (day <= Integer.parseInt(maxDay(month)));
        } else {
            return false;
        }
    }
    
    /**
     * Check if a year is valid.
     * @param year The year.
     * @return true if year >= 0, false otherwise.
     */
    private boolean validYear(int year) {
        return (year >=0);
    }
    
    /**
     * Return the number of days in a given month.
     * @param month The month (1 to 12).
     * @return The number of days in the month, 31 if month is invalid.
     */
    private String maxDay(int month) {
        switch (month) {
            case 2:
                return "28";
            case 4:
            case 6:
            case 9:
            case 11:
                return "30";
            default:
                return "31";
        }
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
    
    /**
     * Test if a given tag name is recognized.
     * @param tag The tag name to test.
     * @return true if it is one of the recognized tag names.
     */
    private boolean isRecognizedTag(String tag) {
        return (
            tag.equals(TAG_OUTER) ||
            tag.equals(TAG_INST) ||
            tag.equals(TAG_DATE_START) ||
            tag.equals(TAG_DATE_END) ||
            tag.equals(TAG_SEVERITY) ||
            tag.equals(TAG_DESCRIPTION) ||
            tag.equals(TAG_MINES) ||
            tag.equals(TAG_BOUNDARY) ||
            tag.equals(TAG_COORD)
        );
    }
}
