package service;

import model.Calculable;
import model.Grade;

import java.util.List;

/**
 * Computes averages over a list of grades.
 *
 * Single responsibility: numeric calculation only. This class has no idea
 * where the grades came from or which student they belong to - it just
 * takes a List<Grade> and does math. That makes it trivial to unit test
 * and reusable for any future feature that needs a subject-type average
 * (e.g. class-wide statistics in a later phase).
 *
 * Extracted from the old GradeManager, which used to mix storage,
 * calculation, and console printing all in one class.
 */
public class GradeAverageCalculator implements Calculable<Grade> {

    /**
     * Satisfies the Calculable contract by delegating to the overall
     * average - the most natural single-number summary of a list of grades.
     */
    @Override
    public double calculate(List<Grade> grades) {
        return calculateOverallAverage(grades);
    }

    /**
     * @param value the value to round
     * @return {@code value} rounded to one decimal place
     */
    public double round(double value) {
        return Math.round(value * 10) / 10.0;
    }

    /**
     * @param grades the grades to average
     * @return the average of just the Core-subject grades, or {@code 0.0}
     *         if there are none
     */
    public double calculateCoreAverage(List<Grade> grades) {
        return averageByType(grades, "Core");
    }

    /**
     * @param grades the grades to average
     * @return the average of just the Elective-subject grades, or
     *         {@code 0.0} if there are none
     */
    public double calculateElectiveAverage(List<Grade> grades) {
        return averageByType(grades, "Elective");
    }

    /**
     * @param grades the grades to average
     * @return the simple average across all given grades, or {@code 0.0}
     *         if the list is null or empty
     */
    public double calculateOverallAverage(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Grade g : grades) {
            total = total + g.getGrade();
        }
        return total / grades.size();
    }

    /**
     * @param grades      the grades to filter and average
     * @param subjectType the subject type to match, e.g. "Core" or "Elective"
     * @return the average of grades matching that subject type, or
     *         {@code 0.0} if the list is null/empty or nothing matches
     */
    private double averageByType(List<Grade> grades, String subjectType) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        int count = 0;
        for (Grade g : grades) {
            if (g.getSubject().getSubjectType().equals(subjectType)) {
                total = total + g.getGrade();
                count = count + 1;
            }
        }

        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }
}