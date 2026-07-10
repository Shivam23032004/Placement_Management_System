package com.placement.dsa;

import java.util.ArrayList;
import java.util.List;

/**
 * DSA: Queue (FIFO — First In First Out)
 * Member: Shashank
 *
 * Custom queue implemented using a singly-linked Node structure.
 * Each node holds a pending application entry (studentId + driveId).
 * Enqueue → adds to rear. Dequeue → removes from front.
 * DB persistence is handled separately by ApplicationQueueDAO.
 */
public class ApplicationQueue {

    // ---- Inner Node class ----
    private static class Node {
        int studentId;
        int driveId;
        String studentName;
        String driveName;
        Node next;

        Node(int studentId, int driveId, String studentName, String driveName) {
            this.studentId   = studentId;
            this.driveId     = driveId;
            this.studentName = studentName;
            this.driveName   = driveName;
            this.next        = null;
        }
    }

    private Node front; // dequeue from here (oldest entry)
    private Node rear;  // enqueue here   (newest entry)
    private int  size;

    public ApplicationQueue() {
        front = null;
        rear  = null;
        size  = 0;
    }

    /**
     * Enqueue: add a new application entry to the rear.
     * Time complexity: O(1)
     */
    public void enqueue(int studentId, int driveId, String studentName, String driveName) {
        Node newNode = new Node(studentId, driveId, studentName, driveName);
        if (rear == null) {
            // Queue is empty — front and rear both point to the new node
            front = newNode;
            rear  = newNode;
        } else {
            rear.next = newNode;
            rear      = newNode;
        }
        size++;
    }

    /**
     * Dequeue: remove and return front entry.
     * Returns int[] {studentId, driveId}, or null if empty.
     * Time complexity: O(1)
     */
    public int[] dequeue() {
        if (isEmpty()) return null;
        int[] data = { front.studentId, front.driveId };
        front = front.next;
        if (front == null) rear = null; // queue became empty
        size--;
        return data;
    }

    /**
     * Peek: see front entry without removing it.
     * Time complexity: O(1)
     */
    public String peekInfo() {
        if (isEmpty()) return "Queue is empty.";
        return "Next: " + front.studentName + "  →  Drive: " + front.driveName;
    }

    /**
     * Returns all entries in FIFO order as readable strings.
     * Time complexity: O(n)
     */
    public List<String> viewAllInOrder() {
        List<String> list = new ArrayList<>();
        Node current = front;
        int position = 1;
        while (current != null) {
            list.add(position + ".  " + current.studentName
                    + "  →  " + current.driveName
                    + "  [Student ID: " + current.studentId + "]");
            current = current.next;
            position++;
        }
        return list;
    }

    public boolean isEmpty() { return front == null; }
    public int size()        { return size; }
}
