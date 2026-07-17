package service;

import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ClassStatisticsPrinter")
class ClassStatisticsPrinterTest {

    private GradeManager gradeManager;
    private StudentManager studentManager;
    private ClassStatisticsPrinter printer;
    private CoreSubject math;
    private ElectiveSubject art;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        gradeManager = new GradeManager();
        studentManager = new StudentManager();
        printer = new ClassStatisticsPrinter(gradeManager, studentManager, new ClassStatisticsCalculator());
        math = new CoreSubject("Mathematics", "MATH101");
        art = new ElectiveSubject("Art", "ART101");

        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("shows an unavailable message when no grades have been recorded")
    void printClassStatistics_noGrades_showsUnavailableMessage() {
        printer.printClassStatistics();

        assertTrue(capturedOut.toString().contains("statistics unavailable"));
    }

    @Test
    @DisplayName("prints distribution, statistical analysis, and subject performance for a populated class")
    void printClassStatistics_withData_printsAllSections() {
        Student regular = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        Student honors = new HonorsStudent("Jado", 17, "jado@school.sch", "222");
        studentManager.addStudent(regular);
        studentManager.addStudent(honors);

        gradeManager.addGrade(new Grade(regular.getStudentId(), math, 70));
        gradeManager.addGrade(new Grade(regular.getStudentId(), art, 80));
        gradeManager.addGrade(new Grade(honors.getStudentId(), math, 95));
        regular.recordGrade(70);
        regular.recordGrade(80);
        honors.recordGrade(95);

        printer.printClassStatistics();

        String output = capturedOut.toString();
        assertTrue(output.contains("GRADE DISTRIBUTION"));
        assertTrue(output.contains("STATISTICAL ANALYSIS"));
        assertTrue(output.contains("SUBJECT PERFORMANCE"));
        assertTrue(output.contains("STUDENT TYPE COMPARISON"));
        assertTrue(output.contains("Total Students: 2"));
        assertTrue(output.contains("Total Grades Recorded: 3"));
    }

    @Test
    @DisplayName("shows correct highest and lowest grades with student and subject")
    void printClassStatistics_showsHighestAndLowest() {
        Student student = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        studentManager.addStudent(student);

        gradeManager.addGrade(new Grade(student.getStudentId(), math, 40));
        gradeManager.addGrade(new Grade(student.getStudentId(), art, 95));
        student.recordGrade(40);
        student.recordGrade(95);

        printer.printClassStatistics();

        String output = capturedOut.toString();
        assertTrue(output.contains("Highest Grade:  95%"));
        assertTrue(output.contains("Lowest Grade:   40%"));
    }
}