/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator.util;

import dungeonette.domain.Specification;

/**
 *
 */
public class WeightedSeekDirection {
    
    public static int optimalRandom(int cx, int cy, Specification spec) {
        int weightWest = Math.abs(0-cx)+1;
        int weightEast = Math.abs(spec.maxX-cx)+1;
        int weightNorth = Math.abs(0-cy)+1;
        int weightSouth = Math.abs(spec.maxY-cy)+1;
        
        int common = (weightWest+weightEast+weightNorth+weightSouth/12);
        
        if (cx>spec.gridX/4 && cx<spec.gridX-1) {
            weightWest=common;
        }
        if (cx<spec.gridX-(spec.gridX/4) && cx>0) {
            weightEast=common;
        }
        if (cy>spec.gridY/4 && cx<spec.gridY-1) {
            weightNorth=common;
        }
        if (cy<spec.gridY-(spec.gridY/4) && cy>0) {
            weightSouth=common;
        }
        
        
        weightWest = spec.randomi.nextInt(weightWest);
        weightEast =  spec.randomi.nextInt(weightEast);
        weightNorth = spec.randomi.nextInt(weightNorth);
        weightSouth =  spec.randomi.nextInt(weightSouth);
        
        if (weightNorth>weightEast && weightNorth>weightWest && weightNorth>weightSouth) {
            return 0;
        }
        
        if (weightEast>weightWest && weightEast>weightNorth && weightEast>weightSouth) {
            return 3;
        }
        
        if (weightWest>weightEast && weightWest>weightNorth && weightWest>weightSouth) {
            return 2;
        }
        
        return 1;
    }
    
}
