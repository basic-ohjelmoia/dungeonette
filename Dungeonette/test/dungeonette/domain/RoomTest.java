/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
  */
public class RoomTest {
    
    public Room room;
    
    public RoomTest() {
        this.room = new Room(new Point(5,5), new Dimension(10,10), 1, 'n', new Random());
    }

    /**
     * Test of generateShape method, of class Room.
     */
    @Test
    public void testGenerateShape() {
        
        for (int i = 0; i<100; i++) {
            
        int floorCount=0;
        int wallCount=0;
        int solidCount=0;
        boolean nullFound=false;
        
        int tileTotal=room.dimension.width*room.dimension.height;
        
        for (int y=0; y<room.dimension.height; y++) {
            for (int x=0; x<room.dimension.width; x++) {
                if (room.getShape()[x][y]==0) {
                    nullFound=true;
                }
                if (room.getShape()[x][y]=='.') {
                    solidCount++;
                }
                if (room.getShape()[x][y]=='+') {
                    floorCount++;
                }
                if (room.getShape()[x][y]=='#') {
                    wallCount++;
                }
                
            }
        }
        
        assertTrue(floorCount>0);
        assertTrue(floorCount<tileTotal);
        
        assertTrue(wallCount>0);
        assertTrue(wallCount<tileTotal);
        
        assertTrue(solidCount>=0);
        assertTrue(solidCount<tileTotal);
        
         assertTrue(solidCount+floorCount+wallCount==tileTotal);
        
         assertFalse(nullFound);
         
        }
    }

    /**
     * Test of deCornerize method, of class Room.
     */
    @Test
    public void testDeCornerize() {
        
        System.out.println("== RUNNING TEST: testDeCornerize == ");
           for (int i = 0; i<100; i++) {
        
        Room cRoom = new Room(new Point(5,5), new Dimension(20,20), 1, 'x', new Random());  // 'x' designates a forced corner removal
        
        int floorCount=0;
                
        for (int y=0; y<cRoom.dimension.height; y++) {
            for (int x=0; x<cRoom.dimension.width; x++) {
 
                if (cRoom.getShape()[x][y]=='+') {
                    floorCount++;
                }
                
            }
        }
               System.out.println("Iteration #"+i+", floor tile count prior to decornerization: "+floorCount+"; floor tile count after: "+cRoom.getArea());
        assertTrue(floorCount<cRoom.getArea());
           }
    }

    /**
     * Test of print method, of class Room.
     */
    @Test
    public void testPrint() {
        
        Point originalLocation = this.room.location;
        
        
        assertTrue(this.room.print((originalLocation.x*10)+5, (originalLocation.y*10)+5).charAt(1)=='+');
        
        assertTrue(this.room.dimension.width*this.room.dimension.height==100);
        assertTrue(this.room.print((originalLocation.x*10), (originalLocation.y*10)).charAt(1)!='?');
        assertTrue(this.room.print((originalLocation.x*10)-1, (originalLocation.y*10)-1).charAt(1)=='?');
        assertTrue(this.room.print((originalLocation.x*10)+9, (originalLocation.y*10)+9).charAt(1)!='?');
        assertTrue(this.room.print((originalLocation.x*10)+10, (originalLocation.y*10)+10).charAt(1)=='?');
        
        assertTrue(this.room.print(Integer.MAX_VALUE,Integer.MIN_VALUE).charAt(1)=='?');
        
    }
    
}
