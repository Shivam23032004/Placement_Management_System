package com.placement.dsa;

import com.placement.model.Student;
import java.util.ArrayList;
import java.util.List;

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

        // Agar current node ki CGPA max se choti hai, toh right subtree mein bhi results ho sakte hain
        if (node.student.getCgpa() < maxCgpa) {
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
}