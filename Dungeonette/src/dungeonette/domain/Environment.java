/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import dungeonette.generator.Architect;
import dungeonette.generator.PassageCarver;
import dungeonette.generator.DungeonPrinter;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 * Environment is the parent object class for a multi-floor dungeon. The dungeon
 * is stored either into a 3-dimensional char array (not yet implemented) or
 * into an one dimensional array of floor objects (each of which contain a two
 * dimensional char array).
 *
 * The class currently contains a lot of program logic (dungeon generation)
 * which should be refactored into suitable sub classes.
 *
 * Still, the Environment class already produces quite advanced random dungeons!
 *
 * @author Tuomas Honkala
 */
public class Environment {

    private char[][][] tiles;
    private int[][][] tileID;
    private int xMax;
    private int yMax;
    private int zMax;
    private Floor[] floors;
    private Specification spec;

    /**
     * Constructor for an environment object. The constructor is basically given
     * the size and scope of the dungeon in form of max x, max y and max z
     *
     * Eventually the constructor should also accept parameters, which could be
     * used to specify the type of dungeon (sparse/dense, long passages/short
     * passages, lots of doors/no doors etc.) that should be generated.
     *
     * @param spec specification of the dungeon
    */
    public Environment(Specification spec) {
        int floorWidth = spec.maxX;
        int floorHeigth = spec.maxY;
        int numberOfFloors = spec.maxZ;

        this.tiles = new char[floorWidth][floorHeigth][numberOfFloors];
        this.tileID = new int[floorWidth][floorHeigth][numberOfFloors];
        this.xMax = floorWidth;
        this.yMax = floorHeigth;
        this.zMax = numberOfFloors;
        this.floors = new Floor[zMax];
        this.spec = spec;
    }
    
    public void generateFloors() {
        Point pointOfEntry = new Point(5,5);
        for (int i = 0; i< spec.maxZ; i++ ) {
            Architect.generateFloor(this, spec, i, new Point(pointOfEntry.x, pointOfEntry.y));
            pointOfEntry=floors[i].pointOfExit;
            System.out.println("gen floors: "+i+" exits at "+pointOfEntry.toString());
        }
        
        for (int i = 0; i< spec.maxZ; i++ ) {
            System.out.println("\n======================= DUNGEON LEVEL "+i+" ==============================\n");
        DungeonPrinter.printFloor(floors[i], spec);
        }
    }


    /**
     * Returns the array of floors
     * NOT IN USE CURRENTLY!
     * @return array of floors
     */
    public Floor[] getFloors() {
        return this.floors;
    }

    /**
     * Method for calling out room id numbers tied to specific tiles (id 0 = tile is not related to any specific room)
     * NOT IN USE CURRENTLY!
     * @return 3D-array of id numbers
     */
    public int[][][] getTileIDs() {
        return this.tileID;
    }
}
