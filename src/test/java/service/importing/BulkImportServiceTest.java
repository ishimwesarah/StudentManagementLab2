package service.importing;

import exception.InvalidFileFormatException;
import model.CoreSubject;
import model.ElectiveSubject;
import model.RegularStudent;
import model.Student;
import model.Subject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GradeManager;
import service.StudentManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BulkImportService")
class BulkImportServiceTest {

    private StudentManager studentManager;
    private GradeManager gradeManager;
    private BulkImportService importService;
    private Student existingStudent;
    private Path tempFile;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        gradeManager = new GradeManager();

        existingStudent = new RegularStudent("Aline", 16, "aline@school.sch", "111");
        studentManager.addStudent(existingStudent);

        List<Subject> knownSubjects = List.of(
                new CoreSubject("Mathematics", "MATH101"),
                new ElectiveSubject("Art", "ART101")
        );

        importService = new BulkImportService(new CSVParser(), studentManager, gradeManager, knownSubjects);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    private Path writeTempCsv(String content) throws IOException {
        tempFile = Files.createTempFile("bulk-import-service-test", ".csv");
        Files.writeString(tempFile, content);
        return tempFile;
    }

    @Test
    @DisplayName("imports a valid row and adds the grade to the real GradeManager")
    void importFromFile_validRow_addsGrade() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        existingStudent.getStudentId() + ",Mathematics,Core,85\n"
        );

        BulkImportResult result = importService.importFromFile(file);

        assertEquals(1, result.getTotalRows());
        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(1, gradeManager.getGradeCount());
    }

    @Test
    @DisplayName("fails a row with an unknown student ID, without affecting other rows")
    void importFromFile_unknownStudentId_recordsFailure() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        "STU999,Mathematics,Core,85\n" +
                        existingStudent.getStudentId() + ",Art,Elective,70\n"
        );

        BulkImportResult result = importService.importFromFile(file);

        assertEquals(2, result.getTotalRows());
        assertEquals(1, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailures().get(0).getReason().contains("STU999"));
    }

    @Test
    @DisplayName("fails a row with a grade out of range")
    void importFromFile_gradeOutOfRange_recordsFailure() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        existingStudent.getStudentId() + ",Mathematics,Core,150\n"
        );

        BulkImportResult result = importService.importFromFile(file);

        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailures().get(0).getReason().contains("out of range"));
    }

    @Test
    @DisplayName("fails a row with an unknown subject")
    void importFromFile_unknownSubject_recordsFailure() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        existingStudent.getStudentId() + ",Chemistry,Core,85\n"
        );

        BulkImportResult result = importService.importFromFile(file);

        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailures().get(0).getReason().contains("Unknown subject"));
    }

    @Test
    @DisplayName("fails a row where subject type doesn't match the known subject's actual type")
    void importFromFile_subjectTypeMismatch_recordsFailure() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        existingStudent.getStudentId() + ",Mathematics,Elective,85\n"
        );

        BulkImportResult result = importService.importFromFile(file);

        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailures().get(0).getReason().contains("mismatch"));
    }

    @Test
    @DisplayName("processes a mixed file with both successes and failures, matching the row numbers")
    void importFromFile_mixedFile_countsCorrectly() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        existingStudent.getStudentId() + ",Mathematics,Core,85\n" +   // row 2 - success
                        "STU999,Mathematics,Core,90\n" +                              // row 3 - unknown student
                        existingStudent.getStudentId() + ",Art,Elective,105\n"        // row 4 - out of range
        );

        BulkImportResult result = importService.importFromFile(file);

        assertEquals(3, result.getTotalRows());
        assertEquals(1, result.getSuccessCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(3, result.getFailures().get(0).getRowNumber());
        assertEquals(4, result.getFailures().get(1).getRowNumber());
    }

    @Test
    @DisplayName("propagates InvalidFileFormatException for a bad header")
    void importFromFile_badHeader_throwsInvalidFileFormatException() throws IOException {
        Path file = writeTempCsv("Wrong,Header,Format\nfoo,bar,baz\n");

        assertThrows(InvalidFileFormatException.class, () -> importService.importFromFile(file));
    }
}