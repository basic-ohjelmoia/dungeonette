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
 * @author mikromafia
 */
public class Carver {
 
 
               /**
     * This method draws out the passages between the rooms AFTER all
     * rooms have been placed.
     * 
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

            startX=(Math.max(startX, 1)); startX=(Math.min(startX, 98));
            startY=(Math.max(startY, 1)); startY=(Math.min(startY, 98));
            
            endX=(Math.max(endX, 1)); endX=(Math.min(endX, 98));
            endY=(Math.max(endY, 1)); endY=(Math.min(endY, 98));
            
            
            
            int cx = startX;
            int cy = startY;
            System.out.println("route " + i + " from " + cx + "," + cy + ", to " + endX + "," + endY);
            int chaos = 0;
   
            // the while loop keeps on going until the route has been carved all the way to the destination
            // coordinates
        
            int chaosStepCounter=0;
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

                // map key:
                // +    ... floor
                // #    ... outer wall
                // .    ... unused space (solid ground or whatnot)
                System.out.println("cx nw : "+cx+","+cy);
                
                
                
                if (tiles[cx][cy] != '+') {
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

        System.out.println("printing II...");
        for (int y = 0; y < 100; y++) {
            System.out.print("\n");
            for (int x = 0; x < 100; x++) {

                if (tiles[x][y] == 0) {
                    System.out.print("..");

                } else {

                    
                    Room room = floor.roomLayout[x / 10][y / 10];

                    if (room!=null && tiles[x][y]=='+' && x%10==4 && y%10==4) {
                        
                        if (floor.getRoomCount()==room.id) {
                            System.out.print("EXIT");x++;
                        }
                        else if (room.id==1) {
                            System.out.print("ENTR");x++;
                        }
                        else {
                                                if (room.id<10) {
                            System.out.print("0");
                        }
                        System.out.print(room.id);
                        }

                        
                    } else {
                    
                    System.out.print(tiles[x][y] + "" + tiles[x][y]);
                    }
                }
            }
        }

    }
    
}
