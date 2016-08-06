/*
 *
 */
package dungeonette;

import dungeonette.domain.Environment;

/**
 * Dungeonette is a program which generates random dungeons for an 
 * unnamed tiled-based role playing game I'm working on.
 * @author Tuomas Honkala
 */
public class Dungeonette {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        Environment env = new Environment(100,100,1);
        env.generate();
    
    }
    
    
}
