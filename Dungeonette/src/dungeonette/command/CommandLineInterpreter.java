/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.command;

import dungeonette.domain.Specification;
import java.util.Random;

/**
 *
 * @author mikromafia
 */
public class CommandLineInterpreter {
    
    public static void printHelp() {
        System.out.println("\n\n________                                           __    __ ___________");
        System.out.println("\\______ \\  __ __  ____   ____  ____   ____   _____/  |__/  |\\_   _____/");
        System.out.println(" |    |  \\|  |  \\/    \\_/ __ \\/  _ \\ /    \\_/ __ \\   __\\   __\\    __)_"); 
        System.out.println(" |    `   \\  |  /   |  \\  ___(  <_> )   |  \\  ___/|  |  |  | |        \\");
        System.out.println("/_______  /____/|___|  /\\___  >____/|___|  /\\___  >__|  |__|/_______  /");
        System.out.println("        \\/           \\/     \\/           \\/     \\/                  \\/ ");
        System.out.println("by Tuomas Honkala\n\n");
        System.out.println("Available command line commands:\n\n");
        System.out.println("-x <10...2000> | width (units of 10) of each floor ");
        System.out.println("-y <10...2000> | height (units of 10) of each floor ");
        System.out.println("-z <1...10000> | number of floors");
        System.out.println("-density <10...100> | room density (100 = very closely packed rooms)");
        System.out.println("-interconnectivity <1...100> | room interconnectivity (100 = as interconnected as possible)");
        System.out.println("-straightness <50...100> | higher the value, the straighter the passageways");
        System.out.println("-deadendiness <1...100> | higher the value the more dead-ends are added)");
        System.out.println("-supersize | try to create as many large rooms as possible");
        System.out.println("-long | try to create a sprawling dungeon with long winding tunnels.");
        System.out.println("-random | use a RANDOM seed");
        System.out.println("-seed <seed_word> | type in a seed for the dungeon");
        System.out.println("-speedtest | run as performance test only, no dungeon.txt written!");
        System.out.println("\n\nNOTE: Unless -random or -seed commands are used, the dungeon will be generated using the default (hardcoded) seed.");
        System.out.println("\nIf not parameters are specified by the user, default parameters will be used.");
    
    }
    
    public static Specification createSpecification(String[] args) {
           int x = 140;
        int y = 100;
        int z = 5;
        int deadEndiness=5;
        int roomConnectivity=1;
        int pivotSeekPersistence=4;
        int density = 88;
        int passageStraightness = 80;
        int interconnectivity = 10;
        int deadendiness = 50;
        boolean supersizeRooms = false;
        boolean longCorridors = false;
        boolean interConnected = false;
        boolean useRandomSeed=false;
        boolean speedTest=false;
        
        String seed = "Tähän kirjoitetusta lauseesta muodostettu siemenluku määrää minkälainen dungeon generoidaan kunhan muut parametrit säilyvät samoina.";
        
        for (int i = 0; i<args.length; i++) {
            boolean hasNext = i<args.length-1;
             if (args[i].contains("-seed") && hasNext) {
                    seed = args[i+1];
            }
             if (args[i].contains("-random")) {
                    useRandomSeed=true;
            }
            if (args[i].contains("-x") && hasNext) {
                    x = Integer.parseInt(args[i+1])*10;
                    x = Math.max(50, Math.min(20000, x));
            }
            if (args[i].contains("-y") && hasNext) {
                    y = Integer.parseInt(args[i+1])*10;
                    y = Math.max(50, Math.min(20000, y));
            }
            if (args[i].contains("-z") && hasNext) {
                    z = Integer.parseInt(args[i+1]);
                    z = Math.max(1, Math.min(10000, z));
            }
            if (args[i].contains("-density") && hasNext) {
                    density = Integer.parseInt(args[i+1]);
                    density = Math.max(10, Math.min(100, density));
            }
             if (args[i].contains("-straight") && hasNext) {
                    passageStraightness = Integer.parseInt(args[i+1]);
                    passageStraightness = Math.max(50, Math.min(100, passageStraightness));
            }
            
            if (args[i].contains("-intercon") && hasNext) {
                    interconnectivity = Integer.parseInt(args[i+1]);
                    interconnectivity = Math.max(1, Math.min(100, interconnectivity));
            }
            if (args[i].contains("-deadend") && hasNext) {
                    deadendiness = Integer.parseInt(args[i+1]);
                    deadendiness = Math.max(1, Math.min(100, deadendiness));
            }
            if (args[i].contains("-supersize")) {
                    supersizeRooms=true;
            }
            if (args[i].contains("-long")) {
                    longCorridors=true;
            }
            if (args[i].contains("-speedtest")) {
                    speedTest=true;
            }
        }
        System.out.println("Spec: "+x+","+y+", "+z);
        Specification spec = new Specification(x,y,z);
        spec.density=(int)(((x/10)*(y/10))/3);//*((density+1)/10));
        spec.volatility=(int)((x/10)*(y/10)/4);//*((density+1)/10));
        spec.roomDensity=density;//Math.max(Math.min(2, (int)((double)(density/50))*24),20);
        System.out.println("density: "+spec.density+", volatility: "+spec.volatility+", roomDensity: "+spec.roomDensity);
        if (speedTest) {
            spec.speedTest=true;
        }
        
        if (supersizeRooms) {
            spec.twoByTwos=Specification.SEMI_COMMON;
            spec.threeByThrees=Specification.VERY_COMMON;
            spec.twoByOnes=Specification.RARE;
            spec.largeRoomPersistence=5;
        }
        if (longCorridors) {
            spec.pivotSeekPersistence=(x+y)/4;
            spec.roomDensity=Math.min(15, spec.roomDensity);
            spec.largeRoomPersistence=-2+(x+y)/4;
            spec.midsizeRoomPersistence=-1+(x+y)/4;
        }
        spec.roomConnectivity=interconnectivity/10;
        spec.deadEndiness=(int)(x+y/25)*(deadendiness/100);
        spec.passageStraightnessPercentile=passageStraightness;
        
        spec.setSeed(seed);
        if (useRandomSeed) {
            spec.randomi=new Random(System.currentTimeMillis());
        }
        spec.fileWritingOnly=true;
        
        return spec;
    }
}
