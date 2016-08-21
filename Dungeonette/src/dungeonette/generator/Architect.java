/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Environment;
import dungeonette.domain.Floor;
import dungeonette.domain.Room;
import dungeonette.domain.Specification;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 *
 *  Architect contains the main loop used for generating the dungeon floors.
 */
public class Architect {

    /**
     * This enum represents north, south, east and west .
     */
    public enum Dir {

        NORTH('n', 0, 0, -1),
        SOUTH('n', 1, 0, 1),
        WEST('n', 2, -1, 0),
        EAST('n', 3, 1, 0);

        public final char name;
        public final int id;
        public final int x;
        public final int y;

        /**
         * Constructor for directions.
         * @param name char representation of the direction
         * @param id id number of the direction (these are referred to elsewhere in the code)
         * @param x this direction represents this much change in the x coordinate
         * @param y this direction represents this much change in the y coordinate
         */
        private Dir(char name, int id, int x, int y) {
            this.name = name;
            this.id = id;
            this.x = x;
            this.y = y;
        }

        /**
         * Returns a direction based on the id number
         * @param id id number of the direction
         * @return a direction is returned
         */
        public static Dir getDir(int id) {
            if (id == 0) {
                return Dir.NORTH;
            } else if (id == 1) {
                return Dir.SOUTH;
            } else if (id == 2) {
                return Dir.WEST;
            }

            return Dir.EAST;

        }

        @Override
        public String toString() {
            return "" + name;
        }
    }

    /**
     * A much refactored  method that basically runs the entirety of the dungeon
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
     * 1) The very first room is placed on the point of origin of the floor map.
     * This location should be considered as the entranced to the dungeon floor
     * (ex. staircase) 2) each new room gets placed into a queue. While the
     * active room has "pivots" left, the algorithm tries to create a neighbour
     * room for this room to connect to. If valid spot to place this neighbor is
     * found, the active room and the neighbouring room are marked for a
     * connecting passage (the passage itself is not yet generated). 3) if a new
     * room was created, then that will become the NEW active room. If no
     * neighbour was created, then the new active room gets dequeued from the
     * room queue. 4) This process goes on and on until enough rooms have been
     * generated OR there are no active rooms with pivots left. 5) After all the
     * rooms have been generated, the passages betweem them will get carved out.
     *
     * @param env Container of the Environment where the dungeon is stored
     * @param spec Specification of the dungeon
     * @param floorLevel floorlevel (number) being generated
     * @param pointOfEntry coordinates for the first room's entry stairwell
     */
    public static void generateFloor(Environment env, Specification spec, int floorLevel, Point pointOfEntry) {

        Floor floor = new Floor(spec, pointOfEntry); // The point object here refers the point of entry (first room location) of the floor.

        int rooms = 1;        // this is basically the serial number of the room currently being generated. Must start from one NOT zero!

        Random randomi = new Random();

        int cx = pointOfEntry.x;       // cx and cy refer to the "current x coordinate" and "current y coordinate" of the dungeon generation process.
        int cy = pointOfEntry.y;       // cx and cy are based on the coarse grid 10x10 format
        
        // as the algorithm actually handles the dungeon in a coarse grid of 10 x 10 tiles
        // the point of origin coordinates need to be divided by 10

        Point temporaryOrigin = new Point(cx, cy);       // this information is needed in order to connect
        // passages from the point of origin and the next room
        // being generated

        int maxRooms = randomi.nextInt(Math.max(2, (spec.volatility-(spec.funnelEffect*floorLevel))))
                + Math.max(0, (spec.density - (spec.funnelEffect*floorLevel)));            // maximum number of rooms being generated for the dungeon
        // the actualy reaching of "the max" is NOT guaranteed currently

        System.out.println("%%%% floorlevel " + floorLevel + " start: " + cx + "," + cy+", max rooms:  "+maxRooms);
        
        int failuresSinceLastRoomGeneration = 0;          // a safety which ensures that the algorithm eventually fails in case it reaches a logical dead-end

        // HERE WE START BY INSERTING THE ENTRY POINT ROOM INTO THE FLOOR AS THE ROOM #1
        RoomInserter.seeIfItFits(floor, spec, cx, cy, new Dimension(10, 10), 'n', temporaryOrigin, rooms);
        rooms++;

        // ================
        // IMPORTANT!!!!!!!
        // ================
        // below lies the main loop used for the dungeon generation
        while (rooms < maxRooms) {// && failuresSinceLastRoomGeneration < 100) {
            Room parentOfTheNextRoom = null;

            if (!floor.getRoomQueue().isEmpty()) {
                if (failuresSinceLastRoomGeneration > 20) {
                    parentOfTheNextRoom = floor.getRoomQueue().dequeue();
                    if (parentOfTheNextRoom == null) {
                        System.out.println("BREAK! on while-loop's first deque ");
                        break;
                    }
                    failuresSinceLastRoomGeneration = 0;
                } else {
                    parentOfTheNextRoom = floor.getRoomQueue().front();
                }

            }

            if (floor.getRoomQueue().isEmpty() && failuresSinceLastRoomGeneration > 20) {
                failuresSinceLastRoomGeneration = 101;

                //   System.out.println("epic fail! Queue failed on room " + rooms);
                break;
            } else if (parentOfTheNextRoom != null) {

                cx = parentOfTheNextRoom.location.x;
                cy = parentOfTheNextRoom.location.y;
            }

            int arpa = randomi.nextInt(4);

            char from = ' ';

            boolean roomGenerated = false;
            boolean obstacleMet = false;

            // after picking a general direction (north, west, east, south) the algorithm
            // makes three tries into that direction trying to generate the next room
            // 
            for (int tries = 0; tries < spec.pivotSeekPersistence && !roomGenerated; tries++) {

                if (tries < spec.pivotSeekPersistence) {
                    Dir direction = Dir.getDir(arpa);
                    from = direction.name;
                    cx += direction.x;
                    cy += direction.y;
                }

                // if the room generation process goes out of bounds or meets and obstace,
                // the algorithm randomly picks a new point of origin (room) and restarts the
                // room generation process from there
                if (cx < 0 || cx > spec.gridX-1 || cy < 0 || cy > spec.gridY-1 || obstacleMet) {
                    failuresSinceLastRoomGeneration++;

                    parentOfTheNextRoom = null;

                    

                } else if (cx >= 0 && cx < spec.gridX && cy >= 0 && cy < spec.gridY) {  // seeking must stay within the  outer bounds of the floor

                    Dimension dimension = new Dimension(10, 10);

                    int roomHash = (parentOfTheNextRoom.location.x * parentOfTheNextRoom.location.y) + arpa + (rooms % 3) + cx + cy - floor.getRoomQueue().getSize();

                    if ((roomHash) % spec.twoByOnes == 1 && tries <= spec.midsizeRoomPersistence) {
                        dimension = new Dimension(20, 10);
                    }
                    if ((roomHash) % spec.twoByOnes == 2 && tries <= spec.midsizeRoomPersistence) {
                        dimension = new Dimension(10, 20);
                    }
                    if ((roomHash) % spec.twoByTwos == 3 && tries <= spec.largeRoomPersistence) {
                        dimension = new Dimension(20, 20);
                    }
                    if ((roomHash) % spec.threeByThrees == 4 && tries <= spec.largeRoomPersistence) {
                        dimension = new Dimension(30, 30);
                    }

                    if (rooms == 1) {
                        dimension = new Dimension(10, 10);
                    }

                    // if the insert room method call returns TRUE then the new room was successfully placed into the map!
                    if (RoomInserter.seeIfItFits(floor, spec, cx, cy, dimension, from, parentOfTheNextRoom.location, rooms)) {
                        roomGenerated = true;

                        
                        failuresSinceLastRoomGeneration = 0;
                        rooms++;

                        parentOfTheNextRoom = null;

                      
                    } // if the room was too large to generate in THIS location, the algorithm generates a passage instead
                    // however, passageway must be placed in a proper direction when the seek has reached the outer bounds of the floor
                    else if (floor.roomLayout[cx][cy] == null && tries <= spec.passagePersistence
                            && !(from == 'n' && cy == 0) && !(from == 's' && cy == spec.gridY-1) && !(from == 'w' && cx == 0) && !(from == 'e' && cx == spec.gridX-1)) {

                        floor.noRoom[cx][cy] = true;  // this basically marks a grid coordinate where no future rooms can be placed.
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

        finalize(floor, spec);
        env.getFloors()[floorLevel] = floor;
    }

    /**
     * Finalizes the dungeon floor by storing the room tiles into the char matrix, adding connecting passages, adding random passages and placing doorways.
     * @param floor floor being processed
     * @param spec specification of the dungeon containg the floor
     */
    private static void finalize(Floor floor, Specification spec) {
        // storeRoomsIntoTiles out the results (the entire dungeon floor)
        floor.storeRoomsIntoTiles();

        // adds some random passage ways to the floor
        for (int i = 0; i < spec.deadEndiness; i++) {
            floor.addRandomRoute(false);

        }

        for (int i = 0; i < spec.roomConnectivity; i++) {

            floor.addRandomRoute(true);
        }

        
        
        PassageCarver.processAllRoutes(floor, spec);
        DoorInserter.processAllDoors(floor, spec);

        
    }

}
