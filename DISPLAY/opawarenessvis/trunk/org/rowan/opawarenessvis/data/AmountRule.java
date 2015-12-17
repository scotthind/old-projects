package org.rowan.opawarenessvis.data;

import java.util.Set;

/**
 * Class Amount defines a condition dependant on a specific number of components
 * of a specific type, and, if given a system, can determine whether or not the
 * condition is met.
 * 
 * @author Dan Urbano
 */
public class AmountRule implements Rule {

    private final String parentComponentID;
    private final String componentType;
    private final int threshold;
    private final Condition condition;

    /**
     * Create a new AmountRule for a given component type, a condition, and a
     * threshold.
     * @param parentComponentID The ID of the component for which only its
     *                          subcomponents of type componentType are
     *                          to be considered for the condition.
     * @param componentType The type of component corresponding to the condition.
     * @param condition     The condition corresponding to the threshold.
     * @param threshold     The threshold for which the condition must meet.
     */
    public AmountRule(String parentComponentID, String componentType, Condition condition, int threshold) {
        this.parentComponentID = parentComponentID;
        this.componentType = componentType;
        this.condition = condition;
        this.threshold = threshold;
    }

    /**
     * Determine whether or not this AmountRule meets its condition.
     * @param system The system to check the rule against.
     * @return True or false, depending on whether this AmountRule's condition is met.
     */
    @Override
    public boolean meetsCondition(OpSystem system) {
        int amount = 0;
        Component parentComponent = system.getComponentMap().get(parentComponentID);
        if (parentComponent != null && parentComponent instanceof Entity) {
            Entity parentEntity = (Entity) parentComponent;
            Set<Component> components = parentEntity.getComponents();
            for (Component component : components) {
                if (component.getType().equalsIgnoreCase(componentType)) {
                    amount++;
                }
            }
            return satisfies(amount);
        } else {
            //TODO throw an exception
            return false;
        }
    }

    /**
     * Does a given amount satisfy the condition and threshold?
     * @param amount The amount to check.
     * @return True or false, depending on whether the amount meets this
     *         AmountRule's condition against its threshold.
     */
    private boolean satisfies(int amount) {
        switch (condition) {
            case AtLeast:
            case GreaterThanOrEqualTo:
                return (amount >= threshold);
            case AtMost:
            case LessThanOrEqualTo:
                return (amount <= threshold);
            case EqualTo:
                return (amount == threshold);
            case LessThan:
                return (amount < threshold);
            case GreaterThan:
            default:
                return (amount > threshold);
        }
    }

    public String toString() {
        return condition + " " + threshold + " of type " + componentType;
    }
}
