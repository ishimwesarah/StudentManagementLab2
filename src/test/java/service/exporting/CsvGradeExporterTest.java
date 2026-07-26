package service.exporting;

import exception.ReportExportException;
import model.CoreSubject;
import model.Grade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CsvGradeExporter")
class CsvGradeExporterTest {

    private CsvGradeExporter exporter;
    private Path tempDir;
    private CoreSubject math;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("csv-exporter-test");
        exporter = new CsvGradeExporter(tempDir);
        math = new CoreSubject("Mathematics", "MATH101");
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteRecursively(tempDir);
    }

    @Test
    @DisplayName("getFormatName returns CSV")
    void getFormatName_returnsCsv() {
        assertEquals("CSV", exporter.getFormatName());
    }

    @Test
    @DisplayName("export writes a header row followed by one row per grade")
    void export_writesHeaderAndDataRows() throws ReportExportException, IOException {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 85),
                new Grade("STU002", math, 90)
        );

        Path written = exporter.export(grades, "test_export");
        String content = Files.readString(written);

        assertTrue(content.startsWith("StudentID,SubjectName,SubjectType,Grade"));
        assertTrue(content.contains("STU001,Mathematics,Core,85.0"));
        assertTrue(content.contains("STU002,Mathematics,Core,90.0"));
    }

    @Test
    @DisplayName("export with no grades still writes just the header row")
    void export_noGrades_writesHeaderOnly() throws ReportExportException, IOException {
        Path written = exporter.export(List.of(), "empty_export");
        String content = Files.readString(written);

        assertEquals("StudentID,SubjectName,SubjectType,Grade\n", content);
    }

    @Test
    @DisplayName("export creates the target directory if it doesn't exist yet")
    void export_createsTargetDirectory() throws ReportExportException {
        Path written = exporter.export(List.of(new Grade("STU001", math, 85)), "nested_test");

        assertTrue(Files.exists(written));
    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                for (Path child : stream.toList()) {
                    deleteRecursively(child);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}