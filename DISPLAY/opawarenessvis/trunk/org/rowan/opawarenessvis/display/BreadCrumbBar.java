package org.rowan.opawarenessvis.display;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class is the detail window for an entity or an asset.
 * @author Shahid Akhter
 */
public class BreadCrumbBar extends JPanel {

    private static BreadCrumbBar instance = null;
    private List<JLabel> labels = new ArrayList<JLabel>();
    private List<Displayable> displayables = new ArrayList<Displayable>();
    private Map<String, String> imageMap;
    private final MainJOGL mainJOGL;

    private BreadCrumbBar(MainJOGL mainJOGL, Map<String, String> imageMap) {
        this.imageMap = imageMap;
        this.mainJOGL = mainJOGL;

        setLayout(new FlowLayout(FlowLayout.LEADING));
    }

    /**
     * Creates an instance of a BreadCrumbBar
     * @param labels The list of labels
     * @param typeLabel The Type Label of the Detail Window
     */
    public static void init(final MainJOGL mainJOGL, final Map<String, String> imageMap) {
        if (instance == null) {
            instance = new BreadCrumbBar(mainJOGL, imageMap);
        }
    }

    /**
     * Get the BreadCrumbBar instance
     * @return A BreadCrumbBar
     */
    public static BreadCrumbBar getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }
    
    /**
     * Clear all bread crumbs from the bar.
     */
    public void clearCrumbs() {
        labels.clear();
        displayables.clear();
        removeAll();
    }

    /**
     * Adds a Displayable label and icon to the Bread Crumb Bar
     * @param d A Displayable to be added to the Bread Crumb Bar
     */
    public void addCrumb(final Displayable d) {
        if (!displayables.isEmpty() && displayables.get(displayables.size()-1).equals(d)) {
            return;
        }
        String iconPath = imageMap.get(d.getType());
        JLabel label = new JLabel();
        if (iconPath == null) {
            iconPath = imageMap.get("Unidentified");
        }
        label.setText(d.getID());
        label.setIcon(MainGUI.createImageIcon(iconPath, d.getID(), -1, 30, Color.WHITE));
        DetailWindow.getInstance().setFields(d.getName(), d.getType(), d.getID(), true);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = labels.indexOf((JLabel) e.getComponent());
                for (int i = labels.size() - 1; i > index; i--) {
                    BreadCrumbBar.getInstance().remove((JLabel) labels.get(i));
                    labels.remove(i);
                    displayables.remove(i);
                }
                mainJOGL.setDisplayable(d, true);
                DetailWindow.getInstance().setFields(d.getName(), d.getType(), d.getID(), true);
                MainGUI.frame.repaint();
            }
        });
        
        labels.add(label);
        displayables.add(d);
        add(label);
    }
    
    /**
     * Get the last added Displayable.
     */
    public Displayable getLastDisplayable() {
        return displayables.get(displayables.size()-1);
    }
    
    /**
     * Get the number of labels/displayables.
     */
    public int getTheSize() {
        return displayables.size();
    }
    
    /**
     * Remove the last crumb.
     */
    public void removeLastCrumb() {
        displayables.remove(displayables.size()-1);
        JLabel x = labels.remove(labels.size()-1);
        remove(x);
    }
}
