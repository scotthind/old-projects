package org.rowan.opawarenessvis.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.rowan.opawarenessvis.data.EntityOrAssetRule;
import org.rowan.opawarenessvis.data.OpSystem;
import org.rowan.opawarenessvis.parser.DataParser;
import org.rowan.opawarenessvis.parser.OpParseLogException;
import org.rowan.opawarenessvis.parser.RulesParser;
import org.rowan.opawarenessvis.parser.XMLParser;

/**
 * 
 * @author Jon Schuff, Dan Urbano
 * @version 1.0
 */
public class MainGUI {

    protected static JFrame frame = new JFrame("Operational Awareness Tool v1.0");
    protected static int customizationPanelWidth;
    private int customizationPanelHeight;
    private int breadcrumbPanelWidth;
    private int breadcrumbPanelHeight;
    private int glCanvasPanelWidth;
    private int glCanvasPanelHeight;
    private Map<String, String> imageMap = new HashMap<String, String>();
    private GLJPanel glCanvas;
    private Map<String, OpSystem> systems = new HashMap<String, OpSystem>();
    private JComboBox systemBox;
    private JComboBox missionBox;
    private Map<String, Map<String, List<EntityOrAssetRule>>> systemMissionMap = new HashMap<String, Map<String, List<EntityOrAssetRule>>>();
    private MainJOGL mainJOGL;
    private static final List<String> LOG_NOTHING_PARSED = Arrays.asList(new String[]{"There was no data to parse!"});

    /**
     * Entry point into PathFinder. Create a new <code>MainGUI</code>
     * instance and initiate the display.
     */
    public static void main(String[] args) {
        System.setProperty("sun.awt.noerasebackground", "true");
        new MainGUI(new Dimension(900, 750));

    }

    public MainGUI(Dimension canvasSize) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 750));
        frame.setResizable(true);

        customizationPanelWidth = 200;
        customizationPanelHeight = 750;

        breadcrumbPanelWidth = 900;
        breadcrumbPanelHeight = 60;

        glCanvasPanelWidth = 800;
        glCanvasPanelHeight = 600;

        initialize();
        glCanvas.addGLEventListener(mainJOGL);
        glCanvas.addMouseListener(mainJOGL);
        glCanvas.addMouseWheelListener(mainJOGL);
        glCanvas.addMouseMotionListener(mainJOGL);
    }

    protected final void initialize() {

        //TODO remove these .put statements when image parser is created
        imageMap.put("Engine", "org/rowan/opawarenessvis/images/engine.png");
        imageMap.put("Unidentified", "org/rowan/opawarenessvis/images/unidentified.png");
        imageMap.put("Car", "org/rowan/opawarenessvis/images/car.png");
        imageMap.put("Tire", "org/rowan/opawarenessvis/images/tire.png");
        imageMap.put("Cylinder", "org/rowan/opawarenessvis/images/cylinder.PNG");
        imageMap.put("Sensor", "org/rowan/opawarenessvis/images/sensor.PNG");

        addMenuBar();//add menubar to frame (top)

        addOpenGlWindow();//add openGL window to frame (center)

        mainJOGL = new MainJOGL(glCanvas, frame); //initiailize main jogl

        addCustomizationSidebar();//Add customization bar to frame (right)

        addBreadcrumbBar();//Add 

        postFrame();// Assure valid layout


    }

    private void addOpenGlWindow() {

        GLCapabilities capabilities = new GLCapabilities();

        // The canvas is the widget that's drawn in the JFrame
        glCanvas = new GLJPanel(capabilities);
        glCanvas.setSize(glCanvasPanelWidth, glCanvasPanelHeight);

        frame.getContentPane().add(glCanvas, BorderLayout.CENTER);
    }

    private void addMenuBar() {

        JMenuBar menuBar = new JMenuBar();// Main menubar
        JMenu menu; //Menu in the menbar
        JMenuItem menuItem;//Item in a menu

        menu = new JMenu("File"); //Populate File Menu

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItem); //add "Exit" to File Menu
        menuBar.add(menu); //Add "File Menu" to MenuBar

        menu = new JMenu("Parse");
        menuItem = new JMenuItem("Data File (xml)");
        final JFileChooser fcData = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fcData.showOpenDialog(frame);
            }
        });
        fcData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    DataParser dp = new DataParser();
                    String msg = "";
                    String msgTitle = "";
                    int msgType = JOptionPane.INFORMATION_MESSAGE;
                    try {
                        List<String> log = XMLParser.parse(dp, new FileReader(fcData.getSelectedFile()));
                        if (dp.extractData().isEmpty()) {
                            throw new OpParseLogException(LOG_NOTHING_PARSED);
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
                        for (OpSystem system : dp.extractData()) {
                            systems.put(system.getID(), system);
                            systemBox.addItem(system);
                        }
                        systemBox.setEnabled(true);

                    } catch (OpParseLogException ex) {
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
                        msg += "\nError: " + (ex.getMessage() == null ? "Unknown" : ex.getMessage());
                        msgType = JOptionPane.ERROR_MESSAGE;
                    } finally {
                        JOptionPane.showMessageDialog(frame, msg, msgTitle, msgType);
                    }



                }
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);
        menuItem = new JMenuItem("Rules File (xml)");
        final JFileChooser fcRules = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fcRules.showOpenDialog(frame);
            }
        });
        fcRules.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    RulesParser rp = new RulesParser();
                    String msg = "";
                    String msgTitle = "";
                    int msgType = JOptionPane.INFORMATION_MESSAGE;
                    try {
                        List<String> log = XMLParser.parse(rp, new FileReader(fcRules.getSelectedFile()));
                        if (rp.extractRules().isEmpty()) {
                            throw new OpParseLogException(LOG_NOTHING_PARSED);
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
                        for (String sysKey : rp.extractRules().keySet()) {
                            systemMissionMap.put(sysKey, rp.extractRules().get(sysKey));
                            for (String missionKey : rp.extractRules().get(sysKey).keySet()) {
                                for (EntityOrAssetRule rule : rp.extractRules().get(sysKey).get(missionKey)) {
                                    rule.addToComponent(missionKey, systems.get(sysKey));
                                }
                            }
                            // Mission box won't update unless the user re-chooses their system
                            // this will force them to
                            systemBox.setSelectedIndex(0);
                        }
                    } catch (OpParseLogException ex) {
                        msgTitle = "Error";
                        msg = "An error occured during the parsing process. Nothing was parsed.\n"
                                + "The error and any warnings found below are in chronological order.";
                        List<String> log = ex.getLog();
                        for (int i = 0; i < log.size(); i++) {
                            msg += "\n" + (i + 1) + ") " + log.get(i);
                        }
                        msgType = JOptionPane.ERROR_MESSAGE;
                    } catch (NullPointerException ex) {
                        msgTitle = "Warning";
                        msg = "A rule was found for data that does not exist. Data must be parsed\n"
                                + "before rules can be parsed. The parser might not have finished parsing.";
                        msgType = JOptionPane.WARNING_MESSAGE;
                    } catch (Exception ex) {
                        msgTitle = "Error";
                        msg = "A fatal error occured during the parsing process. Nothing was parsed.\n"
                                + "Please make sure the xml file is in the correct format.";
                        msg += "\nError: " + (ex.getMessage() == null ? "Unknown" : ex.getMessage());
                        msgType = JOptionPane.ERROR_MESSAGE;
                    } finally {
                        JOptionPane.showMessageDialog(frame, msg, msgTitle, msgType);
                    }
                }
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);
        menuItem = new JMenuItem("Images File (xml)");
        menuItem.setEnabled(false); //TODO SET TRUE WHEN WORKING
        final JFileChooser fcImages = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fcImages.showOpenDialog(frame);
            }
        });
        //TODO MAKE IMAGE PARSER THEN UNCOMMENT CODE BELOW
//        fcImages.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
//                    ImageParser ip = new ImageParser();           
//                    String msg = "";
//                    String msgTitle = "";
//                    int msgType = JOptionPane.INFORMATION_MESSAGE;
//                    try {
//                        List<String> log = XMLParser.parse(ip, new FileReader(fcImages.getSelectedFile()));  
//                        if (ip.extractImages().isEmpty()) {
//                            throw new OpParseLogException(LOG_NOTHING_PARSED);
//                        }
//                        if (log.isEmpty()) {
//                            msgTitle = "Success";
//                            msg = "Parsing was successful!";
//                        } else {
//                            msgTitle = "Warning";
//                            msg = "One or more warnings occured during the parsing process.\n" +
//                              "The warning(s) listed below are in chronological order.";
//                            for (int i = 0; i < log.size(); i++) {
//                                msg += "\n" + (i+1) + ") " + log.get(i);
//                            }
//                            msgType = JOptionPane.WARNING_MESSAGE;
//                        }
//                        for (String : ip.extractImages()) {
//                            // might not be strings? anyway do the image stuff here
//                        }
//                    } catch (OpParseLogException ex) {
//                        msgTitle = "Error";
//                        msg = "An error occured during the parsing process. Nothing was parsed.\n" +
//                              "The error and any warnings found below are in chronological order.";
//                        List<String> log = ex.getLog();
//                        for (int i=0; i<log.size(); i++) {
//                            msg += "\n" + (i+1) + ") " + log.get(i);
//                        }
//                        msgType = JOptionPane.ERROR_MESSAGE;
//                    } catch (Exception ex) {
//                        msgTitle = "Error";
//                        msg = "A fatal error occured during the parsing process. Nothing was parsed.\n" +
//                              "Please make sure the xml file is in the correct format.";
//                        msg+= "\nError: " + (ex.getMessage() == null? "Unknown" : ex.getMessage());
//                        msgType = JOptionPane.ERROR_MESSAGE;
//                    } finally {
//                        JOptionPane.showMessageDialog(frame, msg, msgTitle, msgType);
//                    }
//                }
//            }
//        });
        menu.add(menuItem);
        menuBar.add(menu);


        menu = new JMenu("Help"); //Populate Help Menu

        menuItem = new JMenuItem("About");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Operational Awareness Tool\n" +
                        "Created in the SEGV lab in the Rowan University CS Department\n" +
                        "Part of the MSE - Rowan Collaboration\n" +
                        "Authors: Shahid Akhter, Jon Schuff, Dan Urbano",
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menu.add(menuItem); //add "About" to Help Menu

        menuBar.add(menu); //Add "Help Menu" to MenuBar

        // Add menubar to the content pane
        frame.getContentPane().add(menuBar, BorderLayout.PAGE_START);
    }

    private void addCustomizationSidebar() {

        TitledBorder title;
        Border blackline;

        blackline = BorderFactory.createLineBorder(Color.black);

        JPanel customizationPanel = new JPanel(new BorderLayout()); //Main panel
        customizationPanel.setPreferredSize(new Dimension(customizationPanelWidth, customizationPanelHeight));
        customizationPanel.setLayout(new BoxLayout(customizationPanel, BoxLayout.PAGE_AXIS));

        GridLayout detailLayout = new GridLayout(0, 2);
        detailLayout.setVgap(10);
        JPanel innerCustomizationPanel = new JPanel(detailLayout);
        innerCustomizationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        title = BorderFactory.createTitledBorder(blackline, "Details");
        title.setTitlePosition(TitledBorder.TOP);
        innerCustomizationPanel.setBorder(title);

        JLabel nameDetail = new JLabel("N/A");
        JLabel typeDetail = new JLabel("N/A");
        JLabel idDetail = new JLabel("N/A");
        JLabel readinessDetail = new JLabel("N/A");

        JLabel label = new JLabel("System:");
        innerCustomizationPanel.add(label);
        innerCustomizationPanel.add(nameDetail);
        label = new JLabel("ID:");
        innerCustomizationPanel.add(label);
        innerCustomizationPanel.add(idDetail);
        label = new JLabel("Type:");
        innerCustomizationPanel.add(label);
        innerCustomizationPanel.add(typeDetail);
        label = new JLabel("Status:");
        innerCustomizationPanel.add(label);
        innerCustomizationPanel.add(readinessDetail);

        DetailWindow.init(nameDetail, typeDetail, idDetail, readinessDetail);

        customizationPanel.add(innerCustomizationPanel, BorderLayout.PAGE_START);// Add top panel

        JPanel previewPanel = new JPanel();

        PreviewLabel.init(imageMap, previewPanel);

        JLabel imageLabel = PreviewLabel.getInstance();
        previewPanel.add(imageLabel);


        title = BorderFactory.createTitledBorder(blackline, "Preview");
        title.setTitlePosition(TitledBorder.TOP);
        previewPanel.setBorder(title);
        previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 205));

        final JButton readinessButton = new JButton("Check Readiness");
        systemBox = new JComboBox();
        systemBox.setEnabled(false);
        systemBox.addItem("Select System");
        systemBox.setPreferredSize(new Dimension(customizationPanelWidth, 30));
        systemBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                readinessButton.setEnabled(false);
                if (e.getStateChange() == ItemEvent.SELECTED && !(e.getItem().equals("Select System"))) {
                    OpSystem system = (OpSystem) (e.getItem());
                    SystemTree.getInstance().clearNodes();
                    SystemTree.getInstance().updateTree(system.getID());
                    SystemTree.getInstance().setCurrentSystem(system);
                    SystemTree.getInstance().setSelectionRow(5);
                    Map<String, List<EntityOrAssetRule>> map = (Map<String, List<EntityOrAssetRule>>) systemMissionMap.get(system.getID());
                    missionBox.setEnabled(true);
                    missionBox.removeAllItems();
                    missionBox.addItem("Select Mission");
                    if (map != null) {
                        for (String str : map.keySet()) {
                            missionBox.addItem(str);
                        }
                    }
                    
                }
            }
        });
        missionBox = new JComboBox();
        missionBox.setEnabled(false);
        missionBox.setPreferredSize(new Dimension(customizationPanelWidth, 30));
        missionBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    readinessButton.setEnabled(!e.getItem().equals("Select Mission"));
                }
            }
        });
        
        readinessButton.setEnabled(false);
        readinessButton.setPreferredSize(new Dimension(customizationPanelWidth, 30));
        readinessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (missionBox.getSelectedItem() != null) {
                    if (!(missionBox.getSelectedItem().equals("Select Mission"))
                            && !(systemBox.getSelectedItem().equals("Select System"))
                            && !(systemBox.getSelectedItem().equals("No System Loaded"))) {
                        BreadCrumbBar.getInstance().clearCrumbs();
                        BreadCrumbBar.getInstance().addCrumb((Displayable) systemBox.getSelectedItem());
                        mainJOGL.setSystemAndMission((OpSystem) systemBox.getSelectedItem(), (String) missionBox.getSelectedItem(), true);
                        System.err.println("hello");
                        SystemTree.getInstance().setEnabled(true);
                    }
                }
            }
        });
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(systemBox);
        infoPanel.add(missionBox);
        infoPanel.add(readinessButton);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
        
        title = BorderFactory.createTitledBorder(blackline, "System & Mission Select");
        title.setTitlePosition(TitledBorder.TOP);
        infoPanel.setBorder(title);

        SystemTree.init(systems, new DefaultMutableTreeNode("System"), mainJOGL);
        JPanel treePanel = new JPanel(new GridLayout(1, 0));
        final JTree tree = SystemTree.getInstance();
        tree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                
                if (e.getClickCount() == 1 && SystemTree.getInstance().isEnabled()) {
                    TreePath paths = tree.getSelectionPath();
                    String node = paths.getLastPathComponent().toString();
                    if (!(node.equals("System"))) {
                        OpSystem system = SystemTree.getInstance().getCurrentSystem();
                        Displayable d;
                        if (node.equals(system.getName())) {
                            d = (Displayable) system;
                        } else {
                            d = (Displayable) system.getComponentMap().get(node);
                        }

                        mainJOGL.setDisplayable(d, true);
                        BreadCrumbBar.getInstance().addCrumb(d);
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        JScrollPane scrollpane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        treePanel.add(scrollpane);
        title = BorderFactory.createTitledBorder(blackline, "System Tree");
        title.setTitlePosition(TitledBorder.TOP);
        treePanel.setBorder(title);

        customizationPanel.add(infoPanel);
        customizationPanel.add(treePanel);
        customizationPanel.add(previewPanel);


        // Add the customization panel to the conent pane
        frame.getContentPane().add(customizationPanel, BorderLayout.LINE_END);
    }

    private void addBreadcrumbBar() {
        BorderLayout bLayout = new BorderLayout();
        FlowLayout fLayout = new FlowLayout();
        fLayout.setHgap(10);
        BreadCrumbBar.init(mainJOGL, imageMap);
        JPanel innerPanel = BreadCrumbBar.getInstance();

        JScrollPane pane = new JScrollPane(innerPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel breadcrumbBar = new JPanel(bLayout);
        breadcrumbBar.setPreferredSize(new Dimension(breadcrumbPanelWidth, breadcrumbPanelHeight));
        breadcrumbBar.add(pane);
        frame.add(breadcrumbBar, BorderLayout.PAGE_END);//add Breadcrumb Bar
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid. 
     *
     * Images should be 194xM where m is close to 194, for best results.
     */
    protected static ImageIcon createImageIcon(String path,
            String description, int width, int height, final Color color) {
        Image image = null;
        try {
            InputStream is = ClassLoader.getSystemClassLoader().
                    getResourceAsStream(path);
            image = ImageIO.read(is);
        } catch (IOException ex) {

        }

        image = image.getScaledInstance(width, height, 0);
        if (color != null) {
            BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            ImageFilter filter = new RGBImageFilter() {

                public int colorRBG = color.getRGB() | 0xFF000000;

                @Override
                public int filterRGB(int x, int y, int rgb) {
                    if ((rgb | 0xFF000000) == colorRBG) {
                        return 0x00FFFFFF & rgb;
                    } else {
                        return rgb;
                    }
                }
            };
            ImageProducer ip = new FilteredImageSource(bi.getSource(), filter);
            image = Toolkit.getDefaultToolkit().createImage(ip);
        }
        return new ImageIcon(image);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid. 
     *
     * Images should be 194xM where m is close to 194, for best results.
     */
    protected static ImageIcon createImageIcon(String path,
            String description, int width, int height) {
        return createImageIcon(path, description, width, height, null);
    }

    private void postFrame() {
        frame.validate();
        frame.pack();
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * Start the application.
     * @param appName The name of the application.
     * @param appFrameClass The class of the application frame.
     * @return The Application Frame which was created.
     */
    public JFrame start(String appName, Class appFrameClass) {

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

    public int getBreadcrumbPanelLength() {
        return breadcrumbPanelHeight;
    }

    public void setBreadcrumbPanelLength(int breadcrumbPanelLength) {
        this.breadcrumbPanelHeight = breadcrumbPanelLength;
    }

    public int getBreadcrumbPanelWidth() {
        return breadcrumbPanelWidth;
    }

    public void setBreadcrumbPanelWidth(int breadcrumbPanelWidth) {
        this.breadcrumbPanelWidth = breadcrumbPanelWidth;
    }

    public int getCustomizationPanelLength() {
        return customizationPanelHeight;
    }

    public void setCustomizationPanelLength(int customizationPanelLength) {
        this.customizationPanelHeight = customizationPanelLength;
    }

    public int getCustomizationPanelWidth() {
        return customizationPanelWidth;
    }

    public void setCustomizationPanelWidth(int customizationPanelWidth) {
        this.customizationPanelWidth = customizationPanelWidth;
    }

    public int getGlCanvasPanelLength() {
        return glCanvasPanelHeight;
    }

    public void setGlCanvasPanelLength(int glCanvasPanelLength) {
        this.glCanvasPanelHeight = glCanvasPanelLength;
    }

    public int getGlCanvasPanelWidth() {
        return glCanvasPanelWidth;
    }

    public void setGlCanvasPanelWidth(int glCanvasPanelWidth) {
        this.glCanvasPanelWidth = glCanvasPanelWidth;
    }
}
