package org.rowan.pathfinder.parser;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.rowan.pathfinder.pathfinder.TerrainType;
import org.rowan.pathfinder.pathfinder.Vehicle;

/**
 * Class <code>VehicleParser</code> is responsible for parsing an XML file
 * with vehicle data into <code>Vehicle</code> objects.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class VehicleParser implements XMLSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, OUTER, INST, NAME, HEIGHT, WIDTH, MAXSPEED,
                              MINE, TRAV, TRAVINNER, UNKNOWN}
    /** The name of the outer tag of the file */
    private static final String TAG_OUTER = "Vehicles";
    /** The name of a vehicle instance tag */
    private static final String TAG_INST  = "Vehicle";
    /** The name of the name tag */
    private static final String TAG_NAME = "Name";
    /** The name of the height tag */
    private static final String TAG_HEIGHT = "Height"; 
    /** The name of the width tag */
    private static final String TAG_WIDTH = "Width"; 
     /** The name of the max speed tag */
    private static final String TAG_MAXSPEED = "MaxSpeed";
    /** The name of the mine resistant tag */
    private static final String TAG_MINE_RESIST  = "MineResistant";
    /** The name of the traversable tag */
    private static final String TAG_TRAVERSE  = "TerrainTraversability";
       
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The set of Vehicles that have been parsed */
    private Set<Vehicle> vehicles = new HashSet<Vehicle>();
    /** The mode that parser is in */
    private Mode mode = Mode.NONE;
    /** The name of the vehicle */
    private String v_name = null;
    /** The height of the vehicle */
    private double v_height = -1f;
    /** The width of the vehicle */
    private double v_width = -1f;
    /** The max speed of the vehicle */
    private int v_maxspeed = -1;
    /** Is the vehicle resistant to mines? */
    private boolean v_mine_resist = false;
    /** The terrain type that is about to be added to the map */
    private TerrainType v_type = null;
    /** The traversability map of the vehicle */
    private Map<TerrainType, Double> v_map = new EnumMap<TerrainType, Double>(TerrainType.class);
    
    /**
     * Return all vehicles that were parsed using this parser. Be warned,
     * if this parser is reused, any newly parsed vehicles will be added
     * to this list unless clearVehicles() is called.
     * @return all parsed vehicles.
     */
    public Set<Vehicle> extractVehicles() {
        return vehicles;
    }
    
    /**
     * Clear all vehicles from this parser so it can be reused. Be warned,
     * you must call extractVehicles() before this method to retrieve a
     * list of any previously parsed vehicles.
     */
    public void clearVehicles() {
        vehicles = new HashSet<Vehicle>();
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
            String s = stack.peek();
            if (s.equals(tag)) {
                closeTag(tag, line, col); //we closed the last opened tag, everything is OK
                return;
            }
            
            s = stack.pop();
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
            case NAME:
                v_name = text.trim();
                break;
            case HEIGHT:
                v_height = getDoubleHorW(text, line, col);
                break;
            case WIDTH:
                v_width = getDoubleHorW(text, line, col);
                break;
            case MAXSPEED:
                v_maxspeed = getIntMS(text, line, col);
                break;
            case MINE:
                v_mine_resist = getBoolMS(text, line, col);
                break;
            case TRAVINNER:
                TerrainType type = TerrainType.valueOf(stack.peek());
                double value = getDoubleT(text, line, col);
                v_map.put(type, value);
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
                    v_name = null;
                    v_height = -1f;
                    v_width = -1f;
                    v_maxspeed = -1;
                    v_mine_resist = false;
                    v_type = null;
                    v_map = new EnumMap<TerrainType, Double>(TerrainType.class);
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_NAME)) {
            switch (mode) {
                case INST:
                    mode = Mode.NAME;
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
        } else if (isEqual(tag, TAG_WIDTH)) {
            switch (mode) {
                case INST:
                    mode = Mode.WIDTH;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_MAXSPEED)) {
            switch (mode) {
                case INST:
                    mode = Mode.MAXSPEED;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_MINE_RESIST)) {
            switch (mode) {
                case INST:
                    mode = Mode.MINE;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_TRAVERSE)) {
            switch (mode) {
                case INST:
                    mode = Mode.TRAV;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else {
            //determine if the tag is equal to a terrain tag
            try {
                TerrainType type = TerrainType.valueOf(tag.trim());
                if (mode == Mode.TRAV) {
                    v_type = type;
                    mode = Mode.TRAVINNER;
                } else {
                    Warnings.unexpectedOpen(tag, TAG_TRAVERSE, line, col);
                }
            } catch (IllegalArgumentException ex) {
                Warnings.unknownOpenTag(tag, line, col);
            } finally {
                return;
            }
        }
    }
    
    /**
     * Handle a closing tag.
     * @param tag The tag that was closed.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    private void closeTag(String tag, int line, int col) throws PFParseException {
        String s = stack.pop();
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
                    if (v_name == null) {
                        Warnings.unexpectedClose(tag, TAG_NAME, line, col);
                    } else if (v_height < 0) {
                        Warnings.unexpectedClose(tag, TAG_HEIGHT, line, col);
                    } else if (v_width < 0) {
                        Warnings.unexpectedClose(tag, TAG_WIDTH, line, col);
                    } else if (v_maxspeed < 0) {
                        Warnings.unexpectedClose(tag, TAG_MAXSPEED, line, col);
                    } else {
                        vehicles.add(new Vehicle(v_name, v_height, v_width, v_maxspeed, v_mine_resist, v_map));
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
            mode = Mode.OUTER;
        } else if (isEqual(tag, TAG_NAME)) {
            switch (mode) {
                case NAME:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_HEIGHT)) {
            switch (mode) {
                case HEIGHT:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_WIDTH)) {
            switch (mode) {
                case WIDTH:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        }
        else if (isEqual(tag, TAG_MAXSPEED)) {
            switch (mode) {
                case MAXSPEED:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } 
        else if (isEqual(tag, TAG_MINE_RESIST)) {
            switch (mode) {
                case MINE:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_TRAVERSE)) {
            switch (mode) {
                case TRAV:
                    mode = Mode.INST;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else {
            //determine if the tag is equal to a terrain tag
            try {
                TerrainType.valueOf(tag.trim());
                if (stack.isEmpty()) {
                    Errors.invalidClose(tag, line, col);
                } else {
                    if (s.equals(tag.trim())) {
                        if (mode == Mode.TRAVINNER) {
                            v_type = null;
                            mode = Mode.TRAV;
                        } else {
                            Errors.unknownOnClose(tag, line, col);
                        }
                    } else {
                        Errors.incorrectClose(tag, s, line, col);
                    }
                }
            } catch (IllegalArgumentException ex) {
                Warnings.unknownCloseTag(tag, line, col);
            } finally {
                return;
            }
        }
    }
    
    /**
     * Return a double value from text containing vehicle height or width data.
     * @param text The text containing the vehicle height or width.
     * @param line The line number that the data is on.
     * @param col The column number that the data is on.
     * @return A double representing the vehicle height or width contained in text.
     */
    private double getDoubleHorW(String text, int line, int col) throws PFParseException {
        try {
            double dub = Double.parseDouble(text.trim());
            return dub;
        } catch (NumberFormatException nfe) {
            Errors.invalidInput(stack.peek(), "The vehicle height or width was not a recognized number.", line, col);
        }
        return -1f;
    }
     /**
     * Return a double value from text containing traversability data.
     * @param text The text containing the traversability for a particular terrain.
     * @param line The line number that the data is on.
     * @param col The column number that the data is on.
     * @return A double representing the traversability value contained in text.
     */
    private double getDoubleT(String text, int line, int col) throws PFParseException {
        try {
            double input, dub;
            input = Double.parseDouble(text.trim());
            dub = Math.max(input, 0);
            dub = Math.min(1, dub);
            if (input != dub)
                Warnings.unexpectedText(stack.peek(), line, col);
            return dub;
        } catch (NumberFormatException nfe) {
            Errors.invalidInput(stack.peek(), "The traversability value was not a recognized number.", line, col);
        }
        return -1f;
    }
    
    /**
     * Return an int value from text containing vehicle max speed data.
     * @param text The text containing the vehicle max speed.
     * @param line The line number that the data is on.
     * @param col The column number that the data is on.
     * @return An int representing the vehicle max speed contained in text.
     */
    private int getIntMS(String text, int line, int col) throws PFParseException {
        try {
            int num = Integer.parseInt(text.trim());
            return num;
        } catch (NumberFormatException nfe) {
            Errors.invalidInput(stack.peek(), "The vehicle max speed was not a recognized number.", line, col);
        }
        return -1;
    }
    
    /**
     * Return a boolean value from text containing vehicle mine resistance data.
     * @param text The text containing the vehicle mine resistance.
     * @param line The line number that the data is on.
     * @param col The column number that the data is on.
     * @return A boolean representing the vehicle's resistance to mines.
     */
    private boolean getBoolMS(String text, int line, int col) throws PFParseException {
        String t = text.trim();
        if (t.equals("1")) return true;
        if (t.equals("0")) return false;
        if (t.equalsIgnoreCase("t")) return true;
        if (t.equalsIgnoreCase("f")) return false;
        if (t.equalsIgnoreCase("yes")) return true;
        if (t.equalsIgnoreCase("no")) return false;
        if (t.equalsIgnoreCase("true")) return true;
        if (t.equalsIgnoreCase("false")) return false;
        
        Errors.invalidInput(stack.peek(), "The vehicle mine resistance not a recognized boolean.", line, col);
        return false;
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
            tag.equals(TAG_HEIGHT) ||
            tag.equals(TAG_WIDTH) ||
            tag.equals(TAG_MAXSPEED) ||
            tag.equals(TAG_MINE_RESIST) ||
            tag.equals(TAG_TRAVERSE) ||
            Arrays.asList(TerrainType.values()).contains(TerrainType.valueOf(tag))
        );
    }
}
