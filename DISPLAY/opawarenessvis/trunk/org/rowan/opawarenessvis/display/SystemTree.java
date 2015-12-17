package org.rowan.opawarenessvis.display;

import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import org.rowan.opawarenessvis.data.Asset;
import org.rowan.opawarenessvis.data.Component;
import org.rowan.opawarenessvis.data.Entity;
import org.rowan.opawarenessvis.data.OpSystem;

/**
 * This class is the tree which will show the overall System Heirarchy
 * @author Shahid Akhter
 */
public class SystemTree extends JTree {

    private static SystemTree instance = null;
    private Map<String, OpSystem> systems;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;
    private final MainJOGL mainJOGL;
    private OpSystem currentSystem = null;

    private SystemTree(Map<String, OpSystem> systems, DefaultMutableTreeNode root, MainJOGL mainJOGL) {
        super(root);
        this.root = root;
        this.systems = systems;
        this.model = (DefaultTreeModel) getModel();
        this.mainJOGL = mainJOGL;
        setEnabled(false);

    }

    /**
     * Creates an instance of a SystemTree
     */
    public static void init(Map<String, OpSystem> systems, DefaultMutableTreeNode root,
            MainJOGL mainJOGL) {
        if (instance == null) {
            instance = new SystemTree(systems, root, mainJOGL);
        }
    }

    /**
     * Get the SystemTree instance
     * @return A SystemTree
     */
    public static SystemTree getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    /**
     * Updates the tree with the System Heirarchy
     * @param system An OpSystem to be displayed in the SystemTree
     */
    public void updateTree(String system) {
        OpSystem sys = systems.get(system);
        MutableTreeNode node = new DefaultMutableTreeNode(sys.getName());
        model.insertNodeInto(node, root, root.getChildCount());
        for (Component c : sys.getComponents()) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(c.getID());
            if (c instanceof Asset) {
                model.insertNodeInto(newNode, node, node.getChildCount());
            } else {
                model.insertNodeInto(findAllChildren((Entity) c), node, 0);
            }
        }
    }

    /**
     * Recursively finds all children of an Entity
     * @param entity A component with multiple children
     * @return A DefaultMutableTreeNode containing all of its children
     */
    private DefaultMutableTreeNode findAllChildren(Entity entity) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(entity.getID());
        for (Component c : entity.getComponents()) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(c.getID());
            if (c instanceof Asset) {
                model.insertNodeInto(newNode, node, node.getChildCount());
            } else {
                model.insertNodeInto(findAllChildren((Entity) c), node, 0);
            }

        }
        return node;

    }

    /**
     * Clears the JTree
     */
    public void clearNodes() {
        while (!model.isLeaf(root)) {
            model.removeNodeFromParent((MutableTreeNode) model.getChild(root, 0));
        }
    }

    /**
     * Sets the current system
     * @param system
     */
    public void setCurrentSystem(OpSystem system) {
        currentSystem = system;
    }

    /**
     * Returns the current system being viewed.
     * @return An OpSystem which is the current system.
     */
    public OpSystem getCurrentSystem() {
        return currentSystem;
    }
}
