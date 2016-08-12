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
    
           
    public Specification(int x, int y, int z) {
        this.maxX=x;
        this.maxY=y;
        this.maxZ=z;
        
        this.gridX=(x+9)/10;
        this.gridY=(y+9)/10;
    }
}
