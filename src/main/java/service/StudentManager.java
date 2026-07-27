package service;

import exception.StudentNotFoundException;
import model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores and retrieves Student objects (composition).
 *
 * Single responsibility: managing the student collection.
 * It does NOT calculate statistics and does NOT print reports -
 * see StudentAverageCalculator and StudentReportPrinter for those.
 *
 * Storage design: the array remains the source of truth for insertion
 * order (matching the original Lab 1 requirement to use arrays), while
 * a HashMap is maintained alongside it purely for fast lookup by ID.
 * findStudent() went from O(n) linear search to O(1) average-case
 * lookup - the array still gets scanned for getAllStudents() and
 * getAllStudentIds(), where visiting every student in order is the
 * actual goal, and a HashMap would offer no benefit there since you'd
 * have to touch every entry regardless.
 */
public class StudentManager {

    private Student[] students = new Student[50];
    private int studentCount = 0;

    /**
     * Keyed by student ID (case-insensitive, stored lowercase) for O(1)
     * average-case lookup. Kept in sync with the array on every add.
     */
    private final Map<String, Student> studentsById = new HashMap<>();

    public boolean addStudent(Student student) {
        if (studentCount >= students.length) {
            System.out.println("Cannot add student, the list is full.");
            return false;
        }

        students[studentCount] = student;
        studentCount = studentCount + 1;
        studentsById.put(student.getStudentId().toLowerCase(), student);
        return true;
    }

    /**
     * O(1) average-case lookup by student ID (case-insensitive), using
     * the HashMap - a significant improvement over the previous O(n)
     * linear scan through the array, especially as the roster grows.
     *
     * @throws StudentNotFoundException if no student with this ID is registered
     */
    public Student findStudent(String studentId) throws StudentNotFoundException {
        Student student = studentsById.get(studentId.toLowerCase());
        if (student == null) {
            throw new StudentNotFoundException("Student with ID '" + studentId + "' not found in the system.");
        }
        return student;
    }

    public int getStudentCount() {
        return studentCount;
    }

    /**
     * Returns all currently registered students, in registration order.
     * Deliberately reads from the array, not the HashMap - HashMap does
     * not guarantee any particular iteration order, and registration
     * order is what callers of this method actually expect.
     */
    public List<Student> getAllStudents() {
        List<Student> result = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            result.add(students[i]);
        }
        return result;
    }

    /**
     * Returns the IDs of every registered student, in registration order.
     * Used to build helpful error messages when a lookup fails.
     */
    public List<String> getAllStudentIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            ids.add(students[i].getStudentId());
        }
        return ids;
    }
}