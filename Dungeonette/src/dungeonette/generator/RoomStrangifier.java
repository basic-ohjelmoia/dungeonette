/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.generator;

import dungeonette.domain.Room;
import java.awt.Point;
import java.util.Random;

/**
 *
 * This class gives an alternative shape for a room.
 * The larger the room, the "stranger" the new shape should end up being.
 */
public class RoomStrangifier {

 /**
  * Reshapes a room.
  * @param room room being reshaped
  */
    public static void reshape(Room room) {
        System.out.println("room " + room.id + " strangified!!!!!!!");
        int xMax = room.dimension.width;
        int yMax = room.dimension.height;
        room.resetArea();

        for (int y = 0; y < yMax; y++) {
            for (int x = 0; x < xMax; x++) {
                room.getShape()[x][y] = '.';
            }
        }

        int shapes = 8;
        Random randomi = room.getRandom();

        int prevX = room.dimension.width / 3;
        int prevY = room.dimension.height / 3;
        int sx = -1;
        int sx2 = -1;
        int sy = -1;
        int sy2 = -1;
        while (shapes > 0) {

            while (true) {
                sx = randomi.nextInt(xMax - 3);
                sy = randomi.nextInt(yMax - 3);

                sx2 = xMax - randomi.nextInt(xMax - sx);
                sy2 = yMax - randomi.nextInt(yMax - sy);
                if (sx <= prevX && sx2 >= prevX && sy <= prevY && sy2 >= prevY) {
                    break;
                }
            }
            prevX = sx + 1;
            prevY = sy + 1;

            room.getDoorwayArray()[shapes] = new Point(prevX + (room.location.x * 10), prevY + (room.location.y * 10));
            room.getDoorwayArray()[0] = room.getDoorwayArray()[shapes];
            room.getDoorwayArray()[9] = room.getDoorwayArray()[shapes];

            for (int y = 1; y < yMax - 1; y++) {
                for (int x = 1; x < xMax - 1; x++) {
                    if (x >= sx && x <= sx2 && y >= sy && y <= sy2) {
                        room.getShape()[x][y] = '+';
                        room.addArea();
                    }
                }
            }
            shapes--;
        }

        for (int y = 1; y < yMax - 1; y++) {
            for (int x = 1; x < xMax - 1; x++) {

                if (room.getShape()[x][y] == '+') {

                    for (int by = -1; by <= 1; by++) {
                        for (int bx = -1; bx <= 1; bx++) {
                            if (room.getShape()[x + bx][y + by] == '.') {
                                room.getShape()[x + bx][y + by] = '#';
                            }
                        }
                    }

                }
            }
        }

    }

}
