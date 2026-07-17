package service;

import model.Student;

import java.util.List;

/**
 * Computes aggregate grade statistics across a collection of students.
 *
 * Single responsibility: numeric calculation only. No storage, no printing.
 * Extracted from the old StudentManager, which used to mix all three concerns.
 */
public class StudentAverageCalculator {

    /**
     * @param students the students to average
     * @return the average of each student's own average grade, or
     *         {@code 0.0} if the list is null or empty
     */
    public double calculateClassAverage(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Student student : students) {
            total = total + student.calculateAverageGrade();
        }
        return total / students.size();
    }

    /**
     * @param value the value to round
     * @return {@code value} rounded to the nearest whole number
     */
    public double round(double value) {
        return Math.round(value);
    }
}