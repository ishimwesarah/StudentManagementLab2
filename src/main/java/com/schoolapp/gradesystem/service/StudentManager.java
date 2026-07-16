package service;

import model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores and retrieves Student objects (composition).
 *
 * Single responsibility: managing the student collection.
 * It does NOT calculate statistics and does NOT print reports -
 * see StudentAverageCalculator and StudentReportPrinter for those.
 */
public class StudentManager {

    private Student[] students = new Student[50];
    private int studentCount = 0;

    public boolean addStudent(Student student) {
        if (studentCount >= students.length) {
            System.out.println("Cannot add student, the list is full.");
            return false;
        }

        students[studentCount] = student;
        studentCount = studentCount + 1;
        return true;
    }

    // Linear Search
    public Student findStudent(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equalsIgnoreCase(studentId)) {
                return students[i];
            }
        }
        return null;
    }

    public int getStudentCount() {
        return studentCount;
    }

    /**
     * Returns all currently registered students, in registration order.
     */
    public List<Student> getAllStudents() {
        List<Student> result = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            result.add(students[i]);
        }
        return result;
    }
}