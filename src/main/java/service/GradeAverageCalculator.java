package service;

import model.Calculable;
import model.Grade;

import java.util.List;

public class GradeAverageCalculator implements Calculable<Grade> {

    @Override
    public double calculate(List<Grade> grades) {
        return calculateOverallAverage(grades);
    }

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

        return grades.stream()
                .mapToDouble(Grade::getGrade)
                .average()
                .orElse(0.0);
    }

    private double averageByType(List<Grade> grades, String subjectType) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        return grades.stream()
                .filter(g -> g.getSubject().getSubjectType().equals(subjectType))
                .mapToDouble(Grade::getGrade)
                .average()
                .orElse(0.0);
    }
}