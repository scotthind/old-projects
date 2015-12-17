package org.rowan.pathfinder.display;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog; 
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.rowan.pathfinder.pathfinder.Transformer;
import org.rowan.pathfinder.pathfinder.Vehicle;

/**
 * This class is the dialog window for selecting additional details.
 * 
 * @author John Schuff, Dan Urbano
 */
final class AdditionalDetailsDialog extends JDialog {
    private Vehicle[] vehicles;
    
    /**
     * Get the parse dialog instance.
     * @param frame The calling JFrame.
     * @param director The director instance.
     * @return The parse dialog instance.
     */    
    public AdditionalDetailsDialog(final JFrame frame, final int distance,
            final int speed, final int safety, final Director director) {
        // initialize local variables
        super(frame, "Additional Detail Selection", true);
        
        Container panel = getContentPane();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        Dimension dimPanel = new Dimension(400, 250);
        
        // initialize the labels
        JLabel modeLabel = new JLabel("Choose Path Mode");
        JLabel vehicleLabel = new JLabel("Choose Vehicles");
        JLabel vehicleSubLabel = new JLabel("(Ctrl Click to Select Multiple) (Default is \"Human\")");
        modeLabel.setAlignmentX(0.5f);
        vehicleLabel.setAlignmentX(0.5f);
        vehicleSubLabel.setAlignmentX(0.5f);
        
        //initialize the radio buttons
        final JRadioButton roadButton = new JRadioButton("Road");
        final JRadioButton offroadButton = new JRadioButton("Off-Road");
        JRadioButton bothButton = new JRadioButton("Both");

        roadButton.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(roadButton);
        group.add(offroadButton);
        group.add(bothButton);
        
        // initialize the JList
        List<Vehicle> listOfVehicles = new ArrayList<Vehicle>(director.getVehicles());
        if (listOfVehicles == null || listOfVehicles.isEmpty()) {
            listOfVehicles = new ArrayList<Vehicle>();
            listOfVehicles.add(Vehicle.createDefaultVehicle());
        }
        vehicles = new Vehicle[listOfVehicles.size()];
        for (int i=0; i<listOfVehicles.size(); i++) {
            vehicles[i] = listOfVehicles.get(i);
        }
        String[] vehicleNames = getVehicleNames();
        final JList list = new JList(vehicleNames); //data has type Object[]
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(8);
        
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(150, 80));
        
        // intialize buttons
        JButton acceptButton = new JButton("Accept");
        acceptButton.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                dispose();
                HashSet<Vehicle> vehicleSet = new HashSet<Vehicle>();
                for(Integer i : list.getSelectedIndices()) {
                    vehicleSet.add(vehicles[i]);
                }
                if (vehicleSet.isEmpty()) {
                    vehicleSet = null;
                }
                Transformer.TransformMode mode = roadButton.isSelected()? Transformer.TransformMode.ROAD_ONLY :
                        (offroadButton.isSelected()? Transformer.TransformMode.TERRAIN_ONLY :
                        (Transformer.TransformMode.ROAD_AND_TERRAIN));
                CalculationWorker workHorse = new CalculationWorker(
                        director, vehicleSet, mode, distance, speed, safety);
                workHorse.execute();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                dispose();
            }
        });
        
        // intialize inner pannels
        JPanel radioPanel = new JPanel();
        BoxLayout x = new BoxLayout(radioPanel, BoxLayout.LINE_AXIS);
        radioPanel.setLayout(x);
        radioPanel.add(Box.createHorizontalGlue());
        radioPanel.add(roadButton);
        radioPanel.add(offroadButton);
        radioPanel.add(bothButton);
        radioPanel.add(Box.createHorizontalGlue());
        roadButton.setAlignmentX(0.5f);
        offroadButton.setAlignmentX(0.5f);
        bothButton.setAlignmentX(0.5f);
        JPanel bottomPanel = new JPanel();
        BoxLayout y = new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS);
        bottomPanel.setLayout(y);
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(acceptButton);
        bottomPanel.add(Box.createHorizontalStrut(5));
        bottomPanel.add(cancelButton);
        bottomPanel.add(Box.createHorizontalStrut(5));
        
        // add everything to the panel
        panel.add(modeLabel);
        panel.add(radioPanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(vehicleLabel);
        panel.add(vehicleSubLabel);
        panel.add(listScroller);
        panel.add(Box.createVerticalStrut(2));
        panel.add(bottomPanel);
        
        // set up the dialog properties and return it
        setMinimumSize(dimPanel);
        setPreferredSize(dimPanel);
        setMaximumSize(dimPanel);
        setResizable(false);
        validate();
        setLocationRelativeTo(frame);
    }

    private String[] getVehicleNames() {
        String[] names = new String[vehicles.length];
        for (int i=0; i<vehicles.length; i++) {
            names[i] = vehicles[i].getName();
        }
        return names;
    }
        
}
