package com.placement.dsa;

import java.util.ArrayList;
import java.util.List;

public class UndoStack<T> {

    private List<T> stack;

    public UndoStack() {
        stack = new ArrayList<>();
    }

    public void push(T item) {
        stack.add(item);
    }

    public T pop() {
        if (isEmpty()) return null;
        return stack.remove(stack.size() - 1);
    }

    public T peek() {
        if (isEmpty()) return null;
        return stack.get(stack.size() - 1);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}