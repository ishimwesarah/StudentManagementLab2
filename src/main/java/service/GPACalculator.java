package service;

import model.Grade;

import java.util.List;

/**
 * Converts percentage grades to the standard 4.0 GPA scale.
 *
 * Single responsibility: GPA conversion math only. No storage, no printing -
 * that's GPAReportPrinter's job.
 */
public class GPACalculator {

    /**
     * Converts a percentage score to GPA points on the standard 4.0 scale.
     *
     * @param percentage a score in the 0-100 range
     * @return the corresponding GPA points, from 0.0 to 4.0
     */
    public double toGpaPoints(double percentage) {
        if (percentage >= 93) return 4.0;
        if (percentage >= 90) return 3.7;
        if (percentage >= 87) return 3.3;
        if (percentage >= 83) return 3.0;
        if (percentage >= 80) return 2.7;
        if (percentage >= 77) return 2.3;
        if (percentage >= 73) return 2.0;
        if (percentage >= 70) return 1.7;
        if (percentage >= 67) return 1.3;
        if (percentage >= 60) return 1.0;
        return 0.0;
    }

    /**
     * Converts a percentage score to its letter-grade-with-modifier
     * equivalent (e.g. "A-", "B+"), matching the same scale as
     * {@link #toGpaPoints(double)}.
     *
     * @param percentage a score in the 0-100 range
     * @return the corresponding letter grade
     */
    public String toLetterGrade(double percentage) {
        if (percentage >= 93) return "A";
        if (percentage >= 90) return "A-";
        if (percentage >= 87) return "B+";
        if (percentage >= 83) return "B";
        if (percentage >= 80) return "B-";
        if (percentage >= 77) return "C+";
        if (percentage >= 73) return "C";
        if (percentage >= 70) return "C-";
        if (percentage >= 67) return "D+";
        if (percentage >= 60) return "D";
        return "F";
    }

    /**
     * Cumulative GPA is the simple average of each individual grade's GPA points.
     *
     * @param grades the grades to average
     * @return the cumulative GPA, or {@code 0.0} if the list is null or empty
     */
    public double calculateCumulativeGpa(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Grade g : grades) {
            total = total + toGpaPoints(g.getGrade());
        }
        return total / grades.size();
    }

    /**
     * @param value the value to round
     * @return {@code value} rounded to two decimal places
     */
    public double round(double value) {
        return Math.round(value * 100) / 100.0;
    }
}