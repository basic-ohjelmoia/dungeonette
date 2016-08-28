/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Room;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

/**
 *
 * Class used to decorate newly constructed rooms with random items, furniture
 * and such.
 *
 * This class is intended purely as proof of concept, there is no actual deep
 * logic behind what sort items would be actually placed.
 *
 */
public class RoomDecorator {

    /**
     * This method is run once every time a new room is constructed. The higher
     * the room.id number is, the more likely the room is going to contain
     * items.
     *
     * @param room Room being decorated
     */
    public static void decorate(Room room) {

        Random randomi = room.getRandom();

        char[][] shape = room.getShape();
        char[][] items = room.getItems();
        boolean[][] illegals = new boolean[room.dimension.width][room.dimension.height];

        for (Point pt : room.getDoorwayArray()) {
            if (pt != null) {

                illegals[pt.x - (room.location.x * 10)][pt.y - (room.location.y * 10)] = true;
            }
        }

        Dimension dimension = room.dimension;
        int size = (dimension.height * dimension.width) / 5;
        int itemsGenerated = 0;

        for (int y = 1; y < dimension.height - 1; y++) {
            for (int x = 1; x < dimension.width - 1; x++) {
                if (isLegal(x, y, dimension, shape, items, illegals)) {
                    if (randomi.nextInt(100 + (size+(size/2)) + (Math.min(room.id / 3, 15)))
                            > 95 - (room.id/3) + randomi.nextInt(1+(itemsGenerated * 2))) {

                        int arpa = randomi.nextInt(6);
                        itemsGenerated++;

                        if (arpa == 0 && room.id < 20) {
                            items[x][y] = '$';
                        } else if (arpa == 0) {
                            items[x][y] = 'Æ';
                        } else if (arpa == 1) {
                            items[x][y] = '€';
                        } else if (arpa == 2) {
                            items[x][y] = '£';
                        } else if (arpa == 3) {
                            items[x][y] = '%';
                        } else if (arpa == 4) {
                            items[x][y] = '§';
                        } else if (arpa == 5) {
                            items[x][y] = '½';
                        }

                        if (randomi.nextInt(5) != 0) {
                            illegals[x][y] = true;
                        }

                    }

                }

            }
        }

    }

    /**
     * Checks wether a tile is a legal position for a POSSIBLE item placement.
     *
     * Currently, a legal position ALMOST always equals a floor tile next to a wall.
     *
     * @param x x position INSIDE the room
     * @param y y position INSIDE the room
     * @param dimensnion Dimensions of the room
     * @param shape tiles of the room
     * @param items items of the room
     * @param illegals coordinates where now new items are allowed
     * @return if item placement is legal, then true, otherwise false
     */
    private static boolean isLegal(int x, int y, Dimension dimension, char[][] shape, char[][] items, boolean[][] illegals) {

        if (!(shape[x + 1][y] == '#' || shape[x - 1][y] == '#' || shape[x][y + 1] == '#' || shape[x][y - 1] == '#')) {
            return false;
        }

        if (shape[x][y] == '+' && !illegals[x][y]
                && !illegals[x][y + 1] && !illegals[x][y - 1] && !illegals[x + 1][y] && !illegals[x - 1][y]) {
            return true;
        }

        if (x > 2 && y > 2 && x < dimension.width - 2 && y < dimension.height - 2) {
            boolean allCleanFloorTiles = true;
            for (int sy = y - 2; sy < y + 2; sy++) {
                for (int sx = x - 2; sx < x + 2; sx++) {
                    if (shape[sx][sy] != '+') {
                        allCleanFloorTiles = false;
                    }
                    if (Math.abs(x-sx)<=1 && Math.abs(y-sy)<=1 && illegals[sx][sy]) {
                        allCleanFloorTiles = false;
                    }
                }

            }
            if (allCleanFloorTiles) {
                return true;
            }
        }

        return false;
    }
}
