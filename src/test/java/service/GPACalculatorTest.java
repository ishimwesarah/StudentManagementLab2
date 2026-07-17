package service;

import model.CoreSubject;
import model.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("GPACalculator")
class GPACalculatorTest {

    private GPACalculator gpaCalculator;
    private CoreSubject math;

    @BeforeEach
    void setUp() {
        gpaCalculator = new GPACalculator();
        math = new CoreSubject("Mathematics", "MATH101");
    }

    @ParameterizedTest(name = "{0}% maps to {1} GPA points")
    @CsvSource({
            "100, 4.0",
            "93,  4.0",
            "92,  3.7",
            "90,  3.7",
            "89,  3.3",
            "87,  3.3",
            "86,  3.0",
            "83,  3.0",
            "82,  2.7",
            "80,  2.7",
            "79,  2.3",
            "77,  2.3",
            "76,  2.0",
            "73,  2.0",
            "72,  1.7",
            "70,  1.7",
            "69,  1.3",
            "67,  1.3",
            "66,  1.0",
            "60,  1.0",
            "59,  0.0",
            "0,   0.0"
    })
    void toGpaPoints_mapsPercentageCorrectly(double percentage, double expectedPoints) {
        assertEquals(expectedPoints, gpaCalculator.toGpaPoints(percentage));
    }

    @ParameterizedTest(name = "{0}% maps to letter {1}")
    @CsvSource({
            "93,  A",
            "90,  A-",
            "87,  B+",
            "83,  B",
            "80,  B-",
            "77,  C+",
            "73,  C",
            "70,  C-",
            "67,  D+",
            "60,  D",
            "59,  F"
    })
    void toLetterGrade_mapsPercentageCorrectly(double percentage, String expectedLetter) {
        assertEquals(expectedLetter, gpaCalculator.toLetterGrade(percentage));
    }

    @Test
    @DisplayName("cumulative GPA averages each grade's GPA points")
    void calculateCumulativeGpa_averagesGpaPoints() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 93), // 4.0
                new Grade("STU001", math, 83)  // 3.0
        );

        // (4.0 + 3.0) / 2 = 3.5
        assertEquals(3.5, gpaCalculator.calculateCumulativeGpa(grades));
    }

    @Test
    @DisplayName("cumulative GPA is 0.0 for an empty or null grade list")
    void calculateCumulativeGpa_emptyOrNull_returnsZero() {
        assertEquals(0.0, gpaCalculator.calculateCumulativeGpa(List.of()));
        assertEquals(0.0, gpaCalculator.calculateCumulativeGpa(null));
    }

    @Test
    @DisplayName("round() rounds to two decimal places")
    void round_roundsToTwoDecimalPlaces() {
        assertEquals(3.74, gpaCalculator.round(3.735));
        assertEquals(3.7, gpaCalculator.round(3.7));
    }
}