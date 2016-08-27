/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Floor;
import dungeonette.domain.Specification;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 */
public class ExtraTestsForPassageCarver {

    @Test
    public void testThinWallRemoval() {
        Specification spec = new Specification(100, 100, 1);
        Floor floor = new Floor(spec);

        int originalWallTiles=0;
        
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if ((y % 2 == 0 ) || y == 0 || y == 99 || x == 0 || x == 99) {
                    floor.getTiles()[x][y] = '#';
                    originalWallTiles++;
                    if (y % 20 == 0) {
                        floor.getTileIDs()[x][y]=1;
                    }
                } else {
                    floor.getTiles()[x][y] = '+';
                }
            }
        }

        PassageCarver.removeThinPassageWalls(floor, spec);
        
        int afterRemovalWallTiles=0;
        
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (floor.getTiles()[x][y]=='#') {
                    afterRemovalWallTiles++;
                }
            }
        }
        
        System.out.println("Passage Carver Wall Removal test numbers: before: "+originalWallTiles+", after: "+afterRemovalWallTiles);
        
        assertTrue(originalWallTiles > afterRemovalWallTiles);

    }

}
