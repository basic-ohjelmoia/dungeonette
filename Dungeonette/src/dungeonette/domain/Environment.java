/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 * Environment is the parent object class for a multi-floor dungeon. The dungeon
 * is stored either into a 3-dimensional char array (not yet implemented) or
 * into an one dimensional array of floor objects (each of which contain a two
 * dimensional char array).
 *
 * The class currently contains a lot of program logic (dungeon generation)
 * which should be refactored into suitable sub classes.
 *
 * Still, the Environment class already produces quite advanced random dungeons!
 *
 * @author Tuomas Honkala
 */
public class Environment {

    private char[][][] tiles;
    private int xMax;
    private int yMax;
    private int zMax;
    private Floor[] floors;

    /**
     * Constructor for an environment object. The constructor is basically given
     * the size and scope of the dungeon in form of max x, max y and max z
     *
     * Eventually the constructor should also accept parameters, which could be
     * used to specify the type of dungeon (sparse/dense, long passages/short
     * passages, lots of doors/no doors etc.) that should be generated.
     *
     * @param floorWidth Maximum width (x) of each dungeon floor (partially
     * hard-coded to 100)
     * @param floorHeigth Maximum height (y) of each dungeon floor (partially
     * hard-coded to 100)
     * @param numberOfFloors Number of floors (z) of the entirety of the dungeon
     * 8partially hardcoded to 1)
     */
    public Environment(int floorWidth, int floorHeigth, int numberOfFloors) {
        this.tiles = new char[floorWidth][floorHeigth][numberOfFloors];
        this.xMax = floorWidth;
        this.yMax = floorHeigth;
        this.zMax = numberOfFloors;
        this.floors = new Floor[zMax];
    }

    /**
     * Placeholderish-method that basically runs the entirety of the dungeon
     * generation process.
     *
     * The code here needs some serious refactoring and general cleaning up.
     *
     * Still, the results are quite good.
     *
     * The point of origin (first room) of the dungeon is currently hard-coded
     * into the center of the floor.
     *
     *
     */
    public void generate() {
        Floor floor = new Floor(xMax, yMax, new Point(45, 45));

        int rooms = 1;        // this is basically the serial number of the room currently being generated

        Random randomi = new Random();

        int cx = 45 / 10;       // cx and cy refer to the "current x coordinate" and "current y coordinate" of
        int cy = 45 / 10;       // the dungeon generation process.
                                // as the algorithm actually handles the dungeon in a coarse grid of 10 x 10 tiles
                                  // the point of origin coordinates need to be divided by 10

        Point temporaryOrigin = new Point(cx, cy);       // this information is needed in order to connect
                                                         // passages from the point of origin and the next room
                                                         // being generated

        char oldFrom = ' ';

        int maxRooms = randomi.nextInt(40) + 20;            // maximum number of rooms being generated for the dungeon
        // reaching "the max" is NOT guaranteed currently

        int failuresSinceLastRoomGeneration = 0;          // a safety which ensures that the algorithm eventually
                                                         // fails in case it reaches a logical dead-end

        Point[] roomLocations = new Point[maxRooms + 1];   // array for room locatios

        
        // here lies the main loop used for the dungeon generation
        while (rooms < maxRooms && failuresSinceLastRoomGeneration < 100) {
            Room temp = null;
            if (!floor.getRoomQueue().isEmpty()) {temp=floor.getRoomQueue().front();}
                    
                    if (temp!=null) {
                        System.out.println("löytyi q-room");
                    cx=temp.location.x;
                    cy=temp.location.y;
                    } else if (rooms>1) {
                        failuresSinceLastRoomGeneration=101; cx=-999;
                        System.out.println("Queue failed on room "+rooms);
                    }
                    
            int arpa = randomi.nextInt(4);
            
            // oldFrom is used to avoid trying the previously failed direction again
            if ((oldFrom == 'n' || cy==0) && arpa == 0) {
                arpa = 1;
            }
            if ((oldFrom == 's' || cy==9) && arpa == 1) {
                arpa = 2;
            }
            if ((oldFrom == 'w' || cx==0) && arpa == 2) {
                arpa = 3;
            }
            if ((oldFrom == 'e' || cx==9) && arpa == 3) {
                arpa = 0;
            }

            char from = ' ';
            char out = ' ';

            
            boolean roomGenerated = false;
            boolean obstacleMet = false;

            // after picking a general direction (north, west, east, south) the algorithm
            // makes three tries into that direction trying to generate the next room
            // 
            for (int tries = 0; tries < 3 && !roomGenerated; tries++) {
           
                if (arpa == 0 && tries < 2) {
                    from = 'n';
                    out = 's';
                    cy--;
                }
                if (arpa == 1 && tries < 2) {
                    from = 's';
                    out = 'n';
                    cy++;
                }
                if (arpa == 2 && tries < 2) {
                    from = 'w';
                    out = 'e';
                    cx--;
                }
                if (arpa == 3 && tries < 2) {
                    from = 'e';
                    out = 'w';
                    cx++;
                }

                // if the room generation process goes out of bounds or meets and obstace,
                // the algorithm randomly picks a new point of origin (room) and restarts the
                // room generation process from there
                if (cx < 0 || cx > 9 || cy < 0 || cy > 9 || obstacleMet) {
                    failuresSinceLastRoomGeneration++;

                    int roomArpa = 1;
                    if (rooms > 2) {
                        roomArpa = Math.max(1, randomi.nextInt(rooms));
                    }
                    
                    cx = roomLocations[roomArpa].x;
                    cy = roomLocations[roomArpa].y;
                    
                       temp = null;
                      System.out.println("floor-q empty: "+floor.getRoomQueue().isEmpty());
                    if (!floor.getRoomQueue().isEmpty()) {temp=floor.getRoomQueue().dequeue();}
                    
                    if (temp!=null) {
                        System.out.println("löytyi q-room #"+temp.id);
                    cx=temp.location.x;
                    cy=temp.location.y;
                    } else {
                        //failuresSinceLastRoomGeneration=101; 
                        cx=-999;
                        System.out.println("Queue failed on room "+rooms);
                    }
                    
                    temporaryOrigin = new Point(cx, cy);
                    out = ' ';
                    from = ' ';
                  //  tries=100;
                }

                else if (cx >= 0 && cx < 10 && cy >= 0 && cy < 10) {
                    Dimension dimension = new Dimension(10, 10);
                    if (rooms % 11 == 7 && tries <= 1) {
                        dimension = new Dimension(20, 10);
                    }
                    if (rooms % 11 == 5 && tries <= 1) {
                        dimension = new Dimension(10, 20);
                    }
                    if (rooms % 11 == 3 && tries <= 1) {
                        dimension = new Dimension(20, 20);
                    }
                    if (rooms % 11 == 4 && tries <= 1) {
                        dimension = new Dimension(30, 30);
                    }
                    
                    // if the insert room method call returns TRUE then the new room was successfully placed into the map!
                    if (floor.insertRoom(cx, cy, dimension, from, temporaryOrigin, rooms)) {
                        roomGenerated = true;
                        oldFrom = out;
                        System.out.println("Room #" + rooms + " of dim " + dimension.width + "," + dimension.height + " generated at " + cx + ", " + cy);
                        roomLocations[rooms] = new Point(cx, cy);
                        temporaryOrigin = new Point(cx, cy);
                        failuresSinceLastRoomGeneration = 0;
                        rooms++;

                        floor.roomLayout[cx][cy].outDirection = out;
                    } 
                    
                    // if the room was too large to generate in THIS location, the algorithm generates a passage instead
                    else if (floor.roomLayout[cx][cy] == null && tries == 0
                            && !(from == 'n' && cy == 0) && !(from == 's' && cy == 9) && !(from == 'w' && cx == 0) && !(from == 'e' && cx == 9)) {
                        
                        // THIS CODE NO LONGER SERVES PURPOSE??!
//                        System.out.println("generated a passage on try " + tries);
//                        int dw = 3;
//                        int dh = 3;
//
//                        if (from == 'n' || from == 's') {
//                            dh = 10;
//                        } else {
//                            dw = 10;
//                        }
                        
                    } else if (floor.roomLayout[cx][cy] != null) {
                        obstacleMet = true;
                    }
                }
            }
            System.out.println("failures now "+failuresSinceLastRoomGeneration);
        }
        
        // print out the results (the entire dungeon floor)
        floor.print();
        floor.addRandomRoute(false);
        floor.addRandomRoute(false);
        floor.addRandomRoute(false);
        floor.addRandomRoute(true);
        floor.carveRoutes();

        this.floors[0]=floor;
    }

    /**
     * Returns the array of floors
     * @return array of floors
     */
    public Floor[] getFloors() {
        return this.floors;
    }
    
}
