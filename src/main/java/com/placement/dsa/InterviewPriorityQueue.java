package com.placement.dsa;
        
import com.placement.model.Student;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class InterviewPriorityQueue {

    // Max-Heap banaya hai: highest CGPA wala student sabse pehle bahar aayega
    private PriorityQueue<Student> queue;

    public InterviewPriorityQueue() {
        // Comparator.reverseOrder() se PriorityQueue Max-Heap ki tarah behave karti hai
        queue = new PriorityQueue<>(
            Comparator.comparingDouble(Student::getCgpa).reversed()
        );
    }

    // Ek shortlisted student ko interview queue mein daalo
    public void addToQueue(Student student) {
        queue.add(student);
    }

    // Sabse high-priority student ko queue se nikalo (interview ke liye bulao)
    public Student getNextForInterview() {
        return queue.poll();  // poll() nikalta hai aur queue se remove bhi karta hai
    }

    // Dekho agla kaun hai, bina queue se nikale
    public Student peekNext() {
        return queue.peek();
    }

    // Kitne students queue mein baaki hain
    public int remainingCount() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Poori queue ko priority order mein dikhana (bina actual queue ko modify kiye)
    public List<Student> viewFullOrder() {
        PriorityQueue<Student> copy = new PriorityQueue<>(queue);
        List<Student> ordered = new ArrayList<>();
        while (!copy.isEmpty()) {
            ordered.add(copy.poll());
        }
        return ordered;
    }
}
