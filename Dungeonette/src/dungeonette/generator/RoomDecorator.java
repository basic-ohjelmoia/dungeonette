/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Room;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 *
 * Class used to decorate newly constructed rooms with random items, furniture and such.
 * 
 * This class is intended purely as proof of concept, there is no actual deep logic behind what sort items would be actually placed.
 *
 */
public class RoomDecorator {

    /**
     * This method is run once every time a new room is constructed.
     * The higher the room.id number is, the more likely the room is going to contain items.
     * 
     * @param room 
     */
    public static void decorate(Room room) {

        Random randomi = new Random();
        
        char[][] shape = room.getShape();
        char[][] items = room.getItems();
        boolean[][] illegals = new boolean[room.dimension.width][room.dimension.height];
          
        for (Point pt : room.getDoorwayArray()) {
          if (pt!=null) {  
          
            illegals[pt.x-(room.location.x*10)][pt.y-(room.location.y*10)]=true;}
        }
        
        Dimension dimension = room.dimension;
        int size = (dimension.height*dimension.width)/5;
        int itemsGenerated=0;
        
        for (int y = 1; y < dimension.height-1; y++) {
            for (int x = 1; x < dimension.width-1; x++) {
                if (isLegal(x,y,shape,items,illegals)) {
                    if (randomi.nextInt(100+size+(Math.min(room.id/2 , 20))) >
                            95 - room.id +(itemsGenerated*2)) {
                        
                        int arpa = randomi.nextInt(6);
                        itemsGenerated++;
                        
                        if (arpa==0 && room.id<20){
                            items[x][y]='$';
                        } else if (arpa==0) {
                            items[x][y]='Æ';
                        }
                        else if (arpa==1) {
                            items[x][y]='€';
                        }
                        else if (arpa==2) {
                            items[x][y]='£';
                        }
                        else if (arpa==3) {
                            items[x][y]='%';
                        }
                        else if (arpa==4) {
                            items[x][y]='§';
                        }
                        else if (arpa==5) {
                            items[x][y]='½';
                        }
                        System.out.println("item generated!! type: "+items[x][y]);
                        if (randomi.nextInt(5)!=0) {
                            illegals[x][y]=true;
                        }
                        
                    }
                    
                }
                
                
            }
        }

    }

    
    private static boolean isLegal(int x, int y, char[][] shape, char[][] items, boolean[][] illegals) {
        
        
        if (!(shape[x+1][y]=='#' || shape[x-1][y]=='#' || shape[x][y+1]=='#' || shape[x][y-1]=='#' )) {
            return false;
        }
        
        if (shape[x][y]=='+' && !illegals[x][y] 
                && !illegals[x][y+1] && !illegals[x][y-1] && !illegals[x+1][y] && !illegals[x-1][y] ) {
            return true;
        }
        
        
        return false;
    }
}
