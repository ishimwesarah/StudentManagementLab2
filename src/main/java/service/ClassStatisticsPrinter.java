package service;

import exception.StudentNotFoundException;
import model.Grade;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ClassStatisticsPrinter {

    private final GradeManager gradeManager;
    private final StudentManager studentManager;
    private final ClassStatisticsCalculator calculator;
    private final CourseTracker courseTracker;

    public ClassStatisticsPrinter(GradeManager gradeManager, StudentManager studentManager, ClassStatisticsCalculator calculator) {
        this.gradeManager = gradeManager;
        this.studentManager = studentManager;
        this.calculator = calculator;
        this.courseTracker = new CourseTracker(gradeManager);
    }

    public void printClassStatistics() {
        List<Grade> allGrades = gradeManager.getAllGrades();
        List<Student> allStudents = studentManager.getAllStudents();

        System.out.println();
        System.out.println("CLASS STATISTICS");
        System.out.println("---------------------------------------------");
        System.out.println();
        System.out.println("Total Students: " + allStudents.size());
        System.out.println("Total Grades Recorded: " + allGrades.size());
        System.out.println("Unique Courses Tracked: " + courseTracker.getUniqueCourseCount());

        if (allGrades.isEmpty()) {
            System.out.println();
            System.out.println("No grades recorded yet - statistics unavailable.");
            return;
        }

        printGradeDistribution(allGrades);
        printStatisticalAnalysis(allGrades);
        printSubjectPerformance(allGrades);
        printStudentTypeComparison(allStudents);
    }

    private void printGradeDistribution(List<Grade> grades) {
        int[] distribution = calculator.gradeDistribution(grades);
        String[] labels = {"90-100% (A)", "80-89%  (B)", "70-79%  (C)", "60-69%  (D)", "0-59%   (F)"};

        System.out.println();
        System.out.println("GRADE DISTRIBUTION");
        System.out.println("---------------------------------------------");

        int total = grades.size();
        for (int i = 0; i < 5; i++) {
            int count = distribution[i];
            double percentage = (count * 100.0) / total;
            System.out.println(labels[i] + ": " + bar(percentage) + " " + formatPercent(percentage)
                    + "% (" + count + " grade" + (count == 1 ? "" : "s") + ")");
        }
    }

    private String bar(double percentage) {
        int filled = (int) Math.round(percentage / 5.0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append(i < filled ? '\u2588' : '\u2591');
        }
        return sb.toString();
    }

    private String formatPercent(double value) {
        return String.format("%4.1f", value);
    }

    private void printStatisticalAnalysis(List<Grade> grades) {
        double mean = calculator.round(calculator.mean(grades));
        double median = calculator.round(calculator.median(grades));
        double mode = calculator.round(calculator.mode(grades));
        double stdDev = calculator.round(calculator.standardDeviation(grades));
        double highest = calculator.highest(grades);
        double lowest = calculator.lowest(grades);

        System.out.println();
        System.out.println("STATISTICAL ANALYSIS");
        System.out.println("---------------------------------------------");
        System.out.println("Mean (Average):      " + mean + "%");
        System.out.println("Median:              " + median + "%");
        System.out.println("Mode:                " + mode + "%");
        System.out.println("Standard Deviation:  " + stdDev + "%");
        System.out.println("Range:               " + calculator.round(highest - lowest)
                + "% (" + (int) lowest + "% - " + (int) highest + "%)");

        Grade highestGrade = findGradeWithValue(grades, highest);
        Grade lowestGrade = findGradeWithValue(grades, lowest);

        System.out.println();
        System.out.println("Highest Grade:  " + (int) highest + "% (" + studentLabel(highestGrade)
                + " - " + highestGrade.getSubject().getSubjectName() + ")");
        System.out.println("Lowest Grade:   " + (int) lowest + "% (" + studentLabel(lowestGrade)
                + " - " + lowestGrade.getSubject().getSubjectName() + ")");
    }

    private Grade findGradeWithValue(List<Grade> grades, double value) {
        for (Grade g : grades) {
            if (g.getGrade() == value) {
                return g;
            }
        }
        return grades.get(0);
    }

    private String studentLabel(Grade grade) {
        try {
            Student student = studentManager.findStudent(grade.getStudentId());
            return student.getStudentId();
        } catch (StudentNotFoundException e) {
            return grade.getStudentId();
        }
    }

    private void printSubjectPerformance(List<Grade> grades) {
        List<Grade> coreGrades = filterByType(grades, "Core");
        List<Grade> electiveGrades = filterByType(grades, "Elective");

        System.out.println();
        System.out.println("SUBJECT PERFORMANCE");
        System.out.println("---------------------------------------------");

        if (!coreGrades.isEmpty()) {
            System.out.println("Core Subjects:     " + calculator.round(calculator.mean(coreGrades)) + "% average");
            printPerSubjectBreakdown(coreGrades);
        }

        System.out.println();

        if (!electiveGrades.isEmpty()) {
            System.out.println("Elective Subjects: " + calculator.round(calculator.mean(electiveGrades)) + "% average");
            printPerSubjectBreakdown(electiveGrades);
        }
    }

    private List<Grade> filterByType(List<Grade> grades, String type) {
        List<Grade> result = new ArrayList<>();
        for (Grade g : grades) {
            if (g.getSubject().getSubjectType().equals(type)) {
                result.add(g);
            }
        }
        return result;
    }

    private void printPerSubjectBreakdown(List<Grade> grades) {
        LinkedHashSet<String> subjectNames = new LinkedHashSet<>();
        for (Grade g : grades) {
            subjectNames.add(g.getSubject().getSubjectName());
        }
        for (String name : subjectNames) {
            double avg = calculator.round(calculator.averageForSubject(grades, name));
            System.out.println("  " + name + ": " + avg + "%");
        }
    }

    private void printStudentTypeComparison(List<Student> students) {
        int regularCount = 0;
        int honorsCount = 0;
        for (Student s : students) {
            if (s instanceof HonorsStudent) {
                honorsCount++;
            } else if (s instanceof RegularStudent) {
                regularCount++;
            }
        }

        System.out.println();
        System.out.println("STUDENT TYPE COMPARISON");
        System.out.println("---------------------------------------------");
        System.out.println("Regular Students:  " + calculator.round(calculator.regularStudentAverage(students))
                + "% average (" + regularCount + " students)");
        System.out.println("Honors Students:   " + calculator.round(calculator.honorsStudentAverage(students))
                + "% average (" + honorsCount + " students)");
    }
}