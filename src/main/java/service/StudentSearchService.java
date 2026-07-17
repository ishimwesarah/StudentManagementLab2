package service;

import model.HonorsStudent;
import model.RegularStudent;
import model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches the student roster by ID, name, grade range, or student type.
 *
 * Single responsibility: search/filtering logic only. Depends on
 * StudentManager for the underlying data, same pattern as the other
 * printers - no duplicated storage here.
 */
public class StudentSearchService {

    private final StudentManager studentManager;

    public StudentSearchService(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    /**
     * Exact match search by student ID (case-insensitive), returning at
     * most one result.
     */
    public List<Student> searchById(String studentId) {
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                result.add(s);
                break;
            }
        }
        return result;
    }

    /**
     * Partial, case-insensitive match against student name.
     */
    public List<Student> searchByName(String nameQuery) {
        List<Student> result = new ArrayList<>();
        String query = nameQuery.toLowerCase();
        for (Student s : studentManager.getAllStudents()) {
            if (s.getName().toLowerCase().contains(query)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Students whose current average grade falls within [minGrade, maxGrade],
     * inclusive.
     */
    public List<Student> searchByGradeRange(double minGrade, double maxGrade) {
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            double avg = s.calculateAverageGrade();
            if (avg >= minGrade && avg <= maxGrade) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Students matching the given type ("Regular" or "Honors"), case-insensitive.
     */
    public List<Student> searchByType(String studentType) {
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if ("Honors".equalsIgnoreCase(studentType) && s instanceof HonorsStudent) {
                result.add(s);
            } else if ("Regular".equalsIgnoreCase(studentType) && s instanceof RegularStudent) {
                result.add(s);
            }
        }
        return result;
    }
}