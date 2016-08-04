/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette;

import dungeonette.domain.Environment;

/**
 *
 * @author tuoma
 */
public class Dungeonette {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        Environment env = new Environment(100,100,1);
        env.generate();
    
    }
    
    
}
