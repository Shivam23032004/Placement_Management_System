package com.placement.dsa;

import com.placement.model.Student;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudentHashIndex {

    // student_id -> Student object (fast lookup by roll number)
    private Map<Integer, Student> idIndex;

    // email -> Student object (fast lookup by email, useful for login)
    private Map<String, Student> emailIndex;

    // Duplicate check ke liye (e.g. duplicate application detect karna)
    private Set<String> emailSet;

    public StudentHashIndex() {
        idIndex = new HashMap<>();
        emailIndex = new HashMap<>();
        emailSet = new HashSet<>();
    }

    // Database se aaye saare students ko ek saath index mein daalo
    public void buildIndex(List<Student> students) {
        for (Student s : students) {
            idIndex.put(s.getStudentId(), s);
            emailIndex.put(s.getEmail(), s);
            emailSet.add(s.getEmail());
        }
    }

    // Ek naya student add karo index mein (jab database mein bhi add ho)
    public void addStudent(Student student) {
        idIndex.put(student.getStudentId(), student);
        emailIndex.put(student.getEmail(), student);
        emailSet.add(student.getEmail());
    }

    // O(1) average time lookup by student ID (roll number)
    public Student findById(int studentId) {
        return idIndex.get(studentId);
    }

    // O(1) average time lookup by email
    public Student findByEmail(String email) {
        return emailIndex.get(email);
    }

    // Check karo email already exist karta hai ya nahi (duplicate registration rokne ke liye)
    public boolean emailExists(String email) {
        return emailSet.contains(email);
    }

    public int totalStudents() {
        return idIndex.size();
    }
}