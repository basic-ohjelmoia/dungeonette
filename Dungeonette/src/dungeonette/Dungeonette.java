/*
 *
 */
package dungeonette;

import dungeonette.domain.Environment;
import dungeonette.domain.Specification;


/**
 * Dungeonette is a program which generates random dungeons for an 
 * unnamed tiled-based role playing game I'm working on.
 * 
 * 
 * @author Tuomas Honkala
 */
public class Dungeonette {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
                
        Specification spec = new Specification(140,110,5); // dungeon specification 
        spec.setSeed("siemenluku määrää minkälainen dungeon generoidaan kunhan muut parametrit säilyvät samoina");
        Environment env = new Environment(spec);    // the environment for the dungeon generation
        env.generateFloors(); // go-code
    
    }
    
    
}
