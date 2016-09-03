/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Floor;
import dungeonette.domain.Specification;

/**
 *
 * This class places doorways into the dungeon after the passages have been
 * carved.
 *
 */
public class DoorInserter {

    /**
     * This method adds doorways into the passages after passagecarving has been
     * processed.
     *
     * @param floor floor being processed
     * @param spec specification for the dungeon containing the floor
     */
    public static void processAllDoors(Floor floor, Specification spec) {

        char[][] tiles = floor.getTiles();
     
        for (int y = 1; y < spec.maxY-1; y++) {

            for (int x = 1; x < spec.maxX-1; x++) {
                if (floor.getDoorTiles()[x][y] == 1) {
                    if (tiles[x + 1][y] == '#' && tiles[x - 1][y] == '#' && tiles[x][y - 1] == '+' && tiles[x][y + 1] == '+') {
                        tiles[x][y] = '=';
                    } else if (tiles[x][y - 1] == '#' && tiles[x][y + 1] == '#' && tiles[x + 1][y] == '+' && tiles[x - 1][y] == '+') {
                        tiles[x][y] = '|';
                    }
                }
            }
        }
    }
}
