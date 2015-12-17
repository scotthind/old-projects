package org.rowan.pathfinder.pathfinder;

/**
 * Enum <code>TerrainType</code> holds all the different possible terrain types.
 * 
 * @author Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public enum TerrainType {
    FOREST_LIGHT,       //Small vehicles may be able to travel through
    FOREST_MEDIUM,      //Can only be traversed by foot
    FOREST_HEAVY,       //Probably can't be traversed
    WATER_WADABLE,      //Can walk through it
    WATER_SHALLOW,      //Too deep to wade but unsafe for boats
    WATER_MIDDEPTH,     //Safe for small boats only
    WATER_DEEP,         //Safe for all boats
    SURFACE_IMPASSABLE, //Any type of terrain that is virtually impassable by individuals, and not passable by vehicles
    SURFACE_SAND,       //
    SURFACE_MUD,        //
    SURFACE_SNOW,       //
    SURFACE_ICE,        //
    SURFACE_GRASS,      //
    SURFACE_TALLGRASS,  //
    SURFACE_EARTH_BARE, //
    SURFACE_EARTH_RED,  //
    SURFACE_SWAMP,      //Tree filled wetlands
    SURFACE_MARSH,      //Tree-less wetlands, lots of cattails and weeds and grass
    SURFACE_ROCKY,      //
    SURFACE_FARMLAND,   //
    SURFACE_PAVED,      //
    OBSTACLE_BUILDING,  //
    OBSTACLE_OTHER      //
}
