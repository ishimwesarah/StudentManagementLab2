package service.exporting;

import exception.ReportExportException;
import model.CoreSubject;
import model.Grade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BinaryGradeExporter")
class BinaryGradeExporterTest {

    private BinaryGradeExporter exporter;
    private Path tempDir;
    private CoreSubject math;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("binary-exporter-test");
        exporter = new BinaryGradeExporter(tempDir);
        math = new CoreSubject("Mathematics", "MATH101");
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteRecursively(tempDir);
    }

    @Test
    @DisplayName("getFormatName returns Binary")
    void getFormatName_returnsBinary() {
        assertEquals("Binary", exporter.getFormatName());
    }

    @Test
    @DisplayName("export creates a real, non-empty file")
    void export_createsFile() throws ReportExportException, IOException {
        List<Grade> grades = List.of(new Grade("STU001", math, 85));

        Path written = exporter.export(grades, "test_export");

        assertTrue(Files.exists(written));
        assertTrue(Files.size(written) > 0);
    }

    @Test
    @DisplayName("a serialized file can be read back into real Grade objects with the same data")
    void export_thenReadBack_dataSurvivesIntact() throws ReportExportException, IOException, ClassNotFoundException {
        List<Grade> original = List.of(
                new Grade("STU001", math, 85),
                new Grade("STU002", math, 90)
        );

        Path written = exporter.export(original, "roundtrip_test");

        List<Grade> readBack;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(written))) {
            @SuppressWarnings("unchecked")
            List<Grade> result = (List<Grade>) in.readObject();
            readBack = result;
        }

        assertEquals(2, readBack.size());
        assertEquals("STU001", readBack.get(0).getStudentId());
        assertEquals(85.0, readBack.get(0).getGrade());
        assertEquals("Mathematics", readBack.get(0).getSubject().getSubjectName());
        assertEquals("STU002", readBack.get(1).getStudentId());
    }

    @Test
    @DisplayName("export with no grades still produces a valid, readable file")
    void export_noGrades_stillProducesReadableFile() throws ReportExportException, IOException, ClassNotFoundException {
        Path written = exporter.export(List.of(), "empty_export");

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(written))) {
            @SuppressWarnings("unchecked")
            List<Grade> result = (List<Grade>) in.readObject();
            assertTrue(result.isEmpty());
        }
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