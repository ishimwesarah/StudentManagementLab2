package service;

import exception.StudentNotFoundException;
import model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All public methods are synchronized - this class's array and HashMap
 * are read and written by the console's main thread AND by
 * GpaRecalculationScheduler's background thread. Synchronizing every
 * method guarantees no two threads can execute any combination of them
 * (read+read, read+write, write+write) on the same instance at once.
 */
public class StudentManager {

    private Student[] students = new Student[50];
    private int studentCount = 0;
    private final Map<String, Student> studentsById = new HashMap<>();

    public synchronized boolean addStudent(Student student) {
        if (studentCount >= students.length) {
            System.out.println("Cannot add student, the list is full.");
            return false;
        }

        students[studentCount] = student;
        studentCount = studentCount + 1;
        studentsById.put(student.getStudentId().toLowerCase(), student);
        return true;
    }

    public synchronized Student findStudent(String studentId) throws StudentNotFoundException {
        Student student = studentsById.get(studentId.toLowerCase());
        if (student == null) {
            throw new StudentNotFoundException("Student with ID '" + studentId + "' not found in the system.");
        }
        return student;
    }

    public synchronized int getStudentCount() {
        return studentCount;
    }

    public synchronized List<Student> getAllStudents() {
        List<Student> result = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            result.add(students[i]);
        }
        return result;
    }

    public synchronized List<String> getAllStudentIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            ids.add(students[i].getStudentId());
        }
        return ids;
    }
}