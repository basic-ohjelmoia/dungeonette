/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Floor;
import dungeonette.domain.Room;

/**
 *
 * Class used to generate passages between existing rooms.
 * Only run after ALL rooms have been generated!
 */
public class PassageCarver {
 
 
               /**
     * This method draws out the passages between the rooms AFTER all
     * rooms have been placed.
     * 
     * @param floor floor being carved
     */
    public static void processAllRoutes(Floor floor) { 
            
        char[][] tiles=floor.getTiles();
        int routes = floor.getRoutes();
        
        System.out.println("routes " + routes);
    
        // the for loop processes each pair of passage/route departures and destinations.
        for (int i = 0; i < routes; i++) {
            System.out.println("i = "+i+", routef "+floor.getRouteFrom()[i].toString());
            int startX = (floor.getRouteFrom()[i].x);// * 10) + 5;
            int startY = (floor.getRouteFrom()[i].y);// * 10) + 5;

            int endX = (floor.getRouteTo()[i].x);// * 10) + 5;
            int endY = (floor.getRouteTo()[i].y);// * 10) + 5;

            int idTrueFrom = floor.getRouteIDFrom()[i];
            int idTrueTo = floor.getRouteIDTo()[i];
            int idMin = floor.getRouteIDFrom()[i]-5;
            int idMax = floor.getRouteIDFrom()[i]+5;
            
            if ((floor.connected[idTrueFrom] || floor.connected[idTrueTo]) && floor.connected[idTrueTo]!=floor.connected[idTrueFrom]) {
                System.out.println("from  (#"+idTrueFrom+"): "+floor.connected[idTrueFrom]+" vs to  (#"+idTrueTo+"): "+floor.connected[idTrueTo]);
                floor.connected[idTrueFrom]=true;
                floor.connected[idTrueTo]=true;
                idTrueFrom=0;
                
            }
           
            
            startX=(Math.max(startX, 1)); startX=(Math.min(startX, 98));
            startY=(Math.max(startY, 1)); startY=(Math.min(startY, 98));
            
            endX=(Math.max(endX, 1)); endX=(Math.min(endX, 98));
            endY=(Math.max(endY, 1)); endY=(Math.min(endY, 98));
            
            
            
            int cx = startX;
            int cy = startY;
          
            int chaos = 0;
   
            // the while loop keeps on going until the route has been carved all the way to the destination
            // coordinates
        
            int chaosStepCounter=0;
            int previousTileID=idTrueFrom;
                    
            while (true) {

                int oldChaos = chaos;

                // "chaos" tries to add some randomness to the passage drawa. 
                // without it, all the passages would be boring straight lines
                if (chaos < 3 && cx > 1 && cx < 98 && cy > 1 && cy < 98) {
                    if ((cx + cy) % 29 == 3) {
                        cx++;
                        chaos++;
                    } else if ((cx + cy) % 29 == 17) {
                        cx--;
                        chaos++;
                    } else if ((cx + cy) % 29 == 13) {
                        cy++;
                        chaos++;
                    } else if ((cx + cy) % 29 == 27) {
                        cy--;
                        chaos++;
                    }
                }

                if (oldChaos == chaos) {
                    if (cx < endX) {
                        cx++;
                    } else if (cx > endX) {
                        cx--;
                    } else if (cy < endY) {
                        cy++;
                    } else if (cy > endY) {
                        cy--;
                    }
                } 

             
                
                // Below the code tries to place a marker for a POSSIBLE (not guaranteed!) door location
                // The door placement currently works rather poorly, basically resulting only a fraction of the doors
                // had been given markers. This is due to the fact that the passages added actually reshape the room boundaries
                // therefore making many of the previously placed door markers invalid!
                
                int roomID = floor.getTileIDs()[cx][cy];
         
                if (tiles[cx][cy]=='+' && floor.getTileIDs()[cx][cy]!=previousTileID) {
                    System.out.println("oveksi merkittiin "+cx+","+cy);
                  //  floor.getDoorTiles()[cx][cy]=1;

                }
                if (floor.roomLayout[cx/10][cy/10]!=null) {
                    if (floor.roomLayout[cx/10][cy/10].id!=previousTileID && (floor.roomLayout[cx/10][cy/10].id==idTrueTo || floor.roomLayout[cx/10][cy/10].id==idTrueFrom)) {
                        floor.getDoorTiles()[cx][cy]=1;
                        floor.getTileIDs()[cx][cy]=floor.roomLayout[cx/10][cy/10].id;
                        roomID=floor.getTileIDs()[cx][cy];
                    }
                }
                
                previousTileID=roomID;
                        
                
                                // map key:
                // +    ... floor
                // #    ... outer wall
                // .    ... unused space (solid ground or whatnot)

                
                // the strange id checks are basically used to ensure that the dungeon doesn't emd up TOO connected
                // ideally you're only connecting the two rooms the passageway is inteded to connect, and don't touch any
                // rooms in between
                // however, connecting a room with no legal route to the dungeon entrance trumps any other considerations!!!
                if (tiles[cx][cy] != '+' && (roomID==0 || idTrueFrom==0 || idTrueTo==0 || roomID==idTrueTo || (roomID>=idMin && roomID<=idMax))) {
                    tiles[cx][cy] = '+';
                    for (int sy = cy - 1; sy <= cy + 1; sy++) {
                        for (int sx = cx - 1; sx <= cx + 1; sx++) {
                            if (sx == cx && sy == cy) {
                                sx++;
                            }
                            if (tiles[sx][sy] == '.') {
                                tiles[sx][sy] = '#';
                            }
                        }
                    }
                }
                
                if (cx == endX && cy == endY) {
                    System.out.println("terminated at " + cx + "," + cy);
                    break;

                }
                
                

            }
        }
        
        System.out.println("set up doorways"); 
        for (int y = 0; y < 100; y++) {
            
            for (int x = 0; x < 100; x++) {
                if (floor.getDoorTiles()[x][y]==1) {
                        if (tiles[x+1][y]=='#' && tiles[x-1][y]=='#' && tiles[x][y-1]=='+' && tiles[x][y+1]=='+') {
                            tiles[x][y]='=';
                        } else if (tiles[x][y-1]=='#' && tiles[x][y+1]=='#' && tiles[x+1][y]=='+' && tiles[x-1][y]=='+' ) {
                            tiles[x][y]='|';
                        }
                    }
            }
        }

 

    }
    
}