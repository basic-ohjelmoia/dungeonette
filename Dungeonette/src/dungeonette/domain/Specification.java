/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import java.util.Random;

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

    /**
     * If this classicPringing is false, then extended char set is used for printing. This functionality has been taken out due to unreliable handling of extended char sets.
     */
    public boolean classicPrinting;
    /**
     * The common random object shared by the entire dungeon.
     */
    public Random randomi;
    /**
     * Seed word which dictates the layout of the dungeon. The size and the parameters of the dungeon need to be identical in order for the same seed to recreate the same dungeon.
     */
    public String seed;
    
    public int maxX;
    public int maxY;
    public int maxZ;
    
    /**
     * Number of x coodinates on the coarse grid. Each floor can be divided into coarse grid where each square represents an area of 10x10 coordinates.
     */
    public int gridX;
    /**
     * Number of y coodinates on the coarse grid. Each floor can be divided into coarse grid where each square represents an area of 10x10 coordinates.
     */
    public int gridY;
    
    /**
     * Number of additional dead-ends generated per floor
     */
    public int deadEndiness;
    
    /**
     * Number of bonus connecting passageways are generated per floor.
     */
    public int roomConnectivity;
    
    /**
     * maximum number of rooms being generated per floor
     */
    public int density; 
      
    /**
     * Each depts dungeon floor substracts the density/volatility by multiple of funnelEffect.
     * 1 = no funnelEffect.
     */
    public int funnelEffect;
    
    /**
     * larger the number, the farther the seek for neighbouring room location continues
     */
    public int pivotSeekPersistence;   
    /**
     *  larger the number, the longer the distance between two neighbouring rooms might be
     */
    public int passagePersistence;  
    /**
     * Density of room placement.
     * VERY IMPACTFUL to dungeon's appearance... 20 = full room density... 10 = nicely balanced look... 3 = very sparse dungeon
     */
    public int roomDensity;         
    
    /**
     * With this boolean set TRUE the scope of the floors will fluctuate wildly.
     */
    public boolean volatileRooms;
    
    /**
     *  how often mid sized rooms get (theoretically) generated
     */
    public int midsizeRoomPersistence;  
    /**
     * how often large sized rooms get (theoretically) generated
     */
    public int largeRoomPersistence;    
            
    /**
     *  impacts the likelyhood of midsized rooms
     */
    public int twoByOnes;       
    /**
     *   impacts the likelyhood of large rooms
     */
    public int twoByTwos;      
    /**
     *  impact the likelyhood of v.large rooms
     */
    public int threeByThrees;   
    
    /**
     * Likelyhood  of passage staying on course. 80...90 should be a pretty good figure. Less than 50 might look crazy.
     */
    public int passageStraightnessPercentile;
    
    public static final int VERY_COMMON = 3;
    public static final int SEMI_COMMON = 5;
    public static final int COMMON = 7;
    public static final int UNCOMMON = 12;
    public static final int RARE = 13;
    public static final int VERY_RARE = 23;
    public static final int NEVER = 99999;
    
    public boolean fileWritingOnly;
    public boolean speedTest;
           
    public Specification(int x, int y, int z) {
        this.maxX=x;
        this.maxY=y;
        this.maxZ=z;
        
        this.gridX=(x+9)/10;
        this.gridY=(y+9)/10;
        
        this.density=(gridX*gridY)/2;
        this.funnelEffect =1;
        
        this.twoByOnes=VERY_COMMON;
        this.twoByTwos=COMMON;
        this.threeByThrees=RARE;
        
        this.classicPrinting=false;
        
        this.pivotSeekPersistence=2;
        
        this.passageStraightnessPercentile=75;
        this.passagePersistence=4;
        this.roomDensity=92;         
        this.midsizeRoomPersistence=1;
        this.largeRoomPersistence=2;
        
        this.deadEndiness = 25;
        this.roomConnectivity = 1;
        
        this.speedTest=false;
        
        randomi = new Random();
    }
    
    /**
     * Sets a new seed for the Random object.
     * @param seed seed word.
     */
    public void setSeed(String seed) {
        this.seed=seed;
        
        int actual = 0;
        if (seed.length()%3==0) {
            actual=Integer.MAX_VALUE;
        } else if (seed.length()%3==1) {
            actual=Integer.MIN_VALUE;
        }
            
        
        for (int i = 0; i<seed.length();i++) {
            if (seed.charAt(i)==0) {
                actual*=(1+seed.length()+i);
            } else {
            actual*=seed.charAt(i);
            }
            actual-=(seed.charAt(i)+(seed.length()/3));
        }
  
        System.out.println("The Dungeon is generated out of seed: "+actual);
        this.randomi = new Random(actual);
    }
}
