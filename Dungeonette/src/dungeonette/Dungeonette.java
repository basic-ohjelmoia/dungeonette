/*
 *
 */
package dungeonette;

import dungeonette.domain.Environment;
import dungeonette.domain.Specification;
import java.util.ArrayList;

/**
 * Dungeonette is a program which generates random dungeons for an 
 * unnamed tiled-based role playing game I'm working on.
 * 
 * PLEASE NOTE:
 * The core of the dungeon generation is currently run inside of the Environment class under the generate() method.
 * It's a very messy piece of coding which needs some serious refactoring!!
 * 
 * @author Tuomas Honkala
 */
public class Dungeonette {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
                
        Specification spec = new Specification(140,110,5); // dungeon specification 
        Environment env = new Environment(spec);    // the environment for the dungeon generation
        env.generateFloors(); // go-code
    
    }
    
    
}
