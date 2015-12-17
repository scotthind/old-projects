
package org.rowan.camera;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.rowan.linalgtoolkit.*;
import org.rowan.linalgtoolkit.transform3d.*;

/**
 * A graphical control interface for interacting with <code>Camera</code> objects.
 * Every control panel has a reference to a <code>Camera</code> object, to which 
 * movement and rotation commands are relayed. Buttons to perform movements and 
 * rotations are provided on the UI, as well as a button to toggle the associated 
 * camera's focus lock.
 * <p>
 * <code>ControlPanel</code>s maintain settable movement and rotation speeds.
 * Movement speed is the distance, in Euclidean units, to move when an appropriate
 * movement button is pressed. Rotation speed is the angle, in radians, of rotation
 * applied when an appropriate rotation button is pressed. Sliders are provided
 * on the control panel for both movement and rotation speed, to allow users to
 * change the current speed within a threshold. For both movement and rotation
 * speed, the current speed, as well as the minimum/maximum speeds accessable via
 * respective sliders are settable programmatically.
 * <p>
 * The <code>ControlPanel</code> class extends <code>JPanel</code>, so it can
 * be embedded as needed into any custom UI. Alternatively, the <code>ControlWindow</code> 
 * provides a <code>ControlPanel</code> in a stand-alone window. 
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public class ControlPanel extends JPanel {
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** Default movement speed. */
    public static final double DEFAULT_MOVE_SPEED = 0.003;
    
    /** Default minimum movement speed visible on the movement speed slider. */
    public static final double DEFAULT_MIN_MOVE_SPEED = 0.0001;
    
    /** Default maximum movement speed visible on the movement speed slider. */
    public static final double DEFAULT_MAX_MOVE_SPEED = 0.02;
    
    
    /** Default rotation speed. */
    public static final double DEFAULT_ROT_ANGLE = Math.toRadians(.03);
    
    /** Default minimum rotation speed visible on the rotation speed slider. */
    public static final double DEFAULT_MIN_ROT_ANGLE = Math.toRadians(0.01);
    
    /** Default maximum rotation speed visible on the rotation speed slider. */
    public static final double DEFAULT_MAX_ROT_ANGLE = Math.toRadians(.2);
    
    
    /** Button actions. */
    private static final int UP         = 0;
    private static final int DOWN       = 1;
    private static final int LEFT       = 2;
    private static final int RIGHT      = 3;
    private static final int FORWARD    = 4;
    private static final int BACKWARD   = 5;
    private static final int PITCH_UP   = 6;
    private static final int PITCH_DOWN = 7;
    private static final int YAW_LEFT   = 8;
    private static final int YAW_RIGHT  = 9;
    private static final int ROLL_LEFT  = 10;
    private static final int ROLL_RIGHT = 11;
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The camera being controled by the control panel. */
    private Camera camera;
    
    
    /** The movement speed slider. */
    private JSlider moveSpeedSlider;
    
    /** The rotation speed slider. */
    private JSlider rotSpeedSlider;
    
    /** The focus lock toggle button. */
    private JToggleButton focusLockToggle;
    
    
    /** The current movement speed, in Euclidean units. */
    private double moveSpeed;
    
    /** The minimum movement speed visible on the movement speed slider. */
    private double minMoveSpeed;
    
    /** The maximum movement speed visible on the movement speed slider. */
    private double maxMoveSpeed;
    
    
    /** The current rotation speed, in radians. */
    private double rotSpeed;
    
    /** The minimum rotation speed visible on the rotation speed slider. */
    private double minRotSpeed;
    
    /** The maximum rotation speed visible on the rotation speed slider. */
    private double maxRotSpeed;


    /*********************************************
     * MARK: Constructors
     *********************************************/
    
    /**
     * Creates a <code>ControlPanel</code> with no associated camera.
     */
    public ControlPanel() {
        this(null);
    }
    
    /**
     * Creates a <code>ControlPanel</code> to control a given camera.
     * @param camera    A camera to be controlled by the control panel.
     */
    public ControlPanel(Camera camera) {
        // initialize camera
        this.camera = camera;
        
        // setup the UI
        setupUI();
        
        // initialize fields with default values
        this.minMoveSpeed = DEFAULT_MIN_MOVE_SPEED;
        this.maxMoveSpeed = DEFAULT_MAX_MOVE_SPEED;
        setMoveSpeed(DEFAULT_MOVE_SPEED);
        this.minRotSpeed = DEFAULT_MIN_ROT_ANGLE;
        this.maxRotSpeed = DEFAULT_MAX_ROT_ANGLE;
        setRotSpeed(DEFAULT_ROT_ANGLE);
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the camera controlled by the control panel.
     * @return  The control panel's associated <code>Camera</code> object.
     */
    public Camera getCamera() {
        return this.camera;
    }
    
    /**
     * Returns the current movement speed, in Euclidean units.
     * @return  The magnitude of the movement vector to be applied when moving
     *          the associated camera.
     */
    public double getMoveSpeed() {
        return this.moveSpeed;
    }
    
    /**
     * Returns the minimum movement speed, in Euclidean units.
     * @return  The minimum value on the movement speed slider.
     */
    public double getMinMoveSpeed() {
        return this.minMoveSpeed;
    }
    
    /**
     * Returns the maximum movement speed, in Euclidean units.
     * @return  The maximum value on the movement speed slider.
     */
    public double getMaxMoveSpeed() {
        return this.maxMoveSpeed;
    }
    
    /**
     * Returns the current rotation speed, in radians.
     * @return  The angle, in radians, to be applied when rotating the associated
     *          camera.
     */
    public double getRotSpeed() {
        return this.rotSpeed;
    }
    
    /**
     * Returns the minimum rotation speed, in radians.
     * @return  The minimum value on the rotation speed slider.
     */
    public double getMinRotSpeed() {
        return this.minRotSpeed;
    }
    
    /**
     * Returns the maximum rotation speed, in radians.
     * @return  The maximum value on the rotation speed slider.
     */
    public double getMaxRotSpeed() {
        return this.maxRotSpeed;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the camera controlled by the control panel.
     * @param camera    The <code>Camera</code> object to be controlled.
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    /**
     * Sets the current movement speed. The given value will be clamped to the
     * range <code>[getMinMoveSpeed(), getMaxMoveSpeed()]</code>.
     * @param moveSpeed The magnitude of the movement vector to be applied when
     *                  moving the associated camera.
     */
    public void setMoveSpeed(double moveSpeed) {
        // clamp given value to [min, max]
        moveSpeed = (moveSpeed < this.minMoveSpeed)? this.minMoveSpeed : moveSpeed;
        moveSpeed = (moveSpeed > this.maxMoveSpeed)? this.maxMoveSpeed : moveSpeed;
        
        // set movement speed
        this.moveSpeed = moveSpeed;
        
        // update UI
        updateUIElements();
    }
    
    /**
     * Sets the minimum movement speed. The given value will be clamped to the
     * range <code>[0, getMaxMoveSpeed()]</code>.
     * @param minMoveSpeed  The minimum value on the movement speed slider.
     */
    public void setMinMoveSpeed(double minMoveSpeed) {
        // clamp given value
        minMoveSpeed = (minMoveSpeed < 0)? 0 : minMoveSpeed;
        minMoveSpeed = (minMoveSpeed > this.maxMoveSpeed)? this.maxMoveSpeed : minMoveSpeed;
        
        // set minimum movement speed
        this.minMoveSpeed = minMoveSpeed;
    }
    
    /**
     * Sets the maximum movement speed. The given value will be clamped to a
     * value such that <code>maxMoveSpeed > getMaxMoveSpeed()</code>.
     * @param maxMoveSpeed  The maximum value on the movement speed slider.
     */
    public void setMaxMoveSpeed(double maxMoveSpeed) {
        // clamp given value
        maxMoveSpeed = (maxMoveSpeed < this.minMoveSpeed)? this.minMoveSpeed : maxMoveSpeed;
        
        // set minimum movement speed
        this.maxMoveSpeed = maxMoveSpeed;
    }
    
    /**
     * Sets the current rotation speed, in radians. The given angle will be clamped 
     * to the range <code>[getMinRotSpeed(), getMaxRotSpeed()]</code>.
     * @param rotSpeed  The magnitude of the movement vector to be applied when
     *                  moving the associated camera.
     */
    public void setRotSpeed(double rotSpeed) {
        // normalize angle
        rotSpeed = normalizeAngle(rotSpeed);
        
        // clamp given value to [min, max]
        rotSpeed = (rotSpeed < this.minRotSpeed)? this.minRotSpeed : rotSpeed;
        rotSpeed = (rotSpeed > this.maxRotSpeed)? this.maxRotSpeed : rotSpeed;
        
        // set rotation speed
        this.rotSpeed = rotSpeed;
        
        // update UI
        updateUIElements();
    }
    
    /**
     * Sets the minimum rotation speed, in radians. The given angle will normalized 
     * to the range <code>[0, 2*Math.PI]</code>, and subsequently clamped to the 
     * range <code>[0, getMaxRotSpeed()]</code>.
     * @param minRotSpeed   The minimum value on the rotation speed slider.
     */
    public void setMinRotSpeed(double minRotSpeed) {
        // normalize angle
        minRotSpeed = normalizeAngle(minRotSpeed);
        
        // clamp given angle
        minRotSpeed = (minRotSpeed < 0)? 0 : minRotSpeed;
        minRotSpeed = (minRotSpeed > this.maxRotSpeed)? this.maxRotSpeed : minRotSpeed;
        
        // set minimum rotation speed
        this.minRotSpeed = minRotSpeed;
    }
    
    /**
     * Sets the maximum rotation speed, in radians. The given angle will normalized 
     * to the range <code>[0, 2*Math.PI]</code>, and subsequently clamped such 
     * that <code>maxRotSpeed > getMinRotSpeed().
     * @param maxRotSpeed   The maximum value on the rotation speed slider.
     */
    public void setMaxRotSpeed(double maxRotSpeed) {
        // normalize angle
        maxRotSpeed = normalizeAngle(maxRotSpeed);
        
        // clamp given angle
        maxRotSpeed = (maxRotSpeed < this.minRotSpeed)? this.minRotSpeed : maxRotSpeed;
        
        // set maximum rotation speed
        this.maxRotSpeed = maxRotSpeed;
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/
    
    /**
     * Normalizes a given angle, in radians.
     * @param angle The angle to be normalized.
     */
    private double normalizeAngle(double angle) {
        while (angle < 0)
            angle += 2*Math.PI;
        while (angle > 2*Math.PI)
            angle -= 2*Math.PI;
        
        return angle;
    }
    
    /**
     * Syncs data-specific UI elements, such as sliders.
     */
    public void updateUIElements() {
        int value;
        
        // update movement speed slider
        value = (int) (this.moveSpeed / (this.maxMoveSpeed - this.minMoveSpeed) * 100);
        this.moveSpeedSlider.setValue(value);
        
        // update rotation speed slider
        value = (int) (this.rotSpeed / (this.maxRotSpeed - this.minRotSpeed) * 100);
        this.rotSpeedSlider.setValue(value);
        
        // update focus lock state
        if (this.camera != null) {
            if (camera.focusLocked())
                this.focusLockToggle.setText("Unlock");
            else
                this.focusLockToggle.setText("Lock");
        }
    }
    
    /**
     * Sets up the control panel UI.
     */
    private void setupUI() {
        // movement panel
        this.add(createMovementPanel());
        
        // glue
        this.add(Box.createGlue());
        
        // focus lock panel
        this.add(createFocusLockPanel());
        
        // glue
        this.add(Box.createGlue());
        
        // rotation panel
        this.add(createRotationPanel());
    }
    
    /**
     * Creates and returns the movement control panel.
     * @return  The created movement control panel.
     */
    private JPanel createMovementPanel() {
        // create panel
        JPanel movePanel = newPanel(BoxLayout.Y_AXIS);
        
        // set border        
        Border etchedBorder = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                                                                 BorderFactory.createEmptyBorder(2, 2, 2, 2));
        movePanel.setBorder(BorderFactory.createTitledBorder(etchedBorder,  "Movement"));
        
        // add components
        {
            movePanel.add(newButton("Up", 100, 35, UP));
            
            // left/right panel
            JPanel lrPanel = newPanel(BoxLayout.X_AXIS);
            {
                // left panel
                JPanel lPanel = newPanel(BoxLayout.Y_AXIS);
                {
                    lPanel.add(Box.createGlue());
                    lPanel.add(newButton("Left", 50, 80, LEFT));
                }
                lrPanel.add(lPanel);
                
                // forward/backward panel
                JPanel fbPanel = newPanel(BoxLayout.Y_AXIS);
                fbPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
                {
                    fbPanel.add(newButton("Forward", 90, 38, FORWARD));
                    fbPanel.add(newButton("Backward", 90, 38, BACKWARD));
                }
                lrPanel.add(fbPanel);
                
                // right panel
                JPanel rPanel = newPanel(BoxLayout.Y_AXIS);
                {
                    rPanel.add(Box.createGlue());
                    rPanel.add(newButton("Right", 50, 80, RIGHT));
                }
                lrPanel.add(rPanel);
            }
            movePanel.add(lrPanel);
            
            movePanel.add(newButton("Down", 100, 35, DOWN));
            
            
            // spacing
            movePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            movePanel.add(new JSeparator());
            
            // slider label
            JLabel label = new JLabel("Speed", JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            movePanel.add(label);
            
            // speed slider
            this.moveSpeedSlider = new JSlider(SwingConstants.HORIZONTAL);
            moveSpeedSlider.setMinimum(1);
            moveSpeedSlider.addChangeListener(new SliderChangeListener());
            movePanel.add(moveSpeedSlider);
            
        }
        
        // return movement panel
        return movePanel;
    }
    
    /**
     * Creates and returns the rotation control panel.
     * @return  The created rotation control panel.
     */
    private JPanel createRotationPanel() {
        // create panel
        JPanel rotPanel = newPanel(BoxLayout.Y_AXIS);
        
        // set border        
        Border etchedBorder = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                                                                 BorderFactory.createEmptyBorder(2, 2, 2, 2));
        rotPanel.setBorder(BorderFactory.createTitledBorder(etchedBorder, "Rotation"));
        
        // add components
        {
            // inner panel
            JPanel innerPanel = newPanel(BoxLayout.X_AXIS);
            {
                // yaw left panel
                JPanel ylPanel = newPanel(BoxLayout.Y_AXIS);
                {
                    ylPanel.add(Box.createGlue());
                    ylPanel.add(newButton("<html>Yaw<br>Left", 50, 80, YAW_LEFT));
                }
                innerPanel.add(ylPanel);
                
                // pitch/roll panel
                JPanel prPanel = newPanel(BoxLayout.Y_AXIS);
                {
                    prPanel.add(newButton("Pitch Up", 95, 44, PITCH_UP));
                    
                    // roll panel
                    JPanel rPanel = newPanel(BoxLayout.X_AXIS);
                    {
                        rPanel.add(newButton("<html>Roll<br>Left", 46, 62, ROLL_LEFT));
                        rPanel.add(newButton("<html>&nbsp;Roll<br>Right", 46, 62, ROLL_RIGHT));
                    }
                    prPanel.add(rPanel);
                    
                    prPanel.add(newButton("Pitch Down", 95, 44, PITCH_DOWN));
                }
                innerPanel.add(prPanel);
                
                // yaw right panel
                JPanel yrPanel = newPanel(BoxLayout.Y_AXIS);
                {
                    yrPanel.add(Box.createGlue());
                    yrPanel.add(newButton("<html>&nbsp;Yaw<br>Right", 50, 80, YAW_RIGHT));
                }
                innerPanel.add(yrPanel);
                
            }
            rotPanel.add(innerPanel);
            
            
            // spacing
            rotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            rotPanel.add(new JSeparator());
            
            // slider label
            JLabel label = new JLabel("Speed", JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            rotPanel.add(label);
            
            // rotation slider
            this.rotSpeedSlider = new JSlider(SwingConstants.HORIZONTAL);
            rotSpeedSlider.setMinimum(1);
            rotSpeedSlider.addChangeListener(new SliderChangeListener());
            rotPanel.add(rotSpeedSlider);
            
        }
        
        // return rotation panel
        return rotPanel;
    }
    
    /**
     * Creates and returns the focus lock panel.
     * @return  The created focus lock panel.
     */
    private JPanel createFocusLockPanel() {
        // create panel
        JPanel panel = newPanel(BoxLayout.Y_AXIS);
        
        // set border
        Border bevelBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        panel.setBorder(BorderFactory.createTitledBorder(bevelBorder, "Focus"));
        
        // add components
        {
            // create toggle button
            this.focusLockToggle = new JToggleButton("Lock");
            focusLockToggle.setPreferredSize(new Dimension(100, 50));
            focusLockToggle.setFocusable(false);
            
            // define toggle's action listener
            ActionListener toggleListener = new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    JToggleButton toggle = (JToggleButton) actionEvent.getSource();
                    camera.setFocusLocked(toggle.isSelected());
                    if (toggle.isSelected())
                        toggle.setText("Unlock");
                    else
                        toggle.setText("Lock");
                }
                
            };
            
            // set toggle's action listener
            focusLockToggle.addActionListener(toggleListener);
            panel.add(wrap(focusLockToggle));
        }
        
        // return focus lock panel
        return panel;
    }
    
    /**
     * Creates and returns a formatted JPanel using a BoxLayout layout manger
     * according to the given axis.
     * @param axis  The axis used for the JPanel's BoxLayout.
     * @return      The created and formatted JPanel object.
     */
    private JPanel newPanel(int axis) {
        // create panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, axis));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        // return panel
        return panel;
    }
    
    /**
     * Creates and returns a wrapped JButton with given text, width, and height.
     * @param text      The button's text. 
     * @param width     The button's width.
     * @param height    The button's height.
     * @param action    The action to be performed by the button.
     * @return          The created JButton object.
     */
    private JPanel newButton(String text, int width, int height, final int action) {
        // create button
        JButton button = new JButton(text) {
            public Insets getInsets() {
                return new Insets(2, 2, 2, 2);
            }
        };
        
        if (width > 0 && height > 0)
            button.setPreferredSize(new Dimension(width, height));
        button.setFocusable(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        // define button behavior
        button.addMouseListener(new RepeatMouseAdapter(action));
            
        
        // return wrapped button
        return wrap(button);
    }
    
    /**
     * Wraps a given component in a JPanel to preserve formatting.
     * @param component The component to be wrapped.
     * @return          A JPanel, which wraps the given component.
     */
    private JPanel wrap(JComponent component) {
        // create panel
        JPanel panel = new JPanel();
        panel.add(component);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // return panel
        return panel;
    }
    
    /*********************************************
     * MARK: Mouse Listener
     *********************************************/
    
    /**
     * Custom mouse listener class.
     */
    private class RepeatMouseAdapter implements MouseListener {
        // A timer for repeating input
        private Timer repeatTimer;
        
        // The action to be performed when the mouse is clicked.
        private int action;
        
        /**
         * Creates a instance of this custom mouse listener to react with a
         * given action.
         * @param action    The action to be performed when the mouse is clicked.
         */
        public RepeatMouseAdapter(int action) {
            this.action = action;
        }
        
        /**
         * Invoked when a mouse button is pressed.
         * @param e A mouse event.
         */
        public void mousePressed(MouseEvent e) {
            // perform initial action
            performAction();
            
            // create timer to repeat
            repeatTimer = new Timer(0, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    performAction();
                }
            });
            repeatTimer.setInitialDelay(1);
            repeatTimer.start();
        }
        
        /**
         * Invoked when a mouse button is released.
         * @param e A mouse event.
         */
        public void mouseReleased(MouseEvent e) {
            if (repeatTimer != null)
                repeatTimer.stop();
        }
        
        /**
         * Invoked when a mouse button is clicked (press and release).
         * @param e A mouse event.
         */
        public void mouseClicked(MouseEvent e) {
        }
        
        /**
         * Invoked when a mouse enters a component.
         * @param e A mouse event.
         */
        public void mouseEntered(MouseEvent e) {
        }
        
        /**
         * Invoked when a mouse exits a component.
         * @param e A mouse event.
         */
        public void mouseExited(MouseEvent e) {
            if (repeatTimer != null)
                repeatTimer.stop();
        }
    
        /**
         * Invoked when a button is pressed to perform a given action.
         * @param action    The action to be performed.
         */
        private void performAction() {
            // no camera?
            if (camera == null)
                return;
            
            // perform action
            switch (this.action) {
                case UP:
                    camera.move(new Vector3D(0, moveSpeed, 0), true);
                    break;
                case DOWN:
                    camera.move(new Vector3D(0, -moveSpeed, 0), true);
                    break;
                case LEFT:
                    camera.move(new Vector3D(-moveSpeed, 0, 0), true);
                    break;
                case RIGHT:
                    camera.move(new Vector3D(moveSpeed, 0, 0), true);
                    break;
                case FORWARD:
                    camera.move(new Vector3D(0, 0, -moveSpeed), true);
                    break;
                case BACKWARD:
                    camera.move(new Vector3D(0, 0, moveSpeed), true);
                    break;
                case PITCH_UP:
                    camera.rotate(new Rotation(rotSpeed, 0, 0), true);
                    break;
                case PITCH_DOWN:
                    camera.rotate(new Rotation(-rotSpeed, 0, 0), true);
                    break;
                case YAW_LEFT:
                    camera.rotate(new Rotation(0, rotSpeed, 0), true);
                    break;
                case YAW_RIGHT:
                    camera.rotate(new Rotation(0, -rotSpeed, 0), true);
                    break;
                case ROLL_LEFT:
                    camera.rotate(new Rotation(0, 0, rotSpeed), true);
                    break;
                case ROLL_RIGHT:
                    camera.rotate(new Rotation(0, 0, -rotSpeed), true);
                    break;
            }
        }
    }
    
    
    /*********************************************
     * MARK: Change Listener
     *********************************************/
    
    /**
     * A custom change listener for updating values associated with JSliders.
     */
    private class SliderChangeListener implements ChangeListener {
        
        /**
         * Invoked when the target of the listener has changed its state.
         * @param e A ChangeEvent object
         */
        public void stateChanged(ChangeEvent e) {
            if (e.getSource().equals(moveSpeedSlider)) {
                double sliderVal = (double) moveSpeedSlider.getValue();
                moveSpeed = sliderVal * (maxMoveSpeed - minMoveSpeed)/100;
                
            } else if (e.getSource().equals(rotSpeedSlider)) {
                double sliderVal = (double) rotSpeedSlider.getValue();
                rotSpeed = sliderVal * (maxRotSpeed - minRotSpeed)/100;
            }
        }
    }
}
