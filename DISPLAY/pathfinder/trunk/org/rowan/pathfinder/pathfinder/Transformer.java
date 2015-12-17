package org.rowan.pathfinder.pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.rowan.linalgtoolkit.BoundingBox2D;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.shapes2d.Segment2D;

/**
 * Class <code>Transformer</code> responsibilities include:
 * 1) Adding all possible <code>Traversable</code>s to a list.
 *      - <code>RoadSegment</code> will be based on provided road data.
 *      - <code>OffRoadSegment</code> will be added based on Terrain data.
 * 2) Converting <code>Underpass</code> to Clearance and assigning it to the
 *    appropriate <code>Traversable</code> (if applicable).
 * 3) Adding an <code>Event</code>s to a <code>Traversable</code> if it is
 *    contained within it. (if applicable).
 * 4) Adding a <code>Terrain</code> to a <code>Traversable</code> if it is
 *    contained within it. (if applicable).
 * 5) Adding a <code>SpeedLimit</code> to it's appropriate <code>RoadSegment</code>
 *
 * @author Shahid Akhter, Dan Urbano
 * @version 1.0
 * @since 1.0
 */
public class Transformer {

    /** Any traversable with the cost infinity must never be traversed */
    static final double INFINITY = -1;
    /** The maximum distance between two points to be considered "within proximity" */

    private static double MAX_PROXIMITY_DISTANCE_TERRAIN = .0002; //needs to be changed
    private static double MAX_PROXIMITY_DISTANCE_ROAD = .0001; //needs to be changed
    private static double MAX_PROXIMITY_DISTANCE_ENTERED_COORDS = .001; //needs to be changed
    /** The set of all traversables (will be created in transform() */
    private static Set<Traversable> traversables;

        /** The mode that the transformer should operate in */
    public enum TransformMode {
        ROAD_ONLY, TERRAIN_ONLY, ROAD_AND_TERRAIN
    };

    /** Transform will attach all events, speed limits, and underpasses to
     * all RoadSegments. It will also create a set of OffRoadSegments based on
     * the vehicles that need to travel from start to end and the set of given
     * Terrains.
     * @param roads A set of traversable roads. Could be null if only traveling
     *              off-road.
     * @param events A set of events that have occurred in the surrounding area.
     *               Could be null if no events have taken place in the area.
     * @param speedLimits A set of speed limits for the roads. Could be null
     *                    if there are no roads.
     * @param terrains A set of terrains that will be used in finding off-road
     *                 paths. Could be null if only traveling on roads.
     * @param underpasses A set of underpasses that will be converted to clearances.
     * @param vehicles A set of all vehicles that need to travel form start to end.
     * @param safetyCoefficient The user defined value which represents the
     *                          importance of safety.
     * @param speedCoefficient The user defined value which represents the
     *                          importance of speed.
     * @param distanceCoefficient The user defined value which represents the
     *                          importance of distance.
     * @param start A vector representing the start location of travel.
     * @param end A vector representing the end location of travel.
     * @param mode The mode that the transformer should operate in. If
     *             ROAD_ONLY, the terrains parameter will be ignored. If
     *             TERRAIN_ONLY, the roads, speedLimits, and underpasses
     *             will be ignored.
     * @return An ArrayList containing the start and end traversables.
     */
    public static ArrayList<Traversable> transform(Set<RoadSegment> roads, Set<Event> events,
            Set<SpeedLimit> speedLimits, Set<Terrain> terrains, Set<Underpass> underpasses,
            Set<Vehicle> vehicles, double safetyCoefficient, double speedCoefficient,
            double distanceCoefficient, Vector2D start, Vector2D end, TransformMode mode) 
            throws IllegalArgumentException {

        
        if (vehicles == null) {
            vehicles = new HashSet<Vehicle>();
            vehicles.add(Vehicle.createDefaultVehicle());
        }
        Traversable s = new RoadSegment(new Segment2D(start, start), "");
        Traversable e = new RoadSegment(new Segment2D(end, end), "");
        s = proximityCheck(s, roads, terrains, mode, vehicles);
        e = proximityCheck(e, roads, terrains, mode, vehicles);
        if (s == null) {
            throw new IllegalArgumentException("The start location was invalid. Please try again.");
        } else if (e == null) {
            throw new IllegalArgumentException("The end location was invalid. Please try again.");
        }

        Set<OffRoadSegment> offroads = new HashSet<OffRoadSegment>();
        switch (mode) {
            case TERRAIN_ONLY:
                roads = Collections.EMPTY_SET;
            case ROAD_AND_TERRAIN:
                setTerrainNeighbors(terrains);
                offroads = calculateOffRoadSegments(mode, vehicles, terrains, s, e);
                break;
        }

        attachSpeedLimitsToRoads(speedLimits, roads);
      //  System.err.println("Attaching speed limits finished...");
    //    System.err.println("Roads Size: " + ((roads == null ? 0 : roads.size())));
    //    System.err.println("Offroads Size: " + (offroads == null ? 0 : offroads.size()));
        traversables = splitIntersectingSegments(roads, offroads, mode);
     //   System.err.println("Split Intersecting Segments Finished...");
        int rs = 0, os = 0;
        for (Traversable t : traversables) {
            if (t instanceof RoadSegment) {
                rs++;
            } else {
                os++;
            }
        }
    //    System.err.println("Roads Size: " + rs);
     //   System.err.println("Offroads Size: " + os);
        traversables.add(e);
        traversables.add(s);

        attachEventsToTraversables(events, traversables);
      //  System.err.println("Attaching Events Finished...");
        attachUnderpassesToRoads(underpasses, traversables);
      //  System.err.println("Attaching Underpasses Finished...");
        calculateWeights(vehicles, safetyCoefficient, speedCoefficient, distanceCoefficient);
      //  System.err.println("Calculating weights Finished!");
        setNeighbors(traversables);
      //  System.err.println("Setting Neighbors Finished...");

        ArrayList startAndEnd = new ArrayList();
        startAndEnd.add(s);
        startAndEnd.add(e);
        return startAndEnd;
    }

    /**
     * Given a set of traversables and events, attach all intersecting events to
     * the traversables they intersect.
     * @param events The set of events to transform.
     * @param traversables The set of traversables to attach event references to.
     */
    private static void attachEventsToTraversables(Set<Event> events, Set<Traversable> traversables) {
        if (traversables == null || traversables.isEmpty() || events == null || events.isEmpty()) {
            return;
        }
        // algorithm runs in O(n^2), where n = max(numEvents, numTraversables)
        // this algorithm should be replaced with a faster one if possible
        for (Traversable traversable : traversables) {
            for (Event event : events) {
                if (event.getBoundary().intersects(traversable.getSegment())) {
                    traversable.getEvents().add(event);
                }
            }
        }
    }

    /**
     * Given a set of roads and speed limits, attach all speed limits to
     * the road segments they correspond to.
     * @param speedLimits The set of speed limits to add to roads..
     * @param roads The set of roads to attach event references to.
     */
    private static void attachSpeedLimitsToRoads(Set<SpeedLimit> speedLimits, Set<RoadSegment> roads) {
        if (roads.isEmpty() || speedLimits == null || speedLimits.isEmpty()) {
            return;
        }
        for (SpeedLimit sl : speedLimits) {
            if (sl.getStart() == null) {
                for (RoadSegment road : roads) {
                    if (road.getRoadName().trim().equalsIgnoreCase(sl.getRoadName().trim())) {
                        road.setSpeedLimit(sl.getLimit());
                    }
                }
            } else {
                double distance;
                double sMinDistance = MAX_PROXIMITY_DISTANCE_ROAD;
                double eMinDistance = MAX_PROXIMITY_DISTANCE_ROAD;
                RoadSegment sClosestSegment = null, eClosestSegment = null;
                boolean sCloserToSegmentStart = true, eCloserToSegmentStart = true;
                for (RoadSegment road : roads) {
                    if (road.getRoadName().trim().equalsIgnoreCase(sl.getRoadName().trim())) {
                        // is the start of the segment near the start of the speed limit?
                        distance = Logic2D.getStart(road.getSegment()).distance(sl.getStart());
                        if (distance < sMinDistance) {
                            sMinDistance = distance;
                            sClosestSegment = road;
                            sCloserToSegmentStart = true;
                        }
                        // is the end of the segment near the start of the speed limit?
                        distance = Logic2D.getEnd(road.getSegment()).distance(sl.getStart());
                        if (distance < sMinDistance) {
                            sMinDistance = distance;
                            sClosestSegment = road;
                            sCloserToSegmentStart = false;
                        }
                        // is the start of the segment near the end of the speed limit?
                        distance = Logic2D.getStart(road.getSegment()).distance(sl.getEnd());
                        if (distance < eMinDistance) {
                            eMinDistance = distance;
                            eClosestSegment = road;
                            eCloserToSegmentStart = true;
                        }
                        // is the end of the segment near the end of the speed limit?
                        distance = Logic2D.getEnd(road.getSegment()).distance(sl.getEnd());
                        if (distance < sMinDistance) {
                            eMinDistance = distance;
                            eClosestSegment = road;
                            eCloserToSegmentStart = false;
                        }
                    }
                }
                if (sClosestSegment == null || eClosestSegment == null) {
                    //TODO ERROR OUT, COULDN'T FIND A ROAD SEGMENT CLOSE
                    //ENOUGH TO ONE OF THE SPEED LIMIT LOCATIONS,
                    //CAN'T ATTACH SPEED LIMIT TO A ROAD
                } else {
                    // find the path between sClosestSegment and eClosestSegment
                    // and set the speed limit of all segments in between
                    List<RoadSegment> list = new ArrayList<RoadSegment>();
                    Vector2D end = eCloserToSegmentStart
                            ? Logic2D.getStart(eClosestSegment.getSegment())
                            : Logic2D.getEnd(eClosestSegment.getSegment());
                    if (findPath(sClosestSegment, end, list)) {
                        if (!sCloserToSegmentStart) {
                            list.remove(0);
                        }
                        setSpeedLimits(list, sl.getLimit());
                    } else {
                        Set<Traversable> travs = new HashSet<Traversable>();
                        for (RoadSegment r : roads) {
                            travs.add(r);
                        }
                        RoadSegment otherWay = findDuplicateSegment(sClosestSegment, travs);
                        list.clear();
                        if (otherWay == null) {
                            //TODO ERROR OUT COULDN'T FIND PATH FROM START TO END
                        } else if (findPath(otherWay, end, list)) {
                            if (!sCloserToSegmentStart) {
                                list.remove(0);
                            }
                            setSpeedLimits(list, sl.getLimit());
                        } else {
                            //TODO ERROR OUT WITH SAME ERROR AS OTHERWAY==NULL
                        }
                    }
                }
            }
        }
    }

    /**
     * Given a road segment and a set of traversables, find the road segment whose
     * start point is the same as the given road segment's end point and whose
     * end point is the same as the given road segment's start point.
     * @param road The road segment to find the duplicate of.
     * @param travs The set of traversables to search for the duplicate. Only
     *              RoadSegments will be considered, not OffRoadSegments.
     * @return The duplicate road segment or null if none could be found.
     */
    private static RoadSegment findDuplicateSegment(RoadSegment road, Set<Traversable> travs) {
        for (Traversable trav : travs) {
            if (trav instanceof RoadSegment) {
                RoadSegment temp = (RoadSegment) trav;
                if (Logic2D.getStart(road.getSegment()).equals(Logic2D.getEnd(trav.getSegment()))
                        && Logic2D.getEnd(road.getSegment()).equals(Logic2D.getStart(trav.getSegment()))) {
                    return temp;
                }
            }
        }
        return null;
    }
    
    /**
     * Given a list of road segments, set the speed limit of every segment.
     * @param list The list of road segments to set the speed limit of.
     * @param limit The speed limit to set.
     */
    private static void setSpeedLimits(Iterable<RoadSegment> list, int limit) {
        for (RoadSegment road : list) {
            road.setSpeedLimit(limit);
        }
    }

    /**
     * Given a RoadSegment and an end point, find a path of RoadSegments leading
     * from the start to the end.
     * @param start The RoadSegment to start from.
     * @param end The point to end at.
     * @param list An empty list which will be filled with the correct path if
     *             a path was found, or arbitrary elements otherwise.
     * @return true if a path was found, false otherwise.
     */
    private static boolean findPath(RoadSegment start, Vector2D end, List<RoadSegment> list) {
        // this path may be valid, store the start of this path in the list
        list.add(start);
        boolean found = false;
        for (Traversable t : start.getNeighbors()) {
            if (t instanceof RoadSegment) {
                // iterate over all neighbors which are road segments with
                // the same name as the start road segment
                RoadSegment temp = (RoadSegment) t;
                if (temp.getRoadName().trim().equalsIgnoreCase(start.getRoadName().trim())) {
                    // base case
                    if (Logic2D.getStart(temp.getSegment()).equals(end)) {
                        list.remove(start);
                        return true;
                    } else if (Logic2D.getEnd(temp.getSegment()).equals(end)) {
                        return true;
                    }

                    // recursive case
                    if (!list.contains(temp)) {
                        found = findPath(temp, end, list);
                        if (found) {
                            return true;
                        }
                    }
                }
            }
        }
        list.remove(start);
        return false;
    }

    /**
     * Given a set of traversables and underpasses, add the max clearance height
     * to each road in the set of traversables based on any underpasses
     * that intersect (or are close enough to) the road.
     * @param underpasses The set of underpasses to add to roads. 
     * @param traversables The set of traversables to attach an underpass to. 
     */
    private static void attachUnderpassesToRoads(Set<Underpass> underpasses, Set<Traversable> traversables) {
        if (traversables.isEmpty() || underpasses == null || underpasses.isEmpty()) {
            return;
        }
        for (Underpass underpass : underpasses) {
            double distance;
            double minDistance = MAX_PROXIMITY_DISTANCE_ROAD;
            RoadSegment closestSegment = null;
            for (Traversable traversable : traversables) {
                if (traversable instanceof RoadSegment) {
                    RoadSegment road = (RoadSegment) traversable;
                    if (road.getRoadName() != null) {
                        if (road.getRoadName().trim().equalsIgnoreCase(underpass.getUnderRoad().trim())) {
                            distance = road.getSegment().distance(underpass.getLocation());
                            // is the road segment near the underpass?
                            if (distance < minDistance) {
                                minDistance = distance;
                                closestSegment = road;
                            }
                        }
                    }
                }
            }
            if (closestSegment == null) {
                //TODO ERROR OUT, COULDN'T ATTACH UNDERPASS
            } else {
                // attach underpass to the road
                closestSegment.addClearanceHeight(underpass.getHeight());
                findDuplicateSegment(closestSegment, traversables).addClearanceHeight(underpass.getHeight());
            }
        }
    }

    /**
     * Set the neighbors of every traversable.
     * @param traversables The set of roads to find neighbors for.
     */
    private static void setNeighbors(Set<Traversable> traversables) {
        HashMap<String, ArrayList<Traversable>> endMap = new HashMap<String, ArrayList<Traversable>>();
        Vector2D start, end;
        String str;

        for(Traversable t: traversables)
        {
            t.getNeighbors().clear();
        }

        for (Traversable t : traversables) {
            end = Logic2D.getEnd(t.getSegment());
            str = end.toString();
            if (endMap.containsKey(str)) {
                endMap.get(str).add(t);
            } else {
                ArrayList newEnd = new ArrayList<Traversable>();
                newEnd.add(t);
                endMap.put(str, newEnd);
            }
        }
        for (Traversable t : traversables) {
            start = Logic2D.getStart(t.getSegment());
            str = start.toString();
            ArrayList<Traversable> travsEndingAtMe = endMap.get(str);
            if (travsEndingAtMe != null) {
                for (Traversable trav : travsEndingAtMe) {
                    if (!(trav.equals(t))) {
                        trav.getNeighbors().add(t);
                    }
                }
            }
        }
    }

    /**
     * Sets the neighbors of every Terrain
     * @param terrains The set of terrains to find neighbors for.
     */
    public static void setTerrainNeighbors(Set<Terrain> terrains) {
        for (Terrain terrainA : terrains) {
            for (Terrain terrainB : terrains) {
                if (Logic2D.isNeighbor(terrainA.getBoundary(), terrainB.getBoundary())
                        && !(terrainA.equals(terrainB))) {
                    terrainA.addNeighbor(terrainB);
                }
            }
        }

    }

    /**
     * Given a set of roads and offroads, combine them into one set of
     * Traversables.
     * @param offroads The set of OffRoadSegments to combine.
     * @param roads The set of RoadSegments to combine.
     * @return The set of Traversables making up of all roads and offroads.
     */
    private static Set<Traversable> combine(Set<RoadSegment> roads, Set<OffRoadSegment> offroads) {
        Set<Traversable> traversables = new HashSet<Traversable>();
        if (offroads != null) {
            for (OffRoadSegment offroad : offroads) {
                traversables.add(offroad);
            }
        }
        if (roads != null) {
            for (RoadSegment road : roads) {
                traversables.add(road);
            }
        }
        return traversables;
    }

    private static Set<OffRoadSegment> calculateOffRoadSegments(TransformMode mode,
            Set<Vehicle> vehicles, Set<Terrain> terrains, Traversable s, Traversable e) {

        Set<OffRoadSegment> offroads = new HashSet<OffRoadSegment>();
        HashMap<Terrain, List> linkMap = new HashMap<Terrain, List>();
        Terrain startTerrain = null, endTerrain = null;
        double lowestSpeed;
        for (Terrain t : terrains) {
            linkMap.put(t, new ArrayList());

            if (startTerrain == null && t.getBoundary().intersects(s.getSegment())) {
                startTerrain = t;
            }
            if (endTerrain == null && t.getBoundary().intersects(e.getSegment())) {
                endTerrain = t;
            }
        }

        // the main points of the start and end terrains should not
        // be the centroids, they should be the start/end locations
        if (startTerrain != null) {
            startTerrain.setMainPoint(Logic2D.getStart(s.getSegment()));
        }
        if (endTerrain != null) {
            endTerrain.setMainPoint(Logic2D.getEnd(e.getSegment()));
        }

        // calculate offroads from terrain to terrain
        for (Terrain t : terrains) {
            for (Terrain n : t.getNeighbors()) {
                if (!(linkMap.get(t).contains(n))) {
                    Vector2D tEnd = Logic2D.closestPoint(t.getMainPoint(), n.getBoundary());
                    Vector2D nEnd = Logic2D.closestPoint(n.getMainPoint(), t.getBoundary());

                    Vector2D midpoint = new Vector2D((tEnd.getX() + nEnd.getX()) / 2,
                            (tEnd.getY() + nEnd.getY()) / 2);

                    Segment2D tSegment = new Segment2D(t.getMainPoint(), midpoint);
                    Segment2D tSegmentReversed = new Segment2D(midpoint, t.getMainPoint());
                    Segment2D nSegment = new Segment2D(n.getMainPoint(), midpoint);
                    Segment2D nSegmentReversed = new Segment2D(midpoint, n.getMainPoint());


                    lowestSpeed = findLowestSpeed(vehicles, t.getType());
                    OffRoadSegment tPath = new OffRoadSegment(tSegment, t.getType(), lowestSpeed);
                    OffRoadSegment tPathReverse = new OffRoadSegment(tSegmentReversed, t.getType(), lowestSpeed);
                    lowestSpeed = findLowestSpeed(vehicles, n.getType());
                    OffRoadSegment nPath = new OffRoadSegment(nSegment, n.getType(), lowestSpeed);
                    OffRoadSegment nPathReverse = new OffRoadSegment(nSegmentReversed, n.getType(), lowestSpeed);
                    offroads.add(tPath);
                    offroads.add(nPath);
                    offroads.add(nPathReverse);
                    offroads.add(tPathReverse);
                    n.addUnityPoint(midpoint);
                    t.addUnityPoint(midpoint);
                    linkMap.get(n).add(t);
                }
            }

            if (t.getUnityPoints().size() != 2) {
                List<Vector2D> cycleOrder = Logic2D.createCycle(t.getUnityPoints());
                if (!(cycleOrder.isEmpty())) {
                    for (int i = 0; i < cycleOrder.size(); i++) {
                        if ((i + 1) == cycleOrder.size()) {
                            Segment2D offRoad = new Segment2D(cycleOrder.get(i), cycleOrder.get(0));
                            Segment2D reverseOffRoad = new Segment2D(cycleOrder.get(0), cycleOrder.get(i));
                            lowestSpeed = findLowestSpeed(vehicles, t.getType());
                            offroads.add(new OffRoadSegment(offRoad, t.getType(), lowestSpeed));
                            offroads.add(new OffRoadSegment(reverseOffRoad, t.getType(), lowestSpeed));
                        } else {
                            Segment2D offRoad = new Segment2D(cycleOrder.get(i), cycleOrder.get(i + 1));
                            Segment2D reverseOffRoad = new Segment2D(cycleOrder.get(i + 1), cycleOrder.get(i));
                            lowestSpeed = findLowestSpeed(vehicles, t.getType());
                            offroads.add(new OffRoadSegment(offRoad, t.getType(), lowestSpeed));
                            offroads.add(new OffRoadSegment(reverseOffRoad, t.getType(), lowestSpeed));
                        }
                    }
                }
            } else {
                List<Vector2D> cycleOrder = new LinkedList(t.getUnityPoints());
                Segment2D offRoad = new Segment2D(cycleOrder.get(0), cycleOrder.get(1));
                Segment2D reverseOffRoad = new Segment2D(cycleOrder.get(1), cycleOrder.get(0));
                lowestSpeed = findLowestSpeed(vehicles, t.getType());
                offroads.add(new OffRoadSegment(offRoad, t.getType(), lowestSpeed));
                offroads.add(new OffRoadSegment(reverseOffRoad, t.getType(), lowestSpeed));
            }

        }
        return offroads;
    }

    private static Traversable proximityCheck(Traversable t, Set<RoadSegment> roads,
            Set<Terrain> terrains, TransformMode mode, Set<Vehicle> vehicles) {
        double distance;
        double minDistance = MAX_PROXIMITY_DISTANCE_ENTERED_COORDS;
        Vector2D fromPoint = Logic2D.getEnd(t.getSegment());
        Vector2D toPoint = null;
        boolean inUsableTerrain;
        TerrainType type;
        
        switch (mode) {
            case ROAD_ONLY:
                // find the closest road to "t"
                for (RoadSegment road : roads) {

                    distance = Logic2D.getStart(road.getSegment()).distance(fromPoint);
                    if (distance < minDistance) {
                        minDistance = distance;
                        toPoint = Logic2D.getStart(road.getSegment());
                    }
                }
                if (toPoint == null) {
                    return null;
                } else {
                  //  System.err.println(new RoadSegment(new Segment2D(toPoint, toPoint), ""));
                    return new RoadSegment(new Segment2D(toPoint, toPoint), "");
                }
            case TERRAIN_ONLY:
                // is "t" in usuable terrain?
                inUsableTerrain = false;
                type = null;
                for (Terrain terrain : terrains) {
                    if (!inUsableTerrain && terrain.getBoundary().intersects(t.getSegment())) {
                        inUsableTerrain = true;
                        type = terrain.getType();
                    }
                }
                if (!inUsableTerrain) {
                    Terrain terrain = null;
                    for (Terrain ter : terrains) {
                        if (!inUsableTerrain && ter.getBoundary().distance(t.getSegment()) < MAX_PROXIMITY_DISTANCE_TERRAIN) {
                            inUsableTerrain = true;
                            terrain = ter;
                        }
                    }
                    if (inUsableTerrain) {
                        Vector2D newPoint = Logic2D.closestPoint(Logic2D.getStart(t.getSegment()), terrain.getBoundary());
                        double lowestSpeed = findLowestSpeed(vehicles, terrain.getType());
                        return new OffRoadSegment(new Segment2D(newPoint, newPoint), terrain.getType(), lowestSpeed);
                    } else {
                        // couldn't create offroad segment from "t" because "t"
                        // is not inside a valid terrain boundary
                        return null;
                    }
                }
                double lowestSpeed = findLowestSpeed(vehicles, type);
                return new OffRoadSegment(t.getSegment(), type, lowestSpeed);
            case ROAD_AND_TERRAIN:
                // find the closest road to "t"
                for (RoadSegment road : roads) {
                    distance = Logic2D.getStart(road.getSegment()).distance(fromPoint);
                    if (distance < minDistance) {
                        minDistance = distance;
                        toPoint = Logic2D.getStart(road.getSegment());
                    }
                }
                if (toPoint != null) {
                    return new RoadSegment(new Segment2D(toPoint, toPoint), "");
                }
                // no closest road found, attempt to create offroad segments
                // that will lead us from "t" to the nearest road
                inUsableTerrain = false;
                type = null;
                for (Terrain terrain : terrains) {
                    if (!inUsableTerrain && terrain.getBoundary().intersects(t.getSegment())) {
                        inUsableTerrain = true;
                        type = terrain.getType();
                    }
                }
                if (!inUsableTerrain) {
                    Terrain terrain = null;
                    for (Terrain ter : terrains) {
                        if (!inUsableTerrain && ter.getBoundary().distance(t.getSegment()) < MAX_PROXIMITY_DISTANCE_TERRAIN) {
                            inUsableTerrain = true;
                            terrain = ter;
                        }
                    }
                    if (inUsableTerrain) {
                        Vector2D newPoint = Logic2D.closestPoint(Logic2D.getStart(t.getSegment()), terrain.getBoundary());
                        lowestSpeed = findLowestSpeed(vehicles, terrain.getType());
                        return new OffRoadSegment(new Segment2D(newPoint, newPoint), terrain.getType(), lowestSpeed);
                    } else {
                        // couldn't create offroad segments from "t" to nearest road
                        // because "t" is not inside a valid terrain boundary
                        return null;
                    }
                } else {
                    lowestSpeed = findLowestSpeed(vehicles, type);
                    return new OffRoadSegment(t.getSegment(), type, lowestSpeed);
                }
        }

        return t;
    }

    /**
     * This method calculates the distance, safety, and speed value of each
     * traversable. After the values are calculated, they would need to be
     * normalized. After normalization, the coefficient will be applied to the
     * values, added together, and the cost is set to this new summed value.
     * @param vehicles A set of vehicles that will be traveling.
     * @param safetyCoefficient The user defined value which represents the
     *                          importance of safety.
     * @param speedCoefficient The user defined value which represents the
     *                          importance of speed.
     * @param distanceCoefficient The user defined value which represents the
     *                          importance of distance.
     */
    private static void calculateWeights(Set<Vehicle> vehicles, double safetyCoefficient,
            double speedCoefficient, double distanceCoefficient) {

        //TODO Handle null vehicles possibly

        for (Traversable t : traversables) {
            if (t.isValid(vehicles)) {
                t.calculateDistanceValue();
                t.calculateSafetyValue();
                t.calculateSpeedValue(vehicles);
            }
        }

        double[] minMaxValues = getMaxMinValues();
        double minDis = minMaxValues[0];
        double minSpd = minMaxValues[1];
        double minSaf = minMaxValues[2];
        double maxDis = minMaxValues[3];
        double maxSpd = minMaxValues[4];
        double maxSaf = minMaxValues[5];

        for (Traversable t : traversables) {
            if (t.getCost() != INFINITY) {
                double totalCost = 0;
                totalCost += distanceCoefficient * ((t.getDistanceValue() - minDis) / (maxDis - minDis));
                totalCost += speedCoefficient * ((t.getSpeedValue() - minSpd) / (maxSpd - minSpd));
                totalCost += safetyCoefficient * ((t.getSafetyValue() - minSaf) / (maxSaf - minSaf));
                t.setCost(totalCost);
            }
        }
    }

    /**
     * Return the maximum values for all possible weights.
     * @return The maximum and minimum distance, speed, and safety weights
     *         for all traversables.
     */
    private static double[] getMaxMinValues() {
        double minDis = Double.MAX_VALUE;
        double minSpd = Double.MAX_VALUE;
        double minSaf = Double.MAX_VALUE;
        double maxDis = Double.MIN_VALUE;
        double maxSpd = Double.MIN_VALUE;
        double maxSaf = Double.MIN_VALUE;

        for (Traversable t : traversables) {
            if (t.getDistanceValue() < minDis) {
                minDis = t.getDistanceValue();
            }
            if (t.getSpeedValue() < minSpd) {
                minSpd = t.getSpeedValue();
            }
            if (t.getSafetyValue() < minSaf) {
                minSaf = t.getSafetyValue();
            }
            if (t.getDistanceValue() > maxDis) {
                maxDis = t.getDistanceValue();
            }
            if (t.getSpeedValue() > maxSpd) {
                maxSpd = t.getSpeedValue();
            }
            if (t.getSafetyValue() > maxSaf) {
                maxSaf = t.getSafetyValue();
            }
        }
        return new double[]{minDis, minSpd, minSaf, maxDis, maxSpd, maxSaf};
    }

    /**
     * Given a set of traversables, split all intersecting segments into two
     * separate segments.
     * @param traversables The set of traversables to split intersections.
     * @param mode The mode of the transformer.
     * @return A new set of traversables where no two offroad segments intersect
     *         and no offroad segment intersects with a road segment.
     */
    private static Set<Traversable> splitIntersectingSegments(Set<RoadSegment> roads, Set<OffRoadSegment> offRoads, TransformMode mode) {
        Set<Traversable> allSegments = new HashSet<Traversable>();
        if (mode.equals(TransformMode.ROAD_ONLY)) {
            // roads can never split with other roads, return the orginal roads
            return combine(roads, null);
        } else if (mode.equals((TransformMode.TERRAIN_ONLY))) {
            // ignore all roads, run algorithm just on offroads
            allSegments = combine(null, offRoads);
        } else {
            // run algorithm with both offroads and roads
            allSegments = combine(roads, offRoads);
        }
        Set<Traversable> newSegments = new HashSet<Traversable>();
        TreeMap<Double, List<Traversable>> mapx = new TreeMap<Double, List<Traversable>>();
        TreeMap<Double, List<Traversable>> mapy = new TreeMap<Double, List<Traversable>>();
        HashMap<Traversable, Set<Traversable>> possibleIntersectorsMap = new HashMap<Traversable, Set<Traversable>>();
        BoundingBox2D boundingBox;
        
        
        // pre-processing. traversables will only split if they intersect
        // an offroad. find the bouding box for every offroad. place the X
        // value of it's left side and the X value of it's right side
        // into the mapX. (Every entry between the two values corresponds to
        // a bounding box whose left or right side is within this bounding box).
        // Do the same for mapY.
        for (Traversable trav : allSegments) {
            boundingBox = trav.getSegment().boundingBox();
            double x1 = boundingBox.getA().getX();
            double x2 = boundingBox.getB().getX();
            double y1 = boundingBox.getA().getY();
            double y2 = boundingBox.getB().getY();
            putBoundingBoxInMap(x1, x2, y1, y2, mapx, mapy, trav);
            Set<Traversable> set = new HashSet<Traversable>();
            possibleIntersectorsMap.put(trav, set);
        }

        // Loop through all traversables and split as necessary.
        for (Traversable t1 : allSegments) {
            // the possibleX and possibleY maps coorespond to every offroad segment
            // which has a bounding box whose left OR right side is within the left
            // and right sides of t1's bounding box and whose y top OR bottom
            // side is within the top and bottom sides of t1's bounding box, thus
            // limiting the amount of possible intersecting offroad segments greatly
            boundingBox = t1.getSegment().boundingBox();
            double x1 = boundingBox.getA().getX();
            double x2 = boundingBox.getB().getX();
            double y1 = boundingBox.getA().getY();
            double y2 = boundingBox.getB().getY();
            NavigableMap<Double, List<Traversable>> possibleX = mapx.subMap(x1, true, x2, true);
            NavigableMap<Double, List<Traversable>> possibleY = mapy.subMap(y1, true, y2, true);

            // find the number of sides of each bounding box that intersects
            // t1's bounding box. stores these values into the found map
            Set<Traversable> possibleIntersectors = possibleIntersectorsMap.get(t1);
            HashMap<Traversable, XYNum> found = new HashMap<Traversable, XYNum>();
            for (Double x : possibleX.keySet()) {
                for (Traversable tX : possibleX.get(x)) {
                    if (!tX.getSegment().equals(t1.getSegment())) {
                        if (!found.containsKey(tX)) {
                            found.put(tX, new XYNum());
                        } else {
                            found.get(tX).x++;
                        }
                     }
                }
            }
            for (Double y : possibleY.keySet()) {
                for (Traversable tY : possibleY.get(y)) {
                    if (!tY.getSegment().equals(t1.getSegment())) {
                        if (!found.containsKey(tY)) {
                            found.put(tY, new XYNum());
                        } else {
                            found.get(tY).y++;
                        }
                    }
                }
            }
            
            for (Traversable t2 : found.keySet()) {
                int numX = found.get(t2).x;
                int numY = found.get(t2).y;
                if (numX == 1 && numY == 1) {
                    possibleIntersectors.add(t2);
                } else if (numX == 2 && numY == 0) {
                    boundingBox = t2.getSegment().boundingBox();
                    double t2y1 = boundingBox.getA().getY();
                    double t2y2 = boundingBox.getB().getY();
                    if (t2y1 < y1 && t2y2 > y2) {
                        possibleIntersectors.add(t2);
                    }
                } else if (numX == 0 && numY == 2) {
                    boundingBox = t2.getSegment().boundingBox();
                    double t2x1 = boundingBox.getA().getX();
                    double t2x2 = boundingBox.getB().getX();
                    if (t2x1 < x1 && t2x2 > x2) {
                        possibleIntersectors.add(t2);
                    }
                } else if ((numX == 2 && numY == 1) || (numX == 1 && numY == 2)) {
                    possibleIntersectors.add(t2);
                    possibleIntersectorsMap.get(t2).add(t1);
                } else if (numX == 2 && numY == 2) {
                    possibleIntersectors.add(t2);
                    possibleIntersectorsMap.get(t2).add(t1);
                }
            }
        }
        
        for (Traversable t1 : allSegments) {
            Set<Traversable> possibleIntersectors = possibleIntersectorsMap.get(t1);
            List<TraversableAndPOI> intersectors = new ArrayList<TraversableAndPOI>();
            for (Traversable t2 : possibleIntersectors) {
                if (t1 instanceof RoadSegment && t2 instanceof RoadSegment) {
                    continue;
                }

                // split segments if at least one is not a road
                if (t1.getSegment().intersects(t2.getSegment())) {
                    Vector2D t1s = Logic2D.getStart(t1.getSegment());
                    Vector2D t1e = Logic2D.getEnd(t1.getSegment());
                    Vector2D t2s = Logic2D.getStart(t2.getSegment());
                    Vector2D t2e = Logic2D.getEnd(t2.getSegment());
                    if (!t1s.equals(t2s) && !t1s.equals(t2e) && !t1e.equals(t2s) && !t1e.equals(t2e)) {
                        // get the point of intersection
                        Vector2D poi = t1.getSegment().intersection(t2.getSegment()).getWorldVertices().get(0);
                        intersectors.add(new TraversableAndPOI(t2, poi, Logic2D.getStart(t1.getSegment())));
                    }
                }
            }

            // if there are no intersecting segments with this one, move on
            if (intersectors.isEmpty()) {
                newSegments.add(t1);
                continue;
            }

            // sort all intersecting segments based on the distance to t1's start
            // and for each intersecting segment, split up t1
            Collections.sort(intersectors);
            Vector2D prev = Logic2D.getStart(t1.getSegment());
            if (t1 instanceof RoadSegment) {
                RoadSegment r = (RoadSegment) t1;
                RoadSegment newSegment;
                for (TraversableAndPOI travPOI : intersectors) {
                    Vector2D poi = travPOI.POI;
                    if (!(poi.equals(prev))) {
                        // create new segments out of t1 from the start point
                        // to the point of intersection with the next segment
                        // after each iteration, change the "start point" to
                        // the point we ended at last time
                        newSegment = new RoadSegment(new Segment2D(prev, poi), r.getRoadName());
                        newSegment.setSpeedLimit(r.getSpeedLimit());
                        newSegments.add(newSegment);
                        prev = poi;
                    }
                }
                // add the very last segment from the last "start point" to the end of t1
                newSegment = new RoadSegment(new Segment2D(prev, Logic2D.getEnd(r.getSegment())), r.getRoadName());
                newSegment.setSpeedLimit(r.getSpeedLimit());
                newSegments.add(newSegment);
            } else if (t1 instanceof OffRoadSegment) {
                OffRoadSegment o = (OffRoadSegment) t1;
                OffRoadSegment newSegment;
                for (TraversableAndPOI travPOI : intersectors) {
                    Vector2D poi = travPOI.POI;
                    if (!(poi.equals(prev))) {
                        // create new segments out of t1 from the start point
                        // to the point of intersection with the next segment
                        // after each iteration, change the "start point" to
                        // the point we ended at last time
                        newSegment = new OffRoadSegment(new Segment2D(prev, poi), o.getTerrainType(), o.getSpeedLimit());
                        newSegments.add(newSegment);
                        prev = poi;
                    }
                }
                // add the very last segment from the last "start point" to the end of t1
                newSegment = new OffRoadSegment(new Segment2D(prev, Logic2D.getEnd(o.getSegment())), o.getTerrainType(), o.getSpeedLimit());
                newSegments.add(newSegment);
            }
        }
        return newSegments;
    }
    
    private static void putBoundingBoxInMap(double x1, double x2, double y1, double y2,
            TreeMap<Double, List<Traversable>> mapx, TreeMap<Double, List<Traversable>> mapy,
            Traversable trav) {
        addBoundingBoxToMapList(x1, mapx, trav);
        addBoundingBoxToMapList(x2, mapx, trav);
        addBoundingBoxToMapList(y1, mapy, trav);
        addBoundingBoxToMapList(y2, mapy, trav);
    }
    
    private static void addBoundingBoxToMapList(double coord,
            TreeMap<Double, List<Traversable>> map, Traversable trav) {
        if (map.get(coord) == null) {
            ArrayList<Traversable> list = new ArrayList<Traversable>();
            list.add(trav);
            map.put(coord, list);
        } else {
            map.get(coord).add(trav);
        }
    }

    /**
     * Finds the lowest speed of a set of vehicles based on the terrain type
     * @param vehicles A set of Vehicles
     * @param TerrainType The type of terrain
     * @return The lowest speed
     */
    private static double findLowestSpeed(Set<Vehicle> vehicles, TerrainType type) {
        double lowestSpeed = Double.MAX_VALUE;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getMaxSpeed() * vehicle.getTraversabilityMap().get(type) < lowestSpeed) {
                lowestSpeed = vehicle.getMaxSpeed() * vehicle.getTraversabilityMap().get(type);
            }
        }

        return lowestSpeed;
    }
}


