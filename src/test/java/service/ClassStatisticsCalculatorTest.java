package service;

import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ClassStatisticsCalculator")
class ClassStatisticsCalculatorTest {

    private ClassStatisticsCalculator stats;
    private CoreSubject math;
    private ElectiveSubject art;

    @BeforeEach
    void setUp() {
        stats = new ClassStatisticsCalculator();
        math = new CoreSubject("Mathematics", "MATH101");
        art = new ElectiveSubject("Art", "ART101");
    }

    @Test
    @DisplayName("mean computes the simple average")
    void mean_computesAverage() {
        List<Grade> grades = List.of(new Grade("STU001", math, 80), new Grade("STU001", math, 90));
        assertEquals(85.0, stats.mean(grades));
    }

    @Test
    @DisplayName("mean is 0.0 for an empty list")
    void mean_emptyList_returnsZero() {
        assertEquals(0.0, stats.mean(List.of()));
    }

    @Test
    @DisplayName("median is the middle value for an odd count")
    void median_oddCount_returnsMiddleValue() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 90),
                new Grade("STU001", math, 70),
                new Grade("STU001", math, 80)
        );
        assertEquals(80.0, stats.median(grades));
    }

    @Test
    @DisplayName("median averages the two middle values for an even count")
    void median_evenCount_averagesMiddleTwo() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 70),
                new Grade("STU001", math, 100),
                new Grade("STU001", math, 80),
                new Grade("STU001", math, 90)
        );
        assertEquals(85.0, stats.median(grades));
    }

    @Test
    @DisplayName("mode returns the most frequent value")
    void mode_returnsMostFrequentValue() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 80),
                new Grade("STU001", math, 80),
                new Grade("STU001", math, 90)
        );
        assertEquals(80.0, stats.mode(grades));
    }

    @Test
    @DisplayName("mode breaks ties by choosing the smaller value")
    void mode_tie_choosesSmallerValue() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 90),
                new Grade("STU001", math, 80)
        );
        assertEquals(80.0, stats.mode(grades));
    }

    @Test
    @DisplayName("standard deviation is 0.0 with fewer than 2 grades")
    void standardDeviation_fewerThanTwoGrades_returnsZero() {
        assertEquals(0.0, stats.standardDeviation(List.of()));
        assertEquals(0.0, stats.standardDeviation(List.of(new Grade("STU001", math, 80))));
    }

    @Test
    @DisplayName("standard deviation computes correctly for a simple symmetric case")
    void standardDeviation_symmetricCase_computesCorrectly() {
        // mean = 80, diffs -10/+10, variance = (100+100)/2 = 100, sqrt = 10
        List<Grade> grades = List.of(new Grade("STU001", math, 70), new Grade("STU001", math, 90));
        assertEquals(10.0, stats.standardDeviation(grades));
    }

    @Test
    @DisplayName("highest and lowest find the extremes")
    void highestAndLowest_findExtremes() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 70),
                new Grade("STU001", math, 90),
                new Grade("STU001", math, 50)
        );
        assertEquals(90.0, stats.highest(grades));
        assertEquals(50.0, stats.lowest(grades));
    }

    @Test
    @DisplayName("highest and lowest are 0.0 for an empty list")
    void highestAndLowest_emptyList_returnZero() {
        assertEquals(0.0, stats.highest(List.of()));
        assertEquals(0.0, stats.lowest(List.of()));
    }

    @Test
    @DisplayName("gradeDistribution buckets scores into A/B/C/D/F correctly")
    void gradeDistribution_bucketsCorrectly() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 95), // A
                new Grade("STU001", math, 91), // A
                new Grade("STU001", math, 85), // B
                new Grade("STU001", math, 75), // C
                new Grade("STU001", math, 65), // D
                new Grade("STU001", math, 50)  // F
        );

        int[] distribution = stats.gradeDistribution(grades);

        assertEquals(2, distribution[0]); // A
        assertEquals(1, distribution[1]); // B
        assertEquals(1, distribution[2]); // C
        assertEquals(1, distribution[3]); // D
        assertEquals(1, distribution[4]); // F
    }

    @Test
    @DisplayName("gradeDistribution treats exact boundary scores correctly")
    void gradeDistribution_boundaryScores() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 90), // A (>= 90)
                new Grade("STU001", math, 80), // B (>= 80)
                new Grade("STU001", math, 70), // C (>= 70)
                new Grade("STU001", math, 60), // D (>= 60)
                new Grade("STU001", math, 59)  // F (< 60)
        );

        int[] distribution = stats.gradeDistribution(grades);

        assertEquals(1, distribution[0]);
        assertEquals(1, distribution[1]);
        assertEquals(1, distribution[2]);
        assertEquals(1, distribution[3]);
        assertEquals(1, distribution[4]);
    }

    @Test
    @DisplayName("averageForSubject only averages grades in that subject")
    void averageForSubject_filtersBySubjectName() {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 80),
                new Grade("STU001", math, 90),
                new Grade("STU001", art, 100)
        );

        assertEquals(85.0, stats.averageForSubject(grades, "Mathematics"));
        assertEquals(100.0, stats.averageForSubject(grades, "Art"));
        assertEquals(0.0, stats.averageForSubject(grades, "English"));
    }

    @Test
    @DisplayName("regularStudentAverage and honorsStudentAverage only average their own type")
    void studentTypeAverages_filterByType() {
        Student regular1 = new RegularStudent("Aline", 16, "a@school.sch", "111");
        regular1.recordGrade(80);

        Student regular2 = new RegularStudent("Bob", 16, "b@school.sch", "222");
        regular2.recordGrade(60);

        Student honors1 = new HonorsStudent("Jado", 17, "j@school.sch", "333");
        honors1.recordGrade(90);

        List<Student> students = List.of(regular1, regular2, honors1);

        assertEquals(70.0, stats.regularStudentAverage(students)); // (80+60)/2
        assertEquals(90.0, stats.honorsStudentAverage(students));
    }

    @Test
    @DisplayName("round() rounds to one decimal place")
    void round_roundsToOneDecimalPlace() {
        assertEquals(83.3, stats.round(83.26));
    }
}