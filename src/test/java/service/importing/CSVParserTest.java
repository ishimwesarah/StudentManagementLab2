package service.importing;

import exception.InvalidFileFormatException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CSVParser")
class CSVParserTest {

    private CSVParser parser;
    private Path tempFile;

    @BeforeEach
    void setUp() {
        parser = new CSVParser();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    private Path writeTempCsv(String content) throws IOException {
        tempFile = Files.createTempFile("bulk-import-test", ".csv");
        Files.writeString(tempFile, content);
        return tempFile;
    }

    @Test
    @DisplayName("parses valid rows into CSVGradeRecords")
    void parse_validFile_returnsRecords() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        "STU001,Mathematics,Core,85\n" +
                        "STU002,Art,Elective,92\n"
        );

        List<CSVGradeRecord> records = parser.parse(file);

        assertEquals(2, records.size());
        assertEquals("STU001", records.get(0).getStudentId());
        assertEquals("Mathematics", records.get(0).getSubjectName());
        assertEquals("Core", records.get(0).getSubjectType());
        assertEquals("85", records.get(0).getRawGrade());
    }

    @Test
    @DisplayName("row numbers are 1-indexed matching spreadsheet row positions")
    void parse_assignsCorrectRowNumbers() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        "STU001,Mathematics,Core,85\n" +
                        "STU002,Art,Elective,92\n"
        );

        List<CSVGradeRecord> records = parser.parse(file);

        assertEquals(2, records.get(0).getRowNumber());
        assertEquals(3, records.get(1).getRowNumber());
    }

    @Test
    @DisplayName("skips blank lines without treating them as data rows")
    void parse_skipsBlankLines() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        "STU001,Mathematics,Core,85\n" +
                        "\n" +
                        "STU002,Art,Elective,92\n"
        );

        List<CSVGradeRecord> records = parser.parse(file);

        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("skips rows with the wrong number of columns")
    void parse_skipsMalformedRows() throws IOException, InvalidFileFormatException {
        Path file = writeTempCsv(
                "StudentID,SubjectName,SubjectType,Grade\n" +
                        "STU001,Mathematics,Core,85\n" +
                        "STU002,Art,Elective\n" + // missing grade column
                        "STU003,Science,Core,90\n"
        );

        List<CSVGradeRecord> records = parser.parse(file);

        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("throws InvalidFileFormatException for a missing/wrong header")
    void parse_wrongHeader_throws() throws IOException {
        Path file = writeTempCsv(
                "Name,Subject,Type,Score\n" +
                        "STU001,Mathematics,Core,85\n"
        );

        assertThrows(InvalidFileFormatException.class, () -> parser.parse(file));
    }

    @Test
    @DisplayName("throws InvalidFileFormatException for an empty file")
    void parse_emptyFile_throws() throws IOException {
        Path file = writeTempCsv("");

        assertThrows(InvalidFileFormatException.class, () -> parser.parse(file));
    }

    @Test
    @DisplayName("throws InvalidFileFormatException for a nonexistent file")
    void parse_nonexistentFile_throws() {
        Path fakePath = Path.of("does_not_exist_12345.csv");

        assertThrows(InvalidFileFormatException.class, () -> parser.parse(fakePath));
    }
}