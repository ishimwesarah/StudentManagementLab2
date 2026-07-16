package service;

import model.Grade;
import model.Student;

import java.util.List;

/**
 * Responsible only for formatting and printing a single student's grade
 * report to the console.
 *
 * Delegates storage lookups to GradeManager and math to GradeAverageCalculator -
 * this class only knows how to lay text out on screen.
 */
public class GradeReportPrinter {

    private final GradeManager gradeManager;
    private final GradeAverageCalculator calculator;

    public GradeReportPrinter(GradeManager gradeManager, GradeAverageCalculator calculator) {
        this.gradeManager = gradeManager;
        this.calculator = calculator;
    }

    public void printReport(Student student) {
        String studentId = student.getStudentId();
        List<Grade> studentGrades = gradeManager.getGradesByStudent(studentId);

        System.out.println();
        System.out.println("Student: " + studentId + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");

        if (studentGrades.isEmpty()) {
            System.out.println("Passing Grade: " + (int) student.getPassingGrade() + "%");
            System.out.println("---------------------------------------------");
            System.out.println("No grades recorded for this student.");
            System.out.println("---------------------------------------------");
            return;
        }

        double overall = calculator.round(calculator.calculateOverallAverage(studentGrades));
        System.out.println("Current Average: " + overall + "%");

        if (student.isPassing()) {
            System.out.println("Status: PASSING");
        } else {
            System.out.println("Status: FAILING");
        }

        System.out.println();
        System.out.println("GRADE HISTORY");
        System.out.println("-------------------------------------------------------------------");
        System.out.println("GRD ID  | DATE       | SUBJECT          | TYPE      | GRADE");
        System.out.println("-------------------------------------------------------------------");

        // Newest first (grades were recorded oldest-first, so walk backwards).
        for (int i = studentGrades.size() - 1; i >= 0; i--) {
            Grade g = studentGrades.get(i);
            System.out.println(g.getGradeId() + " |  " + g.getDate() + " |  "
                    + g.getSubject().getSubjectName() + " |         " + g.getSubject().getSubjectType()
                    + " |          " + g.getGrade() + "%");
        }
        System.out.println("-------------------------------------------------------------------");

        System.out.println("Total Grades: " + studentGrades.size());
        System.out.println("Core Subjects Average: " + calculator.round(calculator.calculateCoreAverage(studentGrades)) + "%");
        System.out.println("Elective Subjects Average: " + calculator.round(calculator.calculateElectiveAverage(studentGrades)) + "%");
        System.out.println("Overall Average: " + overall + "%");
    }
}