package service.concurrency;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AuditLogger {

    private static final int MAX_RECENT_EVENTS = 10;

    private final ExecutorService loggingExecutor;
    private final Path logFile;

    /**
     * A LinkedList used as a Deque - efficient adding at one end and
     * removing from the other, which is exactly the "keep the last N,
     * drop the oldest" pattern below. Access is synchronized since both
     * the logging thread (writing) and the console's main thread
     * (reading, if this were ever displayed) could touch it.
     */
    private final Deque<String> recentEvents = new LinkedList<>();

    public AuditLogger() {
        this.loggingExecutor = Executors.newSingleThreadExecutor();
        this.logFile = Paths.get("logs", "audit.log");
    }

    public void log(String event) {
        loggingExecutor.submit(() -> writeEntry(event));
    }

    private void writeEntry(String event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = "[" + timestamp + "] " + event;

        recordRecentEvent(line);

        try {
            Files.createDirectories(logFile.getParent());
            Files.writeString(logFile, line + System.lineSeparator(), StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("(Audit log write failed: " + e.getMessage() + ")");
        }
    }

    private synchronized void recordRecentEvent(String line) {
        recentEvents.addFirst(line);
        if (recentEvents.size() > MAX_RECENT_EVENTS) {
            recentEvents.removeLast();
        }
    }

    /**
     * Returns the most recent events, most recent first, without
     * needing to re-read or re-parse the log file from disk.
     */
    public synchronized List<String> getRecentEvents() {
        return new java.util.ArrayList<>(recentEvents);
    }

    public void shutdown() {
        loggingExecutor.shutdown();
        try {
            loggingExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}