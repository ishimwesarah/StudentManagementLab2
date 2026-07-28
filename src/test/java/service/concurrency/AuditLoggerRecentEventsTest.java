package service.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AuditLogger - recent events buffer")
class AuditLoggerRecentEventsTest {

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
    @DisplayName("getRecentEvents returns entries most-recent-first")
    void getRecentEvents_returnsMostRecentFirst() throws InterruptedException {
        auditLogger.log("First event");
        auditLogger.log("Second event");

        boolean done = waitUntil(() -> auditLogger.getRecentEvents().size() >= 2, 2000);
        assertTrue(done);

        List<String> recent = auditLogger.getRecentEvents();
        assertTrue(recent.get(0).contains("Second event"), "Most recent event should be first");
        assertTrue(recent.get(1).contains("First event"));
    }

    @Test
    @DisplayName("only the most recent 10 events are kept, older ones are dropped")
    void getRecentEvents_keepsOnlyLast10() throws InterruptedException {
        for (int i = 1; i <= 15; i++) {
            auditLogger.log("Event " + i);
        }

        boolean done = waitUntil(() -> auditLogger.getRecentEvents().size() == 10, 3000);
        assertTrue(done);

        List<String> recent = auditLogger.getRecentEvents();
        assertEquals(10, recent.size());
        assertTrue(recent.get(0).contains("Event 15"), "Newest event should be at the front");
        assertTrue(recent.get(9).contains("Event 6"), "Oldest retained event should be Event 6 - events 1-5 should be dropped");
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