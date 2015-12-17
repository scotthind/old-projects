package org.rowan.tda.tda2d.organize2d;

import java.util.List;
import java.util.LinkedList;
import org.rowan.linalgtoolkit.BoundingBox2D;
import org.rowan.linalgtoolkit.Vector2D;
import org.rowan.linalgtoolkit.logic2d.LinAlg2D;
import org.rowan.linalgtoolkit.shapes2d.*;

/**
 * The <code>Spiral2D</class> class will take a collection of <code>Shape2D</code>
 * instances and position them as close as possible to the origin. If a high level
 * area is given, the positioning of the <code>Shape2D</code> must not intersect
 * the high level area. Whether or not the <code>Shape2D</code> instances can
 * overlap is determined by the user.
 * <p>
 * The algorithm computes the bounding box of each <code>Shape2D</code> instance.
 * The collection of shapes is then sorted based on the area of each bounding box
 * in descending order. The first <code>Shape3D</code> instance is placed at the
 * origin. A placement bounding box is then constructed, which will contain all
 * of the placed <code>Shape2D</code> instances. The algorithm will start to place
 * <code>Shape2D</code> instances along the bottom side of the placement bounding
 * box. Once the length of the bottom side of the bounding box has been reached,
 * the algorithm changes its placement direction to left and begins to place shapes
 * on the left side of the placement bounding box. Each time the placement direction
 * changes, the placement bounding box will expand to include <code>Shape2D</code> i
 * instances placed on the previous side of the placement bounding box. This algorithm
 * will continue this process until all <code>Shape2D</code> instances have been
 * placed.
 *
 * @author Jonathan Palka, Robert Russell
 * @version 1.1
 * @since 1.1
 */
public class Spiral2D {

    /*********************************************
     * MARK: Fields
     *********************************************/
    /** Determines which way to spiral when positioning shapes. */
    private final int DOWN = 0;
    private final int LEFT = 1;
    private final int UP = 2;
    private final int RIGHT = 3;

    /** The collection of two-dimensional shapes to be organized. */
    private LinkedList<Shape2D> shapes;

    /** The high level area to organize the shapes in. */
    private Rectangle2D highLevelArea;

    /** Flag that determines whether the shapes can overlap. */
    private boolean overLap;

    /** Flag that determines whether the shapes have to be contained within a high level area. */
    private boolean contained;

    /** An array that keeps track of which shapes have been placed. If a given
     * position contains zero, then it means the shape at this index has been placed. */
    private int[] placedShapes;

    /*********************************************
     * MARK: Constructors
     *********************************************/

    /**
     * Constructs an instance of <code>Spiral2D</code>.
     * @param highLevelArea The high level area to organize the shapes in.
     * @param shapes the collection of two-dimensional shapes to be organized.
     * @param overLap <true> If the shapes can overlap;
     * <code>false</code> otherwise, which means the shapes cannot overlap.
     */
    public Spiral2D(Rectangle2D highLevelArea, List<Shape2D> shapes, boolean overLap) {
        this.shapes = new LinkedList<Shape2D>(shapes);
        this.highLevelArea = highLevelArea;

        placedShapes = new int[shapes.size()];
        for (int i = 0; i < shapes.size(); i++)
            placedShapes[i] = i;

        // If there is no high level area shapes don't have to be contained.
        if (highLevelArea != null)
            contained = true;
        else
            contained = false;

        this.overLap = overLap;
    }

    /*********************************************
     * MARK: Run
     *********************************************/

    /**
     * Executes the <code>Spiral2D</code> algorithm. This algorithm will take a
     * collection of <code>Shape2D</code> instances and position them as close
     * as possible to the origin. If a high level area was given by the user when
     * instantiating this class, the positioning of the <code>Shape2D</code> must
     * not intersect the high level area. Whether or not the <code>Shape2D</code>
     * instances can overlap was determined by the user when instantiating this class.
     * @return <code>true</code> if algorithm was able to obey the overlap and
     * contained flag; <code>false</code> otherwise.
     */
    public boolean run() {
        preprocess();

        //First shape starts as the first middle area
        BoundingBox2D bxT = shapes.get(0).boundingBox();
        //Quadrant boundries
        double leftX = bxT.getA().getX();
        double rightX = bxT.getB().getX();
        double topY = bxT.getB().getY();
        double botY = bxT.getA().getY();

        //Points that should be the same for placing
        //X Y values to use on shape
        double hugX = 0;
        double hugY = 0;
        //X Y values to match up with
        double placeX = rightX;
        double placeY = topY;

        //Place first shape so spiral can go
        BoundingBox2D temp = shapes.get(1).boundingBox();
        if (temp.width() > temp.height()) {
            shapes.get(1).rotate(Math.PI / 2);
            temp = shapes.get(1).boundingBox();
        }

        hugX = temp.getA().getX();
        hugY = temp.getB().getY();
        Vector2D tempPos = shapes.get(1).getPosition();
        shapes.get(1).setPosition(new Vector2D(placeX - hugX, placeY - hugY));
        placedShapes[0] = 0;
        placedShapes[1] = 0;

        //Set the starting direction as down
        int direction = DOWN;
        //Do we need to change direction?
        boolean newDir = false;

        int prevShapePlaced = 1;

        //If a shape can't fit going on direction, flip it
        boolean rDown = false;
        boolean rLeft = false;
        boolean rUp = false;
        boolean rRight = false;
        //Did the algorithm fail?
        boolean returnFlag = true;

        //Were the first two shapes to big for the highLevel area?
        if (contained && (!LinAlg2D.contains(highLevelArea, shapes.get(0)) ||
                          !LinAlg2D.contains(highLevelArea, shapes.get(1)))) {
            returnFlag = false;
        }

        //Loop to go through all shapes and place them around in a spiral
        for (int i = 2; i < shapes.size(); i++) {
            if (placedShapes[i] == 0) {
                continue;
            }

            BoundingBox2D bx = shapes.get(i).boundingBox();
            BoundingBox2D preBx = shapes.get(prevShapePlaced).boundingBox();

            if (direction == DOWN) {
                //Rotate shape so it's parallel to the side
                if (bx.width() > bx.height() && !rDown) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                }
                //Match top right point of shape with
                hugX = bx.getA().getX();
                hugY = bx.getB().getY();
                //bottom left point of previous shape
                placeX = preBx.getA().getX();
                placeY = preBx.getA().getY();

                //Does shape go beyond the boundries?
                if (placeY - bx.height() < botY) {
                    //If so switch direction
                    newDir = true;
                }
            } else if (direction == LEFT) {
                //Rotate shape so it's parallel to the side
                if (bx.height() > bx.width() && !rLeft) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                }
                //Match top right point of shape with
                hugX = bx.getB().getX();
                hugY = bx.getB().getY();
                //top left point of previous shape
                placeX = preBx.getA().getX();
                placeY = preBx.getB().getY();

                //Does shape go beyond the boundries?
                if (placeX - bx.width() < leftX) {
                    //If so switch direction
                    newDir = true;
                }
            } else if (direction == UP) {
                //Rotate shape so it's parallel to the side
                if (bx.width() > bx.height() && !rUp) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                }
                //Match bottom right point of shape with
                hugX = bx.getB().getX();
                hugY = bx.getA().getY();
                //top right point of previous shape
                placeX = preBx.getB().getX();
                placeY = preBx.getB().getY();

                //Does shape go beyond the boundries?
                if (placeY + bx.height() > topY) {
                    //If so switch direction
                    newDir = true;
                }
            } else if (direction == RIGHT) {
                //Rotate shape so it's parallel to the side
                if (bx.height() > bx.width() && !rRight) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                }
                //Match bottom left point of shape with
                hugX = bx.getA().getX();
                hugY = bx.getA().getY();
                //bottom right point of previous shape
                placeX = preBx.getB().getX();
                placeY = preBx.getA().getY();

                //Does shape go beyond the boundries?
                if (placeX + bx.width() > rightX) {
                    //If so switch direction
                    newDir = true;
                }
            }

            //Place shape if we didn't change direction or we had to rotate a shape
            if (!newDir || rRight || rLeft || rUp || rDown) {
                tempPos = new Vector2D(placeX - hugX, placeY - hugY);
                shapes.get(i).setPosition(tempPos);
                placedShapes[i] = 0;

                //Does the shape have to be contained?
                if (contained && !LinAlg2D.contains(highLevelArea, shapes.get(i))) {
                    //If we had to rotate a shape there were no other places the shape could fit
                    if (rDown || rUp || rLeft || rRight) {
                        shapes.get(i).setPosition(Vector2D.ORIGIN);
                        placedShapes[i] = i;
                        //If shapes can overlap start over again
                        if (overLap) {
                            run(i);
                            return returnFlag;
                        } else //go outside the box, algorithm failed
                        {
                            //Algorithm failed
                            returnFlag = false;
                            //Ignore the high level area
                            contained = false;
                            //Reset variables
                            rDown = false;
                            rUp = false;
                            rLeft = false;
                            rRight = false;
                            //We want to change direction
                            newDir = true;
                        }
                    } else {
                        newDir = true;
                    }
                }
            }

            int count = 0;
            //Find a new area
            while (newDir) {
                if (count == 0 && prevShapePlaced != 1) {
                    fillHole(bxT, prevShapePlaced, direction);
                }

                count++;
                newDir = false;
                direction = (direction + 1) % 4;

                //If we can't place it on either side it's to long, so rotate it
                //next time around
                if (count == 4) {
                    if (direction == DOWN) {
                        rDown = !rDown;
                    } else if (direction == LEFT) {
                        rLeft = !rLeft;
                    } else if (direction == UP) {
                        rUp = !rUp;
                    } else if (direction == RIGHT) {
                        rRight = !rRight;
                    }
                    //Start main loop again starting with that shape
                    placedShapes[i] = 0;
                    i--;
                    continue;
                }

                shapes.get(i).setPosition(Vector2D.ORIGIN);
                placedShapes[i] = i;

                //Create a new complex shape from all the previous shapes to use
                // as the new middle area
                LinkedList<Shape2D> complex = new LinkedList<Shape2D>();
                for (int j = 0; j < placedShapes.length; j++) {
                    if (placedShapes[j] == 0) {
                        complex.add(shapes.get(j));
                    }
                }

                ComplexShape2D cS = new ComplexShape2D(complex);

                bxT = cS.boundingBox();
                //Quadrant boundries
                leftX = bxT.getA().getX();
                rightX = bxT.getB().getX();
                topY = bxT.getB().getY();
                botY = bxT.getA().getY();

                //Shape didn't fit on previous side so make it the first
                //shape on the next side.
                if (direction == DOWN) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                    hugX = bx.getA().getX();
                    hugY = bx.getB().getY();

                    tempPos = new Vector2D(rightX - hugX, topY - hugY);
                } else if (direction == LEFT) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                    hugX = bx.getB().getX();
                    hugY = bx.getB().getY();

                    tempPos = new Vector2D(rightX - hugX, botY - hugY);
                } else if (direction == UP) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                    hugX = bx.getB().getX();
                    hugY = bx.getA().getY();

                    tempPos = new Vector2D(leftX - hugX, botY - hugY);
                } else if (direction == RIGHT) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                    hugX = bx.getA().getX();
                    hugY = bx.getA().getY();

                    tempPos = new Vector2D(leftX - hugX, topY - hugY);
                }
                shapes.get(i).setPosition(tempPos);
                placedShapes[i] = 0;
                //Does the shape have to be contained and is it?
                if (contained && !LinAlg2D.contains(highLevelArea, shapes.get(i))) {
                    newDir = true;
                    shapes.get(i).setPosition(Vector2D.ORIGIN);
                    placedShapes[i] = i;
                }
            }
            prevShapePlaced = i;
        }
        return returnFlag;
    }

    /*********************************************
     * MARK: Algorithm Steps
     *********************************************/
    /**
     * Performs the pre-processing of the <code>Spiral2D</code> algorithm. The
     * collection of shapes is then sorted based on the area of each bounding box
     * in descending order. All shapes are placed at the origin.
     */
    private void preprocess() {
        double[] area = new double[shapes.size()];
        for (int i = 0; i < shapes.size(); i++) {
            area[i] = shapes.get(i).area();
        }

        //Sort shapes in from high to low area.
        for (int i = 0; i < shapes.size(); i++) {
            for (int i2 = i + 1; i2 < shapes.size(); i2++) {
                if (area[i] < area[i2]) {
                    //Swap shapes
                    Shape2D temp = shapes.get(i2);
                    shapes.remove(i2);
                    shapes.add(i2, shapes.get(i));
                    shapes.remove(i);
                    shapes.add(i, temp);
                    //Swap areas
                    double tempArea = area[i2];
                    area[i2] = area[i];
                    area[i] = tempArea;
                }
            }
        }

        //Start all shapes at the origin
        for (int i = 0; i < shapes.size(); i++) {
            shapes.get(i).setPosition(Vector2D.ORIGIN);
        }
        if (highLevelArea != null) {
            highLevelArea.setPosition(Vector2D.ORIGIN);
        }
    }

    /**
     * Performs the <code>Spiral2D</code> algorithm starting a certain shape and continues to the end.
     * @param start The shape index number to start at. 0 through list.size() are valid queries.
     * @return returnFlag <code>true</code> if algorithm was able to obey the overlap and contained flag;
     * otherwise <code>false</code>.
     */
    private boolean run(int start) {
        BoundingBox2D bxT = shapes.get(start).boundingBox();
        //Quadrant boundries
        double leftX = bxT.getA().getX();
        double rightX = bxT.getB().getX();
        double topY = bxT.getB().getY();
        double botY = bxT.getA().getY();

        //Points that should be the same for placing
        //X Y values to use on shape
        double hugX = 0;
        double hugY = 0;
        //X Y values to match up with
        double placeX = rightX;
        double placeY = topY;

        //Place first shape so spiral can go
        BoundingBox2D temp;
        try {
            temp = shapes.get(start + 1).boundingBox();
        } catch (IndexOutOfBoundsException e) {
            //Previouse shape was last shape
            return !LinAlg2D.contains(highLevelArea, shapes.get(start));
        }
        if (temp.width() > temp.height()) {
            shapes.get(start + 1).rotate(Math.PI / 2);
            temp = shapes.get(start + 1).boundingBox();
        }

        hugX = temp.getA().getX();
        hugY = temp.getB().getY();
        Vector2D tempPos = shapes.get(start + 1).getPosition();
        shapes.get(1).setPosition(new Vector2D(placeX - hugX, placeY - hugY));
        placedShapes[start] = 0;
        placedShapes[start + 1] = 0;

        //Set the starting direction as down
        int direction = DOWN;
        //Do we need to change direction?
        boolean newDir = false;
        //Did the algorithm fail?
        boolean returnFlag = true;

        if (!LinAlg2D.contains(highLevelArea, shapes.get(start)) || !LinAlg2D.contains(highLevelArea, shapes.get(start + 1))) {
            returnFlag = false;
        }

        int prevShapePlaced = 1;

        //If a shape can't fit going on direction, flip it
        boolean rDown = false;
        boolean rLeft = false;
        boolean rUp = false;
        boolean rRight = false;

        for (int i = start + 2; i < shapes.size(); i++) {
            if (placedShapes[i] == 0) {
                continue;
            }

            BoundingBox2D bx = shapes.get(i).boundingBox();
            BoundingBox2D preBx = shapes.get(prevShapePlaced).boundingBox();

            if (direction == DOWN) {
                //Rotate shape so it's parallel to the side
                if (bx.width() > bx.height() && !rDown) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                }
                //Match top right point of shape with
                hugX = bx.getA().getX();
                hugY = bx.getB().getY();
                //bottom left point of previous shape
                placeX = preBx.getA().getX();
                placeY = preBx.getA().getY();

                //Does shape go beyond the boundries?
                if (placeY - bx.height() < botY) {
                    //If so switch direction
                    newDir = true;
                }
            } else if (direction == LEFT) {
                //Rotate shape so it's parallel to the side
                if (bx.height() > bx.width() && !rLeft) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                }
                //Match top right point of shape with
                hugX = bx.getB().getX();
                hugY = bx.getB().getY();
                //top left point of previous shape
                placeX = preBx.getA().getX();
                placeY = preBx.getB().getY();

                //Does shape go beyond the boundries?
                if (placeX - bx.width() < leftX) {
                    //If so switch direction
                    newDir = true;
                }
            } else if (direction == UP) {
                //Rotate shape so it's parallel to the side
                if (bx.width() > bx.height() && !rUp) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                }
                //Match bottom right point of shape with
                hugX = bx.getB().getX();
                hugY = bx.getA().getY();
                //top right point of previous shape
                placeX = preBx.getB().getX();
                placeY = preBx.getB().getY();

                //Does shape go beyond the boundries?
                if (placeY + bx.height() > topY) {
                    //If so switch direction
                    newDir = true;
                }
            } else if (direction == RIGHT) {
                //Rotate shape so it's parallel to the side
                if (bx.height() > bx.width() && !rRight) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                }
                //Match bottom left point of shape with
                hugX = bx.getA().getX();
                hugY = bx.getA().getY();
                //bottom right point of previous shape
                placeX = preBx.getB().getX();
                placeY = preBx.getA().getY();

                //Does shape go beyond the boundries?
                if (placeX + bx.width() > rightX) {
                    //If so switch direction
                    newDir = true;
                }
            }

            //Place shape if we didn't change direction or we had to rotate a shape
            if (!newDir || rRight || rLeft || rUp || rDown) {
                tempPos = new Vector2D(placeX - hugX, placeY - hugY);
                shapes.get(i).setPosition(tempPos);
                placedShapes[i] = 0;
                //Does the shape have to be contained?
                if (contained && !LinAlg2D.contains(highLevelArea, shapes.get(i))) {
                    //Are there spots left?
                    if (rDown || rUp || rLeft || rRight) {
                        //No spots left so start over
                        shapes.get(i).setPosition(Vector2D.ORIGIN);
                        placedShapes[i] = i;
                        run(i);
                        return true;
                    } else {
                        newDir = true;
                    }
                }
            }

            int count = 0;
            boolean end = false;
            //Find a new area
            while (newDir) {
                count++;
                newDir = false;
                direction = (direction + 1) % 4;

                //If we can't place it on either side start up in the middle again.
                if (count == 4) {
                    shapes.get(i).setPosition(Vector2D.ORIGIN);
                    placedShapes[i] = i;
                    run(i);
                    return true;
                }

                shapes.get(i).setPosition(Vector2D.ORIGIN);
                placedShapes[i] = i;

                //Create a new complex shape from all the previous shapes to use
                // as the new middle area
                LinkedList<Shape2D> complex = new LinkedList<Shape2D>();
                for (int j = start; j < placedShapes.length; j++) {
                    if (placedShapes[j] == 0) {
                        complex.add(shapes.get(j));
                    }
                }
                ComplexShape2D cS = new ComplexShape2D(complex);

                bxT = cS.boundingBox();
                //Quadrant boundries
                leftX = bxT.getA().getX();
                rightX = bxT.getB().getX();
                topY = bxT.getB().getY();
                botY = bxT.getA().getY();

                //Shape didn't fit on previous side so make it the first
                //shape on the next side.
                if (direction == DOWN) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                    hugX = bx.getA().getX();
                    hugY = bx.getB().getY();

                    tempPos = new Vector2D(rightX - hugX, topY - hugY);
                } else if (direction == LEFT) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                    hugX = bx.getB().getX();
                    hugY = bx.getB().getY();

                    tempPos = new Vector2D(rightX - hugX, botY - hugY);
                } else if (direction == UP) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                    hugX = bx.getB().getX();
                    hugY = bx.getA().getY();

                    tempPos = new Vector2D(leftX - hugX, botY - hugY);
                } else if (direction == RIGHT) {
                    shapes.get(i).rotate(Math.PI / 2);
                    bx = shapes.get(i).boundingBox();
                    hugX = bx.getA().getX();
                    hugY = bx.getA().getY();

                    tempPos = new Vector2D(leftX - hugX, topY - hugY);
                }
                shapes.get(i).setPosition(tempPos);
                placedShapes[i] = 0;
                //Does the shape have to be contained and is it?
                if (contained && !LinAlg2D.contains(highLevelArea, shapes.get(i))) {
                    //If it's not contained, rerun loop and try next side
                    newDir = true;
                    shapes.get(i).setPosition(Vector2D.ORIGIN);
                    placedShapes[i] = i;
                }
            }
            if (!end) {
                prevShapePlaced = i;
                end = false;
            }
        }
        return returnFlag;
    }

    /**
     * Fills the hole at the end of a placement direction if there is one. If a
     * shape exists that will not exceed the remaining length of the placement
     * bounding box, then it will be placed here to maximize organization.
     * @param bxt BoundingBox of all the other shapes not in this arm.
     * @param shapeNum shape number of shape that was last placed.
     * @param direction Current direction.
     */
    private void fillHole(BoundingBox2D bxt, int shapeNum, int direction) {
        BoundingBox2D bx;
        double xRight = 0.0;
        double yTop = 0.0;
        double xLeft = 0.0;
        double yBot = 0.0;
        double height = 0.0;
        double width = 0.0;
        Vector2D pos = null;

        bx = shapes.get(shapeNum).boundingBox();
        if (direction == DOWN) {
            xRight = bx.getB().getX();
            xLeft = bxt.getB().getX();
            yTop = bx.getA().getY();
            yBot = bxt.getA().getY();

            height = Math.abs(yTop - yBot);
            width = Math.abs(xRight - xLeft);
            pos = new Vector2D(xRight - width / 2, yTop - height / 2);
        } else if (direction == LEFT) {
            xRight = bx.getA().getX();
            xLeft = bxt.getA().getX();
            yTop = bxt.getA().getY();
            yBot = bx.getA().getY();

            height = Math.abs(yTop - yBot);
            width = Math.abs(xRight - xLeft);
            pos = new Vector2D(xRight - width / 2, yTop - height / 2);
        } else if (direction == UP) {
            xRight = bxt.getA().getX();
            xLeft = bx.getA().getX();
            yTop = bxt.getB().getY();
            yBot = bx.getB().getY();

            height = Math.abs(yTop - yBot);
            width = Math.abs(xRight - xLeft);
            pos = new Vector2D(xRight - width / 2, yTop - height / 2);
        } else if (direction == RIGHT) {
            xRight = bxt.getB().getX();
            xLeft = bx.getB().getX();
            yTop = bx.getB().getY();
            yBot = bxt.getB().getY();

            height = Math.abs(yTop - yBot);
            width = Math.abs(xRight - xLeft);
            pos = new Vector2D(xRight - width / 2, yTop - height / 2);
        }

        //Find a shape that fits in the dimensions found
        for (int i = shapeNum + 2; i < shapes.size(); i++) {
            if (placedShapes[i] != 0) {
                bx = shapes.get(i).boundingBox();
                if (bx.width() <= width && bx.height() <= height) {
                    shapes.get(i).setPosition(pos);
                    placedShapes[i] = 0;
                    return;
                }

                shapes.get(i).rotate(Math.PI / 2);
                bx = shapes.get(i).boundingBox();
                if (bx.width() <= width && bx.height() <= height) {
                    shapes.get(i).setPosition(pos);
                    placedShapes[i] = 0;
                    return;
                }
            }
        }
    }
}
