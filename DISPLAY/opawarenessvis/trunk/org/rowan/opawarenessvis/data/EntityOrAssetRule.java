package org.rowan.opawarenessvis.data;

/**
 * Interface EntityOrAssetRule defines the rules that can be linked to an
 * Entity or Asset.
 * 
 * @author Dan Urbano
 */
public interface EntityOrAssetRule extends Rule {
    /**
     * Return the component id of the Entity or Asset that this rule should
     * belong to.
     * @return The component id.
     */
    public String getComponentId();
    
    /**
     * Given a mission objective and a system, find the corresponding component
     * to this rule, and add this rule to that component.
     * @param objective The mission objective.
     * @param system The system containing the component for this rule.
     */
    public void addToComponent(String objective, OpSystem system);
}
