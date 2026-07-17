package service;

import exception.ReportExportException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FileExporter")
class FileExporterTest {

    private FileExporter fileExporter;
    private Path originalWorkingDir;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        fileExporter = new FileExporter();
        // FileExporter always writes under "reports/" relative to the
        // working directory, so for a clean test we create and clean up
        // a reports/ folder inside a temp directory rather than touching
        // the real project's reports/ folder.
        tempDir = Files.createTempDirectory("file-exporter-test");
        originalWorkingDir = Path.of(System.getProperty("user.dir"));
        System.setProperty("user.dir", tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setProperty("user.dir", originalWorkingDir.toString());
        deleteRecursively(tempDir);
    }

    @Test
    @DisplayName("exportToFile creates the reports directory and writes the content")
    void exportToFile_writesContentToReportsDirectory() throws ReportExportException, IOException {
        Path written = fileExporter.exportToFile("test_report", "Hello, report!");

        assertTrue(Files.exists(written));
        assertEquals("Hello, report!", Files.readString(written));
        assertTrue(written.toString().endsWith("test_report.txt"));
    }

    @Test
    @DisplayName("fileSizeInKb returns a positive value for a written file")
    void fileSizeInKb_returnsPositiveValueForRealFile() throws ReportExportException {
        Path written = fileExporter.exportToFile("size_test", "Some content that takes up a bit of space.");

        double sizeKb = fileExporter.fileSizeInKb(written);

        assertTrue(sizeKb > 0.0);
    }

    @Test
    @DisplayName("fileSizeInKb returns 0.0 for a nonexistent file")
    void fileSizeInKb_nonexistentFile_returnsZero() {
        Path fakePath = tempDir.resolve("does_not_exist.txt");

        assertEquals(0.0, fileExporter.fileSizeInKb(fakePath));
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