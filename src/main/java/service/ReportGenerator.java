package service;

import model.Grade;
import model.Student;

import java.util.List;

/**
 * Generates grade report content as plain text. Single responsibility:
 * building the report string - it has no idea where that string ends up
 * (console, file, network). See FileExporter for writing it to disk.
 */
public class ReportGenerator {

    private final GradeAverageCalculator calculator;

    public ReportGenerator(GradeAverageCalculator calculator) {
        this.calculator = calculator;
    }

    public String generateSummaryReport(Student student, List<Grade> grades) {
        StringBuilder sb = new StringBuilder();

        sb.append("STUDENT GRADE REPORT - SUMMARY\n");
        sb.append("=============================================\n");
        sb.append("Student ID: ").append(student.getStudentId()).append("\n");
        sb.append("Name: ").append(student.getName()).append("\n");
        sb.append("Type: ").append(student.getStudentType()).append(" Student\n");
        sb.append("Passing Grade: ").append((int) student.getPassingGrade()).append("%\n");
        sb.append("=============================================\n\n");

        if (grades.isEmpty()) {
            sb.append("No grades recorded for this student.\n");
            return sb.toString();
        }

        double overall = calculator.round(calculator.calculateOverallAverage(grades));
        double core = calculator.round(calculator.calculateCoreAverage(grades));
        double elective = calculator.round(calculator.calculateElectiveAverage(grades));

        sb.append("Total Grades: ").append(grades.size()).append("\n");
        sb.append("Core Subjects Average: ").append(core).append("%\n");
        sb.append("Elective Subjects Average: ").append(elective).append("%\n");
        sb.append("Overall Average: ").append(overall).append("%\n");
        sb.append("Status: ").append(student.isPassing() ? "PASSING" : "FAILING").append("\n");

        return sb.toString();
    }

    public String generateDetailedReport(Student student, List<Grade> grades) {
        StringBuilder sb = new StringBuilder(generateSummaryReport(student, grades));

        if (grades.isEmpty()) {
            return sb.toString();
        }

        sb.append("\nGRADE HISTORY\n");
        sb.append("---------------------------------------------------------------------\n");
        sb.append("GRD ID  | DATE       | SUBJECT          | TYPE      | GRADE\n");
        sb.append("---------------------------------------------------------------------\n");

        for (int i = grades.size() - 1; i >= 0; i--) {
            Grade g = grades.get(i);
            sb.append(g.getGradeId()).append(" |  ").append(g.getDate()).append(" |  ")
                    .append(g.getSubject().getSubjectName()).append(" |         ")
                    .append(g.getSubject().getSubjectType()).append(" |          ")
                    .append(g.getGrade()).append("%\n");
        }
        sb.append("---------------------------------------------------------------------\n");

        return sb.toString();
    }
}