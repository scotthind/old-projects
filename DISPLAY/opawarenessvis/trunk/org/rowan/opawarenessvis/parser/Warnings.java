package org.rowan.opawarenessvis.parser;

/**
 * Class Warnings is a collection of static methods for reporting
 * parsing warnings.
 * 
 * @author Dan Urbano
 */
public class Warnings {
    /**
     * Report the warning: A tag was opened but it is unrecognized.
     * @param badTag The unrecognized opening tag.
     * @param line line # of the warning.
     * @param col column # of the warning.
     */
    static void unknownOpenTag(String badTag, int line, int col) {
        XMLParser.warning("The tag <" + badTag + "> is unrecognized, and is being ignored.", line, col);
    }
    
    /**
     * Report the warning: A tag was closed but it is unrecognized.
     * @param badTag The unrecognized closing tag.
     * @param line line # of the warning.
     * @param col column # of the warning.
     */
    static void unknownCloseTag(String badTag, int line, int col) {
        XMLParser.warning("The tag </" + badTag + "> is unrecognized, and is being ignored.", line, col);
    }
    
    /**
     * Report the warning: expectedTag was supposed to be closed, but badTag
     * was closed instead.
     * @param badTag The tag which was closed.
     * @param expectedTag The tag which should have been closed.
     * @param line line # of the warning.
     * @param col column # of the warning.
     */
    static void unexpectedClose(String badTag, String expectedTag, int line, int col) {
        XMLParser.warning("The parser reached </" + badTag + "> but it expected "
                + "a <" + expectedTag + "> instead. Since information is "
                + "missing, one or more expected objects may not be created.", line, col);
    }
    
    /**
     * Report the warning: expectedTag was supposed to be opened tag before
     * badTag, but badTag was opened instead.
     * @param badTag The tag which was opened.
     * @param expectedTag The tag which should have been opened first.
     * @param line line # of the warning.
     * @param col column # of the warning.
     */
    static void unexpectedOpen(String badTag, String expectedTag, int line, int col) {
        XMLParser.warning("The parser reached <" + badTag + "> but it expected "
                + "a <" + expectedTag + "> before reaching <" + badTag + ">. "
                + "Since information is missing, one or more expected objects "
                + "may not be created.", line, col);
    }
    
    /**
     * Report the warning: there was unexpected text surrounded by a tag.
     * @param tag The tag surrounding the unexpected text.
     * @param line line # of the warning.
     * @param col column # of the warning.
     */
    static void unexpectedText(String tag, int line, int col) {
        XMLParser.warning("The parser encounted text between a <" + tag + "> and </" +
                tag + "> when there was not supposed to be. Ignoring text.", line, col);
    }
    
    /**
     * Report the warning: there was text which was an invalid format.
     * @param text The invalid text.
     * @param msg A custom message (may be null) to append to the error.
     * @param line The line # of the warning.
     * @param col The column # of the warning.
     */
    static void invalidFormat(String text, String msg, int line, int col) {
        XMLParser.warning("The parser encountered the text, \"" + text + "\" " +
                "which is in an invalid format. " + (msg == null? "" : msg), line, col);
    }

    /**
     * Report a warning with a given message.
     * @param msg The warning message.
     * @param line The line # of the warning.
     * @param col The column # of the warning.
     */
    static void general(String msg, int line, int col) {
        XMLParser.warning(msg, line, col);
    }
}