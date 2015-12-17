package org.rowan.pathfinder.parser;

import java.util.List;

/**
 * Exception for parsing errors.
 * 
 * @author Dan Urbano.
 */
public class PFParseLogException extends Exception {
    private final List<String> log;
    
    public PFParseLogException (List<String> log) {
        this.log = log;
    }
    
    public List<String> getLog() {
        return log;
    }
}
