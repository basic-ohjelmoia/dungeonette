/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mikromafia
 */
public class EnvironmentTest {
    
    public Environment env;
    
    public EnvironmentTest() {
        
                this.env = new Environment(new Specification(100,100,1));
            
    
    }

    /**
     * Test of generate method, of class Environment.
     */
    @Test
    public void testGenerate() {
     
        System.out.println("== Running dungeon generation test == ");
        int[] results = new int[10];
        for (int i=0; i<10; i++) {
            env.generate();
            
            Floor floor = env.getFloors()[0];
            
            int floorTiles=0;
            
            
           for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (floor.getTiles()[x][y] == '+') {
                    floorTiles++;
                }
            }
        }
        
           results[i]=floorTiles;
            
        assertTrue(floorTiles>500);
        
    }
        
        System.out.println("== test results == ");
        for (int i = 0; i<10; i++) {
            System.out.println("Dungeon generation iteration #"+i+", floor-tiles: "+results[i]);
        }
    }
    
}
