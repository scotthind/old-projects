package org.rowan.pathfinder.parser;

/**
 * Interface <code>XMLSubParser</code> defines the methods that need to be
 * implemented by any parser who will be leveraging XMLParser. Any implementing
 * class will have these methods called by XMLParser when parsing through a
 * document.
 * 
 * @author Dan Urbano, John Schuff
 * @version 1.0
 * @since 1.0
 */
public interface XMLSubParser {
    /**
     * Called when an opening XML tag has been parsed. For example, the string
     * "name" will be the actual parameter of this method call while parsing the
     * following piece of XML: {@code 
     *     <name>John Doe</name>
     * }
     * @param tag The name of the tag that starts an XML element.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    public void startElement(String tag, int line, int col) throws PFParseException;

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
