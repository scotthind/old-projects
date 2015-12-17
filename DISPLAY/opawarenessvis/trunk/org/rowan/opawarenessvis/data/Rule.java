package org.rowan.opawarenessvis.data;

/**
 * Interface Rule defines complex conditions and, if given a OpSystem, can 
 * determine if the conditions are met or not.
 * 
 * @author Shahid Akhter, Dan Urbano
 */
public interface Rule {
    
    /**
     * Determine if a rule meets all of its conditions.
     * @param OpSystem The overall system for this rule.
     * @return true if the rule meets its condition.
     */
    public boolean meetsCondition(OpSystem system);
}
