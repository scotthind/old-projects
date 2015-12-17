
package org.rowan.camera;

import java.awt.*;
import org.rowan.linalgtoolkit.*;
import org.rowan.linalgtoolkit.transform3d.*;

/**
 * The <code>GestureController</code> class provides a collection of static methods
 * for invoking <code>Camera</code> behaviors based on mouse or touch gestures.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 */
public abstract class GestureController {
     
    
    /*********************************************
     * MARK: Controls
     *********************************************/

    /**
     * Moves a given camera according to a gesture defined by given delta values. 
     * <p>
     * If <code>relative</code> is set to <code>true</code>, the movement will
     * be performed relative to the camera's current orientation. Otherwise, the
     * rotation will be performed relative to the world coordinate system.
     * <p>
     * The given <code>xDelta</code> and <code>yDelta</code> values will be 
     * associated with the axes given for <code>xAxis</code> and <code>yAxis</code>, 
     * respectively, when applied to 3D space.
     * @param camera            The camera to be moved.
     * @param xDelta            The gesture delta, in pixels, in the 'x' direction.
     * @param yDelta            The gesture delta, in pixels, in the 'y' direction.
     * @param xAxis             The axis to which <code>xDelta</code>, will be 
     *                          projected when movement is applied.
     * @param yAxis             The axis to which <code>yDelta</code>, will be 
     *                          projected when movement is applied.
     * @param xUnitsPerPixel    The amount of movement, in Euclidean units, to
     *                          be applied to the camera per pixel of <code>xDelta</code>.
     * @param yUnitsPerPixel    The amount of movement, in Euclidean units, to
     *                          be applied to the camera per pixel of <code>yDelta</code>.
     * @param relative          <code>true</code> for movement relative to the 
     *                          camera's current orientation; <code>false</code> 
     *                          for absolute movement, relative to the world
     *                          coordinate system.
     */
    public static void move(Camera camera,
                            int xDelta, int yDelta, 
                            Vector3D xAxis, Vector3D yAxis, 
                            double xUnitsPerPixel, double yUnitsPerPixel,
                            boolean relative) {
        
        // apply movement for x
        double xMag = xUnitsPerPixel * (double)xDelta;
        camera.move(new Vector3D(xAxis, xMag), relative);
        
        // apply movement for y
        double yMag = yUnitsPerPixel * (double)yDelta;
        camera.move(new Vector3D(yAxis, yMag), relative);
    }
    
    /**
     * Rotates a given camera according to a gesture defined by given delta
     * values. The given <code>xDelta</code> and <code>yDelta</code> values will
     * be associated with rotation about the axes given for <code>xAxis</code> 
     * and <code>yAxis</code>, respectively. 
     * <p>
     * If <code>relative</code> is set to <code>true</code>, the movement will
     * be performed relative to the camera's current orientation. Otherwise, the
     * rotation will be performed relative to the world coordinate system.
     * @param camera            The camera to be rotated.
     * @param xDelta            The gesture delta, in pixels, in the 'x' direction.
     * @param yDelta            The gesture delta, in pixels, in the 'y' direction.
     * @param xAxis             The axis about which the rotation associated with
     *                          <code>xDelta</code>, will be applied.
     * @param yAxis             The axis about which the rotation associated with
     *                          <code>yDelta</code>, will be applied.
     * @param xAnglePerPixel    The angle of rotation to be applied per pixel 
     *                          of <code>xDelta</code>.
     * @param yAnglePerPixel    The angle of rotation to be applied per pixel 
     *                          of <code>yDelta</code>.
     * @param relative          <code>true</code> for movement relative to the
     *                          camera's current orientation; <code>false</code> for
     *                          absolute movement, relative to the world coordinate system.
     */
    public static void rotate(Camera camera,
                       int xDelta, int yDelta, 
                       Vector3D xAxis, Vector3D yAxis, 
                       double xAnglePerPixel, double yAnglePerPixel,
                       boolean relative) {
        
        // weight given axes with angles as magnitude
        xAxis = new Vector3D(xAxis, xAnglePerPixel);
        yAxis = new Vector3D(yAxis, yAnglePerPixel);
        
        // add weighted axes
        Vector3D sum = xAxis.add(yAxis);
        
        // extract rotation axis and angle
        double angle = sum.magnitude();
        Vector3D axis = sum.unitVector();
        
        // apply rotation
        camera.rotate(new Rotation(axis, angle), relative);
    }
    
    /**
     * Rotates a given camera around a given point according to a gesture defined 
     * by given delta values. The rotation will be performed relative to the world
     * coordinate system. The given <code>xDelta</code> and <code>yDelta</code> 
     * values will be associated with rotation about the axes given for <code>xAxis</code> 
     * and <code>yAxis</code>, respectively. 
     * <p>
     * If <code>relative</code> is set to <code>true</code>, the movement will
     * be performed relative to the camera's current orientation. Otherwise, the
     * rotation will be performed relative to the world coordinate system.
     * If the <cod>reorient</code> flag is set to <code>true</code>, the rotation
     * will be applied as a standard rotational transformation would; altering
     * the camera's orientation. If set to <code>false</code>, the camera's 
     * orientation will remain unchanged as the rotation is performed.
     * @param camera            The camera to be rotated.
     * @param point             The point about which the camera will rotate.
     * @param xDelta            The gesture delta, in pixels, in the 'x' direction.
     * @param yDelta            The gesture delta, in pixels, in the 'y' direction.
     * @param xAxis             The axis about which the rotation associated with
     *                          <code>xDelta</code>, will be applied.
     * @param yAxis             The axis about which the rotation associated with
     *                          <code>yDelta</code>, will be applied.
     * @param xAnglePerPixel    The angle of rotation to be applied per pixel 
     *                          of <code>xDelta</code>.
     * @param yAnglePerPixel    The angle of rotation to be applied per pixel 
     *                          of <code>yDelta</code>.
     * @param reorient          <code>true</code> for movement relative to the 
     *                          camera's current orientation; <code>false</code> 
     *                          for absolute movement, relative to the world 
     *                          coordinate system.
     */
    public static void rotate(Camera camera,
                       Vector3D point, 
                       int xDelta, int yDelta, 
                       Vector3D xAxis, Vector3D yAxis, 
                       double xAnglePerPixel, double yAnglePerPixel,
                       boolean reorient) {   
                
        // weight given axes with angle as magnitude
        xAxis = new Vector3D(xAxis, xAnglePerPixel);
        yAxis = new Vector3D(yAxis, yAnglePerPixel);
        
        // add weighted axes
        Vector3D sum = xAxis.add(yAxis);
        
        // extract rotation axis and angle
        double angle = sum.magnitude();
        Vector3D axis = sum.unitVector();
        
        // apply rotation
        camera.rotate(new Rotation(axis, angle), point, reorient);
    }
    
    
    /*********************************************
     * MARK: Static
     *********************************************/
    
    /**
     * Computes an appropriate angle-per-pixel value needed to simulate a drag 
     * gesture defined by given delta values on a viewport with given dimensions
     * and field of view angle.
     * @param xDelta    The gesture delta, in pixels, in the 'x' direction.
     * @param yDelta    The gesture delta, in pixels, in the 'y' direction.
     * @param size      The size of the viewport on which the gesture occured.
     * @param fovy       The viewport's field of view angle in the y direction.
     * @return          An array containing the appropriate angle-per-pixel values
     *                  needed for the x and y directions to simulate a drag gesture.
     */
    public static double[] computeDragAnglePerPixel(int xDelta, int yDelta, Dimension size, double fovy) {
        // compute aspect ratio
        double aspect = (double)size.width / (double)size.height;
        
        // compute rotation angles
        double[] angles = new double[2];
        angles[0] = ((double)xDelta/(double)size.width) * (fovy * aspect);
        angles[1] = ((double)yDelta/(double)size.height) * fovy;
        
        // return computed angles
        return angles;
    }
}
