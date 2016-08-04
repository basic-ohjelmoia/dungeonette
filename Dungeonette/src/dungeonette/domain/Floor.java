/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author tuoma
 */
public class Floor {
    
    private Grid[][] grids;
    private char[][] tiles;
    public Room[][] roomLayout;
    private int xMax;
    private int yMax;
    //private int currentRoomID;
    private Point entry;
    
    public Floor(int xMax, int yMax, Point pointOfEntry) {
        grids=new Grid[xMax/20][yMax/20];
        tiles=new char[xMax][yMax];
        roomLayout=new Room[xMax/10][yMax/10];
        this.xMax=xMax;
        this.yMax=yMax;
        this.entry=pointOfEntry;
    }
    
    public boolean insertRoom(int roomLayoutX, int roomLayoutY, Dimension dimension, char fromDirection, int currentRoomID) {
        int gridX=roomLayoutX/2;
        int gridY=roomLayoutY/2;
        Grid currentGrid = grids[gridX][gridY];
        
        if (currentGrid==null) {
            currentGrid = new Grid();
            grids[roomLayoutX/2][roomLayoutY/2]= currentGrid;
        }
        
        String quadrants = "";
        if (dimension.width>10 && dimension.height>10) {
            quadrants="nwneswse";
        }
        if (dimension.width>10 && dimension.height<=10 && fromDirection=='n') {
            quadrants="nwne";
        }
         if (dimension.width>10 && dimension.height<=10 && fromDirection=='s') {
            quadrants="swse";
        }
         if (dimension.width<=10 && dimension.height>10 && fromDirection=='w') {
            quadrants="nwsw";
        }
         if (dimension.width<=10 && dimension.height>10 && fromDirection=='e') {
            quadrants="nese";
        }
         
         
         if (dimension.width<=10 && dimension.height<=10) {
             quadrants+=fromDirection;
             
         }
         Room room = new Room(dimension, currentRoomID, fromDirection);
         if (currentGrid.insertRoomIntoGrid(quadrants, currentRoomID)) {
             System.out.println(" etsitään,,, "+currentRoomID+ ", curr layout xy: "+roomLayoutX+","+roomLayoutY);
             if (currentGrid.roomID[0][0]==currentRoomID) {
                 roomLayout[(gridX*2)][gridY*2]=room;
             }
              if (currentGrid.roomID[1][0]==currentRoomID) {
                 roomLayout[(gridX*2)+1][gridY*2]=room;
             }
               if (currentGrid.roomID[0][1]==currentRoomID) {
                 roomLayout[(gridX*2)][(gridY*2)+1]=room;
             }
                if (currentGrid.roomID[1][1]==currentRoomID) {
                 roomLayout[(gridX*2)+1][(gridY*2)+1]=room;
             }
                     
                      
                      return true;
         }
         return false;
    }
    
    
    public boolean insertRoom2(int rlx, int rly, Dimension dimension, char fromDirection, int currentRoomID) {
   
        int size = (dimension.height*dimension.width)/100;
        int reqX[] = new int[size];
        int reqY[] = new int[size];

        
       
        String quadrants = "";
      
        if (size>3) {
            
            if (rlx>=9 || rly>=9) {
                return false;
            }
            
             reqX[1]=rlx+1;
             reqY[1]=rly;
             reqX[2]=rlx;
             reqY[2]=rly+1;
             reqX[3]=rlx+1;
             reqY[3]=rly+1;
             
        }
          else if (dimension.width>10) {
              
              if (rlx>=9) {
                return false;
            }
              
             reqX[1]=rlx+1;
              reqY[1]=rly;
        }
        else if (dimension.height>10) {
            
            if (rly>=9) {
                return false;
            }
            
            reqX[1]=rlx;
             reqY[1]=rly+1;
        }
            
            reqX[0]=rlx;
            reqY[0]=rly;
        
            boolean failed=false;
            for (int i = 0; i<size && !failed; i++) {
                if (roomLayout[reqX[i]][reqY[i]]!=null) {
                    failed=true;
                }
            }
            if (failed) {
                return false;
            }
             Room room = new Room(dimension, currentRoomID, fromDirection);
             for (int i = 0; i<size; i++) {
                roomLayout[reqX[i]][reqY[i]]=room;
                
            }
            
                      
                      return true;
         
    }
    
    public void print() {
        System.out.println("printing...");
        for (int y=0; y<100; y++) {
            System.out.print("\n");
            for (int x=0; x<100; x++) {
                if (roomLayout[x/10][y/10]==null) {
                    System.out.print("..");
                } else if (x%10==0 || y%10==0 || x%10==9 || y%10==9){
                       Room room = roomLayout[x/10][y/10];
                       if ((room.outDirection=='w' || room.fromDirection=='w' )&& x%10==0 && y%10==5) {
                           System.out.print("==");
                       } else if ((room.fromDirection=='e'  ||room.fromDirection=='e') && x%10==9 && y%10==5) {
                           System.out.print("==");
                       }
                       else if ((room.fromDirection=='s' || room.fromDirection=='n') && x%10==5 && y%10==0) {
                           System.out.print("||");
                       }
                       else if ((room.fromDirection=='n' || room.fromDirection=='s') && x%10==5 && y%10==9) {
                           System.out.print("||");
                       }
                       else {
                    System.out.print("##");
                       }
                }
                else {
                    Room room = roomLayout[x/10][y/10];
                    String id = ""+room.id;
                    if (id.length()<2) {
                        id="0"+id;
                    }
                    System.out.print(id);
                }
            }
        }
        
        
    }
}
