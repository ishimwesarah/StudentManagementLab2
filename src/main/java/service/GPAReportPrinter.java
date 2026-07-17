package service;

import model.Grade;
import model.Student;

import java.util.List;

/**
 * Formats and prints a single student's GPA report: per-subject breakdown,
 * cumulative GPA, overall letter grade, and class rank.
 *
 * Delegates conversion math to GPACalculator and storage lookups to
 * GradeManager/StudentManager - this class only knows how to lay text out
 * on screen, same pattern as GradeReportPrinter and StudentReportPrinter.
 */
public class GPAReportPrinter {

    private final GradeManager gradeManager;
    private final StudentManager studentManager;
    private final GPACalculator gpaCalculator;

    public GPAReportPrinter(GradeManager gradeManager, StudentManager studentManager, GPACalculator gpaCalculator) {
        this.gradeManager = gradeManager;
        this.studentManager = studentManager;
        this.gpaCalculator = gpaCalculator;
    }

    public void printGpaReport(Student student) {
        List<Grade> grades = gradeManager.getGradesByStudent(student.getStudentId());

        System.out.println();
        System.out.println("Student: " + student.getStudentId() + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");

        if (grades.isEmpty()) {
            System.out.println("No grades recorded for this student - GPA cannot be calculated.");
            return;
        }

        double overallAverage = averagePercentage(grades);
        System.out.println("Overall Average: " + round1(overallAverage) + "%");

        System.out.println();
        System.out.println("GPA CALCULATION (4.0 Scale)");
        System.out.println("---------------------------------------------");
        System.out.println("Subject      | Grade  | GPA Points");
        System.out.println("---------------------------------------------");

        for (Grade g : grades) {
            double points = gpaCalculator.toGpaPoints(g.getGrade());
            String letter = gpaCalculator.toLetterGrade(g.getGrade());
            System.out.println(pad(g.getSubject().getSubjectName(), 12) + " | " + (int) g.getGrade()
                    + "%    | " + points + " (" + letter + ")");
        }
        System.out.println("---------------------------------------------");

        double cumulativeGpa = gpaCalculator.round(gpaCalculator.calculateCumulativeGpa(grades));
        String overallLetter = gpaCalculator.toLetterGrade(overallAverage);
        double classAvgGpa = gpaCalculator.round(classAverageGpa());

        System.out.println();
        System.out.println("Cumulative GPA: " + cumulativeGpa + " / 4.0");
        System.out.println("Letter Grade: " + overallLetter);
        System.out.println("Class Rank: " + calculateRank(student) + " of " + studentManager.getStudentCount());

        System.out.println();
        System.out.println("Performance Analysis:");
        if (cumulativeGpa >= 3.5) {
            System.out.println("\u2713 Excellent performance (3.5+ GPA)");
        }
        if (student.isPassing()) {
            System.out.println("\u2713 Meeting passing grade requirement (" + (int) student.getPassingGrade() + "%)");
        }
        if (cumulativeGpa >= classAvgGpa) {
            System.out.println("\u2713 Above class average (" + classAvgGpa + " GPA)");
        } else {
            System.out.println("Below class average (" + classAvgGpa + " GPA)");
        }
    }

    private double averagePercentage(List<Grade> grades) {
        double total = 0.0;
        for (Grade g : grades) {
            total = total + g.getGrade();
        }
        return total / grades.size();
    }

    private double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }

    /**
     * Rank among all students by cumulative GPA (1 = highest). Students with
     * no grades are treated as GPA 0.0 for ranking purposes.
     */
    private int calculateRank(Student student) {
        double studentGpa = gpaCalculator.calculateCumulativeGpa(gradeManager.getGradesByStudent(student.getStudentId()));

        int rank = 1;
        for (Student other : studentManager.getAllStudents()) {
            if (other.getStudentId().equals(student.getStudentId())) {
                continue;
            }
            double otherGpa = gpaCalculator.calculateCumulativeGpa(gradeManager.getGradesByStudent(other.getStudentId()));
            if (otherGpa > studentGpa) {
                rank++;
            }
        }
        return rank;
    }

    /**
     * Average cumulative GPA across all students who have at least one grade.
     */
    private double classAverageGpa() {
        List<Student> students = studentManager.getAllStudents();
        if (students.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        int count = 0;
        for (Student s : students) {
            List<Grade> studentGrades = gradeManager.getGradesByStudent(s.getStudentId());
            if (studentGrades.isEmpty()) {
                continue;
            }
            total = total + gpaCalculator.calculateCumulativeGpa(studentGrades);
            count = count + 1;
        }

        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }

    private String pad(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }
}