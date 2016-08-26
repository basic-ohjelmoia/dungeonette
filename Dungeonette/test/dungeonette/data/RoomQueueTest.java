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
 */
public class RoomQueueTest {

    private RoomQueue rq;
    private Random randomi;

    public RoomQueueTest() {
        rq = new RoomQueue();
        randomi = new Random(10);
    }

    /**
     * Test main functions of the RoomQueu class.
     */
    @Test
    public void testFrontAndEnqueueAndDeQueueAndEmptyAndSize() {
        rq = new RoomQueue();

        Room r1 = new Room(new Point(0, 0), new Dimension(10, 10), 1, 'n', randomi);
        Room r2 = new Room(new Point(0, 0), new Dimension(10, 10), 2, 'n', randomi);
        Room r3 = new Room(new Point(0, 0), new Dimension(10, 10), 3, 'n', randomi);

        r1.removeAllPivots();
        r2.removeAllPivots();
        r3.removeAllPivots();

        assertTrue(rq.isEmpty());
        assertTrue(rq.getSize() == 0);

        rq.enqueue(r1);

        assertTrue(!rq.isEmpty());
        assertTrue(rq.getSize() == 1);

        rq.enqueue(r2);

        assertTrue(!rq.isEmpty());
        assertTrue(rq.getSize() == 2);

        rq.dequeue();
        assertTrue(!rq.isEmpty());
        assertTrue(rq.getSize() == 1);

        rq.enqueue(r3);
        assertTrue(!rq.isEmpty());
        assertTrue(rq.getSize() == 2);

        rq.dequeue();
        rq.front();
        assertTrue(rq.getSize() == 1);
        rq.dequeue();
        assertTrue(rq.isEmpty());
    }

  

}
