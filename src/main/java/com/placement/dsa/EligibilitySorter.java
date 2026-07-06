package com.placement.dsa;

import com.placement.model.Drive;
import com.placement.model.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EligibilitySorter {

    // Ek drive ke liye eligible students filter karo, aur CGPA descending order mein rank karo
    public List<Student> getEligibleStudentsRanked(List<Student> allStudents, Drive drive) {
        List<Student> eligible = new ArrayList<>();

        for (Student s : allStudents) {
            if (isEligible(s, drive)) {
                eligible.add(s);
            }
        }

        // Custom comparator: highest CGPA pehle (descending order)
        eligible.sort(Comparator.comparingDouble(Student::getCgpa).reversed());

        return eligible;
    }

    // Eligibility check: branch match + CGPA cutoff + backlog limit
    public boolean isEligible(Student student, Drive drive) {
        boolean cgpaOk = student.getCgpa() >= drive.getMinCgpa();
        boolean backlogOk = student.getBacklogCount() <= drive.getMaxBacklogs();
        boolean branchOk = drive.getEligibleBranches() != null &&
                            drive.getEligibleBranches().contains(student.getBranch());

        return cgpaOk && backlogOk && branchOk;
    }

    // Multi-key sort: pehle CGPA (descending), fir backlog count (ascending) tie-breaker ke liye
    public List<Student> rankByMultipleCriteria(List<Student> students) {
        List<Student> sorted = new ArrayList<>(students);

        sorted.sort(
            Comparator.comparingDouble(Student::getCgpa).reversed()
                      .thenComparingInt(Student::getBacklogCount)
        );

        return sorted;
    }

    // Branch-wise students group karke count karna (statistics ke liye useful)
    public void printBranchWiseCount(List<Student> students) {
        java.util.Map<String, Integer> branchCount = new java.util.HashMap<>();

        for (Student s : students) {
            branchCount.merge(s.getBranch(), 1, Integer::sum);
        }

        for (var entry : branchCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " students");
        }
    }
}