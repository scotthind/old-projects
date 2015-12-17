/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rowan.pathfinder.display;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * This class is the dialog window for all parsing options.
 * 
 * @author Jon Schuff, Dan Urbano
 */
final class EventDetailsDialog extends JDialog {

    private static EventDetailsDialog instance = null;

    /**
     * Get the parse dialog instance.
     * @param frame The calling JFrame.
     * @param director The director instance.
     * @return The parse dialog instance.
     */
    public static JDialog getInstance(JFrame frame, Director director) {
        if (instance == null) {
            instance = new EventDetailsDialog(frame, director);
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    private EventDetailsDialog(final JFrame frame, final Director director) {
        // initialize local variables
        super(frame, "Event Details Window", false);
        Container panel = getContentPane();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        Dimension dim50by25 = new Dimension(50, 25);
        Dimension dim100by25 = new Dimension(100, 25);
        Dimension dim150by25 = new Dimension(150, 25);
        Dimension dim200by25 = new Dimension(200, 25);

        Dimension dim100by100 = new Dimension(100, 100);
        Dimension dim150by100 = new Dimension(150, 100);


        Dimension dim200by100 = new Dimension(200, 100);
        int vPad = 10;
        int hPad = 5;
        int leftSidePad = 20;
        Dimension dimPanel = new Dimension(300, 325);

        // initialize the labels

        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setPreferredSize(dim150by25);

        JLabel endDateLabel = new JLabel("End Date (can be empty):");
        endDateLabel.setPreferredSize(dim150by25);

        JLabel severityLabel = new JLabel("Severity Level (0-10):");
        severityLabel.setPreferredSize(dim150by25);

        JLabel hasMinesLabel = new JLabel("Contains Landmines:");
        hasMinesLabel.setPreferredSize(dim150by25);

        JLabel descriptionLabel = new JLabel("Add Description: ");
        descriptionLabel.setPreferredSize(dim100by100);

        // initialize the text fields
        String[] yesOrNo = {"Yes", "No"};

        // init the combo boxes

        final JTextField startDateField = new JTextField("mm/dd/yyyy");
        startDateField.setPreferredSize(dim100by25);

        final JTextField endDateField = new JTextField("mm/dd/yyyy");
        endDateField.setPreferredSize(dim100by25);

        final JTextField severityField = new JTextField("5");
        severityField.setPreferredSize(dim100by25);

        final JComboBox hasMinesComboBox = new JComboBox(yesOrNo);
        hasMinesComboBox.setPreferredSize(dim100by25);

        final JTextArea descriptionField = new JTextArea();
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setPreferredSize(dim150by100);

        JButton acceptButton = new JButton("Accept");
        acceptButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                String[] sD = startDateField.getText().split("[-_./]+");
       
                Calendar startDate = GregorianCalendar.getInstance();
                try {
                    startDate.set(Integer.parseInt(sD[2]), Integer.parseInt(sD[0]) -1, Integer.parseInt(sD[1]));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame,
                            "Error reading start date: please input a date in MM/DD/YYYY format.",
                            "Can't Create Event", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Calendar endDate;

                if (!endDateField.getText().isEmpty()) {//a date was given
                    try {
                        String[] eD = endDateField.getText().split("[-_./]+");
                        endDate = GregorianCalendar.getInstance();
                        endDate.set(Integer.parseInt(eD[2]), Integer.parseInt(eD[0]) -1, Integer.parseInt(eD[0]));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame,
                                "Error reading end date: please input a date in MM/DD/YYYY format.",
                                "Can't Create Event", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                else
                    endDate = null;

                double severity = Integer.parseInt(severityField.getText()) / 10.0; //User inputs 9, feed .9
                if(severity < 0 || severity > 10){
                    JOptionPane.showMessageDialog(frame,
                                "Error reading severity level: please input a number 0-10.",
                                "Can't Create Event", JOptionPane.ERROR_MESSAGE);
                        return;
                }
                boolean hasMines;
                if (hasMinesComboBox.getSelectedItem().equals("Yes")) {
                    hasMines = true;
                } else {
                    hasMines = false;
                }

                if (!director.doneCreatingEvent(startDate, endDate, severity, hasMines, descriptionField.getText())) {
                    JOptionPane.showMessageDialog(frame,
                            "Error creating event: must be a convex shape.",
                            "Can't Create Event", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                director.startCreatingEvent();
                //save event created
                releaseInstance();
            }
        });

        acceptButton.setPreferredSize(dim100by25);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent event) {
                director.doneCreatingEvent(null, null, 0.0, false, null);
                director.startCreatingEvent();
                releaseInstance();

            }
        });
        cancelButton.setPreferredSize(dim100by25);


        // add the components to the panel
        panel.add(startDateLabel);
        panel.add(startDateField);
        panel.add(endDateLabel);
        panel.add(endDateField);
        panel.add(severityLabel);
        panel.add(severityField);
        panel.add(hasMinesLabel);
        panel.add(hasMinesComboBox);
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(acceptButton);
        panel.add(cancelButton);


        // lay out the components in the panel
        putRowContraints(layout, panel, startDateLabel, startDateField, panel, true, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, endDateLabel, endDateField, startDateLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, severityLabel, severityField, endDateLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, hasMinesLabel, hasMinesComboBox, severityLabel, false, vPad, hPad, leftSidePad);
        putRowContraints(layout, panel, descriptionLabel, descriptionField, hasMinesLabel, false, vPad, hPad, leftSidePad);

        putRowContraints(layout, panel, acceptButton, cancelButton, descriptionLabel, false, vPad, hPad, leftSidePad + 25);

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