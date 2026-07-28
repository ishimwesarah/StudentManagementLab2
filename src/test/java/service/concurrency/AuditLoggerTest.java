package service.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AuditLogger")
class AuditLoggerTest {

    private AuditLogger auditLogger;
    private final Path logFile = Paths.get("logs", "audit.log");

    @BeforeEach
    void setUp() throws IOException {
        auditLogger = new AuditLogger();
        Files.deleteIfExists(logFile);
    }

    @AfterEach
    void tearDown() throws IOException {
        auditLogger.shutdown();
        Files.deleteIfExists(logFile);
    }

    @Test
    @DisplayName("log() eventually writes the entry to the audit file")
    void log_writesEntryToFile() throws InterruptedException, IOException {
        auditLogger.log("Test event occurred");

        boolean written = waitUntil(() -> Files.exists(logFile), 2000);
        assertTrue(written, "Expected the log file to be created within 2 seconds");

        String content = Files.readString(logFile);
        assertTrue(content.contains("Test event occurred"));
    }

    @Test
    @DisplayName("multiple log entries are all preserved, in order, with none overwritten")
    void log_multipleEntries_allPreservedInOrder() throws InterruptedException, IOException {
        auditLogger.log("First event");
        auditLogger.log("Second event");
        auditLogger.log("Third event");

        boolean written = waitUntil(() -> {
            try {
                return Files.exists(logFile) && Files.readString(logFile).contains("Third event");
            } catch (IOException e) {
                return false;
            }
        }, 2000);
        assertTrue(written, "Expected all three entries to be written within 2 seconds");

        String content = Files.readString(logFile);
        int firstIndex = content.indexOf("First event");
        int secondIndex = content.indexOf("Second event");
        int thirdIndex = content.indexOf("Third event");

        assertTrue(firstIndex < secondIndex, "Entries should appear in the order they were logged");
        assertTrue(secondIndex < thirdIndex, "Entries should appear in the order they were logged");
    }

    @Test
    @DisplayName("each log entry includes a timestamp")
    void log_entryIncludesTimestamp() throws InterruptedException, IOException {
        auditLogger.log("Timestamped event");

        waitUntil(() -> Files.exists(logFile), 2000);

        String content = Files.readString(logFile);
        // A timestamp formatted as yyyy-MM-dd HH:mm:ss will always contain
        // at least one hyphen-separated date segment like "2026-07-28".
        assertTrue(content.matches("(?s).*\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\].*"));
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
}