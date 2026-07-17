package service;

import model.Grade;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Computes class-wide statistics across all recorded grades: central
 * tendency (mean, median, mode), spread (standard deviation), grade
 * distribution by letter, and Regular vs. Honors student comparison.
 *
 * Single responsibility: statistical calculation only. No storage,
 * no printing - see ClassStatisticsPrinter for that.
 */
public class ClassStatisticsCalculator {

    /**
     * @param grades the grades to average
     * @return the simple mean of all given grades, or {@code 0.0} if the
     *         list is null or empty
     */
    public double mean(List<Grade> grades) {
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
     * @param grades the grades to find the median of
     * @return the median grade, or {@code 0.0} if the list is null or empty
     */
    public double median(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        List<Double> scores = new ArrayList<>();
        for (Grade g : grades) {
            scores.add(g.getGrade());
        }
        Collections.sort(scores);

        int size = scores.size();
        int mid = size / 2;
        if (size % 2 == 0) {
            return (scores.get(mid - 1) + scores.get(mid)) / 2.0;
        }
        return scores.get(mid);
    }

    /**
     * Returns the most frequently occurring grade. If there's a tie, returns
     * the smallest of the tied values (a simple, deterministic tie-break).
     *
     * @param grades the grades to find the mode of
     * @return the most frequent grade, or {@code 0.0} if the list is null or empty
     */
    public double mode(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        java.util.Map<Double, Integer> counts = new java.util.HashMap<>();
        for (Grade g : grades) {
            counts.merge(g.getGrade(), 1, Integer::sum);
        }

        double bestValue = 0.0;
        int bestCount = -1;
        for (java.util.Map.Entry<Double, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > bestCount
                    || (entry.getValue() == bestCount && entry.getKey() < bestValue)) {
                bestValue = entry.getKey();
                bestCount = entry.getValue();
            }
        }
        return bestValue;
    }

    /**
     * Computes the population standard deviation of the given grades.
     *
     * @param grades the grades to measure spread across
     * @return the standard deviation, or {@code 0.0} if there are fewer
     *         than 2 grades (spread is undefined for 0 or 1 values)
     */
    public double standardDeviation(List<Grade> grades) {
        if (grades == null || grades.size() < 2) {
            return 0.0;
        }

        double avg = mean(grades);
        double sumSquaredDiffs = 0.0;
        for (Grade g : grades) {
            double diff = g.getGrade() - avg;
            sumSquaredDiffs = sumSquaredDiffs + (diff * diff);
        }
        double variance = sumSquaredDiffs / grades.size();
        return Math.sqrt(variance);
    }

    /**
     * @param grades the grades to search
     * @return the highest grade in the list, or {@code 0.0} if the list is empty
     */
    public double highest(List<Grade> grades) {
        double max = Double.MIN_VALUE;
        for (Grade g : grades) {
            if (g.getGrade() > max) {
                max = g.getGrade();
            }
        }
        return grades.isEmpty() ? 0.0 : max;
    }

    /**
     * @param grades the grades to search
     * @return the lowest grade in the list, or {@code 0.0} if the list is empty
     */
    public double lowest(List<Grade> grades) {
        double min = Double.MAX_VALUE;
        for (Grade g : grades) {
            if (g.getGrade() < min) {
                min = g.getGrade();
            }
        }
        return grades.isEmpty() ? 0.0 : min;
    }

    /**
     * Counts grades falling into each letter bucket: A (90-100), B (80-89),
     * C (70-79), D (60-69), F (below 60).
     *
     * @param grades the grades to bucket
     * @return a 5-element array where index 0=A count, 1=B, 2=C, 3=D, 4=F
     */
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

    /**
     * @param grades      the grades to filter and average
     * @param subjectName the exact subject name to match, e.g. "Mathematics"
     * @return the average grade for that subject, or {@code 0.0} if none found
     */
    public double averageForSubject(List<Grade> grades, String subjectName) {
        double total = 0.0;
        int count = 0;
        for (Grade g : grades) {
            if (g.getSubject().getSubjectName().equals(subjectName)) {
                total = total + g.getGrade();
                count = count + 1;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    /**
     * @param students the students to filter and average
     * @return the average of each Regular student's own average grade, or
     *         {@code 0.0} if there are none
     */
    public double regularStudentAverage(List<Student> students) {
        return averageByType(students, RegularStudent.class);
    }

    /**
     * @param students the students to filter and average
     * @return the average of each Honors student's own average grade, or
     *         {@code 0.0} if there are none
     */
    public double honorsStudentAverage(List<Student> students) {
        return averageByType(students, HonorsStudent.class);
    }

    /**
     * @param students the students to filter
     * @param type     the concrete Student subtype to match
     * @return the average of matching students' own average grades, or
     *         {@code 0.0} if none match
     */
    private double averageByType(List<Student> students, Class<?> type) {
        double total = 0.0;
        int count = 0;
        for (Student s : students) {
            if (type.isInstance(s)) {
                total = total + s.calculateAverageGrade();
                count = count + 1;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    /**
     * @param value the value to round
     * @return {@code value} rounded to one decimal place
     */
    public double round(double value) {
        return Math.round(value * 10) / 10.0;
    }
}