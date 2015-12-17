package org.rowan.pathfinder.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.pathfinder.pathfinder.Logic2D;

/**
 * Test Class used to help in the creation of Event XML files. Delete upon
 * delivery.
 */
public class EventParserKMLtoXML implements OSMSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, PLACEMARK, DESC, POLY, OB, LR, COORDS}
    private static final String TAG_PLACEMARK= "Placemark";
    private static final String TAG_COORDS = "coordinates";
    private static final String TAG_DESC = "description";
    private static final String TAG_POLY = "Polygon";
    private static final String TAG_OB = "outerBoundaryIs";
    private static final String TAG_LR = "LinearRing";
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The mode of that parser is in */
    private Mode mode = Mode.NONE;
    private LinkedList<Vector2D> r_coordList = new LinkedList<Vector2D>();
    private String r_sev = null;
    private FileWriter fw;
    
    
    /**
     * Clear all roads from this parser so it can be reused. Be warned,
     * you must call extractRoads() before this method to retrieve a
     * list of any previously parsed roads.
     */
    public void init(File file) {
        try {
            fw = new FileWriter(file);
            fw.write("<Events>\n");
        } catch (IOException ex) {
           // System.err.println("bad file name");
        }
    }
    
    public void close() {
        try {
            fw.write("</Events>\n");
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(EventParserKMLtoXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
        switch (mode) {
            case DESC:
                r_sev = text.toString();
                break;
            case COORDS:
                String[] vectors = text.trim().split(" ");
                for (int i = 0; i < vectors.length; i++) {
                    String[] abc = vectors[i].split(",");
                    r_coordList.add(new Vector2D(Double.parseDouble(abc[0]), Double.parseDouble(abc[1])));
                }
                break;
        }
    }

    /**
     * Handle an opening tag.
     * @param tag The tag that was opened.
     * @param attributes The attributes dictionary of the tag.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    private void openTag(String tag, Dictionary attributes, int line, int col) throws PFParseException {
        if (isEqual(tag, TAG_PLACEMARK)) {
            switch (mode) {
                case NONE:
                    mode = Mode.PLACEMARK;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_DESC)) {
            switch (mode) {
                case PLACEMARK:
                    mode = Mode.DESC;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_POLY)) {
            switch (mode) {
                case PLACEMARK:
                    r_coordList.clear();
                    mode = Mode.POLY;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_OB)) {
            switch (mode) {
                case POLY:
                    mode = Mode.OB;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_LR)) {
            switch (mode) {
                case OB:
                    mode = Mode.LR;
                    break;
                default:
            }            
        } else if (isEqual(tag, TAG_COORDS)) {
            switch (mode) {
                case LR:
                    mode = Mode.COORDS;
                    break;
                default:
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
        if (isEqual(tag, TAG_COORDS)) {
            switch (mode) {
                case COORDS:
                    try {
                        fw.write("\t<Event>\n");
                        fw.write("\t\t<StartDate>05/25/2005</StartDate>\n");
                        fw.write("\t\t<EndDate>10/15/2010</EndDate>\n");
                        fw.write("\t\t<Severity>" + r_sev + "</Severity>\n");
                        fw.write("\t\t<Description>Event Description</Description>\n");
                        fw.write("\t\t<HasMines>false</HasMines>\n");
                        fw.write("\t\t<OuterBoundary>\n");
                        for (Vector2D v : r_coordList) {
                            fw.write("\t\t\t<Coord>" + v.getX() + "," + v.getY() + "</Coord>\n");
                        }
                        fw.write("\t\t</OuterBoundary>\n");
                        fw.write("\t</Event>\n");
                    } catch (IOException ex) {
                        Logger.getLogger(EventParserKMLtoXML.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    mode = Mode.LR;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_LR)) {
            switch (mode) {
                case LR:
                    mode = Mode.OB;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_OB)) {
            switch (mode) {
                case OB:
                    mode = Mode.POLY;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_POLY)) {
            switch (mode) {
                case POLY:
                    mode = Mode.PLACEMARK;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_DESC)) {
            switch (mode) {
                case DESC:
                    mode = Mode.PLACEMARK;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        }else if (isEqual(tag, TAG_PLACEMARK)) {
            switch (mode) {
                case PLACEMARK:
                    mode = Mode.NONE;
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
