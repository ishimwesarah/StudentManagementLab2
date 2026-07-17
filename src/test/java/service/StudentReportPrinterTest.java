package service;

import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Demonstrates isolating StudentReportPrinter from its collaborators with
 * Mockito instead of exercising a real StudentManager/StudentAverageCalculator.
 * This is the shape unit tests for the service layer should take once real
 * I/O or heavier dependencies show up in later phases (file export, CSV import).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentReportPrinter (with mocked collaborators)")
class StudentReportPrinterTest {

    @Mock
    private StudentManager studentManager;

    @Mock
    private StudentAverageCalculator calculator;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void redirectSystemOut() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void restoreSystemOut() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("prints a message and does nothing else when there are no students")
    void printAllStudents_noStudents_printsEmptyMessage() {
        when(studentManager.getAllStudents()).thenReturn(List.of());

        StudentReportPrinter printer = new StudentReportPrinter(studentManager, calculator);
        printer.printAllStudents();

        assertTrue(capturedOut.toString().contains("No students registered yet."));
    }

    @Test
    @DisplayName("delegates average calculation to the injected calculator")
    void printAllStudents_delegatesToCalculator() {
        Student aline = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        aline.recordGrade(80);

        when(studentManager.getAllStudents()).thenReturn(List.of(aline));
        when(calculator.round(80.0)).thenReturn(80.0);
        when(calculator.calculateClassAverage(List.of(aline))).thenReturn(80.0);

        StudentReportPrinter printer = new StudentReportPrinter(studentManager, calculator);
        printer.printAllStudents();

        verify(calculator).calculateClassAverage(List.of(aline));
        assertTrue(capturedOut.toString().contains("Aline"));
    }
}