package service;

import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GradeManager")
class GradeManagerTest {

    private GradeManager gradeManager;
    private CoreSubject math;
    private ElectiveSubject art;

    @BeforeEach
    void setUp() {
        gradeManager = new GradeManager();
        math = new CoreSubject("Mathematics", "MATH101");
        art = new ElectiveSubject("Art", "ART101");
    }

    @Test
    @DisplayName("addGrade increases the grade count")
    void addGrade_increasesCount() {
        assertTrue(gradeManager.addGrade(new Grade("STU001", math, 85)));
        assertEquals(1, gradeManager.getGradeCount());
    }

    @Test
    @DisplayName("getGradesByStudent only returns grades for that student")
    void getGradesByStudent_filtersCorrectly() {
        gradeManager.addGrade(new Grade("STU001", math, 85));
        gradeManager.addGrade(new Grade("STU002", art, 70));
        gradeManager.addGrade(new Grade("STU001", art, 90));

        List<Grade> aliceGrades = gradeManager.getGradesByStudent("STU001");

        assertEquals(2, aliceGrades.size());
        for (Grade g : aliceGrades) {
            assertEquals("STU001", g.getStudentId());
        }
    }

    @Test
    @DisplayName("getGradesByStudent returns an empty list for a student with no grades")
    void getGradesByStudent_noGrades_returnsEmptyList() {
        assertTrue(gradeManager.getGradesByStudent("STU999").isEmpty());
    }

    @Test
    @DisplayName("getAllGrades returns every stored grade")
    void getAllGrades_returnsEverything() {
        gradeManager.addGrade(new Grade("STU001", math, 85));
        gradeManager.addGrade(new Grade("STU002", art, 70));

        assertEquals(2, gradeManager.getAllGrades().size());
    }
}