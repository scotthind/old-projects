package org.rowan.pathfinder.display;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.SurfaceIcon;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwindx.examples.util.ImageAnnotation;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.Polygon2D;
import org.rowan.pathfinder.networking.client.ClientTcpConnectionHandler;
import org.rowan.pathfinder.networking.server.EventMessage;
import org.rowan.pathfinder.parser.EventParser;
import org.rowan.pathfinder.parser.OSMParser;
import org.rowan.pathfinder.parser.PFParseLogException;
import org.rowan.pathfinder.parser.RoadParser;
import org.rowan.pathfinder.parser.SpeedLimitParser;
import org.rowan.pathfinder.parser.TerrainParser;
import org.rowan.pathfinder.parser.UnderpassParser;
import org.rowan.pathfinder.parser.VehicleParser;
import org.rowan.pathfinder.parser.XMLParser;
import org.rowan.pathfinder.parser.XMLSubParser;
import org.rowan.pathfinder.pathfinder.Event;
import org.rowan.pathfinder.pathfinder.Logic2D;
import org.rowan.pathfinder.pathfinder.Path;
import org.rowan.pathfinder.pathfinder.Pathfinder;
import org.rowan.pathfinder.pathfinder.RoadSegment;
import org.rowan.pathfinder.pathfinder.SpeedLimit;
import org.rowan.pathfinder.pathfinder.Terrain;
import org.rowan.pathfinder.pathfinder.TerrainType;
import org.rowan.pathfinder.pathfinder.Transformer;
import org.rowan.pathfinder.pathfinder.Traversable;
import org.rowan.pathfinder.pathfinder.Underpass;
import org.rowan.pathfinder.pathfinder.Vehicle;

/**
 * Class <code>Director</code> is responsible for controlling all of the user's
 * activity. It is is the core model, or "back end" to the GUI.
 * 
 * @author Dan Urbano, Shahid Akhter, Jon Schuff
 * @version 1.0
 * @since 1.0
 */
public class Director {

    private Set<Event> events = new HashSet<Event>();
    private Set<Terrain> terrains = new HashSet<Terrain>();
    private Set<RoadSegment> roads = new HashSet<RoadSegment>();
    private Set<SpeedLimit> speedLimits = new HashSet<SpeedLimit>();
    private Set<Underpass> underpasses = new HashSet<Underpass>();
    private Set<Vehicle> vehicles = new HashSet<Vehicle>();
    private WorldWindowGLJPanel wwd;
    private Model mainViewModel;
    private boolean isCreatingEvent = false;
    private boolean isCreatingTerrain = false;
    private boolean isCreatingPath = false;
    private List<LatLon> newEventLatLonList = new LinkedList<LatLon>();
    private List<LatLon> newTerrainLatLonList = new LinkedList<LatLon>();
    private List<LatLon> newPathLatLonList = new LinkedList<LatLon>();
    private BasicShapeAttributes polyAttr, lineAttr;
    private RenderableLayer drawingLayer;
    private RenderableLayer terrainLayer;
    private RenderableLayer eventLayer;
    private RenderableLayer databaseEventLayer;
    private RenderableLayer pathLayer;
    private RenderableLayer underpassLayer;
    private SurfacePolygon prevEventShape = null;
    private SurfacePolygon prevTerrainShape = null;
    private SurfacePolyline prevPathLine = null;
    private List<Event> creatingEventList = new ArrayList<Event>();
    private List<Terrain> creatingTerrainList = new ArrayList<Terrain>();
    private Vector2D pathStartPoint = null;
    private Vector2D pathEndPoint = null;
    private boolean didLastEventFail = false;
    private boolean didLastTerrainFail = false;
    private JFrame frame;
    private MainGUI gui;
    private ClientTcpConnectionHandler serverUplink;
    public static volatile int decay1Years = 0;
    public static volatile int decay1Months = 0;
    public static volatile int decay1Days = 0;
    public static volatile int decay2Years = 0;
    public static volatile int decay2Months = 0;
    public static volatile int decay2Days = 0;
    private static final List<String> LOG_NOTHING_PARSED = Arrays.asList(new String[]{"There was no correct data to parse!"});
    private String databaseTable = MainGUI.DEFAULT_EVENTTABLE_NAME;
    private List<Event> receivedEventQueue;
    private boolean shouldShowAlert = true;

    public Director(MainGUI gui, Model model, WorldWindowGLJPanel wwd, JFrame frame) {
        this.gui = gui;
        this.frame = frame;

        drawingLayer = new RenderableLayer();
        terrainLayer = new RenderableLayer();
        eventLayer = new RenderableLayer();
        databaseEventLayer = new RenderableLayer();
        pathLayer = new RenderableLayer();
        underpassLayer = new RenderableLayer();
        drawingLayer.setName("Drawing Layer");
        terrainLayer.setName("Terrain Layer");
        eventLayer.setName("Event Layer");
        databaseEventLayer.setName("Database Event Layer");
        pathLayer.setName("Path Layer");
        underpassLayer.setName("Underpass Layer");
        this.wwd = wwd;
        model.getLayers().add(drawingLayer);
        model.getLayers().add(terrainLayer);
        model.getLayers().add(pathLayer);
        model.getLayers().add(eventLayer);
        model.getLayers().add(databaseEventLayer);
        model.getLayers().add(underpassLayer);
        polyAttr = new BasicShapeAttributes();
        polyAttr.setOutlineMaterial(new Material(Color.WHITE));
        polyAttr.setInteriorMaterial(new Material(Color.WHITE));
        polyAttr.setOutlineOpacity(.5);
        polyAttr.setInteriorOpacity(.5);
        lineAttr = new BasicShapeAttributes();
        lineAttr.setOutlineMaterial(new Material(Color.RED));
        lineAttr.setInteriorMaterial(new Material(Color.RED));
        lineAttr.setOutlineWidth(10);
        lineAttr.setOutlineOpacity(1);
        lineAttr.setInteriorOpacity(1);

        mainViewModel = model;
        receivedEventQueue = new LinkedList<Event>();
    }

    public void connectToServer() {
        
        serverUplink = ClientTcpConnectionHandler.getInstance(this, gui.getDatabaseIP(), 1338, "events");
        
        if(serverUplink == null){ //Problem opening a client connection (didnt start)
          //  System.err.println("Error: Unable to retrieve an instance of the connection. Please Try again.");
        }
        else{
            serverUplink.start();
        }
        
    }
    //Simple check to see if the uplink is connected
    public boolean isConnectedToServer() {
        if(serverUplink != null && serverUplink.isConnected())
            return true;
        return false;
    }

     public void loadEventsFromDatabase(String tableName) throws IOException {
         this.databaseTable = tableName;
        serverUplink.initConnectionToDatabase(tableName);
    }
    public void startCreatingEvent() {
        isCreatingEvent = true;
    }

    public void endCreatingEvent() {
        isCreatingEvent = false;
    }

    public boolean isCreatingEvent() {
        return isCreatingEvent;
    }

    public void makeNewEventPoint(LatLon point) {
        if (point == null) {
            return;
        }
        didLastEventFail = false;
        newEventLatLonList.add(point);
    }

    public void drawNewEvent(LatLon currentPoint) {
        if (currentPoint == null) {
            return;
        }
        List<LatLon> drawList = new LinkedList<LatLon>();
        for (LatLon l : newEventLatLonList) {
            drawList.add(l);
        }
        drawList.add(currentPoint);
        SurfacePolygon eventShape = new SurfacePolygon(polyAttr, drawList);
        if (prevEventShape != null) {
            drawingLayer.removeRenderable(prevEventShape);
        }
        if (eventShape != null) {
            drawingLayer.addRenderable(eventShape);
            prevEventShape = eventShape;
        }
    }

    public boolean doneCreatingEvent(Calendar startDate, Calendar endDate,
            double severity, boolean hasMines, String description) {

        SurfacePolygon eventShape = new SurfacePolygon(polyAttr, newEventLatLonList);
        if (prevEventShape != null) {
            drawingLayer.removeRenderable(prevEventShape);
        }
        if (startDate == null) {
            newEventLatLonList = new LinkedList<LatLon>();
            //System.out.println("RETURNING FALSE FROM DONECREATINGEVENT");
            return false;
        }

        drawingLayer.addRenderable(eventShape);
        prevEventShape = null;
        isCreatingEvent = false;
        List<Vector2D> vertList = new LinkedList<Vector2D>();
        for (LatLon l : newEventLatLonList) {
            vertList.add(Logic2D.latLonToVector2D(l));
        }
        newEventLatLonList = new LinkedList<LatLon>();
        if (Polygon2D.validateVertices(Logic2D.centerVertices(vertList))) {
            Event event = new Event(startDate, endDate, severity, description, new Polygon2D(Logic2D.getCentroid(vertList), Logic2D.centerVertices(vertList)), hasMines);
            creatingEventList.add(event);
        } else {
            Collections.reverse(vertList);
            if (Polygon2D.validateVertices(Logic2D.centerVertices(vertList))) {
                Event event = new Event(startDate, endDate, severity, description, new Polygon2D(Logic2D.getCentroid(vertList), Logic2D.centerVertices(vertList)), hasMines);
                creatingEventList.add(event);
            } else {
                drawingLayer.removeRenderable(eventShape);
                wwd.redraw();
                didLastEventFail = true;
                //System.out.println("RETURNING FALSE FROM DONECREATINGEVENT");
                return false;
            }
        }

        //System.out.println("RETURNING TRUE FROM DONEEVENT");
        return true;
    }

    public boolean didLastEventFail() {
        return didLastEventFail;
    }

    public void startEventList() {
        creatingEventList = new ArrayList<Event>();
    }

    public void endEventList(int saveOption) {

        if (saveOption != 3) {//three is our cancel scenario


            switch (saveOption) {
                case 0: //JUST UPLOAD TO DB
                    uploadToServer();
                    break;
                case 2: //UPLOAD AND SAVE LOCALLY
                    uploadToServer();
                case 1: //JUST SAVE LOCALLY
                    String filename = (String) JOptionPane.showInputDialog(
                            frame,
                            "Please specify a file name:\n",
                            "Local Options",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "events.xml");
                    saveEventLocally(filename);
                    break;
            }
        }
        //elseif(saveOption == 3) we just clear everything and return
        drawingLayer.removeAllRenderables();
        creatingEventList = new ArrayList<Event>();
    }

    public boolean hasCreatedEvents() {
        return !creatingEventList.isEmpty();
    }

    public boolean hasCreatedTerrains() {
        return !creatingTerrainList.isEmpty();
    }

    @SuppressWarnings("empty-statement")
    private void uploadToServer() {

        if (serverUplink == null || !serverUplink.isConnected()) {
            connectToServer();
        }

        if (serverUplink != null && serverUplink.isConnected()) {
            String tableName = null;
            if (!creatingEventList.isEmpty()) {
                tableName = JOptionPane.showInputDialog(frame, "What table would you like to send the event(s) to? (Leave blank for current table: " + databaseTable + ")");
                if (tableName.equals("")) {
                    tableName = databaseTable; //set the table name to default
                }
            }

            for (Event e : creatingEventList) {
                //System.out.println("Event: " + e);
                e.convertPolyToList();
                serverUplink.eMessage = new EventMessage(tableName, e);
                serverUplink.interrupt();
                while (serverUplink.eMessage != null);
                //System.out.println("Set the eMessage to be sent");
            }
        }
        //System.out.println("Finished sending to database");
    }

    public void saveEventLocally(String filename) {
        String directory = gui.getEventSaveDirectory();
        if (directory.endsWith("."))//If there is for some reason a period at the end, trim it
        {
            directory = directory.substring(0, directory.length() - 1);
        }
        File f = new File(directory + filename);

        try {
            boolean shouldOverwrite = true;

            if (f.exists()) {
                if (!f.canWrite()) {
                    String rand = Math.random() + "";
                    String newFileName = filename.replaceAll(".xml", "") + (rand.substring(2)) + ".xml";
                    f = new File(directory + newFileName);
                    f.createNewFile();
                    JOptionPane.showMessageDialog(frame, "Can not modifty this file. Creating a new file: " + newFileName,
                            "Can not modify file", JOptionPane.WARNING_MESSAGE);
                } else {
                    int x = JOptionPane.showOptionDialog(frame, "This file already exists. "
                            + "Would you like to overwrite the file or append to it?",
                            "Would you like to overwrite?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, new String[]{"Overwite", "Append"},
                            "Append");
                    shouldOverwrite = (x == JOptionPane.YES_OPTION);
                    if (x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                }
            } else {
                f.createNewFile();
            }

            FileWriter fw;

            if (!shouldOverwrite) {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                StringBuilder string = new StringBuilder();

                String lineToRemove = "</Events>";
                String currentLine;

                while ((currentLine = reader.readLine()) != null) {
                    // trim newline when comparing with lineToRemove
                    String trimmedLine = currentLine.trim();
                    if (trimmedLine.equals(lineToRemove)) {
                        continue;
                    }
                    string.append(currentLine).append("\n");
                }

                reader.close();
                f.createNewFile();
                fw = new FileWriter(f);
                fw.append(string.toString());
            } else {
                fw = new FileWriter(f);
                fw.append("<Events>\n");
            }
            for (Terrain t : creatingTerrainList) {
                fw.append(t.export());
            }
            fw.append("</Events>");
            fw.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error: could not create file " + filename,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void startCreatingTerrain() {
        isCreatingTerrain = true;
    }

    public void endCreatingTerrain() {
        isCreatingTerrain = false;
    }

    public boolean isCreatingTerrain() {
        return isCreatingTerrain;
    }

    /**
     * Returns true if the event was a valid shape, false otherwise.
     */
    public boolean stopDrawingCurrentEvent() {
        isCreatingEvent = false;
        List<Vector2D> vertList = new LinkedList<Vector2D>();
        for (LatLon l : newEventLatLonList) {
            vertList.add(Logic2D.latLonToVector2D(l));
        }
        if (Polygon2D.validateVertices(Logic2D.centerVertices(vertList))) {
            return true;
        } else {
            Collections.reverse(vertList);
            if (Polygon2D.validateVertices(Logic2D.centerVertices(vertList))) {
                return true;
            }
        }
        if (prevEventShape != null) {
            drawingLayer.removeRenderable(prevEventShape);
        }
        newEventLatLonList = new LinkedList<LatLon>();
        didLastEventFail = true;
        prevEventShape = null;
        return false;
    }

    /**
     * Returns true if the terrain was a valid shape, false otherwise.
     */
    public boolean stopDrawingCurrentTerrain() {
        isCreatingTerrain = false;
        List<Vector2D> vertList = new LinkedList<Vector2D>();
        for (LatLon l : newTerrainLatLonList) {
            vertList.add(Logic2D.latLonToVector2D(l));
        }
        if (Polygon2D.validateVertices(Logic2D.centerVertices(vertList))) {
            return true;
        } else {
            Collections.reverse(vertList);
            if (Polygon2D.validateVertices(Logic2D.centerVertices(vertList))) {
                return true;
            }
        }
        if (prevTerrainShape != null) {
            drawingLayer.removeRenderable(prevTerrainShape);
        }
        newTerrainLatLonList = new LinkedList<LatLon>();
        didLastTerrainFail = true;
        prevTerrainShape = null;
        return false;
    }

    public boolean isCreatingPath() {
        return isCreatingPath;
    }

    public void startCreatingPath() {
        isCreatingPath = true;
    }

    public boolean endCreatingPath() {
        isCreatingPath = false;
        if (doneCreatingPath() == false) {
            isCreatingPath = true;
            return false;
        }
        return true;
    }

    //If there is already one point set, then we are setting the end point
    public boolean isEndingPathPoint() {
        return newPathLatLonList.size() == 2;
    }

    public boolean doneCreatingPath() {
        if (newPathLatLonList.size() != 2) {
            return false;
        }
        try {
            pathStartPoint = Logic2D.latLonToVector2D(newPathLatLonList.get(0));
            pathEndPoint = Logic2D.latLonToVector2D(newPathLatLonList.get(1));
            SurfacePolyline pathLine = new SurfacePolyline(lineAttr, newPathLatLonList);
            if (prevPathLine != null) {
                pathLayer.removeRenderable(prevPathLine);
            }
            pathLayer.addRenderable(pathLine);
            prevPathLine = pathLine;
            isCreatingPath = false;
            newPathLatLonList = new LinkedList<LatLon>();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void drawNewPath(LatLon currentPoint) {
        if (currentPoint == null) {
            return;
        }
        List<LatLon> drawList = new LinkedList<LatLon>();
        for (LatLon l : newPathLatLonList) {
            drawList.add(l);
        }

        drawList.add(currentPoint);
        SurfacePolyline pathLine = new SurfacePolyline(lineAttr, drawList);
        if (prevPathLine != null) {
            pathLayer.removeRenderable(prevPathLine);
        }
        if (pathLine != null) {
            pathLayer.addRenderable(pathLine);
            prevPathLine = pathLine;
        }
    }

    public void makeNewPathPoint(LatLon point) {
        if (point == null) {
            return;
        }
        newPathLatLonList.add(point);
    }

    public void makeNewTerrainPoint(LatLon point) {
        if (point == null) {
            return;
        }
        didLastTerrainFail = false;
        newTerrainLatLonList.add(point);
    }

    public void drawNewTerrain(LatLon currentPoint) {
        if (currentPoint == null) {
            return;
        }
        List<LatLon> drawList = new LinkedList<LatLon>();
        for (LatLon l : newTerrainLatLonList) {
            drawList.add(l);
        }
        drawList.add(currentPoint);
        SurfacePolygon terrainShape = new SurfacePolygon(polyAttr, drawList);
        if (prevTerrainShape != null) {
            drawingLayer.removeRenderable(prevTerrainShape);
        }
        if (terrainShape != null) {
            drawingLayer.addRenderable(terrainShape);
            prevTerrainShape = terrainShape;
        }
    }

    public boolean doneCreatingTerrain(TerrainType terrainType, String description) {
        SurfacePolygon terrainShape = new SurfacePolygon(polyAttr, newTerrainLatLonList);
        if (prevTerrainShape != null) {
            drawingLayer.removeRenderable(prevTerrainShape);
        }
        if (terrainType == null) {
            newTerrainLatLonList = new LinkedList<LatLon>();
            return false;
        }
        drawingLayer.addRenderable(terrainShape);
        prevTerrainShape = null;
        isCreatingTerrain = false;
        List<Vector2D> vertList = new LinkedList<Vector2D>();
        for (LatLon l : newTerrainLatLonList) {
            vertList.add(Logic2D.latLonToVector2D(l));
        }
        newTerrainLatLonList = new LinkedList<LatLon>();
        if (Polygon2D.validateVertices(Logic2D.centerVertices(vertList))) {
            Terrain terrain = new Terrain(new Polygon2D(Logic2D.getCentroid(vertList), Logic2D.centerVertices(vertList)), terrainType, description);
            creatingTerrainList.add(terrain);
        } else {
            Collections.reverse(vertList);
            if (Polygon2D.validateVertices(Logic2D.centerVertices(vertList))) {
                Terrain terrain = new Terrain(new Polygon2D(Logic2D.getCentroid(vertList), Logic2D.centerVertices(vertList)), terrainType, description);
                creatingTerrainList.add(terrain);
            } else {
                drawingLayer.removeRenderable(terrainShape);
                didLastTerrainFail = true;
                return false;
            }
        }
        return true;
    }

    public boolean didLastTerrainFail() {
        return didLastTerrainFail;
    }

    public void startTerrainList() {
        creatingTerrainList = new ArrayList<Terrain>();
    }

    //0 Saves to a file, 
    public void endTerrainList(int userChoice) {

        drawingLayer.removeAllRenderables();

        if (userChoice == 0) { //0-save locally
            String filename = (String) JOptionPane.showInputDialog(
                    frame,
                    "Please specify a file name:\n",
                    "Local Options",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "terrains.xml");
            saveTerrainLocally(filename);
        }
//        else if (userChoice == 1)//User cancelled the save
//        {
//            tempTerrainLayer.removeAllRenderables();
//        }
        //Just clear everything out and go home

        creatingTerrainList = new ArrayList<Terrain>();

    }

    public void saveTerrainLocally(String filename) {

        wwd.redraw();

        String directory = gui.getTerrainSaveDirectory();
        if (directory.endsWith("."))//If there is for some reason a period at the end, trim it
        {
            directory = directory.substring(0, directory.length() - 1);
        }
     //   System.out.println("Directory: " + directory);
        File f = new File(directory + filename);


        try {
            boolean shouldOverwrite = true;

            if (f.exists()) {
                if (!f.canWrite()) {
                    String rand = Math.random() + "";
                    String newFileName = filename.replaceAll(".xml", "") + (rand.substring(2)) + ".xml";
                    f = new File(directory + newFileName);
                    f.createNewFile();
                    JOptionPane.showMessageDialog(frame, "Can not modifty this file. Creating a new file: " + newFileName,
                            "Can not modify file", JOptionPane.WARNING_MESSAGE);
                } else {
                    int x = JOptionPane.showOptionDialog(frame, "This file already exists. "
                            + "Would you like to overwrite the file or append to it?",
                            "Would you like to overwrite?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, new String[]{"Overwite", "Append"},
                            "Append");
                    shouldOverwrite = (x == JOptionPane.YES_OPTION);
                    if (x == JOptionPane.CLOSED_OPTION || x == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                }
            } else {
                f.createNewFile();
            }

            FileWriter fw;

            if (!shouldOverwrite) {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                StringBuilder string = new StringBuilder();

                String lineToRemove = "</Terrains>";
                String currentLine;

                while ((currentLine = reader.readLine()) != null) {
                    // trim newline when comparing with lineToRemove
                    String trimmedLine = currentLine.trim();
                    if (trimmedLine.equals(lineToRemove)) {
                        continue;
                    }
                    string.append(currentLine).append("\n");
                }

                reader.close();
                f.createNewFile();
                fw = new FileWriter(f);
                fw.append(string.toString());
            } else {
                fw = new FileWriter(f);
                fw.append("<Terrains>\n");
            }
            for (Terrain t : creatingTerrainList) {
                fw.append(t.export());
            }
            fw.append("</Terrains>");
            fw.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error: could not create file " + filename,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Model getModel() {
        return mainViewModel;
    }

    /**
     * Parses an OSM file and loads it into the director.
     * @param f An OSM file which contains road data.
     * @param speedLimitMap A map of speed limits. Could be null.
     * @param shouldOverwrite True if data should be overwritten, false otherwise
     */
    public void loadRoads(File f, Map<String, Double> speedLimitMap, boolean shouldOverwrite) {
        RoadParser parser = new RoadParser(speedLimitMap);
        String msg = "";
        String msgTitle = "";
        int msgType = JOptionPane.INFORMATION_MESSAGE;
        try {
            List<String> log = OSMParser.parse(parser, new FileReader(f));
            if (parser.extractRoads().isEmpty()) {
                throw new PFParseLogException(LOG_NOTHING_PARSED);
            }
            if (log.isEmpty()) {
                msgTitle = "Success";
                msg = "Parsing was successful!";
            } else {
                msgTitle = "Warning";
                msg = "One or more warnings occured during the parsing process.\n"
                        + "The warning(s) listed below are in chronological order.";
                for (int i = 0; i < log.size(); i++) {
                    msg += "\n" + (i + 1) + ") " + log.get(i);
                }
                msgType = JOptionPane.WARNING_MESSAGE;
            }
            if (shouldOverwrite || roads == null) {
                roads = parser.extractRoads();
            } else {
                roads.addAll(parser.extractRoads());
            }
        } catch (PFParseLogException ex) {
            msgTitle = "Error";
            msg = "An error occured during the parsing process. Nothing was parsed.\n"
                    + "The error and any warnings found below are in chronological order.";
            List<String> log = ex.getLog();
            for (int i = 0; i < log.size(); i++) {
                msg += "\n" + (i + 1) + ") " + log.get(i);
            }
            msgType = JOptionPane.ERROR_MESSAGE;
        } catch (Exception ex) {
            msgTitle = "Error";
            msg = "A fatal error occured during the parsing process. Nothing was parsed.\n"
                    + "Please make sure the osm file is in the correct format.";
            msg += "\nError: " + (ex.toString() == null ? "Unknown" : ex.getMessage());
            msgType = JOptionPane.ERROR_MESSAGE;
        } catch (Error er)  {
            msgTitle = "Error";
            msg = "A fatal error occured during the parsing process. Nothing was parsed.\n"
                    + "Please make sure all of Pathfinder's dependencies are in the proper location.";
            msg += "\nError: " + (er.toString() == null ? "Unknown" : er.getMessage());
            msgType = JOptionPane.ERROR_MESSAGE;
        } finally {
            JOptionPane.showMessageDialog(frame, msg, msgTitle, msgType);
        }
    }

    /**
     * Throw the correct exception of the XML sub parser did not find data.
     */
    private void checkEmpty(XMLSubParser parser) throws PFParseLogException {
        if (parser instanceof TerrainParser) {
            TerrainParser p = (TerrainParser) parser;
            if (p.extractTerrains().isEmpty()) {
                throw new PFParseLogException(LOG_NOTHING_PARSED);
            }
        } else if (parser instanceof EventParser) {
            EventParser p = (EventParser) parser;
            if (p.extractEvents().isEmpty()) {
                throw new PFParseLogException(LOG_NOTHING_PARSED);
            }
        } else if (parser instanceof UnderpassParser) {
            UnderpassParser p = (UnderpassParser) parser;
            if (p.extractUnderpasses().isEmpty()) {
                throw new PFParseLogException(LOG_NOTHING_PARSED);
            }
        } else if (parser instanceof SpeedLimitParser) {
            SpeedLimitParser p = (SpeedLimitParser) parser;
            if (p.extractSpeedLimits().isEmpty()) {
                throw new PFParseLogException(LOG_NOTHING_PARSED);
            }
        } else if (parser instanceof VehicleParser) {
            VehicleParser p = (VehicleParser) parser;
            if (p.extractVehicles().isEmpty()) {
                throw new PFParseLogException(LOG_NOTHING_PARSED);
            }
        }
    }

    private void loadXMLFile(File file, XMLSubParser parser) {
        String msg = "";
        String msgTitle = "";
        int msgType = JOptionPane.INFORMATION_MESSAGE;
        try {
            List<String> log = XMLParser.parse(parser, new FileReader(file));
            checkEmpty(parser);
            if (log.isEmpty()) {
                msgTitle = "Success";
                msg = "Parsing was successful!";
            } else {
                msgTitle = "Warning";
                msg = "One or more warnings occured during the parsing process.\n"
                        + "The warning(s) listed below are in chronological order.";
                for (int i = 0; i < log.size(); i++) {
                    msg += "\n" + (i + 1) + ") " + log.get(i);
                }
                msgType = JOptionPane.WARNING_MESSAGE;
            }
        } catch (PFParseLogException ex) {
            msgTitle = "Error";
            msg = "An error occured during the parsing process. Nothing was parsed.\n"
                    + "The error and any warnings found below are in chronological order.";
            List<String> log = ex.getLog();
            for (int i = 0; i < log.size(); i++) {
                msg += "\n" + (i + 1) + ") " + log.get(i);
            }
            msgType = JOptionPane.ERROR_MESSAGE;
        } catch (Exception ex) {
            msgTitle = "Error";
            msg = "A fatal error occured during the parsing process. Nothing was parsed.\n"
                    + "Please make sure the xml file is in the correct format.";
            msg += "\nError: " + (ex.toString() == null ? "Unknown" : ex.getMessage());
            msgType = JOptionPane.ERROR_MESSAGE;
        } catch (Error er)  {
            msgTitle = "Error";
            msg = "A fatal error occured during the parsing process. Nothing was parsed.\n"
                    + "Please make sure all of Pathfinder's dependencies are in the proper location.";
            msg += "\nError: " + (er.toString() == null ? "Unknown" : er.getMessage());
            msgType = JOptionPane.ERROR_MESSAGE;
        } finally {
            JOptionPane.showMessageDialog(frame, msg, msgTitle, msgType);
        }
    }

    /**
     * Parses Terrain XML file and loads the terrain into the Director and
     * WorldWind
     * @param f An XML file which contains terrain data.
     * @param shouldOverwrite True if data should be overwritten, false otherwise
     */
    public void loadTerrains(File f, boolean shouldOverwrite) {

        TerrainParser parser = new TerrainParser();
        loadXMLFile(f, parser);
        if (shouldOverwrite || terrains == null) {
            terrains = parser.extractTerrains();
            terrainLayer.removeAllRenderables();
        } else {
            terrains.addAll(parser.extractTerrains());
        }
        terrainLayer.removeAllRenderables();
        for (Terrain t : terrains) {
            t.addToLayer(terrainLayer);
        }

        wwd.redraw();
        return;
    }

    /**
     * Parses Event XML file and loads the terrain into the Director and
     * WorldWind
     * @param f An XML file which contains event data
     * @param shouldOverwrite True if data should be overwritten, false otherwise
     */
    public void loadEvents(File f, boolean shouldOverwrite) {

        EventParser parser = new EventParser();
        loadXMLFile(f, parser);
        events = parser.extractEvents();
        if (shouldOverwrite || events == null) {
            events = parser.extractEvents();
            eventLayer.removeAllRenderables();
        } else {
            events.addAll(parser.extractEvents());
        }
        eventLayer.removeAllRenderables();
        for (Event e : events) {
            e.addToLayer(eventLayer);
        }
        wwd.redraw();
        return;
    }

    /**
     * Parses Event XML file and loads the Speed Limits into the Director
     * @param f An XML file with Speed Limit data
     * @param shouldOverwrite True if data should be overwritten, false otherwise
     */
    public void loadSpeedlimits(File f, boolean shouldOverwrite) {
        SpeedLimitParser parser = new SpeedLimitParser();
        loadXMLFile(f, parser);
        if (shouldOverwrite || speedLimits == null) {
            speedLimits = parser.extractSpeedLimits();
        } else {
            speedLimits.addAll(parser.extractSpeedLimits());
        }
        return;
    }

    /**
     * Parses an Underpass XML file and loads the Underpasses into the Director
     * @param f An XML file with Underpass data.
     * @param shouldOverwrite True if data should be overwritten, false otherwise
     */
    public void loadUnderpasses(File f, boolean shouldOverwrite) {
        UnderpassParser parser = new UnderpassParser();
        loadXMLFile(f, parser);

        if (shouldOverwrite || underpasses == null) {
            underpasses = parser.extractUnderpasses();
        } else {
            underpasses.addAll(parser.extractUnderpasses());
        }

        for (Underpass u : underpasses) {
            u.addToLayer(underpassLayer);
        }
        wwd.redraw();
        return;
    }

    /**
     * Parses a Vehicle XML file and loads the Vehicles into the Director
     * @param f An XML file with Vehicle data.
     * @param shouldOverwrite True if data should be overwritten, false otherwise
     */
    public void loadVehicles(File f, boolean shouldOverwrite) {
        VehicleParser parser = new VehicleParser();
        loadXMLFile(f, parser);
        if (shouldOverwrite || vehicles == null) {
            vehicles = parser.extractVehicles();
        } else {
            vehicles.addAll(parser.extractVehicles());
        }
        return;
    }

    /**
     * Calls on the Transformer and Pathfinder methods to find the paths.
     * @param mode The mode of travel. Could be Road Only, Terrain Only, or Both.
     * @param safety The safety coefficient.
     * @param speed The speed coefficient.
     * @param distance The distance coefficient.
     * @param start The start location of the path.
     * @param end The end location of the path.
     * @param vehicles The vehicles to be used in this path
     * @param frame The overall frame of the Main GUI
     */
    public void findAndDrawPaths(Transformer.TransformMode mode, double safety,
            double speed, double distance, Set<Vehicle> vehicles) {

        if (mode == Transformer.TransformMode.ROAD_ONLY) {
            if (roads.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "No Road File Loaded.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

        } else if (mode == Transformer.TransformMode.TERRAIN_ONLY) {
            if (terrains.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "No Terrain File Loaded.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            if (roads.isEmpty() || terrains.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "No Road File or Terrain File Loaded.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        clearPathLayer();

        try {
            List<Traversable> trav = Transformer.transform(roads, events,
                    speedLimits, terrains, underpasses, vehicles, safety,
                    speed, distance, pathStartPoint, pathEndPoint, mode);
            List<Path> paths = Pathfinder.getPaths(trav.get(0), trav.get(1));
            if (paths.isEmpty()) {    
                JOptionPane.showMessageDialog(frame, "No paths could be found! Please make sure the start and end points are connected via loaded roads and/or loaded terrains such that it is possible to traverse from start to end with every selected vehicle.", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {

                Vector2D start = Logic2D.getStart(trav.get(0).getSegment());
                Vector2D end = Logic2D.getStart(trav.get(1).getSegment());
                SurfaceIcon startIcon = new SurfaceIcon(
                        getWhiteTransparentBufferedImage("org/rowan/pathfinder/images/start.png"),
                        Logic2D.vector2DToLatLon(start));
                SurfaceIcon endIcon = new SurfaceIcon(
                        getWhiteTransparentBufferedImage("org/rowan/pathfinder/images/end.png"),
                        Logic2D.vector2DToLatLon(end));
                startIcon.setMinSize(50);
                startIcon.setOpacity(.7);
                endIcon.setMinSize(50);
                endIcon.setOpacity(.7);
                for (int i = paths.size() - 1; i >= 0; i--) {
                    paths.get(i).draw(pathLayer, i);
                }
                
                pathLayer.addRenderable(endIcon);
                pathLayer.addRenderable(startIcon);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error: " + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            wwd.redraw();
        }
    }

    public void clearPathLayer() {
        pathLayer.removeAllRenderables();
    }

    /**
     * Returns the events loaded in Director.
     * @return A set of Events
     */
    public Set<Event> getEvents() {
        return events;
    }

    /**
     * Returns the roads loaded into the Director.
     * @return A set of Roads
     */
    public Set<RoadSegment> getRoads() {
        return roads;
    }

    /**
     * Returns the speed limits loaded into the Director.
     * @return A set of Speed Limits
     */
    public Set<SpeedLimit> getSpeedLimits() {
        return speedLimits;
    }

    /**
     * Returns the terrains loaded into the Director.
     * @return A set of Terrains
     */
    public Set<Terrain> getTerrains() {
        return terrains;
    }

    /**
     * Returns the underpasses loaded into the Director.
     * @return A set of Underpasses
     */
    public Set<Underpass> getUnderpasses() {
        return underpasses;
    }

    /**
     * Returns the Vehicles loaded into the Director.
     * @return A set of Vehicles
     */
    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * This method is called when the user clicks the event received notification, 
     * which empties the queue into the databaseEventLayer and redraws
     */
    public void drawNewlyReceivedEvents() {

        //System.out.println("CLIENT THREAD INVOKING EVENT DRAWING");
      //  System.out.println(receivedEventQueue.size());
        int size = receivedEventQueue.size();
        for(int i = 0; i < size; i++){
            receivedEventQueue.remove(0).addToLayer(databaseEventLayer);
        }
        wwd.redraw();
    }
    
    /**
     * This method is used to add a newly received event into the "waiting to be drawn" queue
     * @param e 
     */
    public void addNewEventToQueue(Event e){
        receivedEventQueue.add(e);
    }
    
    private BufferedImage getWhiteTransparentBufferedImage(String imageLocation) throws IOException {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(imageLocation);
        Image image = ImageIO.read(is);
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        RGBImageFilter filter = new RGBImageFilter() {

            public int whiteRGB = Color.WHITE.getRGB() | 0xFF000000;

            @Override
            public int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == whiteRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        ImageProducer ip = new FilteredImageSource(bi.getSource(), filter);
        Image createImage = Toolkit.getDefaultToolkit().createImage(ip);
        BufferedImage bi2 = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi2.createGraphics();
        g2.drawImage(createImage, 0, 0, null);
        g2.dispose();
        return bi2;
    }
    
    public String getDatabaseTable() {
        return databaseTable;
    }

    public void setDatabaseTable(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    public MainGUI getGui() {
        return gui;
    }

    public boolean shouldShowAlert() {
        return shouldShowAlert;
    }

    public void setShouldShowAlert(boolean shouldShowAlert) {
        this.shouldShowAlert = shouldShowAlert;
    }

   

   
}
