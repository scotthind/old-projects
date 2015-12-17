package org.rowan.opawarenessvis.display;


import javax.swing.JLabel;

/**
 * This class is the detail window for an entity or an asset.
 * @author Shahid Akhter
 */
public class DetailWindow {

    private static DetailWindow instance = null;
    private JLabel nameLabel;
    private JLabel typeLabel;
    private JLabel idLabel;
    private JLabel readinessLabel;

    private DetailWindow(final JLabel nameLabel, final JLabel typeLabel,
            final JLabel idLabel, final JLabel isReadyLabel) {
        this.nameLabel = nameLabel;
        this.typeLabel = typeLabel;
        this.idLabel = idLabel;
        this.readinessLabel = isReadyLabel;
    }

    /**
     * Creates an instance of a DetailWindow
     * @param nameLabel The Name Label of the Detail Window
     * @param typeLabel The Type Label of the Detail Window
     * @param idLabel The ID Label of the Detail Window
     * @param raedinessLabel The Readiness Label of the Detail Window
     */
    public static void init(final JLabel nameLabel, final JLabel typeLabel,
            final JLabel idLabel, final JLabel isReadyLabel) {
        if (instance == null) {
            instance = new DetailWindow(nameLabel, typeLabel, idLabel, isReadyLabel);
        }
    }

    /**
     * Get the DetailWindow instance
     * @return A DetailWindow
     */
    public static DetailWindow getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    /**
     *
     * @param name The name of the Component
     * @param type The type of Component
     * @param id The ID of the Component
     * @param isReady True if the component is ready for the mission
     */
    public void setFields(String name, String type, String id, boolean isReady) {
        nameLabel.setText(name);
        typeLabel.setText(type);
        idLabel.setText(id);
        if (isReady) {
            readinessLabel.setText("Ready");
        } else {
            readinessLabel.setText("Not ready");
        }

    }
}
