package org.rowan.pathfinder.display;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * This class is the dialog window for all parsing options.
 * 
 * @author Dan Urbano
 */
final class ParseDialog extends JDialog {
    private static ParseDialog instance = null;
    
    /**
     * Get the parse dialog instance.
     * @param frame The calling JFrame.
     * @param director The director instance.
     * @return The parse dialog instance.
     */
    public static JDialog getInstance(JFrame frame, Director director) {
        if (instance == null) {
            instance = new ParseDialog(frame, director);
        }
        return instance;
    }
    
    private ParseDialog(final JFrame frame, final Director director) {
        // initialize local variables
        super(frame, "Parsing Window", false);
        Container panel = getContentPane();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        Dimension dim100by25 = new Dimension(100, 25);
        Dimension dim125by25 = new Dimension(125, 25);
        Dimension dim450by25 = new Dimension(450, 25);
        Dimension dimPanel = new Dimension(835, 255);
        int vPad = 10;
        int hPad = 5;
        int leftSidePad = 20;
        
        // initialize the labels
        JLabel roadLabel = new JLabel("Load Road File");
        JLabel vehicleLabel = new JLabel("Load Vehicle File");
        JLabel terrainLabel = new JLabel("Load Terrain File");
        JLabel eventLabel = new JLabel("Load Event File");
        JLabel underpassLabel = new JLabel("Load Underpass File");
        JLabel speedlimitLabel = new JLabel("Load Speed Limit File");
        roadLabel.setPreferredSize(dim125by25);
        vehicleLabel.setPreferredSize(dim125by25);
        terrainLabel.setPreferredSize(dim125by25);
        eventLabel.setPreferredSize(dim125by25);
        underpassLabel.setPreferredSize(dim125by25);
        speedlimitLabel.setPreferredSize(dim125by25);
        
        // initialize the text fields
        final JTextField roadTextField = new JTextField();
        final JTextField vehicleTextField = new JTextField();
        final JTextField terrainTextField = new JTextField();
        final JTextField eventTextField = new JTextField();
        final JTextField underpassTextField = new JTextField();
        final JTextField speedlimitTextField = new JTextField();
        roadTextField.setPreferredSize(dim450by25);
        vehicleTextField.setPreferredSize(dim450by25);
        terrainTextField.setPreferredSize(dim450by25);
        eventTextField.setPreferredSize(dim450by25);
        underpassTextField.setPreferredSize(dim450by25);
        speedlimitTextField.setPreferredSize(dim450by25);
        
        // intialize the buttons
        JButton roadBrowseButton = new JButton("Browse");
        JButton vehicleBrowseButton = new JButton("Browse");
        JButton terrainBrowseButton = new JButton("Browse");
        JButton eventBrowseButton = new JButton("Browse");
        JButton underpassBrowseButton = new JButton("Browse");
        JButton speedlimitBrowseButton = new JButton("Browse");
        roadBrowseButton.setPreferredSize(dim100by25);
        vehicleBrowseButton.setPreferredSize(dim100by25);
        terrainBrowseButton.setPreferredSize(dim100by25);
        eventBrowseButton.setPreferredSize(dim100by25);
        underpassBrowseButton.setPreferredSize(dim100by25);
        speedlimitBrowseButton.setPreferredSize(dim100by25);
        JButton roadLoadButton = new JButton("Load");
        JButton vehicleLoadButton = new JButton("Load");
        JButton terrainLoadButton = new JButton("Load");
        JButton eventLoadButton = new JButton("Load");
        JButton underpassLoadButton = new JButton("Load");
        JButton speedlimitLoadButton = new JButton("Load");
        roadLoadButton.setPreferredSize(dim100by25);
        roadLoadButton.setMaximumSize(dim100by25);
        vehicleLoadButton.setPreferredSize(dim100by25);
        terrainLoadButton.setPreferredSize(dim100by25);
        eventLoadButton.setPreferredSize(dim100by25);
        underpassLoadButton.setPreferredSize(dim100by25);
        speedlimitLoadButton.setPreferredSize(dim100by25);
        
        // intialize the file choosers
        final JFileChooser roadFileChooser = new JFileChooser();
        final JFileChooser vehicleFileChooser = new JFileChooser();
        final JFileChooser terrainFileChooser = new JFileChooser();
        final JFileChooser eventFileChooser = new JFileChooser();
        final JFileChooser underpassFileChooser = new JFileChooser();
        final JFileChooser speedlimitFileChooser = new JFileChooser();
        
        // add action listeners to the file choosers
        roadFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File file = roadFileChooser.getSelectedFile();
                    roadTextField.setText(file.getAbsolutePath());
                }
            }
        });
        vehicleFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File file = vehicleFileChooser.getSelectedFile();
                    vehicleTextField.setText(file.getAbsolutePath());
                }
            }
        });
        terrainFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File file = terrainFileChooser.getSelectedFile();
                    terrainTextField.setText(file.getAbsolutePath());
                }
            }
        });
        eventFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File file = eventFileChooser.getSelectedFile();
                    eventTextField.setText(file.getAbsolutePath());
                }
            }
        });
        underpassFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File file = underpassFileChooser.getSelectedFile();
                    underpassTextField.setText(file.getAbsolutePath());
                }
            }
        });
        speedlimitFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File file = speedlimitFileChooser.getSelectedFile();
                    speedlimitTextField.setText(file.getAbsolutePath());
                }
            }
        });
        
        // add action listeners to the browse buttons
        roadBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roadFileChooser.showOpenDialog(frame);
            }
        });
        vehicleBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vehicleFileChooser.showOpenDialog(frame);
            }
        });
        terrainBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terrainFileChooser.showOpenDialog(frame);
            }
        });
        eventBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventFileChooser.showOpenDialog(frame);
            }
        });
        underpassBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                underpassFileChooser.showOpenDialog(frame);
            }
        });
        speedlimitBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                speedlimitFileChooser.showOpenDialog(frame);
            }
        });
        
        // add action listeners to the load buttons
        roadLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean shouldOverwrite = true;
                File file = new File(roadTextField.getText());
                if (file.exists()) {
                    if (!director.getRoads().isEmpty()) {
                        String str = "road(s)";
                        int x = JOptionPane.showOptionDialog(frame, "Some " + str + " have already been loaded. " +
                            "Would you like to overwrite them or append to them?",
                            "Would you like to overwrite?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, new String[] {"Overwite", "Append"},
                            instance);
                        shouldOverwrite = (x == JOptionPane.YES_OPTION);
                        if (x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                    director.loadRoads(file, null, shouldOverwrite);
                } else {
                    String error;
                    if (roadTextField.getText().trim().equals("")) {
                        error = "No file specified!";
                    } else {
                        error = file.getAbsolutePath() + " is not a valid file!";
                    }
                    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        vehicleLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean shouldOverwrite = true;
                File file = new File(vehicleTextField.getText());
                if (file.exists()) {
                    if (!director.getVehicles().isEmpty()) {
                        String str = "vehicle(s)";
                        int x = JOptionPane.showOptionDialog(frame, "Some " + str + " have already been loaded. " +
                                "Would you like to overwrite them or append to them?",
                                "Would you like to overwrite?", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, new String[] {"Overwite", "Append"},
                                "Append");
                        shouldOverwrite = (x == JOptionPane.YES_OPTION);
                        if (x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                    director.loadVehicles(file, shouldOverwrite);
                } else {
                    String error;
                    if (vehicleTextField.getText().trim().equals("")) {
                        error = "No file specified!";
                    } else {
                        error = file.getAbsolutePath() + " is not a valid file!";
                    }
                    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        terrainLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean shouldOverwrite = true;
                File file = new File(terrainTextField.getText());
                if (file.exists()) {
                    if (!director.getTerrains().isEmpty()) {
                        String str = "terrain(s)";
                        int x = JOptionPane.showOptionDialog(frame, "Some " + str + " have already been loaded. " +
                                "Would you like to overwrite them or append to them?",
                                "Would you like to overwrite?", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, new String[] {"Overwite", "Append"},
                                "Append");
                        shouldOverwrite = (x == JOptionPane.YES_OPTION);
                        if (x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                    director.loadTerrains(file, shouldOverwrite);
                } else {
                    String error;
                    if (terrainTextField.getText().trim().equals("")) {
                        error = "No file specified!";
                    } else {
                        error = file.getAbsolutePath() + " is not a valid file!";
                    }
                    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        eventLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean shouldOverwrite = true;
                File file = new File(eventTextField.getText());
                if (file.exists()) {
                    if (!director.getEvents().isEmpty()) {
                        String str = "event(s)";
                        int x = JOptionPane.showOptionDialog(frame, "Some " + str + " have already been loaded. " +
                                "Would you like to overwrite them or append to them?",
                                "Would you like to overwrite?", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, new String[] {"Overwite", "Append"},
                                "Append");
                        shouldOverwrite = (x == JOptionPane.YES_OPTION);
                        if (x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                    director.loadEvents(file, shouldOverwrite);
                } else {
                    String error;
                    if (eventTextField.getText().trim().equals("")) {
                        error = "No file specified!";
                    } else {
                        error = file.getAbsolutePath() + " is not a valid file!";
                    }
                    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        underpassLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean shouldOverwrite = true;
                File file = new File(underpassTextField.getText());
                if (file.exists()) {
                    if (!director.getUnderpasses().isEmpty()) {
                        String str = "underpass(es)";
                        int x = JOptionPane.showOptionDialog(frame, "Some " + str + " have already been loaded. " +
                                "Would you like to overwrite them or append to them?",
                                "Would you like to overwrite?", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, new String[] {"Overwite", "Append"},
                                "Append");
                        shouldOverwrite = (x == JOptionPane.YES_OPTION);
                        if (x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                    director.loadUnderpasses(file, shouldOverwrite);
                } else {
                    String error;
                    if (underpassTextField.getText().trim().equals("")) {
                        error = "No file specified!";
                    } else {
                        error = file.getAbsolutePath() + " is not a valid file!";
                    }
                    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        speedlimitLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean shouldOverwrite = true;
                File file = new File(speedlimitTextField.getText());
                if (file.exists()) {
                    if (!director.getSpeedLimits().isEmpty()) {
                        String str = "spped limit(s)";
                        int x = JOptionPane.showOptionDialog(frame, "Some " + str + " have already been loaded. "
                                + "Would you like to overwrite them or append to them?",
                                "Would you like to overwrite?", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, new String[]{"Overwite", "Append"},
                                "Append");
                        shouldOverwrite = (x == JOptionPane.YES_OPTION);
                        if (x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                    director.loadSpeedlimits(file, shouldOverwrite);
                } else {
                    String error;
                    if (speedlimitTextField.getText().trim().equals("")) {
                        error = "No file specified!";
                    } else {
                        error = file.getAbsolutePath() + " is not a valid file!";
                    }
                    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // add the components to the panel
        panel.add(roadLabel);
        panel.add(roadTextField);
        panel.add(roadBrowseButton);
        panel.add(roadLoadButton);
        panel.add(vehicleLabel);
        panel.add(vehicleTextField);
        panel.add(vehicleBrowseButton);
        panel.add(vehicleLoadButton);
        panel.add(terrainLabel);
        panel.add(terrainTextField);
        panel.add(terrainBrowseButton);
        panel.add(terrainLoadButton);
        panel.add(eventLabel);
        panel.add(eventTextField);
        panel.add(eventBrowseButton);
        panel.add(eventLoadButton);
        panel.add(underpassLabel);
        panel.add(underpassTextField);
        panel.add(underpassBrowseButton);
        panel.add(underpassLoadButton);
        panel.add(speedlimitLabel);
        panel.add(speedlimitTextField);
        panel.add(speedlimitBrowseButton);
        panel.add(speedlimitLoadButton);
        
        // lay out the components in the panel
        putRowContraints(layout, panel, roadLabel, roadTextField, roadBrowseButton, roadLoadButton, panel, true, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, vehicleLabel, vehicleTextField, vehicleBrowseButton, vehicleLoadButton, roadLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, terrainLabel, terrainTextField, terrainBrowseButton, terrainLoadButton, vehicleLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, eventLabel, eventTextField, eventBrowseButton, eventLoadButton, terrainLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, underpassLabel, underpassTextField, underpassBrowseButton, underpassLoadButton, eventLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, speedlimitLabel, speedlimitTextField, speedlimitBrowseButton, speedlimitLoadButton, underpassLabel, false, vPad, hPad, leftSidePad);

        // set up the dialog properties and return it
        setMinimumSize(dimPanel);
        setPreferredSize(dimPanel);
        setMaximumSize(dimPanel);
        setResizable(false);
        validate();
        setLocationRelativeTo(frame);
    }
    
    public void putRowContraints(SpringLayout layout, Container panel, JLabel label, 
            JTextField textField, JButton browseButton, JButton loadButton,
            Component northComponent, boolean useNorth, int vPad, int hPad, int leftSidePad) {
        String edge = useNorth? SpringLayout.NORTH : SpringLayout.SOUTH;
        //vertical constraints
        layout.putConstraint(SpringLayout.NORTH, label, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, textField, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, browseButton, vPad, edge, northComponent);
        layout.putConstraint(SpringLayout.NORTH, loadButton, vPad, edge, northComponent);
        //horizontal constraints
        layout.putConstraint(SpringLayout.WEST, label, leftSidePad, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.WEST, textField, hPad, SpringLayout.EAST, label);
        layout.putConstraint(SpringLayout.WEST, browseButton, hPad, SpringLayout.EAST, textField);
        layout.putConstraint(SpringLayout.WEST, loadButton, hPad, SpringLayout.EAST, browseButton);
    }    
}


