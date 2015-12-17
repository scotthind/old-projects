package org.rowan.tda.tda2d.organize2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>OrganizeArea2D</code> class is a tactical decision aid will take a collection
 * of <code>Shape2D</code> instances and position them as close as possible to
 * the origin. The positioning of the <code>Shape2D</code> instances must take a
 * high level area into consideration, meaning the positioning of the <code>Shape2D</code>
 * must not intersect the high level area. Whether or not the <code>Shape2D</code>
 * instances can overlap is determined by the user. The <code>Spiral2D</code>
 * class provides an algorithm to perform the positioning of the given shapes.
 *
 * @author Jonathan Palka, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class OrganizeArea2D
{
    /*********************************************
     * MARK: Fields
     *********************************************/

    /** The instance of Spiral2D to execute the algorithm. */
    private Spiral2D spiralAlgorithm;

    /**
     * The algorithm modified the ordering of the collection of shapes, so this
     * collection is used as opposed to the collection given by the user. The
     * user's collection should not be modified, as it might be used for other
     * tasks external to this class.
     */
    private LinkedList<Shape2D> processedShapes;

    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Constructs an instance of <code>Organize2D</code>.
     * @param highLevelArea The high level area to organize the shapes in.
     * @param shapes the collection of two-dimensional shapes to be organized.
     * @param overLap <true> If the shapes can overlap;
     * <code>false</code> otherwise, which means the shapes cannot overlap.
     */
    public OrganizeArea2D(Rectangle2D highLevelArea, List<Shape2D> shapes, boolean overLap)
    {
        this.processedShapes = new LinkedList<Shape2D>(shapes);
        spiralAlgorithm = new Spiral2D(highLevelArea, shapes, overLap);
    }

    /*********************************************
     * MARK: Run
     *********************************************/

    /**
     * Executes the <code>Organize2D</code> algorithm. This algorithm will take
     * the <code>Shape2D</code> instances given to this class and position them
     * as close as possible to the origin. The positioning of the <code>Shape2D</code>
     * instances must take a high level area into consideration, meaning the
     * positioning of the <code>Shape2D</code> must not intersect the high level
     * area. Whether or not the <code>Shape2D</code> instances can overlap was
     * determined by the user when instantiating this class.
     * @return <code>true</code> if all shapes were successfully placed in the
     * high-level component and overlap rules are satisfied; <code>false</code> otherwise.
     */
    public boolean run() {
        return spiralAlgorithm.run();
    }
}