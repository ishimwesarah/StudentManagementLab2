package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeTest {

    private final CoreSubject math = new CoreSubject("Mathematics", "MATH101");

    @Test
    void gradeId_matchesExpectedFormat() {
        Grade grade = new Grade("STU001", math, 85);

        // The static counter is shared across the whole test run, so we can't
        // assert an exact ID value here - just that the format is right.
        assertTrue(grade.getGradeId().matches("GRD\\d{3}"));
    }

    @Test
    void storesStudentIdSubjectAndGrade() {
        Grade grade = new Grade("STU001", math, 85);

        assertEquals("STU001", grade.getStudentId());
        assertEquals(math, grade.getSubject());
        assertEquals(85, grade.getGrade());
    }

    @ParameterizedTest(name = "a grade of {0} maps to letter {1}")
    @CsvSource({
            "100, A",
            "90,  A",
            "89,  B",
            "80,  B",
            "79,  C",
            "70,  C",
            "69,  D",
            "60,  D",
            "59,  F",
            "0,   F"
    })
    void getLetterGrade_mapsScoreToCorrectLetter(double score, String expectedLetter) {
        Grade grade = new Grade("STU001", math, score);

        assertEquals(expectedLetter, grade.getLetterGrade());
    }
}