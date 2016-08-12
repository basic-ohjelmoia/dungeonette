/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.data;

import dungeonette.domain.Room;

/**
 *
 * @author mikromafia
 */
public class RoomNode {
    
    private Room room;
    private RoomNode next;
    private RoomNode previous;
        
    public RoomNode(Room room, RoomNode previous) {
        this.room=room;
        this.previous=previous;
        this.next=null;
    }
 
    public void setNext(RoomNode next) {
        this.next=next;
    }
    
    public Room getRoom() {
        return this.room;
    }
    
    public RoomNode getNext() {
        return this.next;
    }
    
    public RoomNode getPrevious() {
        return this.previous;
    }
    
    public boolean isParent() {
        if (previous==null) {
            return true;
        }
        return false;
    }
        
}
