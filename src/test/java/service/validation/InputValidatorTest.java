package service.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("InputValidator")
class InputValidatorTest {

    private final InputValidator validator = new InputValidator();

    @ParameterizedTest(name = "\"{0}\" is a valid student ID")
    @ValueSource(strings = {"STU001", "STU999", "STU123"})
    void isValidStudentId_acceptsCorrectFormat(String studentId) {
        assertTrue(validator.isValidStudentId(studentId));
    }

    @ParameterizedTest(name = "\"{0}\" is NOT a valid student ID")
    @ValueSource(strings = {"STU1", "STU0001", "stu001abc", "STUD001", "", "STU"})
    void isValidStudentId_rejectsIncorrectFormat(String studentId) {
        assertFalse(validator.isValidStudentId(studentId));
    }

    @ParameterizedTest(name = "\"{0}\" is a valid email")
    @ValueSource(strings = {"alice@school.edu", "bob.smith@university.ac.uk", "test+tag@mail.com"})
    void isValidEmail_acceptsCorrectFormat(String email) {
        assertTrue(validator.isValidEmail(email));
    }

    @ParameterizedTest(name = "\"{0}\" is NOT a valid email")
    @ValueSource(strings = {"not-an-email", "missing@domain", "@nodomain.com", "spaces in@email.com"})
    void isValidEmail_rejectsIncorrectFormat(String email) {
        assertFalse(validator.isValidEmail(email));
    }

    @ParameterizedTest(name = "\"{0}\" is a valid date")
    @ValueSource(strings = {"2026-07-28", "1999-01-01", "2000-12-31"})
    void isValidDate_acceptsCorrectFormat(String date) {
        assertTrue(validator.isValidDate(date));
    }

    @ParameterizedTest(name = "\"{0}\" is NOT a valid date")
    @ValueSource(strings = {"28-07-2026", "2026/07/28", "2026-7-28", "notadate"})
    void isValidDate_rejectsIncorrectFormat(String date) {
        assertFalse(validator.isValidDate(date));
    }

    @ParameterizedTest(name = "\"{0}\" is a valid course code")
    @ValueSource(strings = {"ENG101", "MATH999", "SCI100", "AB100"})
    void isValidCourseCode_acceptsCorrectFormat(String courseCode) {
        assertTrue(validator.isValidCourseCode(courseCode));
    }

    @ParameterizedTest(name = "\"{0}\" is NOT a valid course code")
    @ValueSource(strings = {"eng101", "ENGLISH101", "ENG10", "101ENG", "E101"})
    void isValidCourseCode_rejectsIncorrectFormat(String courseCode) {
        assertFalse(validator.isValidCourseCode(courseCode));
    }

    @ParameterizedTest(name = "phone \"{0}\" is valid")
    @ValueSource(strings = {"+250780905123", "0780905123", "+1 555 123 4567", "555-123-4567"})
    void isValidPhone_acceptsCommonFormats(String phone) {
        assertTrue(validator.isValidPhone(phone));
    }

    @ParameterizedTest(name = "phone \"{0}\" is NOT valid")
    @ValueSource(strings = {"abc", "123", ""})
    void isValidPhone_rejectsObviouslyInvalidInput(String phone) {
        assertFalse(validator.isValidPhone(phone));
    }

    @Test
    void isValidStudentId_null_returnsFalse() {
        assertFalse(validator.isValidStudentId(null));
    }
}