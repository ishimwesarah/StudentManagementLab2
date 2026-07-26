package service;

import model.RegularStudent;
import model.Searchable;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RegexStudentSearchService")
class RegexStudentSearchServiceTest {

    private StudentManager studentManager;
    private RegexStudentSearchService regexSearch;

    private Student alice;
    private Student bob;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        regexSearch = new RegexStudentSearchService(studentManager);

        alice = new RegularStudent("Alice Johnson", 16, "alice@university.edu", "111");
        bob = new RegularStudent("Bob Smith", 17, "bob@gmail.com", "222");

        studentManager.addStudent(alice);
        studentManager.addStudent(bob);
    }

    @Test
    @DisplayName("search matches students by email domain pattern")
    void search_matchesByEmailDomain() {
        List<Student> results = regexSearch.search(".*@university\\.edu$");

        assertEquals(1, results.size());
        assertTrue(results.contains(alice));
    }

    @Test
    @DisplayName("search matches students by a name pattern, case-insensitively")
    void search_matchesByNamePattern_caseInsensitive() {
        List<Student> results = regexSearch.search("^bob");

        assertEquals(1, results.size());
        assertTrue(results.contains(bob));
    }

    @Test
    @DisplayName("search returns an empty list when the pattern matches nothing")
    void search_noMatch_returnsEmptyList() {
        assertTrue(regexSearch.search("^ZZZ999$").isEmpty());
    }

    @Test
    @DisplayName("search throws IllegalArgumentException for a malformed pattern")
    void search_malformedPattern_throws() {
        assertThrows(IllegalArgumentException.class, () -> regexSearch.search("["));
    }

    @Test
    @DisplayName("searchByIdPattern matches the standard STU### ID format")
    void searchByIdPattern_matchesStudentIdFormat() {
        List<Student> results = regexSearch.searchByIdPattern("^STU\\d{3}$");

        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("both Searchable implementations can be used interchangeably through the interface type")
    void bothImplementations_workThroughSharedInterfaceType() {
        Searchable stringSearch = new StudentSearchService(studentManager);
        Searchable patternSearch = new RegexStudentSearchService(studentManager);

        // Same variable type, same method call, two completely different
        // matching strategies underneath - this is the actual point of
        // the Searchable interface.
        List<Student> resultsFromString = stringSearch.search("alice");
        List<Student> resultsFromRegex = patternSearch.search("^alice");

        assertEquals(1, resultsFromString.size());
        assertEquals(1, resultsFromRegex.size());
        assertTrue(resultsFromString.contains(alice));
        assertTrue(resultsFromRegex.contains(alice));
    }
}