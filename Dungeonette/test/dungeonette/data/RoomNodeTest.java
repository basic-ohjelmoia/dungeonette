/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.data;

import dungeonette.domain.Room;
import java.awt.Dimension;
import java.awt.Point;
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
        
         r1 = new Room(new Point(0, 0), new Dimension(10, 10), 1, 'n');
         r2 = new Room(new Point(0, 0), new Dimension(10, 10), 2, 'n');
         r3 = new Room(new Point(0, 0), new Dimension(10, 10), 3, 'n');

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
        assertTrue(rn1.isParent());
        assertTrue(!rn2.isParent());
        assertTrue(rn2.getPrevious().getRoom().id==1);
        assertTrue(rn1.getPrevious()==null);
        assertTrue(rn2.getPrevious().getPrevious()==null);
    }

    
}
