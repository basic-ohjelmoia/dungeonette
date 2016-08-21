/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import dungeonette.generator.DoorInserter;
import dungeonette.generator.DungeonPrinter;
import dungeonette.generator.PassageCarver;
import dungeonette.generator.RoomInserter;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mikromafia
 */
public class FloorTest {

    public Floor floor;
    public Specification spec;

    public FloorTest() {

        this.spec = new Specification(100, 100,1);
        this.floor = new Floor(spec, new Point(5, 5));
        this.floor.removeSalts();
    }

    /**
     * Test of insertRoom method, of class Floor (also tests the RoomInserterClass).
     */
    @Test
    public void testInsertRoom() {

        assertTrue(RoomInserter.seeIfItFits(floor, spec, 5, 5, new Dimension(10, 10), 'n', new Point(5, 5), 1));

        assertTrue(!RoomInserter.seeIfItFits(floor, spec, -5, -5, new Dimension(10, 10), 's', new Point(5, 5), 2));
        assertTrue(!RoomInserter.seeIfItFits(floor, spec, 5, 66, new Dimension(10, 10), 'n', new Point(5, 5), 2));

        // NOTE: The room id number is not supposed to increment beyond two as the previous two RoomInserts will fail due to out-of-bounds insertion coordinates
        assertTrue(RoomInserter.seeIfItFits(floor, spec, 8, 8, new Dimension(20, 20), 'n', new Point(5, 5), 2));
    }

    /**
     * Test of storeRoomsIntoTiles method, of class Floor.
     */
    @Test
    public void testPrint() {

        this.floor = new Floor(new Specification(100, 100,1), new Point(5, 5));
        this.floor.removeSalts();
        assertTrue(RoomInserter.seeIfItFits(floor, spec, 5, 5, new Dimension(10, 10), 'n', new Point(5, 5), 1));

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
     * Test of PassageCarver static method, of class Floor (also tests the PassageCarver class).
     */
    @Test
    public void testPassageCarving() {

        this.floor = new Floor(new Specification(100, 100,1), new Point(5, 5));
        this.floor.removeSalts();
        assertTrue(RoomInserter.seeIfItFits(this.floor, this.spec, 5, 5, new Dimension(10, 10), 'n', new Point(5, 5), 1));
        assertTrue(RoomInserter.seeIfItFits(this.floor, this.spec, 5, 7, new Dimension(10, 10), 'n', new Point(5, 5), 2));

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

        PassageCarver.processAllRoutes(floor,spec);

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (this.floor.getTiles()[x][y] == '+') {
                    floorTilesAfterCarving++;
                }
            }
        }
        DungeonPrinter.printFloor(this.floor, this.spec);
        assertTrue(floorTilesAfterCarving > floorTiles);

    }

    
    /**
     * Test of random route placement
     */
    @Test
    public void testPassageCarverWithAdditionalRoutesAndDoorPlacement() {

        this.floor = new Floor(new Specification(100, 100,1), new Point(5, 5));
        this.floor.removeSalts();
        assertTrue(RoomInserter.seeIfItFits(this.floor, this.spec, 5, 5, new Dimension(10, 10), 'n', new Point(5, 5), 1));
        assertTrue(RoomInserter.seeIfItFits(this.floor, this.spec, 5, 8, new Dimension(10, 10), 'n', new Point(5, 5), 2));
        assertTrue(RoomInserter.seeIfItFits(this.floor, this.spec, 8, 8, new Dimension(10, 10), 'n', new Point(5, 8), 3));
        assertTrue(RoomInserter.seeIfItFits(this.floor, this.spec, 8, 5, new Dimension(10, 10), 'n', new Point(5, 5), 4));

     
        this.floor.storeRoomsIntoTiles();
        PassageCarver.processAllRoutes(floor,spec);
        DoorInserter.processAllDoors(floor, spec);
        
        int floorTiles = 0;
        int doorTiles = 0;

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (this.floor.getTiles()[x][y] == '+') {
                    floorTiles++;
                }
                if (this.floor.getTiles()[x][y] == '=' || this.floor.getTiles()[x][y] == '|') {
                    doorTiles++;
                }
            }
        }

        assertTrue(floorTiles > 0);
        assertTrue(doorTiles > 0);

        int floorTilesRandomPassage = 0;

        this.floor.addRandomRoute(true);
        
             PassageCarver.processAllRoutes(floor,spec);
        
        

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (this.floor.getTiles()[x][y] == '+') {
                    floorTilesRandomPassage++;
                }
            }
        }

        assertTrue(floorTilesRandomPassage > floorTiles);
        
        
    }

    
    /**
     * Test of crossy passages;
     * No rooms will be placed for this test, only crossy passages!
     */
    @Test
    public void testCrossyPassages() {

        this.floor = new Floor(new Specification(100, 100,1), new Point(5, 5));
        
        this.floor.addCrossyPassage(new Point(50,50), 'n');
        this.floor.addCrossyPassage(new Point(60,60), 's');
        this.floor.addCrossyPassage(new Point(70,70), 'w');
        this.floor.addCrossyPassage(new Point(80,80), 'e');
        
        
        PassageCarver.processAllRoutes(floor,spec);
        
        int floorTiles = 0;

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (this.floor.getTiles()[x][y] == '+') {
                    floorTiles++;
                }
            }
        }

        assertTrue(floorTiles > 0);
        
        
        
        
    }
    
    @Test
    public void testForAWallPass() {
        char[][] tiles = new char[10][10];
            
        tiles[0][0]='.';tiles[1][0]='.';tiles[2][0]='.';
        tiles[0][1]='.';tiles[1][1]='.';tiles[2][1]='.';
        tiles[0][2]='#';tiles[1][2]='#';tiles[2][2]='#';
        tiles[0][3]='+';tiles[1][3]='+';tiles[2][3]='+';
        tiles[0][4]='#';tiles[1][4]='#';tiles[2][4]='#';
        

          int wallPasses=1;
          
          tiles[1][2]='+';
          wallPasses=PassageCarver.checkForAWallPass(1,2,wallPasses,'n', tiles);
          System.out.println("wall passes I : "+wallPasses+", tile 1,1 = "+tiles[1][1]);
          assertTrue(wallPasses==1);
          
          tiles[0][0]='.';tiles[1][0]='.';tiles[2][0]='.';
        tiles[0][1]='.';tiles[1][1]='.';tiles[2][1]='.';
        tiles[0][2]='#';tiles[1][2]='#';tiles[2][2]='#';
        tiles[0][3]='+';tiles[1][3]='+';tiles[2][3]='+';
        tiles[0][4]='#';tiles[1][4]='#';tiles[2][4]='#';
          
          wallPasses=PassageCarver.checkForAWallPass(1,1,wallPasses,'s', tiles);
          
          assertTrue(wallPasses==0);
          
        
    }
}
