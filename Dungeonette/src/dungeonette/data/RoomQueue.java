/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dungeonette.data;

import dungeonette.domain.Room;

/**
 * Queue for RoomNodes, with each RoomNode containg a room.
 *
 * This class has some built-in integration for the dungeon generation
 * algorithm.
 *
 * The queue revolves around the concept of pivots. A pivot is a POTENTIAL place
 * for a passageway connecting to a neighbouring room. As long as a room has
 * pivots left, it's considered "active" anc will NOT get dequeued by a
 * dequeue() call. However, each call for a dequeue() WILL deplete a single
 * pivot from the room. Once all pivots have been spent, the room will be ripe
 * for a proper dequeue.
 *
 */
public class RoomQueue {

    private RoomNode first;
    private RoomNode last;
    private int size;
    public boolean debugTelegraphInnerWorkings;

    public RoomQueue() {
        this.first = null;
        this.last = null;
    }

    /**
     * Returns the first room in the queue without removing it from the queue
     * (no pivot spent).
     *
     * @return returns the first room without removing it from the queue
     */
    public Room front() {
        return this.first.getRoom();
    }

    /**
     * TRIES to remove the first room in the queue. If the room has no more
     * pivots left, then the removal will happen. Otherwise the room will loose
     * one pivot but only get front()'ed.
     *
     * @return returns and possibly removes the first room in queue
     */
    public Room dequeue() {
        if (this.first.getRoom().hasPivots()) {
            if (debugTelegraphInnerWorkings) {
                System.out.println("DEBUG! dequeue did not happen since " + this.first.getRoom().id + " had a pivot(s) left!");
            }
            return front();
        }

        RoomNode dequed = this.first;

        if (size > 1) {
            this.first = dequed.getNext();
        } else if (size == 1) {
            this.first = dequed.getNext();
            this.last = dequed;
        } else {
            this.first = null;
            this.last = null;
            size = 0;
            return null;

        }
        size--;
        return dequed.getRoom();
    }

    /**
     * Adds a new room to the end of the queue.
     *
     * @param room room being added
     */
    public void enqueue(Room room) {
        RoomNode enqued = new RoomNode(room, this.last);
        if (size == 0) {
            this.first = enqued;
        } else if (size == 1) {
            this.first.setNext(enqued);
        } else {
            this.last.setNext(enqued);
        }
        last = enqued;
        size++;
        if (debugTelegraphInnerWorkings) {
            System.out.println("DEBUG! after enqueue the queue has size of (" + size + ") with first room id being: " + this.first.getRoom().id + ", last room id: " + this.last.getRoom().id);
        }
    }

    /**
     * Returns the size of the queue
     *
     * @return queue size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * If the queue is empty, this method will return TRUE
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        if (this.size == 0) {
            return true;
        }
        return false;
    }
}
