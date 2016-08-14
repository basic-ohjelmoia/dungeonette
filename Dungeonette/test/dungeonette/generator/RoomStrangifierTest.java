/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Room;
import java.awt.Dimension;
import java.awt.Point;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mikromafia
 */
public class RoomStrangifierTest {

    public Room room;

    public RoomStrangifierTest() {
        room = new Room(new Point(0, 0), new Dimension(20, 20), 1, 'n');
    }

    /**
     * Test of reshape method, of class RoomStrangifier.
     */
    @Test
    public void testReshape() {
        for (int i = 0; i<10; i++) {
        room = new Room(new Point(0, 0), new Dimension(20, 20), 1, 'n');
        RoomStrangifier.reshape(room);

        room.generatePivots();
        Dimension dimension = room.dimension;
        char[][] shape = room.getShape();

        int numberOfFloorTiles = 0;
        int numberOfUnconnectedFloorTiles = 0;

        System.out.println("Strangiefied room: === iteration; "+i+" ====");
        for (int y = 0; y < dimension.height; y++) {
            System.out.print("\n");
            for (int x = 0; x < dimension.width; x++) {
                System.out.print(shape[x][y]);
                if (shape[x][y] == '+') {
                    numberOfFloorTiles++;
                    if (shape[x + 1][y] != '+' && shape[x - 1][y] != '+' && shape[x][y + 1] != '+' && shape[x][y - 1] != '+') {
                        numberOfUnconnectedFloorTiles++;

                    }
                }
            }
        }
        
        System.out.println("Number of interconnecting floortiles ="+numberOfFloorTiles);
        System.out.println("Number of non-connecting floortiles ="+numberOfUnconnectedFloorTiles);
        assertTrue(numberOfUnconnectedFloorTiles<1);
        assertTrue(numberOfFloorTiles>1);
        }
    }

}
