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
public class Room {
    
    public Dimension dimension;
    public int id;
    public char fromDirection;
    public char outDirection;
    
    public Room(Dimension dimension, int newID, char from) {
        this.dimension=dimension;
        this.id=newID;
        this.fromDirection=from;
    }
}
