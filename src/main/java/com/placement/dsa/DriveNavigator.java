package com.placement.dsa;

import com.placement.model.Drive;
import java.util.List;

/**
 * DSA: Doubly Linked List (DLL)
 * Member: Shashank
 *
 * Each node stores a Drive object and has both a 'next' and 'prev' pointer,
 * enabling bidirectional traversal (Prev ← → Next).
 * Used in AdminDashboard to browse drives one-by-one like a navigator.
 */
public class DriveNavigator {

    // ---- Inner Node class ----
    private static class Node {
        Drive data;
        Node  prev;
        Node  next;

        Node(Drive data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node head;    // first drive (leftmost)
    private Node tail;    // last drive  (rightmost)
    private Node current; // currently viewed node
    private int  size;

    public DriveNavigator() {
        head    = null;
        tail    = null;
        current = null;
        size    = 0;
    }

    /**
     * Build the DLL from a list of Drive objects.
     * Clears any existing list first.
     * Time complexity: O(n)
     */
    public void loadDrives(List<Drive> drives) {
        head    = null;
        tail    = null;
        current = null;
        size    = 0;

        for (Drive drive : drives) {
            Node newNode = new Node(drive);
            if (head == null) {
                // First node — head and tail point to it
                head = newNode;
                tail = newNode;
            } else {
                // Append to tail and link bidirectionally
                newNode.prev = tail;
                tail.next    = newNode;
                tail         = newNode;
            }
            size++;
        }
        current = head; // start at the first drive
    }

    /**
     * Get the currently viewed drive.
     */
    public Drive getCurrent() {
        return (current != null) ? current.data : null;
    }

    /**
     * Move forward and return next drive.
     * Stays at tail if already at end.
     */
    public Drive getNext() {
        if (current != null && current.next != null) {
            current = current.next;
        }
        return (current != null) ? current.data : null;
    }

    /**
     * Move backward and return previous drive.
     * Stays at head if already at start.
     */
    public Drive getPrev() {
        if (current != null && current.prev != null) {
            current = current.prev;
        }
        return (current != null) ? current.data : null;
    }

    /** @return true if a next node exists */
    public boolean hasNext() { return current != null && current.next != null; }

    /** @return true if a previous node exists */
    public boolean hasPrev() { return current != null && current.prev != null; }

    /** Reset cursor to the first drive (head) */
    public void reset()      { current = head; }

    public int     size()    { return size; }
    public boolean isEmpty() { return size == 0; }
}
