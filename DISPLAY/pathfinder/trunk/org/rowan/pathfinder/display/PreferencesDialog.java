package org.rowan.pathfinder.display;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import org.rowan.pathfinder.pathfinder.TerrainType;

/**
 * This class is the dialog window for all parsing options.
 * 
 * @author Jon Schuff, Dan Urbano
 */
final class PreferencesDialog extends JDialog {

    private static PreferencesDialog instance = null;
    private final Director director;
    private final MainGUI gui;

    /**
     * Get the parse dialog instance.
     * @param frame The calling JFrame.
     * @param director The director instance.
     * @return The parse dialog instance.
     */
    public static JDialog getInstance(final MainGUI gui, Director director) {
        if (instance == null) {
            instance = new PreferencesDialog(gui, director);
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    private PreferencesDialog(final MainGUI gui, final Director director) {

        // initialize local variables
        super(gui.getFrame(), "Preferences", false);
        this.gui = gui;
        this.director = director;
        Container panel = getContentPane();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        Dimension dim55by25 = new Dimension(55, 25);
        Dimension dim75by25 = new Dimension(75, 25);
        Dimension dim100by25 = new Dimension(100, 25);
        Dimension dim125by25 = new Dimension(125, 25);
        Dimension dim400by25 = new Dimension(400, 25);
        int vPad = 10;
        int hPad = 5;
        int leftSidePad = 20;
        Dimension dimPanel = new Dimension(680, 250);

        // initialize the labels
        JLabel tFilePathLabel = new JLabel("Terrain File Directory:");
        JLabel eFilePathLabel = new JLabel("Event File Directory:");
        JLabel dbPathLabel = new JLabel("Database Address:");
        JLabel cutoff1Label = new JLabel("Event Begin Decay:");
        JLabel cutoff2Label = new JLabel("Event End Decay:");
        JLabel years1 = new JLabel("years ago,");
        JLabel months1 = new JLabel("months ago,");
        JLabel days1 = new JLabel("days ago.");
        JLabel years2 = new JLabel("years ago,");
        JLabel months2 = new JLabel("months ago,");
        JLabel days2 = new JLabel("days ago.");
        tFilePathLabel.setPreferredSize(dim125by25);
        eFilePathLabel.setPreferredSize(dim125by25);
        dbPathLabel.setPreferredSize(dim125by25);
        cutoff1Label.setPreferredSize(dim125by25);
        cutoff2Label.setPreferredSize(dim125by25);
        years1.setPreferredSize(dim75by25);
        months1.setPreferredSize(dim75by25);
        days1.setPreferredSize(dim75by25);
        years2.setPreferredSize(dim75by25);
        months2.setPreferredSize(dim75by25);
        days2.setPreferredSize(dim75by25);
        
        //intialize text fields
        final JTextField fieldYears1 = new JTextField("" + Director.decay1Years);
        final JTextField fieldMonths1 = new JTextField("" + Director.decay1Months);
        final JTextField fieldDays1 = new JTextField("" + Director.decay1Days);
        final JTextField fieldYears2 = new JTextField("" + Director.decay2Years);
        final JTextField fieldMonths2 = new JTextField("" + Director.decay2Months);
        final JTextField fieldDays2 = new JTextField("" + Director.decay2Days);
        fieldYears1.setPreferredSize(dim55by25);
        fieldMonths1.setPreferredSize(dim55by25);
        fieldDays1.setPreferredSize(dim55by25);
        fieldYears2.setPreferredSize(dim55by25);
        fieldMonths2.setPreferredSize(dim55by25);
        fieldDays2.setPreferredSize(dim55by25);

        // initialize the text fields
        Object[] types = TerrainType.values();

        // init the combo boxes

        final JTextField terrainSavePath = new JTextField(gui.getTerrainSaveDirectory());
        terrainSavePath.setPreferredSize(dim400by25);
        terrainSavePath.setEditable(false);

        final JFileChooser terrainSavePathChooser = new JFileChooser();
        terrainSavePathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        final JButton browseTerrainPathButton = new JButton("Browse...");
        browseTerrainPathButton.setPreferredSize(new Dimension(95, 25));
        browseTerrainPathButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //GENERATE 
                terrainSavePathChooser.showOpenDialog(instance.getFrame());
                if(terrainSavePathChooser.getSelectedFile() != null)
                    terrainSavePath.setText(terrainSavePathChooser.getSelectedFile().getAbsolutePath());
            }
        });

        final JTextField eventSavePath = new JTextField(gui.getEventSaveDirectory());
        eventSavePath.setPreferredSize(dim400by25);
        eventSavePath.setEditable(false);
        
        final JFileChooser eventSavePathChooser = new JFileChooser();
        eventSavePathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        final JButton browseEventPathButton = new JButton("Browse...");
        browseEventPathButton.setPreferredSize(new Dimension(95, 25));
        browseEventPathButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //GENERATE 
                eventSavePathChooser.showOpenDialog(instance.getFrame());
                eventSavePath.setText(eventSavePathChooser.getSelectedFile().getAbsolutePath());
            }
        
        });
        
        final JTextField dbTextField = new JTextField(gui.getDatabaseIP());
        dbTextField.setPreferredSize(dim125by25);
        final JButton saveDbButton = new JButton("Save");
        saveDbButton.setPreferredSize(new Dimension(85, 25));
        saveDbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dbTextField.getText().trim().length() > 7)
                    gui.setDatabaseIP(dbTextField.getText().trim());
                else
                    JOptionPane.showMessageDialog(gui.getFrame(), "Invalid IP address entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton acceptButton = new JButton("Accept");
        acceptButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(terrainSavePath.getText() != null)
                    gui.setTerrainSaveDirectory(terrainSavePath.getText());
                if(eventSavePath.getText() != null)
                    gui.setEventSaveDirectory(eventSavePath.getText());
                
                try {
                    Director.decay1Years = Integer.parseInt(fieldYears1.getText().trim());
                } catch (NumberFormatException ex) {
                    // ignore
                }
                try {
                    Director.decay1Months = Integer.parseInt(fieldMonths1.getText().trim());
                } catch (NumberFormatException ex) {
                    // ignore
                }
                try {
                    Director.decay1Days = Integer.parseInt(fieldDays1.getText().trim());
                } catch (NumberFormatException ex) {
                    // ignore
                }
                try {
                    Director.decay2Years = Integer.parseInt(fieldYears2.getText().trim());
                } catch (NumberFormatException ex) {
                    // ignore
                }
                try {
                    Director.decay2Months = Integer.parseInt(fieldMonths2.getText().trim());
                } catch (NumberFormatException ex) {
                    // ignore
                }
                try {
                    Director.decay2Days = Integer.parseInt(fieldDays2.getText().trim());
                } catch (NumberFormatException ex) {
                    // ignore
                }
                
                releaseInstance();
            }
        });
        acceptButton.setPreferredSize(dim100by25);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                releaseInstance();

            }
        });
        cancelButton.setPreferredSize(dim100by25);


        // add the components to the panel
        panel.add(tFilePathLabel);
        panel.add(terrainSavePath);
        panel.add(browseTerrainPathButton);
        panel.add(eFilePathLabel);
        panel.add(eventSavePath);
        panel.add(browseEventPathButton);
        panel.add(acceptButton);
        panel.add(cancelButton);
        panel.add(dbPathLabel);
        panel.add(dbTextField);
        panel.add(saveDbButton);
        panel.add(cutoff1Label);
        panel.add(cutoff2Label);
        panel.add(years1);
        panel.add(years2);
        panel.add(months1);
        panel.add(months2);
        panel.add(days1);
        panel.add(days2);
        panel.add(fieldYears1);
        panel.add(fieldMonths1);
        panel.add(fieldDays1);
        panel.add(fieldYears2);
        panel.add(fieldMonths2);
        panel.add(fieldDays2);

        // lay out the components in the panel
        putRowContraints(layout, panel, tFilePathLabel, terrainSavePath, browseTerrainPathButton,  panel, true, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, eFilePathLabel, eventSavePath, browseEventPathButton, tFilePathLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, dbPathLabel, dbTextField, saveDbButton, eFilePathLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, cutoff1Label, fieldYears1, years1, fieldMonths1, months1, fieldDays1, days1, dbPathLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, cutoff2Label, fieldYears2, years2, fieldMonths2, months2, fieldDays2, days2, cutoff1Label, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, acceptButton, cancelButton, cutoff2Label, false, vPad, hPad, leftSidePad + 200);

        // set up the dialog properties and return it
        setMinimumSize(dimPanel);
        setPreferredSize(dimPanel);
        setMaximumSize(dimPanel);
        setResizable(false);
        validate();
        setLocationRelativeTo(gui.getFrame());
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
    
    public void putRowContraints(SpringLayout layout, Container panel, Component leftComponent,
            Component centerComponent, Component rightComponent, Component northComponent, boolean useNorth,
            int vPad, int hPad, int leftSidePad) {
        String edge = useNorth ? SpringLayout.NORTH : SpringLayout.SOUTH;
        //vertical constraints
        layout.putConstraint(SpringLayout.NORTH, leftComponent, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, centerComponent, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, rightComponent, vPad, edge, northComponent);
        //horizontal constraints
        layout.putConstraint(SpringLayout.WEST, leftComponent, leftSidePad, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.WEST, centerComponent, hPad, SpringLayout.EAST, leftComponent);
        layout.putConstraint(SpringLayout.WEST, rightComponent, hPad, SpringLayout.EAST, centerComponent);
    }
    
    public void putRowContraints(SpringLayout layout, Container panel, Component comp1,
            Component comp2, Component comp3, Component comp4, Component comp5, Component comp6,
            Component comp7, Component northComponent, boolean useNorth, int vPad, int hPad, int leftSidePad) {
        String edge = useNorth ? SpringLayout.NORTH : SpringLayout.SOUTH;
        //vertical constraints
        layout.putConstraint(SpringLayout.NORTH, comp1, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, comp2, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, comp3, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, comp4, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, comp5, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, comp6, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, comp7, vPad, edge, northComponent);
        //horizontal constraints
        layout.putConstraint(SpringLayout.WEST, comp1, leftSidePad, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.WEST, comp2, hPad, SpringLayout.EAST, comp1);
        layout.putConstraint(SpringLayout.WEST, comp3, hPad, SpringLayout.EAST, comp2);
        layout.putConstraint(SpringLayout.WEST, comp4, hPad, SpringLayout.EAST, comp3);
        layout.putConstraint(SpringLayout.WEST, comp5, hPad, SpringLayout.EAST, comp4);
        layout.putConstraint(SpringLayout.WEST, comp6, hPad, SpringLayout.EAST, comp5);
        layout.putConstraint(SpringLayout.WEST, comp7, hPad, SpringLayout.EAST, comp6);
    }

    public JFrame getFrame() {
        return gui.getFrame();
    }
}