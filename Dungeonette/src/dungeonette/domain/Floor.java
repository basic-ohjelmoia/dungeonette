/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import dungeonette.data.RoomQueue;
import dungeonette.generator.RoomInserter;
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
    private int[][] tileIDs; // room id number of each tile (0 = un-used space)
    private char[][] doorTiles;
    
    public Room[][] roomLayout; // seeIfItFits layout is stored into a coarse 10 x 10 grid 
    public boolean[][] noRoom; // rooms can't be placed on these coarse grids
    public boolean[] connected;
    
    private int xMax;
    private int yMax;
    private Point entry;    // The starting point of the floor
    private Point[] routeFrom;  // array for start points of each passages
    private Point[] routeTo; // array for end points of each passages
    private int[] routeIDFrom; // room id number that relates to the originating room of the route)
    private int[] routeIDTo; // room id number that relates to the destination room of the route)
    private int routes;   // number of passages generated so far (hardcoded max: 200)
    private int roomCount;
    private RoomQueue roomQueue;    // queue for active rooms looking for neighbours
    private Specification spec;     // object for dungeon's specifications
    
    /**
     * Constructor for the floor.
     *
     * @param spec Specification object of the dungeon
     * @param pointOfEntry The 10 by 10 grid location of the first seeIfItFits being
 generated.
     */
    public Floor(Specification spec, Point pointOfEntry) {
        this.spec=spec;
        xMax=spec.maxX;
        yMax=spec.maxY;
        tiles = new char[xMax][yMax];
        doorTiles = new char[xMax][yMax];
        tileIDs = new int[xMax][yMax];
        roomLayout = new Room[xMax / 10][yMax / 10];
        noRoom= new boolean[xMax / 10][yMax / 10];
        this.entry = pointOfEntry;
        this.routeFrom = new Point[200];
        this.routeTo = new Point[200];
        this.routeIDFrom = new int[200];
        this.routeIDTo = new int[200];
        this.roomQueue = new RoomQueue();
        this.connected= new boolean[200];
        this.connected[1]=true;
        if (spec.roomDensity<10) {
            addSaltToGrid();
        }
    }

    
    /**
     * "Salt" refers to coarse grid locations where no rooms can be placed.
     * The density of salt has a huge impact on the dungeon's looks.
     * Passageways ARE legal on salted grids.
     * 
     */
    public void addSaltToGrid() {
        Random randomi = new Random();
        
        
        for (int y=0; y<spec.gridY; y++) {
            for (int x=0; x<spec.gridX; x++) {
                
                
                
                if (x>=entry.x-2 && x<=entry.x+2 && y>=entry.y-2 && y<=entry.y+2) {
                    // nothing --- secures the space around the floor entrance 
                } 
                else if (randomi.nextInt(spec.roomDensity)==1) {
                      this.noRoom[x][y]=true;
                      x++;
                }

            }
        }
    }
    
    /**
     * A method which tries to insert a seeIfItFits of specific size into specific
 location on the floor map. Returns TRUE if the placement was successful.
     *
     * @param rlx x coordinate for the new seeIfItFits location
     * @param rly y coordinate for the new seeIfItFits location
     * @param dimension dimensions of the new seeIfItFits (either 10x10, 20x10, 10x20, 20x20 or 30x30)
     * @param fromDirection general direction of the previous (connecting) seeIfItFits
     * @param origin loxation of the previous (connecting) seeIfItFits
     * @param currentRoomID serial number which will be given to the new seeIfItFits (if placement is successful)
     * @return true if the placement was successful
     */
    public boolean insertRoom(int rlx, int rly, Dimension dimension, char fromDirection, Point origin, int currentRoomID) {

        if (rlx<0 || rly<0 || rlx>=xMax/10 || rly>=yMax/10 ) {
            return false;
        }
        if (noRoom[rlx][rly]) {
            return false;
        }
        return RoomInserter.seeIfItFits(this, rlx, rly, dimension, fromDirection, origin, currentRoomID);

    }


    /**
     * Prints the seeIfItFits placement while storing the tiles into the char array.
     * Passages between the rooms are not yet carved out.
     */
    public void print() {
        System.out.println("printing...");
        for (int y = 0; y < 100; y++) {
            System.out.print("\n");
            for (int x = 0; x < 100; x++) {
                if (roomLayout[x / 10][y / 10] == null) {
                    
                    tiles[x][y] = '.';
                    if (noRoom[x/10][y/10]) {
                        System.out.print("s.");
                    } else {
                        System.out.print("..");
                    }
                } else {
                    Room room = roomLayout[x / 10][y / 10];

                    System.out.print(room.print(x, y));
                    tiles[x][y] = room.print(x, y).charAt(1);
                    if (tiles[x][y]!='.') {
                        tileIDs[x][y]=room.id;
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
    
    /**
     * Adds a random passageway to the dungeon.
     * The passageway always is connected FROM an existing room.
     * If the connecting parameter is FALSE, then the passageway is generated as a deadend.
     * @param connecting true if the route connects to another room, false if the route is a deadend
     */
    public void addRandomRoute(boolean connecting) {
       Random randomi = new Random();
       Room origin = null;
       
       routeIDTo[routes]=0;
       routeIDFrom[routes]=0;
       
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
       
    /**
     * Method for calling for the queue of active rooms
     * @return 
     */
    public RoomQueue getRoomQueue() {
        return this.roomQueue;
    }
    
    /**
     * Number of routes stored for the floor
     * @return number of routes
     */
    public int getRoutes() {
        return this.routes;
    }
    
    /**
     * number of rooms on the floor
     * @return number of rooms
     */
    public int getRoomCount() {
        return this.roomCount;
    }
    
    /**
     * returns an array of points referring to origin points of passageways
     * @return array of points
     */
    public Point[] getRouteFrom() {
        return this.routeFrom;
    }
    
    /**
     * returns an array of points referring to destination points of passageways
     * @return array of points
     */
    public Point[] getRouteTo() {
        return this.routeTo;
    }

    /**
     * increments the room counter
     */
    public void addRoomCount() {
        this.roomCount++;
    }
    
    /**
     * increments the route counter
     */
      public void addRouteCount() {
        this.routes++;
    }
      
      /**
       * If the tile is identifiable to specific room, this matrix stores it's number.
       * Zeroes refer to tiles which DON'T belong to rooms.
       * @return matrix of integer id's
       */
      public int[][] getTileIDs() {
          return this.tileIDs;
      }
      
      /**
       * Routes are identified by origin room and destination room id numbers
       * This method returns an array id numbers of the room from which the psassageway originates from.
       * @return id numbers
       */
      public int[] getRouteIDFrom() {
          return this.routeIDFrom;
      }
      
      /**
       * Routes are identified by origin room and destination room id numbers
       * This method returns an array id numbers of the room for which the psassageway destines to.
       * @return id numbers
       */
      public int[] getRouteIDTo() {
          return this.routeIDTo;
      }
    
      /**
       * A placeholderish method which returns a char array containg door locations
       * @return char array of door locations
       */
      public char[][] getDoorTiles() {
          return this.doorTiles;
      }
}
