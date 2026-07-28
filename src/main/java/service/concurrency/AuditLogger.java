package service.concurrency;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private final Deque<String> recentEvents = new LinkedList<>();

    private BufferedWriter writer;

    public AuditLogger() {
        this.loggingExecutor = Executors.newSingleThreadExecutor();
        this.logFile = Paths.get("logs", "audit.log");
        loggingExecutor.submit(this::openWriter);
    }

    /**
     * Opens one persistent BufferedWriter for the lifetime of this
     * logger, rather than opening/closing a file handle on every single
     * log() call. Safe to only ever touch from the single logging
     * thread - no synchronization needed on the writer itself, since
     * nothing else ever accesses it.
     */
    private void openWriter() {
        try {
            Files.createDirectories(logFile.getParent());
            writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("(Could not open audit log for writing: " + e.getMessage() + ")");
        }
    }

    public void log(String event) {
        loggingExecutor.submit(() -> writeEntry(event));
    }

    private void writeEntry(String event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = "[" + timestamp + "] " + event;

        recordRecentEvent(line);

        if (writer == null) {
            System.out.println("(Audit log write skipped: writer not available)");
            return;
        }

        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
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

    public synchronized List<String> getRecentEvents() {
        return new java.util.ArrayList<>(recentEvents);
    }

    public void shutdown() {
        loggingExecutor.submit(this::closeWriter);
        loggingExecutor.shutdown();
        try {
            loggingExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void closeWriter() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.out.println("(Could not close audit log writer: " + e.getMessage() + ")");
            }
        }
    }
}