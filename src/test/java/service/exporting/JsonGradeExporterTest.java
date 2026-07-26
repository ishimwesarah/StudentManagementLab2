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

@DisplayName("JsonGradeExporter")
class JsonGradeExporterTest {

    private JsonGradeExporter exporter;
    private Path tempDir;
    private CoreSubject math;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("json-exporter-test");
        exporter = new JsonGradeExporter(tempDir);
        math = new CoreSubject("Mathematics", "MATH101");
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteRecursively(tempDir);
    }

    @Test
    @DisplayName("getFormatName returns JSON")
    void getFormatName_returnsJson() {
        assertEquals("JSON", exporter.getFormatName());
    }

    @Test
    @DisplayName("export writes a valid-looking JSON array with one object per grade")
    void export_writesJsonArray() throws ReportExportException, IOException {
        List<Grade> grades = List.of(
                new Grade("STU001", math, 85),
                new Grade("STU002", math, 90)
        );

        Path written = exporter.export(grades, "test_export");
        String content = Files.readString(written);

        assertTrue(content.startsWith("["));
        assertTrue(content.trim().endsWith("]"));
        assertTrue(content.contains("\"studentId\": \"STU001\""));
        assertTrue(content.contains("\"studentId\": \"STU002\""));
        assertTrue(content.contains("\"grade\": 85.0"));
    }

    @Test
    @DisplayName("export with no grades still writes a valid empty array")
    void export_noGrades_writesEmptyArray() throws ReportExportException, IOException {
        Path written = exporter.export(List.of(), "empty_export");
        String content = Files.readString(written);

        assertEquals("[\n]\n", content);
    }

    @Test
    @DisplayName("export does not leave a trailing comma after the last item")
    void export_noTrailingComma() throws ReportExportException, IOException {
        List<Grade> grades = List.of(new Grade("STU001", math, 85));

        Path written = exporter.export(grades, "single_export");
        String content = Files.readString(written);

        assertTrue(!content.contains("}\n  ,"));
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