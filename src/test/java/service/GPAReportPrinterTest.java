package service;

import model.CoreSubject;
import model.Grade;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GPAReportPrinter")
class GPAReportPrinterTest {

    private GradeManager gradeManager;
    private StudentManager studentManager;
    private GPAReportPrinter printer;
    private CoreSubject math;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        gradeManager = new GradeManager();
        studentManager = new StudentManager();
        printer = new GPAReportPrinter(gradeManager, studentManager, new GPACalculator());
        math = new CoreSubject("Mathematics", "MATH101");

        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("shows a no-grades message for a student with no recorded grades")
    void printGpaReport_noGrades_showsMessage() {
        Student student = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        studentManager.addStudent(student);

        printer.printGpaReport(student);

        assertTrue(capturedOut.toString().contains("GPA cannot be calculated"));
    }

    @Test
    @DisplayName("prints subject breakdown, cumulative GPA, and letter grade for a student with grades")
    void printGpaReport_withGrades_printsBreakdownAndGpa() {
        Student student = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        studentManager.addStudent(student);
        gradeManager.addGrade(new Grade(student.getStudentId(), math, 93)); // 4.0 GPA points

        printer.printGpaReport(student);

        String output = capturedOut.toString();
        assertTrue(output.contains("GPA CALCULATION"));
        assertTrue(output.contains("Mathematics"));
        assertTrue(output.contains("Cumulative GPA: 4.0"));
        assertTrue(output.contains("Letter Grade: A"));
    }

    @Test
    @DisplayName("ranks the student correctly among multiple students by GPA")
    void printGpaReport_multipleStudents_showsCorrectRank() {
        Student topStudent = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        Student lowerStudent = new RegularStudent("Bob", 16, "bob@school.sch", "222");
        studentManager.addStudent(topStudent);
        studentManager.addStudent(lowerStudent);

        gradeManager.addGrade(new Grade(topStudent.getStudentId(), math, 95));   // 4.0 GPA
        gradeManager.addGrade(new Grade(lowerStudent.getStudentId(), math, 65)); // 1.0 GPA

        printer.printGpaReport(lowerStudent);

        assertTrue(capturedOut.toString().contains("Class Rank: 2 of 2"));
    }

    @Test
    @DisplayName("shows the excellent-performance note when cumulative GPA is 3.5 or above")
    void printGpaReport_highGpa_showsExcellentPerformanceNote() {
        Student student = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        studentManager.addStudent(student);
        gradeManager.addGrade(new Grade(student.getStudentId(), math, 95));

        printer.printGpaReport(student);

        assertTrue(capturedOut.toString().contains("Excellent performance"));
    }
}