/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import dungeonette.generator.PassageCarver;
import dungeonette.generator.DungeonPrinter;
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
    private int[][][] tileID;
    private int xMax;
    private int yMax;
    private int zMax;
    private Floor[] floors;
    private Specification spec;

    /**
     * Constructor for an environment object. The constructor is basically given
     * the size and scope of the dungeon in form of max x, max y and max z
     *
     * Eventually the constructor should also accept parameters, which could be
     * used to specify the type of dungeon (sparse/dense, long passages/short
     * passages, lots of doors/no doors etc.) that should be generated.
     *
     * @param spec specification of the dungeon
    */
    public Environment(Specification spec) {
        int floorWidth = spec.maxX;
        int floorHeigth = spec.maxY;
        int numberOfFloors = spec.maxZ;

        this.tiles = new char[floorWidth][floorHeigth][numberOfFloors];
        this.tileID = new int[floorWidth][floorHeigth][numberOfFloors];
        this.xMax = floorWidth;
        this.yMax = floorHeigth;
        this.zMax = numberOfFloors;
        this.floors = new Floor[zMax];
        this.spec = spec;
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
     * A GENERAL DESCRIPTION OF HOW THE GENERATION PROCESS WORKS:
     * 
     * 1)   The very first room is placed on the point of origin of the floor map. This location should be considered as the
     *      entranced to the dungeon floor (ex. staircase)
     * 2)   each new room gets placed into a queue. While the active room has "pivots" left, the algorithm tries to create
     *      a neighbour room for this room to connect to. If valid spot to place this neighbor is found, the active room and
     *      the neighbouring room are marked for a connecting passage (the passage itself is not yet generated).
     * 3)   if a new room was created, then that will become the NEW active room. If no neighbour was created, then the new 
     *      active room gets dequeued from the room queue.
     * 4)   This process goes on and on until enough rooms have been generated OR there are no active rooms with pivots left.
     * 5)   After all the rooms have been generated, the passages betweem them will get carved out.
     *
     */
    public void generate() {
        Floor floor = new Floor(spec, new Point(45, 45)); // The point object here refers the point of entry (first room location) of the floor.

        int rooms = 1;        // this is basically the serial number of the room currently being generated. Must start from one NOT zero!

        Random randomi = new Random();

        int cx = 45 / 10;       // cx and cy refer to the "current x coordinate" and "current y coordinate" of
        int cy = 45 / 10;       // the dungeon generation process.
        // as the algorithm actually handles the dungeon in a coarse grid of 10 x 10 tiles
        // the point of origin coordinates need to be divided by 10

        Point temporaryOrigin = new Point(cx, cy);       // this information is needed in order to connect
        // passages from the point of origin and the next room
        // being generated

        char oldFrom = ' ';

        int maxRooms = randomi.nextInt(spec.volatility) + spec.density;            // maximum number of rooms being generated for the dungeon
        // the actualy reaching of "the max" is NOT guaranteed currently

        int failuresSinceLastRoomGeneration = 0;          // a safety which ensures that the algorithm eventually fails in case it reaches a logical dead-end

        
        // ================
        // IMPORTANT!!!!!!!
        // ================
        // below lies the main loop used for the dungeon generation
        while (rooms < maxRooms) {// && failuresSinceLastRoomGeneration < 100) {
            Room temp = null;
            if (!floor.getRoomQueue().isEmpty()) {
                if (failuresSinceLastRoomGeneration>20) {
                    temp = floor.getRoomQueue().dequeue();
                    if (temp==null) {
                        System.out.println("BREAK! on while-loop's first deque ");
                        break;
                    }
                    failuresSinceLastRoomGeneration=0;
                } else {
                temp = floor.getRoomQueue().front();
                }
        
            }

            
            if (floor.getRoomQueue().isEmpty() && failuresSinceLastRoomGeneration>20) {
                failuresSinceLastRoomGeneration = 101;
                cx = -999;
                System.out.println("epic fail! Queue failed on room " + rooms);
                break;
            } else if (temp != null) {
                System.out.println("löytyi q-room #"+temp.id+" at "+temp.location.x+","+temp.location.y);
                System.out.println("samaan aikaan queue size "+floor.getRoomQueue().getSize());
                cx = temp.location.x;
                cy = temp.location.y;
            }

            int arpa = randomi.nextInt(4);


            char from = ' ';
            char out = ' ';

            boolean roomGenerated = false;
            boolean obstacleMet = false;

            // after picking a general direction (north, west, east, south) the algorithm
            // makes three tries into that direction trying to generate the next room
            // 
            for (int tries = 0; tries < spec.pivotSeekPersistence && !roomGenerated; tries++) {

                if (arpa == 0 && tries < spec.pivotSeekPersistence) {
                    from = 'n';
                    out = 's';
                    cy--;
                }
                if (arpa == 1 && tries < spec.pivotSeekPersistence) {
                    from = 's';
                    out = 'n';
                    cy++;
                }
                if (arpa == 2 && tries < spec.pivotSeekPersistence) {
                    from = 'w';
                    out = 'e';
                    cx--;
                }
                if (arpa == 3 && tries < spec.pivotSeekPersistence) {
                    from = 'e';
                    out = 'w';
                    cx++;
                }

                // if the room generation process goes out of bounds or meets and obstace,
                // the algorithm randomly picks a new point of origin (room) and restarts the
                // room generation process from there
                if (cx < 0 || cx > 9 || cy < 0 || cy > 9 || obstacleMet) {
                    failuresSinceLastRoomGeneration++;

                    temp = null;
                    System.out.println("floor-q empty: " + floor.getRoomQueue().isEmpty());
                    if (!floor.getRoomQueue().isEmpty()) {
                        temp = floor.getRoomQueue().front();
                    }

                    if (temp != null) {
                        System.out.println("löytyi q-room #" + temp.id);
                        cx = temp.location.x;
                        cy = temp.location.y;
                    } else {
                        
                        System.out.println("Queue failed on room " + rooms);
                    }

                    temporaryOrigin = new Point(cx, cy);
                    out = ' ';
                    from = ' ';
                    
                } else if (cx >= 0 && cx < 10 && cy >= 0 && cy < 10) {  // seeking must stay within the  outer bounds of the floor
                    
                    Dimension dimension = new Dimension(10, 10);
                    System.out.println("specs. "+spec.twoByOnes);
                    
                    int roomHash = (temporaryOrigin.x*temporaryOrigin.y)+arpa+(rooms%3)+cx+cy-floor.getRoomQueue().getSize();
                    
                    if ((roomHash) % spec.twoByOnes == 1 && tries <= spec.midsizeRoomPersistence) {
                        dimension = new Dimension(20, 10);
                    }
                    if ((roomHash) % spec.twoByOnes == 2 && tries <= spec.midsizeRoomPersistence) {
                        dimension = new Dimension(10, 20);
                    }
                    if ((roomHash) % spec.twoByTwos == 3 && tries <= spec.largeRoomPersistence) {
                        dimension = new Dimension(20, 20);
                    }
                    if (( roomHash) % spec.threeByThrees == 4 && tries <= spec.largeRoomPersistence) {
                        dimension = new Dimension(30, 30);
                    }
                    // if the insert room method call returns TRUE then the new room was successfully placed into the map!
                    if (floor.insertRoom(cx, cy, dimension, from, temporaryOrigin, rooms)) {
                        roomGenerated = true;
                  
                      
                        temporaryOrigin = new Point(cx, cy);
                        failuresSinceLastRoomGeneration = 0;
                        rooms++;

                        floor.roomLayout[cx][cy].outDirection = out;
                        temp = null;
                      
                        if (!floor.getRoomQueue().isEmpty()) {
                            temp = floor.getRoomQueue().dequeue();      // here we fetch the next active room from which the dungeon generation proceeds from
                                                                        // PLEASE NOTE that the dequeue() CAN result the SAME active room as before if the room still has
                                                                        // active pivots (outbound passageways) left
                        }

                        if (temp != null) {
                            cx = temp.location.x;
                            cy = temp.location.y;
                        } else {
                            System.out.println("*** OH NOES! ****");
                            System.out.println("Queue failed on room " + rooms);
                        }
                    } 
                    // if the room was too large to generate in THIS location, the algorithm generates a passage instead
                    // however, passageway must be placed in a proper direction when the seek has reached the outer bounds of the floor
                    else if (floor.roomLayout[cx][cy] == null && tries <= spec.passagePersistence
                            && !(from == 'n' && cy == 0) && !(from == 's' && cy == 9) && !(from == 'w' && cx == 0) && !(from == 'e' && cx == 9)) {
                        
                        floor.noRoom[cx][cy]=true;  // this basically marks a grid coordinate where no future rooms can be placed.
                                                    // however, this grid coordiate is still valid realestate for passageways
                        

                    } else if (floor.roomLayout[cx][cy] != null) {
                        obstacleMet = true;
                    }
                }
            }
            failuresSinceLastRoomGeneration++;
            System.out.println("failures now " + failuresSinceLastRoomGeneration);
        }
// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
// THE MAIN DUNGEON GENERATION WHILE-LOOP TERMINATES ABOVE!        
//   =======================================
        
        
        
        // print out the results (the entire dungeon floor)
        floor.print();
      
        // adds some random passage ways to the floor
        floor.addRandomRoute(false);
        floor.addRandomRoute(false);
        floor.addRandomRoute(false);
        floor.addRandomRoute(true);

        PassageCarver.processAllRoutes(floor);
        DungeonPrinter.printFloor(floor);
        //floor.carveRoutes();

        this.floors[0] = floor;
    }

    /**
     * Returns the array of floors
     * NOT IN USE CURRENTLY!
     * @return array of floors
     */
    public Floor[] getFloors() {
        return this.floors;
    }

    /**
     * Method for calling out room id numbers tied to specific tiles (id 0 = tile is not related to any specific room)
     * NOT IN USE CURRENTLY!
     * @return 3D-array of id numbers
     */
    public int[][][] getTileIDs() {
        return this.tileID;
    }
}
