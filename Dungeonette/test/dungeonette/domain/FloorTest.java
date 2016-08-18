/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import dungeonette.generator.PassageCarver;
import java.awt.Dimension;
import java.awt.Point;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mikromafia
 */
public class FloorTest {

    public Floor floor;

    public FloorTest() {

        this.floor = new Floor(new Specification(100, 100,1), new Point(5, 5));
    }

    /**
     * Test of insertRoom method, of class Floor (also tests the RoomInserterClass).
     */
    @Test
    public void testInsertRoom() {

        assertTrue(floor.insertRoom(5, 5, new Dimension(10, 10), 'n', new Point(5, 5), 1));

        assertTrue(!floor.insertRoom(-5, -5, new Dimension(10, 10), 's', new Point(5, 5), 1));
        assertTrue(!floor.insertRoom(5, 66, new Dimension(10, 10), 'n', new Point(5, 5), 1));

        assertTrue(floor.insertRoom(5, 6, new Dimension(30, 30), 'n', new Point(5, 5), 1));
    }

    /**
     * Test of storeRoomsIntoTiles method, of class Floor.
     */
    @Test
    public void testPrint() {

        this.floor = new Floor(new Specification(100, 100,1), new Point(5, 5));
        this.floor.insertRoom(5, 5, new Dimension(10, 10), 'n', new Point(5, 5), 1);

        int emptyTileCount = 0;
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (this.floor.getTiles()[x][y] == 0) {
                    emptyTileCount++;
                }
            }
        }

        assertTrue(emptyTileCount == 100 * 100);

        int emptyTileCountAfterPrintCall = 0;

        this.floor.storeRoomsIntoTiles();

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (this.floor.getTiles()[x][y] == 0) {
                    emptyTileCountAfterPrintCall++;
                }
            }
        }

        assertTrue(emptyTileCountAfterPrintCall == 0);
    }

    /**
     * Test of carveRoutes method, of class Floor (also tests the PassageCarver class).
     */
    @Test
    public void testCarveRoutes() {

        this.floor = new Floor(new Specification(100, 100,1), new Point(5, 5));
        this.floor.insertRoom(5, 5, new Dimension(10, 10), 'n', new Point(5, 5), 1);
        this.floor.insertRoom(5, 7, new Dimension(10, 10), 'n', new Point(5, 5), 1);

        this.floor.storeRoomsIntoTiles();

        int floorTiles = 0;

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (this.floor.getTiles()[x][y] == '+') {
                    floorTiles++;
                }
            }
        }

        assertTrue(floorTiles > 0);

        int floorTilesAfterCarving = 0;

        PassageCarver.processAllRoutes(floor);

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (this.floor.getTiles()[x][y] == '+') {
                    floorTilesAfterCarving++;
                }
            }
        }

        assertTrue(floorTilesAfterCarving > floorTiles);

    }

}
