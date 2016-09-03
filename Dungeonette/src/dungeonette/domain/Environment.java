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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

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
    private long timer;
    private long writeTimer;

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
        this.timer = System.currentTimeMillis();
        int floorWidth = spec.maxX;
        int floorHeigth = spec.maxY;
        int numberOfFloors = spec.maxZ;

        this.tiles = new char[1][1][1];//floorWidth][floorHeigth][numberOfFloors];
        this.tileID = new int[1][1][1];//[floorWidth][floorHeigth][numberOfFloors];
        this.xMax = floorWidth;
        this.yMax = floorHeigth;
        this.zMax = numberOfFloors;
        this.floors = new Floor[zMax];
        this.spec = spec;
    }

    public void generateFloors() {
        Point pointOfEntry = new Point(spec.gridX / 2, spec.gridY / 2);

        Architect.initiateFloors(this, spec);

        for (int i = 0; i < spec.maxZ; i++) {
            Architect.generateFloor(this, spec, i, new Point(pointOfEntry.x, pointOfEntry.y));
            pointOfEntry = floors[i].pointOfExit;
            System.out.println("\n");
        }
        long result = System.currentTimeMillis() - timer;
       
        
        // no file is going to be written if Dungeonette is run in speedtest mode.
        if (!spec.speedTest) {
            writeTimer = System.currentTimeMillis();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("dungeon.txt", false));
                for (int i = 0; i < spec.maxZ; i++) {

                    Floor next = null;
                    if (i < spec.maxZ - 1 && spec.maxZ > 1) {
                        next = floors[i + 1];
                    }
                    for (StringBuilder line : DungeonPrinter.printFloor(floors[i], next, spec)) {
                        writer.write(line.toString());
                    }
                }
                writer.write("\n\n\n__________________________________________________________");
                
                writer.close();
                

            } catch (Exception e) {
                System.out.println("Oops!");
            }
            writeTimer = System.currentTimeMillis()-writeTimer;
        }
        String quickTake = "Generated " + spec.maxZ + " levels of " + (spec.maxX * spec.maxY) + " coordinates each (" + spec.maxX + " x " + spec.maxY + " x " + spec.maxZ + ") in " + result + " ms";
        System.out.println(quickTake);
        if (writeTimer>0L) {
            System.out.println("Filewrite time: "+writeTimer+" ms");
        }
    }

    /**
     * Returns the array of floors NOT IN USE CURRENTLY!
     *
     * @return array of floors
     */
    public Floor[] getFloors() {
        return this.floors;
    }

    /**
     * Method for calling out room id numbers tied to specific tiles (id 0 =
     * tile is not related to any specific room) NOT IN USE CURRENTLY!
     *
     * @return 3D-array of id numbers
     */
    public int[][][] getTileIDs() {
        return this.tileID;
    }
}
