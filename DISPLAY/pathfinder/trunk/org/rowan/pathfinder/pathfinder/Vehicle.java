package org.rowan.pathfinder.pathfinder;

import java.util.HashMap;
import java.util.Map;

/**
 * Class <code>Vehicle</code> will represent a user-defined vehicle. A Vehicle
 * will contain its traversability capabilities as well as general information
 * which will be useful in determining optimal paths.
 * 
 * @author Dan Urbano, Shahid Akhter
 * @version 1.0
 * @since 1.0
 */
public class Vehicle {
    /** The name of the Vehicle */
    private String name;
    /** The height of the vehicle, represented in meters */
    private double height;
    /** The width of the vehicle represented in meters */
    private double width;
    /** The maximum speed of the vehicle represented in Kilometers per Hour */
    private int maxSpeed;
    /** Represents if the vehicle is capable of traveling through mines. */
    private boolean mineResistant;
    /** Map containing the traversability values associated with a given terrain
     * for a vehicle. */
    private Map<TerrainType, Double> traversabilityMap;

    /**
     * Create a Vehicle class with the fields parsed from the Vehicle XML File
     * which should include the height, width, maximum speed, it's terrain
     * traversability capabilities and if it is able to withstand mines.
     * @param height The height of the vehicle in meters.
     * @param width The width of the vehicle in meters.
     * @param maxSpeed The maximum speed of the vehicle in kilometers per hour.
     * @param mineResistant Whether or not the vehicle can withstanding
     *                      traversing through mines.
     * @param traversabilityMap
     */
    public Vehicle(String name, double height, double width, int maxSpeed,
            boolean mineResistant, Map<TerrainType, Double> traversabilityMap) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.maxSpeed = maxSpeed;
        this.mineResistant = mineResistant;
        this.traversabilityMap = traversabilityMap;
    }
    
    /**
     * Returns the height of the vehicle.
     * @return Height (meters)
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the width of the vehicle.
     * @return Width (meters)
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the max speed of the vehicle.
     * @return Max Speed (KM/H)
     */
    public int getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Returns whether or not a vehicle is capable of traversing through mines.
     * @return True if able to withstand mines, false otherwise.
     */
    public boolean isMineResistant() {
        return mineResistant;
    }
    
    public String getName() {
        return name;
    }

    /**
     * Returns a map containing the traversability values of a vehicle with
     * their associated Terrain Type.
     * @return 
     */
    public Map<TerrainType, Double> getTraversabilityMap() {
        return traversabilityMap;
    }

    
    public static Vehicle createDefaultVehicle() {
        Map<TerrainType, Double> map = new HashMap<TerrainType, Double>();
        map.put(TerrainType.FOREST_LIGHT, 1.0);
        map.put(TerrainType.FOREST_MEDIUM, .9);
        map.put(TerrainType.FOREST_HEAVY, .8);
        map.put(TerrainType.WATER_WADABLE, .6);
        map.put(TerrainType.WATER_SHALLOW, .2);
        map.put(TerrainType.WATER_MIDDEPTH, .2);
        map.put(TerrainType.WATER_DEEP, .2);
        map.put(TerrainType.SURFACE_IMPASSABLE, 0.0);
        map.put(TerrainType.SURFACE_SAND, .6);
        map.put(TerrainType.SURFACE_MUD, .8);
        map.put(TerrainType.SURFACE_SNOW, .4);
        map.put(TerrainType.SURFACE_ICE, .3);
        map.put(TerrainType.SURFACE_GRASS, 1.0);
        map.put(TerrainType.SURFACE_TALLGRASS, .9);
        map.put(TerrainType.SURFACE_EARTH_BARE, 1.0);
        map.put(TerrainType.SURFACE_EARTH_RED, 1.0);
        map.put(TerrainType.SURFACE_SWAMP, .1);
        map.put(TerrainType.SURFACE_MARSH, .1);
        map.put(TerrainType.SURFACE_ROCKY, .3);
        map.put(TerrainType.SURFACE_FARMLAND, .9);
        map.put(TerrainType.SURFACE_PAVED, 1.0);
        map.put(TerrainType.OBSTACLE_BUILDING, 0.0);
        map.put(TerrainType.OBSTACLE_OTHER, 0.0);

        Vehicle human = new Vehicle("Human", 1.763, .5, 5, false, map);
        return human;
    }
    
    @Override
    public String toString() {
        return "Name: " + name + " Height: " + height + " Width: " + width + " MaxSpeed: "
                + maxSpeed + "km/h " + " Mine Resistant: " + mineResistant
                + " Traversability Map: " + traversabilityMap;
    }
}
