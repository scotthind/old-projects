package org.rowan.opawarenessvis.data;
import java.util.List;

/**
 * Class AndRule defines complex conditions and, if given a OpSystem, can
 * determine if the conditions are all met or at least one is not met.
 * 
 * @author Dan Urbano
 */
public class AndRule implements EntityOrAssetRule {
    private final List<Rule> rules;
    private final String componentID;
    
    /**
     * Create a new AndRule with a list of rules.
     * @param rules The list of rules.
     * @param componentID The ID of the component used to check for this AndRule.
     */
    public AndRule(List<Rule> rules, String componentID) {
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
     * Determine whether or not this AndRule meets all of its conditions.
     * @param system The system to check the rule against.
     * @return True if all of the conditions are met, or false if at least
     *         one of the conditions is not met.
     */
    @Override
    public boolean meetsCondition(OpSystem system) {
        boolean doesMeet = true;
        for (Rule rule : rules) {
            doesMeet = doesMeet && rule.meetsCondition(system);
            if (!doesMeet) {
                return false;
            }
        }
        return true;
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
        for (int i = 1; i < rules.size(); i++) {
            s += " AND " + rules.get(i);
        }

        s += ")";

        return s;
    }
}
