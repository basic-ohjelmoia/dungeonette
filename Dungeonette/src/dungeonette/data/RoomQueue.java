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
public class RoomQueue {
    
    private RoomNode first;
    private RoomNode last;
    private int size;
    
    public RoomQueue() {
        this.first=null;
        this.last=null;
    }
    
    public Room front() {
        return this.first.getRoom();
    }
    
    public Room dequeue() {
         if (this.first.getRoom().hasPivots()) {
             System.out.println(this.first.getRoom().id+" had a pivot");
                return front();
        }
        
        RoomNode dequed = this.first;
        System.out.println("removed ("+size+") "+this.first.getRoom().id);
        System.out.print("after DEq: first: "+this.first.getRoom().id);
                System.out.print(", last: "+this.last.getRoom().id+"\n");
        
        if (size>1) {
            this.first=dequed.getNext();
        }
        
        else if (size==1) {
            this.first=dequed.getNext();
            this.last=dequed;
        }
        else {
            this.first=null;
            this.last=null;
            size=0;
            return null;
            
        }
        size--;
        return dequed.getRoom();
    }
    
    public void enqueue(Room room) {
        RoomNode enqued = new RoomNode(room,this.last);
        if (size==0) {this.first=enqued;}
        else if (size==1) {
            this.first.setNext(enqued);
        } else {
            this.last.setNext(enqued);
        }
        last=enqued;
        size++;
        System.out.println("after eq: ("+size+") first: "+this.first.getRoom().id+", last: "+this.last.getRoom().id);
    }
    
    public int getSize() {
        return this.size;
    }
    
    public boolean isEmpty() {
        if (this.size==0) {return true;}
        return false;
    }
}
