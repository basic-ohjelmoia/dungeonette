/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.data;

import dungeonette.domain.Room;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mikromafia
 */
public class RoomNodeTest {
    
   public Room r1; 
   public Room r2;
   public Room r3;
    
    public RoomNodeTest() {
        Random randomi = new Random(10);
        
         r1 = new Room(new Point(0, 0), new Dimension(10, 10), 1, 'n',randomi);
         r2 = new Room(new Point(0, 0), new Dimension(10, 10), 2, 'n',randomi);
         r3 = new Room(new Point(0, 0), new Dimension(10, 10), 3, 'n',randomi);

        r1.removeAllPivots();
        r2.removeAllPivots();
        r3.removeAllPivots();
        
    }

    /**
     * Test of main methods, of class RoomNode.
     */
    @Test
    public void testMainMethods() {
        RoomNode rn1 = new RoomNode(r1, null);
        RoomNode rn2 = new RoomNode(r2, rn1);
        rn1.setNext(rn2);
        assertTrue(rn1.getNext().getRoom().id==2);
        assertTrue(rn1.isRoot());
        assertTrue(!rn2.isRoot());
        assertTrue(rn2.getPrevious().getRoom().id==1);
        assertTrue(rn1.getPrevious()==null);
        assertTrue(rn2.getPrevious().getPrevious()==null);
    }

    
}
