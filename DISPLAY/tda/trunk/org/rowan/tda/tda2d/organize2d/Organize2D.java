package org.rowan.tda.tda2d.organize2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>Organize2D</code> class is a tactical decision aid will take a collection
 * of <code>Shape2D</code> instances and position them as close as possible to
 * the origin without any of the shapes overlapping. The <code>Spiral2D</code>
 * class provides an algorithm to perform the positioning of the given shapes.
 *
 * @author Jonathan Palka, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class Organize2D
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
     * @param shapes the collection of two-dimensional shapes to be organized.
     */
    public Organize2D(List<Shape2D> shapes)
    {
        this.processedShapes = new LinkedList<Shape2D>(shapes);
        this.spiralAlgorithm = new Spiral2D(null, processedShapes, false);
    }

    /*********************************************
     * MARK: Run
     *********************************************/

    /**
     * Executes the <code>Organize2D</code> algorithm. This algorithm will take
     * the <code>Shape2D</code> instances given to this class and position them
     * as close as possible to the origin without any of the shapes overlapping.
     * @return <code>true</code> if algorithm was able run successfully; 
     * <code>false</code> otherwise.
     */
    public boolean run() {
        return spiralAlgorithm.run();
    }
}