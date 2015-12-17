package org.rowan.pathfinder.parser;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.pathfinder.pathfinder.Underpass;

/**
 * Class <code>UnderpassParser</code> is responsible for parsing an XML file
 * with underpass data into <code>Underpass</code> objects.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class UnderpassParser implements XMLSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, OUTER, INST, COORD, OVER, UNDER, HEIGHT,
                              UNKNOWN}
    /** The name of the outer tag of the file */
    private static final String TAG_OUTER = "Underpasses";
    /** The name of an underpass instance tag */
    private static final String TAG_INST  = "Underpass";
     /** The name of the coordinates tag */
    private static final String TAG_COORD = "Coord";
    /** The name of the over road tag */
    private static final String TAG_OVER  = "OverRoad";
    /** The name of the under road tag */
    private static final String TAG_UNDER  = "UnderRoad";
    /** The name of the height tag */
    private static final String TAG_HEIGHT = "Height";    
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The set of Underpasses that have been parsed */
    private Set<Underpass> underPasses = new HashSet<Underpass>();
    /** The mode of that parser is in */
    private Mode mode = Mode.NONE;
    /** The road name of the road that is located over the underpass */
    private String up_over = null;
    /** The road name of the road that is located under the underpass */
    private String up_under = null;
    /** The coordinates of the underpass */
    private Vector2D up_location = null;
    /** The height of the underpass from the ground of the under road. */
    private double up_height = -1f;
    
    /**
     * Return all underpasses that were parsed using this parser. Be warned,
     * if this parser is reused, any newly parsed underpasses will be added
     * to this list unless clearUnderpasses() is called.
     * @return all parsed underpasses.
     */
    public Set<Underpass> extractUnderpasses() {
        return underPasses;
    }
    
    /**
     * Clear all underpasses from this parser so it can be reused. Be warned,
     * you must call extractUnderpasses() before this method to retrieve a
     * list of any previously parsed underpasses.
     */
    public void clearUnderpasses() {
        underPasses = new HashSet<Underpass>();
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
        if (mode == Mode.COORD) {
            up_location = getCoords(text, line, col);
        } else if (mode == Mode.OVER) {
            up_over = text.trim();
        } else if (mode == Mode.UNDER) {
            up_under = text.trim();
        } else if (mode == Mode.HEIGHT) {
            up_height = getHeight(text, line, col);
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
                    up_location = null;
                    up_over = null;
                    up_under = null;
                    up_height = -1f;
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_COORD)) {
            switch (mode) {
                case INST:
                    mode = Mode.COORD;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_HEIGHT)) {
            switch (mode) {
                case INST:
                    mode = Mode.HEIGHT;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_OVER)) {
            switch (mode) {
                case INST:
                    mode = Mode.OVER;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_UNDER)) {
            switch (mode) {
                case INST:
                    mode = Mode.UNDER;
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
                    if (up_location == null) {
                        Warnings.unexpectedClose(tag, TAG_COORD, line, col);
                    } else if (up_under == null) {
                        Warnings.unexpectedClose(tag, TAG_UNDER, line, col);
                    } else if (up_height < 0) {
                        Warnings.unexpectedClose(tag, TAG_HEIGHT, line, col);
                    } else {
                        underPasses.add(new Underpass(up_location, up_over, up_under, up_height));
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
            up_location = null;
            up_over = null;
            up_under = null;
            up_height = -1f;
            mode = Mode.OUTER;
        } else if (isEqual(tag, TAG_COORD)) {
            switch (mode) {
                case COORD:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_OVER)) {
            switch (mode) {
                case OVER:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        }
        else if (isEqual(tag, TAG_UNDER)) {
            switch (mode) {
                case UNDER:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } 
        else if (isEqual(tag, TAG_HEIGHT)) {
            switch (mode) {
                case HEIGHT:
                    mode = Mode.INST;
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
     * Return the underpass height from text containing underpass height data.
     * @param text The text containing the underpass height.
     * @param line The line number that the height is on.
     * @param col The column number that the height is on.
     * @return A double representing the underpass height contained in text.
     */
    private double getHeight(String text, int line, int col) throws PFParseException {
        try {
            double height = Double.parseDouble(text.trim());
            return height;
        } catch (NumberFormatException nfe) {
            Errors.invalidInput(stack.peek(), "The underpass height was not a recognized number.", line, col);
        }
        return -1f;
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
            tag.equals(TAG_COORD) ||
            tag.equals(TAG_OVER) ||
            tag.equals(TAG_UNDER) ||
            tag.equals(TAG_HEIGHT)
        );
    }
}
