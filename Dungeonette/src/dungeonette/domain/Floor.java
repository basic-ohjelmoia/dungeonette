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

    public char[][] debugTiles;
    private char[][] tiles; // all coordinates of the floor map
    private char[][] items;
    private int[][] tileIDs; // room id number of each tile (0 = un-used space)
    private char[][] doorTiles;
    
    public Room[][] roomLayout; // seeIfItFits layout is stored into a coarse 10 x 10 grid 
    public boolean[][] noRoom; // rooms can't be placed on these coarse grids
    public boolean[] connected;
    public boolean[] isCrossyPassage;
    
    private int xMax;
    private int yMax;
    private Point pointOfEntry;    // The starting point of the floor
    public Point pointOfExit;
    private Point[] routeFrom;  // array for start points of each passages
    private Point[] routeTo; // array for end points of each passages
    private int[] routeIDFrom; // room id number that relates to the originating room of the route)
    private int[] routeIDTo; // room id number that relates to the destination room of the route)
    private int routes;   // number of passages generated so far (hardcoded max: 200)
    private int roomCount;
    private RoomQueue roomQueue;    // queue for active rooms looking for neighbours
    private Specification spec;     // object for dungeon's specifications
    
    private final int maxNumberOfPassages = 200;
    private int plusThese;
    
    /**
     * Constructor for the floor. Note that the object is constructed WITHOUT the pointOfEntry object.
     *
     * @param spec Specification object of the dungeon
     */
    public Floor(Specification spec) {
        this.spec=spec;
        this.plusThese=spec.volatility+spec.density;
        xMax=spec.maxX;
        yMax=spec.maxY;
        tiles = new char[xMax][yMax];
        items = new char[xMax][yMax];
        debugTiles = new char[xMax][yMax];
        doorTiles = new char[xMax][yMax];
        tileIDs = new int[xMax][yMax];
        roomLayout = new Room[xMax / 10][yMax / 10];
        noRoom= new boolean[xMax / 10][yMax / 10];
        this.routeFrom = new Point[maxNumberOfPassages+plusThese];
        this.routeTo = new Point[maxNumberOfPassages+plusThese];
        this.routeIDFrom = new int[maxNumberOfPassages+plusThese];
        this.routeIDTo = new int[maxNumberOfPassages+plusThese];
        this.roomQueue = new RoomQueue();
        this.connected= new boolean[maxNumberOfPassages+plusThese];
        this.isCrossyPassage= new boolean[maxNumberOfPassages+plusThese];
        this.connected[1]=true;
    }

    
    /**
     * "Salt" refers to coarse grid locations where no rooms can be placed.
     * The density of salt has a huge impact on the dungeon's looks.
     * Passageways ARE legal on salted grids.
     * 
     */
    public void addSaltToGrid() {
        Random randomi = spec.randomi;
        
        
        for (int y=0; y<spec.gridY; y++) {
            for (int x=0; x<spec.gridX; x++) {
                
                
                
                if (x>=pointOfEntry.x-2 && x<=pointOfEntry.x+2 && y>=pointOfEntry.y-2 && y<=pointOfEntry.y+2) {
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
     * Removes all salts from the floor. 
     * Used in debugging, might have further uses as well.
     */
    public void removeSalts() {
                      this.noRoom= new boolean [spec.gridX][spec.gridY];
    }


    /**
     * Prints the room placement while storing the tiles into the char array.
     * Passages between the rooms are not yet carved out.
     */
    public void storeRoomsIntoTiles() {
        System.out.println("printing...");
        for (int y = 0; y < spec.maxY; y++) {
            System.out.print("\n");
            for (int x = 0; x < spec.maxX; x++) {
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
                    tiles[x][y] = room.getTile(x, y,true);//.print(x, y).charAt(1);
                    if (tiles[x][y]!='.') {
                        tileIDs[x][y]=room.id;
                    }
                    items[x][y] = room.getTile(x, y, false);

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
     * Returns two dimensional array of char items of the floor.
     * @return two dimensional array of char items of the floor.
     */
    public char[][] getItems() {
        return this.items;
    }
    
    /**
     * Adds a random passageway to the dungeon.
     * The passageway always is connected FROM an existing room.
     * If the connecting parameter is FALSE, then the passageway is generated as a deadend.
     * @param connecting true if the route connects to another room, false if the route is a deadend
     */
    public void addRandomRoute(boolean connecting) {
       Random randomi = spec.randomi;
       Room origin = null;
       
       routeIDTo[routes]=0;
       routeIDFrom[routes]=0;
       
       while (origin==null)
       {
           Point point = new Point(randomi.nextInt(spec.gridX), randomi.nextInt(spec.gridY));
           origin = roomLayout[point.x][point.y];
       }
       routeFrom[routes]=origin.getDoorway();
       
       if (connecting) {
          
           origin=null;
           while (origin==null)
       {
           Point point = new Point(randomi.nextInt(spec.gridX), randomi.nextInt(spec.gridY));
           origin = roomLayout[point.x][point.y];
       }
        routeTo[routes]=origin.getDoorway();
       } else {
       
           while (true) {
       int toX = randomi.nextInt(50)-25+routeFrom[routes].x;
       int toY = randomi.nextInt(50)-25+routeFrom[routes].y;
       
       toX=Math.max(1, toX); toX=Math.min(toX, spec.maxX-2);
       toY=Math.max(1, toY); toY=Math.min(toY, spec.maxY-2);
       
       if (tiles[toX][toY]=='.') {
       routeTo[routes]= new Point(toX,toY);
           break;
       }
           }
       
       }
       routes++;
    }
       
    /**
     * Calling this method adds a crossing passageway to the specified location.
     * The crossing passageway should result as a dead-end (terminate before breaching any room outer boundary walls).
     * Crossy passage tiles are marked as ¤+ in the dungeon printouts.
     * @param start starting coordinates of the crossed passageway
     * @param dir direction of the MAIN passageway (not the crossing passageway!!!!!)
     */
    public void addCrossyPassage(Point start, char dir) {
        int targetX=start.x;
        int targetX2=start.x;
        int targetY=start.y;
        int targetY2=start.y;
        
        Random randomi = spec.randomi;
        int oneWay=randomi.nextInt(6);
        int other=randomi.nextInt(6);
        
        if (dir=='n' || dir=='s') {
            targetX=Math.max(1, targetX-oneWay);
            targetX2=Math.min(spec.maxX-3, targetX2+other);
        } else {
            targetY=Math.max(1, targetY-oneWay);
            targetY2=Math.min(spec.maxY-3, targetY2+other);
        }
        routeFrom[routes]=new Point(start);
        routeTo[routes]=new Point(new Point(targetX, targetY));
        isCrossyPassage[routes]=true;
//        routeIDTo[routes]=-11111;
//        routeIDFrom[routes]=-11111;
        
        routes++;
        
        routeFrom[routes]=new Point(start);
        routeTo[routes]=new Point(new Point(targetX2, targetY2));
        isCrossyPassage[routes]=true;
//        routeIDTo[routes]=-11111;
//        routeIDFrom[routes]=-11111;
        
        routes++;
        System.out.println("crossy road created!");
    }
    
    /**
     * Method for calling for the queue of active rooms
     * @return returns the queue
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
      
      /**
       * Sets the point of entry (the first room) of the floor.
       * After the point is set, the salt is added.
       * @param pt grid coordinate of the first room of the floor
       */
      public void setPointOfEntry(Point pt) {
         this.pointOfEntry=pt;
         if (spec.roomDensity<20) {  // density 20 = no salt added!
            addSaltToGrid();
        }
      }
}
