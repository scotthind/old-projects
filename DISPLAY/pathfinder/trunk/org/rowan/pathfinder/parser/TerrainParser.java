package org.rowan.pathfinder.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.Polygon2D;
import org.rowan.pathfinder.pathfinder.Logic2D;
import org.rowan.pathfinder.pathfinder.Terrain;
import org.rowan.pathfinder.pathfinder.TerrainType;

/**
 * Class <code>TerrainParser</code> is responsible for parsing an XML file
 * with terrain data into <code>Terrain</code> objects.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class TerrainParser implements XMLSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, OUTER, INST, TYPE, DESC, BOUND, COORD,
                              UNKNOWN}
    /** The name of the outer tag of the file */
    private static final String TAG_OUTER = "Terrains";
    /** The name of an event instance tag */
    private static final String TAG_INST  = "Terrain";
    /** The name of the type tag */
    private static final String TAG_TYPE  = "Type";
    /** The name of the description tag */
    private static final String TAG_DESCRIPTION  = "Description";
    /** The name of the boundary tag */
    private static final String TAG_BOUNDARY  = "OuterBoundary";
     /** The name of the coordinates tag */
    private static final String TAG_COORD = "Coord"; 
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The set of Events that have been parsed */
    private Set<Terrain> terrains = new HashSet<Terrain>();
    /** The mode of that parser is in */
    private Mode mode = Mode.NONE;
    /** The type of the terrain */
    private TerrainType t_type = null;
    /** The description of the event */
    private String t_description = null;
    /** The polygon representing the boundary of the event */
    private Polygon2D t_boundary = null;
    /** The list of vertices that will make up the polygon */
    private List<Vector2D> t_vertices = new ArrayList<Vector2D>();
    
    /**
     * Return all terrains that were parsed using this parser. Be warned,
     * if this parser is reused, any newly parsed events will be added
     * to this list unless clearTerrain() is called.
     * @return all parsed terrains.
     */
    public Set<Terrain> extractTerrains() {
        return terrains;
    }
    
    /**
     * Clear all terrains from this parser so it can be reused. Be warned,
     * you must call extractTerrains() before this method to retrieve a
     * list of any previously parsed terrains.
     */
    public void clearTerrains() {
        terrains = new HashSet<Terrain>();
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
            case TYPE:
                if (Arrays.asList(TerrainType.values()).contains(TerrainType.valueOf(text.trim()))) {
                    t_type = TerrainType.valueOf(text.trim());
                } else {
                    Warnings.invalidFormat(text.trim(), text.trim() +
                            " was not a recognized terrain type. ", line, col);
                }
                break;
            case DESC:
                t_description = text.trim();
                break;
            case COORD:
                t_vertices.add(getCoords(text.trim(), line, col));
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
                    t_type = null;
                    t_description = null;
                    t_vertices = new ArrayList<Vector2D>();
                    t_boundary = null;
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_TYPE)) {
            switch (mode) {
                case INST:
                    mode = Mode.TYPE;
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
                    if (t_type == null) {
                        Warnings.unexpectedClose(tag, TAG_TYPE, line, col);
                    } else {
                        terrains.add(new Terrain(t_boundary, t_type, t_description));
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
            t_type = null;
            t_description = null;
            t_vertices = new ArrayList<Vector2D>();
            t_boundary = null;
            mode = Mode.OUTER;
        } else if (isEqual(tag, TAG_TYPE)) {
            switch (mode) {
                case TYPE:
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
        } else if (isEqual(tag, TAG_BOUNDARY)) {
            switch (mode) {
                case BOUND:
                    if (t_vertices.size() < 3) {
                        Warnings.unexpectedClose(tag, TAG_COORD, line, col);
                    } else {
                        Vector2D position = Logic2D.getCentroid(t_vertices);
                        if (!Polygon2D.validateVertices(Logic2D.centerVertices(t_vertices))) {
                            Collections.reverse(t_vertices);
                            if (!Polygon2D.validateVertices(Logic2D.centerVertices(t_vertices))) {
                                Warnings.general("Found </" + TAG_BOUNDARY +
                                        "> but the coordinates given do not " +
                                        "form a convex polygon.", line, col);
                            } else {
                                t_boundary = new Polygon2D(position, Logic2D.centerVertices(t_vertices));
                            }
                        } else {
                            t_boundary = new Polygon2D(position, Logic2D.centerVertices(t_vertices));
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
    
    /**
     * Test if a given tag name is recognized.
     * @param tag The tag name to test.
     * @return true if it is one of the recognized tag names.
     */
    private boolean isRecognizedTag(String tag) {
        return (
            tag.equals(TAG_OUTER) ||
            tag.equals(TAG_INST) ||
            tag.equals(TAG_DESCRIPTION) ||
            tag.equals(TAG_BOUNDARY) ||
            tag.equals(TAG_COORD)
        );
    }
}
