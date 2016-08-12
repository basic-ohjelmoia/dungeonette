/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

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

    public Dimension dimension;
    public int id;
    public char fromDirection;
    public char outDirection;
    public Point location;
    public Point roomCenter;
    private boolean horizontalPassage;
    private boolean verticalPassage;
    private char[][] shape;
    private int area;
    private boolean debugForceCornerRemoval;
    private Point[] doorways;
    private int pivots;

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
     */
    public Room(Point location, Dimension dimension, int newID, char from) {
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

        if (from=='x') {
            debugForceCornerRemoval=true;
        }
        
        if (id%2==0 && dimension.height>=20 || dimension.height>=20) {
         RoomStrangifier.reshape(this);
        } else {
        generateShape();
        }
    }

    /**
     * Generates the general shape of the room.
     *
     * The rooms are constructed as square-shaped. generateShape() deforms the
     * room into a rectangle.
     *
     */
    public void generateShape() {
        Random randomi = new Random();

        // h(orizontal)Margins and v(ertical)Margins set the new boundaries for the room's outer walls.
        int hMargin = randomi.nextInt(3 + (dimension.width / 8));
        int hMargin2 = randomi.nextInt(3 + (dimension.width / 8)) + 1;
        int vMargin = randomi.nextInt(3 + (dimension.height / 8));
        int vMargin2 = randomi.nextInt(3 + (dimension.height / 8)) + 1;
        
        
        // Some rooms should ignore the prior vertical and horizontal margin deformations and stay as perfect (full size) squares.
        if (randomi.nextInt(15) > 4) {
            hMargin = 0;
            vMargin = 0;
            hMargin2 = 1;
            vMargin2 = 1;
        }

        int hCenter=dimension.width/2 - hMargin2+hMargin;
        int vCenter=dimension.height/2 - vMargin2+vMargin;
        System.out.println("v cent "+hCenter+","+vCenter);
        int doorCount=0;
        
        // This for-loop generates the actual room shape. 
        // It's important to note that unused space needs to be marked as solid ground ('.' tiles).
        // Map key:
        // #    ... Outer wall tile
        // +    ... Floor tile 
        // .    ... Unused space (solid ground)
        for (int y = 0; y < dimension.height; y++) {
            for (int x = 0; x < dimension.width; x++) {
                if (x < hMargin || x > dimension.width - hMargin2
                        || y < vMargin || y > dimension.height - vMargin2) {
                    shape[x][y] = '.';
                } else if (x == hMargin || x == dimension.width - hMargin2
                        || y == vMargin || y == dimension.height - vMargin2) {
                    shape[x][y] = '#';

                } else {
                    shape[x][y] = '+';
                    area++;
                    if (Math.abs(x-hCenter)<=1 && Math.abs(y-vCenter)<=1) {
                        doorways[doorCount]=new Point(x+(location.x*10),y+(location.y*10));
                        doorCount++;
                    }
                }

            }
        }
        // 50 % of the rooms will be further deformed with a deCornerize() call.
        if (randomi.nextBoolean() || debugForceCornerRemoval) {
            deCornerize();
        } else {
            if (randomi.nextBoolean()) {
            doorways[9]=new Point(hMargin+2+(location.x*10), vMargin+2+(location.y*10));
            } else {
                doorways[9]=new Point(10-hMargin2-2+(location.x*10), 10-vMargin2-2+(location.y*10));
            }
        }
        this.pivots = Math.max(1, area/75);
        this.pivots = Math.min(this.pivots, 4);
//        if (this.id % 7==0) {
//            this.pivots=1;
//        }
//   
//        if (this.id % 19==0) {
//            this.pivots=2;
//        }
        if (this.id<15) {
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
     * This method deforms the room by removing as many as four corners from the
     * original shape.
     *
     * The room might also be deformed by adding a solid center ("doughnut
     * shaped room").
     *
     * Some rooms might survive the deCornerize() completely unchanged!
     */
    public void deCornerize() {
        Random randomi = new Random();

        // These booleans refer to the corners being removed. If "nw" is true, then the north-west corner will get cut.
        boolean nw = randomi.nextBoolean();  if (debugForceCornerRemoval) {nw=true;}
        boolean ne = randomi.nextBoolean();
        boolean sw = randomi.nextBoolean();
        boolean se = randomi.nextBoolean();

        int cutCorners = 0;
        if (nw) {
            cutCorners++;
        }
        if (ne) {
            cutCorners++;
        }
        if (sw) {
            cutCorners++;
        }
        if (ne) {
            cutCorners++;
        }

        // The center boolean refers to the addition of the solid room center. 
        // There probably should be better safeguards against adding too large of a center into a too small of a room...
        boolean center = false;
        if (randomi.nextInt(4) == 1 && area >= 75) {// && !nw&&!ne&&!sw&&!se) {
            center = true;
        }

        // These dividers refer to the severity of the floor-space being removed. The smaller the divider is, the larger the cut.
        int cornerDivider = 4;
        int centerDivider = 4;
        if (cutCorners > 0) {
            centerDivider = 3;

            // Addition of a solid center will be cancelled, if a small-ish room is already having all 4 corners removed.
            if (ne && nw && sw && se && area < 100) {
                center = false;
            }
        }
        int mod=0;
        
        if (!center && area >64) {
            cornerDivider = 3;
            System.out.println("cut corners: " + cutCorners + ", dims: " + dimension.width + ", " + dimension.height);
            if (cutCorners == 1 && (dimension.width > 10 || dimension.height > 10)) {// && area>=81) {
                cornerDivider = 2;
                mod=1;
                System.out.println("cordir2");
            }
        }

        // These ints mark the coordinate boundaries for the corners/center being removed from the room.
        int cornerClipX = (dimension.width / cornerDivider) -mod;
        int cornerClipX2 = dimension.width - (dimension.width / cornerDivider) + mod;
        int cornerClipY = (dimension.height / cornerDivider) -mod;
        int cornerClipY2 = dimension.height - (dimension.height / cornerDivider) + mod;

        int centerClipX = dimension.width / centerDivider;
        int centerClipX2 = dimension.width - (dimension.width / centerDivider);
        int centerClipY = dimension.height / centerDivider;
        int centerClipY2 = dimension.height - (dimension.height / centerDivider);

        // This for-loop handles the actual deformation of the room.
        // Map key:
        // #    ... Outer wall tile
        // +    ... Floor tile 
        // .    ... Unused space (solid ground)
        for (int y = 0; y < dimension.height; y++) {
            for (int x = 0; x < dimension.width; x++) {

                if (center) {
                    if (x > centerClipX + 1 && x < centerClipX2 - 1 && y > centerClipY + 1 && y < centerClipY2 - 1) {
                        shape[x][y] = '.';
                    }
                    if ((x == centerClipX + 1 || x == centerClipX2 - 1) && y >= centerClipY + 1 && y <= centerClipY2 - 1 && shape[x][y] != '.') {
                        shape[x][y] = '#';
                    }
                    if ((y == centerClipY + 1 || y == centerClipY2 - 1) && x >= centerClipX + 1 && x <= centerClipX2 - 1 && shape[x][y] != '.') {
                        shape[x][y] = '#';
                    }

                }

                if (nw && x < cornerClipX && y < cornerClipY) {
                    shape[x][y] = '.';
                }
                if (nw && x == cornerClipX && y < cornerClipY && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }
                if (nw && y == cornerClipY && x < cornerClipX && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }
                if (nw && y == cornerClipY && x == cornerClipX && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }

                if (ne && x > cornerClipX2 && y < cornerClipY) {
                    shape[x][y] = '.';
                }
                if (ne && x == cornerClipX2 && y < cornerClipY && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }
                if (ne && y == cornerClipY && x > cornerClipX2 && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }
                if (ne && y == cornerClipY && x == cornerClipX2 && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }

                if (sw && x < cornerClipX && y > cornerClipY2) {
                    shape[x][y] = '.';
                }
                if (sw && x == cornerClipX && y > cornerClipY2 && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }
                if (sw && y == cornerClipY2 && x < cornerClipX && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }
                if (sw && y == cornerClipY2 && x == cornerClipX && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }

                if (se && x > cornerClipX2 && y > cornerClipY2) {
                    shape[x][y] = '.';
                }
                if (se && x == cornerClipX2 && y > cornerClipY2 && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }
                if (se && y == cornerClipY2 && x > cornerClipX2 && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }
                if (se && y == cornerClipY2 && x == cornerClipX2 && shape[x][y] != '.') {
                    shape[x][y] = '#';
                }

                if (shape[x][y]=='+' && doorways[9]==null) {
                    doorways[9]=new Point(x+(location.x*10),y+(location.y*10));
                }
                
            }
        }
    }

    
    
    /**
     * Fetches a tile represented as a char from as specific coordinate of the room. This coordinate matches the exact coordinates of a dungeon floor.
     * While the room is situated on a coarse 10x10 grid (or multiple grids), the actual room tiles have fine 1x1 coordinates.
     *  
     * @param x Fine x coordinate on the dungeon floor.
     * @param y Fine y coordinate on the dungeon floor.
     * @return Returns the char of the tile
     */
    public char getTile(int x, int y) {
        
        
        x -= location.x * 10;
        y -= location.y * 10;

        if (x<0 || y<0 || x>=dimension.width || y>=dimension.height) {
            return '?';
        }
        
        return shape[x][y];
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
        char c = getTile(x, y);
        
        return ""+c+""+c;
    }
    
    public Point[] getDoorwayArray() {
        return this.doorways;
    }
    
    public Point getDoorway() {
        long timer = System.currentTimeMillis();
        int dice = (int)(timer%10);
        System.out.println("dice: "+dice);
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
     * @return 
     */
    
    public char[][] getShape() {
        return this.shape;
    }

    /**
     * Returns the number of floor tiles in the room.
     * This number is counted prior the decornerization.
     * @return number of floor tiles (before decornerization)
     */
    public int getArea() {
        return this.area;
    }
    
    public void resetArea() {
        this.area=0;
    }
    
    public void addArea() {
        this.area++;
    }
    
    public boolean hasPivots() {
        if (this.pivots<1) {return false;}
            
        this.pivots--;
        return true;
    }
}
