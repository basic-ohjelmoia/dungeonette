/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator.util;

import dungeonette.domain.Specification;
import java.awt.Dimension;

/**
 *
 * This class helps the Architect to choose the size (dimensions) of the next room being generated
 */
public class PickTheNextRoomSize {
    
    public static Dimension fromTheseParameters(Specification spec, int tries) {
        
         Dimension returnable = new Dimension(10,10);
         
         
         int diceTwoByOnesHorizontal = spec.randomi.nextInt(spec.twoByOnes);
            if (tries>spec.midsizeRoomPersistence) {diceTwoByOnesHorizontal=9999;}
            
         int diceTwoByOnesVertical = spec.randomi.nextInt(spec.twoByOnes);
            if (tries>spec.midsizeRoomPersistence) {diceTwoByOnesVertical=9999;}
            
         int diceTwoByTwos = spec.randomi.nextInt(spec.twoByTwos);
            if (tries>spec.largeRoomPersistence) {diceTwoByTwos=9999;}
         
        int diceThreeByThrees = spec.randomi.nextInt(spec.threeByThrees);
            if (tries>spec.largeRoomPersistence) {diceThreeByThrees=9999;}
            
         
         
         if (diceTwoByOnesHorizontal < diceTwoByOnesVertical &&
             diceTwoByOnesHorizontal < diceTwoByTwos &&
             diceTwoByOnesHorizontal < diceThreeByThrees) {
             return new Dimension(20, 10);
         }
         
          if (diceTwoByOnesVertical < diceTwoByOnesHorizontal &&
             diceTwoByOnesVertical < diceTwoByTwos &&
             diceTwoByOnesVertical < diceThreeByThrees) {
             return new Dimension(10, 20);
         }
                    
          if (diceTwoByTwos < diceTwoByOnesVertical &&
             diceTwoByTwos < diceTwoByOnesHorizontal &&
             diceTwoByTwos < diceThreeByThrees) {
             return new Dimension(20, 20);
         }
          
          if (diceThreeByThrees < diceTwoByOnesVertical &&
             diceThreeByThrees < diceTwoByTwos &&
             diceThreeByThrees < diceTwoByOnesHorizontal) {
             return new Dimension(30, 30);
         }
        
          return returnable;
    }
    
}
