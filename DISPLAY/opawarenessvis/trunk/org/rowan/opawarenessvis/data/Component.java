package org.rowan.opawarenessvis.data;

import java.util.HashMap;
import java.util.Map;
import org.rowan.opawarenessvis.display.Displayable;

/**
 * Class Component is a parent class of an Entity and Asset.
 * 
 * @author Shahid Akhter
 */
public abstract class Component implements Displayable, Comparable<Component> {
    public static String SORT_OBJECTIVE = "";
    protected Map<String, Rule> ruleMap = new HashMap<String, Rule>();
    protected OpSystem system;
    protected String type;
    protected String id;
    protected String currentObjective;

    public Component(OpSystem system, String type, String id) {
        this.system = system;
        this.type = type;
        this.id = id;
    }

    /**
     * Returns if this component is ready based on the rules of this OpSystem.
     */
    public boolean isReady(String objective) {
        if (!this.ruleMap.containsKey(objective)) {
            return false;
        }
        currentObjective = objective;
        return this.ruleMap.get(objective).meetsCondition(system);
    }

    /**
     * Attaches a Rule to a particular mission objective.
     * @param objective
     * @param rule
     */
    public void addRule(String objective, Rule rule) {
        ruleMap.put(objective, rule);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getID() {
        return id;
    }
    
    @Override
    public int compareTo(Component other) {
        int returnVal = 0;
        if ((isReady(SORT_OBJECTIVE) &&  other.isReady(SORT_OBJECTIVE)) ||
           (!isReady(SORT_OBJECTIVE) && !other.isReady(SORT_OBJECTIVE))) {
            // they have the same readyness, sort on alphabetical order on the ID
            returnVal = this.getID().compareTo(other.getID());
        } else if (isReady(SORT_OBJECTIVE) && !other.isReady(SORT_OBJECTIVE)) {
            returnVal = -1;
        } else if (!isReady(SORT_OBJECTIVE) && other.isReady(SORT_OBJECTIVE)) {
            returnVal = 1;
        }
        return returnVal;
    }
}
