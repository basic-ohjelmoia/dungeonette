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

        System.out.println("routes " + routes);

        // the for loop processes each pair of passage/route departures and destinations.
        for (int i = 0; i < floor.getRoutes(); i++) {
            // System.out.println("i = "+i+", routef "+floor.getRouteFrom()[i].toString());
            int startX = (floor.getRouteFrom()[i].x);// * 10) + 5;
            int startY = (floor.getRouteFrom()[i].y);// * 10) + 5;

            int endX = (floor.getRouteTo()[i].x);// * 10) + 5;
            int endY = (floor.getRouteTo()[i].y);// * 10) + 5;

            int idTrueFrom = floor.getRouteIDFrom()[i];
            int idTrueTo = floor.getRouteIDTo()[i];
            int idMin = floor.getRouteIDFrom()[i] - 5;
            int idMax = floor.getRouteIDFrom()[i] + 5;

            if ((floor.connected[idTrueFrom] || floor.connected[idTrueTo]) && floor.connected[idTrueTo] != floor.connected[idTrueFrom]) {
                //   System.out.println("from  (#"+idTrueFrom+"): "+floor.connected[idTrueFrom]+" vs to  (#"+idTrueTo+"): "+floor.connected[idTrueTo]);
                floor.connected[idTrueFrom] = true;
                floor.connected[idTrueTo] = true;
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

            int stepCounter = 0;
            int passageCounter = 0;
            int wallPasses = 0;

            boolean crossyGenerated = false;

            while (true) {

                int oldChaos = chaos;
                char dir = ' ';
                int roomID = floor.getTileIDs()[cx][cy];
                boolean isItACrossyPassage = floor.isCrossyPassage[i];

                //   System.out.println("passage "+i+", onko crossy: "+isItACrossyPassage);
                // "chaos" tries to add some randomness to the passage drawa. 
                // without it, all the passages would be boring straight lines
                if (chaos < 3 && cx > 1 && cx < spec.maxX - 2 && cy > 1 && cy < spec.maxY - 2 && !isItACrossyPassage) {
                    if ((cx + cy) % 29 == 3) {
                        cx++;
                        dir = 'e';
                        chaos++;
                    } else if ((cx + cy) % 29 == 17) {
                        cx--;
                        dir = 'w';
                        chaos++;
                    } else if ((cx + cy) % 29 == 13) {
                        cy++;
                        dir = 's';
                        chaos++;
                    } else if ((cx + cy) % 29 == 27) {
                        cy--;
                        dir = 'n';
                        chaos++;
                    }
                }

                if (oldChaos == chaos) {
                    if (cx < endX) {
                        cx++;
                        dir = 'e';
                    } else if (cx > endX) {
                        cx--;
                        dir = 'w';
                    } else if (cy < endY) {
                        cy++;
                        dir = 's';
                    } else if (cy > endY) {
                        cy--;
                        dir = 'n';
                    }
                }
                
                if (floor.roomLayout[cx/10][cy/10]!=null) {
                    floor.getItems()[cx][cy]=0;
                }
                

                boolean midPasssage = (((dir == 'n' || dir == 's') && (tiles[cx + 1][cy] == '+' && tiles[cx + 1][cy] == '#' && tiles[cx - 1][cy] == '#'))
                        || ((dir == 'w' || dir == 'e') && (tiles[cx + 1][cy] == '+' && tiles[cx][cy + 1] == '#' && tiles[cx][cy - 1] == '#')));

                if (midPasssage) {
                    passageCounter++;
                } else {
                    passageCounter = 0;
                }

                // Below the code tries to place a marker for a POSSIBLE (not guaranteed!) door location
                // The door placement currently works rather poorly, basically resulting only a fraction of the doors
                // had been given markers. This is due to the fact that the passages added actually reshape the room boundaries
                // therefore making many of the previously placed door markers invalid!
                if (tiles[cx][cy] == '+' && floor.getTileIDs()[cx][cy] != previousTileID) {
                    //    System.out.println("oveksi merkittiin "+cx+","+cy);
                    //  floor.getDoorTiles()[cx][cy]=1;

                }
                if (floor.roomLayout[cx / 10][cy / 10] != null) {
                    if (floor.roomLayout[cx / 10][cy / 10].id != previousTileID && (floor.roomLayout[cx / 10][cy / 10].id == idTrueTo || floor.roomLayout[cx / 10][cy / 10].id == idTrueFrom)) {
                        floor.getDoorTiles()[cx][cy] = 1;
                        floor.getTileIDs()[cx][cy] = floor.roomLayout[cx / 10][cy / 10].id;
                        roomID = floor.getTileIDs()[cx][cy];
                    }
                }

                previousTileID = roomID;

                // map key:
                // +    ... floor
                // #    ... outer wall
                // .    ... unused space (solid ground or whatnot)
                // the strange id checks are basically used to ensure that the dungeon doesn't emd up TOO connected
                // ideally you're only connecting the two rooms the passageway is inteded to connect, and don't touch any
                // rooms in between
                // however, connecting a room with no legal route to the dungeon entrance trumps any other considerations!!!
                char tile = tiles[cx][cy];
                if (tile == '#') {
                    wallPasses++;
                }

                // The following 'if' is used to check wether the tile carver is about to remove a wall tile AND IF SO wether the wall removal is LEGAL. 
                if (tile != '+' && ((isItACrossyPassage && wallPasses <= 1) || 
                        ((roomID == 0 || idTrueFrom == 0 || idTrueTo == 0 || roomID == idTrueTo || (roomID >= idMin && roomID <= idMax))))) {
                    
                    tiles[cx][cy] = '+';
                    if (isItACrossyPassage) {
                        floor.debugTiles[cx][cy] = '¤';

                    }

                    wallPasses = checkForAWallPass(cx, cy, wallPasses, dir, tiles);

                } else if (isItACrossyPassage && wallPasses > 1) {
                    break;
                }

//                
                if (cx == endX && cy == endY) {
                    System.out.println("terminated at " + cx + "," + cy);
                    break;

                }
                stepCounter++;

                if (passageCounter > 6 && midPasssage && i % 2 == 0 && floor.getRoutes() < 199 && !crossyGenerated) {
                    floor.addCrossyPassage(new Point(cx, cy), dir);
                    crossyGenerated = true;
                }

            }
        }

    }

    /**
     * This method checks wether a wall tile is about to be passaed.
     * It also surrounds any such floor tile with walls which has a blank '.' tile next to it.
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

}
