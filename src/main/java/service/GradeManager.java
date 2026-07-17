package service;

import model.Grade;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores and retrieves Grade objects (composition).
 *
 * Single responsibility: managing the grade collection.
 * It does NOT calculate averages and does NOT print reports -
 * see GradeAverageCalculator and GradeReportPrinter for those.
 */
public class GradeManager {

    private Grade[] grades = new Grade[200];
    private int gradeCount = 0;

    public boolean addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            System.out.println("Cannot add grade, storage is full.");
            return false;
        }

        grades[gradeCount] = grade;
        gradeCount = gradeCount + 1;
        return true;
    }

    public int getGradeCount() {
        return gradeCount;
    }

    /**
     * Returns all grades belonging to the given student, in the order they
     * were recorded (oldest first).
     */
    public List<Grade> getGradesByStudent(String studentId) {
        List<Grade> result = new ArrayList<>();
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentId().equalsIgnoreCase(studentId)) {
                result.add(grades[i]);
            }
        }
        return result;
    }

    /**
     * Returns every grade currently stored, across all students.
     */
    public List<Grade> getAllGrades() {
        List<Grade> result = new ArrayList<>();
        for (int i = 0; i < gradeCount; i++) {
            result.add(grades[i]);
        }
        return result;
    }
}