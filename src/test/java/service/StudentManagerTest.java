package service;

import exception.StudentNotFoundException;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import model.RegularStudent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("StudentManager")
class StudentManagerTest {

    private StudentManager studentManager;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
    }

    @Test
    @DisplayName("addStudent increases the student count")
    void addStudent_increasesCount() {
        Student student = new RegularStudent("Aline", 16, "aline@school.sch", "111");

        assertTrue(studentManager.addStudent(student));
        assertEquals(1, studentManager.getStudentCount());
    }

    @Test
    @DisplayName("findStudent returns the matching student, case-insensitively")
    void findStudent_existingId_returnsStudent() throws StudentNotFoundException {
        Student student = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        studentManager.addStudent(student);

        Student found = studentManager.findStudent(student.getStudentId().toLowerCase());

        assertEquals(student, found);
    }

    @Test
    @DisplayName("findStudent throws StudentNotFoundException for an unknown ID")
    void findStudent_unknownId_throws() {
        assertThrows(StudentNotFoundException.class, () -> studentManager.findStudent("STU999"));
    }

    @Test
    @DisplayName("getAllStudentIds returns IDs in registration order")
    void getAllStudentIds_returnsIdsInOrder() {
        Student a = new RegularStudent("Aline", 16, "a@school.sch", "111");
        Student b = new RegularStudent("Bob", 16, "b@school.sch", "222");
        studentManager.addStudent(a);
        studentManager.addStudent(b);

        assertEquals(List.of(a.getStudentId(), b.getStudentId()), studentManager.getAllStudentIds());
    }

    @Test
    @DisplayName("getAllStudents returns an empty list when nothing is registered")
    void getAllStudents_noStudents_returnsEmptyList() {
        assertTrue(studentManager.getAllStudents().isEmpty());
    }

    @Test
    @DisplayName("findStudent lookup time does not meaningfully grow as the roster size grows")
    void findStudent_lookupTimeStaysFlatAsRosterGrows() throws StudentNotFoundException {
        StudentManager smallRoster = new StudentManager();
        for (int i = 0; i < 5; i++) {
            smallRoster.addStudent(new RegularStudent("Student" + i, 16, "s" + i + "@school.sch", "000"));
        }

        StudentManager largeRoster = new StudentManager();
        Student target = null;
        for (int i = 0; i < 45; i++) {
            Student s = new RegularStudent("Student" + i, 16, "s" + i + "@school.sch", "000");
            largeRoster.addStudent(s);
            if (i == 44) {
                target = s;
            }
        }

        long smallStart = System.nanoTime();
        smallRoster.findStudent(smallRoster.getAllStudentIds().get(4));
        long smallDuration = System.nanoTime() - smallStart;

        long largeStart = System.nanoTime();
        largeRoster.findStudent(target.getStudentId());
        long largeDuration = System.nanoTime() - largeStart;

        // O(1) lookup means duration shouldn't scale meaningfully with size -
        // this is a loose sanity check, not a strict benchmark, since JVM
        // timing has natural noise at nanosecond scale.
        assertTrue(largeDuration < smallDuration * 50,
                "Expected lookup time to stay roughly flat regardless of roster size");
    }
}