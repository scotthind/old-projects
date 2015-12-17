package org.rowan.pathfinder.pathfinder;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.SurfacePolygon;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.Polygon2D;

/**
 * Class <code>Terrain</code> will be used for the offroad algorithm. This
 * class should contain everything necessary to construct a shape on our
 * Djikstra graph in order to determine offroad paths.
 * 
 * @author Dan Urbano, Shahid Akhter
 * @version 1.0
 * @since 1.0
 */
public class Terrain {

    /** The outer boundary of the terrain. */
    private Polygon2D boundary;
    /** The type of terrain. Necessary for vehicle traversability consideration.*/
    private TerrainType type;
    /** Description of the terrain. */
    private String description;
    /** The main point of a terrain. Definable by other classes */
    private Vector2D mainPoint;
    /** The neighbors of this terrain */
    private Set<Terrain> neighbors = new HashSet<Terrain>();
    /** The set of unity points */
    private Set<Vector2D> unityPoints = new HashSet<Vector2D>();
    private BasicShapeAttributes terrainAttr;

    /**
     * Terrains will be created in order to aid in generating logical offroad
     * edges.
     * @param boundary A Polygon2D representing the shape of the terrain.
     * @param type A type of terrain e.g. FOREST_LIGHT
     * @param description The description of the terrain.
     */
    public Terrain(Polygon2D boundary, TerrainType type, String description) {
        this.boundary = boundary;
        this.type = type;
        this.description = description;
        this.mainPoint = Logic2D.getCentroid(boundary.getWorldVertices());

        terrainAttr = new BasicShapeAttributes();
        terrainAttr.setOutlineMaterial(new Material(getColor(type)));
        terrainAttr.setInteriorMaterial(new Material(getColor(type)));
        terrainAttr.setOutlineOpacity(.4);
        terrainAttr.setInteriorOpacity(.4);

    }

    /**
     * Returns the shape of the Terrain
     * @return A Polygon2D which represents the shape of the terrain.
     */
    public Polygon2D getBoundary() {
        return boundary;
    }

    /**
     * Returns the description of the Terrain
     * @return A String representing the description of the terrain.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the type of terrain
     * @return returns the type of terrain e.g. Forest_LIGHT
     */
    public TerrainType getType() {
        return type;
    }

    /**
     * Set the main point of this terrain to mainPoint.
     * @param mainPoint The point to become the main point of the terrain.
     */
    public void setMainPoint(Vector2D mainPoint) {
        this.mainPoint = mainPoint;
    }

    /**
     * Get the main point of this terrain.
     * @return The point that was set as the main point of this terrain (possibly null).
     */
    public Vector2D getMainPoint() {
        return mainPoint;
    }

    /**
     * Adds a terrain to this terrains set of neighbors
     * @param t A terrain that is neighboring this terrain.
     */
    public void addNeighbor(Terrain t) {
        neighbors.add(t);
    }

    /**
     * Returns a set of this terrain's neighbors.
     * @return The set of this terrains neighbors.
     */
    public Set<Terrain> getNeighbors() {
        return neighbors;
    }

    /**
     * Adds a unity point to this terrains set of unity points.
     * @param uPoint A point of unity from one terrain to another.
     */
    public void addUnityPoint(Vector2D uPoint) {
        unityPoints.add(uPoint);
    }

    /**
     * Returns the set of unity points.
     */
    public Set<Vector2D> getUnityPoints() {
        return unityPoints;
    }

    @Override
    public String toString() {
        return "Type: " + type + " Verticies: " + boundary.getWorldVertices()
                + "Description: " + description;
    }

    public void addToLayer(RenderableLayer layer) {
        List<LatLon> positions = new ArrayList<LatLon>();
        for (Vector2D v : boundary.getWorldVertices()) {
            positions.add(Logic2D.vector2DToLatLon(v));
        }
        SurfacePolygon eventShape = new SurfacePolygon(terrainAttr, positions);
        eventShape.setValue("Details", annotationText());
        layer.addRenderable(eventShape);
    }

    /**
     * Returns a string that represents the proper format of the event
     * based on the XML requirements.
     * @return A string in Event XML Format.
     */
    public String export() {

        String export = "";
        export += "\t<Terrain>\n";
        export += "\t\t<Type>" + type + "</Type>\n";
        export += "\t\t<Description>" + description + "</Description>\n";
        export += "\t\t<OuterBoundary>\n";
        for (Vector2D v : boundary.getWorldVertices()) {
            export += "\t\t\t<Coord>" + v.getX() + "," + v.getY() + "</Coord>\n";
        }
        export += "\t\t</OuterBoundary>\n";
        export += "\t</Terrain>\n";

        return export;
    }

    private String annotationText() {
        String str = "Terrain\n";

        str += "Type: " + type + "\n";
        if (description != null) {
            str += "Description: " + description + "\n";
        }

        return str;
    }

    private Color getColor(TerrainType type) {
        switch (type) {
            case FOREST_LIGHT:
                return new Color(0, 220, 0);
            case FOREST_MEDIUM:
                return new Color(0, 175, 0);
            case FOREST_HEAVY:
                return new Color(0, 120, 0);
            case WATER_WADABLE:
                return new Color(96, 174, 255);
            case WATER_SHALLOW:
                return new Color(0, 128, 255);
            case WATER_MIDDEPTH:
                return new Color(0, 96, 191);
            case WATER_DEEP:
                return new Color(0, 48, 96);
            case SURFACE_IMPASSABLE:
                return Color.BLACK;
            case SURFACE_SAND:
                return new Color(243, 186, 84);
            case SURFACE_MUD:
                return new Color(150, 107, 10);
            case SURFACE_SNOW:
                return new Color(220, 255, 255);
            case SURFACE_ICE:
                return new Color(170, 200, 255);
            case SURFACE_GRASS:
                return new Color(128, 255, 0);
            case SURFACE_TALLGRASS:
                return new Color(100, 200, 0);
            case SURFACE_EARTH_BARE:
                return new Color(220, 110, 0);
            case SURFACE_EARTH_RED:
                return new Color(210, 80, 0);
            case SURFACE_SWAMP:
                return new Color(90, 165, 85);
            case SURFACE_MARSH:
                return new Color(80, 130, 0);
            case SURFACE_ROCKY:
                return new Color(150, 150, 150);
            case SURFACE_FARMLAND:
                return new Color(230, 220, 80);
            case SURFACE_PAVED:
                return new Color(100, 100, 100);
            case OBSTACLE_BUILDING:
                return new Color(0, 0, 0);
            case OBSTACLE_OTHER:
                return new Color(0, 0, 0);
            default:
                return Color.BLACK;
        }
    }
}
