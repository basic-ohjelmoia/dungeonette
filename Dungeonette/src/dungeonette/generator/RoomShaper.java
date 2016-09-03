/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Room;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 *
 * This class gives the basic rectangular shape to a room.
 * 
 */
public class RoomShaper {
    
    /**
     * Generates the general shape of the room.
     *
     * The rooms are constructed as square-shaped. generateShape() deforms the
     * room into a rectangle.
     *
     * @param room newly constructed room being shaped
     */
    public static void generateShape(Room room) {
        Random randomi = room.getRandom();

        Point location = room.location;
        Point[] doorways = room.getDoorwayArray();
        Dimension dimension = room.dimension;
        char[][] shape = room.getShape();
        
        
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
                    room.addArea();
                    
                    if (Math.abs(x-hCenter)<=1 && Math.abs(y-vCenter)<=1) {
                        doorways[doorCount]=new Point(x+(location.x*10),y+(location.y*10));
                        doorCount++;
                    }
                }

            }
        }
        // 50 % of the rooms will be further deformed with a deCornerize() call.
        if (randomi.nextBoolean() || room.debugForceCornerRemoval) {
           deCornerize(room);
        } else {
            if (randomi.nextBoolean()) {
            doorways[9]=new Point(hMargin+2+(location.x*10), vMargin+2+(location.y*10));
            } else {
                doorways[9]=new Point(10-hMargin2-2+(location.x*10), 10-vMargin2-2+(location.y*10));
            }
        }
        if (randomi.nextBoolean()) {
            RoomDecorator.decorate(room);
        }
    }
    
    
    
    /**
     * This method deforms the room by removing as many as four corners from the
     * original shape.
     *
     * The room might also be deformed by adding a solid center ("doughnut
     * shaped room").
     *
     * Some rooms might survive the deCornerize() completely unchanged!
     * @param room room being decornerized
     */
    public static void deCornerize(Room room) {
        Random randomi = room.getRandom();

        int area = room.getArea();
            Point location = room.location;
        Point[] doorways = room.getDoorwayArray();
        Dimension dimension = room.dimension;
        char[][] shape = room.getShape();
        
        
        // These booleans refer to the corners being removed. If "nw" is true, then the north-west corner will get cut.
        boolean nw = randomi.nextBoolean();  if (room.debugForceCornerRemoval) {nw=true;}
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
       
            if (cutCorners == 1 && (dimension.width > 10 || dimension.height > 10)) {// && area>=81) {
                cornerDivider = 2;
                mod=1;
               
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
        if (randomi.nextBoolean()) {
            RoomDecorator.decorate(room);
        }
    }

    
}
