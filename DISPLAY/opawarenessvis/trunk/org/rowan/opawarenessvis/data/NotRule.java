package org.rowan.opawarenessvis.data;

import java.util.List;

/**
 * Class NotRule defines an inverted condition and, if given a OpSystem, can
 * determine if the inverted condition is met.
 * 
 * @author Dan Urbano
 */
public class NotRule implements Rule {

    private final List<Rule> rule;

    /**
     * Create a new NotRule with a given rule.
     * @param rule The given rule should be the first element of the list.
     *             Any other elements of the list will be ignored.
     */
    public NotRule(List<Rule> rule) {
        this.rule = rule;
    }

    /**
     * Determine whether or not this NotRule meets its condition.
     * @param system The system to check the rule against.
     * @return True if the underlying rule is false, and false if the underlying
     *         rule is true.
     */
    @Override
    public boolean meetsCondition(OpSystem system) {
        return (!rule.get(0).meetsCondition(system));
    }

    public String toString() {
        String s = "(NOT " + rule.get(0);

        s += ")";

        return s;
    }
}
