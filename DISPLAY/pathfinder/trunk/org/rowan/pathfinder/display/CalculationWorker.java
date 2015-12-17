package org.rowan.pathfinder.display;

import java.util.Set;
import javax.swing.SwingWorker;
import org.rowan.pathfinder.pathfinder.Transformer;
import org.rowan.pathfinder.pathfinder.Vehicle;

/**
 * CalculationWorker is a threaded Swing Worker that will call the appropriate
 * methods to calculate a path.
 * 
 * @author John Schuff, Dan Urbano
 */
public class CalculationWorker extends SwingWorker<String, Void> {

    private Director director;
    private Set<Vehicle> vehicles;
    private Transformer.TransformMode mode;
    private double distance;
    private double speed;
    private double safety;

    public CalculationWorker(Director director, Set<Vehicle> vehicles,
            Transformer.TransformMode mode, int distance, int speed, int safety) {
        
        this.director = director;
        this.mode = mode;
        this.vehicles = vehicles;
        this.distance = (double)distance/100;
        this.speed = (double)speed/100;
        this.safety = (double)safety/100;
    }

    @Override
    protected String doInBackground() throws Exception {
        director.findAndDrawPaths(mode, safety, speed, distance, vehicles);
        return null;
    }

    @Override
    protected void done() {

        //do List<Path> paths = get(), get() grabs the rtn val from doInBG when its finished
        //now we actually draw the paths, since this done function blocks the event thread
    }
}
