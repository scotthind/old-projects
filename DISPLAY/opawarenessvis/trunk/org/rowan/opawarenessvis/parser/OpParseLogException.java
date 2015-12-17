package org.rowan.opawarenessvis.parser;

import java.util.List;

/**
 * Exception for parsing errors.
 * 
 * @author Dan Urbano.
 */
public class OpParseLogException extends Exception {
    private final List<String> log;
    
    public OpParseLogException (List<String> log) {
        this.log = log;
    }
    
    public List<String> getLog() {
        return log;
    }
}
