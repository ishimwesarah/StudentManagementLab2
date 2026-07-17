package service;

import exception.StudentNotFoundException;
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

    /**
     * Adds a student to the roster.
     *
     * @param student the student to add
     * @return {@code true} if added successfully, {@code false} if the
     *         roster is already full (capacity 50)
     */
    public boolean addStudent(Student student) {
        if (studentCount >= students.length) {
            System.out.println("Cannot add student, the list is full.");
            return false;
        }

        students[studentCount] = student;
        studentCount = studentCount + 1;
        return true;
    }

    /**
     * Linear search for a student by ID (case-insensitive).
     *
     * @param studentId the ID to look up
     * @return the matching student
     * @throws StudentNotFoundException if no student with this ID is registered
     */
    public Student findStudent(String studentId) throws StudentNotFoundException {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equalsIgnoreCase(studentId)) {
                return students[i];
            }
        }
        throw new StudentNotFoundException("Student with ID '" + studentId + "' not found in the system.");
    }

    /**
     * @return the number of students currently registered
     */
    public int getStudentCount() {
        return studentCount;
    }

    /**
     * @return all currently registered students, in registration order
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
     *
     * @return a list of every registered student's ID
     */
    public List<String> getAllStudentIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            ids.add(students[i].getStudentId());
        }
        return ids;
    }
}