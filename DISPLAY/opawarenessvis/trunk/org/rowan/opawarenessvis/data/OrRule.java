package org.rowan.opawarenessvis.data;

import java.util.List;

/**
 * Class OrRule defines complex conditions and, if given a OpSystem, can
 * determine if at least one of the conditions are all met or none of them are.
 * 
 * @author Dan Urbano
 */
public class OrRule implements EntityOrAssetRule {
    private final String componentID;
    private final List<Rule> rules;
    
    /**
     * Create a new OrRule with a list of rules.
     * @param rules The list of rules.
     * @param componentID The ID of the component used to check for this OrRule.
     */
    public OrRule(List<Rule> rules, String componentID) {
        this.componentID = componentID;
        this.rules = rules;
    }
    
    /**
     * Return the component id of the Entity or Asset that this rule should
     * belong to.
     * @return The component id.
     */
    @Override
    public String getComponentId() {
        return componentID;
    }
    
    /**
     * Determine whether or not this OrRule meets all of its conditions.
     * @param system The system to check the rule against.
     * @return False if none of the conditions are met, or true if at least
     *         one of the conditions is met.
     */
    @Override
    public boolean meetsCondition(OpSystem system) {
        boolean doesMeet = false;
        for (Rule rule : rules) {
            doesMeet = doesMeet || rule.meetsCondition(system);
            if (doesMeet) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Given a mission objective and a system, find the corresponding component
     * to this rule, and set add this rule to that component.
     * @param objective The mission objective.
     * @param system The system containing the component for this rule.
     */
    @Override
    public void addToComponent(String objective, OpSystem system) {
        if (componentID == null) {
            return;
        } else if (componentID.equals(system.getID())) {
            system.addRule(objective, this);
        } else {
            Component component = system.getComponentMap().get(componentID);
            component.addRule(objective, this);
        }
    }

    @Override
    public String toString() {
        String s = "(" + rules.get(0);
        for (int i=1; i<rules.size(); i++) {
            s += " OR " + rules.get(i);
        }

        s += ")";

        return s;
    }
}
