package org.rowan.pathfinder.parser;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.pathfinder.pathfinder.SpeedLimit;

/**
 * Class <code>SpeedLimitParser</code> is responsible for parsing an XML file
 * with speed limit data into <code>SpeedLimit</code> objects.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class SpeedLimitParser implements XMLSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, OUTER, INST_NO_COORDS, INST_AFTER_START_OR_MID,
                              INST_AFTER_END, START, MID, END, NAME, COORD_START,
                              COORD_MID, COORD_END, LIMIT_OUTER, LIMIT_START,
                              LIMIT_MID, UNKNOWN}
    /** The name of the outer tag of the file */
    private static final String TAG_OUTER = "SpeedLimits";
    /** The name of a speed limit instance tag */
    private static final String TAG_INST  = "SpeedLimit";
    /** The name of a road name tag */
    private static final String TAG_NAME  = "RoadName";
    /** The name of the start tag */
    private static final String TAG_START = "Start";
    /** The name of the mid tag */
    private static final String TAG_MID   = "Mid";
    /** The name of the end tag */
    private static final String TAG_END   = "End";
    /** The name of the coordinates tag */
    private static final String TAG_COORD = "Coord";
    /** The name of the limit tag */
    private static final String TAG_LIMIT = "Limit";    
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The set of SpeedLimits that have been parsed */
    private Set<SpeedLimit> speedLimits = new HashSet<SpeedLimit>();
    /** The mode of that parser is in */
    private Mode mode = Mode.NONE;
    /** The road name of the speed limit currently being parsed */
    private String sl_name = null;
    /** The limit of the speed limit currently being parsed */
    private int sl_limit = -1;
    /** The starting coordinates of the speed limit currently being parsed */
    private Vector2D sl_start = null;
    /** The ending coordinates of the speed limit currently being parsed */
    private Vector2D sl_end = null;
    
    /**
     * Return all speed limits that were parsed using this parser. Be warned,
     * if this parser is reused, any new parsed speed limits will be added
     * to this list unless clearSpeedLimits() is called.
     * @return all parsed speed limits.
     */
    public Set<SpeedLimit> extractSpeedLimits() {
        return speedLimits;
    }
    
    /**
     * Clear all speed limits from this parser so it can be reused. Be warned,
     * you must call extractSpeedLimits() before this method to retrieve a
     * list of any previously parsed speed limits.
     */
    public void clearSpeedLimits() {
        speedLimits = new HashSet<SpeedLimit>();
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
        if (mode == Mode.NAME) {
            sl_name = text.trim();
        } else if (mode == Mode.COORD_START) {
            sl_start = getCoords(text, line, col);
        } else if (mode == Mode.COORD_MID) {
            sl_end = getCoords(text, line, col);            
        } else if (mode == Mode.COORD_END) {
            sl_end = getCoords(text, line, col);
        } else if (mode == Mode.LIMIT_OUTER) {
            sl_limit = getLimit(text, line, col);
        } else if (mode == Mode.LIMIT_START) {
            sl_limit = getLimit(text, line, col);
        } else if (mode == Mode.LIMIT_MID) {
            sl_limit = getLimit(text, line, col);
        } else {
            if (!text.trim().equals("")) {
                String tag;
                if (!stack.isEmpty())
                    tag = "(no_tag)";
                else
                    tag = stack.peek();
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
                    sl_name = null;
                    sl_limit = -1;
                    sl_start = null;
                    sl_end = null;
                    mode = Mode.INST_NO_COORDS;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_NAME)) {
            switch (mode) {
                case INST_NO_COORDS:
                    mode = Mode.NAME;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_START)) {
            switch (mode) {
                case INST_NO_COORDS:
                    mode = Mode.START;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_MID)) {
            switch (mode) {
                case INST_AFTER_START_OR_MID:
                    mode = Mode.MID;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_END)) {
            switch (mode) {
                case INST_AFTER_START_OR_MID:
                    mode = Mode.END;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_COORD)) {
            switch (mode) {
                case START:
                    mode = Mode.COORD_START;
                    break;
                case MID:
                    mode = Mode.COORD_MID;
                    break;
                case END:
                    mode = Mode.COORD_END;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_LIMIT)) {
            switch (mode) {
                case INST_NO_COORDS:
                    if (sl_name == null) {
                        Warnings.unexpectedOpen(tag, TAG_NAME, line, col);
                    }
                    mode = Mode.LIMIT_OUTER;
                    break;
                case START:
                    mode = Mode.LIMIT_START;
                    break;
                case MID:
                    if (sl_limit >= 0) { //limit tag came before the coord tag
                        Warnings.unexpectedOpen(tag, TAG_COORD, line, col);
                    }
                    mode = Mode.LIMIT_MID;
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
                case INST_NO_COORDS:
                    if (sl_name == null) {
                        Warnings.unexpectedClose(tag, TAG_NAME, line, col);
                    } else if (sl_limit < 0) {
                        Warnings.unexpectedClose(tag, TAG_LIMIT, line, col);
                    } else {
                        speedLimits.add(new SpeedLimit(null, null, sl_name, sl_limit));
                    }
                    break;
                case INST_AFTER_END:
                    if (sl_name == null) {
                        Warnings.unexpectedClose(tag, TAG_NAME, line, col);
                    } else if (sl_limit < 0) {
                        Warnings.unexpectedClose(tag, TAG_LIMIT, line, col);
                    } else if (sl_start == null || sl_end == null) {
                        Warnings.unexpectedClose(tag, TAG_COORD, line, col);
                    } else {
                        speedLimits.add(new SpeedLimit(sl_start, sl_end, sl_name, sl_limit));
                    }
                    break;
                case INST_AFTER_START_OR_MID:
                    Warnings.unexpectedClose(tag, TAG_END, line, col);
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
            sl_name = null;
            sl_limit = -1;
            sl_start = null;
            sl_end = null;
            mode = Mode.OUTER;
        } else if (isEqual(tag, TAG_NAME)) {
            switch (mode) {
                case NAME:
                    if (stack.peek().equals(TAG_INST)) {
                        mode = Mode.INST_NO_COORDS;
                    } else {
                        Errors.unknownOnClose(tag, line, col);
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_START)) {
            switch (mode) {
                case START:
                    if (stack.peek().equals(TAG_INST)) {
                        mode = Mode.INST_AFTER_START_OR_MID;
                    } else {
                        Errors.unknownOnClose(tag, line, col);
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_MID)) {
            switch (mode) {
                case MID:
                    mode = Mode.INST_AFTER_START_OR_MID;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_END)) {
            switch (mode) {
                case END:
                    mode = Mode.INST_AFTER_END;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_COORD)) {
            switch (mode) {
                case COORD_START:
                    mode = Mode.START;
                    break;
                case COORD_MID:
                    if (sl_name == null) {
                        Warnings.unexpectedClose(tag, TAG_NAME, line, col);
                    } else if (sl_limit < 0) {
                        Warnings.unexpectedClose(tag, TAG_LIMIT, line, col);
                    } else if (sl_start == null || sl_end == null) {
                        Warnings.unexpectedClose(tag, TAG_COORD, line, col);
                    } else {
                        speedLimits.add(new SpeedLimit(sl_start, sl_end, sl_name, sl_limit));
                    }
                    sl_start = new Vector2D(sl_end.getX(), sl_end.getY()); //need a shallow copy
                    sl_end = null;
                    sl_limit = -1;
                    mode = Mode.MID;
                    //keep name the same, since you are on the same road
                    break;
                case COORD_END:
                    mode = Mode.END;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_LIMIT)) {
            switch (mode) {
                case LIMIT_OUTER:
                    mode = Mode.INST_NO_COORDS;
                    break;
                case LIMIT_START:
                    mode = Mode.START;
                    break;
                case LIMIT_MID:
                    mode = Mode.MID;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else {
            Warnings.unknownCloseTag(tag, line, col);
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
     * Return the speed limit from a text containing speed limit data.
     * @param text The text containing the speed limit.
     * @param line The line number that the limit is on.
     * @param col The column number that the limit is on.
     * @return An int representing the speed limit contained in text.
     */
    private int getLimit(String text, int line, int col) throws PFParseException {
        try {
            int limit = Integer.parseInt(text.trim());
            return limit;
        } catch (NumberFormatException nfe) {
            Errors.invalidInput(stack.peek(), "The speed limit was not a recognized number.", line, col);
        }
        return -1;
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
            tag.equals(TAG_NAME) ||
            tag.equals(TAG_START) ||
            tag.equals(TAG_MID) ||
            tag.equals(TAG_END) ||
            tag.equals(TAG_COORD) ||
            tag.equals(TAG_LIMIT)
        );
    }
}
