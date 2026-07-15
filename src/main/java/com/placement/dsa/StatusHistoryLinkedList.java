package com.placement.dsa;

import com.placement.model.StatusHistoryEntry;
import java.util.ArrayList;
import java.util.List;

public class StatusHistoryLinkedList {

    private class Node {
        StatusHistoryEntry entry;
        Node next;

        Node(StatusHistoryEntry entry) {
            this.entry = entry;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public void addStatusChange(StatusHistoryEntry entry) {
        Node newNode = new Node(entry);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public List<String> getTimeline() {
        List<String> timeline = new ArrayList<>();
        Node current = head;
        int step = 1;
        while (current != null) {
            timeline.add(step + ". " + current.entry.getStatus() + " (on " + current.entry.getChangedAt() + ")");
            current = current.next;
            step++;
        }
        return timeline;
    }

    public int size() {
        return size;
    }
}