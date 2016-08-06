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
 * Object class for the rooms placed into the dungeon.
 * 
 * The class contains a lot of program logic and lacks proper setters and getters.
 * 
 * @author tuomas honkala
 */
public class Room {
    
    public Dimension dimension;
    public int id;
    public char fromDirection;
    public char outDirection;
    public Point location;
    public Point roomCenter;
    private boolean horizontalPassage;
    private boolean verticalPassage;
    private char[][] shape;
    private int area;
    
    public Room(Point location, Dimension dimension, int newID, char from) {
        this.dimension=dimension;
        this.location=location;
        this.id=newID;
        this.fromDirection=from;
        this.shape= new char[dimension.width][dimension.height];
        if (dimension.width==3) {horizontalPassage=true;}
        if (dimension.height==3) {verticalPassage=true;}
        generateShape();
    }
    
    public void generateShape() {
        Random randomi = new Random();
        int hMargin=randomi.nextInt(3+dimension.width/8);
        int hMargin2=randomi.nextInt(3+dimension.width/8)+1;
        int vMargin=randomi.nextInt(3+dimension.height/8);
        int vMargin2=randomi.nextInt(3+dimension.height/8)+1;
        
        if (randomi.nextInt(15)>4) {
            hMargin=0; vMargin=0; hMargin2=1; vMargin2=1;
        }
        
        for (int y=0; y<dimension.height; y++) {
        for (int x=0; x<dimension.width; x++) {
            if (x<hMargin || x>dimension.width-hMargin2 ||
                    y<vMargin || y>dimension.height-vMargin2 
                    ) {
                shape[x][y]='.';
            } else if (
                    x==hMargin || x==dimension.width-hMargin2 ||
                    y==vMargin || y==dimension.height-vMargin2 
                    ) {
                shape[x][y]='#'; 
                
        }            
         else {
                  shape[x][y]='+';   
                  area++;
                }
            
        }
    }
        if (randomi.nextBoolean()) {
            deCornerize();
        }
    }
    
    public void deCornerize() {
        Random randomi = new Random();
        boolean nw=randomi.nextBoolean();
        boolean ne=randomi.nextBoolean();
        boolean sw=randomi.nextBoolean();
        boolean se=randomi.nextBoolean();
        int cutCorners=0;
        if (nw) {cutCorners++;}
        if (ne) {cutCorners++;}
        if (sw) {cutCorners++;}
        if (ne) {cutCorners++;}
        
        boolean center=false;
        if (randomi.nextInt(4)==1 && area>=75 ) {// && !nw&&!ne&&!sw&&!se) {
            center=true;
        }
        int cornerDivider=4;
        int centerDivider=4;
        if (cutCorners>0) {
            centerDivider=3;
            if (ne && nw && sw && se && area<100 ) {
                center=false;
            }
        }
        if (!center) {
            cornerDivider=3;
            System.out.println("cut corners: "+cutCorners+", dims: "+dimension.width+", "+dimension.height);
            if (cutCorners==1 && (dimension.width>10 || dimension.height>10)) {// && area>=81) {
                cornerDivider=2;
                System.out.println("cordir2");
            }
        }
        
        int clipX=dimension.width/cornerDivider;
        int clipX2=dimension.width-dimension.width/cornerDivider;
        int clipY=dimension.height/cornerDivider;
        int clipY2=dimension.height-dimension.height/cornerDivider;
        
          int cornerX=dimension.width/centerDivider;
        int cornerX2=dimension.width-dimension.width/centerDivider;
        int cornerY=dimension.height/centerDivider;
        int cornerY2=dimension.height-dimension.height/centerDivider;
        
             for (int y=0; y<dimension.height; y++) {
        for (int x=0; x<dimension.width; x++) {
            
            if (center) {
                if (x>cornerX+1 && x<cornerX2-1 && y>cornerY+1 && y<cornerY2-1) {
                     shape[x][y]='.';
                }
                if ((x==cornerX+1 || x==cornerX2-1) && y>=cornerY+1 && y<=cornerY2-1 &&  shape[x][y]!='.') {
                     shape[x][y]='#';
                }
                if ((y==cornerY+1 || y==cornerY2-1)&& x>=cornerX+1 && x<=cornerX2-1 &&  shape[x][y]!='.') {
                     shape[x][y]='#';
                }
                
            }
            
            
            if (nw && x<clipX && y<clipY) {
                shape[x][y]='.';
            }
            if (nw && x==clipX && y<clipY && shape[x][y]!='.') {
                shape[x][y]='#';
            }
              if (nw && y==clipY && x<clipX && shape[x][y]!='.') {
                shape[x][y]='#';
            }
                  if (nw && y==clipY && x==clipX && shape[x][y]!='.') {
                shape[x][y]='#';
            }
                  
                    if (ne && x>clipX2 && y<clipY) {
                shape[x][y]='.';
            }
            if (ne && x==clipX2 && y<clipY && shape[x][y]!='.') {
                shape[x][y]='#';
            }
              if (ne && y==clipY && x>clipX2 && shape[x][y]!='.') {
                shape[x][y]='#';
            }
                  if (ne && y==clipY && x==clipX2 && shape[x][y]!='.') {
                shape[x][y]='#';
            }
                  
                               if (sw && x<clipX && y>clipY2) {
                shape[x][y]='.';
            }
            if (sw && x==clipX && y>clipY2 && shape[x][y]!='.') {
                shape[x][y]='#';
            }
              if (sw && y==clipY2 && x<clipX && shape[x][y]!='.') {
                shape[x][y]='#';
            }
                  if (sw && y==clipY2 && x==clipX && shape[x][y]!='.') {
                shape[x][y]='#';
            }
                
                  
                  
                    if (se && x>clipX2 && y>clipY2) {
                shape[x][y]='.';
            }
            if (se && x==clipX2 && y>clipY2 && shape[x][y]!='.') {
                shape[x][y]='#';
            }
              if (se && y==clipY2 && x>clipX2 && shape[x][y]!='.') {
                shape[x][y]='#';
            }
                  if (se && y==clipY2 && x==clipX2 && shape[x][y]!='.') {
                shape[x][y]='#';
            }
                
            
        }
        }
    }

        public String print(int x, int y) {
        x-=location.x*10;
        y-=location.y*10;
        String palautettava = ""+id;
        if (horizontalPassage || verticalPassage) {
            return passagePrint(x,y);
        }
            return ""+shape[x][y]+shape[x][y];
        }
    
    public String printOld(int x, int y) {
        x-=location.x*10;
        y-=location.y*10;
        String palautettava = ""+id;
        if (horizontalPassage || verticalPassage) {
            return passagePrint(x,y);
        }
  
        if ((y==0 || y==dimension.height-1) && x%10 == 5 && (fromDirection=='n' || outDirection=='s')) {
            return "||";
        }
        if ((x==0 || x==dimension.width-1) && y%10 == 5 && (fromDirection=='w' || outDirection=='e')) {
            return "==";
        }
        
        
        
              if (x==0 || x==dimension.width-1) {
            return "##";
        }
        if (y==0 || y==dimension.height-1) {
            return "##";
        }
        
        
        if (palautettava.length()<2) {
            palautettava="0"+palautettava;
        } 
        
        if ( (x+y)%2==0) {
            return "´´";
        } 
        return palautettava;
    }
    
    public String passagePrint(int x, int y) {
        if ((x<5 || x>5) && horizontalPassage) {
            return "..";
        }
        if ((y<5 || y>5) && verticalPassage) {
            return "..";
        }
              if ((x==5 || x==5) && horizontalPassage) {
            return "||";
        }
              if ((y==5 || y==5) && verticalPassage) {
            return "==";
        }
              return "``";
    }
}
