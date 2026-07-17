package service;

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
public class GradeAverageCalculator {

    public double round(double value) {
        return Math.round(value * 10) / 10.0;
    }

    public double calculateCoreAverage(List<Grade> grades) {
        return averageByType(grades, "Core");
    }

    public double calculateElectiveAverage(List<Grade> grades) {
        return averageByType(grades, "Elective");
    }

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