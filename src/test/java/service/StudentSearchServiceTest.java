package service;

import model.HonorsStudent;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("StudentSearchService")
class StudentSearchServiceTest {

    private StudentManager studentManager;
    private StudentSearchService searchService;

    private Student aliceJohnson;
    private Student bobJohnson;
    private Student carolMartinez;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        searchService = new StudentSearchService(studentManager);

        aliceJohnson = new RegularStudent("Alice Johnson", 16, "alice@school.sch", "111");
        aliceJohnson.recordGrade(80);

        bobJohnson = new HonorsStudent("Bob Johnson", 17, "bob@school.sch", "222");
        bobJohnson.recordGrade(90);

        carolMartinez = new RegularStudent("Carol Martinez", 15, "carol@school.sch", "333");
        carolMartinez.recordGrade(60);

        studentManager.addStudent(aliceJohnson);
        studentManager.addStudent(bobJohnson);
        studentManager.addStudent(carolMartinez);
    }

    @Test
    @DisplayName("searchById finds an exact, case-insensitive match")
    void searchById_exactMatch_findsStudent() {
        List<Student> results = searchService.searchById(aliceJohnson.getStudentId().toLowerCase());

        assertEquals(1, results.size());
        assertEquals(aliceJohnson, results.get(0));
    }

    @Test
    @DisplayName("searchById returns an empty list for an unknown ID")
    void searchById_unknownId_returnsEmptyList() {
        assertTrue(searchService.searchById("STU999").isEmpty());
    }

    @Test
    @DisplayName("searchByName finds all partial, case-insensitive matches")
    void searchByName_partialMatch_findsAllMatchingStudents() {
        List<Student> results = searchService.searchByName("john");

        assertEquals(2, results.size());
        assertTrue(results.contains(aliceJohnson));
        assertTrue(results.contains(bobJohnson));
    }

    @Test
    @DisplayName("searchByName returns an empty list when nothing matches")
    void searchByName_noMatch_returnsEmptyList() {
        assertTrue(searchService.searchByName("xyz").isEmpty());
    }

    @Test
    @DisplayName("searchByGradeRange finds students whose average falls within range, inclusive")
    void searchByGradeRange_findsStudentsInRange() {
        List<Student> results = searchService.searchByGradeRange(80, 90);

        assertEquals(2, results.size());
        assertTrue(results.contains(aliceJohnson));
        assertTrue(results.contains(bobJohnson));
    }

    @Test
    @DisplayName("searchByGradeRange excludes students outside the range")
    void searchByGradeRange_excludesOutsideRange() {
        List<Student> results = searchService.searchByGradeRange(85, 100);

        assertEquals(1, results.size());
        assertTrue(results.contains(bobJohnson));
    }

    @Test
    @DisplayName("searchByType finds only Honors students")
    void searchByType_honors_findsOnlyHonorsStudents() {
        List<Student> results = searchService.searchByType("Honors");

        assertEquals(1, results.size());
        assertTrue(results.contains(bobJohnson));
    }

    @Test
    @DisplayName("searchByType finds only Regular students")
    void searchByType_regular_findsOnlyRegularStudents() {
        List<Student> results = searchService.searchByType("Regular");

        assertEquals(2, results.size());
        assertTrue(results.contains(aliceJohnson));
        assertTrue(results.contains(carolMartinez));
    }
}