/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Floor;
import dungeonette.domain.Room;
import dungeonette.domain.Specification;
import java.awt.Point;

/**
 *
 * Class used to generate passages between existing rooms. Only run after ALL
 * rooms have been generated!
 */
public class PassageCarver {

    /**
     * This method draws out the passages between the rooms AFTER all rooms have
     * been placed.
     *
     * @param floor floor being carved
     * @param spec specification for the dungeon containing the floor
     */
    public static void processAllRoutes(Floor floor, Specification spec) {

        char[][] tiles = floor.getTiles();
        int routes = floor.getRoutes();

        // the for loop processes each pair of passage/route departures and destinations.
        for (int i = 0; i < floor.getRoutes(); i++) {
            // System.out.println("i = "+i+", routef "+floor.getRouteFrom()[i].toString());
            int startX = (floor.getRouteFrom()[i].x);// * 10) + 5;
            int startY = (floor.getRouteFrom()[i].y);// * 10) + 5;

            int endX = (floor.getRouteTo()[i].x);// * 10) + 5;
            int endY = (floor.getRouteTo()[i].y);// * 10) + 5;

            int idTrueFrom = floor.getRouteIDFrom()[i];     // id number of the room from which the passage originates from
            int idTrueTo = floor.getRouteIDTo()[i];         // id number of the room to which the passage is targetting
            int idTrueFromMemory = idTrueFrom;
            
            int idMin = floor.getRouteIDFrom()[i] - 5;      // passages are allowed to punch thru non related rooms within -5...+5 range of id numbers
            int idMax = floor.getRouteIDFrom()[i] + 5;

            // A connected room is a room that has a legal pathway to the first room of the floor.
            // If the passage is being carved between two rooms (i.e. it's not a dead-end) then if either of these rooms is "connected", the passage will make both of them "connected"
            boolean atleastOneOfTheRoomsHasAccessToFirstRoomOfTheFloor = ((floor.connected[idTrueFrom] || floor.connected[idTrueTo]) && floor.connected[idTrueTo] != floor.connected[idTrueFrom]) ;
            
            if (atleastOneOfTheRoomsHasAccessToFirstRoomOfTheFloor) {

//                floor.connected[idTrueFrom] = true;
//                floor.connected[idTrueTo] = true;
                idTrueFrom = 0;

            }

            startX = (Math.max(startX, 1));
            startX = (Math.min(startX, spec.maxX - 2));
            startY = (Math.max(startY, 1));
            startY = (Math.min(startY, spec.maxY - 2));

            endX = (Math.max(endX, 1));
            endX = (Math.min(endX, spec.maxX - 2));
            endY = (Math.max(endY, 1));
            endY = (Math.min(endY, spec.maxY - 2));

            int cx = startX;
            int cy = startY;

            int chaos = 0;

            // the while loop keeps on going until the route has been carved all the way to the destination
            // coordinates
            int chaosStepCounter = 0;
            int previousTileID = idTrueFrom;

            boolean passageIsNowDetouring = false;
            int detourLength = 0;
            Architect.Dir detourDirection = Architect.Dir.NORTH;
            int detoursConducted = 0;

            int stepCounter = 0;
            int passageCounter = 0;
            int wallPasses = 0;

            boolean crossyGenerated = false;
          //  boolean neverDetour = false;
            
            boolean forceN = false;
            boolean forceS = false;
            boolean forceW = false;
            boolean forceE = false;

            while (true) {

                Architect.Dir direction = Architect.Dir.NORTH;
                boolean notNearFloorBoundary = cx > 1 && cx < spec.maxX - 2 && cy > 1 && cy < spec.maxY - 2;
                if (notNearFloorBoundary) {
                    forceN=false;forceS=false;forceW=false;forceE=false;
                }
                
                boolean forceDirection = (forceN || forceS || forceW || forceE);
                
                if ((cx < endX && !forceDirection) || forceE) {
                    direction = Architect.Dir.EAST;
                } else if ((cx > endX && !forceDirection) || forceW) {
                    direction = Architect.Dir.WEST;
                } else if ((cy < endY && !forceDirection) || forceS) {
                    direction = Architect.Dir.SOUTH;
                } else if ((cy > endY && !forceDirection) || forceN) {
                    direction = Architect.Dir.NORTH;
                }
                
                
                
                
                int roomID = floor.getTileIDs()[cx][cy];
                boolean isItACrossyPassage = floor.isCrossyPassage[i];
                
                boolean detourFrequencyReached = passageCounter > 2 + (2 * detoursConducted);
                boolean detourIsGreenToGo = spec.randomi.nextInt(100) > Math.min(95, spec.passageStraightnessPercentile);

                // PassageDetouring is basically the process of adding extra bends to passageways which otherwise would continue straight
                if (!passageIsNowDetouring && !isItACrossyPassage && notNearFloorBoundary && detourFrequencyReached && detourIsGreenToGo) { // passageCounter>2+(2*detoursConducted) && 
                    passageIsNowDetouring = true;
                    detoursConducted++;
                    detourLength = spec.randomi.nextInt(7) + 1 + spec.randomi.nextInt(1);
                    boolean headsOrTails = spec.randomi.nextBoolean();

                    if (direction == Architect.Dir.EAST || direction == Architect.Dir.WEST) {
                        if (headsOrTails) {
                            detourDirection = Architect.Dir.NORTH;
                        } else {
                            detourDirection = Architect.Dir.SOUTH;
                        }
                    }
                    if (direction == Architect.Dir.NORTH || direction == Architect.Dir.SOUTH) {
                        if (headsOrTails) {
                            detourDirection = Architect.Dir.WEST;
                        } else {
                            detourDirection = Architect.Dir.EAST;
                        }
                    }
                }

                if (detourLength > 0 && notNearFloorBoundary && passageIsNowDetouring && !forceDirection) {
                    direction = detourDirection;
                    detourLength--;
                } else {
                    passageIsNowDetouring = false;
                    detourLength = 0;
                }

                char dir = direction.name;
                boolean midPasssage = (((dir == 'n' || dir == 's') && (tiles[cx][cy] == '+' && tiles[cx + 1][cy] == '#' && tiles[cx - 1][cy] == '#'))
                        || ((dir == 'w' || dir == 'e') && (tiles[cx][cy] == '+' && tiles[cx][cy + 1] == '#' && tiles[cx][cy - 1] == '#')));

                if (midPasssage) {
                    passageCounter++;
                } else {
                    passageCounter = 0;
                }

                cx += direction.x;
                cy += direction.y;

                if (floor.roomLayout[cx / 10][cy / 10] != null) {
                    floor.getItems()[cx][cy] = 0;     // passages supercede items
                }

                // Below the code tries to place a marker for a POSSIBLE (not guaranteed!) door location
                if (floor.roomLayout[cx / 10][cy / 10] != null) {
                    if (floor.roomLayout[cx / 10][cy / 10].id != previousTileID && (floor.roomLayout[cx / 10][cy / 10].id == idTrueTo || floor.roomLayout[cx / 10][cy / 10].id == idTrueFrom)) {
                        floor.getDoorTiles()[cx][cy] = 1; // a door location is added when the destination room boundary has been reached
                        floor.getTileIDs()[cx][cy] = floor.roomLayout[cx / 10][cy / 10].id;
                        roomID = floor.getTileIDs()[cx][cy];
                    }
                }

                previousTileID = roomID;

                // map key:
                // +    ... floor
                // #    ... outer wall
                // .    ... unused space (solid ground or whatnot)
                // the room id checks are basically used to ensure that the dungeon doesn't emd up TOO connected
                // ideally you're only connecting the two rooms the passageway is inteded to connect, and don't touch any
                // rooms in between
                // however, connecting a room with no legal route to the dungeon entrance trumps any other considerations!!!
                char tile = tiles[cx][cy];

                boolean tileIsAWall = tile == '#';
                boolean tileIsAFloor = tile == '+';
                boolean tileIsPartOfARoom = roomID != 0;
                boolean passageMissingEitherOriginOrDestination = (idTrueFrom == 0 || idTrueTo == 0);
                boolean passageHasReachedTheDestinationRoom = roomID == idTrueTo;
                boolean passageIsBreachingNeighbouringRoomID = roomID >= idMin && roomID <= idMax;
                boolean deadEndPassageIsStillWithinWallPassLimit = isItACrossyPassage && wallPasses <= 1;
                boolean deadEndPassageHasToppedTheWallPassLimit = isItACrossyPassage && wallPasses > 1;

                if (tileIsAWall) {
                    wallPasses++;
                }

                // The following 'if' is used to check wether the tile carver is about to remove a wall tile AND IF SO wether the wall removal is LEGAL. 
                if (!tileIsAFloor && notNearFloorBoundary
                        && (deadEndPassageIsStillWithinWallPassLimit || !tileIsPartOfARoom || passageMissingEitherOriginOrDestination
                        || passageHasReachedTheDestinationRoom || passageIsBreachingNeighbouringRoomID)) {

                    tiles[cx][cy] = '+';
                    if (isItACrossyPassage) {
                        floor.debugTiles[cx][cy] = '¤';

                    }

                    wallPasses = checkForAWallPass(cx, cy, wallPasses, dir, tiles);
                } else if (!notNearFloorBoundary && cx != endX && cy != endY && !isItACrossyPassage) {
                   // Point newDoorways = floor.
                    //cx=startX; cy=startY; neverDetour=true;
                    if (cx<5) {forceE=true;}
                    else if (cx>spec.maxX-5) {forceW=true;}
                    else if (cy<5) {forceS=true;}
                    else if (cy>spec.maxY-5) {forceN=true;}
                  //  System.out.println("Floor "+floor.level+" room #"+idTrueTo+" connection FAIL to room #"+idTrueFromMemory+", "+startX+","+startY+"->{"+cx+","+cy+"}->"+endX+","+endY);
                } else if (deadEndPassageHasToppedTheWallPassLimit) {
                    break;
                }

                if (cx == endX && cy == endY) {
                    if (atleastOneOfTheRoomsHasAccessToFirstRoomOfTheFloor) {
                    //    System.out.println("Floor "+floor.level+" room #"+idTrueTo+" was connected by room #"+idTrueFromMemory+", "+startX+","+startY+"->"+endX+","+endY);
                        floor.connected[idTrueFromMemory] = true;
                        floor.connected[idTrueTo] = true;
                    }
                    break;

                }
                stepCounter++;

                if (passageCounter > 6 && midPasssage && i % 2 == 0 && floor.getRoutes() < 199 && !crossyGenerated) {
                    floor.addCrossyPassage(new Point(cx, cy), dir);
                    crossyGenerated = true;
                }

            }
        }

        removeThinPassageWalls(floor, spec);

    }

    /**
     * This method checks wether a wall tile is about to be passaed. It also
     * surrounds any such floor tile with walls which has a blank '.' tile next
     * to it.
     *
     * @param cx carver x coordinate
     * @param cy carver y coordinate
     * @param wallPasses current number of wallpasses
     * @param dir current direction of the carving
     * @param tiles two dimensional char array of tiles being processed
     * @return updated number of wallpasses (the int might stay the same!)
     */
    public static int checkForAWallPass(int cx, int cy, int wallPasses, char dir, char[][] tiles) {
        
        for (int sy = cy - 1; sy <= cy + 1; sy++) {
            for (int sx = cx - 1; sx <= cx + 1; sx++) {
                if (sx == cx && sy == cy) {
                    sx++;
                }
                if (tiles[sx][sy] == '.') {
                    tiles[sx][sy] = '#';
                    if ((sx == -1 && sy == 0 && dir == 'w') || (sx == 1 && sy == 0 && dir == 'e')) {
                        wallPasses = 0;
                    }
                    if ((sy == -1 && sx == 0 && dir == 'n') || (sx == 0 && sy == 1 && dir == 's')) {
                        wallPasses = 0;
                    }
                }
            }
        }
        return wallPasses;
    }

    
    /**
     * If two passageways are running parrallel next to each other, then the wall between them will be removed,
     * creating a wider room-like space. While these open areas might look like rooms, they are not considered as such.
     * 
     * Thin wall will not be removed, if the tiles in question connect to a "real" (id numbered) room.
     * 
     * @param floor floor being checked for this wall removal
     * @param spec specification of the dungeon
     */
    public static void removeThinPassageWalls(Floor floor, Specification spec) {

        for (int y = 1; y < spec.maxY - 1; y++) {

            for (int x = 1; x < spec.maxX - 1; x++) {

                char tile = floor.getTiles()[x][y];

                if (tile == '#' && floor.getTileIDs()[x][y] == 0) {

                    if ((floor.getTiles()[x + 1][y] == '+' && floor.getTiles()[x - 1][y] == '+' && floor.getTileIDs()[x + 1][y] == 0 && floor.getTileIDs()[x - 1][y] == 0)
                            || (floor.getTiles()[x][y + 1] == '+' && floor.getTiles()[x][y - 1] == '+' && floor.getTileIDs()[x][y + 1] == 0 && floor.getTileIDs()[x][y - 1] == 0)) {
                        floor.getTiles()[x][y] = '+';
                    }
                }
            }
        }

    }

}
