package service;

import model.HonorsStudent;
import model.RegularStudent;
import model.Searchable;
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
public class StudentSearchService implements Searchable {

    private final StudentManager studentManager;

    public StudentSearchService(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    /**
     * Satisfies the Searchable contract by delegating to name-based
     * partial matching - the most natural default meaning of "search"
     * for this string-based strategy.
     */
    @Override
    public List<Student> search(String query) {
        return searchByName(query);
    }

    /**
     * Exact match search by student ID (case-insensitive).
     *
     * @param studentId the ID to search for
     * @return a single-element list containing the match, or an empty
     *         list if no student has that ID
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
     *
     * @param nameQuery the text to search for anywhere in a student's name
     * @return every student whose name contains the query, in roster order
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
     * @param minGrade the lower bound, inclusive
     * @param maxGrade the upper bound, inclusive
     * @return every student whose current average grade falls within
     *         [minGrade, maxGrade]
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
     * @param studentType the type to match, "Regular" or "Honors"
     *                     (case-insensitive)
     * @return every student of the matching type
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