/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import dungeonette.data.RoomQueue;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 * Floor is an object class which is used to store the information for a single
 * dungeon floor.
 *
 * Proper setters and getters to be implemeneted, so the class is not 100 %
 * encapsulated.
 *
 * The class contains program logic which should be refactored into separate classes.
 * 
 * @author tuomas honkala
 */
public class Floor {

    private char[][] tiles; // all coordinates of the floor map
    public Room[][] roomLayout; // room layout is stored into a coarse 10 x 10 grid 

    private int xMax;
    private int yMax;
    private Point entry;
    private Point[] routeFrom;  // array for start points of each passages
    private Point[] routeTo; // array for end points of each passages
    private int routes;   // number of passages generated so far (hardcoded max: 200)
    private int roomCount;
    private RoomQueue roomQueue;
    
    /**
     * Constructor for the floor.
     *
     * @param xMax Maximum width of the dungeon floor (please use 100 for now)
     * @param yMax Maximum height of the dungeon floor (please use 100 for now)
     * @param pointOfEntry The 10 by 10 grid location of the first room being
     * generated.
     */
    public Floor(int xMax, int yMax, Point pointOfEntry) {
        tiles = new char[xMax][yMax];
        roomLayout = new Room[xMax / 10][yMax / 10];
        this.xMax = xMax;
        this.yMax = yMax;
        this.entry = pointOfEntry;
        this.routeFrom = new Point[200];
        this.routeTo = new Point[200];
        this.roomQueue = new RoomQueue();
    }

    /**
     * A method which tries to insert a room of specific size into specific
     * location on the floor map. Returns TRUE if the placement was successful.
     *
     * @param rlx x coordinate for the new room location
     * @param rly y coordinate for the new room location
     * @param dimension dimensions of the new room (either 10x10, 20x10, 10x20,
     * 20x20 or 30x30)
     * @param fromDirection general direction of the previous (connecting) room
     * @param origin loxation of the previous (connecting) room
     * @param currentRoomID serial number which will be given to the new room
     * (if placement is successful)
     * @return true if the placement was successful
     */
    public boolean insertRoom(int rlx, int rly, Dimension dimension, char fromDirection, Point origin, int currentRoomID) {

        if (rlx<0 || rly<0 || rlx>=xMax/10 || rly>=yMax/10 ) {
            return false;
        }
        
        int size = (dimension.height * dimension.width) / 100;

        // as the room might be too large to fit into a single 10 x 10 grid, these two arrays are used
        // to store the room's theoretical grid locations 
        int reqX[] = new int[size];
        int reqY[] = new int[size];

        // centerX and centerY refer to the supposed center point of the room (important for proper
        // passage connectivity when the room is too large to fit into a single 10x10 grid ). 
        int centerX = 0;
        int centerY = 0;

        // xStep and yStep are used to ensure that the room is oriented correctly in relation to the  floor map boundaries
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
        if (size > 3) {

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

        } else if (dimension.width > 10) {

            if (rlx >= 9) {
                return false;
            }

            reqX[1] = rlx + xStep;
            reqY[1] = rly;
            yStep = 0;
        } else if (dimension.height > 10) {

            if (rly >= 9) {
                return false;
            }

            reqX[1] = rlx;
            reqY[1] = rly + yStep;
            xStep = 0;
        }

        reqX[0] = rlx;
        reqY[0] = rly;

        boolean failed = false;

        // if any of the required sub-grids of the room are already in use, the room placement will FAIL
        for (int i = 0; i < size && !failed; i++) {
            if (roomLayout[reqX[i]][reqY[i]] != null) {
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

        // Here the actual room object gets constructed
        Room room = new Room(new Point(Math.min(rlx, rlx + xStep), Math.min(rly, rly + yStep)), dimension, currentRoomID, fromDirection);

        // The following sets the passage information between the new room and the previous connecting room
        room.roomCenter = new Point(centerX, centerY);
        routeFrom[routes] = origin;
        routeTo[routes] = new Point(room.location);
        routeTo[routes] = room.roomCenter;
        
        routeTo[routes] = room.getDoorway();
        if (roomLayout[origin.x][origin.y]!=null) {
        routeFrom[routes] = roomLayout[origin.x][origin.y].getDoorway();
        } else {
            routeFrom[routes] = routeTo[routes];
        }
        
        System.out.println("routeFrom = "+routeFrom[routes].toString());
        System.out.println("routeTO = "+routeTo[routes].toString());
        System.out.println("Room loc = " + room.location.toString() + " vs center " + room.roomCenter.toString());
        routes++;
        for (int i = 0; i < size; i++) {
            roomLayout[reqX[i]][reqY[i]] = room;

        }
        this.roomQueue.enqueue(room);
        
        roomCount++;
        return true;

    }


    /**
     * Prints the room placement while storing the tiles into the char array.
     * Passages between the rooms are not yet carved out.
     */
    public void print() {
        System.out.println("printing...");
        for (int y = 0; y < 100; y++) {
            System.out.print("\n");
            for (int x = 0; x < 100; x++) {
                if (roomLayout[x / 10][y / 10] == null) {
                    System.out.print("..");
                    tiles[x][y] = '.';
                } else {
                    Room room = roomLayout[x / 10][y / 10];

                    System.out.print(room.print(x, y));
                    tiles[x][y] = room.print(x, y).charAt(1);

                }
            }
        }
        

    }

    /**
     * This method draws out the passages between the rooms AFTER all
     * rooms have been placed.
     * 
     */
    public void carveRoutes() {
        System.out.println("routes " + routes);
    
        // the for loop processes each pair of passage/route departures and destinations.
        for (int i = 0; i < routes; i++) {
            System.out.println("i = "+i+", routef "+routeFrom[i].toString());
            int startX = (routeFrom[i].x);// * 10) + 5;
            int startY = (routeFrom[i].y);// * 10) + 5;

            int endX = (routeTo[i].x);// * 10) + 5;
            int endY = (routeTo[i].y);// * 10) + 5;

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

                    
                    Room room = roomLayout[x / 10][y / 10];

                    if (room!=null && tiles[x][y]=='+' && x%10==4 && y%10==4) {
                        
                        if (roomCount==room.id) {
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
    
    /**
     * Returns two dimensional array of char tiles of the floor.
     * @return two dimensional array of char tiles of the floor.
     */
    public char[][] getTiles() {
        return this.tiles;
    }
    
    public void addRandomRoute(boolean connecting) {
       Random randomi = new Random();
       Room origin = null;
       
       
       while (origin==null)
       {
           Point point = new Point(randomi.nextInt(10), randomi.nextInt(10));
           origin = roomLayout[point.x][point.y];
       }
       routeFrom[routes]=origin.getDoorway();
       
       if (connecting) {
          
           origin=null;
           while (origin==null)
       {
           Point point = new Point(randomi.nextInt(10), randomi.nextInt(10));
           origin = roomLayout[point.x][point.y];
       }
        routeTo[routes]=origin.getDoorway();
       } else {
       
           while (true) {
       int toX = randomi.nextInt(50)-25+routeFrom[routes].x;
       int toY = randomi.nextInt(50)-25+routeFrom[routes].y;
       
       toX=Math.max(1, toX); toX=Math.min(toX, 98);
       toY=Math.max(1, toY); toY=Math.min(toY, 98);
       
       if (tiles[toX][toY]=='.') {
       routeTo[routes]= new Point(toX,toY);
           break;
       }
           }
       
       }
       routes++;
    }
       
    public RoomQueue getRoomQueue() {
        return this.roomQueue;
    }
}
