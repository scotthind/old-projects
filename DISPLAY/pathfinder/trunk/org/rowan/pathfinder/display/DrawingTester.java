/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rowan.pathfinder.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JApplet;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.pathfinder.pathfinder.Logic2D;
import org.rowan.pathfinder.pathfinder.OffRoadSegment;
import org.rowan.pathfinder.pathfinder.Path;
import org.rowan.pathfinder.pathfinder.RoadSegment;
import org.rowan.pathfinder.pathfinder.Terrain;
import org.rowan.pathfinder.pathfinder.Traversable;

/**
 *
 * @author Dan
 */
public class DrawingTester extends JApplet {

    static List<Point> terrainPoints = new ArrayList<Point>();
    static List<Point> offRoadPoints = new ArrayList<Point>();
    static List<Point> travPoints = new ArrayList<Point>();
    static List<Point> trav1Points = new ArrayList<Point>();
    static List<Point> trav2Points = new ArrayList<Point>();
    static List<Point> trav3Points = new ArrayList<Point>();
    static List<Point> roadPoints = new ArrayList<Point>();
    static List<Point> points = new ArrayList<Point>();
    static List<Point> centers = new ArrayList<Point>();
    static List<String> descripts = new ArrayList<String>();
    static double minTerrainX;
    static double maxX;
    static double minTerrainY;
    static double minOffX;
    static double minOffY;
    static double minRoadX;
    static double minRoadY;
    static double minTravX;
    static double minTravY;
    static double minTrav1X;
    static double minTrav1Y;
    static double minTrav2X;
    static double minTrav2Y;
    static double minTrav3X;
    static double minTrav3Y;

//    public static void go() {
//        JFrame f = new JFrame("whatevertest");
//        f.addWindowListener(new WindowAdapter() {
//
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
//        JApplet applet = new DrawingTester();
//        f.getContentPane().add("Center", applet);
//        applet.init();
//        f.pack();
//        f.setSize(new Dimension(600, 600));
//        f.show();
//    }

    static void drawMap(Set<Terrain> terrains, Set<OffRoadSegment> offroads,
            Set<RoadSegment> roads, Set<Traversable> travs, List<Path> paths) {
        if (terrains != null) {
            drawTerrains(terrains);
        }
        if (offroads != null) {
            drawOffRoads(offroads);
        }
        if (roads != null) {
            drawRoads(roads);
        }
        if (travs != null) {
            drawTraversables(travs);
        }

            drawPaths(paths);
        




    }

    static void drawTerrains(Set<Terrain> terrains) {
        centers = new ArrayList<Point>();
        descripts = new ArrayList<String>();
        minTerrainX = minimumX(terrains);
        maxX = maximumX(terrains);
        minTerrainY = minimumY(terrains);

        for (Terrain t : terrains) {
            List<Vector2D> verts = t.getBoundary().getWorldVertices();
            for (int i = 0; i < verts.size() - 1; i++) {
                drawTerrainLine(convert(verts.get(i)), convert(verts.get(i + 1)));
            }
            drawTerrainLine(convert(verts.get(verts.size() - 1)), convert(verts.get(0)));
            centers.add(convert(Logic2D.getCentroid(verts)));
            descripts.add(t.getDescription());
        }


    }

    static void drawPaths(List<Path> paths) {
        Set<Traversable> trav1 = new HashSet<Traversable>();
        Set<Traversable> trav2 = new HashSet<Traversable>();
        Set<Traversable> trav3 = new HashSet<Traversable>();
        for (int i = 0; i < paths.size(); i++) {
            System.out.println(i);
            if (i == 0) {
                for (Traversable t : paths.get(i).getRoute()) {
                    trav1.add(t);
                }
            }

            if (i == 1) {
                for (Traversable t : paths.get(i).getRoute()) {
                    trav2.add(t);
                }
            }

            if (i == 2) {
                for (Traversable t : paths.get(i).getRoute()) {
                    trav3.add(t);
                }
            }
        }
        System.out.println(trav1);
        System.out.println(trav2);
        System.out.println(trav3);
        minTrav1X = minimumTravX(trav1);
        minTrav1Y = minimumTravY(trav1);
        trav1Points = new ArrayList<Point>();

        for (Traversable t : trav1) {
            List<Vector2D> verts = t.getSegment().getWorldVertices();
            for (int i = 0; i < verts.size() - 1; i++) {
                drawPoint(convert(verts.get(i)), convert(verts.get(i)));
                trav1Points.add(convert(verts.get(i)));
                trav1Points.add(convert(verts.get(i + 1)));

            }
            drawPoint(convert(verts.get(verts.size() - 1)), convert(verts.get(verts.size() - 1)));
            trav1Points.add(convert(verts.get(verts.size() - 1)));
            trav1Points.add(convert(verts.get(0)));
        }

        minTrav2X = minimumTravX(trav2);
        minTrav2Y = minimumTravY(trav2);
        trav2Points = new ArrayList<Point>();

        for (Traversable t : trav2) {
            List<Vector2D> verts = t.getSegment().getWorldVertices();
            for (int i = 0; i < verts.size() - 1; i++) {
                drawPoint(convert(verts.get(i)), convert(verts.get(i)));
                trav2Points.add(convert(verts.get(i)));
                trav2Points.add(convert(verts.get(i + 1)));

            }
            drawPoint(convert(verts.get(verts.size() - 1)), convert(verts.get(verts.size() - 1)));
            trav2Points.add(convert(verts.get(verts.size() - 1)));
            trav2Points.add(convert(verts.get(0)));
        }

        minTrav3X = minimumTravX(trav3);
        minTrav3Y = minimumTravY(trav3);
        trav3Points = new ArrayList<Point>();

        for (Traversable t : trav3) {
            List<Vector2D> verts = t.getSegment().getWorldVertices();
            for (int i = 0; i < verts.size() - 1; i++) {
                drawPoint(convert(verts.get(i)), convert(verts.get(i)));
                trav3Points.add(convert(verts.get(i)));
                trav3Points.add(convert(verts.get(i + 1)));

            }
            drawPoint(convert(verts.get(verts.size() - 1)), convert(verts.get(verts.size() - 1)));
            trav3Points.add(convert(verts.get(verts.size() - 1)));
            trav3Points.add(convert(verts.get(0)));
        }


    }

    static void drawTraversables(Set<Traversable> travs) {
        minTravX = minimumTravX(travs);
        minTravY = minimumTravY(travs);
        travPoints = new ArrayList<Point>();

        for (Traversable t : travs) {
            List<Vector2D> verts = t.getSegment().getWorldVertices();
            for (int i = 0; i < verts.size() - 1; i++) {
                drawPoint(convert(verts.get(i)), convert(verts.get(i)));
                drawTrav(convert(verts.get(i)), convert(verts.get(i + 1)));
            }
            drawPoint(convert(verts.get(verts.size() - 1)), convert(verts.get(verts.size() - 1)));
            drawTrav(convert(verts.get(verts.size() - 1)), convert(verts.get(0)));

        }

    }

    static void drawRoads(Set<RoadSegment> roads) {
        minRoadX = minimumRoadX(roads);
        minRoadY = minimumRoadY(roads);
        roadPoints = new ArrayList<Point>();

        for (RoadSegment t : roads) {
            List<Vector2D> verts = t.getSegment().getWorldVertices();
            for (int i = 0; i < verts.size() - 1; i++) {
                drawPoint(convert(verts.get(i)), convert(verts.get(i)));
                drawRoad(convert(verts.get(i)), convert(verts.get(i + 1)));
            }
            drawPoint(convert(verts.get(verts.size() - 1)), convert(verts.get(verts.size() - 1)));
            drawRoad(convert(verts.get(verts.size() - 1)), convert(verts.get(0)));

        }

    }

    static void drawOffRoads(Set<OffRoadSegment> offroads) {

        minOffX = minimumOffRoadX(offroads);
        minOffY = minimumOffRoadY(offroads);
        offRoadPoints = new ArrayList<Point>();

        for (OffRoadSegment t : offroads) {
            List<Vector2D> verts = t.getSegment().getWorldVertices();
            for (int i = 0; i < verts.size() - 1; i++) {
                drawPoint(convert(verts.get(i)), convert(verts.get(i)));
                drawOffRoad(convert(verts.get(i)), convert(verts.get(i + 1)));
            }
            drawPoint(convert(verts.get(verts.size() - 1)), convert(verts.get(verts.size() - 1)));
            drawOffRoad(convert(verts.get(verts.size() - 1)), convert(verts.get(0)));

        }
    }

    private static double minimumTravX(Set<Traversable> travs) {
        double minTravX = Double.MAX_VALUE;
        for (Traversable t : travs) {
            for (Vector2D v : t.getSegment().getWorldVertices()) {
                if (v.getX() < minTravX) {
                    minTravX = v.getX();
                }
            }
        }
        return minTravX;
    }

    private static double minimumTravY(Set<Traversable> travs) {
        double minTravY = Double.MAX_VALUE;
        for (Traversable t : travs) {
            for (Vector2D v : t.getSegment().getWorldVertices()) {
                if (v.getY() < minTravY) {
                    minTravY = v.getY();
                }
            }
        }
        return minTravY;
    }

    private static double minimumOffRoadX(Set<OffRoadSegment> offroads) {
        double minOffX = Double.MAX_VALUE;
        for (OffRoadSegment t : offroads) {
            for (Vector2D v : t.getSegment().getWorldVertices()) {
                if (v.getX() < minOffX) {
                    minOffX = v.getX();
                }
            }
        }
        return minOffX;
    }

    private static double minimumOffRoadY(Set<OffRoadSegment> offroads) {
        double minOffY = Double.MAX_VALUE;
        for (OffRoadSegment t : offroads) {
            for (Vector2D v : t.getSegment().getWorldVertices()) {
                if (v.getY() < minOffY) {
                    minOffY = v.getY();
                }
            }
        }
        return minOffY;
    }

    private static double minimumRoadX(Set<RoadSegment> offroads) {
        double minOffX = Double.MAX_VALUE;
        for (RoadSegment t : offroads) {
            for (Vector2D v : t.getSegment().getWorldVertices()) {
                if (v.getX() < minOffX) {
                    minOffX = v.getX();
                }
            }
        }
        return minOffX;
    }

    private static double minimumRoadY(Set<RoadSegment> offroads) {
        double minOffY = Double.MAX_VALUE;
        for (RoadSegment t : offroads) {
            for (Vector2D v : t.getSegment().getWorldVertices()) {
                if (v.getY() < minOffY) {
                    minOffY = v.getY();
                }
            }
        }
        return minOffY;
    }

    private static Point convert(Vector2D p) {
        double x, y;
        x = p.getX() - minTerrainX;
        y = -(p.getY() + minTerrainY);

        x *= 20;
        y *= 20;

        x += 5;
        y += 60;

        return new Point((int) x, (int) y);
    }

    @Override
    public void paint(Graphics g) {


        g.setColor(Color.CYAN);
        for (int i = 0; i < terrainPoints.size(); i += 2) {
            g.drawLine(terrainPoints.get(i).x,
                    terrainPoints.get(i).y,
                    terrainPoints.get(i + 1).x,
                    terrainPoints.get(i + 1).y);
        }

        g.setColor(Color.BLUE);
        for (int i = 0; i < descripts.size(); i++) {
            String str = descripts.get(i);
            int x = centers.get(i).x;
            int y = centers.get(i).y;
            g.drawString(str, x, y);
        }

        g.setColor(Color.GREEN);
        for (int i = 0; i < offRoadPoints.size(); i += 2) {
            g.drawLine(offRoadPoints.get(i).x,
                    offRoadPoints.get(i).y,
                    offRoadPoints.get(i + 1).x,
                    offRoadPoints.get(i + 1).y);
        }
        g.setColor(Color.BLACK);
        for (int i = 0; i < roadPoints.size(); i += 2) {
            g.drawLine(roadPoints.get(i).x,
                    roadPoints.get(i).y,
                    roadPoints.get(i + 1).x,
                    roadPoints.get(i + 1).y);
        }

        g.setColor(Color.BLUE);
        for (int i = 0; i < travPoints.size(); i += 2) {
            g.drawLine(travPoints.get(i).x,
                    travPoints.get(i).y,
                    travPoints.get(i + 1).x,
                    travPoints.get(i + 1).y);
        }

        g.setColor(Color.ORANGE);

        for (int i = 0; i < points.size(); i++) {
            g.fillOval(points.get(i).x - 3,
                    points.get(i).y - 3,
                    6,
                    6);
        }

        g.setColor(Color.GREEN);
        for (int i = 0; i < trav1Points.size(); i += 2) {
            g.drawLine(trav1Points.get(i).x,
                    trav1Points.get(i).y,
                    trav1Points.get(i + 1).x,
                    trav1Points.get(i + 1).y);
        }
        g.setColor(Color.BLUE);
        for (int i = 0; i < trav2Points.size(); i += 2) {
            g.drawLine(trav2Points.get(i).x,
                    trav2Points.get(i).y,
                    trav2Points.get(i + 1).x,
                    trav2Points.get(i + 1).y);
        }
        g.setColor(Color.RED);
        for (int i = 0; i < trav3Points.size(); i += 2) {
            g.drawLine(trav3Points.get(i).x,
                    trav3Points.get(i).y,
                    trav3Points.get(i + 1).x,
                    trav3Points.get(i + 1).y);
        }




    }

    private static void drawTrav(Point p1, Point p2) {
        travPoints.add(p1);
        travPoints.add(p2);
    }

    private static void drawRoad(Point p1, Point p2) {
        roadPoints.add(p1);
        roadPoints.add(p2);
    }

    private static void drawTerrainLine(Point p1, Point p2) {
        terrainPoints.add(p1);
        terrainPoints.add(p2);
    }

    private static void drawOffRoad(Point p1, Point p2) {
        offRoadPoints.add(p1);
        offRoadPoints.add(p2);
    }

    private static void drawPoint(Point p1, Point p2) {
        points.add(p1);
        points.add(p2);
    }

    private static double maximumX(Set<Terrain> terrains) {
        double maxX = Double.MIN_VALUE;
        for (Terrain t : terrains) {
            for (Vector2D v : t.getBoundary().getWorldVertices()) {
                if (v.getX() > maxX) {
                    maxX = v.getX();
                }
            }
        }
        return maxX;
    }

    private static double minimumY(Set<Terrain> terrains) {
        double minY = Double.MAX_VALUE;
        for (Terrain t : terrains) {
            for (Vector2D v : t.getBoundary().getWorldVertices()) {
                if (v.getY() < minY) {
                    minY = v.getY();
                }
            }
        }
        return minY;
    }

    private static double minimumX(Set<Terrain> terrains) {
        double minX = Double.MAX_VALUE;
        for (Terrain t : terrains) {
            for (Vector2D v : t.getBoundary().getWorldVertices()) {
                if (v.getX() < minX) {
                    minX = v.getX();
                }
            }
        }
        return minX;
    }
    
    
    
    
    
}
