package service;

import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("StudentAverageCalculator")
class StudentAverageCalculatorTest {

    private final StudentAverageCalculator calculator = new StudentAverageCalculator();

    @Test
    @DisplayName("returns 0.0 for an empty student list")
    void classAverage_emptyList_returnsZero() {
        assertEquals(0.0, calculator.calculateClassAverage(new ArrayList<>()));
    }

    @Test
    @DisplayName("returns 0.0 for a null student list")
    void classAverage_nullList_returnsZero() {
        assertEquals(0.0, calculator.calculateClassAverage(null));
    }

    @Test
    @DisplayName("averages each student's own average grade")
    void classAverage_computesMeanOfStudentAverages() {
        Student a = new RegularStudent("Alice", 16, "a@school.sch", "111");
        a.recordGrade(80);
        a.recordGrade(90); // average = 85

        Student b = new RegularStudent("Bob", 16, "b@school.sch", "222");
        b.recordGrade(60);
        b.recordGrade(70); // average = 65

        List<Student> students = List.of(a, b);

        // (85 + 65) / 2 = 75
        assertEquals(75.0, calculator.calculateClassAverage(students));
    }

    @Test
    @DisplayName("round() rounds to the nearest whole number")
    void round_roundsToNearestWholeNumber() {
        assertEquals(83.0, calculator.round(82.6));
        assertEquals(82.0, calculator.round(82.4));
    }
}