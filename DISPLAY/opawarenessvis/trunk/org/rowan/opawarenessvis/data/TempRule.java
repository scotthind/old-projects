package org.rowan.opawarenessvis.data;

/**
 * Class TempRule defines an invalid Rule that is a placeholder for actual
 * Rules.
 * 
 * @author Dan Urbano
 */
public class TempRule implements Rule {
    private final String componentID;
    
    /**
     * Create a new TempRule with the given component ID.
     * @param componentID The component id of the actual rule that replaces this TempRule.
     */
    public TempRule(String componentID) {
        this.componentID = componentID;
    }
    
    /**
     * Return the component id.
     * @return The component id of the actual rule that replaces this TempRule.
     */
    public String getComponentID() {
        return componentID;
    }
    
    /**
     * As this rule is simply a placeholder, this method returns false.
     * @param system parameter is ignored.
     * @return false.
     */
    @Override
    public boolean meetsCondition(OpSystem system) {
        //TODO throw an exception
        return false;
    }
}
