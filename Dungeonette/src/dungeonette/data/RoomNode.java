/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.data;

import dungeonette.domain.Room;

/**
 *
 * RoomNode is an RoomQueue'able container holding a single room object.
 * A RoomNode knows both it's parent and it's child RoomNode.
 */
public class RoomNode {
    
    /**
     * Room contained in the roomnode
     */
    private Room room;
    /**
     * Child of the roomnode
     */
    private RoomNode next;
    /**
     * Parent of the roomnode.
     */
    private RoomNode previous;
        
    /**
     * Creates new room node
     * @param room Room being contained
     * @param previous reference to the PARENT roomnode of this new roomnode
     */
    public RoomNode(Room room, RoomNode previous) {
        this.room=room;
        this.previous=previous;
        this.next=null;
    }
 
    /**
     * Sets reference to the child of this roomnode
     * @param next child of THIS roomnode
     */
    public void setNext(RoomNode next) {
        this.next=next;
    }
    
    /**
     * Returns the room contained inside the RoomNode
     * @return room object (NOT ROOMNODE!!)
     */
    public Room getRoom() {
        return this.room;
    }
    
    /**
     * Calls for the child room node
     * @return child room node
     */
    public RoomNode getNext() {
        return this.next;
    }
    
    /**
     * Calls for the parent room node
     * @return parent room node
     */
    public RoomNode getPrevious() {
        return this.previous;
    }
    
    /**
     * If the roomnode has no valid parent, then the roomnode considers itself the root of the roomqueue.
     * @return returns true if the roomnode is the root
     */
    public boolean isRoot() {
        if (previous==null) {
            return true;
        }
        return false;
    }
        
}
