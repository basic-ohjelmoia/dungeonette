/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

/**
 *
 * @author tuoma
 */
public class Specification {
    
    public int maxX;
    public int maxY;
    public int maxZ;
    
    public int gridX;
    public int gridY;
    public int density;
    
    public int pivotSeekPersistence;
    public int passagePersistence;
    public int roomDensity;
    public int midsizeRoomPersistence;
    public int largeRoomPersistence;
            
    public int twoByOnes;
    public int twoByTwos;
    public int threeByThrees;
    
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
        
        this.twoByOnes=VERY_COMMON;
        this.twoByTwos=COMMON;
        this.threeByThrees=RARE;
        
        
        this.pivotSeekPersistence=3;
        
        this.passagePersistence=3;
        this.roomDensity=50;
        this.midsizeRoomPersistence=1;
        this.largeRoomPersistence=1;
    }
}
