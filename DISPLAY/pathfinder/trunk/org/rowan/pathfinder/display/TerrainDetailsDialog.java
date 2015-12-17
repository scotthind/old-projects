package org.rowan.pathfinder.display;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import org.rowan.pathfinder.pathfinder.TerrainType;

/**
 * This class is the dialog window for all parsing options.
 * 
 * @author Jon Schuff, Dan Urbano
 */
final class TerrainDetailsDialog extends JDialog {

    private static TerrainDetailsDialog instance = null;

    /**
     * Get the parse dialog instance.
     * @param frame The calling JFrame.
     * @param director The director instance.
     * @return The parse dialog instance.
     */
    public static JDialog getInstance(JFrame frame, Director director) {
        if (instance == null) {
            instance = new TerrainDetailsDialog(frame, director);
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    private TerrainDetailsDialog(final JFrame frame, final Director director) {
        // initialize local variables
        super(frame, "Terrain Details Window", false);
        Container panel = getContentPane();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        Dimension dim50by25 = new Dimension(50, 25);
        Dimension dim100by25 = new Dimension(100, 25);
        Dimension dim200by25 = new Dimension(200, 25);
        Dimension dim100by100 = new Dimension(100, 100);

        Dimension dim150by25 = new Dimension(150, 25);
        Dimension dim200by100 = new Dimension(200, 100);
        int vPad = 10;
        int hPad = 5;
        int leftSidePad = 20;
        Dimension dimPanel = new Dimension(350, 220);

        // initialize the labels
        JLabel typeLabel = new JLabel("Choose Type: ");
        JLabel descriptionLabel = new JLabel("Add Description: ");
        typeLabel.setPreferredSize(dim100by25);
        descriptionLabel.setPreferredSize(dim100by100);

        // initialize the text fields
        Object[] types = TerrainType.values();

        // init the combo boxes
        final JComboBox typeComboBox = new JComboBox(types);
        typeComboBox.setPreferredSize(dim200by25);

        final JTextArea descriptionField = new JTextArea();
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setPreferredSize(dim200by100);

        JButton acceptButton = new JButton("Accept");
        acceptButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                if (!director.doneCreatingTerrain((TerrainType) typeComboBox.getSelectedItem(), descriptionField.getText())) {
                    JOptionPane.showMessageDialog(frame,
                            "Can't create the terrain: it must be a convex shape.",
                            "Can't Create Terrain", JOptionPane.WARNING_MESSAGE);
                }
                director.startCreatingTerrain();
                releaseInstance();
            }
        });

        acceptButton.setPreferredSize(dim100by25);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                director.doneCreatingTerrain(null, null);
                director.startCreatingTerrain();
                releaseInstance();

            }
        });
        cancelButton.setPreferredSize(dim100by25);
        addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                director.doneCreatingTerrain(null, null);
                director.startCreatingTerrain();
                releaseInstance();

            }

            public void windowClosed(WindowEvent e) {


            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        });

        // add the components to the panel
        panel.add(typeLabel);
        panel.add(typeComboBox);
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(acceptButton);
        panel.add(cancelButton);

        // lay out the components in the panel
        putRowContraints(layout, panel, typeLabel, typeComboBox, panel, true, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, descriptionLabel, descriptionField, typeLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, acceptButton, cancelButton, descriptionLabel, false, vPad, hPad, leftSidePad + 55);

        // set up the dialog properties and return it
        setMinimumSize(dimPanel);
        setPreferredSize(dimPanel);
        setMaximumSize(dimPanel);
        setResizable(false);
        validate();
        setLocationRelativeTo(frame);
    }

    public void putRowContraints(SpringLayout layout, Container panel, Component leftComponent,
            Component rightComponent, Component northComponent, boolean useNorth,
            int vPad, int hPad, int leftSidePad) {
        String edge = useNorth ? SpringLayout.NORTH : SpringLayout.SOUTH;
        //vertical constraints
        layout.putConstraint(SpringLayout.NORTH, leftComponent, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, rightComponent, vPad, edge, northComponent);
        //horizontal constraints
        layout.putConstraint(SpringLayout.WEST, leftComponent, leftSidePad, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.WEST, rightComponent, hPad, SpringLayout.EAST, leftComponent);
    }
}
