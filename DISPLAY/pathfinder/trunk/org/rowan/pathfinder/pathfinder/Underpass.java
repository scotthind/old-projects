package org.rowan.pathfinder.pathfinder;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.SurfaceCircle;
import java.awt.Color;
import org.rowan.linalgtoolkit.Vector2D;

/**
 * Class <code>Underpass</code> represents a location on a road with a maximum
 * height requirement in order to travel on the road.
 * 
 * @author Dan Urbano, Shahid Akhter
 * @version 1.0
 * @since 1.0
 */
public class Underpass {

    /** The coordinate at which the underpass is located. */
    private Vector2D location;
    /** The road passing over the road with the height requirement. It is possible
    to be null in the event of a tunnel.*/
    private String overRoad;
    /** The road that has the height requirement */
    private String underRoad;
    /** The maximum possible height possible to get through the underpass
    represented in meters */
    private double height;

    /**
     * Create a new Underpass object, given a location, a clearance height,
     * and the roads that intersect to form the underpass
     * @param location The coordinates of the underpass' location
     * @param overRoad The name of the road that travels over the underpass
     *                 (possibly null if the underpass is not under a road)
     * @param underRoad The road that travels under the underpass
     * @param height The clearance height of the underpass (in meters)
     */
    public Underpass(Vector2D location, String overRoad, String underRoad, double height) {
        if ((location == null) || (underRoad == null)) {
            throw new NullPointerException();
        }

        this.location = location;
        this.overRoad = overRoad;
        this.underRoad = underRoad;
        this.height = height;
    }

    /**
     * Returns the height requirement of the underpass.
     * @return The height of the underpass (in meters).
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the location of the underpass.
     * @return Coordinates of the Underpass.
     */
    public Vector2D getLocation() {
        return location;
    }

    /**
     * Returns the name of the higher road of the underpass.
     * @return Name of the road over the underpass (null if none present)
     */
    public String getOverRoad() {
        return overRoad;
    }

    /**
     * Returns the name of the road with the height requirement.
     * @return Name the road under the underpass
     */
    public String getUnderRoad() {
        return underRoad;
    }

    public void addToLayer(RenderableLayer layer) {
        LatLon loc = Logic2D.vector2DToLatLon(location);
        BasicShapeAttributes upAttr;
        upAttr = new BasicShapeAttributes();
        upAttr.setOutlineMaterial(new Material(Color.PINK));
        upAttr.setInteriorMaterial(new Material(Color.PINK));
        upAttr.setOutlineOpacity(.8);
        upAttr.setInteriorOpacity(.8);

        SurfaceCircle upShape = new SurfaceCircle(upAttr, loc, 2);
        upShape.setValue("Details", annotationText());
        layer.addRenderable(upShape);
    }

    public String toString() {
        return "Location: " + location.getX() + "," + location.getY()
                + " OverRoad: " + overRoad + " UnderRoad: " + underRoad
                + " Height: " + height;
    }

    private String annotationText()
    {
        String str = "Underpass" + "\n";
        str += "Road: " + underRoad + "\n";
        str += "Max Height: " + height + " meters";

        return str;
    }
}
