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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Proves that Calculable<T> genuinely earns its keep as a generic
 * interface, in two different ways:
 *
 * 1. The SAME method call (calculate()) works correctly across two
 *    completely unrelated data types (Grade vs Student), with the
 *    compiler enforcing type safety at every call site - no casting,
 *    no Object, no risk of mixing up which list belongs to which
 *    calculator.
 *
 * 2. TWO DIFFERENT classes can implement Calculable<Grade> with the
 *    same T, yet produce genuinely different results - proving the
 *    interface describes a shared SHAPE of operation, not a single
 *    hardcoded calculation.
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

    @Test
    @DisplayName("two different Calculable<Grade> implementers produce genuinely different results from the same data")
    void twoImplementersOfSameT_produceDifferentResults() {
        CoreSubject math = new CoreSubject("Mathematics", "MATH101");
        List<Grade> grades = List.of(
                new Grade("STU001", math, 95),
                new Grade("STU001", math, 65)
        );

        // Both of these are Calculable<Grade> - identical T - but they
        // represent completely different calculations underneath.
        Calculable<Grade> percentageAverage = new GradeAverageCalculator();
        Calculable<Grade> gpaAverage = new GPACalculator();

        double percentageResult = percentageAverage.calculate(grades);
        double gpaResult = gpaAverage.calculate(grades);

        // (95 + 65) / 2 = 80.0 as a plain percentage average
        assertEquals(80.0, percentageResult);

        // 95 -> 4.0 GPA points, 65 -> 1.0 GPA points, average = 2.5
        assertEquals(2.5, gpaResult);

        // The real point: same interface, same T, same input data,
        // but calculate() means something different depending on WHICH
        // implementer you're holding - proving the interface describes
        // a shared shape, not a single fixed formula.
        assertNotEquals(percentageResult, gpaResult);
    }
}