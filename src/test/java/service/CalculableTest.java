package service;

import model.Calculable;
import model.CoreSubject;
import model.Grade;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Proves that GradeAverageCalculator and StudentAverageCalculator - two
 * calculators built for completely unrelated data types - can both be
 * used through the exact same generic Calculable<T> interface. This is
 * the actual point of making Calculable generic rather than writing two
 * separate, near-identical interfaces.
 */
@DisplayName("Calculable<T> - generic interface used across unrelated types")
class CalculableTest {

    @Test
    @DisplayName("Calculable<Grade> and Calculable<Student> both work through the same interface shape")
    void bothCalculators_workThroughSharedGenericInterface() {
        CoreSubject math = new CoreSubject("Mathematics", "MATH101");

        Calculable<Grade> gradeCalculator = new GradeAverageCalculator();
        List<Grade> grades = List.of(
                new Grade("STU001", math, 80),
                new Grade("STU001", math, 90)
        );

        Calculable<Student> studentCalculator = new StudentAverageCalculator();
        Student alice = new RegularStudent("Alice", 16, "alice@school.sch", "111");
        alice.recordGrade(70);
        Student bob = new RegularStudent("Bob", 16, "bob@school.sch", "222");
        bob.recordGrade(90);
        List<Student> students = List.of(alice, bob);

        // Same method name, same interface type, two genuinely different
        // T's (Grade vs Student) - the compiler enforces each one stays
        // correctly typed to its own data, with no casting required anywhere.
        double gradeResult = gradeCalculator.calculate(grades);
        double studentResult = studentCalculator.calculate(students);

        assertEquals(85.0, gradeResult);
        assertEquals(80.0, studentResult);
    }
}