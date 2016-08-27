/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import dungeonette.generator.RoomDecorator;
import dungeonette.generator.RoomShaper;
import dungeonette.generator.RoomStrangifier;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 * Object class for the rooms placed into the dungeon.
 *
 * The class contains a lot of program logic and lacks proper setters and
 * getters.
 *
 * @author tuomas honkala
 */
public class Room {

    private Random randomi;
    public Dimension dimension;
    public int id;
    public char fromDirection;
    public char outDirection;
    public Point location;
    public Point roomCenter;
    private boolean horizontalPassage;
    private boolean verticalPassage;
    private char[][] shape;
    private char[][] items;
    private int area;
    public boolean debugForceCornerRemoval;
    private Point[] doorways;
    private int pivots;
    public boolean isConnectedToFloorStart;

    /**
     * Constructor for a room in a dungeon floor.
     *
     * @param location Coarse 10x10 location of the room
     * @param dimension Fine 1x1 dimensions of the room (legal dimensions:
     * 10x10,10x20,20x10,20x20 and 30x30). Each unit represents one tile (map
     * coordinate)
     * @param newID Serial number of the room (unclear wether serial numbers
     * should be reset for each floor or keep persistently incrementing
     * throughout the entire multi-floor dungeon)
     * @param from Direction (n-s-w-e) of the rooms previous connecting room
     * ("parent room")
     * @param randomi Random object received from Specification object
     */
    public Room(Point location, Dimension dimension, int newID, char from, Random randomi) {
        this.randomi=randomi;
        if (!(dimension.width==10 || dimension.width==20 || dimension.width==30)) {
            dimension.width=10;
        }
        if (!(dimension.height==10 || dimension.height==20 || dimension.height==30)) {
            dimension.height=10;
        }
        
        this.dimension = dimension;
        this.location = location;
        this.doorways = new Point[10];
        this.id = newID;
        this.fromDirection = from;
        this.shape = new char[dimension.width][dimension.height];
        this.items = new char[dimension.width][dimension.height];

        if (from=='x') {
            debugForceCornerRemoval=true;
        }
        initialize();
    }
    
    private void initialize() {
        
        if (id%2==0 && (dimension.height>=20 || dimension.height>=20)) {
             RoomStrangifier.reshape(this);
        } else {
            RoomShaper.generateShape(this);
        }
        
        
        generatePivots();
        
        
//        System.out.println("print out room "+id);
//        for (int y=0; y<dimension.height; y++) {
//            System.out.print("\n");
//            for (int x=0; x<dimension.width; x++) {
//                System.out.print(this.shape[x][y]);
//            } 
//        }
        
        if (id==1) {
            isConnectedToFloorStart=true;
        }
    }



    
    
    /**
     * Fetches a tile represented as a char from as specific coordinate of the room. This coordinate matches the exact coordinates of a dungeon floor.
     * While the room is situated on a coarse 10x10 grid (or multiple grids), the actual room tiles have fine 1x1 coordinates.
     *  
     * @param x Fine x coordinate on the dungeon floor.
     * @param y Fine y coordinate on the dungeon floor.
     * @param trueForTileFalseForItem set true if you want to fetch the actual TILE, false if you want to fetch the item
     * @return Returns the char of the tile
     */
    public char getTile(int x, int y, boolean trueForTileFalseForItem) {
        
        
        x -= location.x * 10;
        y -= location.y * 10;

        if (x<0 || y<0 || x>=dimension.width || y>=dimension.height) {
            return '?';
        }
        if (trueForTileFalseForItem) {
        return shape[x][y];
        }
        return items[x][y];
    }
    
     /**
     * Prints out a specific tile (coordinate) of a room in relation to the exact coordinates of a dungeon floor.
     * While the room is situated on a coarse 10x10 grid (or multiple grids), the actual room tiles have fine 1x1 coordinates.
     *  
     * @param x Fine x coordinate on the dungeon floor.
     * @param y Fine y coordinate on the dungeon floor.
     * @return Returns the tile, with the char doubled for better console visualization.
     */
    public String print(int x, int y) {
        char c = getTile(x, y,true);
        
        return ""+c+""+c;
    }
    
    /**
     * Returns the array of possible passageway exit points ("doorways", though not necessarily doors) 
     * @return array of points
     */
    public Point[] getDoorwayArray() {
        return this.doorways;
    }
    
    public Point getDoorway() {
        //long timer = System.currentTimeMillis();
        //int dice = (int)(timer%10);
        //System.out.println("dice: "+dice);
        int dice = randomi.nextInt(10);
        if (doorways[dice]!=null) {
        return doorways[dice];
        }
        else {
            for (int i =9;i>0;i--) {
                if (doorways[i]!=null) {
                    return doorways[i];
                }
            }
            
        }
        return new Point((location.x*10)+5,(location.y*10)+5);
    }
    
    /**
     * Return the two dimensional char array containing the room tiles.
     * @return returns the array of chars representing tiles
     */
    
    public char[][] getShape() {
        return this.shape;
    }
    
     /**
     * Return the two dimensional char array containing the room items.
     * @return returns the array of chars representing items.
     */
    
    public char[][] getItems() {
        return this.items;
    }


    /**
     * Returns the number of floor tiles in the room.
     * This number is counted prior the decornerization.
     * @return number of floor tiles (before decornerization)
     */
    public int getArea() {
        return this.area;
    }
    
    /**
     * Resets the counter for floor tiles of the room
     */
    public void resetArea() {
        this.area=0;
    }
    
    /**
     * Increments the floor tile counter by one
     */
    public void addArea() {
        this.area++;
    }
    
    /**
     * If the room still has accessible pivots (points of outbound passageways) this method returns TRUE-
     * NOTE: Each time this method is called, the number of pivots is decremented by one.
     * @return true if the room has atleast 1 pivot left.
     */
    public boolean hasPivots() {
        if (this.pivots<1) {return false;}
            
        this.pivots--;
        return true;
    }
    
    /**
     * Generates the starting pivots for the room.
     * Each room should have atleast 1 pivot.
     * A large room should have several.
     * 
     * The pivot count has an YUUUUUGE impact on the type of floor being generated. 
     * Lot of pivots per room results a very interconnected "messy" dungeon.
     * Limited number of pivots results a sparse dungeon. If the pivot number is TOO LOW then the algorithm might actually "break" and create a very tiny dungeon.
     */
    public void generatePivots() {
            this.pivots = Math.max(1, area/75);
        this.pivots = Math.min(this.pivots, 3);

        if (this.id<10) {
            this.pivots++;
        }
        if (this.id<5) {
            this.pivots++;
        }
        if (this.id%10==0) {
            this.pivots++;
        }
        
        System.out.println("Room "+this.id+" generated with "+this.pivots+" pivots");

    }
    
    /**
     * removes all pivots
     */
    public void removeAllPivots() {
        this.pivots=0;
    }
    
    /**
     * Returns the common random object used throughout the dungeon
     * @return random object
     */
    public Random getRandom() {
        return this.randomi;
    }
    
    /**
     * Sets the pivot count manually
     * @param pivots number of pivots
     */
    public void setPivots(int pivots) {
        this.pivots=pivots;
    }
}
