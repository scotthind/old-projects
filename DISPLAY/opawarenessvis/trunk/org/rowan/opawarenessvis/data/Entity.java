package org.rowan.opawarenessvis.data;

import java.util.HashSet;
import java.util.Set;
import org.rowan.opawarenessvis.display.DetailWindow;

/**
 * An <code>Entity</code> represents either an overall system such as a car
 * or a subsystem such as a car's engine.
 * 
 * @author Shahid Akhter
 */
public class Entity extends Component {

    private Set<Component> components = new HashSet<Component>();

    public Entity(OpSystem system, String type, String id) {
        super(system, type, id);
    }

    /**
     * Adds a component to the Set of components in an entity.
     * @param c Either an Entity or an Asset
     */
    public void addToComponents(Component c) {
        components.add(c);
    }

    /**
     * Returns the set of components.
     * @return A set of components.
     */
    public Set<Component> getComponents() {
        return components;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDetailWindow() {
        DetailWindow.getInstance().setFields(system.getName(), type, id, isReady(currentObjective));
    }
    
    @Override
    public String getName() {
        return system.getName();
    }
    
    @Override
    public String toString() {
        return "Entity (" + getID() + ")";
    }
}
