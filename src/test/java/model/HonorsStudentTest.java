package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("HonorsStudent")
class HonorsStudentTest {

    @Test
    @DisplayName("passing grade is 60.0")
    void getPassingGrade_returns60() {
        HonorsStudent student = new HonorsStudent("Jado", 17, "jado@school.sch", "111");

        assertEquals(60.0, student.getPassingGrade());
    }

    @Test
    @DisplayName("becomes honors-eligible once the average reaches 85")
    void checkHonorsEligibility_averageAtThreshold_isEligible() {
        HonorsStudent student = new HonorsStudent("Jado", 17, "jado@school.sch", "111");
        student.recordGrade(85);
        student.recordGrade(85);

        assertTrue(student.checkHonorsEligibility());
        assertTrue(student.isHonorsEligible());
    }

    @Test
    @DisplayName("is not honors-eligible below the 85 threshold")
    void checkHonorsEligibility_belowThreshold_isNotEligible() {
        HonorsStudent student = new HonorsStudent("Jado", 17, "jado@school.sch", "111");
        student.recordGrade(84);
        student.recordGrade(84);

        assertFalse(student.checkHonorsEligibility());
        assertFalse(student.isHonorsEligible());
    }

    @Test
    @DisplayName("eligibility re-evaluates when new grades change the average")
    void checkHonorsEligibility_reevaluatesAfterNewGrades() {
        HonorsStudent student = new HonorsStudent("Jado", 17, "jado@school.sch", "111");
        student.recordGrade(90);
        student.recordGrade(90);
        assertTrue(student.checkHonorsEligibility());

        student.recordGrade(40); // drags the average below 85
        assertFalse(student.checkHonorsEligibility());
    }

    @Test
    @DisplayName("isPassing uses the 60% honors threshold, not the regular 50%")
    void isPassing_usesHonorsThreshold() {
        HonorsStudent student = new HonorsStudent("Jado", 17, "jado@school.sch", "111");
        student.recordGrade(55); // would pass as a Regular student, not as Honors

        assertFalse(student.isPassing());
    }
}