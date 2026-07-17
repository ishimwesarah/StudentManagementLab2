package service;

import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ReportGenerator")
class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private CoreSubject math;
    private ElectiveSubject art;
    private Student student;

    @BeforeEach
    void setUp() {
        reportGenerator = new ReportGenerator(new GradeAverageCalculator());
        math = new CoreSubject("Mathematics", "MATH101");
        art = new ElectiveSubject("Art", "ART101");
        student = new RegularStudent("Aline", 16, "aline@school.sch", "111");
    }

    @Test
    @DisplayName("summary report includes student info and status for a student with grades")
    void generateSummaryReport_withGrades_includesKeyDetails() {
        List<Grade> grades = List.of(
                new Grade(student.getStudentId(), math, 80),
                new Grade(student.getStudentId(), art, 90)
        );
        student.recordGrade(80);
        student.recordGrade(90);

        String report = reportGenerator.generateSummaryReport(student, grades);

        assertTrue(report.contains(student.getStudentId()));
        assertTrue(report.contains("Aline"));
        assertTrue(report.contains("Total Grades: 2"));
        assertTrue(report.contains("Overall Average: 85.0%"));
        assertTrue(report.contains("PASSING"));
    }

    @Test
    @DisplayName("summary report handles a student with no grades")
    void generateSummaryReport_noGrades_showsNoGradesMessage() {
        String report = reportGenerator.generateSummaryReport(student, List.of());

        assertTrue(report.contains("No grades recorded for this student."));
        assertFalse(report.contains("Total Grades"));
    }

    @Test
    @DisplayName("detailed report includes the summary plus grade history rows")
    void generateDetailedReport_includesSummaryAndHistory() {
        List<Grade> grades = List.of(new Grade(student.getStudentId(), math, 80));
        student.recordGrade(80);

        String report = reportGenerator.generateDetailedReport(student, grades);

        assertTrue(report.contains("GRADE HISTORY"));
        assertTrue(report.contains("Mathematics"));
        assertTrue(report.contains("Overall Average"));
    }

    @Test
    @DisplayName("detailed report for a student with no grades has no grade history section")
    void generateDetailedReport_noGrades_hasNoHistorySection() {
        String report = reportGenerator.generateDetailedReport(student, List.of());

        assertFalse(report.contains("GRADE HISTORY"));
        assertTrue(report.contains("No grades recorded for this student."));
    }
}