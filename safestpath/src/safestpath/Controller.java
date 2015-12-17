/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */

/*
 * User_Input.java
 *
 * Created on Nov 16, 2010, 4:28:33 PM
 */
package safestpath;

import html.FTPController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import safestpath.pathing.Graph;
import safestpath.mapElements.Location;
import javax.swing.*;
import safestpath.mapElements.Intersection;
import safestpath.mapElements.Route;
import safestpath.parser.KML_Parser;
import safestpath.pathing.PathFinder;
import safestpath.events.Event_Data;

/**
 * The controller class takes care of showing the user interface and setting the
 * right flow of control for the program. As it controls the ui, it also controls
 * taking in user input as well. The controller makes all of the function calls necessary
 * to create the routes specified by the points given and type of route wanted. It
 * also takes care of error checking for the inputs.
 *
 * All of the user interface variables are pre-named since we used an automated
 * UI creator.
 */
public class Controller extends javax.swing.JFrame {

    //start and end points read from the ui
    private double startLat = 0.0;
    private double startLong = 0.0;
    private double endLat = 0.0;
    private double endLong = 0.0;
    //start and end points are created into location objects once verified
    //that they are valid coords
    private Location startLocation = null;
    private Location endLocation = null;
    //the 3 map types
    private static boolean safest = false;
    private static boolean shortest = false;
    private static boolean fastest = false;
    //the graph of roads and intersections
    private Graph graph;
    //temp holders for the lat/longitude inputs, and sever info
    private String lat1, lat2, lon1, lon2;
    private String serverName, loginName, serverPassword;
    private String roadDataPath, eventDataPath;

    /** Creates new form User_Input */
    public Controller()
    {
        initComponents();
        lat1 = "";
        lat2 = "";
        lon1 = "";
        lon2 = "";

        serverName = "";
        loginName = "";
        serverPassword = "";

        //default file paths if no files are given
        roadDataPath = "doc.kml";
        eventDataPath = "eventData.kml";
    }

    /**
     * Parse the road and event data with a kml_parser
     */
    private void parseData()
    {
        KML_Parser parser = new KML_Parser();

        parser.initParser(roadDataPath);

        parser.parseRoadData();

        parser.initParser(eventDataPath);

        Event_Data events = parser.parseEventData();

        graph = parser.transformToGraph();

        graph.addSeveritesToRoad(events);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jCheckBox3 = new javax.swing.JCheckBox();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextField2.setText("Latitude");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField3.setText("Longitude");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.setText("Longitude");
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel1.setText("Start");

        jLabel2.setText("End");

        jCheckBox1.setText("Fastest");

        jCheckBox2.setText("Shortest");

        jButton1.setText("Submit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setText("Latitude");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("Safest");

        jTextField5.setText("Server Name");
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jTextField6.setText("Login Name");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jPasswordField1.setText("jPasswordField1");
        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Server");

        jLabel4.setText("Server Login");

        jLabel5.setText("Server Password");

        jButton2.setText("Add Road Data File");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Add Event Data File");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2)
                                .addComponent(jButton2))
                            .addComponent(jLabel5))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField2)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)))
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(113, 113, 113)
                                .addComponent(jButton3))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jButton1)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jCheckBox1)
                        .addComponent(jCheckBox2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jCheckBox3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jCheckBox3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox2)
                        .addGap(3, 3, 3)
                        .addComponent(jCheckBox1)
                        .addGap(32, 32, 32))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //submit button
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        fastest = jCheckBox1.isSelected();
        shortest = jCheckBox2.isSelected();
        safest = jCheckBox3.isSelected();

        //make sure at least 1 route type is selected
        if (!fastest && !shortest && !safest)
        {
            JOptionPane.showMessageDialog(null, "Choose a route type.", "Message", 1);
        }

        try
        {
            //get all lat/lon inputs and make sure that they are valid

            lat1 = jTextField1.getText();
            startLat = Double.parseDouble(lat1);
            verifyLatitudeCoordinates(startLat);

            lat2 = jTextField2.getText();
            endLat = Double.parseDouble(lat2);
            verifyLatitudeCoordinates(endLat);

            lon1 = jTextField3.getText();
            startLong = Double.parseDouble(lon1);
            verifyLongitudeCoordinates(startLong);

            lon2 = jTextField4.getText();
            endLong = Double.parseDouble(lon2);
            verifyLongitudeCoordinates(endLong);

            //create the actual start and end locations
            startLocation = new Location(startLat, startLong);
            endLocation = new Location(endLat, endLong);

            //get the server info
            serverName = jTextField5.getText();
            loginName = jTextField6.getText();
            serverPassword = new String(jPasswordField1.getPassword());

        }
        //null pointer is thrown if no start and end points are inputted
        catch (NullPointerException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Enter Start and End Coordinates", "Message", 1);
        }
        //number format exception is thrown if the lat/long points are not valid
        catch (NumberFormatException nfe)
        {
            nfe.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid Coordinates", "Message", 1);
        }

        //parse the road/event data once the file paths are found
        parseData();

        try
        {
            try
            {
                //actually find the routes from the start to the end
                findPaths();
            }
            //uri syntax exception if the server information is wrong
            catch (URISyntaxException ex)
            {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //file not found exception thrown if the ftpcontroller cannot upload the
        //route kml file
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        //io exception thrown if there is trouble with the ftp uploading
        catch (IOException ex)
        {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        lat2 = jTextField2.getText();
        System.out.println(lat2);
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        lat1 = jTextField1.getText();
        System.out.println(lat1);
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        lon1 = jTextField3.getText();
        System.out.println(lon1);
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        lon2 = jTextField4.getText();
        System.out.println(lon2);
    }//GEN-LAST:event_jTextField4ActionPerformed
    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTextField5ActionPerformed
    {//GEN-HEADEREND:event_jTextField5ActionPerformed
        serverName = jTextField5.getText();
    }//GEN-LAST:event_jTextField5ActionPerformed
    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTextField6ActionPerformed
    {//GEN-HEADEREND:event_jTextField6ActionPerformed
        loginName = jTextField6.getText();
    }//GEN-LAST:event_jTextField6ActionPerformed
    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jPasswordField1ActionPerformed
    {//GEN-HEADEREND:event_jPasswordField1ActionPerformed
        //getpassword returns a character array so have to make it into a string
        serverPassword = new String(jPasswordField1.getPassword());
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
    {//GEN-HEADEREND:event_jButton2ActionPerformed
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.showOpenDialog(this);

        File file = fileChooser.getSelectedFile();

        System.out.println(file.getAbsoluteFile());

        roadDataPath = file.getAbsolutePath();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton3ActionPerformed
    {//GEN-HEADEREND:event_jButton3ActionPerformed
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.showOpenDialog(this);

        File file = fileChooser.getSelectedFile();

        System.out.println(file.getAbsoluteFile());

        eventDataPath = file.getAbsolutePath();
    }//GEN-LAST:event_jButton3ActionPerformed
    /**
     * verify that entered coordinates are valid
     */
    private boolean verifyLatitudeCoordinates(double latitude)
    {
        if (latitude <= 90 && latitude >= -90)
        {
            return true;


        }
        else
        {
            throw new NumberFormatException("Latitude must be between -90 and 90");
        }


    }

    /**
     * verifty that the longitude coordinates are correct
     */
    private boolean verifyLongitudeCoordinates(double longitude)
    {
        if (longitude <= 180 && longitude >= -180)
        {
            return true;


        }
        else
        {
            throw new NumberFormatException("Longitude must be between -180 and 180");
        }


    }

    /**
     * The main method in this case will just continously run the user interface
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run()
            {
                new Controller().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables

    private void findPaths() throws FileNotFoundException, IOException, URISyntaxException
    {
        //find the start location
        Intersection start = null;
        Intersection end = null;

        //find if the start and end points are already intersection locations
        for (Intersection i : graph.getAllIntersections())
        {
            if (startLocation.equals(i.getLocation()))
            {
                start = i;
            }

            if (endLocation.equals(i.getLocation()))
            {
                end = i;
            }
        }

        //if either was null that means they were not intersections, so
        //find the nearest intersection and use that instead
        if (start == null)
        {
            start = graph.nearestIntersection(startLocation);
        }
        if (end == null)
        {
            end = graph.nearestIntersection(endLocation);
        }

        //make sure neither start or end are null, meaning no intersections could be
        //found. This should never happen since we find the nearest intersection
        //from the start and end point
        if (start == null || end == null)
        {
            JOptionPane.showMessageDialog(null, "Invalid coordinate positions", "Message", 1);
        }

        //make sure start and end are differant locations
        if (start.equals(end))
        {
            JOptionPane.showMessageDialog(this, "The locations must correspond to unique map locations");

            //return so that no routes are found
            return;
        }

        //find the paths
        PathFinder pathFinder = new PathFinder(graph, fastest, shortest, safest, start, end);
        pathFinder.execute();
        List<Route> routes = pathFinder.getPath();

        //upload the event data file to the ftp server
        FTPController ftp = new FTPController(serverName, loginName, serverPassword);
        ftp.uploadToServer(new File(eventDataPath), "public_html");

        Output output = new Output(routes, ftp);

        try
        {
            output.writeToFile();//display the generated kml in a web browser
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

            //open up the routes in a web page
            java.net.URI uri = new java.net.URI("file:///Users/freise29/safestpath/src/html/routes.html");
            desktop.browse(uri);
        }
        //only exception that should be thrown here is that there was trouble with finding the server
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Server not found", "Message", 1);
        }
    }
}
