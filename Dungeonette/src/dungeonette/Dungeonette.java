/*
 *
 */
package dungeonette;

import dungeonette.domain.Environment;
import dungeonette.domain.Specification;
import java.util.Scanner;

/**
 * Dungeonette is a program which generates random dungeons for an unnamed
 * tiled-based role playing game I'm working on.
 *
 *
 * @author Tuomas Honkala
 */
public class Dungeonette {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String cmd = "";
        int x = 140;
        int y = 100;
        int z = 5;
        int deadEndiness=5;
        int roomConnectivity=1;
        int pivotSeekPersistence=4;

        String seed = "Tähän kirjoitetusta lauseesta muodostettu siemenluku määrää minkälainen dungeon generoidaan kunhan muut parametrit säilyvät samoina.";

        while (true) {
            System.out.println("==================DUNGEONETTE==================");
            System.out.println("Type in dungeon floor width (default: 14) OR press ENTER at any point to skip ahead with a default dungeon!");
            cmd = scanner.nextLine();
            if (cmd.length() > 0) {
                x = Integer.parseInt(cmd) * 10;
            } else {
                break;
            }
            System.out.println("Type in dungeon floor heigth (default: 10)");
            cmd = scanner.nextLine();
            if (cmd.length() > 0) {
                y = Integer.parseInt(cmd) * 10;
            } else {
                break;
            }
            System.out.println("Type in number of dungeon floors (default: 5)");
            cmd = scanner.nextLine();
            if (cmd.length() > 0) {
                z = Integer.parseInt(cmd);
            } else {
                break;
            }
//            System.out.println("Want lots of dead ends? (yes/no)");
//            cmd = scanner.nextLine();
//            if (cmd.contains("y")) {
//               deadEndiness=15;  
//            } 
//            System.out.println("Want lots of interconnecting passageways? (yes/no)");
//            cmd = scanner.nextLine();
//            if (cmd.contains("y")) {
//               roomConnectivity=6;  
//            } 
//            System.out.println("Want to spread the dungeon out? (yes/no)");
//            cmd = scanner.nextLine();
//            if (cmd.contains("y")) {
//               pivotSeekPersistence=7;
//            }
            System.out.println("Type in something to create dungeon seed (or press enter to use default seed)");
            cmd = scanner.nextLine();
            if (cmd.length() > 0) {
                seed = cmd;
            }
            break;
        }

        Specification spec = new Specification(x, y, z); // dungeon specification 
        spec.setSeed(seed);
        spec.deadEndiness=deadEndiness;
        spec.roomConnectivity=roomConnectivity;
        //spec.pivotSeekPersistence=pivotSeekPersistence;
        
        Environment env = new Environment(spec);    // the environment for the dungeon generation
        env.generateFloors(); // go-code

    }

}
