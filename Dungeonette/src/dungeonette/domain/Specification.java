/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

/**
 *
 * This class is used for storing the paramets which control the type of dungeon being generated.
 * 
 * These parameters are not yet fully implemented, i.e. some parts of the code still containg hardcoded numbers which
 * ignore any of this specification stuff...
 * 
 * 
 */
public class Specification {
    
    public int maxX;
    public int maxY;
    public int maxZ;
    
    public int gridX;
    public int gridY;
    
    public int density; // minimum number of rooms being generated
    public int volatility; // maximum additional number of rooms being generated
    // NOTE: density+volatility  should be less than (gridX*gridY)/2
    
    public int pivotSeekPersistence;     // larger the number, the farther the seek for neighbouring room location continues
    public int passagePersistence;      // larger the number, the longer the distance between two neighbouring rooms might be
    public int roomDensity;         // VERY IMPACTFUL... 10 = full room density... 3 = very sparse dungeon
    
    public int midsizeRoomPersistence;  // how often mid sized rooms get (theoretically) generated
    public int largeRoomPersistence;    // how often large sized rooms get (theoretically) generated
            
    public int twoByOnes;       //  impacts the likelyhood of midsized rooms
    public int twoByTwos;       //  impacts the likelyhood of large rooms
    public int threeByThrees;   // impact the likelyhood of v.large rooms
    
    public final int VERY_COMMON = 3;
    public final int COMMON = 7;
    public final int UNCOMMON = 9;
    public final int RARE = 13;
    public final int VERY_RARE = 23;
    public final int NEVER = 99999;
    
           
    public Specification(int x, int y, int z) {
        this.maxX=x;
        this.maxY=y;
        this.maxZ=z;
        
        this.gridX=(x+9)/10;
        this.gridY=(y+9)/10;
        
        this.density=25;
        this.volatility=30;
        
        this.twoByOnes=VERY_COMMON;
        this.twoByTwos=COMMON;
        this.threeByThrees=RARE;
        
        
        this.pivotSeekPersistence=3;
        
        this.passagePersistence=3;
        this.roomDensity=8;         
        this.midsizeRoomPersistence=1;
        this.largeRoomPersistence=1;
    }
}
