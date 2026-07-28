package service;

import model.Grade;

import java.util.ArrayList;
import java.util.List;

/**
 * All public methods are synchronized - see StudentManager for why:
 * this class's array is read and written by both the console's main
 * thread and GpaRecalculationScheduler's background thread.
 */
public class GradeManager {

    private Grade[] grades = new Grade[200];
    private int gradeCount = 0;

    public synchronized boolean addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            System.out.println("Cannot add grade, storage is full.");
            return false;
        }

        grades[gradeCount] = grade;
        gradeCount = gradeCount + 1;
        return true;
    }

    public synchronized int getGradeCount() {
        return gradeCount;
    }

    public synchronized List<Grade> getGradesByStudent(String studentId) {
        List<Grade> result = new ArrayList<>();
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentId().equalsIgnoreCase(studentId)) {
                result.add(grades[i]);
            }
        }
        return result;
    }

    public synchronized List<Grade> getAllGrades() {
        List<Grade> result = new ArrayList<>();
        for (int i = 0; i < gradeCount; i++) {
            result.add(grades[i]);
        }
        return result;
    }
}