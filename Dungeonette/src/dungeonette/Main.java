package dungeonette;

/*
 *
 */


import dungeonette.command.CommandLineInterpreter;
import dungeonette.domain.Environment;
import dungeonette.domain.Specification;
import java.util.Random;
import java.util.Scanner;

/**
 * Main is a program which generates random dungeons for an unnamed
 tiled-based role playing game I'm working on.
 *
 *
 * @author Tuomas Honkala
 */
public class Main {

    /**
     * There are many different command line arguments which can be used to control the Dungeon generation process.
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        
        for (String str : args) {
            if (str.contains("help")) {
                CommandLineInterpreter.printHelp();
                System.exit(0);
            }
        }
        
        Scanner scanner = new Scanner(System.in);
       
        int x = 160;
        int y = 120;
        int z = 3;
        
        

        String seed = "Tähän kirjoitetusta lauseesta muodostettu siemenluku määrää minkälainen dungeon generoidaan kunhan muut parametrit säilyvät samoina.";

        Specification spec = null;
        if (args.length<1) {
            spec = new Specification(x, y, z);
            spec.setSeed(seed);
        } else {
            spec = CommandLineInterpreter.createSpecification(args);
        }
         
    //    spec.randomi=new Random(System.currentTimeMillis());
        Environment env = new Environment(spec);    // the environment for the dungeon generation
        env.generateFloors(); 

    }

}
