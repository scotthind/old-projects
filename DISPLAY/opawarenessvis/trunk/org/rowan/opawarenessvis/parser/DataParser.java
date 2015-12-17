package org.rowan.opawarenessvis.parser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import org.rowan.opawarenessvis.data.Asset;
import org.rowan.opawarenessvis.data.Entity;
import org.rowan.opawarenessvis.data.OpSystem;

/**
 * Class RulesParser is responsible for parsing an XML file
 * with rules data into <code>Rules</code> objects.
 * 
 * @author Dan Urbano
 */
public class DataParser implements XMLSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, OUTER, SYSTEM, ENTITY, ASSET,
                              UNKNOWN}
    /** The name of the outer tag of the file */
    private static final String TAG_OUTER = "Data";
    /** The name of the system tag */
    private static final String TAG_SYSTEM  = "System";
    /** The name of the entity tag */
    private static final String TAG_ENTITY  = "Entity";
    /** The name of the asset tag*/
    private static final String TAG_ASSET  = "Asset";
    /** The name of the name attribute */
    private static final String ATR_NAME = "name";
    /** The name of the type attribute */
    private static final String ATR_TYPE  = "type";
    /** The name of the id attribute */
    private static final String ATR_ID  = "id";
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The mode of that parser is in */
    private Mode mode = Mode.NONE;
    /** The current system */
    private OpSystem d_system = null;
    /** The system name */
    private String d_sys_name = null;
    /** The system type */
    private String d_sys_type = null;
    /** The system id */
    private String d_sys_id = null;
    /** The current entity */
    private Entity d_entity = null;
    /** The entity type */
    private String d_entity_type = null;
    /** The entity id */
    private String d_entity_id = null;
    /** The current asset */
    private Asset d_asset = null;
    /** The asset type */
    private String d_asset_type = null;
    /** The asset id */
    private String d_asset_id = null;
    /** The stack of lists of rules */
    private Stack<Entity> d_entity_stack = new Stack<Entity>();
    /** The systems that were parsed */
    private List<OpSystem> d_data = new ArrayList<OpSystem>();

    /**
     * Return all systems that were parsed using this parser. Be warned,
     * if this parser is reused, any newly parsed data will be added
     * to this list unless clearData() is called.
     * @return All parsed Systems.
     */
    public List<OpSystem> extractData() {
        return d_data;
    }
    
    /**
     * Clear all data from this parser so it can be reused. Be warned,
     * you must call extractData() before this method to retrieve a
     * list of any previously parsed systems.
     */
    public void clearData() {
        d_entity_stack = new Stack<Entity>();
        d_data = new ArrayList<OpSystem>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String tag, Dictionary attributes, int line, int col) throws OpParseException {
        stack.push(tag);
        openTag(tag, attributes, line, col);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String tag, int line, int col) throws OpParseException {
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
    public void text(String text, int line, int col) throws OpParseException {
        text = text.trim();
        if (text.isEmpty()) {
            return;
        }
        
        if (mode.equals(Mode.ASSET)) {
            double score = getDouble(text, line, col);
            d_asset = new Asset(d_system, d_asset_type, d_asset_id, score);
            d_system.getComponentMap().put(d_asset.getID(),d_asset);
        }
        
    }

    /**
     * Handle an opening tag.
     * @param tag The tag that was opened.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    private void openTag(String tag, Dictionary attributes, int line, int col) throws OpParseException {
        if (isEqual(tag, TAG_OUTER)) {
            switch (mode) {
                case NONE:
                    mode = Mode.OUTER;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
                    break;
            }
        } else if (isEqual(tag, TAG_SYSTEM)) {
            switch (mode) {
                case OUTER:
                    d_sys_name = (String)attributes.get(ATR_NAME);
                    d_sys_type = (String)attributes.get(ATR_TYPE);
                    d_sys_id = (String)attributes.get(ATR_ID);
                    d_system = new OpSystem(d_sys_name, d_sys_type, d_sys_id);
                    d_entity_id = null;
                    d_asset_id = null;
                    mode = Mode.SYSTEM;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
                    break;
            }
        } else if (isEqual(tag, TAG_ENTITY)) {
            switch (mode) {
                case SYSTEM:
                case ENTITY:
                    d_entity_type = (String)attributes.get(ATR_TYPE);
                    d_entity_id = (String)attributes.get(ATR_ID);
                    d_entity = new Entity(d_system, d_entity_type, d_entity_id);
                    d_system.getComponentMap().put(d_entity.getID(),d_entity);
                    d_entity_stack.add(d_entity);
                    mode = Mode.ENTITY;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
                    break;
            }
        } else if (isEqual(tag, TAG_ASSET)) {
            switch (mode) {
                case SYSTEM:
                case ENTITY:
                    d_asset_type = (String)attributes.get(ATR_TYPE);
                    d_asset_id = (String)attributes.get(ATR_ID);
                    mode = Mode.ASSET;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
                    break;
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
    private void closeTag(String tag, int line, int col) throws OpParseException {
        Entity entity;
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
                    return;
            }
        } else if (isEqual(tag, TAG_SYSTEM)) {
            switch (mode) {
                case SYSTEM:
                    d_data.add(d_system);
                    mode = Mode.OUTER;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
        } else if (isEqual(tag, TAG_ENTITY)) {
            switch (mode) {
                case ENTITY:
                    entity = d_entity_stack.pop();
                    if (d_entity_stack.isEmpty()) {
                        d_entity = null;
                    } else {
                        d_entity = d_entity_stack.peek();
                    }
                    String prevTag = stack.peek();
                    if (isEqual(prevTag, TAG_ENTITY)) {
                        d_entity.getComponents().add(entity);
                        mode = Mode.ENTITY;
                    } else if (isEqual(prevTag, TAG_SYSTEM)) {
                        d_system.getComponents().add(entity);
                        mode = Mode.SYSTEM;
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
        } else if (isEqual(tag, TAG_ASSET)) {
            switch (mode) {
                case ASSET:
                    String prevTag = stack.peek();
                    if (isEqual(prevTag, TAG_ENTITY)) {
                        d_entity.getComponents().add(d_asset);
                        mode = Mode.ENTITY;
                    } else if (isEqual(prevTag, TAG_SYSTEM)) {
                        d_system.getComponents().add(d_asset);
                        mode = Mode.SYSTEM;
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
        } else {
            Warnings.unknownCloseTag(tag, line, col);
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
     * Return a double value from text.
     * @param text The text containing the double value.
     * @param line The line number that the data is on.
     * @param col The column number that the data is on.
     * @return A double obtained from the text.
     */
    private double getDouble(String text, int line, int col) throws OpParseException {
        try {
            double input, dub;
            input = Double.parseDouble(text.trim());
//            dub = Math.max(input, 0);
//            dub = Math.min(1, dub);
//            if (input != dub)
//                Warnings.unexpectedText(stack.peek(), line, col);
            return input;
        } catch (NumberFormatException nfe) {
            Errors.invalidInput(stack.peek(), "The score value was not a recognized number.", line, col);
        }
        return -1f;
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
            tag.equals(TAG_SYSTEM) ||
            tag.equals(TAG_ENTITY) || 
            tag.equals(TAG_ASSET)
        );
    }
}
