package service;

import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CourseTracker")
class CourseTrackerTest {

    private GradeManager gradeManager;
    private CourseTracker courseTracker;
    private CoreSubject math;
    private ElectiveSubject art;

    @BeforeEach
    void setUp() {
        gradeManager = new GradeManager();
        courseTracker = new CourseTracker(gradeManager);
        math = new CoreSubject("Mathematics", "MATH101");
        art = new ElectiveSubject("Art", "ART101");
    }

    @Test
    @DisplayName("multiple grades in the same subject collapse into one unique entry")
    void getUniqueCourses_duplicatesCollapseToOne() {
        gradeManager.addGrade(new Grade("STU001", math, 80));
        gradeManager.addGrade(new Grade("STU002", math, 90));
        gradeManager.addGrade(new Grade("STU003", math, 70));

        Set<String> courses = courseTracker.getUniqueCourses();

        assertEquals(1, courses.size());
        assertTrue(courses.contains("Mathematics"));
    }

    @Test
    @DisplayName("distinct subjects are all tracked separately")
    void getUniqueCourses_tracksDistinctSubjects() {
        gradeManager.addGrade(new Grade("STU001", math, 80));
        gradeManager.addGrade(new Grade("STU001", art, 90));

        Set<String> courses = courseTracker.getUniqueCourses();

        assertEquals(2, courses.size());
        assertTrue(courses.contains("Mathematics"));
        assertTrue(courses.contains("Art"));
    }

    @Test
    @DisplayName("getUniqueCourseCount matches the actual set size")
    void getUniqueCourseCount_matchesSetSize() {
        gradeManager.addGrade(new Grade("STU001", math, 80));
        gradeManager.addGrade(new Grade("STU001", art, 90));
        gradeManager.addGrade(new Grade("STU002", math, 70));

        assertEquals(2, courseTracker.getUniqueCourseCount());
    }

    @Test
    @DisplayName("getUniqueCoursesForStudent only counts that student's own courses")
    void getUniqueCoursesForStudent_filtersToOneStudent() {
        gradeManager.addGrade(new Grade("STU001", math, 80));
        gradeManager.addGrade(new Grade("STU001", art, 90));
        gradeManager.addGrade(new Grade("STU002", math, 70));

        Set<String> aliceCourses = courseTracker.getUniqueCoursesForStudent("STU001");

        assertEquals(2, aliceCourses.size());
        assertTrue(aliceCourses.contains("Mathematics"));
        assertTrue(aliceCourses.contains("Art"));
    }

    @Test
    @DisplayName("returns an empty set when there are no grades at all")
    void getUniqueCourses_noGrades_returnsEmptySet() {
        assertTrue(courseTracker.getUniqueCourses().isEmpty());
    }
}