package service.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ImportDirectoryWatcher")
class ImportDirectoryWatcherTest {

    private Path watchedDir;
    private ImportDirectoryWatcher watcher;
    private CopyOnWriteArrayList<Path> detectedFiles;

    @BeforeEach
    void setUp() throws IOException {
        watchedDir = Files.createTempDirectory("watcher-test");
        detectedFiles = new CopyOnWriteArrayList<>();
        watcher = new ImportDirectoryWatcher(watchedDir, detectedFiles::add);
    }

    @AfterEach
    void tearDown() throws IOException {
        watcher.stop();
        deleteRecursively(watchedDir);
    }

    @Test
    @DisplayName("detects a new CSV file dropped into the watched directory")
    void start_detectsNewCsvFile() throws IOException, InterruptedException {
        watcher.start();

        Path newFile = watchedDir.resolve("october_grades.csv");
        Files.writeString(newFile, "StudentID,SubjectName,SubjectType,Grade\n");

        boolean detected = waitUntil(() -> !detectedFiles.isEmpty(), 3000);

        assertTrue(detected, "Expected the new CSV file to be detected within 3 seconds");
        assertEquals(1, detectedFiles.size());
        assertTrue(detectedFiles.get(0).toString().endsWith("october_grades.csv"));
    }

    @Test
    @DisplayName("ignores non-CSV files created in the watched directory")
    void start_ignoresNonCsvFiles() throws IOException, InterruptedException {
        watcher.start();

        Path newFile = watchedDir.resolve("notes.txt");
        Files.writeString(newFile, "just some notes");

        // Give the watcher a genuine chance to react, then confirm it
        // deliberately did NOT report this file.
        Thread.sleep(1000);

        assertTrue(detectedFiles.isEmpty(), "A .txt file should not have been reported as a CSV import");
    }

    @Test
    @DisplayName("detects multiple CSV files dropped one after another")
    void start_detectsMultipleFiles() throws IOException, InterruptedException {
        watcher.start();

        Files.writeString(watchedDir.resolve("batch1.csv"), "header\n");
        Files.writeString(watchedDir.resolve("batch2.csv"), "header\n");

        boolean detected = waitUntil(() -> detectedFiles.size() >= 2, 3000);

        assertTrue(detected, "Expected both CSV files to be detected within 3 seconds");
    }

    private boolean waitUntil(java.util.function.BooleanSupplier condition, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            Thread.sleep(50);
        }
        return false;
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