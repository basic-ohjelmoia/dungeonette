/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Floor;
import dungeonette.domain.Room;
import dungeonette.domain.Specification;
import java.io.FileWriter;

/**
 *
 * Prints out the finished dunngeon, floor by floor
 */
public class DungeonPrinter {

    /**
     * Prints out the finished floor.
     * @param floor floor being printed
     * @param next the floor under the floor
     * @param spec specification of the dungeon containing the floor
     * @return Array of Strings containing the floor map
     */
    public static String[] printFloor(Floor floor, Floor next, Specification spec) {
        String[] output = new String[spec.maxY+1];
        String floorHeader = "\n\n======================= DUNGEON LEVEL "+floor.level+" ==============================\n\n"+
        "::: map key ::: ## wall ::: ++ floor ::: ||   == doors ::: ½ $ € % £ § generic items ::: Æ boss ::: <> room reachable ::: >< room unreachable :::  : shape of the room below\n\n";
        output[0]=floorHeader;
        System.out.println(floorHeader);
        if (next==null) {next=floor;}
        
        
      
        char[][] tiles = floor.getTiles();
        for (int y = 0; y < spec.maxY; y++) {
            
            String line = "";
            for (int x = 0; x < spec.maxX; x++) {

                if (tiles[x][y] == 0) {
                    line+="..";
                    //System.out.print("..");
                }
//                else if (floor.debugTiles[x][y]!=0) {
//                            System.out.print("+¤");
//                } 
                else {

                    Room room = floor.roomLayout[x / 10][y / 10];

                    if (room != null && tiles[x][y] == '+' && x % 10 == 4 && y % 10 == 4) {

                        if (floor.getRoomCount() == room.id) {
                            line+="EXIT";
                            //System.out.print("EXIT");
                            x++;
                        } else if (room.id == 1) {
                            line+="ENTR";
                            //System.out.print("ENTR");
                            x++;
                        } else {
                            if (room.id < 10) {
                                line+="0";
                                //System.out.print("0");
                            }
                            line+=room.id;
                            if (room.id>99) {
                                if (room.id<1000) {
                                line+=tiles[x+1][y];
                                }
                               x++;
                               
                            }
//                            System.out.print(room.id);
                        }

                    } else {
                        if (room != null && tiles[x][y] == '+' && x % 10 == 4 && y % 10 == 5) {
                            
                            // Rooms which are connected to the floor entrance are marked with a diamond <>.
                            // Unconnected rooms (which shouldn't even exist!!) are marked with an X  >< 
                            if (floor.connected[room.id]) {
                                line+="<>";
                                //System.out.print("<>");
                            } else {
                                line+="><";
                                //System.out.print("><");
                            }
                        } else {
                            char first = tiles[x][y];
                            char second = floor.getItems()[x][y];
                          
                            if (first=='.' && next.getTiles()[x][y]!='.') {
                                first=':';
                            }
                            
                            if (room!=null) {// && (x+y)%2==0) {
                                if (room.id==1 && first=='+') {
                                        first='°';
                                }
                                if (room.id==floor.getRoomCount() && first=='+') {
                                        first='°';
                                }
                            }
                            
                            if (second==0) {
                                second=first;
                            } 
                            line+=first+""+second;
                            //System.out.print(first+""+second);
                            

                        }
                    }
                }
            }
            line+="\n";
            if (!spec.fileWritingOnly) {
            System.out.print(line);
            }
            output[y+1]=line;
        }
        return output;
    }
}
