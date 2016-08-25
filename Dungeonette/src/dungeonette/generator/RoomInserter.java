/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Floor;
import dungeonette.domain.Room;
import dungeonette.domain.Specification;
import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * Class used to examine wether a room of specific size can be placed on a specific location of a floor
 */
public class RoomInserter {

    /**
     * A method which tries to insert a seeIfItFits of specific size into
     * specific location on the floor map. Returns TRUE if the placement was
     * successful.
     *
     * @param floor Floor being processed
     * @param spec Specification for the dungeon
     * @param rlx x coordinate for the new seeIfItFits location
     * @param rly y coordinate for the new seeIfItFits location
     * @param dimension dimensions of the new seeIfItFits (either 10x10, 20x10,
     * 10x20, 20x20 or 30x30)
     * @param fromDirection general direction of the previous (connecting)
     * seeIfItFits
     * @param origin location of the previous (connecting) room
     * @param currentRoomID serial number which will be given to the new
     * seeIfItFits (if placement is successful)
     * @return true if the placement was successful
     */
    public static boolean seeIfItFits(Floor floor, Specification spec, int rlx, int rly, Dimension dimension, char fromDirection, Point origin, int currentRoomID) {

        
        if (rlx<0 || rly<0 || rlx>=spec.gridX || rly>=spec.gridY ) {
            System.out.println("rlx "+rlx+" rly "+rly+" | yritetriin insertoida yli boundaryn");
            return false;
        }
        if (floor.noRoom[rlx][rly]) {
            return false;
        }
        
        
        int size = (dimension.height * dimension.width) / 100;
        //System.out.println("room size " + size + ", dims " + dimension.width + "." + dimension.height);
        
        
        
        // as the seeIfItFits might be too large to fit into a single 10 x 10 grid, these two arrays are used
        // to store the seeIfItFits's theoretical grid locations 
        int reqX[] = new int[size];
        int reqY[] = new int[size];

        // centerX and centerY refer to the supposed center point of the seeIfItFits (important for proper
        // passage connectivity when the seeIfItFits is too large to fit into a single 10x10 grid ). 
        int centerX = 0;
        int centerY = 0;

        // xStep and yStep are used to ensure that the seeIfItFits is oriented correctly in relation to the  floor map boundaries
        int xStep = 1;
        int yStep = 1;

        if (rlx < 5) {
            xStep = -1;

        }
        if (rly < 5) {
            yStep = -1;
        }
        if (size == 1) {
            xStep = 0;
            yStep = 0;
        }

        if (size > 1) {
            if (rlx + xStep > 9 || rly + yStep > 9 || rlx + xStep < 0 || rly + yStep < 0
                    || rlx > 9 || rly > 9 || rlx < 0 || rly < 0) {
                return false;
            }
            if (size == 9 && (rlx < 3 || rlx > 7 || rly < 3 || rly > 7)) {
                return false;
            }
        }
        if (size >= 4) {

            reqX[1] = rlx + xStep;
            reqY[1] = rly;
            reqX[2] = rlx;
            reqY[2] = rly + yStep;
            reqX[3] = rlx + xStep;
            reqY[3] = rly + yStep;
            if (size == 9) {
                reqX[4] = rlx + xStep + xStep;
                reqY[4] = rly;
                reqX[5] = rlx + xStep + xStep;
                reqY[5] = rly + yStep;
                reqX[6] = rlx + xStep + xStep;
                reqY[6] = rly + yStep + yStep;
                reqX[7] = rlx;
                reqY[7] = rly + yStep + yStep;
                reqX[8] = rlx + xStep;
                reqY[8] = rly + yStep + yStep;
                xStep = xStep + xStep;
                yStep = yStep + yStep;
            }

        } else if (size == 2) {
            if (dimension.width > 10) {

                if (rlx >= 9) {
                    return false;
                }

                reqX[1] = rlx + xStep;
                reqY[1] = rly;
                yStep = 0;
            }

            if (dimension.height > 10) {

                if (rly >= 9) {
                    return false;
                }

                reqX[1] = rlx;
                reqY[1] = rly + yStep;
                xStep = 0;
            }

        }

        reqX[0] = rlx;
        reqY[0] = rly;

        boolean failed = false;
     
        // if any of the required sub-grids of the seeIfItFits are already in use, the seeIfItFits placement will FAIL
        for (int i = 0; i < size && !failed; i++) {
           
            if (floor.roomLayout[reqX[i]][reqY[i]] != null) {
                failed = true;
                continue;
            }
            if (floor.noRoom[reqX[i]][reqY[i]]) {
                failed = true;
            }
            centerX += reqX[i];
            centerY += reqY[i];
        }
        if (failed) {
      
            return false;
        }
        centerX /= size;
        centerY /= size;

        // Here the actual seeIfItFits object gets constructed
        Room room = new Room(new Point(Math.min(rlx, rlx + xStep), Math.min(rly, rly + yStep)), dimension, currentRoomID, fromDirection, spec.randomi);

        // The following sets the passage information between the new seeIfItFits and the previous connecting seeIfItFits
        room.roomCenter = new Point(centerX, centerY);

        int routes = floor.getRoutes();

        floor.getRouteFrom()[routes] = origin;

        floor.getRouteTo()[routes] = room.getDoorway();
       
        
        System.out.println("room.id: "+room.id);
        System.out.println("Origin is null ? "+origin==null);
        System.out.println("floor.roomLayout[origin.x][origin.y].id is null? "+(floor.roomLayout[origin.x][origin.y]==null));
        if (room.id == 1) {
            floor.getRouteIDFrom()[routes] = 0;
        } else {
            floor.getRouteIDFrom()[routes] = floor.roomLayout[origin.x][origin.y].id;
        }

        floor.getRouteIDTo()[routes] = room.id;
        
        
//        System.out.println("Origin x ja y "+origin.x+","+origin.y);
//        System.out.println("| lokaatio layoutissa null?: "+(floor.roomLayout[origin.x][origin.y]==null));
        
        
        if (floor.roomLayout[origin.x][origin.y] != null && currentRoomID>1) {
            floor.getRouteFrom()[routes] = floor.roomLayout[origin.x][origin.y].getDoorway();
        } else {
            floor.getRouteFrom()[routes] = floor.getRouteTo()[routes];
        }

//        System.out.println("routeFrom = " + floor.getRouteFrom()[routes].toString());
//        System.out.println("routeTO = " + floor.getRouteTo()[routes].toString());
//        System.out.println("Room loc = " + room.location.toString() + " vs center " + room.roomCenter.toString());
        
        floor.addRouteCount();

        for (int i = 0; i < size; i++) {
            floor.roomLayout[reqX[i]][reqY[i]] = room;

        }
        floor.getRoomQueue().enqueue(room);

        floor.addRoomCount();
        floor.pointOfExit = new Point (centerX, centerY);//room.location;
        return true;

    }

}
