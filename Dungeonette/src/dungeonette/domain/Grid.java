/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.domain;

import java.awt.Dimension;

/**
 *
 * @author tuoma
 */
public class Grid {

    private boolean[][] used;
    public int[][] roomID;
    private boolean quarterSpent;
    private boolean halfSpent;
    private boolean fullSpent;
    private int quadrantCounter;

    public enum Size {

        FULL,
        HALF,
        QUARTER;
    }

//    public enum Quadrant {
//                
//                NE,NW,SE,SW;
//    }
    public Grid() {
        this.used = new boolean[2][2];
        this.roomID = new int[2][2];
    }

    public boolean insertRoomIntoGrid(String quadrants, int id) {

        if (quadrantCounter >= 4) {
            return false;
        }

        boolean nw = false;
        boolean ne = false;
        boolean sw = false;
        boolean se = false;
        boolean smallRoom = false;
        if (quadrants.length() == 1) {
            smallRoom = true;
            quadrants += " ";
        }

        int failedQuadrants = 0;

        for (int i = 0; i < quadrants.length(); i += 2) {
            char q1 = quadrants.charAt(i);
            char q2 = quadrants.charAt(i + 1);

            if ((q1 == 'n' && q2 == 'w') || (q1 == 'n' && q2 == ' ') || (q1 == 'w' && q2 == ' ')) {
                if (this.used[0][0]) {
                    failedQuadrants++;
                } else {
                    nw = true;
                    if (smallRoom) {
                        q1 = ' ';
                    }
                }

            }
            if ((q1 == 'n' && q2 == 'e') || (q1 == 'n' && q2 == ' ') || (q1 == 'e' && q2 == ' ')) {
                if (this.used[1][0]) {
                    failedQuadrants++;
                } else {
                    ne = true;
                    if (smallRoom) {
                        q1 = ' ';
                    }
                }
            }
            if ((q1 == 's' && q2 == 'w') || (q1 == 's' && q2 == ' ') || (q1 == 'w' && q2 == ' ')) {
                if (this.used[0][1]) {
                    failedQuadrants++;
                } else {
                    sw = true;
                    if (smallRoom) {
                        q1 = ' ';

                    }
                }
            }
            if ((q1 == 's' && q2 == 'e') || (q1 == 's' && q2 == ' ') || (q1 == 'e' && q2 == ' ')) {
                if (this.used[1][1]) {
                    failedQuadrants++;
                } else {
                    se = true;
                    if (smallRoom) {
                        q1 = ' ';
                    }
                }
            }

        }
        
        if (failedQuadrants>(4-(quadrants.length()/2))) {
            return false;
        }
        
        if (nw) {
            this.used[0][0] = true;
            this.roomID[0][0] = id;
            quadrantCounter++;
        }
        if (ne) {
            this.used[1][0] = true;
            this.roomID[1][0] = id;
            quadrantCounter++;
        }
        if (sw) {
            this.used[0][1] = true;
            this.roomID[0][1] = id;
            quadrantCounter++;
        }
        if (se) {
            this.used[1][1] = true;
            this.roomID[1][1] = id;
            quadrantCounter++;
        }

        return true;

    }

}
