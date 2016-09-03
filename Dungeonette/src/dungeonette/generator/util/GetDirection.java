/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator.util;

import dungeonette.domain.Specification;
import dungeonette.generator.Architect;

/**
 *
 * Small class for fetching a seek direction to the Architect.
 */
public class GetDirection {

    /**
     * Returns the course for the Architect seeker routine while reversing the
     * course if the floor boundary has been met.
     *
     * @param cx seeker x coordinate (coarse)
     * @param cy seeker y coordinate (coarse)
     * @param directionalInteger directional integer of the Architect.Dir enum
     * @param spec Specification of the dungeon
     * @return The Direction for the seeker to continue towards
     */
    public static Architect.Dir whileBeindMindfulOfTheBoundary(int cx, int cy, int directionalInteger, Specification spec) {
        Architect.Dir direction = Architect.Dir.getDir(directionalInteger);
        if (direction == Architect.Dir.NORTH && cy <= 0) {
            direction = Architect.Dir.SOUTH;

        }
        if (direction == Architect.Dir.SOUTH && cy >= spec.gridY - 1) {
            direction = Architect.Dir.NORTH;

        }
        if (direction == Architect.Dir.WEST && cx <= 0) {
            direction = Architect.Dir.EAST;

        }
        if (direction == Architect.Dir.EAST && cx >= spec.gridX - 1) {
            direction = Architect.Dir.WEST;

        }

        return direction;

    }

    public static Architect.Dir forADetour(Architect.Dir direction, Specification spec) {
        boolean headsOrTails = spec.randomi.nextBoolean();

        Architect.Dir detourDirection = direction;
        if (direction == Architect.Dir.EAST || direction == Architect.Dir.WEST) {
            if (headsOrTails) {
                detourDirection = Architect.Dir.NORTH;
            } else {
                detourDirection = Architect.Dir.SOUTH;
            }
        }
        if (direction == Architect.Dir.NORTH || direction == Architect.Dir.SOUTH) {
            if (headsOrTails) {
                detourDirection = Architect.Dir.WEST;
            } else {
                detourDirection = Architect.Dir.EAST;
            }
        }
        return detourDirection;
    }
}
