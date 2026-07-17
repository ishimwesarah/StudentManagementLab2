package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RegularStudent")
class RegularStudentTest {

    @Test
    @DisplayName("passing grade is 50.0")
    void getPassingGrade_returns50() {
        RegularStudent student = new RegularStudent("Aline", 16, "aline@school.sch", "111");

        assertEquals(50.0, student.getPassingGrade());
    }

    @Test
    @DisplayName("student type is Regular")
    void getStudentType_returnsRegular() {
        RegularStudent student = new RegularStudent("Aline", 16, "aline@school.sch", "111");

        assertEquals("Regular", student.getStudentType());
    }

    @Test
    @DisplayName("passes with an average at or above 50")
    void isPassing_atThreshold_isTrue() {
        RegularStudent student = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        student.recordGrade(50);

        assertTrue(student.isPassing());
    }

    @Test
    @DisplayName("fails with an average below 50")
    void isPassing_belowThreshold_isFalse() {
        RegularStudent student = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        student.recordGrade(49);

        assertFalse(student.isPassing());
    }

    @Test
    @DisplayName("a student with no grades yet is not passing")
    void isPassing_noGradesYet_isFalse() {
        RegularStudent student = new RegularStudent("Aline", 16, "aline@school.sch", "111");

        assertEquals(0.0, student.calculateAverageGrade());
        assertFalse(student.isPassing());
    }

    @Test
    @DisplayName("recordGrade rejects grades outside 0-100")
    void recordGrade_outOfRange_isRejected() {
        RegularStudent student = new RegularStudent("Aline", 16, "aline@school.sch", "111");

        assertFalse(student.recordGrade(-1));
        assertFalse(student.recordGrade(101));
        assertEquals(0, student.getNumberOfGrades());
    }
}