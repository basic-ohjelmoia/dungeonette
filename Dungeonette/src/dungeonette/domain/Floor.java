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
    private Point[] routeFrom;
    private Point[] routeTo;
    private int routes;
    
    
    public Floor(int xMax, int yMax, Point pointOfEntry) {
        grids=new Grid[xMax/20][yMax/20];
        tiles=new char[xMax][yMax];
        roomLayout=new Room[xMax/10][yMax/10];
        this.xMax=xMax;
        this.yMax=yMax;
        this.entry=pointOfEntry;
        this.routeFrom = new Point[200];
        this.routeTo = new Point[200];
    }
    
   
    
    
    public boolean insertRoom(int rlx, int rly, Dimension dimension, char fromDirection, Point origin, int currentRoomID) {
   
        int size = (dimension.height*dimension.width)/100;
        int reqX[] = new int[size];
        int reqY[] = new int[size];
        
        int centerX=0;
        int centerY=0;

        int xStep=1;
        int yStep=1;
        if (rlx<5) {xStep=-1;}
        if (rly<5) {yStep=-1;}
        if (size==1) {
            xStep=0;yStep=0;
        }
       
        String quadrants = "";
      
          if (size>1) {
            if (rlx+xStep>9 || rly+yStep>9 || rlx+xStep<0 || rly+yStep<0 ||
                    rlx>9 || rly>9 || rlx<0 || rly<0
                    ) {
                return false;
            }
            if (size==9 && (rlx<3 || rlx>7 || rly<3 || rly>7 )) {
                return false;
            }
          }
        if (size>3) {
            
            
             reqX[1]=rlx+xStep;
             reqY[1]=rly;
             reqX[2]=rlx;
             reqY[2]=rly+yStep;
             reqX[3]=rlx+xStep;
             reqY[3]=rly+yStep;
             if (size==9) {
                 reqX[4]=rlx+xStep+xStep;
                 reqY[4]=rly;
                 reqX[5]=rlx+xStep+xStep;
                 reqY[5]=rly+yStep;
                 reqX[6]=rlx+xStep+xStep;
                 reqY[6]=rly+yStep+yStep;
                 reqX[7]=rlx;
                 reqY[7]=rly+yStep+yStep;
                 reqX[8]=rlx+xStep;
                 reqY[8]=rly+yStep+yStep;
                 xStep=xStep+xStep;
                 yStep=yStep+yStep;
             }
             
        }
          else if (dimension.width>10) {
              
              if (rlx>=9) {
                return false;
            }
              
             reqX[1]=rlx+xStep;
              reqY[1]=rly;
              yStep=0;
        }
        else if (dimension.height>10) {
            
            if (rly>=9) {
                return false;
            }
            
            reqX[1]=rlx;
             reqY[1]=rly+yStep;
             xStep=0;
        }
            
            reqX[0]=rlx;
            reqY[0]=rly;
        
            boolean failed=false;
            for (int i = 0; i<size && !failed; i++) {
                if (roomLayout[reqX[i]][reqY[i]]!=null) {
                    failed=true;
                }
                 centerX+=reqX[i];
                    centerY+=reqY[i];
            }
            if (failed) {
                return false;
            }
            centerX/=size;
            centerY/=size;
            
             Room room = new Room(new Point(Math.min(rlx,rlx+xStep),Math.min(rly, rly+yStep)), dimension, currentRoomID, fromDirection);
             room.roomCenter=new Point(centerX, centerY);
             routeFrom[routes]=origin;
             routeTo[routes]=new Point(room.location);
             routeTo[routes]=room.roomCenter;
             System.out.println("Room loc = "+room.location.toString()+" vs center "+room.roomCenter.toString());
             routes++;
             for (int i = 0; i<size; i++) {
                roomLayout[reqX[i]][reqY[i]]=room;
                
            }
            
                      
                      return true;
         
    }
    
    public void printOld() {
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
    
        public void print() {
        System.out.println("printing...");
        for (int y=0; y<100; y++) {
            System.out.print("\n");
            for (int x=0; x<100; x++) {
                if (roomLayout[x/10][y/10]==null) {
                    System.out.print("..");
                    tiles[x][y]='.';
                } else {
                       Room room = roomLayout[x/10][y/10];
                      
                      System.out.print(room.print(x, y));
                      tiles[x][y]=room.print(x, y).charAt(1);
                      
                      
                }
            }
        }
        carveRoutes();
        
    }
        
        public void carveRoutes() {
            System.out.println("routes "+routes);
            for (int i = 0; i<routes; i++) {
                int startX=(routeFrom[i].x*10)+5;
                int startY=(routeFrom[i].y*10)+5;
                
                int endX=(routeTo[i].x*10)+5;
                int endY=(routeTo[i].y*10)+5;
                
                int cx=startX;
                int cy=startY;
                System.out.println("route "+i+" from "+cx+","+cy+", to "+endX+","+endY);
                int chaos=0;
                while(true) {
                 
                    int oldChaos=chaos;
                    
                    if (chaos<3 && cx>0 && cx<99 && cy>0 && cy<99 ) {
                    if ((cx+cy)%29==3) {cx++;chaos++;}
                    else if ((cx+cy)%29==17) {cx--;chaos++;}
                    else if ((cx+cy)%29==13) {cy++;chaos++;}
                    else if ((cx+cy)%29==27) {cy--;chaos++;}
                    } 
                    
                    if (oldChaos==chaos) {
                           if (cx<endX) {cx++;}
                    else if (cx>endX) {cx--;}
                    else if (cy<endY) {cy++;}
                    else if (cy>endY) {cy--;}
                    }
                    
                    if (tiles[cx][cy]!='+') {
                        tiles[cx][cy]='+';
                        for (int sy=cy-1; sy<=cy+1; sy++) {
                        for (int sx=cx-1; sx<=cx+1; sx++) {
                            if (sx==cx && sy==cy) {
                                sx++;
                            }
                            if (tiles[sx][sy]=='.') {
                                tiles[sx][sy]='#';
                            }
                        }
                        }
                    }
                    if (cx==endX && cy==endY) {
                        System.out.println("terminated at "+cx+","+cy);
                        break;
                        
                    }
                
                }
            }
            
            System.out.println("printing II...");
        for (int y=0; y<100; y++) {
            System.out.print("\n");
            for (int x=0; x<100; x++) {
                
                if (tiles[x][y]==0) {
                    System.out.print("..");
                    
                } else {
                       
                      System.out.print(tiles[x][y]+""+tiles[x][y]);
                      
                      
                      
                }
            }
        }
            
        }
}
