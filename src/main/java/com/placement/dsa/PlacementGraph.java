package com.placement.dsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * DSA: Graph (Adjacency List) + BFS (Breadth-First Search)
 * Member: Ayush
 *
 * Represents the placement network as a directed graph:
 *   - Nodes  : students (identified by studentId) + companies (by companyId)
 *   - Edges  : an application = a directed edge  student → company
 *
 * Adjacency list representation using HashMap<Integer, List<Integer>>.
 *
 * BFS is used to:
 *   1. Find all companies a given student has applied to.
 *   2. Find all students who applied to the same companies (degree-2 BFS).
 *
 * Time Complexity: BFS = O(V + E)  where V = nodes, E = edges (applications)
 * Space Complexity: O(V + E) for adjacency list storage
 */
public class PlacementGraph {

    // studentId  → list of companyIds they applied to
    private final Map<Integer, List<Integer>> studentToCompanies;

    // companyId  → list of studentIds who applied
    private final Map<Integer, List<Integer>> companyToStudents;

    public PlacementGraph() {
        studentToCompanies = new HashMap<>();
        companyToStudents  = new HashMap<>();
    }

    /** Add a student node (if not already present) */
    public void addStudentNode(int studentId) {
        studentToCompanies.putIfAbsent(studentId, new ArrayList<>());
    }

    /** Add a company node (if not already present) */
    public void addCompanyNode(int companyId) {
        companyToStudents.putIfAbsent(companyId, new ArrayList<>());
    }

    /**
     * Add a directed edge: student → company (represents one application).
     * Also adds the reverse mapping (company → student) for easy lookup.
     */
    public void addEdge(int studentId, int companyId) {
        studentToCompanies.computeIfAbsent(studentId, k -> new ArrayList<>()).add(companyId);
        companyToStudents.computeIfAbsent(companyId,  k -> new ArrayList<>()).add(studentId);
    }

    /**
     * BFS from a student node — returns all company IDs reachable (applied to).
     * Uses a Queue internally for BFS level-order traversal.
     * Time complexity: O(V + E)
     */
    public List<Integer> bfsCompaniesFromStudent(int studentId) {
        List<Integer> visited = new ArrayList<>();
        Queue<Integer> bfsQueue = new LinkedList<>(); // BFS queue
        Set<Integer>   seen     = new HashSet<>();

        bfsQueue.add(studentId);
        seen.add(studentId);

        while (!bfsQueue.isEmpty()) {
            int current = bfsQueue.poll();
            List<Integer> neighbors = studentToCompanies.getOrDefault(current, new ArrayList<>());
            for (int companyId : neighbors) {
                if (!seen.contains(companyId)) {
                    seen.add(companyId);
                    visited.add(companyId);
                    // (companies don't have outgoing edges in this graph)
                }
            }
        }
        return visited;
    }

    /**
     * Degree-2 BFS: find all students who applied to the same companies as the given student.
     * Path: studentId → company → other students
     * Time complexity: O(V + E)
     */
    public List<Integer> getRelatedStudents(int studentId) {
        List<Integer> related = new ArrayList<>();
        Set<Integer>  seen    = new HashSet<>();
        seen.add(studentId);

        List<Integer> appliedCompanies = studentToCompanies.getOrDefault(studentId, new ArrayList<>());
        for (int companyId : appliedCompanies) {
            List<Integer> others = companyToStudents.getOrDefault(companyId, new ArrayList<>());
            for (int otherId : others) {
                if (!seen.contains(otherId)) {
                    seen.add(otherId);
                    related.add(otherId);
                }
            }
        }
        return related;
    }

    /** Full adjacency list: studentId → list of companyIds */
    public Map<Integer, List<Integer>> getAdjacencyList() { return studentToCompanies; }

    public int studentCount() { return studentToCompanies.size(); }
    public int companyCount() { return companyToStudents.size(); }
    public int edgeCount() {
        return studentToCompanies.values().stream().mapToInt(List::size).sum();
    }
}
