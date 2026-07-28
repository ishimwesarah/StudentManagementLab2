package service.concurrency;

import exception.ReportExportException;
import model.CoreSubject;
import model.Exportable;
import model.Grade;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GradeManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ConcurrentBatchExporter")
class ConcurrentBatchExporterTest {

    private GradeManager gradeManager;
    private CoreSubject math;

    @BeforeEach
    void setUp() {
        gradeManager = new GradeManager();
        math = new CoreSubject("Mathematics", "MATH101");
    }

    private Student addStudentWithGrade(String name, double score) {
        Student student = new RegularStudent(name, 16, name + "@school.sch", "000");
        gradeManager.addGrade(new Grade(student.getStudentId(), math, score));
        return student;
    }

    @Test
    @DisplayName("exportAll succeeds for every student and reports zero failures")
    void exportAll_allStudentsSucceed() throws InterruptedException, IOException {
        Path tempDir = Files.createTempDirectory("batch-export-test");
        try {
            List<Exportable> exporters = List.of(new RecordingExporter(tempDir));
            ConcurrentBatchExporter batchExporter = new ConcurrentBatchExporter(gradeManager, exporters);

            List<Student> students = List.of(
                    addStudentWithGrade("Alice", 80),
                    addStudentWithGrade("Bob", 90),
                    addStudentWithGrade("Carol", 70)
            );

            BatchExportResult result = batchExporter.exportAll(students, 3);

            assertEquals(3, result.getSuccessCount());
            assertTrue(result.getFailures().isEmpty());
        } finally {
            deleteRecursively(tempDir);
        }
    }

    @Test
    @DisplayName("tasks genuinely run on more than one thread, not sequentially on the caller's thread")
    void exportAll_tasksRunOnMultipleThreads() throws InterruptedException {
        Set<String> threadNamesUsed = ConcurrentHashMap.newKeySet();

        List<Exportable> exporters = List.of(new ThreadRecordingExporter(threadNamesUsed));
        ConcurrentBatchExporter batchExporter = new ConcurrentBatchExporter(gradeManager, exporters);

        List<Student> students = List.of(
                addStudentWithGrade("Alice", 80),
                addStudentWithGrade("Bob", 90),
                addStudentWithGrade("Carol", 70),
                addStudentWithGrade("Dan", 60)
        );

        batchExporter.exportAll(students, 4);

        // If this ran sequentially on the caller's thread, threadNamesUsed
        // would contain exactly one name (this test's own thread). Seeing
        // more than one distinct thread name is direct evidence the pool
        // genuinely distributed work across multiple worker threads.
        assertTrue(threadNamesUsed.size() > 1,
                "Expected work to be spread across multiple threads, but only saw: " + threadNamesUsed);
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

    /**
     * A minimal real Exportable that just writes a marker file - used to
     * confirm export() genuinely got called for each student, without
     * needing the full CSV/JSON/Binary exporters for this test.
     */
    private static class RecordingExporter implements Exportable {
        private final Path directory;

        RecordingExporter(Path directory) {
            this.directory = directory;
        }

        @Override
        public Path export(List<Grade> grades, String filename) throws ReportExportException {
            try {
                Path file = directory.resolve(filename + ".marker");
                Files.writeString(file, "exported");
                return file;
            } catch (IOException e) {
                throw new ReportExportException(e.getMessage());
            }
        }

        @Override
        public String getFormatName() {
            return "Marker";
        }
    }

    /**
     * Records which thread name executed each export call, without
     * writing anything to disk - used purely to prove concurrency.
     */
    private static class ThreadRecordingExporter implements Exportable {
        private final Set<String> threadNamesUsed;

        ThreadRecordingExporter(Set<String> threadNamesUsed) {
            this.threadNamesUsed = threadNamesUsed;
        }

        @Override
        public Path export(List<Grade> grades, String filename) {
            threadNamesUsed.add(Thread.currentThread().getName());
            return Path.of(filename);
        }

        @Override
        public String getFormatName() {
            return "ThreadRecorder";
        }
    }
}