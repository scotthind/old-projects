package org.rowan.pathfinder.parser;

import java.util.Dictionary;

/**
 * Interface <code>OSMSubParser</code> defines the methods that need to be
 * implemented by any parser who will be leveraging OSMParser. Any implementing
 * class will have these methods called by OSMParser when parsing through a
 * document.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public interface OSMSubParser {
    /**
     * Called when an opening XML tag has been parsed. For example, the string
     * "name" will be the actual parameter of this method call while parsing the
     * following piece of XML: {@code 
     *     <name type=full>John Doe</name>
     * }
     * and the attributes actual parameter will be a Dictionary with one key,
     * "type" which is mapped to the value "full".
     * @param tag The name of the tag that starts an XML element.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    public void startElement(String tag, Dictionary attributes, int line, int col) throws PFParseException;

    /**
     * Called when a closing XML tag has been parsed. For example, the string
     * "name" will be the actual parameter of this method call while parsing the
     * following piece of XML: {@code 
     *     <name>John Doe</name>
     * }
     * @param tag The name of the tag that ends an XML element.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    public void endElement(String tag, int line, int col) throws PFParseException;

    /**
     * Called when the text surrounded by XML tags has been parsed. For example,
     * the string "John Doe" will be the actual parameter of this method call
     * while parsing the following piece of XML: {@code 
     *     <name>John Doe</name>
     * }
     * @param str The text surrounded by XML tags.
     * @param line The line number that the text is on.
     * @param col The column number that the text is on.
     */
    public void text(String str, int line, int col) throws PFParseException;
}
