package service;

import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("GradeAverageCalculator")
class GradeAverageCalculatorTest {

    private GradeAverageCalculator calculator;
    private CoreSubject math;
    private ElectiveSubject art;

    @BeforeEach
    void setUp() {
        calculator = new GradeAverageCalculator();
        math = new CoreSubject("Mathematics", "MATH101");
        art = new ElectiveSubject("Art", "ART101");
    }

    @Test
    @DisplayName("returns 0.0 for an empty grade list")
    void overallAverage_emptyList_returnsZero() {
        assertEquals(0.0, calculator.calculateOverallAverage(new ArrayList<>()));
    }

    @Test
    @DisplayName("returns 0.0 for a null grade list")
    void overallAverage_nullList_returnsZero() {
        assertEquals(0.0, calculator.calculateOverallAverage(null));
    }

    @Test
    @DisplayName("computes the overall average across mixed subject types")
    void overallAverage_mixedGrades_computesCorrectMean() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 80),
                new Grade("STU001", art, 90)
        );

        assertEquals(85.0, calculator.calculateOverallAverage(grades));
    }

    @Test
    @DisplayName("core average only includes Core subject grades")
    void coreAverage_ignoresElectiveGrades() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 80),
                new Grade("STU001", math, 90),
                new Grade("STU001", art, 40)
        );

        assertEquals(85.0, calculator.calculateCoreAverage(grades));
    }

    @Test
    @DisplayName("elective average only includes Elective subject grades")
    void electiveAverage_ignoresCoreGrades() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 100),
                new Grade("STU001", art, 60),
                new Grade("STU001", art, 80)
        );

        assertEquals(70.0, calculator.calculateElectiveAverage(grades));
    }

    @Test
    @DisplayName("core average is 0.0 when the student has no core grades")
    void coreAverage_noCoreGrades_returnsZero() {
        List<Grade> grades = List.of(new Grade("STU001", art, 75));

        assertEquals(0.0, calculator.calculateCoreAverage(grades));
    }

    @Test
    @DisplayName("round() rounds to one decimal place")
    void round_roundsToOneDecimalPlace() {
        assertEquals(83.3, calculator.round(83.26));
        assertEquals(83.3, calculator.round(83.25));
    }
}