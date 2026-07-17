package service;

import exception.StudentNotFoundException;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}