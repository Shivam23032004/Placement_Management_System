package com.placement.dsa;

import com.placement.model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * DSA: Binary Search Tree (BST) — keyed on Student CGPA
 * Member: Shivam
 *
 * Operations:
 *   insert()        — O(log n) average, O(n) worst
 *   rangeSearch()   — O(log n + k), k = results in range
 *   getAllSorted()   — O(n) in-order traversal
 *   findTopper()    — O(log n) rightmost node
 *   deleteStudent() — O(log n) average, with 3-case BST deletion  [NEW - Shivam]
 *   getHeight()     — O(n) recursive height                        [NEW - Shivam]
 */

public class StudentBST {


    // Node class — har node ek Student hold karta hai, CGPA key hai
    private class Node {
        Student student;
        Node left, right;

        Node(Student student) {
            this.student = student;
        }
    }

    private Node root;

    // Insert a student into the BST based on CGPA
    public void insert(Student student) {
        root = insertRec(root, student);
    }

    private Node insertRec(Node node, Student student) {
        if (node == null) {
            return new Node(student);
        }

        if (student.getCgpa() < node.student.getCgpa()) {
            node.left = insertRec(node.left, student);
        } else {
            node.right = insertRec(node.right, student);
        }
        return node;
    }

    // Range Search: returns all students with CGPA between minCgpa and maxCgpa (inclusive)
    public List<Student> rangeSearch(double minCgpa, double maxCgpa) {
        List<Student> result = new ArrayList<>();
        rangeSearchRec(root, minCgpa, maxCgpa, result);
        return result;
    }

    private void rangeSearchRec(Node node, double minCgpa, double maxCgpa, List<Student> result) {
        if (node == null) return;

        // Agar current node ki CGPA min se badi hai, toh left subtree mein bhi results ho sakte hain
        if (node.student.getCgpa() > minCgpa) {
            rangeSearchRec(node.left, minCgpa, maxCgpa, result);
        }

        // Agar current node range ke andar hai, toh usse result mein add karo
        if (node.student.getCgpa() >= minCgpa && node.student.getCgpa() <= maxCgpa) {
            result.add(node.student);
        }

        // Agar current node ki CGPA max se choti ya barabar hai, toh right subtree mein bhi results ho sakte hain
        if (node.student.getCgpa() <= maxCgpa) {
            rangeSearchRec(node.right, minCgpa, maxCgpa, result);
        }
    }

    // In-order traversal: returns all students sorted by CGPA (ascending)
    public List<Student> getAllSortedByCgpa() {
        List<Student> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }

    private void inorderRec(Node node, List<Student> result) {
        if (node == null) return;
        inorderRec(node.left, result);
        result.add(node.student);
        inorderRec(node.right, result);
    }

    // Find the topper (highest CGPA) — rightmost node in BST
    public Student findTopper() {
        if (root == null) return null;
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.student;
    }

    // ---- NEW: Shivam — BST Deletion (3-case algorithm) ----

    /**
     * Delete a student from the BST by CGPA.
     * Three cases handled:
     *   Case 1: Leaf node          → simply remove
     *   Case 2: One child          → replace node with its child
     *   Case 3: Two children       → replace with inorder successor (min of right subtree)
     * Time complexity: O(log n) average
     */
    public void deleteStudent(double cgpa) {
        root = deleteRec(root, cgpa);
    }

    private Node deleteRec(Node node, double cgpa) {
        if (node == null) return null; // not found

        if (cgpa < node.student.getCgpa()) {
            node.left = deleteRec(node.left, cgpa);
        } else if (cgpa > node.student.getCgpa()) {
            node.right = deleteRec(node.right, cgpa);
        } else {
            // Found the node to delete — 3 cases:

            // Case 1: Leaf node (no children)
            if (node.left == null && node.right == null) return null;

            // Case 2a: Only right child
            if (node.left == null)  return node.right;

            // Case 2b: Only left child
            if (node.right == null) return node.left;

            // Case 3: Two children
            // Find inorder successor = smallest node in right subtree
            Node successor = getMin(node.right);
            node.student   = successor.student;
            // Delete the inorder successor from right subtree
            node.right = deleteRec(node.right, successor.student.getCgpa());
        }
        return node;
    }

    /** Helper: find the minimum node (leftmost) in a subtree */
    private Node getMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    // ---- NEW: Shivam — BST Height ----

    /**
     * Returns the height of the BST.
     * Height = number of edges on the longest root-to-leaf path.
     * Empty tree height = 0.
     * Time complexity: O(n) — visits every node once.
     */
    public int getHeight() {
        return heightRec(root);
    }

    private int heightRec(Node node) {
        if (node == null) return 0;
        int leftHeight  = heightRec(node.left);
        int rightHeight = heightRec(node.right);
        return 1 + Math.max(leftHeight, rightHeight);
    }
}