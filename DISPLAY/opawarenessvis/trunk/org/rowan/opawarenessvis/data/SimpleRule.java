package org.rowan.opawarenessvis.data;

/**
 * Class SimpleRule defines a basic condition, and, if given a system, can
 * determine whether or not the condition is met.
 * 
 * @author Dan Urbano
 */
public class SimpleRule implements EntityOrAssetRule {
    private final String componentID;
    private final double threshold;
    private final Condition condition;
    
    /**
     * Create a new SimpleRule for a given component id, a condition, and a
     * threshold.
     * @param componentID The ID of the component used to check for this
     *                    SimpleRule.
     * @param condition   The condition corresponding to the threshold.
     * @param threshold   The threshold for which the condition must meet.
     */
    public SimpleRule(String componentID, Condition condition, double threshold) {
        this.componentID = componentID;
        this.condition = condition;
        this.threshold = threshold;
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
     * Determine whether or not this SimpleRule meets its condition.
     * @param system The system to check the rule against.
     * @return True or false, depending on whether this SimpleRule's condition is met.
     */
    @Override
    public boolean meetsCondition(OpSystem system) {
        Component component = system.getComponentMap().get(componentID);
        if (component != null && component instanceof Asset) {
            Asset asset = (Asset)component;
            double score = asset.getScore();
            return satisfies(score);
        } else {
            //TODO throw an exception
            return false;
        }
    }

    /**
     * Given a mission objective and a system, find the corresponding component
     * to this rule, and set add this rule to that component.
     * @param objective The mission objective.
     * @param system The system containing the component for this rule.
     */
    @Override
    public void addToComponent(String objective, OpSystem system) {
        Component component = system.getComponentMap().get(componentID);
        if (component != null && component instanceof Asset) {
            component.addRule(objective, this);
        } else {
            //TODO throw an exception
            return;
        }
    }

    /**
     * Does a given score satisfy the condition and threshold?
     * @param score The score to check.
     * @return True or false, depending on whether the score meets this
     *         SimpleRule's condition against its threshold.
     */
    private boolean satisfies(double score) {
        switch (condition) {
            case AtLeast:
            case GreaterThanOrEqualTo:
                return (score >= threshold);
            case AtMost:
            case LessThanOrEqualTo:
                return (score <= threshold);
            case EqualTo:
                return (score == threshold);
            case LessThan:
                return (score < threshold);
            case GreaterThan:
                default:
                return (score > threshold);
        }
    }
    
    public String toString()
    {
        return componentID + " " + condition + " " + threshold;
    }
}
