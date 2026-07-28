package service;

import model.Grade;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassStatisticsCalculator {

    public double mean(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }
        return grades.stream()
                .mapToDouble(Grade::getGrade)
                .average()
                .orElse(0.0);
    }

    public double median(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        List<Double> scores = grades.stream()
                .map(Grade::getGrade)
                .sorted()
                .collect(Collectors.toList());

        int size = scores.size();
        int mid = size / 2;
        if (size % 2 == 0) {
            return (scores.get(mid - 1) + scores.get(mid)) / 2.0;
        }
        return scores.get(mid);
    }

    public double mode(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        Map<Double, Long> counts = grades.stream()
                .collect(Collectors.groupingBy(Grade::getGrade, Collectors.counting()));

        double bestValue = 0.0;
        long bestCount = -1;
        for (Map.Entry<Double, Long> entry : counts.entrySet()) {
            if (entry.getValue() > bestCount
                    || (entry.getValue() == bestCount && entry.getKey() < bestValue)) {
                bestValue = entry.getKey();
                bestCount = entry.getValue();
            }
        }
        return bestValue;
    }

    public double standardDeviation(List<Grade> grades) {
        if (grades == null || grades.size() < 2) {
            return 0.0;
        }

        double avg = mean(grades);
        double sumSquaredDiffs = grades.stream()
                .mapToDouble(g -> Math.pow(g.getGrade() - avg, 2))
                .sum();

        double variance = sumSquaredDiffs / grades.size();
        return Math.sqrt(variance);
    }

    public double highest(List<Grade> grades) {
        return grades.stream()
                .mapToDouble(Grade::getGrade)
                .max()
                .orElse(0.0);
    }

    public double lowest(List<Grade> grades) {
        return grades.stream()
                .mapToDouble(Grade::getGrade)
                .min()
                .orElse(0.0);
    }

    public int[] gradeDistribution(List<Grade> grades) {
        int[] buckets = new int[5];
        for (Grade g : grades) {
            double score = g.getGrade();
            if (score >= 90) {
                buckets[0]++;
            } else if (score >= 80) {
                buckets[1]++;
            } else if (score >= 70) {
                buckets[2]++;
            } else if (score >= 60) {
                buckets[3]++;
            } else {
                buckets[4]++;
            }
        }
        return buckets;
    }

    public double averageForSubject(List<Grade> grades, String subjectName) {
        return grades.stream()
                .filter(g -> g.getSubject().getSubjectName().equals(subjectName))
                .mapToDouble(Grade::getGrade)
                .average()
                .orElse(0.0);
    }

    public double regularStudentAverage(List<Student> students) {
        return averageByType(students, RegularStudent.class);
    }

    public double honorsStudentAverage(List<Student> students) {
        return averageByType(students, HonorsStudent.class);
    }

    private double averageByType(List<Student> students, Class<?> type) {
        return students.stream()
                .filter(type::isInstance)
                .mapToDouble(Student::calculateAverageGrade)
                .average()
                .orElse(0.0);
    }

    public double round(double value) {
        return Math.round(value * 10) / 10.0;
    }
}