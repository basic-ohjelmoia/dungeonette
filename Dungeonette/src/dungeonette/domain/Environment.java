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
        while (rooms<20) {
            int arpa = randomi.nextInt(4);
            char from = ' ';
            char out = ' ';
                    
            System.out.println("cx cy "+cx+","+cy);
            for (int tries=0; tries<2; tries++) {
            if (arpa==0) {from='n'; out='s'; cy--;}
             if (arpa==1) {from='s'; out='n';cy++;}
              if (arpa==2) {from='w'; out='e';cx--;}
               if (arpa==3) {from='e'; out='w'; cx++;}
            
               if (cx<0 || cx>9 || cy<0 || cy>9) {
                   cx= randomi.nextInt(10);
                   cy= randomi.nextInt(10);
               }
               
               if (cx>=0 && cx<10 && cy>=0 && cy<10) {
                   Dimension dimension = new Dimension(10,10);
                   if (rooms%11==7) {
                       dimension = new Dimension(20,10);
                   }
                    if (rooms%11==5) {
                       dimension = new Dimension(10,20);
                   }
                     if (rooms%11==3) {
                       dimension = new Dimension(20,20);
                   }
                   if (floor.insertRoom2(cx, cy, dimension, from, rooms)) {
                       tries+=5;
                       rooms++;
                       System.out.println("Room of dim "+dimension.width+","+dimension.height+" generated at "+cx+", "+cy);
                       floor.roomLayout[cx][cy].outDirection=out;
                   }
               }
            }
        }
        floor.print();
        
    }
    
}
