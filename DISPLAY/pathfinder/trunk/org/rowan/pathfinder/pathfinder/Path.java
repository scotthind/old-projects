package org.rowan.pathfinder.pathfinder;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolyline;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.List;

/**
 * The <code>Path</code> will be made up of a list of <code>Vector2D</code>
 * which will be converted into the approrpriate Position to be drawn on
 * WorldWind.
 * @author Shahid Akhter
 */
public class Path {

    /*The list of traversables in this Path */
    private List<Traversable> route;
    /*The Shape Attributes used to render green lines */
    private ShapeAttributes green;
    /*The Shape Attributes used to render blue lines */
    private ShapeAttributes blue;
    /*The Shape Attributes used to draw red lines */
    private ShapeAttributes red;

    public Path(List<Traversable> route) {
        this.route = route;
        green = new BasicShapeAttributes();
        green.setDrawOutline(true);
        green.setOutlineMaterial(new Material(Color.GREEN));
        green.setOutlineOpacity(.6);
        green.setInteriorOpacity(.6);
        green.setOutlineWidth(5);
        green.setEnableAntialiasing(true);

        blue = new BasicShapeAttributes();
        blue.setDrawOutline(true);
        blue.setOutlineOpacity(.6);
        blue.setInteriorOpacity(.6);
        blue.setOutlineMaterial(new Material(Color.BLUE));
        blue.setOutlineWidth(5);

        red = new BasicShapeAttributes();
        red.setDrawOutline(true);
        red.setOutlineOpacity(.6);
        red.setInteriorOpacity(.6);
        red.setOutlineMaterial(new Material(Color.RED));
        red.setOutlineWidth(5);
    }

    /**
     * This method will be responsible for drawing the Path onto WorldWind
     */
    public void draw(RenderableLayer layer, int pathNum) {


        for (Traversable t : route) {
            List<LatLon> list = new ArrayList<LatLon>();

            LatLon start = Logic2D.vector2DToLatLon(Logic2D.getStart(t.getSegment()));
            LatLon end = Logic2D.vector2DToLatLon(Logic2D.getEnd(t.getSegment()));

            list.add(start);
            list.add(end);

            SurfacePolyline poly = new SurfacePolyline(list);
            if (pathNum == 0) {
                poly.setAttributes(green);
            } else if (pathNum == 1) {
                poly.setAttributes(blue);
            } else {
                poly.setAttributes(red);
            }
            poly.setValue("Details", annotationText(pathNum));

            layer.addRenderable(poly);
        }
    }

    /**
     * Returns the list of traversables in this path.
     * @return A list of traversables.
     */
    public List<Traversable> getRoute() {
        return route;
    }

    /**
     * The total cost of the road.
     * @return
     */
    public double getTotalCost() {
        double sum = 0;


        for (Traversable t : route) {
            sum += t.getCost();
        }


        return sum;


    }

    /**
     * Returns the total distance of the path.
     * @return A double in KM of the total distance.
     */
    private double getTotalDistance() {
        double distance = 0;
        DecimalFormat twoPlaces = new DecimalFormat("#.##");
        for (Traversable t : route) {
            distance += t.getDistance();
        }
        
        return Double.valueOf(twoPlaces.format(distance));
    }

    /**
     * Generates the text that will be displayed in a tooltip on WorldWind.
     * @param pathNum The path number being drawn
     * @return A string containing the annoation.
     */
    private String annotationText(int pathNum) {
        String str = "";
        double time = getTravelTime();
        DecimalFormat twoPlaces = new DecimalFormat("#.##");
        if (pathNum == 0) {
            str += "Optimal Path" + "\n";
        } else {
            str += "Alternative Path " + pathNum+ "\n";
        }
        str += "Distance: " + getTotalDistance() + " km" + "\n";
        if(time < 1)
            str += "Travel Time: " + Double.valueOf(twoPlaces.format(getTravelTime() * 60)) + " Minutes";
        else
            str += "Travel Time: " + getTravelTime() + " Hours";

        return str;

    }

    private double getTravelTime()
    {
        double time = 0;
        DecimalFormat twoPlaces = new DecimalFormat("#.##");
        for(Traversable t: route)
            time += t.getTravelTime();
        return Double.valueOf(twoPlaces.format(time));
    }

    public String toString() {
        String str = "";
        for (Traversable trav : route) {
            str += trav;
        }
        return str;
    }
}
