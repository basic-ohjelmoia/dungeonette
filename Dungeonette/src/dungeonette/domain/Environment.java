/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author tuoma
 */
public class Environment {
    
    private char[][][] tiles;
    private int xMax;
    private int yMax;
    private int zMax;
    private Floor[] floors;
    
    public Environment(int floorWidth, int floorHeigth, int numberOfFloors) {
        this.tiles = new char[floorWidth][floorHeigth][numberOfFloors];
        this.xMax=floorWidth;
        this.yMax=floorHeigth;
        this.zMax=numberOfFloors;
        this.floors=new Floor[zMax];
    }
    
    public void generate() {
        Floor floor = new Floor(xMax, yMax, new Point(45,45));
        int rooms=1;
        Random randomi = new Random();
        int cx=45/10;
        int cy=45/10;
        
        Point temporaryOrigin = new Point(cx,cy);
        
        char oldFrom=' ';
        
        int maxRooms=randomi.nextInt(40)+20;
        int failuresSinceLastRoomGeneration=0;
        Point[] roomLocations= new Point[maxRooms+1];
        while (rooms<maxRooms && failuresSinceLastRoomGeneration<100) {
            int arpa = randomi.nextInt(4);
            if (oldFrom=='n' && arpa==0) {arpa=1;}
            if (oldFrom=='s' && arpa==1) {arpa=2;}
            if (oldFrom=='w' && arpa==2) {arpa=3;}
            if (oldFrom=='e' && arpa==3) {arpa=0;}
            
            char from = ' ';
            char out = ' ';
                    
            System.out.println("cx cy "+cx+","+cy);
            boolean roomGenerated=false;
            boolean obstacleMet=false;
            
            
            for (int tries=0; tries<3 && !roomGenerated; tries++) {
                System.out.println("Try number "+tries);
            if (arpa==0 && tries<2) {from='n'; out='s'; cy--;}
             if (arpa==1 && tries<2) {from='s'; out='n';cy++;}
              if (arpa==2 && tries<2) {from='w'; out='e';cx--;}
               if (arpa==3 && tries<2) {from='e'; out='w'; cx++;}
            
               if (cx<0 || cx>9 || cy<0 || cy>9 || obstacleMet) {
                   failuresSinceLastRoomGeneration++;
                   
                   int roomArpa=1;
                   if (rooms>2) {roomArpa=Math.max(1,randomi.nextInt(rooms));}
                   System.out.println("Rooms "+rooms+" vs "+roomArpa+" vs roomLoc.length "+roomLocations.length);
                   cx= roomLocations[roomArpa].x;
                   cy= roomLocations[roomArpa].y;
                   temporaryOrigin=new Point(cx,cy);
                   out=' ';
                   from=' ';
               }
               
               if (cx>=0 && cx<10 && cy>=0 && cy<10) {
                   Dimension dimension = new Dimension(10,10);
                   if (rooms%11==7 && tries<=1) {
                       dimension = new Dimension(20,10);
                   }
                    if (rooms%11==5 && tries<=1) {
                       dimension = new Dimension(10,20);
                   }
                     if (rooms%11==3 && tries<=1) {
                       dimension = new Dimension(20,20);
                   }
                         if (rooms%11==4 && tries<=1) {
                       dimension = new Dimension(30,30);
                   }
                   if (floor.insertRoom(cx, cy, dimension, from, temporaryOrigin, rooms)) {
                       roomGenerated=true;
                       oldFrom=out;
                       System.out.println("Room #"+rooms+" of dim "+dimension.width+","+dimension.height+" generated at "+cx+", "+cy);
                       roomLocations[rooms]=new Point(cx,cy);
                       temporaryOrigin=new Point(cx,cy);
                       failuresSinceLastRoomGeneration=0;
                       rooms++;
                       
                       floor.roomLayout[cx][cy].outDirection=out;
                   } else if (floor.roomLayout[cx][cy]==null && tries==0 && 
                           !(from=='n' && cy==0) && !(from=='s' && cy==9) && !(from=='w' && cx==0) && !(from=='e' && cx==9) ) {
                       System.out.println("generated a passage on try "+tries);
                       int dw=3; int dh=3;
                       
                       if (from=='n' || from=='s') {
                           dh=10;
                       } else {
                           dw=10;
                       }
            //           floor.roomLayout[cx][cy]= new Room(new Point(cx,cy), new Dimension(dw,dh), -1,from);
                       
                   } else if (floor.roomLayout[cx][cy]!=null) {
                       obstacleMet=true;
                   }
               }
            }
        }
        floor.print();
        
    }
    
}
