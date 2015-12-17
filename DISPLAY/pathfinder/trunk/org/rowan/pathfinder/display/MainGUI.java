package org.rowan.pathfinder.display;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.applications.worldwindow.core.ToolTipAnnotation;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * Copyright (C) 2001, 2011 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
/**
 * Provides a base application framework for displaying WorldWind.
 *
 * @version $Id: MainGUI.java 1 2011-07-16 23:22:47Z dcollins $
 * @edited_by Jon Schuff, Dan Urbano
 */
public class MainGUI {

    private MainGUI gui;
    private WorldWindowGLJPanel wwd;
    private Director director;
    private int customizationPanelWidth = 150;
    private int customizationPanelHeight = 600;
    private int alertPanelWidth = 900;
    private int alertPanelHeight = 20;
    private int glCanvasPanelWidth = 800;
    private int glCanvasPanelHeight = 600;
    protected static JFrame frame = new JFrame("Pathfinder Tool v1.0");
    public static final String DEFAULT_EVENTTABLE_NAME = "events";
    private volatile boolean isSettingDistance = false;
    private volatile boolean isSettingSpeed = false;
    private volatile boolean isSettingSafety = false;
    private volatile boolean isDistanceLocked = false;
    private volatile boolean isSpeedLocked = false;
    private volatile boolean isSafetyLocked = false;
    private JLabel distanceLock;
    private JLabel speedLock;
    private JLabel safetyLock;
    private final JPanel innerDistancePanel = new JPanel();
    private final JPanel innerSpeedPanel = new JPanel();
    private final JPanel innerSafetyPanel = new JPanel();
    private static final String IMAGE_PATH = "org/rowan/pathfinder/images/";
    private static final Icon iconLock = getWhiteAsZeroAlpha(getImageIcon(IMAGE_PATH + "locked.gif", 20, 20), 20, 20);
    private static final Icon iconUnlock = getWhiteAsZeroAlpha(getImageIcon(IMAGE_PATH + "unlocked.gif", 20, 20), 20, 20);
    private Border etchedLoweredBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    private JRadioButton pathRadioButton;
    private JRadioButton terrainRadioButton;
    private JRadioButton eventRadioButton;
    private final JButton calculateButton = new JButton("Calculate Path");
    private JButton pathButton;
    public static JButton terrainButton;
    public static JButton eventButton;
    private JLabel statusLabel;
    private JLabel alertLabel;
    private String terrainSaveDirectory;
    private String eventSaveDirectory;
    private String databaseIP = "127.0.0.1";
    private JMenuItem loadEventsOption;
    private JMenuItem connectToServerOption;

    /**
     * Create a new innerCustomizationPanel given the size of the canvas and a boolean
     * declaring whether or not to include the status bar.
     * @param canvasSize A Dimension representing the size of the canvas.
     * @param includeStatusBar true if the status bar is to be included.
     */
    public MainGUI() {
        gui = this;

        // Create the WorldWind innerCustomizationPanel according to the specified canvas size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.wwd = this.createWorldWindow();
        this.wwd.setPreferredSize(new Dimension(glCanvasPanelWidth, glCanvasPanelHeight));

        // Create the default model as described in the current worldwind properties.
        Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        director = new Director(this, m, wwd, frame);
        this.wwd.setModel(m);

        // Setup a select listener for the worldmap click-and-go feature
        this.wwd.addSelectListener(new ClickAndGoSelectListener(getWwd(), WorldMapLayer.class));

        // Add the components to the application window
        frame.add(this.wwd, BorderLayout.CENTER);
        
        terrainSaveDirectory = eventSaveDirectory = new File(".").getAbsolutePath();
        
        initialize();
    }

    /**
     * Create the WorldWind window.
     * @return The created window.
     */
    protected final WorldWindowGLJPanel createWorldWindow() {
        return new WorldWindowGLJPanel();
    }

    /**
     * Return the WorldWind canvas.
     * @return The WorldWind canvas.
     */
    public WorldWindowGLJPanel getWwd() {
        return wwd;
    }

    /**
     * Initialize the application frame with the following properties.
     * @param includeStatusBar true to include the status bar.
     * @param includeLayerPanel true to include to the layer innerCustomizationPanel.
     * @param includeStatsPanel true to include the statistics innerCustomizationPanel.
     */
    protected final void initialize() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;

        // Create File menu
        menu = new JMenu("File");

        connectToServerOption = new JMenuItem("Connect to Database Server");
        connectToServerOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                director.connectToServer();
                if(director.isConnectedToServer()){// SUCCESS, enable our Load Events option
                    connectToServerOption.setEnabled(false);
                    loadEventsOption.setEnabled(true);//disabled until the server is connected
                    JOptionPane.showMessageDialog(director.getFrame(),
                        "Connected to Server.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                    
            }
        });
        menu.add(connectToServerOption);
        
        loadEventsOption = new JMenuItem("Load Events from Database");
        loadEventsOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = JOptionPane.showInputDialog(frame, "What table would you like to load event(s) from?", director.getDatabaseTable());
                if (tableName != null) {
                    if (tableName.trim().isEmpty()) {
                        tableName = DEFAULT_EVENTTABLE_NAME;
                    }
                    director.setShouldShowAlert(false);
                    try {
                        director.loadEventsFromDatabase(tableName);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Unable to load events from database!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    try {
                        Thread.sleep(2000);
                    } catch(InterruptedException ex) {
                        //ignroed
                    }
                    director.drawNewlyReceivedEvents();
                    director.setShouldShowAlert(true);
                }
            }
        });
        loadEventsOption.setEnabled(false);//disabled until the server is connected
        
        menu.add(loadEventsOption);
        
        menuItem = new JMenuItem("Preferences");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.getInstance(gui, director).setVisible(true);
            }
        });
        menu.add(menuItem);
        
        

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        // Create Layers menu
        menu = new JMenu("Layers");
        createLayersMenu(menu);
        menuBar.add(menu);//Add Layers menu to menu bar

        // Create Parse menu
        menu = new JMenu("Parse");
        menuItem = new JMenuItem("Open Parse Window");
        menuItem.addActionListener(getParseActionListener());
        menu.add(menuItem);
        menuBar.add(menu);//Add Parse menu to menu bar

        //Create Help menu
        menu = new JMenu("Help");

        menuItem = new JMenuItem("About");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Path Finder Tool\n" +
                        "Created in the SEGV lab in the Rowan University CS Department\n" +
                        "Part of the MSE - Rowan Collaboration\n" +
                        "Authors: Shahid Akhter, Jon Schuff, Dan Urbano",
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);//Add Help menu to menu bar

        // Add menubar to the content pane
        frame.getContentPane().add(menuBar, BorderLayout.PAGE_START);

        addCustomizationSidebar(BorderLayout.LINE_END);//BUILD CUSTOMIZATION BAR

        addAlertBottomBar();

        // Create and install the view controls layer and register a controller for it with the World Window.
        ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
        insertBeforeCompass(getWwd(), viewControlsLayer);
        this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

        // Register a rendering exception listener that's notified when exceptions occur during rendering.
        this.getWwd().addRenderingExceptionListener(new RenderingExceptionListener() {

            @Override
            public void exceptionThrown(Throwable t) {
                if (t instanceof WWAbsentRequirementException) {
                    String message = "Computer does not meet minimum graphics requirements.\n";
                    message += "Please install up-to-date graphics driver and try again.\n";
                    message += "Reason: " + t.getMessage() + "\n";
                    message += "This program will end when you press OK.";

                    JOptionPane.showMessageDialog(MainGUI.frame, message, "Unable to Start Program",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                }
            }
        });

        this.getWwd().getInputHandler().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == 1) {
                        if (director.isCreatingEvent()) {
                            director.makeNewEventPoint(wwd.getCurrentPosition());
                        
                            e.consume();
                            eventButton.setEnabled(false);
                        } else if (director.isCreatingTerrain()) {
                            director.makeNewTerrainPoint(wwd.getCurrentPosition());
                          
                            e.consume();
                            terrainButton.setEnabled(false);
                        } else if (director.isCreatingPath()) {
                            director.makeNewPathPoint(wwd.getCurrentPosition());
                            if (director.isEndingPathPoint()) {
                                calculateButton.setEnabled(true);
                                pathButton.setEnabled(true);
                                pathButton.doClick();
                            }
                            e.consume();
                        }
                    } else if (e.getClickCount() == 2) {
                        if (director.isCreatingEvent()) {
                            if (!director.stopDrawingCurrentEvent()) {
                                JOptionPane.showMessageDialog(frame,
                                "Can't create the event: it must be a convex shape.",
                                "Can't Create Event", JOptionPane.WARNING_MESSAGE);
                                director.startCreatingEvent();
                            } else {
                                //Note: this guy will end the current terrain and 
                                //start the new creation when finished
                              
                                EventDetailsDialog.getInstance(frame, director).setVisible(true);
                               
                            }
                        
                            e.consume();
                            eventButton.setEnabled(true);
                        } else if (director.isCreatingTerrain()) {
                            if (!director.stopDrawingCurrentTerrain()) {
                                JOptionPane.showMessageDialog(frame,
                                "Can't create the terrain: it must be a convex shape.",
                                "Can't Create Terrain", JOptionPane.WARNING_MESSAGE);
                                director.startCreatingTerrain();
                            } else {
                                //Note: this guy will end the current terrain and 
                                //start the new creation when finished
                                TerrainDetailsDialog.getInstance(frame, director).setVisible(true);
                            }
                         
                            e.consume();
                            terrainButton.setEnabled(true);
                        } else if (director.isCreatingPath()) {
                            director.makeNewPathPoint(wwd.getCurrentPosition());
                            if (director.isEndingPathPoint()) {
                                //pathButton.setEnabled(true);
                                //pathButton.doClick();
                            }
                            e.consume();
                        }
                    }
                }
            }
        });


        this.getWwd().addPositionListener(new PositionListener() {

            @Override
            public void moved(PositionEvent event) {
                if (director.isCreatingEvent()) {
                    director.drawNewEvent(wwd.getCurrentPosition());
                }
                if (director.isCreatingTerrain()) {
                    director.drawNewTerrain(wwd.getCurrentPosition());
                }
                if (director.isCreatingPath()) {
                    director.drawNewPath(wwd.getCurrentPosition());
                }
            }
        });

        this.getWwd().addSelectListener(new SelectListener() {

            Object lastHoverObject = null;
            AnnotationLayer layer = new AnnotationLayer();
            ToolTipAnnotation annotation = new ToolTipAnnotation("");
            LayerList layers = director.getModel().getLayers();

            @Override
            public void selected(SelectEvent event) {
                if (event.getEventAction().equals(SelectEvent.HOVER)
                        && event.getTopObject() instanceof SurfaceShape) {
                    AVList detailList = (AVList) event.getTopObject();
                    String str = detailList.getStringValue("Details");
                    if (str != null) {
                        lastHoverObject = event.getTopObject();
                        annotation.setText(str);
                        annotation.setScreenPoint(event.getPickPoint());
                        layer.removeAllAnnotations();
                        layer.addAnnotation(annotation);
                        layers.add(layer);
                    }

                }

                if (event.getEventAction().equals(SelectEvent.HOVER)
                        && !(event.getTopObject() instanceof SurfaceShape)) {

                    if (layers.contains(layer)) {
                        layers.remove(layer);
                        wwd.redraw();
                        //  lastHoverObject == null;
                    }
                }
            }
        });

        // Assure valid layout
        postFrame();

        // Center the application on the screen and allow resizing
        WWUtil.alignComponent(null, frame, AVKey.CENTER);
        frame.setMinimumSize(new Dimension(800, 680));
        frame.setResizable(true);
    }

    private void addCustomizationSidebar(String guiArea) {

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.PAGE_AXIS));
        JPanel innerTopPanel, innerCenterPanel, innerBottomPanel;
        JPanel innerInnerPanel;
        JLabel label;
        final JSlider distanceSlider = new JSlider(JSlider.VERTICAL, 0, 100, 34);
        final JSlider speedSlider = new JSlider(JSlider.VERTICAL, 0, 100, 33);
        final JSlider safetySlider = new JSlider(JSlider.VERTICAL, 0, 100, 33);
        distanceSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        speedSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        safetySlider.setAlignmentX(Component.LEFT_ALIGNMENT);

        outerPanel.setPreferredSize(new Dimension(customizationPanelWidth, customizationPanelHeight));

        //Generate and add the CENTER inner panel
        innerCenterPanel = new JPanel(new BorderLayout());
        innerCenterPanel.setPreferredSize(new Dimension(customizationPanelWidth, 100));
        innerCenterPanel.setMaximumSize(new Dimension(customizationPanelWidth, 100));
        innerCenterPanel.setBorder(etchedLoweredBorder);
        innerInnerPanel = new JPanel(new GridLayout(3, 2));

        label = new JLabel("Distance");
        innerInnerPanel.add(label);
        final JTextField distanceBox = new JTextField("34");
        distanceBox.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                String typed = distanceBox.getText();
                int newValue;
                if (!typed.matches("\\d+") || typed.length() > 3) {
                    newValue = 100 - speedSlider.getValue() - safetySlider.getValue();
                    distanceBox.setText("" + newValue);
                } else {
                    newValue = Integer.parseInt(typed);
                }
                if (newValue < 0) {
                    newValue = 0;
                }
                if (newValue > 100) {
                    newValue = 100;
                }
                set(distanceSlider, speedSlider, safetySlider,
                        distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                        newValue, null, null);
            }
        });
        distanceBox.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {
                String typed = distanceBox.getText();
                int newValue;
                if (!typed.matches("\\d+") || typed.length() > 3) {
                    newValue = 100 - speedSlider.getValue() - safetySlider.getValue();
                    distanceBox.setText("" + newValue);
                } else {
                    newValue = Integer.parseInt(typed);
                }
                if (newValue < 0) {
                    newValue = 0;
                }
                if (newValue > 100) {
                    newValue = 100;
                }
                set(distanceSlider, speedSlider, safetySlider,
                        distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                        newValue, null, null);
            }
        });
        distanceLock = new JLabel("", iconUnlock, JLabel.CENTER);
        distanceLock.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isSpeedLocked || isSafetyLocked) {
                    return;
                }
                isDistanceLocked = !isDistanceLocked;
                distanceLock.setIcon(isDistanceLocked ? iconLock : iconUnlock);
                distanceSlider.setEnabled(isDistanceLocked ? false : true);
                distanceBox.setEnabled(isDistanceLocked ? false : true);
                if (isDistanceLocked) {

                    String typed = distanceBox.getText();
                    int newValue;
                    if (!typed.matches("\\d+") || typed.length() > 3) {
                        newValue = 100 - speedSlider.getValue() - safetySlider.getValue();
                        distanceBox.setText("" + newValue);
                    } else {
                        newValue = Integer.parseInt(typed);
                    }
                    if (newValue < 0) {
                        newValue = 0;
                    }
                    if (newValue > 100) {
                        newValue = 100;
                    }
                    isDistanceLocked = false;
                    set(distanceSlider, speedSlider, safetySlider,
                            distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                            newValue, null, null);
                    isDistanceLocked = true;

                    int maxValue = 100 - distanceSlider.getValue();
                    int newSize = Math.max(21, (int) Math.floor(200 * (maxValue / 100d))) - 20;
                    int newStrut = 200 - (newSize + 20);
                    newStrut = Math.min(newStrut, 160);

                    speedSlider.setMaximum(maxValue);
                    speedSlider.setSize(speedSlider.getWidth(), newSize);
                    for (Component c : innerSpeedPanel.getComponents()) {
                        if (!c.equals(speedSlider)) {
                            innerSpeedPanel.remove(c);
                        }
                    }
                    innerSpeedPanel.add(Box.createVerticalStrut(newStrut));
                    innerSpeedPanel.add(speedSlider);
                    innerSpeedPanel.validate();

                    safetySlider.setMaximum(maxValue);
                    Dictionary labels = safetySlider.getLabelTable();
                    labels.remove(100);
                    labels.put(new Integer(maxValue), new JLabel("Max"));
                    safetySlider.setLabelTable(labels);
                    safetySlider.setSize(safetySlider.getWidth(), newSize);
                    for (Component c : innerSafetyPanel.getComponents()) {
                        if (!c.equals(safetySlider)) {
                            innerSafetyPanel.remove(c);
                        }
                    }
                    innerSafetyPanel.add(Box.createVerticalStrut(newStrut));
                    innerSafetyPanel.add(safetySlider);
                    innerSafetyPanel.validate();
                } else {
                    speedSlider.setSize(speedSlider.getWidth(), 200);
                    speedSlider.setMaximum(100);
                    for (Component c : innerSpeedPanel.getComponents()) {
                        if (!c.equals(speedSlider)) {
                            innerSpeedPanel.remove(c);
                        }
                    }
                    innerSpeedPanel.validate();

                    safetySlider.setSize(safetySlider.getWidth(), 200);
                    safetySlider.setMaximum(100);
                    Hashtable labelTable = new Hashtable();
                    labelTable.put(new Integer(0), new JLabel("Min"));
                    labelTable.put(new Integer(100), new JLabel("Max"));
                    safetySlider.setLabelTable(labelTable);
                    for (Component c : innerSafetyPanel.getComponents()) {
                        if (!c.equals(safetySlider)) {
                            innerSafetyPanel.remove(c);
                        }
                    }
                    innerSafetyPanel.validate();
                }
            }
        });
        JPanel distanceBoxPanel = new JPanel();
        distanceBoxPanel.setLayout(new BoxLayout(distanceBoxPanel, BoxLayout.LINE_AXIS));
        distanceBoxPanel.add(distanceBox);
        distanceBoxPanel.add(distanceLock);
        innerInnerPanel.add(distanceBoxPanel);

        label = new JLabel("Speed");
        innerInnerPanel.add(label);
        final JTextField speedBox = new JTextField("33");
        speedBox.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                String typed = speedBox.getText();
                int newValue;
                if (!typed.matches("\\d+") || typed.length() > 3) {
                    newValue = 100 - distanceSlider.getValue() - safetySlider.getValue();
                    speedBox.setText("" + newValue);
                } else {
                    newValue = Integer.parseInt(typed);
                }
                if (newValue < 0) {
                    newValue = 0;
                }
                if (newValue > 100) {
                    newValue = 100;
                }
                set(distanceSlider, speedSlider, safetySlider,
                        distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                        null, newValue, null);
            }
        });
        speedBox.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {
                String typed = speedBox.getText();
                int newValue;
                if (!typed.matches("\\d+") || typed.length() > 3) {
                    newValue = 100 - distanceSlider.getValue() - safetySlider.getValue();
                    speedBox.setText("" + newValue);
                } else {
                    newValue = Integer.parseInt(typed);
                }
                if (newValue < 0) {
                    newValue = 0;
                }
                if (newValue > 100) {
                    newValue = 100;
                }
                set(distanceSlider, speedSlider, safetySlider,
                        distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                        null, newValue, null);
            }
        });
        speedLock = new JLabel("", iconUnlock, JLabel.CENTER);
        speedLock.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isDistanceLocked || isSafetyLocked) {
                    return;
                }
                isSpeedLocked = !isSpeedLocked;
                speedLock.setIcon(isSpeedLocked ? iconLock : iconUnlock);
                speedSlider.setEnabled(isSpeedLocked ? false : true);
                speedBox.setEnabled(isSpeedLocked ? false : true);
                if (isSpeedLocked) {
                    String typed = speedBox.getText();
                    int newValue;
                    if (!typed.matches("\\d+") || typed.length() > 3) {
                        newValue = 100 - distanceSlider.getValue() - safetySlider.getValue();
                        speedBox.setText("" + newValue);
                    } else {
                        newValue = Integer.parseInt(typed);
                    }
                    if (newValue < 0) {
                        newValue = 0;
                    }
                    if (newValue > 100) {
                        newValue = 100;
                    }
                    isSpeedLocked = false;
                    set(distanceSlider, speedSlider, safetySlider,
                            distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                            null, newValue, null);
                    isSpeedLocked = true;

                    int maxValue = 100 - speedSlider.getValue();
                    int newSize = Math.max(21, (int) Math.floor(200 * (maxValue / 100d))) - 20;
                    int newStrut = 200 - (newSize + 20);
                    newStrut = Math.min(newStrut, 160);

                    distanceSlider.setMaximum(maxValue);
                    distanceSlider.setSize(distanceSlider.getWidth(), newSize);
                    for (Component c : innerDistancePanel.getComponents()) {
                        innerDistancePanel.remove(c);
                    }
                    innerDistancePanel.add(Box.createVerticalStrut(newStrut));
                    innerDistancePanel.add(distanceSlider);
                    innerDistancePanel.validate();

                    safetySlider.setMaximum(maxValue);
                    Dictionary labels = safetySlider.getLabelTable();
                    labels.remove(100);
                    labels.put(new Integer(maxValue), new JLabel("Max"));
                    safetySlider.setLabelTable(labels);
                    safetySlider.setSize(safetySlider.getWidth(), newSize);
                    for (Component c : innerSafetyPanel.getComponents()) {
                        innerSafetyPanel.remove(c);
                    }
                    innerSafetyPanel.add(Box.createVerticalStrut(newStrut));
                    innerSafetyPanel.add(safetySlider);
                    innerSafetyPanel.validate();
                } else {
                    distanceSlider.setSize(distanceSlider.getWidth(), 200);
                    distanceSlider.setMaximum(100);
                    for (Component c : innerDistancePanel.getComponents()) {
                        if (!c.equals(distanceSlider)) {
                            innerDistancePanel.remove(c);
                        }
                    }
                    innerDistancePanel.validate();

                    safetySlider.setSize(safetySlider.getWidth(), 200);
                    safetySlider.setMaximum(100);
                    Hashtable labelTable = new Hashtable();
                    labelTable.put(new Integer(0), new JLabel("Min"));
                    labelTable.put(new Integer(100), new JLabel("Max"));
                    safetySlider.setLabelTable(labelTable);
                    for (Component c : innerSafetyPanel.getComponents()) {
                        if (!c.equals(safetySlider)) {
                            innerSafetyPanel.remove(c);
                        }
                    }
                    innerSafetyPanel.validate();
                }
            }
        });
        JPanel speedBoxPanel = new JPanel();
        speedBoxPanel.setLayout(new BoxLayout(speedBoxPanel, BoxLayout.LINE_AXIS));
        speedBoxPanel.add(speedBox);
        speedBoxPanel.add(speedLock);
        innerInnerPanel.add(speedBoxPanel);

        label = new JLabel("Safety");
        innerInnerPanel.add(label);
        final JTextField safetyBox = new JTextField("33");
        safetyBox.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                String typed = safetyBox.getText();
                int newValue;
                if (!typed.matches("\\d+") || typed.length() > 3) {
                    newValue = 100 - distanceSlider.getValue() - speedSlider.getValue();
                    safetyBox.setText("" + newValue);
                } else {
                    newValue = Integer.parseInt(typed);
                }
                if (newValue < 0) {
                    newValue = 0;
                }
                if (newValue > 100) {
                    newValue = 100;
                }
                set(distanceSlider, speedSlider, safetySlider,
                        distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                        null, null, newValue);
            }
        });
        safetyBox.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {
                String typed = safetyBox.getText();
                int newValue;
                if (!typed.matches("\\d+") || typed.length() > 3) {
                    newValue = 100 - distanceSlider.getValue() - speedSlider.getValue();
                    safetyBox.setText("" + newValue);
                } else {
                    newValue = Integer.parseInt(typed);
                }
                if (newValue < 0) {
                    newValue = 0;
                }
                if (newValue > 100) {
                    newValue = 100;
                }
                set(distanceSlider, speedSlider, safetySlider,
                        distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                        null, null, newValue);
            }
        });
        safetyLock = new JLabel("", iconUnlock, JLabel.CENTER);
        safetyLock.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isDistanceLocked || isSpeedLocked) {
                    return;
                }

                isSafetyLocked = !isSafetyLocked;
                safetyLock.setIcon(isSafetyLocked ? iconLock : iconUnlock);
                safetySlider.setEnabled(isSafetyLocked ? false : true);
                safetyBox.setEnabled(isSafetyLocked ? false : true);
                if (isSafetyLocked) {
                    String typed = safetyBox.getText();
                    int newValue;
                    if (!typed.matches("\\d+") || typed.length() > 3) {
                        newValue = 100 - distanceSlider.getValue() - speedSlider.getValue();
                        safetyBox.setText("" + newValue);
                    } else {
                        newValue = Integer.parseInt(typed);
                    }
                    if (newValue < 0) {
                        newValue = 0;
                    }
                    if (newValue > 100) {
                        newValue = 100;
                    }
                    isSafetyLocked = false;
                    set(distanceSlider, speedSlider, safetySlider,
                            distanceSlider.getValue(), speedSlider.getValue(), safetySlider.getValue(),
                            null, null, newValue);
                    isSafetyLocked = true;

                    int maxValue = 100 - safetySlider.getValue();
                    int newSize = Math.max(21, (int) Math.floor(200 * (maxValue / 100d))) - 20;
                    int newStrut = 200 - (newSize + 20);
                    newStrut = Math.min(newStrut, 160);

                    distanceSlider.setMaximum(maxValue);
                    distanceSlider.setSize(distanceSlider.getWidth(), newSize);
                    for (Component c : innerDistancePanel.getComponents()) {
                        innerDistancePanel.remove(c);
                    }
                    innerDistancePanel.add(Box.createVerticalStrut(newStrut));
                    innerDistancePanel.add(distanceSlider);
                    innerDistancePanel.validate();

                    speedSlider.setMaximum(maxValue);
                    speedSlider.setSize(speedSlider.getWidth(), newSize);
                    for (Component c : innerSpeedPanel.getComponents()) {
                        innerSpeedPanel.remove(c);
                    }
                    innerSpeedPanel.add(Box.createVerticalStrut(newStrut));
                    innerSpeedPanel.add(speedSlider);
                    innerSpeedPanel.validate();
                } else {
                    distanceSlider.setSize(distanceSlider.getWidth(), 200);
                    distanceSlider.setMaximum(100);
                    for (Component c : innerDistancePanel.getComponents()) {
                        if (!c.equals(distanceSlider)) {
                            innerDistancePanel.remove(c);
                        }
                    }
                    innerDistancePanel.validate();

                    speedSlider.setSize(speedSlider.getWidth(), 200);
                    speedSlider.setMaximum(100);
                    for (Component c : innerSpeedPanel.getComponents()) {
                        if (!c.equals(speedSlider)) {
                            innerSpeedPanel.remove(c);
                        }
                    }
                    innerSpeedPanel.validate();
                }
            }
        });
        JPanel safetyBoxPanel = new JPanel();
        safetyBoxPanel.setLayout(new BoxLayout(safetyBoxPanel, BoxLayout.LINE_AXIS));
        safetyBoxPanel.add(safetyBox);
        safetyBoxPanel.add(safetyLock);
        innerInnerPanel.add(safetyBoxPanel);

        innerCenterPanel.add(innerInnerPanel, BorderLayout.PAGE_START);
        innerInnerPanel = new JPanel();
        innerInnerPanel.add(calculateButton);
        calculateButton.setEnabled(false);
        innerCenterPanel.add(innerInnerPanel, BorderLayout.CENTER);

        //Generate and add the TOP inner panel
        innerTopPanel = new JPanel();
        innerTopPanel.setLayout(new BoxLayout(innerTopPanel, BoxLayout.LINE_AXIS));
        innerTopPanel.setBorder(etchedLoweredBorder);
        innerTopPanel.setPreferredSize(new Dimension(customizationPanelWidth, 240));
        innerTopPanel.setMinimumSize(new Dimension(customizationPanelWidth, 240));
        innerTopPanel.setMaximumSize(new Dimension(customizationPanelWidth, 240));
        innerTopPanel.add(Box.createHorizontalStrut(15));

        distanceSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                int value = distanceSlider.getValue();
                if (isSettingDistance) {
                    isSettingDistance = false;
                    distanceBox.setText("" + value);
                    return;
                }
                if (isDistanceLocked) {
                    distanceSlider.setValue(100 - speedSlider.getValue() - safetySlider.getValue());
                    distanceBox.setText("" + value);
                    return;
                } else if (isSpeedLocked || isSafetyLocked) {
                    int maxValue = 100 - (isSpeedLocked ? speedSlider.getValue() : safetySlider.getValue());
                    if (value > maxValue) {
                        value = maxValue;
                        distanceSlider.setValue(value);

                    }
                }
                set(distanceSlider, speedSlider, safetySlider,
                        100 - speedSlider.getValue() - safetySlider.getValue(),
                        speedSlider.getValue(),
                        safetySlider.getValue(),
                        value, null, null);
                distanceBox.setText("" + value);
            }
        });
        innerDistancePanel.setLayout(new BoxLayout(innerDistancePanel, BoxLayout.PAGE_AXIS));
        innerDistancePanel.setMinimumSize(new Dimension(40, customizationPanelHeight));
        innerDistancePanel.setMaximumSize(new Dimension(40, customizationPanelHeight));
        innerDistancePanel.add(distanceSlider);
        innerDistancePanel.validate();
        innerTopPanel.add(innerDistancePanel);

        speedSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                int value = speedSlider.getValue();
                if (isSettingSpeed) {
                    isSettingSpeed = false;
                    speedBox.setText("" + value);
                    return;
                }
                if (isSpeedLocked) {
                    speedSlider.setValue(100 - distanceSlider.getValue() - safetySlider.getValue());
                    speedBox.setText("" + value);
                    return;
                } else if (isDistanceLocked || isSafetyLocked) {
                    int maxValue = 100 - (isDistanceLocked ? distanceSlider.getValue() : safetySlider.getValue());
                    if (value > maxValue) {
                        value = maxValue;
                        speedSlider.setValue(maxValue);

                    }
                }
                set(distanceSlider, speedSlider, safetySlider,
                        distanceSlider.getValue(),
                        100 - distanceSlider.getValue() - safetySlider.getValue(),
                        safetySlider.getValue(),
                        null, value, null);
                speedBox.setText("" + value);
            }
        });
        innerSpeedPanel.setLayout(new BoxLayout(innerSpeedPanel, BoxLayout.PAGE_AXIS));
        innerSpeedPanel.setMinimumSize(new Dimension(40, customizationPanelHeight));
        innerSpeedPanel.setMaximumSize(new Dimension(40, customizationPanelHeight));
        innerSpeedPanel.add(speedSlider);
        innerSpeedPanel.validate();
        innerTopPanel.add(innerSpeedPanel);

        safetySlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                int value = safetySlider.getValue();
                if (isSettingSafety) {
                    isSettingSafety = false;
                    safetyBox.setText("" + value);
                    return;
                }
                if (isSafetyLocked) {
                    safetySlider.setValue(100 - distanceSlider.getValue() - speedSlider.getValue());
                    safetyBox.setText("" + value);
                    return;
                } else if (isDistanceLocked || isSpeedLocked) {
                    int maxValue = 100 - (isDistanceLocked ? distanceSlider.getValue() : speedSlider.getValue());
                    if (value > maxValue) {
                        value = maxValue;
                        safetySlider.setValue(maxValue);
                    }
                }
                set(distanceSlider, speedSlider, safetySlider,
                        distanceSlider.getValue(),
                        speedSlider.getValue(),
                        100 - distanceSlider.getValue() - speedSlider.getValue(),
                        null, null, value);
                safetyBox.setText("" + value);
            }
        });
        innerSafetyPanel.setLayout(new BoxLayout(innerSafetyPanel, BoxLayout.PAGE_AXIS));
        innerSafetyPanel.setMinimumSize(new Dimension(40, customizationPanelHeight));
        innerSafetyPanel.setMaximumSize(new Dimension(40, customizationPanelHeight));
        innerSafetyPanel.add(safetySlider);
        innerSafetyPanel.validate();
        innerTopPanel.add(innerSafetyPanel);

        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(0), new JLabel("Min"));
        labelTable.put(new Integer(100), new JLabel("Max"));
        safetySlider.setLabelTable(labelTable);
        safetySlider.setPaintLabels(true);

        //Generate and add the BOTTOM inner panel
        innerBottomPanel = new JPanel(new GridLayout(0, 1)); //label-text panel
        innerBottomPanel.setPreferredSize(new Dimension(customizationPanelWidth, 260));
        innerBottomPanel.setMaximumSize(new Dimension(customizationPanelWidth, 260));
        innerBottomPanel.setBorder(etchedLoweredBorder);

        ActionListener radioListener = getRadioButtonListener();//Generate our radio button listener
        ActionListener buttonListener = getButtonListener();//Generate our button button listener
        //Listener to handle the radio button functionality, keeping only the current button choice enabled

        ButtonGroup g = new ButtonGroup();
        ////
        pathRadioButton = new JRadioButton("Find New Path", true);
        pathRadioButton.addActionListener(radioListener);
        g.add(pathRadioButton);
        innerBottomPanel.add(pathRadioButton);

        pathButton = new JButton("Start");
        pathButton.addActionListener(buttonListener);
        pathButton.setEnabled(true);
        innerBottomPanel.add(pathButton);
        ////

        terrainRadioButton = new JRadioButton("Create Terrains", false);
        terrainRadioButton.addActionListener(radioListener);
        g.add(terrainRadioButton);
        innerBottomPanel.add(terrainRadioButton);

        terrainButton = new JButton("Start");
        terrainButton.addActionListener(buttonListener);
        terrainButton.setEnabled(false);
        innerBottomPanel.add(terrainButton);
        ////

        eventRadioButton = new JRadioButton("Create Events", false);
        eventRadioButton.addActionListener(radioListener);
        g.add(eventRadioButton);
        innerBottomPanel.add(eventRadioButton);

        eventButton = new JButton("Start");
        eventButton.addActionListener(buttonListener);
        eventButton.setEnabled(false);
        innerBottomPanel.add(eventButton);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new AdditionalDetailsDialog(frame,
                        distanceSlider.getValue(), speedSlider.getValue(),
                        safetySlider.getValue(), director);
                dialog.setVisible(true);
            }
        });

        ////
        innerTopPanel.add(Box.createHorizontalStrut(15));
        outerPanel.add(innerTopPanel);//ADD TOP PANEL
        outerPanel.add(innerCenterPanel);//ADD CENTER PANEL
        outerPanel.add(innerBottomPanel);//ADD BOTTOM PANEL

        // Add the customization panl to the conent pane
        frame.getContentPane().add(outerPanel, guiArea);
    }

    private void set(JSlider distanceSlider, JSlider speedSlider, JSlider safetySlider, int prevDist, int prevSpeed, int prevSafety, Integer newDist, Integer newSpeed, Integer newSafety) {
        int[] values;
        int delta, maxValue;
        if (newDist != null) {
            if (isDistanceLocked) {
                return;
            }
            delta = newDist - prevDist;
            if (isSpeedLocked || isSafetyLocked) {
                maxValue = 100 - (isSpeedLocked ? prevSpeed : prevSafety);
                if (newDist > maxValue) {
                    newDist = maxValue;
                    delta = newDist - prevDist;
                }
                if (isSpeedLocked) {
                    newSafety = prevSafety - delta;
                    newSpeed = prevSpeed;
                } else {
                    newSpeed = prevSpeed - delta;
                    newSafety = prevSafety;
                }
            } else if (prevSpeed > prevSafety) {
                values = getValues(delta, prevSpeed, prevSafety);
                newSpeed = values[0];
                newSafety = values[1];
            } else {
                values = getValues(delta, prevSafety, prevSpeed);
                newSpeed = values[1];
                newSafety = values[0];
            }
//            System.err.println("Dist changed from " + prevDist + " to " + newDist +
//                    ", so speed (" + prevSpeed + "=>" + newSpeed + ") and safety (" +
//                    prevSafety + "=>" + newSafety + ")");
        } else if (newSpeed != null) {
            if (isSpeedLocked) {
                return;
            }
            delta = newSpeed - prevSpeed;
            if (isDistanceLocked || isSafetyLocked) {
                maxValue = 100 - (isDistanceLocked ? prevDist : prevSafety);
                if (newSpeed > maxValue) {
                    newSpeed = maxValue;
                    delta = newSpeed - prevSpeed;
                }
                if (isDistanceLocked) {
                    newSafety = prevSafety - delta;
                    newDist = prevDist;
                } else {
                    newDist = prevDist - delta;
                    newSafety = prevSafety;
                }
            } else if (prevDist > prevSafety) {
                values = getValues(delta, prevDist, prevSafety);
                newDist = values[0];
                newSafety = values[1];
            } else {
                values = getValues(delta, prevSafety, prevDist);
                newDist = values[1];
                newSafety = values[0];
            }
//            System.err.println("Speed changed from " + prevSpeed + " to " + newSpeed +
//                    ", so distance (" + prevDist + "=>" + newDist + ") and safety (" +
//                    prevSafety + "=>" + newSafety + ")");
        } else if (newSafety != null) {
            if (isSafetyLocked) {
                return;
            }
            delta = newSafety - prevSafety;
            if (isDistanceLocked || isSpeedLocked) {
                maxValue = 100 - (isDistanceLocked ? prevDist : prevSpeed);
                if (newSafety > maxValue) {
                    newSafety = maxValue;
                    delta = newSafety - prevSafety;
                }
                if (isDistanceLocked) {
                    newSpeed = prevSpeed - delta;
                    newDist = prevDist;
                } else {
                    newDist = prevDist - delta;
                    newSpeed = prevSpeed;
                }
            } else if (prevDist > prevSpeed) {
                values = getValues(delta, prevDist, prevSpeed);
                newDist = values[0];
                newSpeed = values[1];
            } else {
                values = getValues(delta, prevSpeed, prevDist);
                newDist = values[1];
                newSpeed = values[0];
            }
//            System.err.println("Safety changed from " + prevSafety + " to " + newSafety +
//                    ", so distance (" + prevDist + "=>" + newDist + ") and speed (" +
//                    prevSpeed + "=>" + newSpeed + ")");
        }
        isSettingDistance = true;
        isSettingSpeed = true;
        isSettingSafety = true;
        distanceSlider.setValue(newDist);
        speedSlider.setValue(newSpeed);
        safetySlider.setValue(newSafety);
    }

    private int[] getValues(int delta, int preVal1, int preVal2) {
        double ratio = (double) preVal1 / (double) (preVal1 + preVal2);
        int delta2 = (int) Math.copySign(Math.floor(Math.abs(delta * ratio)), delta);
        int[] returnVal = {(preVal1 - delta2), (preVal2 - (delta - delta2))};
        return returnVal;
    }

    private void addAlertBottomBar() {

        JPanel alertBottomBar = new JPanel(new BorderLayout());
        alertBottomBar.setPreferredSize(new Dimension(alertPanelWidth, alertPanelHeight));

        Icon icon = getImageIcon(IMAGE_PATH + "alert.gif", 20, 20);
        alertLabel = new JLabel("Alert! Please click for details.", icon, JLabel.CENTER);
        alertLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(frame, 
                                           "A new event was received from the server, which\n "
                                           + "may cause the last calculated path to be inaccurate.\n"
                                           + " Path recalculation is recommended.",
                                           "Important!", JOptionPane.WARNING_MESSAGE);
                director.drawNewlyReceivedEvents();
                hideAlert();
            }
        });
        hideAlert();
        

        //JLabel alertLabel = new JLabel(" Alert Placeholder Label!");
        //alertLabel.setIcon(new Icon());
        
        alertBottomBar.add(alertLabel, BorderLayout.LINE_START);
        
        frame.add(alertBottomBar, BorderLayout.PAGE_END);
    }

    public void showAlert() {
        alertLabel.setEnabled(true);
        alertLabel.setVisible(true);
    }
    
    public void hideAlert() {
        alertLabel.setEnabled(false);
        alertLabel.setVisible(false);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid. 
     * Images should all be 128x128 for this build
     */
    protected static ImageIcon getImageIcon(String path,
            int scaleX, int scaleY) {
        Image image = null;
        try {
            InputStream is = ClassLoader.getSystemClassLoader().
                    getResourceAsStream(path);
            image = ImageIO.read(is);
        } catch (IOException ex) {
        //    System.err.println("IO Exception: " + ex);
        }

        return new ImageIcon(image.getScaledInstance(scaleX, scaleY, 0));

    }

    /**
     * Take an image icon, and turn all white pixels to transparent.
     */
    private static ImageIcon getWhiteAsZeroAlpha(ImageIcon imageIcon, int w, int h) {
        Image i = imageIcon.getImage();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(i, 0, 0, null);
        g.dispose();
        ImageFilter filter = new RGBImageFilter() {

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
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(ip));
    }

    private void postFrame() {
        frame.validate();
        frame.pack();
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * Create the layers menu by adding each layer and its checkbox
     * to the menu.
     * @param layersMenu The layers menu object.
     */
    private void createLayersMenu(JMenu layersMenu) {
        // Fill the layers innerCustomizationPanel with the titles of all layers in the world window's current model.
        JCheckBoxMenuItem item;
        LayerAction action;
        WorldWindowGLJPanel wwd = this.getWwd();

        for (Layer layer : wwd.getModel().getLayers()) {
            action = new LayerAction(layer, wwd, layer.isEnabled());
            item = new JCheckBoxMenuItem(action);
            item.setSelected(action.selected);
            layersMenu.add(item);
        }
    }

    /**
     * Insert a given layer into the WorldWindow just below the compass layer.
     * @param wwd The WorldWindow to insert a layer into.
     * @param layer The layer to insert into the WorldWindow.
     */
    public void insertBeforeCompass(WorldWindow wwd, Layer layer) {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l instanceof CompassLayer) {
                compassPosition = layers.indexOf(l);
            }
        }
        layers.add(compassPosition, layer);
    }

    /**
     * Insert a given layer into the WorldWindow just below the placenames layer.
     * @param wwd The WorldWindow to insert a layer into.
     * @param layer The layer to insert into the WorldWindow.
     */
    public void insertBeforePlacenames(WorldWindow wwd, Layer layer) {
        // Insert the layer into the layer list just before the placenames.
        int placeNamePosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l instanceof PlaceNameLayer) {
                placeNamePosition = layers.indexOf(l);
            }
        }
        layers.add(placeNamePosition, layer);
    }

    /**
     * Insert a given layer into the WorldWindow just above the placenames layer.
     * @param wwd The WorldWindow to insert a layer into.
     * @param layer The layer to insert into the WorldWindow.
     */
    public void insertAfterPlacenames(WorldWindow wwd, Layer layer) {
        // Insert the layer into the layer list just after the placenames.
        int placeNamePosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l instanceof PlaceNameLayer) {
                placeNamePosition = layers.indexOf(l);
            }
        }
        layers.add(placeNamePosition + 1, layer);
    }

    /**
     * Insert a given layer into the WorldWindow just below the target layer,
     * specified by the targetName.
     * @param wwd The WorldWindow to insert a layer into.
     * @param layer The layer to insert into the WorldWindow.
     * @param targetName The name of the layer from which the given layer will
     *                   be inserted above.
     */
    public void insertBeforeLayerName(WorldWindow wwd, Layer layer, String targetName) {
        // Insert the layer into the layer list just before the target layer.
        int targetPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l.getName().indexOf(targetName) != -1) {
                targetPosition = layers.indexOf(l);
                break;
            }
        }
        layers.add(targetPosition, layer);
    }

    /**
     * Start the application.
     * @param appName The name of the application.
     * @param appFrameClass The class of the application frame.
     * @return The Application Frame which was created.
     */
    public JFrame start(String appName, Class appFrameClass) {
        if (Configuration.isMacOS() && appName != null) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        }

        try {
            final JFrame frame = (JFrame) appFrameClass.newInstance();
            frame.setTitle(appName);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    frame.setVisible(true);
                }
            });
            return frame;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Listener to handle the radio button functionality, keeping only the current button choice enabled
    private ActionListener getRadioButtonListener() {

        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(pathRadioButton)) {
                    pathButton.setEnabled(true);
                    terrainButton.setEnabled(false);
                    eventButton.setEnabled(false);
                } else if (e.getSource().equals(terrainRadioButton)) {
                    pathButton.setEnabled(false);
                    terrainButton.setEnabled(true);
                    eventButton.setEnabled(false);
                } else if (e.getSource().equals(eventRadioButton)) {
                    pathButton.setEnabled(false);
                    terrainButton.setEnabled(false);
                    eventButton.setEnabled(true);
                }
            }
        };
    }
    //Listener to handle the path/terrain/event building functionality, keeping only the current button choice enabled

    private ActionListener getButtonListener() {

        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(pathButton)) {
                    if (pathButton.getText().equals("Start")) {//PATH HAS BEGUN
                        //FIRE POINT SELECTION FUNCTIONALITY
                        //ONCE TWO POINTS ARE REGISTERED, RE-ENABLE THESE BUTTONS AND SET TEXT BACK
                        //System.out.println("IN inner IF");
                        pathButton.setText("Waiting...");
                        pathButton.setEnabled(false);
                        pathButton.setEnabled(false);
                        pathRadioButton.setEnabled(false);
                        terrainRadioButton.setEnabled(false);
                        eventRadioButton.setEnabled(false);

                        director.startCreatingPath();

                    } else if (pathButton.getText().equals("Waiting...")) {//PATH HAS BEGAN
                        //FIRE POINT SELECTION FUNCTIONALITY
                        //ONCE TWO POINTS ARE REGISTERED, RE-ENABLE THESE BUTTONS AND SET TEXT BACK
                        if (director.endCreatingPath() == true) {
                            pathButton.setText("Start");
                            pathButton.setEnabled(true);
                            pathRadioButton.setEnabled(true);
                            terrainRadioButton.setEnabled(true);
                            eventRadioButton.setEnabled(true);
                        }
                    }
                } else if (e.getSource().equals(terrainButton)) {
                    if (terrainButton.getText().equals("Start")) {
                        //FIRE POINT SELECTION FUNCTIONALITY
                        //ONCE TERRAIN REGISTERED, RE-ENABLE THESE BUTTONS AND SET TEXT BACK
                        terrainButton.setText("End");
                       // terrainButton.setEnabled(false);
                        pathRadioButton.setEnabled(false);
                        terrainRadioButton.setEnabled(false);
                        eventRadioButton.setEnabled(false);

                        director.startCreatingTerrain();
                    } else if (terrainButton.getText().equals("End")) {

                        //FIRE POINT SELECTION FUNCTIONALITY
                        //ONCE TERRAIN REGISTERED, RE-ENABLE THESE BUTTONS AND SET TEXT BACK
                        terrainButton.setText("Start");
                        terrainButton.setEnabled(true);
                        pathRadioButton.setEnabled(true);
                        terrainRadioButton.setEnabled(true);
                        eventRadioButton.setEnabled(true);

                        director.endCreatingTerrain();
                        if (!director.hasCreatedTerrains()) {
                            return;
                        }
                        int userChoice = showCreationDialog("terrain");
                        //userChoice ==> 0-Save to Database
                        //               1-Save Locally
                        //               2- Both
                        //               3- Cancel
                        director.endTerrainList(userChoice);//0-save, 1- clear rendered and cancel


                    }
                } else if (e.getSource().equals(eventButton)) {
                    if (eventButton.getText().equals("Start")) {
                        //FIRE POINT SELECTION FUNCTIONALITY
                        //ONCE TWO POINTS ARE REGISTERED, RE-ENABLE THESE BUTTONS AND SET TEXT BACK
                        eventButton.setText("End");
                        //eventButton.setEnabled(false);
                        pathRadioButton.setEnabled(false);
                        terrainRadioButton.setEnabled(false);
                        eventRadioButton.setEnabled(false);

                        director.startCreatingEvent();
                    } else if (eventButton.getText().equals("End")) {
                        //FIRE POINT SELECTION FUNCTIONALITY
                        //ONCE TWO POINTS ARE REGISTERED, RE-ENABLE THESE BUTTONS AND SET TEXT BACK
                        eventButton.setText("Start");
                        //eventButton.setEnabled(true);
                        pathRadioButton.setEnabled(true);
                        terrainRadioButton.setEnabled(true);
                        eventRadioButton.setEnabled(true);


                        director.endCreatingEvent();//Done creating event, now prompt user about what to do
                        if (!director.hasCreatedEvents()) {
                            return;
                        }
                        int userChoice = showCreationDialog("event");
                        //userChoice ==> 0-Save to Database
                        //               1-Save Locally
                        //               2- Both
                        //               3- Cancel
                        director.endEventList(userChoice);
                    }
                }
            }
        };


    }

    private int showCreationDialog(String type) {

        if (type.equals("terrain")) {

            Object[] options = {"Save", "Cancel"};

            return JOptionPane.showOptionDialog(frame,
                    "What would you like to do with the newly created " + type + "(s)?",
                    "Creation Options",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
        } else if (type.equals("event")) {

            Object[] options = {"Report to Database", "Save Locally", "Both", "Cancel"};

            return JOptionPane.showOptionDialog(frame,
                    "What would you like to do with the newly created " + type + "(s)?",
                    "Creation Options",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
        } else {
            return -1;
        }
    }

    private ActionListener getParseActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog parseDialog = ParseDialog.getInstance(frame, director);
                parseDialog.setVisible(true);
            }
        };
    }

    /**
     * An action specifying the enabling and disabling of layers.
     */
    protected static class LayerAction extends AbstractAction {

        WorldWindow wwd;
        private Layer layer;
        private boolean selected;

        /**
         * Create a new LayerAction.
         * @param layer The layer associated with the action.
         * @param wwd The WorldWindow displaying WorldWind.
         * @param selected true if the action represents a selected layer.
         */
        public LayerAction(Layer layer, WorldWindow wwd, boolean selected) {
            super(layer.getName());
            this.wwd = wwd;
            this.layer = layer;
            this.selected = selected;
            this.layer.setEnabled(this.selected);
        }

        /**
         * Handle an action event by enabling or disabling the appropriate layer.
         * @param actionEvent The ActionEvent to handle.
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // Simply enable or disable the layer based on its toggle button.
            if (((JCheckBoxMenuItem) actionEvent.getSource()).isSelected()) {
                this.layer.setEnabled(true);
            } else {
                this.layer.setEnabled(false);
            }

            wwd.redraw();
        }
    }

    /**
     * Set System properties before any other initialization.
     */
    static {
        System.setProperty("java.net.useSystemProxies", "true");
        if (Configuration.isMacOS()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "World Wind Application");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
            System.setProperty("apple.awt.brushMetalLook", "true");
        } else if (Configuration.isWindowsOS()) {
            System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        }
    }
    
    
    public JFrame getFrame() {
        return frame;
    }
    
    public String getEventSaveDirectory() {
        return eventSaveDirectory;
    }

    public void setEventSaveDirectory(String eventSaveDirectory) {
        this.eventSaveDirectory = eventSaveDirectory;
    }

    public String getTerrainSaveDirectory() {
        return terrainSaveDirectory;
    }

    public void setTerrainSaveDirectory(String terrainSaveDirectory) {
        this.terrainSaveDirectory = terrainSaveDirectory;
    }
    
    public String getDatabaseIP() {
        return databaseIP;
    }
    
    public void setDatabaseIP(String ip) {
        this.databaseIP = ip;
    } 

    /**
     * Entry point into PathFinder. Create a new <code>MainGUI</code>
     * instance and initiate the display.
     */
    public static void main(String[] args) {
        MainGUI mainGUI = new MainGUI();
    }
}
