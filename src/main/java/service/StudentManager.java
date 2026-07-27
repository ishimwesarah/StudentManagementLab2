package service;

import exception.StudentNotFoundException;
import model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentManager {

    private Student[] students = new Student[50];
    private int studentCount = 0;
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

    public List<Student> getAllStudents() {
        List<Student> result = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            result.add(students[i]);
        }
        return result;
    }

    public List<String> getAllStudentIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            ids.add(students[i].getStudentId());
        }
        return ids;
    }
}