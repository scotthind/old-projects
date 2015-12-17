package org.rowan.pathfinder.parser;

/**
 * Class <code>Errors</code> is a collection of static methods for reporting
 * specific parsing errors.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class Errors {
    /**
     * Report the error: an opening tag was found in an unexpected location.
     * @param badTag The offending tag.
     * @param line line # of the error.
     * @param col column # of the error.
     */
    static void unexpectedOpen(String badTag, int line, int col) throws PFParseException {
        XMLParser.error("The tag <" + badTag + "> was found in an unexpected location.", line, col);
        throw new PFParseException();
    }
    
    /**
     * Report the error: the text inside a tag contained an error.
     * @param tag The tag surrounding the error.
     * @param msg A custom message (may be null) to append to the error.
     * @param line line # of the error.
     * @param col column # of the error.
     */
    static void invalidInput(String tag, String msg, int line, int col) throws PFParseException {
        XMLParser.error("The text inside <" + tag + "> contained an error. " + 
                (msg == null? "" : msg), line, col);
        throw new PFParseException();
    }
    
    /**
     * Report the error: the parser expected all the tags to be closed, but
     * it reached another opening tag.
     * @param tag The offending opening tag.
     * @param line line # of the error.
     * @param col column # of the error.
     */
    static void expectedToHaveClosedAll(String tag, int line, int col) throws PFParseException {
        XMLParser.error("The parser reached </" + tag + ">, which it expected to be the "
                + "last closing tag, but there were other tags still opened.", line, col);
        throw new PFParseException();
    }
    
    /**
     * Report the error: something unexpected happened after reading an ending tag.
     * @param tag The ending (or closing) tag.
     * @param line line # of the error.
     * @param col column # of the error.
     */
    static void unknownOnClose(String tag, int line, int col) throws PFParseException {
        XMLParser.error("The parser has performed an unexpected operation after reading "
                + "a </" + tag + "> tag.", line, col);
        throw new PFParseException();
    }
    
    /**
     * Report the error: a tag was closed but it didn't seem to be open.
     * @param badTag The offending tag.
     * @param line line # of the error.
     * @param col column # of the error.
     */
    static void invalidClose(String badTag, int line, int col) throws PFParseException {
        XMLParser.error("The parser reached </" + badTag + ">, it could not "
                + "find a previously opened <" + badTag + ">.", line, col);
        throw new PFParseException();
    }
    
    /**
     * Report the error: expectedTag was supposed to be closed, but instead
     * badTag was closed.
     * @param badTag the tag which was closed.
     * @param expectedTag the tag which should have been closed.
     * @param line line # of the error.
     * @param col column # of the error.
     */
    static void incorrectClose(String badTag, String expectedTag, int line, int col) throws PFParseException {
        XMLParser.error("The parser reached </" + badTag + "> but it expected "
                + "the tag </" + expectedTag + "> instead.", line, col);
        throw new PFParseException();
    }
    
    /**
     * Report the error: there was text which was an invalid format.
     * @param text The invalid text.
     * @param msg A custom message (may be null) to append to the error.
     * @param line The line # of the error.
     * @param col The column # of the error.
     */
    static void invalidFormat(String text, String msg, int line, int col) throws PFParseException {
        XMLParser.error("The parser encountered the text, \"" + text + "\" " +
                "which is in an invalid format. " + (msg == null? "" : msg), line, col);
        throw new PFParseException();
    }
}