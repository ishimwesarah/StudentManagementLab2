package service;

import model.CoreSubject;
import model.Grade;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GpaRankingService")
class GpaRankingServiceTest {

    private StudentManager studentManager;
    private GradeManager gradeManager;
    private GpaRankingService rankingService;
    private CoreSubject math;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
        rankingService = new GpaRankingService(gradeManager, studentManager, new GPACalculator());
        math = new CoreSubject("Mathematics", "MATH101");
    }

    private Student addStudentWithGrade(String name, double score) {
        Student student = new RegularStudent(name, 16, name + "@school.sch", "000");
        studentManager.addStudent(student);
        gradeManager.addGrade(new Grade(student.getStudentId(), math, score));
        student.recordGrade(score);
        return student;
    }

    @Test
    @DisplayName("buildRankingMap keeps GPA keys sorted ascending, TreeMap's natural order")
    void buildRankingMap_keysAreSortedAscending() {
        addStudentWithGrade("Low", 65);
        addStudentWithGrade("High", 95);
        addStudentWithGrade("Mid", 80);

        TreeMap<Double, List<Student>> rankings = rankingService.buildRankingMap();

        assertEquals(rankings.firstKey(), rankings.navigableKeySet().first());
        assertTrue(rankings.firstKey() < rankings.lastKey());
    }

    @Test
    @DisplayName("students with identical GPAs are grouped together under one key")
    void buildRankingMap_tiedStudents_shareOneKey() {
        addStudentWithGrade("Alice", 95);
        addStudentWithGrade("Bob", 95);

        TreeMap<Double, List<Student>> rankings = rankingService.buildRankingMap();

        assertEquals(1, rankings.size());
        assertEquals(2, rankings.firstEntry().getValue().size());
    }

    @Test
    @DisplayName("getTopStudents returns the highest-GPA students first")
    void getTopStudents_returnsHighestFirst() {
        Student low = addStudentWithGrade("Low", 65);
        Student high = addStudentWithGrade("High", 95);
        Student mid = addStudentWithGrade("Mid", 80);

        List<Student> top2 = rankingService.getTopStudents(2);

        assertEquals(2, top2.size());
        assertEquals(high, top2.get(0));
        assertEquals(mid, top2.get(1));
    }

    @Test
    @DisplayName("calculateRank gives rank 1 to the highest GPA")
    void calculateRank_highestGpa_isRankOne() {
        Student high = addStudentWithGrade("High", 95);
        addStudentWithGrade("Low", 65);

        assertEquals(1, rankingService.calculateRank(high));
    }

    @Test
    @DisplayName("calculateRank correctly skips past a tied group to rank the next distinct GPA")
    void calculateRank_afterTiedGroup_skipsCorrectly() {
        addStudentWithGrade("Alice", 95);
        addStudentWithGrade("Bob", 95);
        Student third = addStudentWithGrade("Carol", 80);

        // Alice and Bob are tied for rank 1 (both at 95). Carol, the next
        // distinct GPA, should be rank 3, not rank 2 - the tied pair
        // occupies two of the top three positions.
        assertEquals(3, rankingService.calculateRank(third));
    }

    @Test
    @DisplayName("calculateRank gives tied students the same rank")
    void calculateRank_tiedStudents_getSameRank() {
        Student alice = addStudentWithGrade("Alice", 95);
        Student bob = addStudentWithGrade("Bob", 95);

        assertEquals(rankingService.calculateRank(alice), rankingService.calculateRank(bob));
    }
}