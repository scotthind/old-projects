package org.rowan.opawarenessvis.display;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class displays a preview image of an entity, asset, or system
 * @author Shahid Akhter
 */
public class PreviewLabel extends JLabel {

    private static PreviewLabel instance = null;
    private JPanel panel;
    private Map<String, String> imageMap;

    private PreviewLabel(Map<String, String> imageMap, JPanel panel) {
        this.imageMap = imageMap;
        this.panel = panel;
        ImageIcon icon =MainGUI.createImageIcon(imageMap.get("Unidentified"), null, MainGUI.customizationPanelWidth, -1,
                Color.WHITE);
        setIcon(icon);
        setHorizontalAlignment(CENTER);

    }

    /**
     * Creates an instance of a PreviewLabel
     */
    public static void init(Map<String, String> imageMap, JPanel panel) {
        if (instance == null) {
            instance = new PreviewLabel(imageMap,panel );
        }
    }

    /**
     * Get the PreviewLabel instance
     * @return A PreviewLabel
     */
    public static PreviewLabel getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    /**
     * Sets the icon of the preview label
     * @param type
     */
    public void updatePanel(String type)
    {
        ImageIcon icon = MainGUI.createImageIcon(imageMap.get(type), null, MainGUI.customizationPanelWidth, -1, Color.WHITE);
        if(icon == null)
            icon = MainGUI.createImageIcon("Unidentified", null, MainGUI.customizationPanelWidth, -1, Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, icon.getIconHeight() + 10));
        setIcon(icon);
    }
}
