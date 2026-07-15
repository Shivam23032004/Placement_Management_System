package com.placement.dsa;

import com.placement.model.Student;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * DSA: Merge Sort (Divide and Conquer)
 * Member: Ayush
 *
 * Custom merge sort — does NOT use Collections.sort or Arrays.sort internally.
 * Algorithm:
 *   1. Divide the list into two halves recursively until size == 1.
 *   2. Merge the two sorted halves back together using a comparator.
 *
 * Time Complexity : O(n log n) — guaranteed in all cases
 * Space Complexity: O(n)       — extra space for temporary lists during merge
 *
 * Tracks merge step count for display in UI.
 */
public class MergeSortUtil {

    private static int stepCount = 0; // counts merge comparisons for display

    /**
     * Public entry point. Sorts a copy of the list — original is unchanged.
     *
     * @param list       List of students to sort
     * @param comparator Comparator defining sort order (e.g. CGPA descending)
     * @return New sorted list
     */
    public static List<Student> mergeSort(List<Student> list, Comparator<Student> comparator) {
        stepCount = 0; // reset before each sort
        List<Student> copy = new ArrayList<>(list);
        List<Student> result = mergeSortRec(copy, comparator);
        System.out.println("[MergeSortUtil] Sorted " + list.size()
                + " students | Merge comparisons: " + stepCount
                + " | O(n log n)");
        return result;
    }

    /**
     * Recursive divide step: split list in half and recurse.
     */
    private static List<Student> mergeSortRec(List<Student> list, Comparator<Student> comparator) {
        if (list.size() <= 1) return list; // base case

        int mid = list.size() / 2;
        List<Student> left  = mergeSortRec(new ArrayList<>(list.subList(0, mid)), comparator);
        List<Student> right = mergeSortRec(new ArrayList<>(list.subList(mid, list.size())), comparator);

        return merge(left, right, comparator);
    }

    /**
     * Merge step: combine two sorted halves into one sorted list.
     * This is where comparisons happen — we track them via stepCount.
     */
    private static List<Student> merge(List<Student> left, List<Student> right,
                                       Comparator<Student> comparator) {
        List<Student> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            stepCount++; // count each comparison
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }

        // Append any remaining elements
        while (i < left.size())  merged.add(left.get(i++));
        while (j < right.size()) merged.add(right.get(j++));

        return merged;
    }

    /**
     * Returns the number of comparison steps from the last mergeSort() call.
     * Useful for displaying algorithm stats in UI.
     */
    public static int getLastStepCount() {
        return stepCount;
    }
}
