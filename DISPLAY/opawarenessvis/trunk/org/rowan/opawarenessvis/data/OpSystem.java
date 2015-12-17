package org.rowan.opawarenessvis.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.rowan.opawarenessvis.display.DetailWindow;
import org.rowan.opawarenessvis.display.Displayable;

/**
 * OpSystem represents an overall OpSystem to be monitored. A OpSystem
 * may have one or more <code>Component</code>s
 * 
 * @author Shahid Akhter
 */
public class OpSystem implements Displayable {

    private Map<String, Component> componentMap = new HashMap<String, Component>();
    private Set<Component> components = new HashSet<Component>();
    private String name;
    private String type;
    private String id;
    private String currentObjective;
    private Map<String, Rule> ruleMap = new HashMap<String, Rule>();

    public OpSystem(String name, String type, String id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }

    /**
     * Returns the component map of this OpSystem.
     * @return A map
     */
    public Map<String, Component> getComponentMap() {
        return componentMap;
    }
    
    /**
     * Returns the components directly under this system.
     * @return A map
     */
    public Set<Component> getComponents() {
        return components;
    }


    public boolean isReady(String objective) {
        return ruleMap.get(objective).meetsCondition(this);
    }

    /**
     * Attaches a Rule to a particular mission objective.
     * @param objective
     * @param rule
     */
    public void addRule(String objective, Rule rule) {
        currentObjective = objective;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDetailWindow() {
        DetailWindow.getInstance().setFields(name, type, id, isReady(currentObjective));
    }

    /**
     * Return the name of the system.
     */
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}

