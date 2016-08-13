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
    public Room[][] roomLayout; // seeIfItFits layout is stored into a coarse 10 x 10 grid 
    public boolean[][] noRoom; // rooms can't be placed on these coarse grids
    
    private int xMax;
    private int yMax;
    private Point entry;
    private Point[] routeFrom;  // array for start points of each passages
    private Point[] routeTo; // array for end points of each passages
    private int routes;   // number of passages generated so far (hardcoded max: 200)
    private int roomCount;
    private RoomQueue roomQueue;
    private Specification spec;
    
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
        roomLayout = new Room[xMax / 10][yMax / 10];
        noRoom= new boolean[xMax / 10][yMax / 10];
        this.entry = pointOfEntry;
        this.routeFrom = new Point[200];
        this.routeTo = new Point[200];
        this.roomQueue = new RoomQueue();
        if (spec.roomDensity<100) {
            addSaltToGrid();
        }
    }

    public void addSaltToGrid() {
        Random randomi = new Random();
        int addedEmptySpaces=0;
        
        for (int y=0; y<spec.gridY; y++) {
            for (int x=0; x<spec.gridX; x++) {
                
                
                
                if (x>=entry.x-2 && x<=entry.x+2 && y>=entry.y-2 && y<=entry.y+2) {
                    // nothing
                } 
                else if (randomi.nextInt(5)==1) {
                      this.noRoom[x][y]=true;
                }
//                
//                else if (addedEmptySpaces>((spec.gridX*spec.gridY)-spec.roomDensity)) {
//                    // nothing      
//                }
//                else if (randomi.nextInt(Math.max(spec.roomDensity,20))<randomi.nextInt(200)) {
//                    this.noRoom[x][y]=true;
//                    addedEmptySpaces++;
//                }
            }
        }
    }
    
    /**
     * A method which tries to insert a seeIfItFits of specific size into specific
 location on the floor map. Returns TRUE if the placement was successful.
     *
     * @param rlx x coordinate for the new seeIfItFits location
     * @param rly y coordinate for the new seeIfItFits location
     * @param dimension dimensions of the new seeIfItFits (either 10x10, 20x10, 10x20,
 20x20 or 30x30)
     * @param fromDirection general direction of the previous (connecting) seeIfItFits
     * @param origin loxation of the previous (connecting) seeIfItFits
     * @param currentRoomID serial number which will be given to the new seeIfItFits
 (if placement is successful)
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
    
    public int getRoutes() {
        return this.routes;
    }
    
    public int getRoomCount() {
        return this.roomCount;
    }
    
    public Point[] getRouteFrom() {
        return this.routeFrom;
    }
    
    public Point[] getRouteTo() {
        return this.routeTo;
    }

    public void addRoomCount() {
        this.roomCount++;
    }
    
      public void addRouteCount() {
        this.routes++;
    }
    
}
