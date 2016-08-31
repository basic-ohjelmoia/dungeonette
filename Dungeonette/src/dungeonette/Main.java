package dungeonette;

/*
 *
 */


import dungeonette.command.CommandLineInterpreter;
import dungeonette.domain.Environment;
import dungeonette.domain.Specification;
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
       
        int x = 240;
        int y = 140;
        int z = 5;
       

        String seed = "Tähän kirjoitetusta lauseesta muodostettu siemenluku määrää minkälainen dungeon generoidaan kunhan muut parametrit säilyvät samoina.";

        Specification spec = null;
        if (args.length<1) {
            spec = new Specification(x, y, z);
            spec.setSeed(seed);
        } else {
            spec = CommandLineInterpreter.createSpecification(args);
        }
         
        Environment env = new Environment(spec);    // the environment for the dungeon generation
        env.generateFloors(); // go-code

    }

}
